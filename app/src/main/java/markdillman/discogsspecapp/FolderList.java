package markdillman.discogsspecapp;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markdillman on 3/19/17.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "folders"
})
public class FolderList {
    @JsonProperty("folders") private List<Folder> folders = new ArrayList<Folder>();
    @JsonProperty("folders")
    public List<Folder> getFolders(){
        return folders;
    }
    @JsonProperty("folders")
    public void setFolders(List<Folder> folders){
        this.folders = folders;
    }
}
