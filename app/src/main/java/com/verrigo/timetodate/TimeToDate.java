package com.verrigo.timetodate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Verrigo on 29.07.2018.
 */

public class TimeToDate {

    private final static int SECS_IN_MINUTE = 60;
    private final static int MINUTES_IN_HOUR = 60;
    private final static int HOURS_IN_DAY = 24;


    public final static String format = "yyyy-MM-dd-kk";

    private int _id = 0;
    private String name;
    private String date;

    TimeToDate() {
    }

    TimeToDate(String name, String date) {
        this.name = name;
        this.date = date;
    }
    TimeToDate(String name, String date, int _id) {
        this._id = _id;
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static String currentLeftTime(String dateTime) {
        SimpleDateFormat ft = new SimpleDateFormat(format);
        Date parsingDate = null;
        try {
            parsingDate = ft.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startInMills = System.currentTimeMillis();
        long endInMills = parsingDate.getTime();

        long secondsToDate = (endInMills - startInMills) / 1000;
        if (secondsToDate > 0) {
            int days = (int) (secondsToDate / (SECS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY));
            int reminderOfDayInSecs = (int) (secondsToDate - (days * SECS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY));
            int hours = reminderOfDayInSecs / (SECS_IN_MINUTE * MINUTES_IN_HOUR);
            int mins = (reminderOfDayInSecs % (SECS_IN_MINUTE * MINUTES_IN_HOUR)) / SECS_IN_MINUTE;
            int secs = reminderOfDayInSecs % SECS_IN_MINUTE;
            if (days == 0) {
                return String.format("%02d:%02d:%02d", hours, mins, secs);
            } else {
                return String.format("%d д. и %02d:%02d:%02d", days, hours, mins, secs);
            }
        } else {
            return "Данное событие уже наступило!";
        }
    }

    public int get_id() {
        return _id;
    }
}
