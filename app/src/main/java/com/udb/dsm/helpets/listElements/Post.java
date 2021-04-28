package com.udb.dsm.helpets.listElements;

public class Post {
    public String postId;
    public String userId;
    public String userName;
    public String userImageProfile;
    public String postDate;
    public String userAddress;
    public String postTitle;
    public String postDescription;


    public Post() {}

    public Post(String userId, String userName, String userImageProfile, String postDate, String userAddress, String postTitle, String postDescription) {
        this.userId = userId;
        this.userName = userName;
        this.userImageProfile = userImageProfile;
        this.postDate = postDate;
        this.userAddress = userAddress;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public String getUserImageProfile() {
        return userImageProfile;
    }

    public void setUserImageProfile(String userImageProfile) {
        this.userImageProfile = userImageProfile;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }
}
