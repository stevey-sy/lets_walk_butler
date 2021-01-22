package com.example.lets_walk_butler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        // Intent로부터 전달받은 string 시간정보.
        String getAlarmString = intent.getExtras().getString("state");

        //RingtonPlayingService 서비스 intent 생성
        Intent serviceIntent = new Intent(context, RingtonePlayingService.class);

        //RingtonPlayigService로 extra string 값 보내기
        serviceIntent.putExtra("state", getAlarmString);

        //start the ringtone service


        this.context.startForegroundService(serviceIntent);
        //this.context.startService(serviceIntent);


    }
}
