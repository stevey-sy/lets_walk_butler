package com.example.lets_walk_butler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    TimePicker alarmTimePicker;
    Context context;
    PendingIntent pendingIntent;
    TextView subheadingAlarm;
    TextView tvAlarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        subheadingAlarm = findViewById(R.id.subheading_alarm);
        tvAlarmTime = findViewById(R.id.tv_alarm_time);

        subheadingAlarm.setVisibility(View.INVISIBLE);
        tvAlarmTime.setVisibility(View.INVISIBLE);


        this.context = this;
        // 알람 매니저 설정
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmTimePicker = findViewById(R.id.time_picker);
        final Calendar calendar = Calendar.getInstance();

        // 알람리시버 intent 생성
        final Intent myIntent = new Intent(this.context, AlarmReceiver.class);

        // 사용자가 알람 설정 버튼을 눌렀을 때의 작동
        Button alarmOn = findViewById(R.id.btn_start);
        alarmOn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // calendar에 시간 셋팅
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                // 시간 가져옴
                int hour = alarmTimePicker.getHour();
                int minute = alarmTimePicker.getMinute();
                Toast.makeText(AlarmActivity.this,"Alarm 예정 " + hour + "시 " + minute + "분",Toast.LENGTH_SHORT).show();
                // reveiver에 string 값 넘겨주기
                myIntent.putExtra("state","alarm on");
                pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                //알람 셋팅

                tvAlarmTime.setText( hour + "시 " + minute + "분");
                subheadingAlarm.setVisibility(View.VISIBLE);
                tvAlarmTime.setVisibility(View.VISIBLE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        });

        // 알람 정지 버튼
        Button alarm_off = findViewById(R.id.btn_finish);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AlarmActivity.this,"Alarm 종료",Toast.LENGTH_SHORT).show();
                // 알람매니저 취소
                alarmManager.cancel(pendingIntent);
                // receiver에 알람 종료를 알리는 intent
                myIntent.putExtra("state","alarm off");
                subheadingAlarm.setVisibility(View.INVISIBLE);
                tvAlarmTime.setVisibility(View.INVISIBLE);

                // 알람취소
                sendBroadcast(myIntent);
            }
        });


    }
}
