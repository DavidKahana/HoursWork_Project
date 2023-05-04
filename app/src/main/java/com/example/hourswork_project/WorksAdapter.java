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
import java.text.DecimalFormat;
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
    private SharedPreferences sharedPreferences;
    private double salaryDaily;

    public WorksAdapter(Context context, List<Work> workList) {
        this.workList = workList;
        this.inflater = LayoutInflater.from(context);
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.sharedPreferences = context.getSharedPreferences("Definitions", 0);
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


        int numOfDaysWeek = sharedPreferences.getInt("NumOfDaysWorking" , 0) ;
        double numberHourlyWage;
        if (sharedPreferences.getBoolean("MinSalary" , false)){
            numberHourlyWage = sharedPreferences.getFloat("numberHourlyWageMinSalary" , 0);
        }
        else{
            numberHourlyWage = sharedPreferences.getFloat("numberHourlyWageFloat" , 0);
        }
        long duration = workList.get(position).startDate - workList.get(position).endDate;

        Boolean salaryOnBreaking = sharedPreferences.getBoolean("SalaryOnBreak" , false);
        int numOfBreaking = sharedPreferences.getInt("numberSelectTimeOfBreak" , 0);
        if (salaryOnBreaking == false) {
            if (duration > 6 * 60 * 60 * 1000) {
                duration = breaking(duration, numOfBreaking);

            }
        }

        Log.d("ww", "num: "+ numOfDaysWeek);
        Log.d("ww", "number: "+ numberHourlyWage);
        Log.d("ww", "du: "+ duration);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        salaryDaily = salaryDay(numberHourlyWage , duration , numOfDaysWeek);


        String startDate = dateFormat.format(work.getStartDate());
        String endDate = dateFormat.format(work.getEndDate());
        String dayOfWeek = hebrewDay(work.getStartDate());
        String salaryDay = decimalFormat.format(salaryDaily) + " ש''ח " ;


        holder.startDateTextView.setText(startDate);
        holder.endDateTextView.setText(endDate);
        holder.tvDayOfWeek.setText(dayOfWeek);
        holder.tvSalaryDay.setText(salaryDay);

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

    public static double salaryDay(double numberHourlyWage , long duration , int num){
        double salary = 0 ;

        long time150p = calculateTime150p(duration , num);

        long secondsTime150p = time150p / 1000;
        long minutesTime150p = secondsTime150p / 60;
        long hoursTime150p = minutesTime150p / 60;

        long time125p = calculateTime125p(duration , num);

        long secondsTime125p = time125p / 1000;
        long minutesTime125p = secondsTime125p / 60;
        long hoursTime125p = minutesTime125p / 60;

        long basic = duration - time150p - time125p;

        long secondsBasic = basic / 1000;
        long minutesBasic= secondsBasic/ 60;
        long hoursBasic = minutesBasic / 60;


        salary += ((numberHourlyWage * 1.5 * (minutesTime150p%60)) / 60.0 );
        salary += (numberHourlyWage * 1.5 * hoursTime150p );
        salary += ((numberHourlyWage * 1.25 * (minutesTime125p%60)) / 60.0 );
        salary += (numberHourlyWage * 1.25 * hoursTime125p);
        salary += ((numberHourlyWage * (minutesBasic%60)) / 60.0 );
        salary += (numberHourlyWage * hoursBasic);



        return salary;
    }
    public static long calculateTime125p(long time, int num) {
        long eightHoursThirtySixMins = 8 * 60 * 60 * 1000 + 36 * 60 * 1000;
        long tenHoursThirtySixMins = 10 * 60 * 60 * 1000 + 36 * 60 * 1000;
        long twoHours = 2 * 60 * 60 * 1000;

        if (num == 5) {
            if (time < eightHoursThirtySixMins) {
                return 0;
            } else if (time < tenHoursThirtySixMins) {
                return time - eightHoursThirtySixMins;
            } else {
                return twoHours;
            }
        } else if (num == 6) {
            if (time < 8 * 60 * 60 * 1000) {
                return 0;
            } else if (time < 10 * 60 * 60 * 1000) {
                return time - 8 * 60 * 60 * 1000;
            } else {
                return twoHours;
            }
        } else {
            // handle invalid num value
            return -1;
        }

    }

    public static long calculateTime150p(long time, int num) {
        long eightHoursThirtySixMins = 8 * 60 * 60 * 1000 + 36 * 60 * 1000;
        long tenHoursThirtySixMins = 10 * 60 * 60 * 1000 + 36 * 60 * 1000;

        if (num == 5) {
            if (time < tenHoursThirtySixMins) {
                return 0;
            }
            else {
                return time - eightHoursThirtySixMins;
            }
        }
        else if (num == 6) {
            if (time < 10 * 60 * 60 * 1000) {
                return 0;
            }
            else {
                return time - 10 * 60 * 60 * 1000 ;
            }
        } else {
            // handle invalid num value
            return -1;
        }

    }

    public static long breaking (long duration , int minutes){
        long sixHours = 6 * 60 * 60 * 1000;
        long sixHoursAndTimeBreak =6 * 60 * 60 * 1000 + minutes * 60 * 1000;

        if (duration <= sixHoursAndTimeBreak && duration > sixHours){
            return sixHours;
        }
        else{
            return duration - minutes * 60 * 1000;
        }
    }





}