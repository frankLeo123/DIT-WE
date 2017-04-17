package com.bupt.weeat;

import android.app.Application;
import android.content.Context;

import com.bupt.weeat.entity.User;
import com.squareup.leakcanary.LeakCanary;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

//LeakCanary开源检测内存泄露的
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Context mContext;
    private static MyApplication myApplication = null;


    public static MyApplication getMyApplication() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        mContext = getApplicationContext();
        LeakCanary.install(this);
        Bmob.initialize(myApplication, Constant.APPLICATION_ID);

    }

    public static Context getContext() {
        return mContext;
    }

   /* public  boolean isLogin() {

        return getCurrentUser() != null;

    }*/
    public boolean isLogin() {
        BmobUser user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null) {
            return true;
        }
        return false;
    }
    public  User getCurrentUser() {
        User user=BmobUser.getCurrentUser(mContext, User.class);
        if (user!=null)
        return user;
        return null;
    }
}

