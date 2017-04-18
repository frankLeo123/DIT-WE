package com.bupt.weeat.data;

import com.bupt.weeat.model.GoodBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//数据吧，一时没看懂
public class NewGoodData {

    public ArrayList<GoodBean> parserNewDish(String jsonContent) {
        ArrayList<GoodBean> NewDish_list = null;
        try {
            JSONObject json = new JSONObject(jsonContent);
            int code = json.getInt("errno");
            if (code == 0) {
                NewDish_list = new ArrayList<>();
                JSONObject data = json.getJSONObject("data");
                JSONArray dishArray = data.getJSONArray("New_dishes");
                for (int i = 0; i < dishArray.length(); i++) {
                    final GoodBean dishObj = new GoodBean();
                    JSONObject dish = dishArray.getJSONObject(i);
                    dishObj.setId(dish.getString("id"));
                    dishObj.setName(dish.getString("name"));
                    dishObj.setFlavor(dish.getString("tastes"));
                    dishObj.setHeat(dish.getString("heat"));
                    dishObj.setUpdateTime(dish.getString("updateTime"));
                    dishObj.setPrice(dish.getString("price") );
                    dishObj.setPraise(dish.getString("praise"));
                    JSONObject dishCover = dish.getJSONObject("Dish_Cover");
                    String imageUrl = dishCover.getString("path");
                    dishObj.setImageUrl(imageUrl);
                    JSONObject windowCover = dish.getJSONObject("Dish_Window");
                    dishObj.setWindowId(windowCover.getString("id"));
                    dishObj.setLocation(windowCover.getString("location"));
                    System.out.print(dishArray.length());
                    NewDish_list.add(dishObj);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return NewDish_list;
    }
}
