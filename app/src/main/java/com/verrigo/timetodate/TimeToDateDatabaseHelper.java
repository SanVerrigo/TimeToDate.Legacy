package com.verrigo.timetodate;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by Verrigo on 29.07.2018.
 */

public class TimeToDateDatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "timeToDateDB";

    public static final String TABLE_NAME = "tableName";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DESCRIPTION = "description";


    private final String createCommand = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)",
            TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_DATE);
    private final String updateToSecondVersionCommand = String.format("ALTER TABLE %s ADD COLUMN %s TEXT", TABLE_NAME, COLUMN_DESCRIPTION);

    @SuppressLint("DefaultLocale")
    private String deleteByIdCommand (long _id){
        return String.format("delete from %s where %s='%d'", TimeToDateDatabaseHelper.TABLE_NAME, COLUMN_ID, _id);
    }

    public TimeToDateDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateTimeToDateDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateTimeToDateDatabase(db, oldVersion, newVersion);
    }

    private void updateTimeToDateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL(createCommand);
        }
        if (oldVersion < 2) {
            db.execSQL(updateToSecondVersionCommand);
        }

    }

    public void addNewTimeToDate(TimeToDate timeToDate) {
        ContentValues timeToDateValues = new ContentValues();
        timeToDateValues.put(COLUMN_NAME, timeToDate.getName());
        timeToDateValues.put(COLUMN_DATE, timeToDate.getDate());
        timeToDateValues.put(COLUMN_DESCRIPTION, timeToDate.getDescription());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, timeToDateValues);
    }

    public TimeToDate getTimeToDate(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("SELECT * FROM %s WHERE _id=%s", TABLE_NAME, id);
        Cursor cursor = db.rawQuery(query, null);
        TimeToDate receivedTimeToDate = new TimeToDate();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            receivedTimeToDate.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            receivedTimeToDate.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
            receivedTimeToDate.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        }
        return receivedTimeToDate;
    }

    public List<TimeToDate> dbParseListTimeToDates(){
        List<TimeToDate> timeToDates = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {COLUMN_ID, COLUMN_NAME, COLUMN_DATE, COLUMN_DESCRIPTION}, null, null, null, null, null);
        String date;
        String name;
        int _id;
        String description;
        if (cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            _id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
            timeToDates.add(new TimeToDate(name, date, description, _id));
            while (cursor.moveToNext()) {
                date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                _id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                timeToDates.add(new TimeToDate(name, date, description, _id));
            }
        }
        return timeToDates;
    }

    public void deleteTimeToDateRecord(long id, Context context) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deleteByIdCommand(id));
        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
    }


}
