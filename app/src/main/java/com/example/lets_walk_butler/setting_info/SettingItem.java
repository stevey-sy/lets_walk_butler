package com.example.lets_walk_butler.setting_info;

import android.net.Uri;
import android.widget.Spinner;

public class SettingItem {

    private String dogNameStr;
    private String dogAgeStr;
    private String dogWeight;
    private String dogCategory;
    private Uri iconUri;

    public SettingItem(String dogNameStr, String dogAgeStr, String dogWeight, String dogCategory, Uri icon) {
        this.dogNameStr = dogNameStr;
        this.dogAgeStr = dogAgeStr;
        this.dogWeight = dogWeight;
        this.dogCategory = dogCategory;
        this.iconUri = icon;
    }

    public SettingItem() {
    }

    public SettingItem(String dogName, String strAge, String dogWeight, Spinner dogCategory, Uri icon) {
    }


    public String getDogNameStr() {
        return dogNameStr;
    }

    public void setDogNameStr(String dogNameStr) {
        this.dogNameStr = dogNameStr;
    }

    public String getDogAgeStr() {
        return dogAgeStr;
    }

    public void setDogAgeStr(String dogAgeStr) {
        this.dogAgeStr = dogAgeStr;
    }

    public String getDogWeight() { return dogWeight; }

    public void setDogWeight(String dogWeight) { this.dogWeight = dogWeight; }

    public String getDogCategory() { return dogCategory; }

    public void setDogCategory(String dogCategory) { this.dogCategory = dogCategory; }

    public Uri getIconUri() {
        return iconUri;
    }

    public void setIconUri(Uri iconUri) {
        this.iconUri = iconUri;
    }

    //    public Uri getIconURI() {
//        return iconDrawable;
//    }
//
//    public void setIconURI(URI iconDrawable) {
//        this.iconDrawable = iconDrawable;
//    }
}
