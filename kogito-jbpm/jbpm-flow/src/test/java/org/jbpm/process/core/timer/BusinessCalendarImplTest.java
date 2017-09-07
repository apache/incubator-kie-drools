/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.core.timer;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.kie.api.time.SessionPseudoClock;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class BusinessCalendarImplTest extends AbstractBaseTest {

    public void addLogger() { 
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
    @Test
    public void testCalculateHours() {
        Properties config = new Properties();
        String expectedDate = "2012-05-04 16:45";
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-04 13:45").getTime());
        
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);
        
        Date result = businessCal.calculateBusinessTimeAsDate("3h");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateHoursCustomWorkingHours() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.HOURS_PER_DAY, "6");
        String expectedDate = "2012-05-04 15:45";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-03 13:45").getTime());
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("8h");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateHoursPassingOverWeekend() {
        Properties config = new Properties();
        String expectedDate = "2012-05-07 12:45";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-04 13:45").getTime());        
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("7h");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    
    @Test
    public void testCalculateHoursPassingOverCustomDefinedWeekend() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.WEEKEND_DAYS, Calendar.FRIDAY + ","+Calendar.SATURDAY);
        String expectedDate = "2012-05-06 12:45";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-03 13:45").getTime());    
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("7h");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateMinutesPassingOverWeekend() {
        Properties config = new Properties();
        String expectedDate = "2012-05-07 09:15";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-04 16:45").getTime());
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("30m");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateMinutesPassingOverHoliday() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.HOLIDAYS, "2012-05-12:2012-05-19");
        String expectedDate = "2012-05-21 09:15";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-11 16:45").getTime());
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("30m");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateDays() {
        Properties config = new Properties();
        String expectedDate = "2012-05-14 09:00";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDate("2012-05-04").getTime());      
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("6d");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateDaysStartingInWeekend() {
        Properties config = new Properties();
        String expectedDate = "2012-05-09 09:00";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDate("2012-05-05").getTime());
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("2d");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateDaysCustomWorkingDays() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.DAYS_PER_WEEK, "4");
        config.setProperty(BusinessCalendarImpl.WEEKEND_DAYS, Calendar.FRIDAY + ","+Calendar.SATURDAY + "," +Calendar.SUNDAY);
        String expectedDate = "2012-05-15 14:30";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-03 14:30").getTime());    
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("6d");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateDaysMiddleDay() {
        Properties config = new Properties();
        String expectedDate = "2012-05-11 12:27";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-03 12:27").getTime());
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("6d");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateDaysHoursMinutes() {
        Properties config = new Properties();
        String expectedDate = "2012-05-14 14:20";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDate("2012-05-04").getTime());
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("6d4h80m");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateTimeDaysHoursMinutesHolidays() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.HOLIDAYS, "2012-05-10:2012-05-19");
        String expectedDate = "2012-05-21 14:20";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDate("2012-05-04").getTime()); 
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("6d4h80m");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateTimeDaysHoursMinutesSingleDayHolidays() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.HOLIDAYS, "2012-05-07");
        String expectedDate = "2012-05-08 13:20";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDate("2012-05-04").getTime());       
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("1d4h20m");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateTimeDaysHoursMinutesSingleDayHolidaysInMiddleOfWeek() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.HOLIDAYS, "2012-05-09");
        String expectedDate = "2012-05-10 15:30";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-08 11:10").getTime());
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("1d4h20m");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateDaysPassingOverHolidayAtYearEnd() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.HOLIDAYS, "2012-12-31:2013-01-01");
        String expectedDate = "2013-01-04 09:15";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-12-28 16:45").getTime());        
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        
        Date result = businessCal.calculateBusinessTimeAsDate("2d30m");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateDaysPassingOverHolidayAtYearEndWithWildcards() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.HOLIDAYS, "*-12-31:*-01-01");
        String expectedDate = "2013-01-02 09:15";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-12-28 16:45").getTime());
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);
        
        
        Date result = businessCal.calculateBusinessTimeAsDate("2d30m");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateISOHours() {
        Properties config = new Properties();
        String expectedDate = "2012-05-04 16:45";
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-04 13:45").getTime());
        
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);
        
        Date result = businessCal.calculateBusinessTimeAsDate("PT3H");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testCalculateISODaysAndHours() {
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.HOLIDAYS, "2012-05-09");
        String expectedDate = "2012-05-10 15:30";
        
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-08 11:10").getTime());
        
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);
        
        Date result = businessCal.calculateBusinessTimeAsDate("P1DT4H20M");
        
        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }
    
    @Test
    public void testSingleHolidayWithinGivenTime() {
    	final Properties props = new Properties();
		props.put(BusinessCalendarImpl.HOLIDAYS, "2015-01-13");
		String expectedDate = "2015-01-15 11:38";
 
		SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTimeAndMillis("2015-01-08 11:38:30.198").getTime());
 
		BusinessCalendarImpl businessCalendarImpl = new BusinessCalendarImpl(props, clock);
 
		Date result = businessCalendarImpl.calculateBusinessTimeAsDate("4d");
		assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm", result));
    }

    @Test
    public void testCalculateMillisecondsAsDefault() {
        Properties config = new Properties();
        String expectedDate = "2012-05-04 16:45:10.000";
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTimeAndMillis("2012-05-04 16:45:00.000").getTime());

        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(config, clock);

        Date result = businessCal.calculateBusinessTimeAsDate("10000");

        assertEquals(expectedDate, formatDate("yyyy-MM-dd HH:mm:ss.SSS", result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingConfigurationDualArgConstructor() {
        SessionPseudoClock clock = new StaticPseudoClock(parseToDateWithTime("2012-05-04 13:45").getTime());
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(null, clock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingConfigurationSingleArgConstructor() {
        BusinessCalendarImpl businessCal = new BusinessCalendarImpl(null);
    }

    private Date parseToDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        Date testTime;
        try {
            testTime = sdf.parse(dateString);
            
            return testTime;
        } catch (ParseException e) {
            return null;
        }
    }
    
    private Date parseToDateWithTime(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        Date testTime;
        try {
            testTime = sdf.parse(dateString);
            
            return testTime;
        } catch (ParseException e) {
            return null;
        }        
    }
    
    private Date parseToDateWithTimeAndMillis(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        Date testTime;
        try {
            testTime = sdf.parse(dateString);
            
            return testTime;
        } catch (ParseException e) {
            return null;
        }        
    }
    
    
    private String formatDate(String pattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        
        String testTime = sdf.format(date);
        
        return testTime;
                
    }
    
    private class StaticPseudoClock implements SessionPseudoClock {

    	private long currentTime;
    	
    	private StaticPseudoClock(long currenttime) {
    		this.currentTime = currenttime;
    	}
    	
		public long getCurrentTime() {
			return this.currentTime;
		}

		public long advanceTime(long amount, TimeUnit unit) {
			throw new UnsupportedOperationException("It is static clock and does not allow advance time operation");
		}
    	
    }
}
