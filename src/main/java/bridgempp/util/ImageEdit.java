package bridgempp.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.Base64;

import javax.imageio.ImageIO;

/**
 * Created by alex on 06.05.16.
 */
public class ImageEdit {


    public static String imageAsTag(String url, int Imagewidth, int Imageheight, String alt, String imageformat){
        int width = Imagewidth > 320 ? 320 : Imagewidth;
        int height = Imageheight > 320 ? 320 : Imageheight;

        return "<img src=\"data:image/jpeg;base64,"  + Base64.getEncoder().encodeToString(resizeImage(url,width,height,imageformat)) + "\" " +
            "alt=\"" + alt + "\" width=\"" + width + "\" height=\"" + height + "\"/>";
    }

    public static String imageAsTag(String url, int Imagewidth, int Imageheight, String alt ){
        int width = Imagewidth > 320 ? 320 : Imagewidth;
        int height = Imageheight > 320 ? 320 : Imageheight;

        return "<img src=\"data:image/jpeg;base64,"  + Base64.getEncoder().encodeToString(resizeImage(url,width,height)) + "\" " +
               "alt=\"" + alt + "\" width=\"" + width + "\" height=\"" + height + "\"/>";
    }

    public static byte[] resizeImage(String url, int width, int height){
        try {
            return resizeImage((new URL(url)).openConnection(),width,height);
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] resizeImage(URLConnection connection, int imagewidth, int imageHeight){
        return resizeImage(connection,imagewidth,imageHeight,"JPG");
    }

    public static byte[] resizeImage(String url, int imageWidth, int imageHeight, String imageType){
        try{
            return resizeImage(new URL(url).openConnection(),imageHeight,imageHeight,imageType);
        }
        catch(Exception e){
            return null;
        }
    }

    public static byte[] resizeImage(URLConnection connection, int imageWidth, int imageHeight, String imageType) {
        int width = imageWidth;
        int height = imageHeight;
        
        try {
            BufferedImage originalImage = ImageIO.read(connection.getInputStream());
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setBackground(Color.WHITE);
            graphics.drawImage(originalImage, 0, 0, width, height, null);
            graphics.finalize();
            graphics.dispose();
            ByteBuffer buffer = ByteBuffer.allocate(49000);
            ImageIO.write(resizedImage, imageType, new OutputStream() {

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
