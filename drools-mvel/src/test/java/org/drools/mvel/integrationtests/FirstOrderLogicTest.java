/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.core.audit.WorkingMemoryConsoleLogger;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Cheesery;
import org.drools.mvel.compiler.FactA;
import org.drools.mvel.compiler.FactB;
import org.drools.mvel.compiler.FactC;
import org.drools.mvel.compiler.Message;
import org.drools.mvel.compiler.Order;
import org.drools.mvel.compiler.OrderItem;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.PersonInterface;
import org.drools.mvel.compiler.SpecialString;
import org.drools.mvel.compiler.State;
import org.drools.mvel.compiler.StockTick;
import org.drools.mvel.compiler.Triangle;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.RemoveIdentitiesOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionClock;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FirstOrderLogicTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(FirstOrderLogicTest.class);

    protected KieSession createKnowledgeSession(KieBase kbase) { 
        return kbase.newKieSession();
    }
    
    @Test
    public void testCollect() throws Exception {
        List results = new ArrayList();

        KieBase kbase = loadKnowledgeBase("test_Collect.drl");
        KieSession wm = createKnowledgeSession(kbase);

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "stilton",
                               7 ) );
        wm.insert( new Cheese( "stilton",
                               8 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );
        wm = SerializationHelper.getSerialisedStatefulKnowledgeSession(wm, true);
        results = (List) wm.getGlobal( "results" );

        wm.insert( new Cheese( "provolone",
                               20 ) );
        wm.insert( new Person( "Bob",
                               "stilton" ) );
        wm.insert( new Person( "Mark",
                               "provolone" ) );
        wm = SerializationHelper.getSerialisedStatefulKnowledgeSession(wm, true);
        results = (List) wm.getGlobal( "results" );

        wm.fireAllRules();

        wm = SerializationHelper.getSerialisedStatefulKnowledgeSession(wm, true);
        results = (List) wm.getGlobal( "results" );

        assertEquals( 1,
                             results.size() );
        assertEquals( 3,
                             ((Collection) results.get( 0 )).size() );
        assertEquals( ArrayList.class.getName(),
                             results.get( 0 ).getClass().getName() );
    }

    @Test
    public void testCollectNodeSharing() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_collectNodeSharing.drl");
        KieSession wm = createKnowledgeSession(kbase);

        List results = new ArrayList();
        wm.setGlobal( "results",
                                 results );

        wm = SerializationHelper.getSerialisedStatefulKnowledgeSession(wm, true);
        results = (List) wm.getGlobal( "results" );

        wm.insert( new Cheese( "stilton",
                                          10 ) );
        wm = SerializationHelper.getSerialisedStatefulKnowledgeSession(wm, true);
        results = (List) wm.getGlobal( "results" );

        wm.insert( new Cheese( "brie",
                                          15 ) );

        wm.fireAllRules();

        wm = SerializationHelper.getSerialisedStatefulKnowledgeSession(wm, true);
        results = (List) wm.getGlobal( "results" );

        assertEquals( 1,
                      results.size() );

        assertEquals( 2,
                      ((List) results.get( 0 )).size() );
    }

    @Test
    public void testCollectModify() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_Collect.drl");
        KieSession wm = createKnowledgeSession(kbase);

        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = (FactHandle) wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = (FactHandle) wm.insert( bob );

        // ---------------- 1st scenario 
        int fireCount = 0;
        wm.fireAllRules();
        assertEquals( ++fireCount,
                             results.size() );
        assertEquals( 3,
                             ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 2nd scenario 
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );

        wm.fireAllRules();

        assertEquals( ++fireCount,
                             results.size() );
        assertEquals( 3,
                             ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 3rd scenario 
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        assertEquals( fireCount,
                             results.size() );

        // ---------------- 4th scenario 
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint 
        assertEquals( fireCount,
                             results.size() );
    }

    @Test
    public void testCollectResultConstraints() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_CollectResultConstraints.drl");
        KieSession wm = createKnowledgeSession(kbase);
        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm = SerializationHelper.getSerialisedStatefulKnowledgeSession(wm, true);
        results = (List) wm.getGlobal( "results" );

        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( 1,
                      ((Collection) results.get( 0 )).size() );

        wm.insert( new Cheese( "stilton",
                               7 ) );
        wm.insert( new Cheese( "stilton",
                               8 ) );
        wm.fireAllRules();

        wm = SerializationHelper.getSerialisedStatefulKnowledgeSession(wm, true);
        results = (List) wm.getGlobal( "results" );

        assertEquals( 1,
                      results.size() );
        // It's 3 as while the rule does not fire, it does continue to evaluate and update the collection
        assertEquals( 3,
                      ((Collection) results.get( 0 )).size() );
        assertEquals( ArrayList.class.getName(),
                      results.get( 0 ).getClass().getName() );
    }

    @Test
    public void testExistsWithBinding() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_ExistsWithBindings.drl");
        KieSession wm = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        wm.setGlobal( "results",
                                 list );

        final Cheese c = new Cheese( "stilton",
                                     10 );
        final Person p = new Person( "Mark",
                                     "stilton" );
        wm.insert( c );
        wm.insert( p );
        wm.fireAllRules();

        assertTrue( list.contains( c.getType() ) );
        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testNot() throws Exception {
        KieBase kbase = loadKnowledgeBase("not_rule_test.drl");
        KieSession wm = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        wm.setGlobal( "list", list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        final FactHandle stiltonHandle = (FactHandle) wm.insert( stilton );
        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final FactHandle cheddarHandle = (FactHandle) wm.insert( cheddar );
        wm.fireAllRules();

        assertEquals( 0,
                      list.size() );

        wm.retract( stiltonHandle );

        wm.fireAllRules();

        assertEquals( 4,
                      list.size() );
        assertTrue( list.contains( new Integer( 5 ) ) );
        assertTrue( list.contains( new Integer( 6 ) ) );
        assertTrue( list.contains( new Integer( 7 ) ) );
        assertTrue( list.contains( new Integer( 8 ) ) );
    }

    @Test
    public void testNotWithBindings() throws Exception {
        KieBase kbase = loadKnowledgeBase("not_with_bindings_rule_test.drl");
        KieSession wm = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        wm.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        final FactHandle stiltonHandle = (FactHandle) wm.insert( stilton );
        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final FactHandle cheddarHandle = (FactHandle) wm.insert( cheddar );

        final PersonInterface paul = new Person( "paul",
                                                 "stilton",
                                                 12 );
        wm.insert( paul );
        wm.fireAllRules();

        assertEquals( 0,
                      list.size() );

        wm.retract( stiltonHandle );

        wm.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testExists() throws Exception {
        KieBase kbase = loadKnowledgeBase("exists_rule_test.drl");
        KieSession wm = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        wm.setGlobal( "list", list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final FactHandle cheddarHandle = (FactHandle) wm.insert( cheddar );
        wm.fireAllRules();

        assertEquals( 0,
                      list.size() );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        final FactHandle stiltonHandle = (FactHandle) wm.insert( stilton );
        wm.fireAllRules();

        assertEquals( 1,
                      list.size() );

        final Cheese brie = new Cheese( "brie",
                                        5 );
        final FactHandle brieHandle = (FactHandle) wm.insert( brie );
        wm.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testExists2() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_exists.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final Cheese provolone = new Cheese( "provolone",
                                             5 );
        final Person edson = new Person( "Edson",
                                         "cheddar" );
        final Person bob = new Person( "Bob",
                                       "muzzarela" );

        workingMemory.insert( cheddar );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        workingMemory.insert( provolone );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        workingMemory.insert( edson );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );

        workingMemory.insert( bob );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testExists3() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_Exists_JBRULES_2810.drl");
        KieSession ksession = createKnowledgeSession(kbase);

        WorkingMemoryConsoleLogger logger = new WorkingMemoryConsoleLogger( ksession );
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testForall() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_Forall.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

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
    public void testForall2() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_Forall2.drl");
        KieSession ksession = createKnowledgeSession(kbase);

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal( "results",
                            list );

        final State state = new State( "SP" );
        ksession.insert( state );

        final Person bob = new Person( "Bob" );
        bob.setStatus( state.getState() );
        bob.setAlive( true );
        ksession.insert( bob );

        ksession.fireAllRules();

        assertEquals( 0,
                      list.size() );

        final State qc = new State( "QC" );
        ksession.insert( qc );
        final Person john = new Person( "John" );
        john.setStatus( qc.getState() );
        john.setAlive( false );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testRemoveIdentitiesSubNetwork() throws Exception {
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption(RemoveIdentitiesOption.YES);

        KieBase kbase = loadKnowledgeBase(conf, "test_removeIdentitiesSubNetwork.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Person bob = new Person( "bob",
                                       "stilton" );
        workingMemory.insert( bob );

        final Person mark = new Person( "mark",
                                        "stilton" );
        workingMemory.insert( mark );

        final Cheese stilton1 = new Cheese( "stilton",
                                            6 );
        final FactHandle stilton1Handle = (FactHandle) workingMemory.insert( stilton1 );
        final Cheese stilton2 = new Cheese( "stilton",
                                            7 );
        final FactHandle stilton2Handle =  (FactHandle) workingMemory.insert( stilton2 );

        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        workingMemory.retract( stilton1Handle );

        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( mark,
                      list.get( 0 ) );

        workingMemory.retract( stilton2Handle );

        workingMemory.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertEquals( bob,
                      list.get( 1 ) );
    }

    @Test
    public void testCollectWithNestedFromWithParams() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_CollectWithNestedFrom.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        final Person bob = new Person( "bob",
                                       "stilton" );

        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( new Cheese( "stilton",
                                        10 ) );
        cheesery.addCheese( new Cheese( "brie",
                                        20 ) );
        cheesery.addCheese( new Cheese( "muzzarela",
                                        8 ) );
        cheesery.addCheese( new Cheese( "stilton",
                                        5 ) );
        cheesery.addCheese( new Cheese( "provolone",
                                        1 ) );

        workingMemory.insert( bob );
        workingMemory.insert( cheesery );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      results.size() );
        List cheeses = (List) results.get( 0 );
        assertEquals( 2,
                      cheeses.size() );
        assertEquals( bob.getLikes(),
                      ((Cheese) cheeses.get( 0 )).getType() );
        assertEquals( bob.getLikes(),
                      ((Cheese) cheeses.get( 1 )).getType() );

    }

    @Test
    public void testCollectModifyAlphaRestriction() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_CollectAlphaRestriction.drl");
        KieSession wm = createKnowledgeSession(kbase);

        final List results = new ArrayList();

        wm.setGlobal( "results", results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton", 10 ), 
                                             new Cheese( "stilton", 2 ), 
                                             new Cheese( "stilton", 5 ), 
                                             new Cheese( "brie", 15 ), 
                                             new Cheese( "brie", 16 ), 
                                             new Cheese( "provolone", 8 )};

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] =  (FactHandle) wm.insert( cheese[i] );
        }

        // ---------------- 1st scenario 
        int fireCount = 0;
        wm.fireAllRules();
        assertEquals( ++fireCount,  results.size() );
        assertEquals( 3, ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(),  results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 2nd scenario 
        final int index = 1;
        cheese[index].setType( "brie" );
        wm.update( cheeseHandles[index], cheese[index] );
        wm.fireAllRules();

        assertEquals( ++fireCount, results.size() );
        assertEquals( 2, ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(), results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 3rd scenario 
        wm.retract( cheeseHandles[2] );
        wm.fireAllRules();

        assertEquals( ++fireCount,  results.size() );
        assertEquals( 1,  ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(), results.get( fireCount - 1 ).getClass().getName() );

    }

    @Test
    public void testForallSinglePattern() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_ForallSinglePattern.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );
        int fired = 0;

        // no cheeses, so should fire 
        workingMemory.fireAllRules();
        assertEquals( ++fired,
                      list.size() );

        // only stilton, so should not fire again 
        FactHandle stilton1 = (FactHandle) workingMemory.insert( new Cheese( "stilton",
                                                                10 ) );
        workingMemory.fireAllRules();
        assertEquals( fired,
                      list.size() );

        // only stilton, so should not fire again 
        FactHandle stilton2 = (FactHandle) workingMemory.insert( new Cheese( "stilton",
                                                                11 ) );
        workingMemory.fireAllRules();
        assertEquals( fired,
                      list.size() );

        // still only stilton, so should not fire  
        workingMemory.retract( stilton1 );
        workingMemory.fireAllRules();
        assertEquals( fired,
                      list.size() );

        // there is a brie, so should not fire  
        FactHandle brie = (FactHandle) workingMemory.insert( new Cheese( "brie",
                                                            10 ) );
        workingMemory.fireAllRules();
        assertEquals( fired,
                      list.size() );

        // no brie anymore, so should fire  
        workingMemory.retract( brie );
        workingMemory.fireAllRules();
        assertEquals( ++fired,
                      list.size() );

        // no more cheese, but since it already fired, should not fire again 
        workingMemory.retract( stilton2 );
        workingMemory.fireAllRules();
        assertEquals( fired,  
                      list.size() );

    }

    @Test
    public void testForallSinglePattern2() throws Exception {
        final KieBase kbase = loadKnowledgeBase( "test_ForallSinglePattern2.drl" );
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert( new Triangle( 3,
                                       3,
                                       3 ) );
        ksession.insert( new Triangle( 3,
                                       3,
                                       3 ) );

        // no cheeses, so should fire 
        int fired = ksession.fireAllRules();
        assertEquals( 1,
                      fired );

        ksession.dispose();
    }

    @Test
    public void testMVELCollect() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_MVELCollect.drl");
        KieSession wm = createKnowledgeSession(kbase);

        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "stilton",
                               7 ) );
        wm.insert( new Cheese( "stilton",
                               8 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );
        wm.insert( new Cheese( "provolone",
                               20 ) );
        wm.insert( new Person( "Bob",
                               "stilton" ) );
        wm.insert( new Person( "Mark",
                               "provolone" ) );

        wm.fireAllRules();

        assertEquals( 1,
                             results.size() );
        assertEquals( 6,
                             ((List) results.get( 0 )).size() );
    }

    @Test
    public void testNestedCorelatedRulesWithForall() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_NestedCorrelatedRulesWithForall.drl");
        KieSession session = createKnowledgeSession(kbase);

        List list1 = new ArrayList();
        List list2 = new ArrayList();
        List list3 = new ArrayList();
        List list4 = new ArrayList();

        session.setGlobal( "list1",
                           list1 );
        session.setGlobal( "list2",
                           list2 );
        session.setGlobal( "list3",
                           list3 );
        session.setGlobal( "list4",
                           list4 );

        SpecialString first42 = new SpecialString( "42" );
        SpecialString second42 = new SpecialString( "42" );
        SpecialString world = new SpecialString( "World" );

        //System.out.println( "Inserting ..." );

        session.insert( world );
        session.insert( first42 );
        session.insert( second42 );

        //System.out.println( "Done." );

        //System.out.println( "Firing rules ..." );

        // check all lists are empty 
        assertTrue( list1.isEmpty() );
        assertTrue( list2.isEmpty() );
        assertTrue( list3.isEmpty() );
        assertTrue( list4.isEmpty() );

        session.fireAllRules();

        //System.out.println( "Done." );

        // check first list is populated correctly 
        assertEquals( 0,
                      list1.size() );

        // check second list is populated correctly         
        assertEquals( 0,
                      list2.size() );

        // check third list is populated correctly         
        assertEquals( 1,
                      list3.size() );

        // check fourth list is populated correctly         
        assertEquals( 0,
                      list4.size() );
    }

    @Test
    public void testFromInsideNotAndExists() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_FromInsideNotAndExists.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final Cheese provolone = new Cheese( "provolone",
                                             5 );
        final Cheesery cheesery = new Cheesery();

        cheesery.addCheese( cheddar );
        cheesery.addCheese( provolone );

        FactHandle handle = (FactHandle) workingMemory.insert( cheesery );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        cheesery.addCheese( new Cheese( "stilton",
                                        10 ) );
        cheesery.removeCheese( cheddar );
        workingMemory.update( handle,
                              cheesery );
        workingMemory.fireAllRules();
        assertEquals( 2,
                      list.size() );

    }

    @Test
    public void testOr() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_OrNesting.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final Cheese provolone = new Cheese( "provolone",
                                             5 );
        final Cheese brie = new Cheese( "brie",
                                        15 );
        final Person mark = new Person( "mark",
                                        "stilton" );

        FactHandle ch = (FactHandle) workingMemory.insert( cheddar );
        FactHandle ph = (FactHandle) workingMemory.insert( provolone );
        FactHandle bh = (FactHandle) workingMemory.insert( brie );
        FactHandle markh = (FactHandle) workingMemory.insert( mark );

        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );
    }

    // JBRULES-2482 
    @Test
    public void testOrWithVariableResolution() throws Exception {
//        KieBase kbase = loadKnowledgeBase( "test_OrCEFollowedByMultipleEval.drl");
//        KieSession workingMemory = createKnowledgeSession(kbase);

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_OrCEFollowedByMultipleEval.drl",
                                                            FirstOrderLogicTest.class ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );

        final KieSession ksession = createKnowledgeSession(kbase);

        final AgendaEventListener al = mock( AgendaEventListener.class );
        ksession.addEventListener( al );

        ksession.insert( new FactA( "a" ) );
        ksession.insert( new FactB( "b" ) );
        ksession.insert( new FactC( "c" ) );

        ksession.fireAllRules();
        verify( al,
                times( 6 ) ).afterMatchFired(any(AfterMatchFiredEvent.class));
    }

    // JBRULES-2526 
    @Test
    public void testOrWithVariableResolution2() throws Exception {
        //        KieBase kbase = loadKnowledgeBase( "test_OrCEFollowedByMultipleEval.drl");
        //        KieSession workingMemory = createKnowledgeSession(kbase);

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_OrCEFollowedByMultipleEval2.drl",
                                                            FirstOrderLogicTest.class ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );

        final KieSession ksession = createKnowledgeSession(kbase);

        final AgendaEventListener al = mock( AgendaEventListener.class );
        ksession.addEventListener( al );

        ksession.insert( new FactA( "a" ) );
        ksession.insert( new FactB( "b" ) );
        ksession.insert( new FactC( "c" ) );

        ksession.fireAllRules();
        verify( al,
                times( 8 ) ).afterMatchFired(any(AfterMatchFiredEvent.class));
    }

    @Test
    public void testCollectWithMemberOfOperators() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_CollectMemberOfOperator.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Order order1 = new Order( 1,
                                        "bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );
        final Order order2 = new Order( 2,
                                        "mark" );
        final OrderItem item21 = new OrderItem( order2,
                                                1 );
        final OrderItem item22 = new OrderItem( order2,
                                                2 );

        workingMemory.insert( order1 );
        workingMemory.insert( item11 );
        workingMemory.insert( item12 );
        workingMemory.insert( order2 );
        workingMemory.insert( item21 );
        workingMemory.insert( item22 );

        workingMemory.fireAllRules();

        int index = 0;
        assertEquals( 8,
                      list.size() );
        assertSame( order1,
                    list.get( index++ ) );
        assertSame( item11,
                    list.get( index++ ) );
        assertSame( order2,
                    list.get( index++ ) );
        assertSame( item21,
                    list.get( index++ ) );
        assertSame( order1,
                    list.get( index++ ) );
        assertSame( item11,
                    list.get( index++ ) );
        assertSame( order2,
                    list.get( index++ ) );
        assertSame( item21,
                    list.get( index++ ) );

    }

    @Test
    public void testCollectWithContainsOperators() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_CollectContainsOperator.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Order order1 = new Order( 1,
                                        "bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );
        final Order order2 = new Order( 2,
                                        "mark" );
        final OrderItem item21 = new OrderItem( order2,
                                                1 );
        final OrderItem item22 = new OrderItem( order2,
                                                2 );

        workingMemory.insert( order1 );
        workingMemory.insert( item11 );
        workingMemory.insert( item12 );
        workingMemory.insert( order2 );
        workingMemory.insert( item21 );
        workingMemory.insert( item22 );

        workingMemory.fireAllRules();

        int index = 0;
        assertEquals( 8,
                      list.size() );
        assertSame( order1,
                    list.get( index++ ) );
        assertSame( item11,
                    list.get( index++ ) );
        assertSame( order2,
                    list.get( index++ ) );
        assertSame( item21,
                    list.get( index++ ) );
        assertSame( order1,
                    list.get( index++ ) );
        assertSame( item11,
                    list.get( index++ ) );
        assertSame( order2,
                    list.get( index++ ) );
        assertSame( item21,
                    list.get( index++ ) );

    }

    @Test
    public void testForallSinglePatternWithExists() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_ForallSinglePatternWithExists.drl");
        KieSession workingMemory = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.insert( new Cheese( "stilton",
                                          10 ) );
        workingMemory.insert( new Cheese( "brie",
                                          10 ) );
        workingMemory.insert( new Cheese( "brie",
                                          10 ) );
        workingMemory.insert( new Order( 1,
                                         "bob" ) );
        workingMemory.insert( new Person( "bob",
                                          "stilton",
                                          10 ) );
        workingMemory.insert( new Person( "mark",
                                          "stilton" ) );

        workingMemory.fireAllRules();

        assertEquals( 1, list.size() );

    }

    @Test
    public void testCollectResultBetaConstraint() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_CollectResultsBetaConstraint.drl");
        KieSession wm = createKnowledgeSession(kbase);

        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Double( 10 ) );
        wm.insert( new Integer( 2 ) );

        wm.fireAllRules();

        assertEquals( 0,
                             results.size() );

        wm.insert( new Double( 15 ) );
        wm.fireAllRules();

        assertEquals( 2,
                             results.size() );

        assertEquals( "collect",
                             results.get( 0 ) );
        assertEquals( "accumulate",
                             results.get( 1 ) );
    }

    @Test
    public void testFromWithOr() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_FromWithOr.drl");
        KieSession session = createKnowledgeSession(kbase);

        final List<Address> results = new ArrayList<Address>();
        session.setGlobal( "results",
                           results );

        Address a1 = new Address();
        a1.setZipCode( "12345" );
        Address a2 = new Address();
        a2.setZipCode( "54321" );
        Address a3 = new Address();
        a3.setZipCode( "99999" );

        Person p = new Person();
        p.addAddress( a1 );
        p.addAddress( a2 );
        p.addAddress( a3 );

        session.insert( p );
        session.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( a1 ) );
        assertTrue( results.contains( a2 ) );

    }

    @Test
    public void testForallWithSlidingWindow() throws Exception {
        final KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieBase kbase = loadKnowledgeBase( "test_ForallSlidingWindow.drl");
        KieSession ksession = createKnowledgeSession(kbase, conf);

        final SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );

        // advance time... no events, so forall should fire 
        clock.advanceTime( 60,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );

        int seq = 1;
        // advance time... there are matching events now, but forall still not fire 
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 60 
        clock.advanceTime( 5,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 65 
        clock.advanceTime( 5,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );

        // advance time... there are non-matching events now, so forall de-activates 
        ksession.insert( new StockTick( seq++,
                                        "IBM",
                                        10,
                                        clock.getCurrentTime() ) ); // 70 
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );

        // advance time... there are non-matching events now, so forall is still deactivated 
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 80 
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );

        // advance time... non-matching event expires now, so forall should fire 
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 90 
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );

        // advance time... forall still matches and should not fire 
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 100 
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2,  
                      results.size() );

        // advance time... forall still matches and should not fire 
        clock.advanceTime( 60,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2,  
                      results.size() );

    }

    @Test
    public void testCollectFromMVELAfterOr() throws Exception {
        KieBase kbase = loadKnowledgeBase( "test_CollectFromMVELAfterOr.drl");
        KieSession wm = createKnowledgeSession(kbase);

        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        Person jill = new Person( "jill" );

        Person bob = new Person( "bob" );
        List addresses = new ArrayList();
        addresses.add( new Address( "a" ) );
        addresses.add( new Address( "b" ) );
        addresses.add( new Address( "c" ) );
        bob.setAddresses( addresses );

        wm.insert( jill );
        wm.insert( bob );

        wm = SerializationHelper.getSerialisedStatefulKnowledgeSession(wm, true);
        results = (List) wm.getGlobal( "results" );

        wm.fireAllRules();

        assertEquals( 2,
                             results.size() );
        assertEquals( 3,
                             ((Collection) results.get( 0 )).size() );
    }

    @Test
    public void testCollectAfterOrCE() throws Exception {
        Collection<KiePackage> pkgs = loadKnowledgePackages("test_OrCEFollowedByCollect.drl");

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(pkgs);
        KieSession session = createKnowledgeSession(kbase);

        //Set up facts
        final Cheesery bonFromage = new Cheesery();
        bonFromage.addCheese( new Cheese( "cheddar" ) );
        bonFromage.addCheese( new Cheese( "cheddar" ) );

        session.insert( bonFromage );

        int rules = session.fireAllRules();
        assertEquals( 2,
                      rules );

        //Serialize and test again
        pkgs = SerializationHelper.serializeObject(pkgs);
        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( pkgs );
        
        session = createKnowledgeSession(kbase);
        session.insert( bonFromage );

        rules = session.fireAllRules();
        assertEquals( 2,
                      rules );

    }
    
    @Test 
    public void testLotsOfOrs() throws Exception {
        // Decomposed this test down to just two rules, while still exhibiting the problem
        // Uncomment rest of rule as those are fixed, to complicate it again.
        String str = "package org.drools.mvel.compiler.test\n" +
                "\n" + 
                "import " + FirstOrderLogicTest.class.getCanonicalName() + ".Field;\n" + 
                " \n" + 
                "rule \"test\"\n" + 
                "    when\n" + 
                "        (\n" + 
                "            ( \n" + 
                "                a : Field( name == \"a\") and\n" + 
                "                eval( !a.getValue().equals(\"a\") ) and\n" + 
                "                b : Field( name == \"b\" ) and\n" + 
                "                eval( b.intValue()>10 )\n" + 
                "           )\n" +  
                "           or\n" + 
                "           (\n" + 
                "                b2 : Field( name == \"b\" ) and\n" + 
                "                eval( b2.intValue()<10 )\n" + 
                "           )\n" + 
                "        )\n" + 
                "        and \n" + 
                "        (\n" + 
                "            t : Field( name == \"t\" ) and\n" + 
                "            eval( t.getValue().equals(\"Y\") )\n" + 
                "        )\n" + 
                "        and (\n" + 
                "           (\n" + 
                "                c : Field( name == \"c\" ) and\n" + 
                "                eval( c.getValue().equals(\"c\") ) and\n" +                 
                "                d : Field( name == \"d\" ) and\n" + 
                "                eval( d.intValue()<5 )\n" + 
                "           ) \n" + 
                "           or \n" + 
                "           (\n" + 
                "                c : Field( name == \"c\" ) and\n" + 
                "                eval( c.getValue().equals(\"c\") ) and\n" + 
                "                d : Field( name == \"d\" ) and\n" + 
                "                eval( d.intValue()<20 )\n" + 
                "           ) \n" + 
                "           or \n" + 
                "           ( \n" + 
                "                c : Field( name == \"c\") and\n" + 
                "                eval( c.getValue().equals(\"d\") ) and\n" + 
                "                d : Field( name == \"d\" ) and\n" + 
                "                eval( d.intValue()<20 )\n" + 
                "           )\n" + 
                "        )\n" + 
                "    then\n" + 
                "        System.out.println( \"Worked!\" ); \n" + 
                "end";
        
        logger.info( str );
        
        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = createKnowledgeSession(kbase);
             
        ksession.insert(new Field("t", "Y"));
        ksession.insert(new Field("a", "b"));
        ksession.insert(new Field("b", "15"));
        ksession.insert(new Field("c", "d"));
        ksession.insert(new Field("d", "15"));
        ksession.fireAllRules();   
        ksession.dispose();
    }

    @Test 
    public void testOrs() throws Exception {
        String str = "package org.drools.mvel.integrationtests\n" +
                "import " + Message.class.getName() + "\n" +
                "rule X\n" + 
                "    when\n" +
                "        Message( message == 'test' )\n" +
                "        Message( !fired ) or eval( !false )\n" + 
                "    then\n" + 
                "end\n";
        
        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = createKnowledgeSession(kbase);
        
        ksession.insert( new Message( "test" ) );
        int rules = ksession.fireAllRules();
        assertEquals( 2, rules );
        ksession.dispose();
    }

    public class Field {
        public Field(String name, String value) {
            super();
            this.name = name;
            this.value = value;
        }
        
        private String name;
        private String value;
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        
        public int intValue() {
            Integer intValue = Integer.valueOf(value);
            return intValue;
        }
    }    
}
