/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.StockTick;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class AccumulateOnlyPatternTest extends OnlyPatternTest {

    public AccumulateOnlyPatternTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testAccumulateWithMaxCalendarNullDate() {
        //DROOLS-4990
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                        "rule AccumulateMaxDate\n" +
                        "  dialect \"java\"\n" +
                        "  when\n" +
                        "  $max1 : Number() from accumulate(\n" +
                        "    StockTick(isSetDueDate == true, $time : dueDate);\n" +
                        "    max($time.getTime().getTime()))\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        StockTick st = new StockTick("RHT");
        ksession.insert(st);
        StockTick st2 = new StockTick("IBM");
        st2.setDueDate(new GregorianCalendar(2020, Calendar.FEBRUARY, 4));
        ksession.insert(st2);
        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testSubnetworkTuple() {
        final String drl =
                "import java.math.*; " +
                        "import " + InputDataTypes.class.getCanonicalName() + "; " +
                        "import " + ControlType.class.getCanonicalName() + "; " +
                        "global java.util.List result; \n" +
                        "rule \"AccumulateMinDate\" " +
                        "	when " +
                        "		$min : Number() from accumulate ( ControlType( booleanValue == false ) and InputDataTypes($dueDate : dueDate );" +
                        "                                           min($dueDate.getTime().getTime())" +
                        "                                         ) " +
                        " " +
                        "	then " +
                        "		result.add($min); " +
                        "end";

        final KieSession ksession = getKieSession(drl);

        List<Number> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ControlType t1 = new ControlType(false);
        ksession.insert(t1);

        Calendar year2019 = calendarFromString("2019-11-06T16:00:00.00-07:00");
        InputDataTypes i1 = new InputDataTypes(year2019);
        ksession.insert(i1);

        InputDataTypes i2 = new InputDataTypes(calendarFromString("2020-11-06T16:00:00.00-07:00"));
        ksession.insert(i2);

        try {
            assertEquals(1, ksession.fireAllRules());
            assertEquals(year2019.getTime().getTime(), result.iterator().next().longValue());
        } finally {
            ksession.dispose();
        }
    }

    public static class InputDataTypes {

        private Calendar dueDate;

        public InputDataTypes(Calendar dueDate) {
            this.dueDate = dueDate;
        }

        public Calendar getDueDate() {
            return dueDate;
        }

        public void setDueDate(Calendar date) {
            dueDate = date;
        }
    }

    public static class ControlType {

        private boolean booleanValue;

        public ControlType(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }

        public void setBooleanValue(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }

        public boolean getBooleanValue() {
            return booleanValue;
        }
    }

    private GregorianCalendar calendarFromString(String inputString) {
        return GregorianCalendar.from(ZonedDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(inputString)));
    }
}
