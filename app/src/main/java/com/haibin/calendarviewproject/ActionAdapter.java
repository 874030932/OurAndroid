package com.haibin.calendarviewproject;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionAdapter extends ArrayAdapter {
    private int resource;

    public ActionAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
        this.resource = resource;//resource为listView的每个子项的布局id
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) throws NullPointerException {
        Action act = (Action) getItem(position);//获得实例
        View view = LayoutInflater.from(getContext()).inflate(resource, null);
        TextView actionTitle = (TextView) view.findViewById(R.id.action_title);
        TextView actionContent = (TextView) view.findViewById(R.id.action_content);
        TextView actionTime = (TextView) view.findViewById(R.id.action_begin_time);
        actionTitle.setText(act.getTitle());
        actionContent.setText(act.getContent());
        actionTime.setText(act.getBeginTime());
        return view;
    }
}
