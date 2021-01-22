package com.example.lets_walk_butler.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_walk_butler.FoodActivity;
import com.example.lets_walk_butler.MealMemoItem;
import com.example.lets_walk_butler.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MealListAdapter extends RecyclerView.Adapter<MealListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MealMemoItem> mList;

    // 커스텀 어댑터 생성자
    public MealListAdapter(Context context, int resource, ArrayList<MealMemoItem> itemList) {
        mContext = context;
        mList = itemList;
    }

    public MealListAdapter(ArrayList<MealMemoItem> list) {
        this.mList = list;
    }

    public MealListAdapter(Context context, ArrayList<MealMemoItem> list) {
        mList = list;
        mContext = context;
    }

    @NonNull
    @Override
    // 뷰홀더를 만든다. 매개변수로
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_meal_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }


    // 각 아이템의 위치와 데이터를 보관할 holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        //viewHolder.categoryText.setText(mList.get(position).getCategory());
        viewHolder.memoText.setText(mList.get(position).getMemo());
//        viewHolder.dateText.setText(mList.get(position).getRegDate());
        viewHolder.food_name_text.setText(mList.get(position).getFood_name());
        viewHolder.weight_text.setText(mList.get(position).getFood_weight());
        //viewHolder.type_category_text.setText(mList.get(position).getType_category());
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        //TextView categoryText;
        protected TextView memoText;
        protected TextView dateText;

        SharedPreferences prefs;
        String strFoodName;
        String strFoodWeight;
        String strFoodMemo;
        String strDataSet;
        StringBuilder stringBuilder = new StringBuilder();
        String[] array;


        protected TextView food_name_text;
        protected TextView weight_text;
        //TextView type_category_text;

        public ViewHolder(View itemView) {
            super(itemView);

            //categoryText = itemView.findViewById(R.id.category);
            this.memoText = itemView.findViewById(R.id.show_memo);
            //this.dateText = itemView.findViewById(R.id.show_regdate);

            this.food_name_text = itemView.findViewById(R.id.input_food_name);
            this.weight_text = itemView.findViewById(R.id.input_food_weight);
            //type_category_text = itemView.findViewById(R.id.input_weight_type);

            itemView.setOnCreateContextMenuListener(this);

        }
        // 사용자가 아이템을 길게 클릭했을 때에 메뉴 생성
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            // 수정버튼 추가 / 다이어로그 생성 / GETADAPTERPOSITION / 글작성 코드 추가
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                // 사용자가 수정 버튼 눌렀을 때의 작동
                switch (item.getItemId()) {
                    case 1001:
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_meal, null, false);
                        builder.setView(view);

                        final Button ButtonSubmit = (Button) view.findViewById(R.id.register);
                        // final EditText edit_meal_type = (EditText) view.findViewById(R.id.dialog_meal_type);
                        final EditText edit_name = (EditText) view.findViewById(R.id.food_name);
                        final EditText edit_weight = (EditText) view.findViewById(R.id.food_weight);
                        //final EditText edit_weight_type = (EditText) view.findViewById(R.id.dialog_weight_type);
                        final EditText edit_memo = (EditText) view.findViewById(R.id.memo);

                        //edit_meal_type.setText(m_list.get(getAdapterPosition()).getCategory());
                        edit_name.setText(mList.get(getAdapterPosition()).getFood_name());
                        edit_weight.setText(mList.get(getAdapterPosition()).getFood_weight());
                        //edit_weight_type.setText(m_list.get(getAdapterPosition()).getType_category());
                        edit_memo.setText(mList.get(getAdapterPosition()).getMemo());

                        final AlertDialog dialog = builder.create();

                        // Shared 에 저장된 데이터 불러오기
                        prefs = mContext.getSharedPreferences("MEAL_FILE", MODE_PRIVATE);
                        strDataSet = prefs.getString("mealLog", "");

                        // shared 저장되어있던 데이터를 문자열 리스트에 정렬
                        array = strDataSet.split(",");
                        for (int i=0; i<=array.length; i++) {
                            Log.d("데이터 문자열리스트화", "문자열" + array.length);
                        }
                        // 수정 완료 버튼을 눌렀을 때의 작동
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                //String meal_type = edit_meal_type.getText().toString();
                                String mealName = edit_name.getText().toString();
                                String mealWeight = edit_weight.getText().toString();
                                //String meal_weight_type = edit_weight_type.getText().toString();
                                String mealMemo = edit_memo.getText().toString();

                                MealMemoItem edit_list = new MealMemoItem(mealName, mealWeight, mealMemo);
                                mList.set(getAdapterPosition(), edit_list);
                                notifyItemChanged(getAdapterPosition());

                                // 수정하려는 부분에 문자열 배치 (문자열 자리를 하나씩 추가로 생성)
                                int position = getAdapterPosition();
                                String editFoodName = array[position*2];
                                String editWeight = array[(position*2) +1];
                                String editMemo = array[(position*2) +2];
                                // 전에 배치되었던 문자열 데이터를 하나의 변수로 합친다.
                                String oldText = editFoodName+","+editWeight+","+editMemo+",";
                                // 교체될 문자열을 하나로 합친다.
                                String newText = mealName+","+mealWeight+","+mealMemo+",";
                                // 문자열 데이터 교체
                                String editText = strDataSet.replace(oldText,newText);
                                // 쉐어드에 세팅한다.
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("mealLog", editText);
                                editor.apply();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        break;
                    // 사용자가 삭제버튼 눌렀을 때의 케이스
                    case 1002:

                        int position = getAdapterPosition();
                        mList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), mList.size());

                        // Shared 데이터 불러와서 삭제처리
                        prefs = mContext.getSharedPreferences("MEAL_FILE", MODE_PRIVATE);
                        strDataSet = prefs.getString("mealLog", "");

                        array = strDataSet.split(",");
                        // 불러온 문자열을 array에 나열한다.
                        for(int i=0; i<= array.length; i++) {
                            Log.d("데이터 문자열리스트화", "문자열" + array.length);
                        }
                        // 삭제될 위치의 문자열 자리를 하나씩 더 만든다.
                        //int position = getAdapterPosition();
                        String editFoodName = array[position*2];
                        String editWeight = array[(position*2) +1];
                        String editMemo = array[(position*2) +2];

                        String oldText = editFoodName+","+editWeight+","+editMemo+",";
                        String newText = strDataSet.replace(oldText, "");

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("mealLog", newText);
                        editor.apply();

                        break;

                }
               return true;
            }
        };
    }
}
