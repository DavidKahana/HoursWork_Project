package com.example.hourswork_project;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WorksDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "work.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "work";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";




    public WorksDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_START_DATE + " BIGINT NOT NULL, " +
                COLUMN_END_DATE + " BIGINT NOT NULL);";
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addWork(Work work) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_START_DATE, work.getStartDate());
        values.put(COLUMN_END_DATE, work.getEndDate());

        db.insert(TABLE_NAME, null, values);
        db.close();


    }



    public List<Work> getAllWorks() {
        List<Work> works = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int startDateIndex = cursor.getColumnIndex(COLUMN_START_DATE);
                int endDateIndex = cursor.getColumnIndex(COLUMN_END_DATE);

                if (idIndex >= 0 && startDateIndex >= 0 && endDateIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    long startDate = cursor.getLong(startDateIndex);
                    long endDate = cursor.getLong(endDateIndex);

                    Work work = new Work(id, endDate, startDate);
                    works.add(work);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return works;
    }

    public Work getWorkById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " WHERE id='" + id + "'", null);
        Work work = null;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long start = cursor.getLong(1);
                long end = cursor.getLong(2);
                work = new Work(id, start, end);
            }
            while (cursor.moveToNext());
        }
        return work;
    }


    public void updateWork(Work work) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_START_DATE, work.getStartDate());
        values.put(COLUMN_END_DATE, work.getEndDate());

        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(work.getId())});
        db.close();
    }




    public void deleteWork(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[] { String.valueOf(id) });

        db.close();
    }

    public List<Work> getWorksByMonth(int month) {
        List<Work> works = new ArrayList<>();

        // Get the start and end timestamps for the specified month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        long endOfMonth = calendar.getTimeInMillis();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_START_DATE + " >= " + startOfMonth + " AND " + COLUMN_START_DATE + " <= " + endOfMonth;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int startDateIndex = cursor.getColumnIndex(COLUMN_START_DATE);
                int endDateIndex = cursor.getColumnIndex(COLUMN_END_DATE);

                if (idIndex >= 0 && startDateIndex >= 0 && endDateIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    long startDate = cursor.getLong(startDateIndex);
                    long endDate = cursor.getLong(endDateIndex);

                    Work work = new Work(id, endDate, startDate);
                    works.add(work);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return works;
    }

    public List<Work> getWorksByMonthAndYear(int month, int year) {
        List<Work> works = new ArrayList<>();

        // Get the start and end timestamps for the specified month and year
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        long endOfMonth = calendar.getTimeInMillis();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_START_DATE + " >= " + startOfMonth + " AND " + COLUMN_START_DATE + " <= " + endOfMonth;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int startDateIndex = cursor.getColumnIndex(COLUMN_START_DATE);
                int endDateIndex = cursor.getColumnIndex(COLUMN_END_DATE);

                if (idIndex >= 0 && startDateIndex >= 0 && endDateIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    long startDate = cursor.getLong(startDateIndex);
                    long endDate = cursor.getLong(endDateIndex);

                    Work work = new Work(id, endDate, startDate);
                    works.add(work);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return works;
    }


    public int[] getDaysInEachMonth(int year) {
        int[] daysInEachMonth = new int[12]; // Array to hold days in each month (January is at index 0)

        // Get the unique start days for all members
        List<Long> uniqueStartDays = getUniqueStartDays(year);

        // Calculate the number of days in each month
        Calendar calendar = Calendar.getInstance();
        for (Long startDay : uniqueStartDays) {
            calendar.setTimeInMillis(startDay);

            int month = calendar.get(Calendar.MONTH);

            daysInEachMonth[month]++;
        }

        return daysInEachMonth;
    }

    private List<Long> getUniqueStartDays() {
        List<Long> uniqueStartDays = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT " + COLUMN_START_DATE + " FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int startDateIndex = cursor.getColumnIndex(COLUMN_START_DATE);
                if (startDateIndex >= 0) {
                    long startDate = cursor.getLong(startDateIndex);
                    uniqueStartDays.add(getStartOfDay(startDate));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return uniqueStartDays;
    }
    private List<Long> getUniqueStartDays(int year) {
        List<Long> uniqueStartDays = new ArrayList<>();

        // Constructing the query with the WHERE clause to filter by year
        String selectQuery = "SELECT DISTINCT " + COLUMN_START_DATE + " FROM " + TABLE_NAME +
                " WHERE strftime('%Y', " + COLUMN_START_DATE + "/1000, 'unixepoch') = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(year)});

        if (cursor.moveToFirst()) {
            do {
                int startDateIndex = cursor.getColumnIndex(COLUMN_START_DATE);
                if (startDateIndex >= 0) {
                    long startDate = cursor.getLong(startDateIndex);
                    uniqueStartDays.add(getStartOfDay(startDate));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return uniqueStartDays;
    }


    private long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


}
