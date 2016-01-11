package com.bromancelabs.photogallery.models;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {
    private static final String PHOTO_URL = "http://www.flickr.com/photos/";

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("owner")
    @Expose
    private String owner;

    @SerializedName("secret")
    @Expose
    private String secret;

    @SerializedName("server")
    @Expose
    private String server;

    @SerializedName("farm")
    @Expose
    private Integer farm;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("ispublic")
    @Expose
    private Integer ispublic;

    @SerializedName("isfriend")
    @Expose
    private Integer isfriend;

    @SerializedName("isfamily")
    @Expose
    private Integer isfamily;

    @SerializedName("url_s")
    @Expose
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getFarm() {
        return farm;
    }

    public void setFarm(Integer farm) {
        this.farm = farm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIspublic() {
        return ispublic;
    }

    public void setIspublic(Integer ispublic) {
        this.ispublic = ispublic;
    }

    public Integer getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(Integer isfriend) {
        this.isfriend = isfriend;
    }

    public Integer getIsfamily() {
        return isfamily;
    }

    public void setIsfamily(Integer isfamily) {
        this.isfamily = isfamily;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Uri getPhotoUri() {
        return Uri.parse(url);
    }

    public Uri getPhotoPageUri() {
        return Uri.parse(PHOTO_URL + owner + "/" + id);
    }

    @Override
    public String toString() {
        return "{Title: " + title + "}";
    }
}
