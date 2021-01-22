package com.example.lets_walk_butler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MealLogAdapter extends BaseAdapter {

    private ArrayList<ListViewMealLog> meal_log_item_list = new ArrayList<ListViewMealLog>();

    public MealLogAdapter () {

    }

    @Override
    public int getCount() {
        return meal_log_item_list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // 리스트뷰 레이아웃을 inflate하여 convertView로 연결.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_meal_log, parent, false);
        }

        // 화면에 표시될 view 로부터 위젯에 대한 참조 획득.
        ImageView icon_image_view = (ImageView) convertView.findViewById(R.id.imageview_meal_log);
        TextView textview_date = (TextView) convertView.findViewById(R.id.text_meal_log_show_date);
        TextView textview_food_name = (TextView) convertView.findViewById(R.id.text_meal_log_food_name);
        TextView textview_food_weight = (TextView) convertView.findViewById(R.id.text_meal_log_food_weight);

        //Data Set 연결
        ListViewMealLog list_view_item = meal_log_item_list.get(position);

        // 아이템 내 위젯에 데이터 전송
        icon_image_view.setImageDrawable(list_view_item.getIcon());
        textview_date.setText(list_view_item.getDate());
        textview_food_name.setText(list_view_item.getFoodName());
        textview_food_weight.setText(list_view_item.getFoodWeight());

        return convertView;
    }

    public void add_item(Drawable icon, String date, String food_name, String food_weight) {
        ListViewMealLog item = new ListViewMealLog();

        item.setIcon(icon);
        item.setDate(date);
        item.setFoodName(food_name);
        item.setFoodWeight(food_weight);

        meal_log_item_list.add(item);
    }
}
