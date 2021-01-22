package com.example.lets_walk_butler;

import android.graphics.drawable.Drawable;

public class MealItem {

    String name;
    String weight;
    //Drawable resId;

    public MealItem(String name, String weight ) {
        this.name = name;
        this.weight = weight;
        //this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

//    public Drawable getResId() {
//        return resId;
//    }
//    public void setResId() {
//        this.resId = resId;
//    }

}
