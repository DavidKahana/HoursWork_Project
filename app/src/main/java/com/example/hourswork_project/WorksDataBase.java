package com.example.hourswork_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
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
                COLUMN_START_DATE + " INTEGER NOT NULL, " +
                COLUMN_END_DATE + " INTEGER NOT NULL);";
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Implement upgrade strategy
    }

    public void addWork(Work work) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_START_DATE, work.getStartDate());
        values.put(COLUMN_END_DATE, work.getEndDate());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteWork(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[] { String.valueOf(id) });
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

                    Work work = new Work(endDate, startDate,id );
                    works.add(work);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return works;
    }



    public void updateWork(Work work) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_START_DATE, work.getStartDate());
        values.put(COLUMN_END_DATE, work.getEndDate());

        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[] { String.valueOf(work.getId()) });
        db.close();
    }
}
