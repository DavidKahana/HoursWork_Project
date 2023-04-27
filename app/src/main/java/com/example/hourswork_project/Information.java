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
public class Information extends Fragment {

    Button btnMinSalary , btnTravelExpenses , btnOvertime;
    TextView tvMinSalary , tvTravelExpenses , tvOvertime;

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

        tvMinSalary = view.findViewById(R.id.tvMinSalary);
        tvTravelExpenses = view.findViewById(R.id.tvTravelExpenses);
        tvOvertime = view.findViewById(R.id.tvOvertime);


        btnMinSalary = view.findViewById(R.id.btnMinSalary);
        btnTravelExpenses = view.findViewById(R.id.btnTravelExpenses);
        btnOvertime = view.findViewById(R.id.btnOvertime);

        String strOverTime = "אם קבעת בהגדרות כי העסק שלך\n" +
                "\n" +
                "פועל חמישה ימים בשבוע, אז השעות הנוספות תחושבנה כך: \n" +
                "* שעות נוספות של 125% מהשכר הרגיל תחושבנה על כל שעה שמעבר לשמונה שעות ו- 36 דקות.\n" +
                "\n" +
                " * שעות נוספות של 150% מהשכר הרגיל תחושבנה על כל שעה שמעבר לעשר שעות ו- 36 דקות.\n" +
                "\n" +
                "אם קבעת בהגדרות כי העסק שלך פועל שישה ימים בשבוע, אז השעות הנוספות\n" +
                "תחושבנה כך:\n" +
                "* שעות נוספות של 125% מהשכר\n" +
                "הרגיל תחושבנה על כל שעה שמעבר\n" +
                "לשמונה שעות.\n" +
                "\n" +
                "* שעות נוספות של 150% מהשכר\n" +
                "הרגיל תחושבנה על כל שעה שמעבר\n" +
                "לעשר שעות.";

        tvOvertime.setText(strOverTime);

        String strMinSalary = "עד גיל 16 : 22.54\n" +
                "מגיל 16 עד 17 : 24.15\n" +
                "מגיל 17 עד 18 : 26.73\n" +
                "מגיל 18 ומעלה : 29.96\n";

        tvMinSalary.setText(strMinSalary);



        if (btnMinSalary != null) {
            btnMinSalary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == btnMinSalary){
                        if (tvMinSalary.getVisibility() == View.GONE) {
                            tvMinSalary.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvMinSalary.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
        if (btnTravelExpenses != null) {
            btnTravelExpenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == btnTravelExpenses){
                        if (tvTravelExpenses.getVisibility() == View.GONE) {
                            tvTravelExpenses.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvTravelExpenses.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
        if (btnOvertime != null) {
            btnOvertime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == btnOvertime){
                        if (tvOvertime.getVisibility() == View.GONE) {
                            tvOvertime.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvOvertime.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        return view;
    }

}