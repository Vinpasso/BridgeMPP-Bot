package bots.MemeBot.dto;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.apache.http.protocol.HTTP;

import bridgempp.bot.wrapper.Message;
import lombok.Data;

@Data
public class Meme {
	private int id;
	private String name;
	private String url;
	private int width;
	private int height;

	public String toHtml() {
		URLConnection connection = null;
		try {
			URL url = new URL("http://" + this.url);
			connection = url.openConnection();
			byte[] image;
			image = resizeImage(connection, 100, 100);
			return "<img src='" + url + "' width=" + width + " height=" + height + "/>";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public byte[] resizeImage(URLConnection connection, int width, int height) {
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
}
