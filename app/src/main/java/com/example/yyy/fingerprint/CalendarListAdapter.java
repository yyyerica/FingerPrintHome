package com.example.yyy.fingerprint;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by YYY on 2017/2/23.
 */

public class CalendarListAdapter extends ArrayAdapter<String[]> {

    TextView timeText,nameText, guid;

    private int resourceId;
    public CalendarListAdapter(Context context, int resource, List<String[]> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        String[] map = getItem(position);
        //list里有三个string分别存放name(0）time(1) date(2)

        // 获取数据
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            timeText = (TextView)view.findViewById(R.id.timeText);
            nameText = (TextView)view.findViewById(R.id.nameText);
            //guid = (TextView)view.findViewById(R.id.guidText);
        } else {
            view = convertView;
        }

        if(map!=null) {
                timeText.setText(map[0]);
                nameText.setText(map[1]);
                //guid.setText(map[2]);
        }

        return view;

    }
}
