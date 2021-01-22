package com.example.lets_walk_butler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MealMemoItem {

    private String category;
    private String memo;
    // private String regDate;
    private String type_category;
    private String food_name;
    private String food_weight;


    public MealMemoItem(String food_name, String food_weight, String memo) {
//        SimpleDateFormat formatter =
//                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
//        Date date = new Date();

//        this.category = category;
        this.memo = memo;
//        this.regDate = formatter.format(date);
//        this.type_category = type_category;
        this.food_name = food_name;
        this.food_weight = food_weight;
    }

//    public String getRegDate() {
//        return regDate;
//    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

//    public void setRegDate(String regDate) {
//        this.regDate = regDate;
//    }

    public String getType_category() {
        return type_category;
    }

    public void setType_category(String type_category) {
        this.type_category = type_category;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_weight() {
        return food_weight;
    }

    public void setFood_weight(String food_weight) {
        this.food_weight = food_weight;
    }
}
