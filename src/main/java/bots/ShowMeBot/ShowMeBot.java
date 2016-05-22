package bots.ShowMeBot;

import static bridgempp.util.ImageEdit.resizeImage;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class ShowMeBot extends Bot {

	private String[] triggers = { "zeig mir", "zeige mir", "show me" };

	@Override
	public void initializeBot() {

	}

	@Override
	public void messageReceived(Message message) {
		String query = hasTrigger(message.getMessage());
		if (query == null) {
			return;
		}
		try {
			query = URLEncoder.encode(query.trim(), "UTF-8");
//			String htmlText = IOUtils.toString(new URL("http://www.google.com/search?q=" + query + "&tbm=isch"));
//			htmlText = Pattern.compile("<!--.*?-->", Pattern.DOTALL).matcher(htmlText).replaceAll("");
//			Matcher matcher = Pattern.compile("<img.*src=\"([^\"]*?)\".*/>").matcher(htmlText);
//			if (!matcher.find()) {
//				sendMessage(new Message(message.getGroup(), "No search result for: " + query, "Plain Text"));
//				return;
//			}
//			URL imageURL = new URL(matcher.group(1));
			QueryResult result = getGoogleImageSearchResult(query, message.getMessage().toLowerCase().contains("random"));
			URLConnection connection = result.resultURL.openConnection();
			byte[] image;
			image = resizeImage(connection, 100, 100);
			if (image != null) {
				sendMessage(new Message(message.getGroup(), "\n<img src=\"data:image/jpeg;base64,"
						+ Base64.getEncoder().encodeToString(image) + "\" alt=\"" + query
						+ "\" width=\"320\" height=\"240\"/>", MessageFormat.XHTML));
			}
			sendMessage(new Message(message.getGroup(), "<br/><img src=\"" + result.resultURL.toString() + "\" alt=\"" + query
					+ "\" width=\"100\" height=\"100\"/><br/>\nSource (" + (result.index + 1) + "/" + result.response + "): "+ result.resultURL.toString().replaceAll("&", "&amp;") + "<br/>\nQuery: " + result.searchURL.toString().replaceAll("&", "&amp;"), MessageFormat.XHTML));
		} catch (Exception e) {
			sendMessage(new Message(message.getGroup(), "An error has occured loading the Image: " + e.toString(), MessageFormat.PLAIN_TEXT));
		}
	}



	public QueryResult getGoogleImageSearchResult(String query, boolean random) throws IOException {
		QueryResult result = new QueryResult();
		result.queryURL = new URL("https://www.googleapis.com/customsearch/v1?cx=016170731421469020746%3Amnifwlr6hw8&key=AIzaSyC_4KVn-EWwHTXuFLbmyDxnjrVX09WWY8s&searchType=image&q=" + query);
		result.searchURL = new URL("https://www.google.com/search?tbm=isch&q=" + query);
		URLConnection connection = result.queryURL.openConnection();
		connection.addRequestProperty("Referer", "http://vinpasso.org");
		String jsonSearchQuery = IOUtils.toString(connection.getInputStream());
		JSONObject jsonQuery = new JSONObject(jsonSearchQuery);
		JSONArray queryArray = jsonQuery.getJSONArray("items");
		result.response = queryArray.length();
		result.index = (random)?new Random().nextInt(Math.max(result.response, 0)):0;
		result.resultURL = new URL(queryArray.getJSONObject(result.index).getString("link"));
		return result;
	}

	public String hasTrigger(String message) {
		message = message.trim().toLowerCase();
		for (int i = 0; i < triggers.length; i++) {
			if (message.contains(triggers[i])) {
				return message.substring(message.indexOf(triggers[i]) + triggers[i].length());
			}
		}
		return null;
	}
}
