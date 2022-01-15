package com.example.mdpmessenger.Models;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String search;

    public User(String id, String username, String imageURL, String search){
        this.id=id;
        this.username = username;
        this.imageURL = imageURL;
        this.search = search;
    }

    public User(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSearch() {
        return search;
    }
}