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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.FactA;
import org.drools.mvel.compiler.FactB;
import org.drools.mvel.compiler.Order;
import org.drools.mvel.compiler.OrderItem;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.PersonInterface;
import org.drools.mvel.compiler.Precondition;
import org.drools.mvel.compiler.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieModule;
import org.kie.api.definition.KiePackage;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class DynamicRulesTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DynamicRulesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test(timeout=10000)
    public void testDynamicRuleAdditions() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResources("test", getClass(), kieBaseTestConfiguration, "test_Dynamic1.drl");
        KieSession workingMemory = kbase.newKieSession();
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

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_Dynamic2.drl").getKiePackages();
        kbase.addPackages(kpkgs);

        workingMemory.fireAllRules();
        assertEquals(5,
                     list.size());

        assertEquals("stilton",
                     list.get(0));

        assertTrue("cheddar".equals(list.get(1)) || "cheddar".equals(list.get(2)));

        assertTrue("stilton".equals(list.get(1)) || "stilton".equals(list.get(2)));

        list.clear();

        kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_Dynamic3.drl").getKiePackages();
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

        kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_Dynamic4.drl").getKiePackages();
        kbase.addPackages(kpkgs);
        workingMemory.fireAllRules();

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
        InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResources("test", getClass(), kieBaseTestConfiguration, "test_Dynamic1.drl", "test_Dynamic3.drl", "test_Dynamic4.drl");

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_Dynamic2.drl").getKiePackages();
        kbase.addPackages(kpkgs);

        KieSession wm = kbase.newKieSession();
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
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromClasspathResources("test", getClass(), kieBaseTestConfiguration, "test_Dynamic1.drl",
                                                                                                           "test_Dynamic2.drl",
                                                                                                           "test_Dynamic3.drl",
                                                                                                           "test_Dynamic4.drl");

        KieSession workingMemory = kbase.newKieSession();

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

    @Ignore("Fails with standard-drl after changing to new API. See DROOLS-6060")
    @Test(timeout=10000)
    public void testDynamicFunction() throws Exception {
        //JBRULES-1258 serialising a package breaks function removal -- left the serialisation commented out for now

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicFunction1.drl").getKiePackages();

        InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResources("test", getClass(), kieBaseTestConfiguration);

        kbase.addPackages( kpkgs );
//        kbase = SerializationHelper.serializeObject( kbase );

        KieSession workingMemory = kbase.newKieSession();

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
        Collection<KiePackage> kpkgs2 = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicFunction2.drl").getKiePackages();
        kbase.addPackages( kpkgs2 );

        final Cheese brie = new Cheese( "brie",
                                        5 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 6 ),
                      list.get( 1 ) );

        Collection<KiePackage> kpkgs3 = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicFunction3.drl").getKiePackages();

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
        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_RemovePackage.drl").getKiePackages();
        final String packageName = kpkgs.iterator().next().getName();
        InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResources("test", getClass(), kieBaseTestConfiguration);

        kbase.addPackages( kpkgs );

        KieSession session = kbase.newKieSession();

        session.insert( new Precondition( "genericcode",
                                          "genericvalue" ) );
        session.fireAllRules();

        InternalKnowledgeBase ruleBaseWM = (InternalKnowledgeBase) session.getKieBase();
        ruleBaseWM.removeKiePackage( packageName );

        kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_RemovePackage.drl").getKiePackages();

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
        InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResources("test", getClass(), kieBaseTestConfiguration);
        KieSession session = kbase.newKieSession();

        final Cheese a = new Cheese( "stilton",
                                     10 );
        final Cheese b = new Cheese( "stilton",
                                     15 );
        final Cheese c = new Cheese( "stilton",
                                     20 );
        session.insert( a );
        session.insert( b );
        session.insert(c);

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRules.drl").getKiePackages();
        kbase.addPackages(kpkgs);

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, 
                                                                             true );

        session.fireAllRules();
    }

    @Test(timeout=10000)
    public void testDynamicRules2() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResources("test", getClass(), kieBaseTestConfiguration);
        KieSession session = kbase.newKieSession();

        // Assert some simple facts
        final FactA a = new FactA( "hello",
                                   new Integer( 1 ),
                                   new Float( 3.14 ) );
        final FactB b = new FactB( "hello",
                                   new Integer( 2 ),
                                   new Float( 6.28 ) );
        session.insert( a );
        session.insert( b );

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRules2.drl").getKiePackages();
        kbase.addPackages( kpkgs );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, 
                                                                             true );

        session.fireAllRules();
    }

    @Test(timeout=10000)
    public void testRuleBaseAddRemove() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResources("test", getClass(), kieBaseTestConfiguration);

        //add and remove
        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_Dynamic1.drl").getKiePackages();
        String pkgName = kpkgs.iterator().next().getName();
        kbase.addPackages( kpkgs );
        kbase.removeKiePackage( pkgName );

        //add and remove again
        kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_Dynamic1.drl").getKiePackages();
        pkgName = kpkgs.iterator().next().getName();
        kbase.addPackages( kpkgs );
        kbase.removeKiePackage( pkgName );
    }

    @Test(timeout=10000)
    public void testClassLoaderSwitchsUsingConf() throws Exception {
        try {
            // Creates first class loader and use it to load fact classes
            ClassLoader loader1 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Class cheeseClass = loader1.loadClass( "org.drools.mvel.compiler.Cheese" );

            InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResourcesWithClassLoaderForKieBuilder("test", getClass(), loader1, kieBaseTestConfiguration, "test_Dynamic1.drl");
            KieSession wm = kbase.newKieSession();

            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            // Creates second class loader and use it to load fact classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            cheeseClass = loader2.loadClass( "org.drools.mvel.compiler.Cheese" );

            kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResourcesWithClassLoaderForKieBuilder("test", getClass(), loader2, kieBaseTestConfiguration, "test_Dynamic1.drl");
            wm = kbase.newKieSession();
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

            KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Dynamic1.drl");
            KieSession wm = kbase.newKieSession();

            wm.insert( cheeseClass.newInstance() );
            wm.fireAllRules();

            // Creates second class loader and use it to load fact classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/" )},
                                                            this.getClass().getClassLoader() );
            Thread.currentThread().setContextClassLoader( loader2 );
            cheeseClass = loader2.loadClass( "org.drools.mvel.compiler.Cheese" );

            kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Dynamic1.drl");
            wm = kbase.newKieSession();

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
        InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, originalDrl);
        KieSession session = kbase.newKieSession();

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

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_CollectDynamicRules2.drl").getKiePackages();
        kbase.addPackages(kpkgs);

        session.fireAllRules();

        // fire all rules is automatic
        assertEquals( 1,
                      list.size() );
        assertEquals( 2,
                      ((List<?>) list.get( 0 )).size() );
    }

    @Test(timeout=10000)
    public void testDynamicNotNode() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_CollectDynamicRules1.drl");
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

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicNotNode.drl").getKiePackages();
        kbase.addPackages( kpkgs );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              false );

        results = (List) ksession.getGlobal( "results" );

        ksession.fireAllRules();

        assertEquals( 0,
                      results.size() );

        kbase.removeKiePackage( "org.drools.mvel.compiler" );

        ksession.retract( ksession.getFactHandle( b ) );

        kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicNotNode.drl").getKiePackages();

        kbase.addPackages( kpkgs );

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
            InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_DynamicRulesTom.drl");
            KieSession session = kbase.newKieSession();

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

            Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRulesFred.drl").getKiePackages();
            kbase.addPackages(kpkgs);
            session.fireAllRules();

            assertEquals( 2,
                          results.size() );
            assertTrue( results.contains( h2.getObject() ) );
            assertTrue( results.contains( h4.getObject() ) );
            results.clear();

            kbase.removeKiePackage( "tom" );

            kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRulesEd.drl").getKiePackages();
            kbase.addPackages(kpkgs);
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
        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRulesWithSubnetwork1.drl",
                                                                                    "test_DynamicRulesWithSubnetwork.drl").getKiePackages();
        
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
        kbase.addPackages( kpkgs );
        
        kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRulesWithSubnetwork2.drl").getKiePackages();
        kbase.addPackages( kpkgs );

        KieSession session = kbase.newKieSession();

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
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration,
                                                                                                           "test_DynamicRulesWithSubnetwork1.drl",
                                                                                                           "test_DynamicRulesWithSubnetwork2.drl",
                                                                                                           "test_DynamicRulesWithSubnetwork.drl");

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
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_DynamicRulesWithSubnetwork.drl");
        String packageName = kbase.getKiePackages().iterator().next().getName();

        KieSession workingMemory = kbase.newKieSession();

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

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRulesWithSubnetwork.drl").getKiePackages();
        ruleBaseWM.addPackages(kpkgs);
        
        workingMemory.fireAllRules();
        results = (List) workingMemory.getGlobal( "results" );
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        ruleBaseWM.removeKiePackage( packageName );
        ruleBaseWM.addPackages(kpkgs);
        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ((List) results.get( 0 )).size() );
        results.clear();

        ruleBaseWM.removeKiePackage( packageName );
        ruleBaseWM.addPackages(kpkgs);
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
            InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
            Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRulesWithSubnetwork.drl").getKiePackages();

            KiePackage kpkg = (KiePackage) kpkgs.toArray()[0];
            kbase.addPackages(kpkgs);
            kbase.removeKiePackage(kpkg.getName());

            //add and remove again
            kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRulesWithSubnetwork.drl").getKiePackages();
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
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicWithEntryPoint.drl").getKiePackages();

        KieSession ksession = kbase.newKieSession();

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
            Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResourcesWithClassLoaderForKieBuilder("test", getClass(), loader1, kieBaseTestConfiguration, "test_EnumSerialization.drl").getKiePackages();

            // adding original packages to a kbase just to make sure they are fine
            KieBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null, loader1 );
            final KieModule kieModule = KieUtil.getKieModuleFromResourcesWithClassLoaderForKieBuilder("test", loader1, kieBaseTestConfiguration);
            InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.newKieBaseFromReleaseId(kieModule.getReleaseId(), kbaseConf);

            kbase.addPackages( kpkgs );
            KieSession ksession = kbase.newKieSession();
            List list = new ArrayList();
            ksession.setGlobal( "list", list);
            assertEquals( 1, ksession.fireAllRules() );
            assertEquals( 1, list.size() );

            // now, create another classloader and make sure it has access to the classes
            ClassLoader loader2 = new SubvertedClassLoader( new URL[]{getClass().getResource( "/testEnum.jar" )},
                                                            this.getClass().getClassLoader() );
            loader2.loadClass( "org.drools.Primitives" );
            loader2.loadClass( "org.drools.TestEnum" );

            // create another kbase
            KieBaseConfiguration kbaseConf2 = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( null,
                                                                                                  loader2 );
            final KieModule kieModule2 = KieUtil.getKieModuleFromResourcesWithClassLoaderForKieBuilder("test2", loader2, kieBaseTestConfiguration);
            InternalKnowledgeBase kbase2 = (InternalKnowledgeBase) KieBaseUtil.newKieBaseFromReleaseId(kieModule2.getReleaseId(), kbaseConf2);

            kbase2.addPackages( kpkgs );
            ksession = kbase2.newKieSession();
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

            // Build it using the current context
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            Collection<KiePackage> kpkgs = null;
            try {
                Thread.currentThread().setContextClassLoader( loader1 );
                // create a builder with the given classloader
                kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_EnumSerialization.drl").getKiePackages();

                // adding original packages to a kbase just to make sure they are fine
                InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
                kbase.addPackages( kpkgs );

                KieSession ksession = kbase.newKieSession();
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
            try {
                Thread.currentThread().setContextClassLoader( loader2 );
                
                // Note: This test originally serialize/deserialize kpkgs with different context classloaders.
                // Now exec-model doesn't support package serialization so we may remove this test.

                // create another kbase
                InternalKnowledgeBase kbase2 = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test2", kieBaseTestConfiguration);
                kbase2.addPackages( kpkgs );

                KieSession ksession = kbase2.newKieSession();
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
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_DynamicRulesWithNotSubnetwork.drl");
        KieSession ksession = kbase.newKieSession();


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
        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_DynamicRulesWithNotSubnetwork.drl").getKiePackages();
        kbase.addPackages( kpkgs );
        ksession.fireAllRules();

                // rule should be reactivated, since data is still in the session
        verify( alistener,
                times( 2 ) ).matchCreated(any(org.kie.api.event.rule.MatchCreatedEvent.class));

    }

    @Test(timeout=10000)
    public void testSharedLIANodeRemoval() throws Exception {
        // it's not a true share, but the liaNode will have two sinks, due to subnetwork.
        String str = "global java.util.List list;\n";
        str += "rule \"test\"\n";
        str += "when\n";
        str += "  exists(eval(true))\n";
        str += "then\n";
        str += " list.add(\"fired\");\n";
        str += "end\n";

        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromKieModuleFromDrl("tmp", kieBaseTestConfiguration, str).getKiePackages();

        // Add once ...
        kbase.addPackages( kpkgs );

        // This one works
        List list = new ArrayList();
        KieSession session = kbase.newKieSession();
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
        session = kbase.newKieSession();
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );
    }

    @Test(timeout=10000)
    public void testDynamicRulesWithTypeDeclarations() {
        
        // Note: This test originally use "kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbase );" which is not possible with KieBuilder
        // Probably this new test is not valid for exec-model and we can remove this test
        
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

        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromKieModuleFromDrl("tmp", kieBaseTestConfiguration, type).getKiePackages();
        kbase.addPackages(kpkgs);
        
        KieSession ksession = kbase.newKieSession();

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );
        
        ksession.fireAllRules();
        verify( ael, never() ).afterMatchFired( any( AfterMatchFiredEvent.class ) );

        kpkgs = KieBaseUtil.getKieBaseFromKieModuleFromDrl("tmp", kieBaseTestConfiguration, type, r1).getKiePackages();
        kbase.addPackages(kpkgs);
        
        ksession.fireAllRules();
        ArgumentCaptor<AfterMatchFiredEvent> capt = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael, times(1) ).afterMatchFired( capt.capture() );
        assertThat( "R1", is( capt.getValue().getMatch().getRule().getName() ) );

        kpkgs = KieBaseUtil.getKieBaseFromKieModuleFromDrl("tmp", kieBaseTestConfiguration, type, r2).getKiePackages();
        kbase.addPackages(kpkgs);
        
        ksession.fireAllRules();
        verify( ael, times(2) ).afterMatchFired( capt.capture() );
        assertThat( "R2", is( capt.getAllValues().get( 2 ).getMatch().getRule().getName() ) );
        
        ksession.dispose();
        
    }

    @Test(timeout=10000)
    public void testJBRULES_2206() {
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        ((RuleBaseConfiguration) config).setRuleBaseUpdateHandler( null );

        final KieModule kieModule = KieUtil.getKieModuleFromResources(KieUtil.generateReleaseId("test"), kieBaseTestConfiguration);
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.newKieBaseFromReleaseId(kieModule.getReleaseId(), config);
        KieSession session = kbase.newKieSession();

        AgendaEventListener ael = mock( AgendaEventListener.class );
        session.addEventListener( ael );

        for ( int i = 0; i < 5; i++ ) {
            session.insert( new Cheese() );
        }

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_JBRULES_2206_1.drl").getKiePackages();
        kbase.addPackages(kpkgs);

        ((InternalAgenda) session.getAgenda()).evaluateEagerList();

        // two matching rules were added, so 2 activations should have been created 
        verify( ael, times( 2 ) ).matchCreated(any(MatchCreatedEvent.class));
        int fireCount = session.fireAllRules();
        // both should have fired
        assertEquals( 2, fireCount );

        kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_JBRULES_2206_2.drl").getKiePackages();
        kbase.addPackages(kpkgs);
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

        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromKieModuleFromDrl("tmp", kieBaseTestConfiguration, drl1).getKiePackages();
        kbase.addPackages(kpkgs);

        KieSession ksession = kbase.newKieSession();
        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert("test");
        ksession.insert(4);
        ksession.insert(8);
        ksession.insert(12);

        ksession.fireAllRules();
        assertEquals(8, (int)list.get(0));
        list.clear();

        kpkgs = KieBaseUtil.getKieBaseFromKieModuleFromDrl("tmp", kieBaseTestConfiguration, drl2).getKiePackages();
        kbase.addPackages(kpkgs);

        kbase.removeRule("defaultpkg", "R1");

        ksession.fireAllRules();
        assertEquals(12, (int)list.get(0));
    }
}
