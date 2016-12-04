package bots.TUMCanteenBot;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;
import bridgempp.bot.wrapper.Schedule;
import bridgempp.util.Log;

public class TUMCanteenBot extends Bot {
	
	private static final String baseAPIUrl = "http://www.devapp.it.tum.de/mensaapp/exportDB.php";
	private static final Pattern canteenQueryPattern = Pattern.compile("^\\?\\p{Alpha}+ (\\H+)");
	
	private static final SimpleDateFormat canteenDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final int SCHEDULE_DAILY_MESSSAGE_AT_HOURS = 8;
	private static final int SWITCH_TO_NEXT_DAY_HOUR = 15;
	
	private Consumer<String> debugOutputReader;
	
	static {
		canteenDateFormat.setTimeZone(CanteenTimeHelper.CET_ZONE);
	}
	
	// all prices are for students only
	private static int[] pricesDailyDish = {
		100, 155, 190, 240
	};
	private static int[] pricesSpecialAndOrganicDish = {
		155, 190, 240, 260, 280, 300, 320, 350, 400, 450
	};
	
	private static enum DishType {
		DAILY("Daily dishes"), // = Tagesgericht
		SPECIAL("Special dishes"), // = Aktionsessen
		ORGANIC("Organic dishes"); // = Biogericht
		
		String name;
		
		DishType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	@SuppressWarnings("unused")
	private static class DishStruct {
		public Date date;
		public DishType type;
		public String name;
		public int price;
	}
	
	private static class CanteenStruct {
		public int id;
		public String name;
		/**
		 * The time when the {@code cachedDishes} were last updated or {@code null if nothing is cached}
		 */
		public long currentMenuDate;
		public List<DishStruct>[] cachedDishes;
	}
	
	private static int lookUpDishPrice(DishType dishType, int priceType) {
		int[] array = null;
		switch (dishType) {
			case DAILY:
				array = TUMCanteenBot.pricesDailyDish;
				break;
				
			case SPECIAL:
			case ORGANIC:
				array = TUMCanteenBot.pricesSpecialAndOrganicDish;
				break;
		}
		
		if (priceType < 0 || priceType >= array.length) {
			return -1;
		}
		return array[priceType];
	}
	
	private static JSONObject queryAPI() throws MalformedURLException, JSONException, IOException {
		return queryAPI("");
	}
	
	private static JSONObject queryAPI(String urlSuffix) throws MalformedURLException, JSONException, IOException {
		URL apiUrl = new URL(baseAPIUrl + urlSuffix);
		URLConnection connection = apiUrl.openConnection();
		connection.setRequestProperty("User-Agent", "TCA-Client"); // user agent used by the TUM Android App
		
		try (InputStream in = connection.getInputStream()) {
			return new JSONObject(new JSONTokener(in));
		}
	}
	
	
	private boolean isInitialized;
	private Map<Integer, CanteenStruct> canteensMap;
	private Map<Integer, Future<?>> scheduledMap;

	@Override
	public void initializeBot() {
		isInitialized = false;
		canteensMap = new HashMap<>();
		scheduledMap = new HashMap<>();
		name = "TUM Canteen Bot";
		
		try {
			JSONObject root = queryAPI();
			JSONArray canteenList = root.getJSONArray("mensa_mensen");
			
			// iterate through the list of canteen locations and store them by their id
			for (int i = 0; i < canteenList.length(); i++) {
				JSONObject canteen = canteenList.getJSONObject(i);
				
				String name = canteen.getString("name");
				int id;
				try {
					id = Integer.parseInt(canteen.getString("id"));
					
				} catch (NumberFormatException e) {
					Log.log(Level.WARNING, "Invalid ID for canteen " + name + ", skipping", e);
					continue;
				}
				
				CanteenStruct c = new CanteenStruct();
				c.id = id;
				c.name = name;
				c.currentMenuDate = -1;
				canteensMap.put(id, c);
			}
			
			isInitialized = canteensMap.size() > 0;
			
		} catch (JSONException e) {
			Log.log(Level.WARNING, "The API returned invalid JSON", e);
			
		} catch (IOException e) {
			Log.log(Level.WARNING, "Could not initialize TUM canteen bot", e);
		}
		
		if (!isInitialized) {
			canteensMap = null;
		}
	}

	@Override
	public void messageReceived(Message message) {
		if (!isInitialized) {
			return; // bot did not initialize correctly
		}
		
		String msg = message.getMessage().toLowerCase();
		if (msg.startsWith("?listcanteens")) {
			respondListCanteens(message);
			
		} else if (msg.startsWith("?canteendaily")) {
			getCanteenFromMessage(message).ifPresent((canteen) -> enableDailyMessage(canteen, message));
			
		} else if (msg.startsWith("?canteendisabledaily")) {
			getCanteenFromMessage(message).ifPresent((canteen) -> disableDailyMessage(canteen, message));

		} else if (msg.startsWith("?canteen")) {
			getCanteenFromMessage(message).ifPresent((canteen) -> queryCanteen(canteen, message.getGroup()));
		}
			
	}
	
	private Optional<CanteenStruct> getCanteenFromMessage(Message message) {
		Matcher matcher = canteenQueryPattern.matcher(message.getMessage());
		if (!matcher.find()) {
			sendMessage(message.getGroup(), "To see a list of all available canteens, type ?listcanteens");
			return Optional.empty();
		}
		
		String idString = matcher.group(1).trim();
		int id;
		try {
			id = Integer.parseInt(idString);
			
		} catch (NumberFormatException e) {
			sendMessage(message.getGroup(), idString + " is not a valid integer canteen ID! To see a list of all available canteens, type ?listcanteens");
			return Optional.empty();
		}
		
		CanteenStruct selectedCanteen = canteensMap.get(id);
		if (selectedCanteen == null) {
			sendMessage(message.getGroup(), "A canteen with the ID " + id + " does not exist! To see a list of all available canteens and their IDs, type ?listcanteens");
			return Optional.empty();
		}
		
		return Optional.of(selectedCanteen);
	}

	private void respondListCanteens(Message message) {
		StringBuilder b = new StringBuilder(15 + 25 * canteensMap.size());
		b.append("\nID  NAME\n");
		
		boolean first = true; // meh
		for (Entry<Integer, CanteenStruct> c : canteensMap.entrySet()) {
			if (first) {
				first = false;
				
			} else {
				b.append('\n');
			}
			
			b.append(c.getKey());
			b.append(' ');
			b.append(c.getValue().name);
		}
		
		sendMessage(message.getGroup(), b.toString());
		
	}
	
	@SuppressWarnings("unchecked")
	private void queryCanteen(CanteenStruct selectedCanteen, String replyGroupId) {
		List<DishStruct>[] dishCategories;
		
		Calendar date = CanteenTimeHelper.getToday();
		// if it's later than, for example, 15:00, switch to displaying the menu of the next day
		if (CanteenTimeHelper.getNow().get(Calendar.HOUR_OF_DAY) >= SWITCH_TO_NEXT_DAY_HOUR)
			date.roll(Calendar.DATE, 1);
		
		// skip the weekend
		switch (date.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SATURDAY:
				date.roll(Calendar.DATE, 1);
			case Calendar.SUNDAY:
				date.roll(Calendar.DATE, 1);
		}
		
		// check if we can use the cached API response
		if (selectedCanteen.currentMenuDate != date.getTimeInMillis()) {
			dishCategories = new List[DishType.values().length];
			for (int i = 0; i < dishCategories.length; i++) {
				dishCategories[i] = new ArrayList<DishStruct>(6);
			}
			
			try {
				parseCanteenMenu(selectedCanteen.id, date, dishCategories);
				
			} catch (JSONException e) {
				Log.log(Level.WARNING, "The API returned invalid JSON", e);
				sendMessage(replyGroupId, "ERROR: Could not retrieve results because the API returned invalid JSON.");
				return;
				
			} catch (IOException e) {
				Log.log(Level.WARNING, "Could not query the TUM canteen API", e);
				sendMessage(replyGroupId, "ERROR: Could not query the TUM canteen API.");
				return;
			}
			
			selectedCanteen.currentMenuDate = date.getTimeInMillis();
			selectedCanteen.cachedDishes = dishCategories;
			
		} else {
			dishCategories = selectedCanteen.cachedDishes;
		}
		
		
		int numDishes = 0;
		for (List<DishStruct> l : dishCategories) {
			numDishes += l.size();
		}
		
		String weekday = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG_FORMAT, Locale.UK);
		// either it's the weekend or a bug, hopefully the former
		if (numDishes == 0) {
			sendMessage(replyGroupId, "Canteen \"" + selectedCanteen.name + "\" does not serve any dishes on " + weekday + ".");
			return;
		}
		
		StringBuilder b = new StringBuilder(400);
		b.append("Dishes served on " + weekday + " at the \"" + selectedCanteen.name + "\"\n");
		
		for (int i = 0; i < dishCategories.length; i++) {
			List<DishStruct> l = dishCategories[i];
			if (l.size() == 0) {
				continue;
			}
			
			b.append("\n");
			b.append(DishType.values()[i].toString());
			b.append(":\n");
			
			l.sort((d1, d2) -> d1.price - d2.price);
			for (DishStruct dish : l) {
				b.append("- " + dish.name + " ");
				b.append(String.format("%d.%02d€\n", dish.price / 100, dish.price % 100));
			}
		}
		
		sendMessage(replyGroupId, b.toString());
	}

	private void enableDailyMessage(CanteenStruct canteen, Message message) {
		sendMessage(message.getGroup(), "Daily messages scheduled for canteen " + canteen.id);
		
		// check if already scheduled
		if (scheduledMap.containsKey(canteen.id))
			return;
		
		final Runnable dailyMessage = new Runnable() {
			
			@Override
			public void run() {
				if (scheduledMap.containsKey(canteen.id)) {
					// not the first run, query the canteen and send a chat message
					queryCanteen(canteen, message.getGroup());
				}
				
				if (Thread.interrupted())
					return;
				
				long delay = CanteenTimeHelper.getMillisecondsUntilTomorrow(SCHEDULE_DAILY_MESSSAGE_AT_HOURS, 0, 0);
				Future<?> scheduledFutureMessage = Schedule.scheduleOnce(this, delay, TimeUnit.MILLISECONDS);
				scheduledMap.put(canteen.id, scheduledFutureMessage);
			}
			
		};
		dailyMessage.run();
	}

	private void disableDailyMessage(CanteenStruct canteen, Message message) {
		Future<?> future = scheduledMap.remove(canteen.id);
		if (future != null)
			future.cancel(true);
		
		sendMessage(message.getGroup(), "Disabled daily messages for canteen " + canteen.id);
	}

	private void parseCanteenMenu(int canteenId, Calendar date, List<DishStruct>[] dishCategories) throws MalformedURLException, JSONException, IOException {
		JSONObject root = queryAPI("?mensa_id=" + canteenId);
		JSONArray canteenMenu = root.getJSONArray("mensa_menu");
		
		for (int i = 0; i < canteenMenu.length(); i++) {
			JSONObject dishObj = canteenMenu.getJSONObject(i);
			String dateString = dishObj.getString("date");
			Date dateDish;
			try {
				dateDish = canteenDateFormat.parse(dateString);
				
			} catch (ParseException e) {
				Log.log(Level.WARNING, "The API returned an invalid date string for dish #" + dishObj.getString("id") + ", skipping", e);
				continue;
			}
			
			// skip dishes for days we are not looking for
			if (date.getTimeInMillis() != dateDish.getTime()) {
				continue;
			}
			
			DishType type;
			switch (dishObj.getString("type_short")) {
				case "tg":
					type = DishType.DAILY;
					break;
				case "ae":
					type = DishType.SPECIAL;
					break;
				case "bg":
					type = DishType.ORGANIC;
					break;
				default:
					Log.log(Level.WARNING, "The API returned an invalid dish type for #" + dishObj.getString("id") + ", skipping");
					continue;
			}
			
			String priceTypeString = dishObj.getString("type_nr");
			int priceType;
			try {
				priceType = Integer.parseInt(priceTypeString) - 1; // because this is going to be an array index
				
			} catch (NumberFormatException e) {
				Log.log(Level.WARNING, "The API returned an invalid price type for dish #" + dishObj.getString("id") + ", skipping");
				continue;
			}
			
			DishStruct dish = new DishStruct();
			dish.date = dateDish;
			dish.name = dishObj.getString("name");
			dish.type = type;
			dish.price = lookUpDishPrice(type, priceType);
			dishCategories[type.ordinal()].add(dish);
		}
	}
	
	private void sendMessage(String group, String answer) {
		if (debugOutputReader != null) {
			debugOutputReader.accept(answer);
			return;
		}
		
		Message answerMessage = new Message(group, answer, MessageFormat.PLAIN_TEXT);
		sendMessage(answerMessage);
	}
	
	public void setDebugOutputReader(Consumer<String> consumer) {
		debugOutputReader = consumer;
	}

}
