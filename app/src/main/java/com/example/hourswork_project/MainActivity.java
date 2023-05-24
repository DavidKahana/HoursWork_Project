package com.example.hourswork_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    androidx.fragment.app.FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Button btnEntrance , btnHoursReport , btnInformation , btnDefinitions;
    private static final int REQUEST_CODE_RECEIVE_NOTIFICATIONS = 123;
    Entrance entrance = new Entrance();
    HoursReport hoursReport = new HoursReport();
    Information information = new Information();
    Definitions definitions = new Definitions();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();


        btnEntrance = (Button) findViewById(R.id.btnEntrance);
        btnHoursReport = (Button) findViewById(R.id.btnHoursReport);
        btnInformation = (Button) findViewById(R.id.btnInformation);
        btnDefinitions = (Button) findViewById(R.id.btnDefinitions);

        btnEntrance.setOnClickListener(this);
        btnHoursReport.setOnClickListener(this);
        btnInformation.setOnClickListener(this);
        btnDefinitions.setOnClickListener(this);

        Intent intent = getIntent();
        int state = intent.getIntExtra("state" , 0);
        Log.d("c1" , "state: " + state);

        if (state == 1){
            getSupportFragmentManager().beginTransaction().replace(R.id.container , hoursReport).commit();

        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.container , entrance).commit();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // call the method to show the notification
                    showNotification();
                }
            }, 10000); // 10 seconds

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channelId", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }



    }
    private void showNotification() {
        // Create an Intent to open MainActivity when the notification is clicked
        Intent intent = new Intent(this, Entrance.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channelId")
                .setSmallIcon(R.drawable.remind)
                .setContentTitle("תזכורת")
                .setContentText("אל תשכח למלא את כל שעות העבודה שלך שעשית לאחרונה!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }



    @Override
    public void onClick(View v) {



        if (v == btnEntrance){
            getSupportFragmentManager().beginTransaction().replace(R.id.container , entrance).commit();
        }
        else if (v == btnHoursReport){
            getSupportFragmentManager().beginTransaction().replace(R.id.container , hoursReport).commit();
        }
        else if (v == btnInformation){
            getSupportFragmentManager().beginTransaction().replace(R.id.container , information).commit();
        }
        else if (v == btnDefinitions){
            getSupportFragmentManager().beginTransaction().replace(R.id.container , definitions).commit();
        }



    }
}