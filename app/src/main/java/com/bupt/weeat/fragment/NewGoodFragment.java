package com.bupt.weeat.fragment;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bupt.weeat.Constant;
import com.bupt.weeat.R;
import com.bupt.weeat.activity.GoodDetailActivity;
import com.bupt.weeat.adapter.NewGoodAdapter;
import com.bupt.weeat.db.DishDB;
import com.bupt.weeat.model.GoodBean;
import com.bupt.weeat.ui.RecyclerItemClickListener;
import com.bupt.weeat.utils.HttpUtils;
import com.bupt.weeat.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.InjectView;


public class NewGoodFragment extends BaseFragment {
    @InjectView(value = R.id.new_dish_recycler)
    RecyclerView recyclerView;
    private static final int NEW_DISH_CODE = 0;
    private ArrayList<GoodBean> list;
    private static final String TAG = NewGoodFragment.class.getSimpleName();
    private NewGoodAdapter adapter;
    private Handler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.new_good_layout;
    }

    @Override
    protected void initData() {
        super.initData();
        list = new ArrayList<>();
        mHandler = new MyHandler(NewGoodFragment.this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new NewGoodAdapter(list, context);
        recyclerView.setAdapter(adapter);
        queryNewDish();
    }

    @Override
    protected void setListener() {
        super.setListener();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    Intent intent = new Intent(context, GoodDetailActivity.class);
                    intent.putExtra("new_dish_data", list.get(position));
                    intent.putExtra("DISH_CODE", NEW_DISH_CODE);
                    Log.i(TAG,"onItemClick");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }) {
        });
    }

    public static NewGoodFragment newInstance() {
        return new NewGoodFragment();

    }

    private class MyHandler extends Handler {
        private final WeakReference<NewGoodFragment> mTarget;

        public MyHandler(NewGoodFragment fragment) {
            mTarget = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mTarget.get() != null) {
                switch (msg.what) {
                    case 0x01:
                        ArrayList<GoodBean> lists = (ArrayList<GoodBean>) msg.obj;
                        list.addAll(lists);
                        adapter.notifyDataSetChanged();
                        LogUtils.i(TAG, "list size: " + list.size());
                        break;
                    case 0x02:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void queryNewDish() {
        DishDB mDishDB = DishDB.getInstance(context);
        ArrayList<GoodBean> dish_list = mDishDB.queryDishFromDataBase("NewDish");
        if (dish_list.size() > 0) {
            LogUtils.i(TAG, "query from database");
            list.addAll(dish_list);
            adapter.notifyDataSetChanged();
        } else {
            LogUtils.i(TAG, "query from server");
            HttpUtils.connectToServer(Constant.NEW_DISH_URL, mHandler, context);
        }
    }
}
