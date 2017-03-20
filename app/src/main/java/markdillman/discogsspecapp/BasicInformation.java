package markdillman.discogsspecapp;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.ArrayList;
/**
 * Created by markdillman on 3/19/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder({
        "title",
        "year",
        "resource_url",
        "formats",
        "labels",
        "artists"
})
public class BasicInformation {
    @JsonProperty("title")private String title;
    @JsonProperty("year")private int year;
    @JsonProperty("resource_url")private String resource_url;
    @JsonProperty("formats")private List<Format> formats = new ArrayList<Format>();
    @JsonProperty("labels")private List<Label> labels = new ArrayList<Label>();
    @JsonProperty("artists")private List<Artist> artists = new ArrayList<Artist>();

    @JsonProperty("title")public String getTitle(){
        return title;
    }
    @JsonProperty("title")public void setTite(String title){
        this.title = title;
    }

    @JsonProperty("year")public int getYear(){
        return year;
    }
    @JsonProperty("year")public void setYear(int year){
        this.year = year;
    }

    @JsonProperty("resource_url")public int getResource_url(){
        return year;
    }
    @JsonProperty("resource_url")public void serResource_url(String resource_url){
        this.resource_url = resource_url;
    }

    @JsonProperty("formats")public List<Format> getFormats(){
        return formats;
    }
    @JsonProperty("formats")public void setFormats(List<Format> formats){
        this.formats = formats;
    }

    @JsonProperty("labels")public List<Label> getLabels(){
        return labels;
    }
    @JsonProperty("labels")public void setLabels(List<Label> labels){
        this.labels = labels;
    }

    @JsonProperty("artists")public List<Artist> getArtists(){
        return artists;
    }
    @JsonProperty("artists")public void setArtists(List<Artist> artists){
        this.artists = artists;
    }
}
