package com.example.lets_walk_butler;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.lets_walk_butler.walk_log.WalkDTO;
import com.example.lets_walk_butler.walk_log.WalkLogAdapter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WalkDiaryActivity extends AppCompatActivity {

    ListView listView = null;
    WalkLogAdapter adapter = null;
    ArrayList<WalkDTO> walkDTOArrayList = new ArrayList<WalkDTO> ();

    Toolbar toolbar;
    ActionBar actionBar;

    String strWalkDate = null;
    String strWalkTime = null;
    String strWalkStepNumber = null;
    String strMemo = null;
    String strMeter = null;
    Uri uriPhoto = null;

    Intent photoIntent;

    ImageView ivPhoto = null;
    public static final int KITKAT_VALUE = 1002;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_diary);

        // 툴바 설정
        toolbar = findViewById(R.id.toolbar_walk);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        // 사용자에게 카메라 사용 권한을 묻는다.
        askPermission();
        // 사용자로부터 사용가능한 카메라 기능을 할 수 있는 앱을 확인한다
//        PackageManager packageManager = getPackageManager(); if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
//        }
        
        // xml 연결
        listView = (ListView) findViewById(R.id.list_view_walk_diary);
        adapter = new WalkLogAdapter(this, R.layout.walk_diary_item, walkDTOArrayList);
        
        // list view 세팅
        listView.setAdapter(adapter);
        // Context Menu 등록.
        registerForContextMenu(listView);
        // 리스트뷰 클릭 이벤트
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 2021-01-27  pop up 메뉴 생성 부분

                WalkDTO item = (WalkDTO) parent.getItemAtPosition(position);
            }
        });

        // SharedPreference 데이터 로딩
        getSharedPreferenceData();
    }

    // 툴바 메뉴 불러오기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu, menu);
        return true;
    }
    // 툴바 메뉴 클릭시 이벤트 (프로필 추가, 뒤로가기)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_add:
                // 메뉴바에서 추가 버튼 눌렸을 때
                addWalkLog();
                return true;
            // 툴바 홈 버튼 눌렀을 때의 이벤트
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addWalkLog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WalkDiaryActivity.this);
        View view = LayoutInflater.from(WalkDiaryActivity.this).inflate(R.layout.dailog_walk_diary, null, false);
        builder.setView(view);

        final Button BtnSubmit = (Button) view.findViewById(R.id.btn_walk_dialog);
        final EditText editDate = (EditText) view.findViewById(R.id.insert_walk_date);
        final EditText editTime = (EditText) view.findViewById(R.id.insert_walk_time);
        final EditText editStepNumber = (EditText)view.findViewById(R.id.insert_walk_step);
        final EditText editMemo = (EditText)view.findViewById(R.id.insert_walk_memo);
        final EditText editMeter = (EditText)view.findViewById(R.id.insert_walk_meter);
        ivPhoto = (ImageView)view.findViewById(R.id.walklog_photo);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGallery();
            }
        });

        final AlertDialog dialog = builder.create();
        // 작성 완료 버튼 눌렀을 때
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strWalkDate = editDate.getText().toString();
                strWalkTime = editTime.getText().toString();
                strWalkStepNumber = editStepNumber.getText().toString();
                strMemo = editMemo.getText().toString();
                strMeter = editMeter.getText().toString();

                WalkDTO item = new WalkDTO (strWalkDate, strWalkTime, strWalkStepNumber, strMeter, strMemo, uriPhoto);
                walkDTOArrayList.add(item);

                saveSettingData();


                adapter.notifyDataSetChanged();
                dialog.dismiss();

                Log.d("리스트뷰 아이템", "추가");
            }
        });
        dialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.walklog_context_menu, menu);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        // 사용자가 선택한 리스트 아이템의 위치를 알려줄 변수.
        final int index = info.position;

        int itemId = item.getItemId();
        //사용자가 수정버튼 클릭했을 때,
        if (itemId == R.id.modify) {

            AlertDialog.Builder builder = new AlertDialog.Builder(WalkDiaryActivity.this);
            View view = LayoutInflater.from(WalkDiaryActivity.this).inflate(R.layout.dailog_walk_diary, null, false);
            builder.setView(view);

            final Button btnSubmit = (Button) view.findViewById(R.id.btn_walk_dialog);
            final EditText editDate = (EditText) view.findViewById(R.id.insert_walk_date);
            final EditText editTime = (EditText) view.findViewById(R.id.insert_walk_time);
            final EditText editStepNumber = (EditText)view.findViewById(R.id.insert_walk_step);
            final EditText editMemo = (EditText)view.findViewById(R.id.insert_walk_memo);
            final EditText editMeter = (EditText)view.findViewById(R.id.insert_walk_meter);
            final ImageView editWalkImage = (ImageView)view.findViewById(R.id.walklog_photo);
            ivPhoto = (ImageView)view.findViewById(R.id.walklog_photo);

            // 사용자가 입력한 데이터가 좌표를 따라서 세팅된다.
            editDate.setText(walkDTOArrayList.get(index).getDate());
            editTime.setText(walkDTOArrayList.get(index).getWalkTime());
            editStepNumber.setText(walkDTOArrayList.get(index).getWalkStep());
            editMemo.setText(walkDTOArrayList.get(index).getMemo());
            editMeter.setText(walkDTOArrayList.get(index).getWalkMeter());
            // 사용자가 dialog 에서 이미지를 클릭했을 때의 작동
            ivPhoto.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    selectGallery();
                }
            });

            ivPhoto.setImageURI(walkDTOArrayList.get(index).getWalkImageUri());


            final AlertDialog submitDialog = builder.create();
            btnSubmit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 수정완료 버튼을 클릭했을 때, SharedPreference를 생성한다.
                    SharedPreferences prefs = getSharedPreferences("WALK_FILE", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    String walkDate = editDate.getText().toString();
                    String walkTime = editTime.getText().toString();
                    String walkStepCount = editStepNumber.getText().toString();
                    String walkMemo = editMemo.getText().toString();
                    String walkMeter = editMeter.getText().toString();

                    // 사용자가 입력한 정보를 가져와 객체에 담는다.
                    WalkDTO editItem = new WalkDTO(walkDate, walkTime, walkStepCount, walkMeter, walkMemo, uriPhoto);
                    walkDTOArrayList.set(index, editItem);
                    // SharedPreference에 저장되어 있는 dogInfo 키의 자료를 불러온다.
                    String json = prefs.getString("walkLog", null);
                    try {
                        //json String 을 매개변수로 받는 JSONArray 생성, JSON Object 를 불러온다.
                        JSONArray jsonArray = new JSONArray(json);
                        JSONObject dogInfoObject = jsonArray.getJSONObject(index);
                        // JSON Object가 해당 키값에 매치되는 데이터를 가져온다.
                        dogInfoObject.getString("WalkDate");
                        dogInfoObject.getString("WalkTime" );
                        dogInfoObject.getString("WalkStep");
                        dogInfoObject.getString("WalkMeter");
                        dogInfoObject.getString("WalkMemo");
                        dogInfoObject.getString("WalkImageUri");
                        //dogInfoObject.getString("DegType");
                        // 가져온 데이터를 사용자가 입력한 데이터와 바꾼다.
                        dogInfoObject.put("WalkDate", walkDate);
                        dogInfoObject.put("WalkTime", walkTime);
                        dogInfoObject.put("WalkStep", walkStepCount);
                        dogInfoObject.put("WalkMeter", walkMeter);
                        dogInfoObject.put("WalkMemo", walkMemo);
                        dogInfoObject.put("WalkImageUri", uriPhoto);
                        // dogInfoObject.put("DogType", category);
                        // 바꿔진 데이터를 다시 Json Array에 입력한다.
                        jsonArray.put(index, dogInfoObject);
                        // 다시 SharedPreference 에 저장한다.
                        editor.putString("walkLog", jsonArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    editor.apply();
                    adapter.notifyDataSetChanged();
                    submitDialog.dismiss();
                    Log.d("리스트뷰 아이템", "수정");
                }
            });
            submitDialog.show();
        } else if (itemId == R.id.delete) {
            // 삭제 버튼을 눌렀을 때,
            SharedPreferences prefs = getSharedPreferences("WALK_FILE", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            // json object에 string으로 저장되어 있는 데이터를 불러온다.
            String json = prefs.getString("walkLog", null);
            JSONArray jsonArray = null;
            try {
                // 불러온 데이터를 JSON Array에 나열한다.
                jsonArray = new JSONArray(json);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    // 사용자가 지정한 위치의 데이터를 제거한다.
                    jsonArray.remove(index);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert jsonArray != null;
            // SharedPreference 에 데이터가 남지않도록 삭제한다.
            editor.remove("WalkDate");
            editor.remove("WalkTime");
            editor.remove("WalkStep");
            editor.remove("walkMeter");
            editor.remove("WalkMemo");
            editor.remove("WalkImageUri");

            // 제거 후, 데이터를 다시 SharedPreference에 저장한다.
            editor.putString("walkLog", jsonArray.toString());
            walkDTOArrayList.remove(index);
            adapter.notifyDataSetChanged();
            editor.apply();
            Log.d("리스트뷰 아이템", "삭제");
            Toast.makeText(this, "항목이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    // 사용자에게 사진 사용의 권한을 허가 받는다.
    private void askPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                Toast.makeText(getApplicationContext(), "사용 권한을 허가했습니다." , Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                Toast.makeText(getApplicationContext(), "사용 권한을 거부하셨습니다. 설정에서 변경이 가능합니다." , Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_picture))
                .setDeniedMessage(getResources().getString(R.string.permission_setting))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void selectGallery () {

        if (Build.VERSION.SDK_INT < 19) {
            photoIntent = new Intent();
            photoIntent.setAction(Intent.ACTION_GET_CONTENT);
            photoIntent.setType("image/*");
            startActivityForResult(photoIntent, KITKAT_VALUE);
        } else {
            photoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            photoIntent.addCategory(Intent.CATEGORY_OPENABLE);
            photoIntent.setType("image/*");
            startActivityForResult(photoIntent, KITKAT_VALUE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KITKAT_VALUE) {
            if (resultCode == Activity.RESULT_OK) {
                uriPhoto = data.getData();
                if (uriPhoto != null) {
                    Glide.with(this).load(uriPhoto.toString()).into(ivPhoto);
                }
                // do something here
            }
        }
    }

    // SharedPreference에 데이터를 저장할 때 사용할 메서드
    public void saveSettingData () {
        //dogInfoFile이라는 이름으로 파일 생성.
        SharedPreferences prefs = getSharedPreferences("WALK_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String json = prefs.getString("walkLog", null);

        //한 아이템의 JSON 데이터가 들어갈 ARRAY 선언
        //한 아이템의 데이터가 들어갈 JSONObject 선언
        try {
            if (json == null) {
                // json 이 null 일 때에는 App이 null point로 꺼지므로, null 일 때에는 초기화된 JSON Array를 생성.
                JSONArray jsonArray = new JSONArray();
                JSONObject dogInfoObject = new JSONObject();
                dogInfoObject.put("WalkDate", strWalkDate);
                dogInfoObject.put("WalkTime", strWalkTime);
                dogInfoObject.put("WalkStep", strWalkStepNumber);
                dogInfoObject.put("WalkMeter", strMeter);
                dogInfoObject.put("WalkMemo", strMemo);
                dogInfoObject.put("WalkImageUri", uriPhoto);
                jsonArray.put(dogInfoObject);
                // SharedPreference 에 저장.
                editor.putString("walkLog", jsonArray.toString());
            } else {
                JSONArray dogArray = new JSONArray(json);
                JSONObject dogInfoObject = new JSONObject();
                dogInfoObject.put("WalkDate", strWalkDate);
                dogInfoObject.put("WalkTime", strWalkTime);
                dogInfoObject.put("WalkStep", strWalkStepNumber);
                dogInfoObject.put("WalkMeter", strMeter);
                dogInfoObject.put("WalkMemo", strMemo);
                dogInfoObject.put("WalkImageUri", uriPhoto);
                dogArray.put(dogInfoObject);
                editor.putString("walkLog", dogArray.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }
    // SharedPreference 에 저장된 자료를 불러올 때 사용할 메서드
    private void getSharedPreferenceData () {
        SharedPreferences prefs = getSharedPreferences("WALK_FILE", MODE_PRIVATE);

        // 쉐어드에 저장한 문자열을 불러온다.
        String json = prefs.getString("walkLog", null);

        //만약 sharedpreference에 저장된 값이 null이 아니라면,
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                // Json Array에 저장되어 있던 데이터를 읽어온다.
                for (int i=0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String walkDate = jsonObject.getString("WalkDate");
                    String walkTime = jsonObject.getString("WalkTime");
                    String walkStepCount = jsonObject.getString("WalkStep");
                    String walkMeter = jsonObject.getString("WalkMeter");
                    String walkMemo = jsonObject.getString("WalkMemo");
                    String walkImage = jsonObject.getString("WalkImageUri");

                    Uri walkPhotoUri = Uri.parse(walkImage);

                    // 읽어온 데이터를 사용자가 볼 수 있도록 출력한다.
                    walkDTOArrayList.add(new WalkDTO(walkDate,walkTime,walkStepCount,walkMeter,walkMemo,walkPhotoUri));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
