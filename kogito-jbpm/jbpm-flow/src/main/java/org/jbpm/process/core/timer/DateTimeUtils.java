package org.jbpm.process.core.timer;

import org.drools.core.time.TimeUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;

public class DateTimeUtils extends TimeUtils {
    
    public static boolean isRepeatable(String dateTimeStr) {
        if (dateTimeStr != null && dateTimeStr.startsWith("R")) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isPeriod(String dateTimeStr) {
        if (dateTimeStr != null && dateTimeStr.startsWith("P")) {
            return true;
        }
        
        return false;
    }

    public static long parseDateTime(String dateTimeStr) {
            DateTime dt = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(dateTimeStr);
            return dt.getMillis();
    }
    
    
    public static long parseDuration(String durationStr) {
        if (isPeriod(durationStr)) {
            Period p = ISOPeriodFormat.standard().parsePeriod(durationStr);
            return p.toStandardDuration().getMillis();
        } else {
            return TimeUtils.parseTimeString(durationStr);
        }
    }
    
    public static long parseDateAsDuration(String dateTimeStr) {
        try {
        
            DateTime dt = ISODateTimeFormat.dateTimeParser().parseDateTime(dateTimeStr);
            Duration duration = new Duration(System.currentTimeMillis(), dt.getMillis());
            
            return duration.getMillis();
        } catch (Exception e) {
            return TimeUtils.parseTimeString(dateTimeStr);
        }
    }
    
    public static String[] parseISORepeatable(String isoString) {
    	String[] result = new String[3];
    	String[] elements = isoString.split("/");
        if (elements.length==3) {
        	result[0] = elements[0].substring(1);
        	result[1] = elements[1];
        	result[2] = elements[2];
        } else {
        	result[0] = elements[0].substring(1);
        	result[1] = new DateTime().toString();
        	result[2] = elements[1];
        }
        
        return result;
    }
    
    public static long[] parseRepeatableDateTime(String dateTimeStr) {
        long[] result = new long[3];
        if (isRepeatable(dateTimeStr)) {
        	
        	String[] parsed = parseISORepeatable(dateTimeStr);
            String repeats = parsed[0];
            String delayIn = parsed[1];
            String periodIn = parsed[2];
 
            DateTime startAtDelay =  ISODateTimeFormat.dateTimeParser().parseDateTime(delayIn);
            Duration startAtDelayDur = new Duration(System.currentTimeMillis(), startAtDelay.getMillis());
            if (startAtDelayDur.getMillis() <= 0) {
                // need to introduce delay to allow all initialization
                startAtDelayDur = Duration.standardSeconds(1);
            }
            Period period = ISOPeriodFormat.standard().parsePeriod(periodIn);
            result[0] = Long.parseLong(repeats.length()==0?"-1":repeats);
            result[1] = startAtDelayDur.getMillis();
            result[2] = period.toStandardDuration().getMillis();
            
            return result;
        } else {
            
            int index = dateTimeStr.indexOf("###");
            if (index != -1) {
                String period = dateTimeStr.substring(index + 3);
                String delay = dateTimeStr.substring(0, index);
                result = new long[]{TimeUtils.parseTimeString(delay), TimeUtils.parseTimeString(period)};
                
                return result;
            }
            result = new long[]{TimeUtils.parseTimeString(dateTimeStr)};
            return result;
        }  
    }
    
}
