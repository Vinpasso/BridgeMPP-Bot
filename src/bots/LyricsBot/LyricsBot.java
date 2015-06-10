package bots.LyricsBot;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class LyricsBot extends Bot {

	@Override
	public void initializeBot() {
	}

	@Override
	public void messageReceived(Message message) {
		try {
			if(message.getMessage().contains("?lyrics "))
			{
			sendMessage(new Message(message.getGroup(),
					getSongData(message.getMessage().replaceAll("\\?lyrics ", "")), MessageFormat.PLAIN_TEXT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSongData(String query) throws Exception {
		URL url = new URL("http://api.lyricsnmusic.com/songs?api_key=bca4a09909e6837db805b28d13ea4b&per_page=1&q=" + URLEncoder.encode(query, "UTF-8"));
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("Referer", "http://vinpasso.org");
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0");
		connection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

		String jsonSearchQuery = IOUtils.toString(connection.getInputStream());
		JSONArray jsonQuery = new JSONArray(jsonSearchQuery);
		return "Artist: " + jsonQuery.getJSONObject(0).getJSONObject("artist").getString("name") + "\nTitle: "
				+ jsonQuery.getJSONObject(0).getString("title") + "\n"
				+ jsonQuery.getJSONObject(0).getString("snippet");

	}
}
