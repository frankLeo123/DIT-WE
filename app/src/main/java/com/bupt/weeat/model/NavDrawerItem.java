package com.bupt.weeat.model;



//左侧抽屉model
public class NavDrawerItem {
    private String list_item_text;
    private int list_item_image;

    public NavDrawerItem(String list_item_title, int list_item_image) {
        super();
        this.list_item_text = list_item_title;
        this.list_item_image = list_item_image;
    }

    public int getList_item_image() {
        return list_item_image;
    }

    public void setList_item_image(int list_item_image) {
        this.list_item_image = list_item_image;
    }

    public String getList_item_text() {
        return list_item_text;
    }

    public void setList_item_title(String list_item_title) {
        this.list_item_text = list_item_title;
    }


}
