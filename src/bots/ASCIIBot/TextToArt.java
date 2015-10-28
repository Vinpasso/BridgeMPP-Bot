package bots.ASCIIBot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class TextToArt {

	public static String textToArt(String text)
	{
		String result = "";
		BufferedImage renderBuffer = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = renderBuffer.createGraphics();
		for(int character = 0; character < text.length(); character++)
		{
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, renderBuffer.getWidth(), renderBuffer.getHeight());
			graphics.setColor(Color.BLACK);
			graphics.drawString(text.substring(character, character+1), 0, 0);
			for(int y = 0; y < renderBuffer.getHeight(); y++)
			{
				for(int x = 0; x < renderBuffer.getWidth(); x++)
				{
					result += (renderBuffer.getRGB(x, y)==0x00FFFFFF)?" ":"*";
				}
				result += "\n";
			}
		}
		return result;
	}
	
}
