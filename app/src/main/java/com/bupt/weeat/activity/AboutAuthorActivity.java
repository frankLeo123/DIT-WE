package com.bupt.weeat.activity;


import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bupt.weeat.R;

import butterknife.InjectView;

public class AboutAuthorActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    public int getLayoutId() {
        return R.layout.about_author_activity;
    }

    @Override
    protected void initData() {
        super.initData();
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
