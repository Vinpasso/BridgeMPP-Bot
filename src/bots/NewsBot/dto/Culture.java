package bots.NewsBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
@Data
public class Culture {
    @JsonProperty("culture_code")
    private String id;
    @JsonProperty("display_culture_name")
    private String name;
    @JsonProperty("english_culture_name")
    private String nameEng;
}
