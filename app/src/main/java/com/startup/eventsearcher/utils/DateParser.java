package com.startup.eventsearcher.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateParser {

    private static final String TAG = "tgDateParser";

    public static Date parseDate(String date, String time) {
        String concatString = date + "T" + time + "Z";
        try {
            return new SimpleDateFormat("dd.MM.yyyy'T'HH:mm'Z'", Locale.getDefault()).parse(concatString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getDateFormatDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getDateFormatMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
    }

    public static String getDateFormatTime(Date date) {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return localDateFormat.format(date);
    }

    public static String getDateFormatDate(Date date) {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return localDateFormat.format(date);
    }

    public static Date getDateWithMinusHours(Date date, int hours){
        Log.d(TAG, "getDateWithMinusHours: date = " + date.toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -hours);
        Date returnDate = calendar.getTime();

        Log.d(TAG, "getDateWithMinusHours: returnDate = " + returnDate.toString());
        return returnDate;
    }
}
