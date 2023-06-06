package com.example.hourswork_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ActionToItem extends AppCompatActivity {

    // Declare variables
    Button btnItemDelete;
    TextView tvItemStart, tvItemStop, tvTitleItemDurationWorkingIncludingBreaking, tvItemDurationWorking, tvItemDurationWorkingIncludingBreaking, tvItemMoreHours125, tvItemMoreHours150, tvItemSalary;
    WorksDataBase worksDataBase;
    Work work;
    int id;
    long duration;
    Date startUpdate, stopUpdate, dateAndTime, start, stop;
    SharedPreferences sharedPreferences;
    SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy HH:mm");
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_to_item);

        // Initialize views
        tvItemStart = findViewById(R.id.tvItemStart);
        tvItemStop = findViewById(R.id.tvItemStop);
        tvItemDurationWorking = findViewById(R.id.tvItemDurationWorking);
        tvItemDurationWorkingIncludingBreaking = findViewById(R.id.tvItemDurationWorkingIncludingBreaking);
        tvItemMoreHours125 = findViewById(R.id.tvItemMoreHours125);
        tvItemMoreHours150 = findViewById(R.id.tvItemMoreHours150);
        tvItemSalary = findViewById(R.id.tvItemSalary);
        tvTitleItemDurationWorkingIncludingBreaking = findViewById(R.id.tvTitleItemDurationWorkingIncludingBreaking);

        btnItemDelete = findViewById(R.id.btnItemDelete);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("Definitions", 0);

        // Get intent data
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);

        Log.d("david", "id: " + id);
        worksDataBase = new WorksDataBase(this);

        // Retrieve work details from the database
        work = worksDataBase.getWorkById(id);

        Log.d("david", "onCreate: " + work.toString());
        start = Calendar.getInstance().getTime();
        start.setTime(work.getStartDate());
        stop = Calendar.getInstance().getTime();
        stop.setTime(work.getEndDate());
        tvItemStart.setText(date.format(start));
        tvItemStop.setText(date.format(stop));

        // Show the duration and other details
        showResult();

        // Set click listener for start time text view
        tvItemStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show date-time picker dialog for selecting start time
                showDateTimeDialogStart();
            }
        });

        // Set click listener for stop time text view
        tvItemStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show date-time picker dialog for selecting stop time
                showDateTimeDialogStop();
            }
        });

        // Set click listener for delete button
        btnItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(ActionToItem.this);

                // Set the alert message and title
                builder.setMessage("אתה בטוח שאתה רוצה למחוק את המשמרת?")
                        .setTitle("Confirmation");

                // Set the positive button and its click listener
                builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int idd) {
                        // Delete the work from the database
                        worksDataBase.deleteWork(id);

                        // Start the main activity
                        Intent i = new Intent(ActionToItem.this, MainActivity.class);
                        i.putExtra("state", 1);
                        startActivity(i);
                    }
                });

                // Set the negative button and its click listener
                builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                // Create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_item_monthlySummary) {
            // Get the start date of the work
            Date startDate = new Date(work.getStartDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            // Extract the year and month from the start date
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            // Start the monthly summary activity with the selected year and month
            Intent intent = new Intent(this, MonthlySummary.class);
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            startActivity(intent);
            return true;
        }

        if (itemId == R.id.menu_item_backToHoursReport) {
            // Start the main activity
            Intent i = new Intent(ActionToItem.this, MainActivity.class);
            i.putExtra("state", 1);
            startActivity(i);
            return true;
        }

        if (itemId == R.id.menu_item_calendar) {
            if (isEventAlreadyAdded(getApplicationContext(), start.getTime(), stop.getTime())) {
                // Event already exists, no need to add it again
                Toast.makeText(getApplicationContext(), "עבודה זאת כבר נשמרה בלוח שנה בעבר", Toast.LENGTH_LONG).show();
            } else {
                // Check if the required permissions are granted
                if (hasCalendarPermissions()) {
                    // Permissions already granted, proceed with adding the event to the calendar
                    addEventToCalendar(getApplicationContext(), start.getTime(), stop.getTime());
                } else {
                    // Request permissions from the user
                    requestCalendarPermissions();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static long getDurationMillis(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }

    public static void addEventToCalendar(Context context, long startMillis, long endMillis) {
        // Create a new event in the calendar
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // ID of the calendar you want to add the event to
        values.put(CalendarContract.Events.TITLE, "עבודה");
        values.put(CalendarContract.Events.DESCRIPTION, "Event Description");
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        // Insert the event into the calendar
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        Toast.makeText(context.getApplicationContext(), "העבודה נשמרה בלוח שנה בהצלחה!", Toast.LENGTH_LONG).show();

        // Get the event ID (optional)
        long eventId = Long.parseLong(((Uri) uri).getLastPathSegment());
    }

    public static long breaking(long duration, int minutes) {
        // Calculate the duration of 6 hours in milliseconds
        long sixHours = 6 * 60 * 60 * 1000;
        // Calculate the duration of 6 hours plus additional minutes in milliseconds
        long sixHoursAndTimeBreak = 6 * 60 * 60 * 1000 + minutes * 60 * 1000;

        if (duration <= sixHoursAndTimeBreak && duration > sixHours) {
            // If the duration is within the break period, return the duration of six hours
            return sixHours;
        } else {
            // Subtract the additional minutes from the duration
            return duration - minutes * 60 * 1000;
        }
    }

    public static String formatDuration(long duration) {
        // Convert the duration to seconds
        long seconds = duration / 1000;
        // Convert the seconds to minutes
        long minutes = seconds / 60;
        // Convert the minutes to hours
        long hours = minutes / 60;
        // Convert the hours to days
        long days = hours / 24;

        String result = "";
        if (days > 0) {
            result += days + " ימים ";
        }
        if (hours % 24 > 0) {
            result += hours % 24 + " שעות ";
        }
        if (minutes % 60 > 0) {
            result += minutes % 60 + " דקות ";
        }
        if (result.equals("")) {
            // If no duration is present, set the result to "0 דקות"
            result = "0 דקות";
        }

        return result;
    }

    public static long calculateTime125p(long time, int num) {
        // Calculate the duration of 8 hours and 36 minutes in milliseconds
        long eightHoursThirtySixMins = 8 * 60 * 60 * 1000 + 36 * 60 * 1000;
        // Calculate the duration of 10 hours and 36 minutes in milliseconds
        long tenHoursThirtySixMins = 10 * 60 * 60 * 1000 + 36 * 60 * 1000;
        // Calculate the duration of 2 hours in milliseconds
        long twoHours = 2 * 60 * 60 * 1000;

        if (num == 5) {
            if (time < eightHoursThirtySixMins) {
                // If the time is less than 8 hours and 36 minutes, return 0
                return 0;
            } else if (time < tenHoursThirtySixMins) {
                // If the time is less than 10 hours and 36 minutes, subtract the base time from the time
                return time - eightHoursThirtySixMins;
            } else {
                // If the time exceeds 10 hours and 36 minutes, return the duration of two hours
                return twoHours;
            }
        } else if (num == 6) {
            if (time < 8 * 60 * 60 * 1000) {
                // If the time is less than 8 hours, return 0
                return 0;
            } else if (time < 10 * 60 * 60 * 1000) {
                // If the time is less than 10 hours, subtract the base time from the time
                return time - 8 * 60 * 60 * 1000;
            } else {
                // If the time exceeds 10 hours, return the duration of two hours
                return twoHours;
            }
        } else {
            // Handle invalid num value
            return -1;
        }
    }

    public static long calculateTime150p(long time, int num) {
        // Calculate the duration of 8 hours and 36 minutes in milliseconds
        long eightHoursThirtySixMins = 8 * 60 * 60 * 1000 + 36 * 60 * 1000;
        // Calculate the duration of 10 hours and 36 minutes in milliseconds
        long tenHoursThirtySixMins = 10 * 60 * 60 * 1000 + 36 * 60 * 1000;
        // Calculate the duration of 2 hours in milliseconds
        long twoHours = 2 * 60 * 60 * 1000;

        if (num == 5) {
            if (time < tenHoursThirtySixMins) {
                // If the time is less than 10 hours and 36 minutes, return 0
                return 0;
            } else {
                // Subtract the base time from the time
                return time - eightHoursThirtySixMins;
            }
        } else if (num == 6) {
            if (time < 10 * 60 * 60 * 1000) {
                // If the time is less than 10 hours, return 0
                return 0;
            } else {
                // Subtract the base time from the time
                return time - 10 * 60 * 60 * 1000;
            }
        } else {
            // Handle invalid num value
            return -1;
        }
    }

    public static double salaryDay(double numberHourlyWage, long duration, int num) {
        double salary = 0;

        // Calculate the time at 150% pay rate
        long time150p = calculateTime150p(duration, num);

        // Convert the time at 150% pay rate to seconds, minutes, and hours
        long secondsTime150p = time150p / 1000;
        long minutesTime150p = secondsTime150p / 60;
        long hoursTime150p = minutesTime150p / 60;

        // Calculate the time at 125% pay rate
        long time125p = calculateTime125p(duration, num);

        // Convert the time at 125% pay rate to seconds, minutes, and hours
        long secondsTime125p = time125p / 1000;
        long minutesTime125p = secondsTime125p / 60;
        long hoursTime125p = minutesTime125p / 60;

        // Calculate the basic time at regular pay rate
        long basic = duration - time150p - time125p;

        // Convert the basic time to seconds, minutes, and hours
        long secondsBasic = basic / 1000;
        long minutesBasic = secondsBasic / 60;
        long hoursBasic = minutesBasic / 60;

        // Calculate the salary based on the pay rates and durations
        salary += ((numberHourlyWage * 1.5 * (minutesTime150p % 60)) / 60.0);
        salary += (numberHourlyWage * 1.5 * hoursTime150p);
        salary += ((numberHourlyWage * 1.25 * (minutesTime125p % 60)) / 60.0);
        salary += (numberHourlyWage * 1.25 * hoursTime125p);
        salary += ((numberHourlyWage * (minutesBasic % 60)) / 60.0);
        salary += (numberHourlyWage * hoursBasic);

        return salary;
    }

    private void showDateTimeDialogStart() {
        final Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog to select the start date
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the selected date to the calendar object
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Create a TimePickerDialog to select the start time
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the selected time to the calendar object
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        // Save the selected start time to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("updateStart", calendar.getTime().getTime());
                        editor.commit();

                        // Retrieve the saved start time from SharedPreferences
                        long l = sharedPreferences.getLong("updateStart", 0);
                        dateAndTime = new Date(l);

                        // Check if the start time is after the stop time
                        if (dateAndTime.getTime() >= stop.getTime()) {
                            Toast.makeText(view.getContext(), "היציאה חייבת להיות אחרי הכניסה", Toast.LENGTH_LONG).show();
                        } else {
                            // Format the start date and display it in a TextView
                            String strDate = date.format(dateAndTime);
                            tvItemStart.setText(strDate);

                            // Retrieve the saved start time from SharedPreferences again
                            long l2 = sharedPreferences.getLong("updateStart", 0);
                            startUpdate = new Date(l2);

                            // Create a Work object with the updated start and stop times
                            Work work = new Work(id, startUpdate.getTime(), stop.getTime());

                            // Update the work entry in the database
                            worksDataBase.updateWork(work);

                            // Update the 'start' variable with the updated start time
                            start = startUpdate;

                            // Show the updated result
                            showResult();
                        }
                    }
                };

                // Show the TimePickerDialog to select the start time
                new TimePickerDialog(view.getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        // Show the DatePickerDialog to select the start date
        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showDateTimeDialogStop() {
        final Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog to select the stop date
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the selected date to the calendar object
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Create a TimePickerDialog to select the stop time
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the selected time to the calendar object
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        // Save the selected stop time to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("updateStop", calendar.getTime().getTime());
                        editor.commit();

                        // Retrieve the saved stop time from SharedPreferences
                        long l = sharedPreferences.getLong("updateStop", 0);
                        dateAndTime = new Date(l);

                        // Check if the start time is after the stop time
                        if (start.getTime() >= dateAndTime.getTime()) {
                            Toast.makeText(view.getContext(), "היציאה חייבת להיות אחרי הכניסה", Toast.LENGTH_LONG).show();
                        } else {
                            // Format the stop date and display it in a TextView
                            String strDate = date.format(dateAndTime);
                            tvItemStop.setText(strDate);

                            // Retrieve the saved stop time from SharedPreferences again
                            long l2 = sharedPreferences.getLong("updateStop", 0);
                            stopUpdate = new Date(l2);

                            // Create a Work object with the updated start and stop times
                            Work w = new Work(id, start.getTime(), stopUpdate.getTime());

                            // Update the work entry in the database
                            worksDataBase.updateWork(w);

                            // Update the 'stop' variable with the updated stop time
                            stop = stopUpdate;

                            // Show the updated result
                            showResult();
                        }
                    }
                };

                // Show the TimePickerDialog to select the stop time
                new TimePickerDialog(view.getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        // Show the DatePickerDialog to select the stop date
        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showResult() {
        // Calculate the duration between start and stop times
        duration = getDurationMillis(start, stop);

        // Display the duration in a TextView
        tvItemDurationWorking.setText(formatDuration(duration));

        // Check if the option to include breaks in the duration is enabled
        Boolean salaryOnBreaking = sharedPreferences.getBoolean("SalaryOnBreak", false);

        // Get the number of breaks selected by the user
        int numOfBreaking = sharedPreferences.getInt("numberSelectTimeOfBreak", 0);

        if (salaryOnBreaking == false) {
            // If breaks are not included in the duration
            if (duration > 6 * 60 * 60 * 1000) {
                // Exclude the breaks from the duration
                duration = breaking(duration, numOfBreaking);

                // Show the label for the duration including breaks
                tvTitleItemDurationWorkingIncludingBreaking.setText("משך עבודה עבורו קיבלתי שכר (כלומר ללא זמן ההפסקה): ");
                tvTitleItemDurationWorkingIncludingBreaking.setVisibility(View.VISIBLE);

                // Display the duration including breaks in a TextView
                tvItemDurationWorkingIncludingBreaking.setText(formatDuration(duration));
                tvItemDurationWorkingIncludingBreaking.setVisibility(View.VISIBLE);
            } else {
                // Hide the label and TextView for the duration including breaks
                tvTitleItemDurationWorkingIncludingBreaking.setVisibility(View.GONE);
                tvItemDurationWorkingIncludingBreaking.setVisibility(View.GONE);
            }
        }

        // Get the number of working days in a week
        int numOfDaysWeek = sharedPreferences.getInt("NumOfDaysWorking", 0);

        // Display the calculated duration for 125% and 150% overtime in TextViews
        tvItemMoreHours125.setText(formatDuration(calculateTime125p(duration, numOfDaysWeek)));
        tvItemMoreHours150.setText(formatDuration(calculateTime150p(duration, numOfDaysWeek)));

        // Get the hourly wage from SharedPreferences based on the selected salary option
        double numberHourlyWage;
        if (sharedPreferences.getBoolean("MinSalary", false)) {
            numberHourlyWage = sharedPreferences.getFloat("numberHourlyWageMinSalary", 0);
        } else {
            numberHourlyWage = sharedPreferences.getFloat("numberHourlyWageFloat", 0);
        }

        // Calculate the daily salary based on the hourly wage and duration
        double salaryDaily = salaryDay(numberHourlyWage, duration, numOfDaysWeek);

        // Display the daily salary in a TextView
        tvItemSalary.setText(decimalFormat.format(salaryDaily) + " שקלים חדשים ");

        // Save the calculated daily salary to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("salaryDaily", (float) salaryDaily);
        editor.commit();
    }

    private boolean hasCalendarPermissions() {
        // Check if the app has calendar permissions
        int readCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);

        // Return true if both read and write calendar permissions are granted
        return readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCalendarPermissions() {
        // Request calendar permissions from the user
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_CALENDAR,
                            Manifest.permission.WRITE_CALENDAR
                    },
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with adding the event to the calendar
                addEventToCalendar(getApplicationContext(), start.getTime(), stop.getTime());
            } else {
                // Permissions denied, show a message or handle the scenario accordingly
                Toast.makeText(this, "Calendar permissions denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static boolean isEventAlreadyAdded(Context context, long startMillis, long endMillis) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] projection = {CalendarContract.Events._ID};
        String selection = CalendarContract.Events.DTSTART + " = ? AND " +
                CalendarContract.Events.DTEND + " = ?";
        String[] selectionArgs = {String.valueOf(startMillis), String.valueOf(endMillis)};
        Cursor cursor = cr.query(uri, projection, selection, selectionArgs, null);

        boolean eventAlreadyAdded = false;
        if (cursor != null && cursor.moveToFirst()) {
            // An event with the same start and end time already exists
            eventAlreadyAdded = true;
            cursor.close();
        }

        return eventAlreadyAdded;
    }
}