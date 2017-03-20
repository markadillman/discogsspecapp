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
        "releases"
})
public class ReleaseList {
    @JsonProperty("releases")private List<Release> releases = new ArrayList<Release>();
    @JsonProperty("releases")public void setReleases(List<Release> releases){
        this.releases = releases;
    }
    @JsonProperty("releases")public List<Release> getReleases(){
        return releases;
    }
}
