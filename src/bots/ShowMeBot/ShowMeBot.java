package bots.ShowMeBot;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

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
			query = URLEncoder.encode(query, "UTF-8");
//			String htmlText = IOUtils.toString(new URL("http://www.google.com/search?q=" + query + "&tbm=isch"));
//			htmlText = Pattern.compile("<!--.*?-->", Pattern.DOTALL).matcher(htmlText).replaceAll("");
//			Matcher matcher = Pattern.compile("<img.*src=\"([^\"]*?)\".*/>").matcher(htmlText);
//			if (!matcher.find()) {
//				sendMessage(new Message(message.getGroup(), "No search result for: " + query, "Plain Text"));
//				return;
//			}
//			URL imageURL = new URL(matcher.group(1));
			URL imageURL = new URL(getGoogleImageSearchResult(query, message.getMessage().toLowerCase().contains("random")));
			URLConnection connection = imageURL.openConnection();
			byte[] image;
			image = resizeImage(connection, 100, 100);
			if (image != null) {
				sendMessage(new Message(message.getGroup(), "<img src=\"data:image/jpeg;base64,"
						+ Base64.getEncoder().encodeToString(image) + "\" alt=\"" + query
						+ "\" width=\"320\" height=\"240\"/>", MessageFormat.XHTML));
			}
			sendMessage(new Message(message.getGroup(), "<img src=\"" + imageURL.toString() + "\" alt=\"" + query
					+ "\" width=\"100\" height=\"100\"/> Source: " + imageURL.toString(), MessageFormat.XHTML));
		} catch (Exception e) {
			sendMessage(new Message(message.getGroup(), "An error has occured loading the Image: " + e.toString(), MessageFormat.PLAIN_TEXT));
		}
	}

	private byte[] resizeImage(URLConnection connection, int width, int height) {
		try {
			BufferedImage originalImage = ImageIO.read(connection.getInputStream());
			BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = resizedImage.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.drawImage(originalImage, 0, 0, width, height, null);
			graphics.finalize();
			graphics.dispose();
			ByteBuffer buffer = ByteBuffer.allocate(49000);
			ImageIO.write(resizedImage, "JPG", new OutputStream() {

				@Override
				public void write(int b) throws IOException {
					buffer.put((byte) b);
				}

				@Override
				public void write(byte[] bytes, int start, int length) {
					buffer.put(bytes, start, length);
				}

			});
			buffer.flip();
			byte[] array = new byte[buffer.remaining()];
			buffer.get(array, 0, array.length);
			return array;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getGoogleImageSearchResult(String query, boolean random) throws IOException {
		URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query);
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("Referer", "http://vinpasso.org");
		String jsonSearchQuery = IOUtils.toString(connection.getInputStream());
		JSONObject jsonQuery = new JSONObject(jsonSearchQuery);
		JSONArray queryArray = jsonQuery.getJSONObject("responseData").getJSONArray("results");
		int chooseQuery = (random)?new Random().nextInt(Math.max(queryArray.length(), 0)):0;
		return queryArray.getJSONObject(chooseQuery).getString("unescapedUrl");
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
