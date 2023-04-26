package com.example.hourswork_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Entrance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Entrance extends Fragment {

    Button btnStartStop, btnDateAndTime;
    TextView tvTimeEnter , tvTimeStop , tvDuration;
    Date dateAndTime , dateStart , dateStop;
    SharedPreferences sharedPreferences;
    SimpleDateFormat dateFormat;
    String date;
    long duration;
    WorksDataBase worksDataBase;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Entrance() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Entrance.
     */
    // TODO: Rename and change types and number of parameters
    public static Entrance newInstance(String param1, String param2) {
        Entrance fragment = new Entrance();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrance, container, false);


        btnStartStop = view.findViewById(R.id.btnStartStop);
        tvTimeEnter = view.findViewById(R.id.tvTimeEnter);
        sharedPreferences = getContext().getSharedPreferences("Dates", 0);
        tvTimeStop = view.findViewById(R.id.tvTimeStop);
        tvDuration = view.findViewById(R.id.tvDuration);

        worksDataBase = new WorksDataBase(getContext());

        btnDateAndTime = view.findViewById(R.id.btnDate);
        btnDateAndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v == btnDateAndTime) {
                    showDateTimeDialog();
                }
            }
        });

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v==btnStartStop)
                {


                    if (btnStartStop.getText().equals("start") ){

                        long l = sharedPreferences.getLong("date" , 0);
                        dateAndTime = new Date(l);


                        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm" );
                        date = dateFormat.format(dateAndTime);
                        tvTimeEnter.setText( "שעת כניסה:" + '\n' + date);
                        tvTimeStop.setText("");
                        tvDuration.setText("");

                        dateStart = dateAndTime;

                        btnDateAndTime.setText("select date and time!");
                        btnStartStop.setText("stop");
                    }
                    else if (btnStartStop.getText().equals("stop") && dateAndTime  != null){

                        long l = sharedPreferences.getLong("date" , 0);
                        dateAndTime = new Date(l);


                        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm" );
                        date = dateFormat.format(dateAndTime);
                        tvTimeStop.setText( "שעת יציאה:" + '\n' + date);

                        dateStop = dateAndTime;
                        duration = getDurationMillis(dateStart ,dateStop );
                        tvDuration.setText(formatDuration(duration));

                        btnDateAndTime.setText("select date and time!");
                        btnStartStop.setText("start");

                        Work work = new Work(dateStart.getTime(),dateStop.getTime());
                        worksDataBase.addWork(work);



                    }
                }
            }
        });
        return view;
}

    private void showDateTimeDialog() {
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


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("date" , calendar.getTime().getTime());
                        editor.commit();

                        long l = sharedPreferences.getLong("date" , 0);
                        dateAndTime = new Date(l);


                        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm" );
                        date = dateFormat.format(dateAndTime);
                        String str = "You selected :" + date;
                        Toast.makeText(getContext(),str,Toast.LENGTH_LONG).show();
                        btnDateAndTime.setText(date);
                    }
                };

                new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

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
        if (hours > 0) {
            result += hours % 24 + " שעות ";
        }
        if (minutes > 0) {
            result += minutes % 60 + " דקות ";
        }
        if (result.equals("")) {
            result = "0 דקות";
        }

        return result;
    }





}


