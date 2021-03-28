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
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
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

    @Test
    public void testAccumulateCountWithExists() {
//        The following rule uses an accumulate to count all the name Strings for which at least one Person
//        of that name exists. Expected behavior:
//        - A name should be counted exactly once no matter how many Persons with that name exists.
//        - The rule should fire exactly once and there should be a single Result inserted.
        String str = ""
                + "import " + Person.class.getCanonicalName() + ";\n"
                + "import " + Result.class.getCanonicalName() + ";\n"
                + "rule countUsedNames when\n"
                + "  accumulate(\n"
                + "    $name : String()\n"
                + "    and exists Person(name == $name);\n"
                + "    $count : count($name)\n"
                + "  )\n"
                + "then\n"
                + "  insert( new Result($count));\n"
                + "  System.out.println(kcontext.getMatch().getObjects());\n"
                + "end\n";
        KieSession ksession = getKieSession( str );

        ReteDumper.dumpRete(ksession);

        ksession.insert("Andy"); // used 3x => counted 1x
        ksession.insert("Bill"); // used 1x => counted 1x
        ksession.insert("Carl"); // unused => not counted
        ksession.insert(new Person("Andy", 1));
        ksession.insert(new Person("Andy", 2));
        ksession.insert(new Person("Andy", 3));
        ksession.insert(new Person("Bill", 4));
        ksession.insert(new Person("x", 8));
        ksession.insert(new Person("y", 9));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(2L, results.iterator().next().getValue());
    }

    @Test
    public void testAccumulateWithIndirectArgument() {
        String str =
                "global java.util.List resultTotal; \n" +
                        "global java.util.List resultPair; \n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "import " + Pair.class.getCanonicalName() + ";\n" +
                        "rule R " +
                        "    when\n" +
                        "        accumulate(\n" +
                        "            Person($n1 : name)\n" +
                        "            and Person($n2 : name);\n" +
                        "            $pair :  collectList(Pair.create($n1, $n2)), \n" +
                        "            $total : count(Pair.create($n1, $n2))\n" +
                        "        )\n" +
                        "    then\n" +
                        "        resultTotal.add($total);\n" +
                        "        resultPair.add($pair);\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        final List<Number> resultTotal = new ArrayList<>();
        ksession.setGlobal("resultTotal", resultTotal);

        final List<List<Pair>> resultPair = new ArrayList<>();
        ksession.setGlobal("resultPair", resultPair);

        Person mario = new Person("Mario", 40);
        ksession.insert(mario);

        int rulesFired = ksession.fireAllRules();
        Assertions.assertThat(rulesFired).isGreaterThan(0);
        Assertions.assertThat(resultTotal).contains(1l);

        List<Pair> resultPairItem = resultPair.iterator().next();
        Pair firstPair = resultPairItem.iterator().next();
        assertEquals("Mario", firstPair.getFirst());
        assertEquals("Mario", firstPair.getSecond());
    }

    @Test
    public void testVariableWithMethodCallInAccFunc() {
        final String str = "package org.drools.mvel.compiler\n" +
                           "import " + ControlFact.class.getCanonicalName() + ";" +
                           "import " + Payment.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    Payment( $dueDate : dueDate)\n" +
                           "    Number( longValue == $dueDate.getTime().getTime() ) from accumulate (" +
                           "      ControlFact( $todaysDate : todaysDate )" +
                           "      and\n" +
                           "      Payment( $dueDate_acc : dueDate, eval($dueDate_acc.compareTo($todaysDate) <= 0) )\n" +
                           "      ;max($dueDate_acc.getTime().getTime())\n" +
                           "    )\n" +
                           "then\n" +
                           "end\n";

        System.out.println(str);

        KieSession ksession = getKieSession(str);

        final Payment payment = new Payment();
        payment.setDueDate(Calendar.getInstance());

        final ControlFact controlFact = new ControlFact();
        controlFact.setTodaysDate(Calendar.getInstance());

        ksession.insert(controlFact);
        ksession.insert(payment);
        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testVariableWithMethodCallInAccFuncSimple() {
        final String str = "package org.drools.mvel.compiler\n" +
                           "import " + FactA.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    Number(  ) from accumulate (" +
                           "      FactA( $valueA : value, $valueA > 0 )\n" +
                           "      ;max($valueA.intValue())\n" +
                           "    )\n" +
                           "then\n" +
                           "end\n";

        System.out.println(str);

        KieSession ksession = getKieSession(str);

        final FactA factA = new FactA();
        factA.setValue(1);

        ksession.insert(factA);
        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    public static class ControlFact {

        private Calendar todaysDate;

        public Calendar getTodaysDate() {
            return todaysDate;
        }

        public void setTodaysDate(Calendar todaysDate) {
            this.todaysDate = todaysDate;
        }

    }

    public static class Payment {

        private Calendar dueDate;

        public Calendar getDueDate() {
            return dueDate;
        }

        public void setDueDate(Calendar dueDate) {
            this.dueDate = dueDate;
        }

    }

    public static class FactA {

        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

    }

    @Test
    // Flow DSL is incorrectly adding a Person Pattern before the accumulate
    public void testAccumulateOnTwoPatterns() {
        // DROOLS-5738
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R1 when\n" +
                        "  accumulate (\n" +
                        "       $p1: Person() and $p2: Person( age > $p1.age ), $sum : sum(Person.sumAges($p1, $p2)) " +
                        "         )" +
                        "then\n" +
                        "  insert($sum);\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 46 ) );
        ksession.insert( new Person( "Mark", 44 ) );

        ksession.fireAllRules();

        List<Number> results = getObjectsIntoList(ksession, Number.class);
        assertEquals( 1, results.size() );
        assertEquals( 90, results.get(0) );
    }
}
