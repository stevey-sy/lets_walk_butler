package com.example.lets_walk_butler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MealDiaryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_diary);


        // 식사기록에서 사용자가 작성한 사료명 문자열을 받는 intent
        Intent get_food_name = getIntent();
        // 화면에 표시될 문자열 정의
        String food_name = get_food_name.getStringExtra("input_food_name");
        // 화면에 표시될 위치 정의
        TextView food_name_text = findViewById(R.id.show_food_name);
        // 표시될 위치에 정의된 문자열 세팅.
        food_name_text.setText(food_name);


        ListView list_view ;
        final MealLogAdapter adapter;

        // Adapter 생성
        adapter = new MealLogAdapter ();

        //리스트뷰와 adapter 연결
        list_view = (ListView) findViewById(R.id.meal_log_list_view);
        list_view.setAdapter(adapter);

        //아이템 추가
        adapter.add_item(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground), "2020/05/10", "로얄젤리", "120g");
        adapter.add_item(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground), "2020/05/10", "개껌", "1개");
        adapter.add_item(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground), "2020/05/05", "참치", "120g");



        //클릭 이벤트 정의
//        list_view.setOnClickListener(new AdapterView.OnItemClickListener() {
//
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                ListViewMealLog item = (ListViewMealLog) parent.getItemAtPosition(position);
//
//                String str_date = item.getDate();
//                String str_memo = item.getMemo();
//                Drawable icon_drawable = item.getIcon();
//
//            }
//        });
    }
}
