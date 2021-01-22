package com.example.lets_walk_butler.walk_log;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.lets_walk_butler.R;

public class WalkItemView extends LinearLayout {

    TextView text_date;
    TextView text_distance;
    TextView text_time;
    TextView text_memo;
    ImageView image_walk;

    public WalkItemView(Context context) {
        super(context);
        init(context);
    }

    public WalkItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater !=null;
        inflater.inflate(R.layout.walk_diary_item, this, true);

        text_date = (TextView) findViewById(R.id.text_walk_date);
        text_time = (TextView) findViewById(R.id.text_walk_time);
        text_distance = (TextView) findViewById(R.id.text_walk_distance);
        text_memo = (TextView) findViewById(R.id.text_walk_memo);
        image_walk = (ImageView) findViewById(R.id.image_walk_diary);
    }

    public void setDate(String date) {
        text_date.setText(date);
    }
    public void setDistance(String distance) {
        text_distance.setText(distance);
    }
    public void setTime(String time) {
        text_time.setText(time);
    }
    public void setMemo(String memo) {
        text_memo.setText(memo);
    }
    public void setImage(Drawable image) {
        image_walk.setImageDrawable(image);
    }
}
