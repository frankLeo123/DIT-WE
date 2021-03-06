package com.bupt.weeat.utils;

import android.content.Context;

import com.bupt.weeat.entity.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

//Bomb实现登录注册 改了一波
///////////////////////////////////////////////////////////////
public class UserUtils  {
    private static final String TAG="UserUtils";
    private Context mContext;

    public UserUtils(Context mContext) {
        this.mContext = mContext;
    }

    //用户注册SignUp 邮箱，用户名，密码
    public void SignUp(String userMail,String userName,String Password){
        User user=new User();
        user.setUsername(userName);
        user.setEmail(userMail);
        user.setPassword(Password);
//        user.signUp(mContext, new SaveListener() {
//            @Override
//            public void onSuccess() {
//                mSignUpListener.onSignUpSuccess();
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                 mSignUpListener.onSignUpFailure(s);
//            }
////            public void onFailure(int i, String s) {
////                 mSignUpListener.onSignUpSuccess();
////            }
//        });
        ///////////////////////////////////////////////
        //需要<>写入User，改为自定义User
        ///////////////////////////////////////////////
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    mSignUpListener.onSignUpSuccess();
                }else{
                    mSignUpListener.onSignUpFailure();
                }
            }
        });
    }
    private SignUpListener mSignUpListener;
    public interface SignUpListener{
        void onSignUpSuccess();
//        void onSignUpFailure(String s);
        //都改为空
        void onSignUpFailure();

    }
    public void setSignUpListener(SignUpListener mSignUpListener){
        this.mSignUpListener=mSignUpListener;

    }
    //用户登录SignIn 用户名和密码
    public void Login(String userName,String Password){
        User user=new User();
        user.setPassword(Password);
        user.setUsername(userName);
//        user.login(mContext, new SaveListener() {
//            @Override
//            public void onSuccess() {
//              mLoginListener.onLoginSuccess();
//            }
//
//            //搞一波
//            ////////////////////////////////////////////////////////////////////////////////////
//            @Override
//            public void onFailure(int i, String s) {
//                mLoginListener.onLoginFailure(s);
//            }
////            public void onFailure(int i, String s) {
////                mLoginListener.onLoginSuccess();
////            }
//        });
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    mLoginListener.onLoginSuccess();
                }else{
                    mLoginListener.onLoginFailure();
                }
            }
        });

    }
   private LoginListener mLoginListener;
    public interface LoginListener{
       void onLoginSuccess();
//       void onLoginFailure(String s);
       void onLoginFailure();
   }
    public void setLoginListener(LoginListener mLoginListener){
              this.mLoginListener=mLoginListener;

    }

}
