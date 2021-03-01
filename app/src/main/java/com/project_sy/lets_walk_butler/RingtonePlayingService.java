package com.project_sy.lets_walk_butler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class RingtonePlayingService extends Service {

    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;
    Notification timeCompleteNotifi;

    @Nullable
    @Override
    //IBinder 객체를 반환한다. IBinder는 서비스와 컴포넌트 사이에서 인터페이스 역할
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "산책 시간 알림", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            // 알람시간 때에 사용자에게 notification을 보낸다.
            // notification을 클릭할 시, 앱의 메인화면으로 이동
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle("\"집사야, 산책갈 시간이야!\"")
//                    .setContentText("산책 시간을 알려드립니다.")
//                    .setColor(Color.GREEN)
////                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true)
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setDefaults(Notification.FLAG_AUTO_CANCEL)
//                    .build();
//            startForeground(1, notification);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_paw)
                    .setContentTitle("\"집사야, 산책갈 시간이야!\"")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, notification.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 리시버로 부터 "state"의 intent 키값을 받아서 실행될 작동들
        String getState = intent.getExtras().getString("state");
        assert getState != null;
        switch (getState) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        // 알람음 재생 X , 알람음 시작 클릭
        if(!this.isRunning && startId == 1) {
//            mediaPlayer = MediaPlayer.create(this, R.raw.morning_alarm);
//            mediaPlayer.start();
            this.isRunning = true;
            this.startId = 0;
        }

        // 알람음 재생 가능 , 알람음 종료 버튼 클릭
        else if(this.isRunning && startId == 0) {

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            this.isRunning = false;
            this.startId = 0;
        }

        // 알람음 재생 불가능할 때, 알람음 종료 버튼 클릭
        else if(!this.isRunning && startId == 0) {
            this.isRunning = false;
            this.startId = 0;
        }
        // 알람음 재생 가능할 때 , 알람음 시작 버튼 클릭
        else if(this.isRunning && startId == 1){
            this.isRunning = true;
            this.startId = 1;
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
