package com.example.hourswork_project;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    SharedPreferences sharedPreferences1 , sharedPreferences2;
    SimpleDateFormat dateFormat;
    String date , phoneNumber , strMessage;
    long duration;
    Boolean sendSms;
    WorksDataBase worksDataBase;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

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
        tvTimeStop = view.findViewById(R.id.tvTimeStop);
        tvDuration = view.findViewById(R.id.tvDuration);

        sharedPreferences1 = getContext().getSharedPreferences("Dates", 0);

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
                    startStopDate();

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
                        calendar.set(calendar.SECOND ,0);
                        calendar.set(calendar.MILLISECOND , 0);


                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                        editor.putLong("date" , calendar.getTime().getTime());
                        editor.commit();

                        long l = sharedPreferences1.getLong("date" , 0);
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

    private void sendSMS(String phoneNo, String message) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();

                // Divide the message into parts if it's too long
                ArrayList<String> messageParts = smsManager.divideMessage(message);

                // Send the message as multiple parts if necessary
                if (messageParts.size() > 1) {
                    ArrayList<PendingIntent> sentIntents = new ArrayList<>();
                    ArrayList<PendingIntent> deliveredIntents = new ArrayList<>();

                    for (int i = 0; i < messageParts.size(); i++) {
                        sentIntents.add(null);
                        deliveredIntents.add(null);
                    }

                    smsManager.sendMultipartTextMessage(phoneNo, null, messageParts, sentIntents, deliveredIntents);
                } else {
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                }

                Toast.makeText(getContext(), "SMS sent successfully.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "SMS failed to send.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }


    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS(phoneNumber, strMessage);
                } else {
                    Toast.makeText(getContext(),
                            "SMS permission denied.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void startStopDate(){

        if (btnStartStop.getText().equals("להתחיל") ){

            long l = sharedPreferences1.getLong("date" , 0);
            dateAndTime = new Date(l);


            dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm" );
            date = dateFormat.format(dateAndTime);
            tvTimeEnter.setText( "שעת כניסה:" + '\n' + date);
            tvTimeStop.setText("");
            tvDuration.setText("");

            strMessage =  "שעת כניסה:" + '\n' + date;
            dateStart = dateAndTime;

            btnDateAndTime.setText("תבחר תאריך ושעה!");
            btnStartStop.setText("לסיים");
        }
        else if (btnStartStop.getText().equals("לסיים") && dateAndTime  != null ){

            long l = sharedPreferences1.getLong("date" , 0);
            dateAndTime = new Date(l);

            if (dateStart.getTime()>= dateAndTime.getTime()){
                Toast.makeText(getContext(), "היציאה חייבת להיות אחרי הכניסה", Toast.LENGTH_LONG).show();
            }
            else {
                dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                date = dateFormat.format(dateAndTime);
                tvTimeStop.setText("שעת יציאה:" + '\n' + date);

                strMessage += '\n' + "שעת יציאה:" + '\n' + date;
                dateStop = dateAndTime;
                duration = getDurationMillis(dateStart, dateStop);
                tvDuration.setText(formatDuration(duration));
                strMessage += '\n' + formatDuration(duration);

                btnDateAndTime.setText("תבחר תאריך ושעה!");
                btnStartStop.setText("להתחיל");

                Work work = new Work(dateStart.getTime(), dateStop.getTime());
                worksDataBase.addWork(work);

                sharedPreferences2 = getContext().getSharedPreferences("Definitions", 0);
                phoneNumber = sharedPreferences2.getString("phoneNumber", null);
                sendSms = sharedPreferences2.getBoolean("sendSms", false);

                if (sendSms) {
                    sendSMS(phoneNumber, strMessage);
                }
            }
        }

    }
}


