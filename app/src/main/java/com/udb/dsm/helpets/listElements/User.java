package com.udb.dsm.helpets.listElements;

public class User {
    public String userId;
    public String userName;
    public String userEmail;
    public String userImageProfile;
    public String userImageBackground;
    public String userAddress;
    public String userPhone;

    public User() {}

    public User(String userId, String userName, String userEmail, String userImageProfile, String userImageBackground, String userAddress, String userPhone) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userImageProfile = userImageProfile;
        this.userImageBackground = userImageBackground;
        this.userAddress = userAddress;
        this.userPhone = userPhone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImageProfile() {
        return userImageProfile;
    }

    public void setUserImageProfile(String userImageProfile) {
        this.userImageProfile = userImageProfile;
    }

    public String getUserImageBackground() {
        return userImageBackground;
    }

    public void setUserImageBackground(String userImageBackground) {
        this.userImageBackground = userImageBackground;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
