package com.example.lets_walk_butler;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FoodActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // 식사 일지를 저장할 array list
    private ArrayList<MealMemoItem> mArrayList;
    private MealListAdapter mAdapter = null;
    // 저장되어 있는 강아지 이름 리스트를 담을 array list
    private ArrayList<String> nameList;
    private ArrayAdapter<String> namesAdapter;
    // 식사량 측정 단위가 들어갈 array list
    private ArrayList<String> weightList;
    private ArrayAdapter<String> weightAdapter;
    // 식사 타입 리스트
    private ArrayList<String> mealTypeList;
    private ArrayAdapter<String> mealTypeAdapter;

    Toolbar toolbar;
    ActionBar actionBar;
    Menu action;

    String foodName = null;
    String foodWeight = null;
    String memo = null;
    String petName = null;
    String measureType = null;
    String mealDate = null;
    String mealType = null;
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

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(FoodActivity.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // 식사 내용을 저장할 array list 생성
        mArrayList = new ArrayList<>();
        mAdapter = new MealListAdapter(getApplicationContext(), mArrayList);
        mRecyclerView.setAdapter(mAdapter);

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
                addMealLog();
                return true;
            // 툴바 홈 버튼 눌렀을 때의 이벤트
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addMealLog() {
        // 식사할 강아지의 이름 선택을 위해 프로필 데이터 조회
        checkProfiles();
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodActivity.this);
        View view = LayoutInflater.from(FoodActivity.this).inflate(R.layout.dialog_edit_meal, null, false);
        builder.setView(view);

        // 날짜 선택
        final EditText datePicker = (EditText) view.findViewById(R.id.tv_meal_date);
        // 오늘 날짜를 미리 edit text 에 세팅해 놓는다.
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy/MM/dd");
        Date todayDate = new Date();
        String date = dateFormat.format(todayDate);
        datePicker.setText(date);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
                datePicker.setText(mealDate);
            }
        });

        // 강아지 이름 선택
        final Spinner nameSpinner = (Spinner) view.findViewById(R.id.spinner_dog_name);
        nameSpinner.setAdapter(namesAdapter);
        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String guideMessage = "강아지를 선택해주세요";
                if(!nameList.get(i).contains(guideMessage)) {
                    Toast.makeText(getApplicationContext(),nameList.get(i)+"가 선택되었습니다.",
                            Toast.LENGTH_SHORT).show();
                    // 저장할 강아지 이름 세팅
                    petName = nameList.get(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Name List ", "비어 있음");
            }
        });

        // 식사 카테고리 리스트 (식사, 간식, 약, 기타)
        mealTypeList = new ArrayList<>();
        mealTypeList.add("식사 카테고리");
        mealTypeList.add("식사");
        mealTypeList.add("간식");
        mealTypeList.add("약");
        // Spinner 에 사용하기 위해 Array Adapter 적용
        mealTypeAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, mealTypeList);

        // 먹은 음식의 수량 or 무게를 저장할 단위를 선정하는 spinner
        final Spinner mealTypeSpinner = (Spinner) view.findViewById(R.id.meal_category);
        mealTypeSpinner.setAdapter(mealTypeAdapter);
        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // spinner 에서 선택한 측정 단위를 변수에 담는다.
                mealType = mealTypeList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Weight List ", "비어 있음");
            }
        });


        // 식사량 측정 단위 array list 생성
        weightList = new ArrayList<>();
        weightList.add("g");
        weightList.add("개");
        weightList.add("ml");
        // Spinner 에 사용하기 위해 Array Adapter 적용
        weightAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, weightList);

        // 먹은 음식의 수량 or 무게를 저장할 단위를 선정하는 spinner
        final Spinner weightSpinner = (Spinner) view.findViewById(R.id.spinner_weight_type);
        weightSpinner.setAdapter(weightAdapter);
        weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // spinner 에서 선택한 측정 단위를 변수에 담는다.
                measureType = weightList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Weight List ", "비어 있음");
            }
        });

        // Dialog 의 view 와 연결
        final Button ButtonSubmit = (Button) view.findViewById(R.id.register);
        final EditText editName = (EditText) view.findViewById(R.id.food_name);
        final EditText editWeight = (EditText) view.findViewById(R.id.food_weight);
        final EditText editMemo = (EditText) view.findViewById(R.id.memo);

        ButtonSubmit.setText("작성 완료");
        final AlertDialog dialog = builder.create();

        // 작성 완료 버튼이 눌렸을 때의 이벤트
        ButtonSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 사용자가 입력한 내용을 가져옴
                // 식사일
                String date = datePicker.getText().toString();
                // 음식 이름
                foodName = editName.getText().toString();
                // 식사 종류
                String inserted_meal_type = mealType;
                // 사용자가 입력한 음식의 무게 or 개수,
                String inserted_weight = editWeight.getText().toString();
                // spinner 에 적용된 단위를 합친다
                foodWeight = inserted_weight + " " + measureType;
                // 메모 사항
                memo = editMemo.getText().toString();

                StringBuilder stringBuilder = new StringBuilder();
                // StringBuilder 에 사용자가 입력한 여러 문자열들을 한 문자열 변수에 모두 모은다.
                stringBuilder.append(date).append(",");
                stringBuilder.append(petName).append(",");
                stringBuilder.append(mealType).append(",");
                stringBuilder.append(foodName).append(",");
                stringBuilder.append(foodWeight).append(",");
                stringBuilder.append(memo).append(",");
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

                getSharedPreferencesData();

                // 가져온 내용을 ArrayList에 추가
//                MealMemoItem mealData = new MealMemoItem(date, petName, foodName, foodWeight, memo);
//                mArrayList.add(0, mealData);
//
//                mAdapter.notifyItemInserted(0);
//                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    // SharedPreference 에 저장된 문자열을 가져올 때 사용할 메서드
    public void getSharedPreferencesData() {
        SharedPreferences prefs = getSharedPreferences("MEAL_FILE", MODE_PRIVATE);
        String noData = "";
        strDataSet = prefs.getString("mealLog", noData);

        Log.d("strDataSet 로드 ", strDataSet);

        if (strDataSet != null) {
            array = strDataSet.split(",");
            try {
                // [i] / [i]+1 / [i]+2 / 방식으로 문자열마다 하나의 자리를 생성한다.
                for(int i=0; i< array.length; i+=6) {
                    mealDate = array[i];
                    petName = array[i+1];
                    mealType = array[i+2];
                    foodName = array[i+3];
                    foodWeight = array[i+4];
                    memo = array [i+5];

                    //구분된 문자열을 아이템에 추가한다.
                    MealMemoItem mealData = new MealMemoItem(mealDate ,petName, mealType, foodName, foodWeight, memo);
                    mArrayList.add(0, mealData);
                    mAdapter.notifyItemInserted(0);
                    mAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.d("배열 분리 ", "오류 발생");
//                System.out.println("데이터 로딩 배열 오류");
            }
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        String date = year + "/" + month + "/" + dayOfMonth;
        mealDate = date;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferencesData();
    }

}
