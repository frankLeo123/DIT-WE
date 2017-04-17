package com.bupt.weeat.activity;


import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bupt.weeat.R;

import butterknife.InjectView;

public class SearchActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void initData() {
        super.initData();
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        setTitle("搜索");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
