package com.example.smartelectronicaccesscontrolsystem.Model;

public class authlog {

    public String id;
    public String name;
//    public String staffId;
    public String time;
    public String description;
//    public String date;


    public authlog(){

    }

    public authlog(String id, String description, String name,String time){
        this.id=id;
//        this.staffId=staffId;
        this.description=description;
        this.name=name;
        this.time=time;
//        this.date=date;
    }

//    public authlog(String staffId, String description, String name, String time){
//        this.staffId=staffId;
//        this.description=description;
//        this.name=name;
//        this.time=time;
//    }

//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getStaffId() {
//        return staffId;
//    }
//
//    public void setStaffId(String staffId) {
//        this.staffId = staffId;
//    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
