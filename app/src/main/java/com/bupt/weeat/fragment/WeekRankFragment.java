package com.bupt.weeat.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bupt.weeat.Constant;
import com.bupt.weeat.R;
import com.bupt.weeat.activity.GoodDetailActivity;
import com.bupt.weeat.adapter.WeekRankAdapter;
import com.bupt.weeat.db.DishDB;
import com.bupt.weeat.model.GoodBean;
import com.bupt.weeat.ui.RecyclerItemClickListener;
import com.bupt.weeat.utils.HttpUtils;
import com.bupt.weeat.utils.LogUtils;

import net.JsonRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.InjectView;

public class WeekRankFragment extends BaseFragment {
    @InjectView(R.id.week_rank_recycler)
    RecyclerView recyclerView;
    private ArrayList<GoodBean> list;


    private static final int WEEK_DISH_CODE = 1;
    private MyHandler mHandler;
    private DishDB mDishDB;
    private WeekRankAdapter adapter;
    private static final String TAG = WeekRankFragment.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.weekly_rank_layout;
    }

    @Override
    protected void initData() {
        super.initData();
        list = new ArrayList<>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new WeekRankAdapter(context, list);
        recyclerView.setAdapter(adapter);
        mHandler = new MyHandler(WeekRankFragment.this);
        queryNewDish();
    }

    public void getData() {
        final JsonRequest<GoodBean> request = new JsonRequest<>(Request.Method.GET, Constant.WEEK_RANK_URL, GoodBean.class, new Response.Listener<GoodBean>() {
            @Override
            public void onResponse(GoodBean response) {
                try {
                    list.clear();
                    list.addAll(response.getData());
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.setTag("query");
        client.addTask(request);
    }

    @Override
    protected void setListener() {
        super.setListener();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG,"onItemClick");
                Intent intent = new Intent(context, GoodDetailActivity.class);
                intent.putExtra("week_dish_data", list.get(position));
                intent.putExtra("DISH_CODE", WEEK_DISH_CODE);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.getRequestQueue().cancelAll("query");
    }

    public static WeekRankFragment newInstance() {

        return new WeekRankFragment();


    }


  private class MyHandler extends Handler {
        private final WeakReference<WeekRankFragment> mTarget;

        public MyHandler(WeekRankFragment fragment) {
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
                        break;
                    case 0x02:
                        Toast.makeText(context, R.string.collect_fail, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

   private void queryNewDish() {
        mDishDB = DishDB.getInstance(context);
        ArrayList<GoodBean> dish_list = mDishDB.queryDishFromDataBase("WeekDish");
        if (dish_list.size() > 0) {
            LogUtils.i(TAG, "query from database");
            list.addAll(dish_list);
            adapter.notifyDataSetChanged();
        } else {
            LogUtils.i(TAG, "query from server");
            HttpUtils.connectToServer(Constant.WEEK_RANK_URL, mHandler, context);
        }

    }
}
