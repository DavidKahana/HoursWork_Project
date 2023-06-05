package com.example.hourswork_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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

    Button btnItemDelete;
    TextView tvItemStart , tvItemStop , tvTitleItemDurationWorkingIncludingBreaking , tvItemDurationWorking , tvItemDurationWorkingIncludingBreaking , tvItemMoreHours125 , tvItemMoreHours150 , tvItemSalary ;
    WorksDataBase worksDataBase;
    Work work;
    int id ;
    long duration;
    Date startUpdate , stopUpdate , dateAndTime , start , stop;
    SharedPreferences sharedPreferences;
    SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy HH:mm" );
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_to_item);



        tvItemStart = findViewById(R.id.tvItemStart);
        tvItemStop = findViewById(R.id.tvItemStop);
        tvItemDurationWorking = findViewById(R.id.tvItemDurationWorking);
        tvItemDurationWorkingIncludingBreaking = findViewById(R.id.tvItemDurationWorkingIncludingBreaking);
        tvItemMoreHours125 = findViewById(R.id.tvItemMoreHours125);
        tvItemMoreHours150 = findViewById(R.id.tvItemMoreHours150);
        tvItemSalary = findViewById(R.id.tvItemSalary);
        tvTitleItemDurationWorkingIncludingBreaking = findViewById(R.id.tvTitleItemDurationWorkingIncludingBreaking);

        btnItemDelete = findViewById(R.id.btnItemDelete);

        sharedPreferences = getSharedPreferences("Definitions", 0);

        Intent intent=getIntent();

        id = intent.getIntExtra("id",0);

        Log.d("david", "id: "+ id);
        worksDataBase = new WorksDataBase(this);

        work = worksDataBase.getWorkById(id);

        Log.d("david", "onCreate: "+ work.toString());
        start = Calendar.getInstance().getTime();
        start.setTime(work.getStartDate());
        stop = Calendar.getInstance().getTime();
        stop.setTime(work.getEndDate());
        tvItemStart.setText( date.format(start));
        tvItemStop.setText( date.format(stop));


        showResult();


        tvItemStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    showDateTimeDialogStart();

            }
        });

        tvItemStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDateTimeDialogStop();

            }
        });


        btnItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                worksDataBase.deleteWork(id);

                Intent i = new Intent( ActionToItem.this , MainActivity.class);
                i.putExtra("state" , 1);
                startActivity(i);

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
            Date startDate = new Date(work.getStartDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            Intent intent = new Intent(this , MonthlySummary.class);
            intent.putExtra("year" , year);
            intent.putExtra("month" , month);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.menu_item_backToHoursReport) {

            Intent i = new Intent( ActionToItem.this , MainActivity.class);
            i.putExtra("state" , 1);
            startActivity(i);

            return true;
        }
        if (itemId == R.id.menu_item_calendar){

            if (isEventAlreadyAdded(getApplicationContext() , start.getTime() , stop.getTime())) {
                // Event already exists, no need to add it again
                Toast.makeText(getApplicationContext(),"עבודה זאת כבר נשמרה בלוח שנה בעבר",Toast.LENGTH_LONG).show();
            }
            else {
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

        Toast.makeText(context.getApplicationContext(), "העבודה נשמרה בלוח שנה בהצלחה!",Toast.LENGTH_LONG).show();
        // Get the event ID (optional)
        long eventId = Long.parseLong(((Uri) uri).getLastPathSegment());
    }

    public static long breaking (long duration , int minutes){
        long sixHours = 6 * 60 * 60 * 1000;
        long sixHoursAndTimeBreak =6 * 60 * 60 * 1000 + minutes * 60 * 1000;

        if (duration <= sixHoursAndTimeBreak && duration > sixHours){
            return sixHours;
        }
        else{
            return duration - minutes * 60 * 1000;
        }
    }

    public static String formatDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String result = "";
        if (days > 0) {
            result += days + " ימים ";
        }
        if (hours%24 > 0) {
            result += hours % 24 + " שעות ";
        }
        if (minutes%60 > 0) {
            result += minutes % 60 + " דקות ";
        }
        if (result.equals("")) {
            result = "0 דקות";
        }

        return result;
    }

    public static long calculateTime125p(long time, int num) {
        long eightHoursThirtySixMins = 8 * 60 * 60 * 1000 + 36 * 60 * 1000;
        long tenHoursThirtySixMins = 10 * 60 * 60 * 1000 + 36 * 60 * 1000;
        long twoHours = 2 * 60 * 60 * 1000;

        if (num == 5) {
            if (time < eightHoursThirtySixMins) {
                return 0;
            } else if (time < tenHoursThirtySixMins) {
                return time - eightHoursThirtySixMins;
            } else {
                return twoHours;
            }
        } else if (num == 6) {
            if (time < 8 * 60 * 60 * 1000) {
                return 0;
            } else if (time < 10 * 60 * 60 * 1000) {
                return time - 8 * 60 * 60 * 1000;
            } else {
                return twoHours;
            }
        } else {
            // handle invalid num value
            return -1;
        }

        }

    public static long calculateTime150p(long time, int num) {
        long eightHoursThirtySixMins = 8 * 60 * 60 * 1000 + 36 * 60 * 1000;
        long tenHoursThirtySixMins = 10 * 60 * 60 * 1000 + 36 * 60 * 1000;
        long twoHours = 2 * 60 * 60 * 1000;

        if (num == 5) {
            if (time < tenHoursThirtySixMins) {
                return 0;
            }
        else {
                return time - eightHoursThirtySixMins;
        }
        }
        else if (num == 6) {
             if (time < 10 * 60 * 60 * 1000) {
                return 0;
            }
             else {
                return time - 10 * 60 * 60 * 1000 ;
            }
        } else {
            // handle invalid num value
            return -1;
        }

    }

    public static double salaryDay(double numberHourlyWage , long duration , int num){
        double salary = 0 ;

        long time150p = calculateTime150p(duration , num);

        long secondsTime150p = time150p / 1000;
        long minutesTime150p = secondsTime150p / 60;
        long hoursTime150p = minutesTime150p / 60;

        long time125p = calculateTime125p(duration , num);

        long secondsTime125p = time125p / 1000;
        long minutesTime125p = secondsTime125p / 60;
        long hoursTime125p = minutesTime125p / 60;

        long basic = duration - time150p - time125p;

        long secondsBasic = basic / 1000;
        long minutesBasic= secondsBasic/ 60;
        long hoursBasic = minutesBasic / 60;


        salary += ((numberHourlyWage * 1.5 * (minutesTime150p%60)) / 60.0 );
        salary += (numberHourlyWage * 1.5 * hoursTime150p );
        salary += ((numberHourlyWage * 1.25 * (minutesTime125p%60)) / 60.0 );
        salary += (numberHourlyWage * 1.25 * hoursTime125p);
        salary += ((numberHourlyWage * (minutesBasic%60)) / 60.0 );
        salary += (numberHourlyWage * hoursBasic);



        return salary;
    }
    private void showDateTimeDialogStart() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);



                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(calendar.SECOND ,0);
                        calendar.set(calendar.MILLISECOND , 0);


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("updateStart" , calendar.getTime().getTime());
                        editor.commit();

                        long l = sharedPreferences.getLong("updateStart" , 0);
                        dateAndTime = new Date(l);

                        if (dateAndTime.getTime()>= stop.getTime()){
                            Toast.makeText(view.getContext(), "היציאה חייבת להיות אחרי הכניסה", Toast.LENGTH_LONG).show();
                        }
                        else {

                            String strDate = date.format(dateAndTime);
                            String str = "You selected :" + strDate;
                            Toast.makeText(view.getContext(), str, Toast.LENGTH_LONG).show();
                            tvItemStart.setText(strDate);

                            long l2 = sharedPreferences.getLong("updateStart", 0);
                            startUpdate = new Date(l2);

                            Work work = new Work(id, startUpdate.getTime(), stop.getTime());
                            worksDataBase.updateWork(work);
                            start = startUpdate;
                            showResult();
                        }
                    }
                };

                new TimePickerDialog(view.getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void showDateTimeDialogStop() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);



                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(calendar.SECOND ,0);
                        calendar.set(calendar.MILLISECOND , 0);


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("updateStop" , calendar.getTime().getTime());
                        editor.commit();

                        long l = sharedPreferences.getLong("updateStop" , 0);
                        dateAndTime = new Date(l);

                        if (start.getTime()>= dateAndTime.getTime()){
                            Toast.makeText(view.getContext(), "היציאה חייבת להיות אחרי הכניסה", Toast.LENGTH_LONG).show();
                        }
                        else {
                        String strDate = date.format(dateAndTime);
                        String str = "You selected :" + strDate;
                        Toast.makeText(view.getContext(),str,Toast.LENGTH_LONG).show();
                        tvItemStop.setText(strDate);

                        long l2 = sharedPreferences.getLong("updateStop", 0);
                        stopUpdate = new Date(l2);

                        Work w = new Work(id, start.getTime(), stopUpdate.getTime());
                        worksDataBase.updateWork(w);
                        stop = stopUpdate;
                        showResult();
                        }
                    }
                };

                new TimePickerDialog(view.getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void showResult(){
        duration = getDurationMillis(start ,stop );

        tvItemDurationWorking.setText( formatDuration(duration));

        Boolean salaryOnBreaking = sharedPreferences.getBoolean("SalaryOnBreak" , false);
        int numOfBreaking = sharedPreferences.getInt("numberSelectTimeOfBreak" , 0);
        if (salaryOnBreaking == false){
            if (duration > 6 * 60 * 60 * 1000){
                duration = breaking(duration , numOfBreaking);
                tvTitleItemDurationWorkingIncludingBreaking.setText("משך עבודה עבורו קיבלתי שכר (כלומר ללא זמן ההפסקה): " );
                tvTitleItemDurationWorkingIncludingBreaking.setVisibility(View.VISIBLE);
                tvItemDurationWorkingIncludingBreaking.setText(formatDuration(duration));
                tvItemDurationWorkingIncludingBreaking.setVisibility(View.VISIBLE);
            }
            else{
                tvTitleItemDurationWorkingIncludingBreaking.setVisibility(View.GONE);
                tvItemDurationWorkingIncludingBreaking.setVisibility(View.GONE);
            }
        }

        int numOfDaysWeek = sharedPreferences.getInt("NumOfDaysWorking" , 0) ;
        Log.d("mmm", "m: "+ numOfDaysWeek);
        tvItemMoreHours125.setText( formatDuration(calculateTime125p(duration , numOfDaysWeek)));
        tvItemMoreHours150.setText( formatDuration(calculateTime150p(duration , numOfDaysWeek)));

        double numberHourlyWage;
        if (sharedPreferences.getBoolean("MinSalary" , false)){
            numberHourlyWage = sharedPreferences.getFloat("numberHourlyWageMinSalary" , 0);
        }
        else{
            numberHourlyWage = sharedPreferences.getFloat("numberHourlyWageFloat", 0);
        }
        double salaryDaily = salaryDay(numberHourlyWage , duration , numOfDaysWeek);

        tvItemSalary.setText(decimalFormat.format(salaryDaily) + " שקלים חדשים ");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("salaryDaily" , (float) salaryDaily);
        editor.commit();
    }


    private boolean hasCalendarPermissions() {
        int readCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);

        return readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCalendarPermissions() {
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