package com.bupt.weeat.activity;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bupt.weeat.R;
import com.bupt.weeat.adapter.RecipeAdapter;
import com.bupt.weeat.ui.RecyclerItemClickListener;

import butterknife.InjectView;

public class RecipeRecommendActivity extends BaseActivity {
    @InjectView(R.id.recipe_recycler)
    RecyclerView recyclerView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    public int getLayoutId() {
        return R.layout.activity_recipe_recommend;
    }

    @Override
    protected void initData() {
        super.initData();
        setSupportActionBar(toolbar);
        setTitle("食谱推荐");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        RecipeAdapter adapter=new RecipeAdapter(this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
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
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }
}
