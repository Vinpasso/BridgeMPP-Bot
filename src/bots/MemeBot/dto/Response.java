package bots.MemeBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Response {
    /*
     {
         "success": true,
        "data": {
        "memes": [
        {
            "id": "61579",
            "name": "One Does Not Simply",
            "url": "http://i.imgflip.com/1bij.jpg",
            "width": 568,
            "height": 335
        },
        {
            "id": "101470",
            "name": "Ancient Aliens",
            "url": "http://i.imgflip.com/26am.jpg",
            "width": 500,
            "height": 437
        }
        // probably a lot more memes here..
        ]
    }
    */

    private boolean success;
    @JsonProperty("data.memes")
    private List<Meme> memes;
}
