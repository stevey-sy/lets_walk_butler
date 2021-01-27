package com.example.lets_walk_butler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_walk_butler.MealMemoItem;
import com.example.lets_walk_butler.R;

import java.util.ArrayList;

//import androidx.appcompat.app.AlertDialog;

public class MealListAdapter extends RecyclerView.Adapter<MealListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MealMemoItem> mList;
    private MealLogClickListener mListener;

    // 커스텀 어댑터 생성자
    public MealListAdapter(Context context, int resource, ArrayList<MealMemoItem> itemList) {
        mContext = context;
        mList = itemList;
    }

    public MealListAdapter(ArrayList<MealMemoItem> list) {
        this.mList = list;
    }

    public MealListAdapter(Context context, ArrayList<MealMemoItem> list, MealLogClickListener listener) {
        this.mList = list;
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    // 뷰홀더를 만든다. 매개변수로
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_meal_item, parent, false);

        return new ViewHolder(v, mListener);
    }

    // 각 아이템의 위치와 데이터를 보관할 holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.tvMealType.setText(mList.get(position).getMealType());
        // 식사 종류 마다 다른 아이콘 출력
        String mealType = mList.get(position).getMealType();
        if(mealType.equals("식사")) {
            viewHolder.mealImage.setImageResource(R.drawable.ic_dog_food_white);
        } else if (mealType.equals("간식")) {
            viewHolder.mealImage.setImageResource(R.drawable.ic_bone_white);
        } else if (mealType.equals("약")) {
            viewHolder.mealImage.setImageResource(R.drawable.ic_pill_white);
        }

        viewHolder.tvMealDate.setText(mList.get(position).getMealDate());
        viewHolder.tvPetName.setText(mList.get(position).getPetName());
        viewHolder.memoText.setText(mList.get(position).getMemo());
        viewHolder.food_name_text.setText(mList.get(position).getFood_name());
        viewHolder.weight_text.setText(mList.get(position).getFood_weight());
        viewHolder.tvMeasureUnit.setText(mList.get(position).getMeasureType());
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public interface MealLogClickListener {
        void onRowClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //TextView categoryText;
        TextView memoText;
        TextView tvMealDate;
        TextView tvPetName;
        TextView tvMealType;
        TextView tvMeasureUnit;
        ImageView mealImage;
        LinearLayout mRowContainer;
        MealLogClickListener mListener;
        TextView food_name_text;
        TextView weight_text;

        public ViewHolder(View itemView, MealLogClickListener listener) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.meal_image);
            tvMealType = itemView.findViewById(R.id.category);
            tvMealDate = itemView.findViewById(R.id.show_regdate);
            memoText = itemView.findViewById(R.id.show_memo);
            tvPetName = itemView.findViewById(R.id.tv_pet_name);
            food_name_text = itemView.findViewById(R.id.input_food_name);
            weight_text = itemView.findViewById(R.id.input_food_weight);
            tvMeasureUnit = itemView.findViewById(R.id.tv_measure_unit);
            mRowContainer = itemView.findViewById(R.id.meal_log_item_view);
            mListener = listener;
            mRowContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.meal_log_item_view:
                    mListener.onRowClick(mRowContainer, getAdapterPosition());
                    break;
            }
        }
    }
}
