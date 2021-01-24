package com.example.lets_walk_butler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MealActivity extends AppCompatActivity implements View.OnClickListener {

    RadioGroup radio_group_meal_type = null;
    RadioButton btn_main_dish = null;
    RadioButton btn_side_dish = null;
    String selected_meal_type = null;


    String text_meal_time = null;
    String text_meal_date = null;

    // 날짜, 식사타입 정보 들어갈 array list 생성
//    ArrayList<SampleData> meal_data_list;

    Calendar user_calendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener user_date_picker = new DatePickerDialog.OnDateSetListener () {
        public void onDateSet(DatePicker view, int year, int month, int day_of_month) {
            user_calendar.set(Calendar.YEAR, year);
            user_calendar.set(Calendar.MONTH, month);
            user_calendar.set(Calendar.DAY_OF_MONTH, day_of_month);
            updateLabel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        Button btn_meal_complete = findViewById(R.id.btn_meal_record_complete);
        btn_meal_complete.setOnClickListener(this);

        //라디오 그룹 생성
        radio_group_meal_type = (RadioGroup) findViewById(R.id.radio_group_meal);
        radio_group_meal_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_btn_main_dish) {
                    selected_meal_type = "식사";
                } else if (checkedId == R.id.radio_btn_side_dish) {
                    selected_meal_type = "간식";
                }
            }
        });

        //사용자가 날짜를 정하는 부분
        EditText user_edit_date = (EditText) findViewById(R.id.show_meal_date);
        user_edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MealActivity.this, user_date_picker, user_calendar.get(Calendar.YEAR), user_calendar.get(Calendar.MONTH), user_calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final EditText user_edit_time = (EditText) findViewById(R.id.show_meal_what_time);
        user_edit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar user_current_time = Calendar.getInstance();
                int hour = user_current_time.get(Calendar.HOUR_OF_DAY);
                int minute = user_current_time.get(Calendar.MINUTE);
                TimePickerDialog user_time_picker;
                user_time_picker = new TimePickerDialog(MealActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet (TimePicker time_picker, int selected_hour, int selected_minute) {
                        String time_state = "AM";

                        if (selected_hour > 12) {
                            selected_hour -= 12;
                            time_state = "PM";
                        }

                        //EditText 에 출력할 형식 지정
                        user_edit_time.setText(time_state + " "+ selected_hour + " 시 "+ selected_minute + " 분");
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 Time Picker 출현
                user_time_picker.setTitle("Select Time");
                user_time_picker.show();
            }
        });


        this.InitializeMealData();

    }

    public void InitializeMealData() {
//
//        meal_data_list = new ArrayList<SampleData>();
//        meal_data_list.add(new SampleData(text_meal_date, "간식"));

    }

    // 현재 날짜 정보를 표시하는 메서드
    private void updateLabel() {
        // 출력형식 2020 / 05 / 05
        String user_format = "yyyy/MM/dd";
        SimpleDateFormat simple_date_format = new SimpleDateFormat(user_format, Locale.KOREA);

        EditText edit_date = (EditText) findViewById(R.id.show_meal_date);
        edit_date.setText(simple_date_format.format(user_calendar.getTime()));
    }

    public String checked_type (View v) {
        //라디오 버튼 생성
        btn_main_dish = (RadioButton) findViewById(R.id.radio_btn_main_dish);
        btn_side_dish = (RadioButton) findViewById(R.id.radio_btn_side_dish);

        String type_result = null;
        if (btn_main_dish.isChecked()) {
            type_result = "식사";
        } else if (btn_side_dish.isChecked()) {
            type_result = "간식";
        }

        return type_result;
    }



    @Override
    public void onClick(View v) {

        // 기록된 자료들을 Diary 클래스로 보내는 intent 생성
        Intent send_meal_info = new Intent (this, MealDiaryActivity.class);
        // 사용자가 입력한 식사량 문자열 데이터를 EditText 에 담는다.
        EditText food_amount = findViewById(R.id.edit_food_amount);
        String text_food_amount = food_amount.getText().toString();

        // 사용자가 입력한 사료명 문자열 데이터를 EditText 에 담는다.
        EditText food_name = findViewById(R.id.edit_food_name);
        String text_food_name = food_name.getText().toString();

        // 사용자가 입력한 날짜 정보를 EditText에 담는다.
        EditText meal_date = findViewById(R.id.show_meal_date);
        text_meal_date = meal_date.getText().toString();

        // 사용자가 입력한 시간 정보를 EditText에 담는다.
        EditText meal_time = findViewById(R.id.show_meal_what_time);
        text_meal_time = meal_time.getText().toString();

        // 사용자가 입력한 메모 정보를 EditText에 담는다.
        EditText meal_memo = findViewById(R.id.edit_text_meal_memo);
        String text_memo = meal_memo.getText().toString();


        send_meal_info.putExtra("input_food_amount", text_food_amount);
        send_meal_info.putExtra("input_food_name", text_food_name);
        send_meal_info.putExtra("input_meal_date", text_meal_date);
        send_meal_info.putExtra("input_meal_time", text_meal_time);
        send_meal_info.putExtra("input_meal_memo", text_memo);
        send_meal_info.putExtra("selected_meal_type", checked_type(v));

        if (v.getId() == R.id.btn_meal_record_complete) {
            startActivity(send_meal_info);
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    }
