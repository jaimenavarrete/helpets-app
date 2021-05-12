package com.udb.dsm.helpets.listElements;

import java.util.HashMap;
import java.util.List;

public class Post {
    public String postId;
    public String postDate;
    public String postAddress;
    public String postTitle;
    public String postDescription;
    public String postCategory;
    public String postImage;
    public int postLikes;

    public String userId;
    public String userName;
    public String userImageProfile;

    public Post() {}

    public Post(String userId, String userName, String userImageProfile, String postDate, String postAddress, String postTitle, String postDescription, String postCategory, String postImage, int postLikes) {
        this.userId = userId;
        this.userName = userName;
        this.userImageProfile = userImageProfile;
        this.postDate = postDate;
        this.postAddress = postAddress;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.postCategory = postCategory;
        this.postImage = postImage;
        this.postLikes = postLikes;
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

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
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

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
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

    public int getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(int postLikes) {
        this.postLikes = postLikes;
    }
}
