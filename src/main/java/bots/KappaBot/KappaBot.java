package bots.KappaBot;

import bridgempp.util.JsonReader;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Created by alex on 06.05.16.
 */
public class KappaBot {

    public class EmoteInfo {
        public final String emotename;
        public final String description;
        public final String image_id;

        public EmoteInfo(String emotename, String description, String image_id) {
            this.emotename = emotename;
            this.description = description;
            this.image_id = image_id;
        }
    }

    Map<String,EmoteInfo> emotes_to_url = new HashMap();

    private void fillMap(JSONObject downloaded){
        JSONObject emotes = downloaded.getJSONObject("emotes");
        for(Iterator it = emotes.keys(); it.hasNext();){

            String key = (String)it.next();
            JSONObject emote = emotes.getJSONObject(key);
            emotes_to_url.put(key.toLowerCase(),new EmoteInfo(key,emote.optString("description"),String.valueOf(emote.getInt("image_id"))));
        }
    }

    public int getEmoteCount(){
        return emotes_to_url.size();
    }

    public String refresh_emotes(){
        try{
            //download the emote map. To use the full map use the following url: (may or may not work)
            // https://twitchemotes.com/api_cache/v2/global.json
            fillMap(JsonReader.readJsonFromUrl("http://twitchemotes.com/api_cache/v2/global.json"));
            return null;
        }
        catch (Exception c){
            try{
                fillMap(JsonReader.readJsonFromUrl("http://twitchemotes.com/api_cache/v2/global.json"));
                return null;
            }catch(Exception e){
                return e.getMessage();
            }
        }
    }

    public static String getUrlFromImageID(String id){
        return "https://static-cdn.jtvnw.net/emoticons/v1/" + id + "/2.0";
    }

    public EmoteInfo[] getEmpoteUrls (String message){

        List<EmoteInfo> result = new ArrayList<>();
        for( String word: message.toLowerCase().split(" ")){
            EmoteInfo emote = emotes_to_url.get(word);
            if(  emote != null){
                result.add(emote);
            }
        }
        return result.toArray(new EmoteInfo[result.size()]);
    }


    public static void main(String args[]) throws IOException {

        boolean exit = false;
        KappaBot kappabot = new KappaBot();
        kappabot.refresh_emotes();
        Scanner reader = new Scanner(System.in);

        while (!exit) {
            String line = reader.nextLine();
            exit = line.equals("exit");
            EmoteInfo[] emoteurls = kappabot.getEmpoteUrls(line);

            if (emoteurls != null) {
                System.out.println("Kappabot says:");
                for( EmoteInfo emote : emoteurls) {
                    System.out.println(emote);
                }
            }
        }
        reader.close();
    }

}
