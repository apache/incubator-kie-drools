package org.drools.core.time.impl;

import java.text.ParseException;
import java.util.Set;

import org.junit.Test;

import static org.assertj.core.api.Assertions.fail;

public class Quartz601Test {

    @Test
    public void testNormal() {
        for(int i=0; i<6; i++) {
            assertParsesForField("0 15 10 * * ? 2005", i);
        }
    }
    @Test
    public void testSecond() {
          assertParsesForField("58-4 5 21 ? * MON-FRI", 0);
    }
    @Test
    public void testMinute() {
          assertParsesForField("0 58-4 21 ? * MON-FRI", 1);
    }
    @Test
    public void testHour() {
          assertParsesForField("0 0/5 21-3 ? * MON-FRI", 2);
    }
    @Test
    public void testDayOfWeekNumber() {
          assertParsesForField("58 5 21 ? * 6-2", 5);
    }
    @Test
    public void testDayOfWeek() {
          assertParsesForField("58 5 21 ? * FRI-TUE", 5);
    }
    @Test
    public void testDayOfMonth() {
          assertParsesForField("58 5 21 28-5 1 ?", 3);
    }
    @Test
    public void testMonth() {
          assertParsesForField("58 5 21 ? 11-2 FRI", 4);
    }
    @Test
    public void testAmbiguous() {
          System.err.println( assertParsesForField("0 0 14-6 ? * FRI-MON", 2) );
          System.err.println( assertParsesForField("0 0 14-6 ? * FRI-MON", 5) );

          System.err.println( assertParsesForField("55-3 56-2 6 ? * FRI", 0) );
          System.err.println( assertParsesForField("55-3 56-2 6 ? * FRI", 1) );
    }

    private Set assertParsesForField(String expression, int constant) {
        try {
            TestCronExpression cronExpression = new TestCronExpression(expression);
            Set set = cronExpression.getSetPublic(constant);
            if(set.size() == 0) {
                fail("Empty field ["+constant+"] returned for " + expression);
            }
            return set;
        } catch(ParseException pe) {
            fail("Exception thrown during parsing: " + pe);
        }
        return null;  // not reachable
    }

}

class TestCronExpression extends CronExpression {

    public TestCronExpression(String cronExpression) throws ParseException {
        super(cronExpression);
    }

    public Set getSetPublic(int constant) {
        return super.getSet(constant);
    }

}
