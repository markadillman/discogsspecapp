package markdillman.discogsspecapp;

import com.fasterxml.jackson.annotation.*;

/**
 * Created by markdillman on 3/19/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "count",
        "resource_url",
        "id",
        "name"
})

public class Folder {

    @JsonInclude(JsonInclude.Include.NON_NULL)

    @JsonProperty("count")private int count;
    @JsonProperty("resource_url")private String resource_url;
    @JsonProperty("id")private int id;
    @JsonProperty("name")private String name;
    public Folder(){}
    public Folder(int count,String resource_url,int id,String name){
        this.count = count;
        this.name = name;
        this.resource_url = resource_url;
        this.id = id;
    }
    @JsonProperty("count")
    public int getCount(){
        return count;
    }
    @JsonProperty("count")
    public void setCount(int count){
        this.count = count;
    }
    @JsonProperty("resource_url")
    public String getResource_url(){
        return resource_url;
    }
    @JsonProperty("resource_url")
    public void setResource_url(String resource_url){
        this.resource_url = resource_url;
    }
    @JsonProperty("id")
    public int getId(){
        return id;
    }
    @JsonProperty("id")
    public void setId(int id){
        this.id=id;
    }
    @JsonProperty("name")
    public String getName(){
        return name;
    }
    @JsonProperty("name")
    public void setName(String name){
        this.name = name;
    }
}

