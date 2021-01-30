package com.example.lets_walk_butler;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoWalkActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback, SensorEventListener {

    private GoogleMap mMap;
    private Marker currentMarker = null;

    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private int mStepDetector;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    // 위치 업데이트 주기 1초
    private static final int UPDATE_INTERVAL_MS = 1000;
    // 가장 빠른 업데이트 주기 0.5초초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;
    private static final int REQUEST_IMAGE_CAPTURE = 3001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int MY_PERMISSION_STORAGE = 1111;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final int GET_GALLERY_VALUE = 1002;

    File uploadImage;
    boolean needRequest = false;
    Location mCurrentLocation;
    LatLng currentPosition = null;
    LatLng previousPosition = null;
    // GPS 위치를 지속적으로 얻어줄 스위치
    int tracking = 0;

    private FusedLocationProviderClient providerClient;
    private LocationRequest locationRequest;
    private Location location;

    Handler meterUpdateHandler;
    String strMeterUpload;
    Double totalDistance = 0.0;
    Double doubleDistance = 0.0;
    double radius = 0.0;
    double maximumRadius = 0.0;
    double distance = 0.0;
    ArrayList<Polyline> polylines = new ArrayList<Polyline>();

    //SupportMapFragment supportMapFragment;

    private View mLayout;
    private Button btnWalkStart, btnWalkPause, btnWalkFinish;
    private TextView tvTime, tvStepCounter, tvMeterCounter;
    private Thread meterThread = null;
    private Thread stopWatchThread = null;
    private String record = "";
    private Boolean timeCountRunning = true;
    private String meterResult = null;
    private String setTime;

    private Intent photoIntent;
    private ImageView cameraCapture = null;

    private String imageFilePath;
    Uri captureUri = null;
    String dialogImagePath;
    String [] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    Toolbar toolbar;
    ActionBar actionBar;
    Menu action;

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 툴바 설정
//        toolbar = findViewById(R.id.toolbar_walk);
//        setSupportActionBar(toolbar);
//        actionBar = getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        // 레이아웃이 변할때마다 플레그를 세팅한다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_go_walk);
        // 라이브러리를 이용한 사용자 권한 허용 요청
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("사용자의 움직임을 감지합니다. 동의하나요?")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.ACTIVITY_RECOGNITION)
                .check();
        // 카메라 권한 체크 부분
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();


        mLayout = findViewById(R.id.layout_go_walk);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                // 1초 간격으로 위치를 업데이트 한다.
                .setInterval(100000)
//                .setInterval(UPDATE_INTERVAL_MS)
                // 가장 빠른 업데이트 간격은 0.5초로 한다.
//                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
                .setFastestInterval(5000);
        // 위치정보 준비를 요청한다.
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        providerClient = LocationServices.getFusedLocationProviderClient(this);
        // 구글 맵 동기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // findViewById. 버튼 레이아웃과 클래스 연동
        //setMissionTime = findViewById(R.id.set_mission_time);
        btnWalkStart = findViewById(R.id.walkStartButton);
        btnWalkPause = findViewById(R.id.pauseButton);
        btnWalkFinish = findViewById(R.id.stopButton);
        tvTime = findViewById(R.id.timeCount);
        Button btnCapture = findViewById(R.id.btnCamera);
        tvStepCounter = findViewById(R.id.calorieCount);
        tvMeterCounter = findViewById(R.id.meterCount);
        cameraCapture = findViewById(R.id.photo_view);

        // 스톱워치 시간표시부분 초기화
        tvTime.setText("00:00:00");
        // 스톱워치의 기록이 표시될 위치의 초기화
//        recordView.setText("");

        // 카메라 버튼 눌렀을 때의 작동
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        Log.d("카메라", "카메라 기능 IOEXCEPTION 에러");
                    }

                    if (photoFile != null) {
                        captureUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }
//                cameraIntent();
//                captureCamera();
            }
        });

        // 각 버튼 눌렀을 때의 작동
        // 시작 버튼 눌렀을 때,
        btnWalkStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                // 스타트 버튼 눌렀을 때, 스타트 버튼은 사라지고 일시정지, 정지 버튼이 보이게 된다.
                v.setVisibility(View.GONE);
                btnWalkPause.setVisibility(View.VISIBLE);
                btnWalkFinish.setVisibility(View.VISIBLE);
//                guideProgress.setVisibility(View.GONE);

                // step Counter
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                // 걸음을 감지할 때마다 결과값 1을 출력한다.
                stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                if (stepCountSensor == null) {
//                    Toast.makeText(this, "No Step Detected", Toast.LENGTH_SHORT).show();
                }

                if (sensorManager != null) {
                    // 센서 메니저가 걸음을 감지하면, UI에 숫자를 올리도록 한다.
                    sensorManager.registerListener(GoWalkActivity.this, stepCountSensor, SensorManager.SENSOR_DELAY_UI);
                } else {
                    Toast.makeText(GoWalkActivity.this, "Sensor Not Found", Toast.LENGTH_SHORT).show();
                }

                // 스타트 버튼을 누를 때마다 새로운 스레드 객체를 새로 만든다.
                stopWatchThread = new Thread(new timeThread());
                stopWatchThread.start();
                //showProgress(tvShowProgress);
                // 스타트 버튼을 누르면, 이전 위치와 현 위치의 정보를 연산한다.
                tracking = 1;
                // Meter 변수의 합계를 구할 스레드
                meterThread = new Thread(new meterThread());
                meterThread.start();

            }
        });

        // 산책정지 버튼 눌렀을 때
        btnWalkFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            // 정지 버튼을 누르면, 일시정지 버튼이 사라지고, 산책 시작 버튼이 다시 생성된다.
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                btnWalkPause.setVisibility(View.GONE);
                btnWalkStart.setVisibility(View.VISIBLE);
//                tvTotalTimeTitle.setVisibility(View.VISIBLE);
//                recordView.setVisibility(View.VISIBLE);

                //쓰레드를 interrupt(); 하여 멈춘다.
                stopWatchThread.interrupt();
                record = String.valueOf(tvTime.getText());
//                recordView.setText(record);
                tvTime.setText("00:00:00");
                // GPS 정보 간 거리차이 연산을 멈춘다.
                tracking = 0;
                meterThread.interrupt();
                // 걸음 감지를 멈춘다.
                sensorManager.unregisterListener(GoWalkActivity.this);

                // 다이어로그 생성하여 사용자가 산책기록을 할 것인지 확인
                final AlertDialog.Builder quitCheckBuilder = new AlertDialog.Builder(GoWalkActivity.this);
                quitCheckBuilder.setTitle ("산책 종료");
                quitCheckBuilder.setMessage("산책을 기록하시겠습니까?");
                quitCheckBuilder.setCancelable(false);
                quitCheckBuilder.setPositiveButton("기록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GoWalkActivity.this);
                        View view = LayoutInflater.from(GoWalkActivity.this).inflate(R.layout.dailog_walk_diary, null, false);
                        builder.setView(view);

                        final Button BtnSubmit = (Button) view.findViewById(R.id.btn_walk_dialog);
                        final EditText editDate = (EditText) view.findViewById(R.id.insert_walk_date);
                        final EditText editTime = (EditText) view.findViewById(R.id.insert_walk_time);
                        final EditText editStepNumber = (EditText)view.findViewById(R.id.insert_walk_step);
                        final EditText editMeter = (EditText)view.findViewById(R.id.insert_walk_meter);
                        final EditText editMemo = (EditText)view.findViewById(R.id.insert_walk_memo);
                        //ivPhoto = (ImageView)view.findViewById(R.id.walklog_photo);

                        // SD카드, 앱 내부에 저장된 이미지 불러오기
                        if (uploadImage != null) {
                            dialogImagePath = uploadImage.getAbsolutePath();
                        }
                        //Environment.getExternalStorageDirectory().getAbsolutePath()는 SD카드의 절대경로를 구하는 매소드
                        //path에 불러올 비트맵파일의 주소명을 초기화 시켜준다.
                        Log.d("TAG", dialogImagePath);
                        BitmapFactory.Options bo = new BitmapFactory.Options();
                        bo.inSampleSize = 2;
                        Bitmap bmp = BitmapFactory.decodeFile(dialogImagePath, bo);
                        //저장되있던 비트맵 불러온다.
                        ImageView imageView = (ImageView)view.findViewById(R.id.walklog_photo);
                        imageView.setImageBitmap(bmp);
                        // 최종 시간기록을 불러온다.
                        editTime.setText(record);
                        // 최종 걸음수를 불러온다.
                        editStepNumber.setText(String.valueOf(mStepDetector));
                        // 최종 meter 이동 거리를 불러온다.
                        editMeter.setText(meterResult);
                        // 기록된 정보를 산책 일지로 넘긴다.
                        final AlertDialog addWalkItemDialog = builder.create();
                        BtnSubmit.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                String strWalkDate = editDate.getText().toString();
                                String strTime = editTime.getText().toString();
                                String strMemo = editMemo.getText().toString();
                                String strMeter = editMeter.getText().toString();
                                String strStepNumber = editStepNumber.getText().toString();
                                // SharedPreference에 저장한다.
                                SharedPreferences prefs = getSharedPreferences("WALK_FILE", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();

                                String json = prefs.getString("walkLog", null);

                                try {
                                    if (json == null) {
                                        // json 이 null 일 때에는 App이 null point로 꺼지므로, null 일 때에는 초기화된 JSON Array를 생성.
                                        JSONArray jsonArray = new JSONArray();
                                        JSONObject dogInfoObject = new JSONObject();
                                        dogInfoObject.put("WalkDate", strWalkDate);
                                        dogInfoObject.put("WalkTime", strTime);
                                        dogInfoObject.put("WalkStep", strStepNumber);
                                        dogInfoObject.put("WalkMeter", strMeter);
                                        dogInfoObject.put("WalkMemo", strMemo);
                                        dogInfoObject.put("WalkImageUri", captureUri);
                                        jsonArray.put(dogInfoObject);
                                        // SharedPreference 에 저장.
                                        editor.putString("walkLog", jsonArray.toString());
                                    } else {
                                        JSONArray dogArray = new JSONArray(json);
                                        JSONObject dogInfoObject = new JSONObject();
                                        dogInfoObject.put("WalkDate", strWalkDate);
                                        dogInfoObject.put("WalkTime", strTime);
                                        dogInfoObject.put("WalkStep", strStepNumber);
                                        dogInfoObject.put("WalkMeter", strMeter);
                                        dogInfoObject.put("WalkMemo", strMemo);
                                        dogInfoObject.put("WalkImageUri", captureUri);

                                        dogArray.put(dogInfoObject);
                                        editor.putString("walkLog", dogArray.toString());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                editor.apply();
                                addWalkItemDialog.dismiss();
                                // 종료된 후에는 메인페이지로 이동한다.
                                Intent goMainPage = new Intent(GoWalkActivity.this, MainActivity.class);
                                startActivity(goMainPage);

                            }
                        });
                        addWalkItemDialog.show();

//                        final AlertDialog dialog = builder.create();

                    }
                })
                .setNegativeButton("종료", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 종료된 후에는 메인페이지로 이동한다.
                        Intent intent = new Intent (GoWalkActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog = quitCheckBuilder.create();
                alertDialog.show();
            }
        });

        // 일시정지 버튼 눌렀을 때
        btnWalkPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeCountRunning = !timeCountRunning;
                // timeCountRunning 이 true 값일 때에만, 스톱워치 스레드가 작동한다,
                // false로 바꿔줌으로 일시정지 효과를 볼 수 있다.
                if(timeCountRunning){
                    btnWalkPause.setText("일시 정지");
                    // 스톱 워치가 멈추면 GPS 추적도 멈춘다.
                    tracking = 0;
                } else {
                    btnWalkPause.setText("계속");
                    tracking = 1;
                }
            }
        });

    }

    // 툴바 메뉴 클릭시 이벤트 (프로필 추가, 뒤로가기)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // 툴바 홈 버튼 눌렀을 때의 이벤트
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        mMap = googleMap;

        setDefaultLocation();
        // 위치 퍼미션을 허가했는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 이미 퍼미션을 가지고 있다면
            // 위치 업데이트 시작
            startLocationUpdates();
        } else {
            // 퍼미션 요청을 허용한 적이 없다면, 요청이 필요.

            // 사용자가 퍼미션을 거부한 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // 사용자에게 퍼미션을 요청.
                        ActivityCompat.requestPermissions (GoWalkActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE );

                    }
                }).show();
            } else {
                // 사용자가 퍼미션 거부를 한 적이 없는 경우, 퍼미션 요청을 바로 한다.
                // 요청 결과는 onRequestPermissionResult 에서 수신된다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10))
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick : ");
            }
        });
    }

    // 위치정보를 갱신하는 작업.
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            // 현 위치의 위도와 경도를 구하여 리스트에 저장한다.
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() >0) {
                location = locationList.get(locationList.size() -1);

                previousPosition = currentPosition;
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                // NullPoint 에러 방지
                if (previousPosition == null) previousPosition = currentPosition;

//                if (previousPosition != currentPosition) {
//                    double radius = 3;
//                    double distance = SphericalUtil.computeDistanceBetween(currentPosition, addedMarker.getPosition());
//                    if ((distance < radius) && (!previousPosition.equals(currentPosition))) {
//                        strMeterUpload = Double.toString(distance);
//                    }
//                }
                // 현 위치와 이전 위치의 거리 차이를 구한다.
                if (tracking == 1) {
                    radius = 0.05;
                    maximumRadius = 4.0;
                    distance = SphericalUtil.computeDistanceBetween(currentPosition, previousPosition);
                    // 현위치와 이전위치의 거리차이가 50m 보다 작으면 위치 정보의 차이를 구한다.
                    // distance = 내가 움직인 거리
                    // radius = ~m 이상 움직였을 때 의 기준
                    if ((distance > radius) && (!previousPosition.equals(currentPosition)) && (distance < maximumRadius)) {
                        Log.d("now distance ", String.valueOf(distance));
                        Log.d("previous Position ", String.valueOf(previousPosition));
                        Log.d("current Position ", String.valueOf(currentPosition));
                        doubleDistance = Math.round(distance*100)/100.0;
                    }
                }
                // 갱신된 정보에 따라 마커가 이동한다.
                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도: "+ String.valueOf(location.getLatitude())
                        + "경도: "+String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : "+ markerSnippet);

                // 현재 위치로 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocation = location;
                drawPath();
            }
        }
    };

    private void drawPath(){
        //polyline을 그려주는 메소드
        PolylineOptions options = new PolylineOptions().add(previousPosition).add(currentPosition).width(15).color(Color.BLACK).geodesic(true);
        polylines.add(mMap.addPolyline(options));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 18));
    }

    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
            providerClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkLocationPermission())mMap.setMyLocationEnabled(true);
        }
    }

        @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkLocationPermission()) {
            Log.d(TAG, "onStart : call client.requestLocationUpdates");
            providerClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }
    }

        @Override
    protected void onStop() {
        super.onStop();
        if (providerClient != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            providerClient.removeLocationUpdates(locationCallback);
        }
    }

    public String getCurrentAddress(LatLng latlng) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);

        } catch (IOException ioException) {
            // 네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GSP 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
//            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String marketTitle, String markerSnippet) {

        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(marketTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
    }

    public void setDefaultLocation() {

        // 디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);
    }

    // 여기부터는 런타임 퍼미션 처리를 위한 메소드
    private boolean checkLocationPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String [] permissions,
                                           @NonNull int [] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면,
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명, 앱을 종료한다.
                // 2가지 경우가 있다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 하면 사용가능
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해 주세요.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            finish();
                        }
                    }).show();
                } else {

                    // "다시 묻지 않음" 을 사용자가 체크하고 거부를 선택한 경우, 기기의 설정에서 권한 허용 가능.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 기기의 설정(앱 정보)에서 권한을 허용해야 합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    // 여기서부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GoWalkActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 수정을 하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent
                        = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                ExifInterface exif = null;

                try {
                    exif = new ExifInterface(imageFilePath);
                    //galleryAddPic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int exifOrientation;
                int exifDegree;
                if (exif != null) {
                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    exifDegree = exifOrientationToDegress(exifOrientation);
                } else {
                    exifDegree = 0;
                }
                ((ImageView) findViewById(R.id.photo_view)).setImageBitmap(rotate(bitmap, exifDegree));
                ImageView imageView = findViewById(R.id.photo_view);
        }

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                // 사용자가 GPS 활성화 했는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되었음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }

    private int exifOrientationToDegress(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // 활용할 수 있는 카메라 앱이 있는 지 체크하는 방법
    public static boolean isIntentAvailable( Context context, String action){
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent( action);
        List<ResolveInfo> list = packageManager.queryIntentActivities( intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    String currentPhotoPath;
    // 파일에 시간을 부여하여 중복되지않도록 방지
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        uploadImage = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imageFilePath = uploadImage.getAbsolutePath();

        return uploadImage;
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        // 퍼미션이 허용되었을 때의 액션
        public void onPermissionGranted() {
            Toast.makeText(GoWalkActivity.this, "카메라 권한이 허가됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        // 퍼미션이 거절되었을 때의 액션
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(GoWalkActivity.this, "카메라 권한이 거부됨\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public void MissionCompleteNotification () {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, GoWalkActivity.class);
        startActivity(notificationIntent);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("집사야, 수고했어!")
                .setContentText("목표 산책시간을 달성하였습니다.")
                .setColor(Color.BLACK)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        } else
            builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

            assert notificationManager != null;
            notificationManager.notify(2, builder.build()); // 고유숫자로 노티피케이션 동작시킴

    }

    // stop watch에 사용될 핸들러
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            //시간 포맷
            //int mSec = msg.arg1 % 100;
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 /100) / 60;
            int hour = (msg.arg1 / 100) / 360;

            String result = String.format("%02d:%02d:%02d", hour,min,sec);
            tvTime.setText(result);

        }
    };

    // 스톱워치 스레드
    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;
            while(true) {
                while(timeCountRunning) {
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }
    // 현위치의 GPS, 이전 위치의 GSP의 거리를 Meter 수치로 변경하여 UI에 나타낸다.
    @SuppressLint("HandlerLeak")
    Handler getMeterUpdateHandler = new Handler() {

        public void handleMessage(@NonNull Message msg) {

            if ((distance > radius) && (!previousPosition.equals(currentPosition)) && (distance < maximumRadius)) {
                totalDistance += doubleDistance;
                // 조건을 건다.
                // total Distance 가 얼만큼 이상 움직이면
                Double result = Math.round(totalDistance*100)/100.0;
                Log.d("totalDistance ", String.valueOf(totalDistance));
                meterResult = Double.toString(result);
                tvMeterCounter.setText(meterResult);
            }
        }

    };

    // 거리 정보가 1초에 한번씩 업데이트 되도록 설정.
    public class meterThread implements Runnable {
        @Override
        public void run() {
            int i = 0;
            while(true) {
                while(timeCountRunning) {
                    Message msg = new Message();
                    msg.arg1 = i++;
                    getMeterUpdateHandler.sendMessage(msg);

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
    protected void onResume() {
        super.onResume();
    }

        @Override
    protected void onPause() {
        super.onPause();
//        sensorManager.unregisterListener(this);

    }

    @Override
    // 걸음이 감지될 때마다 리턴 값 1을 더하는 함수
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                mStepDetector ++;
                tvStepCounter.setText(String.valueOf(mStepDetector));
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
    }
    private void selectGallery () {

        if (Build.VERSION.SDK_INT < 19) {
            photoIntent = new Intent();
            photoIntent.setAction(Intent.ACTION_GET_CONTENT);
            photoIntent.setType("image/*");
            startActivityForResult(photoIntent, GET_GALLERY_VALUE);
        } else {
            photoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            photoIntent.addCategory(Intent.CATEGORY_OPENABLE);
            photoIntent.setType("image/*");
            startActivityForResult(photoIntent, GET_GALLERY_VALUE);
        }
    }
}
