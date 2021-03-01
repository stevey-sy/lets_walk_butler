package com.project_sy.lets_walk_butler;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.androdocs.httprequest.HttpRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // 사용자의 권한 요청 결과를 담을 변수
    int PERMISSION_ID = 1001;
    FusedLocationProviderClient mFusedLocationClient;
    TextView tvTemperature, tvDescription, tvHumidity, tvUpdatedTime, latTextView, lonTextView, tvWeatherGuide;
    String serviceKey;

    double lat, lon;
    Location location;
    LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean isGetLocation = false;
    // 1초마다 거리의 차이를 업데이트한다.
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    String temp;
    String weatherDescription;
    String humidity;

    LottieAnimationView animationView, dogAnimationView;
    LottieAnimationView dogSoundView;
    Animation textAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("LifeCycleActivity", "onCreate()");
        serviceKey = getString(R.string.service_key);

        tvTemperature = findViewById(R.id.tv_temperature);
        tvDescription = findViewById(R.id.tv_weather);
        tvHumidity = findViewById(R.id.tv_humidity);
        tvUpdatedTime = findViewById(R.id.tv_updated_time);
        latTextView = findViewById(R.id.tv_latitude);
        tvWeatherGuide = findViewById(R.id.tv_weather_guide);

        textAnim = new AlphaAnimation(0.0f, 1.0f);
        //textAnim.setStartTime();
        textAnim.setDuration(300);
        textAnim.setStartOffset(20);
        textAnim.setRepeatMode(Animation.REVERSE);
        textAnim.setRepeatCount(8);

        tvWeatherGuide.startAnimation(textAnim);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestPermissions();
        getLocation();
        //getLastLocation();
        new weatherTask().execute();

        //메인 액티비티의 버튼 정의
        Button btn_go_walk = findViewById(R.id.btn_main_go_walk);
        btn_go_walk.setOnClickListener(this);

        Button btn_record = findViewById(R.id.btn_main_record);
        btn_record.setOnClickListener(this);

        Button btn_diary = findViewById(R.id.btn_main_diary);
        btn_diary.setOnClickListener(this);

        Button btn_meal = findViewById(R.id.btn_main_meal_record);
        btn_meal.setOnClickListener(this);

        Button btn_walk_log = findViewById(R.id.btn_walk_diary);
        btn_walk_log.setOnClickListener(this);

        ImageView mainImage = (ImageView)findViewById(R.id.main_image);

        // 메인 화면의 아이콘 눌렀을 때의 반응
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 텍스트 애니메이션 효과
                textAnim = new AlphaAnimation(0.0f, 1.0f);
                //textAnim.setStartTime();
                textAnim.setDuration(200);
                textAnim.setStartOffset(20);
                textAnim.setRepeatMode(Animation.REVERSE);
                textAnim.setRepeatCount(10);
                // 날씨의 상태에 따라 다른 메세지가 입력된다.
                tvWeatherGuide.startAnimation(textAnim);

                return false;
            }
        };

        mainImage.setOnTouchListener(onTouchListener);

    }


    //getLocation 함수  선언
    // 위치의 위도와 경도를 구한다.
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // GPS 정보 사용이 가능한지 체크.
            int gpsCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            // GPS 사용 요청이 거부되었을 때의 액션
            if (gpsCheck == PackageManager.PERMISSION_DENIED) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            //GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //현재 네트워크 상태값 알아오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS와 네트워크 사용이 가능하지 않을때 소스 구현
            } else {
                this.isGetLocation = true;
                //네트워크 정보로부터 위치값 가져오기
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    if (locationManager != null) {
                        // GPS 와 네트워크 둘다 사용이 불가능 할 때에는 마지막 조회 위치의 정보를 불러온다.
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도, 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
                // GPS 사용이 가능할 때 작동
                if (isGPSEnabled) {
                    if(location == null) {
                        // 새로운 위치정보를 업데이트
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };


    // 위치정보를 가져올 메서드
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // 사용자에게 권한을 요청
            requestPermissions();
        }
    }

    // 사용자가 사용기기의 위치정보를 껏다가 다시 켰을 때, 이전에 app에 저장되어 있던 위치정보가 사라진다.
    // 사용자가 사용기기의 위치정보를 틀지 않은 상태에서 app을 실행했을 때, 위치정보가 null 로 들어온다.
    // 이런 경우의 수를 대비한 메서드 (위치 정보 갱신)
    @SuppressLint("MissingPermission")
    // GPS 와 인터넷 사용이 가능할 때, 새 위치 정보를 요청한다.
    private void requestNewLocationData(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()
        );
    }
    // callBack 메서드
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            //doubleLatitude = mLastLocation.getLatitude();
            //doubleLongitude = mLastLocation.getLongitude();

            latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");
        }
    };


    // 사용자가 권한을 허가했는지 체크하는 메서드
    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    // 사용자에게 위치정보 권한을 요청하는 메서드
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    // 사용자가 권한을 허가하거나 거절했을 때에 불러오는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }
    // 사용자가 사용기기의 위치정보를 on 해놓았는지 확인하는 메서드
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("StaticFieldLeak")
    // 날씨 정보를 조회하는 AsyncTask
    class weatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            // 백그라운드에서 api를 통해 날씨 정보를 조회한다.
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid="+ serviceKey);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // 받아온 JSON 데이터를 토대로 필요한 정보를 추려낸다.
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "마지막날씨조회: "+ new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.KOREA).format(new Date(updatedAt * 1000));
                // Todo 변수명
                temp = main.getString("temp") + "°C";
                weatherDescription = weather.getString("description");
                humidity = main.getString("humidity") +"%";

                String address = jsonObj.getString ("name") + ", " +sys.getString("country");

                tvTemperature.setText(temp);
                tvDescription.setText(transferWeather(weatherDescription));
                tvHumidity.setText(humidity);
                tvUpdatedTime.setText(updatedAtText);
                latTextView.setText(address);

                // Todo  채울것
            } catch (JSONException e) {
                Log.d("날씨 API 에러 ", String.valueOf(e));
            }
        }
    }
    // API를 통해 가져온 날씨 정보를 번역할 메서드드
    private String transferWeather(String weather) {

//        weatherDescription = weather.toLowerCase();

        if (weather.equals("haze")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("foggy.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘은 산책가도 좋을 것 같아!\"");
            return "흐림";
        }
        else if (weather.equals("fog")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("foggy.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘은 산책가도 좋을 것 같아!\"");
            return "흐림";
        }
        else if (weather.equals("clouds")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("foggy.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘은 산책가도 좋을 것 같아!\"");
            return "구름";
        }
        else if (weather.equals("few clouds")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("partlyCloudy.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘은 산책가도 좋을 것 같아!\"");
            return "구름 조금";
        }
        else if (weather.equals("scattered clouds")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("partlyCloudy.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘은 산책가도 좋을 것 같아!\"");
            return "구름 낌";
        }
        else if (weather.equals("broken clouds")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("foggy.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘은 산책가도 좋을 것 같아!\"");
            return "구름 많음";
        }
        else if (weather.equals("overcast clouds")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("foggy.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘은 산책가도 좋을 것 같아!\"");
            return "구름 많음";
        }
        else if (weather.equals("clear sky")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("sunner.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사야, 날씨가 너무 좋아! 산책 가즈아! >.< \"");
            return "맑음";
        }
        else if (weather.equals("rain")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("rain.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘 산책은 쉬어야할 것 같아..ㅠ\"");
            tvWeatherGuide.setTextColor(Color.parseColor("#a3a3a3"));
            return "비";
        }
        else if (weather.equals("thunderstorm")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("thunderstorm.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘 산책은 쉬어야할 것 같아..ㅠ\"");
            return "비&천둥";
        } else if (weather.equals("shower rain")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("rain.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘 산책은 쉬어야할 것 같아..ㅠ\"");
            return "비";
        } else if (weather.equals("light rain")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("rain.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 오늘 산책은 쉬어야할 것 같아..ㅠ\"");
            return "비";
        }
        else if (weather.equals("mist")) {
            animationView = (LottieAnimationView) findViewById(R.id.ani_sunny);
            animationView.setAnimation("cloud.json");
            animationView.setRepeatCount(10);
            animationView.playAnimation();
            tvWeatherGuide.setText("\"집사, 날씨는 아직 흐리지만 비가 올 수도 있겠어\"");
            return "흐림";
        }
        // Todo 채울 것 로그 찍을 것
        return "NULL";
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LifeCycleActivity", "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LifeCycleActivity", "onResume()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LifeCycleActivity", "onPause()");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LifeCycleActivity", "onStop()");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycleActivity", "onDestroy()");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LifeCycleActivity", "onRestart()");

    }
    // intent 객체를 생성하여 화면 전환 액티비티가 실행되도록 설정
    @Override
    public void onClick(View v) {
        // GoWalk.class 를 열라는 내용의 인텐트(편지지)가 전달된다.
        if (v.getId() == R.id.btn_main_go_walk) {
            Intent open_go_walk = new Intent(this, GoWalkActivity.class);
            startActivity(open_go_walk);
        } else if (v.getId() == R.id.btn_main_record) {
            Intent open_record = new Intent(this, FoodActivity.class);
            startActivity(open_record);
        } else if (v.getId() == R.id.btn_main_diary) {
            Intent open_diary = new Intent (this, SettingInfoActivity.class);
            startActivity(open_diary);
        } else if (v.getId() == R.id.btn_main_meal_record) {
            Intent open_meal = new Intent (this, AlarmActivity.class);
            startActivity(open_meal);
        } else if (v.getId() == R.id.btn_walk_diary) {
            Intent open_walk_diary = new Intent (this, WalkDiaryActivity.class);
            startActivity(open_walk_diary);
        }

    }

}
