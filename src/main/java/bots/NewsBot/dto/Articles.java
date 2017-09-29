package bots.NewsBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
@Data
public class Articles {
    private Article[] articles;
    @JsonProperty("syndication_url")
    private String syndicationUrl;
}
