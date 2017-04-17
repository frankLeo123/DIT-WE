package com.bupt.weeat.fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupt.weeat.Constant;
import com.bupt.weeat.R;
import com.bupt.weeat.activity.ImageDetailActivity;
import com.bupt.weeat.db.DishDB;
import com.bupt.weeat.model.DishBean;
import com.bupt.weeat.utils.HttpUtils;
import com.bupt.weeat.utils.LogUtils;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecommendationFragment extends Fragment implements View.OnClickListener {
    String imageUrl;
    private ImageView randomDishImage;
    private ImageView animLoveIv;
    private View animLoveBg;
    private TextView dishName;
    private TextView dishTaste;
    private TextView dishPraiseNum;
    private TextView dishLocation;
    private ImageView dishPraiseImage;
    private Context mContext;
    private ArrayList<DishBean> list;
    private Handler mHandler;
    private static final String TAG = RecommendationFragment.class.getSimpleName();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.i(TAG, "onCreateView");
        list = new ArrayList<>();
        mContext = getActivity();
        View view = inflater.inflate(R.layout.hot_recom_fragment, container, false);
        randomDishImage = (ImageView) view.findViewById(R.id.random_food_iv);
        dishPraiseImage = (ImageView) view.findViewById(R.id.recommendation_dish_praise_image);
        dishName = (TextView) view.findViewById(R.id.recommendation_dish_name);
        dishPraiseNum = (TextView) view.findViewById(R.id.recommendation_dish_praise_num);
        dishTaste = (TextView) view.findViewById(R.id.recommendation_dish_tastes);
        dishLocation = (TextView) view.findViewById(R.id.recommendation_dish_location);
        animLoveBg = view.findViewById(R.id.anim_love_bg);
        animLoveIv = (ImageView) view.findViewById(R.id.anim_love_iv);
        setListener();
        mHandler = new MyHandler(RecommendationFragment.this);
        try {
            queryRecommendationDish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }


    private void setListener() {
        dishPraiseImage.setOnClickListener(this);
        randomDishImage.setOnClickListener(this);
    }

    public static RecommendationFragment newInstance() {
        return new RecommendationFragment();

    }

    private class MyHandler extends Handler {
        private final WeakReference<RecommendationFragment> mTarget;

        public MyHandler(RecommendationFragment fragment) {
            mTarget = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mTarget.get() != null) {
                switch (msg.what) {
                    case 0x01:
                        ArrayList<DishBean> dish_list = (ArrayList<DishBean>) msg.obj;
                        list.addAll(dish_list);
                        initView(list);
                        break;
                    case 0x02:
                        break;
                }
            }
        }
    }

    private void queryRecommendationDish() {
        DishDB mDishDB = DishDB.getInstance(mContext);
        ArrayList<DishBean> dish_list = mDishDB.queryDishFromDataBase("RecommendationDish");
        if (dish_list.size() > 0) {
            list.addAll(dish_list);
            initView(list);
        } else {
            LogUtils.i(TAG, "query from server");
            HttpUtils.connectToServer(Constant.HOT_RECOMMENDATION_URL, mHandler, mContext);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recommendation_dish_praise_image:
                String praise = (String) dishPraiseNum.getText();
                int currentPraise = Integer.parseInt(praise);
                dishPraiseNum.setText((++currentPraise) + "");
                list.get(0).setPraise((++currentPraise) + "");
                animPictureLove();
                break;
            case R.id.random_food_iv:
                Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                intent.putExtra("image_url", imageUrl);
                startActivity(intent);

            default:
                break;
        }
    }

    private void animPictureLove() {
        animLoveBg.setVisibility(View.VISIBLE);
        animLoveIv.setVisibility(View.VISIBLE);

        animLoveIv.setScaleX(0.1f);
        animLoveIv.setScaleY(0.1f);

        animLoveBg.setScaleX(0.1f);
        animLoveBg.setScaleY(0.1f);
        animLoveBg.setAlpha(1f);

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator bgScaleUpX = ObjectAnimator.ofFloat(animLoveBg, "ScaleX", 0.1f, 1f);
        bgScaleUpX.setDuration(200);
        bgScaleUpX.setInterpolator(DECELERATE_INTERPOLATOR);


        ObjectAnimator bgScaleUpY = ObjectAnimator.ofFloat(animLoveBg, "ScaleY", 0.1f, 1f);
        bgScaleUpY.setDuration(200);
        bgScaleUpY.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator bgAlpha = ObjectAnimator.ofFloat(animLoveBg, "alpha", 1f, 0f);
        bgAlpha.setDuration(200);
        bgAlpha.setStartDelay(150);
        bgAlpha.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator ivScaleUpX = ObjectAnimator.ofFloat(animLoveIv, "ScaleX", 0.1f, 1f);
        ivScaleUpX.setDuration(300);
        ivScaleUpX.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator ivScaleUpY = ObjectAnimator.ofFloat(animLoveIv, "ScaleY", 0.1f, 1f);
        ivScaleUpY.setDuration(300);
        ivScaleUpY.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator ivScaleDownX = ObjectAnimator.ofFloat(animLoveIv, "ScaleX", 1f, 0f);
        ivScaleDownX.setDuration(300);
        ivScaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator ivScaleDownY = ObjectAnimator.ofFloat(animLoveIv, "ScaleY", 1f, 0f);
        ivScaleDownY.setDuration(300);
        ivScaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

        set.playTogether(bgScaleUpX, bgScaleUpY, bgAlpha, ivScaleUpX, ivScaleUpY);
        set.play(ivScaleDownX).with(ivScaleDownY).after(ivScaleUpY);
        set.start();
    }

    private void initView(ArrayList<DishBean> list) {
        try {
            imageUrl = list.get(0).getImageUrl();
            if (!imageUrl.isEmpty()) {
                Picasso.with(mContext)
                        .load(imageUrl)
                        .resize(300, 200)
                        .centerCrop()
                        .into(randomDishImage);

            }
            dishLocation.setText(list.get(0).getLocation());
            dishName.setText(list.get(0).getName());
            dishTaste.setText(list.get(0).getFlavor());
            dishPraiseNum.setText(list.get(0).getPraise());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
