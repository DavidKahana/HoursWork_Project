package com.example.hourswork_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    androidx.fragment.app.FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Button btnEntrance , btnHoursReport , btnInformation , btnDefinitions;


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

    }

    @Override
    public void onClick(View v) {

        Entrance entrance = new Entrance();
        HoursReport hoursReport = new HoursReport();
        Information information = new Information();
        Definitions definitions = new Definitions();


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