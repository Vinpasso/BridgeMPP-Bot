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
import bridgempp.util.Log;

public class TUMCanteenBot extends Bot {
	
	private static final String baseAPIUrl = "http://www.devapp.it.tum.de/mensaapp/exportDB.php";
	private static final Pattern canteenQueryPattern = Pattern.compile("\\?canteen (.++)");
	
	private static final SimpleDateFormat canteenDateFormat = new SimpleDateFormat("yyyy-MM-dd");;
	private static final Calendar calendar = Calendar.getInstance(Locale.UK);
	
	private static String dateToWeekday(Date date) {
		calendar.setTime(date);
		return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG_FORMAT, Locale.UK);
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
		public String name; // TODO implement caching
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

	@Override
	public void initializeBot() {
		isInitialized = false;
		canteensMap = new HashMap<>();;
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
				c.name = name;
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
			
		} else if (msg.startsWith("?canteen")) {
			respondQueryCanteen(message);
		}
	}

	private void respondListCanteens(Message message) {
		StringBuilder b = new StringBuilder(15 + 25 * canteensMap.size());
		b.append("ID  NAME\n");
		
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
		
		Message answer = new Message(message.getGroup(), b.toString(), MessageFormat.PLAIN_TEXT);
		sendMessage(answer);
		
	}
	
	private void respondQueryCanteen(Message message) {
		Matcher matcher = canteenQueryPattern.matcher(message.getMessage());
		if (!matcher.find()) {
			return;
		}
		
		String idString = matcher.group(1).trim();
		int id;
		try {
			id = Integer.parseInt(idString);
			
		} catch (NumberFormatException e) {
			sendMessage(message, idString + " is not a valid integer canteen ID! To see a list of all available canteens, type ?listcanteens");
			return;
		}
		
		CanteenStruct selectedCanteen = canteensMap.get(id);
		if (selectedCanteen == null) {
			sendMessage(message, "A canteen with the ID " + id + " does not exist! To see a list of all available canteens and their IDs, type ?listcanteens");
			return;
		}
		
		try {
			JSONObject root = queryAPI("?mensa_id=" + id);
			JSONArray canteenMenu = root.getJSONArray("mensa_menu");
			
			@SuppressWarnings("unchecked") // because we can't directly create an array with a generic type
			List<DishStruct>[] dishCategories = new List[DishType.values().length];
			for (int i = 0; i < dishCategories.length; i++) {
				dishCategories[i] = new ArrayList<DishStruct>(6);
			}
			
			long offset = 32400000L; // 1000 * 60 * 60 * 9
			// having the offset at 9h means that at 24:00 - 9h = 15:00 the bot will switch
			// to displaying dishes served the next day
			// TODO handle weekends
			long now = new Date().getTime() + offset;
			long twentyFourHours = 86400000L; // == 1000 * 60 * 60 * 24
			Date nowMinus24h = new Date(now - twentyFourHours);
			Date nowPlus24h = new Date(now + twentyFourHours);
			
			for (int i = 0; i < canteenMenu.length(); i++) {
				JSONObject dishObj = canteenMenu.getJSONObject(i);
				String dateString = dishObj.getString("date");
				Date date;
				try {
					date = canteenDateFormat.parse(dateString);
					
				} catch (ParseException e) {
					Log.log(Level.WARNING, "The API returned an invalid date string for dish #" + dishObj.getString("id") + ", skipping", e);
					continue;
				}
				
				// skip dishes not on the menu today
				if (!date.after(nowMinus24h) || !date.before(nowPlus24h)) {
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
				dish.date = date;
				dish.name = dishObj.getString("name");
				dish.type = type;
				dish.price = lookUpDishPrice(type, priceType);
				dishCategories[type.ordinal()].add(dish);
			}
			
			int numDishes = 0;
			for (List<DishStruct> l : dishCategories) {
				numDishes += l.size();
			}
			
			// either it's the weekend or a bug, hopefully the former
			if (numDishes == 0) {
				sendMessage(message, "Canteen \"" + selectedCanteen.name + "\" does not serve any dishes today.");
				return;
			}
			
			StringBuilder b = new StringBuilder(200);
			b.append("Dishes served on " + dateToWeekday(new Date()) + " at the \"" + selectedCanteen.name + "\"\n");
			
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
			
			sendMessage(message, b.toString());
			
		} catch (JSONException e) {
			Log.log(Level.WARNING, "The API returned invalid JSON", e);
			sendMessage(message, "ERROR: Could not retrieve results because the API returned invalid JSON.");
			return;
			
		} catch (IOException e) {
			Log.log(Level.WARNING, "Could not query the TUM canteen API", e);
		}
	}
	
	private void sendMessage(Message received, String answer) {
		Message answerMessage = new Message(received.getGroup(), answer, MessageFormat.PLAIN_TEXT);
		sendMessage(answerMessage);
	}

}
