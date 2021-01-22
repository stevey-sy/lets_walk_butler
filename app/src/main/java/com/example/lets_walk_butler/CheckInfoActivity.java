package com.example.lets_walk_butler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheckInfoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_info);

        // 설정 창에서 입력받은 강아지 이름을 표시한다.
        Intent get_dog_name = getIntent();
        String dog_name = get_dog_name.getStringExtra("input_dog_name");
        TextView show_dog_name = findViewById(R.id.str_show_dog_name);
        show_dog_name.setText(dog_name);

        // 강아지 무게 정보를 받는다
        Intent get_dog_weight = getIntent();
        String dog_weight = get_dog_weight.getStringExtra("selected_weight");
        TextView show_dog_weight = findViewById(R.id.show_info_dog_weight);
        show_dog_weight.setText(dog_weight);

        // 설정 창에서 입력받은 강아지 사이즈 정보를 받는다.
        Intent get_dog_size = getIntent();
        String dog_size = get_dog_size.getStringExtra("selected_size");


        assert dog_size != null;
        if(dog_size.equals("소형견")) {
            TextView show_dog_size = findViewById(R.id.recommend_walk_amount);
            show_dog_size.setText(R.string.str_short_time);
        } else if (dog_size.equals("중형견")) {
            TextView show_dog_size = findViewById(R.id.recommend_walk_amount);
            show_dog_size.setText(R.string.str_medium_time);
        } else if (dog_size.equals("대형견")) {
            TextView show_dog_size = findViewById(R.id.recommend_walk_amount);
            show_dog_size.setText(R.string.str_long_time);
        }


        //나가기 버튼
        Button btn_go_out = findViewById(R.id.btn_go_to_main);
        btn_go_out.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_go_to_main) {
            Intent go_to_main = new Intent (this, MainActivity.class);
            startActivity(go_to_main);
        }

    }
}
