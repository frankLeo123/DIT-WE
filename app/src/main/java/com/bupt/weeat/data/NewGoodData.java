package com.bupt.weeat.data;

import com.bupt.weeat.model.GoodBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//解析网页上的JSON数据(新品速递)
public class NewGoodData {

    public ArrayList<GoodBean> parserNewDish(String jsonContent) {
        //传出ArrayList
        ArrayList<GoodBean> NewGood_list = null;
        try {
            JSONObject json = new JSONObject(jsonContent);
            int code = json.getInt("errno");
            if (code == 0) {
                NewGood_list = new ArrayList<>();
                JSONObject data = json.getJSONObject("data");
                JSONArray dishArray = data.getJSONArray("New_dishes");
                for (int i = 0; i < dishArray.length(); i++) {
                    final GoodBean dishObj = new GoodBean();
                    JSONObject goods = dishArray.getJSONObject(i);

                    dishObj.setId(goods.getString("id"));
                    dishObj.setName(goods.getString("name"));
                    dishObj.setFlavor(goods.getString("tastes"));
                    dishObj.setHeat(goods.getString("heat"));
                    dishObj.setUpdateTime(goods.getString("updateTime"));
                    dishObj.setPrice(goods.getString("price") );
                    dishObj.setPraise(goods.getString("praise"));
                    JSONObject dishCover = goods.getJSONObject("Dish_Cover");
                    String imageUrl = dishCover.getString("path");
                    dishObj.setImageUrl(imageUrl);
                    JSONObject windowCover = goods.getJSONObject("Dish_Window");
                    dishObj.setWindowId(windowCover.getString("id"));
                    dishObj.setGradeName(
                            windowCover.getString("name")
                    );
                    dishObj.setLocation(windowCover.getString("location"));
//                    System.out.print(dishArray.length());

                    NewGood_list.add(dishObj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return NewGood_list;
    }
}
