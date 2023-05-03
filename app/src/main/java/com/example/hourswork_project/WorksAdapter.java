package com.example.hourswork_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
            holder.tvDayOfWeek = convertView.findViewById(R.id.tvDayOfWeek);
            holder.tvSalaryDay = convertView.findViewById(R.id.tvSalaryDay);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Work work = workList.get(position);





        String startDate = dateFormat.format(work.getStartDate());
        String endDate = dateFormat.format(work.getEndDate());
        String dayOfWeek = hebrewDay(work.getStartDate());
        String salaryDay ;


        holder.startDateTextView.setText(startDate);
        holder.endDateTextView.setText(endDate);
        holder.tvDayOfWeek.setText(dayOfWeek);

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
        TextView tvDayOfWeek;
        TextView tvSalaryDay;
    }

    public String hebrewDay (long l){
        Locale locale = new Locale("he", "IL");

        Date date = new Date(l);
// create a Calendar instance and set the date to be checked
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

// get the day of the week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

// get the array of day of the week names in Hebrew
        DateFormatSymbols dfs = new DateFormatSymbols(locale);
        String[] weekdays = dfs.getWeekdays();

// get the day of the week name in Hebrew
        String dayOfWeekName = weekdays[dayOfWeek];
        return dayOfWeekName;
    }



    public void setWorksList(List<Work> worksList) {
        // Sort the list by start date in descending order
        Collections.sort(worksList, new Comparator<Work>() {
            @Override
            public int compare(Work w1, Work w2) {
                return Long.compare(w2.getStartDate(), w1.getStartDate());
            }
        });

        // Set the sorted list as the new data set and notify the adapter
        workList = worksList;
        notifyDataSetChanged();
    }





}
