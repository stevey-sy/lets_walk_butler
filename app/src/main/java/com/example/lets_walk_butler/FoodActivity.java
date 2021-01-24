package com.example.lets_walk_butler;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_walk_butler.adapter.MealListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodActivity extends AppCompatActivity implements View.OnClickListener{

    // 식사 일지를 저장할 array list
    private ArrayList<MealMemoItem> mArrayList;
    private MealListAdapter mAdapter = null;
    // 저장되어 있는 강아지 이름 리스트를 담을 array list
    private ArrayList<String> nameList;
    private ArrayAdapter<String> namesAdapter;

    Toolbar toolbar;
    ActionBar actionBar;
    Menu action;

    String foodName = null;
    String foodWeight = null;
    String memo = null;
    StringBuilder stringBuilder = new StringBuilder();
    String[] array;
    // 음식이름, 음식무게, 메모 문자열 자료가 합쳐질 변수
    String strDataSet = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        // 툴바 설정
        toolbar = findViewById(R.id.toolbar_food);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_left_arrow);

//        MealMemoItem data = new MealMemoItem("로얄사료", "120", "잘 먹는다.");
//        mArrayList.add(data);
        // SharedPreference 에 저장되어 있는 식사 데이터를 로딩
        getSharedPreferencesData();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(FoodActivity.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // 식사 내용을 저장할 array list 생성
        mArrayList = new ArrayList<>();
        mAdapter = new MealListAdapter(getApplicationContext(), mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        // 추가 버튼 눌렀을 때
        Button buttonInsert = (Button)findViewById(R.id.btn_meal_write);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 식사할 강아지의 이름 선택을 위해 프로필 데이터 조회
                checkProfiles();
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodActivity.this);
                View view = LayoutInflater.from(FoodActivity.this).inflate(R.layout.dialog_edit_meal, null, false);
                builder.setView(view);

                final Spinner nameSpinner = (Spinner) view.findViewById(R.id.spinner_dog_name);
                nameSpinner.setAdapter(namesAdapter);
                nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String guideMessage = "강아지를 선택해주세요";
                        if(!nameList.get(i).contains(guideMessage)) {
                            Toast.makeText(getApplicationContext(),nameList.get(i)+"가 선택되었습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        Log.d("Name List ", "비어 있음");
                    }
                });
                final Button ButtonSubmit = (Button) view.findViewById(R.id.register);
                final EditText editName = (EditText) view.findViewById(R.id.food_name);
                final EditText editWeight = (EditText) view.findViewById(R.id.food_weight);
                final EditText editMemo = (EditText) view.findViewById(R.id.memo);

                ButtonSubmit.setText("작성 완료");

                final AlertDialog dialog = builder.create();

                ButtonSubmit.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        // 사용자가 입력한 내용을 가져옴
                        foodName = editName.getText().toString();
                        foodWeight = editWeight.getText().toString();
                        memo = editMemo.getText().toString();

                        // 가져온 내용을 ArrayList에 추가
                        MealMemoItem mealData = new MealMemoItem(foodName, foodWeight, memo);
                        mArrayList.add(0, mealData);

                        // StringBuilder 에 사용자가 입력한 여러 문자열들을 한 문자열 변수에 모두 모은다.
                        stringBuilder.append(foodName + ",");
                        stringBuilder.append(foodWeight + ",");
                        stringBuilder.append(memo + ",");
                        strDataSet = strDataSet + stringBuilder.toString();
                        Log.d("stringBuilder 작동", strDataSet);
                        // split 함수 사용
                        array = strDataSet.split(",");
                        // 합쳐진 문자열을 String array 에 나열
                        for(String box : array) {
                            System.out.println(box);
                        }
                        // SharedPreference 을 생성하여 작성한 문자열을 저장.
                        SharedPreferences prefs = getSharedPreferences("MEAL_FILE", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString("mealLog", strDataSet);
                        editor.apply();

                        mAdapter.notifyItemInserted(0);
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void checkProfiles() {
        // 강아지의 이름들을 담을 array list 생성
        nameList = new ArrayList<>();
        // Shared Preferences 에서 강아지 프로필 조회
        // 강아지 이름을 array list 에 추가
        getPuppyNames(nameList);
        // 이름 데이터가 들어가 있는 array list 를
        // Spinner 에 사용하기 위해 Array Adapter 적용
        namesAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, nameList);
    }

    private void getPuppyNames(ArrayList<String> nameList) {
        nameList.add("강아지를 선택해주세요");
        SharedPreferences prefs = getSharedPreferences("DOG_FILE", MODE_PRIVATE);
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
                    // 읽어온 데이터를 사용자가 볼 수 있도록 출력한다.
                    nameList.add(dogName);
                    Log.d("강아지 이름 ", dogName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
//                addProfile();
                return true;
            // 툴바 홈 버튼 눌렀을 때의 이벤트
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // SharedPreference 에 저장된 문자열을 가져올 때 사용할 메서드
    public void getSharedPreferencesData() {
        SharedPreferences prefs = getSharedPreferences("MEAL_FILE", MODE_PRIVATE);
        strDataSet = prefs.getString("mealLog", "");

        if (strDataSet != null) {
            array = strDataSet.split(",");
            try {
                // [i] / [i]+1 / [i]+2 / 방식으로 문자열마다 하나의 자리를 생성한다.
                for(int i=0; i< array.length; i+=3) {
                    foodName = array[i];
                    foodWeight = array[i+1];
                    memo = array [i+2];

                    //구분된 문자열을 아이템에 추가한다.
                    MealMemoItem mealData = new MealMemoItem(foodName, foodWeight, memo);
                    mArrayList.add(0, mealData);
                    mAdapter.notifyItemInserted(0);
                }
            } catch (Exception e) {
                System.out.println("데이터 로딩 배열 오류");
            }
        }
    }

    @Override
    public void onClick(View v) {
//        registerMemo();
    }
}
