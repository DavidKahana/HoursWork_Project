package com.example.hourswork_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    // Declare variables
    NumberPicker numberPicker;
    Button btnSelectAge, btnSelectTimeOfBreak, btnRestart, btnNumOfDaysWorking, btnNumTravelExpenses;
    int numberAge, numberSelectTimeOfBreak, numOfDaysWorking = 0;
    CheckBox cbMinSalary, cbSalaryOnBreak, sendCheckbox;
    float numberHourlyWage, numberTravelExpenses;
    EditText etAnother, phoneNumberEditText;
    TextView tvHourlyWage;
    boolean SalaryOnBreak, sendSms;
    SharedPreferences sharedPreferences;
    String phoneNumber;
    private static final int CONTACTS_REQUEST_CODE = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Settings() {
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
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize UI components
        cbSalaryOnBreak = view.findViewById(R.id.cbSalaryOnBreak);
        cbMinSalary = view.findViewById(R.id.cbMinSalary);
        tvHourlyWage = view.findViewById(R.id.tvHourlyWage);
        btnSelectAge = view.findViewById(R.id.btnSelectAge);
        btnSelectTimeOfBreak = view.findViewById(R.id.btnSelectTimeOfBreak);
        btnRestart = view.findViewById(R.id.btnRestart);
        btnNumOfDaysWorking = view.findViewById(R.id.btnNumOfDaysWorking);
        phoneNumberEditText = view.findViewById(R.id.phone_number);
        sendCheckbox = view.findViewById(R.id.send_checkbox);
        btnNumTravelExpenses = view.findViewById(R.id.btnNumTravelExpenses);
        ImageButton contactsButton = view.findViewById(R.id.contacts_button);


        sharedPreferences = getContext().getSharedPreferences("Definitions", 0);

        // Set values for UI components based on stored preferences
        if (sharedPreferences.getInt("selectedNumber", 0) > 0) {
            btnSelectAge.setText("הגיל שלך הוא: " + sharedPreferences.getInt("selectedNumber", 0));
        }
        if (sharedPreferences.getBoolean("MinSalary", false)) {
            tvHourlyWage.setText(sharedPreferences.getFloat("numberHourlyWageMinSalary", 0) + " לשעה");
        } else {
            if (sharedPreferences.getFloat("numberHourlyWageFloat", 0) > 0) {
                tvHourlyWage.setText(sharedPreferences.getFloat("numberHourlyWageFloat", 0) + " לשעה");
                cbMinSalary.setChecked(false);
            }
        }
        if (sharedPreferences.getInt("numberSelectTimeOfBreak", 0) > 0) {
            btnSelectTimeOfBreak.setText("אורך ההפסקה בדקות הוא: " + sharedPreferences.getInt("numberSelectTimeOfBreak", 0));
        }
        cbSalaryOnBreak.setChecked(sharedPreferences.getBoolean("SalaryOnBreak", false));

        if (sharedPreferences.getInt("NumOfDaysWorking", 0) == 5) {
            btnNumOfDaysWorking.setText("מספר ימי העבודה בשבוע: " + 5);
        }
        if (sharedPreferences.getInt("NumOfDaysWorking", 0) == 6) {
            btnNumOfDaysWorking.setText("מספר ימי העבודה בשבוע: " + 6);
        }
        if (sharedPreferences.getFloat("numberTravelExpenses", 0) > 0) {
            btnNumTravelExpenses.setText("דמי נסיעות יומי: " + sharedPreferences.getFloat("numberTravelExpenses", 0));
        }
        if (sharedPreferences.getString("phoneNumber", null) != null) {
            phoneNumberEditText.setText(sharedPreferences.getString("phoneNumber", null));
        }
        sendCheckbox.setChecked(sharedPreferences.getBoolean("sendSms", false));

        btnSelectAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnSelectAge) {
                    // Display a dialog to select age
                    Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.number_picker_dialog);
                    numberPicker = dialog.findViewById(R.id.number_picker);
                    numberPicker.setMinValue(14);
                    numberPicker.setMaxValue(120);
                    Button okButton = dialog.findViewById(R.id.btnOk_number_picker);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v == okButton) {
                                // Get selected age and update the button text
                                numberAge = numberPicker.getValue();
                                btnSelectAge.setText("הגיל שלך הוא: " + numberAge);

                                // Save selected age to shared preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("selectedNumber", numberAge);
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
                    // Calculate minimum salary based on age and display it
                    tvHourlyWage.setText(minSalary(numberAge) + " לשעה");

                    // Save minimum salary and update preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("numberHourlyWageMinSalary", (float) minSalary(numberAge));
                    editor.putBoolean("MinSalary", true);
                    editor.commit();
                } else {
                    // Display a dialog to enter custom hourly wage
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
                                    // Get custom hourly wage and display it
                                    numberHourlyWage = Float.parseFloat(strNumber);
                                    tvHourlyWage.setText(strNumber + " לשעה");

                                    // Save custom hourly wage and update preferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putFloat("numberHourlyWageFloat", numberHourlyWage);
                                    editor.putBoolean("MinSalary", false);
                                    editor.commit();
                                    dialog.dismiss();
                                } else {
                                    // Handle empty input case
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
                    // Create a dialog for selecting the time of break
                    Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.number_picker_dialog);
                    numberPicker = dialog.findViewById(R.id.number_picker);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(180);
                    Button okButton = dialog.findViewById(R.id.btnOk_number_picker);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v == okButton) {
                                // Retrieve the selected time of break and update the button text
                                numberSelectTimeOfBreak = numberPicker.getValue();
                                btnSelectTimeOfBreak.setText("אורך ההפסקה בדקות הוא: " + numberSelectTimeOfBreak);

                                // Store the selected time of break in SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("numberSelectTimeOfBreak", numberSelectTimeOfBreak);
                                editor.commit();

                                dialog.dismiss(); // Close the dialog
                            }
                        }
                    });
                    dialog.show(); // Display the dialog
                }
            }
        });

        cbSalaryOnBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbSalaryOnBreak.isChecked()) {
                    // Enable salary on break
                    SalaryOnBreak = true;
                } else {
                    // Disable salary on break
                    SalaryOnBreak = false;
                }

                // Store the value of SalaryOnBreak in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("SalaryOnBreak", SalaryOnBreak);
                editor.commit();
            }
        });

        btnNumOfDaysWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnNumOfDaysWorking) {
                    // Create an AlertDialog builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    // Set the alert message and title
                    builder.setMessage("כמה הימים אתה עובד בשבוע?")
                            .setTitle("Confirmation");

                    // Set the positive button and its click listener
                    builder.setPositiveButton("5 ימים", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Set the number of days working to 5
                            numOfDaysWorking = 5;

                            // Store the number of days working in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("NumOfDaysWorking", numOfDaysWorking);
                            editor.commit();

                            btnNumOfDaysWorking.setText("מספר ימי העבודה בשבוע: " + 5);
                        }
                    });

                    // Set the negative button and its click listener
                    builder.setNegativeButton("6 ימים", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Set the number of days working to 6
                            numOfDaysWorking = 6;

                            // Store the number of days working in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("NumOfDaysWorking", numOfDaysWorking);
                            editor.commit();

                            btnNumOfDaysWorking.setText("מספר ימי העבודה בשבוע: " + 6);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        btnNumTravelExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a dialog for entering travel expenses
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.travel_expenses_picker_dialog);
                etAnother = dialog.findViewById(R.id.etTravelExpenses);
                Button okButton = dialog.findViewById(R.id.btnOk_etTravelExpenses);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == okButton) {
                            // Retrieve the entered travel expenses and update the button text
                            String strNumberTravelExpenses = etAnother.getText().toString();

                            if (!strNumberTravelExpenses.isEmpty()) {
                                numberTravelExpenses = Float.parseFloat(strNumberTravelExpenses);
                                btnNumTravelExpenses.setText("דמי נסיעות יומי: " + numberTravelExpenses);

                                // Store the travel expenses in SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putFloat("numberTravelExpenses", numberTravelExpenses);
                                editor.commit();

                                dialog.dismiss(); // Close the dialog
                            } else {
                                // Handle the case when no travel expenses are entered
                            }
                        }
                    }
                });
                dialog.show(); // Display the dialog
            }
        });

        sendCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sendCheckbox.isChecked()) {
                    // Enable sending SMS
                    sendSms = true;

                    // Retrieve the phone number from the edit text
                    phoneNumber = phoneNumberEditText.getText().toString();

                    // Store the values of sendSms and phoneNumber in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("sendSms", sendSms);
                    editor.putString("phoneNumber", phoneNumber);
                    editor.commit();
                } else {
                    // Disable sending SMS
                    sendSms = false;

                    // Store the value of sendSms in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("sendSms", sendSms);
                    editor.commit();
                }
            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnRestart) {
                    // Create an AlertDialog builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    // Set the alert message and title
                    builder.setMessage("אתה בטוח שאתה רוצה לאפס את הנתונים שהוזנו?")
                            .setTitle("Confirmation");

                    // Set the positive button and its click listener
                    builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Reset all the values in SharedPreferences to their default values
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("numberSelectTimeOfBreak", 0);
                            editor.putFloat("numberHourlyWageFloat", 0);
                            editor.putBoolean("MinSalary", false);
                            editor.putInt("selectedNumber", 0);
                            editor.putBoolean("SalaryOnBreak", false);
                            editor.putInt("NumOfDaysWorking", 0);
                            editor.putFloat("numberTravelExpenses", 0);
                            editor.putString("phoneNumber", "");
                            editor.putBoolean("sendSms", false);
                            editor.commit();

                            // Reset the UI elements to their default values
                            btnSelectAge.setText("גיל");
                            tvHourlyWage.setText("*** לשעה");
                            cbMinSalary.setChecked(false);
                            btnSelectTimeOfBreak.setText("משך זמן ההפסקה (בדקות)");
                            cbSalaryOnBreak.setChecked(false);
                            btnNumOfDaysWorking.setText("מספר ימי העבודה בשבוע");
                            btnNumTravelExpenses.setText("דמי נסיעות יומי");
                            phoneNumberEditText.setText("");
                            sendCheckbox.setChecked(false);
                        }
                    });

                    // Set the negative button and its click listener
                    builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContacts();
            }
        });

        return view;

    }

    private void openContacts() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_REQUEST_CODE);
        } else {
            launchContactsPicker();
        }
    }

    private void launchContactsPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACTS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACTS_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchContactsPicker();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACTS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri contactUri = data.getData();
            if (contactUri != null) {
                retrieveContactPhoneNumber(contactUri);
            }
        }
    }

    private void retrieveContactPhoneNumber(Uri contactUri) {
        Cursor cursor = getContext().getContentResolver().query(contactUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
            String[] selectionArgs = {contactId};

            Cursor phoneCursor = getContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    null
            );

            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                int phoneColumnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = phoneCursor.getString(phoneColumnIndex);
                phoneNumberEditText.setText(phoneNumber);
                phoneCursor.close();

                sendSms = true;
                sendCheckbox.setChecked(true);
                // Retrieve the phone number from the edit text
                phoneNumber = phoneNumberEditText.getText().toString();

                // Store the values of sendSms and phoneNumber in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("sendSms", sendSms);
                editor.putString("phoneNumber", phoneNumber);
                editor.commit();
            }

            cursor.close();
        }
    }










    // Calculate Minimum salary
    public double minSalary(int age) {
        double minSalary = 0;

        if (age < 16)
            minSalary = 22.54;
        else if (age < 17 && age >= 16)
            minSalary = 24.15;
        else if (age < 18 && age >= 17)
            minSalary = 26.73;
        else if (age >= 18)
            minSalary = 29.96;

        return minSalary;
    }
}