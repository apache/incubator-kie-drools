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

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.StockTick;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class AccumulateOnlyPatternTest extends OnlyPatternTest {

    public AccumulateOnlyPatternTest(RUN_TYPE testRunType ) {
        super( testRunType );
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
}
