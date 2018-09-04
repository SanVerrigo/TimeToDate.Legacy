package com.verrigo.timetodate;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String TIME_FORMAT = "HH:mm";


    public boolean isDeletingMode() {
        return isDeletingMode;
    }

    public void setDeletingMode(boolean deletingMode) {
        isDeletingMode = deletingMode;
    }

    private boolean isDeletingMode = false;
    private boolean isExpanded = false;
    private int _id = 0;
    private String name;
    private String date;
    private String description;


    TimeToDate() {
    }

    TimeToDate(String name, String date, String description) {
        this.description = description;
        this.name = name;
        this.date = date;
    }
    TimeToDate(String name, String date, String description, int _id) {
        this.description = description;
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

    // change this part
    public static String currentLeftTime(String dateTime) {
        //start of the new code
        String[] dateTimeList = dateTime.split(" ");
        String rawDate = dateTimeList[0];
        String rawTime = dateTimeList[1];

        DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);
        LocalDate date = LocalDate.parse(rawDate, formatter);
        LocalTime time = LocalTime.parse(rawTime);

        int dayOfDate = date.getDayOfMonth();
        int monthOfDate = date.getMonthOfYear();
        int yearOfDate = date.getYear();
        int hoursOfDate = time.getHourOfDay();
        int minsOfDate = time.getMinuteOfHour();

        DateTime dateTime1 = new DateTime(yearOfDate, monthOfDate, dayOfDate, hoursOfDate, minsOfDate);


        long startInMillis = System.currentTimeMillis();
        long endInMillis = dateTime1.getMillis() - (3 * 60 * 60 * 1000);



        //end of the new code

//        SimpleDateFormat ft = new SimpleDateFormat(format);
//        Date parsingDate = null;
//        try {
//            parsingDate = ft.parse(dateTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        long startInMills = System.currentTimeMillis();
//        long endInMills = parsingDate.getTime();
//
        long secondsToDate = (endInMillis - startInMillis) / 1000;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
