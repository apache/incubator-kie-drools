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
import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.DefaultFactHandle;
import org.drools.compiler.CompositeKnowledgeBuilderImpl;
import org.drools.core.util.FileManager;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.type.Modifies;
import org.drools.definition.type.Position;
import org.drools.definition.type.PropertyReactive;
import org.drools.event.knowledgebase.DefaultKnowledgeBaseEventListener;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.impl.AgendaImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
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

    @Test @Ignore("fixed with mvel 2.1.5.Final")
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


}