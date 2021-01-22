package com.example.lets_walk_butler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DiaryActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);


        // 식사기록에서 사용자가 선택한 식사 타입 정보를 받는 intent
        Intent get_dish_type = getIntent();
        String str_dish_type = get_dish_type.getStringExtra("selected_meal_type");

        assert str_dish_type != null;
        if(str_dish_type.equals("식사")) {
            TextView meal_type = findViewById(R.id.show_meal_type);
            meal_type.setText(str_dish_type);
        } else if (str_dish_type.equals("간식")) {
            TextView meal_type = findViewById(R.id.show_meal_type);
            meal_type.setText(str_dish_type);
        }

        // 식사기록 액티비티에서 작성한 메모 문자열을 받는 intent
        Intent get_memo = getIntent();
        String memo = get_memo.getStringExtra("input_meal_memo");
        TextView memo_text = findViewById(R.id.show_record_list_memo);
        memo_text.setText(memo);

        // 식사기록에서 사용자가 작성한 사료명 문자열을 받는 intent
        Intent get_food_name = getIntent();
        // 화면에 표시될 문자열 정의
        String food_name = get_food_name.getStringExtra("input_food_name");
        // 화면에 표시될 위치 정의
        TextView food_name_text = findViewById(R.id.show_food_name);
        // 표시될 위치에 정의된 문자열 세팅.
        food_name_text.setText(food_name);

        // 식사기록에서 사용자가 작성한 식사량의 문자열을 받는 intent
        Intent get_food_amount = getIntent();
        String food_amount = get_food_amount.getStringExtra("input_food_amount");
        TextView food_amount_text = findViewById(R.id.show_meal_amount);
        food_amount_text.setText(food_amount);

        // 식사기록에서 사용자가 선택한 날짜 정보를 받아올 intent
        Intent get_meal_date = getIntent();
        String meal_date = get_meal_date.getStringExtra("input_meal_date");
        TextView meal_date_text = findViewById(R.id.show_record_list_recorded_date);
        meal_date_text.setText(meal_date);

        // 식사기록에서 사용자가 선택한 시간 정보를 받아올 intent
        Intent get_meal_time = getIntent();
        String meal_time = get_meal_time.getStringExtra("input_meal_time");
        TextView meal_time_text = findViewById(R.id.show_meal_time);
        meal_time_text.setText(meal_time);

        //나가기 버튼
        Button btn_go_out = findViewById(R.id.btn_meal_diary_out);
        btn_go_out.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_meal_diary_out) {
            Intent go_to_main = new Intent (this, MainActivity.class);
            startActivity(go_to_main);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
