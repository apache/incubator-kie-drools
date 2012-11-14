package org.jbpm.process.core.timer;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kie.time.SessionClock;
import org.drools.time.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of BusinessCalendar interface that is configured with properties.
 * Following are supported properties:
 * <ul>
 *  <li>business.hours.per.week - specifies number of working days per week (default 5)</li>
 *  <li>business.hours.per.day - specifies number of working hours per day (default 8)</li>
 *  <li>business.start.hour - specifies starting hour of work day (default 9AM)</li>
 *  <li>business.end.hour - specifies ending hour of work day (default 5PM)</li>
 *  <li>business.holidays - specifies holidays (see format section for details on how to configure it)</li>
 *  <li>business.holiday.date.format - specifies holiday date format used (default yyyy-DD-mm)</li>
 *  <li>business.weekend.days - specifies days of the weekend (default Saturday and Sunday)</li>
 *  <li>business.cal.timezone - specifies time zone to be used (if not given uses default of the system it runs on)</li>
 * </ul>
 * 
 * <b>Format</b><br/>
 * 
 * Holidays can be given in two formats:
 * <ul>
 *  <li>as date range separated with colon - for instance 2012-05-01:2012-05-15</li>
 *  <li>single day holiday - for instance 2012-05-01</li>
 * </ul>
 * each holiday period should be separated from next one with comma: 2012-05-01:2012-05-15,2012-12-24:2012-12-27
 * <br/> 
 * Holiday date format must be given in pattern that is supported by <code>java.text.SimpleDateFormat</code>.<br/>
 * 
 * Weekend days should be given as integer that corresponds to <code>java.util.Calendar</code> constants.
 * <br/>
 * 
 */
public class BusinessCalendarImpl implements BusinessCalendar {
	
	private static final Logger logger = LoggerFactory.getLogger(BusinessCalendarImpl.class);

    private Properties businessCalendarConfiguration;
    
    private int daysPerWeek;
    private int hoursInDay;
    private int startHour; 
    private int endHour; 
    private  String timezone;
    
    private List<TimePeriod> holidays;
    private List<Integer> weekendDays= new ArrayList<Integer>();
    private SessionClock clock;
    
    private static final Pattern SIMPLE  = Pattern.compile( "([+-])?\\s*((\\d+)[Ww])?\\s*((\\d+)[Dd])?\\s*((\\d+)[Hh])?\\s*((\\d+)[Mm])?" );
    private static final int     SIM_WEEK = 3;
    private static final int     SIM_DAY = 5;
    private static final int     SIM_HOU = 7;
    private static final int     SIM_MIN = 9;

    
    public static final String DAYS_PER_WEEK = "business.hours.per.week";
    public static final String HOURS_PER_DAY = "business.hours.per.day";
    public static final String START_HOUR = "business.start.hour";
    public static final String END_HOUR = "business.end.hour";
    // holidays are given as date range and can have more than one value separated with comma
    public static final String HOLIDAYS = "business.holidays";
    public static final String HOLIDAY_DATE_FORMAT = "business.holiday.date.format";
    
    public static final String WEEKEND_DAYS = "business.weekend.days";
    public static final String TIMEZONE = "business.cal.timezone";

    private static final String DEFAULT_PROPERTIES_NAME = "/jbpm.business.calendar.properties";
    
    
    
    
    public BusinessCalendarImpl() {
        String propertiesLocation = System.getProperty("jbpm.business.calendar.properties");
        
        if (propertiesLocation == null) {
            propertiesLocation = DEFAULT_PROPERTIES_NAME;
        }
        businessCalendarConfiguration = new Properties();
        
        InputStream in = this.getClass().getResourceAsStream(propertiesLocation);
        if (in != null) {
            
            try {
                businessCalendarConfiguration.load(in);
            } catch (IOException e) {
               logger.error("Error while loading properties for business calendar", e);

            }
        }
        init();
        
    }
    
    public BusinessCalendarImpl(Properties configuration) {
        this.businessCalendarConfiguration = configuration;
        init();
    }
    
    public BusinessCalendarImpl(Properties configuration, SessionClock clock) {
        this.businessCalendarConfiguration = configuration;
        this.clock = clock;
        init();
    }
    
    protected void init() {
        if (this.businessCalendarConfiguration == null) {
            throw new IllegalArgumentException("BusinessCalendar configuration was not provided.");
        }
            
        daysPerWeek = getPropertyAsInt(DAYS_PER_WEEK, "5");
        hoursInDay = getPropertyAsInt(HOURS_PER_DAY, "8");
        startHour = getPropertyAsInt(START_HOUR, "9"); 
        endHour = getPropertyAsInt(END_HOUR, "17"); 
        holidays = parseHolidays();
        parseWeekendDays();
        this.timezone = businessCalendarConfiguration.getProperty(TIMEZONE);
    }
    
    public long calculateBusinessTimeAsDuration(String timeExpression) {
        if (businessCalendarConfiguration == null) {
            return TimeUtils.parseTimeString(timeExpression);
        }
        
        Date calculatedDate = calculateBusinessTimeAsDate(timeExpression);
        
        return (calculatedDate.getTime() - getCurrentTime());
    }
    
    public Date calculateBusinessTimeAsDate(String timeExpression) {
        if (businessCalendarConfiguration == null) {
            return new Date(TimeUtils.parseTimeString(getCurrentTime() + timeExpression));
        }
        
        
        String trimmed = timeExpression.trim();
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int min = 0;
        
        if( trimmed.length() > 0 ) {
            Matcher mat = SIMPLE.matcher( trimmed );
            if ( mat.matches() ) {
                weeks = (mat.group( SIM_WEEK ) != null) ? Integer.parseInt( mat.group( SIM_WEEK ) ) : 0;
                days = (mat.group( SIM_DAY ) != null) ? Integer.parseInt( mat.group( SIM_DAY ) ) : 0;
                hours = (mat.group( SIM_HOU ) != null) ? Integer.parseInt( mat.group( SIM_HOU ) ) : 0;
                min = (mat.group( SIM_MIN ) != null) ? Integer.parseInt( mat.group( SIM_MIN ) ) : 0;
            }
        }
        int time = 0;
        
        Calendar c = new GregorianCalendar();
        if (timezone != null) {
            c.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        if (this.clock != null) {
            c.setTimeInMillis(this.clock.getCurrentTime());
        }
        
        
        // calculate number of weeks
        int numberOfWeeks = days/daysPerWeek + weeks;
        if (numberOfWeeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, numberOfWeeks);
        }
        handleWeekend(c);
        hours += (days - (numberOfWeeks * daysPerWeek)) * hoursInDay;
        
        // calculate number of days
        int numberOfDays = hours/hoursInDay;
        if (numberOfDays > 0) {
            for (int i = 0; i < numberOfDays; i++) {
                c.add(Calendar.DAY_OF_YEAR, 1);
                handleWeekend(c);
            }
        }

        int currentCalHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= endHour) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.add(Calendar.HOUR_OF_DAY, startHour-currentCalHour);
        } else if (currentCalHour < startHour) {
            c.add(Calendar.HOUR_OF_DAY, startHour);
        }

        // calculate remaining hours
        time = hours - (numberOfDays * hoursInDay);
        c.add(Calendar.HOUR, time);
        handleWeekend(c);
        
        currentCalHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= endHour) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            // set hour to the starting one
            c.set(Calendar.HOUR_OF_DAY, startHour);
            c.add(Calendar.HOUR_OF_DAY, currentCalHour - endHour);
        } else if (currentCalHour < startHour) {
            c.add(Calendar.HOUR_OF_DAY, startHour);
        }
        
        // calculate minutes
        int numberOfHours = min/60;
        if (numberOfHours > 0) {
            c.add(Calendar.HOUR, numberOfHours);
            min = min-(numberOfHours * 60);
        }
        c.add(Calendar.MINUTE, min);
        
        currentCalHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= endHour) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            // set hour to the starting one
            c.set(Calendar.HOUR_OF_DAY, startHour);
            c.add(Calendar.HOUR_OF_DAY, currentCalHour - endHour);
        } else if (currentCalHour < startHour) {
            c.add(Calendar.HOUR_OF_DAY, startHour);
        }
        // take under consideration weekend
        handleWeekend(c);
        // take under consideration holidays
        handleHoliday(c);
 
        return c.getTime();
    }
    
    protected void handleHoliday(Calendar c) {
        if (!holidays.isEmpty()) {
            Date current = c.getTime();
            for (TimePeriod holiday : holidays) {
                // check each holiday if it overlaps current date and break after first match
                if (current.after(holiday.getFrom()) && current.before(holiday.getTo())) {
                    
                    Calendar tmp = new GregorianCalendar();
                    tmp.setTime(holiday.getTo());                    
                    c.add(Calendar.DAY_OF_YEAR, tmp.get(Calendar.DAY_OF_YEAR) - c.get(Calendar.DAY_OF_YEAR));
                    handleWeekend(c);
                    break;
                }
            }
        }
        
    }

    protected int getPropertyAsInt(String propertyName, String defaultValue) {
        String value = businessCalendarConfiguration.getProperty(propertyName, defaultValue);
        
        return Integer.parseInt(value);
    }
    
    protected List<TimePeriod> parseHolidays() {
        String holidaysString = businessCalendarConfiguration.getProperty(HOLIDAYS);
        List<TimePeriod> holidays = new ArrayList<TimePeriod>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (holidaysString != null) {
            String[] hPeriods = holidaysString.split(",");
            SimpleDateFormat sdf = new SimpleDateFormat(businessCalendarConfiguration.getProperty(HOLIDAY_DATE_FORMAT, "yyyy-MM-dd"));
            for (String hPeriod : hPeriods) {
                boolean addNextYearHolidays = false;
                
                String[] fromTo = hPeriod.split(":");
                if (fromTo[0].startsWith("*")) {
                    addNextYearHolidays = true;
                    
                    fromTo[0] = fromTo[0].replaceFirst("\\*", currentYear+"");
                }
                try {
                    if (fromTo.length == 2) {
                        Calendar tmpFrom = new GregorianCalendar();
                        if (timezone != null) {
                            tmpFrom.setTimeZone(TimeZone.getTimeZone(timezone));
                        }
                        tmpFrom.setTime(sdf.parse(fromTo[0]));
  
                        if (fromTo[1].startsWith("*")) {
                            
                            fromTo[1] = fromTo[1].replaceFirst("\\*", currentYear+"");
                        }
                        
                        Calendar tmpTo = new GregorianCalendar();
                        if (timezone != null) {
                            tmpTo.setTimeZone(TimeZone.getTimeZone(timezone));
                        }
                        tmpTo.setTime(sdf.parse(fromTo[1]));
                        Date from = tmpFrom.getTime();
                        
                        
                        tmpTo.add(Calendar.DAY_OF_YEAR, 1);
                        
                        if ((tmpFrom.get(Calendar.MONTH) > tmpTo.get(Calendar.MONTH)) && (tmpFrom.get(Calendar.YEAR) == tmpTo.get(Calendar.YEAR))) {
                            tmpTo.add(Calendar.YEAR, 1);
                        }
                        
                        Date to = tmpTo.getTime();
                        holidays.add(new TimePeriod(from, to));
                        
                        holidays.add(new TimePeriod(from, to));
                        if (addNextYearHolidays) {
                            tmpFrom = new GregorianCalendar();
                            if (timezone != null) {
                                tmpFrom.setTimeZone(TimeZone.getTimeZone(timezone));
                            }
                            tmpFrom.setTime(sdf.parse(fromTo[0]));
                            tmpFrom.add(Calendar.YEAR, 1);
                            
                            from = tmpFrom.getTime();
                            tmpTo = new GregorianCalendar();
                            if (timezone != null) {
                                tmpTo.setTimeZone(TimeZone.getTimeZone(timezone));
                            }
                            tmpTo.setTime(sdf.parse(fromTo[1]));
                            tmpTo.add(Calendar.YEAR, 1);
                            tmpTo.add(Calendar.DAY_OF_YEAR, 1);
                            
                            if ((tmpFrom.get(Calendar.MONTH) > tmpTo.get(Calendar.MONTH)) && (tmpFrom.get(Calendar.YEAR) == tmpTo.get(Calendar.YEAR))) {
                                tmpTo.add(Calendar.YEAR, 1);
                            }
                            
                            to = tmpTo.getTime();
                            holidays.add(new TimePeriod(from, to));
                        }
                    } else {
                        
                        Calendar c = new GregorianCalendar();
                        c.setTime(sdf.parse(fromTo[0]));
                        c.add(Calendar.DAY_OF_YEAR, 1);
                        // handle one day holiday
                        holidays.add(new TimePeriod(sdf.parse(fromTo[0]), c.getTime()));
                        if (addNextYearHolidays) {
                            Calendar tmp = Calendar.getInstance();
                            tmp.setTime(sdf.parse(fromTo[0]));
                            tmp.add(Calendar.YEAR, 1);
                            
                            Date from = tmp.getTime();
                            c.add(Calendar.YEAR, 1);
                            holidays.add(new TimePeriod(from, c.getTime()));
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while parsing holiday in business calendar", e);
                }
            }
        }
        return holidays;
    }
    
    protected void parseWeekendDays() {
        String weekendDays = businessCalendarConfiguration.getProperty(WEEKEND_DAYS);
        
        if (weekendDays == null) {
            this.weekendDays.add(Calendar.SATURDAY);
            this.weekendDays.add(Calendar.SUNDAY);
        } else {
            String[] days = weekendDays.split(",");
            for (String day : days) {
                this.weekendDays.add(Integer.parseInt(day));
            }
        }
    }

    private class TimePeriod {
        private Date from;
        private Date to;

        protected TimePeriod(Date from, Date to) {
            this.from = from;
            this.to = to;
        }

        protected Date getFrom() {
            return this.from;
        }
        
        protected Date getTo() {
            return this.to;
        }
    }

    protected long getCurrentTime() {
        if (clock != null) {
            return clock.getCurrentTime();
        } else {
            return System.currentTimeMillis();
        }
    }
    
    protected boolean isWorkingDay(int day) {
        if (weekendDays.contains(day)) {
            return false;
        }
        
        return true;
    }
    protected void handleWeekend(Calendar c) {
        int dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
        while (!isWorkingDay(dayOfTheWeek)) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
        }
    }
}
