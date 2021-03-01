package com.project_sy.lets_walk_butler;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.project_sy.lets_walk_butler.adapter.CustomAdapter;
import com.project_sy.lets_walk_butler.setting_info.SettingItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SettingInfoActivity extends AppCompatActivity {

    ListView listView = null;
    CustomAdapter adapter = null;
    ArrayList<SettingItem> settingDataArrayList = new ArrayList<SettingItem>();

    Toolbar toolbar;
    ActionBar actionBar;
    Menu action;

    String dogName = null;
    String strAge = null;
    String dogWeight = null;
    String dogCategory = null;
    Uri uriPhoto = null;
    String category = null;
    Bitmap bitmap;
    // 이미지를 저장할 파일
    private File tempFile;

    public static final int GET_GALLERY_VALUE = 1002;
    int PERMISSION_ID = 1001;

    Intent cameraIntent;
    ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_info);

        // 툴바 설정
        toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        // 사용자에게 카메라 사용 권한을 묻는다.
        askPermission();
//        requestPermissions();
        // 사용자로부터 사용가능한 카메라 기능을 할 수 있는 앱을 확인한다
//        PackageManager packageManager = getPackageManager(); if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
//        }

        // 저장되어있는 SharedPreference 데이터를 불러오는 메서드
        // 레이아웃과 연결
        listView = (ListView) findViewById(R.id.setting_listview);
        // adapter 생성
        adapter = new CustomAdapter(this, R.layout.item_setting, settingDataArrayList);
        // 리스트뷰와 adapter 연결
        listView.setAdapter(adapter);
        // 리스트뷰를 클릭했을 떄 인식하도록 설정
        listView.setOnItemClickListener(listener);

        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SettingItem item = (SettingItem) parent.getItemAtPosition(position);
            }
        });
        getSharedPreferenceData ();
    }
    // 툴바 메뉴 불러오기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu, menu);
        action = menu;
        return true;
    }
    // 툴바 메뉴 클릭시 이벤트 (프로필 추가, 뒤로가기)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_add:
                // 메뉴바에서 추가 버튼 눌렸을 때
                addProfile();
                return true;
            // 툴바 홈 버튼 눌렀을 때의 이벤트
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // 사용자가 리스트의 아이템을 클릭했을 때 컨텍스트 메뉴가 작동되도록 레이아웃을 연결 시킴.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void addProfile() {
        // 리스트 아이템 추가할 수 있는 다이어로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingInfoActivity.this);
        View view = LayoutInflater.from(SettingInfoActivity.this).inflate(R.layout.dialog_setting_info, null, false);
        builder.setView(view);
        // 다이어로그 구성요소들 레이아웃 연결.
        final Button BtnSubmit = (Button) view.findViewById(R.id.btn_setting_complete);
        final EditText editName = (EditText) view.findViewById(R.id.edit_name);
        final EditText editAge = (EditText) view.findViewById(R.id.edit_dog_age);
        final EditText editWeight = (EditText)view.findViewById(R.id.edit_dog_weight);
        final EditText editCategory = (EditText)view.findViewById(R.id.setting_category);
        ivPhoto = (ImageView)view.findViewById(R.id.setting_image);

        // 사용자가 dialog 에서 이미지를 클릭했을 때의 작동
        ivPhoto.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                selectGalleryKitkat();
            }
        });

        final AlertDialog dialog = builder.create();

        // 작성 완료버튼 눌렀을 떄
        BtnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dogName = editName.getText().toString();
                strAge = editAge.getText().toString();
                dogWeight = editWeight.getText().toString();
                dogCategory = editCategory.getText().toString();
//                        dogProfile = editProfile.get().toString();

                SettingItem item = new SettingItem (dogName, strAge, dogWeight, dogCategory, uriPhoto);
                settingDataArrayList.add(item);

                // SharedPreference 에 데이터를 저장하는 메서드
                saveSettingData();
                // 어뎁터에 데이터 변경을 갱신시킴.
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                Log.d("리스트뷰 아이템", "추가");
            }
        });

        dialog.show();
    }

    // 컨텍스트 메뉴가 선택되었을 때의 작동.
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        // 사용자가 선택한 리스트 아이템의 위치를 알려줄 변수.
        final int index = info.position;
        int itemId = item.getItemId();
        //사용자가 수정버튼 클릭했을 때,
        if (itemId == R.id.modify) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingInfoActivity.this);
            View view = LayoutInflater.from(SettingInfoActivity.this).inflate(R.layout.dialog_setting_info, null, false);
            builder.setView(view);
            // Dialog xml 연결
            final Button btnSubmit = (Button) view.findViewById(R.id.btn_setting_complete);
            final EditText editName = (EditText) view.findViewById(R.id.edit_name);
            final EditText editAge = (EditText) view.findViewById(R.id.edit_dog_age);
            final EditText editWeight = (EditText)view.findViewById(R.id.edit_dog_weight);
            final EditText editCategory = (EditText)view.findViewById(R.id.setting_category);
            ivPhoto = (ImageView)view.findViewById(R.id.setting_image);

            // 프로필 게시글 번호를 Array List 의 index 로 인식하여 데이터를 view 에 입힌다.
            editName.setText(settingDataArrayList.get(index).getDogNameStr());
            editAge.setText(settingDataArrayList.get(index).getDogAgeStr());
            editWeight.setText(settingDataArrayList.get(index).getDogWeight());
            editCategory.setText(settingDataArrayList.get(index).getDogCategory());
            ivPhoto.setImageURI(settingDataArrayList.get(index).getIconUri());
            uriPhoto = settingDataArrayList.get(index).getIconUri();

            // 사용자가 dialog 에서 이미지를 클릭했을 때의 작동
            ivPhoto.setOnClickListener(new View.OnClickListener(){
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    selectGalleryKitkat();
                }
            });

            final AlertDialog submitDialog = builder.create();
            btnSubmit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 수정완료 버튼을 클릭했을 때, SharedPreference를 생성한다.
                    SharedPreferences prefs = getSharedPreferences("DOG_FILE", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    String dogName = editName.getText().toString();
                    String dogAge = editAge.getText().toString();
                    String dogWeight = editWeight.getText().toString();
                    String dogCategory = editCategory.getText().toString();

                    // 사용자가 입력한 정보를 가져와 객체에 담는다.
                    SettingItem editItem = new SettingItem(dogName, dogAge, dogWeight, dogCategory, uriPhoto);
                    settingDataArrayList.set(index, editItem);
                    // SharedPreference에 저장되어 있는 dogInfo 키의 자료를 불러온다.
                    String json = prefs.getString("dogInfo", null);
                    try {
                        //json String 을 매개변수로 받는 JSONArray 생성, JSON Object 를 불러온다.
                        JSONArray jsonArray = new JSONArray(json);
                        JSONObject dogInfoObject = jsonArray.getJSONObject(index);
                        // JSON Object가 해당 키값에 매치되는 데이터를 가져온다.
                        dogInfoObject.getString("DogName");
                        dogInfoObject.getString("DogAge" );
                        dogInfoObject.getString("DogWeight");
                        dogInfoObject.getString("DogCategory");
                        dogInfoObject.getString("DogUri");
                        //dogInfoObject.getString("DegType");
                        // 가져온 데이터를 사용자가 입력한 데이터와 바꾼다.
                        dogInfoObject.put("DogName", dogName);
                        dogInfoObject.put("DogAge", dogAge);
                        dogInfoObject.put("DogWeight", dogWeight);
                        dogInfoObject.put("DogCategory", dogCategory);
                        dogInfoObject.put("DogUri", uriPhoto);
                        // dogInfoObject.put("DogType", category);
                        // 바꿔진 데이터를 다시 Json Array에 입력한다.
                        jsonArray.put(index, dogInfoObject);
                        // 다시 SharedPreference 에 저장한다.
                        editor.putString("dogInfo", jsonArray.toString());
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
            SharedPreferences prefs = getSharedPreferences("DOG_FILE", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            // json object에 string으로 저장되어 있는 데이터를 불러온다.
            String json = prefs.getString("dogInfo", null);
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
            editor.remove("DogName");
            editor.remove("DogAge");
            editor.remove("DogWeight");
            editor.remove("DogCategory");
            editor.remove("DogUri");

            // 제거 후, 데이터를 다시 SharedPreference에 저장한다.
            editor.putString("dogInfo", jsonArray.toString());
            settingDataArrayList.remove(index);
            adapter.notifyDataSetChanged();
            editor.apply();
            Log.d("리스트뷰 아이템", "삭제");
            Toast.makeText(this, "항목이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        }
    };
    // 사용자에게 사진 사용의 권한을 허가 받는다.
    private void askPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
//                .setRationaleMessage(getResources().getString(R.string.permission_picture))
                .setDeniedMessage(getResources().getString(R.string.permission_setting))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
//                       ,Manifest.permission.CAMERA
                )
                .check();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void selectGalleryKitkat () {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 고르고 나면, startActivityForResult 로 넘어간다.
        startActivityForResult(intent, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 겔러리에서 선택된 이미지를 view 에 뿌려준다.
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    ContentResolver resolver = this.getContentResolver();
                    resolver.takePersistableUriPermission(data.getData(), takeFlags);

                    uriPhoto= data.getData();
                    ivPhoto.setImageURI(uriPhoto);
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uriPhoto);
                    bitmap = ImageDecoder.decodeBitmap(source);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setImage() {
        ImageView imageView = findViewById(R.id.setting_image);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        imageView.setImageBitmap(originalBm);
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate (Bitmap src, float degree) {

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private String getRealPathFromURI (Uri contentUri) {
        int column_index = 0;
        String [] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }


    // SharedPreference에 데이터를 저장할 때 사용할 메서드
    public void saveSettingData () {
        //dogInfoFile이라는 이름으로 파일 생성.
        SharedPreferences prefs = getSharedPreferences("DOG_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String json = prefs.getString("dogInfo", null);


        //한 아이템의 JSON 데이터가 들어갈 ARRAY 선언
        //한 아이템의 데이터가 들어갈 JSONObject 선언
        try {
            if (json == null) {
                // json 이 null 일 때에는 App이 null point로 꺼지므로, null 일 때에는 초기화된 JSON Array를 생성.
                JSONArray jsonArray = new JSONArray();
                JSONObject dogInfoObject = new JSONObject();
                dogInfoObject.put("DogName", dogName);
                dogInfoObject.put("DogAge", strAge);
                dogInfoObject.put("DogWeight", dogWeight);
                dogInfoObject.put("DogCategory", dogCategory);
                dogInfoObject.put("DogUri", uriPhoto);
                jsonArray.put(dogInfoObject);
                // SharedPreference 에 저장.
                editor.putString("dogInfo", jsonArray.toString());
            } else {
                JSONArray dogArray = new JSONArray(json);
                JSONObject dogInfoObject = new JSONObject();
                dogInfoObject.put("DogName", dogName);
                dogInfoObject.put("DogAge", strAge);
                dogInfoObject.put("DogWeight", dogWeight);
                dogInfoObject.put("DogCategory", dogCategory);
                dogInfoObject.put("DogUri", uriPhoto);
                //  "dogInfo" : [{"개이름" : "뽀삐"},{"나이" : " 2"} / {"개이름" : "아롱이"},{"나이" : " 2"}
                // dogInfo 라는 키에 여러 값을 저장 할 수 있다.
                dogArray.put(dogInfoObject);
                editor.putString("dogInfo", dogArray.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    // SharedPreference 에 저장된 데이터를 불러올 때 사용할 메서드
    private void getSharedPreferenceData () {
        SharedPreferences prefs = getSharedPreferences("DOG_FILE", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();

        // 쉐어드에 저장한 문자열을 불러온다.
        String json = prefs.getString("dogInfo", null);

        //만약 sharedpreference에 저장된 값이 null이 아니라면,
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                // Json Array에 저장되어 있던 데이터를 읽어온다.
                for (int i=0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dogName = jsonObject.getString("DogName");
                    String dogAge = jsonObject.getString("DogAge");
                    String dogWeight = jsonObject.getString("DogWeight");
                    String dogCategory = jsonObject.getString("DogCategory");
                    String dogPhoto = jsonObject.getString("DogUri");

                    Uri dogPhotoUri = Uri.parse(dogPhoto);

//                    if (dogPhotoUri != null) {
//                        Glide.with(this).load(dogPhotoUri.toString()).into(ivPhoto);
//                    }


                    //String dogCategory = jsonObject.getString("DogType");
                    // 읽어온 데이터를 사용자가 볼 수 있도록 출력한다.
                    settingDataArrayList.add(new SettingItem(dogName,dogAge,dogWeight,dogCategory,dogPhotoUri));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
