package org.drools.integrationtests;

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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.Address;
import org.drools.Attribute;
import org.drools.Cell;
import org.drools.Cheese;
import org.drools.CheeseEqual;
import org.drools.Cheesery;
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
import org.drools.InsertedObject;
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
import org.drools.PolymorphicFact;
import org.drools.Primitives;
import org.drools.QueryResult;
import org.drools.QueryResults;
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
import org.drools.TestParam;
import org.drools.Win;
import org.drools.Worker;
import org.drools.WorkingMemory;
import org.drools.Cheesery.Maturity;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.audit.WorkingMemoryInMemoryLogger;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.DefaultAgenda;
import org.drools.common.DefaultFactHandle;
import org.drools.common.DisconnectedFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.ParserError;
import org.drools.compiler.PackageBuilder.PackageMergeException;
import org.drools.definition.KnowledgePackage;
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
import org.drools.event.WorkingMemoryEventListener;
import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactTemplate;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.marshalling.MarshallerFactory;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.InvalidRulePackage;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.runtime.Globals;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.impl.FlatQueryResults;
import org.drools.spi.ConsequenceExceptionHandler;
import org.drools.spi.GlobalResolver;
import org.drools.spi.ObjectType;
import org.drools.util.Entry;
import org.drools.util.ObjectHashSet;
import org.drools.util.ObjectHashMap.ObjectEntry;
import org.drools.xml.XmlDumper;

/** Run all the tests with the ReteOO engine implementation */
public class MiscTest extends TestCase {

    protected RuleBase getRuleBase() throws Exception {

        RuleBaseConfiguration config = new RuleBaseConfiguration();
        //config.setPartitionsEnabled( true );
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        //config.setPartitionsEnabled( true );
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    public void testImportFunctions() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ImportFunctions.drl" ) ) );
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
        session.fireAllRules();

        list = (List) session.getGlobal( "list" );
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

    public void testStaticFieldReference() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_StaticField.drl" ) ) );
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

    public void testMetaConsequence() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MetaConsequence.drl" ) ) );
        final Package pkg = builder.getPackage();

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

    public void testEnabledExpression() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_enabledExpression.drl" ) ) );
        final Package pkg = builder.getPackage();

        System.out.println( builder.getErrors().toString() );

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

    public void testMVELSoundex() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "MVEL_soundex.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        Cheese c = new Cheese( "fubar",
                               2 );

        session.insert( c );
        session.fireAllRules();
        assertEquals( 42,
                      c.getPrice() );
    }

    public void testMVELRewrite() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_MVELrewrite.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        List results = new ArrayList();
        session.setGlobal( "results",
                           results );

        Cheese brie = new Cheese( "brie",
                                  2 );
        Cheese stilton = new Cheese( "stilton",
                                     2 );
        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( brie );
        cheesery.addCheese( stilton );

        session.insert( cheesery );
        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( cheesery,
                      results.get( 0 ) );
    }

    public void testGlobals() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "globals_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        session.setGlobal( "string",
                           "stilton" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        session.insert( stilton );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( new Integer( 5 ),
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    public void testGlobals2() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_globalsAsConstraints.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final List results = new ArrayList();
        session.setGlobal( "results",
                           results );

        final List cheeseTypes = new ArrayList();
        session.setGlobal( "cheeseTypes",
                           cheeseTypes );
        cheeseTypes.add( "stilton" );
        cheeseTypes.add( "muzzarela" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        session.insert( stilton );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "results" )).size() );
        assertEquals( "memberOf",
                      ((List) session.getGlobal( "results" )).get( 0 ) );

        final Cheese brie = new Cheese( "brie",
                                        5 );
        session.insert( brie );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( 2,
                      ((List) session.getGlobal( "results" )).size() );
        assertEquals( "not memberOf",
                      ((List) session.getGlobal( "results" )).get( 1 ) );
    }

    public void testGlobalMerge() throws Exception {
        // from JBRULES-1512
        String rule1 = "package com.sample\n" + "rule \"rule 1\"\n" + "    salience 10\n" + "    when\n" + "    l : java.util.List()\n" + "    then\n" + "        l.add( \"rule 1 executed\" );\n" + "end\n";

        String rule2 = "package com.sample\n" + "global String str;\n" + "rule \"rule 2\"\n" + "    when\n" + "    l : java.util.List()\n" + "    then\n" + "        l.add( \"rule 2 executed \" + str);\n" + "end\n";

        PackageBuilder builder1 = new PackageBuilder();
        builder1.addPackageFromDrl( new StringReader( rule1 ) );
        Package pkg1 = builder1.getPackage();
        // build second package
        PackageBuilder builder2 = new PackageBuilder();
        builder2.addPackageFromDrl( new StringReader( rule2 ) );
        Package pkg2 = builder2.getPackage();
        // create rule base and add both packages
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg1 );
        ruleBase.addPackage( pkg2 );

        WorkingMemory wm = ruleBase.newStatefulSession();
        wm.setGlobal( "str",
                      "boo" );
        List list = new ArrayList();
        wm.insert( list );
        wm.fireAllRules();
        assertEquals( "rule 1 executed",
                      list.get( 0 ) );
        assertEquals( "rule 2 executed boo",
                      list.get( 1 ) );
    }
    
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
    
    public void testKnowledgeRuntimeAccess() throws Exception {
        String str = "";
        str += "package org.test\n";
        str +="import org.drools.Message\n";
        str +="rule \"Hello World\"\n";
        str +="when\n";
        str +="    Message( )\n";
        str +="then\n";
        str +="    System.out.println( drools.getKnowledgeRuntime() );\n";
        str +="end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes()), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(  );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase    = SerializationHelper.serializeObject( kbase );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();       
        
        ksession.insert( new Message( "help" ) );
        ksession.fireAllRules();
        ksession.dispose();
    }    

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

    public void testCustomGlobalResolver() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_globalCustomResolver.drl" ) ) );
        final Package pkg = builder.getPackage();

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
            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
            }

            public void writeExternal(ObjectOutput out) throws IOException {
            }

            public Object resolveGlobal(String identifier) {
                return map.get( identifier );
            }

            public void setGlobal(String identifier,
                                  Object value) {
                map.put( identifier,
                         value );
            }

            public Object get(String identifier) {
                return resolveGlobal( identifier );
            }

            public void set(String identifier,
                            Object value) {
                setGlobal( identifier, value );
            }

            public void setDelegate(Globals delegate) {
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
            public Object resolveGlobal(String identifier) {
                return map.get( identifier );
            }

            public void setGlobal(String identifier,
                                  Object value) {
                map.put( identifier,
                         value );
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
            }

            public void writeExternal(ObjectOutput out) throws IOException {
            }
            
            public Object get(String identifier) {
                return resolveGlobal( identifier );
            }

            public void set(String identifier,
                            Object value) {
                setGlobal( identifier, value );
            }

            public void setDelegate(Globals delegate) {
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

    public void testFieldBiningsAndEvalSharing() throws Exception {
        final String drl = "test_FieldBindingsAndEvalSharing.drl";
        evalSharingTest( drl );
    }

    public void testFieldBiningsAndPredicateSharing() throws Exception {
        final String drl = "test_FieldBindingsAndPredicateSharing.drl";
        evalSharingTest( drl );
    }

    private void evalSharingTest(final String drl) throws DroolsParserException,
                                                  IOException,
                                                  Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( drl ) ) );
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
        session.setGlobal( "list", list );

        FactType addressFact = ruleBase.getFactType("com.jboss.qa.Address" );
        Object address = addressFact.newInstance();
        session.insert( address );
        session.fireAllRules();

        list = (List) session.getGlobal( "list" );
        assertEquals( 1, list.size() );

        assertEquals( "r1",
                      list.get( 0 ) );
    }
    

    public void testNullHandling() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NullHandling.drl" ) ) );
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
        System.out.println( ((List) session.getGlobal( "list" )).get( 0 ) );
        assertEquals( 3,
                      ((List) session.getGlobal( "list" )).size() );

        nullPerson = new Person( null );

        session.insert( nullPerson );
        session.fireAllRules();
        assertEquals( 4,
                      ((List) session.getGlobal( "list" )).size() );

    }

    public void NullFieldOnCompositeSink() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NullFieldOnCompositeSink.drl" ) ) );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.insert( new Attribute() );
        workingMemory.insert( new Message() );
        workingMemory = SerializationHelper.serializeObject( workingMemory );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      ((List) workingMemory.getGlobal( "list" )).size() );
        assertEquals( "X",
                      ((List) workingMemory.getGlobal( "list" )).get( 0 ) );

    }

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

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrors() );
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );

        if ( builder.hasErrors() ) {
            System.err.println( builder.getErrors() );
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

    public void testExplicitAnd() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_ExplicitAnd.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        StatefulSession session = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session.insert( new Message( "hola" ) );

        session.fireAllRules();
        assertEquals( 0,
                      list.size() );

        session.insert( new Cheese( "brie",
                                    33 ) );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();
        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
    }

    public void testHelloWorld() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "HelloWorld.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        // go !
        final Message message = new Message( "hola" );
        message.addToList( "hello" );
        message.setNumber( 42 );

        workingMemory.insert( message );
        workingMemory.insert( "boo" );
        //        workingMemory    = SerializationHelper.serializeObject(workingMemory);
        workingMemory.fireAllRules();
        assertTrue( message.isFired() );
        assertEquals( message,
                      ((List) workingMemory.getGlobal( "list" )).get( 0 ) );

    }

    public void testExtends() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "extend_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();
        System.out.println( builder.getErrors() );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        //ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        //Test 2 levels of inheritance, and basic rule
        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        final Cheese mycheese = new Cheese( "cheddar",
                                            4 );
        FactHandle handle = session.insert( mycheese );
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal( "list" )).toString());
        assertEquals( "rule 2b",
                      ((List) session.getGlobal( "list" )).get( 0 ) );
        assertTrue( ((List) session.getGlobal( "list" )).size() == 2 );

        //Test 2nd level (parent) to make sure rule honors the extend rule
        final List list2 = new ArrayList();
        session.setGlobal( "list",
                           list2 );
        session.retract( handle );
        final Cheese mycheese2 = new Cheese( "notcheddar",
                                             4 );
        FactHandle handle2 = session.insert( mycheese2 );
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal( "list" )).toString());
        assertEquals( "rule 4",
                      ((List) session.getGlobal( "list" )).get( 0 ) );
        assertTrue( ((List) session.getGlobal( "list" )).size() == 1 );

        //Test 3 levels of inheritance, all levels
        final List list3 = new ArrayList();
        session.setGlobal( "list",
                           list3 );
        session.retract( handle2 );
        final Cheese mycheese3 = new Cheese( "stilton",
                                             6 );
        FactHandle handle3 = session.insert( mycheese3 );
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal( "list" )).toString());
        assertEquals( "rule 3",
                      ((List) session.getGlobal( "list" )).get( 0 ) );
        assertTrue( ((List) session.getGlobal( "list" )).size() == 1 );

        //Test 3 levels of inheritance, third only
        final List list4 = new ArrayList();
        session.setGlobal( "list",
                           list4 );
        session.retract( handle3 );
        final Cheese mycheese4 = new Cheese( "notstilton",
                                             6 );
        FactHandle handle4 = session.insert( mycheese4 );
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal( "list" )).toString());
        assertTrue( ((List) session.getGlobal( "list" )).size() == 0 );

        //Test 3 levels of inheritance, 2nd only 
        final List list5 = new ArrayList();
        session.setGlobal( "list",
                           list5 );
        session.retract( handle4 );
        final Cheese mycheese5 = new Cheese( "stilton",
                                             7 );
        FactHandle handle5 = session.insert( mycheese5 );
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal( "list" )).toString());
        assertTrue( ((List) session.getGlobal( "list" )).size() == 0 );

    }

    public void testLatinLocale() throws Exception {
        Locale defaultLoc = Locale.getDefault();
        
        try {
            // setting a locale that uses COMMA as decimal separator
            Locale.setDefault( new Locale("pt","BR") );
            
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
            
            assertEquals( 1, results.size() );
            assertEquals( "1",
                          results.get( 0 ) );

            mycheese.setPrice( 8 );
            mycheese.setDoublePrice( 8.50 );
            
            ksession.update( handle, mycheese );
            ksession.fireAllRules();
            assertEquals( 2, results.size() );
            assertEquals( "3",
                          results.get( 1 ) );
        } finally {
            Locale.setDefault( defaultLoc );
        }

    }

    public void testLiteral() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl" ) ) );
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

    public void testLiteralWithEscapes() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_literal_with_escapes.drl" ) ) );
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

        session.fireAllRules();

        assertEquals( expected,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

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
            public void objectUpdated(ObjectUpdatedEvent event) {
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

    public void testFactTemplate() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FactTemplate.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final FactTemplate cheese = pkg.getFactTemplate( "Cheese" );
        final Fact stilton = cheese.createFact( 0 );
        stilton.setFieldValue( "name",
                               "stilton" );
        stilton.setFieldValue( "price",
                               new Integer( 100 ) );
        InternalFactHandle stiltonHandle = (InternalFactHandle) workingMemory.insert( stilton );
        // TODO does not work for facts now. adding equals(object) to it.
        //        workingMemory    = SerializationHelper.serializeObject(workingMemory);

        workingMemory.fireAllRules();

        assertEquals( 1,
                      ((List) workingMemory.getGlobal( "list" )).size() );
        assertEquals( stilton,
                      ((List) workingMemory.getGlobal( "list" )).get( 0 ) );
        final Fact fact = (Fact) ((List) workingMemory.getGlobal( "list" )).get( 0 );
        assertEquals( stilton,
                      fact );
        assertEquals( new Integer( 200 ),
                      fact.getFieldValue( "price" ) );
        assertEquals( -1,
                      stiltonHandle.getId() );
    }

    public void testFactTemplateFieldBinding() throws Exception {
        // from JBRULES-1512
        String rule1 = "package org.drools.entity\n";
        rule1 += " global java.util.List list\n";
        rule1 += "template Settlement\n";
        rule1 += "    String InstrumentType\n";
        rule1 += "    String InstrumentName\n";
        rule1 += "end\n" + "rule TestEntity\n";
        rule1 += "    when\n";
        rule1 += "        Settlement(InstrumentType == \"guitar\", name : InstrumentName)\n";
        rule1 += "    then \n";
        rule1 += "        list.add( name ) ;\n";
        rule1 += "end\n";

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        WorkingMemory wm = ruleBase.newStatefulSession();
        List list = new ArrayList();
        wm.setGlobal( "list",
                      list );

        final FactTemplate cheese = pkg.getFactTemplate( "Settlement" );
        final Fact guitar = cheese.createFact( 0 );
        guitar.setFieldValue( "InstrumentType",
                              "guitar" );
        guitar.setFieldValue( "InstrumentName",
                              "gibson" );
        wm.insert( guitar );

        wm.fireAllRules();
        assertEquals( "gibson",
                      list.get( 0 ) );
    }

    public void testPropertyChangeSupportOldAPI() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_PropertyChange.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

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
        
        
        StatefulKnowledgeSession ksesion = SerializationHelper.getSerialisedStatefulKnowledgeSession( new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) session), MarshallerFactory.newIdentityMarshallingStrategy(), false );

        ksesion.fireAllRules();
        assertEquals( 3,
                      ((List) session.getGlobal( "list" )).size() );

    }

    public void testPropertyChangeSupportNewAPI() throws Exception {
        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_PropertyChangeTypeDecl.drl" ) ),
                     ResourceType.DRL );
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
    
    public void testDisconnectedFactHandle() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        DefaultFactHandle helloHandle = ( DefaultFactHandle ) ksession.insert( "hello" );
        DefaultFactHandle goodbyeHandle = ( DefaultFactHandle ) ksession.insert( "goodbye" );
        
        org.drools.runtime.rule.FactHandle key = new DisconnectedFactHandle( helloHandle.toExternalForm() );
        assertEquals( "hello", ksession.getObject( key ) );
        
        key = new DisconnectedFactHandle( goodbyeHandle.toExternalForm() );
        assertEquals( "goodbye", ksession.getObject( key ) );
        
    }

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
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        session.fireAllRules();

    }

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

        StatefulSession session = ruleBase.newStatefulSession();
        session.insert( cell1 );
        FactHandle cellHandle = session.insert( cell );

        StatefulKnowledgeSession ksesion = SerializationHelper.getSerialisedStatefulKnowledgeSession( new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) session), MarshallerFactory.newIdentityMarshallingStrategy(), false );
        
        ksesion.fireAllRules();
        assertEquals( 9,
                      cell.getValue() );
    }

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

    public void testQuery() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "simple_query_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        
        //ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        session.insert( stilton );
//        session = SerializationHelper.getSerialisedStatefulSession( session,
//                                                                    ruleBase );
        for ( int i = 0; i < 10000; i++) {
	        final QueryResults results = session.getQueryResults( "simple query" );
	        assertEquals( 1,
	                      results.size() );
	        System.gc();
	        Thread.sleep( 200 );
        }
        
    }

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

    public void testReturnValue() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "returnvalue_rule_test.drl" ) ) );
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

    public void testPredicate() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_rule_test.drl" ) ) );
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

    public void testNullConstraint() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "null_constraint.drl" ) ) );
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

    public void testBasicFrom() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_From.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final List list1 = new ArrayList();
        workingMemory.setGlobal( "list1",
                                 list1 );
        final List list2 = new ArrayList();
        workingMemory.setGlobal( "list2",
                                 list2 );
        final List list3 = new ArrayList();
        workingMemory.setGlobal( "list3",
                                 list3 );

        final Cheesery cheesery = new Cheesery();
        final Cheese stilton = new Cheese( "stilton",
                                           12 );
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        cheesery.addCheese( stilton );
        cheesery.addCheese( cheddar );
        workingMemory.setGlobal( "cheesery",
                                 cheesery );
        workingMemory.insert( cheesery );

        Person p = new Person( "stilton" );
        workingMemory.insert( p );

        // TODO - can not serializing rule with basic from: java.io.EOFException.
        //        workingMemory    = SerializationHelper.serializeObject(workingMemory);
        workingMemory.fireAllRules();
        //        workingMemory    = SerializationHelper.serializeObject(workingMemory);
        workingMemory.fireAllRules();

        // from using a global
        assertEquals( 2,
                      ((List) workingMemory.getGlobal( "list1" )).size() );
        assertEquals( cheddar,
                      ((List) workingMemory.getGlobal( "list1" )).get( 0 ) );
        assertEquals( stilton,
                      ((List) workingMemory.getGlobal( "list1" )).get( 1 ) );

        // from using a declaration
        assertEquals( 2,
                      ((List) workingMemory.getGlobal( "list2" )).size() );
        assertEquals( cheddar,
                      ((List) workingMemory.getGlobal( "list2" )).get( 0 ) );
        assertEquals( stilton,
                      ((List) workingMemory.getGlobal( "list2" )).get( 1 ) );

        // from using a declaration
        assertEquals( 1,
                      ((List) workingMemory.getGlobal( "list3" )).size() );
        assertEquals( stilton,
                      ((List) workingMemory.getGlobal( "list3" )).get( 0 ) );
    }

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

        WorkingMemoryInMemoryLogger logger = new WorkingMemoryInMemoryLogger( session );
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

    public void testWithInvalidRule() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
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

    public void testWithInvalidRule2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "invalid_rule2.drl" ) ) );
        assertTrue( builder.hasErrors() );
        String err = builder.getErrors().toString();
        System.out.println( err );
    }

    public void testErrorLineNumbers() throws Exception {
        // this test aims to test semantic errors
        // parser errors are another test case
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "errors_in_rule.drl" ) ) );
        final Package pkg = builder.getPackage();

        final DroolsError err = builder.getErrors().getErrors()[0];
        final DescrBuildError ruleErr = (DescrBuildError) err;
        assertNotNull( ruleErr.getDescr() );
        assertTrue( ruleErr.getLine() != -1 );

        final DroolsError errs[] = builder.getErrors().getErrors();

        assertEquals( 3,
                      builder.getErrors().getErrors().length );

        // check that its getting it from the ruleDescr
        assertEquals( ruleErr.getLine(),
                      ruleErr.getDescr().getLine() );
        // check the absolute error line number (there are more).
        assertEquals( 11,
                      ruleErr.getLine() );

        // now check the RHS, not being too specific yet, as long as it has the
        // rules line number, not zero
        final DescrBuildError rhs = (DescrBuildError) builder.getErrors().getErrors()[2];
        assertTrue( rhs.getLine() > 7 ); // not being too specific - may need to
        // change this when we rework the error
        // reporting

    }

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

        assertEquals( 5,
                      ((List) workingMemory.getGlobal( "list" )).size() );
        assertTrue( ((List) workingMemory.getGlobal( "list" )).contains( "first" ) );
        assertTrue( ((List) workingMemory.getGlobal( "list" )).contains( "second" ) );
        assertTrue( ((List) workingMemory.getGlobal( "list" )).contains( "third" ) );
        assertTrue( ((List) workingMemory.getGlobal( "list" )).contains( "fourth" ) );
        assertTrue( ((List) workingMemory.getGlobal( "list" )).contains( "fifth" ) );

    }

    public void testPredicateAsFirstPattern() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_as_first_pattern.drl" ) ) );
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

        Assert.assertEquals( "The rule is being incorrectly fired",
                             35,
                             mussarela.getPrice() );
        Assert.assertEquals( "Rule is incorrectly being fired",
                             20,
                             provolone.getPrice() );
    }

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

        public void handleException(org.drools.spi.Activation activation,
                                    org.drools.WorkingMemory workingMemory,
                                    Exception exception) {
            this.called = true;
        }

        public boolean isCalled() {
            return this.called;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            called = in.readBoolean();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeBoolean( called );
        }
    }

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

    public void testPredicateException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_PredicateException.drl" ) ) );
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
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

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
            assertTrue( e.getCause().getMessage().endsWith( "this should throw an exception" ) );
        }
    }

    public void testMultiRestrictionFieldConstraint() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MultiRestrictionFieldConstraint.drl" ) ) );
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

    public void testDumpers() throws Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "test_Dumpers.drl" ) ) );

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

    public void testContainsCheese() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ContainsCheese.drl" ) ) );
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

        Assert.assertEquals( "Indexing with null values is not working correctly.",
                             "OK",
                             bob.getStatus() );
        Assert.assertEquals( "Indexing with null values is not working correctly.",
                             "OK",
                             pete.getStatus() );

    }

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

    public void testJoinNodeModifyObject() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_JoinNodeModifyObject.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg1 = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( workingMemory );
        logger.setFileName( "log_20080401" );

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
            Assert.assertTrue( "Processing generated errors: " + errors.toString(),
                               errors.isEmpty() );
            for ( int i = 1; i <= MAX; i++ ) {
                final IndexedNumber n = (IndexedNumber) orderedFacts.get( i - 1 );
                Assert.assertEquals( "Fact is out of order",
                                     i,
                                     n.getIndex() );
            }
        } finally {
            logger.writeToDisk();
        }
    }

    public void testQuery2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Query.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.fireAllRules();

        final QueryResults results = workingMemory.getQueryResults( "assertedobjquery" );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value1" ),
                      results.get( 0 ).get( 0 ) );
    }

    public void testQueryWithParams() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_QueryWithParams.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.fireAllRules();

        QueryResults results = workingMemory.getQueryResults( "assertedobjquery",
                                                              new String[]{"value1"} );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value1" ),
                      results.get( 0 ).get( 0 ) );

        results = workingMemory.getQueryResults( "assertedobjquery",
                                                 new String[]{"value3"} );
        assertEquals( 0,
                      results.size() );

        results = workingMemory.getQueryResults( "assertedobjquery2",
                                                 new String[]{null, "value2"} );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value2" ),
                      results.get( 0 ).get( 0 ) );

        results = workingMemory.getQueryResults( "assertedobjquery2",
                                                 new String[]{"value3", "value2"} );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value2" ),
                      results.get( 0 ).get( 0 ) );
    }

    public void testQueryWithParamsOnKnowledgeApi() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_QueryWithParams.drl" , getClass() ), ResourceType.DRL );

        if  ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();

        org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "assertedobjquery",
                                                              new String[]{"value1"} );
        assertEquals( 1,
                      results.size() );
//        assertEquals( new InsertedObject( "value1" ),
//                      results.get( 0 ).get( 0 ) );

        results = ksession.getQueryResults( "assertedobjquery",
                                                 new String[]{"value3"} );
        assertEquals( 0,
                      results.size() );

        results = ksession.getQueryResults( "assertedobjquery2",
                                                 new String[]{null, "value2"} );
        assertEquals( 1,
                      results.size() );
        
        assertEquals( new InsertedObject( "value2" ), ((org.drools.runtime.rule.QueryResultsRow)results.iterator().next()).get( "assertedobj" ) );

        results = ksession.getQueryResults( "assertedobjquery2",
                                                 new String[]{"value3", "value2"} );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value2" ), ((org.drools.runtime.rule.QueryResultsRow)results.iterator().next()).get( "assertedobj" ) );
    }
    
    public void testQueryWithMultipleResultsOnKnowledgeApi() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Cheese \n";
        str += "query cheeses \n";
        str += "    stilton : Cheese(type == 'stilton') \n";
        str += "    cheddar : Cheese(type == 'cheddar', price == stilton.price) \n";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if  ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Cheese stilton1 = new Cheese( "stilton", 1);
        Cheese cheddar1 = new Cheese( "cheddar", 1);
        Cheese stilton2 = new Cheese( "stilton", 2);
        Cheese cheddar2 = new Cheese( "cheddar", 2);
        Cheese stilton3 = new Cheese( "stilton", 3);
        Cheese cheddar3 = new Cheese( "cheddar", 3);  
        
        Set set = new HashSet();
        List list = new ArrayList();
        list.add(stilton1);
        list.add(cheddar1);
        set.add( list );
        
        list = new ArrayList();
        list.add(stilton2);
        list.add(cheddar2);
        set.add( list );
        
        list = new ArrayList();
        list.add(stilton3);
        list.add(cheddar3);
        set.add( list );
        
        ksession.insert( stilton1 );
        ksession.insert( stilton2 );
        ksession.insert( stilton3 );
        ksession.insert( cheddar1 );
        ksession.insert( cheddar2 );
        ksession.insert( cheddar3 );
        
        org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "cheeses" );  
        assertEquals( 3, results.size() );        
        assertEquals( 2, results.getIdentifiers().length );
        Set newSet = new HashSet();
        for ( org.drools.runtime.rule.QueryResultsRow result : results ) {
            list = new ArrayList();
            list.add( result.get( "stilton" ) );
            list.add( result.get( "cheddar" ));
            newSet.add( list );
        }
        assertEquals( set, newSet );
        
        
        FlatQueryResults flatResults = new FlatQueryResults( ((StatefulKnowledgeSessionImpl)ksession).session.getQueryResults( "cheeses" ) );
        assertEquals( 3, flatResults.size() );
        assertEquals( 2, flatResults.getIdentifiers().length );
        newSet = new HashSet();
        for ( org.drools.runtime.rule.QueryResultsRow result : flatResults ) {
            list = new ArrayList();
            list.add( result.get( "stilton" ) );
            list.add( result.get( "cheddar" ));
            newSet.add( list );
        }
        assertEquals( set, newSet );        
    }
    
    public void testTwoQuerries() throws Exception {
        // @see JBRULES-410 More than one Query definition causes an incorrect
        // Rete network to be built.

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_TwoQuerries.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        workingMemory.insert( stilton );
        final Person per1 = new Person( "stinker",
                                        "smelly feet",
                                        70 );
        final Person per2 = new Person( "skunky",
                                        "smelly armpits",
                                        40 );

        workingMemory.insert( per1 );
        workingMemory.insert( per2 );

        QueryResults results = workingMemory.getQueryResults( "find stinky cheeses" );
        assertEquals( 1,
                      results.size() );

        results = workingMemory.getQueryResults( "find pensioners" );
        assertEquals( 1,
                      results.size() );
    }

    public void testInsurancePricingExample() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "insurance_pricing_example.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        // now create some test data
        final Driver driver = new Driver();
        final Policy policy = new Policy();

        wm.insert( driver );
        wm.insert( policy );

        wm.fireAllRules();

        assertEquals( 120,
                      policy.getBasePrice() );
    }

    public void testLLR() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_JoinNodeModifyTuple.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        // 1st time
        org.drools.Target tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.26544f ) );
        tgt.setLon( new Float( 28.952137f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.8666667f ) );
        wm.insert( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.236874f ) );
        tgt.setLon( new Float( 28.992579f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.8666667f ) );
        wm.insert( tgt );

        wm.fireAllRules();

        // 2nd time
        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.265343f ) );
        tgt.setLon( new Float( 28.952267f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.9f ) );
        wm.insert( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.236935f ) );
        tgt.setLon( new Float( 28.992493f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.9f ) );
        wm.insert( tgt );

        wm.fireAllRules();

        // 3d time
        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.26525f ) );
        tgt.setLon( new Float( 28.952396f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.9333333f ) );
        wm.insert( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.236996f ) );
        tgt.setLon( new Float( 28.992405f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.9333333f ) );
        wm.insert( tgt );

        wm.fireAllRules();

        // 4th time
        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.265163f ) );
        tgt.setLon( new Float( 28.952526f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.9666667f ) );
        wm.insert( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.237057f ) );
        tgt.setLon( new Float( 28.99232f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.9666667f ) );
        wm.insert( tgt );

        wm.fireAllRules();
    }

    public void testDoubleQueryWithExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DoubleQueryWithExists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Person p1 = new Person( "p1",
                                      "stilton",
                                      20 );
        p1.setStatus( "europe" );
        final FactHandle c1FactHandle = workingMemory.insert( p1 );
        final Person p2 = new Person( "p2",
                                      "stilton",
                                      30 );
        p2.setStatus( "europe" );
        final FactHandle c2FactHandle = workingMemory.insert( p2 );
        final Person p3 = new Person( "p3",
                                      "stilton",
                                      40 );
        p3.setStatus( "europe" );
        final FactHandle c3FactHandle = workingMemory.insert( p3 );
        workingMemory.fireAllRules();

        QueryResults queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      queryResults.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p3.setStatus( "america" );
        workingMemory.update( c3FactHandle,
                              p3 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ 1 ], america=[ 2, 3 ]
        p2.setStatus( "america" );
        workingMemory.update( c2FactHandle,
                              p2 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ ], america=[ 1, 2, 3 ]
        p1.setStatus( "america" );
        workingMemory.update( c1FactHandle,
                              p1 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      queryResults.size() );

        // europe=[ 2 ], america=[ 1, 3 ]
        p2.setStatus( "europe" );
        workingMemory.update( c2FactHandle,
                              p2 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p1.setStatus( "europe" );
        workingMemory.update( c1FactHandle,
                              p1 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ 1, 2, 3 ], america=[ ]
        p3.setStatus( "europe" );
        workingMemory.update( c3FactHandle,
                              p3 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      queryResults.size() );
    }

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

    public void testDeclaringAndUsingBindsInSamePattern() throws Exception {
        final RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setRemoveIdentities( true );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DeclaringAndUsingBindsInSamePattern.drl" ) ) );
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

    public void testMissingImports() {
        try {
            final PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_missing_import.drl" ) ) );
            final Package pkg = builder.getPackage();

            RuleBase ruleBase = getRuleBase();
            ruleBase.addPackage( pkg );
            ruleBase = SerializationHelper.serializeObject( ruleBase );

            Assert.fail( "Should have thrown an InvalidRulePackage" );
        } catch ( final InvalidRulePackage e ) {
            // everything fine
        } catch ( final Exception e ) {
            e.printStackTrace();
            Assert.fail( "Should have thrown an InvalidRulePackage Exception instead of " + e.getMessage() );
        }
    }

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

        Assert.assertEquals( "Rule should have fired twice, seting the price to 30",
                             30,
                             e.getPrice() );
        // success
    }

    public void testImportConflict() throws Exception {
        RuleBase ruleBase = getRuleBase();
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ImportConflict.drl" ) ) );
        final Package pkg = builder.getPackage();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
    }

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

    public void testDuplicateVariableBindingError() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_duplicateVariableBindingError.drl" ) ) );
        final Package pkg = builder.getPackage();

        assertFalse( pkg.isValid() );
        System.out.println( pkg.getErrorSummary() );
        assertEquals( 6,
                      pkg.getErrorSummary().split( "\n" ).length );
    }

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

            public void activationCancelled(ActivationCancelledEvent event,
                                            WorkingMemory workingMemory) {
                agendaList.add( event );

            }

            public void activationCreated(ActivationCreatedEvent event,
                                          WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void afterActivationFired(AfterActivationFiredEvent event,
                                             WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event,
                                          WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event,
                                          WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void beforeActivationFired(BeforeActivationFiredEvent event,
                                              WorkingMemory workingMemory) {
                agendaList.add( event );
            }

        };

        final List wmList = new ArrayList();
        final WorkingMemoryEventListener workingMemoryListener = new WorkingMemoryEventListener() {

            public void objectInserted(ObjectInsertedEvent event) {
                wmList.add( event );
            }

            public void objectUpdated(ObjectUpdatedEvent event) {
                wmList.add( event );
            }

            public void objectRetracted(ObjectRetractedEvent event) {
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

    public void testAutomaticBindingsErrors() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_AutoBindingsErrors.drl" ) ) );
        final Package pkg = builder.getPackage();

        assertNotNull( pkg.getErrorSummary() );
    }

    public void testQualifiedFieldReference() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_QualifiedFieldReference.drl" ) ) );
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

    public void testEvalRewrite() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EvalRewrite.drl" ) ) );
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

    public void testMapAccess() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MapAccess.drl" ) ) );
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

    // JBRULES-1808
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

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

    public void testBindingsOnConnectiveExpressions() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_bindings.drl" ) ) );
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

    public void testCrossProductRemovingIdentityEquals() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( MiscTest.class.getResourceAsStream( "test_CrossProductRemovingIdentityEquals.drl" ) ) );

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
        SpecialString second43 = new SpecialString( "42" );
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

        assertEquals( second43,
                      list1.get( 0 ) );
        assertEquals( first42,
                      list1.get( 1 ) );
        assertEquals( second43,
                      list1.get( 2 ) );
        assertEquals( world,
                      list1.get( 3 ) );
        assertEquals( world,
                      list1.get( 4 ) );
        assertEquals( first42,
                      list1.get( 5 ) );

        assertEquals( first42,
                      list2.get( 0 ) );
        assertEquals( second43,
                      list2.get( 1 ) );
        assertEquals( world,
                      list2.get( 2 ) );
        assertEquals( second43,
                      list2.get( 3 ) );
        assertEquals( first42,
                      list2.get( 4 ) );
        assertEquals( world,
                      list2.get( 5 ) );
    }

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

    public void testDynamicallyAddInitialFactRule() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        String rule = "package org.drools.test\n global java.util.List list\n rule xxx\n when\n i:Integer()\nthen\n list.add(i);\nend";
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
        rule = "package org.drools.test\n global java.util.List list\n rule xxx\n when\nthen\n list.add(\"x\");\nend";
        builder.addPackageFromDrl( new StringReader( rule ) );
        pkg = builder.getPackage();

        // Make sure that this rule is fired as the Package is updated, it also tests that InitialFactImpl is still in the network
        // even though the first rule didn't use it.
        ruleBase.addPackage( pkg );
        
        session.fireAllRules();

        assertEquals( "x",
                      list.get( 1 ) );

    }

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

    public void testAutovivificationOfVariableRestrictions() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_AutoVivificationVR.drl" ) ) );
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

    public void testQueryWithCollect() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Query.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.fireAllRules();

        final QueryResults results = workingMemory.getQueryResults( "collect objects" );
        assertEquals( 1,
                      results.size() );

        final QueryResult result = results.get( 0 );
        final List list = (List) result.get( "$list" );

        assertEquals( 2,
                      list.size() );
    }

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

    public void testWorkingMemoryLoggerWithUnbalancedBranches() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Logger.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory wm = ruleBase.newStatefulSession();

        try {
            final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( wm );
            logger.setFileName( "testLogger" );

            wm.fireAllRules();

            wm.insert( new Cheese( "a",
                                   10 ) );
            wm.insert( new Cheese( "b",
                                   11 ) );

            wm.fireAllRules();

            //            logger.writeToDisk();
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "No exception should be raised " );
        }

    }

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

    public void testConsequenceBuilderException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConsequenceBuilderException.drl" ) ) );

        assertTrue( builder.hasErrors() );
    }

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

    public void testModifyBlock() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ModifyBlock.drl" ) ) );
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
        Address addr = new Address("abc");
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

    public void testOrCE() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_OrCE.drl" ) ) );
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

    public void testNPEOnParenthesis() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_ParenthesisUsage.drl" ) ), 
                      ResourceType.DRL );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        final List<Person> results = new ArrayList<Person>(); 
        
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.setGlobal( "results", results );
        
        Person bob = new Person( "Bob", 20 );
        bob.setAlive( true );
        Person foo = new Person( "Foo", 0 );
        foo.setAlive( false );
        
        session.insert( bob );
        session.fireAllRules();
        
        assertEquals( 1, results.size() );
        assertEquals( bob, results.get( 0 ) );
        
        session.insert( foo );
        session.fireAllRules();
        
        assertEquals( 2, results.size() );
        assertEquals( foo, results.get( 1 ) );
    }

    public void testEvalWithLineBreaks() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_EvalWithLineBreaks.drl" ) ), 
                      ResourceType.DRL );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        final List<Person> results = new ArrayList<Person>(); 
        
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.setGlobal( "results", results );
        
        session.insert( Integer.valueOf( 10 ) );
        session.fireAllRules();
        
        assertEquals( 1, results.size() );
        assertEquals( Integer.valueOf( 10 ), results.get( 0 ) );
    }

    public void testDRLWithoutPackageDeclaration() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_NoPackageDeclaration.drl" ) ), 
                      ResourceType.DRL );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        // no package defined, so it is set to the default
        final FactType factType = kbase.getFactType( "defaultpkg", "Person" );
        assertNotNull( factType );
        final Object bob = factType.newInstance();
        factType.set( bob, "name", "Bob" );
        factType.set( bob, "age", Integer.valueOf( 30 ) );
        
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        final List results = new ArrayList(); 
        session.setGlobal( "results", results );
        
        session.insert( bob );
        session.fireAllRules();
        
        assertEquals( 1, results.size() );
        assertEquals( bob, results.get( 0 ) );
    }
    
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

    public void testListOfMaps(){
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("test_TestMapVariableRef.drl", getClass()), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        
        Map mapOne = new HashMap<String,Object>();
        Map mapTwo = new HashMap<String,Object>();
        
        mapOne.put("MSG", "testMessage");
        mapTwo.put("MSGTWO", "testMessage");
        
        list.add(mapOne);
        list.add(mapTwo);
        ksession.insert(list);
        ksession.fireAllRules();
        
        assertEquals(3, list.size());
        
    }

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
        ksession.insert( new Cheese("stilton") );
        ksession.insert( new Cheese("brie") );
        ksession.insert( new Cheese("muzzarella") );
        ksession.insert( new Person( "bob", "stilton" ) );
        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );
        assertEquals( "stilton",
                      results.get( 0 ) );
        assertEquals( "brie",
                      results.get( 1 ) );
        
    }
    
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
        ksession.insert( new Move(1, 2) );
        ksession.insert( new Move(2, 3) );
        
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
        ksession.insert( new Move(2, 3) );
        ksession.insert( new Move(1, 2) );
        
        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( win2 ) );
        assertTrue( results.contains( win3 ) );
        
    }
    
    public void testDroolsQueryCleanup() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_QueryMemoryLeak.drl",
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
        
        String workerId = "B1234";
        Worker worker = new Worker();
        worker.setId(workerId);
        
        org.drools.runtime.rule.FactHandle handle = ksession.insert(worker);
        ksession.fireAllRules();
        
        assertNotNull(handle);
        
        Object retractedWorker = null;
        for(int i = 0; i < 100; i++) {
            retractedWorker = (Object)ksession.getQueryResults("getWorker", new Object[] {workerId});
        }
        
        assertNotNull(retractedWorker);
        
        StatefulKnowledgeSessionImpl sessionImpl = (StatefulKnowledgeSessionImpl)ksession;
        
        ReteooWorkingMemory reteWorkingMemory = sessionImpl.session;
        AbstractWorkingMemory abstractWorkingMemory = (AbstractWorkingMemory)reteWorkingMemory;
        
        InternalRuleBase ruleBase = (InternalRuleBase)abstractWorkingMemory.getRuleBase();
        Collection<EntryPointNode> entryPointNodes = ruleBase.getRete().getEntryPointNodes().values();
        
        EntryPointNode defaultEntryPointNode = null; 
        for(EntryPointNode epNode : entryPointNodes) {
            if(epNode.getEntryPoint().getEntryPointId() == "DEFAULT") {
                defaultEntryPointNode = epNode;
                break;
            }
        }
        assertNotNull(defaultEntryPointNode);
        
        Map<ObjectType, ObjectTypeNode> obnodes =  defaultEntryPointNode.getObjectTypeNodes();

        ObjectType key = new ClassObjectType(DroolsQuery.class);
        ObjectTypeNode droolsQueryNode = obnodes.get(key);
        ObjectHashSet droolsQueryMemory = (ObjectHashSet)abstractWorkingMemory.getNodeMemory(droolsQueryNode);
        assertEquals(0, droolsQueryMemory.size());
        
        Entry[] entries = droolsQueryMemory.getTable();
        int entryCounter = 0;
        for(Entry entry : entries) {
            if (entry != null) {
                entryCounter++;
                ObjectEntry oEntry = (ObjectEntry)entry;
                DefaultFactHandle factHandle = (DefaultFactHandle)oEntry.getValue();
                assertNull(factHandle.getObject());
            }
        }
    }
    
}
