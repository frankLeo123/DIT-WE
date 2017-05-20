package com.bupt.weeat.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bupt.weeat.Constant;
import com.bupt.weeat.R;
import com.bupt.weeat.activity.BaseActivity;
import com.bupt.weeat.activity.NewsDetailsActivity;
import com.bupt.weeat.adapter.HotNewsRecyclerAdapter;
import com.bupt.weeat.entity.DailyNews;
import com.bupt.weeat.ui.PullLoadRecyclerView;
import com.bupt.weeat.ui.SpacesItemDecoration;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

//收藏界面暂时照搬NewGoodFragment
public class CollectFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, HotNewsRecyclerAdapter.OnHotItemClickListener {

    //数据是否加载完毕
    protected boolean isDataLoaded = false;
    //视图是否创建完毕
    protected boolean isViewCreated = false;

    //新闻数据
    private List<DailyNews.StoriesBean> mNewsStories;
    private HotNewsRecyclerAdapter mAdapter;
    private String mDate;
    private Gson mGson;
    private boolean isFirst = true;

    @InjectView(R.id.srl_content)
    protected SwipeRefreshLayout mContentSwipeRefreshLayout;

    @InjectView(R.id.rv_content)
    protected PullLoadRecyclerView mContentRecyclerView;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x122:
                    mContentSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(context, "网络没有链接哦", Toast.LENGTH_SHORT);
                    break;
                case 0x121:
                    loadDataFromNet(getFirstPageUrl());
            }
        }
    };

    @Override
    protected void initData() {
        super.initData();
        mNewsStories = new ArrayList<>();
        mContentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new HotNewsRecyclerAdapter((BaseActivity) context, mNewsStories);
        mAdapter.setHotItemNewsClickListener(this);
        SpacesItemDecoration decoration = new SpacesItemDecoration(2);
        mContentRecyclerView.addItemDecoration(decoration);
        mContentRecyclerView.setAdapter(mAdapter);
        mContentRecyclerView.setPullLoadListener(new PullLoadRecyclerView.onPullLoadListener() {
            @Override
            public void onPullLoad() {
                loadMoreData(mDate);
            }
        });
        mGson = new Gson();
        initSwipeRefreshLayout();
        isViewCreated=true;
    }


    private void initSwipeRefreshLayout() {
        mContentSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color.holo_red_dark);
        mContentSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isDataLoaded && isViewCreated) {
            initData();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reading;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            loadData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mContentSwipeRefreshLayout.isRefreshing()) {
            mContentSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context = null;
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessage(0x122);
    }


    public String getFirstPageUrl() {
        return Constant.ZHIHU_BASIC_URL + Constant.ZHIHU_LATEST;
    }

    /**
     * 加载更多数据
     *
     * @param latestDate
     */
    private void loadMoreData(final String latestDate) {
        JsonObjectRequest moreRequest = new JsonObjectRequest(Constant.ZHIHU_BASIC_URL + Constant.ZHIHU_MORE + latestDate, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    DailyNews dailyNews = mGson.fromJson(response.toString(), DailyNews.class);
                    mDate = dailyNews.getDate();
                    mNewsStories = dailyNews.getStories();
                    mAdapter.addListData(mNewsStories);
                }
                mContentRecyclerView.setIsLoading(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        client.addTask(moreRequest);
    }

    /**
     * 加载第一页数据
     *
     * @param firstPageUrl
     */
    private void loadDataFromNet(String firstPageUrl) {
        JsonObjectRequest lastestRequest = new JsonObjectRequest(firstPageUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            DailyNews dailyNews = mGson.fromJson(response.toString(), DailyNews.class);
                            mAdapter.clearList();
                            mDate = dailyNews.getDate();
                            mNewsStories = dailyNews.getStories();
                            mAdapter.addListData(mNewsStories);
                            mContentSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        client.addTask(lastestRequest);
    }

    private void loadData() {
        mContentSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mContentSwipeRefreshLayout.setRefreshing(true);
            }
        });
        loadDataFromNet(getFirstPageUrl());
        isDataLoaded=true;
    }

    @Override
    public void onHotItemClick(View view, DailyNews.StoriesBean bean) {
        int[] clickLocation = getClickLocation(view);
        startActivity(NewsDetailsActivity.newTopStoriesIntent(context, "4", String.valueOf(bean.getId()), clickLocation));
        context.overridePendingTransition(0, 0);
    }

    protected int[] getClickLocation(View view) {
        int[] clickLocation = new int[2];
        view.getLocationOnScreen(clickLocation);
        clickLocation[0] += view.getWidth() / 2;
        return clickLocation;
    }
}
