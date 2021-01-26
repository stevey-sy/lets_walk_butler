package com.example.lets_walk_butler;

public class MealMemoItem {

    private String category;
    private String memo;
    private String petName;
    // private String regDate;
    private String type_category;
    private String food_name;
    private String food_weight;
    private String mealDate;
    private String mealType;
    private String measureType;


    public MealMemoItem(String meal_date, String pet_name, String meal_type, String food_name, String food_weight, String measure_type, String memo) {
//        SimpleDateFormat formatter =
//                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
//        Date date = new Date();

//        this.category = category;
        this.petName = pet_name;
        this.memo = memo;
        this.mealDate = meal_date;
        this.mealType = meal_type;
//        this.regDate = formatter.format(date);
//        this.type_category = type_category;
        this.food_name = food_name;
        this.food_weight = food_weight;
        this.measureType = measure_type;
    }

    public String getMeasureType() {
        return measureType;
    }

    public String getMealType() {
        return mealType;
    }
    public String getMealDate() {
        return mealDate;
    }

    public String getPetName() {
        return petName;
    }

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
