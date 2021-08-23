/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.testcoverage.common.model.Alarm;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Sensor;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * see JBPM-4764
 */
@RunWith(Parameterized.class)
public class ActivateAndDeleteOnListenerTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ActivateAndDeleteOnListenerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testActivateOnMatchAndDelete() {
        testActivateOnMatch(new DefaultAgendaEventListener(){
            @Override
            public void matchCreated(final MatchCreatedEvent event) {
                final Collection<? extends FactHandle> alarms = event.getKieRuntime().getFactHandles(new ClassObjectFilter(Alarm.class));
                for (final FactHandle alarm : alarms) {
                    event.getKieRuntime().delete(alarm);
                }
            }
        });
    }

    @Test
    public void testActivateOnMatchAndUpdate() {
        testActivateOnMatch(new DefaultAgendaEventListener(){
            @Override
            public void matchCreated(final MatchCreatedEvent event) {
                final Collection<? extends FactHandle> alarms = event.getKieRuntime().getFactHandles(new ClassObjectFilter(Alarm.class));
                for (final FactHandle alarm : alarms) {
                    event.getKieRuntime().update(alarm, new Alarm());
                }
            }
        });
    }

    private void testActivateOnMatch(final AgendaEventListener listener) {
        final String drl =
                "package org.drools.compiler.integrationtests \n" +
                        "import " + Alarm.class.getCanonicalName() + " \n" +
                        "import " + Sensor.class.getCanonicalName() + " \n" +
                        "rule StringRule  @Propagation(EAGER) ruleflow-group \"DROOLS_SYSTEM\"\n" +
                        " when \n" +
                        " $a : Alarm() \n" +
                        " $s : Sensor() \n" +
                        " then \n" +
                        "end \n";

        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            ksession.addEventListener(listener);

            // go !
            final Alarm alarm = new Alarm();
            alarm.setMessage("test");
            alarm.setNumber(123);

            ksession.insert(alarm);

            final Sensor sensor = new Sensor();
            sensor.setPressure(1);
            sensor.setTemperature(25);

            ksession.insert(sensor);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEagerEvaluationWith2Paths() {
        final String drl =
                "package org.simple \n" +
                "rule xxx \n" +
                "when \n" +
                "  $s : String()\n" +
                "  $i : Integer()\n" +
                "then \n" +
                "end  \n" +
                "rule yyy \n" +
                "when \n" +
                "  $s : String()\n" +
                "  $i : Integer()\n" +
                "then \n" +
                "end  \n";

        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();

            final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
                @Override
                public void matchCreated(final org.kie.api.event.rule.MatchCreatedEvent event) {
                    list.add("activated");
                }

                @Override
                public void matchCancelled(final MatchCancelledEvent event ) {
                    list.add("cancelled");
                }
            };
            ksession.addEventListener(agendaEventListener);

            ksession.insert("test");
            assertEquals(0, list.size());

            final FactHandle fh = ksession.insert(1);
            assertEquals(2, list.size());
            assertEquals("activated", list.get(0));
            assertEquals("activated", list.get(1));

            list.clear();
            ksession.delete( fh );
            assertEquals(2, list.size());
            assertEquals("cancelled", list.get(0));
            assertEquals("cancelled", list.get(1));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEagerEvaluationWith2SubPaths() {
        final String drl =
                "package org.simple \n" +
                "rule xxx \n" +
                "when \n" +
                "  $s : String()\n" +
                "  exists( Integer() or Long() )\n" +
                "then \n" +
                "end  \n" +
                "rule yyy \n" +
                "when \n" +
                "  $s : String()\n" +
                "  exists( Integer() or Long() )\n" +
                "then \n" +
                "end  \n";

        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();

            final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
                public void matchCreated(final org.kie.api.event.rule.MatchCreatedEvent event) {
                    list.add("activated");
                }
            };
            ksession.addEventListener(agendaEventListener);

            ksession.insert("test");
            assertEquals(0, list.size());

            ksession.insert(1);
            assertEquals(2, list.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOneLinkedAndOneUnlinkedPath() {
        final String drl =
                "package org.simple \n" +
                "rule xxx \n" +
                "when \n" +
                "  String()\n" +
                "  Integer()\n" +
                "  Long()\n" +
                "then \n" +
                "end  \n" +
                "rule yyy \n" +
                "when \n" +
                "  String()\n" +
                "  Integer()\n" +
                "  Boolean()\n" +
                "then \n" +
                "end  \n";

        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();

            final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
                public void matchCreated(final org.kie.api.event.rule.MatchCreatedEvent event) {
                    list.add(event.getMatch().getRule().getName());
                }
            };
            ksession.addEventListener(agendaEventListener);

            ksession.insert("test");
            assertEquals(0, list.size());

            ksession.insert(Boolean.TRUE);
            assertEquals(0, list.size());

            ksession.insert(1);
            assertEquals(1, list.size());
            assertEquals("yyy", list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOneLazyAndOneImmediateSubPathFromLia() {
        final String drl =
                "package org.simple \n" +
                "rule xxx \n" +
                "when \n" +
                "  $s : String()\n" +
                "  exists( Integer() or Long() )\n" +
                "then \n" +
                "end  \n" +
                "rule yyy \n" +
                "when \n" +
                "  $s : String()\n" +
                "  exists( Integer() or Long() )\n" +
                "then \n" +
                "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("activate-delete-test", kieBaseTestConfiguration, drl);
        final KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( new ForceEagerActivationOption.FILTERED(rule -> rule.getName().equals("yyy")));

        final List<String> list = new ArrayList<>();

        final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
            public void matchCreated(final org.kie.api.event.rule.MatchCreatedEvent event) {
                list.add(event.getMatch().getRule().getName());
            }
        };

        KieSession ksession = null;

        // scenario 1
        ksession = kbase.newKieSession(conf, null);
        try {
            list.clear();
            ksession.addEventListener(agendaEventListener);

            ksession.insert("test");
            assertEquals(0, list.size());

            ksession.insert(1);
            assertEquals(1, list.size());
            assertEquals("yyy", list.get(0));

            list.clear();
            ksession.fireAllRules();
            assertEquals(1, list.size());
            assertEquals("xxx", list.get(0));
        } finally {
            ksession.dispose();
        }

        // scenario 2
        ksession = kbase.newKieSession(conf, null);
        try {
            list.clear();
            ksession.addEventListener(agendaEventListener);

            ksession.insert("test");
            assertEquals(0, list.size());

            ksession.insert(Long.valueOf(1));
            assertEquals(1, list.size());
            assertEquals("yyy", list.get(0));

            list.clear();
            ksession.fireAllRules();
            assertEquals(1, list.size());
            assertEquals("xxx", list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOneLazyAndOneImmediateSubPathAfterLia() {
        final String drl =
              "package org.simple \n" +
              "global java.util.List list; \n" +
              "rule xxx \n" +
              "when \n" +
              "  Integer(this == 0)\n" +
              "  $s : String()\n" +
              "  exists( ( Integer(this == 3) and eval(list.add(\"e1\"))) or (Long(this == 1) and eval(list.add(\"e2\"))) )\n" +
              "then \n" +
              "end  \n" +
              "rule yyy \n" +
              "when \n" +
              "  Integer(this == 0)\n" +
              "  $s : String()\n" +
              "  exists( ( Integer(this == 3) and eval(list.add(\"e1\"))) or (Long(this == 1) and eval(list.add(\"e2\"))) )\n" +
              "then \n" +
              "end  \n" +
              "rule zzz \n" +
              "when \n" +
              "  Integer(this == 0)\n" +
              "  $s : String()\n" +
              "  eval(1==1)\n" +
              "then \n" +
              "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("activate-delete-test", kieBaseTestConfiguration, drl);
        final KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( new ForceEagerActivationOption.FILTERED(rule -> rule.getName().equals("yyy")));

        final List<String> list = new ArrayList<>();
        final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
            public void matchCreated(final org.kie.api.event.rule.MatchCreatedEvent event) {
                list.add(event.getMatch().getRule().getName());
            }
        };

        KieSession ksession = null;

        // scenario 1 - Only insert the Integer side of 'or'
        ksession = kbase.newKieSession(conf, null);
        try {
            list.clear();
            ksession.setGlobal("list", list);
            ksession.addEventListener(agendaEventListener);

            ksession.insert("test");
            assertEquals(0, list.size());

            ksession.insert(0);
            ksession.insert(3);
            assertEquals("[e1, yyy]", list.toString());

            list.clear();
            ksession.fireAllRules();
            assertEquals("[xxx, zzz]", list.toString());
        } finally {
            ksession.dispose();
        }

        // Scenario 2 - Only insert the Long side of 'or'
        ksession = kbase.newKieSession(conf, null);
        try {
            list.clear();
            ksession.setGlobal("list", list);
            ksession.addEventListener(agendaEventListener);

            ksession.insert("test");
            assertEquals(0, list.size());

            ksession.insert(0);
            ksession.insert(Long.valueOf(1));
            assertEquals("[e2, yyy]", list.toString());

            list.clear();
            ksession.fireAllRules();
            assertEquals("[xxx, zzz]", list.toString());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOrPropagatesThroughSubnetwork() {
        final String drl =
              "package org.simple \n" +
              "global java.util.List list; \n" +
              "rule yyy \n" +
              "when \n" +
              "  Integer(this==1)\n" +
              "  String()\n" +
              "  exists( ( Integer(this == 3) and eval(list.add(\"e1\"))) or (Long(this == 4) and eval(list.add(\"e2\"))) )\n" +
              "then \n" +
              "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("activate-delete-test", kieBaseTestConfiguration, drl);
        final KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( new ForceEagerActivationOption.FILTERED(rule -> rule.getName().equals("yyy")));

        final List<String> list = new ArrayList<>();

        final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
            public void matchCreated(final org.kie.api.event.rule.MatchCreatedEvent event) {
                list.add("add:" + event.getMatch().getRule().getName());
            }

            public void matchCancelled(final org.kie.api.event.rule.MatchCancelledEvent event) {
                list.add("rem:" + event.getMatch().getRule().getName());
            }
        };

        KieSession ksession = null;

        // scenario 1 - Add Integer, then Long (with no change), then delete Integer and check not holds. Then delete Long
        ksession = kbase.newKieSession(conf, null);
        try {
            ksession.addEventListener(agendaEventListener);

            list.clear();
            ksession.setGlobal("list", list);
            ksession.insert("test");
            ksession.insert(1);
            assertEquals(0, list.size());

            FactHandle fhInt3 = ksession.insert(3);
            assertEquals("[e1, add:yyy]", list.toString());

            // No change as the int 1 blocks a token propagating from the not node needed for the long join
            list.clear();
            FactHandle fhLong4 = ksession.insert(Long.valueOf(4));
            assertEquals(0, list.size());

            ksession.delete(fhInt3);
            assertEquals("[e2]", list.toString());

            list.clear();
            ksession.delete(fhLong4);
            assertEquals("[rem:yyy]", list.toString());
        } finally {
            ksession.dispose();
        }

        // Scenario 2 - Add Long, then Integer (with no change), then delete Long and check not holds. Then delete Integer
        ksession = kbase.newKieSession(conf, null);
        try {
            ksession.addEventListener(agendaEventListener);

            list.clear();
            ksession.setGlobal("list", list);
            ksession.insert("test");
            ksession.insert(1);
            assertEquals(0, list.size());

            FactHandle fhLong4 = ksession.insert(Long.valueOf(4));
            assertEquals("[e2, add:yyy]", list.toString());

            // Unlike scenario  1, e1 eval is not blocked so it will still eval, but the outer not still holds so no over all change.
            list.clear();
            FactHandle fhInt3 = ksession.insert(3);
            assertEquals("[e1]", list.toString());

            list.clear();
            ksession.delete(fhLong4);
            assertEquals(0, list.size());

            list.clear();
            ksession.delete(fhInt3);
            assertEquals("[rem:yyy]", list.toString());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEagerEvaluationWithSubSubPath() {
        final String drl =
                "package org.simple \n" +
                "rule xxx \n" +
                "when \n" +
                "  $s : String()\n" +
                "  exists( Boolean() and not(not(Integer()) and not(Double())) )\n" +
                "then \n" +
                "end  \n";

        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();

            final AgendaEventListener agendaEventListener = new org.kie.api.event.rule.DefaultAgendaEventListener() {
                public void matchCreated(final org.kie.api.event.rule.MatchCreatedEvent event) {
                    list.add("activated");
                }
            };
            ksession.addEventListener(agendaEventListener);

            ksession.insert(Boolean.TRUE);
            assertEquals(0, list.size());

            ksession.insert("test");
            assertEquals(0, list.size());

            ksession.insert(1);
            assertEquals(1, list.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testSegMemInitializationWithForceEagerActivation() {
        // DROOLS-1247
        final String drl = "global java.util.List list\n" +
                     "declare  SimpleFact end\n" +
                     "declare  AnotherFact end\n" +
                     "\n" +
                     "rule Init when\n" +
                     "    not (SimpleFact())\n" +
                     "    not (AnotherFact())\n" +
                     "then\n" +
                     "    insert(new SimpleFact());\n" +
                     "    insert(new AnotherFact());\n" +
                     "end\n" +
                     "\n" +
                     "rule R1 no-loop when\n" +
                     "    $f : SimpleFact()  \n" +
                     "    $h : AnotherFact() \n" +
                     "    $s : String() \n" +
                     "    eval(true)\n" +
                     "then\n" +
                     "    list.add(\"1\");\n" +
                     "end\n" +
                     "\n" +
                     "rule R2 no-loop when\n" +
                     "    $f : SimpleFact()  \n" +
                     "    $h : AnotherFact()  \n" +
                     "    $s : String() \n" +
                     "then\n" +
                     "    list.add(\"2\");\n" +
                     "end";

        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal( "list", list );

            ksession.insert( "test" );
            ksession.fireAllRules();

            assertEquals( 2, list.size() );
            assertTrue( list.containsAll( asList("1", "2") ) );
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testSegMemInitializationWithForceEagerActivationAndAcc() {
        // DROOLS-1247
        final String drl = "global java.util.List list\n" +
                     "declare  SimpleFact end\n" +
                     "declare  AnotherFact end\n" +
                     "\n" +
                     "rule Init when\n" +
                     "    not (SimpleFact())\n" +
                     "    not (AnotherFact())\n" +
                     "then\n" +
                     "    insert(new SimpleFact());\n" +
                     "    insert(new AnotherFact());\n" +
                     "end\n" +
                     "\n" +
                     "rule R1 no-loop when\n" +
                     "    $f : SimpleFact()  \n" +
                     "    $h : AnotherFact() \n" +
                     "    $s : String() \n" +
                     "    accumulate($i: Integer(), $res : count($i))\n" +
                     "then\n" +
                     "    list.add(\"1\");\n" +
                     "end\n" +
                     "\n" +
                     "rule R2 no-loop when\n" +
                     "    $f : SimpleFact()  \n" +
                     "    $h : AnotherFact()  \n" +
                     "    $s : String() \n" +
                     "then\n" +
                     "    list.add(\"2\");\n" +
                     "end";

        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal( "list", list );

            ksession.insert( "test" );
            ksession.fireAllRules();

            assertEquals( 2, list.size() );
            assertTrue( list.containsAll( asList("1", "2") ) );
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testSegMemInitializationWithForceEagerActivationAndExistsWithNots() {
        // DROOLS-1247
        final String drl = "global java.util.List list\n" +
                     "declare  SimpleFact end\n" +
                     "declare  AnotherFact end\n" +
                     "\n" +
                     "rule Init when\n" +
                     "    not (SimpleFact())\n" +
                     "    not (AnotherFact())\n" +
                     "then\n" +
                     "    insert(new SimpleFact());\n" +
                     "    insert(new AnotherFact());\n" +
                     "end\n" +
                     "\n" +
                     "rule R1 no-loop when\n" +
                     "    $f : SimpleFact()  \n" +
                     "    $h : AnotherFact() \n" +
                     "    $s : String() \n" +
                     "    exists(not(Integer()) or not(Double()))\n" +
                     "then\n" +
                     "    list.add(\"1\");\n" +
                     "end\n" +
                     "\n" +
                     "rule R2 no-loop when\n" +
                     "    $f : SimpleFact()  \n" +
                     "    $h : AnotherFact()  \n" +
                     "    $s : String() \n" +
                     "then\n" +
                     "    list.add(\"2\");\n" +
                     "end";

        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal( "list", list );

            ksession.insert( "test" );
            ksession.fireAllRules();

            assertEquals( 2, list.size() );
            assertTrue( list.containsAll( asList("1", "2") ) );
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testNoLoopWithForceEagerActivation() {
        // DROOLS-1349
        final String drl = "import " + Person.class.getCanonicalName() + "\n" +
                     "\n" +
                     "rule Birthday no-loop when\n" +
                     "    $p: Person()\n" +
                     "then\n" +
                     "    modify($p) { setAge($p.getAge()+1) };\n" +
                     "end";

        final KieSession ksession = getSessionWithEagerActivation(drl);
        try {
            final Person mario = new Person("mario", 42);

            ksession.insert( mario );
            ksession.fireAllRules();

            assertEquals( 43, mario.getAge() );
        } finally {
            ksession.dispose();
        }
    }

    private KieSession getSessionWithEagerActivation(final String drl) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("activate-delete-test", kieBaseTestConfiguration, drl);
        final KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ForceEagerActivationOption.YES );
        return kbase.newKieSession(conf, null);
    }
}
