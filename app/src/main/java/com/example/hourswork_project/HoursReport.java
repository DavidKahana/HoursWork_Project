package com.example.hourswork_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HoursReport#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HoursReport extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView worksLV;
    int finalI;
    WorksDataBase worksDataBase;
    LinearLayout container2;

    public HoursReport() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HoursReport.
     */
    // TODO: Rename and change types and number of parameters
    public static HoursReport newInstance(String param1, String param2) {
        HoursReport fragment = new HoursReport();
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

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_hours_report, container, false);
//        worksLV = view.findViewById(R.id.list_view_works);
//        worksDataBase = new WorksDataBase(getContext());
//
//        WorksAdapter worksAdapter = new WorksAdapter(getContext(), worksDataBase.getAllWorks());
//        worksAdapter.setWorksList(worksDataBase.getAllWorks());
//        worksLV.setAdapter(worksAdapter);
//
//        worksLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Intent intent = new Intent(getContext() , ActionToItem.class);
//
//
//
//                intent.putExtra("id" , worksAdapter.getworkID(i));
//                Log.d("david", "i: "+ i);
//                startActivity(intent);
//
//            }
//        });
//
//
//        return view;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hours_report, container, false);


        container2 = view.findViewById(R.id.container2);

        worksDataBase = new WorksDataBase(getContext());
        int[] daysInEachMonth = worksDataBase.getDaysInEachMonth();

        for (int i = 1; i < 13; i++) {

            if (daysInEachMonth[i - 1] > 0) {

                Button button = new Button(getContext());
                button.setText(getMonthName(i));
                button.setTag(i);

                container2.addView(button);


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        actButton((Integer) button.getTag());

                    }
                });


                ListView listView = new ListView(getContext());

                WorksAdapter adapter = new WorksAdapter(getContext(), worksDataBase.getWorksByMonth(i));
                adapter.setWorksList(worksDataBase.getWorksByMonth(i));

                listView.setAdapter(adapter);

                container2.addView(listView);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Intent intent = new Intent(getContext(), ActionToItem.class);


                        intent.putExtra("id", adapter.getworkID(i));
                        Log.d("david", "i: " + i);
                        startActivity(intent);

                    }
                });



            }


        }

        return view;
    }

    public void actButton (final int i){

        Intent intentMonth = new Intent(getContext(), monthlySummary.class);

        intentMonth.putExtra("i", i);
        Log.d("i", "i: " + i);
        startActivity(intentMonth);
    }

    // Create a separate method to get the OnClickListener for each button
    private View.OnClickListener getButtonClickListener(final int buttonIndex) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button retrievedButton = container2.findViewWithTag("button1");

                if (v == retrievedButton) {

                    Intent intentMonth = new Intent(getContext(), monthlySummary.class);

                    intentMonth.putExtra("i", finalI);
                    Log.d("i", "i: " + finalI);
                    startActivity(intentMonth);
                }
            }
        };
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