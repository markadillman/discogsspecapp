package markdillman.discogsspecapp;

import com.fasterxml.jackson.annotation.*;

/**
 * Created by markdillman on 3/19/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder({
        "id",
        "rating",
        "instance_id",
        "basic_information"
})
public class Release {
    @JsonProperty("id")private int id;
    @JsonProperty("rating")private int rating;
    @JsonProperty("instance_id")private int instance_id;
    @JsonProperty("basic_information") BasicInformation basic_information = new BasicInformation();

    @JsonProperty("id")public void setId(int id){
        this.id = id;
    }
    @JsonProperty("id")public int getId(){
        return id;
    }

    @JsonProperty("rating")public void setRating(int rating){
        this.rating = rating;
    }
    @JsonProperty("rating")public int getRating(){
        return rating;
    }

    @JsonProperty("instance_id")public void setInstance_id(int instance_id){
        this.instance_id = instance_id;
    }
    @JsonProperty("instance_id")public int getInstance_id(){
        return instance_id;
    }

    @JsonProperty("basic_information")public void setBasic_information(BasicInformation basic_information){
        this.basic_information=basic_information;
    }
    @JsonProperty("basic_information")public BasicInformation getBasic_information(){
        return basic_information;
    }
}
