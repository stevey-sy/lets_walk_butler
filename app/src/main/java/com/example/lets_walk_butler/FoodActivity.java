package com.example.lets_walk_butler;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_walk_butler.adapter.MealListAdapter;

import java.util.ArrayList;

public class FoodActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<MealMemoItem> mArrayList;
    private MealListAdapter mAdapter = null;

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
//        context = this;
        // View를 세팅한 메서드 사용.

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(FoodActivity.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();

        mAdapter = new MealListAdapter(this, mArrayList);
        mRecyclerView.setAdapter(mAdapter);

//        MealMemoItem data = new MealMemoItem("로얄사료", "120", "잘 먹는다.");
//        mArrayList.add(data);
        // SharedPreference 에 저장되어 있는 데이터를 로딩
        getSharedPreferencesData();

        // 추가 버튼 눌렀을 때
        Button buttonInsert = (Button)findViewById(R.id.btn_meal_write);
        buttonInsert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 다이어로그를 생성하여 사용자가 아이템 추가 입력 가능
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodActivity.this);
                View view = LayoutInflater.from(FoodActivity.this).inflate(R.layout.dialog_edit_meal, null, false);
                builder.setView(view);

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

    // 사용자가 작성을 마치면 키보드창이 사라지는 메서드
//    private void hideKeyboard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager inputMethodManager =
//                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            assert inputMethodManager != null;
//            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }

//    public void saveSharedData () {
//        SharedPreferences prefs = getSharedPreferences("MEAL_FILE", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//
//        String json = prefs.getString("mealInfo", null);
//
//        try {
//            if (json == null) {
//                JSONArray jsonArray = new JSONArray();
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("FoodName", foodName);
//                jsonObject.put("FoodWeight", foodWeight );
//                jsonObject.put("Memo", memo);
//                jsonArray.put(jsonObject);
//                editor.putString("mealInfo", jsonArray.toString());
//            } else {
//                JSONArray jsonArray= new JSONArray(json);
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("FoodName", foodName);
//                jsonObject.put("FoodWeight", foodWeight );
//                jsonObject.put("Memo", memo);
//                jsonArray.put(jsonObject);
//                editor.putString("mealInfo", jsonArray.toString());
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        editor.apply();
//    }

}
