package com.bupt.weeat.data;

import com.bupt.weeat.model.GoodBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//把Json数据上传到Bmob表
public class HotRecommendationGoodData {
    public ArrayList<GoodBean> parserHotRecommendation(String jsonContent) {
        ArrayList<GoodBean> HotGood_list = null;
        try {
            JSONObject json = new JSONObject(jsonContent);
            int code = json.getInt("errno");
            if (code == 0) {
                HotGood_list = new ArrayList<>();
                JSONObject data = json.getJSONObject("data");
                JSONArray HotGoods = data.getJSONArray("dish_hotest");
                for (int i = 0; i < HotGoods.length(); i++) {
                    JSONObject good = HotGoods.getJSONObject(i);
                    final GoodBean goodObj = new GoodBean();
                    goodObj.setId(good.getString("id"));
                    goodObj.setName(good.getString("name"));
                    goodObj.setPraise(good.getString("praise"));
                    goodObj.setFlavor(good.getString("tastes"));
                    goodObj.setHeat(good.getString("heat"));
                    JSONObject dishCover = good.getJSONObject("Dish_Cover");
                    String imageUrl = dishCover.getString("path");
                    goodObj.setImageUrl(imageUrl);
                    JSONObject dishWindow = good.getJSONObject("Dish_Window");
                    String location = dishWindow.getString("location");
                    goodObj.setLocation(location);
                    HotGood_list.add(goodObj);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return HotGood_list;
    }


}
