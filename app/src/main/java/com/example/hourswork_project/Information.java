package com.example.hourswork_project;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Information#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Information extends Fragment {

    // Declare buttons and text views
    Button btnMinSalary, btnTravelExpenses, btnOvertime, btnNotes;
    TextView tvMinSalary, tvTravelExpenses, tvOvertime;
    private SharedPreferences sharedPreferences;

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

        // Initialize text views and buttons
        tvMinSalary = view.findViewById(R.id.tvMinSalary);
        tvTravelExpenses = view.findViewById(R.id.tvTravelExpenses);
        tvOvertime = view.findViewById(R.id.tvOvertime);

        btnNotes = view.findViewById(R.id.btnNotes);
        btnMinSalary = view.findViewById(R.id.btnMinSalary);
        btnTravelExpenses = view.findViewById(R.id.btnTravelExpenses);
        btnOvertime = view.findViewById(R.id.btnOvertime);

        // Set text for minimum salary
        String strMinSalary = "עד גיל 16 : 22.54\n" +
                "מגיל 16 עד 17 : 24.15\n" +
                "מגיל 17 עד 18 : 26.73\n" +
                "מגיל 18 ומעלה : 29.96\n" +
                "\n * מעודכן לאפריל 23";
        tvMinSalary.setText(strMinSalary);

        // Set text for travel expenses
        String strTravelExpenses = "בלחיצה על יום עבודה מסוים בדוח השעות יוצג הסכום שהרווחת באותו היום ללא חישוב דמי נסיעות.\n" +
                "המערכת תוסיף לשכר החודשי שלך את דמי הנסיעות לפי חישוב יומי (כלומר עבור כל יום עבודה תקבל את הסכום שקבעת בהגדרות).";
        tvTravelExpenses.setText(strTravelExpenses);

        // Set text for overtime information
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

        // Set click listener for notes button
        btnNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotebookDialog();
            }
        });

        // Initialize shared preferences
        sharedPreferences = getContext().getSharedPreferences("notes", MODE_PRIVATE);

        // Set click listener for minimum salary button
        if (btnMinSalary != null) {
            btnMinSalary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == btnMinSalary) {
                        if (tvMinSalary.getVisibility() == View.GONE) {
                            tvMinSalary.setVisibility(View.VISIBLE);
                        } else {
                            tvMinSalary.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        // Set click listener for travel expenses button
        if (btnTravelExpenses != null) {
            btnTravelExpenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == btnTravelExpenses) {
                        if (tvTravelExpenses.getVisibility() == View.GONE) {
                            tvTravelExpenses.setVisibility(View.VISIBLE);
                        } else {
                            tvTravelExpenses.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        // Set click listener for overtime button
        if (btnOvertime != null) {
            btnOvertime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == btnOvertime) {
                        if (tvOvertime.getVisibility() == View.GONE) {
                            tvOvertime.setVisibility(View.VISIBLE);
                        } else {
                            tvOvertime.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        return view;
    }

    /**
     * Opens the notebook dialog for adding or editing notes.
     */
    private void openNotebookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("פנקס הערות");
        builder.setMessage("תכתוב בבקשה את הערות שלך פה:");

        final EditText notesEditText = new EditText(getContext());
        notesEditText.setText(sharedPreferences.getString("notes", ""));
        builder.setView(notesEditText);

        builder.setPositiveButton("שמור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String notes = notesEditText.getText().toString();
                saveNotes(notes);
                Toast.makeText(getContext(), "הערות נשמרו!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Saves the notes in shared preferences.
     *
     * @param notes The notes to be saved.
     */
    private void saveNotes(String notes) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("notes", notes);
        editor.apply();
    }
}
