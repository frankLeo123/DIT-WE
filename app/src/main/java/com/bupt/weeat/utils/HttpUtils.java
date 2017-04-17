package com.bupt.weeat.utils;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.bupt.weeat.Constant;
import com.bupt.weeat.data.HotRecommendationDishData;
import com.bupt.weeat.data.NewDishData;
import com.bupt.weeat.data.WeekDishData;
import com.bupt.weeat.db.DishDB;
import com.bupt.weeat.model.DishBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
////////////////////////////////
//判断http的，一时细节没看懂      //
////////////////////////////////
public class HttpUtils {
    private static String jsonContent = "";
    private static DishDB mDishDB;
//path是判断是哪个模块的
    public static void connectToServer(final String path, final Handler mHandler, final Context mContext) {
        new Thread(new Runnable() {
            HttpURLConnection conn;

            @Override
            public void run() {
                try {
                    LogUtils.i("HttpUtils", Thread.currentThread().getName());

                    URL url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    jsonContent = sb.toString();
                    ArrayList<DishBean> list = new ArrayList<>();
                    //根据模块判断是哪里的信息
                    switch (path) {
                        case Constant.HOT_RECOMMENDATION_URL:
                            HotRecommendationDishData hot_data = new HotRecommendationDishData();
                            list = hot_data.parserHotRecommendation(jsonContent);
                            break;
                        case Constant.NEW_DISH_URL:
                            NewDishData new_data = new NewDishData();
                            list = new_data.parserNewDish(jsonContent);
                            break;
                        case Constant.WEEK_RANK_URL:
                            WeekDishData week_data = new WeekDishData();
                            list = week_data.parserWeekDish(jsonContent);
                            break;
                        default:
                            break;
                    }
                    mDishDB = DishDB.getInstance(mContext);
                    for (DishBean dishObject : list) {
                        mDishDB.saveDishToDataBase(dishObject, path);
                    }
                    if (mHandler != null && list != null) {
                        Message msg = Message.obtain(mHandler, 0x01, list);
                        msg.sendToTarget();
                    } else {
                        Message msg = Message.obtain(mHandler, 0x02);
                        msg.sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }

        }).start();

    }
}
