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

package org.drools.integrationtests;

import org.drools.Address;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.rule.AgendaEventListener;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.impl.AgendaImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Run all the tests with the ReteOO engine implementation
 */
public class MiscTest2 extends CommonTestMethodBase {

    @Test
    public void testUpdateWithNonEffectiveActivations() throws Exception {
        // JBRULES-3604
        String str = "package inheritance\n" +
                "\n" +
                "import org.drools.Address\n" +
                "\n" +
                "rule \"Parent\"\n" +
                "    enabled false\n" +
                "    when \n" +
                "        $a : Address(suburb == \"xyz\")\n" +
                "    then \n" +
                "        System.out.println( $a ); \n" +
                "end \n" +
                "rule \"Child\" extends \"Parent\" \n" +
                "    when \n" +
                "        $b : Address( this == $a, street == \"123\")\n" +
                "    then \n" +
                "        System.out.println( $b ); \n" +
                "end";


        KnowledgeBase knowledgeBase = null;

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL);

        if ( builder.hasErrors() ) {
            throw new RuntimeException(builder.getErrors().toString());
        }
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

        Address address = new Address();

        address.setSuburb("xyz");
        org.drools.runtime.rule.FactHandle addressHandle = ksession.insert(address);

        int rulesFired = ksession.fireAllRules();

        assertEquals( 0, rulesFired );

        address.setStreet("123");


        ksession.update(addressHandle, address);

        rulesFired = ksession.fireAllRules();

        System.out.println( rulesFired );
        assertEquals( 1, rulesFired );

        ksession.dispose();
    }

    @Test
    public void testReuseAgendaAfterException() throws Exception {
        // JBRULES-3677

        String str = "import org.drools.Person;\n" +
                "global java.util.List results;" +
                "rule R1\n" +
                "ruleflow-group \"test\"\n" +
                "when\n" +
                "   Person( $age : age ) \n" +
                "then\n" +
                "   if ($age > 40) throw new RuntimeException(\"Too old\");\n" +
                "   results.add(\"OK\");" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> res = new ArrayList<String>();
        ksession.setGlobal( "results", res );

        AgendaEventListener agendaEventListener = new AgendaEventListener() {
            public void activationCreated(org.drools.event.rule.ActivationCreatedEvent event) {
            }

            public void activationCancelled(org.drools.event.rule.ActivationCancelledEvent event) {
            }

            public void beforeActivationFired(org.drools.event.rule.BeforeActivationFiredEvent event) {
            }

            public void afterActivationFired(org.drools.event.rule.AfterActivationFiredEvent event) {
            }

            public void agendaGroupPopped(org.drools.event.rule.AgendaGroupPoppedEvent event) {
            }

            public void agendaGroupPushed(org.drools.event.rule.AgendaGroupPushedEvent event) {
            }

            public void beforeRuleFlowGroupActivated(org.drools.event.rule.RuleFlowGroupActivatedEvent event) {
            }

            public void afterRuleFlowGroupActivated(org.drools.event.rule.RuleFlowGroupActivatedEvent event) {
                ksession.fireAllRules();
            }

            public void beforeRuleFlowGroupDeactivated(org.drools.event.rule.RuleFlowGroupDeactivatedEvent event) {
            }

            public void afterRuleFlowGroupDeactivated(org.drools.event.rule.RuleFlowGroupDeactivatedEvent event) {
            }
        };

        ksession.addEventListener(agendaEventListener);

        FactHandle fact1 = ksession.insert(new Person("Mario", 38));
        ((AgendaImpl)ksession.getAgenda()).activateRuleFlowGroup("test");

        assertEquals(1, res.size());
        res.clear();

        ksession.retract(fact1);

        FactHandle fact2 = ksession.insert(new Person("Mario", 48));
        try {
            ((AgendaImpl)ksession.getAgenda()).activateRuleFlowGroup("test");
            fail("should throw an Exception");
        } catch (Exception e) { }
        ksession.retract(fact2);

        assertEquals(0, res.size());

        // try to reuse the ksession after the Exception
        FactHandle fact3 = ksession.insert(new Person("Mario", 38));
        ((AgendaImpl)ksession.getAgenda()).activateRuleFlowGroup("test");
        assertEquals(1, res.size());
        ksession.retract(fact3);

        ksession.dispose();
    }

    @Test
    public void testTypeCheckInOr() {
        // BZ-1029911
        String str = "import org.drools.*;\n" +
                     "import java.util.*;\n" +
                     "\n" +
                     "rule \"rule test\"\n" +
                     "    dialect \"java\"\n" +
                     "    \n" +
                     "    when\n" +
                     "        scenario: ScenarioType( this == ScenarioType.Set.ADD || this == ScenarioType.Set.EDIT  );\n" +
                     "        \n" +
                     "    then    \n" +
                     "        System.out.println(\"Test\");\n" +
                     "\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.fireAllRules();
    }

    @Test
    public void testNullValueInFrom() {
        // DROOLS-71
        String str =
                "global java.util.List list\n" +
                "\n" +
                "rule R\n" +
                "when\n" +
                "    $i : Integer( ) from list\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        list.add(1);
        list.add(null);
        list.add(2);

        ksession.fireAllRules();
    }
}
