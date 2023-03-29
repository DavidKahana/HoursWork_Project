package com.example.hourswork_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
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
    TextView tvTimeEnter;
    Date dateAndTime;
    SharedPreferences sharedPreferences;
    private SimpleDateFormat dateFormat;
    private String date;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;


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


        btnDateAndTime = view.findViewById(R.id.btnDate);
        btnDateAndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("ron" , "1: " + dateAndTime);
                showDateTimeDialog();


            }
        });

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v==btnStartStop)
                {

                    Log.d("ron" , "2: " + dateAndTime);

                    if (btnStartStop.getText().equals("start") ){

                        long l = sharedPreferences.getLong("enter" , 0);
                        dateAndTime = new Date(l);
                        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss" );
                        date = dateFormat.format(dateAndTime);
                        tvTimeEnter.setText(date);

                        btnDateAndTime.setText("select date!");
                        btnStartStop.setText("stop");
                    }
                    else if (btnStartStop.getText().equals("stop")){

                        btnDateAndTime.setText("select time!");
                        btnStartStop.setText("start");
                    }
                }
            }
        });
        return view;
}

    private void showDateTimeDialog() {
        // Create a Calendar instance to get the current date and time
        Calendar calendar = Calendar.getInstance();

        // Create the DatePickerDialog with the current date
        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the selected date to the calendar instance
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Create the TimePickerDialog with the current time
                        timePickerDialog = new TimePickerDialog(getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Set the selected time to the calendar instance
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);


                                        // Check if the device is set to 24-hour time format
                                        boolean is24HourFormat = DateTimeFormatter.ofPattern("H").format(LocalDateTime.now()) == "H";

                                        // Update your UI with the selected date and time
                                        // Example: updateTextView(calendar.getTime(), is24HourFormat);
                                    }
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                DateTimeFormatter.ofPattern("H").format(LocalDateTime.now()) == "H"
                        );

                        // Show the TimePickerDialog
                        timePickerDialog.show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
        dateAndTime = calendar.getTime();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("enter" , dateAndTime.getTime());
        editor.commit();
    }
}


