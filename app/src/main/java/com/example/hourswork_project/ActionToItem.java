package com.example.hourswork_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActionToItem extends AppCompatActivity {

    Button btnItemDelete;
    TextView tvItemStart , tvItemStop , tvItemDate , tvItemDurationWorking , tvItemMoreHours125 , tvItemMoreHours150 , tvItemSalary;
    WorksDataBase worksDataBase;
    Work work;
    int id , count = 0;
    SimpleDateFormat hoursAndMin = new SimpleDateFormat("HH:mm");
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

        id = intent.getIntExtra("id",0) ;
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


        btnItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                worksDataBase.deleteWork(id);
            }
        });

    }
}