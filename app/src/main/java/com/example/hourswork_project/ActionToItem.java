package com.example.hourswork_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActionToItem extends AppCompatActivity {

    Button btnItemDelete;
    TextView tvItemStart , tvItemStop , tvItemDate , tvItemDurationWorking , tvItemMoreHours125 , tvItemMoreHours150 , tvItemSalary;
    WorksDataBase worksDataBase;
    Work work;
    int id ;
    long duration;
    SharedPreferences sharedPreferences;
    SimpleDateFormat hoursAndMin = new SimpleDateFormat("HH:mm");
    SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_to_item);
        setTitle("hello");

        tvItemStart = findViewById(R.id.tvItemStart);
        tvItemStop = findViewById(R.id.tvItemStop);
        tvItemDate = findViewById(R.id.tvItemDate);
        tvItemDurationWorking = findViewById(R.id.tvItemDurationWorking);
        tvItemMoreHours125 = findViewById(R.id.tvItemMoreHours125);
        tvItemMoreHours150 = findViewById(R.id.tvItemMoreHours150);
        tvItemSalary = findViewById(R.id.tvItemSalary);

        btnItemDelete = findViewById(R.id.btnItemDelete);

        Intent intent=getIntent();

        id = intent.getIntExtra("id",0);

        Log.d("david", "id: "+ id);
        worksDataBase = new WorksDataBase(this);

        work = worksDataBase.getWorkById(id);

        Log.d("david", "onCreate: "+ work.toString());
        Date start = Calendar.getInstance().getTime();
        start.setTime(work.getStartDate());
        Date stop = Calendar.getInstance().getTime();
        stop.setTime(work.getEndDate());
        tvItemStart.setText("כניסה: " + hoursAndMin.format(start));
        tvItemStop.setText("יציאה: " + hoursAndMin.format(stop));

        if (date.format(start).equals(date.format(stop))){
            tvItemDate.setText(  "תאריך: " + date.format(start));
        }
        else{
            tvItemDate.setText(  "תאריך: " + date.format(start) + " - " + date.format(stop));
        }

        duration = getDurationMillis(start ,stop );
        tvItemDurationWorking.setText( "משך העבודה: " + formatDuration(duration));

        sharedPreferences = getSharedPreferences("Definitions", 0);
        int numOfDaysWeek = sharedPreferences.getInt("NumOfDaysWorking" , 0) ;
        Log.d("mmm", "m: "+ numOfDaysWeek);
        tvItemMoreHours125.setText( "שעות נוספות 125%: " + formatDuration(calculateTime125p(duration , numOfDaysWeek)));
        tvItemMoreHours150.setText( "שעות נוספות 150%: " + formatDuration(calculateTime150p(duration , numOfDaysWeek)));

        double numberHourlyWage = sharedPreferences.getInt("numberHourlyWage" , 0);
        double salaryDaily = salaryDay(numberHourlyWage , duration , numOfDaysWeek);
        tvItemSalary.setText("הסכום שהרווחת סך הכל: " + "\n" + salaryDaily + " שקלים חדשים ");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("salaryDaily" , (float) salaryDaily);
        editor.commit();

        btnItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                worksDataBase.deleteWork(id);
                HoursReport hoursReport = new HoursReport();
                getSupportFragmentManager().beginTransaction().replace(R.id.container , hoursReport).commit();

            }
        });

    }

    public static long getDurationMillis(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }

    public static String formatDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String result = "";
        if (days > 0) {
            result += days + " ימים ";
        }
        if (hours%24 > 0) {
            result += hours % 24 + " שעות ";
        }
        if (minutes%60 > 0) {
            result += minutes % 60 + " דקות ";
        }
        if (result.equals("")) {
            result = "0 דקות";
        }

        return result;
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
        long twoHours = 2 * 60 * 60 * 1000;

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
}