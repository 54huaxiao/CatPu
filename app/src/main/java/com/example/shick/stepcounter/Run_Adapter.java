package com.example.shick.stepcounter;

/**
 * Created by shick on 2016/12/1.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shick on 2016/11/27.
 */
public class Run_Adapter extends ArrayAdapter<Run> {
    private int resourceId;

    public Run_Adapter(Context context, int textViewResourceId, List<Run> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Run runlist = getItem(position);
        View view;
        if (convertView == null) {
            view= LayoutInflater.from(getContext()).inflate(resourceId, null);
        } else {
            view = convertView;
        }
        TextView date = (TextView)view.findViewById(R.id.date);
        date.setText(runlist.getDate());
        TextView time = (TextView)view.findViewById(R.id.time);
        time.setText(runlist.getTime());
        TextView dis = (TextView)view.findViewById(R.id.distance);
        dis.setText(runlist.getDistance());
        return view;
    }
}