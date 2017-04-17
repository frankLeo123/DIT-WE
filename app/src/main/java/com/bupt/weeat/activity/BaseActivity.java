package com.bupt.weeat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bupt.weeat.R;
import com.bupt.weeat.db.SharedPreferencesHelper;
import com.bupt.weeat.utils.ActivityUtils;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不是很懂逻辑
        if (!SharedPreferencesHelper.getTheme(this)) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeNight);
        }
        context=getApplicationContext();
        setContentView(getLayoutId());
        ButterKnife.inject(this);
        initData();
        setListener();
        Log.i("BaseActivity", getClass().getSimpleName());
        ActivityUtils.addActivity(this);
    }

    public abstract int getLayoutId();

    protected void initData() {

    }


    protected void setListener() {

    }

    protected void switchActivity(Class clazz, boolean finished) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if (finished)
            this.finish();

    }

    protected void switchFragment(Fragment from, Fragment to, int id, String tag) {
        if (to == null) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (from == null) {
            transaction.add(id, to, tag);
        } else {
            transaction.hide(from);
            if (to.isAdded()) {
                transaction.show(to);
            } else {
                transaction.add(id, to, tag);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtils.removeActivity(this);
    }
}
