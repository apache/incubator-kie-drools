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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.ActivationListenerFactory;
import org.drools.Address;
import org.drools.Attribute;
import org.drools.Cat;
import org.drools.Cell;
import org.drools.Cheese;
import org.drools.CheeseEqual;
import org.drools.Cheesery;
import org.drools.Cheesery.Maturity;
import org.drools.Child;
import org.drools.ClassObjectFilter;
import org.drools.DomainObjectHolder;
import org.drools.FactA;
import org.drools.FactB;
import org.drools.FactC;
import org.drools.FactHandle;
import org.drools.FirstClass;
import org.drools.FromTestClass;
import org.drools.Guess;
import org.drools.IndexedNumber;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Message;
import org.drools.MockPersistentSet;
import org.drools.Move;
import org.drools.ObjectWithSet;
import org.drools.Order;
import org.drools.OrderItem;
import org.drools.OuterClass;
import org.drools.Person;
import org.drools.PersonFinal;
import org.drools.PersonInterface;
import org.drools.PersonWithEquals;
import org.drools.Pet;
import org.drools.PolymorphicFact;
import org.drools.Primitives;
import org.drools.RandomNumber;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SecondClass;
import org.drools.Sensor;
import org.drools.SpecialString;
import org.drools.State;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.StockTick;
import org.drools.TestParam;
import org.drools.Win;
import org.drools.WorkingMemory;
import org.drools.base.RuleNameEndsWithAgendaFilter;
import org.drools.base.RuleNameEqualsAgendaFilter;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.base.RuleNameStartsWithAgendaFilter;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.DefaultAgenda;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilder.PackageMergeException;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.ParserError;
import org.drools.compiler.xml.XmlDumper;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.definition.type.FactType;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.DefaultWorkingMemoryEventListener;
import org.drools.event.ObjectInsertedEvent;
import org.drools.event.ObjectRetractedEvent;
import org.drools.event.ObjectUpdatedEvent;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.TerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.InvalidRulePackage;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.Globals;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.ConsequenceExceptionHandler;
import org.drools.spi.GlobalResolver;
import org.drools.spi.PropagationContext;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Run all the tests with the ReteOO engine implementation
 */
public class MiscTest {

    protected RuleBase getRuleBase() throws Exception {

        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setMultithreadEvaluation( false );
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    protected RuleBase getRuleBase( final RuleBaseConfiguration config ) throws Exception {

        //config.setPartitionsEnabled( true );
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    @Test
    public void testImportFunctions() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ImportFunctions.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();
        final Cheese cheese = new Cheese( "stilton",
                                          15 );
        session.insert( cheese );
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        int fired = session.fireAllRules();

        list = (List) session.getGlobal( "list" );

        assertEquals( 4,
                      fired );
        assertEquals( 4,
                      list.size() );

        assertEquals( "rule1",
                      list.get( 0 ) );
        assertEquals( "rule2",
                      list.get( 1 ) );
        assertEquals( "rule3",
                      list.get( 2 ) );
        assertEquals( "rule4",
                      list.get( 3 ) );
    }

    @Test
    public void testStaticFieldReference() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_StaticField.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        // will test serialisation of int and typesafe enums tests
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheesery cheesery1 = new Cheesery();
        cheesery1.setStatus( Cheesery.SELLING_CHEESE );
        cheesery1.setMaturity( Maturity.OLD );
        session.insert( cheesery1 );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        final Cheesery cheesery2 = new Cheesery();
        cheesery2.setStatus( Cheesery.MAKING_CHEESE );
        cheesery2.setMaturity( Maturity.YOUNG );
        session.insert( cheesery2 );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        session.fireAllRules();

        assertEquals( 2,
                      list.size() );

        assertEquals( cheesery1,
                      list.get( 0 ) );
        assertEquals( cheesery2,
                      list.get( 1 ) );
    }

    @Test
    public void testMetaConsequence() throws Exception {
        final Package pkg = loadPackage( "test_MetaConsequence.drl" );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();
        List results = new ArrayList();
        session.setGlobal( "results",
                           results );

        session.insert( new Person( "Michael" ) );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        results = (List) session.getGlobal( "results" );

        session.fireAllRules();
        assertEquals( 2,
                      results.size() );
        assertEquals( "bar",
                      (results.get( 0 )) );
        assertEquals( "bar2",
                      (results.get( 1 )) );

    }

    @Test
    public void testEnabledExpression() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_enabledExpression.drl" ) ) );
        final Package pkg = builder.getPackage();

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();
        List results = new ArrayList();
        session.setGlobal( "results",
                           results );

        session.insert( new Person( "Michael" ) );

        //        session = SerializationHelper.getSerialisedStatefulSession( session,
        //                                                                    ruleBase );
        results = (List) session.getGlobal( "results" );

        session.fireAllRules();
        assertEquals( 3,
                      results.size() );
        assertTrue( results.contains( "1" ) );
        assertTrue( results.contains( "2" ) );
        assertTrue( results.contains( "3" ) );

    }

    @Test
    public void testGetStatefulKnowledgeSessions() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "empty.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession_1 = kbase.newStatefulKnowledgeSession();
        String expected_1 = "expected_1";
        String expected_2 = "expected_2";
        org.drools.runtime.rule.FactHandle handle_1 = ksession_1.insert( expected_1 );
        org.drools.runtime.rule.FactHandle handle_2 = ksession_1.insert( expected_2 );
        ksession_1.fireAllRules();
        Collection<StatefulKnowledgeSession> coll_1 = kbase.getStatefulKnowledgeSessions();
        assertTrue( coll_1.size() == 1 );

        StatefulKnowledgeSession ksession_2 = coll_1.iterator().next();
        Object actual_1 = ksession_2.getObject( handle_1 );
        Object actual_2 = ksession_2.getObject( handle_2 );
        assertEquals( expected_1,
                      actual_1 );
        assertEquals( expected_2,
                      actual_2 );

        ksession_1.dispose();
        Collection<StatefulKnowledgeSession> coll_2 = kbase.getStatefulKnowledgeSessions();
        assertTrue( coll_2.size() == 0 );

        // here to make sure it's safe to call dispose() twice
        ksession_1.dispose();
        Collection<StatefulKnowledgeSession> coll_3 = kbase.getStatefulKnowledgeSessions();
        assertTrue( coll_3.size() == 0 );
    }

    @Test
    public void testPrimitiveArray() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_primitiveArray.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();
        List result = new ArrayList();
        session.setGlobal( "result",
                           result );

        final Primitives p1 = new Primitives();
        p1.setPrimitiveIntArray( new int[]{1, 2, 3} );
        p1.setArrayAttribute( new String[]{"a", "b"} );

        session.insert( p1 );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        result = (List) session.getGlobal( "result" );

        session.fireAllRules();
        assertEquals( 3,
                      result.size() );
        assertEquals( 3,
                      ((Integer) result.get( 0 )).intValue() );
        assertEquals( 2,
                      ((Integer) result.get( 1 )).intValue() );
        assertEquals( 3,
                      ((Integer) result.get( 2 )).intValue() );

    }

    @Test
    public void testMVELSoundex() throws Exception {

        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "MVEL_soundex.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );

        Cheese c = new Cheese( "fubar",
                               2 );

        ksession.insert( c );
        ksession.fireAllRules();
        assertEquals( 42,
                      c.getPrice() );
    }

    @Test
    public void testMVELSoundexNoCharParam() throws Exception {

        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "MVEL_soundexNPE2500.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                                      true );

        Cheese foobarCheese = new Cheese( "foobar",
                                          2 );
        Cheese nullCheese = new Cheese( null,
                                        2 );
        Cheese starCheese = new Cheese( "*",
                                        2 );

        ksession.insert( foobarCheese );
        ksession.insert( nullCheese );
        ksession.insert( starCheese );
        ksession.fireAllRules();
        assertEquals( 42,
                              foobarCheese.getPrice() );
        assertEquals( 2,
                      nullCheese.getPrice() );
        assertEquals( 2,
                      starCheese.getPrice() );
    }

    @Test
    public void testMVELRewrite() throws Exception {

        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_MVELrewrite.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );
        List results = new ArrayList();
        ksession.setGlobal( "results",
                            results );

        Cheese brie = new Cheese( "brie",
                                  2 );
        Cheese stilton = new Cheese( "stilton",
                                     2 );
        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( brie );
        cheesery.addCheese( stilton );

        ksession.insert( cheesery );
        ksession.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( cheesery,
                      results.get( 0 ) );
    }

    @Test
    public void testMissingImport() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "import org.drools.Person\n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "when \n";
        str += "    $i : Cheese() \n";
        str += "         MissingClass( fieldName == $i ) \n";
        str += "then \n";
        str += "    list.add( $i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.err.println( kbuilder.getErrors() );
        }
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testInvalidModify1() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "import org.drools.Person\n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    no-loop \n";
        str += "when \n";
        str += "    $i : Cheese() \n";
        str += "then \n";
        str += "    modify( $i ); ";
        str += "    list.add( $i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.err.println( kbuilder.getErrors() );
        }
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testInvalidModify2() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "import org.drools.Person\n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    no-loop \n";
        str += "when \n";
        str += "    $i : Cheese() \n";
        str += "then \n";
        str += "    modify( $i ) { setType( \"stilton\" ); setType( \"stilton\" );}; ";
        str += "    list.add( $i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.err.println( kbuilder.getErrors() );
        }
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testIncrementOperator() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "    $I : Integer() \n";
        str += "then \n";
        str += "    int i = $I.intValue(); \n";
        str += "    i += 5; \n";
        str += "    list.add( i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.err.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( 5 );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertEquals( 10,
                      list.get( 0 ) );
    }

    @Test
    public void testKnowledgeRuntimeAccess() throws Exception {
        String str = "";
        str += "package org.test\n";
        str += "import org.drools.Message\n";
        str += "rule \"Hello World\"\n";
        str += "when\n";
        str += "    Message( )\n";
        str += "then\n";
        str += "    System.out.println( drools.getKnowledgeRuntime() );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Message( "help" ) );
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testEvalWithBigDecimal() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "import java.math.BigDecimal; \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "    $bd : BigDecimal() \n";
        str += "    eval( $bd.compareTo( BigDecimal.ZERO ) > 0 ) \n";
        str += "then \n";
        str += "    list.add( $bd ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.err.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new BigDecimal( 1.5 ) );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertEquals( new BigDecimal( 1.5 ),
                      list.get( 0 ) );
    }

    @Test
    public void testCustomGlobalResolver() throws Exception {
        final Package pkg = loadPackage( "test_globalCustomResolver.drl" );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Map map = new HashMap();
        List list = new ArrayList();
        String string = "stilton";

        map.put( "list",
                 list );
        map.put( "string",
                 string );

        workingMemory.setGlobalResolver( new GlobalResolver() {
            public void readExternal( ObjectInput in ) throws IOException,
                                                      ClassNotFoundException {
            }

            public void writeExternal( ObjectOutput out ) throws IOException {
            }

            public Object resolveGlobal( String identifier ) {
                return map.get( identifier );
            }

            public void setGlobal( String identifier,
                                   Object value ) {
                map.put( identifier,
                         value );
            }

            public Object get( String identifier ) {
                return resolveGlobal( identifier );
            }

            public void set( String identifier,
                             Object value ) {
                setGlobal( identifier,
                           value );
            }

            public void setDelegate( Globals delegate ) {
            }

        } );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( 1,
                      list.size() );

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );
    }

    @Test
    public void testCustomGlobalResolverWithWorkingMemoryObject() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_globalCustomResolver.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Map map = new HashMap();
        List list = new ArrayList();
        String string = "stilton";

        map.put( "list",
                 list );
        map.put( "string",
                 string );

        workingMemory.setGlobalResolver( new GlobalResolver() {
            public Object resolveGlobal( String identifier ) {
                return map.get( identifier );
            }

            public void setGlobal( String identifier,
                                   Object value ) {
                map.put( identifier,
                         value );
            }

            public void readExternal( ObjectInput in ) throws IOException,
                                                      ClassNotFoundException {
            }

            public void writeExternal( ObjectOutput out ) throws IOException {
            }

            public Object get( String identifier ) {
                return resolveGlobal( identifier );
            }

            public void set( String identifier,
                             Object value ) {
                setGlobal( identifier,
                           value );
            }

            public void setDelegate( Globals delegate ) {
            }
        } );

        Cheese bree = new Cheese();
        bree.setPrice( 100 );

        workingMemory.insert( bree );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );

        assertEquals( new Integer( 6 ),
                      list.get( 1 ) );
    }

    @Test
    public void testFieldBiningsAndEvalSharing() throws Exception {
        final String drl = "test_FieldBindingsAndEvalSharing.drl";
        evalSharingTest( drl );
    }

    @Test
    public void testFieldBiningsAndPredicateSharing() throws Exception {
        final String drl = "test_FieldBindingsAndPredicateSharing.drl";
        evalSharingTest( drl );
    }

    private void evalSharingTest( final String drl ) throws DroolsParserException,
                                                    IOException,
                                                    Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( drl ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final TestParam tp1 = new TestParam();
        tp1.setValue2( "boo" );
        session.insert( tp1 );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
    }

    @Test
    public void testGeneratedBeans1() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_GeneratedBeans.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgePackage kpkg = kbuilder.getKnowledgePackages().toArray( new KnowledgePackage[1] )[0];
        assertEquals( 2,
                      kpkg.getRules().size() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // test kbase serialization
        kbase = SerializationHelper.serializeObject( kbase );

        // Retrieve the generated fact type
        FactType cheeseFact = kbase.getFactType( "org.drools.generatedbeans",
                                                 "Cheese" );

        // Create a new Fact instance
        Object cheese = cheeseFact.newInstance();

        // Set a field value using the more verbose method chain...
        // should we add short cuts?
        //        cheeseFact.getField( "type" ).set( cheese,
        //                                           "stilton" );

        cheeseFact.set( cheese,
                        "type",
                        "stilton" );
        assertEquals( "stilton",
                      cheeseFact.get( cheese,
                                      "type" ) );

        FactType personType = kbase.getFactType( "org.drools.generatedbeans",
                                                 "Person" );

        Object ps = personType.newInstance();
        personType.set( ps,
                        "age",
                        42 );

        Map<String, Object> personMap = personType.getAsMap( ps );
        assertEquals( 42,
                      personMap.get( "age" ) );

        personMap.put( "age",
                       43 );
        personType.setFromMap( ps,
                               personMap );

        assertEquals( 43,
                      personType.get( ps,
                                      "age" ) );

        // just documenting toString() result:
        //        assertEquals( "Cheese( type=stilton )",
        //                      cheese.toString() );

        // reading the field attribute, using the method chain
        assertEquals( "stilton",
                      cheeseFact.getField( "type" ).get( cheese ) );

        // creating a stateful session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Object cg = cheeseFact.newInstance();
        ksession.setGlobal( "cg",
                            cg );
        List<Object> result = new ArrayList<Object>();
        ksession.setGlobal( "list",
                            result );

        // inserting fact
        ksession.insert( cheese );

        // firing rules
        ksession.fireAllRules();

        // checking results
        assertEquals( 1,
                      result.size() );
        assertEquals( new Integer( 5 ),
                      result.get( 0 ) );

        // creating a person that likes the cheese:
        // Retrieve the generated fact type
        FactType personFact = kbase.getFactType( "org.drools.generatedbeans",
                                                 "Person" );

        // Create a new Fact instance
        Object person = personFact.newInstance();

        // Set a field value using the more verbose method chain...
        // should we add short cuts?
        personFact.getField( "likes" ).set( person,
                                            cheese );
        // demonstrating primitive type support
        personFact.getField( "age" ).set( person,
                                          7 );

        // just documenting toString() result:
        //        assertEquals( "Person( age=7, likes=Cheese( type=stilton ) )",
        //                      person.toString() );

        // inserting fact
        ksession.insert( person );

        // firing rules
        ksession.fireAllRules();

        // checking results
        assertEquals( 2,
                      result.size() );
        assertEquals( person,
                      result.get( 1 ) );

    }

    @Test
    public void testGeneratedBeansMVEL() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_GeneratedBeansMVEL.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgePackage kpkg = kbuilder.getKnowledgePackages().toArray( new KnowledgePackage[1] )[0];
        assertEquals( 1,
                      kpkg.getRules().size() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // test kbase serialization
        kbase = SerializationHelper.serializeObject( kbase );

        // Retrieve the generated fact type
        FactType pf = kbase.getFactType( "mortgages",
                                         "Applicant" );
        FactType af = kbase.getFactType( "mortgages",
                                         "LoanApplication" );

        Object person = pf.newInstance();
        pf.set( person,
                "creditRating",
                "OK" );

        Object application = af.newInstance();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert( person );
        ksession.insert( application );

        ksession.fireAllRules();
    }

    @Test
    public void testGeneratedBeans2() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_GeneratedBeans2.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgePackage kpkg = kbuilder.getKnowledgePackages().toArray( new KnowledgePackage[1] )[0];
        assertEquals( 2,
                      kpkg.getRules().size() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // test kbase serialization
        kbase = SerializationHelper.serializeObject( kbase );

        // Retrieve the generated fact type
        FactType cheeseFact = kbase.getFactType( "org.drools.generatedbeans",
                                                 "Cheese" );

        // Create a new Fact instance
        Object cheese = cheeseFact.newInstance();

        cheeseFact.set( cheese,
                        "type",
                        "stilton" );
        assertEquals( "stilton",
                      cheeseFact.get( cheese,
                                      "type" ) );

        // testing equals method
        Object cheese2 = cheeseFact.newInstance();
        cheeseFact.set( cheese2,
                        "type",
                        "stilton" );
        assertEquals( cheese,
                      cheese2 );

        FactType personType = kbase.getFactType( "org.drools.generatedbeans",
                                                 "Person" );

        Object ps = personType.newInstance();
        personType.set( ps,
                        "name",
                        "mark" );
        personType.set( ps,
                        "last",
                        "proctor" );
        personType.set( ps,
                        "age",
                        42 );

        Object ps2 = personType.newInstance();
        personType.set( ps2,
                        "name",
                        "mark" );
        personType.set( ps2,
                        "last",
                        "proctor" );
        personType.set( ps2,
                        "age",
                        30 );

        assertEquals( ps,
                      ps2 );

        personType.set( ps2,
                        "last",
                        "little" );

        assertFalse( ps.equals( ps2 ) );

        // creating a stateful session
        StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
        Object cg = cheeseFact.newInstance();
        wm.setGlobal( "cg",
                      cg );
        List result = new ArrayList();
        wm.setGlobal( "list",
                      result );

        // inserting fact
        wm.insert( cheese );

        // firing rules
        wm.fireAllRules();

        // checking results
        assertEquals( 1,
                      result.size() );
        assertEquals( new Integer( 5 ),
                      result.get( 0 ) );

        // creating a person that likes the cheese:
        // Retrieve the generated fact type
        FactType personFact = kbase.getFactType( "org.drools.generatedbeans",
                                                 "Person" );

        // Create a new Fact instance
        Object person = personFact.newInstance();

        // Set a field value using the more verbose method chain...
        // should we add short cuts?
        personFact.getField( "likes" ).set( person,
                                            cheese );
        // demonstrating primitive type support
        personFact.getField( "age" ).set( person,
                                          7 );

        // just documenting toString() result:
        //        assertEquals( "Person( age=7, likes=Cheese( type=stilton ) )",
        //                      person.toString() );

        // inserting fact
        wm.insert( person );

        // firing rules
        wm.fireAllRules();

        // checking results
        assertEquals( 2,
                      result.size() );
        assertEquals( person,
                      result.get( 1 ) );

    }

    @Test
    public void testDeclaredFactAndFunction() throws Exception {
        String rule = "package com.jboss.qa;\n";
        rule += "global java.util.List list\n";
        rule += "declare Address\n";
        rule += "    street: String\n";
        rule += "end\n";
        rule += "function void myFunction() {\n";
        rule += "}\n";
        rule += "rule \"r1\"\n";
        rule += "    dialect \"mvel\"\n";
        rule += "when\n";
        rule += "    Address()\n";
        rule += "then\n";
        rule += "    list.add(\"r1\");\n";
        rule += "end\n";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        FactType addressFact = ruleBase.getFactType( "com.jboss.qa.Address" );
        Object address = addressFact.newInstance();
        session.insert( address );
        session.fireAllRules();

        list = (List) session.getGlobal( "list" );
        assertEquals( 1,
                      list.size() );

        assertEquals( "r1",
                      list.get( 0 ) );
    }

    @Test
    public void testNullHandling() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NullHandling.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        final Cheese nullCheese = new Cheese( null,
                                              2 );
        session.insert( nullCheese );

        final Person notNullPerson = new Person( "shoes butt back" );
        notNullPerson.setBigDecimal( new BigDecimal( "42.42" ) );

        session.insert( notNullPerson );

        Person nullPerson = new Person( "whee" );
        nullPerson.setBigDecimal( null );

        session.insert( nullPerson );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal("list")).get(0));
        assertEquals( 3,
                      ((List) session.getGlobal( "list" )).size() );

        nullPerson = new Person( null );

        session.insert( nullPerson );
        session.fireAllRules();
        assertEquals( 4,
                      ((List) session.getGlobal( "list" )).size() );

    }

    @Test
    public void testNullFieldOnCompositeSink() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_NullFieldOnCompositeSink.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        ksession.insert( new Attribute() );
        ksession.insert( new Message() );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );
        ksession.fireAllRules();

        assertEquals( 1,
                      ((List) ksession.getGlobal( "list" )).size() );
        assertEquals( "X",
                      ((List) ksession.getGlobal( "list" )).get( 0 ) );

    }

    @Test
    public void testEmptyPattern() throws Exception {
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EmptyPattern.drl" ) ) );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        session.insert( stilton );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( 5,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testExplicitAnd() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_ExplicitAnd.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Message( "hola" ) );

        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() );

        ksession.insert( new Cheese( "brie",
                                     33 ) );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );
        ksession.fireAllRules();
        assertEquals( 1,
                      ((List) ksession.getGlobal( "list" )).size() );
    }

    @Test
    public void testHelloWorld() throws Exception {
        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "HelloWorld.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        // go !
        final Message message = new Message( "hola" );
        message.addToList( "hello" );
        message.setNumber( 42 );

        ksession.insert( message );
        ksession.insert( "boo" );
        //        workingMemory    = SerializationHelper.serializeObject(workingMemory);
        ksession.fireAllRules();
        assertTrue( message.isFired() );
        assertEquals( message,
                      ((List) ksession.getGlobal( "list" )).get( 0 ) );

    }

    @Test
    public void testExtends() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "extend_rule_test.drl" ) ) );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        //ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        //Test 2 levels of inheritance, and basic rule
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        final Cheese mycheese = new Cheese( "cheddar",
                                            4 );
        FactHandle handle = session.insert( mycheese );
        session.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertEquals( "rule 4",
                      list.get( 0 ) );
        assertEquals( "rule 2b",
                      list.get( 1 ) );

        //Test 2nd level (parent) to make sure rule honors the extend rule
        list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session.retract( handle );
        final Cheese mycheese2 = new Cheese( "notcheddar",
                                             4 );
        FactHandle handle2 = session.insert( mycheese2 );
        session.fireAllRules();

        assertEquals( "rule 4",
                      list.get( 0 ) );
        assertEquals( 1,
                      list.size() );

        //Test 3 levels of inheritance, all levels
        list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session.retract( handle2 );
        final Cheese mycheese3 = new Cheese( "stilton",
                                             6 );
        FactHandle handle3 = session.insert( mycheese3 );
        session.fireAllRules();
        //System.out.println(list.toString());
        assertEquals( "rule 3",
                      list.get( 0 ) );
        assertEquals( 1,
                      list.size() );

        //Test 3 levels of inheritance, third only
        list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session.retract( handle3 );
        final Cheese mycheese4 = new Cheese( "notstilton",
                                             6 );
        FactHandle handle4 = session.insert( mycheese4 );
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal( "list" )).toString());
        assertTrue( ((List) session.getGlobal( "list" )).size() == 0 );

        //Test 3 levels of inheritance, 2nd only 
        list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session.retract( handle4 );
        final Cheese mycheese5 = new Cheese( "stilton",
                                             7 );
        FactHandle handle5 = session.insert( mycheese5 );
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal( "list" )).toString());
        assertEquals( 0,
                      list.size() );

    }

    @Test
    public void testExtends2() {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        try {
            kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_RuleExtend.drl" ) ),
                          ResourceType.DRL );

            assertFalse( kbuilder.getErrors().toString(),
                         kbuilder.hasErrors() );

            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

            final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

            final List results = new ArrayList();
            ksession.setGlobal( "results",
                                results );

            final Cheese stilton = new Cheese( "stilton",
                                               5 );
            final Cheese cheddar = new Cheese( "cheddar",
                                               7 );
            final Cheese brie = new Cheese( "brie",
                                            5 );

            ksession.insert( stilton );
            ksession.insert( cheddar );
            ksession.insert( brie );

            ksession.fireAllRules();

            assertEquals( 2,
                          results.size() );
            assertEquals( "stilton",
                          results.get( 0 ) );
            assertEquals( "brie",
                          results.get( 1 ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            if ( kbuilder.hasErrors() ) System.out.println( kbuilder.getErrors() );
            fail( "Unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void testLatinLocale() throws Exception {
        Locale defaultLoc = Locale.getDefault();

        try {
            // setting a locale that uses COMMA as decimal separator
            Locale.setDefault( new Locale( "pt",
                                           "BR" ) );

            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_LatinLocale.drl" ) ),
                          ResourceType.DRL );

            assertFalse( kbuilder.getErrors().toString(),
                         kbuilder.hasErrors() );

            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

            final List<String> results = new ArrayList<String>();
            ksession.setGlobal( "results",
                                results );

            final Cheese mycheese = new Cheese( "cheddar",
                                                4 );
            org.drools.runtime.rule.FactHandle handle = ksession.insert( mycheese );
            ksession.fireAllRules();

            assertEquals( 1,
                          results.size() );
            assertEquals( "1",
                          results.get( 0 ) );

            mycheese.setPrice( 8 );
            mycheese.setDoublePrice( 8.50 );

            ksession.update( handle,
                             mycheese );
            ksession.fireAllRules();
            assertEquals( 2,
                          results.size() );
            assertEquals( "3",
                          results.get( 1 ) );
        } finally {
            Locale.setDefault( defaultLoc );
        }

    }

    @Test
    public void testLiteral() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl" ) ) );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        session.fireAllRules();

        assertEquals( "stilton",
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testLiteralWithEscapes() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_literal_with_escapes.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        String expected = "s\tti\"lto\nn";
        final Cheese stilton = new Cheese( expected,
                                           5 );
        session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        int fired = session.fireAllRules();
        assertEquals( 1,
                      fired );

        assertEquals( expected,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testLiteralWithBoolean() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_with_boolean.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final PersonInterface bill = new Person( "bill",
                                                 null,
                                                 12 );
        bill.setAlive( true );
        session.insert( bill );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        session.fireAllRules();

        assertEquals( bill,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testFactBindings() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FactBindings.drl" ) ) );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List events = new ArrayList();
        final WorkingMemoryEventListener listener = new DefaultWorkingMemoryEventListener() {
            public void objectUpdated( ObjectUpdatedEvent event ) {
                events.add( event );
            }
        };

        workingMemory.addEventListener( listener );

        final Person bigCheese = new Person( "big cheese" );
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        bigCheese.setCheese( cheddar );

        final FactHandle bigCheeseHandle = workingMemory.insert( bigCheese );
        final FactHandle cheddarHandle = workingMemory.insert( cheddar );
        workingMemory.fireAllRules();

        ObjectUpdatedEvent event = (ObjectUpdatedEvent) events.get( 0 );
        assertSame( cheddarHandle,
                    event.getFactHandle() );
        assertSame( cheddar,
                    event.getOldObject() );
        assertSame( cheddar,
                    event.getObject() );

        event = (ObjectUpdatedEvent) events.get( 1 );
        assertSame( bigCheeseHandle,
                    event.getFactHandle() );
        assertSame( bigCheese,
                    event.getOldObject() );
        assertSame( bigCheese,
                    event.getObject() );
    }

    @Test
    public void testPropertyChangeSupportOldAPI() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_PropertyChange.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        Environment env = EnvironmentFactory.newEnvironment();
        env.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES,
                 new ObjectMarshallingStrategy[]{
                        new IdentityPlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT )} );
        StatefulSession session = ruleBase.newStatefulSession( null,
                                                               env );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final State state = new State( "initial" );
        session.insert( state,
                        true );
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );

        state.setFlag( true );
        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );

        session.fireAllRules();
        assertEquals( 2,
                      ((List) session.getGlobal( "list" )).size() );

        state.setState( "finished" );

        StatefulKnowledgeSession ksesion = SerializationHelper.getSerialisedStatefulKnowledgeSession( new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) session ),
                                                                                                      //                                                                                              MarshallerFactory.newIdentityMarshallingStrategy(),
                                                                                                      false );

        ksesion.fireAllRules();
        assertEquals( 3,
                      ((List) session.getGlobal( "list" )).size() );

    }

    @Test
    public void testPropertyChangeSupportNewAPI() throws Exception {
        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_PropertyChangeTypeDecl.drl" ) ),
                     ResourceType.DRL );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final State state = new State( "initial" );
        session.insert( state );
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );

        state.setFlag( true );
        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );

        session.fireAllRules();
        assertEquals( 2,
                      ((List) session.getGlobal( "list" )).size() );

        state.setState( "finished" );

        session.dispose();

    }

    @Test
    public void testDisconnectedFactHandle() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        DefaultFactHandle helloHandle = (DefaultFactHandle) ksession.insert( "hello" );
        DefaultFactHandle goodbyeHandle = (DefaultFactHandle) ksession.insert( "goodbye" );

        org.drools.runtime.rule.FactHandle key = new DefaultFactHandle( helloHandle.toExternalForm() );
        assertEquals( "hello",
                      ksession.getObject( key ) );

        key = new DefaultFactHandle( goodbyeHandle.toExternalForm() );
        assertEquals( "goodbye",
                      ksession.getObject( key ) );

    }

    @Test
    public void testBigDecimal() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "big_decimal_and_comparable.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final PersonInterface bill = new Person( "bill",
                                                 null,
                                                 12 );
        bill.setBigDecimal( new BigDecimal( "42" ) );

        final PersonInterface ben = new Person( "ben",
                                                null,
                                                13 );
        ben.setBigDecimal( new BigDecimal( "43" ) );

        session.insert( bill );
        session.insert( ben );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
    }

    @Test
    public void testBigDecimalIntegerLiteral() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "big_decimal_and_literal.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final PersonInterface bill = new Person( "bill",
                                                 null,
                                                 12 );
        bill.setBigDecimal( new BigDecimal( "42" ) );
        bill.setBigInteger( new BigInteger( "42" ) );

        session.insert( bill );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( 6,
                      ((List) session.getGlobal( "list" )).size() );
    }

    @Test
    public void testBigDecimalWithFromAndEval() throws Exception {
        String rule = "package org.test;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    $dec : java.math.BigDecimal() from java.math.BigDecimal.TEN;\n";
        rule += "    eval( $dec.compareTo(java.math.BigDecimal.ONE) > 0 )\n";
        rule += "then\n";
        rule += "    System.out.println(\"OK!\");\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );

        assertFalse( builder.getErrors().toString(),
                     builder.hasErrors() );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        session.fireAllRules();
    }

    @Test
    public void testMVELConsequenceWithMapsAndArrays() throws Exception {
        String rule = "package org.test;\n";
        rule += "import java.util.ArrayList\n";
        rule += "import java.util.HashMap\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Test Rule\"\n";
        rule += "    dialect \"mvel\"";
        rule += "when\n";
        rule += "then\n";
        rule += "    m = new HashMap();\n";
        rule += "    l = new ArrayList();\n";
        rule += "    l.add(\"first\");\n";
        rule += "    m.put(\"content\", l);\n";
        rule += "    System.out.println(((ArrayList)m[\"content\"])[0]);\n";
        rule += "    list.add(((ArrayList)m[\"content\"])[0]);\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( "first",
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testCell() throws Exception {
        final Cell cell1 = new Cell( 9 );
        final Cell cell = new Cell( 0 );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "evalmodify.drl" ) ) );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        RuleBase ruleBase = getRuleBase();
        Package pkg = builder.getPackage();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        Environment env = EnvironmentFactory.newEnvironment();
        env.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES,
                 new ObjectMarshallingStrategy[]{
                        new IdentityPlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT )} );
        StatefulSession session = ruleBase.newStatefulSession( null,
                                                               env );
        session.insert( cell1 );
        FactHandle cellHandle = session.insert( cell );

        StatefulKnowledgeSession ksesion = SerializationHelper.getSerialisedStatefulKnowledgeSession( new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) session ),
                                                                                                      //                                                                                              MarshallerFactory.newIdentityMarshallingStrategy(),
                                                                                                      false );

        ksesion.fireAllRules();
        assertEquals( 9,
                      cell.getValue() );
    }

    @Test
    public void testNesting() throws Exception {
        Person p = new Person();
        p.setName( "Michael" );

        Address add1 = new Address();
        add1.setStreet( "High" );

        Address add2 = new Address();
        add2.setStreet( "Low" );

        List l = new ArrayList();
        l.add( add1 );
        l.add( add2 );

        p.setAddresses( l );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "nested_fields.drl" ) ) );

        assertFalse( builder.getErrors().toString(),
                     builder.hasErrors() );

        DrlParser parser = new DrlParser();
        PackageDescr desc = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "nested_fields.drl" ) ) );
        List packageAttrs = desc.getAttributes();
        assertEquals( 1,
                      desc.getRules().size() );
        assertEquals( 1,
                      packageAttrs.size() );

        RuleDescr rule = (RuleDescr) desc.getRules().get( 0 );
        Map<String, AttributeDescr> ruleAttrs = rule.getAttributes();
        assertEquals( 1,
                      ruleAttrs.size() );

        assertEquals( "mvel",
                      ((AttributeDescr) ruleAttrs.get( "dialect" )).getValue() );
        assertEquals( "dialect",
                      ((AttributeDescr) ruleAttrs.get( "dialect" )).getName() );
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session.insert( p );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

    }

    @Test
    public void testOr() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "or_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        final FactHandle h = session.insert( cheddar );

        session.fireAllRules();

        // just one added
        assertEquals( "got cheese",
                      list.get( 0 ) );
        assertEquals( 1,
                      list.size() );

        session.retract( h );
        session.fireAllRules();

        // still just one
        assertEquals( 1,
                      list.size() );

        session.insert( new Cheese( "stilton",
                                    5 ) );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        // now have one more
        assertEquals( 2,
                      ((List) session.getGlobal( "list" )).size() );

    }

    @Test
    public void testEval() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        session.setGlobal( "five",
                           new Integer( 5 ) );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( stilton,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testJaninoEval() throws Exception {
        final PackageBuilderConfiguration config = new PackageBuilderConfiguration();
        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) config.getDialectConfiguration( "java" );
        javaConf.setCompiler( JavaDialectConfiguration.JANINO );

        final PackageBuilder builder = new PackageBuilder( config );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session.setGlobal( "five",
                           new Integer( 5 ) );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( stilton,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testEvalMore() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test_more.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Person foo = new Person( "foo" );
        session.insert( foo );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( foo,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testReturnValue() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "returnvalue_rule_test.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session.setGlobal( "two",
                           new Integer( 2 ) );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final PersonInterface peter = new Person( "peter",
                                                  null,
                                                  12 );
        session.insert( peter );
        final PersonInterface jane = new Person( "jane",
                                                 null,
                                                 10 );
        session.insert( jane );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( jane,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
        assertEquals( peter,
                      ((List) session.getGlobal( "list" )).get( 1 ) );
    }

    @Test
    public void testPredicate() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_rule_test.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session.setGlobal( "two",
                           new Integer( 2 ) );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final PersonInterface peter = new Person( "peter",
                                                  null,
                                                  12 );
        session.insert( peter );
        final PersonInterface jane = new Person( "jane",
                                                 null,
                                                 10 );
        session.insert( jane );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( jane,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
        assertEquals( peter,
                      ((List) session.getGlobal( "list" )).get( 1 ) );
    }

    @Test
    public void testNullBehaviour() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "null_behaviour.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final PersonInterface p1 = new Person( "michael",
                                               "food",
                                               40 );
        final PersonInterface p2 = new Person( null,
                                               "drink",
                                               30 );
        session.insert( p1 );
        session.insert( p2 );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();
    }

    @Test
    public void testNullConstraint() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "null_constraint.drl" ) ) );

        if ( builder.hasErrors() ) {
            for ( DroolsError error : builder.getErrors().getErrors() ) {
                System.err.println( error );
            }
        }
        assertFalse( builder.getErrors().toString(),
                     builder.hasErrors() );

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();
        final List foo = new ArrayList();
        session.setGlobal( "messages",
                           foo );

        final PersonInterface p1 = new Person( null,
                                               "food",
                                               40 );
        final Primitives p2 = new Primitives();
        p2.setArrayAttribute( null );

        session.insert( p1 );
        session.insert( p2 );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();
        assertEquals( 2,
                      ((List) session.getGlobal( "messages" )).size() );

    }

    @Test
    public void testBasicFrom() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_From.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        final List list1 = new ArrayList();
        ksession.setGlobal( "list1",
                            list1 );
        final List list2 = new ArrayList();
        ksession.setGlobal( "list2",
                            list2 );
        final List list3 = new ArrayList();
        ksession.setGlobal( "list3",
                            list3 );

        final Cheesery cheesery = new Cheesery();
        final Cheese stilton = new Cheese( "stilton",
                                           12 );
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        cheesery.addCheese( stilton );
        cheesery.addCheese( cheddar );
        ksession.setGlobal( "cheesery",
                            cheesery );
        ksession.insert( cheesery );

        Person p = new Person( "stilton" );
        ksession.insert( p );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );
        ksession.fireAllRules();

        // from using a global
        assertEquals( 2,
                      ((List) ksession.getGlobal( "list1" )).size() );
        assertEquals( cheddar,
                      ((List) ksession.getGlobal( "list1" )).get( 0 ) );
        assertEquals( stilton,
                      ((List) ksession.getGlobal( "list1" )).get( 1 ) );

        // from using a declaration
        assertEquals( 2,
                      ((List) ksession.getGlobal( "list2" )).size() );
        assertEquals( cheddar,
                      ((List) ksession.getGlobal( "list2" )).get( 0 ) );
        assertEquals( stilton,
                      ((List) ksession.getGlobal( "list2" )).get( 1 ) );

        // from using a declaration
        assertEquals( 1,
                      ((List) ksession.getGlobal( "list3" )).size() );
        assertEquals( stilton,
                      ((List) ksession.getGlobal( "list3" )).get( 0 ) );
    }

    @Test
    public void testFromWithParams() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FromWithParams.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List list = new ArrayList();
        final Object globalObject = new Object();
        workingMemory.setGlobal( "list",
                                 list );
        workingMemory.setGlobal( "testObject",
                                 new FromTestClass() );
        workingMemory.setGlobal( "globalObject",
                                 globalObject );

        final Person bob = new Person( "bob" );
        workingMemory.insert( bob );

        // TODO java.io.NotSerializableException: org.mvel.util.FastList
        //        workingMemory    = SerializationHelper.serializeObject(workingMemory);
        workingMemory.fireAllRules();

        assertEquals( 6,
                      ((List) workingMemory.getGlobal( "list" )).size() );

        final List array = (List) ((List) workingMemory.getGlobal( "list" )).get( 0 );
        assertEquals( 3,
                      array.size() );
        final Person p = (Person) array.get( 0 );
        assertEquals( p,
                      bob );

        assertEquals( new Integer( 42 ),
                      array.get( 1 ) );

        final List nested = (List) array.get( 2 );
        assertEquals( "x",
                      nested.get( 0 ) );
        assertEquals( "y",
                      nested.get( 1 ) );

        final Map map = (Map) ((List) workingMemory.getGlobal( "list" )).get( 1 );
        assertEquals( 2,
                      map.keySet().size() );

        assertTrue( map.keySet().contains( bob ) );
        assertEquals( globalObject,
                      map.get( bob ) );

        assertTrue( map.keySet().contains( "key1" ) );
        final Map nestedMap = (Map) map.get( "key1" );
        assertEquals( 1,
                      nestedMap.keySet().size() );
        assertTrue( nestedMap.keySet().contains( "key2" ) );
        assertEquals( "value2",
                      nestedMap.get( "key2" ) );

        assertEquals( new Integer( 42 ),
                      ((List) workingMemory.getGlobal( "list" )).get( 2 ) );
        assertEquals( "literal",
                      ((List) workingMemory.getGlobal( "list" )).get( 3 ) );
        assertEquals( bob,
                      ((List) workingMemory.getGlobal( "list" )).get( 4 ) );
        assertEquals( globalObject,
                      ((List) workingMemory.getGlobal( "list" )).get( 5 ) );
    }

    @Test
    public void testFromWithNewConstructor() throws Exception {
        DrlParser parser = new DrlParser();
        PackageDescr descr = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "test_FromWithNewConstructor.drl" ) ) );
        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( descr );
        Package pkg = builder.getPackage();
        pkg.checkValidity();
        pkg = SerializationHelper.serializeObject( pkg );
    }

    /**
     * @see JBRULES-1415 Certain uses of from causes NullPointerException in WorkingMemoryLogger
     */
    @Test
    public void testFromDeclarationWithWorkingMemoryLogger() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.drools.Cheesery\n";
        rule += "import org.drools.Cheese\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    $cheesery : Cheesery()\n";
        rule += "    Cheese( $type : type) from $cheesery.cheeses\n";
        rule += "then\n";
        rule += "    list.add( $type );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( new Cheese( "stilton",
                                        22 ) );

        session.insert( cheesery );

        // TODO java.io.EOFException
        //        session = SerializationHelper.serializeObject(session);
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( "stilton",
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testWithInvalidRule() throws Exception {
        final PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        final JavaDialectConfiguration jconf = (JavaDialectConfiguration) conf.getDialectConfiguration( "java" );
        jconf.setCompiler( JavaDialectConfiguration.ECLIPSE );
        final PackageBuilder builder = new PackageBuilder( conf );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "invalid_rule.drl" ) ) );
        final Package pkg = builder.getPackage();
        // Mark: please check if the conseqeuence/should/shouldn't be built
        // Rule badBoy = pkg.getRules()[0];
        // assertFalse(badBoy.isValid());

        RuntimeException runtime = null;
        // this should ralph all over the place.
        RuleBase ruleBase = getRuleBase();
        try {
            ruleBase.addPackage( pkg );
            fail( "Should have thrown an exception as the rule is NOT VALID." );
        } catch ( final RuntimeException e ) {
            assertNotNull( e.getMessage() );
            runtime = e;
        }
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        assertTrue( builder.getErrors().getErrors().length > 0 );

        final String pretty = builder.getErrors().toString();
        assertFalse( pretty.equals( "" ) );
        assertEquals( pretty,
                      runtime.getMessage() );

    }

    @Test
    public void testWithInvalidRule2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "invalid_rule2.drl" ) ) );
        assertTrue( builder.hasErrors() );
        String err = builder.getErrors().toString();
        System.out.println( err );
    }

    @Test
    public void testErrorLineNumbers() throws Exception {
        // this test aims to test semantic errors
        // parser errors are another test case
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "errors_in_rule.drl" ) ) );
        final Package pkg = builder.getPackage();

        DroolsError[] errors = builder.getErrors().getErrors();
        assertEquals( 3,
                      errors.length );

        final DescrBuildError stiltonError = (DescrBuildError) errors[0];
        assertTrue( stiltonError.getMessage().contains( "Stilton" ) );
        assertNotNull( stiltonError.getDescr() );
        assertTrue( stiltonError.getLine() != -1 );

        // check that its getting it from the ruleDescr
        assertEquals( stiltonError.getLine(),
                      stiltonError.getDescr().getLine() );
        // check the absolute error line number (there are more).
        assertEquals( 11,
                      stiltonError.getLine() );

        final DescrBuildError poisonError = (DescrBuildError) errors[1];
        assertTrue( poisonError.getMessage().contains( "Poison" ) );
        assertEquals( 13,
                      poisonError.getLine() );

        assertTrue( errors[2].getMessage().contains( "add" ) );
        // now check the RHS, not being too specific yet, as long as it has the
        // rules line number, not zero
        final DescrBuildError rhsError = (DescrBuildError) errors[2];
        assertTrue( rhsError.getLine() >= 8 && rhsError.getLine() <= 17 ); // TODO this should be 16
    }

    @Test
    public void testErrorsParser() throws Exception {
        final DrlParser parser = new DrlParser();
        assertEquals( 0,
                      parser.getErrors().size() );
        parser.parse( new InputStreamReader( getClass().getResourceAsStream( "errors_parser_multiple.drl" ) ) );
        assertTrue( parser.hasErrors() );
        assertTrue( parser.getErrors().size() > 0 );
        assertTrue( parser.getErrors().get( 0 ) instanceof ParserError );
        final ParserError first = ((ParserError) parser.getErrors().get( 0 ));
        assertTrue( first.getMessage() != null );
        assertFalse( first.getMessage().equals( "" ) );
    }

    @Test
    public void testFunction() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FunctionInConsequence.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        session.insert( stilton );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( new Integer( 5 ),
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testAssertRetract() throws Exception {
        // postponed while I sort out KnowledgeHelperFixer
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "assert_retract.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface person = new Person( "michael",
                                                   "cheese" );
        person.setStatus( "start" );
        workingMemory.insert( person );

        // TODO org.drools.spi.ConsequenceException: org.drools.FactException: Update error: handle not found for object:
        //        workingMemory    = SerializationHelper.serializeObject(workingMemory);
        workingMemory.fireAllRules();

        List<String> results = (List<String>) workingMemory.getGlobal( "list" );
        System.out.println( results );
        assertEquals( 5,
                      results.size() );
        assertTrue( results.contains( "first" ) );
        assertTrue( results.contains( "second" ) );
        assertTrue( results.contains( "third" ) );
        assertTrue( results.contains( "fourth" ) );
        assertTrue( results.contains( "fifth" ) );

    }

    @Test
    public void testPredicateAsFirstPattern() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_as_first_pattern.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Cheese mussarela = new Cheese( "Mussarela",
                                             35 );
        workingMemory.insert( mussarela );
        final Cheese provolone = new Cheese( "Provolone",
                                             20 );
        workingMemory.insert( provolone );

        workingMemory.fireAllRules();

        assertEquals( "The rule is being incorrectly fired",
                      35,
                      mussarela.getPrice() );
        assertEquals( "Rule is incorrectly being fired",
                      20,
                      provolone.getPrice() );
    }

    @Test
    public void testConsequenceException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConsequenceException.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.insert( brie );

        try {
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Consequence" );
        } catch ( final org.drools.runtime.rule.ConsequenceException e ) {
            assertEquals( "Throw Consequence Exception",
                          e.getRule().getName() );
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    @Test
    public void testCustomConsequenceException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConsequenceException.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setConsequenceExceptionHandler( CustomConsequenceExceptionHandler.class.getName() );

        RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertTrue( ((CustomConsequenceExceptionHandler) ((DefaultAgenda) workingMemory.getAgenda()).getConsequenceExceptionHandler()).isCalled() );
    }

    public static class CustomConsequenceExceptionHandler
            implements
            ConsequenceExceptionHandler {

        private boolean called;

        public void handleException( org.drools.spi.Activation activation,
                                     org.drools.WorkingMemory workingMemory,
                                     Exception exception ) {
            this.called = true;
        }

        public boolean isCalled() {
            return this.called;
        }

        public void readExternal( ObjectInput in ) throws IOException,
                                                  ClassNotFoundException {
            called = in.readBoolean();
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeBoolean( called );
        }
    }

    @Test
    public void testFunctionException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FunctionException.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.insert( brie );

        try {
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Function" );
        } catch ( final Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    @Test
    public void testEvalException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EvalException.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );

        try {
            workingMemory.insert( brie );
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Eval" );
        } catch ( final Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    @Test
    public void testPredicateException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_PredicateException.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );

        try {
            workingMemory.insert( brie );
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Predicate" );
        } catch ( final Exception e ) {
            assertTrue( e.getCause().getMessage().contains( "this should throw an exception" ) );
        }
    }

    @Test
    public void testReturnValueException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ReturnValueException.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );

        try {
            workingMemory.insert( brie );
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the ReturnValue" );
        } catch ( final Exception e ) {
            e.getCause().getMessage().contains( "this should throw an exception" );
        }
    }

    @Test
    public void testMultiRestrictionFieldConstraint() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MultiRestrictionFieldConstraint.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list1 = new ArrayList();
        workingMemory.setGlobal( "list1",
                                 list1 );
        final List list2 = new ArrayList();
        workingMemory.setGlobal( "list2",
                                 list2 );
        final List list3 = new ArrayList();
        workingMemory.setGlobal( "list3",
                                 list3 );
        final List list4 = new ArrayList();
        workingMemory.setGlobal( "list4",
                                 list4 );

        final Person youngChili1 = new Person( "young chili1" );
        youngChili1.setAge( 12 );
        youngChili1.setHair( "blue" );
        final Person youngChili2 = new Person( "young chili2" );
        youngChili2.setAge( 25 );
        youngChili2.setHair( "purple" );

        final Person chili1 = new Person( "chili1" );
        chili1.setAge( 35 );
        chili1.setHair( "red" );

        final Person chili2 = new Person( "chili2" );
        chili2.setAge( 38 );
        chili2.setHair( "indigigo" );

        final Person oldChili1 = new Person( "old chili2" );
        oldChili1.setAge( 45 );
        oldChili1.setHair( "green" );

        final Person oldChili2 = new Person( "old chili2" );
        oldChili2.setAge( 48 );
        oldChili2.setHair( "blue" );

        workingMemory.insert( youngChili1 );
        workingMemory.insert( youngChili2 );
        workingMemory.insert( chili1 );
        workingMemory.insert( chili2 );
        workingMemory.insert( oldChili1 );
        workingMemory.insert( oldChili2 );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list1.size() );
        assertTrue( list1.contains( chili1 ) );

        assertEquals( 2,
                      list2.size() );
        assertTrue( list2.contains( chili1 ) );
        assertTrue( list2.contains( chili2 ) );

        assertEquals( 2,
                      list3.size() );
        assertTrue( list3.contains( youngChili1 ) );
        assertTrue( list3.contains( youngChili2 ) );

        assertEquals( 2,
                      list4.size() );
        assertTrue( list4.contains( youngChili1 ) );
        assertTrue( list4.contains( chili1 ) );
    }

    @Test
    @Ignore
    public void testDumpers() throws Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "test_Dumpers.drl" ) ) );

        if ( parser.hasErrors() ) {
            for ( DroolsError error : parser.getErrors() ) {
                System.err.println( error );
            }
            fail( parser.getErrors().toString() );
        }

        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( pkg );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertEquals( "3 1",
                      list.get( 0 ) );
        assertEquals( "MAIN",
                      list.get( 1 ) );
        assertEquals( "1 1",
                      list.get( 2 ) );

        final DrlDumper drlDumper = new DrlDumper();
        final String drlResult = drlDumper.dump( pkg );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( drlResult ) );

        if ( builder.hasErrors() ) {
            for ( DroolsError error : builder.getErrors().getErrors() ) {
                System.err.println( error );
            }
            fail( parser.getErrors().toString() );
        }

        ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        workingMemory = ruleBase.newStatefulSession();

        list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertEquals( "3 1",
                      list.get( 0 ) );
        assertEquals( "MAIN",
                      list.get( 1 ) );
        assertEquals( "1 1",
                      list.get( 2 ) );

        final XmlDumper xmlDumper = new XmlDumper();
        final String xmlResult = xmlDumper.dump( pkg );

        // System.out.println( xmlResult );

        builder = new PackageBuilder();
        builder.addPackageFromXml( new StringReader( xmlResult ) );

        if ( builder.hasErrors() ) {
            for ( DroolsError error : builder.getErrors().getErrors() ) {
                System.err.println( error );
            }
            fail( parser.getErrors().toString() );
        }

        ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        workingMemory = ruleBase.newStatefulSession();

        list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertEquals( "3 1",
                      list.get( 0 ) );
        assertEquals( "MAIN",
                      list.get( 1 ) );
        assertEquals( "1 1",
                      list.get( 2 ) );
    }

    @Test
    public void testContainsCheese() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ContainsCheese.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           12 );
        workingMemory.insert( stilton );
        final Cheese brie = new Cheese( "brie",
                                        10 );
        workingMemory.insert( brie );

        final Cheesery cheesery = new Cheesery();
        cheesery.getCheeses().add( stilton );
        workingMemory.insert( cheesery );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );

        assertEquals( stilton,
                      list.get( 0 ) );
        assertEquals( brie,
                      list.get( 1 ) );
    }

    @Test
    public void testDuplicateRuleNames() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DuplicateRuleName1.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DuplicateRuleName2.drl" ) ) );
        ruleBase.addPackage( builder.getPackage() );

        // @todo: this is from JBRULES-394 - maybe we should test more stuff
        // here?

    }

    @Test
    public void testNullValuesIndexing() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_NullValuesIndexing.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg1 = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        // Adding person with null name and likes attributes
        final PersonInterface bob = new Person( null,
                                                null );
        bob.setStatus( "P1" );
        final PersonInterface pete = new Person( null,
                                                 null );
        bob.setStatus( "P2" );
        workingMemory.insert( bob );
        workingMemory.insert( pete );

        workingMemory.fireAllRules();

        assertEquals( "Indexing with null values is not working correctly.",
                      "OK",
                      bob.getStatus() );
        assertEquals( "Indexing with null values is not working correctly.",
                      "OK",
                      pete.getStatus() );

    }

    @Test
    public void testEmptyRule() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EmptyRule.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();

        assertTrue( list.contains( "fired1" ) );
        assertTrue( list.contains( "fired2" ) );
    }

    @Test
    public void testjustEval() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NoPatterns.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();

        assertTrue( list.contains( "fired1" ) );
        assertTrue( list.contains( "fired3" ) );
    }

    @Test
    public void testOrWithBinding() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_OrWithBindings.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Person hola = new Person( "hola" );
        workingMemory.insert( hola );

        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );
        Cheese brie = new Cheese( "brie" );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( hola ) );
        assertTrue( list.contains( brie ) );

    }

    @Test
    public void testJoinNodeModifyObject() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_JoinNodeModifyObject.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg1 = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        try {
            final List orderedFacts = new ArrayList();
            final List errors = new ArrayList();
            workingMemory.setGlobal( "orderedNumbers",
                                     orderedFacts );
            workingMemory.setGlobal( "errors",
                                     errors );
            final int MAX = 2;
            for ( int i = 1; i <= MAX; i++ ) {
                final IndexedNumber n = new IndexedNumber( i,
                                                           MAX - i + 1 );
                workingMemory.insert( n );
            }
            workingMemory.fireAllRules();
            assertTrue( "Processing generated errors: " + errors.toString(),
                        errors.isEmpty() );
            for ( int i = 1; i <= MAX; i++ ) {
                final IndexedNumber n = (IndexedNumber) orderedFacts.get( i - 1 );
                assertEquals( "Fact is out of order",
                              i,
                              n.getIndex() );
            }
        } finally {
        }
    }

    @Test
    public void testInsurancePricingExample() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "insurance_pricing_example.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        // now create some test data
        final Driver driver = new Driver();
        final Policy policy = new Policy();

        ksession.insert( driver );
        ksession.insert( policy );

        ksession.fireAllRules();

        assertEquals( 120,
                      policy.getBasePrice() );
    }

    @Test
    public void testLLR() throws Exception {

        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_JoinNodeModifyTuple.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );

        // 1st time
        org.drools.Target tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.26544f ) );
        tgt.setLon( new Float( 28.952137f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.8666667f ) );
        ksession.insert( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.236874f ) );
        tgt.setLon( new Float( 28.992579f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.8666667f ) );
        ksession.insert( tgt );

        ksession.fireAllRules();

        // 2nd time
        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.265343f ) );
        tgt.setLon( new Float( 28.952267f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.9f ) );
        ksession.insert( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.236935f ) );
        tgt.setLon( new Float( 28.992493f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.9f ) );
        ksession.insert( tgt );

        ksession.fireAllRules();

        // 3d time
        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.26525f ) );
        tgt.setLon( new Float( 28.952396f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.9333333f ) );
        ksession.insert( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.236996f ) );
        tgt.setLon( new Float( 28.992405f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.9333333f ) );
        ksession.insert( tgt );

        ksession.fireAllRules();

        // 4th time
        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.265163f ) );
        tgt.setLon( new Float( 28.952526f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.9666667f ) );
        ksession.insert( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.237057f ) );
        tgt.setLon( new Float( 28.99232f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.9666667f ) );
        ksession.insert( tgt );

        ksession.fireAllRules();
    }

    @Test
    public void testFunctionWithPrimitives() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FunctionWithPrimitives.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.insert( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 10 ),
                      list.get( 0 ) );
    }

    @Test
    public void testReturnValueAndGlobal() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ReturnValueAndGlobal.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List matchlist = new ArrayList();
        workingMemory.setGlobal( "matchingList",
                                 matchlist );

        final List nonmatchlist = new ArrayList();
        workingMemory.setGlobal( "nonMatchingList",
                                 nonmatchlist );

        workingMemory.setGlobal( "cheeseType",
                                 "stilton" );

        final Cheese stilton1 = new Cheese( "stilton",
                                            5 );
        final Cheese stilton2 = new Cheese( "stilton",
                                            7 );
        final Cheese brie = new Cheese( "brie",
                                        4 );
        workingMemory.insert( stilton1 );
        workingMemory.insert( stilton2 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      matchlist.size() );
        assertEquals( 1,
                      nonmatchlist.size() );
    }

    @Test
    public void testDeclaringAndUsingBindsInSamePattern() throws Exception {
        final RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setRemoveIdentities( true );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DeclaringAndUsingBindsInSamePattern.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase( config );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List sensors = new ArrayList();

        workingMemory.setGlobal( "sensors",
                                 sensors );

        final Sensor sensor1 = new Sensor( 100,
                                           150 );
        workingMemory.insert( sensor1 );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      sensors.size() );

        final Sensor sensor2 = new Sensor( 200,
                                           150 );
        workingMemory.insert( sensor2 );
        workingMemory.fireAllRules();
        assertEquals( 3,
                      sensors.size() );
    }

    @Test
    public void testMissingImports() {
        try {
            final PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_missing_import.drl" ) ) );
            final Package pkg = builder.getPackage();

            RuleBase ruleBase = getRuleBase();
            ruleBase.addPackage( pkg );
            ruleBase = SerializationHelper.serializeObject( ruleBase );

            fail( "Should have thrown an InvalidRulePackage" );
        } catch ( final InvalidRulePackage e ) {
            // everything fine
        } catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Should have thrown an InvalidRulePackage Exception instead of " + e.getMessage() );
        }
    }

    @Test
    public void testNestedConditionalElements() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NestedConditionalElements.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final State state = new State( "SP" );
        workingMemory.insert( state );

        final Person bob = new Person( "Bob" );
        bob.setStatus( state.getState() );
        bob.setLikes( "stilton" );
        workingMemory.insert( bob );

        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        workingMemory.insert( new Cheese( bob.getLikes(),
                                          10 ) );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testDeclarationUsage() throws Exception {

        try {
            final PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DeclarationUsage.drl" ) ) );
            final Package pkg = builder.getPackage();

            RuleBase ruleBase = getRuleBase();
            ruleBase.addPackage( pkg );
            ruleBase = SerializationHelper.serializeObject( ruleBase );

            fail( "Should have trown an exception" );
        } catch ( final InvalidRulePackage e ) {
            // success ... correct exception thrown
        } catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Wrong exception raised: " + e.getMessage() );
        }
    }

    @Test
    public void testDeclareAndFrom() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_DeclareWithFrom.drl" );
        FactType profileType = kbase.getFactType( "org.drools",
                                                  "Profile" );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Object profile = profileType.newInstance();
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put( "internet",
                 Integer.valueOf( 2 ) );
        profileType.set( profile,
                         "pageFreq",
                         map );

        ksession.insert( profile );
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testDeclarationNonExistingField() throws Exception {
        try {
            final PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DeclarationOfNonExistingField.drl" ) ) );
            final Package pkg = builder.getPackage();

            RuleBase ruleBase = getRuleBase();
            ruleBase.addPackage( pkg );

            fail( "Should have trown an exception" );
        } catch ( final InvalidRulePackage e ) {
            // success ... correct exception thrown
        } catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Wrong exception raised: " + e.getMessage() );
        }
    }

    @Test
    public void testUnbalancedTrees() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_UnbalancedTrees.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory wm = ruleBase.newStatefulSession();

        wm.insert( new Cheese( "a",
                               10 ) );
        wm.insert( new Cheese( "b",
                               10 ) );
        wm.insert( new Cheese( "c",
                               10 ) );
        wm.insert( new Cheese( "d",
                               10 ) );
        final Cheese e = new Cheese( "e",
                                     10 );
        wm.insert( e );

        wm.fireAllRules();

        assertEquals( "Rule should have fired twice, seting the price to 30",
                      30,
                      e.getPrice() );
        // success
    }

    @Test
    public void testImportConflict() throws Exception {
        RuleBase ruleBase = getRuleBase();
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ImportConflict.drl" ) ) );
        final Package pkg = builder.getPackage();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
    }

    @Test
    public void testEmptyIdentifier() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_emptyIdentifier.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final List result = new ArrayList();
        workingMemory.setGlobal( "results",
                                 result );

        final Person person = new Person( "bob" );
        final Cheese cheese = new Cheese( "brie",
                                          10 );

        workingMemory.insert( person );
        workingMemory.insert( cheese );

        workingMemory.fireAllRules();
        assertEquals( 4,
                      result.size() );
    }

    @Test
    public void testDuplicateVariableBinding() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_duplicateVariableBinding.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final Map result = new HashMap();
        workingMemory.setGlobal( "results",
                                 result );

        final Cheese stilton = new Cheese( "stilton",
                                           20 );
        final Cheese brie = new Cheese( "brie",
                                        10 );

        workingMemory.insert( stilton );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();
        assertEquals( 5,
                      result.size() );
        assertEquals( stilton.getPrice(),
                      ((Integer) result.get( stilton.getType() )).intValue() );
        assertEquals( brie.getPrice(),
                      ((Integer) result.get( brie.getType() )).intValue() );

        assertEquals( stilton.getPrice(),
                      ((Integer) result.get( stilton )).intValue() );
        assertEquals( brie.getPrice(),
                      ((Integer) result.get( brie )).intValue() );

        assertEquals( stilton.getPrice(),
                      ((Integer) result.get( "test3" + stilton.getType() )).intValue() );

        workingMemory.insert( new Person( "bob",
                                          brie.getType() ) );
        workingMemory.fireAllRules();

        assertEquals( 6,
                      result.size() );
        assertEquals( brie.getPrice(),
                      ((Integer) result.get( "test3" + brie.getType() )).intValue() );
    }

    @Test
    public void testShadowProxyInHirarchies() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ShadowProxyInHirarchies.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory.insert( new Child( "gp" ) );

        workingMemory.fireAllRules();
    }

    @Test
    public void testSelfReference() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_SelfReference.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        final Order order = new Order( 10,
                                       "Bob" );
        final OrderItem item1 = new OrderItem( order,
                                               1 );
        final OrderItem item2 = new OrderItem( order,
                                               2 );
        final OrderItem anotherItem1 = new OrderItem( null,
                                                      3 );
        final OrderItem anotherItem2 = new OrderItem( null,
                                                      4 );
        workingMemory.insert( order );
        workingMemory.insert( item1 );
        workingMemory.insert( item2 );
        workingMemory.insert( anotherItem1 );
        workingMemory.insert( anotherItem2 );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( item1 ) );
        assertTrue( results.contains( item2 ) );
    }

    @Test
    public void testNumberComparisons() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NumberComparisons.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        // asserting the sensor object
        final RandomNumber rn = new RandomNumber();
        rn.setValue( 10 );
        workingMemory.insert( rn );

        final Guess guess = new Guess();
        guess.setValue( new Integer( 5 ) );

        final FactHandle handle = workingMemory.insert( guess );

        workingMemory.fireAllRules();

        // HIGHER
        assertEquals( 1,
                      list.size() );
        assertEquals( "HIGHER",
                      list.get( 0 ) );

        guess.setValue( new Integer( 15 ) );
        workingMemory.update( handle,
                              guess );

        workingMemory.fireAllRules();

        // LOWER
        assertEquals( 2,
                      list.size() );
        assertEquals( "LOWER",
                      list.get( 1 ) );

        guess.setValue( new Integer( 10 ) );
        workingMemory.update( handle,
                              guess );

        workingMemory.fireAllRules();

        // CORRECT
        assertEquals( 3,
                      list.size() );
        assertEquals( "CORRECT",
                      list.get( 2 ) );

    }

    @Test
    public void testSkipModify() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_skipModify.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        final Cheese cheese = new Cheese( "brie",
                                          10 );
        final FactHandle handle = workingMemory.insert( cheese );

        final Person bob = new Person( "bob",
                                       "stilton" );
        workingMemory.insert( bob );

        cheese.setType( "stilton" );
        workingMemory.update( handle,
                              cheese );
        workingMemory.fireAllRules();
        assertEquals( 2,
                      results.size() );
    }

    @Test
    public void testEventModel() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EventModel.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        final List agendaList = new ArrayList();
        final AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void activationCancelled( ActivationCancelledEvent event,
                                             WorkingMemory workingMemory ) {
                agendaList.add( event );

            }

            public void activationCreated( ActivationCreatedEvent event,
                                           WorkingMemory workingMemory ) {
                agendaList.add( event );
            }

            public void afterActivationFired( AfterActivationFiredEvent event,
                                              WorkingMemory workingMemory ) {
                agendaList.add( event );
            }

            public void agendaGroupPopped( AgendaGroupPoppedEvent event,
                                           WorkingMemory workingMemory ) {
                agendaList.add( event );
            }

            public void agendaGroupPushed( AgendaGroupPushedEvent event,
                                           WorkingMemory workingMemory ) {
                agendaList.add( event );
            }

            public void beforeActivationFired( BeforeActivationFiredEvent event,
                                               WorkingMemory workingMemory ) {
                agendaList.add( event );
            }

            public void afterRuleFlowGroupActivated(
                                                     RuleFlowGroupActivatedEvent event,
                                                     WorkingMemory workingMemory ) {
                // TODO Auto-generated method stub

            }

            public void afterRuleFlowGroupDeactivated(
                                                       RuleFlowGroupDeactivatedEvent event,
                                                       WorkingMemory workingMemory ) {
                // TODO Auto-generated method stub

            }

            public void beforeRuleFlowGroupActivated(
                                                      RuleFlowGroupActivatedEvent event,
                                                      WorkingMemory workingMemory ) {
                // TODO Auto-generated method stub

            }

            public void beforeRuleFlowGroupDeactivated(
                                                        RuleFlowGroupDeactivatedEvent event,
                                                        WorkingMemory workingMemory ) {
                // TODO Auto-generated method stub

            }

        };

        final List wmList = new ArrayList();
        final WorkingMemoryEventListener workingMemoryListener = new WorkingMemoryEventListener() {

            public void objectInserted( ObjectInsertedEvent event ) {
                wmList.add( event );
            }

            public void objectUpdated( ObjectUpdatedEvent event ) {
                wmList.add( event );
            }

            public void objectRetracted( ObjectRetractedEvent event ) {
                wmList.add( event );
            }

        };

        wm.addEventListener( workingMemoryListener );

        final Cheese stilton = new Cheese( "stilton",
                                           15 );
        final Cheese cheddar = new Cheese( "cheddar",
                                           17 );

        final FactHandle stiltonHandle = wm.insert( stilton );

        final ObjectInsertedEvent oae = (ObjectInsertedEvent) wmList.get( 0 );
        assertSame( stiltonHandle,
                    oae.getFactHandle() );

        wm.update( stiltonHandle,
                   stilton );
        final ObjectUpdatedEvent ome = (ObjectUpdatedEvent) wmList.get( 1 );
        assertSame( stiltonHandle,
                    ome.getFactHandle() );

        wm.retract( stiltonHandle );
        final ObjectRetractedEvent ore = (ObjectRetractedEvent) wmList.get( 2 );
        assertSame( stiltonHandle,
                    ore.getFactHandle() );

        wm.insert( cheddar );
    }

    @Test
    public void testImplicitDeclarations() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_implicitDeclarations.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );
        workingMemory.setGlobal( "factor",
                                 new Double( 1.2 ) );

        final Cheese cheese = new Cheese( "stilton",
                                          10 );
        workingMemory.insert( cheese );

        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
    }

    @Test
    public void testMVELImplicitWithFrom() {
        String str = "" +
                     "package org.test \n" +
                     "import java.util.List \n" +
                     "global java.util.List list \n" +
                     "global java.util.List list2 \n" +
                     "rule \"show\" dialect \"mvel\" \n" +
                     "when  \n" +
                     "    $m : List( eval( size == 0 ) ) from [list] \n" +
                     "then \n" +
                     "    list2.add('r1'); \n" +
                     "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "list2",
                            list );

        ksession.fireAllRules();

        assertEquals( "r1",
                      list.get( 0 ) );
    }

    @Test
    public void testJavaImplicitWithFrom() {
        String str = "" +
                     "package org.test \n" +
                     "import java.util.List \n" +
                     "global java.util.List list \n" +
                     "global java.util.List list2 \n" +
                     "rule \"show\" dialect \"java\" \n" +
                     "when  \n" +
                     "    $m : List( eval( size == 0 )  ) from [list] \n" +
                     "then \n" +
                     "    list2.add('r1'); \n" +
                     "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "list2",
                            list );

        ksession.fireAllRules();

        assertEquals( "r1",
                      list.get( 0 ) );
    }

    @Test
    public void testCastingInsideEvals() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_castsInsideEval.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory.setGlobal( "value",
                                 new Integer( 20 ) );

        workingMemory.fireAllRules();
    }

    @Test
    public void testMemberOfAndNotMemberOf() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_memberOf.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           12 );
        final Cheese muzzarela = new Cheese( "muzzarela",
                                             10 );
        final Cheese brie = new Cheese( "brie",
                                        15 );
        workingMemory.insert( stilton );
        workingMemory.insert( muzzarela );

        final Cheesery cheesery = new Cheesery();
        cheesery.getCheeses().add( stilton.getType() );
        cheesery.getCheeses().add( brie.getType() );
        workingMemory.insert( cheesery );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );

        assertEquals( stilton,
                      list.get( 0 ) );
        assertEquals( muzzarela,
                      list.get( 1 ) );
    }

    @Test
    public void testContainsInArray() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_contains_in_array.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Primitives p = new Primitives();
        p.setStringArray( new String[]{"test1", "test3"} );
        workingMemory.insert( p );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );

        assertEquals( "ok1",
                      list.get( 0 ) );
        assertEquals( "ok2",
                      list.get( 1 ) );
    }

    @Test
    public void testNodeSharingNotExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_nodeSharingNotExists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( "rule1",
                      list.get( 0 ) );

        workingMemory.insert( new Cheese( "stilton",
                                          10 ) );
        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );

        assertEquals( "rule2",
                      list.get( 1 ) );

    }

    @Test
    public void testNullBinding() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_nullBindings.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.insert( new Person( "bob" ) );
        workingMemory.insert( new Person( null ) );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( "OK",
                      list.get( 0 ) );

    }

    @Test
    public void testModifyRetractWithFunction() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RetractModifyWithFunction.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final AbstractWorkingMemory workingMemory = (AbstractWorkingMemory) ruleBase.newStatefulSession();

        final Cheese stilton = new Cheese( "stilton",
                                           7 );
        final Cheese muzzarella = new Cheese( "muzzarella",
                                              9 );
        final int sum = stilton.getPrice() + muzzarella.getPrice();
        final FactHandle stiltonHandle = workingMemory.insert( stilton );
        final FactHandle muzzarellaHandle = workingMemory.insert( muzzarella );

        workingMemory.fireAllRules();

        assertEquals( sum,
                      stilton.getPrice() );
        assertEquals( 1,
                      workingMemory.getObjectStore().size() );
        assertNotNull( workingMemory.getObject( stiltonHandle ) );
        assertNotNull( workingMemory.getFactHandle( stilton ) );

        assertNull( workingMemory.getObject( muzzarellaHandle ) );
        assertNull( workingMemory.getFactHandle( muzzarella ) );

    }

    @Test
    public void testConstraintConnectors() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConstraintConnectors.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        final Person youngChili1 = new Person( "young chili1" );
        youngChili1.setAge( 12 );
        youngChili1.setHair( "blue" );
        final Person youngChili2 = new Person( "young chili2" );
        youngChili2.setAge( 25 );
        youngChili2.setHair( "purple" );

        final Person chili1 = new Person( "chili1" );
        chili1.setAge( 35 );
        chili1.setHair( "red" );

        final Person chili2 = new Person( "chili2" );
        chili2.setAge( 38 );
        chili2.setHair( "indigigo" );

        final Person oldChili1 = new Person( "old chili1" );
        oldChili1.setAge( 45 );
        oldChili1.setHair( "green" );

        final Person oldChili2 = new Person( "old chili2" );
        oldChili2.setAge( 48 );
        oldChili2.setHair( "blue" );

        final Person veryold = new Person( "very old" );
        veryold.setAge( 99 );
        veryold.setHair( "gray" );

        workingMemory.insert( youngChili1 );
        workingMemory.insert( youngChili2 );
        workingMemory.insert( chili1 );
        workingMemory.insert( chili2 );
        workingMemory.insert( oldChili1 );
        workingMemory.insert( oldChili2 );
        workingMemory.insert( veryold );

        workingMemory.fireAllRules();

        assertEquals( 4,
                      results.size() );
        assertEquals( chili1,
                      results.get( 0 ) );
        assertEquals( oldChili1,
                      results.get( 1 ) );
        assertEquals( youngChili1,
                      results.get( 2 ) );
        assertEquals( veryold,
                      results.get( 3 ) );
    }

    @Test
    public void testConnectorsAndOperators() throws Exception {
        final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ConstraintConnectorsAndOperators.drl" ) );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new StockTick( 1,
                                        "RHT",
                                        10,
                                        1000 ) );
        ksession.insert( new StockTick( 2,
                                        "IBM",
                                        10,
                                        1100 ) );
        final int fired = ksession.fireAllRules();

        assertEquals( 1,
                      fired );
    }

    @Test
    public void testConstraintConnectorOr() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_ConstraintConnectorOr.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<Person> results = new ArrayList<Person>();
        ksession.setGlobal( "results",
                            results );

        final Person mark = new Person( "Mark" );
        mark.setAlive( true );
        mark.setHappy( true );

        final Person bush = new Person( "Bush" );
        bush.setAlive( true );
        bush.setHappy( false );

        final Person conan = new Person( "Conan" );
        conan.setAlive( false );
        conan.setHappy( true );

        final Person nero = new Person( "Nero" );
        nero.setAlive( false );
        nero.setHappy( false );

        ksession.insert( mark );
        ksession.insert( bush );
        ksession.insert( conan );
        ksession.insert( nero );

        ksession.fireAllRules();

        assertEquals( 3,
                      results.size() );
        assertTrue( results.contains( mark ) );
        assertTrue( results.contains( bush ) );
        assertTrue( results.contains( conan ) );
    }

    @Test
    public void testMatchesNotMatchesCheese() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MatchesNotMatches.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           12 );
        final Cheese stilton2 = new Cheese( "stilton2",
                                            12 );
        final Cheese agedStilton = new Cheese( "aged stilton",
                                               12 );
        final Cheese brie = new Cheese( "brie",
                                        10 );
        final Cheese brie2 = new Cheese( "brie2",
                                         10 );
        final Cheese muzzarella = new Cheese( "muzzarella",
                                              10 );
        final Cheese muzzarella2 = new Cheese( "muzzarella2",
                                               10 );
        final Cheese provolone = new Cheese( "provolone",
                                             10 );
        final Cheese provolone2 = new Cheese( "another cheese (provolone)",
                                              10 );
        workingMemory.insert( stilton );
        workingMemory.insert( stilton2 );
        workingMemory.insert( agedStilton );
        workingMemory.insert( brie );
        workingMemory.insert( brie2 );
        workingMemory.insert( muzzarella );
        workingMemory.insert( muzzarella2 );
        workingMemory.insert( provolone );
        workingMemory.insert( provolone2 );

        workingMemory.fireAllRules();

        System.out.println( list.toString() );
        assertEquals( 4,
                      list.size() );

        assertEquals( stilton,
                      list.get( 0 ) );
        assertEquals( brie,
                      list.get( 1 ) );
        assertEquals( agedStilton,
                      list.get( 2 ) );
        assertEquals( provolone,
                      list.get( 3 ) );
    }

    @Test
    public void testAutomaticBindings() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_AutoBindings.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Person bob = new Person( "bob",
                                       "stilton" );
        final Cheese stilton = new Cheese( "stilton",
                                           12 );
        workingMemory.insert( bob );
        workingMemory.insert( stilton );

        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );

        assertEquals( bob,
                      list.get( 0 ) );
    }

    @Test
    public void testMatchesMVEL() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MatchesMVEL.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final StatefulSession session = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        session.setGlobal( "results",
                           results );

        Map map = new HashMap();
        map.put( "content",
                 "hello ;=" );
        session.insert( map );

        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
    }

    @Test
    public void testMatchesMVEL2() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_MatchesMVEL2.drl" );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Map map = new HashMap();
        map.put( "content",
                 "String with . and (routine)" );
        ksession.insert( map );
        int fired = ksession.fireAllRules();

        assertEquals( 2,
                      fired );
    }

    @Test
    public void testMatchesMVEL3() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_MatchesMVEL2.drl" );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Map map = new HashMap();
        map.put( "content",
                 "String with . and ()" );
        ksession.insert( map );
        int fired = ksession.fireAllRules();

        assertEquals( 1,
                      fired );
    }

    @Test
    public void testAutomaticBindingsErrors() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_AutoBindingsErrors.drl" ) ) );
        final Package pkg = builder.getPackage();

        assertNotNull( pkg.getErrorSummary() );
    }

    @Test
    public void testQualifiedFieldReference() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_QualifiedFieldReference.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Person bob = new Person( "bob",
                                       "stilton" );
        final Cheese stilton = new Cheese( "stilton",
                                           12 );
        workingMemory.insert( bob );
        workingMemory.insert( stilton );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( bob,
                      list.get( 0 ) );
    }

    @Test
    public void testEvalRewrite() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EvalRewrite.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Order order1 = new Order( 10,
                                        "Bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );
        order1.addItem( item11 );
        order1.addItem( item12 );
        final Order order2 = new Order( 11,
                                        "Bob" );
        final OrderItem item21 = new OrderItem( order2,
                                                1 );
        final OrderItem item22 = new OrderItem( order2,
                                                2 );
        order2.addItem( item21 );
        order2.addItem( item22 );
        final Order order3 = new Order( 12,
                                        "Bob" );
        final OrderItem item31 = new OrderItem( order3,
                                                1 );
        final OrderItem item32 = new OrderItem( order3,
                                                2 );
        order3.addItem( item31 );
        order3.addItem( item32 );
        final Order order4 = new Order( 13,
                                        "Bob" );
        final OrderItem item41 = new OrderItem( order4,
                                                1 );
        final OrderItem item42 = new OrderItem( order4,
                                                2 );
        order4.addItem( item41 );
        order4.addItem( item42 );
        workingMemory.insert( order1 );
        workingMemory.insert( item11 );
        workingMemory.insert( item12 );
        workingMemory.insert( order2 );
        workingMemory.insert( item21 );
        workingMemory.insert( item22 );
        workingMemory.insert( order3 );
        workingMemory.insert( item31 );
        workingMemory.insert( item32 );
        workingMemory.insert( order4 );
        workingMemory.insert( item41 );
        workingMemory.insert( item42 );

        workingMemory.fireAllRules();

        assertEquals( 5,
                      list.size() );
        assertTrue( list.contains( item11 ) );
        assertTrue( list.contains( item12 ) );
        assertTrue( list.contains( item22 ) );
        assertTrue( list.contains( order3 ) );
        assertTrue( list.contains( order4 ) );

    }

    @Test
    public void testMapAccess() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MapAccess.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        Map map = new HashMap();
        map.put( "name",
                 "Edson" );
        map.put( "surname",
                 "Tirelli" );
        map.put( "age",
                 "28" );

        workingMemory.insert( map );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( map ) );
    }

    @Test
    public void testNoneTypeSafeDeclarations() {
        // same namespace
        String str = "package org.drools\n" +
                     "global java.util.List list\n" +
                     "declare Person\n" +
                     "    @typesafe(false)\n" +
                     "end\n" +
                     "rule testTypeSafe\n dialect \"mvel\" when\n" +
                     "   $p : Person( object.street == 's1' )\n" +
                     "then\n" +
                     "   list.add( $p );\n" +
                     "end\n";

        executeTypeSafeDeclarations( str,
                                     true );

        // different namespace with import
        str = "package org.drools.test\n" +
                "import org.drools.Person\n" +
                "global java.util.List list\n" +
                "declare Person\n" +
                "    @typesafe(false)\n" +
                "end\n" +
                "rule testTypeSafe\n dialect \"mvel\" when\n" +
                "   $p : Person( object.street == 's1' )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end\n";
        executeTypeSafeDeclarations( str,
                                     true );

        // different namespace without import using qualified name
        str = "package org.drools.test\n" +
                "global java.util.List list\n" +
                "declare org.drools.Person\n" +
                "    @typesafe(false)\n" +
                "end\n" +
                "rule testTypeSafe\n dialect \"mvel\" when\n" +
                "   $p : org.drools.Person( object.street == 's1' )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end\n";
        executeTypeSafeDeclarations( str,
                                     true );

        // this should fail as it's not declared non typesafe 
        str = "package org.drools.test\n" +
                "global java.util.List list\n" +
                "declare org.drools.Person\n" +
                "    @typesafe(true)\n" +
                "end\n" +
                "rule testTypeSafe\n dialect \"mvel\" when\n" +
                "   $p : org.drools.Person( object.street == 's1' )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end\n";
        executeTypeSafeDeclarations( str,
                                     false );
    }

    private void executeTypeSafeDeclarations( String str,
                                              boolean mustSucceed ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            if ( mustSucceed ) {
                fail( kbuilder.getErrors().toString() );
            } else {
                return;
            }
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        Address a = new Address( "s1" );
        Person p = new Person( "yoda" );
        p.setObject( a );

        ksession.insert( p );
        ksession.fireAllRules();
        assertEquals( p,
                      list.get( 0 ) );
    }

    // this is an MVEL regression that we need fixed in mvel-2.0.11
    @Test
    public void testMapAccessWithVariable() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MapAccessWithVariable.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        Map map = new HashMap();
        map.put( "name",
                 "Edson" );
        map.put( "surname",
                 "Tirelli" );
        map.put( "age",
                 "28" );

        workingMemory.insert( map );
        workingMemory.insert( "name" );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( map ) );
    }

    @Test
    public void testHalt() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_halt.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        workingMemory.insert( new Integer( 0 ) );
        workingMemory.fireAllRules();

        assertEquals( 10,
                      results.size() );
        for ( int i = 0; i < 10; i++ ) {
            assertEquals( new Integer( i ),
                          results.get( i ) );
        }
    }

    @Test
    public void testFireLimit() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_fireLimit.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        workingMemory.insert( new Integer( 0 ) );
        workingMemory.fireAllRules();

        assertEquals( 20,
                      results.size() );
        for ( int i = 0; i < 20; i++ ) {
            assertEquals( new Integer( i ),
                          results.get( i ) );
        }
        results.clear();

        workingMemory.insert( new Integer( 0 ) );
        workingMemory.fireAllRules( 10 );

        assertEquals( 10,
                      results.size() );
        for ( int i = 0; i < 10; i++ ) {
            assertEquals( new Integer( i ),
                          results.get( i ) );
        }
        results.clear();

        workingMemory.insert( new Integer( 0 ) );
        workingMemory.fireAllRules( -1 );

        assertEquals( 20,
                      results.size() );
        for ( int i = 0; i < 20; i++ ) {
            assertEquals( new Integer( i ),
                          results.get( i ) );
        }
        results.clear();

    }

    @Test
    public void testEqualitySupport() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_equalitySupport.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setAssertBehaviour( RuleBaseConfiguration.AssertBehaviour.EQUALITY );
        RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        PersonWithEquals person = new PersonWithEquals( "bob",
                                                        30 );

        workingMemory.insert( person );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( "mark",
                      results.get( 0 ) );

    }

    @Test
    public void testCharComparisons() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_charComparisons.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        Primitives p1 = new Primitives();
        p1.setCharPrimitive( 'a' );
        p1.setStringAttribute( "b" );
        Primitives p2 = new Primitives();
        p2.setCharPrimitive( 'b' );
        p2.setStringAttribute( "a" );

        workingMemory.insert( p1 );
        workingMemory.insert( p2 );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      results.size() );
        assertEquals( "1",
                      results.get( 0 ) );
        assertEquals( "2",
                      results.get( 1 ) );
        assertEquals( "3",
                      results.get( 2 ) );

    }

    @Test
    public void testAlphaNodeSharing() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_alphaNodeSharing.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setShareAlphaNodes( false );
        RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        Person p1 = new Person( "bob",
                                5 );
        workingMemory.insert( p1 );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( "1",
                      results.get( 0 ) );
        assertEquals( "2",
                      results.get( 1 ) );

    }

    @Test
    public void testFunctionCallingFunctionWithEclipse() throws Exception {
        PackageBuilderConfiguration packageBuilderConfig = new PackageBuilderConfiguration();
        ((JavaDialectConfiguration) packageBuilderConfig.getDialectConfiguration( "java" )).setCompiler( JavaDialectConfiguration.ECLIPSE );

        final PackageBuilder builder = new PackageBuilder( packageBuilderConfig );

        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_functionCallingFunction.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( 12,
                      ((Integer) list.get( 0 )).intValue() );
    }

    @Test
    public void testFunctionCallingFunctionWithJanino() throws Exception {
        PackageBuilderConfiguration packageBuilderConfig = new PackageBuilderConfiguration();
        ((JavaDialectConfiguration) packageBuilderConfig.getDialectConfiguration( "java" )).setCompiler( JavaDialectConfiguration.JANINO );

        final PackageBuilder builder = new PackageBuilder( packageBuilderConfig );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_functionCallingFunction.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( 12,
                      ((Integer) list.get( 0 )).intValue() );
    }

    @Test
    public void testSelfReference2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_SelfReference2.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        workingMemory.insert( new Cheese() );

        workingMemory.fireAllRules();

        assertEquals( 0,
                      results.size() );
    }

    @Test
    public void testSelfJoinWithIndex() {
        String drl = "";
        drl += "package org.test\n";
        drl += "import org.drools.Person\n";
        drl += "global java.util.List list\n";
        drl += "rule test1\n";
        drl += "when\n";
        drl += "   $p1 : Person( $name : name, $age : age )\n";
        drl += "   $p2 : Person( name == $name, age < $age)\n";
        drl += "then\n";
        drl += "    list.add( $p1 );\n";
        drl += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        Person p1 = new Person( "darth",
                                30 );
        org.drools.runtime.rule.FactHandle fh1 = ksession.insert( p1 );

        Person p2 = new Person( "darth",
                                25 );
        org.drools.runtime.rule.FactHandle fh2 = ksession.insert( p2 ); // creates activation.

        p1.setName( "yoda" );
        ksession.update( fh1,
                         p1 ); // creates activation

        ksession.fireAllRules();

        assertEquals( 0,
                      list.size() );
    }

    @Test
    public void testMergingDifferentPackages() throws Exception {
        // using the same builder
        try {
            final PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RuleNameClashes1.drl" ) ) );
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RuleNameClashes2.drl" ) ) );
            assertEquals( 2,
                          builder.getPackages().length );
            Package pkg1 = builder.getPackageRegistry( "org.drools.package1" ).getPackage();
            assertEquals( "rule 1",
                          pkg1.getRules()[0].getName() );

            Package pkg2 = builder.getPackageRegistry( "org.drools.package2" ).getPackage();
            assertEquals( "rule 1",
                          pkg2.getRules()[0].getName() );

        } catch ( PackageMergeException e ) {
            fail( "unexpected exception: " + e.getMessage() );
        } catch ( RuntimeException e ) {
            e.printStackTrace();
            fail( "unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void testSelfJoinAndNotWithIndex() {
        String drl = "";
        drl += "package org.test\n";
        drl += "import org.drools.Person\n";
        drl += "global java.util.List list\n";
        drl += "rule test1\n";
        drl += "when\n";
        drl += "   $p1 : Person( )\n";
        drl += "     not Person( name == $p1.name, age < $p1.age )\n";
        drl += "   $p2 : Person( name == $p1.name, likes != $p1.likes, age > $p1.age)\n";
        drl += "     not Person( name == $p1.name, likes == $p2.likes, age < $p2.age )\n";
        drl += "then\n";
        drl += "    System.out.println( $p1 + \":\" + $p2 );\n";
        drl += "    list.add( $p1 );\n";
        drl += "    list.add( $p2 );\n";
        drl += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        Person p0 = new Person( "yoda",
                                0 );
        p0.setLikes( "cheddar" );
        org.drools.runtime.rule.FactHandle fh0 = ksession.insert( p0 );

        Person p1 = new Person( "darth",
                                15 );
        p1.setLikes( "cheddar" );
        org.drools.runtime.rule.FactHandle fh1 = ksession.insert( p1 );

        Person p2 = new Person( "darth",
                                25 );
        p2.setLikes( "cheddar" );
        org.drools.runtime.rule.FactHandle fh2 = ksession.insert( p2 ); // creates activation.

        Person p3 = new Person( "darth",
                                30 );
        p3.setLikes( "brie" );
        org.drools.runtime.rule.FactHandle fh3 = ksession.insert( p3 );

        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertSame( p1,
                    list.get( 0 ) );
        assertSame( p3,
                    list.get( 1 ) );

        p1.setName( "yoda" );
        ksession.update( fh1,
                         p1 ); // creates activation

        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );
        assertSame( p2,
                    list.get( 2 ) );
        assertSame( p3,
                    list.get( 3 ) );
    }

    @Test
    public void testMergingDifferentPackages2() throws Exception {
        // using different builders
        try {
            final PackageBuilder builder1 = new PackageBuilder();
            builder1.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RuleNameClashes1.drl" ) ) );
            final Package pkg1 = builder1.getPackage();

            assertEquals( 1,
                          pkg1.getRules().length );

            final PackageBuilder builder2 = new PackageBuilder();
            builder2.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RuleNameClashes2.drl" ) ) );
            final Package pkg2 = builder2.getPackage();

            assertEquals( 1,
                          pkg2.getRules().length );

            RuleBase ruleBase = getRuleBase();
            ruleBase.addPackage( pkg1 );
            ruleBase.addPackage( pkg2 );
            ruleBase = SerializationHelper.serializeObject( ruleBase );
            final WorkingMemory workingMemory = ruleBase.newStatefulSession();

            final List results = new ArrayList();
            workingMemory.setGlobal( "results",
                                     results );

            workingMemory.insert( new Cheese( "stilton",
                                              10 ) );
            workingMemory.insert( new Cheese( "brie",
                                              5 ) );

            workingMemory.fireAllRules();

            assertEquals( results.toString(),
                          2,
                          results.size() );
            assertTrue( results.contains( "p1.r1" ) );
            assertTrue( results.contains( "p2.r1" ) );

        } catch ( PackageMergeException e ) {
            fail( "Should not raise exception when merging different packages into the same rulebase: " + e.getMessage() );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void testMergePackageWithSameRuleNames() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MergePackageWithSameRuleNames1.drl" ) ) );
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MergePackageWithSameRuleNames2.drl" ) ) );
        ruleBase.addPackage( builder.getPackage() );

        StatefulSession session = ruleBase.newStatefulSession();
        final List results = new ArrayList();
        session.setGlobal( "results",
                           results );

        session.fireAllRules();

        assertEquals( 1,
                      results.size() );

        assertEquals( "rule1 for the package2",
                      results.get( 0 ) );
    }

    public static class Foo {
        public String aValue = "";

    }

    @Test
    public void testRuleRemovalWithJoinedRootPattern() {
        String str = "";
        str += "package org.drools \n";
        str += "rule rule1 \n";
        str += "when \n";
        str += "  String() \n";
        str += "  Person() \n";
        str += "then \n";
        str += "end  \n";

        str += "rule rule2 \n";
        str += "when \n";
        str += "  String() \n";
        str += "  Cheese() \n";
        str += "then \n";
        str += "end  \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        DefaultFactHandle handle = (DefaultFactHandle) ksession.insert( "hello" );
        LeftTuple leftTuple = handle.getFirstLeftTuple();
        assertNotNull( leftTuple );
        assertNotNull( leftTuple.getLeftParentNext() );

        kbase.removeRule( "org.drools",
                          "rule2" );

        leftTuple = handle.getFirstLeftTuple();
        assertNotNull( leftTuple );
        assertNull( leftTuple.getLeftParentNext() );

    }

    // JBRULES-1808
    @Test
    public void testKnowledgeHelperFixerInStrings() {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  no-loop true ";
        str += "when \n";
        str += "  $fact : String() \n";
        str += "then \n";
        str += "  list.add(\"This is an update()\"); \n";
        str += "  list.add(\"This is an update($fact)\"); \n";
        str += "  update($fact); \n";
        str += "end  \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        ksession.insert( "hello" );
        ksession.fireAllRules();

        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertEquals( "This is an update()",
                      list.get( 0 ) );
        assertEquals( "This is an update($fact)",
                      list.get( 1 ) );
    }

    @Test
    public void testRuleReplacement() throws Exception {
        // test rule replacement
        final PackageBuilder builder1 = new PackageBuilder();
        builder1.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RuleNameClashes1.drl" ) ) );
        builder1.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RuleNameClashes3.drl" ) ) );
        final Package pkg1 = builder1.getPackage();

        assertEquals( 1,
                      pkg1.getRules().length );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        workingMemory.insert( new Cheese( "stilton",
                                          10 ) );
        workingMemory.insert( new Cheese( "brie",
                                          5 ) );

        workingMemory.fireAllRules();

        assertEquals( results.toString(),
                      0,
                      results.size() );

        workingMemory.insert( new Cheese( "muzzarella",
                                          7 ) );

        workingMemory.fireAllRules();

        assertEquals( results.toString(),
                      1,
                      results.size() );
        assertTrue( results.contains( "p1.r3" ) );
    }

    @Test
    public void testBindingsOnConnectiveExpressions() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_bindings.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        workingMemory.insert( new Cheese( "stilton",
                                          15 ) );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( "stilton",
                      results.get( 0 ) );
        assertEquals( new Integer( 15 ),
                      results.get( 1 ) );
    }

    @Test
    public void testMultipleFroms() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_multipleFroms.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        final Cheesery cheesery = new Cheesery();
        cheesery.addCheese( new Cheese( "stilton",
                                        15 ) );
        cheesery.addCheese( new Cheese( "brie",
                                        10 ) );

        workingMemory.setGlobal( "cheesery",
                                 cheesery );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( 2,
                      ((List) results.get( 0 )).size() );
        assertEquals( 2,
                      ((List) results.get( 1 )).size() );
    }

    @Test
    public void testNullHashing() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NullHashing.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        workingMemory.insert( new Cheese( "stilton",
                                          15 ) );
        workingMemory.insert( new Cheese( "",
                                          10 ) );
        workingMemory.insert( new Cheese( null,
                                          8 ) );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      results.size() );
    }

    @Test
    public void testDefaultBetaConstrains() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DefaultBetaConstraint.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );
        final FirstClass first = new FirstClass( "1",
                                                 "2",
                                                 "3",
                                                 "4",
                                                 "5" );
        final FactHandle handle = workingMemory.insert( first );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( "NOT",
                      results.get( 0 ) );

        workingMemory.insert( new SecondClass() );
        workingMemory.update( handle,
                              first );
        workingMemory.fireAllRules();
        assertEquals( 2,
                      results.size() );
        assertEquals( "NOT",
                      results.get( 1 ) );

        workingMemory.update( handle,
                              first );
        workingMemory.insert( new SecondClass( null,
                                               "2",
                                               "3",
                                               "4",
                                               "5" ) );
        workingMemory.fireAllRules();
        assertEquals( 3,
                      results.size() );
        assertEquals( "NOT",
                      results.get( 2 ) );

        workingMemory.update( handle,
                              first );
        workingMemory.insert( new SecondClass( "1",
                                               null,
                                               "3",
                                               "4",
                                               "5" ) );
        workingMemory.fireAllRules();
        assertEquals( 4,
                      results.size() );
        assertEquals( "NOT",
                      results.get( 3 ) );

        workingMemory.update( handle,
                              first );
        workingMemory.insert( new SecondClass( "1",
                                               "2",
                                               null,
                                               "4",
                                               "5" ) );
        workingMemory.fireAllRules();
        assertEquals( 5,
                      results.size() );
        assertEquals( "NOT",
                      results.get( 4 ) );

        workingMemory.update( handle,
                              first );
        workingMemory.insert( new SecondClass( "1",
                                               "2",
                                               "3",
                                               null,
                                               "5" ) );
        workingMemory.fireAllRules();
        assertEquals( 6,
                      results.size() );
        assertEquals( "NOT",
                      results.get( 5 ) );

        workingMemory.update( handle,
                              first );
        workingMemory.insert( new SecondClass( "1",
                                               "2",
                                               "3",
                                               "4",
                                               null ) );
        workingMemory.fireAllRules();
        assertEquals( 7,
                      results.size() );
        assertEquals( "NOT",
                      results.get( 6 ) );

        workingMemory.insert( new SecondClass( "1",
                                               "2",
                                               "3",
                                               "4",
                                               "5" ) );
        workingMemory.update( handle,
                              first );
        workingMemory.fireAllRules();
        assertEquals( 8,
                      results.size() );
        assertEquals( "EQUALS",
                      results.get( 7 ) );

    }

    @Test
    public void testBooleanWrapper() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_BooleanWrapper.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        Primitives p1 = new Primitives();
        workingMemory.insert( p1 );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      results.size() );

        Primitives p2 = new Primitives();
        p2.setBooleanWrapper( Boolean.FALSE );
        workingMemory.insert( p2 );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      results.size() );

        Primitives p3 = new Primitives();
        p3.setBooleanWrapper( Boolean.TRUE );
        workingMemory.insert( p3 );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );

    }

    @Test
    public void testCrossProductRemovingIdentityEquals() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( MiscTest.class.getResourceAsStream( "test_CrossProductRemovingIdentityEquals.drl" ) ) );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        RuleBase rb = RuleBaseFactory.newRuleBase( conf );
        rb.addPackage( builder.getPackage() );
        rb = SerializationHelper.serializeObject( rb );
        StatefulSession session = rb.newStatefulSession();

        List list1 = new ArrayList();
        List list2 = new ArrayList();

        session.setGlobal( "list1",
                           list1 );
        session.setGlobal( "list2",
                           list2 );

        SpecialString first42 = new SpecialString( "42" );
        SpecialString second43 = new SpecialString( "43" );
        SpecialString world = new SpecialString( "World" );
        session.insert( world );
        session.insert( first42 );
        session.insert( second43 );

        //System.out.println( "Firing rules ..." );

        session.fireAllRules();

        assertEquals( 6,
                      list1.size() );
        assertEquals( 6,
                      list2.size() );

        assertEquals( first42,
                      list1.get( 0 ) );
        assertEquals( world,
                      list1.get( 1 ) );
        assertEquals( second43,
                      list1.get( 2 ) );
        assertEquals( second43,
                      list1.get( 3 ) );
        assertEquals( world,
                      list1.get( 4 ) );
        assertEquals( first42,
                      list1.get( 5 ) );

        assertEquals( second43,
                      list2.get( 0 ) );
        assertEquals( second43,
                      list2.get( 1 ) );
        assertEquals( first42,
                      list2.get( 2 ) );
        assertEquals( world,
                      list2.get( 3 ) );
        assertEquals( first42,
                      list2.get( 4 ) );
        assertEquals( world,
                      list2.get( 5 ) );
    }

    @Test
    public void testIterateObjects() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_IterateObjects.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        workingMemory.insert( new Cheese( "stilton",
                                          10 ) );

        workingMemory.fireAllRules();

        Iterator events = workingMemory.iterateObjects( new ClassObjectFilter( PersonInterface.class ) );

        assertTrue( events.hasNext() );
        assertEquals( 1,
                      results.size() );
        assertEquals( results.get( 0 ),
                      events.next() );
    }

    @Test
    public void testNotInStatelessSession() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NotInStatelessSession.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );
        RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        StatelessSession session = ruleBase.newStatelessSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session.execute( "not integer" );
        assertEquals( "not integer",
                      list.get( 0 ) );
    }

    @Test
    public void testDynamicallyAddInitialFactRule() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        String rule = "package org.drools.test\n" +
                      "global java.util.List list\n" +
                      "rule xxx when\n" +
                      "   i:Integer()\n" +
                      "then\n" +
                      "   list.add(i);\n" +
                      "end";
        builder.addPackageFromDrl( new StringReader( rule ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        session.insert( new Integer( 5 ) );
        session.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );

        builder = new PackageBuilder();
        rule = "package org.drools.test\n" +
                "global java.util.List list\n" +
                "rule xxx when\n" +
                "then\n" +
                "   list.add(\"x\");\n" +
                "end";
        builder.addPackageFromDrl( new StringReader( rule ) );
        pkg = builder.getPackage();

        // Make sure that this rule is fired as the Package is updated, it also tests that InitialFactImpl is still in the network
        // even though the first rule didn't use it.
        ruleBase.addPackage( pkg );

        session.fireAllRules();

        assertEquals( "x",
                      list.get( 1 ) );

    }

    @Test
    public void testEvalRewriteWithSpecialOperators() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EvalRewriteWithSpecialOperators.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Order order1 = new Order( 10,
                                        "Bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );
        order1.addItem( item11 );
        order1.addItem( item12 );
        final Order order2 = new Order( 11,
                                        "Bob" );
        final OrderItem item21 = new OrderItem( order2,
                                                1 );
        final OrderItem item22 = new OrderItem( order2,
                                                2 );
        order2.addItem( item21 );
        order2.addItem( item22 );
        final Order order3 = new Order( 12,
                                        "Bob" );
        final OrderItem item31 = new OrderItem( order3,
                                                1 );
        final OrderItem item32 = new OrderItem( order3,
                                                2 );
        final OrderItem item33 = new OrderItem( order3,
                                                3 );
        order3.addItem( item31 );
        order3.addItem( item32 );
        order3.addItem( item33 );
        final Order order4 = new Order( 13,
                                        "Bob" );
        final OrderItem item41 = new OrderItem( order4,
                                                1 );
        final OrderItem item42 = new OrderItem( order4,
                                                2 );
        order4.addItem( item41 );
        order4.addItem( item42 );
        final Order order5 = new Order( 14,
                                        "Mark" );
        final OrderItem item51 = new OrderItem( order5,
                                                1 );
        final OrderItem item52 = new OrderItem( order5,
                                                2 );
        order5.addItem( item51 );
        order5.addItem( item52 );
        workingMemory.insert( order1 );
        workingMemory.insert( item11 );
        workingMemory.insert( item12 );
        workingMemory.insert( order2 );
        workingMemory.insert( item21 );
        workingMemory.insert( item22 );
        workingMemory.insert( order3 );
        workingMemory.insert( item31 );
        workingMemory.insert( item32 );
        workingMemory.insert( item33 );
        workingMemory.insert( order4 );
        workingMemory.insert( item41 );
        workingMemory.insert( item42 );
        workingMemory.insert( order5 );
        workingMemory.insert( item51 );
        workingMemory.insert( item52 );

        workingMemory.fireAllRules();

        assertEquals( 9,
                      list.size() );
        int index = 0;
        assertEquals( item11,
                      list.get( index++ ) );
        assertEquals( item12,
                      list.get( index++ ) );
        assertEquals( item21,
                      list.get( index++ ) );
        assertEquals( item22,
                      list.get( index++ ) );
        assertEquals( item31,
                      list.get( index++ ) );
        assertEquals( item33,
                      list.get( index++ ) );
        assertEquals( item41,
                      list.get( index++ ) );
        assertEquals( order5,
                      list.get( index++ ) );
        assertEquals( order5,
                      list.get( index++ ) );

    }

    @Test
    public void testImportColision() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        final PackageBuilder builder2 = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "nested1.drl" ) ) );
        builder2.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "nested2.drl" ) ) );
        final Package pkg = builder.getPackage();
        final Package pkg2 = builder2.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase.addPackage( pkg2 );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory.insert( new FirstClass() );
        workingMemory.insert( new SecondClass() );
        workingMemory.insert( new FirstClass.AlternativeKey() );
        workingMemory.insert( new SecondClass.AlternativeKey() );

        workingMemory.fireAllRules();
    }

    @Test
    public void testAutovivificationOfVariableRestrictions() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_AutoVivificationVR.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        workingMemory.insert( new Cheese( "stilton",
                                          10,
                                          8 ) );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      results.size() );
    }

    @Test
    public void testShadowProxyOnCollections() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ShadowProxyOnCollections.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        final Cheesery cheesery = new Cheesery();
        workingMemory.insert( cheesery );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( 1,
                      cheesery.getCheeses().size() );
        assertEquals( results.get( 0 ),
                      cheesery.getCheeses().get( 0 ) );
    }

    @Test
    public void testShadowProxyOnCollections2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ShadowProxyOnCollections2.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final StatefulSession workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        List list = new ArrayList();
        list.add( "example1" );
        list.add( "example2" );

        MockPersistentSet mockPersistentSet = new MockPersistentSet( false );
        mockPersistentSet.addAll( list );
        org.drools.ObjectWithSet objectWithSet = new ObjectWithSet();
        objectWithSet.setSet( mockPersistentSet );

        workingMemory.insert( objectWithSet );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( "show",
                      objectWithSet.getMessage() );
    }

    @Test
    public void testNestedAccessors() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NestedAccessors.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Order order1 = new Order( 11,
                                        "Bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );
        order1.addItem( item11 );
        order1.addItem( item12 );

        workingMemory.insert( order1 );
        workingMemory.insert( item11 );
        workingMemory.insert( item12 );

        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        final Order order2 = new Order( 12,
                                        "Mark" );
        Order.OrderStatus status = new Order.OrderStatus();
        status.setActive( true );
        order2.setStatus( status );
        final OrderItem item21 = new OrderItem( order2,
                                                1 );
        final OrderItem item22 = new OrderItem( order2,
                                                2 );
        order1.addItem( item21 );
        order1.addItem( item22 );

        workingMemory.insert( order2 );
        workingMemory.insert( item21 );
        workingMemory.insert( item22 );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertSame( item21,
                    list.get( 0 ) );
        assertSame( item22,
                    list.get( 1 ) );
    }

    @Test
    public void testWorkingMemoryLoggerWithUnbalancedBranches() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Logger.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory wm = ruleBase.newStatefulSession();

        try {
            wm.fireAllRules();

            wm.insert( new Cheese( "a",
                                   10 ) );
            wm.insert( new Cheese( "b",
                                   11 ) );

            wm.fireAllRules();

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "No exception should be raised " );
        }

    }

    @Test
    public void testFromNestedAccessors() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FromNestedAccessors.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Order order1 = new Order( 11,
                                        "Bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );
        order1.addItem( item11 );
        order1.addItem( item12 );

        workingMemory.insert( order1 );
        workingMemory.insert( item11 );
        workingMemory.insert( item12 );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertSame( order1.getStatus(),
                    list.get( 0 ) );
    }

    @Test
    public void testFromArrayIteration() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FromArrayIteration.drl" ) ) );

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final WorkingMemory session = ruleBase.newStatefulSession();
        List list = new ArrayList();

        session.setGlobal( "list",
                           list );
        session.insert( new DomainObjectHolder() );

        session.fireAllRules();

        assertEquals( 3,
                      list.size() );

        assertEquals( "Message3",
                      list.get( 0 ) );
        assertEquals( "Message2",
                      list.get( 1 ) );
        assertEquals( "Message1",
                      list.get( 2 ) );

    }

    @Test
    public void testSubNetworks() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_SubNetworks.drl" ) ) );

        RuleBase ruleBase = getRuleBase();

        try {
            ruleBase.addPackage( builder.getPackage() );
            ruleBase = SerializationHelper.serializeObject( ruleBase );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception!" );
        }

    }

    @Test
    public void testFinalClass() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FinalClass.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final PersonFinal bob = new PersonFinal();
        bob.setName( "bob" );
        bob.setStatus( null );

        workingMemory.insert( bob );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        // Dynamic addition of rules which use the final class are not supported yet
        //        final PackageBuilder builder2 = new PackageBuilder();
        //        builder2.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FinalClass2.drl" ) ) );
        //        ruleBase.addPackage( builder2.getPackage() );
        //
        //        // it will automatically fire the rule
        //        assertEquals( 2,
        //                      list.size() );
    }

    @Test
    public void testEvalRewriteMatches() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EvalRewriteMatches.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Order order1 = new Order( 14,
                                        "Mark" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );
        order1.addItem( item11 );
        order1.addItem( item12 );

        workingMemory.insert( order1 );
        workingMemory.insert( item11 );
        workingMemory.insert( item12 );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( item11 ) );
        assertTrue( list.contains( item12 ) );
    }

    @Test
    public void testConsequenceBuilderException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConsequenceBuilderException.drl" ) ) );

        assertTrue( builder.hasErrors() );
    }

    @Test
    public void testRuntimeTypeCoercion() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RuntimeTypeCoercion.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final PolymorphicFact fact = new PolymorphicFact( new Integer( 10 ) );
        final FactHandle handle = workingMemory.insert( fact );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertEquals( fact.getData(),
                      list.get( 0 ) );

        fact.setData( "10" );
        workingMemory.update( handle,
                              fact );
        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertEquals( fact.getData(),
                      list.get( 1 ) );

        try {
            fact.setData( new Boolean( true ) );
            workingMemory.update( handle,
                                  fact );
            fail( "Should not allow to compare < with a Boolean object" );
        } catch ( ClassCastException cce ) {
            // success, as can't use "<" to compare to a boolean
        }

    }

    @Test
    public void testRuntimeTypeCoercion2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RuntimeTypeCoercion2.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Primitives fact = new Primitives();
        fact.setBooleanPrimitive( true );
        fact.setBooleanWrapper( new Boolean( true ) );
        fact.setObject( new Boolean( true ) );
        fact.setCharPrimitive( 'X' );
        final FactHandle handle = workingMemory.insert( fact );

        workingMemory.fireAllRules();

        int index = 0;
        assertEquals( list.toString(),
                      4,
                      list.size() );
        assertEquals( "boolean",
                      list.get( index++ ) );
        assertEquals( "boolean wrapper",
                      list.get( index++ ) );
        assertEquals( "boolean object",
                      list.get( index++ ) );
        assertEquals( "char",
                      list.get( index++ ) );

        fact.setBooleanPrimitive( false );
        fact.setBooleanWrapper( null );
        fact.setCharPrimitive( '\0' );
        fact.setObject( new Character( 'X' ) );
        workingMemory.update( handle,
                              fact );
        workingMemory.fireAllRules();
        assertEquals( 5,
                      list.size() );
        assertEquals( "char object",
                      list.get( index++ ) );

        fact.setObject( null );
        workingMemory.update( handle,
                              fact );
        workingMemory.fireAllRules();
        assertEquals( 6,
                      list.size() );
        assertEquals( "null object",
                      list.get( index++ ) );

    }

    @Test
    public void testAlphaEvalWithOrCE() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_AlphaEvalWithOrCE.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        FactA a = new FactA();
        a.setField1( "a value" );

        workingMemory.insert( a );
        workingMemory.insert( new FactB() );
        workingMemory.insert( new FactC() );

        workingMemory.fireAllRules();

        assertEquals( "should not have fired",
                      0,
                      list.size() );
    }

    @Test
    public void testModifyRetractAndModifyInsert() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ModifyRetractInsert.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        Person bob = new Person( "Bob" );
        bob.setStatus( "hungry" );
        workingMemory.insert( bob );
        workingMemory.insert( new Cheese() );
        workingMemory.insert( new Cheese() );

        workingMemory.fireAllRules( 2 );

        assertEquals( "should have fired only once",
                      1,
                      list.size() );
    }

    @Test
    public void testAlphaCompositeConstraints() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_AlphaCompositeConstraints.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        Person bob = new Person( "bob",
                                 30 );

        workingMemory.insert( bob );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testModifyBlock() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ModifyBlock.drl" ) ) );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        Person bob = new Person( "Bob" );
        bob.setStatus( "hungry" );

        Cheese c = new Cheese();

        workingMemory.insert( bob );
        workingMemory.insert( c );

        workingMemory.fireAllRules();

        assertEquals( 10,
                      c.getPrice() );
        assertEquals( "fine",
                      bob.getStatus() );
    }

    @Test
    public void testModifyBlockWithFrom() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ModifyBlockWithFrom.drl" ) ) );
        final Package pkg = builder.getPackage();
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        Person bob = new Person( "Bob" );
        Address addr = new Address( "abc" );
        bob.addAddress( addr );

        workingMemory.insert( bob );
        workingMemory.insert( addr );

        workingMemory.fireAllRules();

        // modify worked
        assertEquals( "12345",
                      addr.getZipCode() );
        // chaining worked
        assertEquals( 1,
                      results.size() );
        assertEquals( addr,
                      results.get( 0 ) );
    }

    // this test requires mvel 1.2.19. Leaving it commented until mvel is released.
    @Test
    public void testJavaModifyBlock() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_JavaModifyBlock.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        Person bob = new Person( "Bob",
                                 30 );
        bob.setStatus( "hungry" );
        workingMemory.insert( bob );
        workingMemory.insert( new Cheese() );
        workingMemory.insert( new Cheese() );
        workingMemory.insert( new OuterClass.InnerClass( 1 ) );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertEquals( "full",
                      bob.getStatus() );
        assertEquals( 31,
                      bob.getAge() );
        assertEquals( 2,
                      ((OuterClass.InnerClass) list.get( 1 )).getIntAttr() );
    }

    @Test
    public void testOrCE() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_OrCE.drl" ) ) );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        Package pkg = builder.getPackage();

        pkg = SerializationHelper.serializeObject( pkg );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.insert( new Cheese( "brie",
                                          10 ) );
        workingMemory.insert( new Person( "bob" ) );

        workingMemory.fireAllRules();

        assertEquals( "should have fired once",
                      1,
                      list.size() );
    }

    @Test
    public void testOrWithAndUsingNestedBindings() {
        String str = "";
        str += "package org.drools\n";
        str += "import org.drools.Person\n";
        str += "global java.util.List mlist\n";
        str += "global java.util.List jlist\n";
        str += "rule rule1 dialect \"mvel\" \n";
        str += "when\n";
        str += "$a : Person( name == \"a\" )\n";
        str += "  (or $b : Person( name == \"b1\" )\n";
        str += "      (and $p : Person( name == \"p2\" )\n";
        str += "           $b : Person( name == \"b2\" ) )\n";
        str += "      (and $p : Person( name == \"p3\" )\n";
        str += "           $b : Person( name == \"b3\" ) )\n";
        str += "   )\n ";
        str += "then\n";
        str += "   mlist.add( $b );\n";
        str += "end\n";
        str += "rule rule2 dialect \"java\" \n";
        str += "when\n";
        str += "$a : Person( name == \"a\" )\n";
        str += "  (or $b : Person( name == \"b1\" )\n";
        str += "      (and $p : Person( name == \"p2\" )\n";
        str += "           $b : Person( name == \"b2\" ) )\n";
        str += "      (and $p : Person( name == \"p3\" )\n";
        str += "           $b : Person( name == \"b3\" ) )\n";
        str += "   )\n ";
        str += "then\n";
        str += "   jlist.add( $b );\n";
        str += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        Person a = new Person( "a" );
        Person b1 = new Person( "b1" );
        Person p2 = new Person( "p2" );
        Person b2 = new Person( "b2" );
        Person p3 = new Person( "p3" );
        Person b3 = new Person( "b3" );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List mlist = new ArrayList();
        List jlist = new ArrayList();

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "mlist",
                            mlist );
        ksession.setGlobal( "jlist",
                            jlist );
        ksession.insert( a );
        ksession.insert( b1 );
        ksession.fireAllRules();
        assertEquals( b1,
                      mlist.get( 0 ) );
        assertEquals( b1,
                      jlist.get( 0 ) );

        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "mlist",
                            mlist );
        ksession.setGlobal( "jlist",
                            jlist );
        ksession.insert( a );
        ksession.insert( b2 );
        ksession.insert( p2 );
        ksession.fireAllRules();
        assertEquals( b2,
                      mlist.get( 1 ) );
        assertEquals( b2,
                      jlist.get( 1 ) );

        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "mlist",
                            mlist );
        ksession.setGlobal( "jlist",
                            jlist );
        ksession.insert( a );
        ksession.insert( b3 );
        ksession.insert( p3 );
        ksession.fireAllRules();
        assertEquals( b3,
                      mlist.get( 2 ) );
        assertEquals( b3,
                      jlist.get( 2 ) );

    }

    @Test
    public void testFieldBindingOnWrongFieldName() {
        //JBRULES-2527

        String str = "";
        str += "package org.drools\n";
        str += "import org.drools.Person\n";
        str += "global java.util.List mlist\n";
        str += "rule rule1 \n";
        str += "when\n";
        str += "   Person( $f : invalidFieldName, eval( $f != null ) )\n";
        str += "then\n";
        str += "end\n";

        try {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

            if ( !kbuilder.hasErrors() ) {
                fail( "KnowledgeBuilder should have errors" );
            }
        } catch ( Exception e ) {
            fail( "Exception should not be thrown " );
        }

        str = "";
        str += "package org.drools\n";
        str += "import org.drools.Person\n";
        str += "global java.util.List mlist\n";
        str += "rule rule1 \n";
        str += "when\n";
        str += "   Person( $f : invalidFieldName, name == ( $f ) )\n";
        str += "then\n";
        str += "end\n";

        try {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

            if ( !kbuilder.hasErrors() ) {
                fail( "KnowledgeBuilder should have errors" );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Exception should not be thrown " );
        }
    }

    @Test
    public void testDeepNestedConstraints() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DeepNestedConstraints.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.insert( new Person( "bob",
                                          "muzzarela" ) );
        workingMemory.insert( new Cheese( "brie",
                                          10 ) );
        workingMemory.insert( new Cheese( "muzzarela",
                                          80 ) );

        workingMemory.fireAllRules();

        assertEquals( "should have fired twice",
                      2,
                      list.size() );
    }

    @Test
    public void testGetFactHandleEqualityBehavior() throws Exception {
        final RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setAssertBehaviour( RuleBaseConfiguration.AssertBehaviour.EQUALITY );
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( conf );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final StatefulSession session = ruleBase.newStatefulSession();

        CheeseEqual cheese = new CheeseEqual( "stilton",
                                              10 );
        session.insert( cheese );
        FactHandle fh = session.getFactHandle( new CheeseEqual( "stilton",
                                                                10 ) );
        assertNotNull( fh );
    }

    @Test
    public void testGetFactHandleIdentityBehavior() throws Exception {
        final RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setAssertBehaviour( RuleBaseConfiguration.AssertBehaviour.IDENTITY );
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( conf );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final StatefulSession session = ruleBase.newStatefulSession();

        CheeseEqual cheese = new CheeseEqual( "stilton",
                                              10 );
        session.insert( cheese );
        FactHandle fh1 = session.getFactHandle( new Cheese( "stilton",
                                                            10 ) );
        assertNull( fh1 );
        FactHandle fh2 = session.getFactHandle( cheese );
        assertNotNull( fh2 );
    }

    @Test
    public void testOrCEFollowedByEval() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_OrCEFollowedByEval.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.insert( new FactA( "X" ) );
        InternalFactHandle b = (InternalFactHandle) workingMemory.insert( new FactB( "X" ) );

        workingMemory.fireAllRules();

        assertEquals( "should have fired",
                      2,
                      list.size() );
        assertTrue( list.contains( b.getObject() ) );
    }

    @Test
    public void testNPEOnMVELAlphaPredicates() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NPEOnMVELPredicate.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "results",
                           list );

        Cheese cheese = new Cheese( "stilton",
                                    10 );
        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( cheese );
        Person bob = new Person( "bob",
                                 "stilton" );
        Cheese cheese2 = new Cheese();
        bob.setCheese( cheese2 );

        FactHandle p = session.insert( bob );
        FactHandle c = session.insert( cheesery );

        session.fireAllRules();

        assertEquals( "should not have fired",
                      0,
                      list.size() );

        cheese2.setType( "stilton" );
        session.update( p,
                        bob );
        session.fireAllRules();

        assertEquals( 1,
                      list.size() );

    }

    @Test
    public void testModifyWithLockOnActive() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ModifyWithLockOnActive.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        session.setGlobal( "results",
                           results );

        final Person bob = new Person( "Bob",
                                       15 );
        final Person mark = new Person( "Mark",
                                        16 );
        final Person michael = new Person( "Michael",
                                           14 );
        session.insert( bob );
        session.insert( mark );
        session.insert( michael );
        session.setFocus( "feeding" );
        session.fireAllRules( 5 );

        assertEquals( 2,
                      ((List) session.getGlobal( "results" )).size() );

    }

    @Test
    public void testNPEOnParenthesis() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_ParenthesisUsage.drl" ) ),
                      ResourceType.DRL );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final List<Person> results = new ArrayList<Person>();

        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.setGlobal( "results",
                           results );

        Person bob = new Person( "Bob",
                                 20 );
        bob.setAlive( true );
        Person foo = new Person( "Foo",
                                 0 );
        foo.setAlive( false );

        session.insert( bob );
        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( bob,
                      results.get( 0 ) );

        session.insert( foo );
        session.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( foo,
                      results.get( 1 ) );
    }

    @Test
    public void testEvalWithLineBreaks() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_EvalWithLineBreaks.drl" ) ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final List<Person> results = new ArrayList<Person>();

        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.setGlobal( "results",
                           results );

        session.insert( Integer.valueOf( 10 ) );
        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( Integer.valueOf( 10 ),
                      results.get( 0 ) );
    }

    @Test
    public void testDRLWithoutPackageDeclaration() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_NoPackageDeclaration.drl" ) ),
                      ResourceType.DRL );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // no package defined, so it is set to the default
        final FactType factType = kbase.getFactType( "defaultpkg",
                                                     "Person" );
        assertNotNull( factType );
        final Object bob = factType.newInstance();
        factType.set( bob,
                      "name",
                      "Bob" );
        factType.set( bob,
                      "age",
                      Integer.valueOf( 30 ) );

        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        final List results = new ArrayList();
        session.setGlobal( "results",
                           results );

        session.insert( bob );
        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( bob,
                      results.get( 0 ) );
    }

    @Test
    public void testKnowledgeContextJava() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_KnowledgeContextJava.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Message() );
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( "Hello World",
                      list.get( 0 ) );
    }

    @Test
    public void testListOfMaps() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_TestMapVariableRef.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map mapOne = new HashMap<String, Object>();
        Map mapTwo = new HashMap<String, Object>();

        mapOne.put( "MSG",
                    "testMessage" );
        mapTwo.put( "MSGTWO",
                    "testMessage" );

        list.add( mapOne );
        list.add( mapTwo );
        ksession.insert( list );
        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );

    }

    @Test
    public void testKnowledgeContextMVEL() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_KnowledgeContextMVEL.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Message() );
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( "Hello World",
                      list.get( 0 ) );
    }

    @Test
    public void testJBRules2055() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_JBRules2055.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );
        ksession.insert( new Cheese( "stilton" ) );
        ksession.insert( new Cheese( "brie" ) );
        ksession.insert( new Cheese( "muzzarella" ) );
        ksession.insert( new Person( "bob",
                                     "stilton" ) );
        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );
        assertEquals( "stilton",
                      results.get( 0 ) );
        assertEquals( "brie",
                      results.get( 1 ) );

    }

    @Test
    public void testJBRules2369() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_JBRules2369.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            fail( "Error loading test_JBRules2369" );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );

        FactA a = new FactA();
        FactB b = new FactB( Integer.valueOf( 0 ) );

        org.drools.runtime.rule.FactHandle aHandle = ksession.insert( a );
        org.drools.runtime.rule.FactHandle bHandle = ksession.insert( b );

        ksession.fireAllRules();

        assertEquals( 1,
                      results.size() );

        ksession.update( aHandle,
                         a );

        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );
    }

    @Test
    public void testInsertionOrder() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_InsertionOrder.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );
        ksession.insert( new Move( 1,
                                   2 ) );
        ksession.insert( new Move( 2,
                                   3 ) );

        Win win2 = new Win( 2 );
        Win win3 = new Win( 3 );

        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( win2 ) );
        assertTrue( results.contains( win3 ) );

        ksession = kbase.newStatefulKnowledgeSession();
        results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );
        // reverse the order of the inserts
        ksession.insert( new Move( 2,
                                   3 ) );
        ksession.insert( new Move( 1,
                                   2 ) );

        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( win2 ) );
        assertTrue( results.contains( win3 ) );

    }

    @Test
    public void testFireAllWhenFiringUntilHalt() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Runnable fireUntilHalt = new Runnable() {
            public void run() {
                ksession.fireUntilHalt();
            }
        };
        Runnable fireAllRules = new Runnable() {
            public void run() {
                ksession.fireAllRules();
            }
        };
        Thread t1 = new Thread( fireUntilHalt );
        Thread t2 = new Thread( fireAllRules );
        t1.start();
        try {
            Thread.currentThread().sleep( 500 );
        } catch ( InterruptedException e ) {
        }
        t2.start();
        // give the chance for t2 to finish
        try {
            Thread.currentThread().sleep( 1000 );
        } catch ( InterruptedException e ) {
        }
        boolean aliveT2 = t2.isAlive();
        ksession.halt();
        try {
            Thread.currentThread().sleep( 1000 );
        } catch ( InterruptedException e ) {
        }
        boolean aliveT1 = t1.isAlive();
        if ( t2.isAlive() ) {
            t2.interrupt();
        }
        if ( t1.isAlive() ) {
            t1.interrupt();
        }
        assertFalse( "T2 should have finished",
                     aliveT2 );
        assertFalse( "T1 should have finished",
                     aliveT1 );
    }

    @Test
    @Ignore
    public void testFireUntilHaltFailingAcrossEntryPoints() throws Exception {
        String rule1 = "package org.drools\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule testFireUntilHalt\n";
        rule1 += "when\n";
        rule1 += "       Cheese()\n";
        rule1 += "  $p : Person() from entry-point \"testep\"\n";
        rule1 += "then \n";
        rule1 += "  list.add( $p ) ;\n";
        rule1 += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        final WorkingMemoryEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "testep" );

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        ksession.insert( new Cheese( "cheddar" ) );
        ksession.fireAllRules();

        Runnable fireUntilHalt = new Runnable() {
            public void run() {
                ksession.fireUntilHalt();
            }
        };

        Thread t1 = new Thread( fireUntilHalt );
        t1.start();

        Thread.currentThread().sleep( 500 );
        ep.insert( new Person( "darth" ) );
        Thread.currentThread().sleep( 500 );
        ksession.halt();
        t1.join( 5000 );
        boolean alive = t1.isAlive();
        if ( alive ) {
            t1.interrupt();
        }
        assertFalse( "Thread should have died!",
                     alive );
        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testNetworkBuildErrorAcrossEntryPointsAndFroms() throws Exception {
        String rule1 = "package org.drools\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "         Cheese() from entry-point \"testep\"\n";
        rule1 += "    $p : Person() from list\n";
        rule1 += "then \n";
        rule1 += "  list.add( \"rule1\" ) ;\n";
        rule1 += "  insert( $p );\n";
        rule1 += "end\n";
        rule1 += "rule rule2\n";
        rule1 += "when\n";
        rule1 += "  $p : Person() \n";
        rule1 += "then \n";
        rule1 += "  list.add( \"rule2\" ) ;\n";
        rule1 += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        final WorkingMemoryEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "testep" );

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        list.add( new Person( "darth" ) );
        ep.insert( new Cheese( "cheddar" ) );

        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
    }

    @Test
    public void testBindingToMissingField() throws Exception {
        // JBRULES-3047
        String rule1 = "package org.drools\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "    Integer( $i : noSuchField ) \n";
        rule1 += "    eval( $i > 0 )\n";
        rule1 += "then \n";
        rule1 += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ),
                      ResourceType.DRL );

        if ( !kbuilder.hasErrors() ) {
            fail( "this should have errors" );
        }
    }

    @Test
    public void testJBRules2140() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_JBRules2140.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );
        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( "java" ) );
        assertTrue( results.contains( "mvel" ) );

    }

    @Test
    public void testGeneratedBeansSerializable() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_GeneratedBeansSerializable.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // test kbase serialization
        kbase = SerializationHelper.serializeObject( kbase );

        // Retrieve the generated fact type
        FactType cheeseFact = kbase.getFactType( "org.drools.generatedbeans",
                                                 "Cheese" );

        assertTrue( "Generated beans must be serializable",
                    Serializable.class.isAssignableFrom( cheeseFact.getFactClass() ) );

        // Create a new Fact instance
        Object cheese = cheeseFact.newInstance();
        cheeseFact.set( cheese,
                        "type",
                        "stilton" );

        // another instance
        Object cheese2 = cheeseFact.newInstance();
        cheeseFact.set( cheese2,
                        "type",
                        "brie" );

        // creating a stateful session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<Number> results = new ArrayList<Number>();
        ksession.setGlobal( "results",
                            results );

        // inserting fact
        ksession.insert( cheese );
        ksession.insert( cheese2 );

        // firing rules
        ksession.fireAllRules();

        // checking results
        assertEquals( 1,
                      results.size() );
        assertEquals( 2,
                      results.get( 0 ).intValue() );

    }

    @Test
    public void testAddRemoveListeners() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_AddRemoveListeners.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        // creating listener as a jmock proxy
        final org.drools.event.rule.WorkingMemoryEventListener wmeListener = mock( org.drools.event.rule.WorkingMemoryEventListener.class );

        ksession.addEventListener( wmeListener );

        // listener will be notified of both facts insertion
        ksession.insert( new Cheese( "stilton" ) );
        ksession.insert( wmeListener );

        // firing rules will remove listener
        ksession.fireAllRules();

        // inserting another object into the working memory, listener should NOT be notified,
        // since it is no longer listening.
        ksession.insert( new Cheese( "brie" ) );

        verify( wmeListener,
                times( 2 ) ).objectInserted( any( org.drools.event.rule.ObjectInsertedEvent.class ) );
    }

    @Test
    public void testInsert() throws Exception {
        String drl = "";
        drl += "package test\n";
        drl += "import org.drools.Person\n";
        drl += "import org.drools.Pet\n";
        drl += "import java.util.ArrayList\n";
        drl += "rule test\n";
        drl += "when\n";
        drl += "$person:Person()\n";
        drl += "$pets : ArrayList()\n";
        drl += "   from collect( \n";
        drl += "      Pet(\n";
        drl += "         ownerName == $person.name\n";
        drl += "      )\n";
        drl += "   )\n";
        drl += "then\n";
        drl += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            fail( errors.toString() );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Person( "Toni" ) );
        ksession.insert( new Pet( "Toni" ) );
    }

    @Test
    public void testMemberOfNotWorkingWithOr() throws Exception {

        String rule = "";
        rule += "package org.drools;\n";
        rule += "import java.util.ArrayList;\n";
        rule += "import org.drools.Person;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    $list: ArrayList()                                   \n";
        rule += "    ArrayList()                                          \n";
        rule += "            from collect(                                \n";
        rule += "                  Person(                                \n";
        rule += "                      (                                  \n";
        rule += "                          pet memberOf $list             \n";
        rule += "                      ) || (                             \n";
        rule += "                          pet == null                    \n";
        rule += "                      )                                  \n";
        rule += "                  )                                      \n";
        rule += "            )\n";
        rule += "then\n";
        rule += "  System.out.println(\"hello person\");\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final org.drools.rule.Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        Person toni = new Person( "Toni",
                                  12 );
        toni.setPet( new Pet( "Mittens" ) );

        session.insert( new ArrayList() );
        session.insert( toni );

        session.fireAllRules();
    }

    @Test
    public void testUnNamed() throws Exception {

        String rule = "";
        rule += "package org.drools;\n";
        rule += "import java.util.ArrayList;\n";
        rule += "import org.drools.Person;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    $list: ArrayList()                                   \n";
        rule += "    ArrayList()                                          \n";
        rule += "            from collect(                                \n";
        rule += "                  Person(                                \n";
        rule += "                      (                                  \n";
        rule += "                          pet memberOf $list             \n";
        rule += "                      ) || (                             \n";
        rule += "                          pet == null                    \n";
        rule += "                      )                                  \n";
        rule += "                  )                                      \n";
        rule += "            )\n";
        rule += "then\n";
        rule += "  System.out.println(\"hello person\");\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final org.drools.rule.Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        Person toni = new Person( "Toni",
                                  12 );
        toni.setPet( new Pet( "Mittens" ) );

        session.insert( new ArrayList() );
        session.insert( toni );

        session.fireAllRules();
    }

    @Test
    @Ignore
    // this isn't possible, we can only narrow with type safety, not widen.
    public void testAccessFieldsFromSubClass() throws Exception {

        // Exception in ClassFieldAccessorStore line: 116

        String rule = "";
        rule += "package org.drools;\n";
        rule += "import org.drools.Person;\n";
        rule += "import org.drools.Pet;\n";
        rule += "import org.drools.Cat;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    Person(\n";
        rule += "      pet.breed == \"Siamise\"\n";
        rule += "    )\n";
        rule += "then\n";
        rule += "System.out.println(\"hello person\");\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final org.drools.rule.Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        Person person = new Person();

        person.setPet( new Cat( "Mittens" ) );

        session.insert( person );

        session.fireAllRules();
    }

    @Test
    public void testGenericsInRHS() throws Exception {

        String rule = "";
        rule += "package org.drools;\n";
        rule += "import java.util.Map;\n";
        rule += "import java.util.HashMap;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "  when\n";
        rule += "  then\n";
        rule += "    Map<String,String> map = new HashMap<String,String>();\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final org.drools.rule.Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        assertNotNull( session );
    }

    @Test
    public void testActivationListener() throws Exception {

        String rule = "";
        rule += "package org.drools;\n";
        rule += "import java.util.Map;\n";
        rule += "import java.util.HashMap;\n";
        rule += "rule \"Test Rule\" @activationListener('blah')\n";
        rule += "  when\n";
        rule += "     String( this == \"xxx\" )\n ";
        rule += "  then\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final org.drools.rule.Package pkg = builder.getPackage();

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        final List list = new ArrayList();
        conf.addActivationListener( "blah",
                                    new ActivationListenerFactory() {

                                        public TerminalNode createActivationListener( int id,
                                                                                      LeftTupleSource source,
                                                                                      org.drools.rule.Rule rule,
                                                                                      GroupElement subrule,
                                                                                      BuildContext context,
                                                                                      Object... args ) {
                                            return new RuleTerminalNode( id,
                                                                         source,
                                                                         rule,
                                                                         subrule,
                                                                         context ) {
                                                @Override
                                                public void assertLeftTuple( LeftTuple tuple,
                                                                             PropagationContext context,
                                                                             InternalWorkingMemory workingMemory ) {
                                                    list.add( "inserted" );
                                                }

                                                @Override
                                                public void modifyLeftTuple( LeftTuple leftTuple,
                                                                             PropagationContext context,
                                                                             InternalWorkingMemory workingMemory ) {
                                                    list.add( "updated" );
                                                }

                                                @Override
                                                public void retractLeftTuple( LeftTuple leftTuple,
                                                                              PropagationContext context,
                                                                              InternalWorkingMemory workingMemory ) {
                                                    list.add( "retracted" );
                                                }
                                            };
                                        }
                                    } );
        final RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        FactHandle fh = session.insert( "xxx" );
        session.update( fh,
                        "xxx" );
        session.retract( fh );

        assertEquals( "inserted",
                      list.get( 0 ) );
        assertEquals( "updated",
                      list.get( 1 ) );
        assertEquals( "retracted",
                      list.get( 2 ) );

        assertNotNull( session );
    }

    @Test
    public void testAccessingMapValues() throws Exception {

        String rule = "";
        rule += "package org.drools;\n";
        rule += "import org.drools.Pet;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "  when\n";
        rule += "    $pet: Pet()\n";
        rule += "    Pet( \n";
        rule += "      ownerName == $pet.attributes[\"key\"] \n";
        rule += "    )\n";
        rule += "  then\n";
        rule += "    System.out.println(\"hi pet\");\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final org.drools.rule.Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        assertNotNull( session );

        Pet pet1 = new Pet( "Toni" );
        pet1.getAttributes().put( "key",
                                  "value" );
        Pet pet2 = new Pet( "Toni" );

        session.insert( pet1 );
        session.insert( pet2 );

        session.fireAllRules();
    }

    @Test
    public void testClassLoaderHits() throws Exception {
        final KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        //conf.setOption( ClassLoaderCacheOption.DISABLED );
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( conf );
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_GeneratedBeansMVEL.drl" ) ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_GeneratedBeans.drl" ) ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_NullFieldOnCompositeSink.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        //((CompositeClassLoader)((PackageBuilderConfiguration)conf).getClassLoader()).dumpStats();

    }

    @Test
    public void testMVELConsequenceWithoutSemiColon1() throws Exception {
        String drl = "";
        drl += "package test\n";
        drl += "import org.drools.Person\n";
        drl += "import org.drools.Pet\n";
        drl += "rule test dialect 'mvel'\n";
        drl += "when\n";
        drl += "$person:Person()\n";
        drl += "$pet:Pet()\n";
        drl += "then\n";
        drl += "    retract($person) // some comment\n";
        drl += "    retract($pet) // another comment\n";
        drl += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        // create working memory mock listener
        org.drools.event.rule.WorkingMemoryEventListener wml = Mockito.mock( org.drools.event.rule.WorkingMemoryEventListener.class );

        ksession.addEventListener( wml );

        org.drools.runtime.rule.FactHandle personFH = ksession.insert( new Person( "Toni" ) );
        org.drools.runtime.rule.FactHandle petFH = ksession.insert( new Pet( "Toni" ) );

        int fired = ksession.fireAllRules();
        assertEquals( 1,
                      fired );

        // capture the arguments and check that the retracts happened
        ArgumentCaptor<org.drools.event.rule.ObjectRetractedEvent> retracts = ArgumentCaptor.forClass( org.drools.event.rule.ObjectRetractedEvent.class );
        verify( wml,
                times( 2 ) ).objectRetracted( retracts.capture() );
        List<org.drools.event.rule.ObjectRetractedEvent> values = retracts.getAllValues();
        assertThat( values.get( 0 ).getFactHandle(),
                    is( personFH ) );
        assertThat( values.get( 1 ).getFactHandle(),
                    is( petFH ) );

    }

    @Test
    public void testRuleMetaAttributes() throws Exception {
        String drl = "";
        drl += "package test\n";
        drl += "rule \"test meta attributes\"\n";
        drl += "    @id(1234 ) @author(  john_doe  ) @text(\"It's an escaped\\\" string\"  )\n";
        drl += "when\n";
        drl += "then\n";
        drl += "    // some comment\n";
        drl += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        Rule rule = kbase.getRule( "test",
                                   "test meta attributes" );

        assertNotNull( rule );
        assertThat( rule.getMetaAttribute( "id" ),
                    is( "1234" ) );
        assertThat( rule.getMetaAttribute( "author" ),
                    is( "john_doe" ) );
        assertThat( rule.getMetaAttribute( "text" ),
                    is( "\"It's an escaped\" string\"" ) );

    }

    // following test depends on MVEL: http://jira.codehaus.org/browse/MVEL-212
    @Test
    public void testMVELConsequenceUsingFactConstructors() throws Exception {
        String drl = "";
        drl += "package test\n";
        drl += "import org.drools.Person\n";
        drl += "global org.drools.runtime.StatefulKnowledgeSession ksession\n";
        drl += "rule test dialect 'mvel'\n";
        drl += "when\n";
        drl += "    $person:Person( name == 'mark' )\n";
        drl += "then\n";
        drl += "    // below constructor for Person does not exist\n";
        drl += "    Person p = new Person( 'bob', 30, 555 )\n";
        drl += "    ksession.update(ksession.getFactHandle($person), new Person('bob', 30, 999, 453, 534, 534, 32))\n";
        drl += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();

        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testRuleChainingWithLogicalInserts() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_RuleChaining.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        // create working memory mock listener
        org.drools.event.rule.WorkingMemoryEventListener wml = Mockito.mock( org.drools.event.rule.WorkingMemoryEventListener.class );
        org.drools.event.rule.AgendaEventListener ael = Mockito.mock( org.drools.event.rule.AgendaEventListener.class );

        ksession.addEventListener( wml );
        ksession.addEventListener( ael );

        int fired = ksession.fireAllRules();
        assertEquals( 3,
                      fired );

        // capture the arguments and check that the rules fired in the proper sequence
        ArgumentCaptor<org.drools.event.rule.AfterActivationFiredEvent> actvs = ArgumentCaptor.forClass( org.drools.event.rule.AfterActivationFiredEvent.class );
        verify( ael,
                times( 3 ) ).afterActivationFired( actvs.capture() );
        List<org.drools.event.rule.AfterActivationFiredEvent> values = actvs.getAllValues();
        assertThat( values.get( 0 ).getActivation().getRule().getName(),
                    is( "init" ) );
        assertThat( values.get( 1 ).getActivation().getRule().getName(),
                    is( "r1" ) );
        assertThat( values.get( 2 ).getActivation().getRule().getName(),
                    is( "r2" ) );

        verify( ael,
                never() ).activationCancelled( any( org.drools.event.rule.ActivationCancelledEvent.class ) );
        verify( wml,
                times( 2 ) ).objectInserted( any( org.drools.event.rule.ObjectInsertedEvent.class ) );
        verify( wml,
                never() ).objectRetracted( any( org.drools.event.rule.ObjectRetractedEvent.class ) );
    }

    @Test
    public void testOrWithReturnValueRestriction() throws Exception {
        String fileName = "test_OrWithReturnValue.drl";
        KnowledgeBase kbase = loadKnowledgeBase( fileName );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Cheese( "brie",
                                     18 ) );
        ksession.insert( new Cheese( "stilton",
                                     8 ) );
        ksession.insert( new Cheese( "brie",
                                     28 ) );

        int fired = ksession.fireAllRules();
        assertEquals( 2,
                      fired );
    }

    @Test
    public void testFromExprFollowedByNot() {
        String rule = "";
        rule += "package org.drools\n";
        rule += "rule \"Rule 1\"\n";
        rule += "    when\n";
        rule += "        Person ($var: pet )\n";
        rule += "        Pet () from $var\n";
        rule += "        not Pet ()\n";
        rule += "    then\n";
        rule += "       System.out.println(\"Fire in the hole\");\n";
        rule += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( rule ) ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            Iterator<KnowledgeBuilderError> errors = kbuilder.getErrors().iterator();

            while ( errors.hasNext() ) {
                System.out.println( "kbuilder error: " + errors.next().getMessage() );
            }
        }

        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

    }

    @Test
    public void testLastMemoryEntryNotBug() {
        // JBRULES-2809
        // This occurs when a blocker is the last in the node's memory, or if there is only one fact in the node
        // And it gets no opportunity to rematch with itself

        String str = "";
        str += "package org.simple \n";
        str += "import " + A.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule x1 \n";
        str += "when \n";
        str += "    $s : String( this == 'x1' ) \n";
        str += "    not A( this != null ) \n";
        str += "then \n";
        str += "  list.add(\"fired x1\"); \n";
        str += "end  \n";
        str += "rule x2 \n";
        str += "when \n";
        str += "    $s : String( this == 'x2' ) \n";
        str += "    not A( field1 == $s, this != null ) \n"; // this ensures an index bucket
        str += "then \n";
        str += "  list.add(\"fired x2\"); \n";
        str += "end  \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        ksession.insert( "x1" );
        ksession.insert( "x2" );
        A a1 = new A( "x1",
                      null );
        A a2 = new A( "x2",
                      null );

        FactHandle fa1 = (FactHandle) ksession.insert( a1 );
        FactHandle fa2 = (FactHandle) ksession.insert( a2 );

        // make sure the 'exists' is obeyed when fact is cycled causing add/remove node memory
        ksession.update( fa1,
                         a1 );
        ksession.update( fa2,
                         a2 );
        ksession.fireAllRules();

        assertEquals( 0,
                      list.size() );

        ksession.dispose();
    }

    @Test
    public void testLastMemoryEntryExistsBug() {
        // JBRULES-2809
        // This occurs when a blocker is the last in the node's memory, or if there is only one fact in the node
        // And it gets no opportunity to rematch with itself

        String str = "";
        str += "package org.simple \n";
        str += "import " + A.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule x1 \n";
        str += "when \n";
        str += "    $s : String( this == 'x1' ) \n";
        str += "    exists A( this != null ) \n";
        str += "then \n";
        str += "  list.add(\"fired x1\"); \n";
        str += "end  \n";
        str += "rule x2 \n";
        str += "when \n";
        str += "    $s : String( this == 'x2' ) \n";
        str += "    exists A( field1 == $s, this != null ) \n"; // this ensures an index bucket
        str += "then \n";
        str += "  list.add(\"fired x2\"); \n";
        str += "end  \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        ksession.insert( "x1" );
        ksession.insert( "x2" );
        A a1 = new A( "x1",
                      null );
        A a2 = new A( "x2",
                      null );

        FactHandle fa1 = (FactHandle) ksession.insert( a1 );
        FactHandle fa2 = (FactHandle) ksession.insert( a2 );

        // make sure the 'exists' is obeyed when fact is cycled causing add/remove node memory
        ksession.update( fa1,
                         a1 );
        ksession.update( fa2,
                         a2 );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );

        ksession.dispose();
    }

    @Test
    public void testNotIterativeModifyBug() {
        // JBRULES-2809
        // This bug occurs when a tuple is modified, the remove/add puts it onto the memory end
        // However before this was done it would attempt to find the next tuple, starting from itself
        // This meant it would just re-add itself as the blocker, but then be moved to end of the memory
        // If this tuple was then removed or changed, the blocked was unable to check previous tuples.

        String str = "";
        str += "package org.simple \n";
        str += "import " + A.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "when \n";
        str += "  $f1 : A() \n";
        str += "    not A(this != $f1,  eval(field2 == $f1.getField2())) \n";
        str += "    eval( !$f1.getField1().equals(\"1\") ) \n";
        str += "then \n";
        str += "  list.add($f1); \n";
        str += "end  \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        A a1 = new A( "2",
                      "2" );
        A a2 = new A( "1",
                      "2" );
        A a3 = new A( "1",
                      "2" );

        FactHandle fa1 = (FactHandle) ksession.insert( a1 );
        FactHandle fa2 = (FactHandle) ksession.insert( a2 );
        FactHandle fa3 = (FactHandle) ksession.insert( a3 );
        ksession.fireAllRules();

        // a1 is blocked by a2
        assertEquals( 0,
                      list.size() );

        // modify a2, so that a1 is now blocked by a3
        a2.setField2( "1" ); // Do
        ksession.update( fa2,
                         a2 );
        a2.setField2( "2" ); // Undo
        ksession.update( fa2,
                         a2 );

        // modify a3 to cycle, so that it goes on the memory end, but in a previous bug still blocked a1
        ksession.update( fa3,
                         a3 );

        a3.setField2( "1" ); // Do
        ksession.update( fa3,
                         a3 );
        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() ); // this should still now blocked by a2, but bug from previous update hanging onto blocked

        ksession.dispose();
    }

    @Test
    public void testExistsIterativeModifyBug() {
        // JBRULES-2809
        // This bug occurs when a tuple is modified, the remove/add puts it onto the memory end
        // However before this was done it would attempt to find the next tuple, starting from itself
        // This meant it would just re-add itself as the blocker, but then be moved to end of the memory
        // If this tuple was then removed or changed, the blocked was unable to check previous tuples.

        String str = "";
        str += "package org.simple \n";
        str += "import " + A.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "when \n";
        str += "  $f1 : A() \n";
        str += "    exists A(this != $f1, eval(field2 == $f1.getField2())) \n";
        str += "    eval( !$f1.getField1().equals(\"1\") ) \n";
        str += "then \n";
        str += "  list.add($f1); \n";
        str += "end  \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        A a1 = new A( "2",
                      "2" );
        A a2 = new A( "1",
                      "2" );
        A a3 = new A( "1",
                      "2" );

        FactHandle fa1 = (FactHandle) ksession.insert( a1 );
        FactHandle fa2 = (FactHandle) ksession.insert( a2 );
        FactHandle fa3 = (FactHandle) ksession.insert( a3 );

        // a2, a3 are blocked by a1        
        // modify a1, so that a1,a3 are now blocked by a2
        a1.setField2( "1" ); // Do
        ksession.update( fa1,
                         a1 );
        a1.setField2( "2" ); // Undo
        ksession.update( fa1,
                         a1 );

        // modify a2, so that a1,a2 are now blocked by a3        
        a2.setField2( "1" ); // Do
        ksession.update( fa2,
                         a2 );
        a2.setField2( "2" ); // Undo
        ksession.update( fa2,
                         a2 );

        // modify a3 to cycle, so that it goes on the memory end, but in a previous bug still blocked a1
        ksession.update( fa3,
                         a3 );

        a3.setField2( "1" ); // Do
        ksession.update( fa3,
                         a3 );
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() ); // a2 should still be blocked by a1, but bug from previous update hanging onto blocked

        ksession.dispose();
    }

    @Test
    public void testBindingsWithOr() throws InstantiationException,
                                    IllegalAccessException {
        // JBRULES-2917: matching of field==v1 || field==v2 breaks when variable binding is added

        String str = "package org.drools\n" +
                     "declare Assignment\n" +
                     "    source : int\n" +
                     "    target : int\n" +
                     "end\n" +
                     "rule ValueIsTheSame1\n" +
                     "when\n" +
                     "    Assignment( $t: target == 10 || target == source )\n" +
                     "then\n" +
                     "end\n" +
                     "rule ValueIsTheSame2\n" +
                     "when\n" +
                     "    Assignment( $t: target == source || target == 10 )\n" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType asgType = kbase.getFactType( "org.drools",
                                              "Assignment" );
        Object asg = asgType.newInstance();
        asgType.set( asg,
                     "source",
                     10 );
        asgType.set( asg,
                     "target",
                     10 );

        ksession.insert( asg );

        int rules = ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      rules );
    }

    @Test
    @Ignore("This test requires MVEL to support .class literals, what it doesn't today")
    public void testMVELClassReferences() throws InstantiationException,
                                         IllegalAccessException {
        String str = "package org.drools\n" +
                     "declare Assignment\n" +
                     "    source : Class\n" +
                     "    target : Class\n" +
                     "end\n" +
                     "rule ObjectIsAssignable1\n" +
                     "when\n" +
                     "    Assignment( $t: target == java.lang.Object.class || target == source )\n" +
                     "then\n" +
                     "end\n" +
                     "rule ObjectIsAssignable2\n" +
                     "when\n" +
                     "    Assignment( $t: target == source || target == java.lang.Object.class )\n" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType asgType = kbase.getFactType( "org.drools",
                                              "Assignment" );
        Object asg = asgType.newInstance();
        asgType.set( asg,
                     "source",
                     Object.class );
        asgType.set( asg,
                     "target",
                     Object.class );

        ksession.insert( asg );

        int rules = ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      rules );
    }

    @Test
    public void testNotMatchesSucceeds() throws InstantiationException,
                                        IllegalAccessException {
        // JBRULES-2914: Rule misfires due to "not matches" not working

        String str = "package org.drools\n" +
                     "rule NotMatches\n" +
                     "when\n" +
                     "    Person( name == null || (name != null && name not matches \"-.{2}x.*\" ) )\n" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person p = new Person( "-..x..xrwx" );

        ksession.insert( p );

        int rules = ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 0,
                      rules );
    }

    @Test
    public void testNotMatchesFails() throws InstantiationException,
                                     IllegalAccessException {
        // JBRULES-2914: Rule misfires due to "not matches" not working

        String str = "package org.drools\n" +
                     "rule NotMatches\n" +
                     "when\n" +
                     "    Person( name == null || (name != null && name not matches \"-.{2}x.*\" ) )\n" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person p = new Person( "d..x..xrwx" );

        ksession.insert( p );

        int rules = ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 1,
                      rules );
    }

    @Test
    public void testNotEqualsOperator() {
        // JBRULES-3003: restriction evaluation returns 'false' for "trueField != falseField"

        String str = "package org.drools\n" +
                     "rule NotEquals\n" +
                     "when\n" +
                     "    Primitives( booleanPrimitive != booleanWrapper )\n" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Primitives p = new Primitives();
        p.setBooleanPrimitive( true );
        p.setBooleanWrapper( Boolean.FALSE );

        ksession.insert( p );

        int rules = ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 1,
                      rules );
    }

    @Test
    public void testNotContainsOperator() {
        // JBRULES-2404: "not contains" operator doesn't work on nested fields

        String str = "package org.drools\n" +
                     "rule NotContains\n" +
                     "when\n" +
                     "    $oi : OrderItem( )\n" +
                     "    $o  : Order( items.values() not contains $oi )" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Order order1 = new Order( 1,
                                  "XYZ" );
        Order order2 = new Order( 2,
                                  "ABC" );
        OrderItem item11 = new OrderItem( order1,
                                          1 );
        order1.addItem( item11 );
        OrderItem item21 = new OrderItem( order2,
                                          1 );
        order2.addItem( item21 );

        ksession.insert( order1 );
        ksession.insert( item11 );

        // should not fire, as item11 is contained in order1.items
        int rules = ksession.fireAllRules();
        assertEquals( 0,
                      rules );

        // should fire as item21 is not contained in order1.items
        ksession.insert( item21 );
        rules = ksession.fireAllRules();
        assertEquals( 1,
                      rules );
    }

    @Test
    public void testOrWithFrom() {
        // JBRULES-2274: Rule does not fire as expected using deep object model and nested 'or' clause

        String str = "package org.drools\n" +
                     "rule NotContains\n" +
                     "when\n" +
                     "    $oi1 : OrderItem( )\n" +
                     "    $o1  : Order(number == 1) from $oi1.order; \n" +
                     "    ( eval(true) or eval(true) )\n" +
                     "    $oi2 : OrderItem( )\n" +
                     "    $o2  : Order(number == 2) from $oi2.order; \n" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Order order1 = new Order( 1,
                                  "XYZ" );
        Order order2 = new Order( 2,
                                  "ABC" );
        OrderItem item11 = new OrderItem( order1,
                                          1 );
        order1.addItem( item11 );
        OrderItem item21 = new OrderItem( order2,
                                          1 );
        order2.addItem( item21 );

        ksession.insert( order1 );
        ksession.insert( order2 );
        ksession.insert( item11 );
        ksession.insert( item21 );

        int rules = ksession.fireAllRules();
        assertEquals( 2,
                      rules );
    }

    @Test
    public void testSoundsLike() {
        // JBRULES-2991: Operator soundslike is broken

        String str = "package org.drools\n" +
                     "rule SoundsLike\n" +
                     "when\n" +
                     "    Person( name soundslike \"Bob\" )\n" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Person( "Bob" ) );
        ksession.insert( new Person( "Mark" ) );

        int rules = ksession.fireAllRules();
        assertEquals( 1,
                      rules );
    }

    @Test
    public void testAgendaFilter1() {
        String str = "package org.drools\n" +
                     "rule Aaa when then end\n" +
                     "rule Bbb when then end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        org.drools.event.rule.AgendaEventListener ael = mock( org.drools.event.rule.AgendaEventListener.class );
        ksession.addEventListener( ael );

        RuleNameStartsWithAgendaFilter af = new RuleNameStartsWithAgendaFilter( "B" );

        int rules = ksession.fireAllRules( af );
        assertEquals( 1,
                      rules );

        ArgumentCaptor<org.drools.event.rule.AfterActivationFiredEvent> arg = ArgumentCaptor.forClass( org.drools.event.rule.AfterActivationFiredEvent.class );
        verify( ael ).afterActivationFired( arg.capture() );
        assertThat( arg.getValue().getActivation().getRule().getName(),
                    is( "Bbb" ) );
    }

    @Test
    public void testAgendaFilter2() {
        String str = "package org.drools\n" +
                     "rule Aaa when then end\n" +
                     "rule Bbb when then end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        org.drools.event.rule.AgendaEventListener ael = mock( org.drools.event.rule.AgendaEventListener.class );
        ksession.addEventListener( ael );

        RuleNameEndsWithAgendaFilter af = new RuleNameEndsWithAgendaFilter( "a" );

        int rules = ksession.fireAllRules( af );
        assertEquals( 1,
                      rules );

        ArgumentCaptor<org.drools.event.rule.AfterActivationFiredEvent> arg = ArgumentCaptor.forClass( org.drools.event.rule.AfterActivationFiredEvent.class );
        verify( ael ).afterActivationFired( arg.capture() );
        assertThat( arg.getValue().getActivation().getRule().getName(),
                    is( "Aaa" ) );
    }

    @Test
    public void testAgendaFilter3() {
        String str = "package org.drools\n" +
                     "rule Aaa when then end\n" +
                     "rule Bbb when then end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        org.drools.event.rule.AgendaEventListener ael = mock( org.drools.event.rule.AgendaEventListener.class );
        ksession.addEventListener( ael );

        RuleNameMatchesAgendaFilter af = new RuleNameMatchesAgendaFilter( ".*b." );

        int rules = ksession.fireAllRules( af );
        assertEquals( 1,
                      rules );

        ArgumentCaptor<org.drools.event.rule.AfterActivationFiredEvent> arg = ArgumentCaptor.forClass( org.drools.event.rule.AfterActivationFiredEvent.class );
        verify( ael ).afterActivationFired( arg.capture() );
        assertThat( arg.getValue().getActivation().getRule().getName(),
                    is( "Bbb" ) );
    }

    @Test
    public void testAgendaFilter4() {
        String str = "package org.drools\n" +
                     "rule Aaa when then end\n" +
                     "rule Bbb when then end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        org.drools.event.rule.AgendaEventListener ael = mock( org.drools.event.rule.AgendaEventListener.class );
        ksession.addEventListener( ael );

        RuleNameEqualsAgendaFilter af = new RuleNameEqualsAgendaFilter( "Aaa" );

        int rules = ksession.fireAllRules( af );
        assertEquals( 1,
                      rules );

        ArgumentCaptor<org.drools.event.rule.AfterActivationFiredEvent> arg = ArgumentCaptor.forClass( org.drools.event.rule.AfterActivationFiredEvent.class );
        verify( ael ).afterActivationFired( arg.capture() );
        assertThat( arg.getValue().getActivation().getRule().getName(),
                    is( "Aaa" ) );
    }

    @Test
    public void testRestrictionsWithOr() {
        // JBRULES-2203: NullPointerException When Using Conditional Element "or" in LHS Together with a Return Value Restriction

        String str = "package org.drools\n" +
                     "rule \"test\"\n" +
                     "when\n" +
                     "    Cheese( price == (1 + 1) );\n" +
                     "    (or eval(true);\n" +
                     "        eval(true);\n" +
                     "    )\n" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Cheese( "Stilton",
                                     2 ) );

        int rules = ksession.fireAllRules();
        assertEquals( 2,
                      rules );
    }

    @Test
    public void testMapModel() {
        String str = "package org.drools\n" +
        		     "import java.util.Map\n" +
                     "rule \"test\"\n" +
                     "when\n" +
                     "    Map( type == \"Person\", name == \"Bob\" );\n" +
                     "then\n" +
                     "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        Map<String, String> mark = new HashMap<String, String>();
        mark.put( "type", "Person" );
        mark.put( "name", "Mark" );

        ksession.insert( mark );

        int rules = ksession.fireAllRules();
        assertEquals( 0,
                      rules );
        
        Map<String, String> bob = new HashMap<String, String>();
        bob.put( "type", "Person" );
        bob.put( "name", "Bob" );

        ksession.insert( bob );

        rules = ksession.fireAllRules();
        assertEquals( 1,
                      rules );
        
    }

    @Test
    @Ignore("TODO unignore when fixing JBRULES-2749")
    public void testPackageNameOfTheBeast() throws Exception {
        // JBRULES-2749 Various rules stop firing when they are in unlucky packagename and there is a function declared

        String ruleFileContent1 = "package org.drools.integrationtests;\n" +
                                  "function void myFunction() {\n" +
                                  "}\n" +
                                  "declare MyDeclaredType\n" +
                                  "  someProperty: boolean\n" +
                                  "end";
        String ruleFileContent2 = "package de.something;\n" + // FAILS
                                  //        String ruleFileContent2 = "package de.somethinga;\n" + // PASSES
                                  //        String ruleFileContent2 = "package de.somethingb;\n" + // PASSES
                                  //        String ruleFileContent2 = "package de.somethingc;\n" + // PASSES
                                  //        String ruleFileContent2 = "package de.somethingd;\n" + // PASSES
                                  //        String ruleFileContent2 = "package de.somethinge;\n" + // FAILS
                                  //        String ruleFileContent2 = "package de.somethingf;\n" + // FAILS
                                  //        String ruleFileContent2 = "package de.somethingg;\n" + // FAILS
                                  "import org.drools.integrationtests.*;\n" +
                                  "rule \"CheckMyDeclaredType\"\n" +
                                  "  when\n" +
                                  "    MyDeclaredType()\n" +
                                  "  then\n" +
                                  "    insertLogical(\"THIS-IS-MY-MARKER-STRING\");\n" +
                                  "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( ruleFileContent1,
                                                           ruleFileContent2 );
        StatefulKnowledgeSession knowledgeSession = kbase.newStatefulKnowledgeSession();

        final FactType myDeclaredFactType = kbase.getFactType( "org.drools.integrationtests",
                                                               "MyDeclaredType" );
        Object myDeclaredFactInstance = myDeclaredFactType.newInstance();
        knowledgeSession.insert( myDeclaredFactInstance );

        int rulesFired = knowledgeSession.fireAllRules();

        assertEquals( 1,
                      rulesFired );

        knowledgeSession.dispose();
    }

    private KnowledgeBase loadKnowledgeBaseFromString( String... drlContentStrings ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for ( String drlContentString : drlContentStrings ) {
            kbuilder.add( ResourceFactory.newByteArrayResource( drlContentString.getBytes() ),
                          ResourceType.DRL );
        }

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    private KnowledgeBase loadKnowledgeBase( String... classPathResources ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for ( String classPathResource : classPathResources ) {
            kbuilder.add( ResourceFactory.newClassPathResource( classPathResource,
                                                                getClass() ),
                          ResourceType.DRL );
        }
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    private Package loadPackage( final String classPathResource ) throws DroolsParserException,
                                                                 IOException {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( classPathResource ) ) );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();
        return pkg;
    }

    private RuleBase loadRuleBase( final Reader reader ) throws IOException,
                                                        DroolsParserException,
                                                        Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            fail( "Error messages in parser, need to sort this our (or else collect error messages):\n"
                    + parser.getErrors() );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        Package pkg = builder.getPackage();
        pkg = SerializationHelper.serializeObject( pkg );

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        // load up the rulebase
        return ruleBase;
    }

    public static class A {
        private String field1;
        private String field2;

        public A(String field1,
                 String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public String getField1() {
            return field1;
        }

        public void setField1( String field1 ) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2( String field2 ) {
            this.field2 = field2;
        }

        public String toString() {
            return "A) " + field1 + ":" + field2;
        }
    }

}
