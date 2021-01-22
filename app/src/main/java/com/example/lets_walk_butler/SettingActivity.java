package com.example.lets_walk_butler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    RadioGroup radio_group_dog_size = null;
    RadioButton btn_size_small = null;
    RadioButton btn_size_medium = null;
    RadioButton btn_size_large = null;
    String selected_size_type = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 설정 완료 버튼에 클릭리스너 설정
        Button btn_setting_complete = findViewById(R.id.btn_setting_complete);
        btn_setting_complete.setOnClickListener(this);

        //
        Button btn_search_size = findViewById(R.id.btn_search_size);
        btn_search_size.setOnClickListener(this);

        //라디오 그룹 생성
        radio_group_dog_size = (RadioGroup) findViewById(R.id.radio_group_size_setting);
        radio_group_dog_size.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_btn_size_small) {
                    selected_size_type = "소형견";
                } else if (checkedId == R.id.radio_btn_size_medium) {
                    selected_size_type = "중형견";
                } else if (checkedId == R.id.radio_btn_size_large) {
                    selected_size_type = "대형견";
                }
            }
        });

    }

    public String checked_size (View v) {
        //라디오 버튼 생성
        btn_size_small = (RadioButton) findViewById(R.id.radio_btn_size_small);
        btn_size_medium = (RadioButton) findViewById(R.id.radio_btn_size_medium);
        btn_size_large = (RadioButton) findViewById(R.id.radio_btn_size_large);

        String size_result = null;
        if (btn_size_small.isChecked()) {
            size_result = "소형견";
        } else if (btn_size_medium.isChecked()) {
            size_result = "중형견";
        } else if (btn_size_large.isChecked()) {
            size_result = "대형견";
        }
        return size_result;
    }


    @Override
    public void onClick(View v) {
        // 세팅 클래스에서 입력받은 데이터를 main 클래스로 보내는 intent 생성
        Intent send_setting_info = new Intent (this, CheckInfoActivity.class);

        // 사용자가 입력한 강아지 이름 데이터를 EditText에 담는다.
        EditText dog_name = findViewById(R.id.edit_dog_name);
        String str_dog_name = dog_name.getText().toString();

        // 강아지 무게 정보를 EditText 에 담는다.
        EditText dog_weight = findViewById(R.id.edit_dog_weight);
        try {
            String input_number = dog_weight.getText().toString().trim();
            int weight_num = Integer.parseInt(input_number);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "숫자만 입력하세요", Toast.LENGTH_LONG).show();
        }

        String str_dog_weight = dog_weight.getText().toString();
        double num_dog_weight = Double.parseDouble(str_dog_weight);
        double result = num_dog_weight;
        double daily_food_weight = result *1000/40;

        Intent search_intent = new Intent (Intent.ACTION_WEB_SEARCH);
        search_intent.putExtra(SearchManager.QUERY, "견종 사이즈 리스트");

        send_setting_info.putExtra("input_dog_name", str_dog_name);
        send_setting_info.putExtra("selected_size", checked_size(v));
        send_setting_info.putExtra("selected_weight", str_dog_weight);
        send_setting_info.putExtra("calc_daily_amount", daily_food_weight);

        if (v.getId() == R.id.btn_setting_complete) {
            startActivity(send_setting_info);
        } else if (v.getId() == R.id.btn_search_size) {
            startActivity(search_intent);
        }

    }
}
