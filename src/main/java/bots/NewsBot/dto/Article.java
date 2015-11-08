package bots.NewsBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
@Data
public class Article {
    private String url;
    private String title;
    private String summary;
    @JsonProperty("publish_date")
    private Date date;
    private String author;
    private String source;
}
