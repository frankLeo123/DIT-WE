package com.bupt.weeat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bupt.weeat.model.GoodBean;
import com.bupt.weeat.Constant;

import java.util.ArrayList;

public class GoodDB {
    private static final String DB_NAME = "dish database";
    private static final int VERSION = 1;
    private static GoodDB mGoodDB;
    private SQLiteDatabase db;
    private static final String TAG = GoodDB.class.getSimpleName();

    public GoodDB(Context mContext) {
        DishOpenHelper helper = new DishOpenHelper(mContext, DB_NAME, null, VERSION);
        db = helper.getWritableDatabase();
    }

    public synchronized static GoodDB getInstance(Context mContext) {
        if (mGoodDB == null)
            mGoodDB = new GoodDB(mContext);
        return mGoodDB;
    }

    public void saveDishToDataBase(GoodBean dishObject, String path) {
        if (dishObject != null) {
            ContentValues values = new ContentValues();
            values.put("dish_name", dishObject.getName());
            values.put("dish_location", dishObject.getLocation());
            values.put("dish_flavor", dishObject.getFlavor());
            values.put("dish_praise", dishObject.getPraise());
            values.put("dish_price", dishObject.getPrice());
            values.put("dish_heat", dishObject.getHeat());
            values.put("dish_windowId", dishObject.getWindowId());
            values.put("dish_windowName", dishObject.getGradeName());
            values.put("dish_imageUrl", dishObject.getImageUrl());
            try {
                values.put("dish_updateTime", dishObject.getUpdateTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (path) {
                case Constant.NEW_GOOD_URL:
                    db.insert(" NewDish", null, values);
                    break;
                case Constant.HOT_RECOMMENDATION_URL:
                    db.insert(" RecommendationDish", null, values);
                    break;
                case Constant.WEEK_RANK_URL:
                    db.insert(" WeekDish", null, values);
                    break;
                default:
                    break;
            }


        }

    }

    public ArrayList<GoodBean> queryDishFromDataBase(String tableName) {
        ArrayList<GoodBean> dish_list = new ArrayList<>();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                GoodBean obj = new GoodBean();
                obj.setName(cursor.getString(cursor.getColumnIndex("dish_name")));
                obj.setLocation(cursor.getString(cursor.getColumnIndex("dish_location")));
                obj.setPraise(cursor.getString(cursor.getColumnIndex("dish_praise")));
                obj.setGradeName(cursor.getString(cursor.getColumnIndex("dish_windowName")));
                obj.setFlavor(cursor.getString(cursor.getColumnIndex("dish_flavor")));
                obj.setWindowId(cursor.getString(cursor.getColumnIndex("dish_windowId")));
                obj.setHeat(cursor.getString(cursor.getColumnIndex("dish_heat")));
                obj.setPrice(cursor.getString(cursor.getColumnIndex("dish_price")));
                obj.setImageUrl(cursor.getString(cursor.getColumnIndex("dish_imageUrl")));
                obj.setUpdateTime(cursor.getString(cursor.getColumnIndex("dish_updateTime")));
                dish_list.add(obj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dish_list;
    }
}
