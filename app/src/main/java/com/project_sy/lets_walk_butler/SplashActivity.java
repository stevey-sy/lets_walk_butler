package com.project_sy.lets_walk_butler;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    LottieAnimationView animationView;
    int waitingTime = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        animationView = (LottieAnimationView) findViewById(R.id.ani_loading);
        animationView.setAnimation("heart.json");
        animationView.setRepeatCount(5);
        animationView.bringToFront();
        animationView.playAnimation();

        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), waitingTime);


    }

    private class splashHandler implements Runnable {

        @Override
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class));
            SplashActivity.this.finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
