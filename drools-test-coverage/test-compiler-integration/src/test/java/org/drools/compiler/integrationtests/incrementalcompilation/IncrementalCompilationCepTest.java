/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.incrementalcompilation;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.spi.ObjectType;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.testcoverage.common.model.ChildEventA;
import org.drools.testcoverage.common.model.ChildEventB;
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.model.MyFact;
import org.drools.testcoverage.common.model.ParentEvent;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.Role;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.TimedRuleExecutionOption;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.builder.conf.PropertySpecificOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class IncrementalCompilationCepTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public IncrementalCompilationCepTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @Test
    public void testRemoveRuleAndThenFactInStreamMode() {
        // DROOLS-731
        final String header = "package org.some.test\n" +
                "import " + MyFact.class.getCanonicalName() + "\n";

        final String declaration = "declare MyFact\n" +
                "@role(event)" +
                "end\n";

        final String rule2 = "rule R when\n" +
                "  $FactA : MyFact ($FactA_field2 : currentValue == 105742)\n" +
                "  not MyFact($FactA_field2 == 105742)\n" +
                "then\n" +
                "end\n";

        final String file2 = header + declaration + rule2;

        final KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, file2);

        // Create a session and fire rules
        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession ksession = kc.newKieSession();

        final MyFact myFact = new MyFact("entry:" + 105742, 105742);
        final FactHandle fh = ksession.insert(myFact);

        // Create a new jar for version 1.1.0
        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration);

        // try to update the container to version 1.1.0
        kc.updateToVersion(releaseId2);

        ksession.delete(fh);
    }

    @Test
    public void testAlphaNodeSharingIsOK() {
        // inspired by drools-usage Fmt9wZUFi8g
        // check timer -scheduled activations are preserved if rule untouched by incremental compilation even with alpha node sharing.

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        final String drl = "package org.drools.compiler\n" +
                "global java.util.List list;\n" +
                "global java.util.List list2;\n" +
                "rule R1\n" +
                " timer (int: 3s)\n" +
                " when\n" +
                "   $m : String()\n" +
                " then\n" +
                "   list.add( $m );\n" +
                "   retract( $m );\n" +
                "end\n" +
                "rule RS\n" +
                " timer (int: 3s)\n" +
                " salience 1\n" +
                " when\n" +
                "   $i : Integer()\n" +
                "   $m : String()\n" +
                " then\n" +
                "   System.out.println($i + \" \"+ $m);" +
                "   list2.add($i + \" \"+ $m);\n" +
                "end\n";

        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, drl);

        final KieContainer kc = ks.newKieContainer(releaseId1);

        final KieSessionConfiguration ksconf = ks.newKieSessionConfiguration();
        ksconf.setOption(TimedRuleExecutionOption.YES);
        ksconf.setOption(TimerJobFactoryOption.get("trackable"));
        ksconf.setOption(ClockTypeOption.PSEUDO);

        final KieSession ksession = kc.newKieSession(ksconf);

        final SessionPseudoClock timeService = ksession.getSessionClock();
        timeService.advanceTime(new Date().getTime(), TimeUnit.MILLISECONDS);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final List list2 = new ArrayList();
        ksession.setGlobal("list2", list2);

        ksession.insert("A");
        ksession.insert(1);
        ksession.fireAllRules();

        assertEquals("1. Initial run: no message expected after rule fired immediately after fireAllRules due to duration of 5 sec", 0, list.size());
        assertEquals("1. Initial run: no message expected after rule fired immediately after fireAllRules due to duration of 5 sec", 0, list2.size());

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.1");
        final String drl2 = "package org.drools.compiler\n" +
                "global java.util.List list;\n" +
                "global java.util.List list2;\n" +
                "rule R1\n" +
                " timer (int: 3s)\n" +
                " when\n" +
                "   $m : String()\n" +
                " then\n" +
                "   list.add( $m );\n" +
                "   list.add( $m );\n" +
                "   retract( $m );\n" +
                "end\n" +
                "rule RS\n" +
                " timer (int: 3s)\n" +
                " salience 1\n" +
                " when\n" +
                "   $i : Integer()\n" +
                "   $m : String()\n" +
                " then\n" +
                "   System.out.println($i + \" \"+ $m);" +
                "   list2.add($i + \" \"+ $m);\n" +
                "end\n";

        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration,
                                     KieSessionTestConfiguration.STATEFUL_PSEUDO, new HashMap<>(), drl2);
        kc.updateToVersion(releaseId2);
        timeService.advanceTime(3200, TimeUnit.MILLISECONDS);

        assertEquals("1. R1 is NOT preserved", 0, list.size());
        assertEquals("1. RS is preserved", 1, list2.size());
    }

    @Test
    public void testRemoveRuleWithNonInitializedPath() {
        // DROOLS-1177
        final String drl1 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                        "declare MyEvent @role( event ) end\n" +
                        "rule \"RG_TEST_1\"\n" +
                        "    when\n" +
                        "       $dummy: MyEvent (id == 1)\n" +
                        "		$other: MyEvent (this != $dummy)\n" +
                        "    then\n" +
                        "        retract($other);\n" +
                        "end\n" +
                        "rule \"RG_TEST_2\"\n" +
                        "    when\n" +
                        "       $dummy: MyEvent (id == 1)\n" +
                        "    then\n" +
                        "        retract($dummy);\n" +
                        "end\n";

        final String drl2 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                        "declare IMyEvent @role( event ) end\n" +
                        "rule \"RG_TEST_2\"\n" +
                        "    when\n" +
                        "       $dummy: MyEvent (id == 1)\n" +
                        "    then\n" +
                        "        retract($dummy);\n" +
                        "end\n";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, drl1);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession ksession = kc.newKieSession();

        ksession.insert(new MyEvent(0));

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, drl2);
        kc.updateToVersion(releaseId2);
    }

    public static class FooEvent {

        private final long mytime;

        public FooEvent(final long mytime) {
            this.mytime = mytime;
        }

        public long getMytime() {
            return mytime;
        }
    }

    @Test
    public void testUpdateWithDeclarationPresent() {
        // DROOLS-560
        final String header = "package org.drools.compiler\n"
                + "import " + FooEvent.class.getCanonicalName() + ";\n"
                + "import " + Message.class.getCanonicalName() + ";\n";

        final String declaration = "declare FooEvent\n"
                + " @timestamp( mytime )\n"
                + " @role( event )\n"
                + "end\n";

        final String rule1 = "rule R1 when\n" +
                " $e : FooEvent( )\n" +
                "then\n" +
                " insert(new Message(\"Hello R1\"));\n" +
                "end\n";

        final String rule2 = "rule R1 when\n" +
                " $e : FooEvent( )\n" +
                "then\n" +
                " insert(new Message(\"Hello R2\"));\n" +
                "end\n";

        final String file1 = header + declaration + rule1;
        final String file2 = header + declaration + rule2;

        final KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, file1);

        // Create a session and fire rules
        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession ksession = kc.newKieSession();
        ksession.insert(new FooEvent(0));
        assertEquals(1, ksession.fireAllRules());

        // Create a new jar for version 1.1.0
        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, file2);

        // try to update the container to version 1.1.0
        final Results results = kc.updateToVersion(releaseId2);

        assertFalse("Errors detected on updateToVersion: " + results.getMessages(org.kie.api.builder.Message.Level.ERROR), results.hasMessages(org.kie.api.builder.Message.Level.ERROR));

        // continue working with the session
        ksession.insert(new FooEvent(1));
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testChangeWindowTime() {
        // DROOLS-853
        final String drl1 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                        "global java.util.concurrent.atomic.AtomicInteger result\n" +
                        "declare MyEvent @expires(5m) @role( event ) end\n" +
                        "rule A when\n" +
                        "    accumulate( $e : MyEvent() over window:time(10s), $result : count($e) )\n" +
                        "then" +
                        "    System.out.println(\"Result-1: \" + $result);\n" +
                        "    result.set( $result.intValue() );\n" +
                        "end";

        final String drl2 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                        "global java.util.concurrent.atomic.AtomicInteger result\n" +
                        "declare MyEvent @expires(5m) @role( event ) end\n" +
                        "rule A when\n" +
                        "    accumulate( $e : MyEvent() over window:time(5s), $result : count($e) )\n" +
                        "then" +
                        "    System.out.println(\"Result-2: \" + $result);\n" +
                        "    result.set( $result.intValue() );\n" +
                        "end";

        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl1);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession ksession = kc.newKieSession();

        final PseudoClockScheduler clock = ksession.getSessionClock();

        final AtomicInteger result = new AtomicInteger(0);
        ksession.setGlobal("result", result);

        ksession.insert(new MyEvent(1));
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new MyEvent(2));
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new MyEvent(3));
        ksession.fireAllRules();
        assertEquals(3, result.get());

        // expires 1
        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(2, result.get());

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl2);
        kc.updateToVersion(releaseId2);

        // shorter window: 2 is out
        ksession.fireAllRules();
        assertEquals(1, result.get());

        ksession.insert(new MyEvent(4));
        ksession.insert(new MyEvent(5));
        ksession.fireAllRules();
        assertEquals(3, result.get());

        // expires 3
        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(2, result.get());

        // expires 4 & 5
        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(0, result.get());
    }

    @Test
    public void testIncrementalCompilationWithSlidingWindow() {
        // DROOLS-881
        final String drl1 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                        "declare MyEvent @role( event ) end\n" +
                        "rule A when\n" +
                        "    Number($number : intValue)\n" +
                        "              from accumulate( MyEvent($id : id) over window:time(10s), sum($id) )\n" +
                        "then\n" +
                        "    System.out.println(\"1. SUM : \" + $number);\n" +
                        "end\n" +
                        "\n" +
                        "rule B when\n" +
                        "    Number($number : intValue)\n" +
                        "              from accumulate( MyEvent($id : id) over window:time(10s), count($id) )\n" +
                        "then\n" +
                        "    System.out.println(\"1. CNT : \" + $number);\n" +
                        "end";

        final String drl2 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                        "declare MyEvent @role( event ) end\n" +
                        "rule A when\n" +
                        "    Number($number : intValue)\n" +
                        "              from accumulate( MyEvent($id : id) over window:time(10s), sum($id) )\n" +
                        "then\n" +
                        "    System.out.println(\"2. SUM : \" + $number);\n" +
                        "end\n" +
                        "\n" +
                        "rule B when\n" +
                        "    Number($number : intValue)\n" +
                        "              from accumulate( MyEvent($id : id) over window:time(10s), count($id) )\n" +
                        "then\n" +
                        "    System.out.println(\"2. CNT : \" + $number);\n" +
                        "end";

        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl1);
        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl2);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession ksession = kc.newKieSession();

        final PseudoClockScheduler clock = ksession.getSessionClock();

        ksession.insert(new MyEvent(1));
        ksession.fireAllRules();

        clock.advanceTime(7, TimeUnit.SECONDS);
        kc.updateToVersion(releaseId2);

        ksession.fireAllRules();

        clock.advanceTime(7, TimeUnit.SECONDS);
        kc.updateToVersion(releaseId1);

        ksession.fireAllRules();
    }

    @Test
    public void testDrlRenamingWithEvents() {
        // DROOLS-965
        final String drl1 =
                "import " + SimpleEvent.class.getCanonicalName() + ";\n" +
                        "\n" +
                        "global java.util.concurrent.atomic.AtomicInteger counter1;\n" +
                        "global java.util.concurrent.atomic.AtomicInteger counter2;\n" +
                        "\n" +
                        "declare SimpleEvent\n" +
                        "    @role( event )\n" +
                        "    @timestamp( timestamp )\n" +
                        "    @expires( 2d )\n" +
                        "end\n" +
                        "\n" +
                        "rule R1 when\n" +
                        "    $s:SimpleEvent(code==\"MY_CODE\")\n" +
                        "then\n" +
                        "    counter1.incrementAndGet();\n" +
                        "end\n" +
                        "\n" +
                        "rule R2 when\n" +
                        "    $s:SimpleEvent(code==\"MY_CODE\")\n" +
                        "    not SimpleEvent(this != $s, this after [0,10s] $s)\n" +
                        "then\n" +
                        "    counter2.incrementAndGet();\n" +
                        "end\n";

        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.1");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl1);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession ksession = kc.newKieSession();
        PseudoClockScheduler clock = ksession.getSessionClock();

        final AtomicInteger counter1 = new AtomicInteger(0);
        final AtomicInteger counter2 = new AtomicInteger(0);
        ksession.setGlobal("counter1", counter1);
        ksession.setGlobal("counter2", counter2);

        ksession.insert(new SimpleEvent("1", "MY_CODE", 0));
        ksession.fireAllRules();
        clock.advanceTime(5, TimeUnit.SECONDS);
        ksession.insert(new SimpleEvent("2", "MY_CODE", 5));
        ksession.fireAllRules();

        assertEquals(2, counter1.get());
        assertEquals(0, counter2.get());

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        // the null drl placeholder is used to have the same drl with a different file name
        // this causes the removal and readdition of both rules
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), null, drl1);
        kc.updateToVersion(releaseId2);

        clock = ksession.getSessionClock();
        clock.advanceTime(16, TimeUnit.SECONDS);
        ksession.insert(new SimpleEvent("3", "MY_CODE", 21));
        ksession.fireAllRules();

        if (kieBaseTestConfiguration.getExecutableModelProjectClass().isPresent()) {
            assertEquals(3, counter1.get());
        } else {
            assertEquals(5, counter1.get());
        }
        assertEquals(1, counter2.get());
    }

    public static class SimpleEvent {

        private final String id;
        private final String code;
        private final long timestamp;

        public SimpleEvent(final String eventId, final String code, final long timestamp) {
            this.id = eventId;
            this.code = code;
            this.timestamp = timestamp * 1000L;
        }

        public String getId() {
            return id;
        }

        public String getCode() {
            return code;
        }

        public Date getTimestamp() {
            return new Date(timestamp);
        }

        @Override
        public String toString() {
            return "SimpleEvent(" + id + ")";
        }
    }

    @Test
    public void testIncrementalCompilationWithTimerNode() {
        // DROOLS-1195
        final String drl1 = "package org.drools.test\n" +
                "import " + DummyEvent.class.getCanonicalName() + "\n" +
                "declare DummyEvent\n" +
                "    @role( event )\n" +
                "    @timestamp( eventTimestamp )\n" +
                "end\n" +
                "rule \"RG_TEST_TIMER\"\n" +
                "timer (int: 0 1; start=$expirationTimestamp , repeat-limit=0 )\n" +
                "    when\n" +
                "       $dummy: DummyEvent (id == 'timer', $expirationTimestamp : systemTimestamp )\n" +
                "    then\n " +
                "System.out.println(\"1\");\n" +
                "end\n";

        final String drl2 = "package org.drools.test\n" +
                "import " + DummyEvent.class.getCanonicalName() + "\n" +
                "declare DummyEvent\n" +
                "    @role( event )\n" +
                "    @timestamp( eventTimestamp )\n" +
                "end\n" +
                "rule \"RG_TEST_TIMER_NEW\"\n" +
                "timer (int: 0 1; start=$expirationTimestamp , repeat-limit=0 )\n" +
                "    when\n" +
                "       $dummy: DummyEvent (id == 'timer', $expirationTimestamp : systemTimestamp )\n" +
                "		DummyEvent (id == 'timer_match')\n" +
                "    then\n " +
                "System.out.println(\"1\");\n" +
                "end\n" +
                "rule \"RG_OTHER_RULE\"\n" +
                "    when\n" +
                "       $dummy: DummyEvent ( id == 'timer' )\n" +
                "    then\n " +
                "System.out.println(\"2\");\n" +
                "end\n";

        final long now = System.currentTimeMillis();
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl1);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession kieSession = kc.newKieSession();

        final DummyEvent dummyEvent = new DummyEvent();
        dummyEvent.setId("timer");
        dummyEvent.setEventTimestamp(now);
        dummyEvent.setSystemTimestamp(now + TimeUnit.HOURS.toMillis(1));

        final DummyEvent other = new DummyEvent();
        other.setId("timer_match");
        other.setEventTimestamp(now);

        kieSession.insert(dummyEvent);
        kieSession.insert(other);

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "2.0.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl2);
        kc.updateToVersion(releaseId2);

        final PseudoClockScheduler scheduler = kieSession.getSessionClock();
        scheduler.setStartupTime(now);
        scheduler.advanceTime(1, TimeUnit.DAYS);
        assertEquals(2, kieSession.fireAllRules());
    }

    public static class DummyEvent {

        private String id;
        private long eventTimestamp;
        private long systemTimestamp;

        public DummyEvent() {
        }

        public DummyEvent(final String id) {
            this.id = id;
        }

        public long getEventTimestamp() {
            return eventTimestamp;
        }

        public void setEventTimestamp(final long eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
        }

        public long getSystemTimestamp() {
            return systemTimestamp;
        }

        public void setSystemTimestamp(final long systemTimestamp) {
            this.systemTimestamp = systemTimestamp;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }
    }

    public static class OtherDummyEvent {

        private String id;
        private long eventTimestamp;
        private long systemTimestamp;

        public OtherDummyEvent() {
        }

        public OtherDummyEvent(final String id) {
            this.id = id;
        }

        public long getEventTimestamp() {
            return eventTimestamp;
        }

        public void setEventTimestamp(final long eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
        }

        public long getSystemTimestamp() {
            return systemTimestamp;
        }

        public void setSystemTimestamp(final long systemTimestamp) {
            this.systemTimestamp = systemTimestamp;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }
    }

    @Test
    public void testEventDeclarationInSeparatedDRL() {
        // DROOLS-1241
        final String drl1 =
                "import " + SimpleEvent.class.getCanonicalName() + ";\n" +
                        "declare SimpleEvent\n" +
                        "    @role( event )\n" +
                        "    @timestamp( timestamp )\n" +
                        "    @expires( 2d )\n" +
                        "end\n";

        final String drl2 =
                "import " + SimpleEvent.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R1 when\n" +
                        "    $s:SimpleEvent(code==\"MY_CODE\") over window:time( 1s )\n" +
                        "then\n" +
                        "    list.add(\"MY_CODE\");\n" +
                        "end\n";

        final String drl3 =
                "import " + SimpleEvent.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R2 when\n" +
                        "    $s:SimpleEvent(code==\"YOUR_CODE\") over window:time( 1s )\n" +
                        "then\n" +
                        "    list.add(\"YOUR_CODE\");\n" +
                        "end\n";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-cep-upgrade", "1.1.1");

        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl1, drl2);
        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession ksession = kc.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(new SimpleEvent("1", "MY_CODE", 0));
        ksession.insert(new SimpleEvent("2", "YOUR_CODE", 0));
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("MY_CODE", list.get(0));
        list.clear();

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-cep-upgrade", "1.1.2");
        // the null drl placeholder is used to have the same drl with a different file name
        // this causes the removal and readdition of both rules
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl1, drl2, drl3);
        final Results results = kc.updateToVersion(releaseId2);
        assertEquals(0, results.getMessages().size());

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("YOUR_CODE", list.get(0));
    }

    @Test
    public void testKeepBuilderConfAfterIncrementalUpdate() {
        // DROOLS-1282
        final String drl1 = "import " + DummyEvent.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  DummyEvent() @watch(id)\n" +
                "then end\n";

        final String drl2 = "import " + DummyEvent.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  DummyEvent() @watch(*)\n" +
                "then end\n";

        final KieServices ks = KieServices.Factory.get();

        final Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALWAYS.toString());

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-property-reactive-upgrade", "1");

        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_REALTIME,
                                     kieModuleConfigurationProperties, drl1);
        final KieContainer container = ks.newKieContainer(releaseId1);
        container.newKieSession();

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-property-reactive-upgrade", "2");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_REALTIME,
                                     kieModuleConfigurationProperties, drl2);

        final Results results = container.updateToVersion(releaseId2);
        assertEquals(0, results.getMessages().size());
    }

    @Test
    public void testIncrementalCompilationWithNewEvent() {
        // DROOLS-1395
        final String drl1 = "package org.drools.test\n" +
                "import " + DummyEvent.class.getCanonicalName() + "\n" +
                "declare DummyEvent\n" +
                "    @role( event )\n" +
                "    @timestamp( eventTimestamp )\n" +
                "end\n" +
                "rule \"RG_TEST_1\"\n" +
                "    when\n" +
                "       $event: DummyEvent ()\n" +
                "    then\n" +
                "        System.out.println(\"RG_TEST_1 fired\");\n" +
                "        retract($event);\n" +
                "end";

        final String drl2 = "package org.drools.test\n" +
                "import " + DummyEvent.class.getCanonicalName() + "\n" +
                "import " + OtherDummyEvent.class.getCanonicalName() + "\n" +
                "declare DummyEvent\n" +
                "    @role( event )\n" +
                "    @timestamp( eventTimestamp )\n" +
                "end\n" +
                "declare OtherDummyEvent\n" +
                "    @role( event )\n" +
                "    @timestamp( eventTimestamp )\n" +
                "end\n" +
                "rule \"RG_TEST_2\"\n" +
                "    when\n" +
                "       $event: DummyEvent ()\n" +
                "       $other : OtherDummyEvent(id == $event.id, this after $event)\n" +
                "    then\n" +
                "        System.out.println(\"RG_TEST_2 fired\");\n" +
                "        retract($other);\n" +
                "end\n" +
                "\n" +
                "rule \"RG_TEST_1\"\n" +
                "    when\n" +
                "       $event: DummyEvent ()\n" +
                "    then\n" +
                "        System.out.println(\"RG_TEST_1 fired\");\n" +
                "        retract($event);\n" +
                "end\n";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl1);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession kieSession = kc.newKieSession();

        DummyEvent evt = new DummyEvent("evt");
        kieSession.insert(evt);
        assertEquals(1, kieSession.fireAllRules());

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "2.0.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl2);
        kc.updateToVersion(releaseId2);

        evt = new DummyEvent("evt");
        kieSession.insert(evt);
        final OtherDummyEvent other = new OtherDummyEvent("evt");
        kieSession.insert(other);
        assertEquals(1, kieSession.fireAllRules());
    }

    @Role(Role.Type.EVENT)
    public static class BooleanEvent implements Serializable {

        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
    }

    @Test
    public void testAddRuleWithSlidingWindows() throws Exception {
        // DROOLS-2292
        final String drl1 = "package org.drools.compiler\n" +
                "import " + List.class.getCanonicalName() + "\n" +
                "import " + BooleanEvent.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "    $e : BooleanEvent(!enabled)\n" +
                "    List(size >= 1) from collect ( BooleanEvent(!enabled) over window:time(1) )\n" +
                "    $toEdit : List() from collect( BooleanEvent(!enabled) over window:time(2) )\n" +
                "then\n" +
                "    modify( (BooleanEvent)$toEdit.get(0) ){ setEnabled( true ) }\n" +
                "end\n";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>());
        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "2.0.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl1);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession kieSession = kc.newKieSession();

        kieSession.insert(new BooleanEvent());
        kieSession.fireAllRules();

        kc.updateToVersion(releaseId2);

        kieSession.fireAllRules();

        final KieMarshallers marshallers = ks.getMarshallers();
        final Marshaller marshaller = marshallers.newMarshaller(kieSession.getKieBase());

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshall(outputStream, kieSession);
    }

    public static class MyEvent {

        private final int id;

        public MyEvent(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "MyEvent: " + id;
        }
    }

    @Test
    public void testObjectTypeNodeExpirationOffset() {
        // DROOLS-6296
        final String drl1 = "package org.drools.test;\n" +
                            "import " + ParentEvent.class.getCanonicalName() + "\n" +
                            "import " + ChildEventA.class.getCanonicalName() + "\n" +
                            "import " + ChildEventB.class.getCanonicalName() + "\n" +
                            "\n" +
                            "declare ChildEventA\n" +
                            "   @role( event )\n" +
                            "   @timestamp( eventTimestamp )\n" +
                            "   @expires(3d)\n" +
                            "end;\n" +
                            "declare ChildEventB\n" +
                            "   @role( event )\n" +
                            "   @timestamp( eventTimestamp )\n" +
                            "   @expires(30d)\n" +
                            "end;" +
                            "\n" +
                            "rule \"detect ChildEventA\"\n" +
                            "when $e: ChildEventA()\n" +
                            "then\n" +
                            "    System.out.println(\"detect ChildEventA\");\n" +
                            "end\n" +
                            "\n" +
                            "rule \"detect ParentEvent\"\n" +
                            "when $e: ParentEvent()\n" +
                            "then\n" +
                            "   System.out.println(\"detect ParentEvent\");\n" +
                            "end\n" +
                            "rule \"detect ChildEventB\"\n" +
                            "when $e: ChildEventB()\n" +
                            "then\n" +
                            "   System.out.println(\"detect ChildEventB\");\n" +
                            "end";

        // just adding a new fact
        final String drl2 = "package org.drools.test;\n" +
                            "import " + ParentEvent.class.getCanonicalName() + "\n" +
                            "import " + ChildEventA.class.getCanonicalName() + "\n" +
                            "import " + ChildEventB.class.getCanonicalName() + "\n" +
                            "import " + Person.class.getCanonicalName() + "\n" +
                            "\n" +
                            "declare ChildEventA\n" +
                            "   @role( event )\n" +
                            "   @timestamp( eventTimestamp )\n" +
                            "   @expires(3d)\n" +
                            "end;\n" +
                            "declare ChildEventB\n" +
                            "   @role( event )\n" +
                            "   @timestamp( eventTimestamp )\n" +
                            "   @expires(30d)\n" +
                            "end;" +
                            "\n" +
                            "rule \"detect ChildEventA\"\n" +
                            "when $e: ChildEventA()\n" +
                            "then\n" +
                            "    System.out.println(\"detect ChildEventA\");\n" +
                            "end\n" +
                            "\n" +
                            "rule \"detect ParentEvent\"\n" +
                            "when $e: ParentEvent()\n" +
                            "then\n" +
                            "   System.out.println(\"detect ParentEvent\");\n" +
                            "end\n" +
                            "rule \"detect ChildEventB\"\n" +
                            "when $e: ChildEventB()\n" +
                            "then\n" +
                            "   System.out.println(\"detect ChildEventB\");\n" +
                            "end\n" +
                            "rule \"detect a Person\"\n" +
                            "when $p: Person()\n" +
                            "then\n" +
                            "  System.out.println(\"detect a Person\");\n" +
                            "end";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl1);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final KieSession kieSession = kc.newKieSession();

        NamedEntryPoint entryPoint = (NamedEntryPoint) kieSession.getEntryPoints().stream().findFirst().get();
        Map<ObjectType, ObjectTypeNode> objectTypeNodes = entryPoint.getEntryPointNode().getObjectTypeNodes();

        //ParentEvent
        ObjectTypeNode parentEventOTN = objectTypeNodes.get(new ClassObjectType(ParentEvent.class));
        assertEquals(-1L, parentEventOTN.getExpirationOffset());

        //ChildEventA
        ObjectTypeNode childEventAOTN = objectTypeNodes.get(new ClassObjectType(ChildEventA.class));
        assertEquals(Duration.of(3, ChronoUnit.DAYS).toMillis() + 1, childEventAOTN.getExpirationOffset());

        //ChildEventB
        ObjectTypeNode childEventBOTN = objectTypeNodes.get(new ClassObjectType(ChildEventB.class));
        assertEquals(Duration.of(30, ChronoUnit.DAYS).toMillis() + 1, childEventBOTN.getExpirationOffset());

        //pseudo clock initialization
        long now = System.currentTimeMillis();
        SessionPseudoClock sessionClock = kieSession.getSessionClock();
        sessionClock.advanceTime(now, TimeUnit.MILLISECONDS);

        ChildEventA childEventA = new ChildEventA(new Date(now), "A");
        kieSession.insert(childEventA);
        kieSession.fireAllRules();

        //ChildEventA expires
        sessionClock.advanceTime(Duration.of(4, ChronoUnit.DAYS).toMillis(), TimeUnit.MILLISECONDS);
        kieSession.fireAllRules();

        //ChildEventA is no longer in WM
        assertEquals(0, kieSession.getFactCount());

        kieSession.dispose();

        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "2.0.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), drl2);
        kc.updateToVersion(releaseId2);

        final KieSession kieSession2 = kc.newKieSession();

        NamedEntryPoint entryPoint2 = (NamedEntryPoint) kieSession2.getEntryPoints().stream().findFirst().get();
        Map<ObjectType, ObjectTypeNode> objectTypeNodes2 = entryPoint2.getEntryPointNode().getObjectTypeNodes();

        //ParentEvent
        ObjectTypeNode parentEventOTN2 = objectTypeNodes2.get(new ClassObjectType(ParentEvent.class));
        assertEquals(-1L, parentEventOTN2.getExpirationOffset());

        //ChildEventA
        ObjectTypeNode childEventAOTN2 = objectTypeNodes2.get(new ClassObjectType(ChildEventA.class));
        assertEquals(Duration.of(3, ChronoUnit.DAYS).toMillis() + 1, childEventAOTN2.getExpirationOffset());

        //ChildEventB
        ObjectTypeNode childEventBOTN2 = objectTypeNodes2.get(new ClassObjectType(ChildEventB.class));
        assertEquals(Duration.of(30, ChronoUnit.DAYS).toMillis() + 1, childEventBOTN2.getExpirationOffset());

        now = System.currentTimeMillis();

        SessionPseudoClock sessionClock2 = kieSession2.getSessionClock();
        sessionClock2.advanceTime(now, TimeUnit.MILLISECONDS);

        ChildEventA childEventA2 = new ChildEventA(new Date(now), "A");
        kieSession2.insert(childEventA2);
        kieSession2.fireAllRules();

        //ChildEventA expires
        sessionClock2.advanceTime(Duration.of(4, ChronoUnit.DAYS).toMillis(), TimeUnit.MILLISECONDS);

        kieSession2.fireAllRules();
        assertEquals(0, kieSession2.getFactCount());

        kieSession2.dispose();
    }
}
