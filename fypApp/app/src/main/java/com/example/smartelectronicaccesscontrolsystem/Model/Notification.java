package com.example.smartelectronicaccesscontrolsystem.Model;


public class Notification {

    public String id;
    public String title;
    public String date;
    public String time;
    public String description;
    public String image;

    public Notification(){

    }

    public Notification(String id, String title, String description, String date, String time,String image){
        this.id=id;
        this.title=title;
        this.description=description;
        this.date=date;
        this.time=time;
        this.image=image;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
