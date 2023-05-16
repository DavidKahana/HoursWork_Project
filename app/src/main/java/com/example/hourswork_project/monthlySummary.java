package com.example.hourswork_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class monthlySummary extends AppCompatActivity {

    TextView tvMonthNameOfMonth , tvMonthNumOfDays_answer , tvMonthTotalHours_answer ,tvMonthTotalHours100p_answer , tvMonthTotalHours125p_answer , tvMonthTotalHours150p_answer , tvMonthTotalsalary_answer;
    SharedPreferences sharedPreferences;
    int numMonth;
    WorksDataBase worksDataBase;
    long duration , durationToatalMonth = 0 , durationToatal125pMonth = 0 , durationToatal150pMonth = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_summary);

        tvMonthNameOfMonth = findViewById(R.id.tvMonthNameOfMonth);
        tvMonthNumOfDays_answer = findViewById(R.id.tvMonthNumOfDays_answer);
        tvMonthTotalHours_answer = findViewById(R.id.tvMonthTotalHours_answer);
        tvMonthTotalHours100p_answer = findViewById(R.id.tvMonthTotalHours100p_answer);
        tvMonthTotalHours125p_answer = findViewById(R.id.tvMonthTotalHours125p_answer);
        tvMonthTotalHours150p_answer = findViewById(R.id.tvMonthTotalHours150p_answer);
        tvMonthTotalsalary_answer = findViewById(R.id.tvMonthTotalsalary_answer);



        sharedPreferences = getSharedPreferences("Definitions", 0);

        Intent intent=getIntent();

        numMonth = intent.getIntExtra("i",0);
        worksDataBase = new WorksDataBase(this);

        int[] daysInEachMonth = worksDataBase.getDaysInEachMonth();


        tvMonthNameOfMonth.setText( "חודש: " + getMonthName(numMonth));
        tvMonthNumOfDays_answer.setText(daysInEachMonth[numMonth - 1] + " ימים");

        List<Work> works = worksDataBase.getWorksByMonth(numMonth);  // Example: Retrieve works for January

        for (Work work : works) {

            long start = work.getStartDate();
            long end = work.getEndDate();

            Date startDate = new Date(start);
            Date endDate = new Date(end);

            long time = getDurationMillis(endDate , startDate);

            durationToatalMonth = durationToatalMonth + time;

        }
        tvMonthTotalHours_answer.setText(formatDuration(durationToatalMonth));

        for (Work work : works) {

            long start = work.getStartDate();
            long end = work.getEndDate();

            Date startDate = new Date(start);
            Date endDate = new Date(end);

            long timeOfDuration = getDurationMillis(endDate, startDate);

            Boolean salaryOnBreaking = sharedPreferences.getBoolean("SalaryOnBreak", false);
            int numOfBreaking = sharedPreferences.getInt("numberSelectTimeOfBreak", 0);

            if (salaryOnBreaking == false) {
                if (timeOfDuration > 6 * 60 * 60 * 1000) {
                    timeOfDuration = breaking(timeOfDuration, numOfBreaking);
                }
            }

            int numOfDaysWeek = sharedPreferences.getInt("NumOfDaysWorking", 0);
            Log.d("Precent", "days: "+ numOfDaysWeek);

            durationToatal125pMonth = durationToatal125pMonth + calculateTime125p(timeOfDuration, numOfDaysWeek);
            durationToatal150pMonth = durationToatal150pMonth + calculateTime150p(timeOfDuration, numOfDaysWeek);

            Log.d("Precent", "p125: "+ durationToatal125pMonth);
            Log.d("Precent", "p150: "+ durationToatal150pMonth);

        }

        tvMonthTotalHours125p_answer.setText(formatDuration(durationToatal125pMonth));
        tvMonthTotalHours150p_answer.setText(formatDuration(durationToatal150pMonth));
        tvMonthTotalHours100p_answer.setText(formatDuration(durationToatalMonth - durationToatal150pMonth - durationToatal125pMonth));


    }


    public String getMonthName(int monthNumber) {
        String monthName = "";

        if (monthNumber >= 1 && monthNumber <= 12) {
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] monthNames = dfs.getMonths();

            monthName = monthNames[monthNumber - 1];
        }

        return monthName;
    }


    public static long getDurationMillis(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
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