package com.example.smartelectronicaccesscontrolsystem.Model;

public class staff {
    public String name;
    public String age;
    public String phoneNumber;
    public String countryCode;
    public String email;
    public String dateOfBirth;
    public String gender;
    public String image;
    public String id;

    public staff() {

    }
    public staff(String id, String name, String age, String phoneNumber, String countryCode,String email,String dateOfBirth,String gender,String image){
        this.id=id;
        this.name = name;
        this.age = age;
        this.phoneNumber=phoneNumber;
        this.countryCode=countryCode;
        this.email=email;
        this.dateOfBirth=dateOfBirth;
        this.gender=gender;
        this.image=image;
    }


    public staff(String id, String name, String age, String phoneNumber, String countryCode,String email,String dateOfBirth,String gender){
        this.id=id;
        this.name = name;
        this.age = age;
        this.phoneNumber=phoneNumber;
        this.countryCode=countryCode;
        this.email=email;
        this.dateOfBirth=dateOfBirth;
        this.gender=gender;

    }

//    public staff(String name, String age, String phoneNumber, String countryCode,String email,String dateOfBirth,String gender,String image){
//        this.name = name;
//        this.age = age;
//        this.phoneNumber=phoneNumber;
//        this.countryCode=countryCode;
//        this.email=email;
//        this.dateOfBirth=dateOfBirth;
//        this.gender=gender;
//        this.image=image;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
