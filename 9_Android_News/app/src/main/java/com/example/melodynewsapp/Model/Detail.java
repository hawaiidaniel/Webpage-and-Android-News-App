package com.example.melodynewsapp.Model;

public class Detail {
    private String title;
    private String section;
    private String date;
    private String description;
    private String image;
    private String sharedlink;

    public Detail() {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSharedlink() {
        return sharedlink;
    }

    public void setSharedlink(String sharedlink) {
        this.sharedlink = sharedlink;
    }
}
