package com.bytedance.androidcamp.network.dou.api;

import com.google.gson.annotations.SerializedName;

public class PostVideoResponse {
    @SerializedName("url")
    private String url;
    @SerializedName("success")
    private boolean success;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public boolean getSuccess(){return success;}
    public void setSuccess(boolean success){this.success = success;}
}
