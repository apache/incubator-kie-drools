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

package org.drools.compiler.integrationtests;

import org.drools.compiler.Address;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.ClassObjectFilter;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.conflict.SalienceConflictResolver;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.util.FileManager;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.runtime.rule.impl.AgendaImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Position;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.PhreakOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.event.kiebase.DefaultKieBaseEventListener;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.FactHandle;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.optimizers.OptimizerFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
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
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

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
                "import org.drools.compiler.Address\n" +
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
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( PhreakOption.ENABLED );
        KnowledgeBase knowledgeBase  = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

        Address address = new Address();

        address.setSuburb("xyz");
        org.kie.api.runtime.rule.FactHandle addressHandle = ksession.insert(address);

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

        FileManager fileManager = new FileManager().setUp();

        try {
            File root = fileManager.getRootDirectory();

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(root, "test.drl.compiled")));
            out.writeObject( kbuilder.getKnowledgePackages());
            out.close();

            KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kconf.setOption( PhreakOption.ENABLED );
            KnowledgeBase kbase  = KnowledgeBaseFactory.newKnowledgeBase(kconf);

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
        KieBaseEventListener listener = new DefaultKieBaseEventListener();
        kbase.addEventListener(listener);
        kbase.addEventListener(listener);
        assertEquals(1, ((KnowledgeBaseImpl) kbase).getRuleBase().getRuleBaseEventListeners().size());
        kbase.removeEventListener(listener);
        assertEquals(0, ((KnowledgeBaseImpl) kbase).getRuleBase().getRuleBaseEventListeners().size());
    }

    @Test
    public void testReuseAgendaAfterException() throws Exception {
        // JBRULES-3677

        String str = "import org.drools.compiler.Person;\n" +
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
            public void matchCreated(org.kie.api.event.rule.MatchCreatedEvent event) {
            }

            public void matchCancelled(org.kie.api.event.rule.MatchCancelledEvent event) {
            }

            public void beforeMatchFired(org.kie.api.event.rule.BeforeMatchFiredEvent event) {
            }

            public void afterMatchFired(org.kie.api.event.rule.AfterMatchFiredEvent event) {
            }

            public void agendaGroupPopped(org.kie.api.event.rule.AgendaGroupPoppedEvent event) {
            }

            public void agendaGroupPushed(org.kie.api.event.rule.AgendaGroupPushedEvent event) {
            }

            public void beforeRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
            }

            public void afterRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
                ksession.fireAllRules();
            }

            public void beforeRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
            }

            public void afterRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
            }
        };

        ksession.addEventListener(agendaEventListener);

        FactHandle fact1 = ksession.insert(new Person("Mario", 38));
        ((AgendaImpl)ksession.getAgenda()).activateRuleFlowGroup("test");
        ksession.fireAllRules();
        assertEquals(1, res.size());
        res.clear();

        ksession.retract(fact1);

        FactHandle fact2 = ksession.insert(new Person("Mario", 48));
        try {
            ((AgendaImpl)ksession.getAgenda()).activateRuleFlowGroup("test");
            ksession.fireAllRules();
            fail("should throw an Exception");
        } catch (Exception e) { }
        ksession.retract(fact2);

        assertEquals(0, res.size());

        // try to reuse the ksession after the Exception
        FactHandle fact3 = ksession.insert(new Person("Mario", 38));
        ((AgendaImpl)ksession.getAgenda()).activateRuleFlowGroup("test");
        ksession.fireAllRules();
        assertEquals(1, res.size());
        ksession.retract(fact3);

        ksession.dispose();

    }

    @Test
    public void testBooleanPropertyStartingWithEmpty() {
        // JBRULES-3690
        String str =
                "declare Fact\n" +
                "   emptyx : boolean\n" +
                "end\n" +
                "\n" +
                "rule \"R1\"\n" +
                "   when\n" +
                "   Fact(emptyx == false)" +
                "   then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
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
        String str = "import org.drools.compiler.Person;\n" +
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

        assertEquals(asList(1, 2, 4, 5, 6), list);
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

        assertEquals(asList(7, 6, 5, 4, 3, 2, 1), list);
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
                "package org.drools.compiler.test;\n" +
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
                "import " + MiscTest2.Foo.class.getCanonicalName() + "\n" +
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
                "import " + MiscTest2.A.class.getCanonicalName() + "\n" +
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
                "import " + MiscTest2.A.class.getCanonicalName() + "\n" +
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
                "import " + MiscTest2.Parameter.class.getCanonicalName() + "\n" +
                "import " + MiscTest2.DataSample.class.getCanonicalName() + "\n" +
                "import " + DataSample.class.getCanonicalName() + "\n" +
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
                "import " + MiscTest2.Parameter.class.getCanonicalName() + "\n" +
                "import " + MiscTest2.DataSample.class.getCanonicalName() + "\n" +
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
                "import " + MiscTest2.AbstractBase.class.getCanonicalName() + "\n" +
                "import " + MiscTest2.StringConcrete.class.getCanonicalName() + "\n" +
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
                "import MiscTest2.Answer\n" +
                "import MiscTest2.AnswerGiver\n" +
                "rule \"Test Rule\"\n" +
                "when\n" +
                "   AnswerGiver(Answer == Answer.YES)\n" +
                "then\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL );
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testDeclaredTypeExtendingInnerClass() {
        // DROOLS-27
        String str =
                "import " + MiscTest2.StaticPerson.class.getCanonicalName() + "\n" +
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
                "import " + MiscTest2.Model.class.getCanonicalName() + "\n" +
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
                "import " + MiscTest2.IntegerWrapperImpl.class.getCanonicalName() + "\n" +
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
    public void testDeclarationsScopeUsingOR2() {
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
                "    insert( new B( \"B\" ) );\n" +
                "end\n" +
                "\n" +
                "rule R when \n" +
                "    A ( $a1 : a1 != null )\n" +
                "    (or\n" +
                "        B( $b1 : b1 != null )\n" +
                "        B( $b1 : b1 == null )\n" +
                "    )\n" +
                "    eval( $a1.compareTo( $b1 ) < 0 )\n" +
                "then\n" +
                "    System.out.println( $b1 );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testDeclarationsScopeUsingOR3() {
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
                "    (or \n" +
                "        A ( $a1 : a1 != null )\n" +
                "        A ( $a1 : a1 != null ) ) \n" +
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
        assertEquals(3, ksession.fireAllRules());
    }

    @Test
    public void testDeclarationsScopeUsingOR4() {
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
                "rule R when \n" +
                "    A ( $a1 : a1 != null )\n" +
                "    (or\n" +
                "        (and\n" +
                "            B( $b1 : b1 != null )\n" +
                "            eval( $a1.compareTo( $b1 ) < 0 )\n" +
                "        )\n" +
                "        (and\n" +
                "            B( b1 == null )\n" +
                "            eval( $a1.compareTo( $b1 ) < 0 )\n" +
                "        )\n" +
                "    )\n" +
                "then\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue(kbuilder.hasErrors());
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
        assertTrue(kbuilder.hasErrors());
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
    public void testNumberInQuotes() throws Exception {
        // DROOLS-68
        String str =
                "declare A\n" +
                "    a1 : String\n" +
                "end\n" +
                "declare B\n" +
                "    b1 : String\n" +
                "end\n" +
                "\n" +
                "rule Init salience 10 when \n" +
                "then\n" +
                "    insert( new A( \"40\" ) );\n" +
                "    insert( new A( \"2abc\" ) );\n" +
                "    insert( new B( \"300\" ) );\n" +
                "end\n" +
                "\n" +
                "rule R1 when\n" +
                "   A( $a1 : a1 ) \n" +
                "   B( b1 > $a1 ) \n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testStringCoercionComparison() {
        // DROOLS-167
        String str = "import " + Person.class.getName() + ";\n" +
                     "rule R1 when\n" +
                     " $p : Person( name < \"90201304122000000000000017\" )\n" +
                     "then end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Person( "90201304122000000000000015", 38 ) );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testAvoidUnwantedSemicolonWhenDelimitingExpression() {
        // DROOLS-86
        String str =
                "global java.util.List l\n" +
                "rule rule1 \n" +
                "    dialect \"mvel\" \n" +
                "when \n" +
                "then \n" +
                "    String s = \"http://onefineday.123\";\n" +
                "    l.add(s);\n" +
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
                " Number(longValue < (Long)7)\n" +
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
                "package org.drools.compiler.integrationtests;\n" +
                "" +
                "import org.drools.compiler.integrationtests.MiscTest2.Foo2; \n" +
                "" +
                "global java.util.List list; \n" +
                "\n" +
                "rule \"Prep\" \n" +
                "when \n" +
                "  $packs : java.util.Collection() \n" +
                "then \n" +
                "   ((org.drools.core.impl.InternalKnowledgeBase)drools.getKieRuntime().getKieBase()).addKnowledgePackages( $packs );" +
                "end \n" +
                "" +
                "rule \"Self-change\"\n" +
                "when\n" +
                "  String( this == \"go\" )\n" +
                "then\n" +
                "   ((org.drools.core.impl.InternalKnowledgeBase)drools.getKieRuntime().getKieBase()).removeRule( \"org.drools.compiler.integrationtests\", \"React\" ); \n" +
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
                "package org.drools.compiler.integrationtests;\n" +
                "" +
                "import org.drools.compiler.integrationtests.MiscTest2.Foo2; \n" +
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
                "package org.drools.compiler.integrationtests;\n" +
                "import org.drools.compiler.integrationtests.MiscTest2.SimpleEvent\n" +
                "declare SimpleEvent\n" +
                "    @role(event)\n" +
                "    @duration(duration)\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    }

    @Test
    public void testDurationAnnotationOnKie() {
        // DROOLS-94
        String str =
                "package org.drools.compiler.integrationtests;\n" +
                "import org.drools.compiler.integrationtests.MiscTest2.SimpleEvent\n" +
                "declare SimpleEvent\n" +
                "    @role(event)\n" +
                "    @duration(duration)\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", str);
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
    }

    @Test
    public void testDurationAnnotationWithError() {
        // DROOLS-94
        String str =
                "package org.drools.compiler.integrationtests;\n" +
                "import org.drools.compiler.integrationtests.MiscTest2.SimpleEvent\n" +
                "declare SimpleEvent\n" +
                "    @role(event)\n" +
                "    @duration(duratio)\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testPhreakWithConcurrentUpdates() {
        // DROOLS-7
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R when\n" +
                "  $s : String()\n" +
                "  $i : Integer()\n" +
                "  not Person( age == $i, name.startsWith($s) )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(30);
        ksession.insert(31);
        ksession.insert("B");
        ksession.insert("D");

        Person pA = new Person("AAA", 30);
        Person pB = new Person("BBB", 30);
        Person pC = new Person("CCC", 31);
        Person pD = new Person("DDD", 31);

        FactHandle fhB = ksession.insert(pB);
        FactHandle fhD = ksession.insert(pD);
        FactHandle fhA = ksession.insert(pA);
        FactHandle fhC = ksession.insert(pC);

        ksession.fireAllRules();

        pB.setAge(31);
        pB.setName("DBB");
        ksession.update(fhB, pB);

        pD.setAge(30);
        pD.setName("BDD");
        ksession.update(fhD, pD);

        assertEquals(0, ksession.fireAllRules());

        pB.setAge(30);
        pB.setName("BBB");
        ksession.update(fhB, pB);

        pD.setAge(31);
        pD.setName("DDD");
        ksession.update(fhD, pD);

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testPhreakWith2Nots() {
        // DROOLS-7
        String str =
                "import org.drools.compiler.Person\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  Person( $age : age, $name : name )\n" +
                "  not Person( name == $name, age == $age + 1 )\n" +
                "  not Person( name == $name, age == $age - 1 )\n" +
                "then\n" +
                "  list.add($age);\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        Person p1 = new Person("AAA", 31);
        Person p2 = new Person("AAA", 34);
        Person p3 = new Person("AAA", 33);

        FactHandle fh1 = ksession.insert(p1);
        FactHandle fh3 = ksession.insert(p3);
        FactHandle fh2 = ksession.insert(p2);

        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals(31, (int)list.get(0));

        list.clear();

        p1.setAge(35);
        ksession.update(fh1, p1);
        p3.setAge(31);
        ksession.update(fh3, p3);

        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals(31, (int)list.get(0));
    }

    @Test
    public void testPhreakTMS() {
        // DROOLS-7
        String str =
                "import org.drools.compiler.Person\n" +
                "import org.drools.compiler.Cheese\n" +
                "rule R when\n" +
                "  Person( $age : age, $name : name == \"A\" )\n" +
                "  not Person( age == $age + 1 )\n" +
                "then\n" +
                "  insertLogical(new Cheese(\"gorgonzola\", 10));\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person p1 = new Person("A", 31);
        FactHandle fh1 = ksession.insert(p1);

        ksession.fireAllRules();

        assertEquals(1, ksession.getObjects(new ClassObjectFilter(Cheese.class)).size());

        Person p2 = new Person("A", 32);
        FactHandle fh2 = ksession.insert(p2);

        ksession.fireAllRules();

        assertEquals(1, ksession.getObjects(new ClassObjectFilter(Cheese.class)).size());
    }

    @Test
    public void testHelloWorld() throws Exception {
        // DROOLS-145
        String drl = "package org.drools.test\n" +
                     "declare Message\n" +
                     "   message : String\n" +
                     "end\n" +
                     "rule R1 when\n" +
                     "   $m : Message( message == \"Hello World\" )\n" +
                     "then\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", drl);

        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals(0, builder.getResults().getMessages().size());
        ks.getRepository().addKieModule(builder.getKieModule());

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        FactType messageType = ksession.getKieBase().getFactType("org.drools.test", "Message");
        Object message = messageType.newInstance();
        messageType.set(message, "message", "Hello World");

        ksession.insert(message);
        assertEquals( 1, ksession.fireAllRules() );

        KieSession ksession2 = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        FactType messageType2 = ksession2.getKieBase().getFactType("org.drools.test", "Message");
        Object message2 = messageType2.newInstance();
        messageType.set(message2, "message", "Hello World");

        ksession2.insert(message2);
        assertEquals( 1, ksession2.fireAllRules() );
    }

    public static class Lecture {
        private final String id;
        private int day;
        private int index;
        private boolean available;

        public Lecture(String id, int day, int index) {
            this(id, day, index, true);
        }

        public Lecture(String id, int day, int index, boolean available) {
            this.id = id;
            this.day = day;
            this.index = index;
            this.available = available;
        }

        public String getId() {
            return id;
        }

        public int getDay() {
            return day;
        }

        public Lecture setDay(int day) {
            this.day = day;
            return this;
        }

        public int getIndex() {
            return index;
        }

        public Lecture setIndex(int index) {
            this.index = index;
            return this;
        }

        public boolean isAvailable() {
            return available;
        }

        public Lecture setAvailable(boolean available) {
            this.available = available;
            return this;
        }

        @Override
        public String toString() {
            return id + " - " + "day = " + getDay() + "; index = " + getIndex();
        }
    }

    @Test
    public void testPhreakInnerJoinNot() {
        // DROOLS-7
        String str =
                "import org.drools.compiler.integrationtests.MiscTest2.Lecture\n" +
                "global java.util.List list;\n" +
                "rule \"curriculumCompactness\"\n" +
                "    when\n" +
                "        $lecture : Lecture(\n" +
                "            $day : day, $index : index\n" +
                "        )\n" +
                "        not Lecture(\n" +
                "            day == $day, index == ($index + 1)\n" +
                "        )\n" +
                "    then\n" +
                "        list.add($lecture.getId());\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        Lecture lA = new Lecture("A", 0, 4);
        Lecture lB = new Lecture("B", 2, 2);
        Lecture lC = new Lecture("C", 2, 1);

        FactHandle fhA = ksession.insert(lA);
        FactHandle fhB = ksession.insert(lB);
        FactHandle fhC = ksession.insert(lC);

        ksession.fireAllRules(); // C gets blocked by B

        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList("A", "B")));
        list.clear();

        ksession.update(fhB, lB.setDay(0).setIndex(4));
        ksession.update(fhC, lC.setDay(0).setIndex(3));
        ksession.fireAllRules(); // B is still a valid blocker for C

        assertEquals(1, list.size());
        assertTrue(list.contains("B"));
        list.clear();

        ksession.update(fhB, lB.setDay(2).setIndex(2));
        ksession.fireAllRules(); // C doesn't find A as blocker

        assertEquals(1, list.size());
        assertTrue(list.contains("B"));
    }

    @Test
    public void testPhreakAccumulate() {
        // DROOLS-7
        String str =
                "import org.drools.compiler.integrationtests.MiscTest2.Lecture\n" +
                "global java.util.List list;\n" +
                "rule \"R1\"\n" +
                "    when\n" +
                "        $lecture : Lecture(\n" +
                "            $day : day, $index : index\n" +
                "        )\n" +
                "        not Lecture(\n" +
                "            day == $day, index == ($index + 1)\n" +
                "        )\n" +
                "    then\n" +
                "        list.add($lecture.getId());\n" +
                "end\n" +
                "rule \"R2\"\n" +
                "    when\n" +
                "        $availableLectures : Number(intValue > 0) from accumulate(\n" +
                "            $lecture : Lecture(\n" +
                "                available == true\n" +
                "            ),\n" +
                "            count($lecture)\n" +
                "        )\n\n" +
                "    then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        Lecture lA = new Lecture("A", 0, 4, true);
        Lecture lB = new Lecture("B", 2, 2, true);
        Lecture lC = new Lecture("C", 2, 1, true);

        FactHandle fhA = ksession.insert(lA);
        FactHandle fhB = ksession.insert(lB);
        FactHandle fhC = ksession.insert(lC);

        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList("A", "B")));
        list.clear();

        ksession.update(fhB, lB.setAvailable(false));
        ksession.fireAllRules();

        ksession.update(fhB, lB.setDay(0).setIndex(3));
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList("B", "C")));
        list.clear();
    }

    @Test
    public void testQueryAndRetract() {
        // DROOLS-7
        String str =
                "global java.util.List list\n" +
                "\n" +
                "query q (String $s)\n" +
                "    String( this == $s )\n" +
                "end" +
                "\n" +
                "rule R1 when\n" +
                "    $x : String( this == \"x\" )\n" +
                "    ?q( \"x\"; )\n" +
                "then\n" +
                "    final java.util.List l = list;" +
                "    org.drools.core.common.AgendaItem item = ( org.drools.core.common.AgendaItem ) drools.getMatch();\n" +
                "    item.setActivationUnMatchListener( new org.kie.internal.event.rule.ActivationUnMatchListener() {\n" +
                "        public void unMatch(org.kie.api.runtime.rule.Session wm, org.kie.api.runtime.rule.Match activation) {\n" +
                "            l.add(\"pippo\");\n" +
                "        }\n" +
                "    } );" +
                "    retract( \"x\" );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal("list",list);

        ksession.insert("x");
        ksession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test(timeout = 5000)
    public void testPhreakNoLoop() {
        // DROOLS-7
        String str =
                "declare Person \n" +
                "    name : String\n" +
                "    age : int\n" +
                "end\n" +
                "\n" +
                "rule Init when \n" +
                "then\n" +
                "    insert( new Person( \"Mario\", 39 ) );\n" +
                "end\n" +
                "\n" +
                "rule R no-loop when\n" +
                "    $p: Person( name == \"Mario\" )\n" +
                "    not String( this == \"go\" )\n" +
                "then\n" +
                "    modify( $p ) { setAge( 40 ) };\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testAddSameResourceTwice() {
        // DROOLS-180
        String str =
                "rule R when\n" +
                "  $s : String()\n" +
                "then\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasResults(ResultSeverity.INFO, ResultSeverity.WARNING, ResultSeverity.ERROR));
    }

    @Test
    public void testTwoTimers() {
        // BZ-980385
        String str =
                "import java.util.Date\n" +
                "import java.util.List\n" +
                "\n" +
                "global List dates\n" +
                "\n" +
                "rule \"intervalRule\"\n" +
                "  timer(int: 200ms 100ms)\n" +
                "when\n" +
                "  String(this == \"intervalRule\")\n" +
                "then\n" +
                "  Date date = new Date();\n" +
                "  dates.add(date);\n" +
                "end\n" +
                "\n" +
                "\n" +
                "// this rule stops timer\n" +
                "rule \"stopIntervalRule\"\n" +
                "  timer(int: 320ms)\n" +
                "when\n" +
                "  $s : String(this == \"intervalRule\")\n" +
                "then\n" +
                "  retract($s);\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        assertEquals(0, ks.newKieBuilder( kfs ).buildAll().getResults().getMessages().size());

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
    }

    @Test
    public void testUnsupportedPolymorphicDeclaration() {
        // DROOLS-162
        String drl = "package org.drools.test; \n" +
                     "" +
                     "declare Foo end \n" +
                     "declare Bar end \n" +
                     "" + "" +
                     "rule X when\n" +
                     " $x : Foo() " +
                     " or " +
                     " $x : Bar() \n" +
                     "then\n" +
                     " System.out.println( $x ); \n" +
                     "end\n" +
                     "" +
                     "rule Init\n" +
                     "when\n" +
                     "then\n" +
                     " insert( new Foo() ); \n" +
                     " insert( new Bar() ); \n" +
                     "end";
        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        System.err.println( kb.getErrors().toString() );
        assertTrue( kb.hasErrors() );
    }

    @Test
    public void testLegacySalienceResolver() {
        // DROOLS-159
        String drl = "package org.drools.test; \n" +
                     "" +
                     "global java.util.List list; \n " +
                     "" +
                     "rule X salience 10 \n" +
                     "then\n" +
                     " list.add( 1 ); \n" +
                     "end\n" +
                     "" +
                     "rule Y salience 5 \n" +
                     "then\n" +
                     " list.add( 2 ); \n" +
                     "end\n" +
                     "";

        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        assertFalse( kb.hasErrors() );

        KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        ((RuleBaseConfiguration) kbconf).setConflictResolver( SalienceConflictResolver.getInstance() );

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( kbconf );
        knowledgeBase.addKnowledgePackages( kb.getKnowledgePackages() );
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );
        knowledgeSession.fireAllRules();

        assertEquals( Arrays.asList( 1, 2 ), list );
    }

    @Test
    public void testUnaryNegation() {
        // DROOLS-177
        String str =
                "rule R when\n" +
                "    Integer( $a: intValue )\n" +
                "    Integer( intValue > $a, intValue == -$a )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(3);
        ksession.insert(-3);

        assertEquals(1, ksession.fireAllRules());
    }
}