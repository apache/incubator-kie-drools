/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

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

import org.kie.api.definition.rule.Rule;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.KieBuilder;
import org.kie.api.command.Command;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.CommandFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DynamicRulesChangesTest {

    // Note: If PARALLEL_THREADS is set to 2 or larger, this test fails with the below Exception even before modifying this test to cover exec-model
    //   Exception in thread pool-7-thread-1: Exception executing consequence for rule "Raise the alarm when we have one or more fires" in defaultpkg:
    //   java.lang.IllegalArgumentException: Rule name 'Raise the alarm when we have one or more fires' does not exist in the Package 'defaultpkg'.
    
    private final KieBaseTestConfiguration kieBaseTestConfiguration;
    private static KieBaseTestConfiguration staticKieBaseTestConfiguration;

    public DynamicRulesChangesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private static final int PARALLEL_THREADS = 1;

    private static InternalKnowledgeBase kbase;
    private ExecutorService executor;

    @Before
    public void setUp() throws Exception {
        staticKieBaseTestConfiguration = kieBaseTestConfiguration;
        executor = Executors.newFixedThreadPool(PARALLEL_THREADS);
        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        addRule("raiseAlarm");
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdownNow();
    }

    @Test(timeout=10000)
    public void testConcurrentRuleAdditions() throws Exception {
        parallelExecute(RulesExecutor.getSolvers());
    }

    @Test(timeout=10000)
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
            assertThat(events.size()).isEqualTo(5);
        }
    }

    public static class RulesExecutor implements Callable<List<String>> {

        public List<String> call() throws Exception {
            final List<String> events = new ArrayList<String>();

            try {
                KieSession ksession = kbase.newKieSession();
                ksession.setGlobal("events", events);

                // phase 1
                Room room1 = new Room("Room 1");
                ksession.insert(room1);
                FactHandle fireFact1 = ksession.insert(new Fire(room1));
                ksession.fireAllRules();
                assertThat(events.size()).isEqualTo(1);

                // phase 2
                Sprinkler sprinkler1 = new Sprinkler(room1);
                ksession.insert(sprinkler1);
                ksession.fireAllRules();
                assertThat(events.size()).isEqualTo(2);

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
                KieSession ksession = kbase.newKieSession();
                ksession.setGlobal("events", events);

                Room room1 = new Room("Room 1");
                Fire fire1 = new Fire(room1);

                // phase 1
                List<Command> cmds = new ArrayList<Command>();
                cmds.add(CommandFactory.newInsert(room1, "room1"));
                cmds.add(CommandFactory.newInsert(fire1, "fire1"));
                cmds.add(CommandFactory.newFireAllRules());
                ksession.execute(CommandFactory.newBatchExecution(cmds));
                assertThat(events.size()).isEqualTo(1);

                // phase 2
                cmds = new ArrayList<Command>();
                cmds.add(CommandFactory.newInsert(new Sprinkler(room1), "sprinkler1"));
                cmds.add(CommandFactory.newFireAllRules());
                ksession.execute(CommandFactory.newBatchExecution(cmds));
                assertThat(events.size()).isEqualTo(2);

                // phase 3
                cmds = new ArrayList<Command>();
                cmds.add(CommandFactory.newDelete(ksession.getFactHandle(fire1)));
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
        addRule(ruleName, null);
    }

    public static void addRule(String ruleName, Rule firingRule) throws Exception {
        String rule = rules.get(ruleName);

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(staticKieBaseTestConfiguration, true, rule);
        Collection<KiePackage> pkgs = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder).getKiePackages();
        kbase.addPackages(pkgs);

        if (firingRule != null) {
            kbase.removeRule("defaultpkg", firingRule.getName());
        }
    }

    // Rules

    private static Map<String, String> rules = new HashMap<String, String>() {{
        put("raiseAlarm",
                "import " +  DynamicRulesChangesTest.class.getCanonicalName() + "\n " +
                "global java.util.List events\n" +
                "rule \"Raise the alarm when we have one or more fires\"\n" +
                "when\n" +
                "    exists DynamicRulesChangesTest.Fire()\n" +
                "then\n" +
                "    insert( new DynamicRulesChangesTest.Alarm() );\n" +
                "    events.add( \"Raise the alarm\" );\n" +
                "    DynamicRulesChangesTest.addRule(\"onFire\", drools.getRule());\n" +
                "end");

       put("onFire",
               "import " +  DynamicRulesChangesTest.class.getCanonicalName() + "\n " +
               "global java.util.List events\n" +
               "rule \"When there is a fire turn on the sprinkler\"\n" +
               "when\n" +
               "    $fire: DynamicRulesChangesTest.Fire($room : room)\n" +
               "    $sprinkler : DynamicRulesChangesTest.Sprinkler( room == $room, on == false )\n" +
               "then\n" +
               "    modify( $sprinkler ) { setOn( true ) };\n" +
               "    events.add( \"Turn on the sprinkler for room \" + $room.getName() );\n" +
               "    DynamicRulesChangesTest.addRule(\"fireGone\", drools.getRule());\n" +
               "end");

        put("fireGone",
                "import " +  DynamicRulesChangesTest.class.getCanonicalName() + "\n " +
                "global java.util.List events\n" +
                "rule \"When the fire is gone turn off the sprinkler\"\n" +
                "when\n" +
                "    $room : DynamicRulesChangesTest.Room( )\n" +
                "    $sprinkler : DynamicRulesChangesTest.Sprinkler( room == $room, on == true )\n" +
                "    not DynamicRulesChangesTest.Fire( room == $room )\n" +
                "then\n" +
                "    modify( $sprinkler ) { setOn( false ) };\n" +
                "    events.add( \"Turn off the sprinkler for room \" + $room.getName() );\n" +
                "    DynamicRulesChangesTest.addRule(\"cancelAlarm\", drools.getRule());\n" +
                "end");

        put("cancelAlarm",
                "import " +  DynamicRulesChangesTest.class.getCanonicalName() + "\n " +
                "global java.util.List events\n" +
                "rule \"Cancel the alarm when all the fires have gone\"\n" +
                "when\n" +
                "    not DynamicRulesChangesTest.Fire()\n" +
                "    $alarm : DynamicRulesChangesTest.ParentAlarm()\n" +
                "then\n" +
                "    retract( $alarm );\n" +
                "    events.add( \"Cancel the alarm\" );\n" +
                "    DynamicRulesChangesTest.addRule(\"status\", drools.getRule());\n" +
                "end");

        put("status",
                "import " +  DynamicRulesChangesTest.class.getCanonicalName() + "\n " +
                "global java.util.List events\n" +
                "rule \"Status output when things are ok\"\n" +
                "when\n" +
                "    not DynamicRulesChangesTest.Alarm()\n" +
                "    not DynamicRulesChangesTest.Sprinkler( on == true )\n" +
                "then\n" +
                "    events.add( \"Everything is ok\" );\n" +
                "end");
    }};

    // Model

    public static class ParentAlarm { }

    public static class Alarm extends ParentAlarm { }

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
