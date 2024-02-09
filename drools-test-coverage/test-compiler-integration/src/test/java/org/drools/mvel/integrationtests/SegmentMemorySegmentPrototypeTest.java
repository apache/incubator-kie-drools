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
import java.util.List;

import org.drools.core.impl.RuleBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.ReteDumper;
import org.drools.core.reteoo.TerminalNode;
import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.integrationtests.DynamicRulesChangesTest.Fire;
import org.drools.mvel.integrationtests.DynamicRulesChangesTest.Room;
import org.drools.mvel.integrationtests.DynamicRulesChangesTest.Sprinkler;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class SegmentMemorySegmentPrototypeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SegmentMemorySegmentPrototypeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private static final String DRL =
            "import " +  DynamicRulesChangesTest.class.getCanonicalName() + "\n " +
            "global java.util.List events\n" +
            "rule \"Raise the alarm when we have one or more fires\"\n" +
            "when\n" +
            "    exists DynamicRulesChangesTest.Fire()\n" +
            "then\n" +
            "    insert( new DynamicRulesChangesTest.Alarm() );\n" +
            "    events.add( \"Raise the alarm\" );\n" +
            "end" +
            "\n" +
            "rule \"When there is a fire turn on the sprinkler\"\n" +
            "when\n" +
            "    $fire: DynamicRulesChangesTest.Fire($room : room)\n" +
            "    $sprinkler : DynamicRulesChangesTest.Sprinkler( room == $room, on == false )\n" +
            "then\n" +
            "    modify( $sprinkler ) { setOn( true ) };\n" +
            "    events.add( \"Turn on the sprinkler for room \" + $room.getName() );\n" +
            "end" +
            "\n" +
            "rule \"When the fire is gone turn off the sprinkler\"\n" +
            "when\n" +
            "    $room : DynamicRulesChangesTest.Room( )\n" +
            "    $sprinkler : DynamicRulesChangesTest.Sprinkler( room == $room, on == true )\n" +
            "    not DynamicRulesChangesTest.Fire( room == $room )\n" +
            "then\n" +
            "    modify( $sprinkler ) { setOn( false ) };\n" +
            "    events.add( \"Turn off the sprinkler for room \" + $room.getName() );\n" +
            "end" +
            "\n" +
            "rule \"Cancel the alarm when all the fires have gone\"\n" +
            "when\n" +
            "    not DynamicRulesChangesTest.Fire()\n" +
            "    $alarm : DynamicRulesChangesTest.Alarm()\n" +
            "then\n" +
            "    retract( $alarm );\n" +
            "    events.add( \"Cancel the alarm\" );\n" +
            "end" +
            "\n" +
            "rule \"Status output when things are ok\"\n" +
            "when\n" +
            "    not DynamicRulesChangesTest.Fire()\n" +
            "    not DynamicRulesChangesTest.Alarm()\n" +
            "    not DynamicRulesChangesTest.Sprinkler( on == true )\n" +
            "then\n" +
            "    events.add( \"Everything is ok\" );\n" +
            "end";

    @Test
    public void testSegmentMemoryPrototype() {
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, DRL);
        KieSession ksession = kbase.newKieSession();
        try {
            checkKieSession(ksession);
        } finally {
            ksession.dispose();
        }

        // Create a 2nd KieSession (that will use segment memory prototype) and check that it works as the former one
        KieSession ksession2 = kbase.newKieSession();
        try {
            checkKieSession(ksession2);
        } finally {
            ksession2.dispose();
        }
    }

    @Test
    public void testSessionCache() {
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, DRL);

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kbase.newKieSession();
        try {
            checkKieSession(ksession);
        } finally {
            try {
                ksession.reset();
                checkKieSession(ksession);
            } finally {
                ksession.dispose();
            }
        }
    }

    private void checkKieSession(KieSession ksession) {
        final List<String> events = new ArrayList<String>();

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
        ksession.delete(fireFact1);
        ksession.fireAllRules();
        assertThat(events.size()).isEqualTo(5);
    }

    @Test
    public void testEnsureRiaSegmentCreationUsingPrototypes() {
        // DROOLS-1739
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Address.class.getCanonicalName() + "\n" +
                "import java.util.List;\n" +
                "\n" +
                "rule rule1 when\n" +
                "    $personE : List()\n" +
                "    Person( ) from $personE\n" +
                "then end\n" +
                "\n" +
                "rule rule2 when\n" +
                "    $personE : List()\n" +
                "    Person( $addresses : addresses ) from $personE\n" +
                "    $address : Address( ) from $addresses\n" +
                "    not (Address( this != $address ) from $addresses)\n" +
                "    String(  )\n" +
                "then end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);

        KieSessionConfiguration conf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ForceEagerActivationOption.YES );

        KieSession ksession = kbase.newKieSession( conf, null );
        try {
            ksession.insert(List.of(new Person()));
            ksession.insert("test");
            assertThat(ksession.fireAllRules()).isEqualTo(1);
        } finally {
            ksession.dispose();
            try {
                ksession = kbase.newKieSession( conf, null );
                ksession.insert(List.of(new Person()));
                ksession.insert("test");
                assertThat(ksession.fireAllRules()).isEqualTo(1);
            } finally {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testSessionReset() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Address.class.getCanonicalName() + "\n" +
                "import java.util.List;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "    String( length == $i )\n" +
                "    Long()\n" +
                "then end\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "    String( length == $i )\n" +
                "    Boolean()\n" +
                "then end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);

        List<TerminalNode> terminalNodes = ReteDumper.collectRete( kbase ).stream()
                .filter( TerminalNode.class::isInstance )
                .map( TerminalNode.class::cast ).collect( toList() );

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl) kbase.newKieSession();

        ksession.insert( 4 );
        ksession.insert( 4L );
        ksession.insert( true );
        ksession.insert( "test" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);


        assertThat(terminalNodes.stream().map(ksession::getNodeMemory).map(PathMemory.class::cast)
                .allMatch(PathMemory::isRuleLinked)).isTrue();

        ksession.reset();

        assertThat(terminalNodes.stream().map(ksession::getNodeMemory).map(PathMemory.class::cast)
                .anyMatch(PathMemory::isRuleLinked)).isFalse();

        ksession.insert( 4 );
        ksession.insert( 4L );
        ksession.insert( "test" );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }
}
