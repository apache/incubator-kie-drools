/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.FactA;
import org.drools.mvel.compiler.FactB;
import org.drools.mvel.compiler.Order;
import org.drools.mvel.compiler.OrderItem;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.PersonInterface;
import org.drools.mvel.compiler.Precondition;
import org.drools.mvel.compiler.StockTick;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.drools.core.util.DroolsStreamUtils;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.KiePackage;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DynamicRulesTest extends CommonTestMethodBase {

    @Test(timeout=10000)
    public void testDynamicRuleAdditions() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) SerializationHelper.serializeObject( loadKnowledgeBase( "test_Dynamic1.drl" ) );
        KieSession workingMemory = createKnowledgeSession(kbase);
        workingMemory.setGlobal("total",
                                new Integer(0));

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
        workingMemory.insert(cheddar);
        workingMemory.fireAllRules();

        assertEquals(1,
                     list.size());

        assertEquals("stilton",
                     list.get(0));

        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_Dynamic2.drl" ) );
        kbase.addPackages(kpkgs);

        workingMemory.fireAllRules();
        assertEquals(5,
                     list.size());

        assertEquals("stilton",
                     list.get(0));

        assertTrue("cheddar".equals(list.get(1)) || "cheddar".equals(list.get(2)));

        assertTrue("stilton".equals(list.get(1)) || "stilton".equals(list.get(2)));

        list.clear();

        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_Dynamic3.drl" ) );
        kbase.addPackages(kpkgs);

        // Package 3 has a rule working on Person instances.
        // As we added person instance in advance, rule should fire now
        workingMemory.fireAllRules();

        assertEquals("Rule from package 3 should have been fired",
                     "match Person ok",
                     bob.getStatus());

        assertEquals(1,
                     list.size());

        assertEquals(bob,
                     list.get(0));

        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_Dynamic4.drl" ) );
        kbase.addPackages(kpkgs);
        workingMemory.fireAllRules();
        kbase = SerializationHelper.serializeObject( kbase );

        assertEquals("Rule from package 4 should have been fired",
                     "Who likes Stilton ok",
                     bob.getStatus());

        assertEquals( 2,
                      list.size() );

        assertEquals(bob,
                     list.get(1));

    }

    @Test(timeout=10000)
    public void testDynamicRuleRemovals() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) SerializationHelper.serializeObject( loadKnowledgeBase( "test_Dynamic1.drl", "test_Dynamic3.drl", "test_Dynamic4.drl" ) );

        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject(loadKnowledgePackages("test_Dynamic2.drl"));
        kbase.addPackages(kpkgs);

        KieSession wm = createKnowledgeSession( kbase );
//        AgendaEventListener ael = mock( AgendaEventListener.class );
//        wm.addEventListener( ael );

        final List< ? > list = new ArrayList<Object>();
        wm.setGlobal( "list",
                                 list );

        final PersonInterface bob = new Person( "bob",
                                                "stilton" );
        bob.setStatus( "Not evaluated" );
        FactHandle fh0 = wm.insert( bob );

        final Cheese stilton1 = new Cheese( "stilton",
                                            5 );
        FactHandle fh1 = wm.insert( stilton1 );

        final Cheese stilton2 = new Cheese( "stilton",
                                            3 );
        FactHandle fh2 = wm.insert( stilton2 );

        final Cheese stilton3 = new Cheese( "stilton",
                                            1 );
        FactHandle fh3 = wm.insert( stilton3 );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        FactHandle fh4 = wm.insert( cheddar );

        wm.fireAllRules();
        assertEquals(15, list.size());
        list.clear();

        kbase.removeRule("org.drools.mvel.compiler.test",
                         "Who likes Stilton");

        wm.update(fh0, bob);
        wm.update(fh1, stilton1);
        wm.update(fh2, stilton2);
        wm.update(fh3, stilton3);
        wm.update(fh4, cheddar);
        wm.fireAllRules();
        assertEquals(12, list.size());
        list.clear();

        kbase.removeRule("org.drools.mvel.compiler.test",
                         "like cheese");

        wm.update( fh0, bob);
        wm.update(fh1, stilton1);
        wm.update(fh2, stilton2);
        wm.update(fh3, stilton3);
        wm.update(fh4, cheddar);
        wm.fireAllRules();
        assertEquals(8, list.size());
        list.clear();

        final Cheese muzzarela = new Cheese( "muzzarela",
                                             5 );
        wm.insert( muzzarela );
        wm.fireAllRules();

        assertEquals( 1, list.size() );
        list.clear();
    }

    @Test(timeout=10000)
    public void testDynamicRuleRemovalsUnusedWorkingMemory() throws Exception {

        InternalKnowledgeBase kbase = (InternalKnowledgeBase) SerializationHelper.serializeObject( loadKnowledgeBase( "test_Dynamic1.drl", 
                                                                                      "test_Dynamic2.drl", 
                                                                                      "test_Dynamic3.drl", 
                                                                                      "test_Dynamic4.drl" ) );

        KieSession workingMemory = createKnowledgeSession( kbase );

        assertEquals( 2,
                      kbase.getKiePackages().size() );

        KiePackage knowledgePackage = null;
        for (KiePackage pkg : kbase.getKiePackages()) {
            if ( pkg.getName().equals( "org.drools.mvel.compiler.test" ) ) {
                knowledgePackage = pkg;
                break;
            }
        }

        assertEquals( 5,
                      knowledgePackage.getRules().size() );

        kbase.removeRule( "org.drools.mvel.compiler.test",
                          "Who likes Stilton" );
        assertEquals( 4,
                      knowledgePackage.getRules().size() );

        kbase.removeRule( "org.drools.mvel.compiler.test",
                          "like cheese" );
        assertEquals( 3,
                      knowledgePackage.getRules().size() );

        kbase.removeKiePackage( "org.drools.mvel.compiler.test" );
        assertEquals( 1,
                      kbase.getKiePackages().size() );
    }

    @Test(timeout=10000)
    public void testDynamicFunction() throws Exception {
        //JBRULES-1258 serialising a package breaks function removal -- left the serialisation commented out for now
        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_DynamicFunction1.drl" ) );
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase( );

        kbase.addPackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );
        
        final KieSession workingMemory = createKnowledgeSession( kbase );

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
        kbase.removeFunction( "org.drools.mvel.compiler.test",
                              "addFive" );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.insert( cheddar );

        try {
            workingMemory.fireAllRules();
            fail( "Function should have been removed and NoClassDefFoundError thrown from the Consequence" );
        } catch ( final Throwable e ) {
        }

        // Check a new function can be added to replace an old function
        Collection<KiePackage> kpkgs2 = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicFunction2.drl" ) );
        kbase.addPackages( kpkgs2 );

        final Cheese brie = new Cheese( "brie",
                                        5 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 6 ),
                      list.get( 1 ) );

        Collection<KiePackage> kpkgs3 = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicFunction3.drl" ) );
        kbase.addPackages( kpkgs3 );

        final Cheese feta = new Cheese( "feta",
                                        5 );
        workingMemory.insert( feta );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 2 ) );
    }

    @Test (timeout=10000)
    public void testRemovePackage() throws Exception {
        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject(loadKnowledgePackages("test_RemovePackage.drl"));
        final String packageName = kpkgs.iterator().next().getName();
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase( );

        kbase.addPackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        KieSession session = createKnowledgeSession( kbase );

        session.insert( new Precondition( "genericcode",
                                          "genericvalue" ) );
        session.fireAllRules();

        InternalKnowledgeBase ruleBaseWM = (InternalKnowledgeBase) session.getKieBase();
        ruleBaseWM.removeKiePackage( packageName );
        
        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_RemovePackage.drl" ) );
        ruleBaseWM.addPackages( kpkgs );
        ruleBaseWM = SerializationHelper.serializeObject( ruleBaseWM );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, 
                                                                             true );
        session.fireAllRules();

        ruleBaseWM.removeKiePackage(packageName);
        ruleBaseWM.addPackages( SerializationHelper.serializeObject( kpkgs ) );

        ruleBaseWM.removeKiePackage( packageName );
        ruleBaseWM.addPackages(SerializationHelper.serializeObject(kpkgs));
    }

    @Test(timeout=10000)
    public void testDynamicRules() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase();
        KieSession session = createKnowledgeSession(kbase);
        final Cheese a = new Cheese( "stilton",
                                     10 );
        final Cheese b = new Cheese( "stilton",
                                     15 );
        final Cheese c = new Cheese( "stilton",
                                     20 );
        session.insert( a );
        session.insert( b );
        session.insert(c);

        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_DynamicRules.drl" ) );
        kbase.addPackages(kpkgs);
        kbase = SerializationHelper.serializeObject( kbase );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, 
                                                                             true );

        session.fireAllRules();
    }

    @Test(timeout=10000)
    public void testDynamicRules2() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase();
        KieSession session = createKnowledgeSession(kbase);

        // Assert some simple facts
        final FactA a = new FactA( "hello",
                                   new Integer( 1 ),
                                   new Float( 3.14 ) );
        final FactB b = new FactB( "hello",
                                   new Integer( 2 ),
                                   new Float( 6.28 ) );
        session.insert( a );
        session.insert( b );

        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_DynamicRules2.drl" ) );
        kbase.addPackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, 
                                                                             true );

        session.fireAllRules();
    }

    @Test(timeout=10000)
    public void testRuleBaseAddRemove() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase( );

        //add and remove
        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_Dynamic1.drl" ) );
        String pkgName = kpkgs.iterator().next().getName();
        kbase.addPackages( kpkgs );
        kbase.removeKiePackage( pkgName );
        kbase = SerializationHelper.serializeObject( kbase );

        //add and remove again
        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages(  "test_Dynamic1.drl" ) );
        pkgName = kpkgs.iterator().next().getName();
        kbase.addPackages( kpkgs );
        kbase.removeKiePackage( pkgName );
        kbase = SerializationHelper.serializeObject( kbase );
    }

    @Test(timeout=10000)
    public void testClassLoaderSwitchsUsingConf() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Class cheeseClass = loader1.loadClass( "org.drools.mvel.compiler.Cheese" );

            KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader1);
            KieBase kbase = loadKnowledgeBase( kbuilderConf, "test_Dynamic1.drl"  );
            KieSession wm = createKnowledgeSession(kbase);

            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            // Creates second class loader and use it to load fact classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            cheeseClass = loader2.loadClass( "org.drools.mvel.compiler.Cheese" );

            kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader2);
            kbase = loadKnowledgeBase(kbuilderConf, "test_Dynamic1.drl");
            wm = createKnowledgeSession(kbase);
            wm.insert(cheeseClass.newInstance());
            wm.fireAllRules();
        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }

    }

    @Test(timeout=10000)
    public void testClassLoaderSwitchsUsingContext() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Thread.currentThread().setContextClassLoader( loader1 );
            Class cheeseClass = loader1.loadClass("org.drools.mvel.compiler.Cheese");

            KieBase kbase = loadKnowledgeBase( "test_Dynamic1.drl" );
            KieSession wm = createKnowledgeSession( kbase );

            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            // Creates second class loader and use it to load fact classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Thread.currentThread().setContextClassLoader( loader2 );
            cheeseClass = loader2.loadClass( "org.drools.mvel.compiler.Cheese" );

            kbase = loadKnowledgeBase("test_Dynamic1.drl");
            wm = createKnowledgeSession(kbase);

            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            Thread.currentThread().setContextClassLoader(original);
        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }
    }

    @Test(timeout=10000)
    public void testCollectDynamicRules() throws Exception {
        checkCollectWithDynamicRules( "test_CollectDynamicRules1.drl" );
    }

    @Test(timeout=10000)
    public void testCollectDynamicRulesWithExistingOTN() throws Exception {
        checkCollectWithDynamicRules( "test_CollectDynamicRules1a.drl" );
    }

    private void checkCollectWithDynamicRules(String originalDrl) throws java.io.IOException, ClassNotFoundException {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase( originalDrl );
        KieSession session = createKnowledgeSession( kbase );

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

        kbase.addPackages( loadKnowledgePackages( "test_CollectDynamicRules2.drl" ) );
        session.fireAllRules();

        kbase = SerializationHelper.serializeObject(kbase);

        // fire all rules is automatic
        assertEquals( 1,
                      list.size() );
        assertEquals( 2,
                      ((List<?>) list.get( 0 )).size() );
    }

    @Test(timeout=10000)
    public void testDynamicNotNode() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase( "test_CollectDynamicRules1.drl" );
        kbase = SerializationHelper.serializeObject( kbase );
        Environment env = EnvironmentFactory.newEnvironment();
        env.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
                 new IdentityPlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT )} );
        KieSession ksession = kbase.newKieSession( null, env );
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

        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicNotNode.drl" ) );
        kbase.addPackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              false );

        results = (List) ksession.getGlobal( "results" );

        ksession.fireAllRules();

        assertEquals( 0,
                      results.size() );

        kbase.removeKiePackage( "org.drools.mvel.compiler" );

        ksession.retract( ksession.getFactHandle( b ) );

        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicNotNode.drl" ) );
        kbase.addPackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              false );

        results = (List<?>) ksession.getGlobal( "results" );
        ksession.fireAllRules();

        assertEquals( 1,
                      results.size() );
    }

    @Test(timeout=10000)
    public void testDynamicRulesAddRemove() {
        try {
            InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase( "test_DynamicRulesTom.drl" );
            KieSession session = createKnowledgeSession( kbase );

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

            kbase.addPackages( loadKnowledgePackages( "test_DynamicRulesFred.drl" ) );
            session.fireAllRules();

            assertEquals( 2,
                          results.size() );
            assertTrue( results.contains( h2.getObject() ) );
            assertTrue( results.contains( h4.getObject() ) );
            results.clear();

            kbase.removeKiePackage( "tom" );

            kbase.addPackages( loadKnowledgePackages( "test_DynamicRulesEd.drl" ) );
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

    @Test(timeout=10000)
    public void testDynamicRuleRemovalsSubNetwork() throws Exception {
        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicRulesWithSubnetwork1.drl",
                                                                                                         "test_DynamicRulesWithSubnetwork.drl" ) );
        
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase();
        kbase.addPackages( kpkgs );
        
        kpkgs = SerializationHelper.serializeObject( loadKnowledgePackages( "test_DynamicRulesWithSubnetwork2.drl" ) );
        kbase.addPackages( kpkgs );

        KieSession session = createKnowledgeSession( kbase );

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
        FactHandle item1Fh = session.insert( item1 );

        OrderItem item2 = new OrderItem( order,
                                         2,
                                         "Prehistoric Britain",
                                         OrderItem.TYPE_BOOK,
                                         15 );
        order.addItem( item2 );
        FactHandle item2Fh = session.insert( item2 );

        OrderItem item3 = new OrderItem( order,
                                         3,
                                         "Holiday Music",
                                         OrderItem.TYPE_CD,
                                         9 );
        order.addItem( item3 );
        FactHandle item3Fh = session.insert( item3 );

        OrderItem item4 = new OrderItem( order,
                                         4,
                                         "Very Best of Mick Jagger",
                                         OrderItem.TYPE_CD,
                                         11 );
        order.addItem( item4 );
        FactHandle item4Fh = session.insert( item4 );

        session.insert( order );
        session.fireAllRules();
        assertEquals( 11, list.size() );

        kbase.removeRule( "org.drools.mvel.compiler",
                          "Apply Discount on all books" );

        list.clear();
        session.update( item1Fh, item1 );
        session.update( item2Fh, item2 );
        session.update( item3Fh, item3 );
        session.update( item4Fh, item4 );
        session.fireAllRules();

        assertEquals( 10, list.size() );

        kbase.removeRule( "org.drools.mvel.compiler",
                          "like book" );
        list.clear();
        session.update( item1Fh, item1 );
        session.update( item2Fh, item2 );
        session.update( item3Fh, item3 );
        session.update( item4Fh, item4 );
        session.fireAllRules();

        assertEquals( 8, list.size() );

        final OrderItem item5 = new OrderItem( order, 5, "Sinatra : Vegas", OrderItem.TYPE_CD, 5 );
        FactHandle item5Fh = session.insert( item5 );

        session.fireAllRules();

        assertEquals( 10, list.size() );

        kbase.removeKiePackage( "org.drools.mvel.compiler" );
        list.clear();
        session.update( item1Fh, item1 );
        session.update( item2Fh, item2 );
        session.update( item3Fh, item3 );
        session.update( item4Fh, item4 );
        session.update( item5Fh, item5 );
        session.fireAllRules();

        assertEquals( 0, list.size() );
    }

    @Test(timeout=10000)
    public void testDynamicRuleRemovalsUnusedWorkingMemorySubNetwork() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase( "test_DynamicRulesWithSubnetwork1.drl",
                                                                                 "test_DynamicRulesWithSubnetwork2.drl",
                                                                                 "test_DynamicRulesWithSubnetwork.drl" );

        assertEquals( 2, kbase.getKiePackages().size() );
        assertEquals( 4,
                      kbase.getPackagesMap().get("org.drools.mvel.compiler").getRules().size() );

        kbase.removeRule( "org.drools.mvel.compiler", "Apply Discount on all books" );
        assertEquals( 3,
                      kbase.getPackagesMap().get("org.drools.mvel.compiler").getRules().size() );

        kbase.removeRule( "org.drools.mvel.compiler", "like book" );
        assertEquals( 2,
                      kbase.getPackagesMap().get("org.drools.mvel.compiler").getRules().size() );

        kbase.removeKiePackage( "org.drools.mvel.compiler" );
        assertEquals( 1,
                      kbase.getKiePackages().size() );
    }

    @Test(timeout=10000)
    public void testRemovePackageSubNetwork() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_DynamicRulesWithSubnetwork.drl" );
        String packageName = kbase.getKiePackages().iterator().next().getName();
        
        KieSession workingMemory = createKnowledgeSession( kbase );

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

        InternalKnowledgeBase ruleBaseWM = (InternalKnowledgeBase) workingMemory.getKieBase();
        ruleBaseWM.removeKiePackage( packageName );
        
        Collection<KiePackage> kpkgs = loadKnowledgePackages( "test_DynamicRulesWithSubnetwork.drl" );
        ruleBaseWM.addPackages( SerializationHelper.serializeObject( kpkgs ) );
        
        workingMemory.fireAllRules();
        results = (List) workingMemory.getGlobal( "results" );
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        ruleBaseWM.removeKiePackage( packageName );
        ruleBaseWM.addPackages( SerializationHelper.serializeObject( kpkgs ) );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        ruleBaseWM.removeKiePackage( packageName );
        ruleBaseWM.addPackages( SerializationHelper.serializeObject( kpkgs ) );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();
    }

    @Test(timeout=10000)
    public void testRuleBaseAddRemoveSubNetworks() throws Exception {
        try {
            //add and remove
            InternalKnowledgeBase kbase = (InternalKnowledgeBase) getKnowledgeBase();
            Collection<KiePackage> kpkgs = loadKnowledgePackages("test_DynamicRulesWithSubnetwork.drl");
            KiePackage kpkg = (KiePackage) kpkgs.toArray()[0];
            kbase.addPackages(kpkgs);
            kbase.removeKiePackage(kpkg.getName());

            //add and remove again
            kpkgs = loadKnowledgePackages("test_DynamicRulesWithSubnetwork.drl");
            kpkg = ( KiePackage ) kpkgs.toArray()[0];
            kbase.addPackages(kpkgs);
            kbase.removeKiePackage(kpkg.getName());
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception" );
        }
    }

    @Test (timeout=10000)
    public void testDynamicRuleAdditionsWithEntryPoints() throws Exception {
        Collection<KiePackage> kpkgs = loadKnowledgePackages("test_DynamicWithEntryPoint.drl" );
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) getKnowledgeBase();

        KieSession ksession = createKnowledgeSession( kbase );

        // now lets add some knowledge to the kbase
        kbase.addPackages( kpkgs );

        List<StockTick> results = new ArrayList<StockTick>();
        ksession.setGlobal( "results",
                            results );

        EntryPoint ep = ksession.getEntryPoint( "in-channel" );
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

    @Test(timeout=10000)
    public void testIsolatedClassLoaderWithEnumsPkgBuilder() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            loader1.loadClass( "org.drools.Primitives" );
            loader1.loadClass( "org.drools.TestEnum" );

            // create a builder with the given classloader
            KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader1);
            Collection<KiePackage> kpkgs = loadKnowledgePackages(conf, "test_EnumSerialization.drl");

            // serialize out
            byte[] out = DroolsStreamUtils.streamOut( kpkgs );

            // adding original packages to a kbase just to make sure they are fine
            KieBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null, loader1 );
            InternalKnowledgeBase kbase = (InternalKnowledgeBase) getKnowledgeBase(kbaseConf);
            kbase.addPackages( kpkgs );
            KieSession ksession = createKnowledgeSession( kbase );
            List list = new ArrayList();
            ksession.setGlobal( "list", list);
            assertEquals( 1, ksession.fireAllRules() );
            assertEquals( 1, list.size() );

            // now, create another classloader and make sure it has access to the classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            loader2.loadClass( "org.drools.Primitives" );
            loader2.loadClass( "org.drools.TestEnum" );

            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            Collection<KiePackage> kpkgs2 = null;
            try {
                Thread.currentThread().setContextClassLoader( loader2 );
                kpkgs2  = (Collection<KiePackage>) DroolsStreamUtils.streamIn( out );
            } finally {
                Thread.currentThread().setContextClassLoader( ccl );
            }

            // create another kbase
            KieBaseConfiguration kbaseConf2 = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null,
                                                                                                  loader2 );
            InternalKnowledgeBase kbase2 = (InternalKnowledgeBase) getKnowledgeBase(kbaseConf2);
            kbase2.addPackages( kpkgs2 );
            ksession = createKnowledgeSession( kbase2 );
            list = new ArrayList();
            ksession.setGlobal( "list", list);
            assertEquals( 1, ksession.fireAllRules() );
            assertEquals( 1, list.size() );

        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }
    }

    @Test(timeout=10000)
    public void testIsolatedClassLoaderWithEnumsContextClassloader() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            loader1.loadClass( "org.drools.Primitives" );
            loader1.loadClass( "org.drools.TestEnum" );

            byte[] out = null;

            // Build it using the current context
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            Collection<KiePackage> kpkgs2 = null;
            try {
                Thread.currentThread().setContextClassLoader( loader1 );
                // create a builder with the given classloader
                Collection<KiePackage> kpkgs = loadKnowledgePackages( "test_EnumSerialization.drl");

                // serialize out
                out = DroolsStreamUtils.streamOut( kpkgs );

                // adding original packages to a kbase just to make sure they are fine
                InternalKnowledgeBase kbase = (InternalKnowledgeBase) getKnowledgeBase();
                kbase.addPackages( kpkgs );

                KieSession ksession = createKnowledgeSession( kbase );
                List list = new ArrayList();
                ksession.setGlobal( "list", list);
                assertEquals( 1, ksession.fireAllRules() );
                assertEquals( 1, list.size() );
            } finally {
                Thread.currentThread().setContextClassLoader( ccl );
            }

            // now, create another classloader and make sure it has access to the classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            loader2.loadClass( "org.drools.Primitives" );
            loader2.loadClass( "org.drools.TestEnum" );

            // set context classloader and use it
            ccl = Thread.currentThread().getContextClassLoader();
            kpkgs2 = null;
            try {
                Thread.currentThread().setContextClassLoader( loader2 );
                kpkgs2  = (Collection<KiePackage>) DroolsStreamUtils.streamIn( out );

                // create another kbase
                InternalKnowledgeBase kbase2 = (InternalKnowledgeBase) getKnowledgeBase();
                kbase2.addPackages( kpkgs2 );

                KieSession ksession = createKnowledgeSession( kbase2 );
                List list = new ArrayList();
                ksession.setGlobal( "list", list);
                assertEquals( 1, ksession.fireAllRules() );
                assertEquals( 1, list.size() );
            } finally {
                Thread.currentThread().setContextClassLoader( ccl );
            }

        } catch ( ClassCastException cce ) {
            cce.printStackTrace();
            fail( "No ClassCastException should be raised." );
        }
    }

    @Test(timeout=10000)
    public void testDynamicRuleRemovalsSubNetworkAndNot() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase("test_DynamicRulesWithNotSubnetwork.drl");
        KieSession ksession = createKnowledgeSession( kbase );


        final AgendaEventListener alistener = mock( AgendaEventListener.class );
        ksession.addEventListener( alistener );

        // pattern does not match, so do not activate
        ksession.insert( new Person( "toni" ) );
        ksession.fireAllRules();
        verify( alistener,
                never() ).matchCreated(any(org.kie.api.event.rule.MatchCreatedEvent.class));

        // pattern matches, so create activation
        ksession.insert( new Person( "bob" ) );
        ksession.fireAllRules();
        verify( alistener,
                times( 1 ) ).matchCreated(any(org.kie.api.event.rule.MatchCreatedEvent.class));

        // already active, so no new activation should be created
        ksession.insert( new Person( "mark" ) );
        ksession.fireAllRules();
        verify( alistener,
                times( 1 ) ).matchCreated(any(org.kie.api.event.rule.MatchCreatedEvent.class));

        kbase.removeKiePackage( "org.drools.mvel.compiler" );

        assertEquals( 0,
                      kbase.getKiePackages().size() );

        // lets re-compile and add it again
        Collection<KiePackage> kpkgs = loadKnowledgePackages("test_DynamicRulesWithNotSubnetwork.drl");
        kbase.addPackages( kpkgs );
        ksession.fireAllRules();

                // rule should be reactivated, since data is still in the session
        verify( alistener,
                times( 2 ) ).matchCreated(any(org.kie.api.event.rule.MatchCreatedEvent.class));

    }

    @Test//(timeout=10000)
    public void testSharedLIANodeRemoval() throws Exception {
        // it's not a true share, but the liaNode will have two sinks, due to subnetwork.
        String str = "global java.util.List list;\n";
        str += "rule \"test\"\n";
        str += "when\n";
        str += "  exists(eval(true))\n";
        str += "then\n";
        str += " list.add(\"fired\");\n";
        str += "end\n";

        InternalKnowledgeBase kbase = (InternalKnowledgeBase) getKnowledgeBase( );
        Collection<KiePackage> kpkgs = SerializationHelper.serializeObject( loadKnowledgePackagesFromString( str ) );

        // Add once ...
        kbase.addPackages( kpkgs );

        // This one works
        List list = new ArrayList();
        KieSession session = createKnowledgeSession( kbase );
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );

        list.clear();
        // ... remove ...
        KiePackage kpkg = ( KiePackage ) kpkgs.toArray()[0];
        kbase.removeKiePackage( kpkg.getName() );
        kbase.addPackages( kpkgs );
        session = createKnowledgeSession( kbase );
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );
    }

    @Test(timeout=10000)
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

        InternalKnowledgeBase kbase = (InternalKnowledgeBase) getKnowledgeBase( );
        kbase.addPackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();

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

    @Test(timeout=10000)
    public void testJBRULES_2206() {
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        ((RuleBaseConfiguration) config).setRuleBaseUpdateHandler( null );
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) getKnowledgeBase( config );
        KieSession session = createKnowledgeSession( kbase );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        session.addEventListener( ael );

        for ( int i = 0; i < 5; i++ ) {
            session.insert( new Cheese() );
        }

        kbase.addPackages( loadKnowledgePackages( "test_JBRULES_2206_1.drl" ));
        ((InternalAgenda) session.getAgenda()).evaluateEagerList();

        // two matching rules were added, so 2 activations should have been created 
        verify( ael, times( 2 ) ).matchCreated(any(MatchCreatedEvent.class));
        int fireCount = session.fireAllRules();
        // both should have fired
        assertEquals( 2, fireCount );

        kbase.addPackages( loadKnowledgePackages( "test_JBRULES_2206_2.drl" ));
        ((InternalAgenda) session.getAgenda()).evaluateEagerList();

        // one rule was overridden and should activate 
        verify( ael, times( 3 ) ).matchCreated(any(MatchCreatedEvent.class));
        fireCount = session.fireAllRules();
        // that rule should fire again
        assertEquals( 1, fireCount );

        session.dispose();
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

    @Test
    public void testSegmentMerging() {
        String drl1 = "global java.util.List list\n" +
                      "rule R1 when\n" +
                      "  $s : String()\n" +
                      "  $i : Integer( this == $s.length() )\n" +
                      "  $j : Integer( this == $i * 2 )\n" +
                      "then\n" +
                      "  list.add( $j );\n" +
                      "end\n";

        String drl2 = "global java.util.List list\n" +
                      "rule R2 when\n" +
                      "  $s : String()\n" +
                      "  $i : Integer( this == $s.length() )\n" +
                      "  $j : Integer( this == $i * 3 )\n" +
                      "then\n" +
                      "  list.add( $j );\n" +
                      "end\n";

        InternalKnowledgeBase kbase = (InternalKnowledgeBase)getKnowledgeBase( );

        kbase.addPackages(loadKnowledgePackagesFromString( drl1 ));

        KieSession ksession = createKnowledgeSession(kbase);
        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert("test");
        ksession.insert(4);
        ksession.insert(8);
        ksession.insert(12);

        ksession.fireAllRules();
        assertEquals(8, (int)list.get(0));
        list.clear();

        kbase.addPackages(loadKnowledgePackagesFromString(drl2));

        kbase.removeRule("defaultpkg", "R1");

        ksession.fireAllRules();
        assertEquals(12, (int)list.get(0));
    }
}