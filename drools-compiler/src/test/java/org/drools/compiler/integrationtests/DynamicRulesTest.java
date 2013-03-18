/*
 * Copyright 2010 JBoss Inc
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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.Person;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.Precondition;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.RuleBaseFactory;
import org.drools.core.StatefulSession;
import org.drools.compiler.StockTick;
import org.drools.core.common.InternalFactHandle;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.definitions.impl.KnowledgePackageImp;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.drools.core.reteoo.ReteooRuleBase;
import org.drools.core.rule.Package;
import org.junit.Test;
import org.kie.internal.KieBaseConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.rule.SessionEntryPoint;
import org.mockito.ArgumentCaptor;

public class DynamicRulesTest extends CommonTestMethodBase {

    @Test
    public void testDynamicRuleAdditions() throws Exception {
        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_Dynamic1.drl" ) );
        StatefulKnowledgeSession workingMemory = createKnowledgeSession( kbase );
        workingMemory.setGlobal( "total",
                                 new Integer( 0 ) );

        final List< ? > list = new ArrayList<Object>();
        workingMemory.setGlobal( "list",
                                 list );

        // Adding person in advance. There is no Person() object
        // type node in memory yet, but the rule engine is supposed
        // to handle that correctly
        final PersonInterface bob = new Person( "bob",
                                                "stilton" );
        bob.setStatus( "Not evaluated" );
        workingMemory.insert( bob );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.insert( stilton );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.insert( cheddar );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( "stilton",
                      list.get( 0 ) );

        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_Dynamic2.drl" ) );
        kbase.addKnowledgePackages( kpkgs );

        workingMemory.fireAllRules();
        assertEquals( 3,
                      list.size() );

        assertEquals( "stilton",
                      list.get( 0 ) );

        assertTrue( "cheddar".equals( list.get( 1 ) ) || "cheddar".equals( list.get( 2 ) ) );

        assertTrue( "stilton".equals( list.get( 1 ) ) || "stilton".equals( list.get( 2 ) ) );

        list.clear();

        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_Dynamic3.drl" ) );
        kbase.addKnowledgePackages( kpkgs );

        // Package 3 has a rule working on Person instances.
        // As we added person instance in advance, rule should fire now
        workingMemory.fireAllRules();

        assertEquals( "Rule from package 3 should have been fired",
                      "match Person ok",
                      bob.getStatus() );

        assertEquals( 1,
                      list.size() );

        assertEquals( bob,
                      list.get( 0 ) );

        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_Dynamic4.drl" ) );
        kbase.addKnowledgePackages( kpkgs );
        workingMemory.fireAllRules();
        kbase = SerializationHelper.serializeObject( kbase );

        assertEquals( "Rule from package 4 should have been fired",
                      "Who likes Stilton ok",
                      bob.getStatus() );

        assertEquals( 2,
                      list.size() );

        assertEquals( bob,
                      list.get( 1 ) );

    }

    @Test
    public void testDynamicRuleRemovals() throws Exception {
        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_Dynamic1.drl", "test_Dynamic3.drl", "test_Dynamic4.drl" ) );

        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_Dynamic2.drl" ) );
        kbase.addKnowledgePackages( kpkgs );

        StatefulKnowledgeSession workingMemory = createKnowledgeSession( kbase );
        AgendaEventListener ael = mock( AgendaEventListener.class );
        workingMemory.addEventListener( ael );

        final List< ? > list = new ArrayList<Object>();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface bob = new Person( "bob",
                                                "stilton" );
        bob.setStatus( "Not evaluated" );
        workingMemory.insert( bob );

        final Cheese stilton1 = new Cheese( "stilton",
                                            5 );
        workingMemory.insert( stilton1 );

        final Cheese stilton2 = new Cheese( "stilton",
                                            3 );
        workingMemory.insert( stilton2 );

        final Cheese stilton3 = new Cheese( "stilton",
                                            1 );
        workingMemory.insert( stilton3 );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.insert( cheddar );

        verify( ael, times( 15 ) ).matchCreated(any(MatchCreatedEvent.class));
        verify( ael, never() ).matchCancelled(any(MatchCancelledEvent.class));

        kbase.removeRule( "org.drools.compiler.test",
                          "Who likes Stilton" );

        verify( ael, times( 3 ) ).matchCancelled(any(MatchCancelledEvent.class));

        kbase.removeRule( "org.drools.compiler.test",
                          "like cheese" );

        verify( ael, times( 7 ) ).matchCancelled(any(MatchCancelledEvent.class));

        final Cheese muzzarela = new Cheese( "muzzarela",
                                             5 );
        workingMemory.insert( muzzarela );

        verify( ael, times( 16 ) ).matchCreated(any(MatchCreatedEvent.class));

        kbase.removeKnowledgePackage( "org.drools.compiler.test" );
        verify( ael, times( 16 ) ).matchCancelled(any(MatchCancelledEvent.class));

        kbase = SerializationHelper.serializeObject( kbase );
    }

    @Test
    public void testDynamicRuleRemovalsUnusedWorkingMemory() throws Exception {

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_Dynamic1.drl", 
                                                                                      "test_Dynamic2.drl", 
                                                                                      "test_Dynamic3.drl", 
                                                                                      "test_Dynamic4.drl" ) );

        StatefulKnowledgeSession workingMemory = createKnowledgeSession( kbase );

        assertEquals( 1,
                      kbase.getKnowledgePackages().size() );
        assertEquals( 5,
                      kbase.getKnowledgePackages().iterator().next().getRules().size() );

        kbase.removeRule( "org.drools.compiler.test",
                          "Who likes Stilton" );
        assertEquals( 4,
                      kbase.getKnowledgePackages().iterator().next().getRules().size() );

        kbase.removeRule( "org.drools.compiler.test",
                          "like cheese" );
        assertEquals( 3,
                      kbase.getKnowledgePackages().iterator().next().getRules().size() );

        kbase.removeKnowledgePackage( "org.drools.compiler.test" );
        assertEquals( 0,
                      kbase.getKnowledgePackages().size() );
    }

    @Test
    public void testDynamicFunction() throws Exception {
        //FIXME JBRULES-1258 serialising a package breaks function removal -- left the serialisation commented out for now
        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_DynamicFunction1.drl" ) );
        KnowledgeBase kbase = loadKnowledgeBase( );

        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );
        
        final StatefulKnowledgeSession workingMemory = createKnowledgeSession( kbase );

        final List<?> list = new ArrayList<Object>();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.insert( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );

        // Check a function can be removed from a package.
        // Once removed any efforts to use it should throw an Exception
        kbase.removeFunction( "org.drools.compiler.test",
                              "addFive" );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.insert( cheddar );

        try {
            workingMemory.fireAllRules();
            fail( "Function should have been removed and NoClassDefFoundError thrown from the Consequence" );
        } catch ( final NoClassDefFoundError e ) {
        }

        // Check a new function can be added to replace an old function
        Collection<KnowledgePackage> kpkgs2 = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicFunction2.drl" ) );
        kbase.addKnowledgePackages( kpkgs2 );

        final Cheese brie = new Cheese( "brie",
                                        5 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 6 ),
                      list.get( 1 ) );

        Collection<KnowledgePackage> kpkgs3 = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicFunction3.drl" ) );
        kbase.addKnowledgePackages( kpkgs3 );

        final Cheese feta = new Cheese( "feta",
                                        5 );
        workingMemory.insert( feta );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 2 ) );
    }

    @Test
    public void testRemovePackage() throws Exception {
        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_RemovePackage.drl" ) );
        final String packageName = kpkgs.iterator().next().getName();
        KnowledgeBase kbase = loadKnowledgeBase( );

        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession session = createKnowledgeSession( kbase );

        session.insert( new Precondition( "genericcode",
                                          "genericvalue" ) );
        session.fireAllRules();

        KnowledgeBase ruleBaseWM = session.getKieBase();
        ruleBaseWM.removeKnowledgePackage( packageName );
        
        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_RemovePackage.drl" ) );
        ruleBaseWM.addKnowledgePackages( kpkgs );
        ruleBaseWM = SerializationHelper.serializeObject( ruleBaseWM );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, 
                                                                             true );
        session.fireAllRules();

        ruleBaseWM.removeKnowledgePackage( packageName );
        ruleBaseWM.addKnowledgePackages( SerializationHelper.serializeObject( kpkgs ) );

        ruleBaseWM.removeKnowledgePackage( packageName );
        ruleBaseWM.addKnowledgePackages( SerializationHelper.serializeObject( kpkgs ) );
    }

    @Test
    public void testDynamicRules() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( );
        StatefulKnowledgeSession session = createKnowledgeSession( kbase );
        final Cheese a = new Cheese( "stilton",
                                     10 );
        final Cheese b = new Cheese( "stilton",
                                     15 );
        final Cheese c = new Cheese( "stilton",
                                     20 );
        session.insert( a );
        session.insert( b );
        session.insert( c );

        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_DynamicRules.drl" ) );
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, 
                                                                             true );

        session.fireAllRules();
    }

    @Test
    public void testDynamicRules2() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( );
        StatefulKnowledgeSession session = createKnowledgeSession( kbase );

        // Assert some simple facts
        final FactA a = new FactA( "hello",
                                   new Integer( 1 ),
                                   new Float( 3.14 ) );
        final FactB b = new FactB( "hello",
                                   new Integer( 2 ),
                                   new Float( 6.28 ) );
        session.insert( a );
        session.insert( b );

        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_DynamicRules2.drl" ) );
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, 
                                                                             true );

        session.fireAllRules();
    }

    @Test
    public void testRuleBaseAddRemove() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( );

        //add and remove
        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_Dynamic1.drl" ) );
        String pkgName = kpkgs.iterator().next().getName();
        kbase.addKnowledgePackages( kpkgs );
        kbase.removeKnowledgePackage( pkgName );
        kbase = SerializationHelper.serializeObject( kbase );

        //add and remove again
        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_Dynamic1.drl" ) );
        pkgName = kpkgs.iterator().next().getName();
        kbase.addKnowledgePackages( kpkgs );
        kbase.removeKnowledgePackage( pkgName );
        kbase = SerializationHelper.serializeObject( kbase );
    }

    @Test
    public void testClassLoaderSwitchsUsingConf() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Class cheeseClass = loader1.loadClass( "org.drools.compiler.Cheese" );

            PackageBuilderConfiguration conf = new PackageBuilderConfiguration( loader1 );
            PackageBuilder builder = new PackageBuilder( conf );
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );

            // must set the classloader for rulebase conf too
            RuleBaseConfiguration rbconf = new RuleBaseConfiguration( loader1 );
            RuleBase ruleBase = RuleBaseFactory.newRuleBase( rbconf );
            Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );
            //            ruleBase    = SerializationHelper.serializeObject(ruleBase);

            StatefulSession wm = ruleBase.newStatefulSession();
            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            // Creates second class loader and use it to load fact classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            cheeseClass = loader2.loadClass( "org.drools.compiler.Cheese" );

            conf = new PackageBuilderConfiguration( loader2 );
            builder = new PackageBuilder( conf );
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );

            rbconf = new RuleBaseConfiguration( loader2 );
            ruleBase = RuleBaseFactory.newRuleBase( rbconf );
            pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );
            //            ruleBase    = SerializationHelper.serializeObject(ruleBase);

            wm = ruleBase.newStatefulSession();
            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();
        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }

    }

    @Test
    public void testClassLoaderSwitchsUsingContext() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Thread.currentThread().setContextClassLoader( loader1 );
            Class cheeseClass = loader1.loadClass( "org.drools.compiler.Cheese" );

            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );

            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );

            StatefulSession wm = ruleBase.newStatefulSession();
            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            // Creates second class loader and use it to load fact classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Thread.currentThread().setContextClassLoader( loader2 );
            cheeseClass = loader2.loadClass( "org.drools.compiler.Cheese" );

            builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );

            ruleBase = RuleBaseFactory.newRuleBase();
            pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );

            wm = ruleBase.newStatefulSession();
            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            Thread.currentThread().setContextClassLoader( original );
        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }
    }

    @Test
    public void testCollectDynamicRules() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_CollectDynamicRules1.drl" );
        StatefulKnowledgeSession session = createKnowledgeSession( kbase );

        List<?> list = new ArrayList<Object>();
        session.setGlobal( "results",
                                 list );

        session.insert( new Cheese( "stilton",
                                          10 ) );
        session.insert( new Cheese( "brie",
                                          10 ) );
        session.insert( new Cheese( "stilton",
                                          10 ) );
        session.insert( new Cheese( "muzzarela",
                                          10 ) );

        kbase.addKnowledgePackages( loadKnowledgePackages( "test_CollectDynamicRules2.drl" ) );
        session.fireAllRules();

        kbase = SerializationHelper.serializeObject( kbase );

        // fire all rules is automatic
        assertEquals( 1,
                      list.size() );
        assertEquals( 2,
                      ((List<?>) list.get( 0 )).size() );

    }

    @Test
    public void testDynamicNotNode() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_CollectDynamicRules1.drl" );
        kbase = SerializationHelper.serializeObject( kbase );
        Environment env = EnvironmentFactory.newEnvironment();
        env.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
                 new IdentityPlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT )} );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( null, env );
        List<?> results = new ArrayList<Object>();
        ksession.setGlobal( "results",
                            results );

        final Cheese a = new Cheese( "stilton",
                                     10 );
        final Cheese b = new Cheese( "stilton",
                                     15 );
        final Cheese c = new Cheese( "stilton",
                                     20 );
        ksession.insert( a );
        ksession.insert( b );
        ksession.insert( c );

        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicNotNode.drl" ) );
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              false );

        results = (List) ksession.getGlobal( "results" );

        ksession.fireAllRules();

        assertEquals( 0,
                      results.size() );

        kbase.removeKnowledgePackage( "org.drools.compiler" );

        ksession.retract( ksession.getFactHandle( b ) );

        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicNotNode.drl" ) );
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              false );

        results = (List<?>) ksession.getGlobal( "results" );
        ksession.fireAllRules();

        assertEquals( 1,
                      results.size() );
    }

    @Test
    public void testDynamicRulesAddRemove() {
        try {
            KnowledgeBase kbase = loadKnowledgeBase( "test_DynamicRulesTom.drl" );
            StatefulKnowledgeSession session = createKnowledgeSession( kbase );

            List<?> results = new ArrayList<Object>();
            session.setGlobal( "results",
                               results );

            InternalFactHandle h1 = (InternalFactHandle) session.insert( new Person( "tom",
                                                                                     1 ) );
            InternalFactHandle h2 = (InternalFactHandle) session.insert( new Person( "fred",
                                                                                     2 ) );
            InternalFactHandle h3 = (InternalFactHandle) session.insert( new Person( "harry",
                                                                                     3 ) );
            InternalFactHandle h4 = (InternalFactHandle) session.insert( new Person( "fred",
                                                                                     4 ) );
            InternalFactHandle h5 = (InternalFactHandle) session.insert( new Person( "ed",
                                                                                     5 ) );
            InternalFactHandle h6 = (InternalFactHandle) session.insert( new Person( "tom",
                                                                                     6 ) );
            InternalFactHandle h7 = (InternalFactHandle) session.insert( new Person( "sreeni",
                                                                                     7 ) );
            InternalFactHandle h8 = (InternalFactHandle) session.insert( new Person( "jill",
                                                                                     8 ) );
            InternalFactHandle h9 = (InternalFactHandle) session.insert( new Person( "ed",
                                                                                     9 ) );
            InternalFactHandle h10 = (InternalFactHandle) session.insert( new Person( "tom",
                                                                                      10 ) );

            session.fireAllRules();

            assertEquals( 3,
                          results.size() );
            assertTrue( results.contains( h1.getObject() ) );
            assertTrue( results.contains( h6.getObject() ) );
            assertTrue( results.contains( h10.getObject() ) );
            results.clear();

            kbase.addKnowledgePackages( loadKnowledgePackages( "test_DynamicRulesFred.drl" ) );
            session.fireAllRules();

            assertEquals( 2,
                          results.size() );
            assertTrue( results.contains( h2.getObject() ) );
            assertTrue( results.contains( h4.getObject() ) );
            results.clear();

            kbase.removeKnowledgePackage( "tom" );

            kbase.addKnowledgePackages( loadKnowledgePackages( "test_DynamicRulesEd.drl" ) );
            session.fireAllRules();

            assertEquals( 2,
                          results.size() );
            assertTrue( results.contains( h5.getObject() ) );
            assertTrue( results.contains( h9.getObject() ) );
            results.clear();

            ((Person) h3.getObject()).setName( "ed" );
            session.update( h3,
                            h3.getObject() );
            session.fireAllRules();

            assertEquals( 1,
                          results.size() );
            assertTrue( results.contains( h3.getObject() ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception: " + e.getMessage() );
        }
    }

    @Test
    public void testDynamicRuleRemovalsSubNetwork() throws Exception {
        Collection<KnowledgePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicRulesWithSubnetwork1.drl", 
                                                                                                         "test_DynamicRulesWithSubnetwork.drl" ) );
        
        KnowledgeBase kbase = loadKnowledgeBase();
        kbase.addKnowledgePackages( kpkgs );
        
        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicRulesWithSubnetwork2.drl" ) );
        kbase.addKnowledgePackages( kpkgs );

        StatefulKnowledgeSession session = createKnowledgeSession( kbase );

        final List<?> list = new ArrayList<Object>();
        session.setGlobal( "results",
                           list );

        Order order = new Order();

        OrderItem item1 = new OrderItem( order,
                                         1,
                                         "Adventure Guide Brazil",
                                         OrderItem.TYPE_BOOK,
                                         24 );
        order.addItem( item1 );
        session.insert( item1 );

        OrderItem item2 = new OrderItem( order,
                                         2,
                                         "Prehistoric Britain",
                                         OrderItem.TYPE_BOOK,
                                         15 );
        order.addItem( item2 );
        session.insert( item2 );

        OrderItem item3 = new OrderItem( order,
                                         3,
                                         "Holiday Music",
                                         OrderItem.TYPE_CD,
                                         9 );
        order.addItem( item3 );
        session.insert( item3 );

        OrderItem item4 = new OrderItem( order,
                                         4,
                                         "Very Best of Mick Jagger",
                                         OrderItem.TYPE_CD,
                                         11 );
        order.addItem( item4 );
        session.insert( item4 );

        session.insert( order );

        assertEquals( 11,
                      getInternalAgenda( session ).getActivations().length );

        kbase.removeRule( "org.drools.compiler",
                          "Apply Discount on all books" );
        assertEquals( 10,
                      getInternalAgenda( session ).getActivations().length );

        kbase.removeRule( "org.drools.compiler",
                          "like book" );

        final OrderItem item5 = new OrderItem( order,
                                               5,
                                               "Sinatra : Vegas",
                                               OrderItem.TYPE_CD,
                                               5 );
        assertEquals( 8,
                      getInternalAgenda( session ).getActivations().length );

        session.insert( item5 );

        assertEquals( 10,
                      getInternalAgenda( session ).getActivations().length );

        kbase.removeKnowledgePackage( "org.drools.compiler" );

        assertEquals( 0,
                      getInternalAgenda( session ).getActivations().length );
    }

    @Test
    public void testDynamicRuleRemovalsUnusedWorkingMemorySubNetwork() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_DynamicRulesWithSubnetwork1.drl",
                                                 "test_DynamicRulesWithSubnetwork2.drl",
                                                 "test_DynamicRulesWithSubnetwork.drl" );

        assertEquals( 1,
                      kbase.getKnowledgePackages().size() );
        assertEquals( 4,
                      kbase.getKnowledgePackages().iterator().next().getRules().size() );

        kbase.removeRule( "org.drools.compiler",
                "Apply Discount on all books" );
        assertEquals( 3,
                      kbase.getKnowledgePackages().iterator().next().getRules().size() );

        kbase.removeRule( "org.drools.compiler",
                "like book" );
        assertEquals( 2,
                      kbase.getKnowledgePackages().iterator().next().getRules().size() );

        kbase.removeKnowledgePackage( "org.drools.compiler" );
        assertEquals( 0,
                      kbase.getKnowledgePackages().size() );
    }

    @Test
    public void testRemovePackageSubNetwork() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_DynamicRulesWithSubnetwork.drl" );
        String packageName = kbase.getKnowledgePackages().iterator().next().getName();
        
        StatefulKnowledgeSession workingMemory = createKnowledgeSession( kbase );

        List<?> results = new ArrayList<Object>();
        workingMemory.setGlobal( "results",
                                 results );

        Order order = new Order();

        OrderItem item1 = new OrderItem( order,
                                         1,
                                         "Adventure Guide Brazil",
                                         OrderItem.TYPE_BOOK,
                                         24 );
        OrderItem item2 = new OrderItem( order,
                                         2,
                                         "Prehistoric Britain",
                                         OrderItem.TYPE_BOOK,
                                         15 );
        OrderItem item3 = new OrderItem( order,
                                         3,
                                         "Holiday Music",
                                         OrderItem.TYPE_CD,
                                         9 );
        OrderItem item4 = new OrderItem( order,
                                         4,
                                         "Very Best of Mick Jagger",
                                         OrderItem.TYPE_CD,
                                         11 );
        OrderItem item5 = new OrderItem( order,
                                         5,
                                         "The Master and Margarita",
                                         OrderItem.TYPE_BOOK,
                                         29 );

        order.addItem( item1 );
        order.addItem( item2 );
        order.addItem( item3 );
        order.addItem( item4 );
        order.addItem( item5 );

        workingMemory.insert( order );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        KnowledgeBase ruleBaseWM = workingMemory.getKieBase();
        ruleBaseWM.removeKnowledgePackage( packageName );
        
        Collection<KnowledgePackage> kpkgs = loadKnowledgePackages( "test_DynamicRulesWithSubnetwork.drl" );
        ruleBaseWM.addKnowledgePackages( SerializationHelper.serializeObject( kpkgs ) );
        
        workingMemory.fireAllRules();
        results = (List) workingMemory.getGlobal( "results" );
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        ruleBaseWM.removeKnowledgePackage( packageName );
        ruleBaseWM.addKnowledgePackages( SerializationHelper.serializeObject( kpkgs ) );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        ruleBaseWM.removeKnowledgePackage( packageName );
        ruleBaseWM.addKnowledgePackages( SerializationHelper.serializeObject( kpkgs ) );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();
    }

    @Test
    public void testRuleBaseAddRemoveSubNetworks() throws Exception {
        try {
            RuleBase ruleBase = RuleBaseFactory.newRuleBase();

            //add and remove
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork.drl" ) ) );
            Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );
            ruleBase.removePackage( pkg.getName() );

            //add and remove again
            builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRulesWithSubnetwork.drl" ) ) );
            pkg = SerializationHelper.serializeObject( builder.getPackage() );
            ruleBase.addPackage( pkg );
            ruleBase.removePackage( pkg.getName() );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception" );
        }
    }

    @Test
    public void testDynamicRuleAdditionsWithEntryPoints() throws Exception {
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_DynamicWithEntryPoint.drl" ) );
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( reader ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

        // now lets add some knowledge to the kbase
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List<StockTick> results = new ArrayList<StockTick>();
        ksession.setGlobal( "results",
                            results );

        SessionEntryPoint ep = ksession.getEntryPoint( "in-channel" );
        ep.insert( new StockTick( 1,
                                  "RHT",
                                  20,
                                  10000 ) );
        ep.insert( new StockTick( 2,
                                  "RHT",
                                  21,
                                  15000 ) );
        ep.insert( new StockTick( 3,
                                  "RHT",
                                  22,
                                  20000 ) );

        ksession.fireAllRules();
        assertEquals( 3,
                      results.size() );

    }

    @Test
    public void testIsolatedClassLoaderWithEnumsPkgBuilder() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );

            // create a builder with the given classloader
            KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( null,
                                                                                                           loader1 );
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder( conf );
            builder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_EnumSerialization.drl" ) ),
                         ResourceType.DRL );
            if ( builder.hasErrors() ) {
                fail( builder.getErrors().toString() );
            }
            Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();
            KnowledgePackage pkg = pkgs.iterator().next();

            // serialize out
            byte[] out = DroolsStreamUtils.streamOut( ((KnowledgePackageImp) pkg).pkg );

            // adding original packages to a kbase just to make sure they are fine
            KieBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null,
                                                                                                       loader1 );
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
            kbase.addKnowledgePackages( pkgs );

            // now, create another classloader and make sure it has access to the classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );

            // create another builder
            KnowledgeBuilderConfiguration conf2 = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( null,
                                                                                                            loader2 );
            KnowledgeBuilder builder2 = KnowledgeBuilderFactory.newKnowledgeBuilder( conf2 );
            builder2.add( ResourceFactory.newByteArrayResource( out ),
                          ResourceType.PKG );
            Collection<KnowledgePackage> pkgs2 = builder2.getKnowledgePackages();

            // create another kbase
            KieBaseConfiguration kbaseConf2 = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null,
                                                                                                        loader2 );
            KnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf2 );
            kbase2.addKnowledgePackages( pkgs2 );

        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }

    }

    @Test
    public void testIsolatedClassLoaderWithEnumsContextClassloader() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            //loader1.loadClass( "org.drools.compiler.Primitives" );
            //loader1.loadClass( "org.drools.compiler.TestEnum" );

            // create a builder with the given classloader
            KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( null,
                                                                                                           loader1 );
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder( conf );
            builder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_EnumSerialization.drl" ) ),
                         ResourceType.DRL );
            Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();
            KnowledgePackage pkg = pkgs.iterator().next();

            // serialize out
            byte[] out = DroolsStreamUtils.streamOut( pkg );

            // adding original packages to a kbase just to make sure they are fine
            KieBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null,
                                                                                                       loader1 );
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
            kbase.addKnowledgePackages( pkgs );

            // now, create another classloader and make sure it has access to the classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            //loader2.loadClass( "org.drools.compiler.Primitives" );
            //loader2.loadClass( "org.drools.compiler.TestEnum" );

            // set context classloader and use it
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader( loader2 );
            KnowledgePackage pkg2 = (KnowledgePackage) DroolsStreamUtils.streamIn( out );
            Collection<KnowledgePackage> pkgs2 = Collections.singleton( pkg2 );
            Thread.currentThread().setContextClassLoader( ccl );

            // create another kbase
            KieBaseConfiguration kbaseConf2 = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null,
                    loader2);
            KnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf2 );
            kbase2.addKnowledgePackages( pkgs2 );

        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }

    }

    @Test
    public void testDynamicRuleRemovalsSubNetworkAndNot() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_DynamicRulesWithNotSubnetwork.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

        final AgendaEventListener alistener = mock( AgendaEventListener.class );
        ksession.addEventListener( alistener );

        // pattern does not match, so do not activate
        ksession.insert( new Person( "toni" ) );
        verify( alistener,
                never() ).matchCreated(any(org.kie.api.event.rule.MatchCreatedEvent.class));

        // pattern matches, so create activation
        ksession.insert( new Person( "bob" ) );
        verify( alistener,
                times( 1 ) ).matchCreated(any(org.kie.api.event.rule.MatchCreatedEvent.class));

        // already active, so no new activation should be created
        ksession.insert( new Person( "mark" ) );
        verify( alistener,
                times( 1 ) ).matchCreated(any(org.kie.api.event.rule.MatchCreatedEvent.class));

        kbase.removeKnowledgePackage( "org.drools.compiler" );

        assertEquals( 0,
                      kbase.getKnowledgePackages().size() );

        // lets re-compile and add it again
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_DynamicRulesWithNotSubnetwork.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // rule should be reactivated, since data is still in the session
        verify( alistener,
                times( 2 ) ).matchCreated(any(org.kie.api.event.rule.MatchCreatedEvent.class));

    }

    @Test
    public void testSharedLIANodeRemoval() throws IOException,
            DroolsParserException {
        String str = "global java.util.List list;\n";
        str += "rule \"test\"\n";
        str += "when\n";
        str += "  exists(eval(true))\n";
        str += "then\n";
        str += " list.add(\"fired\");\n";
        str += "end\n";

        PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackageFromDrl( new StringReader( str ) );
        assertTrue( "Should not have errors",
                    pkgBuilder.getErrors().isEmpty() );

        // Add once ...
        ReteooRuleBase rb = new ReteooRuleBase( "dummy" );
        rb.addPackage( pkgBuilder.getPackage() );

        // This one works
        List list = new ArrayList();
        StatefulSession session = rb.newStatefulSession();
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );

        list.clear();
        // ... remove ...
        rb.removePackage( pkgBuilder.getPackage().getName() );
        rb.addPackage( pkgBuilder.getPackage() );
        session = rb.newStatefulSession();
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testDynamicRulesWithTypeDeclarations() {
        String type = "package com.sample\n" +
                      "declare type Foo\n" +
                      "  id : int\n" +
                      "end\n";

        String r1 = "package com.sample\n" +
                    "rule R1 when\n" +
                    "  not Foo()\n" +
                    "then\n" +
                    "  insert( new Foo(1) );\n" +
                    "end\n";

        String r2 = "package com.sample\n" +
                "rule R2 when\n" +
                "  $f : Foo()\n" +
                "then\n" +
                "  $f.setId( 2 );\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( type.getBytes() ), ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );
        
        ksession.fireAllRules();
        verify( ael, never() ).afterMatchFired( any( AfterMatchFiredEvent.class ) );
        
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbase );
        kbuilder.add( ResourceFactory.newByteArrayResource( r1.getBytes() ), ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );
        
        ksession.fireAllRules();
        ArgumentCaptor<AfterMatchFiredEvent> capt = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael, times(1) ).afterMatchFired( capt.capture() );
        assertThat( "R1", is( capt.getValue().getMatch().getRule().getName() ) );
        
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbase );
        kbuilder.add( ResourceFactory.newByteArrayResource( r2.getBytes() ), ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );
        
        ksession.fireAllRules();
        verify( ael, times(2) ).afterMatchFired( capt.capture() );
        assertThat( "R2", is( capt.getAllValues().get( 2 ).getMatch().getRule().getName() ) );
        
        ksession.dispose();
        
    }

    @Test
    public void testJBRULES_2206() {
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        ((RuleBaseConfiguration) config).setRuleBaseUpdateHandler( null );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( config );
        StatefulKnowledgeSession session = createKnowledgeSession( kbase );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        session.addEventListener( ael );

        for ( int i = 0; i < 5; i++ ) {
            session.insert( new Cheese() );
        }

        addDrlToKBase( kbase, "test_JBRULES_2206_1.drl" );

        // two matching rules were added, so 2 activations should have been created 
        verify( ael, times( 2 ) ).matchCreated(any(MatchCreatedEvent.class));
        int fireCount = session.fireAllRules();
        // both should have fired
        assertEquals( 2, fireCount );

        addDrlToKBase( kbase, "test_JBRULES_2206_2.drl" );

        // one rule was overridden and should activate 
        verify( ael, times( 3 ) ).matchCreated(any(MatchCreatedEvent.class));
        fireCount = session.fireAllRules();
        // that rule should fire again
        assertEquals( 1, fireCount );

        session.dispose();
    }

    private void addDrlToKBase(KnowledgeBase kbase,
                               String drlName) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource(drlName,
                DynamicRulesTest.class),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
    }

    public class SubvertedClassLoader extends URLClassLoader {

        private static final long serialVersionUID = 510l;

        public SubvertedClassLoader(final URL[] urls,
                                    final ClassLoader parentClassLoader) {
            super( urls,
                   parentClassLoader );
        }

        protected synchronized Class loadClass(String name,
                                               boolean resolve) throws ClassNotFoundException {
            // First, check if the class has already been loaded
            Class c = findLoadedClass( name );
            if ( c == null ) {
                try {
                    c = findClass( name );
                } catch ( ClassNotFoundException e ) {
                    c = super.loadClass( name,
                                         resolve );
                }
            }
            return c;
        }
    }
}
