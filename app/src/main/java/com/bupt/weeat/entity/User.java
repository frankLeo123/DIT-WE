package com.bupt.weeat.entity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

//自定义User扩展BombUser属性，实现登录注册
public class User extends BmobUser {
    public static final String TAG = "User";
    private String sex;
    private BmobFile userImage;

    public BmobFile getUserImage() {
        return userImage;
    }

    public void setUserImage(BmobFile userImage) {
        this.userImage = userImage;
    }

    private BmobRelation loveRelation;
    private  BmobRelation zanRelation;

    public BmobRelation getZanRelation() {
        return zanRelation;
    }

    public void setZanRelation(BmobRelation zanRelation) {
        this.zanRelation = zanRelation;
    }

    public BmobRelation getLoveRelation() {
        return loveRelation;
    }

    public void setLoveRelation(BmobRelation loveRelation) {
        this.loveRelation = loveRelation;
    }



    public String getSex() {
        return sex;

    }

    public void setSex(String sex) {
        this.sex = sex;
    }

}
