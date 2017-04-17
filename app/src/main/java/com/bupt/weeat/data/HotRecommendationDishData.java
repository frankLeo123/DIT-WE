package com.bupt.weeat.data;

import com.bupt.weeat.model.DishBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HotRecommendationDishData {
    public ArrayList<DishBean> parserHotRecommendation(String jsonContent) {
        ArrayList<DishBean> HotDish_list = null;
        try {
            JSONObject json = new JSONObject(jsonContent);
            int code = json.getInt("errno");
            if (code == 0) {
                HotDish_list = new ArrayList<>();
                JSONObject data = json.getJSONObject("data");
                JSONArray HotDishes = data.getJSONArray("dish_hotest");
                for (int i = 0; i < HotDishes.length(); i++) {
                    JSONObject dish = HotDishes.getJSONObject(i);
                    final DishBean dishObj = new DishBean();
                    dishObj.setId(dish.getString("id"));
                    dishObj.setName(dish.getString("name"));
                    dishObj.setPraise(dish.getString("praise"));
                    dishObj.setFlavor(dish.getString("tastes"));
                    dishObj.setHeat(dish.getString("heat"));
                    JSONObject dishCover = dish.getJSONObject("Dish_Cover");
                    String imageUrl = dishCover.getString("path");
                    dishObj.setImageUrl(imageUrl);
                    JSONObject dishWindow = dish.getJSONObject("Dish_Window");
                    String location = dishWindow.getString("location");
                    dishObj.setLocation(location);
                    HotDish_list.add(dishObj);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return HotDish_list;
    }


}
