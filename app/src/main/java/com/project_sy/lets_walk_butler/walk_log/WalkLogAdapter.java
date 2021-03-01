package com.project_sy.lets_walk_butler.walk_log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.project_sy.lets_walk_butler.R;

import java.util.ArrayList;

public class WalkLogAdapter extends BaseAdapter {

    public Context mContext = null;
    private ArrayList<WalkDTO> mList = null;
    private LayoutInflater inflater = null;
    private int layout = 0;

    public WalkLogAdapter() {
    }

    public WalkLogAdapter(@NonNull Context context, int layout, ArrayList<WalkDTO> list) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mList = list;
        this.layout = layout;
    }

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
    public View getView(int position, View convertView, ViewGroup parent) {
        // 리스트의 아이템 뷰의 위치를 잡아줄 변수
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(layout, parent, false);
        }
        // 아이템 레이아웃과 어댑터 연결
        TextView tvWalkDate = (TextView) convertView.findViewById(R.id.text_walk_date);
        TextView tvWalkTime = (TextView) convertView.findViewById(R.id.text_walk_time);
        TextView tvStepNumber = (TextView) convertView.findViewById(R.id.text_walk_distance);
        TextView tvMemo = (TextView) convertView.findViewById(R.id.text_walk_memo);
        ImageView ivWalkPhoto = (ImageView) convertView.findViewById(R.id.image_walk_diary);
        TextView tvWalkDistance = (TextView) convertView.findViewById(R.id.text_walk_meter);
        // SettingItem을 통해 사용자가 클릭한 아이템의 위치를 알 수 있다.
        WalkDTO walkItem = mList.get(position);
        // 연결된 아이템 데이터 요소의 위치를 잡아준다.
        tvWalkDate.setText(walkItem.getDate());
        tvWalkTime.setText(walkItem.getWalkTime());
        tvStepNumber.setText(walkItem.getWalkStep());
        tvMemo.setText(walkItem.getMemo());
        ivWalkPhoto.setImageURI(walkItem.getWalkImageUri());
        tvWalkDistance.setText(walkItem.getWalkMeter());

        return convertView;
    }
}
