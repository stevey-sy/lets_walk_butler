package com.example.lets_walk_butler.setting_log;


import android.widget.SpinnerAdapter;

public class SettingDTO {

    public String dog_name;
    public String dog_age;
    public String dog_weight;
    public String dog_type;

    public SettingDTO(String dog_name, String dog_age, String dog_weight, String dog_type) {
        this.dog_name = dog_name;
        this.dog_age = dog_age;
        this.dog_weight = dog_weight;
        this.dog_type = dog_type;
    }

    public String getDogName() {
        return dog_name;
    }

    public void setDogName(String dog_name) {
        this.dog_name = dog_name;
    }

    public String getDogAge() {
        return dog_age;
    }

    public void setDogAge(String dog_age) {
        this.dog_age = dog_age;
    }

    public String getDogWeight() {
        return dog_weight;
    }

    public void setDogWeight(String dog_weight) {
        this.dog_weight = dog_weight;
    }

//    public SpinnerAdapter getDogType() {
//        return dog_type;
//    }

    public void setDogType(String dog_type) {
        this.dog_type = dog_type;
    }


}
