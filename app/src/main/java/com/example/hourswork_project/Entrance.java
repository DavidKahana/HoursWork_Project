package com.example.hourswork_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.DateFormat;
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

    Button btnStartStop , btnDate , btnTime;
    TextView tvTime;
    Date dateAndTime;


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
        btnDate = view.findViewById(R.id.btnDate);
        btnTime = view.findViewById(R.id.btnTime);
        tvTime = view.findViewById(R.id.tvTime);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v==btnDate)
                {
                    Calendar systemCalender = Calendar.getInstance();
                    int year = systemCalender.get(Calendar.YEAR);
                    int month = systemCalender.get(Calendar.MONTH);
                    int day = systemCalender.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),new SetDate(),year,month,day);
                    datePickerDialog.show();

                    systemCalender.set(Calendar.YEAR, year);
                    systemCalender.set(Calendar.MONTH, month);
                    systemCalender.set(Calendar.DAY_OF_MONTH, day);

                    dateAndTime = systemCalender.getTime();


                }
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v==btnTime)
                {
                    Calendar systemCalendar = Calendar.getInstance();
                    int hour = systemCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = systemCalendar.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),new SetYourTime(),hour,minute,true);
                    timePickerDialog.show();;

                    systemCalendar.set(Calendar.HOUR_OF_DAY, hour);
                    systemCalendar.set(Calendar.MINUTE, minute);

                    dateAndTime = systemCalendar.getTime();

                }
            }
        });

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = 0;
                if(v==btnStartStop)
                {

                    if (btnStartStop.getText().equals("start") && dateAndTime != null){
                        btnDate.setText("select date!");
                        btnTime.setText("select time!");
                        btnStartStop.setText("stop");
                    }
                    else if (btnStartStop.getText().equals("stop") && dateAndTime != null){
                        btnDate.setText("select date!");
                        btnTime.setText("select time!");
                        btnStartStop.setText("start");
                    }
                }
            }
        });

        return view;
    }

    public  class SetDate implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear +1;

            String str = "You selected :" + dayOfMonth + "/" + monthOfYear +"/" + year;
            Toast.makeText(view.getContext(),str,Toast.LENGTH_LONG).show();
            btnDate.setText(str);

        }
    }

    public class  SetYourTime implements TimePickerDialog.OnTimeSetListener
    {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            String str = fixStr(hourOfDay,minute);
            Toast.makeText(view.getContext(),str,Toast.LENGTH_LONG).show();
            btnTime.setText(str);

        }
    }

    public String fixStr ( int hourOfDay, int minute){
        String str = "";

        if (hourOfDay < 10 && minute < 10){
            str = "Time is :" + "0"  + hourOfDay +":" + "0" + minute;
        }
        else if (hourOfDay < 10 && minute >= 10){
            str = "Time is :" +"0" + hourOfDay +":" + minute;
        }
        else if (hourOfDay >= 10 && minute < 10){
            str = "Time is :" + hourOfDay +":" + "0" + minute;
        }
        else{
            str = "Time is :" + hourOfDay +":" + minute;
        }

        return str;
    }
}

