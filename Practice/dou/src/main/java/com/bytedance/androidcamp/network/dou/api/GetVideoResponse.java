package com.bytedance.androidcamp.network.dou.api;

import com.bytedance.androidcamp.network.dou.model.Video;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetVideoResponse {
    @SerializedName("feeds")
    private List<Video> videos;
    @SerializedName("success")
    private boolean success;

    public boolean getSuccess(){return success;}
    public void setSuccess(boolean success){this.success = success;}

    public void setVideos(List<Video> videos){this.videos = videos;}
    public List<Video> getVideos(){return videos;}
}
