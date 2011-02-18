package org.drools.integrationtests.eventgenerator.example;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Tools {

    // Utitlity functions for console output
    public static String formattedDate (Calendar date){
        return
            ((date.get(Calendar.HOUR_OF_DAY) < 10)?  "0" : "") + date.get(Calendar.HOUR_OF_DAY) + ":" +
            ((date.get(Calendar.MINUTE) < 10)?       "0" : "") + date.get(Calendar.MINUTE)      + ":" +
            ((date.get(Calendar.SECOND) < 10)?       "0" : "") + date.get(Calendar.SECOND) + "." +
            ((date.get(Calendar.MILLISECOND) < 10)? "0" : "") + ((date.get(Calendar.MILLISECOND) < 100)? "0" : "") + date.get(Calendar.MILLISECOND);
    }

    public static String formattedDate (long dateInMillis){
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(dateInMillis);
        return formattedDate(date);
    }

    public static String formattedInterval(Calendar start, Calendar end){
        return "["+Tools.formattedDate(start)+".."+Tools.formattedDate(end)+"]";
    }

    public static String formattedInterval(long start, long end){
        return "["+Tools.formattedDate(start)+".."+Tools.formattedDate(end)+"]";
    }

    public static void drawLine(){
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
    }
}
