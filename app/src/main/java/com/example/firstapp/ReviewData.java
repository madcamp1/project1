package com.example.firstapp;

public class ReviewData {
    private String title;
    private String description;
    private String postdate;
    private String link;

    ReviewData(){}

    ReviewData(String title, String description, String postdate, String link){
        this.title = title;
        this.description = description;
        this.postdate = postdate;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
