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
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.RuleBaseConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeBuilderResult;
import org.drools.builder.ResourceType;
import org.drools.builder.ResultSeverity;
import org.drools.common.DefaultFactHandle;
import org.drools.conflict.SalienceConflictResolver;
import org.drools.core.util.FileManager;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.type.Modifies;
import org.drools.definition.type.Position;
import org.drools.definition.type.PropertyReactive;
import org.drools.event.knowledgebase.DefaultKnowledgeBaseEventListener;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.impl.AgendaImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Run all the tests with the ReteOO engine implementation
 */
public class MiscTest2 extends CommonTestMethodBase {

    private static final Logger logger = LoggerFactory.getLogger(MiscTest2.class);

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

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL);

        if ( builder.hasErrors() ) {
            throw new RuntimeException(builder.getErrors().toString());
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
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
    public void testClassNotFoundAfterDeserialization() throws Exception {
        // JBRULES-3670
        String drl =
                "package completely.other.deal;\n" +
                "\n" +
                "declare Person\n" +
                "   firstName : String\n" +
                "   lastName : String\n" +
                "end\n" +
                "\n" +
                "rule \"now use it B\"\n" +
                "   when\n" +
                "       Person( $christianName, $surname; )\n" +
                "   then\n" +
                "       insert( new Person( $christianName, null ) );\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException("" + kbuilder.getErrors());
        }

        FileManager fileManager = new FileManager();
        fileManager.setUp();

        try {
            File root = fileManager.getRootDirectory();

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(root, "test.drl.compiled")));
            out.writeObject( kbuilder.getKnowledgePackages());
            out.close();

            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(root, "test.drl.compiled")));
            kbase.addKnowledgePackages((Collection<KnowledgePackage>) in.readObject());
            in.close();
        } finally {
            fileManager.tearDown();
        }
    }

    @Test
    public void testAnalyzeConditionWithVariableRegExp() throws Exception {
        // JBRULES-3659
        String str =
                "dialect \"mvel\"\n" +
                "\n" +
                "declare Person\n" +
                "   name : String\n" +
                "end\n" +
                "declare Stuff\n" +
                "   regexp : String\n" +
                "end\n" +
                "\n" +
                "rule \"Test Regex\"\n" +
                "   salience 100\n" +
                "    when\n" +
                "    then\n" +
                "       insert (new Stuff(\"Test\"));\n" +
                "       insert (new Person(\"Test\"));\n" +
                "end\n" +
                "\n" +
                "rule \"Test Equality\"\n" +
                "   salience 10\n" +
                "    when\n" +
                "       Stuff( $regexp : regexp )\n" +
                "        Person( name matches $regexp )\n" +
                "        //Person( name matches \"Test\" )\n" +
                "    then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testShareAlphaNodesRegardlessDoubleOrSingleQuotes() {
        // JBRULES-3640
        String str =
                "declare RecordA\n" +
                "   id : long\n" +
                "end\n" +
                "\n" +
                "declare RecordB\n" +
                "   id : long\n" +
                "role : String\n" +
                "end\n" +
                "\n" +
                "rule \"insert data 1\"\n" +
                "   salience 10\n" +
                "   when\n" +
                "   then\n" +
                "       insert (new RecordA(100));\n" +
                "       insert (new RecordB(100, \"1\"));\n" +
                "       insert (new RecordB(100, \"2\"));\n" +
                "end\n" +
                "\n" +
                "rule \"test 1\"\n" +
                "   when\n" +
                "       a : RecordA( )\n" +
                "       b : RecordB( id == b.id, role == '1' )\n" +
                "   then\n" +
                "end\n" +
                "\n" +
                "rule \"test 2\"\n" +
                "   when\n" +
                "       a : RecordA( )\n" +
                "       b : RecordB( id == b.id, role == \"1\" )\n" +
                "   then\n" +
                "end\n" +
                "\n" +
                "rule \"test 3\"\n" +
                "   when\n" +
                "       a : RecordA( )\n" +
                "       b : RecordB( id == b.id, role == \"2\" )\n" +
                "   then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        assertEquals(4, ksession.fireAllRules());
    }

    @Test
    public void testKnowledgeBaseEventSupportLeak() throws Exception {
        // JBRULES-3666
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeBaseEventListener listener = new DefaultKnowledgeBaseEventListener();
        kbase.addEventListener(listener);
        kbase.addEventListener(listener);
        assertEquals(1, ((KnowledgeBaseImpl) kbase).getRuleBase().getRuleBaseEventListeners().size());
        kbase.removeEventListener(listener);
        assertEquals(0, ((KnowledgeBaseImpl) kbase).getRuleBase().getRuleBaseEventListeners().size());
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
    public void testMVELForLoop() throws Exception {
        // JBRULES-3717
        String str = "rule demo\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "   for ( int i = 1; i <= 3; i++ ) {\n" +
                "       insert( \"foo\" + i );\n" +
                "   }\n" +
                "end";

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL);

        if ( builder.hasErrors() ) {
            throw new RuntimeException(builder.getErrors().toString());
        }
    }

    @Test
    public void testBigDecimalComparison() throws Exception {
        // JBRULES-3715
        String str = "import org.drools.Person;\n" +
                "rule \"Big Decimal Comparison\"\n" +
                "    dialect \"mvel\"\n" +
                "when\n" +
                "    Person( bigDecimal == 0.0B )\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person p = new Person("Mario", 38);
        p.setBigDecimal(new BigDecimal("0"));
        ksession.insert(p);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test(timeout = 5000)
    public void testInfiniteLoopCausedByInheritance() throws Exception {
        // DROOLS-13
        String str =
                "declare Parent\n" +
                "    active : boolean\n" +
                "end\n" +
                " \n" +
                "declare Child extends Parent\n" +
                "end\n" +
                " \n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "    insert( new Child( false ) );\n" +
                "end\n" +
                " \n" +
                "rule \"Print\"\n" +
                "when\n" +
                "    $g : Child( active == true )\n" +
                "then\n" +
                "end\n" +
                " \n" +
                " \n" +
                "rule \"Switch\"\n" +
                "when\n" +
                "    $item : Parent( active == false )\n" +
                "then\n" +
                "    modify ( $item ) {\n" +
                "            setActive( true );\n" +
                "    }\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();
    }

    @Test
    public void testIntSorting() {
        // DROOLS-15
        String str =
                "global java.util.List list\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "   $number : Number()\n" +
                "   not Number(intValue < $number.intValue)\n" +
                "then\n" +
                "   list.add($number);\n" +
                "   retract($number);\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(5);
        ksession.insert(6);
        ksession.insert(4);
        ksession.insert(1);
        ksession.insert(2);

        ksession.fireAllRules();

        assertEquals(Arrays.asList(1, 2, 4, 5, 6), list);
    }

    @Test
    public void testIntSorting2() {
        // DROOLS-15
        String str =
                "global java.util.List list\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "   $number : Number()\n" +
                "   not Number(intValue > $number.intValue)\n" +
                "then\n" +
                "   list.add($number);\n" +
                "   retract($number);\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(3);
        ksession.insert(7);
        ksession.insert(4);
        ksession.insert(5);
        ksession.insert(2);
        ksession.insert(1);
        ksession.insert(6);

        ksession.fireAllRules();

        assertEquals(Arrays.asList(7, 6, 5, 4, 3, 2, 1), list);
    }

    @Test(timeout = 5000)
    public void testPropertyReactiveOnAlphaNodeFollowedByAccumulate() {
        // DROOLS-16
        String str =
                "package org.drools.pmml.pmml_4_1.test;\n" +
                "\n" +
                "declare Charge\n" +
                "    context     : String\n" +
                "    index       : String = \"-1\"\n" +
                "    source      : String = \"-1\"\n" +
                "    value       : double\n" +
                "end\n" +
                "\n" +
                "declare Neuron\n" +
                "@propertyReactive\n" +
                "    context     : String            @key\n" +
                "    index       : String            @key\n" +
                "    layerIndex  : int\n" +
                "    bias        : double\n" +
                "    fanIn       : int\n" +
                "    value       : double\n" +
                "    dvalue      : double\n" +
                "    normalized  : boolean\n" +
                "end\n" +
                "\n" +
                "rule \"LinkSynapses\"\n" +
                "when\n" +
                "then\n" +
                "    Charge c = new Charge();\n" +
                "    c.setContext( \"MockCold\" );\n" +
                "    c.setSource( \"0\" );\n" +
                "    c.setIndex( \"1\" );\n" +
                "    c.setValue( 0.43 );\n" +
                "    insert(c);\n" +
                "end\n" +
                "\n" +
                "rule \"NeuralFire_MockCold_Layer0\"\n" +
                "when\n" +
                "    $neur : Neuron( context == \"MockCold\",\n" +
                "                    layerIndex == 0\n" +
                "                  )\n" +
                "    accumulate( $c : Charge( context == \"MockCold\", index == $neur.index, $in : value ),\n" +
                "                $list : collectList( $c ),\n" +
                "                $val : sum( $in );\n" +
                "                $list.size() == $neur.fanIn )\n" +
                "then\n" +
                "    double x = 1.0; // $neur.getBias() + $val.doubleValue();\n" +
                "    modify ( $neur ) {\n" +
                "        setValue( x );\n" +
                "    }\n" +
                "end\n" +
                "\n" +
                "rule \"BuildNeurons_MockCold_Layer0\"\n" +
                "when\n" +
                "then\n" +
                "    insert( new Neuron( \"MockCold\",\n" +
                "                               \"1\",\n" +
                "                               0,\n" +
                "                               1.0,\n" +
                "                               1,\n" +
                "                               0.0,\n" +
                "                               0.0,\n" +
                "                               true\n" +
                "                             ) );\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        assertEquals(3, ksession.fireAllRules());
    }

    @Test
    public void testPropertyReactiveAccumulateModification() {
        // DROOLS-16
        String str =
                "package org.drools.test;\n" +
                "\n" +
                "declare Neuron\n" +
                "@propertyReactive\n" +
                "  id : int\n" +
                "  value : double\n" +
                "end\n" +
                "\n" +
                "declare Charge\n" +
                "  nId : int\n" +
                "  val : double\n" +
                "end\n" +
                "\n" +
                "rule \"Log 2\"\n" +
                "salience 9999\n" +
                "when\n" +
                "  $n : Object();\n" +
                "then\n" +
                "end\n" +
                "rule \"Update\"\n" +
                "salience -9999\n" +
                "when\n" +
                "  $c : Charge( val == 1.0 );\n" +
                "then\n" +
                "  modify ( $c ) { " +
                "    setVal( 2.0 ); \n" +
                " } \n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Neuron( 0, 0.0 ) );\n" +
                "  insert( new Charge( 0, 1.0 ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Modify\"\n" +
                "salience -100\n" +
                "when\n" +
                "  $n : Neuron( )\n" +
                "  accumulate( Charge( $v : val ), $x : sum( $v ) )\n" +
                "then\n" +
                "  modify ( $n ) {\n" +
                "    setValue( $x.doubleValue() );\n" +
                "  }\n" +
                "end\n" +
                "\n" +
                "rule \"Watch\"\n" +
                "when\n" +
                "   $n : Neuron() @watch( value )" +
                "then\n" +
                "end\n" +
                "\n" +
                "query getNeuron\n" +
                "  Neuron( $value : value )\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();

        assertEquals(2.0, ksession.getQueryResults( "getNeuron" ).iterator().next().get( "$value" ));
    }

    @Test
    public void testMvelAssignmentToPublicField() {
        String str =
                "import org.drools.integrationtests.MiscTest2.Foo\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "   $foo : Foo()\n" +
                "then\n" +
                "   $foo.x = 1;\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Foo foo1 = new Foo();
        Foo foo2 = new Foo();
        ksession.insert(foo1);
        ksession.insert(foo2);
        ksession.fireAllRules();
        assertEquals(1, foo1.x);
        assertEquals(1, foo2.x);
    }

    public static class Foo {
        public int x;
        public int getX() {
            return x;
        }
        public void setX(int x) {
            this.x = x;
        }
    }

    @Test
    public void testMvelInvokeAsList() {
        String str =
                "import java.util.List;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "declare Project\n" +
                "@typesafe (false)\n" +
                "        list1 : List\n" +
                "        list2 : List\n" +
                "end\n" +
                "\n" +
                "rule kickoff\n" +
                "salience 999999\n" +
                "when\n" +
                "then\n" +
                "    insert( new Project() );\n" +
                "    insert( new Project() );   // necessary to trigger the exception\n" +
                "end\n" +
                "\n" +
                "rule \" Config rule \"\n" +
                "dialect \"mvel\"\n" +
                "no-loop true\n" +
                "when\n" +
                "    P : Project()\n" +
                "then\n" +
                "    modify(P) {\n" +
                "       list1 = Arrays.asList(10, 15, 20, 25),\n" +
                "       list2 = Arrays.asList(11, 2, 3, 4, 5, 10, 9, 8, 7)\n" +
                "    };\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();
    }

    @Test
    public void testDynamicAddRule() {
        // DROOLS-17
        String str =
                "import org.drools.integrationtests.MiscTest2.A\n" +
                "rule r1 when\n" +
                "    $a : A( f1 == 1 )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule r2 when\n" +
                "    $a : A( f2 == 1 )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule r3 when\n" +
                "    $a : A( f3 == 1 )" +
                "then\n" +
                "end";

        String str2 =
                "import org.drools.integrationtests.MiscTest2.A\n" +
                "rule r4 when\n" +
                "    $a : A( f2 == 1, f4 == 1 )" +
                "then\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        FactHandle fh = ksession.insert(new A(1, 1, 1, 1));

        ksession.fireAllRules();

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ksession.fireAllRules();

        // this second insert forces the regeneration of the otnIds
        ksession.insert(new A(2, 2, 2, 2));

        LeftTuple leftTuple = ((DefaultFactHandle) fh).getFirstLeftTuple();
        ObjectTypeNode.Id letTupleOtnId = leftTuple.getLeftTupleSink().getLeftInputOtnId();
        leftTuple = leftTuple.getLeftParentNext();
        while ( leftTuple != null ) {
            assertTrue( letTupleOtnId.before( leftTuple.getLeftTupleSink().getLeftInputOtnId() ) );
            letTupleOtnId = leftTuple.getLeftTupleSink().getLeftInputOtnId();
            leftTuple = leftTuple.getLeftParentNext();
        }
    }

    @PropertyReactive
    public static class A {
        private int f1;
        private int f2;
        private int f3;
        private int f4;

        public A(int f1, int f2, int f3, int f4) {
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
            this.f4 = f4;
        }

        public int getF1() {
            return f1;
        }

        public void setF1(int f1) {
            this.f1 = f1;
        }

        public int getF2() {
            return f2;
        }

        public void setF2(int f2) {
            this.f2 = f2;
        }

        public int getF3() {
            return f3;
        }

        public void setF3(int f3) {
            this.f3 = f3;
        }

        public int getF4() {
            return f4;
        }

        public void setF4(int f4) {
            this.f4 = f4;
        }

        @Override
        public String toString() {
            return "A[f1=" + f1 + ", f2=" + f2 + ", f3=" + f3 + ", f4=" + f4 + "]";
        }
    }

    @Test
    public void testNumberCoercionOnNonGenericMap() {
        // JBRULES-3708
        String str =
                "package com.ilesteban.jit;\n" +
                "\n" +
                "import java.util.Map;\n" +
                "import java.util.EnumMap;\n" +
                "import org.drools.integrationtests.MiscTest2.Parameter\n" +
                "import org.drools.integrationtests.MiscTest2.DataSample\n" +
                "\n" +
                "declare TestObject\n" +
                "    data    :   java.util.Map\n" +
                "end\n" +
                "\n" +
                "rule \"Rule 1\"\n" +
                "when\n" +
                "    $d: DataSample()\n" +
                "then\n" +
                "    //create a new object copying the Map<Parameter, Double> to a Map<Object, Object>\n" +
                "    insert( new TestObject($d.getValues()));\n" +
                "end\n" +
                "\n" +
                "rule \"Rule 2\"\n" +
                "when\n" +
                "    TestObject(data[Parameter.PARAM_A] > 3)\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Map<Parameter, Double> values = new EnumMap<Parameter, Double>(Parameter.class);
        values.put(Parameter.PARAM_A, 4.0);
        DataSample data = new DataSample();
        data.setValues(values);
        ksession.insert(data);

        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testPropertyReactivityWithNestedAccessorsInModify() {
        // JBRULES-3691
        String str =
                "package com.ilesteban.rulenotbeingfired;\n" +
                "\n" +
                "import java.util.Map;\n" +
                "import java.util.EnumMap;\n" +
                "import org.drools.integrationtests.MiscTest2.Parameter\n" +
                "import org.drools.integrationtests.MiscTest2.DataSample\n" +
                "\n" +
                "declare Recommendation\n" +
                "    parameter : Parameter\n" +
                "    value : double\n" +
                "end\n" +
                "\n" +
                "rule \"Init\" salience 100\n" +
                "when\n" +
                "then\n" +
                "    insert(new Recommendation(Parameter.PARAM_A, 1.0));" +
                "end\n" +
                "rule \"Rule 1\"\n" +
                "when\n" +
                "    $d: DataSample()\n" +
                "    $re: Recommendation ($p: parameter, $v: value)\n" +
                "then\n" +
                "    System.out.println(drools.getRule().getName());\n" +
                "    modify($d){\n" +
                "        addValue($re.getParameter(), $re.getValue())\n" +
                "    }\n" +
                "end\n" +
                "\n" +
                "rule \"Data with messages\"\n" +
                "salience -100\n" +
                "when\n" +
                "    $d: DataSample(notEmpty == true)\n" +
                "then\n" +
                "    System.out.println(drools.getRule().getName());\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new DataSample());

        assertEquals(3, ksession.fireAllRules());
    }

    public enum Parameter { PARAM_A, PARAM_B }

    @PropertyReactive
    public static class DataSample {
        private Map<Parameter, Double> values = new EnumMap<Parameter, Double>(Parameter.class);

        public Map<Parameter, Double> getValues() {
            return values;
        }

        public void setValues(Map<Parameter, Double> values) {
            this.values = values;
        }

        @Modifies({"values", "notEmpty"})
        public void addValue(Parameter p, double value){
            this.values.put(p, value);
        }

        public boolean isNotEmpty(){
            return !this.values.isEmpty();
        }
    }

    @Test
    public void testMvelResolvingGenericVariableDeclaredInParentClass() {
        // JBRULES-3684
        String str =
                "import org.drools.integrationtests.MiscTest2.AbstractBase\n" +
                "import org.drools.integrationtests.MiscTest2.StringConcrete\n" +
                "rule \"test\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$S : StringConcrete()\n" +
                "then\n" +
                "$S.getFoo().concat(\"this works with java dialect\");\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
    }

    public static abstract class AbstractBase<T> {
        protected T foo;
        public T getFoo() { return foo; }
    }

    public static class StringConcrete extends AbstractBase<String> {
        public StringConcrete() { this.foo = new String(); }
    }

    @Test
    public void testMvelParsingParenthesisInString() {
        // JBRULES-3698
        String str =
                "rule \"Test Rule\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "String s = new String(\"write something with ) a paren\");\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
    }

    public static enum Answer { YES, NO }
    public static class AnswerGiver {
        public Answer getAnswer() { return Answer.YES; }
    }

    @Test
    public void testCompilationMustFailComparingAClassLiteral() {
        // DROOLS-20
        String str =
                "import org.drools.integrationtests.MiscTest2.Answer\n" +
                "import org.drools.integrationtests.MiscTest2.AnswerGiver\n" +
                "rule \"Test Rule\"\n" +
                "when\n" +
                "   AnswerGiver(Answer == Answer.YES)\n" +
                "then\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testDeclaredTypeExtendingInnerClass() {
        // DROOLS-27
        String str =
                "import org.drools.integrationtests.MiscTest2.StaticPerson\n" +
                "declare StaticPerson end\n"+
                "declare Student extends StaticPerson end\n"+
                "rule Init when\n" +
                "then\n" +
                "    Student s = new Student();\n" +
                "    s.setName( \"Mark\" );\n" +
                "    insert( s );\n" +
                "end\n" +
                "rule Check when\n" +
                "    StaticPerson( name == \"Mark\")\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        assertEquals(2, ksession.fireAllRules());
    }

    public static class StaticPerson {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testAllowEqualityBetweenObjectAndPrimitiveInt() {
        // DROOLS-20
        String str =
                "declare Bean\n" +
                "  items : int\n" +
                "end\n" +
                "\n" +
                "rule \"O\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Bean( 2 ) );\n" +
                "end\n" +
                "\n" +
                "rule \"X\"\n" +
                "when\n" +
                "   Bean( $num : items ) \n" +
                "   accumulate( $o : Object(),\n" +
                "     $list : collectList( $o );\n" +
                "     $list.size == $num" +
                "   )\n" +
                "then\n" +
                "   System.out.println( \"Success!\" );\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testJitConstraintWithOperationOnBigDecimal() {
        // DROOLS-32
        String str =
                "import org.drools.integrationtests.MiscTest2.Model;\n" +
                "import java.math.BigDecimal;\n" +
                "\n" +
                "rule \"minCost\" dialect \"mvel\" \n" +
                "when\n" +
                "    $product : Model(price < (cost + 0.10B))\n" +
                "then\n" +
                "    modify ($product) { price = $product.cost + 0.10B }\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);

        final Model model = new Model();
        model.setCost(new BigDecimal("2.43"));
        model.setPrice(new BigDecimal("2.43"));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert(model);

        int fired = ksession.fireAllRules(2);
        if (fired > 1)
            throw new RuntimeException("loop");
    }

    public static class Model {
        private BigDecimal cost;
        private BigDecimal price;

        public BigDecimal getCost() {
            return cost;
        }
        public void setCost(BigDecimal cost) {
            this.cost = cost;
        }
        public BigDecimal getPrice() {
            return price;
        }
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    @Test
    public void testJitComparable() {
        // DROOLS-37
        String str =
                "import org.drools.integrationtests.MiscTest2.IntegerWrapperImpl;\n" +
                "\n" +
                "rule \"minCost\"\n" +
                "when\n" +
                "    $a : IntegerWrapperImpl()\n" +
                "    IntegerWrapperImpl( this < $a )\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert(new IntegerWrapperImpl(2));
        ksession.insert(new IntegerWrapperImpl(3));

        assertEquals(1, ksession.fireAllRules());
    }

    interface IntegerWraper {
        int getInt();
    }

    public static abstract class AbstractIntegerWrapper implements IntegerWraper, Comparable<IntegerWraper> { }

    public static class IntegerWrapperImpl extends AbstractIntegerWrapper {

        private final int i;

        public IntegerWrapperImpl(int i) {
            this.i = i;
        }

        public int compareTo(IntegerWraper o) {
            return getInt() - o.getInt();
        }

        public int getInt() {
            return i;
        }
    }

    @Test
    public void testEqualityOfDifferentTypes() {
        // DROOLS-42
        String str =
                "declare Person\n" +
                "  name: String\n" +
                "end\n" +
                "declare Customer\n" +
                "extends Person\n" +
                "  rating: int\n" +
                "end\n" +
                "declare Employee\n" +
                "extends Person\n" +
                "  wage: int\n" +
                "end\n" +
                "\n" +
                "rule initphone\n" +
                "salience 100\n" +
                "when\n" +
                "then\n" +
                "    insert( new Customer( \"Joe\", 100 ) );\n" +
                "    insert( new Employee( \"Paul\", 2100 ) );\n" +
                "end\n" +
                "\n" +
                "rule match\n" +
                "when\n" +
                "    $c: Customer()\n" +
                "    $e: Employee( this != $c )\n" +
                "then\n" +
                "    System.out.println( \"c/e \" + $c + \" \" + $e );\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();
    }

    @Test
    public void testUnificationInRule() {
        // DROOLS-45
        String str =
                "declare A\n" +
                "end\n" +
                "\n" +
                "declare B\n" +
                " inner : A\n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  A a = new A();\n" +
                "  insert( a );\n" +
                "  insert( new B( a ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Check\"\n" +
                "when\n" +
                "  B( $in := inner )\n" +
                "  $in := A()\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testDeclarationsScopeUsingOR() {
        // DROOLS-44
        String str =
                "declare A\n" +
                "    a1 : String\n" +
                "end\n" +
                "\n" +
                "declare B\n" +
                "    b1 : String\n" +
                "end\n" +
                "\n" +
                "rule Init salience 10 when \n" +
                "then\n" +
                "    insert( new A( \"A\" ) );\n" +
                "    insert( new B( null ) );\n" +
                "end\n" +
                "\n" +
                "rule R when \n" +
                "    A ( $a1 : a1 != null )\n" +
                "    (or\n" +
                "        (and\n" +
                "            B( $b1 : b1 != null )\n" +
                "            eval( $a1.compareTo( $b1 ) < 0 )\n" +
                "        )\n" +
                "        (and\n" +
                "            B( b1 == null )\n" +
                "            eval( $a1.compareTo(\"B\") < 0 )\n" +
                "        )\n" +
                "    )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testQueryAfterEvalInsideOR() {
        // DROOLS-54
        String str =
                "package pakko\n" +
                "\n" +
                "declare Holder\n" +
                "  str : String\n" +
                "end\n" +
                "\n" +
                "declare Bean\n" +
                "  val : String\n" +
                "end\n" +
                "\n" +
                "declare Mock end \n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Bean( \"xyz\" ) );\n" +
                "  insert( new Holder( \"xyz\" ) );\n" +
                "  insert( new Mock() );\n" +
                "end\n" +
                "\n" +
                "query mock( Mock $m ) $m := Mock() end\n" +
                "\n" +
                "rule \"Check\"\n" +
                "when\n" +
                "  $b : Bean( $t : val )\n" +
                "  ( Holder( $t ; ) or eval( $t.startsWith( \"abc\" ) ) )\n" +
                "  mock( $m ; ) \n" +
                "then\n" +
                "  System.out.println( $m );\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testPackageVisibility() {
        // DROOLS-61
        String str =
                "package org.drools.integrationtests;\n" +
                "rule \"getX\"\n" +
                "when\n" +
                "    $x: PackageProtected( )\n" +
                "then\n" +
                "    System.out.println( $x );\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue( kbuilder.hasErrors() );
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

    @Test
    public void testAvoidUnwantedSemicolonWhenDelimitingExpression() {
        // DROOLS-86
        String str =
                "global java.util.List l\n" +
                "rule rule1 \n" +
                " dialect \"mvel\" \n" +
                "when \n" +
                "then \n" +
                " String s = \"http://onefineday.123\";\n" +
                " l.add(s);\n" +
                "end \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> l = new ArrayList<String>();
        ksession.setGlobal("l", l);

        ksession.fireAllRules();

        assertEquals("http://onefineday.123", l.get(0));
    }

    @Test
    public void testJitCastOfPrimitiveType() {
        // DROOLS-79
        String str =
                "rule R when\n" +
                "    Number(longValue < (Long)7)\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new Long(6));
        assertEquals(1, ksession.fireAllRules());
    }

    public static class Foo2 {
        @Position(0)
        public int x;
        public int getX() {
            return x;
        }
        public void setX(int x) {
            this.x = x;
        }
    }

    @Test
    public void testSelfChangingRuleSet() {
        // DROOLS-92
        String str =
                "package org.drools.integrationtests;\n" +
                "" +
                "import org.drools.integrationtests.MiscTest2.Foo2; \n" +
                "" +
                "global java.util.List list; \n" +
                "\n" +
                "rule \"Prep\" \n" +
                "when \n" +
                "  $packs : java.util.Collection() \n" +
                "then \n" +
                "   drools.getKnowledgeRuntime().getKnowledgeBase().addKnowledgePackages( $packs );" +
                "end \n" +
                "" +
                "rule \"Self-change\"\n" +
                "when\n" +
                "  String( this == \"go\" )\n" +
                "then\n" +
                "   drools.getKnowledgeRuntime().getKnowledgeBase().removeRule( \"org.drools.integrationtests\", \"React\" ); \n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"Insert\"\n" +
                "when\n" +
                "  $i : Integer()\n" +
                "then\n" +
                "  Foo2 foo = new Foo2();\n " +
                "  foo.setX( $i ); \n" +
                "  insert( foo );\n" +
                "end\n" +
                "" +
                "";

        String str2 =
                "package org.drools.integrationtests;\n" +
                "" +
                "import org.drools.integrationtests.MiscTest2.Foo2; \n" +
                "global java.util.List list;\n " +
                "rule \"React\"\n" +
                "when\n" +
                "  $b : Foo2( x < 10 )\n" +
                "then\n" +
                "  System.out.println( \" Foo2 is in \" + $b.getX() );" +
                "  list.add( $b ); \n" +
                "end\n";

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new ByteArrayResource( str2.getBytes() ), ResourceType.DRL );

        System.out.println(  knowledgeBuilder.getErrors() );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( knowledgeBuilder.getKnowledgePackages() );

        ksession.insert( new Integer( 1 ) );
        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.fireAllRules();

        ksession.insert( new Integer( 2 ) );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );

    }

    @Test
    public void testMatchIntegers() {
        // DROOLS-94
        String str =
                "global java.util.List list; \n" +
                "rule R when\n" +
                " $i : Integer( this == 1 )\n" +
                "then\n" +
                " list.add( $i );\n" +
                "end\n" +
                "rule S when\n" +
                " $i : Integer( this == 2 )\n" +
                "then\n" +
                " list.add( $i );\n" +
                "end\n" +
                "rule T when\n" +
                " $i : Integer( this == 3 )\n" +
                "then\n" +
                " list.add( $i );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new Integer( 1 ) );
        ksession.fireAllRules();
    }

    public static class SimpleEvent {
        private long duration;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }

    @Test
    public void testDurationAnnotation() {
        // DROOLS-94
        String str =
                "package org.drools.integrationtests;\n" +
                "import org.drools.integrationtests.MiscTest2.SimpleEvent\n" +
                "declare SimpleEvent\n" +
                " @role(event)\n" +
                " @duration(duration)\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    }

    @Test
    public void testDurationAnnotationWithError() {
        // DROOLS-94
        String str =
                "package org.drools.integrationtests;\n" +
                "import org.drools.integrationtests.MiscTest2.SimpleEvent\n" +
                "declare SimpleEvent\n" +
                " @role(event)\n" +
                " @duration(duratio)\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue(kbuilder.hasErrors());
    }


    @Test
    public void testFactLeak() throws InterruptedException {
        //DROOLS-131
        String drl = "package org.drools.test; \n" +
                     "global java.util.List list; \n" +
                     "" +
                     "" +
                     "rule Intx when\n" +
                     "  $x : Integer() from entry-point \"x\" \n" +
                     "then\n" +
                     "  list.add( $x ); \n" +
                     "end";
        int N = 1100;

        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        final StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        new Thread () {
            public void run () {
                ks.fireUntilHalt();
            }
        }.start ();

        for ( int j = 0; j < N; j++ ) {
            ks.getWorkingMemoryEntryPoint( "x" ).insert( new Integer( j ) );
        }

        Thread.sleep( 1000 );
        ks.halt();

        assertEquals( N, list.size() );
    }

    @Test
    public void testFailedStaticImport()  {
        String drl = "package org.drools.test; \n" +
                     "" +
                     "import function org.does.not.exist.Foo; \n" +
                     "" +                     "" +
                     "rule X when\n" +
                     "then\n" +
                     "end";
        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        assertTrue( kb.hasErrors() );
    }

    @Test
    public void testUnsupportedPolymorphicDeclaration()  {
        String drl = "package org.drools.test; \n" +
                     "" +
                     "declare Foo end  \n" +
                     "declare Bar end  \n" +
                     "" +                     "" +
                     "rule X when\n" +
                     "  $x : Foo() " +
                     "  or " +
                     "  $x : Bar() \n" +
                     "then\n" +
                     "  System.out.println( $x ); \n" +
                     "end\n" +
                     "" +
                     "rule Init\n" +
                     "when\n" +
                     "then\n" +
                     "  insert( new Foo() ); \n" +
                     "  insert( new Bar() ); \n" +
                     "end";
        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        System.err.println( kb.getErrors().toString() );
        assertTrue( kb.hasErrors() );
    }

    @Test
    public void testLegacySalienceResolver()  {
        String drl = "package org.drools.test; \n" +
                     "" +
                     "global java.util.List list; \n " +
                     "" +
                     "rule X salience 10 \n" +
                     "then\n" +
                     "  list.add( 1 ); \n" +
                     "end\n" +
                     "" +
                     "rule Y salience 5 \n" +
                     "then\n" +
                     "  list.add( 2 ); \n" +
                     "end\n" +
                     "";

        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        assertFalse( kb.hasErrors() );

        KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        (( RuleBaseConfiguration) kbconf).setConflictResolver( SalienceConflictResolver.getInstance() );

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( kbconf );
        knowledgeBase.addKnowledgePackages( kb.getKnowledgePackages() );
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );
        knowledgeSession.fireAllRules();

        assertEquals( Arrays.asList( 1, 2 ), list );
    }




    public static interface TradeBooking {
        public TradeHeader getTrade();
    }

    public static interface TradeHeader {
        public void setAction( String s );
        public String getAction();
    }

    public static class TradeHeaderImpl implements TradeHeader {
        private String action;

        public String getAction() {
            return action;
        }

        public void setAction( String action ) {
            this.action = action;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            TradeHeaderImpl that = (TradeHeaderImpl) o;

            if ( action != null ? !action.equals( that.action ) : that.action != null ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return action != null ? action.hashCode() : 0;
        }
    }

    public static class TradeBookingImpl implements TradeBooking {
        private TradeHeader header;

        public TradeBookingImpl( TradeHeader h ) {
            this.header = h;
        }

        public TradeHeader getTrade() {
            return header;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            TradeBookingImpl that = (TradeBookingImpl) o;

            if ( header != null ? !header.equals( that.header ) : that.header != null ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return header != null ? header.hashCode() : 0;
        }
    }



    @Test
    public void testLockOnActive() {
        String drl = "" +
                     "package org.drools.test; \n" +
                     "import org.drools.integrationtests.MiscTest2.TradeBooking;\n" +
                     "import org.drools.integrationtests.MiscTest2.TradeHeader;\n" +
                     "rule \"Rule1\" \n" +
                     "salience 1 \n" +
                     "when\n" +
                     "  $booking: TradeBooking()\n" +
                     "  $trade: TradeHeader() from $booking.getTrade()\n" +
                     "  not String()\n" +
                     "then\n" +
                     "  System.out.println( \"Rule1\" ); \n" +
                     "  $trade.setAction(\"New\");\n" +
                     "  modify($booking) {}\n" +
                     "  insert (\"run\");\n" +
                     "end;\n" +
                     "\n" +
                     "rule \"Rule2\"\n" +
                     "lock-on-active true\n" +
                     "when\n" +
                     "  $booking: TradeBooking( )\n" +
                     "  $trade: Object( ) from $booking.getTrade()\n" +
                     "then\n" +
                     "  System.out.println( \"Rule2\" ); \n" +
                     "end";
        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();

        ks.addEventListener( new AgendaEventListener() {
            int step = 0;

            public void activationCreated( ActivationCreatedEvent event ) {}

            public void activationCancelled( ActivationCancelledEvent event ) {
                switch ( step ) {
                    case 0 : assertEquals( "Rule2", event.getActivation().getRule().getName() );
                        step++;
                        break;
                    case 1 : assertEquals( "Rule1", event.getActivation().getRule().getName() );
                        step++;
                        break;
                    default: fail( "More cancelled activations than expected" );
                }
            }

            public void beforeActivationFired( BeforeActivationFiredEvent event ) {}

            public void afterActivationFired( AfterActivationFiredEvent event ) {
                assertEquals( "Rule1", event.getActivation().getRule().getName() );
            }

            public void agendaGroupPopped( AgendaGroupPoppedEvent event ) {}
            public void agendaGroupPushed( AgendaGroupPushedEvent event ) {}
            public void beforeRuleFlowGroupActivated( RuleFlowGroupActivatedEvent event ) {}
            public void afterRuleFlowGroupActivated( RuleFlowGroupActivatedEvent event ) {}
            public void beforeRuleFlowGroupDeactivated( RuleFlowGroupDeactivatedEvent event ) {}
            public void afterRuleFlowGroupDeactivated( RuleFlowGroupDeactivatedEvent event ) {}
        } );
        ks.fireAllRules();

        TradeBooking tb = new TradeBookingImpl( new TradeHeaderImpl() );

        ks.insert( tb );
        assertEquals( 1, ks.fireAllRules() );
    }


    @Test
    public void testLockOnActiveWithModify() {
        String drl = "" +
                     "package org.drools.test; \n" +
                     "import org.drools.Person; \n" +
                     "" +
                     "rule \"Rule1\" \n" +
                     "salience 1 \n" +
                     "lock-on-active true\n" +
                     "no-loop \n" +
                     "when\n" +
                     "  $p: Person()\n" +
                     "then\n" +
                     "  System.out.println( \"Rule1\" ); \n" +
                     "  modify( $p ) { setAge( 44 ); }\n" +
                     "end;\n" +
                     "\n" +
                     "rule \"Rule2\"\n" +
                     "lock-on-active true\n" +
                     "when\n" +
                     "  $p: Person() \n" +
                     "  String() from $p.getName() \n" +
                     "then\n" +
                     "  System.out.println( \"Rule2\" + $p ); " +
                     "  modify ( $p ) { setName( \"john\" ); } \n" +
                     "end";
        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();
        ks.addEventListener( new DebugAgendaEventListener(  ) );

        ks.fireAllRules();

        Person p = new Person( "mark", 76 );
        ks.insert( p );
        ks.fireAllRules();

        assertEquals( 44, p.getAge() );
        assertEquals( "john", p.getName() );
    }

    @Test
    public void testUnaryNegation() {
        // DROOLS-177
        String str =
                "rule R when\n" +
                " Integer( $a: intValue )\n" +
                " Integer( intValue > $a, intValue == -$a )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(3);
        ksession.insert(-3);

        assertEquals(1, ksession.fireAllRules());
    }

    public static class Conversation {
        private final int id;
        private String family;
        private int timeslot;

        public Conversation(int id) {
            this.id = id;
        }

        public Conversation(int id, String family, int timeslot) {
            this.id = id;
            this.family = family;
            this.timeslot = timeslot;
        }

        public int getId() {
            return id;
        }

        public String getFamily() {
            return family;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public int getTimeslot() {
            return timeslot;
        }

        public void setTimeslot(int timeslot) {
            this.timeslot = timeslot;
        }

        public String toString() {
            return "Conversation #" + getId() + " with " + getFamily() + " @ " + getTimeslot();
        }
    }

    @Test
    public void testNotNodeUpdateBlocker() {
        String str =
                "import org.drools.integrationtests.MiscTest2.Conversation;\n" +
                "global java.util.List list;" +
                "\n" +
                "rule \"familyEnd\" when\n" +
                " $conversation : Conversation(\n" +
                " family != null, $family: family, \n" +
                " $timeslot: timeslot)\n" +
                "\n" +
                " not Conversation(\n" +
                " family == $family, \n" +
                " timeslot > $timeslot);\n" +
                "then\n" +
                " list.add($conversation);\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Conversation> conversations = new ArrayList<Conversation>();
        ksession.setGlobal("list", conversations);

        Conversation c0 = new Conversation(0, "Fusco", 2);
        Conversation c1 = new Conversation(1, "Fusco", 3);
        Conversation c2 = new Conversation(2, "Fusco", 4);

        FactHandle fh0 = ksession.insert(c0);
        FactHandle fh1 = ksession.insert(c1);
        FactHandle fh2 = ksession.insert(c2);

        ksession.fireAllRules();
        assertEquals(1, conversations.size());
        conversations.clear();

        c2.setTimeslot(0);
        ksession.update(fh2, c2);
        ksession.fireAllRules();
        c2.setTimeslot(4);
        ksession.update(fh2, c2);
        ksession.fireAllRules();
        conversations.clear();

        c0.setTimeslot(3);
        ksession.update(fh0, c0);
        ksession.fireAllRules();
        c0.setTimeslot(2);
        ksession.update(fh0, c0);
        ksession.fireAllRules();
        conversations.clear();

        c2.setTimeslot(1);
        ksession.update(fh2, c2);
        ksession.fireAllRules();
        assertEquals(1, conversations.size());
    }

    @Test
    public void testSortWithNot() {
        String str =
                "import java.util.*; \n" +
                "" +
                "global java.util.List list;" +
                "\n" +
                "" +
                "declare Bean \n" +
                "   value   : Integer @key \n" +
                "   mark    : boolean = false \n" +
                "end \n" +
                "" +
                "declare Holder\n" +
                "   map : Map \n" +
                "end \n" +
                "" +
                "rule \"Init\" when\n" +
                "then\n" +
                " insert( new Holder( new HashMap() ) ); \n" +
                " insert( new Bean( 10 ) );\n" +
                " insert( new Bean( 30 ) );\n" +
                " insert( new Bean( 20 ) );\n" +
                " insert( new Bean( 50 ) );\n" +
                " insert( new Bean( 40 ) );\n" +
                "end\n" +
                "" +
                "rule Sort when \n" +
                "   $h : Holder( $map : map ) \n" +
                "   $b : Bean( ! $map.containsKey( value ), $v : value ) \n" +
                "   not Bean( ! $map.containsKey( value ), value > $v ) \n" +
                "then \n" +
                "   list.add( $v ); \n" +
                "   System.out.println( \"Marking \" + $v ); \n" +
                "   modify ( $h ) { getMap().put( $v, $b ); } \n" +
                "end \n" +
                "";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 50, 40, 30, 20, 10 ), list );
    }


    @Test
    public void testListnersOnStatlessSession() {
        // DROOLS-141
        // BZ-999491
        String str =
                "rule R when\n" +
                "  String()\n"  +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();

        final List<String> firings = new ArrayList<String>();

        AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void activationCreated( ActivationCreatedEvent event ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void activationCancelled( ActivationCancelledEvent event ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void beforeActivationFired( BeforeActivationFiredEvent event ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void afterActivationFired( AfterActivationFiredEvent event ) {
                firings.add( "Fired!" );
            }

            public void agendaGroupPopped( AgendaGroupPoppedEvent event ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void agendaGroupPushed( AgendaGroupPushedEvent event ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void beforeRuleFlowGroupActivated( RuleFlowGroupActivatedEvent event ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void afterRuleFlowGroupActivated( RuleFlowGroupActivatedEvent event ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void beforeRuleFlowGroupDeactivated( RuleFlowGroupDeactivatedEvent event ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void afterRuleFlowGroupDeactivated( RuleFlowGroupDeactivatedEvent event ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };

        ksession.addEventListener(agendaEventListener);

        ksession.execute("1");
        ksession.execute("2");

        assertEquals(2, firings.size());

        ksession.removeEventListener(agendaEventListener);

        ksession.execute("3");

        assertEquals(2, firings.size());
    }


    @Test
    public void testKsessionSerializationWithInsertLogical() {
        List<String> firedRules = new ArrayList<String>();
        String str =
                "import java.util.Date;\n" +
                "import org.drools.integrationtests.MiscTest2.Promotion;\n" +
                "\n" +
                "declare Person\n" +
                " name : String\n" +
                " dateOfBirth : Date\n" +
                "end\n" +
                "\n" +
                "declare Employee extends Person\n" +
                " job : String\n" +
                "end\n" +
                "\n" +
                "rule \"Insert Alice\"\n" +
                " when\n" +
                " then\n" +
                " Employee alice = new Employee(\"Alice\", new Date(1973, 7, 2), \"Vet\");\n" +
                " insert(alice);\n" +
                " System.out.println(\"Insert Alice\");\n" +
                "end\n" +
                "\n" +
                "rule \"Insert Bob\"\n" +
                " when\n" +
                " Person(name == \"Alice\")\n" +
                " then\n" +
                " Person bob = new Person(\"Bob\", new Date(1973, 7, 2));\n" +
                " insertLogical(bob);\n" +
                " System.out.println(\"InsertLogical Bob\");\n" +
                "end\n" +
                "\n" +
                "rule \"Insert Claire\"\n" +
                " when\n" +
                " Person(name == \"Bob\")\n" +
                " then\n" +
                " Employee claire = new Employee(\"Claire\", new Date(1973, 7, 2), \"Student\");\n" +
                " insert(claire);\n" +
                " System.out.println(\"Insert Claire\");\n" +
                "end\n" +
                "\n" +
                "rule \"Promote\"\n" +
                " when\n" +
                " p : Promotion(n : name, j : job)\n" +
                " e : Employee(name == n)\n" +
                " then\n" +
                " modify(e) {\n" +
                " setJob(j)\n" +
                " }\n" +
                " retract(p);\n" +
                " System.out.printf(\"Promoted %s to %s%n\", n, j);\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.fireAllRules(); // insertLogical Person(Bob)

        // Serialize and Deserialize
        try {
            Marshaller marshaller = MarshallerFactory.newMarshaller( kbase );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshall(baos, ksession);
            marshaller = MarshallerFactory.newMarshaller(kbase);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            baos.close();
            ksession = (StatefulKnowledgeSession)marshaller.unmarshall(bais);
            bais.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception :" + e.getMessage());
        }

        ksession.insert(new Promotion("Claire", "Scientist"));
        int result = ksession.fireAllRules();

        assertEquals(1, result);
    }

    public static class Promotion {
        private String name;
        private String job;
        public Promotion(String name, String job) {
            this.setName(name);
            this.setJob(job);
        }
        public String getName() {
            return this.name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getJob() {
            return this.job;
        }
        public void setJob(String job) {
            this.job = job;
        }
    }

    @Test
    public void testImportExceptional() throws java.lang.Exception {
        // DROOLS-253 imported Exception would have qualified as the default Exception thrown by the RHS
        String str =
                "import org.acme.healthcare.Exception;\n" +
                "" +
                "global java.util.List list;" +
                "\n" +
                "" +
                "rule \"Init\" when\n" +
                "then\n" +
                "   list.add( 1 ); \n" +
                "end\n" +
                "";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 1 ), list );
    }

    public static class SQLTimestamped {
        private Timestamp start;

        public Timestamp getStart() {
            return start;
        }

        public void setStart( Timestamp start ) {
            this.start = start;
        }

        public SQLTimestamped() {
            start = new Timestamp( new Date().getTime() );
        }
    }

    @Test
    public void testEventWithSQLTimestamp() throws InterruptedException {
        // DROOLS-10
        String str =
                "import org.drools.integrationtests.MiscTest2.SQLTimestamped;\n" +
                "" +
                "global java.util.List list;" +
                "\n" +
                "declare SQLTimestamped @role(event) @timestamp(start) end \n" +
                "" +
                "rule \"Init\" when\n" +
                "   $s1 : SQLTimestamped() \n" +
                "   $s2 : SQLTimestamped( this != $s1, this after $s1 ) \n" +
                "then\n" +
                "   list.add( \"ok\" ); \n" +
                "end\n" +
                "";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new SQLTimestamped() );
        Thread.sleep( 100 );
        ksession.insert( new SQLTimestamped() );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( "ok" ), list );
    }

    public static class Foo3 {
        public boolean getX() { return true; }
        public String isX() { return "x"; }
        public boolean isY() { return true; }
        public String getZ() { return "ok"; }
        public boolean isZ() { return true; }
    }

    @Test
    public void testIsGetClash() {
        // DROOLS-18
        String str =
                "import org.drools.integrationtests.MiscTest2.Foo3;\n" +
                "" +
                "global java.util.List list;" +
                "\n" +
                "" +
                "rule \"Init\" when\n" +
                "   $x : Foo3( x == true, y == true, z == \"ok\", isZ() == true ) \n" +
                "then\n" +
                "   list.add( \"ok\" ); \n" +
                "end\n" +
                "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        assertEquals( 2, kbuilder.getResults( ResultSeverity.WARNING ).size() );
        for ( KnowledgeBuilderResult res : kbuilder.getResults( ResultSeverity.WARNING ) ) {
            System.out.println( res.getMessage() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new Foo3( ) );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( "ok" ), list );
    }


    @Test
    public void testCollectAccumulate() {
        // DROOLS-173
        String drl = "import java.util.ArrayList\n" +
                     "\n" +
                     "global java.util.Map map; \n" +
                     "" +
                     " declare Item\n" +
                     "     code: int\n" +
                     "     price: int\n" +
                     "     present: boolean\n" +
                     " end\n" +
                     "\n" +
                     " rule \"Init\"\n" +
                     " when\n" +
                     " then\n" +
                     "     insert(new Item(1,40,false));\n" +
                     "     insert(new Item(2,40,false));\n" +
                     "     insert(new Item(3,40,false));\n" +
                     "     insert(new Item(4,40,false));\n" +
                     " end\n" +
                     "\n" +
                     " rule \"CollectAndAccumulateRule\"\n" +
                     " when\n" +
                     "     //At least two items that aren't presents\n" +
                     "     objList: ArrayList(size>=2) from collect( Item(present==false))\n" +
                     "     //Total price bigger than 100\n" +
                     "     price: Number(intValue>=100) from accumulate( Item($w:price, present==false), sum($w))\n" +
                     " then\n" +
                     "\n" +
                     "     System.out.println(\"Sum: \"+price);\n" +
                     "     System.out.println(\"Items size: \"+objList.size());\n" +
                     " " +
                     "      map.put( objList.size(), price ); \n" +
                     "     \n" +
                     "     //Look for the minor price item\n" +
                     "     Item min = null;\n" +
                     "     for(Object obj: objList){\n" +
                     "         if (min!=null){\n" +
                     "             min = (min.getPrice()>((Item)obj).getPrice())?(Item)obj:min;\n" +
                     "         }\n" +
                     "         else {\n" +
                     "             min = (Item)obj;\n" +
                     "         }\n" +
                     "     }\n" +
                     "     \n" +
                     "     //And make it a present\n" +
                     "     if (min!=null){\n" +
                     "         modify(min){setPresent(true)};\n" +
                     "     }\n" +
                     " end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Map map = new HashMap(  );
        ksession.setGlobal( "map", map );

        ksession.fireAllRules();

        assertEquals( 2, map.size() );
        assertEquals( 160.0, map.get( 4 ) );
        assertEquals( 120.0, map.get( 3 ) );

    }


    @Test
    public void testExistsOr() {
        // DROOLS-254
        String drl = "package org.drools.test;\n" +
                     "\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare Foo val : String end \n" +
                     "" +
                     "rule Init when $s : String() then retract( $s ); insert( new Foo( $s ) ); end \n" +
                     "" +
                     "rule \"Check Pos\"\n" +
                     "when\n" +
                     "  exists ( Foo( val == \"1\" ) or Foo( val == \"2\" ) )\n" +
                     "then\n" +
                     "  list.add( \"+\" );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Check Neg\"\n" +
                     "when\n" +
                     "  not ( not Foo( val == \"1\" ) and not Foo( val == \"2\" ) )\n" +
                     "then\n" +
                     "  list.add( \"-\" );\n" +
                     "end\n" +
                     "\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( "2" );
        ksession.insert( "3" );
        ksession.insert( "1" );
        ksession.insert( "4" );
        ksession.insert( "5" );
        ksession.insert( "7" );

        ksession.fireAllRules();

        ksession.insert( "1" );
        ksession.insert( "5" );
        ksession.insert( "7" );

        ksession.fireAllRules();

        ksession.insert( "6" );
        ksession.insert( "2" );
        ksession.insert( "2" );

        ksession.fireAllRules();

        System.out.println( list );

        assertEquals( 11, ksession.getObjects().size() );
        assertEquals( 2, list.size() );
        assertTrue( list.contains( "+" ) );
        assertTrue( list.contains( "-" ) );
    }


    @Test
    public void testBindingComplexExpression() {
        // DROOLS-43
        String drl = "package org.drools.test;\n" +
                     "\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare Foo \n" +
                     "  a : int \n" +
                     "  b : int \n" +
                     "end \n" +
                     "" +
                     "rule Init when then insert( new Foo( 3, 4 ) ); end \n" +
                     "" +
                     "rule \"Expr\"\n" +
                     "when\n" +
                     "  $c := Integer() from new Integer( 4 ) \n" +
                     "  Foo(  $a : a + b == 7 && a == 3 && $b : b > 0, $c := b - a == 1 ) \n" +
                     "then\n" +
                     "  list.add( $a );\n" +
                     "  list.add( $b );\n" +
                     "  list.add( $c );\n" +
                     "end\n" +
                     "\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertTrue( ! list.isEmpty() );
        assertEquals( 3, list.size() );
        assertEquals( 3, list.get( 0 ) );
        assertEquals( 4, list.get( 1 ) );
        assertEquals( 4, list.get( 2 ) );

    }

    @Test
    public void testBindingComplexExpressionErrors() {
        // DROOLS-43
        String drl = "package org.drools.test;\n" +
                     "\n" +
                     "declare Foo a : int  b : int end \n" +
                     "rule Init when then insert( new Foo( 3, 4 ) ); end \n" +
                     "\n";

        String err1 =
                     "rule \"Expr\"\n" +
                     "when\n" +
                     "  Foo(  $a : a + $b : b > 5  ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        String err2 =
                     "rule \"Expr\"\n" +
                     "when\n" +
                     "  Foo(  b - a ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder knowledgeBuilder1 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder1.add( new ByteArrayResource( ( drl + err1 ).getBytes() ), ResourceType.DRL );
        System.err.println( knowledgeBuilder1.getErrors() );
        assertTrue( knowledgeBuilder1.hasErrors() );

        KnowledgeBuilder knowledgeBuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder2.add( new ByteArrayResource( ( drl + err2 ).getBytes() ), ResourceType.DRL );
        System.err.println( knowledgeBuilder2.getErrors() );
        assertTrue( knowledgeBuilder2.hasErrors() );
    }



    @Test
    public void testPrimitiveGlobals() {
        String drl = "package org.drools.compiler.integrationtests\n" +
                     "\n" +
                     "global int foo;\n" +
                     "\n" +
                     "";
        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        System.out.println( kb.getErrors() );
        assertTrue( kb.hasErrors() );
    }


    @Test
    public void testMapAccessorWithCustomOp() {
        // DROOLS-216
        String str =
                "import java.util.Map;\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                " Map( this[\"x\"] str[startsWith] \"T\" )\n" +
                " Map( this[\"x\"] soundslike \"Test\" )\n" +
                "then\n" +
                " list.add( 1 );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        Map<String, String> map = new HashMap<String, String>();
        map.put("x", "Test");
        ksession.insert(map);

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 1 ), list );
    }

    @Test
    public void testMapAccessorWithBoundVar() {
        // DROOLS-217
        String str =
                "import java.util.Map;\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                " Map( $val1 : this[\"x\"], $val1 str[startsWith] \"T\" )\n" +
                " Map( $val2 : this[\"x\"], $val2 soundslike \"Test\" )\n" +
                "then\n" +
                " list.add( 1 );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        Map<String, String> map = new HashMap<String, String>();
        map.put("x", "Test");
        ksession.insert(map);

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 1 ), list );
    }


}