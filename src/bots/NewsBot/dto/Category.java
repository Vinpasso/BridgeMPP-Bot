package bots.NewsBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
@Data
public class Category {
    @JsonProperty("category_id")
    private int id;
    @JsonProperty("display_category_name")
    private String name;
    @JsonProperty("english_category_name")
    private String nameEng;
    @JsonProperty("url_category_name")
    private String nameUrl;
}
