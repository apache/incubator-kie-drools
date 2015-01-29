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
import org.drools.compiler.Message;
import org.drools.compiler.Person;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialectConfiguration;
import org.drools.core.ClassObjectFilter;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.RightTupleSets;
import org.drools.core.conflict.SalienceConflictResolver;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FactTemplateImpl;
import org.drools.core.facttemplates.FieldTemplate;
import org.drools.core.facttemplates.FieldTemplateImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Salience;
import org.drools.core.util.FileManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.event.kiebase.DefaultKieBaseEventListener;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
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
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;

/**
 * Run all the tests with the ReteOO engine implementation
 */
public class Misc2Test extends CommonTestMethodBase {

    private static final Logger logger = LoggerFactory.getLogger(Misc2Test.class);

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
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase knowledgeBase  = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

        Address address = new Address();

        address.setSuburb("xyz");
        FactHandle addressHandle = ksession.insert(address);

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
    public void testNPEOnMutableGlobal() throws Exception {
        // BZ-1019473
        String str = "package org.drools.compiler\n" +
                "global java.util.List context\n" + 
                "rule B\n" + 
                "  when\n" +
                "    Message( message == \"b\" )\n" +
                "    $s : String() from context\n" + 
                "  then\n" + 
                "    System.out.println($s);\n" + 
                "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newByteArrayResource(str.getBytes()).setTargetPath("org/drools/compiler/rules.drl") );
        
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        
        assertEquals(0, kbuilder.getResults().getMessages().size());

        ks.newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        KieSession ksession = ks.newKieContainer(kbuilder.getKieModule().getReleaseId()).newKieSession();
        assertNotNull( ksession );
        
        List<String> context = new ArrayList<String>();
        ksession.setGlobal("context", context);
        
        FactHandle b = ksession.insert( new Message( "b" ) );
        ksession.delete(b);
        int fired = ksession.fireAllRules(1);
        
        assertEquals( 0, fired );
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
            kconf.setOption( RuleEngineOption.PHREAK );
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
    public void testEvalBeforeNot() {
        String str =
                "package org.drools.compiler.integration; \n" +
                "import " + A.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule r1\n" +
                "   salience 10\n" +
                "   when\n" +
                "      eval( list.size() == 0 ) \n" +
                "      not  A( )" +
                "   then\n" +
                "       System.out.println('xxx');\n" +
                "end\n" +
                "\n";

        System.out.println( str );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
    }

    @Test
    public void testKnowledgeBaseEventSupportLeak() throws Exception {
        // JBRULES-3666
        KnowledgeBase kbase = getKnowledgeBase();
        KieBaseEventListener listener = new DefaultKieBaseEventListener();
        kbase.addEventListener(listener);
        kbase.addEventListener(listener);
        assertEquals(1, ((KnowledgeBaseImpl) kbase).getKieBaseEventListeners().size());
        kbase.removeEventListener(listener);
        assertEquals(0, ((KnowledgeBaseImpl) kbase).getKieBaseEventListeners().size());
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
        ((InternalAgenda)ksession.getAgenda()).activateRuleFlowGroup("test");
        ksession.fireAllRules();
        assertEquals(1, res.size());
        res.clear();

        ksession.delete(fact1);

        FactHandle fact2 = ksession.insert(new Person("Mario", 48));
        try {
            ((InternalAgenda)ksession.getAgenda()).activateRuleFlowGroup("test");
            ksession.fireAllRules();
            fail("should throw an Exception");
        } catch (Exception e) { }
        ksession.delete(fact2);

        assertEquals(0, res.size());

        // try to reuse the ksession after the Exception
        FactHandle fact3 = ksession.insert(new Person("Mario", 38));
        ((InternalAgenda)ksession.getAgenda()).activateRuleFlowGroup("test");
        ksession.fireAllRules();
        assertEquals(1, res.size());
        ksession.delete(fact3);

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
                "   delete($number);\n" +
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
                "   delete($number);\n" +
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
                "import " + Misc2Test.Foo.class.getCanonicalName() + "\n" +
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
                "import " + Misc2Test.A.class.getCanonicalName() + "\n" +
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
                "import " + Misc2Test.A.class.getCanonicalName() + "\n" +
                "rule r4 when\n" +
                "    $a : A( f2 == 1, f4 == 1 )" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        FactHandle fh = ksession.insert(new A(1, 1, 1, 1));

        ksession.fireAllRules();

        kbase.addKnowledgePackages( loadKnowledgePackagesFromString( str2 ) );

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

        public A() {

        }

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
                "import " + Misc2Test.Parameter.class.getCanonicalName() + "\n" +
                "import " + Misc2Test.DataSample.class.getCanonicalName() + "\n" +
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
                "import " + Misc2Test.Parameter.class.getCanonicalName() + "\n" +
                "import " + Misc2Test.DataSample.class.getCanonicalName() + "\n" +
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
                "import " + Misc2Test.AbstractBase.class.getCanonicalName() + "\n" +
                "import " + Misc2Test.StringConcrete.class.getCanonicalName() + "\n" +
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
                "import Misc2Test.Answer\n" +
                "import Misc2Test.AnswerGiver\n" +
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
                "import " + Misc2Test.StaticPerson.class.getCanonicalName() + "\n" +
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
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testJitConstraintWithOperationOnBigDecimal() {
        // DROOLS-32
        String str =
                "import " + Misc2Test.Model.class.getCanonicalName() + "\n" +
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
                "import " + Misc2Test.IntegerWrapperImpl.class.getCanonicalName() + "\n" +
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
    public void testJitComparable2() {
        // DROOLS-469
        String str =
                "import " + Misc2Test.IntegerWrapperImpl2.class.getCanonicalName() + "\n" +
                "\n" +
                "rule \"minCost\"\n" +
                "when\n" +
                "    $a : IntegerWrapperImpl2()\n" +
                "    IntegerWrapperImpl2( this < $a )\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert(new IntegerWrapperImpl2(2));
        ksession.insert(new IntegerWrapperImpl2(3));

        assertEquals(1, ksession.fireAllRules());
    }

    interface IntegerWraper2 extends Comparable<IntegerWraper2> {
        int getInt();
    }

    public static abstract class AbstractIntegerWrapper2 implements IntegerWraper2 { }

    public static class IntegerWrapperImpl2 extends AbstractIntegerWrapper2 {

        private final int i;

        public IntegerWrapperImpl2(int i) {
            this.i = i;
        }

        public int compareTo(IntegerWraper2 o) {
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


    @Test
    public void testSelfChangingRuleSet() {
        // DROOLS-92
        String str =
                "package org.drools.compiler.integrationtests;\n" +
                "" +
                "import org.drools.compiler.integrationtests.Misc2Test.Foo2; \n" +
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
                "import org.drools.compiler.integrationtests.Misc2Test.Foo2; \n" +
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
                "import org.drools.compiler.integrationtests.Misc2Test.SimpleEvent\n" +
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
                "import org.drools.compiler.integrationtests.Misc2Test.SimpleEvent\n" +
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
                "import org.drools.compiler.integrationtests.Misc2Test.SimpleEvent\n" +
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
        messageType2.set(message2, "message", "Hello World");

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
                "import org.drools.compiler.integrationtests.Misc2Test.Lecture\n" +
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
                "import org.drools.compiler.integrationtests.Misc2Test.Lecture\n" +
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
                "        public void unMatch(org.kie.api.runtime.rule.RuleRuntime wm, org.kie.api.runtime.rule.Match activation) {\n" +
                "            l.add(\"pippo\");\n" +
                "        }\n" +
                "    } );" +
                "    delete( \"x\" );\n" +
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
                "  delete($s);\n" +
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
                "import org.drools.compiler.integrationtests.Misc2Test.Conversation;\n" +
                "global java.util.List list;" +
                "\n" +
                "rule \"familyEnd\" when\n" +
                "   $conversation : Conversation(\n" +
                "       family != null, $family: family, \n" +
                "       $timeslot: timeslot)\n" +
                "\n" +
                "   not Conversation(\n" +
                "       family == $family, \n" +
                "       timeslot > $timeslot);\n" +
                "then\n" +
                "   list.add($conversation);\n" +
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
    public void testFailedStaticImport() {
        // DROOLS-155
        String drl = "package org.drools.test; \n" +
                     "" +
                     "import function org.does.not.exist.Foo; \n" +
                     "" + "" +
                     "rule X when\n" +
                     "then\n" +
                     "end";
        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        assertTrue( kb.hasErrors() );
    }
    
    @Test
    public void testNamedConsequence() {
        List<String> firedRules = new ArrayList<String>();
        String str =
                "import " + Foo.class.getCanonicalName() + "\n" +
                "import " + Foo2.class.getCanonicalName() + "\n" +
                "global java.util.List fired;\n" +
                "rule \"weird foo\"\n" +
                "    when\n" +
                "        \n" +
                "        $foo: Foo($x: x)\n" +
                "        if( $foo.getX() != 1 )  break[needThis]\n" +
                "        $foo2: Foo2(x == $x);\n" +
                "    then\n" +
                "        fired.add(\"We made it!\");\n" +
                "    then[needThis]\n" +
                "        modify($foo){\n" +
                "            setX(1)\n" +
                "        };\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("fired", firedRules);
        ksession.insert(new Foo());
        ksession.insert(new Foo2(1));
        ksession.fireAllRules();

        assertEquals(1, firedRules.size());
    }

    @Test
    public void testNamedConsequenceWithNot() {
        List<String> firedRules = new ArrayList<String>();
        String str =
                "import " + Foo.class.getCanonicalName() + "\n" +
                "import " + Foo2.class.getCanonicalName() + "\n" +
                "global java.util.List fired;\n" +
                "rule \"weird foo\"\n" +
                "    when\n" +
                "        $foo: Foo($x: x)\n" +
                "        if( $foo.getX() != 1 ) break[needThis] \n" +
                "        not( Foo(x == 2) ) \n" +
                "        $foo2: Foo2(x == $x)\n" +
                "    then\n" +
                "        fired.add(\"We made it!\");\n" +
                "    then[needThis]\n" +
                "        modify($foo){\n" +
                "            setX(1)\n" +
                "        };\n" +
                "end";
        
        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("fired", firedRules);
        ksession.insert(new Foo());
        ksession.insert(new Foo2(1));
        ksession.fireAllRules();
        
        assertEquals(1, firedRules.size());
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

    public static class Foo2 {
        @Position(0)
        public int x;
        public Foo2() { }
        public Foo2(int x) {
            this.x = x;
        }
        public int getX() {
            return x;
        }
        public void setX(int x) {
            this.x = x;
        }
    }

    @Test
    public void testBetaNodeInSubnetworkInStreamMode() {
        // BZ-995408
        String str =
                "import " + Foo.class.getCanonicalName() + "\n" +
                "\n" +
                "global java.util.List context;\n" +
                "\n" +
                "declare Foo\n" +
                "    @role( event )\n" +
                "end\n" +
                "\n" +
                "rule \"Rule A\"\n" +
                "when\n" +
                "    $f : Foo( )\n" +
                "    not ( Integer() from context )\n" +
                "then\n" +
                "    $f.setX( 2 );\n" +
                "end";

        KieBaseConfiguration kBaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kBaseConf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBaseFromString(kBaseConf, str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal("context", new ArrayList() {{
            add(new Long(0));
        }});

        Foo foo = new Foo();
        foo.setX(1);
        ksession.insert(foo);
        ksession.fireAllRules();

        assertEquals(2, foo.getX());
    }

    @Test
    public void testIsAWith2KContainers() {
        // BZ-996056
        String str =
                "import org.drools.compiler.Person\n" +
                "\n" +
                "global java.util.List students\n" +
                "\n" +
                "declare trait Student\n" +
                "    school : String\n" +
                "end\n" +
                "\n" +
                "rule \"create student\" \n" +
                "    when\n" +
                "        $student : Person( age < 26, this not isA Student )\n" +
                "    then\n" +
                "        Student s = don( $student, Student.class );\n" +
                "        s.setSchool(\"Masaryk University\");\n" +
                "        update( $student );\n" +
                "end\n" +
                "\n" +
                "rule \"found student\"\n" +
                "    salience 10\n" +
                "    when\n" +
                "        student : Person( this isA Student )\n" +
                "    then\n" +
                "        students.add(student);\n" +
                "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/isA.drl", str);

        KieBuilder kbuilder = ks.newKieBuilder(kfs);

        kbuilder.buildAll();
        assertEquals(0, kbuilder.getResults().getMessages().size());

        ks.newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = ks.newKieContainer(kbuilder.getKieModule().getReleaseId()).newKieSession();
        assertNotNull( ksession );

        List students = new ArrayList();
        ksession.setGlobal("students", students);
        ksession.insert(new Person("tom", 20));
        ksession.fireAllRules();
        assertEquals(1, students.size());
    }

    @Test
    public void testAutomaticallySwitchFromReteOOToPhreak() {
        String str = "rule R when then end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/rule.drl", str);

        KieBuilder kbuilder = ks.newKieBuilder(kfs);

        kbuilder.buildAll();
        assertEquals(0, kbuilder.getResults().getMessages().size());

        KieBaseConfiguration conf = ks.newKieBaseConfiguration();
        conf.setOption(RuleEngineOption.RETEOO);
        KieBase kbase = ks.newKieContainer(kbuilder.getKieModule().getReleaseId()).newKieBase(conf);
        KieSession ksession = kbase.newKieSession();
        ksession.fireAllRules();
    }

    @Test
    public void testListnersOnStatlessKieSession() {
        // DROOLS-141
        // BZ-999491
        String str =
                "rule R when\n" +
                "  String()\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatelessKieSession ksession = kbase.newStatelessKnowledgeSession();

        final List<String> firings = new ArrayList<String>();

        AgendaEventListener agendaEventListener = new AgendaEventListener() {
            public void matchCreated(org.kie.api.event.rule.MatchCreatedEvent event) {
            }

            public void matchCancelled(org.kie.api.event.rule.MatchCancelledEvent event) {
            }

            public void beforeMatchFired(org.kie.api.event.rule.BeforeMatchFiredEvent event) {
            }

            public void afterMatchFired(org.kie.api.event.rule.AfterMatchFiredEvent event) {
                firings.add("Fired!");
            }

            public void agendaGroupPopped(org.kie.api.event.rule.AgendaGroupPoppedEvent event) {
            }

            public void agendaGroupPushed(org.kie.api.event.rule.AgendaGroupPushedEvent event) {
            }

            public void beforeRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
            }

            public void afterRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
            }

            public void beforeRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
            }

            public void afterRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
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
                "import org.drools.compiler.integrationtests.Misc2Test.Promotion;\n" +
                "\n" +
                "declare Person\n" +
                "	name : String\n" +
                "	dateOfBirth : Date\n" +
                "end\n" +
                "\n" +
                "declare Employee extends Person\n" +
                "	job : String\n" +
                "end\n" +
                "\n" +
                "rule \"Insert Alice\"\n" +
                "	when\n" +
                "	then\n" +
                "		Employee alice = new Employee(\"Alice\", new Date(1973, 7, 2), \"Vet\");\n" +
                "		insert(alice);\n" +
                "		System.out.println(\"Insert Alice\");\n" +
                "end\n" +
                "\n" +
                "rule \"Insert Bob\"\n" +
                "	when\n" +
                "		Person(name == \"Alice\")\n" +
                "	then\n" +
                "		Person bob = new Person(\"Bob\", new Date(1973, 7, 2));\n" +
                "		insertLogical(bob);\n" +
                "		System.out.println(\"InsertLogical Bob\");\n" +
                "end\n" +
                "\n" +
                "rule \"Insert Claire\"\n" +
                "	when\n" +
                "		Person(name == \"Bob\")\n" +
                "	then\n" +
                "		Employee claire = new Employee(\"Claire\", new Date(1973, 7, 2), \"Student\");\n" +
                "		insert(claire);\n" +
                "		System.out.println(\"Insert Claire\");\n" +
                "end\n" +
                "\n" +
                "rule \"Promote\"\n" +
                "	when\n" +
                "		p : Promotion(n : name, j : job)\n" +
                "		e : Employee(name == n)\n" +
                "	then\n" +
                "		modify(e) {\n" +
                "			setJob(j)\n" +
                "		}\n" +
                "		delete(p);\n" +
                "		System.out.printf(\"Promoted %s to %s%n\", n, j);\n" +
                "end\n";
        
        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        ksession.fireAllRules(); // insertLogical Person(Bob)
        
        // Serialize and Deserialize
        try {
	        Marshaller marshaller = MarshallerFactory.newMarshaller(kbase);
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
                " list.add( 1 ); \n" +
                "end\n" +
                "";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 1 ), list );
    }

    @Test
    public void testMapAccessorWithCustomOp() {
        // DROOLS-216
        String str =
                "import java.util.Map;\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  Map( this[\"x\"] str[startsWith] \"T\" )\n" +
                "  Map( this[\"x\"] soundslike \"Test\" )\n" +
                "then\n" +
                "  list.add( 1 );\n" +
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
                "  Map( $val1 : this[\"x\"], $val1 str[startsWith] \"T\" )\n" +
                "  Map( $val2 : this[\"x\"], $val2 soundslike \"Test\" )\n" +
                "then\n" +
                "  list.add( 1 );\n" +
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

    @Test(timeout = 10000)
    public void testAgendaGroupSalience() {
        // BZ-999360
        String str =
                "global java.util.List ruleList\n" +
                "\n" +
                "rule first\n" +
                "salience 2\n" +
                "when\n" +
                "then\n" +
                "    ruleList.add(\"first\");\n" +
                "    drools.getKnowledgeRuntime().getAgenda().getAgendaGroup(\"agenda\").setFocus();\n" +
                "end\n" +
                "\n" +
                "rule second\n" +
                "agenda-group \"agenda\"\n" +
                "when\n" +
                "    $s : String( this == 'fireRules' )\n" +
                "then\n" +
                "    ruleList.add(\"second\");\n" +
                "end\n" +
                "\n" +
                "rule third\n" +
                "salience 1\n" +
                "when\n" +
                "    $s : String( this == 'fireRules' )\n" +
                "then\n" +
                "    ruleList.add(\"third\");\n" +
                "end\n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf, str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ArrayList<String> ruleList = new ArrayList<String>();
        ksession.setGlobal("ruleList", ruleList);

        ksession.insert("fireRules");
        ksession.fireAllRules();

        assertEquals(ruleList.get(0), "first");
        assertEquals(ruleList.get(1), "second");
        assertEquals(ruleList.get(2), "third");
    }

    @Test
    public void test3IdenticalForall() {
        // BZ-999851
        String str =
                "rule \"Rule with forall_1\"\n" +
                "when\n" +
                "    forall ($obj : Object()\n" +
                "            Object(this == $obj)\n" +
                "    )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule \"Rule with forall_2\"\n" +
                "when\n" +
                "    forall ($obj : Object()\n" +
                "            Object(this == $obj)\n" +
                "    )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule \"Rule with forall_3\"\n" +
                "when\n" +
                "    forall ($obj : Object()\n" +
                "            Object(this == $obj)\n" +
                "    )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();
    }

    @Test
    public void testSegmentInitialization() {
        // BZ-1011993
        String str =
                "import " + Misc2Test.Resource.class.getCanonicalName() + "\n" +
                "import " + Misc2Test.ResourceRequirement.class.getCanonicalName() + "\n" +
                "import " + Misc2Test.Allocation.class.getCanonicalName() + "\n" +
                "rule R" +
                "    when\n" +
                "        $resource : Resource($capacity : capacity)\n" +
                "        $used : Number(intValue > $capacity) from accumulate(\n" +
                "            ResourceRequirement(resource == $resource,\n" +
                "                    $executionMode : executionMode)\n" +
                "            and Allocation(executionMode == $executionMode),\n" +
                "            sum($executionMode)\n" +
                "        )\n" +
                "    then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new ResourceRequirement(new Resource(2), 3));
        ksession.insert(new Allocation(3));

        ksession.fireAllRules();
    }

    public static class Resource {
        private final int capacity;

        public Resource(int capacity) {
            this.capacity = capacity;
        }

        public int getCapacity() {
            return capacity;
        }
    }

    public static class ResourceRequirement {
        private final Resource resource;
        private final int executionMode;

        public ResourceRequirement(Resource resource, int executionMode) {
            this.resource = resource;
            this.executionMode = executionMode;
        }

        public Resource getResource() {
            return resource;
        }

        public int getExecutionMode() {
            return executionMode;
        }
    }

    public static class Allocation {
        private final int executionMode;

        public Allocation(int executionMode) {
            this.executionMode = executionMode;
        }

        public int getExecutionMode() {
            return executionMode;
        }
    }

    public static interface FooIntf {
        public boolean isSafe();
        public void setSafe( boolean safe );
    }

    public static class BarKlass implements FooIntf {
        public boolean isSafe() { return true; }
        public void setSafe( boolean safe ) { }
    }

    @Test
    public void testMvelJittingWithTraitProxies() throws Exception {
        // DROOLS-291
        String drl = "package org.drools.test; \n" +
                     "" +
                     "import org.drools.compiler.integrationtests.Misc2Test.FooIntf; \n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.BarKlass; \n" +
                     "" +
                     "declare BarKlass end \n" +
                     "declare FooIntf end \n" +
                     "" +
                     "declare trait ExtFoo extends FooIntf end \n" +
                     "" +
                     "declare Kore @Traitable safe : boolean end \n" +
                     "" +
                     "rule \"Test2\" when FooIntf( safe == true ) then end \n" +
                     "" +
                     "rule \"In1\" when $s : String() then don( new Kore( true ), ExtFoo.class ); end \n" +
                     "rule \"In2\" when $s : Integer() then insert( new BarKlass() ); end \n" +
                     "" +
                     "";
        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();

        for ( int j = 0; j < 21; j++ ) {
            ks.insert( "x" + j );
            ks.fireAllRules();
        }

        // wait for jitting
        Thread.sleep(100);

        ks.insert( 0 );
        ks.fireAllRules();
    }

    @Test
    public void testReportErrorOnWrongDateEffective() {
        // BZ-1013545
        String drl =
                // ensure no Locale can parse the Date
                 "rule X date-effective \"9-asbrdfh-1974\" when\n" +
                 "    $s : String() " +
                 "then\n" +
                 "end\n";

        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        assertTrue( kb.hasErrors() );
    }

    @Test
    public void testDeleteInFromNode() {
        String drl =
                "global java.util.Map context\n" +
                "\n" +
                "rule A\n" +
                "  when\n" +
                "     $i : Integer()\n" +
                "  then\n" +
                "     context.remove(\"key\");\n" +
                "end\n" +
                "\n" +
                "rule B\n" +
                "  when\n" +
                "    $s : String() from context.get(\"key\")\n" +
                "  then\n" +
                "    System.out.println($s);" +
                "end\n";

        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();

        Map<String, String> context = new HashMap<String, String>();
        context.put("key", "value");
        ks.setGlobal("context", context);

        ks.insert(1);
        ks.fireAllRules();

        context.put("key", "value");
        //ks.insert(2);
        ks.fireAllRules();
    }

    @Test
    public void testSortWithNot() {
        // DROOLS-200
        String str =
                "import java.util.*; \n" +
                "" +
                "global java.util.List list;" +
                "\n" +
                "" +
                "declare Bean \n" +
                " value : Integer @key \n" +
                " mark : boolean = false \n" +
                "end \n" +
                "" +
                "declare Holder\n" +
                " map : Map \n" +
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
                " $h : Holder( $map : map ) \n" +
                " $b : Bean( ! $map.containsKey( value ), $v : value ) \n" +
                " not Bean( ! $map.containsKey( value ), value > $v ) \n" +
                "then \n" +
                " list.add( $v ); \n" +
                " System.out.println( \"Marking \" + $v ); \n" +
                " modify ( $h ) { getMap().put( $v, $b ); } \n" +
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
    public void testExistsOr() {
        // DROOLS-254
        String drl = "package org.drools.test;\n" +
                     "\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare Foo val : String end \n" +
                     "" +
                     "rule Init when $s : String() then delete( $s ); insert( new Foo( $s ) ); end \n" +
                     "" +
                     "rule \"Check Pos\"\n" +
                     "when\n" +
                     " exists ( Foo( val == \"1\" ) or Foo( val == \"2\" ) )\n" +
                     "then\n" +
                     " list.add( \"+\" );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Check Neg\"\n" +
                     "when\n" +
                     " not ( not Foo( val == \"1\" ) and not Foo( val == \"2\" ) )\n" +
                     "then\n" +
                     " list.add( \"-\" );\n" +
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
            ks.getEntryPoint( "x" ).insert( new Integer( j ) );
        }

        Thread.sleep( 1000 );
        ks.halt();

        assertEquals( N, list.size() );
    }

    @Test
    public void testFactStealing() throws Exception {
        String drl = "package org.drools.test; \n" +
                     "import org.drools.compiler.Person; \n " +
                     "global java.util.List list; \n" +
                     "\n" +
                     "\n" +
                     "rule Sleep \n " +
                     "salience 1000 \n" +
                     "when then \n" +
                     "  System.out.println( Thread.currentThread().getName() + \"Zlip\" ); \n" +
                     "  Thread.sleep( 100 ); \n" +
                     "end \n" +
                     "" +
                     "rule FireAtWill\n" +
                     "when  \n" +
                     "  $p : Person( $n : name ) \n" +
                     "then \n" +
                     "  System.out.println( Thread.currentThread().getName() + \" Ill continue later \" ); \n" +
                     "  Thread.sleep( 100 ); \n" +
                     "  System.out.println( Thread.currentThread().getName() + \" Hello >> \" + $n );\n" +
                     "  list.add( $n ); \n" +
                     "end\n" +
                     "\n" +
                     "rule ImDone\n" +
                     "timer( expr:0 )\n" +
                     "when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  System.out.println( Thread.currentThread().getName() + \"Take out \" + $p ); \n" +
                     "  delete( $p );\n" +
                     "  System.out.println( Thread.currentThread().getName() + \"Taken out \" + $p ); \n" +
                     "  if ( list.isEmpty() ) { list.add( $p.getName() ); } \n" +
                     "end\n" +
                     "\n"
                ;
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );

        StatefulKnowledgeSession knowledgeSession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.insert( new Person( "mark", 67 ) );
        knowledgeSession.fireAllRules();

        Thread.sleep( 500 );
        assertEquals( 1, list.size() );
        assertTrue( list.contains( "mark" ) );
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
                "import org.drools.compiler.integrationtests.Misc2Test.SQLTimestamped;\n" +
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
                "import org.drools.compiler.integrationtests.Misc2Test.Foo3;\n" +
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
    public void testLockOnActive1() {
        // the modify changes the hashcode of TradeHeader
        // this forces the 'from' to think it's new. This results in an insert and a delete propagation from the 'from'
        String drl = "" +
                     "package org.drools.test; \n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.TradeBooking;\n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.TradeHeader;\n" +
                     "rule \"Rule1\" \n" +
                     "salience 1 \n" +
                     "when\n" +
                     "  $booking: TradeBooking()\n" +
                     "  $trade: TradeHeader() from $booking.getTrade()\n" +
                     "  not String()\n" +
                     "then\n" +
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
                     "end";
        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();

        final List created = new ArrayList();
        final List cancelled = new ArrayList();
        final List fired = new ArrayList();

        ks.addEventListener( new DefaultAgendaEventListener() {

            public void matchCreated( MatchCreatedEvent event ) {
                created.add( event.getMatch().getRule().getName() );
            }

            public void matchCancelled( MatchCancelledEvent event ) {
                cancelled.add( event.getMatch().getRule().getName() );
            }

            public void afterMatchFired( AfterMatchFiredEvent event ) {
                fired.add( event.getMatch().getRule().getName() );
            }
        } );
        ks.fireAllRules();

        TradeBooking tb = new TradeBookingImpl( new TradeHeaderImpl() );

        ks.insert( tb );
        assertEquals( 1, ks.fireAllRules() );

        assertEquals( 3, created.size() );
        assertEquals( 2, cancelled.size() );
        assertEquals( 1, fired.size() );


        assertEquals( "Rule2", created.get(0));
        assertEquals( "Rule1", created.get(1));
        assertEquals( "Rule2", created.get(2));

        assertEquals( "Rule2", cancelled.get(0));
        assertEquals( "Rule2", cancelled.get(1));

        assertEquals( "Rule1", fired.get(0));
    }

    @Test
    public void testLockOnActive2() {
        // the modify changes the hashcode of TradeHeader
        // this forces the 'from' to think it's new. This results in an insert and a delete propagation from the 'from'
        String drl = "" +
                     "package org.drools.test; \n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.TradeBooking;\n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.TradeHeader;\n" +
                     "rule \"Rule1\" \n" +
                     "lock-on-active true\n" +
                     "salience 1 \n" +
                     "when\n" +
                     "  $booking: TradeBooking()\n" +
                     "  $trade: TradeHeader() from $booking.getTrade()\n" +
                     "then\n" +
                     "  $trade.setAction(\"New\");\n" +
                     "  modify($booking) {}\n" +
                     "end;\n" +
                     "\n" +
                     "rule \"Rule2\"\n" +
                     "when\n" +
                     "  $booking: TradeBooking( )\n" +
                     "  $trade: Object( ) from $booking.getTrade()\n" +
                     "then\n" +
                     "end";
        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();

        final List created = new ArrayList();
        final List cancelled = new ArrayList();
        final List fired = new ArrayList();

        ks.addEventListener( new DefaultAgendaEventListener() {

            public void matchCreated( MatchCreatedEvent event ) {
                created.add( event.getMatch().getRule().getName() );}

            public void matchCancelled( MatchCancelledEvent event ) {
                cancelled.add( event.getMatch().getRule().getName() );
            }

            public void afterMatchFired( AfterMatchFiredEvent event ) {
                fired.add( event.getMatch().getRule().getName() );
            }
        } );
        ks.fireAllRules();

        TradeBooking tb = new TradeBookingImpl( new TradeHeaderImpl() );

        ks.insert( tb );
        assertEquals( 2, ks.fireAllRules() );

        assertEquals( 3, created.size() );
        assertEquals( 1, cancelled.size() );
        assertEquals( 2, fired.size() );

        assertEquals( "Rule1", created.get(0));
        assertEquals( "Rule1", created.get(1));
        assertEquals( "Rule2", created.get(2));

        assertEquals( "Rule1", cancelled.get(0));

        assertEquals( "Rule1", fired.get(0));
        assertEquals( "Rule2", fired.get(1));
    }

    @Test
    public void testLockOnActiveWithModify() {
        String drl = "" +
                     "package org.drools.test; \n" +
                     "import org.drools.compiler.Person; \n" +
                     "" +
                     "rule \"Rule1\" \n" +
                     "@Eager(true) \n" +
                     "salience 1 \n" +
                     "lock-on-active true\n" +
                     "when\n" +
                     "  $p: Person()\n" +
                     "then\n" +
                     "  System.out.println( \"Rule1\" ); \n" +
                     "  modify( $p ) { setAge( 44 ); }\n" +
                     "end;\n" +
                     "\n" +
                     "rule \"Rule2\"\n" +
                     "@Eager(true) \n" +
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
    public void testLockOnActiveWithModifyNoEager() {
        // DROOLS-280
        String drl = "" +
                     "package org.drools.test; \n" +
                     "import org.drools.compiler.Person; \n" +
                     "" +
                     "rule \"Rule1\" \n" +
                     "salience 1 \n" +
                     "lock-on-active true\n" +
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
    public void testClashingRules() {
        //DROOLS-287
        String drl = "package org.drools.test; \n" +
                     "" +
                     "rule \"Rule_>_all\"" +
                     "when then end \n" +
                     "" +
                     "rule \"Rule_<_all\"" +
                     "when then end \n" +
                     "";
        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );

    }

    @Test
    public void testDontFailOnDuplicatedRuleWithDeclaredTypeError() {
        String rule1 =
                "rule \"Some Rule\"\n" +
                "when\n" +
                "   $s: String()\n" +
                "then\n" +
                "end";

        String rule2 =
                "declare DClass\n" +
                "  prop : String\n" +
                "end\n" +
                "rule \"Some Rule\"\n" +
                "when\n" +
                "   $d: DClass()\n" +
                "then\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule1.getBytes()), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource(rule2.getBytes()), ResourceType.DRL );


        //the default behavior of kbuilder is not to fail because of duplicated
        //rules.
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        //We must have 1 INFO result.
        KnowledgeBuilderResults infos = kbuilder.getResults( ResultSeverity.INFO);
        Assert.assertNotNull( infos );
        Assert.assertEquals(1, infos.size());

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

        KnowledgeBuilderConfiguration kbConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbConf.setOption( LanguageLevelOption.DRL6 );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

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
    public void testBindingComplexExpressionWithDRL5() {
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

        KnowledgeBuilderConfiguration kbConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbConf.setOption( LanguageLevelOption.DRL5 );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, drl );
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
    public void testEvalConstraintWithMvelOperator( ) {
        String drl = "rule \"yeah\" " + "\tdialect \"mvel\"\n when "
                     + "Foo( eval( field soundslike \"water\" ) )" + " then " + "end";
        DrlParser drlParser = new DrlParser();
        PackageDescr packageDescr;
        try {
            packageDescr = drlParser.parse( true, drl);
        } catch ( DroolsParserException e ) {
            throw new RuntimeException( e );
        }
        RuleDescr r = packageDescr.getRules().get( 0 );
        PatternDescr pd = (PatternDescr) r.getLhs().getDescrs().get( 0 );
        assertEquals( 1, pd.getConstraint().getDescrs().size() );
    }


    @Test
    public void testManyAccumulatesWithSubnetworks() {
        String drl = "package org.drools.compiler.tests; \n" +
                     "" +
                     "declare FunctionResult\n" +
                     "    father  : Applied\n" +
                     "end\n" +
                     "\n" +
                     "declare Field\n" +
                     "    applied : Applied\n" +
                     "end\n" +
                     "\n" +
                     "declare Applied\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule \"Seed\"\n" +
                     "when\n" +
                     "then\n" +
                     "    Applied app = new Applied();\n" +
                     "    Field fld = new Field();\n" +
                     "\n" +
                     "    insert( app );\n" +
                     "    insert( fld );\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "\n" +
                     "\n" +
                     "rule \"complexSubNetworks\"\n" +
                     "when\n" +
                     "    $fld : Field( $app : applied )\n" +
                     "    $a : Applied( this == $app )\n" +
                     "    accumulate (\n" +
                     "        $res : FunctionResult( father == $a ),\n" +
                     "        $args : collectList( $res )\n" +
                     "    )\n" +
                     "    accumulate (\n" +
                     "        $res : FunctionResult( father == $a ),\n" +
                     "        $deps : collectList( $res )\n" +
                     "    )\n" +
                     "    accumulate (\n" +
                     "        $x : String()\n" +
                     "        and\n" +
                     "        not String( this == $x ),\n" +
                     "        $exprFieldList : collectList( $x )\n" +
                     "    )\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilderConfiguration kbConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        int num = ksession.fireAllRules();
        // only one rule should fire, but the partial propagation of the asserted facts should not cause a runtime NPE
        assertEquals( 1, num );

    }


    @Test
    public void testLinkRiaNodesWithSubSubNetworks() {
        String drl = "package org.drools.compiler.tests; \n" +
                     "" +
                     "import java.util.*; \n" +
                     "" +
                     "global List list; \n" +
                     "" +
                     "declare MyNode\n" +
                     "end\n" +
                     "" +
                     "rule Init\n" +
                     "when\n" +
                     "then\n" +
                     "    insert( new MyNode() );\n" +
                     "    insert( new MyNode() );\n" +
                     "end\n" +
                     "" +
                     "" +
                     "rule \"Init tree nodes\"\n" +
                     "salience -10\n" +
                     "when\n" +
                     "    accumulate (\n" +
                     "                 MyNode(),\n" +
                     "                 $x : count( 1 )\n" +
                     "               )\n" +
                     "    accumulate (\n" +
                     "                 $n : MyNode()\n" +
                     "                 and\n" +
                     "                 accumulate (\n" +
                     "                    $val : Double( ) from Arrays.asList( 1.0, 2.0, 3.0 ),\n" +
                     "                    $rc : count( $val );\n" +
                     "                    $rc == 3 \n" +
                     "                 ),\n" +
                     "                 $y : count( $n )\n" +
                     "               )\n" +
                     "then\n" +
                     "  list.add( $x ); \n" +
                     "  list.add( $y ); \n" +
                     "  System.out.println( $x ); \n" +
                     "  System.out.println( $y ); \n" +
                     "end\n";

        KnowledgeBuilderConfiguration kbConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<Long> list = new ArrayList<Long>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( 2, list.get(0).intValue() );
        assertEquals( 2, list.get(1).intValue() );

    }

    @Test
    public void testDynamicSalienceUpdate() {
        String drl = "package org.drools.compiler.tests; \n" +
                     "" +
                     "import java.util.*; \n" +
                     "" +
                     "global List list; \n" +
                     "" +
                     "declare Foo value : long end \n" +
                     "" +
                     "rule Nop \n" +
                     " salience( $l ) \n" +
                     "when\n" +
                     "  Foo( $l : value ) \n" +
                     "then\n" +
                     "  System.out.println( \"Never Foo \" + $l ); " +
                     "  list.add( $l ); \n" +
                     "end\n" +
                     "" +
                     "rule Insert \n" +
                     " salience 100 \n" +
                     "when \n" +
                     "  $l : Long() \n" +
                     "then \n" +
                     "  System.out.println( \"Insert Foo \" + $l ); " +
                     "  insertLogical( new Foo( $l ) ); \n" +
                     "end \n" +
                     "" +
                     "rule Clean \n" +
                     " salience 50 \n" +
                     "when \n" +
                     "  $s : String() \n" +
                     "  $l : Long() \n" +
                     "then \n" +
                     "  System.out.println( \"delete \" + $l ); " +
                     "  delete( $l ); \n" +
                     "end \n";

        KnowledgeBuilderConfiguration kbConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList(  );
        ksession.setGlobal( "list", list );

        ksession.insert( 1L );
        ksession.insert( 2L );
        ksession.insert( "go" );

        ksession.fireAllRules();

        assertTrue( list.isEmpty() );
        ksession.dispose();
    }

    @Test
    public void testInitialFactLeaking() {
        // DROOLS-239
        String drl = "global java.util.List list;\n" +
                     "rule R when\n" +
                     "    $o : Object()\n" +
                     "then\n" +
                     "    list.add(1);\n" +
                     "end\n";

        KnowledgeBuilderConfiguration kbConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList(  );
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals(0, list.size());

        ksession.insert("1");
        ksession.fireAllRules();
        assertEquals(1, list.size());

        ksession.insert(1);
        ksession.fireAllRules();
        assertEquals(2, list.size());

        ksession.dispose();
    }

    @Test
    public void testNoLoopWithNamedConsequences() {
        // DROOLS-327
        String drl =
                "import org.drools.compiler.Message\n" +
                "rule \"Hello World\" no-loop\n" +
                "    when\n" +
                "        m : Message( myMessage : message )\n" +
                "        if (status == 0) do[sayHello]\n" +
                "    then\n" +
                "        System.out.println( myMessage );\n" +
                "        m.setMessage( \"Goodbye cruel world\" );\n" +
                "        m.setStatus( Message.GOODBYE );\n" +
                "        update( m );\n" +
                "    then[sayHello]\n" +
                "        System.out.println(\"Hello, I'm here!\");\n" +
                "end\n";

        KnowledgeBuilderConfiguration kbConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new Message("Hello World"));
        ksession.fireAllRules();
    }

    @Test
    public void testInsertModifyInteractionsWithLockOnActive() {
        String drl =
                "package org.drools.compiler.integrationtests;\n" +
                "import org.drools.compiler.Message;\n" +
                "global Message m2;\n" +
                "rule r1 lock-on-active\n" +
                "    when\n" +
                "        m: Message()\n" +
                "    then\n" +
                "        modify( m ){ setMessage2( 'msg2' ) };\n" +
                "        m2 =  new Message( 'msg1' );\n" +
                "        kcontext.getKnowledgeRuntime().setGlobal( 'm2', m2 ); \n" +
                "        insert( m2 );\n" +
                "end\n" +
                "rule r2 lock-on-active salience 1000\n" +
                "    when\n" +
                "        m : Message()\n" +
                "    then\n" +
                "        modify( m ){ setMessage3( 'msg3' ) };\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Message m1 = new Message( "msg1" );
        ksession.insert(m1);
        assertEquals(2, ksession.fireAllRules());

        Message m2 = (Message) ksession.getGlobal( "m2" );

        assertEquals( "msg1", m1.getMessage() );
        assertEquals( "msg2", m1.getMessage2() );
        assertEquals( "msg3", m1.getMessage3() );

        assertEquals( "msg1", m2.getMessage() );
        assertEquals( "Two", m2.getMessage2() ); // r1 does not fire for m2
        assertEquals( "Three", m2.getMessage3() );
    }

    @Test(timeout=10000)
    public void testWumpus1() {
        String drl = "import org.drools.compiler.integrationtests.Misc2Test.Hero;\n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.StepForwardCommand;\n" +
                     "global java.util.List list; \n " +
                     "\n" +
                     "\n" +
                     "rule StepLeft when\n" +
                     "    $h  : Hero( goingRight == false )\n" +
                     "    $sc : StepForwardCommand()\n" +
                     "then\n" +
                     "    modify ( $h ) { setPos( $h.getPos()-1 ) };\n" +
                     "    list.add( 'StepLeft' );\n" +
                     "end\n" +
                     "\n" +
                     "rule StepRight when\n" +
                     "    $h  : Hero( goingRight == true )\n" +
                     "    $sc : StepForwardCommand()\n" +
                     "then\n" +
                     "    modify ( $h ) { setPos( $h.getPos()+1 ) };\n" +
                     "    list.add( 'StepRight' );\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Hero hero = new Hero(1);
        ksession.insert(hero);
        ksession.fireAllRules();

        ksession.insert(new StepForwardCommand());
        assertEquals( 1, ksession.fireAllRules() );
        assertEquals(2, hero.getPos());
    }

    @Test(timeout=10000)
    public void testWumpus2() {
        String drl = "import org.drools.compiler.integrationtests.Misc2Test.Hero;\n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.StepForwardCommand;\n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.ChangeDirectionCommand;\n" +
                     "global java.util.List list; \n " +
                     "\n" +
                     "\n" +
                     "rule StepLeft when\n" +
                     "    $sc : StepForwardCommand()\n" +
                     "    $h  : Hero( goingRight == false )\n" +
                     "then\n" +
                     "    modify ( $h ) { setPos( $h.getPos()-1 ) };\n" +
                     "    list.add( 'StepLeft' );\n" +
                     "end\n" +
                     "\n" +
                     "rule StepRight when\n" +
                     "    $sc : StepForwardCommand()\n" +
                     "    $h  : Hero( goingRight == true )\n" +
                     "then\n" +
                     "    modify ( $h ) { setPos( $h.getPos()+1 ) };\n" +
                     "    list.add( 'StepRight' );\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Hero hero = new Hero(1);
        ksession.insert(hero);
        ksession.fireAllRules();

        ksession.insert(new StepForwardCommand());
        assertEquals( 1, ksession.fireAllRules() );
        assertEquals(2, hero.getPos());
    }

    @Test(timeout=10000)
    public void testWumpus3() {
        String drl = "import org.drools.compiler.integrationtests.Misc2Test.Hero;\n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.StepForwardCommand;\n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.ChangeDirectionCommand;\n" +
                     "global java.util.List list; \n " +
                     "\n" +
                     "rule RotateLeft when\n" +
                     "    $h  : Hero( goingRight == true )\n" +
                     "    $dc : ChangeDirectionCommand()\n" +
                     "then\n" +
                     "    retract ( $dc );   \n" +
                     "    modify ( $h ) { setGoingRight( false ) };\n" +
                     "    list.add( 'RotateLeft' );\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule StepLeft when\n" +
                     "    $h  : Hero( goingRight == false )\n" +
                     "    $sc : StepForwardCommand()\n" +
                     "then\n" +
                     "    retract ( $sc );   \n" +
                     "    modify ( $h ) { setPos( $h.getPos()-1 ) };\n" +
                     "    list.add( 'StepLeft' );\n" +
                     "end\n" +
                     "\n" +
                     "rule StepRight when\n" +
                     "    $h  : Hero( goingRight == true )\n" +
                     "    $sc : StepForwardCommand()\n" +
                     "then\n" +
                     "    retract ( $sc );\n" +
                     "    modify ( $h ) { setPos( $h.getPos()+1 ) };\n" +
                     "    list.add( 'StepRight' );\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Hero hero = new Hero(1);
        ksession.insert(hero);
        ksession.fireAllRules();

        ksession.insert(new StepForwardCommand());
        ksession.fireAllRules();
        assertEquals(2, hero.getPos());

        ksession.insert(new ChangeDirectionCommand());
        ksession.fireAllRules();
        ksession.insert(new StepForwardCommand());
        ksession.fireAllRules();
        assertEquals(1, hero.getPos());
    }

    @Test(timeout=10000)
    public void testWumpus4() {
        String drl = "import org.drools.compiler.integrationtests.Misc2Test.Hero;\n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.StepForwardCommand;\n" +
                     "import org.drools.compiler.integrationtests.Misc2Test.ChangeDirectionCommand;\n" +
                     "global java.util.List list; \n " +
                     "\n" +
                     "rule RotateLeft when\n" +
                     "    $dc : ChangeDirectionCommand()\n" +
                     "    $h  : Hero( goingRight == true )\n" +
                     "then\n" +
                     "    retract ( $dc );   \n" +
                     "    modify ( $h ) { setGoingRight( false ) };\n" +
                     "    list.add( 'RotateLeft' );\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule StepLeft when\n" +
                     "    $sc : StepForwardCommand()\n" +
                     "    $h  : Hero( goingRight == false )\n" +
                     "then\n" +
                     "    retract ( $sc );   \n" +
                     "    modify ( $h ) { setPos( $h.getPos()-1 ) };\n" +
                     "    list.add( 'StepLeft' );\n" +
                     "end\n" +
                     "\n" +
                     "rule StepRight when\n" +
                     "    $sc : StepForwardCommand()\n" +
                     "    $h  : Hero( goingRight == true )\n" +
                     "then\n" +
                     "    retract ( $sc );\n" +
                     "    modify ( $h ) { setPos( $h.getPos()+1 ) };\n" +
                     "    list.add( 'StepRight' );\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Hero hero = new Hero(1);
        ksession.insert(hero);
        ksession.fireAllRules();

        ksession.insert(new StepForwardCommand());
        ksession.fireAllRules();
        assertEquals(2, hero.getPos());



        ksession.insert(new ChangeDirectionCommand());
        ksession.fireAllRules();
        ksession.insert(new StepForwardCommand());
        ksession.fireAllRules();
        assertEquals(1, hero.getPos());
    }

    @PropertyReactive
    public static class Hero {
        private int pos = 1;
        private boolean goingRight = true;

        public Hero(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public boolean isGoingRight() {
            return goingRight;
        }

        public void setGoingRight(boolean goingRight) {
            this.goingRight = goingRight;
        }

    }

    public static class ChangeDirectionCommand { }
    public static class StepForwardCommand { }

    @Test
    public void testDynamicSalience() {
        // DROOLS-334
        String drl = "import org.drools.compiler.integrationtests.Misc2Test.SimpleMessage\n" +
                     "\n" +
                     "rule R1\n" +
                     "    salience ( $index )\n" +
                     "    when\n" +
                     "        $m : SimpleMessage( status == SimpleMessage.Status.ENRICHED, $index : index)\n" +
                     "        Number() from accumulate ( SimpleMessage(), count(1) )\n" +
                     "    then\n" +
                     "        System.out.println(\"R1: \" + $m);\n" +
                     "        modify($m) { setStatus(SimpleMessage.Status.FILTERED) }\n" +
                     "end\n" +
                     "\n" +
                     "rule R2\n" +
                     "    salience( -$index )" +
                     "    when" +
                     "        $m : SimpleMessage( status == SimpleMessage.Status.FILTERED, $index : index)\n" +
                     "    then" +
                     "        delete( $m );" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        SimpleMessage[] msgs = new SimpleMessage[] { new SimpleMessage(0), new SimpleMessage(1), new SimpleMessage(2), new SimpleMessage(3) };
        for (SimpleMessage msg : msgs) {
            ksession.insert(msg);
        }

        ksession.fireAllRules();

        for (SimpleMessage msg : msgs) {
            assertEquals(SimpleMessage.Status.FILTERED, msg.getStatus());
        }

        assertEquals(0, ksession.getFactCount());
    }

    public static class SimpleMessage {

        public enum Status { ENRICHED, TO_SEND, SENT, FILTERED }

        private final int index;
        private Status status = Status.ENRICHED;

        public SimpleMessage(int index) {
            this.index = index;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return "SimpleMessage(" + index + "): " + status;
        }
    }

    @Test
    public void testCollectAndAccumulate() {
        // DROOLS-173
        String drl = "import java.util.List\n" +
                     "\n" +
                     "global List list\n" +
                     "\n" +
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
                     "     objList: List(size>=2) from collect( Item(present==false))\n" +
                     "     //Total price bigger than 100\n" +
                     "     price: Number(intValue>=100) from accumulate( Item($w:price, present==false), sum($w))\n" +
                     " then\n" +
                     "\n" +
                     "     list.add(price);\n" +
                     "     list.add(objList.size());\n" +
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
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals(4, list.size());
        assertEquals(160.0, list.get(0));
        assertEquals(4, list.get(1));
        assertEquals(120.0, list.get(2));
        assertEquals(3, list.get(3));
    }

    @Test
    public void testDateCoercionWithOr() {
        // DROOLS-296
        String drl = "import java.util.Date\n" +
                     "global java.util.List list\n" +
                     "declare DateContainer\n" +
                     "     date: Date\n" +
                     "end\n" +
                     "\n" +
                     "rule Init when\n" +
                     "then\n" +
                     "    insert(new DateContainer(new Date(0)));" +
                     "end\n" +
                     "\n" +
                     "rule \"Test rule\"\n" +
                     "when\n" +
                     "    $container: DateContainer( date >= \"15-Oct-2013\" || date <= \"01-Oct-2013\" )\n" +
                     "then\n" +
                     "    list.add(\"working\");\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("working", list.get(0));
    }

    @Test
    public void testMatchingEventsInStreamMode() {
        // DROOLS-338
        String drl =
                    "import org.drools.compiler.integrationtests.Misc2Test.SimpleEvent\n" +
                     "declare SimpleEvent\n" +
                     "    @role(event)\n" +
                     "end\n" +
                     "\n" +
                     "rule \"RuleA\"\n" +
                     "salience 5\n" +
                     "when\n" +
                     "    $f : SimpleEvent( )\n" +
                     "then\n" +
                     "    delete ($f);\n" +
                     "end\n" +
                     "\n" +
                     "rule \"RuleB\"\n" +
                     "when\n" +
                     "    $f : SimpleEvent( )\n" +
                     "then\n" +
                     "end\n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EventProcessingOption.STREAM );

        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final AtomicInteger i = new AtomicInteger(0);

        ksession.addEventListener( new DefaultAgendaEventListener() {
            public void matchCreated( MatchCreatedEvent event ) {
                i.incrementAndGet();
            }

            public void matchCancelled( MatchCancelledEvent event ) {
                i.decrementAndGet();
            }
        } );

        ksession.insert(new SimpleEvent());
        ksession.fireAllRules();

        assertEquals(1, i.get());
    }

    @Test
    public void testRuleDoesNotRefireOnNewSession() {
        // DROOLS-339
        String drl = "\n" +
                     "global java.util.List list\n" +
                     "rule \"Insert Info\"\n" +
                     "when\n" +
                     "then\n" +
                     "    insert( \"aaa\" );\n" +
                     "    insert( new Integer( 12 ) );\n" +
                     "    insert( new Double( 4.0 ) );\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule \"React\"\n" +
                     "when\n" +
                     "    Integer()\n" +
                     "    accumulate (\n" +
                     "                 $n : Double( ) and\n" +
                     "                      Double(  ),\n" +
                     "                 $x : count( $n )\n" +
                     "               )\n" +
                     "    String( )\n" +
                     "then\n" +
                     "    list.add(\"working\");\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("working", list.get(0));
        ksession.dispose();

        ksession = kbase.newStatefulKnowledgeSession();

        list.clear();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("working", list.get(0));
        ksession.dispose();
    }

    @Test
    public void testWildcardImportForTypeField() throws Exception {
        // DROOLS-348
        String drl = "import java.util.*\n" +
                     "declare MyType\n" +
                     "    l : List\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
    }

    @Test
    public void testWildcardImportForTypeFieldOldApi() {
        // DROOLS-348
        String drl = "import java.util.*\n" +
                     "declare MyType\n" +
                     "    l : List\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    }

    @Test
    public void testTypeCheckInOr() {
        // BZ-1029911
        String str = "import org.drools.compiler.*;\n" +
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
    public void testDynamicNegativeSalienceWithSpace() {
        // DROOLS-302
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R\n" +
                "salience - $age\n" +
                "when\n" +
                "  Person( $age : age )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person p1 = new Person("A", 31);
        FactHandle fh1 = ksession.insert(p1);

        ksession.fireAllRules();
    }

    @Test
    public void testJoinNoLoop() {
        // BZ-1034094
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R no-loop\n" +
                "when\n" +
                "  String()\n" +
                "  $p : Person( $age : age )\n" +
                "then\n" +
                "    modify($p) { setAge( $age + 1 ) }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person mario = new Person("Mario", 38);

        ksession.insert("a");
        ksession.insert("b");
        ksession.insert(mario);
        ksession.fireAllRules();

        assertEquals(40, mario.getAge());
    }

    @Test
    public void testConstraintOnSerializable() {
        // DROOLS-372
        String str =
                "import org.drools.compiler.integrationtests.Misc2Test.SerializableValue\n" +
                "rule R\n" +
                "when\n" +
                "  SerializableValue( value == \"1\" )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new SerializableValue("0"));
        ksession.fireAllRules();
    }

    public static class SerializableValue {
        private final Serializable value;

        public SerializableValue(Serializable value) {
            this.value = value;
        }

        public Serializable getValue() {
            return value;
        }
    }

    @Test(timeout = 10000)
    public void testInfiniteLoopUpdatingWithRBTreeIndexing() {
        // BZ-1040032
        String drl =
                "import org.drools.compiler.Person\n" +
                "rule R when\n" +
                "    $p : Person()\n" +
                "    exists Person( age > $p.age, name.contains($p.name.substring(0, 1)) )\n" +
                "then\n" +
                "end";

        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();

        Person[] ps = new Person[4];
        FactHandle[] fhs = new FactHandle[4];

        ps[0] = new Person("a", 5);
        ps[1] = new Person("b", 5);
        ps[2] = new Person("d", 10);
        ps[3] = new Person("a", 15);

        fhs[0] = ks.insert(ps[0]);
        fhs[1] = ks.insert(ps[1]);
        fhs[2] = ks.insert(ps[2]);
        fhs[3] = ks.insert(ps[3]);

        ps[0].setName("c");
        ks.update(fhs[0], ps[0]);
        ks.fireAllRules();

        ps[2].setName("b");
        ks.update(fhs[2], ps[2]);
        ks.fireAllRules();

        ps[2].setName("d");
        ks.update(fhs[2], ps[2]);
        ks.fireAllRules();

        ps[1].setName("c");
        ks.update(fhs[1], ps[1]);
        ks.fireAllRules();

        ps[3].setName("d");
        ks.update(fhs[3], ps[3]);
        ks.fireAllRules();
    }

    public static class AA {
        int id;
        public AA( int i ) { this.id = i; }
        public boolean match( Long value ) { return true; }
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            AA aa = (AA) o;
            if ( id != aa.id ) return false;
            return true;
        }
        public int hashCode() { return id; }
    }
    public static class BB {
        public Integer getValue() { return 42; }
    }

    @Test
    @Ignore
    public void testJitting() {
        // DROOLS-185
        String str =
                " import org.drools.compiler.integrationtests.Misc2Test.AA; " +
                " import org.drools.compiler.integrationtests.Misc2Test.BB; " +
                " global java.util.List list; \n" +
                " " +
                " rule R \n " +
                " when \n" +
                "    BB( $v : value ) \n" +
                "    $a : AA( match( $v ) ) \n" +
                " then \n" +
                "   list.add( $a ); \n" +
                " end \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert( new BB() );
        for ( int j = 0; j < 100; j++ ) {
            ksession.insert( new AA( j ) );
            ksession.fireAllRules();
        }

        assertEquals( 100, list.size() );
    }


    @Test
    @Ignore
    public void testQueryCorruption() {

        String drl = "package drl;\n" +

                     "declare Anon " +
                     "    cld : String @key " +
                     "    sup : String @key " +
                     "end " +

                     "rule Init " +
                     "when " +
                     "then " +
                     "    insert( 'aa' ); " +
                     "    insert( 'bb' ); " +
                     "    insert( 'cc' ); " +
                     "    insertLogical( new Anon( 'aa', 'bb' ) ); " +
                     "    insertLogical( new Anon( 'cc', 'aa' ) ); " +
                     "end " +

                     "query unravel( String $g, String $c ) " +
                     "    ( " +
                     "        ( Anon( $g, $c ; ) and $c := String( this.contains( \"b\" ) ) ) " +
                     "        or " +
                     "        ( Anon( $g, $x ; ) and unravel( $x, $c ; ) ) " +
                     "    ) " +
                     "end " +

                     "rule Check " +
                     "when " +
                     "    Anon( $e, $par ; ) " +
                     "    unravel( $par, $comp ; ) " +
                     "    ( Double() or eval( 1 == 1  ) ) " +
                     "then\n" +
                     "end\n" +

                     "rule Mod " +
                     "no-loop " +
                     "when\n" +
                     "    $a : Anon( ) " +
                     "    ( Double() or eval( 1 == 1 ) ) " +
                     "then " +
                     "    modify ( $a ) { } " +
                     "end " +
                     "";


        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();

    }


    @Test
    public void testPackagingJarWithTypeDeclarations() throws Exception {
        // BZ-1054823
        String drl1 =
                "package org.drools.compiler\n" +
                "import org.drools.compiler.Message\n" +
                "declare Message\n" +
                "   @role (event)\n" +
                "end\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        byte[] jar = createKJar(ks, releaseId, null, drl1);
        KieModule km = deployJar(ks, jar);

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();
        ksession.insert(new Message("Hello World"));
        ksession.fireAllRules();
    }

    @Test
    public void testStagedTupleLeak() throws Exception {
        // BZ-1056599
        String str =
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    insertLogical( $i.toString() );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    delete( $i );\n" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    $l : Long()\n" +
                "    $s : String( this == $l.toString() )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        for (int i = 0; i < 10; i++) {
            ksession.insert(i);
            ksession.fireAllRules();
        }

        Rete rete = ((KnowledgeBaseImpl)kbase).getRete();
        JoinNode joinNode = null;
        for (ObjectTypeNode otn : rete.getObjectTypeNodes()) {
            if ( String.class == otn.getObjectType().getValueType().getClassType() ) {
                joinNode = (JoinNode)otn.getSinkPropagator().getSinks()[0];
                break;
            }
        }

        assertNotNull(joinNode);
        InternalWorkingMemory wm = (InternalWorkingMemory)ksession;
        BetaMemory memory = (BetaMemory)wm.getNodeMemory(joinNode);
        RightTupleSets stagedRightTuples = memory.getStagedRightTuples();
        assertEquals(0, stagedRightTuples.deleteSize());
        assertNull(stagedRightTuples.getDeleteFirst());
        assertEquals(0, stagedRightTuples.insertSize());
        assertNull(stagedRightTuples.getInsertFirst());
    }

    @Test
    public void testJittingConstraintWithArrayParams() throws Exception {
        // BZ-1057000
        String str =
                "import org.drools.compiler.integrationtests.Misc2Test.Strings\n" +
                "\n" +
                "global java.util.List allList;\n" +
                "global java.util.List anyList;\n" +
                "\n" +
                "rule R_all when\n" +
                "    Strings( containsAll(\"1\", \"2\") )\n" +
                "then\n" +
                "    allList.add(\"1\");\n" +
                "end\n" +
                "\n" +
                "rule R_any when\n" +
                "    Strings( containsAny(new String[] {\"1\", \"2\"}) )\n" +
                "then\n" +
                "    anyList.add(\"1\");\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> allList = new ArrayList<String>();
        ksession.setGlobal("allList", allList);
        List<String> anyList = new ArrayList<String>();
        ksession.setGlobal("anyList", anyList);

        ksession.insert(new Strings("1", "2", "3"));
        ksession.insert(new Strings("2", "3"));
        ksession.fireAllRules();

        assertEquals(1, allList.size());
        assertEquals(2, anyList.size());
    }

    public static class Strings {
        private final String[] strings;

        public Strings(String... strings) {
            this.strings = strings;
        }

        public boolean containsAny(String[] array) {
            for (String candidate : array) {
                for (String s : strings) {
                    if (candidate.equals(s)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean containsAll(String... array) {
            int counter = 0;
            for (String candidate : array) {
                for (String s : strings) {
                    if (candidate.equals(s)) {
                        counter++;
                        break;
                    }
                }
            }
            return counter == array.length;
        }
    }

    public static class ARef {
        public static int getSize(String s) {
            return 0;
        }
    }

    public static class BRef extends ARef {
        public static int getSize(String s) {
            return s.length();
        }
    }

    @Test
    public void testJittingConstraintInvokingStaticMethod() throws Exception {
        // DROOLS-410
        String str =
                "dialect \"mvel\"\n" +
                "import org.drools.compiler.integrationtests.Misc2Test.ARef\n" +
                "import org.drools.compiler.integrationtests.Misc2Test.BRef\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule R when\n" +
                "    $s : String( length == BRef.getSize(this) )\n" +
                "then\n" +
                "    list.add($s);\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert("1234");
        ksession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test
    public void testStagedLeftTupleLeak() throws Exception {
        // BZ-1058874
        String str =
                "rule R1 when\n" +
                "    String( this == \"this\" )\n" +
                "    String( this == \"that\" )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();

        for (int i = 0; i < 10; i++) {
            FactHandle fh = ksession.insert("this");
            ksession.fireAllRules();
            ksession.delete(fh);
            ksession.fireAllRules();
        }

        Rete rete = ((KnowledgeBaseImpl)kbase).getRete();
        LeftInputAdapterNode liaNode = null;
        for (ObjectTypeNode otn : rete.getObjectTypeNodes()) {
            if ( String.class == otn.getObjectType().getValueType().getClassType() ) {
                AlphaNode alphaNode = (AlphaNode)otn.getSinkPropagator().getSinks()[0];
                liaNode = (LeftInputAdapterNode)alphaNode.getSinkPropagator().getSinks()[0];
                break;
            }
        }

        assertNotNull(liaNode);
        InternalWorkingMemory wm = (InternalWorkingMemory)ksession;
        LeftInputAdapterNode.LiaNodeMemory memory = (LeftInputAdapterNode.LiaNodeMemory) wm.getNodeMemory( liaNode );
        LeftTupleSets stagedLeftTuples = memory.getSegmentMemory().getStagedLeftTuples();
        assertEquals(0, stagedLeftTuples.deleteSize());
        assertNull(stagedLeftTuples.getDeleteFirst());
        assertEquals(0, stagedLeftTuples.insertSize());
        assertNull(stagedLeftTuples.getInsertFirst());
    }

    public static class EvallerBean {
        private final Evaller evaller = new Evaller(1);
        public Evaller getEvaller() { return evaller; }
    }

    public static class Evaller {
        private final int size;

        public Evaller() { this(0); }
        public Evaller(int size) { this.size = size; }

        public boolean check( Object o ) { return true; }
        public static boolean checkStatic( Object o ) { return true; }

        public int size() { return size; }
        public int getSize() { return size; }
    }

    @Test
    public void testGlobalInConstraint() throws Exception {
        String str =
                "global " + Evaller.class.getCanonicalName() + " evaller;\n" +
                "global java.util.List list;\n" +
                "declare Foo end\n" +
                "rule Init when then insert( new Foo() ); end\n" +
                "rule R1 when\n" +
                "    $s : Foo( evaller.check( this ) == true )\n" +
                "then\n" +
                "    list.add( 42 );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $s : Foo( evaller.check( this ) == false )\n" +
                "then\n" +
                "    list.add( 43 );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list );
        ksession.setGlobal("evaller", new Evaller() );

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(42, (int)list.get(0));
    }

    @Test
    public void testStaticInConstraint() throws Exception {
        String str =
                "import " + Evaller.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "declare Foo end\n" +
                "rule Init when then insert( new Foo() ); end\n" +
                "rule R1 when\n" +
                "    $s : Foo( Evaller.checkStatic( this ) == true )\n" +
                "then\n" +
                "    list.add( 42 );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $s : Foo( Evaller.checkStatic( this ) == false )\n" +
                "then\n" +
                "    list.add( 43 );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list );

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(42, (int)list.get(0));
    }

    @Test
    public void testFieldPrecedenceOverGlobal() throws Exception {
        String str =
                "import " + EvallerBean.class.getCanonicalName() + ";\n" +
                "global " + Evaller.class.getCanonicalName() + " evaller;\n" +
                "global java.util.List list;\n" +
                "rule R1 when\n" +
                "    $s : EvallerBean( evaller.size() == 1 )\n" +
                "then\n" +
                "    list.add( 42 );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list );
        ksession.setGlobal("evaller", new Evaller());

        ksession.insert(new EvallerBean());
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(42, (int)list.get(0));
    }

    @Test
    public void testFieldPrecedenceOverDeclaration() throws Exception {
        String str =
                "import " + Evaller.class.getCanonicalName() + ";\n" +
                "import " + EvallerBean.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R1 when\n" +
                "    evaller : Evaller()\n" +
                "    $s : EvallerBean( evaller.size() == 1 )\n" +
                "then\n" +
                "    list.add( 42 );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list );

        ksession.insert(new Evaller());
        ksession.insert(new EvallerBean());
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(42, (int) list.get(0));
    }

    @Test
    public void testContainsOnString() {
        // DROOLS-388
        String str =
                "global java.util.List list;\n" +
                "rule R1 when\n" +
                "    $s : String( this contains 'bcd' )\n" +
                "then\n" +
                "    list.add( $s );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list );

        ksession.insert("abcde");
        ksession.insert("bcdef");
        ksession.insert("cdefg");
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.containsAll(Arrays.asList("abcde", "bcdef")));
    }


    @Test
    public void testFunctionJitting() {
        // DROOLS-404
        String str =
                "global java.util.List list;\n" +
                "declare Pippo end;\n" +
                "function boolean alwaysTrue() { return true; }" +
                "rule R1 when\n" +
                "    $s : String( alwaysTrue() )\n" +
                "then\n" +
                "    list.add( $s );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list );

        ksession.insert("abcde");
        ksession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test
    public void testGlobalExtractor() {
        String str = "package org.test; " +
                     "import java.util.*; " +

                     "global java.util.List list; " +
                     "global java.util.Date tat; " +

                     "declare Foo @role(event) @timestamp( ts ) " +
                     "  tis : Date = new Date( 1000 ) " +
                     "  ts  : Date = new Date( 0 ) " +
                     "end " +

                     "rule Init when then insert( new Foo() ); end " +

                     "rule R1 when\n" +
                     "    $s : Foo( tis before tat )\n" +
                     "then\n" +
                     "    list.add( $s );\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list );
        ksession.setGlobal("tat", new Date( 2000 ) );

        ksession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test
    public void testSharedNotWithLeftUpdateAndRightInsert() throws Exception {
        // BZ-1070092
        String str =
                "import " + Foo.class.getCanonicalName() + ";\n" +
                "import " + Foo2.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    f1 : Foo( )\n" +
                "    Foo2( )\n" +
                "    Person( age == f1.x )\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "    Foo(  )\n" +
                "    f2 : Foo2( )\n" +
                "    not Person()\n" +
                "then\n" +
                "    modify( f2 ) { x = 3 };\n" +
                "    insert( new Person() );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Foo f1 = new Foo();
        Foo2 f2 = new Foo2();
        ksession.insert( f1 );
        ksession.insert( f2 );
        assertEquals(2, ksession.fireAllRules() );
        assertEquals(3, f2.getX());
    }

    @Test
    public void testNestedNots1() {
        // DROOLS-444
        String str = "package org.test; " +

                     "rule negation_over_nested " +
                     "when " +
                     " not ( (String() and Integer()) " +
                     " or " +
                     " (String() and Integer())) " +
                     "then " +
                     " System.out.println(\"negation_over_nested\"); " +
                     "end " +
                     " " +

                     "rule negation_distributed_partially_no_sharing " +
                     "when " +
                     " (not (String() and Long())) " +
                     " and " +
                     " (not (String() and Long())) " +
                     "then " +
                     " System.out.println(\"negation_distributed_partially_no_sharing\"); " +
                     "end " +
                     " " +

                     "rule negation_distributed_partially_sharing " +
                     "when " +
                     " (not (String() and Integer())) " +
                     " and " +
                     " (not (String() and Integer())) " +
                     "then " +
                     " System.out.println(\"negation_distributed_partially_sharing\"); " +
                     "end " +
                     " " +

                     "rule negation_distributed_fully " +
                     "when " +
                     " ((not String()) or (not Integer())) " +
                     " and " +
                     " ((not String()) or (not Integer())) " +
                     "then " +
                     " System.out.println(\"negation_distributed_fully\"); " +
                     "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();
        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        int n = ksession.fireAllRules();
        assertEquals(7, n);
    }

    @Test
    public void testNestedNots2() {
        // DROOLS-444
        String str = "package org.test; " +

                     "rule shared_conjunct " +
                     "when " +
                     " (not (String() and Integer())) " +
                     "then " +
                     " System.out.println(\"shared_conjunct\"); " +
                     "end " +

                     "rule negation_over_nested " +
                     "when " +
                     " not ( (String() and Integer()) " +
                     " or " +
                     " (String() and Integer())) " +
                     "then " +
                     " System.out.println(\"negation_over_nested\"); " +
                     "end " +
                     " " +

                     "rule negation_distributed_partially_no_sharing " +
                     "when " +
                     " (not (String() and Long())) " +
                     " and " +
                     " (not (String() and Long())) " +
                     "then " +
                     " System.out.println(\"negation_distributed_partially_no_sharing\"); " +
                     "end " +
                     " " +

                     "rule negation_distributed_partially_sharing " +
                     "when " +
                     " (not (String() and Integer())) " +
                     " and " +
                     " (not (String() and Integer())) " +
                     "then " +
                     " System.out.println(\"negation_distributed_partially_sharing\"); " +
                     "end " +
                     " " +

                     "rule negation_distributed_fully " +
                     "when " +
                     " ((not String()) or (not Integer())) " +
                     " and " +
                     " ((not String()) or (not Integer())) " +
                     "then " +
                     " System.out.println(\"negation_distributed_fully\"); " +
                     "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();
        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        int n = ksession.fireAllRules();
        assertEquals(8, n);
    }

    @Test
    public void testNestedNots3() {
        // DROOLS-444
        String str = "package org.test; " +

                     "rule negation_distributed_partially_no_sharing " +
                     "when " +
                     " (not String()) " +
                     " and " +
                     " (not (Double() and Integer())) " +
                     "then " +
                     " System.out.println(\"firing\"); " +
                     "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();
        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        int n = ksession.fireAllRules();
        assertEquals(1, n);
    }

    @Test
    public void testExtendingDate() {
        // BZ-1072629
        String str = "import " + MyDate.class.getCanonicalName() + " \n"
                     + "rule 'sample rule' \n"
                     + "when \n" + "  $date: MyDate() \n"
                     + "then \n" + "$date.setDescription(\"test\"); \n" + "end \n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(0, results.getMessages().size());
    }

    public static class MyDate extends Date {
        private String description;
        public String getDescription() {
            return this.description;
        }
        public void setDescription(final String desc) {
            this.description = desc;
        }
    }

    @Test
    public void testEvalInSubnetwork() {
        // DROOLS-460
        String str = "global java.util.List list;\n" +
                     "\n" +
                     "declare StatusEvent\n" +
                     "@role(event)\n" +
                     "timestamp : int\n" +
                     "end\n" +
                     "\n" +
                     "rule R when\n" +
                     "$i : Integer()\n" +
                     "eval(true)\n" +
                     "exists(\n" +
                     "Integer(intValue > $i.intValue)\n" +
                     "and eval(true)\n" +
                     ")\n" +
                     "then\n" +
                     "list.add($i.intValue());\n" +
                     "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();
        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(0);
        ksession.fireAllRules();
        ksession.insert(1);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(0, (int)list.get(0));
    }

    @Test
    public void testRedeclaringRuleAttribute() {
        // BZ-1092084
        String str = "rule R salience 10 salience 100 when then end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(1, results.getMessages().size());
    }

    @Test
    public void testMultilineStatement() {
        // BZ-1092502
        String str = "rule \"test\"\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "then\n" +
                     "System  \n" +
                     "  .out  \n" +
                     "  .println(\"hello\");\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(0, results.getMessages().size());
    }

    @Test
    public void testExtendsWithStrictModeOff() {
        // DROOLS-475
        String str =
                "import java.util.HashMap;\n" +
                "dialect \"mvel\"\n" +
                "declare HashMap end\n" +
                "\n" +
                "declare Test extends HashMap end\n" +
                "\n" +
                "rule \"Insert\" salience 0\n" +
                "when\n" +
                "then\n" +
                "Test t = new Test();\n" +
                "t.Price = 10;\n" +
                "t.put(\"A\", \"a\");\n" +
                "t.OtherPrices = new HashMap();\n" +
                "t.OtherPrices.OldPrice = 8;\n" +
                "System.out.println(\"Inserting t=\"+t);\n" +
                "insert(t);\n" +
                "end\n" +
                "\n" +
                "rule \"Test HashMap\" salience -1\n" +
                "when\n" +
                "t: HashMap( Price < 11 )\n" +
                "then\n" +
                "t.Price = 11;\n" +
                "System.out.println(\"In Test HashMap\");\n" +
                "end\n" +
                "\n" +
                "rule \"Test Inherited\" salience -1\n" +
                "when\n" +
                "t: Test( Price < 100 )\n" +
                "then\n" +
                "t.Price = 12;\n" +
                "System.out.println(\"In Test Inherited!\");\n" +
                "end\n" +
                "\n" +
                "rule \"Print Result\" salience -5\n" +
                "when\n" +
                "t: Test()\n" +
                "then\n" +
                "System.out.println(\"Finally Price is =\"+t.Price);\n" +
                "//This as well doesn't print content as per toString() of HashMap is there a way to do that?\n" +
                "System.out.println(\"Finally t=\"+t);\n" +
                "end\n";

        KnowledgeBuilderConfigurationImpl pkgBuilderCfg = new KnowledgeBuilderConfigurationImpl();
        MVELDialectConfiguration mvelConf = (MVELDialectConfiguration) pkgBuilderCfg.getDialectConfiguration( "mvel" );
        mvelConf.setStrict( false );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pkgBuilderCfg);
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertFalse( kbuilder.hasErrors() );
    }

    @Test
    public void testCompilationFailureWithDuplicatedAnnotation() {
        // BZ-1099896
        String str = "declare EventA\n" +
                     " @role(fact)\n" +
                     " @role(event)\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(1, results.getMessages().size());
    }

    @Test
    public void testCrossNoLoopWithNodeSharing() throws Exception {
        // DROOLS-501 Propgation context is not set correctly when nodes are shared
        // This test was looping in 6.1.0-Beta4
        String drl = "package org.drools.compiler.loop " +

                     "rule 'Rule 1' " +
                     "  agenda-group 'Start' " +
                     "  no-loop " +
                     "  when " +
                     "      $thing1 : String() " +
                     "      $thing2 : Integer() " +
                     "  then\n" +
                     "      System.out.println( 'At 1' ); " +
                     "      update( $thing2 ); " +
                     "end " +

                     "rule 'Rule 2' " +
                     "  agenda-group 'End' " +
                     "  no-loop " +
                     "  when " +
                     "      $thing1 : String() " +
                     "      $thing2 : Integer() " +
                     "  then " +
                     "      System.out.println( 'At 2' ); " +
                     "      update( $thing2 ); " +
                     "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession session = helper.build().newKieSession();

        session.insert( "hello" );
        session.insert( new Integer( 42 ) );

        // set the agenda groups in reverse order so that stack is preserved
        session.getAgenda().getAgendaGroup( "End" ).setFocus();
        session.getAgenda().getAgendaGroup( "Start" ).setFocus();

        int x = session.fireAllRules( 10 );
        assertEquals( 2, x );
        session.dispose();
    }

    @Test
    public void testNotNodesUnlinking() throws Exception {
        // BZ-1101471
        String drl = "import " + Trailer.class.getCanonicalName() + ";" +
                     "global java.util.List trailerList;" +
                     "rule R1\n" +
                     "agenda-group 'Start'\n" +
                     "    when\n" +
                     "$trailer : Trailer(status == Trailer.TypeStatus.WAITING);\n" +
                     "not Trailer(status == Trailer.TypeStatus.LOADING); \n" +
                     "not Trailer(status == Trailer.TypeStatus.SHIPPING);\n" +
                     "    then\n" +
                     "        System.out.println( \"[rfgroup1] find waiting trailer : \" + $trailer);\n" +
                     "        $trailer.setStatus(Trailer.TypeStatus.LOADING); \n" +
                     "        update($trailer);\n" +
                     "end\n" +
                     "\n" +
                     "rule R2\n" +
                     "agenda-group 'Start'\n" +
                     "    when\n" +
                     "$trailer : Trailer(status == Trailer.TypeStatus.LOADING);\n" +
                     "    then\n" +
                     "        System.out.println( \"[rfgroup1] ship : \" + $trailer);\n" +
                     "        $trailer.setStatus(Trailer.TypeStatus.SHIPPING);\n" +
                     "        update($trailer);\n" +
                     "end\n" +
                     "\n" +
                     "rule R3\n" +
                     "agenda-group 'Start'\n" +
                     "    when\n" +
                     "$trailer : Trailer(status == Trailer.TypeStatus.SHIPPING);\n" +
                     "    then\n" +
                     "        System.out.println( \"[rfgroup1] shipping done : \" + $trailer);\n" +
                     "        trailerList.add($trailer);\n" +
                     "        retract($trailer);\n" +
                     "end\n" +
                     "\n" +
                     "rule R4\n" +
                     "no-loop\n" +
                     "agenda-group 'End'\n" +
                     "    when\n" +
                     "    then\n" +
                     "        System.out.println( \"[rfgroup2] insert new trailers\");\n" +
                     "        insert(new Trailer(Trailer.TypeStatus.WAITING));\n" +
                     "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ArrayList<Trailer> trailerList = new ArrayList<Trailer>();
        ksession.setGlobal("trailerList", trailerList);

        Trailer trailer1 = new Trailer(Trailer.TypeStatus.WAITING);

        ksession.insert(trailer1);

        // set the agenda groups in reverse order so that stack is preserved
        ksession.getAgenda().getAgendaGroup( "Start" ).setFocus();
        ksession.getAgenda().getAgendaGroup( "End" ).setFocus();
        ksession.getAgenda().getAgendaGroup( "Start" ).setFocus();

        ksession.fireAllRules();

        assertEquals(2, trailerList.size());
    }


    public static class Trailer {
        public enum TypeStatus { WAITING, LOADING, SHIPPING }

        private TypeStatus status;

        public Trailer(TypeStatus status) {
            this.status = status;
        }

        public TypeStatus getStatus() {
            return status;
        }

        public void setStatus(TypeStatus status) {
            this.status = status;
        }
    }

    public static class Host { }

    @Test
    public void testJITIncompatibleTypes() throws Exception {
        // BZ-1101295
        String drl =
                "import " + Host.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    $s: String()" +
                "    Host($s == this)\n" +
                "then\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ksession.insert(new Host());
        ksession.insert("host");
        ksession.fireAllRules();
    }

    public static class TypeA {
        private int id = 1;
        public int getId() { return id; }
    }

    public static class TypeB {
        private int parentId = 1;
        private int id = 2;
        public int getParentId() { return parentId; }
        public int getId() { return id; }
    }

    public static class TypeC {
        private int parentId = 2;
        public int getParentId() { return parentId; }
        public int getValue() { return 1; }
    }

    public static class TypeD {
        private int parentId = 2;
        private int value;
        public int getParentId() { return parentId; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
    }

    @Test
    public void testAccumulateWithNodeSharing() throws Exception {
        // DROOLS-487
        String drl =
                "import " + TypeA.class.getCanonicalName() + ";\n" +
                "import " + TypeB.class.getCanonicalName() + ";\n" +
                "import " + TypeC.class.getCanonicalName() + ";\n" +
                "import " + TypeD.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    $a : TypeA()\n" +
                "    $b : TypeB( parentId == $a.id )\n" +
                "    $d : TypeD( parentId == $b.id, value == 1 )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R2 no-loop when\n" +
                "    $a : TypeA()\n" +
                "    $b : TypeB( parentId == $a.id )\n" +
                "then\n" +
                "    update($b);" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    $a : TypeA()\n" +
                "    $b : TypeB( parentId == $a.id )\n" +
                "    $d : TypeD( parentId == $b.id )\n" +
                "    $result : Number() from accumulate(\n" +
                "        $b_acc : TypeB()\n" +
                "        and\n" +
                "        $c : TypeC( parentId == $b_acc.id, $value : value );\n" +
                "        sum($value)\n" +
                "    )\n" +
                "then\n" +
                "    $d.setValue($result.intValue());\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ksession.insert(new TypeA());
        ksession.insert(new TypeB());
        ksession.insert(new TypeC());
        TypeD d = new TypeD();
        ksession.insert(d);
        ksession.fireAllRules();
        assertEquals(1, d.getValue());
    }

    public static class Reading {
        private final String type;
        private final int value;

        public Reading(String type, int value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public int getValue() {
            return value;
        }
    }

    public static class Alarm {
        private String type;
        private String level;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @Test
    public void testDeletedEvalLeftTuple() throws Exception {
        // BZ-1106300
        String drl =
                "import " + Reading.class.getCanonicalName() + ";\n" +
                "import " + Alarm.class.getCanonicalName() + ";\n" +
                "rule Normal when\n" +
                "    Number( intValue <= 5) from accumulate( reading : Reading(), average( reading.getValue() ) )\n" +
                "    alarm : Alarm ()\n" +
                "then\n" +
                "    System.out.println(kcontext.getRule().getName());" +
                "    delete( alarm );\n" +
                "end\n" +
                "    \n" +
                "rule Abnormal when\n" +
                "    Number( intValue > 5, intValue <= 10 ) from accumulate( reading : Reading(), average( reading.getValue() ) )\n" +
                "    not Alarm ()\n" +
                "then\n" +
                "    System.out.println(kcontext.getRule().getName());" +
                "    Alarm alarm = new Alarm();\n" +
                "    alarm.setType(\"t1\");\n" +
                "    alarm.setLevel( \"ABNORMAL\" );\n" +
                "    insert(alarm);\n" +
                "end\n" +
                "\n" +
                "rule Severe when\n" +
                "    Number( intValue > 10) from accumulate( reading : Reading(), average( reading.getValue() ) )\n" +
                "    not Alarm ()\n" +
                "then\n" +
                "    System.out.println(kcontext.getRule().getName());" +
                "    Alarm alarm = new Alarm();\n" +
                "    alarm.setType(\"t1\");\n" +
                "    alarm.setLevel( \"SEVERE\" );\n" +
                "    insert(alarm);\n" +
                "end\n" +
                "\n" +
                "rule AbnormalToSevere when\n" +
                "    Number( intValue > 10) from accumulate( reading : Reading(), average( reading.getValue() ) )\n" +
                "    alarm : Alarm (level == \"ABNORMAL\")\n" +
                "then\n" +
                "    System.out.println(kcontext.getRule().getName());" +
                "    alarm.setLevel( \"SEVERE\" );\n" +
                "    update(alarm);\n" +
                "end\n" +
                "\n" +
                "rule SevereToAbnormal when\n" +
                "    $type : String()\n" +
                "    accumulate( reading : Reading( type == $type ), $avg : average( reading.getValue() ) )\n" +
                "    eval( $avg.intValue() > 5 && $avg.intValue() <= 10 )\n" +
                "    alarm : Alarm (type == $type, level == \"SEVERE\")\n" +
                "then\n" +
                "    System.out.println(kcontext.getRule().getName());" +
                "    alarm.setLevel( \"ABNORMAL\" );\n" +
                "    update(alarm);\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ksession.insert("t1");

        ksession.insert(new Reading("t1", 12));
        ksession.fireAllRules();
        ksession.insert(new Reading("t1", 0));
        ksession.fireAllRules();
        ksession.insert(new Reading("t1", 0));
        ksession.fireAllRules();

        ksession.insert(new Reading("t1", 16));
        ksession.fireAllRules();
        ksession.insert(new Reading("t1", 32));
        ksession.fireAllRules();
        ksession.insert(new Reading("t1", -6));
        ksession.fireAllRules();
    }

    public static class C1 {
        private int counter = 0;
        private final List<C2> c2s = Arrays.asList(new C2(), new C2());

        public List<C2> getC2s() { return c2s; }

        public int getSize() { return getC2s().size(); }

        public int getCounter() { return counter; }
        public void setCounter(int counter) { this.counter = counter; }
    }

    public static class C2 {
        private final List<C3> c3s = Arrays.asList(new C3(1), new C3(2));
        public List<C3> getC3s() { return c3s; }
    }

    public static class C3 {
        public final int value;
        public C3(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    @Test
    public void testDeleteFromLeftTuple() throws Exception {
        // DROOLS-518
        String drl =
                "import " + C1.class.getCanonicalName() + ";\n" +
                "import " + C2.class.getCanonicalName() + ";\n" +
                "import " + C3.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    $c1 : C1 ( $c2s : c2s, $c2 : c2s.get(counter), counter < size )\n" +
                "    C2 ( $c3s : c3s, this == $c2 ) from $c2s\n" +
                "    accumulate( C3 ( $value : value ) from $c3s;\n" +
                "                $sum : sum($value)\n" +
                "              )\n" +
                "then\n" +
                "    $c1.setCounter($c1.getCounter() + 1);\n" +
                "    update( $c1 );\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ksession.insert(new C1());
        ksession.fireAllRules();
    }

    public interface I0 {
        String getValue();
    }

    public interface I1 extends I0 { }

    public static class X implements I0 {
        @Override
        public String getValue() {
            return "x";
        }
    }

    public static class Y extends X implements I1 { }

    public static class Z implements I1 {
        @Override
        public String getValue() {
            return "x";
        }
    }

    @Test
    public void testMethodResolution() throws Exception {
        // DROOLS-509
        String drl =
                "import " + I1.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    I1 ( value == \"x\" )\n" +
                "then\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ksession.insert(new Y());
        ksession.fireAllRules();
        ksession.insert(new Z());
        ksession.fireAllRules();
    }

    @Test
    public void testCorrectErrorMessageOnDeclaredTypeCompilation() throws Exception {
        // DROOLS-543
        String str = "rule R\n" +
                     "salience 10\n" +
                     "when\n" +
                     " String()\n" +
                     "then\n" +
                     "System.out.println(\"Hi\");\n" +
                     "end\n" +
                     "declare A\n" +
                     " a : SomeNonexistenClass @key\n" +
                     " b : int @key\n" +
                     "end\n" +
                     "\n" +
                     "declare C\n" +
                     " d : int @key\n" +
                     " e : int[]\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(1, results.getMessages().size());
    }

    @Test
    public void testFieldNameStartingWithUnderscore() throws Exception {
        // DROOLS-554
        String str = "import " + Underscore.class.getCanonicalName() + ";\n" +
                     "rule R when\n" +
                     "    Underscore( _id == \"test\" )\n" +
                     "then\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(0, results.getMessages().size());
    }

    public static class Underscore {
        private String _id;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }

    @Test
    public void testSharedQueryNode() throws Exception {
        // DROOLS-561
        String drl =
                "query find( Integer $i, String $s )\n" +
                "    $i := Integer( toString() == $s )\n" +
                "end\n" +
                "\n" +
                "rule R2 salience -1 when\n" +
                "    $s : String()\n" +
                "    ?find( i, $s; )\n" +
                "then\n" +
                "end\n" +
                "rule R1 when\n" +
                "    $s : String()\n" +
                "    ?find( i, $s; )\n" +
                "    $i : Integer( this == 1 ) from i\n" +
                "then\n" +
                "    delete( $s );\n" +
                "end\n";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ksession.insert("1");
        ksession.insert(1);
        ksession.fireAllRules();
    }

    @Test
    public void testLeftTupleGetIndex() throws Exception {
        // DROOLS-570
        String drl =
                "rule R1 when\n" +
                "    $s : String()\n" +
                "    (or Long(this == 1) Long(this == 2) )\n" +
                "then\n" +
                "end\n" +
                "rule R2 extends R1 when\n" +
                "    $n : Number() from accumulate( Integer($value : this); sum($value) )\n" +
                "then\n" +
                "    System.out.println($n);\n" +
                "end\n";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ksession.insert("1");
        ksession.insert(1L);
        ksession.insert(1);
        ksession.fireAllRules();
    }

    @Test
    public void testStrOperatorInAccumulate() throws Exception {
        // DROOLS-574
        String drl =
                "declare Message\n" +
                "    text : String\n" +
                "end\n" +
                "\n" +
                "declare GroupByString\n" +
                "    groupId : String\n" +
                "    groups : String[]\n" +
                "end\n" +
                "\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule \"init words of my interest\"\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    GroupByString grp = new GroupByString();\n" +
                "    grp.setGroupId(\"wordGroup\");\n" +
                "    grp.setGroups(new String[]{\"hi\", \"hello\"});\n" +
                "    insert(grp);\n" +
                "    insert(new Message(\"hi all\"));\n" +
                "    insert(new Message(\"hello world\"));\n" +
                "    insert(new Message(\"bye\"));\n" +
                "    insert(new Message(\"hello everybody\"));\n" +
                "end\n" +
                "\n" +
                "rule \"group by word and count if >=2 then \"\n" +
                "no-loop\n" +
                "when\n" +
                "    $group : GroupByString( groupId == \"wordGroup\")\n" +
                "    $word : String() from $group.groups\n" +
                "    acc ( $msg : Message( text str[startsWith] $word );\n" +
                "        $list : collectList( $msg ),\n" +
                "        $count : count( $msg );\n" +
                "        $count >= 1\n" +
                "        )\n" +
                "then\n" +
                "    list.add(\"group by \" + $word + \" count is \"+ $count);\n" +
                "end\n";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("group by hello count is 2"));
        assertTrue(list.contains("group by hi count is 1"));
    }

    @Test @Ignore
    public void testKeywordAsAttribute() throws Exception {
        // DROOLS-577
        String drl =
                "package foo.bar;\n" +
                        "declare Fired\n" +
                        "        rule: String\n" +
                        "end\n" +
                        "global java.util.List list\n" +
                        "rule \"F060\" dialect \"mvel\"\n" +
                        "when\n" +
                        "then\n" +
                        "    Fired f = new Fired();\n" +
                        "    f.rule = \"F060\";\n" +
                        "    insert(f);\n" +
                        "end\n" +
                        " \n" +
                        "rule \"F060b\"  //this prints F060b: Fired( rule=F060 )\n" +
                        "    dialect \"mvel\"\n" +
                        "when\n" +
                        "        $rule: Fired()\n" +
                        "then\n" +
                        "    list.add( drools.getRule().getName() )\n" +
                        "end\n" +
                        " \n" +
                        "rule \"F060c\"  //doesn't work\n" +
                        "    dialect \"mvel\"\n" +
                        "when\n" +
                        "        $rule: Fired( rule==\"F060c\" )\n" +
                        "then\n" +
                        "    list.add( drools.getRule().getName() )\n" +
                        "end\n" +
                        " \n" +
                        "rule \"F060d\"  //this prints F060d: Fired( rule=F060 )\n" +
                        "    dialect \"mvel\"\n" +
                        "when\n" +
                        "        $rule: Fired()\n" +
                        "        eval( $rule.rule == \"F060\")\n" +
                        "then\n" +
                        "    list.add( drools.getRule().getName() )\n" +
                        "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        System.out.println(list);
        assertEquals(3, list.size());
        assertTrue(list.contains("F060b"));
        assertTrue(list.contains("F060c"));
        assertTrue(list.contains("F060d"));
    }

    @Test
    public void testRuleExtendsWithNamedConsequence() {
        // DROOLS-581
        String drl =
                "package org.drools.test;\n" +
                "global java.util.List list;\n" +
                "rule Base\n" +
                "when\n" +
                "  $i : Integer( ) do[x]\n" +
                "then\n" +
                "then[x]\n" +
                "   list.add( $i );\n" +
                "end\n" +
                "rule Ext extends Base\n" +
                "when\n" +
                "  $d : String()\n" +
                "then\n" +
                "   list.add( $d );\n" +
                "end\n";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession kieSession = helper.build().newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        kieSession.setGlobal( "list", list );

        kieSession.insert( 10 );
        kieSession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( 10, (int) list.get(0) );
        assertEquals( 10, (int) list.get(1) );
    }

    @Test
    public void testRuleExtendsWithOverriddenNamedConsequence() {
        // DROOLS-581
        String drl =
                "package org.drools.test;\n" +
                "global java.util.List list;\n" +
                "rule Base\n" +
                "when\n" +
                "  $i : Integer( ) do[x]\n" +
                "then\n" +
                "then[x] " +
                "   list.add( $i );\n" +
                "end\n" +
                "rule Ext extends Base\n" +
                "when\n" +
                "  $d : String()\n" +
                "then\n" +
                "   list.add( $d );\n" +
                "then[x]\n" +
                "   list.add( \"\" + $i );\n" +
                "end\n";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession kieSession = helper.build().newKieSession();

        List list = new ArrayList();
        kieSession.setGlobal( "list", list );

        kieSession.insert( 10 );
        kieSession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( 10, list.get(0) );
        assertEquals( "10", list.get(1) );
    }

    @Test
    public void testCustomDynamicSalience() {
        String drl  = "package org.drools.test; " +
                      "import " + Person.class.getName() + "; " +
                      "global java.util.List list; " +

                      "rule A " +
                      "when " +
                      "     $person : Person( name == 'a' ) " +
                      "then" +
                      "     list.add( $person.getAge() ); " +
                      "end " +

                      "rule B " +
                      "when " +
                      "     $person : Person( name == 'b' ) " +
                      "then" +
                      "     list.add( $person.getAge() ); " +
                      "end " +
                      "";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession session = helper.build().newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        session.setGlobal( "list", list );

        for ( Rule r : session.getKieBase().getKiePackage( "org.drools.test" ).getRules() ) {
            ((RuleImpl) r).setSalience( new Salience() {
                @Override
                public int getValue( KnowledgeHelper khelper, Rule rule, WorkingMemory workingMemory ) {
                    if ( khelper == null ) { return 0; }
                    InternalFactHandle h = (InternalFactHandle) khelper.getMatch().getFactHandles().get( 0 );
                    return ((Person) h.getObject()).getAge();
                }

                @Override
                public int getValue() { throw new IllegalStateException( "Should not have been called..." ); }

                @Override
                public boolean isDynamic() { return true; }
            } );
        }

        session.insert(new Person( "a", 1 ) );
        session.insert(new Person( "a", 5 ) );
        session.insert(new Person( "a", 3 ) );
        session.insert(new Person( "b", 4 ) );
        session.insert(new Person( "b", 2 ) );
        session.insert(new Person( "b", 6 ) );

        session.fireAllRules();

        assertEquals( Arrays.asList( 6, 5, 4, 3, 2, 1 ), list );
    }

    @Test
    public void testNotWithSubNetwork() {
        String drl  =
                "rule R when\n" +
                "    $s : String( )\n" +
                "    not (\n" +
                "        Long( toString() == $s )\n" +
                "    and\n" +
                "        Integer( toString() == $s )\n" +
                "    )\n" +
                "then\n" +
                "    delete( $s );\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ksession.insert("1");
        FactHandle iFH = ksession.insert(1);
        FactHandle lFH = ksession.insert(1L);
        ksession.fireAllRules();

        ksession.delete(iFH);
        ksession.delete(lFH);
        ksession.fireAllRules();

        assertEquals( 0, ksession.getFactCount() );
    }

    @Test
    public void testGenericsInRHSWithModify() {
        // DROOLS-493
        String drl  =
                "import java.util.Map;\n" +
                "import java.util.HashMap;\n" +
                "rule R no-loop when\n" +
                "    $s : String( )\n" +
                "then\n" +
                "    Map<String,String> a = new HashMap<String,String>();\n" +
                "    modify( $s ) { };" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        ksession.insert("1");
        ksession.fireAllRules();
    }

    @Test
    public void testQueryWithAgendaGroup() {
        // DROOLS-601
        String drl =
                "package org.drools.test; " +
                "global java.util.List list; " +

                "query foo( Integer $i ) " +
                "   $i := Integer() " +
                "end " +

                "rule Detect " +
                "agenda-group 'one' " +
                "when " +
                "   foo( $i ; ) " +
                "then " +
                "   list.add( $i ); " +
                "end " +

                "rule OnceMore " +
                "agenda-group 'two' " +
                "no-loop " +
                "when " +
                "   $i : Integer() " +
                "then " +
                "   update( $i );" +
                "end " +
                "";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession kieSession = helper.build().newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        kieSession.setGlobal( "list", list );

        FactHandle handle = kieSession.insert( 42 );

        Agenda agenda = kieSession.getAgenda();
        agenda.getAgendaGroup("two").setFocus();
        agenda.getAgendaGroup("one").setFocus();

        kieSession.fireAllRules();
        assertEquals( Arrays.asList( 42 ), list );

        kieSession.delete( handle );

        kieSession.insert( 99 );

        agenda.getAgendaGroup("two").setFocus();
        agenda.getAgendaGroup("one").setFocus();

        kieSession.fireAllRules();
        assertEquals( Arrays.asList( 42, 99 ), list );
    }

    @Test
    public void testQueryUsingQueryWithAgendaGroup() {
        // DROOLS-601
        String drl =
                "package org.drools.test; " +
                "global java.util.List list; " +

                "query bar( String $s ) " +
                "   $s := String() " +
                "end " +
                "query foo( Integer $i, String $s ) " +
                "   bar( $s ; ) " +
                "   $i := Integer( toString() == $s ) " +
                "end " +

                "rule Detect " +
                "agenda-group 'one' " +
                "when " +
                "   foo( $i, $s ; ) " +
                "then " +
                "   list.add( $i ); " +
                "end " +

                "rule UpdateInt " +
                "agenda-group 'two' " +
                "no-loop " +
                "when " +
                "   $i : Integer() " +
                "then " +
                "   update( $i );" +
                "end " +

                "rule UpdateString " +
                "agenda-group 'three' " +
                "no-loop " +
                "when " +
                "   $s : String() " +
                "then " +
                "   update( $s );" +
                "end " +
                "";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession kieSession = helper.build().newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        kieSession.setGlobal( "list", list );

        FactHandle iFH = kieSession.insert( 42 );
        FactHandle sFH = kieSession.insert( "42" );

        Agenda agenda = kieSession.getAgenda();
        agenda.getAgendaGroup("three").setFocus();
        agenda.getAgendaGroup("two").setFocus();
        agenda.getAgendaGroup("one").setFocus();

        kieSession.fireAllRules();
        assertEquals( Arrays.asList( 42 ), list );

        //kieSession.delete( iFH );
        kieSession.delete( sFH );

        kieSession.insert( 99 );
        kieSession.insert( "99" );

        agenda.getAgendaGroup("three").setFocus();
        agenda.getAgendaGroup("two").setFocus();
        agenda.getAgendaGroup("one").setFocus();

        kieSession.fireAllRules();
        assertEquals( Arrays.asList( 42, 99 ), list );
    }

    @Test
    public void testFactTemplates() {
        // DROOLS-600
        String drl = "package com.testfacttemplate;" +
                     " rule \"test rule\" " +
                     " dialect \"mvel\" " +
                     " when " +
                     " $test : TestFactTemplate( status == 1 ) " +
                     " then " +
                     " System.out.println( \"Hello World\" ); " +
                     " end ";

        KnowledgePackageImpl kPackage = new KnowledgePackageImpl("com.testfacttemplate");
        FieldTemplate fieldTemplate = new FieldTemplateImpl("status", 0, Integer.class);
        FactTemplate factTemplate = new FactTemplateImpl(kPackage, "TestFactTemplate", new FieldTemplate[]{fieldTemplate});

        KnowledgeBuilder kBuilder = new KnowledgeBuilderImpl(kPackage);
        StringReader rule = new StringReader(drl);
        try {
            ((KnowledgeBuilderImpl) kBuilder).addPackageFromDrl(rule);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBitwiseOperator() {
        // DROOLS-585
        String drl =
                "global java.util.List list;\n" +
                "\n" +
                "rule R when\n" +
                "    $i : Integer( (intValue() & 5) != 0 )\n" +
                "then\n" +
                "    list.add($i);\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent(drl, ResourceType.DRL);
        KieSession kieSession = helper.build().newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        kieSession.setGlobal( "list", list );

        kieSession.insert(3);
        kieSession.insert(2);
        kieSession.insert(6);
        kieSession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList(3, 6)));
    }

    @Test
    public void testNotSubnetwork() {
        // DROOLS-623
        String drl =
                "import " + TypeCC.class.getCanonicalName() + ";\n" +
                "import " + TypeDD.class.getCanonicalName() + ";\n" +
                "  \n" +
                "rule R1 \n" +
                "when  \n" +
                "   $dd : TypeDD( value < 1 )\n" +
                "then  \n" +
                "	System.out.println(\"Rule R1 Fired\");\n" +
                "	modify($dd) { setValue(1); }\n" +
                "end  \n" +
                "  \n" +
                "rule R2 when  \n" +
                "   String( )  \n" +
                "   $cc : TypeCC( value < 1 )\n" +
                "   not(  \n" +
                "	   $cc_not : TypeCC( )  \n" +
                "	   and  \n" +
                "	   $dd_not : TypeDD( value==0 )  \n" +
                "   )  \n" +
                "then  \n" +
                "   System.out.println(\"Rule R2 Fired\");\n" +
                "   modify($cc) { setValue($cc.getValue()+1); }\n" +
                "end; ";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();
        ksession.insert("1");
        ksession.insert("2");

        TypeCC cc = new TypeCC();
        ksession.insert(cc);
        ksession.insert(new TypeDD());

        ksession.fireAllRules();

        System.out.println("Rule R2 is fired count - " +cc.getValue());

        assertEquals("Rule 2 should be fired once as we have firing rule as one of criteria checking rule only fire once",1,cc.getValue());
    }

    public static class ValueContainer {
        private int value;
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
    }

    public static class TypeCC extends ValueContainer { }
    public static class TypeDD extends ValueContainer { }

    @Test
    public void testClassAccumulator() {
        // DROOLS-626
        String drl =
                "global java.util.List list\n" +
                "declare InitClass\n" +
                "  clazz: Class\n" +
                "end\n" +
                "\n" +
                "rule \"init\" when\n" +
                "then\n" +
                "  insert( new InitClass( String.class ) );\n" +
                "  insert( new InitClass( Integer.class ) );\n" +
                "end\n" +
                "\n" +
                "rule \"make init classes\"\n" +
                "when\n" +
                "  accumulate( InitClass( $clazz; ), $classes: collectList( $clazz ) )\n" +
                "then\n" +
                "  list.addAll($classes);\n" +
                "end ";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<Class> list = new ArrayList<Class>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals(2, list.size());
        System.out.println(list);
        assertTrue(list.containsAll(asList(String.class, Integer.class)));
    }

    @Test
    public void testSubnetworkAccumulate() {
        String drl =
                "import " + StringWrapper.class.getCanonicalName() + ";\n" +
                "global StringBuilder sb;" +
                "rule R when\n" +
                "  $s : String()\n" +
                "  Number( $i : intValue ) from accumulate ($sw : StringWrapper( $value : value ) " +
                "                                       and eval( $sw.contains($s) ), " +
                "                                 sum($value) )\n" +
                "then\n" +
                "  sb.append($i);\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        StringBuilder sb = new StringBuilder();
        ksession.setGlobal("sb", sb);

        ksession.insert("test");
        StringWrapper sw = new StringWrapper();
        FactHandle swFH = ksession.insert(sw);
        ksession.fireAllRules();

        sw.setWrapped("test");
        ksession.update(swFH, sw);
        ksession.fireAllRules();

        sw.setWrapped(null);
        ksession.update(swFH, sw);
        ksession.fireAllRules();

        sw.setWrapped("test");
        ksession.update(swFH, sw);
        ksession.fireAllRules();

        sw.setWrapped(null);
        ksession.update(swFH, sw);
        ksession.fireAllRules();

        sw.setWrapped("test");
        ksession.update(swFH, sw);
        ksession.fireAllRules();

        assertEquals("040404", sb.toString());
    }

    public static class StringWrapper {
        private String wrapped;

        public String getWrapped() {
            return wrapped;
        }

        public void setWrapped(String wrapped) {
            this.wrapped = wrapped;
        }

        public boolean contains(String s) {
            return wrapped != null && wrapped.equals(s);
        }

        public int getValue() {
            return wrapped != null ? wrapped.length() : 0;
        }
    }

    @Test
    public void testImportInner() throws Exception {
        // DROOLS-677
        String drl =
                "package org.drools.test; " +
                "import " + Misc2Test.class.getName() + "; " +

                "declare Foo " +
                "   bar : Misc2Test.AA " +
                "end " +
                "" ;

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        assertTrue( helper.verify().getMessages( org.kie.api.builder.Message.Level.ERROR ).isEmpty() );
    }

    @Test
    //DROOLS-678
    public void testAlphaIndexing() throws Exception {
        String drl =
                "    package org.drools.test; " +

                "    declare ObjectB " +
                "       name : String " +
                "       intValue : Integer " +
                "    end " +

                "    rule 'insert object' " +
                "       when " +
                "       then " +
                "           insert( new ObjectB( null, 0 ) ); " +
                "    end " +

                "    rule 'rule 1' " +
                "       when " +
                "           ObjectB( intValue == 1 ) " +
                "       then " +
                "    end " +

                "    rule 'rule 2' " +
                "       when " +
                "           ObjectB( intValue == 2 ) " +
                "       then " +
                "    end " +

                "    rule 'rule 3' " +
                "       when " +
                "           $b : ObjectB( intValue == null ) " +
                "       then\n" +
                "           System.out.println( $b ); " +
                "    end" +
                "\n" ;

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        assertTrue( helper.verify().getMessages( org.kie.api.builder.Message.Level.ERROR ).isEmpty() );
        KieSession ks = helper.build(  ).newKieSession();
        assertEquals( 1, ks.fireAllRules() );
    }

    @Test
    public void testMvelConstraintErrorMessageOnAlpha() throws Exception {
        // DROOLS-687
        String drl =
                " import org.drools.compiler.Person; " +
                " import org.drools.compiler.Address; " +
                " rule 'hello person' " +
                " when " +
                " Person( address.street == 'abbey' ) " +
                " then " +
                " end " +
                "\n" ;
        KieHelper helper = new KieHelper();
        helper.addContent(drl, ResourceType.DRL);
        assertTrue(helper.verify().getMessages( org.kie.api.builder.Message.Level.ERROR).isEmpty());
        KieSession ks = helper.build().newKieSession();
        Person john = new Person("John"); // address is null
        try {
            ks.insert(john);
            ks.fireAllRules();
            fail("Should throw an exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("hello person"));
        }
    }

    @Test
    public void testMvelConstraintErrorMessageOnBeta() throws Exception {
        // DROOLS-687
        String drl =
                " import org.drools.compiler.Person; " +
                " import org.drools.compiler.Address; " +
                " rule 'hello person' " +
                " when " +
                " $s : String( ) " +
                " Person( address.street == $s ) " +
                " then " +
                " end " +
                "\n" ;
        KieHelper helper = new KieHelper();
        helper.addContent(drl, ResourceType.DRL);
        assertTrue(helper.verify().getMessages( org.kie.api.builder.Message.Level.ERROR).isEmpty());
        KieSession ks = helper.build().newKieSession();
        Person john = new Person("John"); // address is null
        try {
            ks.insert("abbey");
            ks.insert(john);
            ks.fireAllRules();
            fail("Should throw an exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("hello person"));
        }
    }

    @Test
    public void testNotExists() {
        // DROOLS-699
        String drl2 =
                "import " + List.class.getCanonicalName() + ";\n"
                + "\n\n"
                + "rule \"NotExists\"\n"
                + "when\n"
                + "$l1: List() \n"
                + "$l2: List() \n"
                + "exists( String() from $l1 ) \n"
                + "not( exists( String() ) )\n"
                + "then end\n";

        KieSession ksession = new KieHelper().addContent(drl2, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        ksession.insert(asList("Mario", "Mark"));
        ksession.insert(asList("Julie", "Leiti"));

        assertEquals(4, ksession.fireAllRules());
    }
}