package com.example.hourswork_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthlySummary extends AppCompatActivity {

    // Declaring variables for TextViews, SharedPreferences, database, and calculation values
    TextView tvMonthNameOfMonth, tvMonthNumOfDays_answer, tvMonthTotalHours_answer, tvMonthTotalHours100p_answer,
            tvMonthTotalHours125p_answer, tvMonthTotalHours150p_answer, tvMonthTotalsalary_answer,
            tvMonthTotalsalaryDetail_answer;
    SharedPreferences sharedPreferences;
    int numMonth, numYear;
    WorksDataBase worksDataBase;
    double salaryTotal, salaryDay, travelsDay, travelsMonth;
    long durationToatalMonth = 0, durationToatal125pMonth = 0, durationToatal150pMonth = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int CREATE_FILE_REQUEST_CODE = 2;
    private Uri outputFileUri;
    List<Work> works;
    SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_summary);

        // Initializing TextViews
        tvMonthNameOfMonth = findViewById(R.id.tvMonthNameOfMonth);
        tvMonthNumOfDays_answer = findViewById(R.id.tvMonthNumOfDays_answer);
        tvMonthTotalHours_answer = findViewById(R.id.tvMonthTotalHours_answer);
        tvMonthTotalHours100p_answer = findViewById(R.id.tvMonthTotalHours100p_answer);
        tvMonthTotalHours125p_answer = findViewById(R.id.tvMonthTotalHours125p_answer);
        tvMonthTotalHours150p_answer = findViewById(R.id.tvMonthTotalHours150p_answer);
        tvMonthTotalsalary_answer = findViewById(R.id.tvMonthTotalsalary_answer);
        tvMonthTotalsalaryDetail_answer = findViewById(R.id.tvMonthTotalsalaryDetail_answer);

        // Initializing SharedPreferences
        sharedPreferences = getSharedPreferences("Definitions", 0);

        // Getting month and year values from Intent
        Intent intent = getIntent();
        numYear = intent.getIntExtra("year", 0);
        numMonth = intent.getIntExtra("month", 0);

        // Initializing WorksDataBase
        worksDataBase = new WorksDataBase(this);

        // Retrieving number of days in the selected month
        int[] daysInEachMonth = worksDataBase.getDaysInEachMonth(numYear);

        // Setting month name and year to TextView
        tvMonthNameOfMonth.setText("חודש: " + getMonthName(numMonth) + " " + numYear);
        // Setting the number of days in the selected month to TextView
        tvMonthNumOfDays_answer.setText(daysInEachMonth[numMonth - 1] + " עבודות");

        // Retrieving works for the selected month and year
        works = worksDataBase.getWorksByMonthAndYear(numMonth, numYear);

        // Calculating total duration of works for the selected month
        for (Work work : works) {
            long start = work.getStartDate();
            long end = work.getEndDate();
            Date startDate = new Date(start);
            Date endDate = new Date(end);
            long time = getDurationMillis(endDate, startDate);
            durationToatalMonth = durationToatalMonth + time;
        }

        // Displaying the total duration of works for the selected month
        tvMonthTotalHours_answer.setText(formatDuration(durationToatalMonth));

        // Calculating durations and salary for each work in the selected month
        for (Work work : works) {
            long start = work.getStartDate();
            long end = work.getEndDate();
            Date startDate = new Date(start);
            Date endDate = new Date(end);
            long timeOfDuration = getDurationMillis(endDate, startDate);

            // Checking if salary should be calculated during breaks
            Boolean salaryOnBreaking = sharedPreferences.getBoolean("SalaryOnBreak", false);
            int numOfBreaking = sharedPreferences.getInt("numberSelectTimeOfBreak", 0);

            if (salaryOnBreaking == false) {
                if (timeOfDuration > 6 * 60 * 60 * 1000) {
                    timeOfDuration = breaking(timeOfDuration, numOfBreaking);
                }
            }

            int numOfDaysWeek = sharedPreferences.getInt("NumOfDaysWorking", 0);

            // Calculating durations at 125% and 150% of the regular rate
            durationToatal125pMonth = durationToatal125pMonth + calculateTime125p(timeOfDuration, numOfDaysWeek);
            durationToatal150pMonth = durationToatal150pMonth + calculateTime150p(timeOfDuration, numOfDaysWeek);

            // Calculating salary for the work
            double numberHourlyWage;
            if (sharedPreferences.getBoolean("MinSalary", false)) {
                numberHourlyWage = sharedPreferences.getFloat("numberHourlyWageMinSalary", 0);
            } else {
                numberHourlyWage = sharedPreferences.getFloat("numberHourlyWageFloat", 0);
            }
            salaryDay = salaryDay(numberHourlyWage, timeOfDuration, numOfDaysWeek);
            salaryTotal = salaryTotal + salaryDay;
        }

        // Displaying durations at 125% and 150% of the regular rate
        tvMonthTotalHours125p_answer.setText(formatDuration(durationToatal125pMonth));
        tvMonthTotalHours150p_answer.setText(formatDuration(durationToatal150pMonth));
        tvMonthTotalHours100p_answer.setText(formatDuration(durationToatalMonth - durationToatal150pMonth - durationToatal125pMonth));

        // Retrieving travel expenses for each day and calculating the total for the month
        travelsDay = sharedPreferences.getFloat("numberTravelExpenses", 0);
        travelsMonth = worksDataBase.getUniqueWorkDaysInMonthAndYear(numMonth , numYear) * travelsDay;

        // Displaying salary details and total salary for the month
        tvMonthTotalsalaryDetail_answer.setText("עבודה: " + decimalFormat.format(salaryTotal) +
                " שקלים חדשים " + "\n" + "נסיעות: " + decimalFormat.format(travelsMonth) + " שקלים חדשים ");

        salaryTotal = salaryTotal + travelsMonth;

        tvMonthTotalsalary_answer.setText(decimalFormat.format(salaryTotal) + " שקלים חדשים ");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.month, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_exel) {

            checkPermissionsAndExport();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void checkPermissionsAndExport() {
        // Check if storage permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        PERMISSION_REQUEST_CODE
                );
            } else {
                // Permission already granted, start SAF file picker
                startFilePicker();
            }
        } else {
            // For older devices, start SAF file picker without checking permissions
            startFilePicker();
        }
    }

    private void startFilePicker() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_TITLE, "exported_file.xlsx");
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }

    private void exportToExcel(List<Work> works) {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Work Data");

        // Create headers
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("מספר מזהה של המשמרת");
        headerRow.createCell(1).setCellValue("תאריך כניסה");
        headerRow.createCell(2).setCellValue("תאריך סיום");
        headerRow.createCell(3).setCellValue("סך הכל");

        // Write work data to the sheet
        for (int i = 0; i < works.size(); i++) {
            Work work = works.get(i);
            Row dataRow = sheet.createRow(i + 1);
            Date dateEnter = new Date(work.getEndDate());
            Date dateFinish = new Date(work.getStartDate());
            dataRow.createCell(0).setCellValue(work.getId());
            dataRow.createCell(1).setCellValue(date.format(dateEnter));
            dataRow.createCell(2).setCellValue(date.format(dateFinish));
            String str = formatDuration(getDurationMillis(dateEnter , dateFinish));
            dataRow.createCell(3).setCellValue(str);
        }
        // Save the workbook to the selected file
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(outputFileUri);
            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(this, "Excel file exported successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export Excel file!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start SAF file picker
                startFilePicker();
            } else {
                Toast.makeText(this, "Storage permission required to export the Excel file!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                outputFileUri = data.getData();
                exportToExcel(works);
            }
        }
    }









    // Returns the name of the month based on the month number
    public String getMonthName(int monthNumber) {
        String monthName = "";

        if (monthNumber >= 1 && monthNumber <= 12) {
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] monthNames = dfs.getMonths();
            monthName = monthNames[monthNumber - 1];
        }

        return monthName;
    }

    // Calculates the duration in milliseconds between two dates
    public static long getDurationMillis(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }

    // Adjusts the duration by subtracting the break time
    public static long breaking(long duration, int minutes) {
        long sixHours = 6 * 60 * 60 * 1000;
        long sixHoursAndTimeBreak = 6 * 60 * 60 * 1000 + minutes * 60 * 1000;

        if (duration <= sixHoursAndTimeBreak && duration > sixHours) {
            return sixHours;
        } else {
            return duration - minutes * 60 * 1000;
        }
    }

    // Formats the duration in milliseconds to a string representation
    public static String formatDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
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
            result = "0 דקות";
        }

        return result;
    }

    // Calculates the duration at 125% of the regular rate
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

    // Calculates the duration at 150% of the regular rate
    public static long calculateTime150p(long time, int num) {
        long eightHoursThirtySixMins = 8 * 60 * 60 * 1000 + 36 * 60 * 1000;
        long tenHoursThirtySixMins = 10 * 60 * 60 * 1000 + 36 * 60 * 1000;

        if (num == 5) {
            if (time < tenHoursThirtySixMins) {
                return 0;
            } else {
                return time - eightHoursThirtySixMins;
            }
        } else if (num == 6) {
            if (time < 10 * 60 * 60 * 1000) {
                return 0;
            } else {
                return time - 10 * 60 * 60 * 1000;
            }
        } else {
            // handle invalid num value
            return -1;
        }
    }

    // Calculates the daily salary based on the hourly wage, duration, and number of working days
    public static double salaryDay(double numberHourlyWage, long duration, int num) {
        double salary = 0;

        long time150p = calculateTime150p(duration, num);
        long secondsTime150p = time150p / 1000;
        long minutesTime150p = secondsTime150p / 60;
        long hoursTime150p = minutesTime150p / 60;

        long time125p = calculateTime125p(duration, num);
        long secondsTime125p = time125p / 1000;
        long minutesTime125p = secondsTime125p / 60;
        long hoursTime125p = minutesTime125p / 60;

        long time100p = duration - time150p - time125p;
        long secondsTime100p = time100p / 1000;
        long minutesTime100p = secondsTime100p / 60;
        long hoursTime100p = minutesTime100p / 60;

        double salary100p = numberHourlyWage * hoursTime100p + numberHourlyWage * minutesTime100p / 60;
        double salary125p = numberHourlyWage * 1.25 * hoursTime125p + numberHourlyWage * 1.25 * minutesTime125p / 60;
        double salary150p = numberHourlyWage * 1.5 * hoursTime150p + numberHourlyWage * 1.5 * minutesTime150p / 60;

        salary = salary100p + salary125p + salary150p;

        return salary;
    }
}
