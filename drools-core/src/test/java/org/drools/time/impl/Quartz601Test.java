/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.time.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Set;

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
