package com.example.hourswork_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import androidx.appcompat.widget.LinearLayoutCompat;
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

    ListView worksLV;
    WorksAdapter worksAdapter;
    WorksDataBase worksDataBase;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hours_report, container, false);

        // Initialize WorksDataBase
        worksDataBase = new WorksDataBase(getContext());

        worksLV = view.findViewById(R.id.LV);

        // Initialize WorksAdapter with data from WorksDataBase
        worksAdapter = new WorksAdapter(getContext(), worksDataBase.getAllWorks());
        worksAdapter.setWorksList(worksDataBase.getAllWorks());

        // Set the adapter for the ListView
        worksLV.setAdapter(worksAdapter);

        worksLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Handle item click

                // Create an intent to navigate to ActionToItem activity
                Intent intent = new Intent(getContext(), ActionToItem.class);

                // Pass the work ID as an extra to the intent
                intent.putExtra("id", worksAdapter.getworkID(i));

                // Log the clicked item's position
                Log.d("david", "i: " + i);

                // Start the ActionToItem activity
                startActivity(intent);
            }
        });

        return view;
    }
}
