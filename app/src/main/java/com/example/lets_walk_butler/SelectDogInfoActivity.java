package com.example.lets_walk_butler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lets_walk_butler.adapter.MealListAdapter;
import com.example.lets_walk_butler.setting_log.SettingAdapter;
import com.example.lets_walk_butler.setting_log.SettingDTO;

import java.util.ArrayList;

public class SelectDogInfoActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    RecyclerView recycler_view;
    LinearLayoutManager layoutManager;
    SettingAdapter setting_adapter;

    Spinner dog_type_spinner;
    EditText name_edit;
    EditText age_edit;
    EditText dog_weight_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dog_info);

        context = this;
        setView();

    }
    private void setView() {
        dog_type_spinner = findViewById(R.id.dog_type_array);
        name_edit = findViewById(R.id.edit_name);
        age_edit = findViewById(R.id.edit_dog_age);
        dog_weight_edit = findViewById(R.id.edit_dog_weight);

        Button registerButton = findViewById(R.id.btn_setting_complete);
        registerButton.setOnClickListener(this);

        recycler_view = findViewById(R.id.recyclerview_main_list);

//        setRecyclerView();
//        setSettingDTO();
    }

//    private void setRecyclerView() {
//        layoutManager = new LinearLayoutManager(context,
//                LinearLayoutManager.VERTICAL, false);
//        recycler_view.setLayoutManager(layoutManager);
//
//        setting_adapter = new SettingAdapter(context,
//                R.layout.item_register_dog, new ArrayList<SettingDTO>());
//        recycler_view.setAdapter(setting_adapter);
//    }

//    private void setSettingDTO() {
//        ArrayList<SettingDTO> list = getMemoDummyList();
//        setting_adapter.addItemList(list);
//    }
//
//    private ArrayList<SettingDTO> getMemoDummyList() {
//        ArrayList<SettingDTO> list = new ArrayList<>();
//
//        SettingDTO item_example = new SettingDTO("코기",
//                "4살", "2.5", "웰시코기");
//        SettingDTO item_example2 = new SettingDTO("랑지",
//                "2살",  "2", "2");
//
//        list.add(item_example);
//        list.add(item_example2);
//
//        return list;
//    }

    @Override
    public void onClick(View v) {

        registerDogInfo();

    }

    private void registerDogInfo() {
        String dog_name = name_edit.getText().toString();
        String dog_age = age_edit.getText().toString();
        String dog_weight = dog_weight_edit.getText().toString();
        String dog_type = (String) dog_type_spinner.getSelectedItem();

        if (TextUtils.isEmpty(dog_name)) {
            Toast.makeText(context, R.string.msg_memo_input, Toast.LENGTH_SHORT).show();
            return;
        }

        addMemoItem(dog_name, dog_age, dog_weight, dog_type);

        dog_type_spinner.setSelection(0);
        name_edit.setText("");

        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addMemoItem(String name, String age, String weight, String dog_type) {
        SettingDTO item = new SettingDTO(name, age, weight, dog_type);

//        setting_adapter.addItem(item);
    }
}
