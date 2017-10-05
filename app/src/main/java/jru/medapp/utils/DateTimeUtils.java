package jru.medapp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    private static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    private static final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");

    public static String toReadable(Date dateToConvert) {
        String convertedDate;
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd", Locale.US);
            convertedDate = formatter.format(dateToConvert);
        } catch (NullPointerException e) {
            e.printStackTrace();
            convertedDate = "";
        }

        return convertedDate.toUpperCase();
    }

    public static String toAMPM(Date dateToConvert) {
        String convertedDate;
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.US);
            convertedDate = formatter.format(dateToConvert);
        } catch (NullPointerException e) {
            e.printStackTrace();
            convertedDate = "";
        }

        return convertedDate.toUpperCase();
    }

    public static String toDuration(Date date) {
        try {
            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(date); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, 8); // adds 8 hour
            // returns new date object, one hour in the future
            Date mDate = cal.getTime();
            Date currentDate = new Date();
            long duration = currentDate.getTime() - mDate.getTime();

            StringBuilder res = new StringBuilder();
            for (int i = 0; i < times.size(); i++) {
                Long current = times.get(i);
                long temp = duration / current;
                if (temp > 0) {
                    res.append(temp).append(" ").append(timesString.get(i)).append(temp > 1 ? "s" : "").append(" ago");
                    break;
                }
            }
            if ("".equals(res.toString()))
                return "just a moment ago";
            else
                return res.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "unable to parse data";
        }
    }

    public static String getLongDateTimeString(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(date);
    }

    public static String TO_HH_MM_SS(String dateToConvert) {

        DateFormat f1 = new SimpleDateFormat("h:mm a", Locale.US);
        String convertedDate = "";
        try {
            Date date = f1.parse(dateToConvert);
            SimpleDateFormat am_pm = new SimpleDateFormat("HH:mm:ss", Locale.US);
            convertedDate = am_pm.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    public static String TO_AM_PM(String dateToConvert) {

        DateFormat f1 = new SimpleDateFormat("HH:mm:ss", Locale.US);
        String convertedDate = "";
        try {
            Date date = f1.parse(dateToConvert);
            SimpleDateFormat am_pm = new SimpleDateFormat("h:mm a", Locale.US);
            convertedDate = am_pm.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    public static Date String_To_Time(String dateToConvert) {

        DateFormat f1 = new SimpleDateFormat("HH:mm:ss", Locale.US);
        Date convertedDate = null;
        try {
            convertedDate = f1.parse(dateToConvert);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    public static Date StrToDate(String dateToConvert) {

        DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date convertedDate = null;
        try {
            convertedDate = f1.parse(dateToConvert);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    public static String DateToStrYYYY(Date date) {

        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
    }

    public static Date StrToDateYYYY(String dateToConvert) {

        DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date convertedDate = null;
        try {
            convertedDate = f1.parse(dateToConvert);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    public static String dateToday() {
        Date date = new Date();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
    }

    public static String dateTodayToast() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = sdfs.format(calendar.getTime()) + " 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date converted = null;
        try {
            converted = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(converted);
    }

    public static Date getDateToday() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = sdfs.format(calendar.getTime()) + " 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        ;
        Date converted = null;
        try {
            converted = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return converted;
    }

    public static Date getDateTodayEnd() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = sdfs.format(calendar.getTime()) + " 23:59:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date converted = null;
        try {
            converted = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return converted;
    }


    public static Long hoursDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();//as given

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long millis = TimeUnit.MILLISECONDS.toMillis(diff);

        return millis;
    }

}