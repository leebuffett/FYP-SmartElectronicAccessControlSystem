package com.example.smartelectronicaccesscontrolsystem.Model;


public class Admin {

    public String adminEmail;
    public String adminName;
    public String phoneNumber;
    public String phoneCountryCode;
    public boolean twoFAStatus;
    public String adminImage;

    public Admin() {

    }

   public Admin(String adminEmail, String adminName) {
        this.adminEmail = adminEmail;
        this.adminName = adminName;
    }

    public Admin(String PhoneCountryCode, String adminEmail, String adminName, String PhoneNumber){
        this.adminEmail = adminEmail;
        this.adminName = adminName;
        this.phoneNumber=PhoneCountryCode;
        this.phoneCountryCode=PhoneNumber;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public boolean isTwoFAStatus() {
        return twoFAStatus;
    }

    public void setTwoFAStatus(boolean twoFAStatus) {
        this.twoFAStatus = twoFAStatus;
    }

    public String getAdminImage() {
        return adminImage;
    }

    public void setAdminImage(String adminImage) {
        this.adminImage = adminImage;
    }
}