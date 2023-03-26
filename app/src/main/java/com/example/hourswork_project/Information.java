package com.example.hourswork_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Information#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Information extends Fragment implements View.OnClickListener {

    Button btnMinSalary , btnTravelExpenses , btnOvertime;
    TextView tvDisplay;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;





    public Information() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Information.
     */
    // TODO: Rename and change types and number of parameters
    public static Information newInstance(String param1, String param2) {
        Information fragment = new Information();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        tvDisplay = view.findViewById(R.id.tvDisdlay);


        btnMinSalary = view.findViewById(R.id.btnInformation);
        btnTravelExpenses = view.findViewById(R.id.btnTravelExpenses);
        btnOvertime = view.findViewById(R.id.btnOvertime);

        if (btnMinSalary != null) {
            btnMinSalary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == btnMinSalary){
                        tvDisplay.setText("שכר מינימום");
                    }
                }
            });
        }
        if (btnTravelExpenses != null) {
            btnTravelExpenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == btnTravelExpenses){
                        tvDisplay.setText("דמי נסיעות");
                    }
                }
            });
        }
        if (btnOvertime != null) {
            btnOvertime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == btnOvertime){
                        tvDisplay.setText("שעות נוספות");
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onClick(View v) {



    }
}