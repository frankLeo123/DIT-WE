package com.bupt.weeat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.weeat.Constant;
import com.bupt.weeat.R;
import com.bupt.weeat.adapter.GoodCommentAdapter;
import com.bupt.weeat.entity.Comment;
import com.bupt.weeat.entity.User;
import com.bupt.weeat.model.DishBean;
import com.bupt.weeat.ui.SendCommentButton;
import com.bupt.weeat.utils.LogUtils;
import com.bupt.weeat.utils.ToastUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

////////////////////////////////////////////////////////////////////
//评论给写死了，改了一些
///////////////////////////////////////////////////////////////////
public class GoodDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = GoodDetailActivity.class.getSimpleName();
    private Context mContext;
    private static final int NEW_DISH_CODE = 0;
    private static final int WEEK_DISH_CODE = 1;
    @InjectView(R.id.dish_detail_image)
    ImageView dishImage;
    @InjectView(R.id.dish_detail_name)
    TextView dishName;
    @InjectView(R.id.dish_detail_price)
    TextView dishPrice;
    @InjectView(R.id.dish_detail_tastes)
    TextView dishTaste;
    @InjectView(R.id.dish_detail_window)
    TextView dishWindow;
    @InjectView(R.id.dish_detail_praise)
    TextView dishPraise;
    @InjectView(R.id.dish_detail_load_more_comments)
    TextView loadMoreComment;
    @InjectView(R.id.dish_detail_comment_bn)
    SendCommentButton dishCommentBn;
    @InjectView(R.id.dish_detail_list)
    ListView commentLv;
    @InjectView(R.id.dish_detail_comment_edit)
    EditText dishCommentEt;

    private ArrayList<Comment> list;

    private GoodCommentAdapter adapter;
    private DishBean dishObject;

    @Override
    public int getLayoutId() {
        return R.layout.good_detail_layout;
    }


    @Override
    protected void initData() {
        super.initData();
        list = new ArrayList<>();
        mContext = this;
        initToolbar();
        int DISH_CODE = getIntent().getIntExtra("DISH_CODE", 2);
        LogUtils.i(TAG, "DISH_CODE :" + DISH_CODE);
        switch (DISH_CODE) {
            case NEW_DISH_CODE:
                dishObject = (DishBean) getIntent().getSerializableExtra("new_dish_data");
                LogUtils.i(TAG, dishObject + "");
                break;
            case WEEK_DISH_CODE:
                dishObject = (DishBean) getIntent().getSerializableExtra("week_dish_data");
                LogUtils.i(TAG, dishObject + "");
                break;
            default:
                break;
        }
        setDishDetailUI();
        uploadDish();
        initListView();

    }

    @Override
    protected void setListener() {
        loadMoreComment.setOnClickListener(this);
        dishImage.setOnClickListener(this);

        dishCommentBn.setOnSendClickListener(new SendCommentButton.onSendClickListener() {
            @Override
            public void onSendClick(View view) {
                if (validateComment()) {
                    dishCommentEt.setText(null);
                    dishCommentBn.setCurrentState(SendCommentButton.STATE_DONE);

                }
            }
        });
    }

    public boolean validateComment() {
        if (isLogin()) {
            String commentContent = dishCommentEt.getText().toString().trim();
            if (commentContent.isEmpty()) {
                ToastUtils.showToast(mContext, R.string.no_empty_comment, Toast.LENGTH_SHORT);
                dishCommentBn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_empty));
                return false;
            } else {
                LogUtils.i(TAG, commentContent);
                try {
                    publishComment(commentContent);
                    hideSoftKeyBoard();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        } else {
            ToastUtils.showToast(mContext, R.string.please_login_first, Toast.LENGTH_SHORT);
            Intent intent = new Intent(GoodDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return false;
        }

    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("商品");
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

    private void setDishDetailUI() {
        try {
            if (dishObject.getImageUrl() != null) {
                Picasso.with(this)
                        .load(dishObject.getImageUrl())
                        .resize(180, 150)
                        .centerInside()
                        .into(dishImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dishName.setText(dishObject.getName());
        dishPrice.setText(dishObject.getPrice() + "元");
        dishTaste.setText(dishObject.getFlavor());
        dishWindow.setText(dishObject.getWindowName());
        LogUtils.i(TAG, dishObject.getWindowName() + "dishObject.getWindowName()");
        dishPraise.setText(dishObject.getPraise());
    }

    private void uploadDish() {
        dishObject.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
    }

    private void initListView() {
        adapter = new GoodCommentAdapter(mContext, list);
        commentLv.setAdapter(adapter);
        if (isLogin()) {
            queryComment();
        }
        setListViewHeightBasedOnChildren(commentLv);
        commentLv.setCacheColorHint(0);
        commentLv.setScrollingCacheEnabled(false);
        commentLv.setScrollContainer(false);
        commentLv.setFastScrollEnabled(true);
        commentLv.setSmoothScrollbarEnabled(true);
        commentLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showToast(GoodDetailActivity.this, "" + (position + 1), Toast.LENGTH_SHORT);
            }
        });
    }


    private void setListViewHeightBasedOnChildren(ListView listview) {
        ListAdapter adapter = listview.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = listview.getPaddingBottom() + listview.getPaddingTop();
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listview);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.height = totalHeight + listview.getDividerHeight() * (adapter.getCount() - 1);
        listview.setLayoutParams(params);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dish_detail_load_more_comments:
                if (!isLogin()) {
                    ToastUtils.showToast(mContext, R.string.please_login_first, Toast.LENGTH_SHORT);
                    Intent intent = new Intent(GoodDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    queryComment();
                }
                break;
            case R.id.dish_detail_image:
                Intent intent = new Intent(this, ImageDetailActivity.class);
                intent.putExtra("image_url",dishObject.getImageUrl());
                startActivity(intent);
            default:
                break;
        }
    }

    private void publishComment(String commentContent) {
        User user = BmobUser.getCurrentUser(mContext, User.class);
//        LogUtils.i(TAG, user.getUsername());
        LogUtils.i(TAG, "马俊辉");
        final Comment comment = new Comment();
        LogUtils.i(TAG, comment.toString());
        comment.setUser(user);
        comment.setCommentContent(commentContent);
        comment.setDishObject(dishObject);
        comment.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                ToastUtils.showToast(GoodDetailActivity.this, R.string.comment_success, Toast.LENGTH_SHORT);
                list.add(comment);
                adapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(commentLv);
                dishCommentEt.setText("");
                dishObject.update(GoodDetailActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.showToast(GoodDetailActivity.this, R.string.upload_post_fail, Toast.LENGTH_SHORT);
            }
        });
    }

    private void queryComment() {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.order("-createdAt");
        // query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        //query.addWhereRelatedTo("relation", new BmobPointer(dishObject));
        query.addWhereEqualTo("dishObject", new BmobPointer(dishObject));
        query.setLimit(Constant.NUM_PER_PAGE);
        BmobDate CurrentTime = new BmobDate(new Date(System.currentTimeMillis()));
        // query.addWhereLessThanOrEqualTo("createAt", CurrentTime);
       /* if (list.size() == 0)
            return;*/
        // query.setSkip(list.size());
        query.include("user");
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> lists) {
                if (lists.size() != 0 && lists.get(lists.size() - 1) != null) {
                    if (lists.size() < Constant.NUM_PER_PAGE)
                        loadMoreComment.setText("暂无更多评论");
                    ToastUtils.showToast(GoodDetailActivity.this, R.string.load_comment_success, Toast.LENGTH_SHORT);
                    list.addAll(lists);
                    LogUtils.i(TAG, lists.size() + "");
                    adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentLv);
                } else {
                    loadMoreComment.setText("暂无更多评论");
                    ToastUtils.showToast(GoodDetailActivity.this, R.string.no_more_comments, Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onError(int i, String s) {
                ToastUtils.showToast(GoodDetailActivity.this, R.string.query_fail, Toast.LENGTH_SHORT);

            }
        });

    }

    /////////////////////////////////////////////
    ///写死了
    //////////////////////////////////////////////
    public boolean isLogin() {
        BmobUser user = BmobUser.getCurrentUser(this, User.class);
        if (user != null) {
            return true;
        }
//        return false;
        return true;
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dishCommentEt.getWindowToken(), 0);

    }


}
