package com.example.hourswork_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActionToItem extends AppCompatActivity {

    Button btnItemDelete , btnItemToMoveHoutsReport;
    TextView tvItemStart , tvItemStop , tvTitleItemDurationWorkingIncludingBreaking , tvItemDurationWorking , tvItemDurationWorkingIncludingBreaking , tvItemMoreHours125 , tvItemMoreHours150 , tvItemSalary ;
    WorksDataBase worksDataBase;
    Work work;
    int id ;
    long duration;
    Date startUpdate , stopUpdate , dateAndTime , start , stop;
    SharedPreferences sharedPreferences;
    SimpleDateFormat hoursAndMin = new SimpleDateFormat("HH:mm");
    SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy HH:mm" );
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_to_item);
        setTitle("hello");

        tvItemStart = findViewById(R.id.tvItemStart);
        tvItemStop = findViewById(R.id.tvItemStop);
        tvItemDurationWorking = findViewById(R.id.tvItemDurationWorking);
        tvItemDurationWorkingIncludingBreaking = findViewById(R.id.tvItemDurationWorkingIncludingBreaking);
        tvItemMoreHours125 = findViewById(R.id.tvItemMoreHours125);
        tvItemMoreHours150 = findViewById(R.id.tvItemMoreHours150);
        tvItemSalary = findViewById(R.id.tvItemSalary);
        tvTitleItemDurationWorkingIncludingBreaking = findViewById(R.id.tvTitleItemDurationWorkingIncludingBreaking);

        btnItemDelete = findViewById(R.id.btnItemDelete);
        btnItemToMoveHoutsReport = findViewById(R.id.btnItemToMoveHoutsReport);

        sharedPreferences = getSharedPreferences("Definitions", 0);

        Intent intent=getIntent();

        id = intent.getIntExtra("id",0);

        Log.d("david", "id: "+ id);
        worksDataBase = new WorksDataBase(this);

        work = worksDataBase.getWorkById(id);

        Log.d("david", "onCreate: "+ work.toString());
        start = Calendar.getInstance().getTime();
        start.setTime(work.getStartDate());
        stop = Calendar.getInstance().getTime();
        stop.setTime(work.getEndDate());
        tvItemStart.setText( date.format(start));
        tvItemStop.setText( date.format(stop));


        showResult();


        tvItemStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    showDateTimeDialogStart();


//                    long l = sharedPreferences.getLong("updateStart", 0);
//                    startUpdate = new Date(l);
//
//                    Work work = new Work(id, startUpdate.getTime(), stop.getTime());
//                    worksDataBase.updateWork(work);
//                    start = startUpdate;


            }
        });

        tvItemStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDateTimeDialogStop();

//                    long l2 = sharedPreferences.getLong("updateStop", 0);
//                    stopUpdate = new Date(l2);
//
//                    Work w = new Work(id, start.getTime(), stopUpdate.getTime());
//                    worksDataBase.updateWork(w);
//                    stop = stopUpdate;
//                    showResult();



            }
        });


        btnItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                worksDataBase.deleteWork(id);
                HoursReport hoursReport = new HoursReport();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.container , hoursReport).commit();

            }
        });


        btnItemToMoveHoutsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HoursReport hoursReport = new HoursReport();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.container , hoursReport).commit();

            }
        });

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
    private void showDateTimeDialogStart() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);



                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(calendar.SECOND ,0);
                        calendar.set(calendar.MILLISECOND , 0);


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("updateStart" , calendar.getTime().getTime());
                        editor.commit();

                        long l = sharedPreferences.getLong("updateStart" , 0);
                        dateAndTime = new Date(l);


                        String strDate = date.format(dateAndTime);
                        String str = "You selected :" + strDate;
                        Toast.makeText(view.getContext(),str,Toast.LENGTH_LONG).show();
                        tvItemStart.setText(strDate);

                        long l2 = sharedPreferences.getLong("updateStart", 0);
                        startUpdate = new Date(l2);

                        Work work = new Work(id, startUpdate.getTime(), stop.getTime());
                        worksDataBase.updateWork(work);
                        start = startUpdate;
                        showResult();
                    }
                };

                new TimePickerDialog(view.getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void showDateTimeDialogStop() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);



                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(calendar.SECOND ,0);
                        calendar.set(calendar.MILLISECOND , 0);


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("updateStop" , calendar.getTime().getTime());
                        editor.commit();

                        long l = sharedPreferences.getLong("updateStop" , 0);
                        dateAndTime = new Date(l);


                        String strDate = date.format(dateAndTime);
                        String str = "You selected :" + strDate;
                        Toast.makeText(view.getContext(),str,Toast.LENGTH_LONG).show();
                        tvItemStop.setText(strDate);

                        long l2 = sharedPreferences.getLong("updateStop", 0);
                        stopUpdate = new Date(l2);

                        Work w = new Work(id, start.getTime(), stopUpdate.getTime());
                        worksDataBase.updateWork(w);
                        stop = stopUpdate;
                        showResult();
                    }
                };

                new TimePickerDialog(view.getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void showResult (){
        duration = getDurationMillis(start ,stop );

        tvItemDurationWorking.setText( formatDuration(duration));

        Boolean salaryOnBreaking = sharedPreferences.getBoolean("SalaryOnBreak" , false);
        int numOfBreaking = sharedPreferences.getInt("numberSelectTimeOfBreak" , 0);
        if (salaryOnBreaking == false){
            if (duration > 6 * 60 * 60 * 1000){
                duration = breaking(duration , numOfBreaking);
                tvTitleItemDurationWorkingIncludingBreaking.setText("משך עבודה עבורו קיבלתי שכר (כלומר ללא זמן ההפסקה): " );
                tvTitleItemDurationWorkingIncludingBreaking.setVisibility(View.VISIBLE);
                tvItemDurationWorkingIncludingBreaking.setText(formatDuration(duration));
                tvItemDurationWorkingIncludingBreaking.setVisibility(View.VISIBLE);
            }
            else{
                tvTitleItemDurationWorkingIncludingBreaking.setVisibility(View.GONE);
                tvItemDurationWorkingIncludingBreaking.setVisibility(View.GONE);
            }
        }

        int numOfDaysWeek = sharedPreferences.getInt("NumOfDaysWorking" , 0) ;
        Log.d("mmm", "m: "+ numOfDaysWeek);
        tvItemMoreHours125.setText( formatDuration(calculateTime125p(duration , numOfDaysWeek)));
        tvItemMoreHours150.setText( formatDuration(calculateTime150p(duration , numOfDaysWeek)));

        double numberHourlyWage = sharedPreferences.getInt("numberHourlyWage" , 0);
        double salaryDaily = salaryDay(numberHourlyWage , duration , numOfDaysWeek);

        tvItemSalary.setText(decimalFormat.format(salaryDaily) + " שקלים חדשים ");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("salaryDaily" , (float) salaryDaily);
        editor.commit();
    }

}