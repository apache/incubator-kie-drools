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

package org.drools.compiler.integrationtests;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.OuterClass;
import org.drools.compiler.Person;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.command.runtime.rule.InsertElementsCommand;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AccumulateTest extends CommonTestMethodBase {

    @Test(timeout = 10000)
    public void testAccumulateModify() throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateModify.drl" );

        final List<?> results = new ArrayList<Object>();
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
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final org.kie.api.runtime.rule.FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 24,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 31,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    @Test(timeout = 10000)
    public void testAccumulate() throws Exception {

        // read in the source
        KieSession wm = getKieSessionFromResources( "test_Accumulate.drl" );

        final List<?> results = new ArrayList<Object>();
        wm.setGlobal( "results",
                      results );

        wm.insert( new Person( "Bob",
                               "stilton",
                               20 ) );
        wm.insert( new Person( "Mark",
                               "provolone" ) );
        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );

        wm.fireAllRules();

        System.out.println( results );

        assertEquals( 5,
                      results.size() );

        assertEquals( 165, results.get( 0 ) );
        assertEquals( 10, results.get( 1 ) );
        assertEquals( 150, results.get( 2 ) );
        assertEquals( 10, results.get( 3 ) );
        assertEquals( 210, results.get( 4 ) );
    }

    @Test(timeout = 10000)
    public void testMVELAccumulate() throws Exception {

        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateMVEL.drl" );
        final List<?> results = new ArrayList<Object>();
        wm.setGlobal( "results",
                      results );

        wm.insert( new Person( "Bob",
                               "stilton",
                               20 ) );
        wm.insert( new Person( "Mark",
                               "provolone" ) );
        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );

        wm.fireAllRules();

        assertEquals( 165, results.get( 0 ) );
        assertEquals( 10, results.get( 1 ) );
        assertEquals( 150, results.get( 2 ) );
        assertEquals( 10, results.get( 3 ) );
        assertEquals( 210, results.get( 4 ) );
    }

    @Test(timeout = 10000)
    public void testAccumulateModifyMVEL() throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateModifyMVEL.drl" );
        final List<?> results = new ArrayList<Object>();

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
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final org.kie.api.runtime.rule.FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 24,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 31,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModify() throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateReverseModify.drl" );
        final List<?> results = new ArrayList<Object>();
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
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final org.kie.api.runtime.rule.FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 24,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        cheese[3].setPrice( 20 );
        wm.update( cheeseHandles[3],
                   cheese[3] );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 36,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModify2() throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateReverseModify2.drl" );
        final List<?> results = new ArrayList<Object>();

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
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 24,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        cheese[3].setPrice( 20 );
        wm.update( cheeseHandles[3],
                   cheese[3] );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 36,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModifyInsertLogical2() throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateReverseModifyInsertLogical2.drl" );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{
                new Cheese( "stilton", 10 ),
                new Cheese( "stilton", 2 ),
                new Cheese( "stilton", 5 ),
                new Cheese( "brie", 15 ),
                new Cheese( "brie", 16 ),
                new Cheese( "provolone", 8 )
        };
        final Person alice = new Person( "Alice", "brie" );
        final Person bob = new Person( "Bob", "stilton" );
        final Person carol = new Person( "Carol", "cheddar" );
        final Person doug = new Person( "Doug", "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle aliceHandle = wm.insert( alice );
        final FactHandle bobHandle = wm.insert( bob );
        // add Carol later
        final FactHandle dougHandle = wm.insert( doug ); // should be ignored

        // alice = 31, bob = 17, carol = 0, doug = 17
        // !alice = 34, !bob = 31, !carol = 65, !doug = 31
        wm.fireAllRules();
        assertEquals( 31, ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // delete stilton=2 ==> bob = 15, doug = 15, !alice = 30, !carol = 61
        wm.delete( cheeseHandles[1] );
        wm.fireAllRules();
        assertEquals( 30, ( (Number) results.get( results.size() - 1 ) ).intValue() );
    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModifyMVEL() throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateReverseModifyMVEL.drl" );
        final List<?> results = new ArrayList<Object>();

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
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 24,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 31,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModifyMVEL2() throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateReverseModifyMVEL2.drl" );
        final List<?> results = new ArrayList<Object>();

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
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 24,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 31,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    @Test(timeout = 10000)
    public void testAccumulateWithFromChaining() throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateWithFromChaining.drl" );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         8 ), new Cheese( "stilton",
                                                                          10 ), new Cheese( "stilton",
                                                                                            9 ), new Cheese( "brie",
                                                                                                             4 ), new Cheese( "brie",
                                                                                                                              1 ), new Cheese( "provolone",
                                                                                                                                               8 )};

        Cheesery cheesery = new Cheesery();

        for ( int i = 0; i < cheese.length; i++ ) {
            cheesery.addCheese( cheese[i] );
        }

        FactHandle cheeseryHandle = wm.insert( cheesery );

        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // one fire, as per rule constraints
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ( (List) results.get( results.size() - 1 ) ).size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setType( "brie" );
        wm.update( cheeseryHandle,
                   cheesery );
        wm.fireAllRules();

        // no fire
        assertEquals( 1,
                      results.size() );
        System.out.println( results );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 3,
                      ( (List) results.get( results.size() - 1 ) ).size() );

        // ---------------- 4th scenario
        cheesery.getCheeses().remove( cheese[3] );
        wm.update( cheeseryHandle,
                   cheesery );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    @Test(timeout = 10000)
    public void testMVELAccumulate2WM() throws Exception {

        // read in the source
        KieBase kbase = loadKnowledgeBase( "test_AccumulateMVEL.drl" );
        KieSession wm1 = createKieSession( kbase );
        final List<?> results1 = new ArrayList<Object>();

        wm1.setGlobal( "results",
                       results1 );

        KieSession wm2 = createKieSession( kbase );
        final List<?> results2 = new ArrayList<Object>();

        wm2.setGlobal( "results",
                       results2 );

        wm1.insert( new Person( "Bob",
                                "stilton",
                                20 ) );
        wm1.insert( new Person( "Mark",
                                "provolone" ) );

        wm2.insert( new Person( "Bob",
                                "stilton",
                                20 ) );
        wm2.insert( new Person( "Mark",
                                "provolone" ) );

        wm1.insert( new Cheese( "stilton",
                                10 ) );
        wm1.insert( new Cheese( "brie",
                                5 ) );
        wm2.insert( new Cheese( "stilton",
                                10 ) );
        wm1.insert( new Cheese( "provolone",
                                150 ) );
        wm2.insert( new Cheese( "brie",
                                5 ) );
        wm2.insert( new Cheese( "provolone",
                                150 ) );
        wm1.fireAllRules();

        wm2.fireAllRules();

        assertEquals( 165, results1.get( 0 ) );
        assertEquals( 10, results1.get( 1 ) );
        assertEquals( 150, results1.get( 2 ) );
        assertEquals( 10, results1.get( 3 ) );
        assertEquals( 210, results1.get( 4 ) );

        assertEquals( 165, results2.get( 0 ) );
        assertEquals( 10, results2.get( 1 ) );
        assertEquals( 150, results2.get( 2 ) );
        assertEquals( 10, results2.get( 3 ) );
        assertEquals( 210, results2.get( 4 ) );
    }

    @Test(timeout = 10000)
    public void testAccumulateInnerClass() throws Exception {

        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateInnerClass.drl" );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        wm.insert( new OuterClass.InnerClass( 10 ) );
        wm.insert( new OuterClass.InnerClass( 5 ) );

        wm.fireAllRules();

        assertEquals( 15, results.get( 0 ) );
    }

    @Test(timeout = 10000)
    public void testAccumulateReturningNull() throws Exception {

        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateReturningNull.drl" );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
    }

    @Test(timeout = 10000)
    public void testAccumulateSumJava() throws Exception {
        execTestAccumulateSum( "test_AccumulateSum.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateSumMVEL() throws Exception {
        execTestAccumulateSum( "test_AccumulateSumMVEL.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMultiPatternWithFunctionJava() throws Exception {
        execTestAccumulateSum( "test_AccumulateMultiPatternFunctionJava.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMultiPatternWithFunctionMVEL() throws Exception {
        execTestAccumulateSum( "test_AccumulateMultiPatternFunctionMVEL.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateCountJava() throws Exception {
        execTestAccumulateCount( "test_AccumulateCount.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateCountMVEL() throws Exception {
        execTestAccumulateCount( "test_AccumulateCountMVEL.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateAverageJava() throws Exception {
        execTestAccumulateAverage( "test_AccumulateAverage.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateAverageMVEL() throws Exception {
        execTestAccumulateAverage( "test_AccumulateAverageMVEL.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMinJava() throws Exception {
        execTestAccumulateMin( "test_AccumulateMin.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMinMVEL() throws Exception {
        execTestAccumulateMin( "test_AccumulateMinMVEL.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMaxJava() throws Exception {
        execTestAccumulateMax( "test_AccumulateMax.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMaxMVEL() throws Exception {
        execTestAccumulateMax( "test_AccumulateMaxMVEL.drl" );
    }

    @Test//(timeout = 10000)
    public void testAccumulateMultiPatternJava() throws Exception {
        execTestAccumulateReverseModifyMultiPattern( "test_AccumulateMultiPattern.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMultiPatternMVEL() throws Exception {
        execTestAccumulateReverseModifyMultiPattern( "test_AccumulateMultiPatternMVEL.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateCollectListJava() throws Exception {
        execTestAccumulateCollectList( "test_AccumulateCollectList.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateCollectListMVEL() throws Exception {
        execTestAccumulateCollectList( "test_AccumulateCollectListMVEL.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateCollectSetJava() throws Exception {
        execTestAccumulateCollectSet( "test_AccumulateCollectSet.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateCollectSetMVEL() throws Exception {
        execTestAccumulateCollectSet( "test_AccumulateCollectSetMVEL.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMultipleFunctionsJava() throws Exception {
        execTestAccumulateMultipleFunctions( "test_AccumulateMultipleFunctions.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMultipleFunctionsMVEL() throws Exception {
        execTestAccumulateMultipleFunctions( "test_AccumulateMultipleFunctionsMVEL.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateMultipleFunctionsConstraint() throws Exception {
        execTestAccumulateMultipleFunctionsConstraint( "test_AccumulateMultipleFunctionsConstraint.drl" );
    }

    @Test(timeout = 10000)
    public void testAccumulateWithAndOrCombinations() throws Exception {
        // JBRULES-3482
        // once this compils, update it to actually assert on correct outputs.

        String rule = "package org.drools.compiler.test;\n" +
                      "import org.drools.compiler.Cheese;\n" +
                      "import org.drools.compiler.Person;\n" +

                      "rule \"Class cast causer\"\n" +
                      "    when\n" +
                      "        $person      : Person( $likes : likes )\n" +
                      "        $total       : Number() from accumulate( $p : Person(likes != $likes, $l : likes) and $c : Cheese( type == $l ),\n" +
                      "                                                min($c.getPrice()) )\n" +
                      "        ($p2 : Person(name == 'nobody') or $p2 : Person(name == 'Doug'))\n" +
                      "    then\n" +
                      "        System.out.println($p2.getName());\n" +
                      "end\n";
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );

        wm.insert( new Cheese( "stilton", 10 ) );
        wm.insert( new Person( "Alice", "brie" ) );
        wm.insert( new Person( "Bob", "stilton" ) );
    }

    @Test//(timeout = 10000)
    public void testAccumulateWithSameSubnetwork() throws Exception {
        String rule = "package org.drools.compiler.test;\n" +
                      "import org.drools.compiler.Cheese;\n" +
                      "import org.drools.compiler.Person;\n" +
                      "global java.util.List list; \n" +
                      "rule r1 salience 100 \n" +
                      "    when\n" +
                      "        $person      : Person( name == 'Alice', $likes : likes )\n" +
                      "        $total       : Number() from accumulate( $p : Person(likes != $likes, $l : likes) and $c : Cheese( type == $l ),\n" +
                      "                                                min($c.getPrice()) )\n" +
                      "    then\n" +
                      "        list.add( 'r1' + ':' + $total);\n" +
                      "end\n" +
                      "rule r2 \n" +
                      "    when\n" +
                      "        $person      : Person( name == 'Alice', $likes : likes )\n" +
                      "        $total       : Number() from accumulate( $p : Person(likes != $likes, $l : likes) and $c : Cheese( type == $l ),\n" +
                      "                                                max($c.getPrice()) )\n" +
                      "    then\n" +
                      "        list.add( 'r2' + ':' + $total);\n" +
                      "end\n" +

                      "";
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );

        List list = new ArrayList();
        wm.setGlobal( "list", list );

        // Check the network formation, to ensure the RiaNode is shared.
        ObjectTypeNode cheeseOtn = LinkingTest.getObjectTypeNode( kbase, Cheese.class );
        ObjectSink[] oSinks = cheeseOtn.getObjectSinkPropagator().getSinks();
        assertEquals( 1, oSinks.length );

        JoinNode cheeseJoin = (JoinNode) oSinks[0];
        LeftTupleSink[] ltSinks = cheeseJoin.getSinkPropagator().getSinks();

        assertEquals( 1, ltSinks.length );
        RightInputAdapterNode rian = (RightInputAdapterNode) ltSinks[0];
        assertEquals( 2, rian.getObjectSinkPropagator().size() );   //  RiaNode is shared, if this has two outputs

        wm.insert( new Cheese( "stilton", 10 ) );
        wm.insert( new Person( "Alice", "brie" ) );
        wm.insert( new Person( "Bob", "stilton" ) );

        wm.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( "r1:10", list.get( 0 ) );
        assertEquals( "r2:10", list.get( 1 ) );
    }

    public void execTestAccumulateSum( String fileName ) throws Exception {
        // read in the source
        KieSession session = getKieSessionFromResources( fileName );

        DataSet data = new DataSet();
        data.results = new ArrayList<Object>();

        session.setGlobal( "results",
                           data.results );

        data.cheese = new Cheese[]{new Cheese( "stilton",
                                               8,
                                               0 ), new Cheese( "stilton",
                                                                10,
                                                                1 ), new Cheese( "stilton",
                                                                                 9,
                                                                                 2 ), new Cheese( "brie",
                                                                                                  11,
                                                                                                  3 ), new Cheese( "brie",
                                                                                                                   4,
                                                                                                                   4 ), new Cheese( "provolone",
                                                                                                                                    8,
                                                                                                                                    5 )};
        data.bob = new Person( "Bob",
                               "stilton" );

        data.cheeseHandles = new FactHandle[data.cheese.length];
        for ( int i = 0; i < data.cheese.length; i++ ) {
            data.cheeseHandles[i] = session.insert( data.cheese[i] );
        }
        data.bobHandle = session.insert( data.bob );

        // ---------------- 1st scenario
        session.fireAllRules();
        assertEquals( 1,
                      data.results.size() );
        assertEquals( 27,
                      ( (Number) data.results.get( data.results.size() - 1 ) ).intValue() );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                             true );
        updateReferences( session,
                          data );

        // ---------------- 2nd scenario
        final int index = 1;
        data.cheese[index].setPrice( 3 );
        session.update( data.cheeseHandles[index],
                        data.cheese[index] );
        int count = session.fireAllRules();
        assertEquals( 1, count );

        assertEquals( 2,
                      data.results.size() );
        assertEquals( 20,
                      ( (Number) data.results.get( data.results.size() - 1 ) ).intValue() );

        // ---------------- 3rd scenario
        data.bob.setLikes( "brie" );
        session.update( data.bobHandle,
                        data.bob );
        session.fireAllRules();

        assertEquals( 3,
                      data.results.size() );
        assertEquals( 15,
                      ( (Number) data.results.get( data.results.size() - 1 ) ).intValue() );

        // ---------------- 4th scenario
        session.delete( data.cheeseHandles[3] );
        session.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 3,
                      data.results.size() );

    }

    private void updateReferences( final KieSession session,
                                   final DataSet data ) {
        data.results = (List<?>) session.getGlobal( "results" );
        for ( Iterator<?> it = session.getObjects().iterator(); it.hasNext(); ) {
            Object next = it.next();
            if ( next instanceof Cheese ) {
                Cheese c = (Cheese) next;
                data.cheese[c.getOldPrice()] = c;
                data.cheeseHandles[c.getOldPrice()] = session.getFactHandle( c );
                assertNotNull( data.cheeseHandles[c.getOldPrice()] );
            } else if ( next instanceof Person ) {
                data.bob = (Person) next;
                data.bobHandle = session.getFactHandle( data.bob );
            }
        }
    }

    public void execTestAccumulateCount( String fileName ) throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( fileName );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         8 ), new Cheese( "stilton",
                                                                          10 ), new Cheese( "stilton",
                                                                                            9 ), new Cheese( "brie",
                                                                                                             4 ), new Cheese( "brie",
                                                                                                                              1 ), new Cheese( "provolone",
                                                                                                                                               8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 3 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 2,
                      results.size() );
        assertEquals( 3,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 3,
                      results.size() );
        assertEquals( 2,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 3,
                      results.size() );

    }

    public void execTestAccumulateAverage( String fileName ) throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( fileName );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            11 ), new Cheese( "brie",
                                                                                                              15 ), new Cheese( "brie",
                                                                                                                                17 ), new Cheese( "provolone",
                                                                                                                                                  8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 10,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 16,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.delete( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    public void execTestAccumulateMin( String fileName ) throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( fileName );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         8 ), new Cheese( "stilton",
                                                                          10 ), new Cheese( "stilton",
                                                                                            9 ), new Cheese( "brie",
                                                                                                             4 ), new Cheese( "brie",
                                                                                                                              1 ), new Cheese( "provolone",
                                                                                                                                               8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 3 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 1,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.delete( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    public void execTestAccumulateMax( String fileName ) throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( fileName );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         4 ), new Cheese( "stilton",
                                                                          2 ), new Cheese( "stilton",
                                                                                           3 ), new Cheese( "brie",
                                                                                                            15 ), new Cheese( "brie",
                                                                                                                              17 ), new Cheese( "provolone",
                                                                                                                                                8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 9,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 17,
                      ( (Number) results.get( results.size() - 1 ) ).intValue() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.delete( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    public void execTestAccumulateCollectList( String fileName ) throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( fileName );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         4 ), new Cheese( "stilton",
                                                                          2 ), new Cheese( "stilton",
                                                                                           3 ), new Cheese( "brie",
                                                                                                            15 ), new Cheese( "brie",
                                                                                                                              17 ), new Cheese( "provolone",
                                                                                                                                                8 )};
        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }

        // ---------------- 1st scenario
        wm.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 6,
                      ( (List) results.get( results.size() - 1 ) ).size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // fire again
        assertEquals( 2,
                      results.size() );
        assertEquals( 6,
                      ( (List) results.get( results.size() - 1 ) ).size() );

        // ---------------- 3rd scenario
        wm.delete( cheeseHandles[3] );
        wm.delete( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    public void execTestAccumulateCollectSet( String fileName ) throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( fileName );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         4 ), new Cheese( "stilton",
                                                                          2 ), new Cheese( "stilton",
                                                                                           3 ), new Cheese( "brie",
                                                                                                            15 ), new Cheese( "brie",
                                                                                                                              17 ), new Cheese( "provolone",
                                                                                                                                                8 )};
        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }

        // ---------------- 1st scenario
        wm.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      ( (Set) results.get( results.size() - 1 ) ).size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // fire again
        assertEquals( 2,
                      results.size() );
        assertEquals( 3,
                      ( (Set) results.get( results.size() - 1 ) ).size() );

        // ---------------- 3rd scenario
        wm.delete( cheeseHandles[3] );
        wm.fireAllRules();
        // fire again
        assertEquals( 3,
                      results.size() );
        assertEquals( 3,
                      ( (Set) results.get( results.size() - 1 ) ).size() );

        // ---------------- 4rd scenario
        wm.delete( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 3,
                      results.size() );

    }

    public void execTestAccumulateReverseModifyMultiPattern( String fileName ) throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( fileName );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{ new Cheese( "stilton", 10 ),
                                              new Cheese( "stilton", 2 ),
                                              new Cheese( "stilton", 5 ),
                                              new Cheese( "brie", 15 ),
                                              new Cheese( "brie", 16 ),
                                              new Cheese( "provolone", 8 ) };

        final Person bob = new Person( "Bob",
                                       "stilton" );
        final Person mark = new Person( "Mark",
                                        "provolone" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );
        final FactHandle markHandle = wm.insert( mark );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        assertEquals( 0,
                      results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        assertEquals( 1,
                      results.size() );
        assertEquals( 32,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 39,
                      ( (Cheesery) results.get( results.size() - 1 ) ).getTotalAmount() );

        // ---------------- 4th scenario
        wm.delete( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                      results.size() );

    }

    @Test(timeout = 10000)
    public void testAccumulateWithPreviouslyBoundVariables() throws Exception {

        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulatePreviousBinds.drl" );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );
        wm.insert( new Cheese( "brie",
                               20 ) );

        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( 45,
                      results.get( 0 ) );
    }

    @Test(timeout = 10000)
    public void testAccumulateMVELWithModify() throws Exception {
        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateMVELwithModify.drl" );
        final List<Number> results = new ArrayList<Number>();
        wm.setGlobal( "results",
                      results );

        Order order = new Order( 1,
                                 "Bob" );
        OrderItem item1 = new OrderItem( order,
                                         1,
                                         "maquilage",
                                         1,
                                         10 );
        OrderItem item2 = new OrderItem( order,
                                         2,
                                         "perfume",
                                         1,
                                         5 );
        order.addItem( item1 );
        order.addItem( item2 );

        wm.insert( order );
        wm.insert( item1 );
        wm.insert( item2 );
        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( 15,
                      results.get( 0 ).intValue() );
        assertEquals( 15.0,
                      order.getTotal(),
                      0.0 );
    }

    @Test(timeout = 10000)
    public void testAccumulateGlobals() throws Exception {

        // read in the source
        KieSession wm = getKieSessionFromResources( "test_AccumulateGlobals.drl" );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );
        wm.setGlobal( "globalValue",
                      50 );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );
        wm.insert( new Cheese( "brie",
                               20 ) );

        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( 100,
                      results.get( 0 ) );
    }

    @Test(timeout = 10000)
    public void testAccumulateNonExistingFunction() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "test_NonExistingAccumulateFunction.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        // should report a proper error, not raise an exception
        assertTrue( "It must report a proper error when trying to use a non-registered funcion",
                    kbuilder.hasErrors() );

        assertTrue( kbuilder.getErrors().toString().contains( "Unknown accumulate function: 'nonExistingFunction' on rule 'Accumulate non existing function - Java'." ) );
        assertTrue( kbuilder.getErrors().toString().contains( "Unknown accumulate function: 'nonExistingFunction' on rule 'Accumulate non existing function - MVEL'." ) );

    }

    @Test(timeout = 10000)
    public void testAccumulateZeroParams() {
        String rule = "global java.util.List list;\n" +
                      "rule fromIt\n" +
                      "when\n" +
                      "    Number( $c: intValue ) from accumulate( Integer(), count( ) )\n" +
                      "then\n" +
                      "    list.add( $c );\n" +
                      "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new Integer( 1 ) );
        ksession.insert( new Integer( 2 ) );
        ksession.insert( new Integer( 3 ) );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 3, list.get( 0 ) );

    }

    public void execTestAccumulateMultipleFunctions( String fileName ) throws Exception {
        KieSession ksession = getKieSessionFromResources( fileName );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ),
                new Cheese( "stilton",
                            3 ),
                new Cheese( "stilton",
                            5 ),
                new Cheese( "brie",
                            15 ),
                new Cheese( "brie",
                            17 ),
                new Cheese( "provolone",
                            8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = (FactHandle) ksession.insert( cheese[i] );
        }
        final FactHandle bobHandle = (FactHandle) ksession.insert( bob );

        // ---------------- 1st scenario
        ksession.fireAllRules();

        ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        Mockito.verify( ael ).afterMatchFired( cap.capture() );

        Match activation = cap.getValue().getMatch();
        assertThat( ( (Number) activation.getDeclarationValue( "$sum" ) ).intValue(),
                    is( 18 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$min" ) ).intValue(),
                    is( 3 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$avg" ) ).intValue(),
                    is( 6 ) );

        Mockito.reset( ael );
        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        ksession.update( cheeseHandles[index],
                         cheese[index] );
        ksession.fireAllRules();

        Mockito.verify( ael ).afterMatchFired( cap.capture() );

        activation = cap.getValue().getMatch();
        assertThat( ( (Number) activation.getDeclarationValue( "$sum" ) ).intValue(),
                    is( 24 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$min" ) ).intValue(),
                    is( 5 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$avg" ) ).intValue(),
                    is( 8 ) );

        Mockito.reset( ael );
        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        ksession.update( bobHandle,
                         bob );
        ksession.fireAllRules();

        Mockito.verify( ael ).afterMatchFired( cap.capture() );

        activation = cap.getValue().getMatch();
        assertThat( ( (Number) activation.getDeclarationValue( "$sum" ) ).intValue(),
                    is( 32 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$min" ) ).intValue(),
                    is( 15 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$avg" ) ).intValue(),
                    is( 16 ) );

        Mockito.reset( ael );
        // ---------------- 4th scenario
        ksession.delete( cheeseHandles[3] );
        ksession.fireAllRules();

        Mockito.verify( ael ).afterMatchFired( cap.capture() );

        activation = cap.getValue().getMatch();
        assertThat( ( (Number) activation.getDeclarationValue( "$sum" ) ).intValue(),
                    is( 17 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$min" ) ).intValue(),
                    is( 17 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$avg" ) ).intValue(),
                    is( 17 ) );
    }

    public void execTestAccumulateMultipleFunctionsConstraint( String fileName ) throws Exception {
        KieSession ksession = getKieSessionFromResources( fileName );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ),
                new Cheese( "stilton",
                            3 ),
                new Cheese( "stilton",
                            5 ),
                new Cheese( "brie",
                            3 ),
                new Cheese( "brie",
                            17 ),
                new Cheese( "provolone",
                            8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = (FactHandle) ksession.insert( cheese[i] );
        }
        final FactHandle bobHandle = (FactHandle) ksession.insert( bob );

        // ---------------- 1st scenario
        ksession.fireAllRules();

        ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        Mockito.verify( ael ).afterMatchFired( cap.capture() );

        Match activation = cap.getValue().getMatch();
        assertThat( ( (Number) activation.getDeclarationValue( "$sum" ) ).intValue(),
                    is( 18 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$min" ) ).intValue(),
                    is( 3 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$avg" ) ).intValue(),
                    is( 6 ) );

        Mockito.reset( ael );
        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        ksession.update( cheeseHandles[index],
                         cheese[index] );
        ksession.fireAllRules();

        Mockito.verify( ael, Mockito.never() ).afterMatchFired( Mockito.any( AfterMatchFiredEvent.class ) );

        Mockito.reset( ael );
        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        ksession.update( bobHandle,
                         bob );
        ksession.fireAllRules();

        Mockito.verify( ael ).afterMatchFired( cap.capture() );

        activation = cap.getValue().getMatch();
        assertThat( ( (Number) activation.getDeclarationValue( "$sum" ) ).intValue(),
                    is( 20 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$min" ) ).intValue(),
                    is( 3 ) );
        assertThat( ( (Number) activation.getDeclarationValue( "$avg" ) ).intValue(),
                    is( 10 ) );

        ksession.dispose();

    }

    public static class DataSet {
        public Cheese[] cheese;
        public FactHandle[] cheeseHandles;
        public Person bob;
        public FactHandle bobHandle;
        public List<?> results;
    }

    @Test(timeout = 10000)
    public void testAccumulateMinMax() throws Exception {
        String drl = "package org.drools.compiler.test \n" +
                     "import org.drools.compiler.Cheese \n" +
                     "global java.util.List results \n " +
                     "rule minMax \n" +
                     "when \n" +
                     "    accumulate( Cheese( $p: price ), $min: min($p), $max: max($p) ) \n" +
                     "then \n" +
                     "    results.add($min); results.add($max); \n" +
                     "end \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

        final List<Number> results = new ArrayList<Number>();
        ksession.setGlobal( "results",
                            results );
        final Cheese[] cheese = new Cheese[]{new Cheese( "Emmentaler",
                                                         4 ),
                new Cheese( "Appenzeller",
                            6 ),
                new Cheese( "Greyerzer",
                            2 ),
                new Cheese( "Raclette",
                            3 ),
                new Cheese( "Olmützer Quargel",
                            15 ),
                new Cheese( "Brie",
                            17 ),
                new Cheese( "Dolcelatte",
                            8 )};

        for ( Cheese aCheese : cheese ) {
            ksession.insert( aCheese );
        }

        // ---------------- 1st scenario
        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );
        assertEquals( results.get( 0 ).intValue(),
                      2 );
        assertEquals( results.get( 1 ).intValue(),
                      17 );
    }

    @Test(timeout = 10000)
    public void testAccumulateCE() throws Exception {
        String drl = "package org.drools.compiler\n" +
                     "global java.util.List results\n" +
                     "rule \"ocount\"\n" +
                     "when\n" +
                     "    accumulate( Cheese(), $c: count(1) )\n" +
                     "then\n" +
                     "    results.add( $c + \" facts\" );\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

        final List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );
        final Cheese[] cheese = new Cheese[]{new Cheese( "Emmentaler",
                                                         4 ),
                new Cheese( "Appenzeller",
                            6 ),
                new Cheese( "Greyerzer",
                            2 ),
                new Cheese( "Raclette",
                            3 ),
                new Cheese( "Olmützer Quargel",
                            15 ),
                new Cheese( "Brie",
                            17 ),
                new Cheese( "Dolcelatte",
                            8 )};

        for ( Cheese aCheese : cheese ) {
            ksession.insert( aCheese );
        }

        // ---------------- 1st scenario
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( "7 facts",
                      results.get( 0 ) );
    }


    @Test(timeout = 10000)
    public void testAccumulateAndRetract() {
        String drl = "package org.drools.compiler;\n" +
                     "\n" +
                     "import java.util.ArrayList;\n" +
                     "\n" +
                     "global ArrayList list;\n" +
                     "\n" +
                     "declare Holder\n" +
                     "    list : ArrayList\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Init\"\n" +
                     "when\n" +
                     "    $l : ArrayList()\n" +
                     "then\n" +
                     "    insert( new Holder($l) );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"axx\"\n" +
                     "when\n" +
                     "    $h : Holder( $l : list )\n" +
                     "    $n : Long() from accumulate (\n" +
                     "                    $b : String( ) from $l;\n" +
                     "                    count($b))\n" +
                     "then\n" +
                     "    System.out.println($n);\n" +
                     "    list.add($n);\n" +
                     "end\n" +
                     "\n" +
                     "rule \"clean\"\n" +
                     "salience -10\n" +
                     "when\n" +
                     "    $h : Holder()\n" +
                     "then\n" +
                     "    retract($h);\n" +
                     "end" +
                     "\n";

        KieSession ks = getKieSessionFromContentStrings( drl );

        ArrayList resList = new ArrayList();
        ks.setGlobal( "list", resList );

        ArrayList<String> list = new ArrayList<String>();
        list.add( "x" );
        list.add( "y" );
        list.add( "z" );

        ks.insert( list );
        ks.fireAllRules();

        assertEquals( 3L, resList.get( 0 ) );
    }

    @Test(timeout = 10000)
    public void testAccumulateWithNull() {
        String drl = "rule foo\n" +
                     "when\n" +
                     "Object() from accumulate( Object(),\n" +
                     "init( Object res = null; )\n" +
                     "action( res = null; )\n" +
                     "result( res ) )\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSessionFromContentStrings( drl );
        ksession.fireAllRules();
        ksession.dispose();
    }

    public static class MyObj {
        public static class NestedObj {
            public long value;

            public NestedObj( long value ) {
                this.value = value;
            }
        }

        private final NestedObj nestedObj;

        public MyObj( long value ) {
            nestedObj = new NestedObj( value );
        }

        public NestedObj getNestedObj() {
            return nestedObj;
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithBoundExpression() {
        String drl = "package org.drools.compiler;\n" +
                     "import " + AccumulateTest.MyObj.class.getCanonicalName() + ";\n" +
                     "global java.util.List results\n" +
                     "rule init\n" +
                     "   when\n" +
                     "   then\n" +
                     "       insert( new MyObj(5) );\n" +
                     "       insert( new MyObj(4) );\n" +
                     "end\n" +
                     "rule foo\n" +
                     "   salience -10\n" +
                     "   when\n" +
                     "       $n : Number() from accumulate( MyObj( $val : nestedObj.value ),\n" +
                     "                                      sum( $val ) )\n" +
                     "   then\n" +
                     "       System.out.println($n);\n" +
                     "       results.add($n);\n" +
                     "end";

        KieBase kbase = loadKnowledgeBaseFromString( drl );
        KieSession ksession = kbase.newKieSession();

        final List<Number> results = new ArrayList<Number>();
        ksession.setGlobal( "results",
                            results );
        ksession.fireAllRules();
        ksession.dispose();
        assertEquals( 1,
                      results.size() );
        assertEquals( 9L,
                      results.get( 0 ) );
    }

    @Test(timeout = 10000)
    public void testInfiniteLoopAddingPkgAfterSession() throws Exception {
        // JBRULES-3488
        String rule = "package org.drools.compiler.test;\n" +
                      "import " + AccumulateTest.Triple.class.getCanonicalName() + ";\n" +
                      "rule \"accumulate 2 times\"\n" +
                      "when\n" +
                      "  $LIST : java.util.List( )" +
                      "  from accumulate( $Triple_1 : Triple( $CN : subject," +
                      "    predicate == \"<http://deductions.sf.net/samples/princing.n3p.n3#number>\", $N : object )," +
                      "      collectList( $N ) )\n" +
                      "  $NUMBER : Number() from accumulate(" +
                      "    $NUMBER_STRING_ : String() from $LIST , sum( Double.parseDouble( $NUMBER_STRING_)) )\n" +
                      "then\n" +
                      "  System.out.println(\"ok\");\n" +
                      "end\n";

        final KnowledgeBase kbase = getKnowledgeBase();

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        // To reproduce, Need to have 3 object asserted (not less) :
        ksession.insert( new Triple( "<http://deductions.sf.net/samples/princing.n3p.n3#CN1>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "200" ) );
        ksession.insert( new Triple( "<http://deductions.sf.net/samples/princing.n3p.n3#CN2>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "100" ) );
        ksession.insert( new Triple( "<http://deductions.sf.net/samples/princing.n3p.n3#CN3>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "100" ) );

        kbase.addKnowledgePackages( loadKnowledgePackagesFromString( rule ) );
        ksession.fireAllRules();
    }

    public static class Triple {
        private String subject;
        private String predicate;
        private String object;

        /**
         * for javabeans
         */
        public Triple() {
        }

        public Triple( String subject, String predicate, String object ) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
            // System.out.print(">>> inst. " + toString() );
        }

        public String getSubject() {
            return subject;
        }

        public String getPredicate() {
            return predicate;
        }

        public String getObject() {
            return object;
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithVarsOutOfHashOrder() throws Exception {
        // JBRULES-3494
        String rule = "package com.sample;\n" +
                      "\n" +
                      "import java.util.List;\n" +
                      "\n" +
                      "declare MessageHolder\n" +
                      "  id : String\n" +
                      "  msg: String\n" +
                      "end\n" +
                      "\n" +
                      "query getResults( String $mId, List $holders )\n" +
                      "  accumulate(  \n" +
                      "    $holder  : MessageHolder( id == $mId, $ans : msg ),\n" +
                      "    $holders := collectList( $holder )\n" +
                      "  ) \n" +
                      "end\n" +
                      "\n" +
                      "rule \"Init\"\n" +
                      "when\n" +
                      "then\n" +
                      "  insert( new MessageHolder( \"1\", \"x\" ) );\n" +
                      "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( rule ) ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        final KnowledgeBase kbase = getKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

        kbase.addKnowledgePackages( loadKnowledgePackagesFromString( rule ) );
        ksession.fireAllRules();

        QueryResults res = ksession.getQueryResults( "getResults", "1", Variable.v );
        assertEquals( 1, res.size() );

        Object o = res.iterator().next().get( "$holders" );
        assertTrue( o instanceof List );
        assertEquals( 1, ( (List) o ).size() );
    }

    @Test(timeout = 10000)
    public void testAccumulateWithWindow() {
        String str = "global java.util.Map map;\n" +
                     " \n" +
                     "declare Double\n" +
                     "@role(event)\n" +
                     "end\n" +
                     " \n" +
                     "declare window Streem\n" +
                     "    Double() over window:length( 10 )\n" +
                     "end\n" +
                     " \n" +
                     "rule \"See\"\n" +
                     "when\n" +
                     "    $a : Double() from accumulate (\n" +
                     "        $d: Double()\n" +
                     "            from window Streem,\n" +
                     "        sum( $d )\n" +
                     "    )\n" +
                     "then\n" +
                     "    System.out.println( \"We have a sum \" + $a );\n" +
                     "end\n";

        KieSession ksession = getKieSessionFromContentStrings( str );

        Map res = new HashMap();
        ksession.setGlobal( "map", res );
        ksession.fireAllRules();

        for ( int j = 0; j < 33; j++ ) {
            ksession.insert( 1.0 * j );
            ksession.fireAllRules();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithEntryPoint() {
        String str = "global java.util.Map map;\n" +
                     " \n" +
                     "declare Double\n" +
                     "@role(event)\n" +
                     "end\n" +
                     " \n" +
                     "rule \"See\"\n" +
                     "when\n" +
                     "    $a : Double() from accumulate (\n" +
                     "        $d: Double()\n" +
                     "            from entry-point data,\n" +
                     "        sum( $d )\n" +
                     "    )\n" +
                     "then\n" +
                     "    System.out.println( \"We have a sum \" + $a );\n" +
                     "end\n";

        KieSession ksession = getKieSessionFromContentStrings( str );

        Map res = new HashMap();
        ksession.setGlobal( "map", res );
        ksession.fireAllRules();

        for ( int j = 0; j < 33; j++ ) {
            ksession.getEntryPoint( "data" ).insert( 1.0 * j );
            ksession.fireAllRules();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithWindowAndEntryPoint() {
        String str = "global java.util.Map map;\n" +
                     " \n" +
                     "declare Double\n" +
                     "@role(event)\n" +
                     "end\n" +
                     " \n" +
                     "declare window Streem\n" +
                     "    Double() over window:length( 10 ) from entry-point data\n" +
                     "end\n" +
                     " \n" +
                     "rule \"See\"\n" +
                     "when\n" +
                     "    $a : Double() from accumulate (\n" +
                     "        $d: Double()\n" +
                     "            from window Streem,\n" +
                     "        sum( $d )\n" +
                     "    )\n" +
                     "then\n" +
                     "    System.out.println( \"We have a sum \" + $a );\n" +
                     "end\n";

        KieSession ksession = getKieSessionFromContentStrings( str );

        Map res = new HashMap();
        ksession.setGlobal( "map", res );
        ksession.fireAllRules();

        for ( int j = 0; j < 33; j++ ) {
            ksession.getEntryPoint( "data" ).insert( 1.0 * j );
            ksession.fireAllRules();
        }
    }

    @Test(timeout = 10000)
    public void test2AccumulatesWithOr() throws Exception {
        // JBRULES-3538
        String str =
                "import java.util.*;\n" +
                "import " + MyPerson.class.getName() + ";\n" +
                "global java.util.Map map;\n" +
                "dialect \"mvel\"\n" +
                "\n" +
                "rule \"Test\"\n" +
                "    when\n" +
                "        $total : Number()\n" +
                "             from accumulate( MyPerson( $age: age ),\n" +
                "                              sum( $age ) )\n" +
                "\n" +
                "        $p: MyPerson();\n" +
                "        $k: List( size > 0 ) from accumulate( MyPerson($kids: kids) from $p.kids,\n" +
                "            init( ArrayList myList = new ArrayList(); ),\n" +
                "            action( myList.addAll($kids); ),\n" +
                "            reverse( myList.removeAll($kids); ),\n" +
                "            result( myList )\n" +
                "        )\n" +
                "\n" +
                "        $r : MyPerson(name == \"Jos Jr Jr\")\n" +

                "        or\n" +
                "        $r : MyPerson(name == \"Jos\")\n" +

                "    then\n" +
                "        Map pMap = map.get( $r.getName() );\n" +
                "        pMap.put( 'total', $total );\n" +
                "        pMap.put( 'p', $p );\n" +
                "        pMap.put( 'k', $k );\n" +
                "        pMap.put( 'r', $r );\n" +
                "        map.put('count', ((Integer)map.get('count')) + 1 );\n " +
                "end\n";

        KieSession ksession = getKieSessionFromContentStrings( str );
        List list = new ArrayList();
        Map map = new HashMap();
        ksession.setGlobal( "map", map );
        map.put( "Jos Jr Jr", new HashMap() );
        map.put( "Jos", new HashMap() );
        map.put( "count", 0 );

        MyPerson josJr = new MyPerson( "Jos Jr Jr", 20,
                                       asList( new MyPerson( "John Jr 1st", 10,
                                                                    asList( new MyPerson( "John Jr Jrx", 4, Collections.<MyPerson>emptyList() ) ) ),
                                                      new MyPerson( "John Jr 2nd", 8, Collections.<MyPerson>emptyList() ) ) );

        MyPerson jos = new MyPerson( "Jos", 30,
                                     asList( new MyPerson( "Jeff Jr 1st", 10, Collections.<MyPerson>emptyList() ),
                                                    new MyPerson( "Jeff Jr 2nd", 8, Collections.<MyPerson>emptyList() ) ) );

        ksession.execute( new InsertElementsCommand( asList( new Object[]{josJr, jos} ) ) );

        ksession.fireAllRules();

        System.out.println( map );

        assertEquals( 2, map.get( "count" ) );
        Map pMap = (Map) map.get( "Jos Jr Jr" );
        assertEquals( 50.0, pMap.get( "total" ) );
        List kids = (List) pMap.get( "k" );
        assertEquals( 1, kids.size() );
        assertEquals( "John Jr Jrx", ( (MyPerson) kids.get( 0 ) ).getName() );
        assertEquals( josJr, pMap.get( "p" ) );
        assertEquals( josJr, pMap.get( "r" ) );

        pMap = (Map) map.get( "Jos" );
        assertEquals( 50.0, pMap.get( "total" ) );
        kids = (List) pMap.get( "k" );
        assertEquals( 1, kids.size() );
        assertEquals( "John Jr Jrx", ( (MyPerson) kids.get( 0 ) ).getName() );
        assertEquals( josJr, pMap.get( "p" ) );
        assertEquals( jos, pMap.get( "r" ) );
    }

    public static class MyPerson {
        public MyPerson( String name, Integer age, Collection<MyPerson> kids ) {
            this.name = name;
            this.age = age;
            this.kids = kids;
        }

        private String name;

        private Integer age;

        private Collection<MyPerson> kids;

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge( Integer age ) {
            this.age = age;
        }

        public Collection<MyPerson> getKids() {
            return kids;
        }

        public void setKids( Collection<MyPerson> kids ) {
            this.kids = kids;
        }
    }

    public static class Course {
        private int minWorkingDaySize;

        public Course( int minWorkingDaySize ) {
            this.minWorkingDaySize = minWorkingDaySize;
        }

        public int getMinWorkingDaySize() {
            return minWorkingDaySize;
        }

        public void setMinWorkingDaySize( int minWorkingDaySize ) {
            this.minWorkingDaySize = minWorkingDaySize;
        }
    }

    public static class Lecture {
        private Course course;
        private int day;

        public Lecture( Course course, int day ) {
            this.course = course;
            this.day = day;
        }

        public Course getCourse() {
            return course;
        }

        public void setCourse( Course course ) {
            this.course = course;
        }

        public int getDay() {
            return day;
        }

        public void setDay( int day ) {
            this.day = day;
        }
    }

    @Test
    public void testAccumulateWithExists() {
        String str =
                "import " + Course.class.getCanonicalName() + "\n" +
                "import " + Lecture.class.getCanonicalName() + "\n" +
                "global java.util.List list; \n" +
                "rule \"minimumWorkingDays\"\n" +
                "    when\n" +
                "        $course : Course($minWorkingDaySize : minWorkingDaySize)\n" +
                "        $dayCount : Number(intValue <= $minWorkingDaySize) from accumulate(\n" +
                "            $day : Integer()\n" +
                "            and exists Lecture(course == $course, day == $day),\n" +
                "            count($day)\n" +
                "        )\n" +
                "        // An uninitialized schedule should have no constraints broken\n" +
                "        exists Lecture(course == $course)\n" +
                "    then\n" +
                "       list.add( $course );\n" +
                "       list.add( $dayCount );\n" +
                "end\n";

        KieSession ksession = getKieSessionFromContentStrings( str );
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Integer day1 = 1;
        Integer day2 = 2;
        Integer day3 = 3;

        Course c = new Course( 2 );

        Lecture l1 = new Lecture( c, day1 );
        Lecture l2 = new Lecture( c, day2 );

        ksession.insert( day1 );
        ksession.insert( day2 );
        ksession.insert( day3 );
        ksession.insert( c );
        ksession.insert( l1 );
        ksession.insert( l2 );

        assertEquals( 1, ksession.fireAllRules() );

        assertEquals( 2, list.size() );
        assertEquals( c, list.get( 0 ) );
        assertEquals( 2l, list.get( 1 ) );
    }

    @Test
    public void testAccumulatesExpireVsCancel() throws Exception {
        // JBRULES-3201
        String drl = "package com.sample;\n" +
                     "\n" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare FactTest\n" +
                     " @role( event ) \n" +
                     "end\n" +
                     " \n" +
                     "rule \"A500 test\"\n" +
                     "when\n" +
                     " accumulate (\n" +
                     " $d : FactTest() over window:time(1m), $tot : count($d); $tot > 0 )\n" +
                     "then\n" +
                     " System.out.println( $tot ); \n" +
                     " list.add( $tot.intValue() ); \n " +
                     "end\n" +
                     "\n";


        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        assertFalse( kbuilder.hasErrors() );

        KieBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbConf.setOption( EventProcessingOption.STREAM );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbConf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( ksConf, null );
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactType ft = kbase.getFactType( "com.sample", "FactTest" );

        ksession.insert( ft.newInstance() );
        ksession.fireAllRules();
        ksession.insert( ft.newInstance() );
        ksession.fireAllRules();
        ksession.insert( ft.newInstance() );
        ksession.fireAllRules();

        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime( 1, TimeUnit.MINUTES );

        ksession.fireAllRules();

        assertFalse( list.contains( 0 ) );
    }


    @Test
    public void testManySlidingWindows() throws Exception {
        String drl = "package com.sample;\n" +
                     "\n" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare Fakt\n" +
                     "  @role( event ) \n" +
                     "  id : int \n" +
                     "end\n" +
                     " \n" +
                     "rule Init \n" +
                     "when \n" +
                     "  $i : Integer() \n" +
                     "then \n" +
                     "  insert( new Fakt( $i ) ); \n" +
                     "end\n" +
                     "" +
                     "rule \"Test\"\n" +
                     "when\n" +
                     "   accumulate ( $d : Fakt( id > 10 ) over window:length(2), $tot1 : count( $d ) ) \n" +
                     "   accumulate ( $d : Fakt( id < 50 ) over window:length(5), $tot2 : count( $d ) ) \n" +
                     "then\n" +
                     "  System.out.println( \"Fire!\" ); \n" +
                     "  list.clear();\n " +
                     "  list.add( $tot1.intValue() ); \n" +
                     "  list.add( $tot2.intValue() ); \n" +
                     "end\n" +
                     "\n";


        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        System.out.println( kbuilder.getErrors() );
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new Integer( 20 ) );
        ksession.fireAllRules();
        assertEquals( list, asList( 1, 1 ) );

        ksession.insert( new Integer( 20 ) );
        ksession.fireAllRules();
        assertEquals( list, asList( 2, 2 ) );

        ksession.insert( new Integer( 20 ) );
        ksession.fireAllRules();
        assertEquals( list, asList( 2, 3 ) );

        ksession.insert( new Integer( 2 ) );
        ksession.fireAllRules();
        assertEquals( list, asList( 2, 4 ) );

        ksession.insert( new Integer( 2 ) );
        ksession.fireAllRules();
        assertEquals( list, asList( 2, 5 ) );

        ksession.insert( new Integer( 2 ) );
        ksession.fireAllRules();
        assertEquals( list, asList( 2, 5 ) );

    }

    @Test
    public void testImportAccumulateFunction() throws Exception {
        String drl = "package org.foo.bar\n"
                     + "import accumulate " + TestFunction.class.getCanonicalName() + " f\n"
                     + "rule X when\n"
                     + "    accumulate( $s : String(),\n"
                     + "                $v : f( $s ) )\n"
                     + "then\n"
                     + "end\n";
        ReleaseId releaseId = new ReleaseIdImpl( "foo", "bar", "1.0" );
        KieServices ks = KieServices.Factory.get();
        createAndDeployJar( ks, releaseId, drl );

        KieContainer kc = ks.newKieContainer( releaseId );
        KieSession ksession = kc.newKieSession();

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        ksession.insert( "x" );
        ksession.fireAllRules();

        ArgumentCaptor<AfterMatchFiredEvent> ac = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael ).afterMatchFired( ac.capture() );

        assertThat( (Integer) ac.getValue().getMatch().getDeclarationValue( "$v" ), is( Integer.valueOf( 1 ) ) );
    }

    @Test
    public void testImportAccumulateFunctionWithDeclaration() throws Exception {
        // DROOLS-750
        String drl = "package org.foo.bar\n"
                     + "import accumulate " + TestFunction.class.getCanonicalName() + " f;\n"
                     + "import " + Person.class.getCanonicalName() + ";\n"
                     + "declare Person \n"
                     + "  @propertyReactive\n"
                     + "end\n"
                     + "rule X when\n"
                     + "    accumulate( $s : String(),\n"
                     + "                $v : f( $s ) )\n"
                     + "then\n"
                     + "end\n";
        ReleaseId releaseId = new ReleaseIdImpl( "foo", "bar", "1.0" );
        KieServices ks = KieServices.Factory.get();
        createAndDeployJar( ks, releaseId, drl );

        KieContainer kc = ks.newKieContainer( releaseId );
        KieSession ksession = kc.newKieSession();

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        ksession.insert( "x" );
        ksession.fireAllRules();

        ArgumentCaptor<AfterMatchFiredEvent> ac = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael ).afterMatchFired( ac.capture() );

        assertThat( (Integer) ac.getValue().getMatch().getDeclarationValue( "$v" ), is( Integer.valueOf( 1 ) ) );
    }

    public static class TestFunction implements AccumulateFunction<Serializable> {
        @Override
        public void writeExternal( ObjectOutput out ) throws IOException {
        }

        @Override
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        }

        @Override
        public Serializable createContext() {
            return null;
        }

        @Override
        public void init( Serializable context ) throws Exception {
        }

        @Override
        public void accumulate( Serializable context, Object value ) {
        }

        @Override
        public void reverse( Serializable context, Object value ) throws Exception {
        }

        @Override
        public Object getResult( Serializable context ) throws Exception {
            return Integer.valueOf( 1 );
        }

        @Override
        public boolean supportsReverse() {
            return true;
        }

        @Override
        public Class<?> getResultType() {
            return Number.class;
        }
    }

    @Test
    public void testAccumulateWithSharedNode() throws Exception {
        // DROOLS-594
        String drl =
                "rule A when" +
                "   Double() " +
                "then " +
                "end " +
                "rule B  " +
                "when " +
                "   Double() " +
                "   String() " +
                "   $list : java.util.List(  this not contains \"XX\" ) " +
                "   $sum  : Integer( ) from accumulate ( $i : Integer(), " +
                "                                        sum( $i ) ) " +
                "then " +
                "    $list.add( \"XX\" );\n" +
                "    update( $list );\n" +
                "    System.out.println(\"FIRED\");\n" +
                "end ";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        List<String> list = new java.util.ArrayList();
        ksession.insert( list );

        ksession.insert( 42.0 );
        ksession.insert( 9000 );
        ksession.insert( "a" );
        ksession.insert( "b" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testEmptyAccumulateInSubnetwork() {
        // DROOLS-598
        String drl =
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $count : Number( ) from accumulate (\n" +
                "        Integer() and\n" +
                "        $s: String();\n" +
                "        count($s)\n" +
                "    )\n" +
                "then\n" +
                "    list.add($count);\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        List<Long> list = new ArrayList<Long>();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 0, (long) list.get( 0 ) );
    }

    @Test
    public void testEmptyAccumulateInSubnetworkFollwedByPattern() {
        // DROOLS-627
        String drl =
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $count : Number( ) from accumulate (\n" +
                "        Integer() and\n" +
                "        $s: String();\n" +
                "        count($s)\n" +
                "    )\n" +
                "    Long()\n" +
                "then\n" +
                "    list.add($count);\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build().newKieSession();

        List<Long> list = new ArrayList<Long>();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );
        ksession.insert( 1L );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 0, (long) list.get( 0 ) );
    }

    @Test
    public void testAccumulateWithoutSeparator() throws Exception {
        // DROOLS-602
        String str = "package org.drools.compiler\n" +
                     "\n" +
                     "rule \"Constraints everywhere\" \n" +
                     "    when\n" +
                     "        $person : Person( $likes : likes )\n" +
                     "        accumulate( Cheese( type == $likes, $price : price )\n" +
                     "                    $sum : sum( $price ),\n" +
                     "                    $avg : average( $price ),\n" +
                     "                    $min : min( $price );\n" +
                     "                    $min == 3,\n" +
                     "                    $sum > 10 )\n" +
                     "    then\n" +
                     "        // do something\n" +
                     "end  ";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertFalse( results.getMessages().isEmpty() );
    }

    @Test
    public void testFromAccumulateWithoutSeparator() throws Exception {
        // DROOLS-602
        String str = "rule R when\n" +
                     "    $count : Number( ) from accumulate (\n" +
                     "        $s: String()\n" +
                     "        count($s)\n" +
                     "    )\n" +
                     "then\n" +
                     "    System.out.println($count);\n" +
                     "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertFalse( results.getMessages().isEmpty() );
    }

    @Test
    public void testAccFunctionOpaqueJoins() throws Exception {
        // DROOLS-661
        testAccFunctionOpaqueJoins(PropertySpecificOption.ALLOWED);
    }

    @Test
    public void testAccFunctionOpaqueJoinsWithPropertyReactivity() throws Exception {
        // DROOLS-1445
        testAccFunctionOpaqueJoins(PropertySpecificOption.ALWAYS);
    }

    private void testAccFunctionOpaqueJoins(PropertySpecificOption propertySpecificOption) throws Exception {
        String str = "package org.test; " +
                     "import java.util.*; " +
                     "global List list; " +
                     "global List list2; " +

                     "declare Tick " +
                     "  tick : int " +
                     "end " +

                     "declare Data " +
                     "  values : List " +
                     "  bias : int = 0 " +
                     "end " +

                     "rule Init " +
                     "when " +
                     "then " +
                     "  insert( new Data( Arrays.asList( 1, 2, 3 ), 1 ) ); " +
                     "  insert( new Data( Arrays.asList( 4, 5, 6 ), 2 ) ); " +
                     "  insert( new Tick( 0 ) );" +
                     "end " +

                     "rule Update " +
                     "  no-loop " +
                     "when " +
                     "  $i : Integer() " +
                     "  $t : Tick() " +
                     "then " +
                     "  System.out.println( 'Set tick to ' + $i ); " +
                     "  modify( $t ) { " +
                     "      setTick( $i ); " +
                     "  } " +
                     "end " +

                     "rule M " +
                     "  dialect 'mvel' " +
                     "when " +
                     "    Tick( $index : tick ) " +
                     "    accumulate ( $data : Data( $bias : bias )," +
                     "                 $tot : sum( $data.values[ $index ] + $bias ) ) " +
                     "then " +
                     "    System.out.println( $tot + ' for J ' + $index ); " +
                     "    list.add( $tot ); " +
                     "end " +

                     "rule J " +
                     "when " +
                     "    Tick( $index : tick ) " +
                     "    accumulate ( $data : Data( $bias : bias )," +
                     "                 $tot : sum( ((Integer)$data.getValues().get( $index )) + $bias ) ) " +
                     "then " +
                     "    System.out.println( $tot + ' for M ' + $index ); " +
                     "    list2.add( $tot ); " +
                     "end ";

        KieHelper helper = new KieHelper( propertySpecificOption );
        KieSession ks = helper.addContent( str, ResourceType.DRL ).build().newKieSession();
        List list = new ArrayList();
        ks.setGlobal( "list", list );
        List list2 = new ArrayList();
        ks.setGlobal( "list2", list2 );

        // init data
        ks.fireAllRules();
        assertEquals( asList( 8.0 ), list );
        assertEquals( asList( 8.0 ), list2 );

        ks.insert( 1 );
        ks.fireAllRules();
        assertEquals( asList( 8.0, 10.0 ), list );
        assertEquals( asList( 8.0, 10.0 ), list2 );

        ks.insert( 2 );
        ks.fireAllRules();
        assertEquals( asList( 8.0, 10.0, 12.0 ), list );
        assertEquals( asList( 8.0, 10.0, 12.0 ), list2 );
    }

    public static class ExpectedMessage {
        String type;

        public ExpectedMessage( String type ) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public static class ExpectedMessageToRegister {

        String type;
        boolean registered = false;
        List<ExpectedMessage> msgs = new ArrayList<ExpectedMessage>();

        public ExpectedMessageToRegister( String type ) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public List<ExpectedMessage> getExpectedMessages() {
            return msgs;
        }

        public boolean isRegistered() {
            return registered;
        }

        public void setRegistered( boolean registered ) {
            this.registered = registered;
        }
    }

    @Test
    public void testReaccumulateForLeftTuple() {

        String drl1 =
                "import " + ExpectedMessage.class.getCanonicalName() + ";\n"
                + "import " + List.class.getCanonicalName() + ";\n"
                + "import " + ExpectedMessageToRegister.class.getCanonicalName() + ";\n"
                + "\n\n"

                + "rule \"Modify\"\n"
                + " when\n"
                + " $etr: ExpectedMessageToRegister(registered == false)"
                + " then\n"
                + " modify( $etr ) { setRegistered( true ) }"
                + " end\n"

                + "rule \"Collect\"\n"
                + " salience 200 \n"
                + " when\n"
                + " etr: ExpectedMessageToRegister($type: type)"
                + " $l : List( ) from collect( ExpectedMessage( type == $type ) from etr.expectedMessages )"
                + " then\n"
                + " java.lang.System.out.println( $l.size() );"
                + " end\n";

        KieSession ksession = new KieHelper().addContent( drl1, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ExpectedMessage psExpMsg1 = new ExpectedMessage( "Index" );

        ExpectedMessageToRegister etr1 = new ExpectedMessageToRegister( "Index" );
        etr1.msgs.add( psExpMsg1 );

        ksession.insert( etr1 );

        ksession.fireAllRules();
    }

    @Test
    public void testNoLoopAccumulate() {
        // DROOLS-694
        String drl1 =
                "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
                "rule NoLoopAccumulate\n" +
                "no-loop\n" +
                "when\n" +
                "    accumulate( $s : String() ; $val : count($s) )\n" +
                "    $a : AtomicInteger( )\n" +
                "then\n" +
                "    modify($a) { set($val.intValue()) }\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( drl1, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        AtomicInteger counter = new AtomicInteger( 0 );
        ksession.insert( counter );

        ksession.insert( "1" );
        ksession.fireAllRules();

        assertEquals( 1, counter.get() );

        ksession.insert( "2" );
        ksession.fireAllRules();

        assertEquals( 2, counter.get() );
    }

    private KieSession getKieSessionFromResources( String... classPathResources ) {
        KieBase kbase = loadKnowledgeBase( null, null, classPathResources );
        return kbase.newKieSession();
    }

    private KieBase loadKieBaseFromString( String... drlContentStrings ) {
        return loadKnowledgeBaseFromString( null, null, drlContentStrings );
    }

    private KieSession getKieSessionFromContentStrings( String... drlContentStrings ) {
        KieBase kbase = loadKnowledgeBaseFromString( null, null, drlContentStrings );
        return kbase.newKieSession();
    }

    @Test
    public void testAccumulateWithOr() {
        // DROOLS-839
        String drl1 =
                "import " + Converter.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  (or\n" +
                "    Integer (this == 1)\n" +
                "    Integer (this == 2)\n" +
                "  )\n" +
                "String( $length : length )\n" +
                "accumulate ( $c : Converter(), $result : sum( $c.convert($length) ) )\n" +
                "then\n" +
                "    list.add($result);\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( drl1, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );
        ksession.insert( "hello" );
        ksession.insert( new Converter() );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "hello".length(), (int)list.get(0), 0.01 );
    }

    @Test
    public void testMvelAccumulateWithOr() {
        // DROOLS-839
        String drl1 =
                "import " + Converter.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R dialect \"mvel\" when\n" +
                "  (or\n" +
                "    Integer (this == 1)\n" +
                "    Integer (this == 2)\n" +
                "  )\n" +
                "String( $length : length )\n" +
                "accumulate ( $c : Converter(), $result : sum( $c.convert($length) ) )\n" +
                "then\n" +
                "    list.add($result);\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( drl1, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Double> list = new ArrayList<Double>();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );
        ksession.insert( "hello" );
        ksession.insert( new Converter() );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "hello".length(), list.get(0), 0.01 );
    }

    public static class Converter {
        public static int convert(int i) {
            return i;
        }
    }

    @Test
    public void testNormalizeStagedTuplesInAccumulate() {
        // DROOLS-998
        String drl =
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    not( String() )\n" +
                "    accumulate(\n" +
                "        $l: Long();\n" +
                "        count($l)\n" +
                "    )\n" +
                "    ( Boolean() or not( Float() ) )\n" +
                "then\n" +
                "    list.add( \"fired\" ); \n" +
                "    insert(new String());\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
    }

    @Test
    public void testIncompatibleTypeOnAccumulateFunction() {
        // DROOLS-1243
        String drl =
                "import " + MyPerson.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $theFrom : BigDecimal() from accumulate(MyPerson( $val : age ); \n" +
                "                                          sum( $val ) )\n" +
                "then\n" +
                "  list.add($theFrom);\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertFalse( results.getMessages().isEmpty() );
    }

    @Test
    public void testIncompatibleListOnAccumulateFunction() {
        // DROOLS-1243
        String drl =
                "import " + MyPerson.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $theFrom : String() from accumulate(MyPerson( $val : age ); \n" +
                "                                          collectList( $val ) )\n" +
                "then\n" +
                "  list.add($theFrom);\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertFalse( results.getMessages().isEmpty() );
    }

    @Test
    public void testTypedSumOnAccumulate() {
        // DROOLS-1175
        String drl1 =
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $i : Integer()\n" +
                "  accumulate ( $s : String(), $result : sum( $s.length() ) )\n" +
                "then\n" +
                "  list.add($result);\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( drl1, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );
        ksession.insert( "hello" );
        ksession.insert( "hi" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "hello".length() + "hi".length(), (int)list.get(0) );
    }

    @Test
    public void testSumAccumulateOnNullValue() {
        // DROOLS-1242
        String drl1 =
                "import " + PersonWithBoxedAge.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  accumulate ( $p : PersonWithBoxedAge(), $result : sum( $p.getAge() ) )\n" +
                "then\n" +
                "  list.add($result);\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( drl1, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list", list );

        ksession.insert( new PersonWithBoxedAge("me", 30) );
        ksession.insert( new PersonWithBoxedAge("you", 40) );
        ksession.insert( new PersonWithBoxedAge("she", null) );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 70, (int)list.get(0) );
    }

    @Test
    public void testMinAccumulateOnComparable() {
        String drl1 =
                "import " + PersonWithBoxedAge.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  accumulate ( $p : PersonWithBoxedAge(), $result : min( $p ) )\n" +
                "then\n" +
                "  list.add($result);\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( drl1, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<PersonWithBoxedAge> list = new ArrayList<PersonWithBoxedAge>();
        ksession.setGlobal( "list", list );

        ksession.insert( new PersonWithBoxedAge("me", 30) );
        ksession.insert( new PersonWithBoxedAge("you", 40) );
        ksession.insert( new PersonWithBoxedAge("she", 25) );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "she", list.get(0).getName() );
    }

    @Test
    public void testMaxAccumulateOnComparable() {
        String drl1 =
                "import " + PersonWithBoxedAge.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  accumulate ( $p : PersonWithBoxedAge(), $result : max( $p ) )\n" +
                "then\n" +
                "  list.add($result);\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( drl1, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<PersonWithBoxedAge> list = new ArrayList<PersonWithBoxedAge>();
        ksession.setGlobal( "list", list );

        ksession.insert( new PersonWithBoxedAge("me", 30) );
        ksession.insert( new PersonWithBoxedAge("you", 40) );
        ksession.insert( new PersonWithBoxedAge("she", 25) );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "you", list.get(0).getName() );
    }

    public static class PersonWithBoxedAge implements Comparable<PersonWithBoxedAge> {
        private final String name;
        private final Integer age;

        public PersonWithBoxedAge( String name, Integer age ) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        @Override
        public int compareTo( PersonWithBoxedAge other ) {
            return age.compareTo( other.getAge() );
        }
    }

    @Test
    public void testTypedMaxOnAccumulate() {
        // DROOLS-1175
        String drl1 =
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $i : Integer()\n" +
                "  $result : Integer() from accumulate ( $s : String(), max( $s.length() ) )\n" +
                "then\n" +
                "  list.add($result);\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( drl1, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );
        ksession.insert( "hello" );
        ksession.insert( "hi" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "hello".length(), (int)list.get(0) );
    }

    @Test
    public void testVarianceDouble() {
        String drl =
                "import org.drools.compiler.Cheese\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  accumulate(\n" +
                "    Cheese($price : price);\n" +
                "    $result : variance($price)\n" +
                "  )\n" +
                "then\n" +
                "  list.add($result);\n" +
                "end";

        KieBase kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build();

        assertEquals(0.00, cheeseInsertsFunction(kieBase, 3, 3, 3, 3, 3), 0.01);
        assertEquals(0.80, cheeseInsertsFunction(kieBase, 4, 4, 3, 2, 2), 0.01);
        assertEquals(1.20, cheeseInsertsFunction(kieBase, 5, 3, 3, 2, 2), 0.01);
        assertEquals(2.80, cheeseInsertsFunction(kieBase, 5, 5, 2, 2, 1), 0.01);
        assertEquals(2.80, cheeseInsertsFunction(kieBase, 6, 3, 3, 2, 1), 0.01);
        assertEquals(4.40, cheeseInsertsFunction(kieBase, 6, 5, 2, 1, 1), 0.01);
        assertEquals(16.00, cheeseInsertsFunction(kieBase, 11, 1, 1, 1, 1), 0.01);
        assertEquals(36.00, cheeseInsertsFunction(kieBase, 15, 0, 0, 0, 0), 0.01);
    }

    @Test
    public void testStandardDeviationDouble() {
        String drl =
                "import org.drools.compiler.Cheese\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  accumulate(\n" +
                "    Cheese($price : price);\n" +
                "    $result : standardDeviation($price)\n" +
                "  )\n" +
                "then\n" +
                "  list.add($result);\n" +
                "end";

        KieBase kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build();

        assertEquals(0.00, cheeseInsertsFunction(kieBase, 3, 3, 3, 3, 3), 0.01);
        assertEquals(0.89, cheeseInsertsFunction(kieBase, 4, 4, 3, 2, 2), 0.01);
        assertEquals(1.10, cheeseInsertsFunction(kieBase, 5, 3, 3, 2, 2), 0.01);
        assertEquals(1.67, cheeseInsertsFunction(kieBase, 5, 5, 2, 2, 1), 0.01);
        assertEquals(1.67, cheeseInsertsFunction(kieBase, 6, 3, 3, 2, 1), 0.01);
        assertEquals(2.10, cheeseInsertsFunction(kieBase, 6, 5, 2, 1, 1), 0.01);
        assertEquals(4.00, cheeseInsertsFunction(kieBase, 11, 1, 1, 1, 1), 0.01);
        assertEquals(6.00, cheeseInsertsFunction(kieBase, 15, 0, 0, 0, 0), 0.01);
    }
    
    private double cheeseInsertsFunction(KieBase kieBase, int... prices) {
        KieSession ksession = kieBase.newKieSession();
        List<Double> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        for (int price : prices) {
            ksession.insert(new Cheese("stilton", price));
        }
        ksession.fireAllRules();
        assertEquals(1, list.size());
        double result = list.get(0);
        FactHandle triggerReverseHandle = ksession.insert(new Cheese("triggerReverse", 7));
        ksession.fireAllRules();
        ksession.delete(triggerReverseHandle);
        list.clear();
        ksession.fireAllRules();
        assertEquals(1, list.size());
        // Check that the reserse() does the opposite of the accumulate()
        assertEquals(result, list.get(0), 0.001);
        ksession.dispose();
        return list.get(0);
    }

    @Test
    public void testConcurrentLeftAndRightUpdate() {
        // DROOLS-1517
        String drl = "package P;\n"
                     + "import " + Visit.class.getCanonicalName() + ";\n"
                     + "global java.util.List list\n"
                     + "rule OvercommittedMechanic\n"
                     + "when\n"
                     + "  Visit($bucket : bucket)\n"
                     + "  $weeklyCommitment : Number() from accumulate(\n"
                     + "	     Visit($duration : duration, bucket == $bucket),\n"
                     + "	          sum($duration)\n"
                     + "      )\n"
                     + "then\n"
                     + "  list.add($weeklyCommitment);"
                     + "end";

        KieSession kieSession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        List list = new ArrayList();
        kieSession.setGlobal( "list", list );

        Visit visit1 = new Visit(1.0);
        Visit visit2 = new Visit(2.0);
        Visit visit3 = new Visit(3.0);
        Visit visit4 = new Visit(4.0);
        int bucketA = 1;
        int bucketB = 2;
        visit1.setBucket(bucketA);
        visit2.setBucket(bucketB);
        visit3.setBucket(bucketB);
        visit4.setBucket(bucketB);

        FactHandle fhVisit1 = kieSession.insert(visit1);
        FactHandle fhVisit2 = kieSession.insert(visit2);
        FactHandle fhVisit3 = kieSession.insert(visit3);
        FactHandle fhVisit4 = kieSession.insert(visit4);

        kieSession.fireAllRules();
        assertTrue( containsExactlyAndClear( list, 9.0, 9.0, 9.0, 1.0 ) );

        kieSession.update(fhVisit4, visit4);
        kieSession.update(fhVisit3, visit3.setBucket(bucketA));
        kieSession.update(fhVisit1, visit1.setBucket(bucketB));

        kieSession.fireAllRules();
        assertTrue( containsExactlyAndClear( list, 7.0, 7.0, 3.0, 7.0 ) );

        kieSession.update(fhVisit1, visit1.setBucket(bucketA));

        kieSession.fireAllRules();
        assertTrue( list.containsAll( asList( 6.0, 4.0, 6.0, 4.0 ) ) );
    }

    public static class Visit {

        private static int TAG = 1;

        private final double duration;
        private int bucket;

        private final int tag;

        public Visit(double duration) {
            this.duration = duration;
            this.tag = TAG++;
        }

        public int getBucket() {
            return bucket;
        }

        public Visit setBucket(int bucket) {
            this.bucket = bucket;
            return this;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public String toString() {
            return "Visit[" + tag + "]";
        }
    }

    private <T> boolean containsExactlyAndClear(List<T> list, T... values) {
        if (list.size() != values.length) {
            return false;
        }
        for (T value : values) {
            if (!list.remove( value )) {
                System.err.println(value + " not present");
                return false;
            }
        }
        return list.isEmpty();
    }
}
