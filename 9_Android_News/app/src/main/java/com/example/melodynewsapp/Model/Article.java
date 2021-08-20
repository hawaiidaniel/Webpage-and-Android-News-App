package com.example.melodynewsapp.Model;

public class Article {
    private String title;
    private String section;
    private String date;
    private String identity;
    private String image;

    public Article() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDate() { return date; }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}