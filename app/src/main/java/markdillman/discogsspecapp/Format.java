package markdillman.discogsspecapp;

import com.fasterxml.jackson.annotation.*;

/**
 * Created by markdillman on 3/19/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder({
        "name"
})
public class Format {
    @JsonProperty("name")private String name;
    @JsonProperty("name")public void setName(String name){
        this.name = name;
    }
    @JsonProperty("name")public String getName(){
        return name;
    }
}
