package com.bupt.weeat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bupt.weeat.Constant;
import com.bupt.weeat.R;
import com.bupt.weeat.entity.NewsDetails;
import com.bupt.weeat.ui.RevealBackgroundView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import net.AsyncHttpClient;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by windylee on 4/14/17.
 */

public class NewsDetailsActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {

    public static final String URL = "url";
    public static final String PATH = "path";
    public static final String LOCATION = "location";
    public NewsDetails mTopDetails;
    public int[] mLocation;
    private String url;
    private String path;
    private boolean isCollected = false;

    @InjectView(R.id.rbv_content)
    RevealBackgroundView mContentRevealBackgroundView;
    @InjectView(R.id.iv_title)
    ImageView mTitleImageView;
    @InjectView(R.id.ctl_title)
    CollapsingToolbarLayout mTitleCollapsingToolbarLayout;
    @InjectView(R.id.wb_content)
    WebView mContentWebView;
    @InjectView(R.id.nsv_content)
    NestedScrollView mContentNestedScrollView;
    @InjectView(R.id.abl_content)
    AppBarLayout mcontentAppBarLayout;
    @InjectView(R.id.tb_title)
    Toolbar mTitleToolbar;

    public static Intent newTopStoriesIntent(Activity activity, String path, String url, int[] clickLocation) {

        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        bundle.putString(PATH, path);
        bundle.putIntArray(LOCATION, clickLocation);
        Intent intent = new Intent(activity, NewsDetailsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.inject(this);

        setSupportActionBar(mTitleToolbar);
        mTitleToolbar.setNavigationIcon(R.drawable.back);

        WebSettings settings = mContentWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);

        parseIntent();
        initData();

        mContentRevealBackgroundView.setOnStateChangeListener(this);
        if (mLocation == null || savedInstanceState != null) {
            mContentRevealBackgroundView.setToFinishedFrame();
        } else {
            mContentRevealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mContentRevealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mContentRevealBackgroundView.startFromLocation(mLocation);
                    return true;
                }
            });
        }
    }

    protected void initData() {
        loadContent();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        url = bundle.getString(URL);
        path = bundle.getString(PATH);
        mLocation = bundle.getIntArray(LOCATION);
    }

    private void loadContent() {
        Gson mGson = new Gson();
//        if (!mCache.isCacheEmpty(url)) {
//            mTopDetails = mGson.fromJson(mCache.getAsString(url), NewsDetails.class);
//            initAppBarLayout();
//            loadWebView();
//        } else {
//            if (NetUtils.hasNetWorkConection(this)) {
        loadLatestData();
//            } else {
//                ToastUtils.showToast(this, "网络没有连接哦");
//            }
    }

    private void loadLatestData() {
        JsonObjectRequest detailsRequest=new JsonObjectRequest(Constant.ZHIHU_BASIC_URL + path + "/news/" + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mTopDetails=new Gson().fromJson(response.toString(), NewsDetails.class);
                initAppBarLayout();
                loadWebView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("newdetails", volleyError.getMessage());
            }
        });
        AsyncHttpClient.getInstance(this).addTask(detailsRequest);
    }

    private void initAppBarLayout() {
        Picasso.with(this).load(mTopDetails.getImage()).into(mTitleImageView);
        mTitleCollapsingToolbarLayout.setTitle(mTopDetails.getTitle());
    }

    private void loadWebView() {
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news\" " +
                "type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + mTopDetails.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        mContentWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mContentWebView != null) {
            mContentWebView.removeAllViews();
            mContentWebView.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mContentWebView != null) {
            mContentWebView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mContentWebView != null) {
            mContentWebView.onResume();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            mContentNestedScrollView.setVisibility(View.VISIBLE);
            mcontentAppBarLayout.setVisibility(View.VISIBLE);
        } else {
            mContentNestedScrollView.setVisibility(View.GONE);
            mcontentAppBarLayout.setVisibility(View.GONE);
        }
    }
}
