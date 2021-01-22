package com.example.lets_walk_butler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lets_walk_butler.R;
import com.example.lets_walk_butler.setting_info.SettingItem;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    public Context mContext = null;
    private ArrayList<SettingItem> mList = null;
    private LayoutInflater inflater = null;
    private int layout = 0;

    public CustomAdapter() {
    }
    // 어댑터 생성자, 받을 매개변수 설정
    public CustomAdapter(@NonNull Context context, int layout, ArrayList<SettingItem> list) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mList = list;
        this.layout = layout;
    }
    // 아이템의 개수파악
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 리스트의 아이템 뷰의 위치를 잡아줄 변수
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(layout, parent, false);
        }
        // 아이템 레이아웃과 어댑터 연결
        TextView dogNameText = (TextView) convertView.findViewById(R.id.dog_name);
        TextView dogAgeText = (TextView) convertView.findViewById(R.id.dog_age);
        TextView dogWeightText = (TextView) convertView.findViewById(R.id.dog_weight);
        TextView dogCategoryText = (TextView) convertView.findViewById(R.id.dog_type);
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.dog_image);
        // SettingItem을 통해 사용자가 클릭한 아이템의 위치를 알 수 있다.
        SettingItem settingItem = mList.get(position);
        // 연결된 아이템 데이터 요소의 위치를 잡아준다.
        dogNameText.setText(settingItem.getDogNameStr());
        dogAgeText.setText(settingItem.getDogAgeStr());
        dogWeightText.setText(settingItem.getDogWeight());
        dogCategoryText.setText(settingItem.getDogCategory());
        iconImageView.setImageURI(settingItem.getIconUri());

        return convertView;
    }
}
