package com.example.amr.sampleapptounderstandapis;

public class MainGridItem {

    private String title;
    private String imageURL;
    private String Published_date;

    public MainGridItem() {
        super();
    }

    public MainGridItem(String title, String imageURL, String published_date) {
        this.title = title;
        this.imageURL = imageURL;
        Published_date = published_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPublished_date() {
        return Published_date;
    }

    public void setPublished_date(String published_date) {
        Published_date = published_date;
    }
}