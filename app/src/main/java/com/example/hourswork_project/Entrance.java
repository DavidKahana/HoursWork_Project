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
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Entrance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Entrance extends Fragment {

    private Button btnStartStop , btnDate;
    private TextView mTextView;
    private Calendar mCalendar;


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
        btnDate = view.findViewById(R.id.btnDateAndTime);
        mTextView = view.findViewById(R.id.tvTime);
        mCalendar = Calendar.getInstance();

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                int day = currentDate.get(Calendar.DAY_OF_MONTH);
                int hour = currentDate.get(Calendar.HOUR_OF_DAY);
                int minute = currentDate.get(Calendar.MINUTE);

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, monthOfYear);
                                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                TimePickerDialog timePicker = new TimePickerDialog(getActivity(),
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                mCalendar.set(Calendar.MINUTE, minute);

                                                long differenceInMillis = Calendar.getInstance().getTimeInMillis() - mCalendar.getTimeInMillis();
                                                String timePassed = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(differenceInMillis),
                                                        TimeUnit.MILLISECONDS.toMinutes(differenceInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(differenceInMillis)),
                                                        TimeUnit.MILLISECONDS.toSeconds(differenceInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(differenceInMillis)));
                                                mTextView.setText("Time passed: " + timePassed);
                                            }
                                        },
                                        hour,
                                        minute,
                                        android.text.format.DateFormat.is24HourFormat(getActivity()));

                                timePicker.show();
                            }
                        },
                        year,
                        month,
                        day);

                datePicker.show();
            }
        });

        return view;
    }
}
