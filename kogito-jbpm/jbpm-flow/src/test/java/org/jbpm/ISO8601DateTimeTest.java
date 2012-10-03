package org.jbpm;

import java.util.Date;

import org.drools.time.TimeUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.junit.Test;

public class ISO8601DateTimeTest {

    @Test
    public void test() {
        
        String delay = "2012-10-02T19:46:10";
        
        DateTime dt = ISODateTimeFormat.dateTimeParser().parseDateTime(delay);
        System.out.println(dt.toDate().getTime() - System.currentTimeMillis());
        
        System.out.println(dt.toDate());
        
        String duration = "PT5S";
        
        Period p = ISOPeriodFormat.standard().parsePeriod(duration);
        System.out.println(p.toStandardDuration().getMillis());
        
        System.out.println(new Date(System.currentTimeMillis() + p.toStandardDuration().getMillis()));
     
        String repeatable = "R5/2012-03-01T13:00:00/P10DT2H30M";
        
        if (repeatable.startsWith("R")) {
            String repeats = null;
            String delayIn = null;
            String periodIn = null;
            String[] elements = repeatable.split("/");
            if (elements.length==3) {
                repeats = elements[0].substring(1);
                delayIn = elements[1];
                periodIn = elements[2];
            } else {
                repeats = elements[0].substring(1);
                delayIn = new DateTime().toString();
                periodIn = elements[1];
            }
            DateTime startAtDelay =  ISODateTimeFormat.dateTimeParser().parseDateTime(delayIn);
            Period period = ISOPeriodFormat.standard().parsePeriod(periodIn);
            System.out.println(p.toStandardDuration().getMillis());
            System.out.println("Repeats " + repeats);
            System.out.println(startAtDelay.toDate());
            System.out.println(period.toStandardDuration().getStandardSeconds());
        }
        
    }

}
