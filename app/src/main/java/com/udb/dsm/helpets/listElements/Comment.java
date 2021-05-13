package com.udb.dsm.helpets.listElements;

public class Comment {
    public String commentId;
    public String commentDate;
    public String commentText;
    public String userId;
    public String userImageProfile;
    public String userName;
    public String postId;

    public Comment() {
    }

    public Comment(String commentId, String commentDate, String commentText, String userId, String userImageProfile, String userName) {
        this.commentId = commentId;
        this.commentDate = commentDate;
        this.commentText = commentText;
        this.userId = userId;
        this.userImageProfile = userImageProfile;
        this.userName = userName;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImageProfile() {
        return userImageProfile;
    }

    public void setUserImageProfile(String userImageProfile) {
        this.userImageProfile = userImageProfile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
