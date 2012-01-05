package org.drools.integrationtests;

import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static junit.framework.Assert.assertEquals;

public class DynamicRulesChangesTest {

    private static final int PARALLEL_THREADS = 3;
    private static final ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_THREADS);

    private static InternalKnowledgeBase kbase;
    private static RuleBase ruleBase;

    @Before
    public void setUp() throws Exception {
        kbase = (InternalKnowledgeBase)KnowledgeBaseFactory.newKnowledgeBase();
        ruleBase = kbase.getRuleBase();
        addRule("raiseAlarm");
    }

    @Test(timeout=10000) @Ignore
    public void testConcurrentRuleAdditions() throws Exception {
        parallelExecute(RulesExecutor.getSolvers());
    }

    @Test(timeout=10000) @Ignore
    public void testBatchRuleAdditions() throws Exception {
        parallelExecute(BatchRulesExecutor.getSolvers());
    }

    private void parallelExecute(Collection<Callable<List<String>>> solvers) throws Exception {
        CompletionService<List<String>> ecs = new ExecutorCompletionService<List<String>>(executor);
        for (Callable<List<String>> s : solvers) {
            ecs.submit(s);
        }
        for (int i = 0; i < PARALLEL_THREADS; ++i) {
            List<String> events = ecs.take().get();
            assertEquals(5, events.size());
        }
    }

    public static class RulesExecutor implements Callable<List<String>> {

        public List<String> call() throws Exception {
            final List<String> events = new ArrayList<String>();

            try {
                StatefulSession ksession = ruleBase.newStatefulSession();
                ksession.setGlobal("events", events);

                // phase 1
                Room room1 = new Room("Room 1");
                ksession.insert(room1);
                FactHandle fireFact1 = ksession.insert(new Fire(room1));
                ksession.fireAllRules();
                assertEquals(1, events.size());

                // phase 2
                Sprinkler sprinkler1 = new Sprinkler(room1);
                ksession.insert(sprinkler1);
                ksession.fireAllRules();
                assertEquals(2, events.size());

                // phase 3
                ksession.retract(fireFact1);
                ksession.fireAllRules();
            } catch (Exception e) {
                System.err.println("Exception in thread " + Thread.currentThread().getName() + ": " + e.getLocalizedMessage());
                throw e;
            }

            return events;
        }

        public static Collection<Callable<List<String>>> getSolvers() {
            Collection<Callable<List<String>>> solvers = new ArrayList<Callable<List<String>>>();
            for (int i = 0; i < PARALLEL_THREADS; ++i) {
                solvers.add(new RulesExecutor());
            }
            return solvers;
        }
    }

    public static class BatchRulesExecutor implements Callable<List<String>> {

        public List<String> call() throws Exception {
            final List<String> events = new ArrayList<String>();

            try {
                StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
                ksession.setGlobal("events", events);

                Room room1 = new Room("Room 1");
                Fire fire1 = new Fire(room1);

                // phase 1
                List<Command> cmds = new ArrayList<Command>();
                cmds.add(CommandFactory.newInsert(room1, "room1"));
                cmds.add(CommandFactory.newInsert(fire1, "fire1"));
                cmds.add(CommandFactory.newFireAllRules());
                ksession.execute(CommandFactory.newBatchExecution(cmds));
                assertEquals(1, events.size());

                // phase 2
                cmds = new ArrayList<Command>();
                cmds.add(CommandFactory.newInsert(new Sprinkler(room1), "sprinkler1"));
                cmds.add(CommandFactory.newFireAllRules());
                ksession.execute(CommandFactory.newBatchExecution(cmds));
                assertEquals(2, events.size());

                // phase 3
                cmds = new ArrayList<Command>();
                cmds.add(CommandFactory.newRetract(ksession.getFactHandle(fire1)));
                cmds.add(CommandFactory.newFireAllRules());
                ksession.execute(CommandFactory.newBatchExecution(cmds));
            } catch (Exception e) {
                System.err.println("Exception in thread " + Thread.currentThread().getName() + ": " + e.getLocalizedMessage());
                throw e;
            }

            return events;
        }

        public static Collection<Callable<List<String>>> getSolvers() {
            Collection<Callable<List<String>>> solvers = new ArrayList<Callable<List<String>>>();
            for (int i = 0; i < PARALLEL_THREADS; ++i) {
                solvers.add(new BatchRulesExecutor());
            }
            return solvers;
        }
    }

    public static void addRule(String ruleName) throws Exception {
        String rule = rules.get(ruleName);
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new StringReader(rule));
        ruleBase.addPackage(builder.getPackage());
    }

    // Rules

    private static Map<String, String> rules = new HashMap<String, String>() {{
        put("raiseAlarm",
                "global java.util.List events\n" +
                "rule \"Raise the alarm when we have one or more fires\" lock-on-active\n" +
                "when\n" +
                "    exists org.drools.integrationtests.DynamicRulesChangesTest.Fire()\n" +
                "then\n" +
                "    insert( new org.drools.integrationtests.DynamicRulesChangesTest.Alarm() );\n" +
                "    events.add( \"Raise the alarm\" );\n" +
                "    org.drools.integrationtests.DynamicRulesChangesTest.addRule(\"onFire\");\n" +
                "end");

       put("onFire",
               "global java.util.List events\n" +
               "rule \"When there is a fire turn on the sprinkler\" lock-on-active\n" +
               "when\n" +
               "    $fire: org.drools.integrationtests.DynamicRulesChangesTest.Fire($room : room)\n" +
               "    $sprinkler : org.drools.integrationtests.DynamicRulesChangesTest.Sprinkler( room == $room, on == false )\n" +
               "then\n" +
               "    modify( $sprinkler ) { setOn( true ) };\n" +
               "    events.add( \"Turn on the sprinkler for room \" + $room.getName() );\n" +
               "    org.drools.integrationtests.DynamicRulesChangesTest.addRule(\"fireGone\");\n" +
               "end");

        put("fireGone",
                "global java.util.List events\n" +
                "rule \"When the fire is gone turn off the sprinkler\" lock-on-active\n" +
                "when\n" +
                "    $room : org.drools.integrationtests.DynamicRulesChangesTest.Room( )\n" +
                "    $sprinkler : org.drools.integrationtests.DynamicRulesChangesTest.Sprinkler( room == $room, on == true )\n" +
                "    not org.drools.integrationtests.DynamicRulesChangesTest.Fire( room == $room )\n" +
                "then\n" +
                "    modify( $sprinkler ) { setOn( false ) };\n" +
                "    events.add( \"Turn off the sprinkler for room \" + $room.getName() );\n" +
                "    org.drools.integrationtests.DynamicRulesChangesTest.addRule(\"cancelAlarm\");\n" +
                "end");

        put("cancelAlarm",
                "global java.util.List events\n" +
                "rule \"Cancel the alarm when all the fires have gone\"\n" +
                "when\n" +
                "    not org.drools.integrationtests.DynamicRulesChangesTest.Fire()\n" +
                "    $alarm : org.drools.integrationtests.DynamicRulesChangesTest.Alarm()\n" +
                "then\n" +
                "    retract( $alarm );\n" +
                "    events.add( \"Cancel the alarm\" );\n" +
                "    org.drools.integrationtests.DynamicRulesChangesTest.addRule(\"status\");\n" +
                "end");

        put("status",
                "global java.util.List events\n" +
                "rule \"Status output when things are ok\"\n" +
                "when\n" +
                "    not org.drools.integrationtests.DynamicRulesChangesTest.Alarm()\n" +
                "    not org.drools.integrationtests.DynamicRulesChangesTest.Sprinkler( on == true )\n" +
                "then\n" +
                "    events.add( \"Everything is ok\" );\n" +
                "end");
    }};

    // Model

    public static class Alarm { }

    public static class Fire {

        private Room room;

        public Fire() { }

        public Fire(Room room) {
            this.room = room;
        }

        public Room getRoom() {
            return room;
        }

        public void setRoom(Room room) {
            this.room = room;
        }
    }

    public static class Room {

        private String name;

        public Room() { }

        public Room(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Room)) return false;
            return name.equals(((Room)obj).getName());
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Sprinkler {

        private Room room;
        private boolean on = false;

        public Sprinkler() { }

        public Sprinkler(Room room) {
            this.room = room;
        }

        public Room getRoom() {
            return room;
        }

        public void setRoom(Room room) {
            this.room = room;
        }

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }

        @Override
        public int hashCode() {
            return room.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Sprinkler)) return false;
            return room.equals(((Sprinkler)obj).getRoom());
        }

        @Override
        public String toString() {
            return "Sprinkler for " + room;
        }
    }
}
