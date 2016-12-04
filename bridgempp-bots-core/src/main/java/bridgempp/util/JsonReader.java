package bridgempp.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by alex on 06.05.16.
 */
public class JsonReader {

    private static String readAll(Reader rd) {
        StringBuilder sb = new StringBuilder();
        int cp;
        try {
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
        } catch(Exception e){
            System.err.println("[JSONReader] failed to read all from Reader in JSONReader.readAll");
        }
        return sb.toString();
    }


    public static JSONObject readJsonFromUrl(String url) throws JSONException {
        InputStream is = null;

        try{
            is = new URL(url).openStream();
        }
        catch(Exception e){
            System.err.println("[JSONReader] failed to request URL: " + url + "in JSONReader.readJsonFromUrl");
            return new JSONObject();
        }
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            try {
                is.close();
            }catch (Exception e){}
        }
    }

    public static void main(String[] args) throws IOException, JSONException {
        JSONObject json = readJsonFromUrl("https://graph.facebook.com/19292868552");
        System.out.println(json.toString());
        System.out.println(json.get("id"));
    }
}

