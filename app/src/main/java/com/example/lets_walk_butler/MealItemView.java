package com.example.lets_walk_butler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.lets_walk_butler.R;

public class MealItemView extends LinearLayout {

    TextView textView;
    TextView textView2;
    ImageView imageView;


    public MealItemView(Context context) {
        super(context);

        init(context);
    }

    public MealItemView (Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init (context);
    }

    private void init(Context context) {
        //단말기 시작될 때 기본적으로 뒤에서 실행
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //단말이 시작될때 리스트 아이템 디자인을 정의한 singer_item.xml 이
        // activity_main.xml에 정의한 리니어 레이아웃에 달라 붙는다.
        inflater.inflate(R.layout.meal_item, this, true);

        //meal item 에 정의한 데이터들 선택자 가져오기
        textView = (TextView) findViewById(R.id.text_food_name);
        textView2 = (TextView) findViewById(R.id.text_food_weight);
//        imageView = (ImageView) findViewById(R.id.imageView);
    }

    // Main에서 getView() 에서 setName 이 호출되면 값을 textView 에 넣어준다.
    public void setName(String name) {
        textView.setText(name);
    }
    public void setWeight(String weight) {
        textView2.setText(weight);
    }

//    public void setImage(Drawable resId) {
//        imageView.setImageDrawable(resId);
//    }

}
