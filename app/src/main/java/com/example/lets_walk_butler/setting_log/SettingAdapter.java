package com.example.lets_walk_butler.setting_log;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lets_walk_butler.MealMemoItem;
import com.example.lets_walk_butler.R;
import com.example.lets_walk_butler.adapter.MealListAdapter;

import java.util.ArrayList;
import java.util.Dictionary;


public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.CustomViewHolder> {

    private ArrayList<SettingDTO> setting_list;
    private Context context;

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.dog_name.setText(setting_list.get(position).getDogName());
        holder.dog_age.setText(setting_list.get(position).getDogAge());
        holder.dog_weight.setText(setting_list.get(position).getDogWeight());
//        holder.dog_type.setAdapter(setting_list.get(position).getDogType());

    }

    @Override
    public int getItemCount() {
        return (null != setting_list ? setting_list.size() : 0);
    }

    // 1. 컨텍스트 메뉴를 사용하라면 RecyclerView.ViewHolder를 상속받은 클래스에서
    // OnCreateContextMenuListener 리스너를 구현

    public class CustomViewHolder extends RecyclerView.ViewHolder

            implements View.OnCreateContextMenuListener {

        TextView dog_name;
        TextView dog_age;
        TextView dog_weight;
        Spinner dog_type;

        public CustomViewHolder(View view) {
            super(view);

            this.dog_name = (TextView) view.findViewById(R.id.show_name);
            this.dog_age = (TextView) view.findViewById(R.id.show_age);
            this.dog_weight = (TextView) view.findViewById(R.id.show_dog_weight);
            this.dog_type = view.findViewById(R.id.show_dog_type);

            // OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정
            view.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 4. 컨텍스트 메뉴에서 항목 클릭시 동작을 설정합니다.
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:  // 5. 편집 항목을 선택시
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        // 다이얼로그를 보여주기 위해 edit_box.xml 파일을 사용합니다.

                        View view = LayoutInflater.from(context)
                                .inflate(R.layout.item_register_dog, null, false);
                        builder.setView(view);
                        final Button btn_submit = (Button) view.findViewById(R.id.btn_setting_complete);
                        final EditText edit_text_name = (EditText) view.findViewById(R.id.edit_name);
                        final EditText edit_text_age = (EditText) view.findViewById(R.id.edit_dog_age);
                        final EditText edit_text_weight = (EditText) view.findViewById(R.id.edit_dog_weight);
                        final Spinner edit_text_type = (Spinner) view.findViewById(R.id.dog_type_array);


                        // 6. 해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.
                        edit_text_name.setText(setting_list.get(getAdapterPosition()).getDogName());
                        edit_text_age.setText(setting_list.get(getAdapterPosition()).getDogAge());
                        edit_text_weight.setText(setting_list.get(getAdapterPosition()).getDogWeight());
//                        edit_text_type.setAdapter(setting_list.get(getAdapterPosition()).getDogType());

                        final AlertDialog dialog = builder.create();
                        btn_submit.setOnClickListener(new View.OnClickListener() {


                            // 7. 수정 버튼을 클릭하면 현재 UI에 입력되어 있는 내용으로

                            public void onClick(View v) {
                                String str_name = edit_text_name.getText().toString();
                                String str_age = edit_text_age.getText().toString();
                                String str_weight = edit_text_weight.getText().toString();
                                String str_type = (String) edit_text_type.getSelectedItem();

                                SettingDTO setting = new SettingDTO(str_name, str_age, str_weight, str_type);


                                // 8. ListArray에 있는 데이터를 변경하고
                                setting_list.set(getAdapterPosition(), setting);


                                // 9. 어댑터에서 RecyclerView에 반영하도록 합니다.

                                notifyItemChanged(getAdapterPosition());

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;

                    case 1002:

                        setting_list.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), setting_list.size());

                        break;

                }
                return true;
            }
        };
    }
}