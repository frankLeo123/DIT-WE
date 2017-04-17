package com.bupt.weeat.activity;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bupt.weeat.R;

import butterknife.InjectView;

//应该删除
////////////////////////////////////////////////////
public class HeatStatisticsActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.add_today_recipe)
    FloatingActionButton fab;
    @Override
    public int getLayoutId() {
        return R.layout.activity_heat_statistic;
    }


    @Override
    protected void initData() {
        super.initData();
        setSupportActionBar(toolbar);
        setTitle("健康饮食");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeatStatisticsActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }
}
