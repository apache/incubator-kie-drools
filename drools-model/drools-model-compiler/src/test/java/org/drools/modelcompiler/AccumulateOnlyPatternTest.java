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
import java.util.Objects;

import org.apache.commons.math3.util.Pair;
import org.assertj.core.api.Assertions;
import org.drools.core.base.accumulators.CollectListAccumulateFunction;
import org.drools.core.base.accumulators.CountAccumulateFunction;
import org.drools.model.DSL;
import org.drools.model.Declaration;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.impl.ModelImpl;
import org.drools.model.view.ViewItem;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.domain.StockTick;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.and;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.from;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;
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

    private static Model getExecutableModel() {
        // Create the first pattern.
        Declaration<MrProcessAssignment> processAssignmentDeclaration =
                declarationOf(MrProcessAssignment.class);
        PatternDSL.PatternDef<MrProcessAssignment> processAssignmentPatternDef =
                pattern(processAssignmentDeclaration)
                        .expr("Problematic filter", a -> a.getMachine() != null && a.isMoved());
        //                                                                            ^^^^^^^^^^^^^^
        //                                                                            No a.isMoved() => no NPE.
        // Note: isMoved() is only true when MrProcessAssignment::originalMachine != MrProcessAssignment::machine.
        // Therefore, isMoved() is:
        //   1/ false at the start of the test,
        //   2/ true after the first switch,
        //   3/ false after the second switch and
        //   4/ true again after the third switch, when the exception is thrown

        // Put it inside the first groupBy.
        Declaration<List<MrProcess>> firstGroupByResult =
                (Declaration) declarationOf(Collection.class);
        PatternDSL.PatternDef<List<MrProcess>> firstGroupByResultPattern = pattern(firstGroupByResult)
                .expr("Non-empty", collection -> !collection.isEmpty(),
                        alphaIndexedBy(Integer.class, Index.ConstraintType.GREATER_THAN, -1, Collection::size, 0));
        // Note: Remove the index and the NPE is not thrown!
        ViewItem<?> firstGroupBy = DSL.accumulate(and(processAssignmentPatternDef),
                accFunction(() -> new CollectListAccumulateFunction())
                        .as(firstGroupByResult));

        // Take the data from the first groupBy.
        Variable<MrProcess> tupleFromFirstGroupByPattern =
                declarationOf(MrProcess.class, "biGrouped", from(firstGroupByResult));
        PatternDSL.PatternDef<MrProcess> newTuplePattern = pattern(tupleFromFirstGroupByPattern)
                .bind(tupleFromFirstGroupByPattern, tuple -> tuple);

        // Take the result of the first groupBy and put it inside the second groupBy
        Variable<Long> outputVariable = declarationOf(Long.class, "result");
        ViewItem<?> secondGroupBy = DSL.accumulate(and(newTuplePattern),
                accFunction(() -> new CountAccumulateFunction())
                        .as(outputVariable));

        // And then take the result from the second groupBy and put it into a variable.
        // Also add a no-op consequence.
        PatternDSL.PatternDef<Long> resultPattern = pattern(outputVariable);
        ConsequenceBuilder._1<Long> consequence = DSL.on(outputVariable)
                .execute(result -> {
                    System.out.println(result); // The result doesn't really matter.
                });

        // Build the executable model.
        Rule rule = rule("somepackage", "somerule")
                .build(firstGroupBy, firstGroupByResultPattern, secondGroupBy, resultPattern, consequence);
        ModelImpl model = new ModelImpl();
        model.addRule(rule);
        return model;
    }

    private static void switchMachinesInAssignments(KieSession session, MrProcessAssignment left,
            MrProcessAssignment right) {
        FactHandle leftHandle = session.getFactHandle(left);
        FactHandle rightHandle = session.getFactHandle(right);
        MrMachine original = left.getMachine();
        left.setMachine(right.getMachine());
        right.setMachine(original);
        session.update(leftHandle, left);
        session.update(rightHandle, right);
        session.fireAllRules();
    }

    @Test
    public void test() {
        // Prepare reproducing data.
        MrMachine machine2 = new MrMachine();
        MrMachine machine3 = new MrMachine();
        MrProcessAssignment assignment1 = new MrProcessAssignment(new MrProcess(), machine3, machine3);
        MrProcessAssignment assignment2 = new MrProcessAssignment(new MrProcess(), machine2, machine2);
        MrProcessAssignment assignment3 = new MrProcessAssignment(new MrProcess(), machine2, machine2);
        MrProcessAssignment assignment4 = new MrProcessAssignment(new MrProcess(), machine3, machine3);

        // Create executable model session from a Constraint Stream.
        KieSession kieSession = KieBaseBuilder.createKieBaseFromModel(getExecutableModel())
                .newKieSession();

        // Insert facts into the session.
        kieSession.insert(assignment1);
        kieSession.insert(assignment2);
        kieSession.insert(assignment3);
        kieSession.insert(assignment4);
        kieSession.fireAllRules();

        // Execute the sequence of session events that triggers the exception.
        switchMachinesInAssignments(kieSession, assignment1, assignment2);
        switchMachinesInAssignments(kieSession, assignment1, assignment2);
        switchMachinesInAssignments(kieSession, assignment4, assignment3);

        kieSession.dispose();
    }

    public static class MrProcess {

    }

    public static class MrProcessAssignment {

        private MrProcess process;
        private MrMachine originalMachine;
        private MrMachine machine;

        public MrProcessAssignment(MrProcess process, MrMachine originalMachine, MrMachine machine) {
            this.process = process;
            this.originalMachine = originalMachine;
            this.machine = machine;
        }

        public MrProcess getProcess() {
            return process;
        }

        public MrMachine getOriginalMachine() {
            return originalMachine;
        }

        public MrMachine getMachine() {
            return machine;
        }

        public void setMachine(MrMachine machine) {
            this.machine = machine;
        }

        // ************************************************************************
        // Complex methods
        // ************************************************************************

        public boolean isMoved() {
            if (machine == null) {
                return false;
            }
            return !Objects.equals(originalMachine, machine);
        }

    }

    public static class MrMachine {

    }


}
