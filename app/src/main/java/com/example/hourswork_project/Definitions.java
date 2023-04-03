package com.example.hourswork_project;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Definitions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Definitions extends Fragment {

    NumberPicker numberPicker;
    Button btnSelectAge , btnSelectTimeOfBreak;
    int selectedNumber , numberSelectTimeOfBreak;
    CheckBox cbMinSalary , cbSalaryOnBreak;
    EditText etAnother;
    TextView tvHourlyWage;
    boolean SalaryOnBreak;
    SharedPreferences sharedPreferences;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Definitions() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Definitions.
     */
    // TODO: Rename and change types and number of parameters
    public static Definitions newInstance(String param1, String param2) {
        Definitions fragment = new Definitions();
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
        View view = inflater.inflate(R.layout.fragment_definitions, container, false);

        cbSalaryOnBreak = view.findViewById(R.id.cbSalaryOnBreak);
        cbMinSalary = view.findViewById(R.id.cbMinSalary);
        tvHourlyWage = view.findViewById(R.id.tvHourlyWage);
        btnSelectAge = view.findViewById(R.id.btnSelectAge);
        btnSelectTimeOfBreak = view.findViewById(R.id.btnSelectTimeOfBreak);

        sharedPreferences = getContext().getSharedPreferences("Definitions", 0);



        btnSelectAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnSelectAge) {
                    Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.number_picker_dialog);
                    numberPicker = dialog.findViewById(R.id.number_picker);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(99);
                    Button okButton = dialog.findViewById(R.id.btnOk_number_picker);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v == okButton) {
                                selectedNumber = numberPicker.getValue();
                                btnSelectAge.setText("הגיל שלך הוא: "+ selectedNumber);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("selectedNumber" , selectedNumber);
                                editor.commit();
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });


        cbMinSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbMinSalary.isChecked()) {

                    tvHourlyWage.setText(minSalary(selectedNumber) + " לשעה");

                }
                else {
                    Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.hourlywage_picker_dialog);
                    etAnother = dialog.findViewById(R.id.etAnother);
                    Button okButton = dialog.findViewById(R.id.btnOk_etAnother);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v == okButton) {
                                String strNumber = etAnother.getText().toString();

                                if (!strNumber.isEmpty()) {
                                    int numberHourlyWage = Integer.parseInt(strNumber);
                                    tvHourlyWage.setText(strNumber + " לשעה");
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("numberHourlyWage" , numberHourlyWage);
                                    editor.commit();
                                    dialog.dismiss();
                                }
                                else{

                                }
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });


        btnSelectTimeOfBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnSelectTimeOfBreak) {
                    Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.number_picker_dialog);
                    numberPicker = dialog.findViewById(R.id.number_picker);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(200);
                    Button okButton = dialog.findViewById(R.id.btnOk_number_picker);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v == okButton) {
                                numberSelectTimeOfBreak = numberPicker.getValue();
                                btnSelectTimeOfBreak.setText("אורך ההפסקה בדקות הוא: "+ selectedNumber);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("numberSelectTimeOfBreak" , numberSelectTimeOfBreak);
                                editor.commit();
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });


        cbSalaryOnBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbSalaryOnBreak.isChecked()) {
                    SalaryOnBreak = true;
                }
                else {
                    SalaryOnBreak = false;
                }
            }

        });


        return view;

    }

    public double minSalary (int age){
        double minSalary = 0;

        if (age < 16)
            minSalary = 21.45;
        else if (age < 17 && age >=  16)
            minSalary = 22.98;
        else if (age < 18 && age >= 17)
            minSalary = 25.43;
        else if (age > 18)
            minSalary = 28.49;

        return minSalary;



    }
}