package org.drools.integrationtests;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.CommonTestMethodBase;
import org.drools.Order;
import org.drools.OrderItem;
import org.drools.OuterClass;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.compiler.PackageBuilder;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.event.rule.AfterMatchFiredEvent;
import org.kie.event.rule.AgendaEventListener;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.rule.FactHandle;
import org.kie.runtime.rule.Match;
import org.kie.runtime.rule.QueryResults;
import org.kie.runtime.rule.Variable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class AccumulateTest extends CommonTestMethodBase {

    @Test
    public void testAccumulateModify() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateModify.drl" );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );

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

        final org.kie.runtime.rule.FactHandle[] cheeseHandles = new org.kie.runtime.rule.FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final org.kie.runtime.rule.FactHandle bobHandle = wm.insert( bob );

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
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                             results.size() );
        assertEquals( 31,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    @Test
    public void testAccumulate() throws Exception {

        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_Accumulate.drl" );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );

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

    @Test
    public void testMVELAccumulate() throws Exception {

        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateMVEL.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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

    @Test
    public void testAccumulateModifyMVEL() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateModifyMVEL.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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

        final org.kie.runtime.rule.FactHandle[] cheeseHandles = new org.kie.runtime.rule.FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final org.kie.runtime.rule.FactHandle bobHandle = wm.insert( bob );

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
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                             results.size() );
        assertEquals( 31,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    @Test
    public void testAccumulateReverseModify() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateReverseModify.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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

        final org.kie.runtime.rule.FactHandle[] cheeseHandles = new org.kie.runtime.rule.FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final org.kie.runtime.rule.FactHandle bobHandle = wm.insert( bob );

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
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

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
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    @Test
    public void testAccumulateReverseModify2() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateReverseModify2.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((Number) results.get( results.size() - 1 )).intValue() );

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
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }
    
    @Test
    public void testAccumulateReverseModifyInsertLogical2() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateReverseModifyInsertLogical2.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
        assertEquals( 31, ((Number) results.get( results.size() - 1 )).intValue() );

        // retract stilton=2 ==> bob = 15, doug = 15, !alice = 30, !carol = 61
        wm.retract(cheeseHandles[1]);
        wm.fireAllRules();
        assertEquals( 30, ((Number) results.get( results.size() - 1 )).intValue() );
    }    

    @Test
    public void testAccumulateReverseModifyMVEL() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateReverseModifyMVEL.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                             results.size() );
        assertEquals( 31,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    @Test
    public void testAccumulateReverseModifyMVEL2() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateReverseModifyMVEL2.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                      ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                      results.size() );
        assertEquals( 31,
                      ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    @Test
    public void testAccumulateWithFromChaining() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateWithFromChaining.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((List) results.get( results.size() - 1 )).size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setType( "brie" );
        wm.update( cheeseryHandle,
                   cheesery );
        wm.fireAllRules();

        // no fire
        assertEquals( 1,
                             results.size() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                             results.size() );
        assertEquals( 3,
                             ((List) results.get( results.size() - 1 )).size() );

        // ---------------- 4th scenario
        cheesery.getCheeses().remove( cheese[3] );
        wm.update( cheeseryHandle,
                   cheesery );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    @Test
    public void testMVELAccumulate2WM() throws Exception {

        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateMVEL.drl"  );
        StatefulKnowledgeSession wm1 = createKnowledgeSession( kbase );
        final List<?> results1 = new ArrayList<Object>();

        wm1.setGlobal( "results",
                       results1 );

        StatefulKnowledgeSession  wm2 = createKnowledgeSession( kbase );
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

    @Test
    public void testAccumulateInnerClass() throws Exception {

        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateInnerClass.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        wm.insert( new OuterClass.InnerClass( 10 ) );
        wm.insert( new OuterClass.InnerClass( 5 ) );

        wm.fireAllRules();

        assertEquals( 15, results.get( 0 ) );
    }

    @Test
    public void testAccumulateReturningNull() throws Exception {

        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateReturningNull.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
        final List<?> results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
    }

    @Test
    public void testAccumulateSumJava() throws Exception {
        execTestAccumulateSum( "test_AccumulateSum.drl" );
    }

    @Test
    public void testAccumulateSumMVEL() throws Exception {
        execTestAccumulateSum( "test_AccumulateSumMVEL.drl" );
    }

    @Test
    public void testAccumulateMultiPatternWithFunctionJava() throws Exception {
        execTestAccumulateSum( "test_AccumulateMultiPatternFunctionJava.drl" );
    }

    @Test
    public void testAccumulateMultiPatternWithFunctionMVEL() throws Exception {
        execTestAccumulateSum( "test_AccumulateMultiPatternFunctionMVEL.drl" );
    }

    @Test
    public void testAccumulateCountJava() throws Exception {
        execTestAccumulateCount( "test_AccumulateCount.drl" );
    }

    @Test
    public void testAccumulateCountMVEL() throws Exception {
        execTestAccumulateCount( "test_AccumulateCountMVEL.drl" );
    }

    @Test
    public void testAccumulateAverageJava() throws Exception {
        execTestAccumulateAverage( "test_AccumulateAverage.drl" );
    }

    @Test
    public void testAccumulateAverageMVEL() throws Exception {
        execTestAccumulateAverage( "test_AccumulateAverageMVEL.drl" );
    }

    @Test
    public void testAccumulateMinJava() throws Exception {
        execTestAccumulateMin( "test_AccumulateMin.drl" );
    }

    @Test
    public void testAccumulateMinMVEL() throws Exception {
        execTestAccumulateMin( "test_AccumulateMinMVEL.drl" );
    }

    @Test
    public void testAccumulateMaxJava() throws Exception {
        execTestAccumulateMax( "test_AccumulateMax.drl" );
    }

    @Test
    public void testAccumulateMaxMVEL() throws Exception {
        execTestAccumulateMax( "test_AccumulateMaxMVEL.drl" );
    }

    @Test
    public void testAccumulateMultiPatternJava() throws Exception {
        execTestAccumulateReverseModifyMultiPattern( "test_AccumulateMultiPattern.drl" );
    }

    @Test
    public void testAccumulateMultiPatternMVEL() throws Exception {
        execTestAccumulateReverseModifyMultiPattern( "test_AccumulateMultiPatternMVEL.drl" );
    }

    @Test
    public void testAccumulateCollectListJava() throws Exception {
        execTestAccumulateCollectList( "test_AccumulateCollectList.drl" );
    }

    @Test
    public void testAccumulateCollectListMVEL() throws Exception {
        execTestAccumulateCollectList( "test_AccumulateCollectListMVEL.drl" );
    }

    @Test
    public void testAccumulateCollectSetJava() throws Exception {
        execTestAccumulateCollectSet( "test_AccumulateCollectSet.drl" );
    }

    @Test
    public void testAccumulateCollectSetMVEL() throws Exception {
        execTestAccumulateCollectSet( "test_AccumulateCollectSetMVEL.drl" );
    }

    @Test
    public void testAccumulateMultipleFunctionsJava() throws Exception {
        execTestAccumulateMultipleFunctions( "test_AccumulateMultipleFunctions.drl" );
    }

    @Test
    public void testAccumulateMultipleFunctionsMVEL() throws Exception {
        execTestAccumulateMultipleFunctions( "test_AccumulateMultipleFunctionsMVEL.drl" );
    }

    @Test
    public void testAccumulateMultipleFunctionsConstraint() throws Exception {
        execTestAccumulateMultipleFunctionsConstraint( "test_AccumulateMultipleFunctionsConstraint.drl" );
    }
    
    @Test 
    public void testAccumulateWithAndOrCombinations() throws Exception {
        // JBRULES-3482
        // once this compils, update it to actually assert on correct outputs.
        
        String rule = "package org.drools.test;\n" +
                      "import org.drools.Cheese;\n" +
                      "import org.drools.Person;\n" +
                        
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

    public void execTestAccumulateSum( String fileName ) throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( fileName  );
        StatefulKnowledgeSession session = createKnowledgeSession( kbase );

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
                             ((Number) data.results.get( data.results.size() - 1 )).intValue() );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, 
                                                                             true );
        updateReferences( session,
                          data );

        // ---------------- 2nd scenario
        final int index = 1;
        data.cheese[index].setPrice( 3 );
        session.update( data.cheeseHandles[index],
                        data.cheese[index] );
        session.fireAllRules();

        assertEquals( 2,
                             data.results.size() );
        assertEquals( 20,
                             ((Number) data.results.get( data.results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        data.bob.setLikes( "brie" );
        session.update( data.bobHandle,
                        data.bob );
        session.fireAllRules();

        assertEquals( 3,
                             data.results.size() );
        assertEquals( 15,
                             ((Number) data.results.get( data.results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        session.retract( data.cheeseHandles[3] );
        session.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 3,
                             data.results.size() );

    }

    private void updateReferences( final StatefulKnowledgeSession session,
                                   final DataSet data ) {
        data.results = (List< ? >) session.getGlobal( "results" );
        for ( Iterator< ? > it = session.getObjects().iterator(); it.hasNext(); ) {
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
        KnowledgeBase kbase = loadKnowledgeBase( fileName  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((Number) results.get( results.size() - 1 )).intValue() );

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
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 3,
                             results.size() );
        assertEquals( 2,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 3,
                             results.size() );

    }

    public void execTestAccumulateAverage( String fileName ) throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( fileName  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                             results.size() );
        assertEquals( 16,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.retract( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    public void execTestAccumulateMin( String fileName ) throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( fileName  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                             results.size() );
        assertEquals( 1,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.retract( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    public void execTestAccumulateMax( String fileName ) throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( fileName  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                             results.size() );
        assertEquals( 17,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.retract( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    public void execTestAccumulateCollectList( String fileName ) throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( fileName  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((List) results.get( results.size() - 1 )).size() );

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
                             ((List) results.get( results.size() - 1 )).size() );

        // ---------------- 3rd scenario
        wm.retract( cheeseHandles[3] );
        wm.retract( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    public void execTestAccumulateCollectSet( String fileName ) throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( fileName  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((Set) results.get( results.size() - 1 )).size() );

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
                             ((Set) results.get( results.size() - 1 )).size() );

        // ---------------- 3rd scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();
        // fire again
        assertEquals( 3,
                             results.size() );
        assertEquals( 3,
                             ((Set) results.get( results.size() - 1 )).size() );

        // ---------------- 4rd scenario
        wm.retract( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 3,
                             results.size() );

    }

    public void execTestAccumulateReverseModifyMultiPattern( String fileName ) throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( fileName  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        assertEquals( 2,
                             results.size() );
        assertEquals( 39,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        assertEquals( 2,
                             results.size() );

    }

    @Test
    public void testAccumulateWithPreviouslyBoundVariables() throws Exception {

        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulatePreviousBinds.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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

    @Test
    public void testAccumulateMVELWithModify() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateMVELwithModify.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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

    @Test
    public void testAccumulateGlobals() throws Exception {

        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_AccumulateGlobals.drl"  );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
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

    @Test
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

    @Test
    public void testAccumulateZeroParams() {
        String rule = "rule fromIt\n" +
                      "when\n" +
                      "    Number( $c: intValue ) from accumulate( Integer(), count( ) )\n" +
                      "then\n" +
                      "    System.out.println( \"got \" + $c );\n" +
                      "end";

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

    public void execTestAccumulateMultipleFunctions( String fileName ) throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( fileName );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

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
        Mockito.verify( ael ).afterMatchFired(cap.capture());

        Match activation = cap.getValue().getMatch();
        assertThat( ((Number) activation.getDeclarationValue( "$sum" )).intValue(),
                    is( 18 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$min" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$avg" )).intValue(),
                    is( 6 ) );

        Mockito.reset( ael );
        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        ksession.update( cheeseHandles[index],
                         cheese[index] );
        ksession.fireAllRules();

        Mockito.verify( ael ).afterMatchFired(cap.capture());

        activation = cap.getValue().getMatch();
        assertThat( ((Number) activation.getDeclarationValue( "$sum" )).intValue(),
                    is( 24 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$min" )).intValue(),
                    is( 5 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$avg" )).intValue(),
                    is( 8 ) );

        Mockito.reset( ael );
        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        ksession.update( bobHandle,
                         bob );
        ksession.fireAllRules();

        Mockito.verify( ael ).afterMatchFired(cap.capture());

        activation = cap.getValue().getMatch();
        assertThat( ((Number) activation.getDeclarationValue( "$sum" )).intValue(),
                    is( 32 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$min" )).intValue(),
                    is( 15 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$avg" )).intValue(),
                    is( 16 ) );

        Mockito.reset( ael );
        // ---------------- 4th scenario
        ksession.retract( cheeseHandles[3] );
        ksession.fireAllRules();

        Mockito.verify( ael ).afterMatchFired(cap.capture());

        activation = cap.getValue().getMatch();
        assertThat( ((Number) activation.getDeclarationValue( "$sum" )).intValue(),
                    is( 17 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$min" )).intValue(),
                    is( 17 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$avg" )).intValue(),
                    is( 17 ) );
    }

    public void execTestAccumulateMultipleFunctionsConstraint( String fileName ) throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( fileName );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

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
        Mockito.verify( ael ).afterMatchFired(cap.capture());

        Match activation = cap.getValue().getMatch();
        assertThat( ((Number) activation.getDeclarationValue( "$sum" )).intValue(),
                    is( 18 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$min" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$avg" )).intValue(),
                    is( 6 ) );

        Mockito.reset( ael );
        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        ksession.update( cheeseHandles[index],
                         cheese[index] );
        ksession.fireAllRules();

        Mockito.verify( ael, Mockito.never() ).afterMatchFired(Mockito.any(AfterMatchFiredEvent.class));

        Mockito.reset( ael );
        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        ksession.update( bobHandle,
                         bob );
        ksession.fireAllRules();

        Mockito.verify( ael ).afterMatchFired(cap.capture());

        activation = cap.getValue().getMatch();
        assertThat( ((Number) activation.getDeclarationValue( "$sum" )).intValue(),
                    is( 20 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$min" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) activation.getDeclarationValue( "$avg" )).intValue(),
                    is( 10 ) );
        
        ksession.dispose();

    }

    public static class DataSet {
        public Cheese[]     cheese;
        public FactHandle[] cheeseHandles;
        public Person       bob;
        public FactHandle   bobHandle;
        public List< ? >    results;
    }

    @Test
    public void testAccumulateMinMax() throws Exception {
        String drl = "package org.drools.test \n" +
                     "import org.drools.Cheese \n" +
                     "global java.util.List results \n " +
                     "rule minMax \n" +
                     "when \n" +
                     "    accumulate( Cheese( $p: price ), $min: min($p), $max: max($p) ) \n" +
                     "then \n" +
                     "    results.add($min); results.add($max); \n" +
                     "end \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

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

        for (Cheese aCheese : cheese) {
            ksession.insert(aCheese);
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
    
    @Test
    public void testAccumulateCE() throws Exception {
        String drl = "package org.drools\n" +
        		     "global java.util.List results\n" +
        		     "rule \"ocount\"\n" + 
        		     "when\n" + 
        		     "    accumulate( Cheese(), $c: count(1) )\n" + 
        		     "then\n" + 
        		     "    results.add( $c + \" facts\" );\n" + 
        		     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

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

        for (Cheese aCheese : cheese) {
            ksession.insert(aCheese);
        }

        // ---------------- 1st scenario
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );
        assertEquals( "7 facts", 
                      results.get( 0 ) );
    }





    @Test
    public void testAccumulateAndRetract() {

        String drl = "package org.drools;\n" +
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
                "                    $b : String( ) from $l\n" +
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()),
                ResourceType.DRL );
            if (kbuilder.hasErrors()) {
                fail(kbuilder.getErrors().toString());
            }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();

        kb.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ks = createKnowledgeSession(kb);

        ArrayList resList = new ArrayList();
            ks.setGlobal("list",resList);

        ArrayList<String> list = new ArrayList<String>();
            list.add("x");
            list.add("y");
            list.add("z");

        ks.insert(list);
        ks.fireAllRules();

        assertEquals(3L, resList.get(0));

    }

    @Test
    public void testAccumulateWithNull() {
        String drl = "rule foo\n" +
                "when\n" +
                "Object() from accumulate( Object(),\n" +
                "init( Object res = null; )\n" +
                "action( res = null; )\n" +
                "result( res ) )\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.fireAllRules();
        ksession.dispose();
    }

    public static class MyObj {
        public static class NestedObj {
            public long value;

            public NestedObj(long value) {
                this.value = value;
            }
        }

        private final NestedObj nestedObj;

        public MyObj(long value) {
            nestedObj = new NestedObj(value);
        }

        public NestedObj getNestedObj() {
            return nestedObj;
        }
    }

    @Test
    public void testAccumulateWithBoundExpression() {
        String drl = "package org.drools;\n" +
                "import org.drools.integrationtests.AccumulateTest.MyObj;\n" +
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List<Number> results = new ArrayList<Number>();
        ksession.setGlobal( "results",
                results );
        ksession.fireAllRules();
        ksession.dispose();
        assertEquals( 1,
                results.size() );
        assertEquals( 9.0,
                results.get( 0 ) );
    }

    @Test(timeout = 5000)
    public void testInfiniteLoopAddingPkgAfterSession() throws Exception {
        // JBRULES-3488
        String rule = "package org.drools.test;\n" +
        "import org.drools.integrationtests.AccumulateTest.Triple;\n" +
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

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( rule ) ),
                ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        // To reproduce, Need to have 3 object asserted (not less) :
        ksession.insert(new Triple("<http://deductions.sf.net/samples/princing.n3p.n3#CN1>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "200"));
        ksession.insert(new Triple("<http://deductions.sf.net/samples/princing.n3p.n3#CN2>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "100"));
        ksession.insert(new Triple("<http://deductions.sf.net/samples/princing.n3p.n3#CN3>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "100"));

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        ksession.fireAllRules();
    }

    public static class Triple {
        private String subject;
        private String predicate;
        private String object;

        /** for javabeans */
        public Triple() {}

        public Triple(String subject, String predicate, String object) {
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

    @Test
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
                "    $holders : collectList( $holder )\n" +
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
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        ksession.fireAllRules();

        QueryResults res = ksession.getQueryResults( "getResults", "1", Variable.v );
        assertEquals( 1, res.size() );

        Object o = res.iterator().next().get( "$holders" );
        assertTrue( o instanceof List );
        assertEquals( 1, ((List) o).size() );
    }

    @Test
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Map res = new HashMap();
        ksession.setGlobal( "map", res );
        ksession.fireAllRules();

        for ( int j = 0; j < 33; j++ ) {
            ksession.insert(1.0 * j);
            ksession.fireAllRules();
        }
    }

    @Test 
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Map res = new HashMap();
        ksession.setGlobal( "map", res );
        ksession.fireAllRules();

        for ( int j = 0; j < 33; j++ ) {
            ksession.getWorkingMemoryEntryPoint( "data" ).insert(1.0 * j);
            ksession.fireAllRules();
        }
    }

    @Test 
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Map res = new HashMap();
        ksession.setGlobal( "map", res );
        ksession.fireAllRules();

        for ( int j = 0; j < 33; j++ ) {
            ksession.getWorkingMemoryEntryPoint( "data" ).insert(1.0 * j);
            ksession.fireAllRules();
        }
    }

    @Test
    public void test2AccumulatesWithOr() throws Exception {
        // JBRULES-3538
        String str =
                "import java.util.*;\n" +
                "import org.drools.integrationtests.AccumulateTest.MyPerson;\n" +
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
                "        MyPerson(name == \"Jos Jr Jr\")\n" +

                "        or\n" +
                "        MyPerson(name == \"Jos\")\n" +

                "    then\n" +
                "        System.out.println(\"hello\");\n" +
                "end\n";

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(ResourceFactory.newByteArrayResource(str.getBytes()));
        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackages(builder.getPackages());
        StatelessSession ss = rb.newStatelessSession();
        ss.execute(new Object[]{
                new MyPerson("John", 20, Arrays.asList(
                        new MyPerson("John Jr 1st", 10, Arrays.asList(new MyPerson("John Jr Jr", 4, Collections.<MyPerson>emptyList()))),
                        new MyPerson("John Jr 2nd", 8, Collections.<MyPerson>emptyList())))
                , new MyPerson("Jeff", 30, Arrays.asList(
                new MyPerson("Jeff Jr 1st", 10, Collections.<MyPerson>emptyList()),
                new MyPerson("Jeff Jr 2nd", 8, Collections.<MyPerson>emptyList())))
        });
    }

    public static class MyPerson {
        public MyPerson(String name, Integer age, Collection<MyPerson> kids) {
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

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Collection<MyPerson> getKids() {
            return kids;
        }

        public void setKids(Collection<MyPerson> kids) {
            this.kids = kids;
        }
    }
}
