package com.bupt.weeat.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bupt.weeat.Constant;
import com.bupt.weeat.R;
import com.bupt.weeat.adapter.CommentListAdapter;
import com.bupt.weeat.entity.Comment;
import com.bupt.weeat.entity.Post;
import com.bupt.weeat.entity.User;
import com.bupt.weeat.ui.CircleTransformation;
import com.bupt.weeat.ui.SendCommentButton;
import com.bupt.weeat.utils.LogUtils;
import com.bupt.weeat.utils.ToastUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

//评论界面未粗线
public class CommentActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.post_comment_list)
    ListView commentList;
    @InjectView(R.id.post_comment_et)
    EditText commentEt;
    @InjectView(R.id.post_comment_button)
    SendCommentButton commentButton;
    @InjectView(value = R.id.pic1)
    public ImageView pic1;
    @InjectView(value = R.id.square_user_avatar_iv)
    public ImageView userAvatar;
    @InjectView(value = R.id.square_praise_post_iv)
    public ImageView praiseImg;
    @InjectView(value = R.id.square_share_post_iv)
    public ImageView shareImg;
    @InjectView(value = R.id.square_user_name_tv)
    TextView userName;
    @InjectView(value = R.id.square_post_content)
    TextView postContent;
    @InjectView(value = R.id.square_post_create_time)
    TextView postCreateTime;
    @InjectView(value = R.id.square_post_image_list)
    LinearLayout postImageList;
    @InjectView(value = R.id.praise_post_counter)
    TextSwitcher likeCounterSwitcher;
    @InjectView(value = R.id.comment_post_counter)
    TextSwitcher commentCounterSwitcher;
    @InjectView(value = R.id.share_post_counter)
    TextSwitcher shareCounterSwitcher;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;


    private List<Comment> lists;
    private BaseAdapter commentAdapter;
    private Post post;
    private static final String TAG = CommentActivity.class.getSimpleName();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);


    @Override
    public int getLayoutId() {
        return R.layout.comment_activity;
    }

    @Override
    protected void initData() {
        super.initData();
        //初始化user评论
        lists = new ArrayList<>();
        initToolbar();
        initComment();
        setupListView();


    }

    private void initComment() {
        post = (Post) getIntent().getSerializableExtra("data");
        LogUtils.i(TAG, "post :" + post);
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("评论");
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

    private void setupListView() {
        commentAdapter = new CommentListAdapter(this, lists);
        commentList.setAdapter(commentAdapter);
        commentList.setCacheColorHint(0);
        commentList.setScrollingCacheEnabled(false);
        commentList.setScrollContainer(false);
        commentList.setFastScrollEnabled(true);
        commentList.setSmoothScrollbarEnabled(true);
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showToast(CommentActivity.this, "" + position, Toast.LENGTH_SHORT);
            }
        });
        queryComment();
        initPostView();
    }


    private void initPostView() {
        User user = BmobUser.getCurrentUser( User.class);
        LogUtils.i(TAG, "user :" + user);
        if (user.getUserImage() != null) {
            String avatarUri = user.getUserImage().getFileUrl();
            if (avatarUri != null) {
                Picasso.with(this).
                        load(Uri.parse(avatarUri)).
                        centerCrop()
                        .resize(72, 72)
                        .placeholder(R.drawable.tou_xiang)
                        .transform(new CircleTransformation())
                        .into(userAvatar);
            }
        }
        userName.setText(user.getUsername());
        postCreateTime.setText(post.getCreatedAt());
        postContent.setText(post.getContent());
        if (null == post.getPostImageFile()) {
            postImageList.setVisibility(View.GONE);
        } else {
            postImageList.setVisibility(View.VISIBLE);
            String pictureUri = post.getPostImageFile().getFileUrl();
            if (pictureUri != null) {
                Glide.clear(pic1);
                Glide.with(this).
                        load(Uri.parse(pictureUri)).
                        diskCacheStrategy(DiskCacheStrategy.RESULT).
                        into(pic1);
            }
        }
        likeCounterSwitcher.setText(post.getPraiseNum() + " " + "赞");
        commentCounterSwitcher.setText(post.getCommentNum() + " " + "评论");
        shareCounterSwitcher.setText(post.getShareNum() + " " + "分享");

        if (post.isMyPraise()) {
            praiseImg.setImageResource(R.drawable.ic_heart_red);
        } else {
            praiseImg.setImageResource(R.drawable.ic_heart_outline_grey);
        }

    }

    @Override
    public void setListener() {
        postImageList.setOnClickListener(this);
        praiseImg.setOnClickListener(this);
        userAvatar.setOnClickListener(this);
        commentButton.setOnSendClickListener(new SendCommentButton.onSendClickListener() {
            @Override
            public void onSendClick(View view) {
                if (onClickCommentButton()) {
                    commentEt.setText(null);
                    commentButton.setCurrentState(SendCommentButton.STATE_DONE);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_share_layout:
                onClickShare();
                break;
            case R.id.square_praise_post_iv:
                onClickPraise();
                break;
            case R.id.square_user_avatar_iv:
                onClickUserAvatar();
                break;
            case R.id.post_comment_et:
                onClickCommentEt();
                break;
            default:
                break;
        }

    }

    private void onClickCommentEt() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentEt, 0);
    }

    private boolean onClickCommentButton() {
        String commentContent = commentEt.getText().toString().trim();
        LogUtils.i(TAG, commentContent);
        if (!TextUtils.isEmpty(commentContent)) {
            User user = BmobUser.getCurrentUser(User.class);
            LogUtils.i(TAG, "user :" + user);
            publishComment(user, commentContent);
            return true;
        } else {
            commentButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_empty));
            ToastUtils.showToast(this, R.string.no_empty_content, Toast.LENGTH_SHORT);
            return false;
        }
    }

    private void publishComment(final User user, String commentContent) {
        LogUtils.i(TAG, " publishComment");
        final Comment comment = new Comment();
        comment.setUser(user);
        comment.setCommentContent(commentContent);
//        comment.save(this, new SaveListener() {
//            @Override
//            public void onSuccess() {
//                ToastUtils.showToast(CommentActivity.this, R.string.comment_success, Toast.LENGTH_SHORT);
//                try {
//                    if (commentAdapter.getCount() < Constant.NUM_PER_PAGE) {
//                        lists.add(comment);
//                        commentAdapter.notifyDataSetChanged();
//                        commentEt.setText("");
//                        hideSoftKeyBoard();
//                        setListViewHeightBasedOnChildren(commentList);
//                        LogUtils.i(TAG, commentList + "");
//                     /*   commentEt.setText("");
//                        hideSoftKeyBoard();*/
//                        BmobRelation relation = new BmobRelation();
//                        relation.add(comment);
//                        post.setRelation(relation);
//                        post.setCommentNum(post.getCommentNum() + 1);
//                        setupCommentsCounter(commentCounterSwitcher);
//                        post.update(CommentActivity.this, new UpdateListener() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onFailure(int i, String s) {
//
//                            }
//                        });
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//
//            public void onFailure(int i, String s) {
//                ToastUtils.showToast(CommentActivity.this, R.string.comment_fail, Toast.LENGTH_SHORT);
//            }
//        });
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    ToastUtils.showToast(CommentActivity.this, R.string.comment_success, Toast.LENGTH_SHORT);
                try {
                    if (commentAdapter.getCount() < Constant.NUM_PER_PAGE) {
                        lists.add(comment);
                        commentAdapter.notifyDataSetChanged();
                        commentEt.setText("");
                        hideSoftKeyBoard();
                        setListViewHeightBasedOnChildren(commentList);
                        LogUtils.i(TAG, commentList + "");
                     /*   commentEt.setText("");
                        hideSoftKeyBoard();*/
                        BmobRelation relation = new BmobRelation();
                        relation.add(comment);
                        post.setRelation(relation);
                        post.setCommentNum(post.getCommentNum() + 1);
                        setupCommentsCounter(commentCounterSwitcher);
//                        post.update(new UpdateListener() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onFailure(int i, String s) {
//
//                            }
//                        });
                        post.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e==null){

                                }else{

                                }
                            }
                        });
                    }
                } catch (Exception e1) {
                    e.printStackTrace();
                }}
                else{

                }
            }
        });


    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEt.getWindowToken(), 0);

    }

    private void onClickUserAvatar() {
        Intent intent = new Intent(CommentActivity.this, UserActivity.class);
        startActivity(intent);

    }


    private void onClickPraise() {
        LogUtils.i(TAG, "onClickPraise");
        final User user = BmobUser.getCurrentUser(User.class);
        final BmobRelation relation = new BmobRelation();
        if (!post.isMyPraise()) {
            setupPraiseAnim();
            post.setMyPraise(true);
            praiseImg.setImageResource(R.drawable.ic_heart_red);
            relation.add(post);
            user.setLoveRelation(relation);
//            user.update(this, new UpdateListener() {
//                @Override
//                public void onSuccess() {
//                    ToastUtils.showToast(CommentActivity.this, R.string.collect_success, Toast.LENGTH_SHORT);
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//                    ToastUtils.showToast(CommentActivity.this, R.string.collect_fail, Toast.LENGTH_SHORT);
//                    post.setMyPraise(false);
//                    relation.remove(post);
//                    user.setLoveRelation(relation);
//                }
//            });
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        ToastUtils.showToast(CommentActivity.this, R.string.collect_success, Toast.LENGTH_SHORT);
                    }else{
                        ToastUtils.showToast(CommentActivity.this, R.string.collect_fail, Toast.LENGTH_SHORT);
                        post.setMyPraise(false);
                        relation.remove(post);
                        user.setLoveRelation(relation);
                    }
                }
            });
//            post.update(this, new UpdateListener() {
//                @Override
//                public void onSuccess() {
//                    post.setPraiseNum(post.getPraiseNum() + 1);
//                    setupLikesCounter(likeCounterSwitcher);
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//                    post.setMyPraise(false);
//                }
//            });
            post.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        post.setPraiseNum(post.getPraiseNum() + 1);
                        setupLikesCounter(likeCounterSwitcher);
                    }else{
                        post.setMyPraise(false);
                    }
                }
            });
        } else {
            post.setMyPraise(false);
            praiseImg.setImageResource(R.drawable.ic_heart_outline_grey);
            relation.remove(post);
            user.setLoveRelation(relation);
//            user.update(this, new UpdateListener() {
//                @Override
//                public void onSuccess() {
//
//                    ToastUtils.showToast(CommentActivity.this, R.string.cancel_collect_success, Toast.LENGTH_SHORT);
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//                    relation.add(post);
//                    user.setLoveRelation(relation);
//                    ToastUtils.showToast(CommentActivity.this, R.string.cancel_collect_fail, Toast.LENGTH_SHORT);
//                }
//            });
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        ToastUtils.showToast(CommentActivity.this, R.string.cancel_collect_success, Toast.LENGTH_SHORT);
                    }
                    else{
                        relation.add(post);
                        user.setLoveRelation(relation);
                        ToastUtils.showToast(CommentActivity.this, R.string.cancel_collect_fail, Toast.LENGTH_SHORT);
                    }
                }
            });
//            post.update(this, new UpdateListener() {
//                @Override
//                public void onSuccess() {
//                    post.setPraiseNum(post.getPraiseNum() - 1);
//                    setupLikesCounter(likeCounterSwitcher);
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//                    post.setMyPraise(true);
//                }
//            });
            post.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        post.setPraiseNum(post.getPraiseNum() - 1);
                        setupLikesCounter(likeCounterSwitcher);
                    }
                    else{
                        post.setMyPraise(true);
                    }
                }
            });
        }


    }

    private void setupPraiseAnim() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator rotation = ObjectAnimator.ofFloat(praiseImg, "rotation", 0f, 360f);
        rotation.setDuration(300);
        rotation.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceX = ObjectAnimator.ofFloat(praiseImg, "scaleX", 0.2f, 1f);
        bounceX.setDuration(300);
        bounceX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceY = ObjectAnimator.ofFloat(praiseImg, "scaleY", 0.2f, 1f);
        bounceY.setDuration(300);
        bounceY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        set.play(rotation);
        set.play(bounceX).with(bounceY).after(rotation);
        set.start();

    }


    private void onClickShare() {
        ToastUtils.showToast(CommentActivity.this, R.string.wait_share, Toast.LENGTH_SHORT);
    }

    private void queryComment() {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.order("-createdAt");
        query.addWhereRelatedTo("relation", new BmobPointer(post));
        query.setLimit(Constant.NUM_PER_PAGE);
        query.setSkip(lists.size());
        query.include("user");
//        query.findObjects(this, new FindListener<Comment>() {
//            @Override
//            public void onSuccess(List<Comment> list) {
//                if (list.size() != 0 && list.get(list.size() - 1) != null) {
//                    lists.addAll(list);
//                    commentAdapter.notifyDataSetChanged();
//                    setListViewHeightBasedOnChildren(commentList);
//                    LogUtils.i(TAG, commentList + "");
//                } else {
//                    ToastUtils.showToast(CommentActivity.this, R.string.no_more_comments, Toast.LENGTH_SHORT);
//                }
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                ToastUtils.showToast(CommentActivity.this, R.string.connect_internet_fail, Toast.LENGTH_SHORT);
//            }
//        });
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if(e==null){
                    if (list.size() != 0 && list.get(list.size() - 1) != null) {
                    lists.addAll(list);
                    commentAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                    LogUtils.i(TAG, commentList + "");
                } else {
                    ToastUtils.showToast(CommentActivity.this, R.string.no_more_comments, Toast.LENGTH_SHORT);
                }
                }else{
                    ToastUtils.showToast(CommentActivity.this, R.string.connect_internet_fail, Toast.LENGTH_SHORT);
                }
            }
        });

    }

    public void setupLikesCounter(TextSwitcher switcher) {
        int currentLikesCount = post.getPraiseNum();
        LogUtils.i(TAG, "currentLikesCount:" + currentLikesCount);
        switcher.setText(currentLikesCount + " " + "赞");

    }

    public void setupCommentsCounter(TextSwitcher switcher) {
        int currentCommentsCount = post.getCommentNum();
        LogUtils.i(TAG, "currentCommentsCount:" + currentCommentsCount);
        switcher.setText(currentCommentsCount + " " + "评论");

    }

    public void setupSharesCounter(TextSwitcher switcher) {
        int currentLikesCount = post.getShareNum();
        switcher.setText(currentLikesCount + " " + "分享");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
}
