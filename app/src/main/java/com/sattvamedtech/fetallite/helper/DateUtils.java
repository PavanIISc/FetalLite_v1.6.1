package com.sattvamedtech.fetallite.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static String convertDateToShortHumanReadable(long iTimeInMillis) {
        String finalDate = null;
        try {
            Date myDate = new Date(iTimeInMillis);
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            timeFormat.setTimeZone(TimeZone.getDefault());
            finalDate = timeFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    public static String convertDateToLongHumanReadable(long iTimeInMillis) {
        String finalDate = null;
        try {
            Date myDate = new Date(iTimeInMillis);
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            timeFormat.setTimeZone(TimeZone.getDefault());
            finalDate = timeFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    public static String convertDateToShortHumanReadable(Date iDate) {
        String finalDate = null;
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            timeFormat.setTimeZone(TimeZone.getDefault());
            finalDate = timeFormat.format(iDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    public static String convertTimeToHumanReadable(long iTimeInMillis) {
        String finalDate = null;
        try {
            Date myDate = new Date(iTimeInMillis);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm aa", Locale.getDefault());
            timeFormat.setTimeZone(TimeZone.getDefault());
            finalDate = timeFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    public static int getAge(long iTimeInMillis) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(iTimeInMillis);
        Calendar endDate = Calendar.getInstance();

        return endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR);
    }
}
