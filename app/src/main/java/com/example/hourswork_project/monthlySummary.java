package com.example.hourswork_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormatSymbols;

public class monthlySummary extends AppCompatActivity {

    TextView tvMonthNameOfMonth , tvMonthNumOfDays_answer , tvMonthTotalHours_answer ,tvMonthTotalHours100p_answer , tvMonthTotalHours125p_answer , tvMonthTotalHours150p_answer , tvMonthTotalsalary_answer;
    SharedPreferences sharedPreferences;
    int numMonth;
    WorksDataBase worksDataBase;

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
        Log.d("Month", "days: "+ daysInEachMonth[numMonth- 1]);



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




}