package com.example.lets_walk_butler;

import android.graphics.drawable.Drawable;

public class ListViewMealLog {

    private Drawable icon_drawable;
    private String str_date;
    private String str_food_name;
    private String str_food_weight;

    // setter 설정
    public void setIcon(Drawable icon) {
        icon_drawable = icon;
    }
    public void setDate(String date) {
        str_date = date;
    }
    public void setFoodName(String food_name) {
        str_food_name = food_name;
    }

    public void setFoodWeight(String food_weight) {
        str_food_weight = food_weight;
    }


    // getter 설정
    public Drawable getIcon() {
        return this.icon_drawable;
    }
    public String getDate() {
        return this.str_date;
    }
    public String getFoodName() {
        return this.str_food_name;
    }
    public String getFoodWeight() {
        return this.str_food_weight;
    }
}
