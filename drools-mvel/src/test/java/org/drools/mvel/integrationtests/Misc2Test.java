/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.ClassObjectFilter;
import org.drools.core.InitialFact;
import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.NodeMemories;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FactTemplateImpl;
import org.drools.core.facttemplates.FieldTemplate;
import org.drools.core.facttemplates.FieldTemplateImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.ReteComparator;
import org.drools.core.reteoo.ReteDumper;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Salience;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.builder.MVELDialectConfiguration;
import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Message;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.PersonHolder;
import org.drools.mvel.integrationtests.facts.FactWithList;
import org.drools.mvel.integrationtests.facts.FactWithString;
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
import org.kie.api.definition.KiePackage;
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
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.event.rule.RuleEventManager;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;

import static org.drools.mvel.compiler.TestUtil.assertDrlHasCompilationError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * Run all the tests with the ReteOO engine implementation
 */
public class Misc2Test extends CommonTestMethodBase {

    private static final Logger logger = LoggerFactory.getLogger( Misc2Test.class );

    @Test
    public void testUpdateWithNonEffectiveActivations() throws Exception {
        // JBRULES-3604
        String str = "package inheritance\n" +
                     "\n" +
                    "import " + Address.class.getCanonicalName() + "\n" +
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

        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( builder.hasErrors() ) {
            throw new RuntimeException( builder.getErrors().toString() );
        }
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages( builder.getKnowledgePackages() );

        KieSession ksession = knowledgeBase.newKieSession();

        Address address = new Address();

        address.setSuburb( "xyz" );
        FactHandle addressHandle = ksession.insert( address );

        int rulesFired = ksession.fireAllRules();

        assertEquals( 0, rulesFired );

        address.setStreet( "123" );


        ksession.update( addressHandle, address );

        rulesFired = ksession.fireAllRules();

        System.out.println( rulesFired );
        assertEquals( 1, rulesFired );

        ksession.dispose();
    }

    @Test
    public void testNPEOnMutableGlobal() throws Exception {
        // BZ-1019473
        String str = "package org.drools.mvel.compiler\n" +
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
        kfs.write( ResourceFactory.newByteArrayResource( str.getBytes() ).setTargetPath( "org/drools/compiler/rules.drl" ) );

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder( kfs );
        kbuilder.buildAll();

        assertEquals( 0, kbuilder.getResults().getMessages().size() );

        ks.newKieContainer( kbuilder.getKieModule().getReleaseId() ).getKieBase();
        KieSession ksession = ks.newKieContainer( kbuilder.getKieModule().getReleaseId() ).newKieSession();
        assertNotNull( ksession );

        List<String> context = new ArrayList<>();
        ksession.setGlobal( "context", context );

        FactHandle b = ksession.insert( new Message( "b" ) );
        ksession.delete( b );
        int fired = ksession.fireAllRules( 1 );

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

        KieBase kbase1 = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieBase kbase2 = SerializationHelper.serializeObject( kbase1, ( (InternalKnowledgeBase) kbase1 ).getRootClassLoader() );
        assertTrue( ReteComparator.areEqual( kbase1, kbase2 ) );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        assertEquals( 2, ksession.fireAllRules() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        assertEquals( 4, ksession.fireAllRules() );
    }

    @Test
    public void testEvalBeforeNot() {
        String str =
                "package org.drools.mvel.compiler.integration; \n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
    }

    @Test
    public void testKnowledgeBaseEventSupportLeak() throws Exception {
        // JBRULES-3666
        KieBase kbase = getKnowledgeBase();
        KieBaseEventListener listener = new DefaultKieBaseEventListener();
        kbase.addEventListener( listener );
        kbase.addEventListener( listener );
        assertEquals( 1, ( (KnowledgeBaseImpl) kbase ).getKieBaseEventListeners().size() );
        kbase.removeEventListener( listener );
        assertEquals( 0, ( (KnowledgeBaseImpl) kbase ).getKieBaseEventListeners().size() );
    }

    @Test
    public void testReuseAgendaAfterException() throws Exception {
        // JBRULES-3677

        String str = "import org.drools.mvel.compiler.Person;\n" +
                     "global java.util.List results;" +
                     "rule R1\n" +
                     "ruleflow-group \"test\"\n" +
                     "when\n" +
                     "   Person( $age : age ) \n" +
                     "then\n" +
                     "   if ($age > 40) throw new RuntimeException(\"Too old\");\n" +
                     "   results.add(\"OK\");" +
                     "end";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        final KieSession ksession = kbase.newKieSession();

        List<String> res = new ArrayList<>();
        ksession.setGlobal( "results", res );

        AgendaEventListener agendaEventListener = new AgendaEventListener() {
            public void matchCreated( org.kie.api.event.rule.MatchCreatedEvent event ) {
            }

            public void matchCancelled( org.kie.api.event.rule.MatchCancelledEvent event ) {
            }

            public void beforeMatchFired( org.kie.api.event.rule.BeforeMatchFiredEvent event ) {
            }

            public void afterMatchFired( org.kie.api.event.rule.AfterMatchFiredEvent event ) {
            }

            public void agendaGroupPopped( org.kie.api.event.rule.AgendaGroupPoppedEvent event ) {
            }

            public void agendaGroupPushed( org.kie.api.event.rule.AgendaGroupPushedEvent event ) {
            }

            public void beforeRuleFlowGroupActivated( org.kie.api.event.rule.RuleFlowGroupActivatedEvent event ) {
            }

            public void afterRuleFlowGroupActivated( org.kie.api.event.rule.RuleFlowGroupActivatedEvent event ) {
                ksession.fireAllRules();
            }

            public void beforeRuleFlowGroupDeactivated( org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event ) {
            }

            public void afterRuleFlowGroupDeactivated( org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event ) {
            }
        };

        ksession.addEventListener( agendaEventListener );

        FactHandle fact1 = ksession.insert( new Person( "Mario", 38 ) );
        ( (InternalAgenda) ksession.getAgenda() ).activateRuleFlowGroup( "test" );
        ksession.fireAllRules();
        assertEquals( 1, res.size() );
        res.clear();

        ksession.delete( fact1 );

        FactHandle fact2 = ksession.insert( new Person( "Mario", 48 ) );
        try {
            ( (InternalAgenda) ksession.getAgenda() ).activateRuleFlowGroup( "test" );
            ksession.fireAllRules();
            fail( "should throw an Exception" );
        } catch (Exception e) {
        }
        ksession.delete( fact2 );

        assertEquals( 0, res.size() );

        // try to reuse the ksession after the Exception
        FactHandle fact3 = ksession.insert( new Person( "Mario", 38 ) );
        ( (InternalAgenda) ksession.getAgenda() ).activateRuleFlowGroup( "test" );
        ksession.fireAllRules();
        assertEquals( 1, res.size() );
        ksession.delete( fact3 );

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

        KieBase kbase = loadKnowledgeBaseFromString( str );
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

        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( builder.hasErrors() ) {
            throw new RuntimeException( builder.getErrors().toString() );
        }
    }

    @Test
    public void testBigDecimalComparison() throws Exception {
        // JBRULES-3715
        String str = "import org.drools.mvel.compiler.Person;\n" +
                     "rule \"Big Decimal Comparison\"\n" +
                     "    dialect \"mvel\"\n" +
                     "when\n" +
                     "    Person( bigDecimal == 0.0B )\n" +
                     "then\n" +
                     "end";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        final KieSession ksession = kbase.newKieSession();

        Person p = new Person( "Mario", 38 );
        p.setBigDecimal( new BigDecimal( "0" ) );
        ksession.insert( p );

        assertEquals( 1, ksession.fireAllRules() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( 5 );
        ksession.insert( 6 );
        ksession.insert( 4 );
        ksession.insert( 1 );
        ksession.insert( 2 );

        ksession.fireAllRules();

        assertEquals( asList( 1, 2, 4, 5, 6 ), list );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( 3 );
        ksession.insert( 7 );
        ksession.insert( 4 );
        ksession.insert( 5 );
        ksession.insert( 2 );
        ksession.insert( 1 );
        ksession.insert( 6 );

        ksession.fireAllRules();

        assertEquals( asList( 7, 6, 5, 4, 3, 2, 1 ), list );
    }

    @Test(timeout = 10000)
    public void testPropertyReactiveOnAlphaNodeFollowedByAccumulate() {
        // DROOLS-16
        String str =
                "package org.kie.pmml.pmml_4_1.test;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        assertEquals( 3, ksession.fireAllRules() );
    }

    @Test
    public void testPropertyReactiveAccumulateModification() {
        // DROOLS-16
        String str =
                "package org.drools.mvel.compiler.test;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ksession.fireAllRules();

        assertEquals( 2.0, ksession.getQueryResults( "getNeuron" ).iterator().next().get( "$value" ) );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        Foo foo1 = new Foo();
        Foo foo2 = new Foo();
        ksession.insert( foo1 );
        ksession.insert( foo2 );
        ksession.fireAllRules();
        assertEquals( 1, foo1.x );
        assertEquals( 1, foo2.x );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
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

        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBaseFromString( str );

        KieSession ksession = kbase.newKieSession();
        FactHandle fh = ksession.insert( new A( 1, 1, 1, 1 ) );

        ksession.fireAllRules();

        kbase.addPackages( loadKnowledgePackagesFromString( str2 ) );

        ksession.fireAllRules();

        // this second insert forces the regeneration of the otnIds
        ksession.insert( new A( 2, 2, 2, 2 ) );

        LeftTuple leftTuple = ( (DefaultFactHandle) fh ).getFirstLeftTuple();
        ObjectTypeNode.Id letTupleOtnId = leftTuple.getInputOtnId();
        leftTuple = leftTuple.getHandleNext();
        while ( leftTuple != null ) {
            assertTrue( letTupleOtnId.before( leftTuple.getInputOtnId() ) );
            letTupleOtnId = leftTuple.getInputOtnId();
            leftTuple = leftTuple.getHandleNext();
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

        public A( int f1, int f2, int f3, int f4 ) {
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
            this.f4 = f4;
        }

        public int getF1() {
            return f1;
        }

        public void setF1( int f1 ) {
            this.f1 = f1;
        }

        public int getF2() {
            return f2;
        }

        public void setF2( int f2 ) {
            this.f2 = f2;
        }

        public int getF3() {
            return f3;
        }

        public void setF3( int f3 ) {
            this.f3 = f3;
        }

        public int getF4() {
            return f4;
        }

        public void setF4( int f4 ) {
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        Map<Parameter, Double> values = new EnumMap<>(Parameter.class);
        values.put( Parameter.PARAM_A, 4.0 );
        DataSample data = new DataSample();
        data.setValues( values );
        ksession.insert( data );

        assertEquals( 2, ksession.fireAllRules() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new DataSample() );

        assertEquals( 3, ksession.fireAllRules() );
    }

    public enum Parameter {PARAM_A, PARAM_B}

    @PropertyReactive
    public static class DataSample {
        private Map<Parameter, Double> values = new EnumMap<>(Parameter.class);

        public Map<Parameter, Double> getValues() {
            return values;
        }

        public void setValues( Map<Parameter, Double> values ) {
            this.values = values;
        }

        @Modifies({"values", "notEmpty"})
        public void addValue( Parameter p, double value ) {
            this.values.put( p, value );
        }

        public boolean isNotEmpty() {
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
    }

    public static abstract class AbstractBase<T> {
        protected T foo;

        public T getFoo() {
            return foo;
        }
    }

    public static class StringConcrete extends AbstractBase<String> {
        public StringConcrete() {
            this.foo = new String();
        }
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
    }

    public static enum Answer {YES, NO}

    public static class AnswerGiver {
        public Answer getAnswer() {
            return Answer.YES;
        }
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
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testDeclaredTypeExtendingInnerClass() {
        // DROOLS-27
        String str =
                "import " + Misc2Test.StaticPerson.class.getCanonicalName() + "\n" +
                "declare StaticPerson end\n" +
                "declare Student extends StaticPerson end\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        assertEquals( 2, ksession.fireAllRules() );
    }

    public static class StaticPerson {
        private String name;

        public String getName() {
            return name;
        }

        public void setName( String name ) {
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
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

        KieBase kbase = loadKnowledgeBaseFromString( str );

        final Model model = new Model();
        model.setCost( new BigDecimal( "2.43" ) );
        model.setPrice( new BigDecimal( "2.43" ) );

        KieSession ksession = kbase.newKieSession();
        ksession.insert( model );

        int fired = ksession.fireAllRules( 2 );
        if ( fired > 1 )
            throw new RuntimeException( "loop" );
    }

    public static class Model {
        private BigDecimal cost;
        private BigDecimal price;

        public BigDecimal getCost() {
            return cost;
        }

        public void setCost( BigDecimal cost ) {
            this.cost = cost;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice( BigDecimal price ) {
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ksession.insert( new IntegerWrapperImpl( 2 ) );
        ksession.insert( new IntegerWrapperImpl( 3 ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    interface IntegerWraper {
        int getInt();
    }

    public static abstract class AbstractIntegerWrapper implements IntegerWraper, Comparable<IntegerWraper> {
    }

    public static class IntegerWrapperImpl extends AbstractIntegerWrapper {

        private final int i;

        public IntegerWrapperImpl( int i ) {
            this.i = i;
        }

        public int compareTo( IntegerWraper o ) {
            return getInt() - o.getInt();
        }

        public int getInt() {
            return i;
        }
    }

    @Test
    public void testJitComparableNoGeneric() {
        // DROOLS-37 BZ-1233976
        String str =
                "import " + ComparableInteger.class.getCanonicalName() + "\n" +
                "\n" +
                "rule \"minCost\"\n" +
                "when\n" +
                "    $a : ComparableInteger()\n" +
                "    ComparableInteger( this < $a )\n" +
                "then\n" +
                "end";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ksession.insert( new ComparableInteger( 2 ) );
        ksession.insert( new ComparableInteger( 3 ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    public static class ComparableInteger implements Comparable {

        private final int i;

        public ComparableInteger( int i ) {
            this.i = i;
        }

        public int compareTo( Object o ) {
            return getInt() - ( (ComparableInteger) o ).getInt();
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ksession.insert( new IntegerWrapperImpl2( 2 ) );
        ksession.insert( new IntegerWrapperImpl2( 3 ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    interface IntegerWraper2 extends Comparable<IntegerWraper2> {
        int getInt();
    }

    public static abstract class AbstractIntegerWrapper2 implements IntegerWraper2 {
    }

    public static class IntegerWrapperImpl2 extends AbstractIntegerWrapper2 {

        private final int i;

        public IntegerWrapperImpl2( int i ) {
            this.i = i;
        }

        public int compareTo( IntegerWraper2 o ) {
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        assertEquals( 2, ksession.fireAllRules() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        assertEquals( 2, ksession.fireAllRules() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        assertEquals( 2, ksession.fireAllRules() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        assertEquals( 3, ksession.fireAllRules() );
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
        assertTrue( kbuilder.hasErrors() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        assertEquals( 2, ksession.fireAllRules() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        list.add( 1 );
        list.add( null );
        list.add( 2 );

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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testStringCoercionComparison() {
        // DROOLS-167
        String str = "import " + Person.class.getName() + ";\n" +
                     "rule R1 when\n" +
                     " $p : Person( name < \"90201304122000000000000017\" )\n" +
                     "then end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<String> l = new ArrayList<>();
        ksession.setGlobal( "l", l );

        ksession.fireAllRules();

        assertEquals( "http://onefineday.123", l.get( 0 ) );
    }

    @Test
    public void testJitCastOfPrimitiveType() {
        // DROOLS-79
        String str =
                "rule R when\n" +
                " Number(longValue < (Long)7)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Long( 6 ) );
        assertEquals( 1, ksession.fireAllRules() );
    }


    @Test
    public void testSelfChangingRuleSet() {
        // DROOLS-92
        String str =
                "package org.drools.mvel.integrationtests;\n" +
                "" +
                "import org.drools.mvel.integrationtests.Misc2Test.Foo2; \n" +
                "" +
                "global java.util.List list; \n" +
                "\n" +
                "rule \"Prep\" \n" +
                "when \n" +
                "  $packs : java.util.Collection() \n" +
                "then \n" +
                "   ((org.drools.core.impl.InternalKnowledgeBase)drools.getKieRuntime().getKieBase()).addPackages( $packs );" +
                "end \n" +
                "" +
                "rule \"Self-change\"\n" +
                "when\n" +
                "  String( this == \"go\" )\n" +
                "then\n" +
                "   ((org.drools.core.impl.InternalKnowledgeBase)drools.getKieRuntime().getKieBase()).removeRule( \"org.drools.mvel.integrationtests\", \"React\" ); \n" +
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
                "package org.drools.mvel.integrationtests;\n" +
                "" +
                "import org.drools.mvel.integrationtests.Misc2Test.Foo2; \n" +
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

        System.out.println( knowledgeBuilder.getErrors() );

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

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

        public void setDuration( long duration ) {
            this.duration = duration;
        }
    }

    @Test
    public void testDurationAnnotation() {
        // DROOLS-94
        String str =
                "package org.drools.mvel.integrationtests;\n" +
                "import org.drools.mvel.integrationtests.Misc2Test.SimpleEvent\n" +
                "declare SimpleEvent\n" +
                "    @role(event)\n" +
                "    @duration(duration)\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
    }

    @Test
    public void testDurationAnnotationOnKie() {
        // DROOLS-94
        String str =
                "package org.drools.mvel.integrationtests;\n" +
                "import org.drools.mvel.integrationtests.Misc2Test.SimpleEvent\n" +
                "declare SimpleEvent\n" +
                "    @role(event)\n" +
                "    @duration(duration)\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();
    }

    @Test
    public void testDurationAnnotationWithError() {
        // DROOLS-94
        String str =
                "package org.drools.mvel.integrationtests;\n" +
                "import org.drools.mvel.integrationtests.Misc2Test.SimpleEvent\n" +
                "declare SimpleEvent\n" +
                "    @role(event)\n" +
                "    @duration(duratio)\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testPhreakWithConcurrentUpdates() {
        // DROOLS-7
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "  $s : String()\n" +
                "  $i : Integer()\n" +
                "  not Person( age == $i, name.startsWith($s) )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( 30 );
        ksession.insert( 31 );
        ksession.insert( "B" );
        ksession.insert( "D" );

        Person pA = new Person( "AAA", 30 );
        Person pB = new Person( "BBB", 30 );
        Person pC = new Person( "CCC", 31 );
        Person pD = new Person( "DDD", 31 );

        FactHandle fhB = ksession.insert( pB );
        FactHandle fhD = ksession.insert( pD );
        FactHandle fhA = ksession.insert( pA );
        FactHandle fhC = ksession.insert( pC );

        ksession.fireAllRules();

        pB.setAge( 31 );
        pB.setName( "DBB" );
        ksession.update( fhB, pB );

        pD.setAge( 30 );
        pD.setName( "BDD" );
        ksession.update( fhD, pD );

        assertEquals( 0, ksession.fireAllRules() );

        pB.setAge( 30 );
        pB.setName( "BBB" );
        ksession.update( fhB, pB );

        pD.setAge( 31 );
        pD.setName( "DDD" );
        ksession.update( fhD, pD );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testPhreakWith2Nots() {
        // DROOLS-7
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  Person( $age : age, $name : name )\n" +
                "  not Person( name == $name, age == $age + 1 )\n" +
                "  not Person( name == $name, age == $age - 1 )\n" +
                "then\n" +
                "  list.add($age);\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person p1 = new Person( "AAA", 31 );
        Person p2 = new Person( "AAA", 34 );
        Person p3 = new Person( "AAA", 33 );

        FactHandle fh1 = ksession.insert( p1 );
        FactHandle fh3 = ksession.insert( p3 );
        FactHandle fh2 = ksession.insert( p2 );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( 31, (int) list.get( 0 ) );

        list.clear();

        p1.setAge( 35 );
        ksession.update( fh1, p1 );
        p3.setAge( 31 );
        ksession.update( fh3, p3 );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( 31, (int) list.get( 0 ) );
    }

    @Test
    public void testPhreakTMS() {
        // DROOLS-7
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "  Person( $age : age, $name : name == \"A\" )\n" +
                "  not Person( age == $age + 1 )\n" +
                "then\n" +
                "  insertLogical(new Cheese(\"gorgonzola\", 10));\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        Person p1 = new Person( "A", 31 );
        FactHandle fh1 = ksession.insert( p1 );

        ksession.fireAllRules();

        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( Cheese.class ) ).size() );

        Person p2 = new Person( "A", 32 );
        FactHandle fh2 = ksession.insert( p2 );

        ksession.fireAllRules();

        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( Cheese.class ) ).size() );
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

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );

        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0, builder.getResults().getMessages().size() );
        ks.getRepository().addKieModule( builder.getKieModule() );

        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();

        FactType messageType = ksession.getKieBase().getFactType( "org.drools.test", "Message" );
        Object message = messageType.newInstance();
        messageType.set( message, "message", "Hello World" );

        ksession.insert( message );
        assertEquals( 1, ksession.fireAllRules() );

        KieSession ksession2 = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();

        FactType messageType2 = ksession2.getKieBase().getFactType( "org.drools.test", "Message" );
        Object message2 = messageType2.newInstance();
        messageType2.set( message2, "message", "Hello World" );

        ksession2.insert( message2 );
        assertEquals( 1, ksession2.fireAllRules() );
    }

    public static class Lecture {
        private final String id;
        private int day;
        private int index;
        private boolean available;

        public Lecture( String id, int day, int index ) {
            this( id, day, index, true );
        }

        public Lecture( String id, int day, int index, boolean available ) {
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

        public Lecture setDay( int day ) {
            this.day = day;
            return this;
        }

        public int getIndex() {
            return index;
        }

        public Lecture setIndex( int index ) {
            this.index = index;
            return this;
        }

        public boolean isAvailable() {
            return available;
        }

        public Lecture setAvailable( boolean available ) {
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
                "import org.drools.mvel.integrationtests.Misc2Test.Lecture\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        Lecture lA = new Lecture( "A", 0, 4 );
        Lecture lB = new Lecture( "B", 2, 2 );
        Lecture lC = new Lecture( "C", 2, 1 );

        FactHandle fhA = ksession.insert( lA );
        FactHandle fhB = ksession.insert( lB );
        FactHandle fhC = ksession.insert( lC );

        ksession.fireAllRules(); // C gets blocked by B

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList( "A", "B" ) ) );
        list.clear();

        ksession.update( fhB, lB.setDay( 0 ).setIndex( 4 ) );
        ksession.update( fhC, lC.setDay( 0 ).setIndex( 3 ) );
        ksession.fireAllRules(); // B is still a valid blocker for C

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "B" ) );
        list.clear();

        ksession.update( fhB, lB.setDay( 2 ).setIndex( 2 ) );
        ksession.fireAllRules(); // C doesn't find A as blocker

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "B" ) );
    }

    @Test
    public void testPhreakAccumulate() {
        // DROOLS-7
        String str =
                "import org.drools.mvel.integrationtests.Misc2Test.Lecture\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        Lecture lA = new Lecture( "A", 0, 4, true );
        Lecture lB = new Lecture( "B", 2, 2, true );
        Lecture lC = new Lecture( "C", 2, 1, true );

        FactHandle fhA = ksession.insert( lA );
        FactHandle fhB = ksession.insert( lB );
        FactHandle fhC = ksession.insert( lC );

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList( "A", "B" ) ) );
        list.clear();

        ksession.update( fhB, lB.setAvailable( false ) );
        ksession.fireAllRules();

        ksession.update( fhB, lB.setDay( 0 ).setIndex( 3 ) );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList( "B", "C" ) ) );
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
                "    delete( \"x\" );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL).build().newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ( (RuleEventManager) ksession ).addEventListener( new RuleEventListener() {
            @Override
            public void onDeleteMatch( Match match ) {
                list.add("test");
            }
        } );

        ksession.insert( "x" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        assertEquals( 2, ksession.fireAllRules() );
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
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue( kbuilder.hasResults( ResultSeverity.INFO, ResultSeverity.WARNING, ResultSeverity.ERROR ) );
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
        assertEquals( 0, ks.newKieBuilder( kfs ).buildAll().getResults().getMessages().size() );

        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();
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
    public void testUnaryNegation() {
        // DROOLS-177
        String str =
                "rule R when\n" +
                "    Integer( $a: intValue )\n" +
                "    Integer( intValue > $a, intValue == -$a )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( 3 );
        ksession.insert( -3 );

        assertEquals( 1, ksession.fireAllRules() );
    }

    public static class Conversation {
        private final int id;
        private String family;
        private int timeslot;

        public Conversation( int id ) {
            this.id = id;
        }

        public Conversation( int id, String family, int timeslot ) {
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

        public void setFamily( String family ) {
            this.family = family;
        }

        public int getTimeslot() {
            return timeslot;
        }

        public void setTimeslot( int timeslot ) {
            this.timeslot = timeslot;
        }

        public String toString() {
            return "Conversation #" + getId() + " with " + getFamily() + " @ " + getTimeslot();
        }
    }

    @Test
    public void testNotNodeUpdateBlocker() {
        String str =
                "import org.drools.mvel.integrationtests.Misc2Test.Conversation;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Conversation> conversations = new ArrayList<>();
        ksession.setGlobal( "list", conversations );

        Conversation c0 = new Conversation( 0, "Fusco", 2 );
        Conversation c1 = new Conversation( 1, "Fusco", 3 );
        Conversation c2 = new Conversation( 2, "Fusco", 4 );

        FactHandle fh0 = ksession.insert( c0 );
        FactHandle fh1 = ksession.insert( c1 );
        FactHandle fh2 = ksession.insert( c2 );

        ksession.fireAllRules();
        assertEquals( 1, conversations.size() );
        conversations.clear();

        c2.setTimeslot( 0 );
        ksession.update( fh2, c2 );
        ksession.fireAllRules();
        c2.setTimeslot( 4 );
        ksession.update( fh2, c2 );
        ksession.fireAllRules();
        conversations.clear();

        c0.setTimeslot( 3 );
        ksession.update( fh0, c0 );
        ksession.fireAllRules();
        c0.setTimeslot( 2 );
        ksession.update( fh0, c0 );
        ksession.fireAllRules();
        conversations.clear();

        c2.setTimeslot( 1 );
        ksession.update( fh2, c2 );
        ksession.fireAllRules();
        assertEquals( 1, conversations.size() );
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
        List<String> firedRules = new ArrayList<>();
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal( "fired", firedRules );
        ksession.insert( new Foo() );
        ksession.insert( new Foo2( 1 ) );
        ksession.fireAllRules();

        assertEquals( 1, firedRules.size() );
    }

    @Test
    public void testNamedConsequenceWithNot() {
        List<String> firedRules = new ArrayList<>();
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal( "fired", firedRules );
        ksession.insert( new Foo() );
        ksession.insert( new Foo2( 1 ) );
        ksession.fireAllRules();

        assertEquals( 1, firedRules.size() );
    }

    public static class Foo {
        public int x;

        public int getX() {
            return x;
        }

        public void setX( int x ) {
            this.x = x;
        }
    }

    public static class Foo2 {
        @Position(0)
        public int x;

        public Foo2() {
        }

        public Foo2( int x ) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public void setX( int x ) {
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
        KieBase kbase = loadKnowledgeBaseFromString( kBaseConf, str );
        KieSession ksession = kbase.newKieSession();

        ksession.setGlobal( "context", new ArrayList() {{
            add( new Long( 0 ) );
        }} );

        Foo foo = new Foo();
        foo.setX( 1 );
        ksession.insert( foo );
        ksession.fireAllRules();

        assertEquals( 2, foo.getX() );
    }

    @Test
    public void testAutomaticallySwitchFromReteOOToPhreak() {
        String str = "rule R when then end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/rule.drl", str );

        KieBuilder kbuilder = ks.newKieBuilder( kfs );

        kbuilder.buildAll();
        assertEquals( 0, kbuilder.getResults().getMessages().size() );

        KieBase kbase = ks.newKieContainer( kbuilder.getKieModule().getReleaseId() ).getKieBase();
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        StatelessKieSession ksession = kbase.newStatelessKieSession();

        final List<String> firings = new ArrayList<>();

        AgendaEventListener agendaEventListener = new AgendaEventListener() {
            public void matchCreated( org.kie.api.event.rule.MatchCreatedEvent event ) {
            }

            public void matchCancelled( org.kie.api.event.rule.MatchCancelledEvent event ) {
            }

            public void beforeMatchFired( org.kie.api.event.rule.BeforeMatchFiredEvent event ) {
            }

            public void afterMatchFired( org.kie.api.event.rule.AfterMatchFiredEvent event ) {
                firings.add( "Fired!" );
            }

            public void agendaGroupPopped( org.kie.api.event.rule.AgendaGroupPoppedEvent event ) {
            }

            public void agendaGroupPushed( org.kie.api.event.rule.AgendaGroupPushedEvent event ) {
            }

            public void beforeRuleFlowGroupActivated( org.kie.api.event.rule.RuleFlowGroupActivatedEvent event ) {
            }

            public void afterRuleFlowGroupActivated( org.kie.api.event.rule.RuleFlowGroupActivatedEvent event ) {
            }

            public void beforeRuleFlowGroupDeactivated( org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event ) {
            }

            public void afterRuleFlowGroupDeactivated( org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event ) {
            }
        };

        ksession.addEventListener( agendaEventListener );

        ksession.execute( "1" );
        ksession.execute( "2" );

        assertEquals( 2, firings.size() );

        ksession.removeEventListener( agendaEventListener );

        ksession.execute( "3" );

        assertEquals( 2, firings.size() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        Map<String, String> map = new HashMap<>();
        map.put( "x", "Test" );
        ksession.insert( map );

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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        Map<String, String> map = new HashMap<>();
        map.put( "x", "Test" );
        ksession.insert( map );

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

        KieBase kbase = loadKnowledgeBaseFromString( kconf, str );
        KieSession ksession = kbase.newKieSession();

        ArrayList<String> ruleList = new ArrayList<>();
        ksession.setGlobal( "ruleList", ruleList );

        ksession.insert( "fireRules" );
        ksession.fireAllRules();

        assertEquals( ruleList.get( 0 ), "first" );
        assertEquals( ruleList.get( 1 ), "second" );
        assertEquals( ruleList.get( 2 ), "third" );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new ResourceRequirement( new Resource( 2 ), 3 ) );
        ksession.insert( new Allocation( 3 ) );

        ksession.fireAllRules();
    }

    public static class Resource {
        private final int capacity;

        public Resource( int capacity ) {
            this.capacity = capacity;
        }

        public int getCapacity() {
            return capacity;
        }
    }

    public static class ResourceRequirement {
        private final Resource resource;
        private final int executionMode;

        public ResourceRequirement( Resource resource, int executionMode ) {
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

        public Allocation( int executionMode ) {
            this.executionMode = executionMode;
        }

        public int getExecutionMode() {
            return executionMode;
        }
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

        KieBase kb = loadKnowledgeBaseFromString( drl );
        KieSession ks = kb.newKieSession();

        Map<String, String> context = new HashMap<>();
        context.put( "key", "value" );
        ks.setGlobal( "context", context );

        ks.insert( 1 );
        ks.fireAllRules();

        context.put( "key", "value" );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();
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

        KieBase kb = loadKnowledgeBaseFromString( drl );
        final KieSession ks = kb.newKieSession();
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        new Thread(ks::fireUntilHalt).start();
        try {
            for ( int j = 0; j < N; j++ ) {
                ks.getEntryPoint( "x" ).insert( new Integer( j ) );
            }

            Thread.sleep( 1000 );
        } finally {
            ks.halt();
            ks.dispose();
            assertEquals( N, list.size() );
        }
    }

    @Test
    public void testFactStealing() throws Exception {
        String drl = "package org.drools.test; \n" +
                     "import org.drools.mvel.compiler.Person; \n " +
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
                     "\n";
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( knowledgeBuilder.getKnowledgePackages() );

        KieSession knowledgeSession = kbase.newKieSession();
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
                "import org.drools.mvel.integrationtests.Misc2Test.SQLTimestamped;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new SQLTimestamped() );
        Thread.sleep( 100 );
        ksession.insert( new SQLTimestamped() );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( "ok" ), list );
    }


    public static class Foo3 {
        public boolean getX() {
            return true;
        }

        public String isX() {
            return "x";
        }

        public boolean isY() {
            return true;
        }

        public String getZ() {
            return "ok";
        }

        public boolean isZ() {
            return true;
        }
    }

    @Test
    public void testIsGetClash() {
        // DROOLS-18
        String str =
                "import org.drools.mvel.integrationtests.Misc2Test.Foo3;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();
        Map map = new HashMap();
        ksession.setGlobal( "map", map );

        ksession.fireAllRules();

        assertEquals( 2, map.size() );
        assertEquals( 160, map.get( 4 ) );
        assertEquals( 120, map.get( 3 ) );

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
        // With Property Reactivity enabled by default this also required adding a @watch(*) annotation
        String drl = "" +
                     "package org.drools.test; \n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.TradeBooking;\n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.TradeHeader;\n" +
                     "rule \"Rule1\" \n" +
                     "salience 1 \n" +
                     "when\n" +
                     "  $booking: TradeBooking()\n" +
                     "  $trade: TradeHeader() @watch(*) from $booking.getTrade()\n" +
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
                     "  $trade: Object( ) @watch(*) from $booking.getTrade()\n" +
                     "then\n" +
                     "end";
        KieBase kb = loadKnowledgeBaseFromString( drl );
        KieSession ks = kb.newKieSession();
        
        ReteDumper.dumpRete(kb);

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


        assertEquals( "Rule2", created.get( 0 ) );
        assertEquals( "Rule1", created.get( 1 ) );
        assertEquals( "Rule2", created.get( 2 ) );

        assertEquals( "Rule2", cancelled.get( 0 ) );
        assertEquals( "Rule2", cancelled.get( 1 ) );

        assertEquals( "Rule1", fired.get( 0 ) );
    }

    @Test
    public void testLockOnActive2() {
        // the modify changes the hashcode of TradeHeader
        // this forces the 'from' to think it's new. This results in an insert and a delete propagation from the 'from'
        // With Property Reactivity enabled by default this also required adding a @watch(*) annotation
        String drl = "" +
                     "package org.drools.test; \n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.TradeBooking;\n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.TradeHeader;\n" +
                     "rule \"Rule1\" \n" +
                     "lock-on-active true\n" +
                     "salience 1 \n" +
                     "when\n" +
                     "  $booking: TradeBooking()\n" +
                     "  $trade: TradeHeader() @watch(*) from $booking.getTrade()\n" +
                     "then\n" +
                     "  $trade.setAction(\"New\");\n" +
                     "  modify($booking) {}\n" +
                     "end;\n" +
                     "\n" +
                     "rule \"Rule2\"\n" +
                     "when\n" +
                     "  $booking: TradeBooking( )\n" +
                     "  $trade: Object( ) @watch(*) from $booking.getTrade()\n" +
                     "then\n" +
                     "end";
        KieBase kb = loadKnowledgeBaseFromString( drl );
        KieSession ks = kb.newKieSession();

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
        assertEquals( 2, ks.fireAllRules() );

        assertEquals( 3, created.size() );
        assertEquals( 1, cancelled.size() );
        assertEquals( 2, fired.size() );

        assertEquals( "Rule1", created.get( 0 ) );
        assertEquals( "Rule1", created.get( 1 ) );
        assertEquals( "Rule2", created.get( 2 ) );

        assertEquals( "Rule1", cancelled.get( 0 ) );

        assertEquals( "Rule1", fired.get( 0 ) );
        assertEquals( "Rule2", fired.get( 1 ) );
    }

    @Test
    public void testLockOnActiveWithModify() {
        String drl = "" +
                     "package org.drools.test; \n" +
                     "import org.drools.mvel.compiler.Person; \n" +
                     "" +
                     "rule \"Rule1\" \n" +
                     "@Propagation(EAGER) \n" +
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
                     "@Propagation(EAGER) \n" +
                     "lock-on-active true\n" +
                     "when\n" +
                     "  $p: Person() \n" +
                     "  String() from $p.getName() \n" +
                     "then\n" +
                     "  System.out.println( \"Rule2\" + $p ); " +
                     "  modify ( $p ) { setName( \"john\" ); } \n" +
                     "end";
        KieBase kb = loadKnowledgeBaseFromString( drl );
        KieSession ks = kb.newKieSession();
        ks.addEventListener( new DebugAgendaEventListener() );

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
                     "import org.drools.mvel.compiler.Person; \n" +
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
        KieBase kb = loadKnowledgeBaseFromString( drl );
        KieSession ks = kb.newKieSession();
        ks.addEventListener( new DebugAgendaEventListener() );

        ks.fireAllRules();

        Person p = new Person( "mark", 76 );
        ks.insert( p );
        ks.fireAllRules();

        assertEquals( 44, p.getAge() );
        assertEquals( "john", p.getName() );
    }

    @Test
    public void testPrimitiveGlobals() {
        String drl = "package org.drools.mvel.integrationtests\n" +
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
        KieBase kb = loadKnowledgeBaseFromString( drl );

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
        kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( rule2.getBytes() ), ResourceType.DRL );


        //the default behavior of kbuilder is not to fail because of duplicated
        //rules.
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        //We must have 1 INFO result.
        KnowledgeBuilderResults infos = kbuilder.getResults( ResultSeverity.INFO );
        assertNotNull( infos );
        assertEquals( 1, infos.size() );

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
        KieBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertTrue( !list.isEmpty() );
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
        KieBase kbase = loadKnowledgeBaseFromString( kbConf, drl );
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertTrue( !list.isEmpty() );
        assertEquals( 3, list.size() );
        assertEquals( 3, list.get( 0 ) );
        assertEquals( 4, list.get( 1 ) );
        assertEquals( 4, list.get( 2 ) );

    }

    @Test
    public void testEvalConstraintWithMvelOperator() {
        String drl = "rule \"yeah\" " + "\tdialect \"mvel\"\n when "
                     + "Foo( eval( field soundslike \"water\" ) )" + " then " + "end";
        DrlParser drlParser = new DrlParser();
        PackageDescr packageDescr;
        try {
            packageDescr = drlParser.parse( true, drl );
        } catch (DroolsParserException e) {
            throw new RuntimeException( e );
        }
        RuleDescr r = packageDescr.getRules().get( 0 );
        PatternDescr pd = (PatternDescr) r.getLhs().getDescrs().get( 0 );
        assertEquals( 1, pd.getConstraint().getDescrs().size() );
    }


    @Test
    public void testManyAccumulatesWithSubnetworks() {
        String drl = "package org.drools.mvel.compiler.tests; \n" +
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
        KieBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        KieSession ksession = kbase.newKieSession();

        int num = ksession.fireAllRules();
        // only one rule should fire, but the partial propagation of the asserted facts should not cause a runtime NPE
        assertEquals( 1, num );

    }


    @Test
    public void testLinkRiaNodesWithSubSubNetworks() {
        String drl = "package org.drools.mvel.compiler.tests; \n" +
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
        KieBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        KieSession ksession = kbase.newKieSession();
        List<Long> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( 2, list.get( 0 ).intValue() );
        assertEquals( 2, list.get( 1 ).intValue() );

    }

    @Test
    public void testDynamicSalienceUpdate() {
        String drl = "package org.drools.mvel.compiler.tests; \n" +
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
        KieBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
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
        KieBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        ksession.insert( "1" );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );

        ksession.insert( 1 );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );

        ksession.dispose();
    }

    @Test
    public void testNoLoopWithNamedConsequences() {
        // DROOLS-327
        String drl =
                "import " + Message.class.getCanonicalName() + "\n" +
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
        KieBase kbase = loadKnowledgeBaseFromString( kbConf, drl );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Message( "Hello World" ) );
        ksession.fireAllRules();
    }

    @Test
    public void testInsertModifyInteractionsWithLockOnActive() {
        String drl =
                "package org.drools.mvel.integrationtests;\n" +
                "import org.drools.mvel.compiler.Message;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();

        Message m1 = new Message( "msg1" );
        ksession.insert( m1 );
        assertEquals( 2, ksession.fireAllRules() );

        Message m2 = (Message) ksession.getGlobal( "m2" );

        assertEquals( "msg1", m1.getMessage() );
        assertEquals( "msg2", m1.getMessage2() );
        assertEquals( "msg3", m1.getMessage3() );

        assertEquals( "msg1", m2.getMessage() );
        assertEquals( "Two", m2.getMessage2() ); // r1 does not fire for m2
        assertEquals( "Three", m2.getMessage3() );
    }

    @Test(timeout = 10000)
    public void testWumpus1() {
        String drl = "import org.drools.mvel.integrationtests.Misc2Test.Hero;\n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.StepForwardCommand;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Hero hero = new Hero( 1 );
        ksession.insert( hero );
        ksession.fireAllRules();

        ksession.insert( new StepForwardCommand() );
        assertEquals( 1, ksession.fireAllRules() );
        assertEquals( 2, hero.getPos() );
    }

    @Test(timeout = 10000)
    public void testWumpus2() {
        String drl = "import org.drools.mvel.integrationtests.Misc2Test.Hero;\n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.StepForwardCommand;\n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.ChangeDirectionCommand;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Hero hero = new Hero( 1 );
        ksession.insert( hero );
        ksession.fireAllRules();

        ksession.insert( new StepForwardCommand() );
        assertEquals( 1, ksession.fireAllRules() );
        assertEquals( 2, hero.getPos() );
    }

    @Test(timeout = 10000)
    public void testWumpus3() {
        String drl = "import org.drools.mvel.integrationtests.Misc2Test.Hero;\n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.StepForwardCommand;\n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.ChangeDirectionCommand;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Hero hero = new Hero( 1 );
        ksession.insert( hero );
        ksession.fireAllRules();

        ksession.insert( new StepForwardCommand() );
        ksession.fireAllRules();
        assertEquals( 2, hero.getPos() );

        ksession.insert( new ChangeDirectionCommand() );
        ksession.fireAllRules();
        ksession.insert( new StepForwardCommand() );
        ksession.fireAllRules();
        assertEquals( 1, hero.getPos() );
    }

    @Test(timeout = 10000)
    public void testWumpus4() {
        String drl = "import org.drools.mvel.integrationtests.Misc2Test.Hero;\n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.StepForwardCommand;\n" +
                     "import org.drools.mvel.integrationtests.Misc2Test.ChangeDirectionCommand;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Hero hero = new Hero( 1 );
        ksession.insert( hero );
        ksession.fireAllRules();

        ksession.insert( new StepForwardCommand() );
        ksession.fireAllRules();
        assertEquals( 2, hero.getPos() );


        ksession.insert( new ChangeDirectionCommand() );
        ksession.fireAllRules();
        ksession.insert( new StepForwardCommand() );
        ksession.fireAllRules();
        assertEquals( 1, hero.getPos() );
    }

    @PropertyReactive
    public static class Hero {
        private int pos = 1;
        private boolean goingRight = true;

        public Hero( int pos ) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }

        public void setPos( int pos ) {
            this.pos = pos;
        }

        public boolean isGoingRight() {
            return goingRight;
        }

        public void setGoingRight( boolean goingRight ) {
            this.goingRight = goingRight;
        }

    }

    public static class ChangeDirectionCommand {
    }

    public static class StepForwardCommand {
    }

    @Test
    public void testDynamicSalience() {
        // DROOLS-334
        String drl = "import org.drools.mvel.integrationtests.Misc2Test.SimpleMessage\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();

        SimpleMessage[] msgs = new SimpleMessage[]{new SimpleMessage( 0 ), new SimpleMessage( 1 ), new SimpleMessage( 2 ), new SimpleMessage( 3 )};
        for ( SimpleMessage msg : msgs ) {
            ksession.insert( msg );
        }

        ksession.fireAllRules();

        for ( SimpleMessage msg : msgs ) {
            assertEquals( SimpleMessage.Status.FILTERED, msg.getStatus() );
        }

        assertEquals( 0, ksession.getFactCount() );
    }

    public static class SimpleMessage {

        public enum Status {ENRICHED, TO_SEND, SENT, FILTERED}

        private final int index;
        private Status status = Status.ENRICHED;

        public SimpleMessage( int index ) {
            this.index = index;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus( Status status ) {
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 4, list.size() );
        assertEquals( 160, list.get( 0 ) );
        assertEquals( 4, list.get( 1 ) );
        assertEquals( 120, list.get( 2 ) );
        assertEquals( 3, list.get( 3 ) );
    }

    @Test
    public void testMatchingEventsInStreamMode() {
        // DROOLS-338
        String drl =
                "import org.drools.mvel.integrationtests.Misc2Test.SimpleEvent\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( kconf, drl );
        KieSession ksession = kbase.newKieSession();

        final AtomicInteger i = new AtomicInteger( 0 );

        ksession.addEventListener( new DefaultAgendaEventListener() {
            public void matchCreated( MatchCreatedEvent event ) {
                i.incrementAndGet();
            }

            public void matchCancelled( MatchCancelledEvent event ) {
                i.decrementAndGet();
            }
        } );

        ksession.insert( new SimpleEvent() );
        ksession.fireAllRules();

        assertEquals( 1, i.get() );
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

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "working", list.get( 0 ) );
        ksession.dispose();

        ksession = kbase.newKieSession();

        list.clear();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "working", list.get( 0 ) );
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

        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();
    }

    @Test
    public void testWildcardImportForTypeFieldOldApi() {
        // DROOLS-348
        String drl = "import java.util.*\n" +
                     "declare MyType\n" +
                     "    l : List\n" +
                     "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();
    }

    @Test
    public void testTypeCheckInOr() {
        // BZ-1029911
        String str = "import org.drools.mvel.compiler.*;\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.fireAllRules();
    }

    @Test
    public void testDynamicNegativeSalienceWithSpace() {
        // DROOLS-302
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R\n" +
                "salience - $age\n" +
                "when\n" +
                "  Person( $age : age )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        Person p1 = new Person( "A", 31 );
        FactHandle fh1 = ksession.insert( p1 );

        ksession.fireAllRules();
    }

    @Test
    public void testJoinNoLoop() {
        // BZ-1034094
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R no-loop\n" +
                "when\n" +
                "  String()\n" +
                "  $p : Person( $age : age )\n" +
                "then\n" +
                "    modify($p) { setAge( $age + 1 ) }\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        Person mario = new Person( "Mario", 38 );

        ksession.insert( "a" );
        ksession.insert( "b" );
        ksession.insert( mario );
        ksession.fireAllRules();

        assertEquals( 40, mario.getAge() );
    }

    @Test
    public void testConstraintOnSerializable() {
        // DROOLS-372
        String str =
                "import org.drools.mvel.integrationtests.Misc2Test.SerializableValue\n" +
                "rule R\n" +
                "when\n" +
                "  SerializableValue( value == \"1\" )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new SerializableValue( "0" ) );
        ksession.fireAllRules();
    }

    public static class SerializableValue {
        private final Serializable value;

        public SerializableValue( Serializable value ) {
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
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    $p : Person()\n" +
                "    exists Person( age > $p.age, name.contains($p.name.substring(0, 1)) )\n" +
                "then\n" +
                "end";

        KieBase kb = loadKnowledgeBaseFromString( drl );
        KieSession ks = kb.newKieSession();

        Person[] ps = new Person[4];
        FactHandle[] fhs = new FactHandle[4];

        ps[0] = new Person( "a", 5 );
        ps[1] = new Person( "b", 5 );
        ps[2] = new Person( "d", 10 );
        ps[3] = new Person( "a", 15 );

        fhs[0] = ks.insert( ps[0] );
        fhs[1] = ks.insert( ps[1] );
        fhs[2] = ks.insert( ps[2] );
        fhs[3] = ks.insert( ps[3] );

        ps[0].setName( "c" );
        ks.update( fhs[0], ps[0] );
        ks.fireAllRules();

        ps[2].setName( "b" );
        ks.update( fhs[2], ps[2] );
        ks.fireAllRules();

        ps[2].setName( "d" );
        ks.update( fhs[2], ps[2] );
        ks.fireAllRules();

        ps[1].setName( "c" );
        ks.update( fhs[1], ps[1] );
        ks.fireAllRules();

        ps[3].setName( "d" );
        ks.update( fhs[3], ps[3] );
        ks.fireAllRules();
    }

    public static class AA {
        int id;

        public AA( int i ) {
            this.id = i;
        }

        public boolean match( Long value ) {
            return true;
        }

        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            AA aa = (AA) o;
            if ( id != aa.id ) return false;
            return true;
        }

        public int hashCode() {
            return id;
        }
    }

    public static class BB {
        public Integer getValue() {
            return 42;
        }
    }

    @Test
    @Ignore
    public void testJitting() {
        // DROOLS-185
        String str =
                " import org.drools.mvel.integrationtests.Misc2Test.AA; " +
                " import org.drools.mvel.integrationtests.Misc2Test.BB; " +
                " global java.util.List list; \n" +
                " " +
                " rule R \n " +
                " when \n" +
                "    BB( $v : value ) \n" +
                "    $a : AA( match( $v ) ) \n" +
                " then \n" +
                "   list.add( $a ); \n" +
                " end \n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new BB() );
        for ( int j = 0; j < 100; j++ ) {
            ksession.insert( new AA( j ) );
            ksession.fireAllRules();
        }

        assertEquals( 100, list.size() );
    }

    @Test
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


        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();
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
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        byte[] jar = createKJar( ks, releaseId, null, drl1 );
        KieModule km = deployJar( ks, jar );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        ksession.fireAllRules();
    }

    @Test
    public void testJittingConstraintWithArrayParams() throws Exception {
        // BZ-1057000
        String str =
                "import org.drools.mvel.integrationtests.Misc2Test.Strings\n" +
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<String> allList = new ArrayList<>();
        ksession.setGlobal( "allList", allList );
        List<String> anyList = new ArrayList<>();
        ksession.setGlobal( "anyList", anyList );

        ksession.insert( new Strings( "1", "2", "3" ) );
        ksession.insert( new Strings( "2", "3" ) );
        ksession.fireAllRules();

        assertEquals( 1, allList.size() );
        assertEquals( 2, anyList.size() );
    }

    public static class Strings {
        private final String[] strings;

        public Strings( String... strings ) {
            this.strings = strings;
        }

        public boolean containsAny( String[] array ) {
            for ( String candidate : array ) {
                for ( String s : strings ) {
                    if ( candidate.equals( s ) ) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean containsAll( String... array ) {
            int counter = 0;
            for ( String candidate : array ) {
                for ( String s : strings ) {
                    if ( candidate.equals( s ) ) {
                        counter++;
                        break;
                    }
                }
            }
            return counter == array.length;
        }
    }

    public static class ARef {
        public static int getSize( String s ) {
            return 0;
        }
    }

    public static class BRef extends ARef {
        public static int getSize( String s ) {
            return s.length();
        }
    }

    @Test
    public void testJittingConstraintInvokingStaticMethod() throws Exception {
        // DROOLS-410
        String str =
                "dialect \"mvel\"\n" +
                "import org.drools.mvel.integrationtests.Misc2Test.ARef\n" +
                "import org.drools.mvel.integrationtests.Misc2Test.BRef\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule R when\n" +
                "    $s : String( length == BRef.getSize(this) )\n" +
                "then\n" +
                "    list.add($s);\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( "1234" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    public static class EvallerBean {
        private final Evaller evaller = new Evaller( 1 );

        public Evaller getEvaller() {
            return evaller;
        }
    }

    public static class Evaller {
        private final int size;

        public Evaller() {
            this( 0 );
        }

        public Evaller( int size ) {
            this.size = size;
        }

        public boolean check( Object o ) {
            return true;
        }

        public static boolean checkStatic( Object o ) {
            return true;
        }

        public int size() {
            return size;
        }

        public int getSize() {
            return size;
        }
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.setGlobal( "evaller", new Evaller() );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 42, (int) list.get( 0 ) );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 42, (int) list.get( 0 ) );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.setGlobal( "evaller", new Evaller() );

        ksession.insert( new EvallerBean() );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 42, (int) list.get( 0 ) );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( new Evaller() );
        ksession.insert( new EvallerBean() );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 42, (int) list.get( 0 ) );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( "abcde" );
        ksession.insert( "bcdef" );
        ksession.insert( "cdefg" );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( Arrays.asList( "abcde", "bcdef" ) ) );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( "abcde" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.setGlobal( "tat", new Date( 2000 ) );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
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

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();
        Foo f1 = new Foo();
        Foo2 f2 = new Foo2();
        ksession.insert( f1 );
        ksession.insert( f2 );
        assertEquals( 2, ksession.fireAllRules() );
        assertEquals( 3, f2.getX() );
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
        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();

        int n = ksession.fireAllRules();
        assertEquals( 7, n );
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
        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();

        int n = ksession.fireAllRules();
        assertEquals( 8, n );
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
        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();

        int n = ksession.fireAllRules();
        assertEquals( 1, n );
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
        assertEquals( 0, results.getMessages().size() );
    }

    public static class MyDate extends Date {
        private String description;

        public String getDescription() {
            return this.description;
        }

        public void setDescription( final String desc ) {
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
        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( 0 );
        ksession.fireAllRules();
        ksession.insert( 1 );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 0, (int) list.get( 0 ) );
    }

    @Test
    public void testRedeclaringRuleAttribute() {
        // BZ-1092084
        String str = "rule R salience 10 salience 100 when then end\n";

        assertDrlHasCompilationError( str, 1 );
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
        assertEquals( 0, results.getMessages().size() );
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( pkgBuilderCfg );
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

        assertDrlHasCompilationError( str, 1 );
    }

    @Test
    public void testCrossNoLoopWithNodeSharing() throws Exception {
        // DROOLS-501 Propgation context is not set correctly when nodes are shared
        // This test was looping in 6.1.0-Beta4
        String drl = "package org.drools.mvel.compiler.loop " +

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

        ArrayList<Trailer> trailerList = new ArrayList<>();
        ksession.setGlobal( "trailerList", trailerList );

        Trailer trailer1 = new Trailer( Trailer.TypeStatus.WAITING );

        ksession.insert( trailer1 );

        // set the agenda groups in reverse order so that stack is preserved
        ksession.getAgenda().getAgendaGroup( "Start" ).setFocus();
        ksession.getAgenda().getAgendaGroup( "End" ).setFocus();
        ksession.getAgenda().getAgendaGroup( "Start" ).setFocus();

        ksession.fireAllRules();

        assertEquals( 2, trailerList.size() );
    }


    public static class Trailer {
        public enum TypeStatus {WAITING, LOADING, SHIPPING}

        private TypeStatus status;

        public Trailer( TypeStatus status ) {
            this.status = status;
        }

        public TypeStatus getStatus() {
            return status;
        }

        public void setStatus( TypeStatus status ) {
            this.status = status;
        }
    }

    public static class Host {
    }

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

        ksession.insert( new Host() );
        ksession.insert( "host" );
        ksession.fireAllRules();
    }

    public static class TypeA {
        private int id = 1;

        public int getId() {
            return id;
        }
    }

    public static class TypeB {
        private int parentId = 1;
        private int id = 2;

        public int getParentId() {
            return parentId;
        }

        public int getId() {
            return id;
        }
    }

    public static class TypeC {
        private int parentId = 2;

        public int getParentId() {
            return parentId;
        }

        public int getValue() {
            return 1;
        }
    }

    public static class TypeD {
        private int parentId = 2;
        private int value;

        public int getParentId() {
            return parentId;
        }

        public int getValue() {
            return value;
        }

        public void setValue( int value ) {
            this.value = value;
        }
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

        ksession.insert( new TypeA() );
        ksession.insert( new TypeB() );
        ksession.insert( new TypeC() );
        TypeD d = new TypeD();
        ksession.insert( d );
        ksession.fireAllRules();
        assertEquals( 1, d.getValue() );
    }

    public static class Reading {
        private final String type;
        private final int value;

        public Reading( String type, int value ) {
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

        public void setLevel( String level ) {
            this.level = level;
        }

        public String getType() {
            return type;
        }

        public void setType( String type ) {
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

        ksession.insert( "t1" );

        ksession.insert( new Reading( "t1", 12 ) );
        ksession.fireAllRules();
        ksession.insert( new Reading( "t1", 0 ) );
        ksession.fireAllRules();
        ksession.insert( new Reading( "t1", 0 ) );
        ksession.fireAllRules();

        ksession.insert( new Reading( "t1", 16 ) );
        ksession.fireAllRules();
        ksession.insert( new Reading( "t1", 32 ) );
        ksession.fireAllRules();
        ksession.insert( new Reading( "t1", -6 ) );
        ksession.fireAllRules();
    }

    public static class C1 {
        private int counter = 0;
        private final List<C2> c2s = Arrays.asList( new C2(), new C2() );

        public List<C2> getC2s() {
            return c2s;
        }

        public int getSize() {
            return getC2s().size();
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter( int counter ) {
            this.counter = counter;
        }
    }

    public static class C2 {
        private final List<C3> c3s = Arrays.asList( new C3( 1 ), new C3( 2 ) );

        public List<C3> getC3s() {
            return c3s;
        }
    }

    public static class C3 {
        public final int value;

        public C3( int value ) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
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

        ksession.insert( new C1() );
        ksession.fireAllRules();
    }

    public interface I0 {
        String getValue();
    }

    public interface I1 extends I0 {
    }

    public static class X implements I0 {
        @Override
        public String getValue() {
            return "x";
        }
    }

    public static class Y extends X implements I1 {
    }

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

        ksession.insert( new Y() );
        ksession.fireAllRules();
        ksession.insert( new Z() );
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

        assertDrlHasCompilationError( str, 1 );
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
        assertEquals( 0, results.getMessages().size() );
    }

    public static class Underscore {
        private String _id;

        public String get_id() {
            return _id;
        }

        public void set_id( String _id ) {
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

        ksession.insert( "1" );
        ksession.insert( 1 );
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

        ksession.insert( "1" );
        ksession.insert( 1L );
        ksession.insert( 1 );
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

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "group by hello count is 2" ) );
        assertTrue( list.contains( "group by hi count is 1" ) );
    }

    @Test
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
                "        $rule: Fired( rule==\"F060\" )\n" +
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

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        System.out.println( list );
        assertEquals( 3, list.size() );
        assertTrue( list.contains( "F060b" ) );
        assertTrue( list.contains( "F060c" ) );
        assertTrue( list.contains( "F060d" ) );
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

        List<Integer> list = new ArrayList<>();
        kieSession.setGlobal( "list", list );

        kieSession.insert( 10 );
        kieSession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( 10, (int) list.get( 0 ) );
        assertEquals( 10, (int) list.get( 1 ) );
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
        assertEquals( 10, list.get( 0 ) );
        assertEquals( "10", list.get( 1 ) );
    }

    @Test
    public void testCustomDynamicSalience() {
        String drl = "package org.drools.test; " +
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

        List<Integer> list = new ArrayList<>();
        session.setGlobal( "list", list );

        for ( Rule r : session.getKieBase().getKiePackage( "org.drools.test" ).getRules() ) {
            ( (RuleImpl) r ).setSalience( new Salience() {
                @Override
                public int getValue( KnowledgeHelper khelper, Rule rule, WorkingMemory workingMemory ) {
                    if ( khelper == null ) {
                        return 0;
                    }
                    InternalFactHandle h = (InternalFactHandle) khelper.getMatch().getFactHandles().get( 0 );
                    return ( (Person) h.getObject() ).getAge();
                }

                @Override
                public int getValue() {
                    throw new IllegalStateException( "Should not have been called..." );
                }

                @Override
                public boolean isDynamic() {
                    return true;
                }
            } );
        }

        session.insert( new Person( "a", 1 ) );
        session.insert( new Person( "a", 5 ) );
        session.insert( new Person( "a", 3 ) );
        session.insert( new Person( "b", 4 ) );
        session.insert( new Person( "b", 2 ) );
        session.insert( new Person( "b", 6 ) );

        session.fireAllRules();

        assertEquals( Arrays.asList( 6, 5, 4, 3, 2, 1 ), list );
    }

    @Test
    public void testNotWithSubNetwork() {
        String drl =
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

        ksession.insert( "1" );
        FactHandle iFH = ksession.insert( 1 );
        FactHandle lFH = ksession.insert( 1L );
        assertEquals( 0, ksession.fireAllRules() );

        ksession.delete( iFH );
        ksession.delete( lFH );
        assertEquals( 1, ksession.fireAllRules() );

        assertEquals( 0, ksession.getFactCount() );
    }

    @Test
    public void testGenericsInRHSWithModify() {
        // DROOLS-493
        String drl =
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

        ksession.insert( "1" );
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

        List<Integer> list = new ArrayList<>();
        kieSession.setGlobal( "list", list );

        FactHandle handle = kieSession.insert( 42 );

        Agenda agenda = kieSession.getAgenda();
        agenda.getAgendaGroup( "two" ).setFocus();
        agenda.getAgendaGroup( "one" ).setFocus();

        kieSession.fireAllRules();
        assertEquals( Arrays.asList( 42 ), list );

        kieSession.delete( handle );

        kieSession.insert( 99 );

        agenda.getAgendaGroup( "two" ).setFocus();
        agenda.getAgendaGroup( "one" ).setFocus();

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

        List<Integer> list = new ArrayList<>();
        kieSession.setGlobal( "list", list );

        FactHandle iFH = kieSession.insert( 42 );
        FactHandle sFH = kieSession.insert( "42" );

        Agenda agenda = kieSession.getAgenda();
        agenda.getAgendaGroup( "three" ).setFocus();
        agenda.getAgendaGroup( "two" ).setFocus();
        agenda.getAgendaGroup( "one" ).setFocus();

        kieSession.fireAllRules();
        assertEquals( Arrays.asList( 42 ), list );

        //kieSession.delete( iFH );
        kieSession.delete( sFH );

        kieSession.insert( 99 );
        kieSession.insert( "99" );

        agenda.getAgendaGroup( "three" ).setFocus();
        agenda.getAgendaGroup( "two" ).setFocus();
        agenda.getAgendaGroup( "one" ).setFocus();

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

        KnowledgePackageImpl kPackage = new KnowledgePackageImpl( "com.testfacttemplate" );
        FieldTemplate fieldTemplate = new FieldTemplateImpl( "status", 0, Integer.class );
        FactTemplate factTemplate = new FactTemplateImpl( kPackage, "TestFactTemplate", new FieldTemplate[]{fieldTemplate} );

        KnowledgeBuilder kBuilder = new KnowledgeBuilderImpl( kPackage );
        StringReader rule = new StringReader( drl );
        try {
            ( (KnowledgeBuilderImpl) kBuilder ).addPackageFromDrl( rule );
        } catch (Exception e) {
            throw new RuntimeException( e );
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
        helper.addContent( drl, ResourceType.DRL );
        KieSession kieSession = helper.build().newKieSession();

        List<Integer> list = new ArrayList<>();
        kieSession.setGlobal( "list", list );

        kieSession.insert( 3 );
        kieSession.insert( 2 );
        kieSession.insert( 6 );
        kieSession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList( 3, 6 ) ) );
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

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        ksession.insert( "1" );
        ksession.insert( "2" );

        TypeCC cc = new TypeCC();
        ksession.insert( cc );
        ksession.insert( new TypeDD() );

        ksession.fireAllRules();

        System.out.println( "Rule R2 is fired count - " + cc.getValue() );

        assertEquals( "Rule 2 should be fired once as we have firing rule as one of criteria checking rule only fire once", 1, cc.getValue() );
    }

    public static class ValueContainer {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue( int value ) {
            this.value = value;
        }
    }

    public static class TypeCC extends ValueContainer {
    }

    public static class TypeDD extends ValueContainer {
    }

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

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Class> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        System.out.println( list );
        assertTrue( list.containsAll( asList( String.class, Integer.class ) ) );
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

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        StringBuilder sb = new StringBuilder();
        ksession.setGlobal( "sb", sb );

        ksession.insert( "test" );
        StringWrapper sw = new StringWrapper();
        FactHandle swFH = ksession.insert( sw );
        ksession.fireAllRules();

        sw.setWrapped( "test" );
        ksession.update( swFH, sw );
        ksession.fireAllRules();

        sw.setWrapped( null );
        ksession.update( swFH, sw );
        ksession.fireAllRules();

        sw.setWrapped( "test" );
        ksession.update( swFH, sw );
        ksession.fireAllRules();

        sw.setWrapped( null );
        ksession.update( swFH, sw );
        ksession.fireAllRules();

        sw.setWrapped( "test" );
        ksession.update( swFH, sw );
        ksession.fireAllRules();

        assertEquals( "040404", sb.toString() );
    }

    public interface TestString<T extends TestString> extends Comparable<TestString<?>> { }

    public static class StringWrapper implements TestString<StringWrapper> {
        private String wrapped;

        public StringWrapper() { }

        public StringWrapper(String wrapped) {
            this.wrapped = wrapped;
        }

        public String getWrapped() {
            return wrapped;
        }

        public void setWrapped( String wrapped ) {
            this.wrapped = wrapped;
        }

        public boolean contains( String s ) {
            return wrapped != null && wrapped.equals( s );
        }

        public int getValue() {
            return wrapped != null ? wrapped.length() : 0;
        }

        @Override
        public int compareTo( TestString o ) {
            return wrapped.compareTo( ( (StringWrapper) o ).wrapped );
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
                "";

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
                "\n";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        assertTrue( helper.verify().getMessages( org.kie.api.builder.Message.Level.ERROR ).isEmpty() );
        KieSession ks = helper.build().newKieSession();
        assertEquals( 1, ks.fireAllRules() );
    }

    @Test
    public void testMvelConstraintErrorMessageOnAlpha() throws Exception {
        // DROOLS-687
        String drl =
                " import org.drools.mvel.compiler.Person; " +
                " import org.drools.mvel.compiler.Address; " +
                " rule 'hello person' " +
                " when " +
                " Person( address.street == 'abbey' ) " +
                " then " +
                " end " +
                "\n";
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        assertTrue( helper.verify().getMessages( org.kie.api.builder.Message.Level.ERROR ).isEmpty() );
        KieSession ks = helper.build().newKieSession();
        Person john = new Person( "John" ); // address is null
        try {
            ks.insert( john );
            ks.fireAllRules();
            fail( "Should throw an exception" );
        } catch (Exception e) {
            assertTrue( e.getMessage().contains( "hello person" ) );
        }
    }

    @Test
    public void testMvelConstraintErrorMessageOnBeta() throws Exception {
        // DROOLS-687
        String drl =
                " import org.drools.mvel.compiler.Person; " +
                " import org.drools.mvel.compiler.Address; " +
                " rule 'hello person' " +
                " when " +
                " $s : String( ) " +
                " Person( address.street == $s ) " +
                " then " +
                " end " +
                "\n";
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        assertTrue( helper.verify().getMessages( org.kie.api.builder.Message.Level.ERROR ).isEmpty() );
        KieSession ks = helper.build().newKieSession();
        Person john = new Person( "John" ); // address is null
        try {
            ks.insert( "abbey" );
            ks.insert( john );
            ks.fireAllRules();
            fail( "Should throw an exception" );
        } catch (Exception e) {
            assertTrue( e.getMessage().contains( "hello person" ) );
        }
    }

    @Test
    public void testPassiveExists() {
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

        KieSession ksession = new KieHelper().addContent( drl2, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ksession.insert( asList( "Mario", "Mark" ) );
        ksession.insert( asList( "Julie", "Leiti" ) );

        assertEquals( 4, ksession.fireAllRules() );
    }

    @Test
    public void testFromAfterOr() {
        // DROOLS-707
        String drl2 =
                "rule \"Disaster Rule\"\n" +
                "    when\n" +
                "        eval(true) or ( eval(false) and Integer() )\n" +
                "        $a : Integer()\n" +
                "        Integer() from $a\n" +
                "    then\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl2, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ksession.insert( 1 );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testMalformedAccumulate() {
        // DROOLS-725
        String str =
                "rule R when\n" +
                "    Number() from accumulate(not Number(),\n" +
                "        init( double total = 0; ),\n" +
                "        action( ),\n" +
                "        reverse( ),\n" +
                "        result( new Double( total ) )\n" +
                "    )\n" +
                "then end\n";

        assertDrlHasCompilationError( str, 1 );
    }

    @Test
    public void testDuplicateDeclarationInAccumulate1() {
        // DROOLS-727
        String drl1 =
                "import java.util.*\n" +
                "rule \"Version 1 - crash\"\n" +
                " when\n" +
                " accumulate( Integer($int: intValue), $list: collectSet($int) )\n" +
                " List() from collect( Integer($list not contains intValue) )\n\n" +
                " accumulate( Integer($int: intValue), $list: collectSet($int) )\n" +
                " then\n" +
                "end\n";

        assertDrlHasCompilationError( drl1, 1 );
    }

    @Test
    public void testDuplicateDeclarationInAccumulate2() {
        // DROOLS-727
        String drl1 =
                "import java.util.*\n" +
                "rule \"Version 2 - pass\"\n" +
                "when\n" +
                " $list: List() from collect( Integer() )\n\n" +
                " accumulate( Integer($int: intValue), $list: collectSet($int) )\n" +
                " List() from collect( Integer($list not contains intValue) )\n" +
                "then\n" +
                "end;\n";

        assertDrlHasCompilationError( drl1, 1 );
    }

    @Test
    public void testCompilationFailureOnNonExistingVariable() {
        // DROOLS-734
        String drl1 =
                "import java.util.*\n" +
                "rule R\n" +
                "when\n" +
                "  String(this after $event)\n" +
                "then\n" +
                "end;\n";

        assertDrlHasCompilationError( drl1, 1 );
    }

    @Test
    public void testJittedConstraintStringAndLong() {
        // DROOLS-740
        String drl =
                " import org.drools.mvel.compiler.Person; " +
                " rule 'hello person' " +
                " when " +
                " Person( name == \"Elizabeth\" + new Long(2L) ) " +
                " then " +
                " end " +
                "\n";
        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ksession.insert( new org.drools.mvel.compiler.Person( "Elizabeth2", 88 ) );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testKieBuilderWithClassLoader() {
        // DROOLS-763
        String drl =
                "import com.billasurf.Person\n" +
                "\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    Person p = new Person();\n" +
                "    p.setAge($i);\n" +
                "    insert(p);\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $p : Person()\n" +
                "then\n" +
                "    list.add($p.getAge());\n" +
                "end\n";

        URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{this.getClass().getResource( "/billasurf.jar" )} );
        KieSession ksession = new KieHelper().setClassLoader( urlClassLoader )
                                             .addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( 18 );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 18, (int) list.get( 0 ) );
    }

    @Test
    public void testInsertAndDelete() {
        String drl =
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    $s : String()\n" +
                "then\n" +
                "    delete($i);\n" +
                "    list.add($s);\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );
        ksession.insert( "a" );
        ksession.insert( "b" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testClearActivationGroupCommand() {
        // DROOLS-828
        String drl =
                "package org.kie.test\n" +
                "global java.util.List list\n" +
                "rule \"Rule in first agenda group\" @Propagation(IMMEDIATE)\n" +
                "agenda-group \"first-agenda\"\n" +
                "salience 10\n" +
                "when\n" +
                "then\n" +
                "list.add(\"Rule in first agenda group executed\");\n" +
                "end\n" +
                "rule \"Rule without agenda group\" @Propagation(IMMEDIATE)\n" +
                "salience 100\n" +
                "when\n" +
                "then\n" +
                "list.add(\"Rule without agenda group executed\");\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ksession.setGlobal( "list", new ArrayList<String>() );
        ksession.getAgenda().getAgendaGroup( "first-agenda" ).setFocus();
        ksession.getAgenda().getAgendaGroup( "first-agenda" ).clear();
        ksession.fireAllRules();

        ArrayList<String> list = (ArrayList<String>) ksession.getGlobal( "list" );
        assertEquals( 1, list.size() );
        assertEquals( "Rule without agenda group executed", list.get( 0 ) );
    }

    @Test
    public void testClearActivationGroupCommandNoImmediatePropagation() {
        // DROOLS-865
        String drl =
                "package org.kie.test\n" +
                "global java.util.List list\n" +
                "rule \"Rule in first agenda group\"\n" +
                "agenda-group \"first-agenda\"\n" +
                "salience 10\n" +
                "when\n" +
                "then\n" +
                "list.add(\"Rule in first agenda group executed\");\n" +
                "end\n" +
                "rule \"Rule without agenda group\"\n" +
                "salience 100\n" +
                "when\n" +
                "then\n" +
                "list.add(\"Rule without agenda group executed\");\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ksession.setGlobal( "list", new ArrayList<String>() );
        ksession.getAgenda().getAgendaGroup( "first-agenda" ).setFocus();
        ksession.getAgenda().getAgendaGroup( "first-agenda" ).clear();
        ksession.fireAllRules();

        ArrayList<String> list = (ArrayList<String>) ksession.getGlobal( "list" );
        assertEquals( 1, list.size() );
        assertEquals( "Rule without agenda group executed", list.get( 0 ) );
    }

    public static class $X {
        public static class $Y {
            private final int value;

            public $Y( int value ) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }
        }
    }

    @Test
    public void testDoubleNestedClass() {
        // DROOLS-815
        String drl =
                "import " + $X.$Y.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    $X.$Y($v : value)\n" +
                "then\n" +
                "    list.add($v);\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( new $X.$Y( 42 ) );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 42, list.get( 0 ) );
    }

    @Test
    public void testWrongNodeSharing() {
        // DROOLS-588
        String drl1 =
                "package test1\n" +
                "import static " + Misc2Test.class.getCanonicalName() + ".parseInt;\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    String( parseInt(this) == 0 )\n" +
                "then\n" +
                "    list.add(\"OK\");\n" +
                "end";

        String drl2 =
                "package test2\n" +
                "import static java.lang.Integer.parseInt;\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    String( parseInt(this) == 0 )\n" +
                "then\n" +
                "    list.add(\"NOT OK\");\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl1, ResourceType.DRL );
        helper.addContent( drl2, ResourceType.DRL );
        KieSession kieSession = helper.build().newKieSession();

        List list = new ArrayList();
        kieSession.setGlobal( "list", list );

        kieSession.insert( "3" );
        kieSession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "OK", list.get( 0 ) );
    }

    public static int parseInt( String s ) {
        return 0;
    }

    @Test
    public void testJittedConstraintComparisonWithIncompatibleObjects() {
        // DROOLS-858
        String drl =
                "package org.drools.mvel.integrationtests\n"
                + "import java.util.Map.Entry\n"
                + "import java.util.Map\n"
                + "import " + NonStringConstructorClass.class.getCanonicalName() + "\n"
                + "global java.util.List list\n"
                + "rule \"FailOnNonStringConstructor\"\n"
                + "    when \n"
                + "        $map : Map()\n"
                + "        $simpleTestObject : NonStringConstructorClass (something==\"simpleTestObject\")\n"
                + "        Entry (\n"
                + "            getKey() == $simpleTestObject\n"
                + "        ) from $map.entrySet()\n"
                + "    then\n"
                + "        list.add(\"Fired\");\n"
                + "end";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Object> globalList = new ArrayList<>();
        ksession.setGlobal( "list", globalList );

        NonStringConstructorClass simpleTestObject = new NonStringConstructorClass();
        simpleTestObject.setSomething( "simpleTestObject" );

        Map<Object, Object> map = new HashMap<>();
        map.put( "someOtherValue", "someOtherValue" );
        map.put( simpleTestObject, "someValue" );

        List<Object> list = new ArrayList<>();
        ksession.insert( map );
        ksession.insert( simpleTestObject );

        ksession.fireAllRules();

        assertEquals( 1, globalList.size() );
    }

    public static class NonStringConstructorClass {
        private String something;

        public String getSomething() {
            return something;
        }

        public void setSomething( String something ) {
            this.something = something;
        }

        @Override
        public String toString() {
            return "NonStringConstructorClass [something=" + something + "]";
        }
    }

    @Test(timeout = 10000L)
    public void testFireUntilHaltWithForceEagerActivation() throws InterruptedException {
        String drl = "global java.util.List list\n" +
                     "rule \"String detector\"\n" +
                     "    when\n" +
                     "        $s : String( )\n" +
                     "    then\n" +
                     "        list.add($s);\n" +
                     "end";

        KieSessionConfiguration config = KieServices.Factory.get().newKieSessionConfiguration();
        config.setOption( ForceEagerActivationOption.YES );

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                                   .build()
                                                   .newKieSession( config, null );

        final Integer monitor = 42;
        int factsNr = 5;

        List<String> list = new NotifyingList<>(factsNr, new Runnable() {
            @Override
            public void run() {
                synchronized (monitor) {
                    monitor.notifyAll();
                }
            }
        });

        ksession.setGlobal( "list", list );

        // thread for firing until halt
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit((Runnable) ksession::fireUntilHalt);
        try {
            for ( int i = 0; i < factsNr; i++ ) {
                ksession.insert( "" + i );
            }

            // wait for rule to fire
            synchronized (monitor) {
                if ( list.size() < factsNr ) {
                    monitor.wait();
                }
            }

            assertEquals( factsNr, list.size() );
        } finally {
            ksession.halt();
            ksession.dispose();
            executorService.shutdownNow();
        }
    }

    public static class NotifyingList<T> extends ArrayList<T> {
        private final int limit;
        private final Runnable listener;

        public NotifyingList( int limit, Runnable listener ) {
            this.limit = limit;
            this.listener = listener;
        }

        @Override
        public boolean add( T t ) {
            boolean result = super.add( t );
            if ( size() == limit ) {
                listener.run();
            }
            return result;
        }
    }

    public class A1 {
        public B1 b = new B1();
    }

    public class B1 {
        public int b1 = 1;
        public int b2 = 2;
        public int b3 = 3;
    }

    @Test
    public void testSkipHashingOfNestedProperties() {
        // DROOLS-870
        String drl =
                "import " + A1.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule One when\n" +
                "  A1(b.b1 == 1)\n" +
                "then\n" +
                "  list.add(\"1\");\n" +
                "end\n" +
                "\n" +
                "rule \"Two\" when\n" +
                "  A1(b.b2 == 2)\n" +
                "then\n" +
                "  list.add(\"2\");\n" +
                "end\n" +
                "\n" +
                "rule \"Three\" when\n" +
                "  A1(b.b3 == 3)\n" +
                "then\n" +
                "  list.add(\"3\");\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( new A1() );
        ksession.fireAllRules();

        assertEquals( 3, list.size() );
        assertTrue( list.containsAll( asList( "1", "2", "3" ) ) );
    }

    @Test
    public void testErrorReportWithWrongAccumulateFunction() {
        // DROOLS-872
        String drl =
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "  Cheese( $type : typo )\n" +
                "  accumulate(\n" +
                "    $c : Cheese( type == $type ),\n" +
                "    $cheeses : collectList( $c ) );\n" +
                "then\n" +
                "end\n";

        assertDrlHasCompilationError( drl, -1 );
    }

    @Test
    public void testVariableMatchesField() throws Exception {
        // DROOLS-882
        String drl =
                "declare RegEx\n" +
                "    pattern : String\n" +
                "end\n" +
                "declare Fact\n" +
                "    field : String\n" +
                "end\n" +
                "rule \"Variable matches field\"\n" +
                "    when\n" +
                "        Fact( $field : field )\n" +
                "        RegEx( $field matches pattern )\n" +
                "    then\n" +
                "        insert(\"Matched \" + $field);\n" +
                "end\n" +
                "rule \"Boot\"\n" +
                "    when\n" +
                "    then\n" +
                "        insert( new RegEx(\"foo.*\") );\n" +
                "        insert( new Fact(\"foobar\") );\n" +
                "        insert( new Fact(\"bar\") );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testEndMethod() throws Exception {
        // DROOLS-889
        String drl =
                "import " + Pattern.class.getCanonicalName() + "\n" +
                "import " + Matcher.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule \"Variable matches field\" when\n" +
                "    $emailAddress :String(this matches \"^.*[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,}).*$\")\n" +
                "then\n" +
                "    Pattern pattern=Pattern.compile(\"[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})\");\n" +
                "    Matcher matcher=pattern.matcher($emailAddress);\n" +
                "    while(matcher.find()){\n" +
                "        list.add($emailAddress.substring(matcher.start(),matcher.end()));\n" +
                "    }\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( "mario.fusco@test.org" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "mario.fusco@test.org", list.get( 0 ) );
    }

    public static class Parent {
        public String value;

        public String getValue() {
            return value;
        }

        public void setValue( String value ) {
            this.value = value;
        }
    }

    public static class ChildA extends Parent {
    }

    public static class ChildB extends Parent {
    }

    @Test
    public void testDifferentClassesWithOR() throws Exception {
        // DROOLS-897
        String drl =
                "import " + ChildA.class.getCanonicalName() + "\n" +
                "import " + ChildB.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    (\n" +
                "    ChildA(value == null, $entity : this) or \n" +
                "    ChildB(value == null, $entity : this)\n" +
                "    )\n" +
                "then\n" +
                "    modify( $entity ) { setValue(\"Done!\"); }\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ChildA childA = new ChildA();
        ChildB childB = new ChildB();

        ksession.insert( childA );
        ksession.insert( childB );
        ksession.fireAllRules();

        assertEquals( "Done!", childA.getValue() );
        assertEquals( "Done!", childB.getValue() );
    }

    @Test
    public void testJittingCollectionCreation() {
        // DROOLS-900
        String drl =
                "import " + Arrays.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    $s : String( Arrays.asList(\"a\", \"b\", \"c\").contains(this) )\n" +
                "then\n" +
                "    list.add($s);\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( "a" );
        ksession.insert( "d" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "a", list.get( 0 ) );
    }

    @Test
    public void testJittingCollectionCreationInParenthesis() {
        // DROOLS-900
        String drl =
                "import " + Arrays.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    $s : String( (Arrays.asList(\"a\", \"b\", \"c\")).contains(this) )\n" +
                "then\n" +
                "    list.add($s);\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( "a" );
        ksession.insert( "d" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "a", list.get( 0 ) );
    }

    @Test
    @Ignore
    public void testBetaMemoryLeakOnSegmentUnlinking() {
        // DROOLS-915
        String drl =
                "rule R1 when\n" +
                "    $a : Integer(this == 1)\n" +
                "    $b : String()\n" +
                "    $c : Integer(this == 2)\n" +
                "    $d : Integer(this == 3)\n" +
                "then \n" +
                "end\n" +
                "rule R2 when\n" +
                "    $a : Integer(this == 1)\n" +
                "    $b : String()\n" +
                "then \n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        FactHandle fh1 = ksession.insert( 1 );
        FactHandle fh2 = ksession.insert( 2 );
        FactHandle fh3 = ksession.insert( 3 );
        FactHandle fhtest = ksession.insert( "test" );
        ksession.fireAllRules();

        ksession.delete( fh3 );
        ksession.fireAllRules();

        ksession.delete( fh1 );
        ksession.delete( fh2 );
        ksession.delete( fhtest );
        ksession.fireAllRules();

        NodeMemories nodeMemories = ( (InternalWorkingMemory) ksession ).getNodeMemories();
        for ( int i = 0; i < nodeMemories.length(); i++ ) {
            Memory memory = nodeMemories.peekNodeMemory( i );
            if ( memory != null && memory.getSegmentMemory() != null ) {
                SegmentMemory segmentMemory = memory.getSegmentMemory();
                System.out.println( memory );
                LeftTuple deleteFirst = memory.getSegmentMemory().getStagedLeftTuples().getDeleteFirst();
                System.out.println( deleteFirst );
                assertNull( deleteFirst );
            }
        }
    }

    @Test
    public void testFunctionInvokingFunction() throws Exception {
        // DROOLS-926
        final String drl =
                "function boolean isOdd(int i) {\n" +
                "    return i % 2 == 1;\n" +
                "}\n" +
                "\n" +
                "function boolean isEven(int i) {\n" +
                "    return !isOdd(i);\n" +
                "}\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule R when\n" +
                "    $i : Integer( isEven( this ) ) \n" +
                "then\n" +
                "    list.add($i);\n" +
                "end\n";

        final KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();

        final int parallelThreads = 10;
        final ExecutorService executor = Executors.newFixedThreadPool( parallelThreads );
        try {
            final Collection<Callable<Boolean>> solvers = new ArrayList<>();
            for ( int i = 0; i < parallelThreads; ++i ) {
                solvers.add(() -> {
                    final KieSession ksession = kbase.newKieSession();
                    try {
                        final List<Integer> list = new ArrayList<>();
                        ksession.setGlobal( "list", list );

                        for ( int i1 = 0; i1 < 100; i1++ ) {
                            ksession.insert(i1);
                        }
                        ksession.fireAllRules();
                        return list.size() == 50;
                    } finally {
                        ksession.dispose();
                    }
                });
            }

            final CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
            for ( final Callable<Boolean> s : solvers ) {
                ecs.submit( s );
            }
            for ( int i = 0; i < parallelThreads; ++i ) {
                assertTrue( ecs.take().get() );
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void testCompilationFailureWithNonExistingField() {
        // BZ-1271534
        String drl =
                "rule R when\n" +
                "  String( $var : lenght )\n" +
                "then\n" +
                "end\n";

        assertDrlHasCompilationError( drl, 1 );
    }

    @Test
    public void testKieBaseSerialization() throws Exception {
        // DROOLS-944
        String drl =
                "import " + Container.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "    Container($offer : objects[\"1-CZ26IQW\"] != null)\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    Container($offer : objects[\"1-CZ26IR8\"] != null)\n" +
                "then\n" +
                "end\n";

        KieBase kbase1 = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieBase kbase2 = SerializationHelper.serializeObject( kbase1, ( (InternalKnowledgeBase) kbase1 ).getRootClassLoader() );
        assertTrue( ReteComparator.areEqual( kbase1, kbase2 ) );
    }

    public static class Container {
        private Map<String, Object> objects = new HashMap<>();

        public Map<String, Object> getObjects() {
            return objects;
        }

        public void setObjects( Map<String, Object> objects ) {
            this.objects = objects;
        }
    }

    @Test
    public void testPatternMatchingWithFakeImplicitCast() {
        // DROOLS-966
        String drl =
                "rule R1 when\n" +
                "    String( this == \"\\\"#\")\n" +
                "then \n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ksession.insert( "\"#" );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testPatternMatchingWithFakeNullSafe() {
        // DROOLS-966
        String drl =
                "rule R1 when\n" +
                "    String( this == \"\\\"!.\")\n" +
                "then \n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ksession.insert( "\"!." );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testLambdaInRHS() {
        checkJava8InRhs("i -> list.add(i)");
    }

    @Test
    public void testMethodReferenceInRHS() {
        checkJava8InRhs("list::add");
    }

    private void checkJava8InRhs(String expr) {
        assumeTrue(System.getProperty("java.version").startsWith( "1.8" ));

        // BZ-1199965
        String drl =
                "global java.util.List list;\n" +
                "rule \"Example with Lambda expression\"\n" +
                "    when\n" +
                "    then\n" +
                "        java.util.List<Integer> $list = java.util.Arrays.asList(1, 2, 3, 4);\n" +
                "        $list.forEach(" + expr + ");\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        assertEquals( 4, list.size() );
        assertTrue( list.containsAll( Arrays.asList(1, 2, 3, 4) ) );
    }

    @Test
    public void testCompareToOnInterface() {
        // DROOLS-1013
        String drl =
                "import " + StringWrapper.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $s1 : StringWrapper()\n" +
                "    $s2 : StringWrapper( this > $s1 )\n" +
                "then\n" +
                "    list.add($s2.getWrapped());\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( new StringWrapper("aaa") );
        ksession.insert( new StringWrapper("bbb") );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "bbb", list.get( 0 ) );
    }

    @Test
    public void testFromEPDontRequireLeftInput() {
        // DROOLS-1014
        String drl =
                "rule R when\n" +
                "    $s1 : String() from entry-point \"xxx\"\n" +
                "then\n" +
                "end\n";

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ) .build();

        Rete rete = ( (KnowledgeBaseImpl) kbase ).getRete();
        LeftInputAdapterNode liaNode = null;
        for ( ObjectTypeNode otn : rete.getObjectTypeNodes() ) {
            Class<?> otnType = ( (ClassObjectType) otn.getObjectType() ).getClassType();
            if ( String.class == otnType ) {
                assertEquals( 1, otn.getObjectSinkPropagator().size() );
            } else if ( InitialFact.class.isAssignableFrom( otnType ) ) {
                assertEquals( 0, otn.getObjectSinkPropagator().size() );
            } else {
                fail("There shouldn't be other OTNs");
            }
        }
    }

    @Test
    public void testIn() {
        // DROOLS-1037
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $p : Person( name == null || (name in (\"Alice\", \"Charlie\", \"David\"))==false )\n" +
                "then\n" +
                "    list.add($p.getName());\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( new Person("Bob") );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "Bob", list.get( 0 ) );
    }

    @Test
    public void testNonSerializableInEvaluatorWrapper() throws Exception {
        // BZ-1315143
        String str = "package org.drools.mvel.compiler\n" +
                     "rule B\n" +
                     "  when\n" +
                     "    $m1 : Message( $message1 : message, $date1 : birthday )\n" +
                     "    $m2 : Message( this != $m1, message != $message1, birthday after $date1 )\n" +
                     "  then\n" +
                     "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ResourceFactory.newByteArrayResource( str.getBytes() ).setTargetPath( "org/drools/compiler/rules.drl" ) );

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder( kfs );
        kbuilder.buildAll();

        assertEquals( 0, kbuilder.getResults().getMessages().size() );

        KieSession ksession1 = ks.newKieContainer( kbuilder.getKieModule().getReleaseId() ).newKieSession();

        Message message1 = new Message();
        message1.setMessage("Hello World");
        message1.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2015-12-15"));

        Message message2 = new Message();
        message2.setMessage("Goodbye World");
        message2.setBirthday(new SimpleDateFormat( "yyyy-MM-dd").parse( "2015-12-16" ) );

        ksession1.insert(message1);
        ksession1.insert(message2);

        int fired1 = ksession1.fireAllRules();

        assertEquals( 1, fired1 );
        ksession1.dispose();

        // Force deepClone
        KieSession ksession2 = ks.newKieContainer( kbuilder.getKieModule().getReleaseId() ).newKieSession();
        Message message3 = new Message();
        message3.setMessage("Hello World");
        message3.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2015-12-15"));

        Message message4 = new Message();
        message4.setMessage("Goodbye World");
        message4.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2015-12-16"));

        ksession2.insert(message3);
        ksession2.insert(message4);

        int fired2 = ksession2.fireAllRules();

        assertEquals( 1, fired2 );
        ksession2.dispose();
    }

    @Test
    public void testWrongNodeSharingWithSameHashCode() throws IllegalAccessException, InstantiationException {

        String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                 "    String()\n" +
                 "    $p: Person( name == \"ATL\", name != null)\n" +
                 "then \n" +
                 "    $p.setHappy(true);\n" +
                 "end\n" +
                "rule R2 when\n" +
                 "    String()\n" +
                 "    $p: Person( name == \"B5L\", name != null)\n" +
                 "then \n" +
                 "    $p.setHappy(true);\n" +
                 "end\n";

        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        kieSession.insert("test");
        Person b5L = new Person("B5L");
        kieSession.insert(b5L);

        assertFalse(b5L.isHappy());
        kieSession.fireAllRules();
        assertTrue(b5L.isHappy());
    }

    @Test
    public void testWrongVariableNameWithSameDeclarationName() {
        // DROOLS-1064
        String str =
                "declare Parameter end\n" +
                "rule R when\n" +
                "    Parameter($b : $b == 0 )\n" +
                "then\n" +
                "end\n";

        assertDrlHasCompilationError( str, -1 );
    }

    @Test
    public void testComplexEvals() {
        // DROOLS-1139
        String drl =
                "rule R1 when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    not( ( eval($s.length() < 2) and (eval(true) or eval(false))))\n" +
                "then \n" +
                "end\n";

        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        kieSession.insert( 42 );
        kieSession.insert( "test" );
        assertEquals(1, kieSession.fireAllRules());
    }

    @Test
    public void testComplexEvals2() {
        // DROOLS-1139
        String drl =
                "rule R1 when\n" +
                "    $s : String()\n" +
                "    Boolean()\n" +
                "    $i : Integer()" +
                "    and (eval($s.length() > 2)\n" +
                "        or (eval(true) and eval(true)))\n" +
                "    and (eval(true)\n" +
                "         or ( eval($i > 2) and (eval(true))))\n\n" +
                "then \n" +
                "end\n";

        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        kieSession.insert( 42 );
        kieSession.insert( "test" );
        kieSession.insert( true );
        assertEquals(4, kieSession.fireAllRules());
    }

    @Test
    public void testDeletedRightTupleInChangedBucket() {
        // PLANNER-488
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    Person( $name: name, $age: age )\n" +
                "    not Person( happy, name == $name, age == $age-1 )\n" +
                "then\n" +
                "end";

        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        Person p1 = new Person( "C", 1, true );
        Person p2 = new Person( "B", 1, true );
        Person p3 = new Person( "B", 2, true );
        Person p4 = new Person( "A", 2 );

        FactHandle fh1 = kieSession.insert( p1 );
        FactHandle fh2 = kieSession.insert( p2 );
        FactHandle fh3 = kieSession.insert( p3 );
        FactHandle fh4 = kieSession.insert( p4 );

        kieSession.fireAllRules();

        p4.setName( "B" );
        p4.setHappy( true );
        kieSession.update( fh4, p4 );

        kieSession.fireAllRules();

        p3.setName( "A" );
        p3.setHappy( false );
        kieSession.update( fh3, p3 );
        p1.setName( "B" );
        kieSession.update( fh1, p1 );
        p2.setName( "C" );
        kieSession.update( fh2, p2 );

        kieSession.fireAllRules();
    }

    @Test
    public void testJittingFunctionReturningAnInnerClass() {
        // DROOLS-1166
        String drl =
                "import " + java.util.function.Function.class.getCanonicalName() + "\n" +
                "function Function<String, Integer> f() {\n" +
                "    return new Function<String, Integer>() {\n" +
                "        public Integer apply(String s) {\n" +
                "            return s.length();\n" +
                "        }\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "rule R when\n" +
                "    $s : String( f().apply(this) > 3 )\n" +
                "then\n" +
                "end\n";

        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        kieSession.insert( "test" );
        assertEquals( 1, kieSession.fireAllRules() );
    }

    @Test
    public void testQueryWithEnum() {
        // DROOLS-1181
        String drl =
                "import " + AnswerGiver.class.getCanonicalName() + "\n" +
                "import " + Answer.class.getCanonicalName() + "\n" +
                "\n" +
                "declare TestThing end\n" +
                "\n" +
                "query TestQuery(Answer enumVal)\n" +
                "  AnswerGiver( answer == enumVal )\n" +
                "end\n" +
                "\n" +
                "query MyQuery()\n" +
                "  TestQuery(Answer.NO;)\n" +
                "end\n" +
                "\n" +
                "rule R when MyQuery() then end\n";


        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        kieSession.insert( new AnswerGiver() );
        assertEquals( 0, kieSession.fireAllRules() );
    }

    @Test
    public void testOrQueryWithEnum() {
        // DROOLS-1181
        String drl =
                "import " + AnswerGiver.class.getCanonicalName() + "\n" +
                "import " + Answer.class.getCanonicalName() + "\n" +
                "\n" +
                "declare TestThing end\n" +
                "\n" +
                "query TestQuery(Answer enumVal)\n" +
                "  AnswerGiver( answer == enumVal )\n" +
                "end\n" +
                "\n" +
                "query ORQuery()\n" +
                "  (\n" +
                "    TestQuery(Answer.YES;)\n" +
                "  ) or (\n" +
                "    TestQuery(Answer.YES;)\n" +
                "  )\n" +
                "end\n" +
                "\n" +
                "rule R when ORQuery() then end\n";

        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        kieSession.insert( new AnswerGiver() );
        assertEquals( 2, kieSession.fireAllRules() );
    }

    @Test
    public void testModifyWithOr() {
        // DROOLS-1185
        String drl =
                "import " + List.class.getCanonicalName() + "\n" +
                "import " + AtomicBoolean.class.getCanonicalName() + "\n" +
                "\n" +
                "rule R when\n" +
                "  $l : List()\n" +
                "  ( String() from $l\n" +
                "  or\n" +
                "  String() from $l )\n" +
                "  $b : AtomicBoolean( get() )\n" +
                "then" +
                "  modify($b) { set(false) }\n" +
                "end\n";

        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        kieSession.insert( asList("test") );
        kieSession.insert( new AtomicBoolean( true ) );
        assertEquals( 1, kieSession.fireAllRules() );
    }

    @Test
    public void testNormalizeRuleName() {
        // DROOLS-1192
        String drl =
                "rule \"rule（hello）\" when\n" +
                "  Integer()\n" +
                "then\n" +
                "end\n";

        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        kieSession.insert( 1 );
        assertEquals( 1, kieSession.fireAllRules() );
    }

    @Test
    public void testCCEAfterDeserialization() throws Exception {
        // DROOLS-1155
        String drl =
                "function boolean checkLength(int length) { return true; }\n" +
                "rule R dialect \"mvel\" when\n" +
                "    Boolean()" +
                "    String( $length : length )\n" +
                "    eval( checkLength($length) )\n" +
                "    ( Integer( ) or eval( true ) )\n" +
                "then\n" +
                "end";

        KieBase kbase1 = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession1 = kbase1.newKieSession();
        ksession1.insert( true );
        ksession1.insert( "test" );
        assertEquals(1, ksession1.fireAllRules());

        KieBase kbase2 = SerializationHelper.serializeObject( kbase1, ( (InternalKnowledgeBase) kbase1 ).getRootClassLoader() );
        KieSession ksession2 = kbase2.newKieSession();
        ksession2.insert( true );
        ksession2.insert( "test" );
        assertEquals(1, ksession2.fireAllRules());
    }

    @Test
    public void testWiringClassOnPackageMerge() throws Exception {
        String drl_init =
                "package init;\n" +
                "import org.kie.test.TestObject\n" +
                "rule RInit when\n" +
                "then\n" +
                "    TestObject obj1 = new TestObject();\n" +
                "    TestObject obj2 = new TestObject();" +
                "    obj1.add(obj2);" +
                "    insert(obj1);\n" +
                "end";

        String drl1 =
                "package p1;\n" +
                "import org.kie.test.TestObject\n" +
                "global java.util.List list;\n" +
                "rule R1 when\n" +
                "    $obj : TestObject( $objs : objects )\n" +
                "    $s : Object() from $objs\n" +
                "then\n" +
                "    list.add(\"R1\");\n" +
                "end";

        String drl2 =
                "package p2;\n" +
                "import org.kie.test.TestObject\n" +
                "global java.util.List list;\n" +
                "rule R2 when\n" +
                "    $obj : TestObject( $objs : objects )\n" +
                "    $s : TestObject() from $objs\n" +
                "then\n" +
                "    list.add(\"R2\");\n" +
                "end";

        String javaSrc =
                "package org.kie.test;\n" +
                "import java.util.*;\n" +
                "\n" +
                "public class TestObject {\n" +
                "    private final List<TestObject> objects = new ArrayList<TestObject>();\n" +
                "\n" +
                "    public List<TestObject> getObjects() {\n" +
                "        return objects;\n" +
                "    }\n" +
                "    public void add(TestObject obj) {\n" +
                "        objects.add(obj);" +
                "    }" +
                "}\n";

        String path = "org/kie/test/MyRuleUnit";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(ks.newKieModuleModel().toXML())
           .write("src/main/resources/a.drl", drl_init)
           .write("src/main/resources/b.drl", drl1)
           .write("src/main/resources/c.drl", drl2)
           .write("src/main/java/org/kie/test/TestObject.java", javaSrc);

        ks.newKieBuilder( kfs ).buildAll();
        KieContainer kcontainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        KieSession kSession = kcontainer.newKieSession();

        List<String> list = new ArrayList<>();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "R1" ) );
        assertTrue( list.contains( "R2" ) );
    }

    @Test
    public void testReorderRightMemoryOnIndexedField() {
        // DROOLS-1174
        String rule = "import " + Misc2Test.Seat.class.getCanonicalName() + ";\n"
                + "\n"
                + "rule twoSameJobTypePerTable when\n"
                + "    $job: String()\n"
                + "    $table : Long()\n"
                + "    not (\n"
                + "        Seat( guestJob == $job, table == $table, $leftId : id )\n"
                + "        and Seat( guestJob == $job, table == $table, id > $leftId )\n"
                + "    )\n"
                + "then\n"
                + "end";

        KieSession kieSession = new KieHelper().addContent(rule, ResourceType.DRL).build().newKieSession();

        String doctor = "D";
        String politician = "P";
        Long table1 = 1L;
        Long table2 = 2L;
        Seat seat0 = new Seat(0, politician, table2);
        Seat seat1 = new Seat(1, politician, null);
        Seat seat2 = new Seat(2, politician, table2);
        Seat seat3 = new Seat(3, doctor, table1);
        Seat seat4 = new Seat(4, doctor, table1);

        kieSession.insert(seat0);
        FactHandle fh1 = kieSession.insert(seat1);
        FactHandle fh2 = kieSession.insert(seat2);
        FactHandle fh3 = kieSession.insert(seat3);
        kieSession.insert(seat4);
        kieSession.insert(politician);
        kieSession.insert(doctor);
        kieSession.insert(table1);
        kieSession.insert(table2);

        assertEquals(2, kieSession.fireAllRules());

        kieSession.update(fh3, seat3); // no change but the update is necessary to reproduce the bug
        kieSession.update(fh2, seat2.setTable(null));
        kieSession.update(fh1, seat1.setTable(table2));

        assertEquals(0, kieSession.fireAllRules());
    }

    public static class Seat {

        private final int id;
        private final String guestJob;
        private Long table;

        public Seat(int id, String guestJob, Long table) {
            this.id = id;
            this.guestJob = guestJob;
            this.table = table;
        }

        public String getGuestJob() {
            return guestJob;
        }

        public int getId() {
            return id;
        }

        public Long getTable() {
            return table;
        }

        public Seat setTable(Long table) {
            this.table = table;
            return this;
        }
    }

    @Test
    public void testChildLeftTuplesIterationOnLeftUpdate() {
        // DROOLS-1186
        String drl =
                "import " + Shift.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    Shift( $end1: end, $employee: employee )\n" +
                "    Shift( employee == $employee, start > $end1 )\n" +
                "    not Shift( employee == $employee, start > $end1 )\n" +
                "then\n" +
                "end";

        KieSession kieSession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        String o = "o";
        String x = "x";

        Shift shift1 = new Shift(10, 11, o);
        Shift shift2 = new Shift(20, 21, o);
        Shift shift3 = new Shift(30, 31, x);
        Shift shift4 = new Shift(40, 41, o);
        Shift shift5 = new Shift(50, 51, o);
        Shift shift6 = new Shift(60, 61, o);
        Shift shift7 = new Shift(70, 71, o);
        Shift shift8 = new Shift(80, 81, o);

        FactHandle fh1 = kieSession.insert(shift1);
        FactHandle fh2 = kieSession.insert(shift2);
        FactHandle fh3 = kieSession.insert(shift3);
        FactHandle fh4 = kieSession.insert(shift4);
        FactHandle fh5 = kieSession.insert(shift5);
        FactHandle fh6 = kieSession.insert(shift6);
        FactHandle fh7 = kieSession.insert(shift7);
        FactHandle fh8 = kieSession.insert(shift8);

        assertEquals( 0, kieSession.fireAllRules() );

        kieSession.update(fh1, shift1.setEmployee(x));
        kieSession.update(fh4, shift4);
        kieSession.update(fh8, shift8);
        kieSession.update(fh5, shift5.setEmployee(x));
        kieSession.update(fh7, shift7);
        kieSession.update(fh2, shift2.setEmployee(x));
        kieSession.update(fh6, shift6);
        kieSession.update(fh3, shift3.setEmployee(o));

        assertEquals( 0, kieSession.fireAllRules() );

        kieSession.update(fh8, shift8.setEmployee(x));
        kieSession.update(fh4, shift4.setEmployee(x));
        kieSession.update(fh7, shift7.setEmployee(x));
        kieSession.update(fh6, shift6.setEmployee(x));

        assertEquals( 0, kieSession.fireAllRules() );
    }

    public static class Shift {

        private final int start;
        private final int end;
        private String employee;

        public Shift(int start, int end, String employee) {
            this.start = start;
            this.end = end;
            this.employee = employee;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public String getEmployee() {
            return employee;
        }

        public Shift setEmployee(String employee) {
            this.employee = employee;
            return this;
        }

        @Override
        public String toString() {
            return "Shift " + employee + " from " + start + " to " + end;
        }
    }

    @Test
    public void test1187() {
        // DROOLS-1187
        String drl = "import " + Misc2Test.Shift1187.class.getCanonicalName() + "\n"
                + "rule insertEmployeeConsecutiveWeekendAssignmentStart when\n"
                + "    Shift1187(\n"
                + "        weekend == true,\n"
                + "        $employee : employee, employee != null,\n"
                + "        $week : week\n"
                + "    )\n"
                + "    // The first working weekend has no working weekend before it\n"
                + "    not Shift1187(\n"
                + "        weekend == true,\n"
                + "        employee == $employee,\n"
                + "        week == ($week - 1)\n"
                + "    )\n"
                + "then\n"
                + "end";

        KieSession kieSession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        Shift1187 shift1 = new Shift1187(0, 4);
        Shift1187 shift2 = new Shift1187(1, 5);
        Shift1187 shift3 = new Shift1187(2, 6);
        Shift1187 shift4 = new Shift1187(2, 6);
        Shift1187 shift5 = new Shift1187(2, 0);
        Shift1187 shift6 = new Shift1187(3, 0);

        String employeeA = "Sarah";
        String employeeB = "Susan";
        String employeeC = "Fred";

        shift4.setEmployee(employeeB);
        shift5.setEmployee(employeeA);

        FactHandle fh1 = kieSession.insert(shift1);
        FactHandle fh2 = kieSession.insert(shift2);
        FactHandle fh3 = kieSession.insert(shift3);
        FactHandle fh4 = kieSession.insert(shift4);
        FactHandle fh5 = kieSession.insert(shift5);
        FactHandle fh6 = kieSession.insert(shift6);

        assertEquals(2, kieSession.fireAllRules());

        kieSession.update(fh6, shift6.setEmployee(employeeA));
        kieSession.update(fh1, shift1.setEmployee(employeeA));
        kieSession.update(fh2, shift2.setEmployee(employeeC));

        assertEquals(1, kieSession.fireAllRules());

        kieSession.update(fh4, shift4.setEmployee(employeeB));
        kieSession.update(fh3, shift3.setEmployee(employeeB));
        kieSession.update(fh5, shift5.setEmployee(employeeB));
        kieSession.update(fh2, shift2.setEmployee(employeeA));
        kieSession.update(fh4, shift4.setEmployee(employeeA));

        kieSession.fireAllRules();
    }
    
   

    public class Shift1187 {

        private final int week;
        private final int dayOfWeek;
        private String employee;

        public Shift1187(int week, int dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            this.week = week;
        }

        public String getEmployee() {
            return employee;
        }

        public Shift1187 setEmployee(String employee) {
            this.employee = employee;
            return this;
        }

        public int getWeek() {
            return week;
        }

        public boolean isWeekend() {
            if (employee == null) {
                return false;
            }
            return dayOfWeek == 6 || dayOfWeek == 0 || dayOfWeek == 5 && hasWeekendOnFriday(employee);
        }

        private boolean hasWeekendOnFriday(String employee) {
            return employee.startsWith("F");
        }
    }

    @Test
    public void testReportFailingConstraintOnError() {
        // DROOLS-1071
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    Person( name.startsWith(\"A\") )\n" +
                "then\n" +
                "end";

        KieSession kieSession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();
        for (int i = 0; i < 100; i++) {
            kieSession.insert( new Person( "A"+i ) );
        }
        kieSession.fireAllRules();

        kieSession.insert( new Person( null ) );
        try {
            kieSession.fireAllRules();
            fail("Evaluation with null must throw a NPE");
        } catch (Exception e) {
            assertTrue( e.getMessage().contains( "name.startsWith(\"A\")" ) );
        }
    }

    public static class TestObject {

        private final Integer value;

        public TestObject(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

    }

    @Test
    public void testNpeInLessThanComparison() {
        // RHBRMS-2462
        String drl = "package com.sample\n"
                     + "import " + TestObject.class.getCanonicalName() + ";\n"
                     + "global java.util.List list\n"
                     + "rule LessThanCompare when\n"
                     + "    TestObject( $value : value )"
                     + "    TestObject( value < $value )"
                     + "then\n"
                     + "    list.add(drools.getRule().getName() + \":\" + $value);\n"
                     + "end\n"
                     + "\n"
                     + "rule GreaterThanCompare when\n"
                     + "    TestObject( $value : value )\n"
                     + "    TestObject( $value > value )\n"
                     + "then\n"
                     + "    list.add(drools.getRule().getName() + \":\" + $value);\n"
                     + "end\n"
                     + "\n"
                     + "rule NotLessThanCompare when\n"
                     + "    TestObject( $value : value )"
                     + "    not ( TestObject( value < $value ) )"
                     + "then\n"
                     + "    list.add(drools.getRule().getName() + \":\" + $value);\n"
                     + "end\n"
                     + "\n"
                     + "rule NotGreaterThanCompare when\n"
                     + "    TestObject( $value : value )\n"
                     + "    not ( TestObject( $value > value ) )\n"
                     + "then\n"
                     + "    list.add(drools.getRule().getName() + \":\" + $value);\n"
                     + "end";

        KieSession kSession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        List<String> list = new ArrayList<>();
        kSession.setGlobal( "list", list );

        kSession.insert(new TestObject(null));
        kSession.insert(new TestObject(5));

        kSession.fireAllRules();

        assertEquals( 4, list.size() );
        assertTrue( list.contains( "NotLessThanCompare:5" ) );
        assertTrue( list.contains( "NotLessThanCompare:null" ) );
        assertTrue( list.contains( "NotGreaterThanCompare:5" ) );
        assertTrue( list.contains( "NotGreaterThanCompare:null" ) );
        assertFalse( list.contains( "LessThanCompare:5" ) );
        assertFalse( list.contains( "LessThanCompare:null" ) );
        assertFalse( list.contains( "GreaterThanCompare:5" ) );
        assertFalse( list.contains( "GreaterThanCompare:null" ) );
    }
    
    @Test
    public void testUnderscoreDoubleMultiplicationCastedToInt() {
        // DROOLS-1420
        String str =
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "  Cheese( $p : price)\n" +
                "then\n" +
                "  int b = (int) ($p * 1_000.0);\n" +
                "  list.add(\"\" + b);" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL).build().newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( new Cheese( "gauda", 42 ) );
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("42000", list.get(0));
    }
    
    /**
     * This test deliberately creates a deadlock, failing the test with a timeout.
     * Helpful to test thread dump when a timeout occur on the JUnit listener.
     * @throws Exception
     */
    @Ignore("This test deliberately creates a deadlock, failing the test with a timeout.\n" + 
            "Helpful to test thread dump when a timeout occur on the JUnit listener.\n" + 
            "See org.kie.test.util.TestStatusListener#testFailure()")
    @Test(timeout=5_000L)
    public void testDeadlock() {
        Object lock1 = 1L;
        Object lock2 = 2L;
        Runnable task1 = () -> {
            synchronized(lock1) {
              try { Thread.sleep(50); } catch (InterruptedException e) {}
              synchronized(lock2) {
              }
            }
        };
        Runnable task2 = () -> {
            synchronized(lock2) {
              try { Thread.sleep(50); } catch (InterruptedException e) {}
              synchronized(lock1) {
              }
            }
        };
        new Thread(task1).start();
        task2.run();
    }

    public static class ElementOperation {
        private AbstractElement element;
        public ElementOperation(AbstractElement element) {
            this.element = element;
        }
        public AbstractElement getElement() {
            return element;
        }
    }
    public static abstract class AbstractElement {
    }
    public static interface MyInterface {
        public void nothing();
    }
    public static class MyElement extends AbstractElement implements MyInterface {
        @Override
        public void nothing() {}
    }
    
    @Test
    public void test01841522() {
        String str = "package com.sample\n" +
                     "import " + ElementOperation.class.getCanonicalName() + ";\n" +
                     "import " + AbstractElement.class.getCanonicalName() + ";\n" +
                     "import " + MyInterface.class.getCanonicalName() + ";\n" +
                     "global java.util.List list\n" +
                     "rule R when\n" +
                     "  ElementOperation( $e : element )      \n" +
                     "  $my: MyInterface( ) from $e           \n" +
                     "then\n" +
                     "  list.add(\"\" );" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent( str, ResourceType.DRL ).build().newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( new ElementOperation( new MyElement() ) );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    @Ignore("This test is supposed to cause a StackOverflow inside mvel but this not always happens")
    public void testStackOverflowInMvel() {
        // DROOLS-1542
        String str1 = "import " + Person.class.getName() + ";\n" +
                     "rule R1 when\n" +
                     " $p : Person( ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i ++) {
            sb.append("name == \"John-" + i + "\" || " );
        }
        String str2 = " age == 20 )\n" +
                     "then end\n";

        String drl = str1 + sb.toString() + str2;

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );

        assertTrue( kbuilder.getErrors().toString().contains( "StackOverflowError" ) );
    }

    @Test
    public void testMergeMVELDialect() {
        // DROOLS-1751
        String drl1 = "package com.sample\n" +
                "import org.drools.mvel.compiler.*;\n" +
                "rule rule1 \n" +
                "    when\n" +
                "        (PersonHolder($addresses : person.addresses))\n" +
                "            &&\n" +
                "        (Address (street == \"AAA\") from $addresses)\n" +
                "    then\n" +
                "end";

        KnowledgeBuilder kbuilder1 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder1.add(ResourceFactory.newByteArrayResource(drl1.getBytes()), ResourceType.DRL);
        Collection<KiePackage> knowledgePackages1 = kbuilder1.getKnowledgePackages();

        String drl2 = "package com.sample\n" +
                "import org.drools.mvel.compiler.*;\n" +
                "rule rule2 \n" +
                "    when\n" +
                "        PersonHolder()\n" +
                "    then\n" +
                "end";

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add(ResourceFactory.newByteArrayResource(drl2.getBytes()), ResourceType.DRL);
        Collection<KiePackage> knowledgePackages2 = kbuilder2.getKnowledgePackages();

        InternalKnowledgeBase kbase1 = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KiePackage> combinedPackages1 = new ArrayList<>();
        combinedPackages1.addAll(knowledgePackages1);
        combinedPackages1.addAll(knowledgePackages2);
        kbase1.addPackages(combinedPackages1); // Add once to make inUse=true

        InternalKnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KiePackage> combinedPackages2 = new ArrayList<>();
        combinedPackages2.addAll(knowledgePackages1);
        combinedPackages2.addAll(knowledgePackages2);
        kbase2.addPackages(combinedPackages2); // this will cause package deepClone

        KieSession ksession = kbase2.newKieSession();

        PersonHolder personHolder = new PersonHolder();
        Person person = new Person("John");
        Address address = new Address("AAA", "BBB", "111");
        person.addAddress(address);
        personHolder.setPerson(person);

        ksession.insert(personHolder);
        int fired = ksession.fireAllRules();

        assertEquals(2, fired);
    }

    @Test
    public void testCollectWithEagerActivation() {
        // DROOLS-4468
        String drl =
                "import java.util.ArrayList\n" +
                "import " +  FactWithList.class.getCanonicalName() + "\n" +
                "import " +  FactWithString.class.getCanonicalName() + "\n" +
                "\n" +
                "dialect \"mvel\"\n" +
                "global java.util.List list; \n" +
                "\n" +
                " rule \"Init\"\n" +
                " when\n" +
                "     $fl: FactWithList(items.size()==0)\n" +
                " then\n" +
                "     $fl.getItems().add(\"A\");\n" +
                "     $fl.getItems().add(\"B\");\n" +
                "     update($fl);\n" +
                " end\n" +
                "\n" +
                " rule \"R1\"\n" +
                " when\n" +
                "     $fl: FactWithList($itemList : items)\n" +
                "     $l: java.util.List(size > 0) from collect(FactWithString($itemList contains stringValue));\n" +
                " then\n" +
                "      list.add(\"R1\"); \n" +
                " end\n" +
                "\n" +
                " rule \"R2\"\n" +
                " when\n" +
                "     $fl: FactWithList($itemList : items)\n" +
                "     not( FactWithString($itemList contains stringValue) )\n" +
                " then\n" +
                "      list.add(\"R2\"); \n" +
                " end";

        KieSessionConfiguration config = KieServices.Factory.get().newKieSessionConfiguration(null);
        config.setOption( ForceEagerActivationOption.YES );

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession( config, null );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert(new FactWithString("A"));
        ksession.insert(new FactWithList());

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue(list.contains("R1"));
    }

    @Test
    public void testModifyAddToList() {
        // DROOLS-4447
        String str =
                "import " + Address.class.getCanonicalName() + "\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule addAddress\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "  not(Address(street==\"Main Street\") from $p.addresses)" +
                "then\n" +
                "    Address address = new Address(\"Main Street\");\n" +
                "    modify($p) { addresses.add(address) }\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        Person martin = new Person( "Martin" );

        ksession.insert( martin );
        ksession.fireAllRules();

        assertEquals( 1, martin.getAddresses().size() );
    }

    @Test
    public void testKieHelperReleaseId() throws Exception {
        String drl =
                "rule R when\n" +
                     "    $s: String()" +
                     "then\n" +
                     "end";

        ReleaseId releaseId = KieServices.get().newReleaseId("org.sample", "test", "1.0.0");
        KieContainer kieContainer = new KieHelper().addContent(drl, ResourceType.DRL)
                                                   .setReleaseId(releaseId)
                                                   .getKieContainer(null);

        assertEquals(releaseId, kieContainer.getReleaseId());

        KieSession ksession = kieContainer.newKieSession();

        ksession.insert("Hello");
        assertEquals(1, ksession.fireAllRules());
    }
}