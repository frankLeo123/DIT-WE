package com.bupt.weeat.entity;


import com.bupt.weeat.model.GoodBean;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject {
    private User user;
    private String commentContent;
    private Post post;
    private GoodBean dishObject;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public GoodBean getDishObject() {
        return dishObject;
    }

    public void setDishObject(GoodBean dishObject) {
        this.dishObject = dishObject;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
