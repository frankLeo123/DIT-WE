package com.bupt.weeat.data;

import com.bupt.weeat.model.GoodBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeekGoodData {
    public ArrayList<GoodBean> parserWeekDish(String jsonContent) {
        ArrayList<GoodBean> WeekDish_list = null;
        try {
            JSONObject json = new JSONObject(jsonContent);
            int code = json.getInt("errno");
            if (code == 0) {
                WeekDish_list = new ArrayList<>();
                JSONObject data = json.getJSONObject("data");
                JSONArray dishArray = data.getJSONArray("dishes");
                JSONArray windowArray = data.getJSONArray("windows");
                for (int i = 0; i < dishArray.length(); i++) {
                  final GoodBean dishObj = new GoodBean();
                    JSONObject dish = dishArray.getJSONObject(i);
                    dishObj.setId(dish.getString("id"));
                    dishObj.setName(dish.getString("name"));
                    dishObj.setFlavor(dish.getString("tastes"));
                    dishObj.setHeat(dish.getString("heat"));
                    dishObj.setPraise(dish.getString("praise"));
                    dishObj.setPrice(dish.getString("price"));
                    JSONObject dishCover = dish.getJSONObject("Dish_Cover");
                    String imageUrl = dishCover.getString("path");
                    dishObj.setImageUrl(imageUrl);
                    JSONObject window = windowArray.getJSONObject(i);
                    dishObj.setWindowId(window.getString("id"));
                    dishObj.setGradeName(window.getString("name"));
                    dishObj.setLocation(window.getString("location"));
                    JSONObject windowCover = window.getJSONObject("Window_Cover");
                    String windowUrl = windowCover.getString("path");
                    dishObj.setWindowUrl(windowUrl);
                    WeekDish_list.add(dishObj);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return WeekDish_list;
    }
}
