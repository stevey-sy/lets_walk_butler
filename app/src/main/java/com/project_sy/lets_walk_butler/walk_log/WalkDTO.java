package com.project_sy.lets_walk_butler.walk_log;

import android.net.Uri;

public class WalkDTO {

    private String walkDate;
    private String walkTime;
    private String walkStep;
    private String walkMemo;
    private String walkMeter;
    private Uri walkImageUri;


    public WalkDTO(String date, String time, String step, String meter, String memo, Uri uri) {
        this.walkDate = date;
        this.walkTime = time;
        this.walkStep = step;
        this.walkMeter = meter;
        this.walkMemo = memo;
        this.walkImageUri = uri;
    }

    public String getDate() {
        return walkDate;
    }

    public String getWalkStep() {
        return walkStep;
    }

    public String getWalkMeter() {return walkMeter;}

    public String getWalkTime() {
        return walkTime;
    }
    public String getMemo () {
        return walkMemo;
    }

    public Uri getWalkImageUri() {
        return walkImageUri;
    }

    public void setWalkDate(String walkDate) {
        this.walkDate = walkDate;
    }

    public void setWalkTime(String walkTime) {
        this.walkTime = walkTime;
    }

    public void setWalkStep(String walkStep) {
        this.walkStep = walkStep;
    }

    public void setWalkMemo(String walkMemo) {
        this.walkMemo = walkMemo;
    }

    public void setWalkMeter(String walkMeter) {
        this.walkMeter = walkMeter;
    }

    public void setWalkImageUri(Uri walkImageUri) {
        this.walkImageUri = walkImageUri;
    }
}
