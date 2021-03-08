package com.project_sy.lets_walk_butler;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project_sy.lets_walk_butler.adapter.MealListAdapter;

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
    private MealListAdapter.MealLogClickListener listener;
    private RecyclerView mRecyclerView;
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
    TextView tv_no_data = null;
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

        tv_no_data = (TextView) findViewById(R.id.guide_message_no_data);

        // 리사이클러 뷰 세팅
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(FoodActivity.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new MealListAdapter(getApplicationContext(), mArrayList, listener);
        // 리사이클러 뷰 클릭 이벤트 (수정, 삭제)
        listener = new MealListAdapter.MealLogClickListener() {
            @Override
            public void onRowClick(View view, final int position) {
                Toast.makeText(getApplicationContext(), "onRowClick " + String.valueOf(position), Toast.LENGTH_SHORT).show();
//                // 수정, 삭제 popup 메뉴 생성
                PopupMenu p = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.setting_context_menu, p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()) {
                            // 수정 버튼
                            case R.id.modify:
                                getDialogForEdit(position);
                                return true;
                            // 삭제 버튼
                            case R.id.delete:
                                AlertDialog.Builder dialog = new AlertDialog.Builder(FoodActivity.this);
                                dialog.setMessage("기록을 삭제하시겠습니까?");
                                dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteMealLog(position);
                                        dialog.dismiss();
                                    }
                                });
                                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                                return true;
                        }
                        return false;
                    }
                });
                p.show();
            }
        };

        // spinner 에 사용할 adapter setting
        setSpinnerAdapter();
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

    // Spinner 에 들어갈 데이터 세팅
    private void setSpinnerAdapter() {
        // 강아지 이름 spinner
        nameList = new ArrayList<>();
        // Shared Preferences 에서 강아지 프로필 조회
        // 강아지 이름을 array list 에 추가
        getPuppyNames(nameList);
        // 이름 데이터가 들어가 있는 array list 를
        // Spinner 에 사용하기 위해 Array Adapter 적용
        namesAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item, nameList);
        namesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // 식사 카테고리 Spinner
        // 식사 카테고리에 Spinner 에 사용할 array list
        mealTypeList = new ArrayList<>();
        mealTypeList.add("식사 카테고리");
        mealTypeList.add("식사");
        mealTypeList.add("간식");
        mealTypeList.add("약");
        // Spinner 에 사용하기 위해 Array Adapter 적용
        mealTypeAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item, mealTypeList);
        mealTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // 식사량 단위 Spinner
        // 식사량 측정 단위 Spinner 에 사용할 array list 생성
        weightList = new ArrayList<>();
        weightList.add("g");
        weightList.add("개");
        weightList.add("ml");
        weightAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item, weightList);
        weightAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
    }

    // 기록 삭제 메소드
    private void deleteMealLog(int position) {

        // 현재 화면 상으로 보이는 array list 에 들어있는 데이터 삭제
        // (표면상으로 삭제)
        mArrayList.remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, mArrayList.size());

        // DB 삭제
        // shared 에서 data base table 불러오기
        SharedPreferences prefs = getSharedPreferences("MEAL_FILE", MODE_PRIVATE);
        String noData = "";

        // 모든 데이터가 들어있는 String
        String allData = prefs.getString("mealLog", noData);
        Log.d("쉐어드 DB: ", allData);

        // 삭제할 부분 탐색
        // db에 저장되어있는 string 을 array 로 옮긴다.
        String[] dataArray;
        dataArray = allData.split(",");
        // 결과:  날씨, 강아지 이름, 식사 카테고리, 음식명, 측정단위, 메모

        // 사용자에게 보여지는 view 에서는 ( array[5], array[4], array[3].. )
        // 역순으로 나열되어 있기 때문에
        // 새로운 array 를 만들어 역순으로 data 를 담는다.
        // 결과:  메모, 측정단위, 음식명, 카테고리, 강아지 이름, 날씨
        ArrayList<String> newList = new ArrayList<String>();
        for(int i=dataArray.length-1; i>=0; i--) {
            newList.add(dataArray[i]);
        }

        Log.d("newList before ", newList.toString());

        // 사용자가 선택한 게시글의 번호 * 7 (게시글 구성하는 변수의 개수) ===> 수정 시작 index.
        // 한 세트에 size + 7 만큼 단어가 들어감
        int numberOfElement = 7;
        int startPoint = position * numberOfElement;

        // 선택된 부분만 데이터 삭제
        for (int i=startPoint; i<7; i++) {
            newList.remove(startPoint);
//            Log.d("삭제 예정: ", newList.get(i));
            Log.d("newList 삭제 번호: ", String.valueOf(i));
//            Log.d("newList 삭제 내용: ", newList.get(startPoint));
        }
        Log.d("newList after ", newList.toString());

        // 삭제 완료.
        // array list ---> 역순으로 반복문을 돌려 String 으로 return 한다.
        // 결과:  날씨, 강아지 이름, 식사 카테고리, 음식명, 측정단위, 메모
        String lastData = "";
        for(int i=newList.size()-1; i>=0; i--) {
            newList.add(dataArray[i]);
            lastData += newList.get(i) + ",";
            Log.d("newList22 ", newList.get(i));
        }
        Log.d("newList lastData ", lastData);

        // SharedPreference 에 변환된 문자열을 저장.
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mealLog", lastData);
        editor.apply();
    }


    // 수정버튼을 눌렀을 대
    private void getDialogForEdit(final int position) {
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
        // 선택된 게시글에 저장되어 있던 강아지 이름을 view 에 입힌다.
        String stored_name = mArrayList.get(position).getPetName();
        // nameList 에서 stored_name 과 같은 것이 있는지 찾은 후에 그것을 setSelection 한다.
        int nameIndex = 0;
        for(int i=0; i< nameList.size(); i++) {
            if (stored_name.equals(nameList.get(i))) {
                nameIndex = i;
            }
        }
        nameSpinner.setAdapter(namesAdapter);
        // 지정한 index 를 spinner 의 default 로 보이게 하는 명령어
        nameSpinner.setSelection(nameIndex);
        namesAdapter.notifyDataSetChanged();
        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String guideMessage = "강아지를 선택해주세요";
                if(!nameList.get(i).contains(guideMessage)) {
                    // 저장할 강아지 이름 세팅
                    petName = nameList.get(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Name List ", "비어 있음");
            }
        });

        // 식사 카테고리
        // Spinner 에 사용하기 위해 Array Adapter 적용
        // 식사의 카테고리(식사, 간식, 약) 정하는 spinner
        final Spinner mealTypeSpinner = (Spinner) view.findViewById(R.id.meal_category);
        // 선택된 게시글에 저장되어 있던 카테고리를 view 에 입힌다.
        String stored_category = mArrayList.get(position).getMealType();
        // nameList 에서 stored_name 과 같은 것이 있는지 찾은 후에 그것을 setSelection 한다.
        int categoryIndex = 0;
        for(int i=0; i< mealTypeList.size(); i++) {
            if (stored_category.equals(mealTypeList.get(i))) {
                categoryIndex = i;
            }
        }
        mealTypeSpinner.setAdapter(mealTypeAdapter);
        mealTypeSpinner.setSelection(categoryIndex);
        mealTypeAdapter.notifyDataSetChanged();
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

        // 식사량 측정 단위
        // 먹은 음식의 수량 or 무게를 저장할 단위를 선정하는 spinner
        final Spinner weightSpinner = (Spinner) view.findViewById(R.id.spinner_weight_type);
        // 선택된 게시글에 저장되어 있던 강아지 이름을 view 에 입힌다.
        String storedUnit = mArrayList.get(position).getMeasureType();
        // nameList 에서 stored_name 과 같은 것이 있는지 찾은 후에 그것을 setSelection 한다.
        int unitIndex = 0;
        for(int i=0; i< weightList.size(); i++) {
            if (storedUnit.equals(weightList.get(i))) {
                unitIndex = i;
            }
        }
        weightSpinner.setAdapter(weightAdapter);
        weightSpinner.setSelection(unitIndex);
        weightAdapter.notifyDataSetChanged();
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
        final Button buttonSubmit = (Button) view.findViewById(R.id.register);
        final EditText editName = (EditText) view.findViewById(R.id.food_name);
        final EditText editWeight = (EditText) view.findViewById(R.id.food_weight);
        final EditText editMemo = (EditText) view.findViewById(R.id.memo);
        // 게시글에 저장되어 있던 데이터를 view에 입힌다.
        editName.setText(mArrayList.get(position).getFood_name());
        editWeight.setText(mArrayList.get(position).getFood_weight());
        editMemo.setText(mArrayList.get(position).getMemo());
        buttonSubmit.setText("작성 완료");
        // 작성 완료 버튼 눌렸을 때
        final AlertDialog dialog = builder.create();
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 사용자가 입력한 값을 가져옴
                String date = datePicker.getText().toString();
                String name = petName;
                String category = mealType;
                String foodName = editName.getText().toString();
                String weight = editWeight.getText().toString();
                String measureUnit = measureType;
                String memo = editMemo.getText().toString();
                // SharedPreference 을 생성하여 작성한 문자열을 저장.
                SharedPreferences prefs = getSharedPreferences("MEAL_FILE", MODE_PRIVATE);
                String noData = "";
                // 모든 데이터가 들어있는 String
                String allData = prefs.getString("mealLog", noData);
                Log.d("쉐어드 DB: ", allData);

                // db에 저장되어있는 string 을 array 로 옮긴다.
                String[] dataArray;
                dataArray = allData.split(",");
                // 사용자에게 보여지는 view 에서는 ( array[5], array[4], array[3].. )
                // 역순으로 나열되어 있기 때문에
                // 새로운 array 를 만들어 역순으로 data 를 담는다.
                ArrayList<String> newList = new ArrayList<String>();
                //
                for(int i=dataArray.length-1; i>=0; i--) {
                    newList.add(dataArray[i]);
                    Log.d("newList ", dataArray[i]);
                }

                // 사용자가 선택한 게시글의 번호 * 7 (게시글 구성하는 변수의 개수) ===> 수정 시작 index.
                // 한 세트에 size + 7 만큼 단어가 들어감
                int numberOfElement = 7;
                int startPoint = position * numberOfElement;

                // 사용자가 입력한 부분을 업데이트 한다
                newList.set(startPoint, memo);
                newList.set(startPoint + 1, measureUnit);
                newList.set(startPoint + 2, weight);
                newList.set(startPoint + 3, foodName);
                newList.set(startPoint + 4, category);
                newList.set(startPoint + 5, name);
                newList.set(startPoint + 6, date);

                // 수정된 array list 를 다시 하나의 string 으로 변환
                String lastData = "";
                for(int i=dataArray.length-1; i>=0; i--) {
                    newList.add(dataArray[i]);
                    lastData += newList.get(i) + ",";
                    Log.d("newList22 ", newList.get(i));
                }
                Log.d("lastData ", lastData);

                // SharedPreference 에 변환된 문자열을 저장.
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("mealLog", lastData);
                editor.apply();

                // 저장완료. 바뀐 정보만 View 에 다시 표시
                MealMemoItem mealData = new MealMemoItem(date, name, category, foodName, weight, measureUnit, memo);
                mArrayList.set(position, mealData);
                mAdapter.notifyItemChanged(position);

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void checkProfiles() {
        // 강아지의 이름들을 담을 array list 생성
        nameList = new ArrayList<>();
        // Shared Preferences 에서 강아지 프로필 조회
        // 강아지 이름을 array list 에 추가
        getPuppyNames(nameList);
        // 이름 데이터가 들어가 있는 array list 를
        // Spinner 에 사용하기 위해 Array Adapter 적용
        namesAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item, nameList);
//                android.R.layout.simple_spinner_dropdown_item, nameList);
        namesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
    }

    private void getPuppyNames(ArrayList<String> nameList) {
        nameList.add("이름 선택");
        SharedPreferences prefs = getSharedPreferences("DOG_FILE", MODE_PRIVATE);
        // 쉐어드에 저장한 문자열을 불러온다.
        String json = prefs.getString("dogInfo", null);
        //만약 shared preference 에 저장된 값이 null 이 아니라면,
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                // Json Array 에 저장되어 있던 데이터를 읽어온다.
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
                String guideMessage = "이름 선택";
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

        // 식사 카테고리
        // Spinner 에 사용하기 위해 Array Adapter 적용
        // 식사의 카테고리(식사, 간식, 약) 정하는 spinner
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

        // 식사량 측정 단위
        // Spinner 에 사용하기 위해 Array Adapter 적용
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
                foodWeight = editWeight.getText().toString();
                // spinner 에 적용된 측정 단위
                String measure = measureType;
                // 메모 사항
                memo = editMemo.getText().toString();

                // 가져온 내용을 ArrayList에 추가
                MealMemoItem mealData = new MealMemoItem(mealDate ,petName, mealType, foodName, foodWeight, measureType, memo);
                mArrayList.add(0, mealData);
                mAdapter.notifyItemInserted(0);
                mAdapter.notifyDataSetChanged();
                tv_no_data.setVisibility(View.GONE);

                StringBuilder stringBuilder = new StringBuilder();
                // StringBuilder 에 사용자가 입력한 여러 문자열들을 한 문자열 변수에 모두 모은다.
                stringBuilder.append(date).append(",");
                stringBuilder.append(petName).append(",");
                stringBuilder.append(mealType).append(",");
                stringBuilder.append(foodName).append(",");
                stringBuilder.append(foodWeight).append(",");
                stringBuilder.append(measure).append(",");
                stringBuilder.append(memo).append(",");
                strDataSet = strDataSet + stringBuilder.toString();
                Log.d("stringBuilder 작동", strDataSet);
                // split 함수 사용
                array = strDataSet.split(",");
                // 합쳐진 문자열을 String array 에 나열
//                for(String box : array) {
//                    System.out.println(box);
//                }
                // SharedPreference 을 생성하여 작성한 문자열을 저장.
                SharedPreferences prefs = getSharedPreferences("MEAL_FILE", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("mealLog", strDataSet);
                editor.apply();

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
        mArrayList = new ArrayList<>();
        if (strDataSet != null) {
            tv_no_data.setVisibility(View.GONE);
            array = strDataSet.split(",");
            try {
                // [i] / [i]+1 / [i]+2 / 방식으로 문자열마다 하나의 자리를 생성한다.
                for(int i=0; i< array.length; i+=7) {
                    mealDate = array[i];
                    petName = array[i+1];
                    mealType = array[i+2];
                    foodName = array[i+3];
                    foodWeight = array[i+4];
                    measureType = array[i+5];
                    memo = array [i+6];

                    //구분된 문자열을 아이템에 추가한다.
                    MealMemoItem mealData = new MealMemoItem(mealDate ,petName, mealType, foodName, foodWeight, measureType, memo);
                    mArrayList.add(0, mealData);
                }
                mAdapter = new MealListAdapter(getApplicationContext(), mArrayList, listener);
                mAdapter.notifyItemInserted(0);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
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
