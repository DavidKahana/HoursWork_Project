package com.example.hourswork_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WorksAdapter extends BaseAdapter {

    private List<Work> workList;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;

    public WorksAdapter(Context context, List<Work> workList) {
        this.workList = workList;
        this.inflater = LayoutInflater.from(context);
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return workList.size();
    }

    @Override
    public Object getItem(int position) {
        return workList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return workList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.work_item, parent, false);
            holder = new ViewHolder();
            holder.startDateTextView = convertView.findViewById(R.id.text_view_start_date);
            holder.endDateTextView = convertView.findViewById(R.id.text_view_end_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Work work = workList.get(position);

        String startDate = dateFormat.format(work.getStartDate());
        String endDate = dateFormat.format(work.getEndDate());



        holder.startDateTextView.setText(startDate);
        holder.endDateTextView.setText(endDate);

        return convertView;
    }

    public int getworkID(int pos){
        Work w =  workList.get(pos);
        Log.d("david", "wrokid: "+ w.getId());

        return w.getId();
    }

    static class ViewHolder {
        TextView startDateTextView;
        TextView endDateTextView;
    }
}
