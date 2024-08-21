/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.mvel.compiler.Cell;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.FactA;
import org.drools.mvel.compiler.Foo;
import org.drools.mvel.compiler.Message;
import org.drools.mvel.compiler.Neighbor;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.PersonInterface;
import org.drools.mvel.compiler.Pet;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ExecutionFlowControlTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ExecutionFlowControlTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test(timeout = 10000)
    public void testSalienceIntegerAndLoadOrder() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_salienceIntegerRule.drl");
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession();
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final PersonInterface person = new Person("Edson", "cheese");
            ksession.insert(person);

            ksession.fireAllRules();

            assertThat(list.size()).as("Three rules should have been fired").isEqualTo(3);
            assertThat(list.get(0)).as("Rule 4 should have been fired first").isEqualTo("Rule 4");
            assertThat(list.get(1)).as("Rule 2 should have been fired second").isEqualTo("Rule 2");
            assertThat(list.get(2)).as("Rule 3 should have been fired third").isEqualTo("Rule 3");
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testSalienceExpression() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_salienceExpressionRule.drl");

        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final PersonInterface person10 = new Person( "bob", "cheese", 10 );
        ksession.insert( person10 );

        final PersonInterface person20 = new Person( "mic", "cheese", 20 );
        ksession.insert( person20 );

        ksession.fireAllRules();

        assertThat(list.size()).as("Two rules should have been fired").isEqualTo(2);
        assertThat(list.get(0)).as("Rule 3 should have been fired first").isEqualTo("Rule 3");
        assertThat(list.get(1)).as("Rule 2 should have been fired second").isEqualTo("Rule 2");
    }
    
    @Test
    public void testSalienceExpressionWithOr() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "import " + FactA.class.getCanonicalName() + "\n"
                      + "import " + Foo.class.getCanonicalName() + "\n"
                      + "import " + Pet.class.getCanonicalName() + "\n"
                      + "rule r1 salience (f1.field2)\n"
                      + "when\n"                      
                      + "    foo: Foo()\n" 
                      + "    ( Pet()  and f1 : FactA( field1 == 'f1') ) or \n"
                      + "    f1 : FactA(field1 == 'f2') \n"                      
                      + "then\n"
                      + "    list.add( f1 );\n"
                      + "    foo.setId( 'xxx' );\n"
                      + "end\n" + "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, text);
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );        
        ksession.insert ( new Foo(null, null) );
        ksession.insert ( new Pet(null) );
        
        FactA fact1 = new FactA();
        fact1.setField1( "f1" );
        fact1.setField2( 10 );
        
        FactA fact2 = new FactA();
        fact2.setField1( "f1" );
        fact2.setField2( 30 );
        
        FactA fact3 = new FactA();
        fact3.setField1( "f2" );
        fact3.setField2( 20 );
        
        ksession.insert( fact1 );
        ksession.insert( fact2 );
        ksession.insert( fact3 );
        
        ksession.fireAllRules();
        System.out.println( list );

        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(fact2);
        assertThat(list.get(1)).isEqualTo(fact3);
        assertThat(list.get(2)).isEqualTo(fact1);     
    }

    @Test
    public void testSalienceMinInteger() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "rule a\n"
                      + "when\n"
                      + "then\n"
                      + "    list.add( \"a\" );\n" + "end\n" + "\n"
                      + "rule b\n"
                      + "   salience ( Integer.MIN_VALUE )\n" + "when\n"
                      + "then\n"
                      + "    list.add( \"b\" );\n" + "end\n" + "\n"
                      + "rule c\n"
                      + "when\n"
                      + "then\n"
                      + "    list.add( \"c\" );\n"
                      + "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, text);

        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertThat(list.get(2)).isEqualTo("b");
    }

    @Test
    public void testLoadOrderConflictResolver() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "rule a\n"
                      + "when\n"
                      + "      s : String( this == 'a') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "end\n" + "\n"

                      + "rule b\n"
                      + "when\n"
                      + "      s : String( this == 'b') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "end\n" + "\n"

                      + "rule c\n"
                      + "when\n"
                      + "      s : String( this == 'c') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "    insert( new String(\"a\") ); \n"
                      + "    insert( new String(\"b\") ); \n"
                      + "end\n" + "\n"

                      + "rule d\n"
                      + "when\n"
                      + "    s : String( this == 'd') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "    insert( new String(\"b\") ); \n"
                      + "    insert( new String(\"a\") ); \n"
                      + "end\n" + "\n"
                      + "rule e\n"
                      + "when\n"
                      + "    s : String( this == 'e') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "end\n" + "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, text);

        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( "a" );
        ksession.insert( "b" );
        ksession.insert( "c" );
        ksession.insert( "d" );
        ksession.insert( "e" );
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(9);
        assertThat(list.get(0)).isEqualTo("a");
        assertThat(list.get(1)).isEqualTo("b");
        assertThat(list.get(2)).isEqualTo("c");
        assertThat(list.get(3)).isEqualTo("a");
        assertThat(list.get(4)).isEqualTo("b");
        assertThat(list.get(5)).isEqualTo("d");
        assertThat(list.get(6)).isEqualTo("a");
        assertThat(list.get(7)).isEqualTo("b");
        assertThat(list.get(8)).isEqualTo("e");
    }


    
    @Test
    public void testEnabledExpressionWithOr() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "import " + FactA.class.getCanonicalName() + "\n"
                      + "import " + Foo.class.getCanonicalName() + "\n"
                      + "import " + Pet.class.getCanonicalName() + "\n"
                      + "rule r1 salience(f1.field2) enabled(f1.field2 >= 20)\n"
                      + "when\n"                      
                      + "    foo: Foo()\n" 
                      + "    ( Pet()  and f1 : FactA( field1 == 'f1') ) or \n"
                      + "    f1 : FactA(field1 == 'f2') \n"                      
                      + "then\n"
                      + "    list.add( f1 );\n"
                      + "    foo.setId( 'xxx' );\n"
                      + "end\n" + "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, text);

        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );        
        ksession.insert ( new Foo(null, null) );
        ksession.insert ( new Pet(null) );
        
        FactA fact1 = new FactA();
        fact1.setField1( "f1" );
        fact1.setField2( 10 );
        
        FactA fact2 = new FactA();
        fact2.setField1( "f1" );
        fact2.setField2( 30 );
        
        FactA fact3 = new FactA();
        fact3.setField1( "f2" );
        fact3.setField2( 20 );
        
        ksession.insert( fact1 );
        ksession.insert( fact2 );
        ksession.insert( fact3 );
        
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(fact2);
        assertThat(list.get(1)).isEqualTo(fact3);   
    }    

    @Test
    public void testNoLoop() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "no-loop.drl");
        KieSession ksession = kbase.newKieSession();;

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        ksession.insert( brie );

        ksession.fireAllRules();

        assertThat(list.size()).as("Should not loop  and thus size should be 1").isEqualTo(1);

    }

    @Test
    public void testNoLoopWithModify() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "no-loop_with_modify.drl");

        KieSession ksession =  kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        ksession.insert( brie );

        ksession.fireAllRules();

        assertThat(list.size()).as("Should not loop  and thus size should be 1").isEqualTo(1);
        assertThat(brie.getPrice()).isEqualTo(50);

    }

    @Test
    public void testLockOnActive() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_LockOnActive.drl");

        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        // AgendaGroup "group1" is not active, so should receive activation
        final Cheese brie12 = new Cheese( "brie", 12 );
        ksession.insert( brie12 );
        ((InternalWorkingMemory)ksession).flushPropagations();
        InternalAgenda agenda = ((InternalAgenda) ksession.getAgenda());
        final InternalAgendaGroup group1 = agenda.getAgendaGroupsManager().getAgendaGroup( "group1" );
        assertThat(group1.size()).isEqualTo(1);

        ksession.getAgenda().getAgendaGroup("group1").setFocus( );
        // AgendaqGroup "group1" is now active, so should not receive activations
        final Cheese brie10 = new Cheese( "brie", 10 );
        ksession.insert( brie10 );
        assertThat(group1.size()).isEqualTo(1);

        final Cheese cheddar20 = new Cheese( "cheddar", 20 );
        ksession.insert( cheddar20 );
        final InternalAgendaGroup group2 = agenda.getAgendaGroupsManager().getAgendaGroup( "group1" );
        assertThat(group2.size()).isEqualTo(1);

        agenda.getAgendaGroupsManager().setFocus(group2);
        final Cheese cheddar17 = new Cheese( "cheddar", 17 );
        ksession.insert( cheddar17 );
        assertThat(group2.size()).isEqualTo(1);
    }

    @Test
    public void testLockOnActiveForMain() {
        String str = "";
        str += "package org.kie \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    lock-on-active true \n";
        str += "when \n";
        str += "    $str : String() \n";
        str += "then \n";
        str += "    list.add( $str ); \n";
        str += "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( "hello1" );
        ksession.insert( "hello2" );
        ksession.insert( "hello3" );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(3);

        ksession.insert( "hello4" );
        ksession.insert( "hello5" );
        ksession.insert( "hello6" );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(6);
    }

    @Test
    public void testLockOnActiveForMainWithHalt() {
        String str = "";
        str += "package org.kie \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    lock-on-active true \n";
        str += "when \n";
        str += "    $str : String() \n";
        str += "then \n";
        str += "    list.add( $str ); \n";
        str += "    if ( list.size() == 2 ) {\n";
        str += "        drools.halt();\n";
        str += "    }";
        str += "end \n";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( "hello1" );
        ksession.insert( "hello2" );
        ksession.insert( "hello3" );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(2);

        // because we have halted, the next 3 will be ignored, but it will still
        // fire the remaing 3rd activation from previous asserts
        ksession.insert( "hello4" );
        ksession.insert( "hello5" );
        ksession.insert( "hello6" );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    public void testLockOnActiveWithModify() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_LockOnActiveWithUpdate.drl");
        KieSession ksession =  kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 13 );

        final Person bob = new Person( "bob" );
        bob.setCheese( brie );

        final Person mic = new Person( "mic" );
        mic.setCheese( brie );

        final Person mark = new Person( "mark" );
        mark.setCheese( brie );

        final FactHandle brieHandle = ksession.insert(brie);
        ksession.insert( bob );
        ksession.insert( mic );
        ksession.insert( mark );

        InternalWorkingMemory wm = ( InternalWorkingMemory )ksession;
        wm.flushPropagations();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();
        final InternalAgendaGroup group1 = agenda.getAgendaGroupsManager().getAgendaGroup( "group1" );
        agenda.getAgendaGroupsManager().setFocus( group1 );
        assertThat(group1.size()).isEqualTo(1);
        RuleAgendaItem ruleItem1 = group1.getActivations().iterator().next();
        ruleItem1.getRuleExecutor().evaluateNetwork(wm.getAgenda());
        assertThat(ruleItem1.getRuleExecutor().getActiveMatches().size()).isEqualTo(3);

        ruleItem1.getRuleExecutor().fire(agenda);
        assertThat(group1.size()).isEqualTo(1);
        assertThat(ruleItem1.getRuleExecutor().getActiveMatches().size()).isEqualTo(2);

        ksession.update( brieHandle, brie );
        assertThat(group1.size()).isEqualTo(1);
        ruleItem1.getRuleExecutor().evaluateNetwork(wm.getAgenda());
        assertThat(ruleItem1.getRuleExecutor().getActiveMatches().size()).isEqualTo(2);

        InternalAgendaGroup group2 = agenda.getAgendaGroupsManager().getAgendaGroup( "group2" );
        agenda.getAgendaGroupsManager().setFocus( group2);
        assertThat(group2.size()).isEqualTo(1);
        RuleAgendaItem ruleItem2 = group2.getActivations().iterator().next();
        ruleItem2.getRuleExecutor().evaluateNetwork(wm.getAgenda());
        assertThat(ruleItem2.getRuleExecutor().getActiveMatches().size()).isEqualTo(3);

        ruleItem2.getRuleExecutor().fire(agenda);
        assertThat(group2.size()).isEqualTo(1);
        assertThat(ruleItem2.getRuleExecutor().getActiveMatches().size()).isEqualTo(2);

        ksession.update( brieHandle, brie );
        assertThat(group2.size()).isEqualTo(1);
        assertThat(ruleItem2.getRuleExecutor().getActiveMatches().size()).isEqualTo(2);
    }

    @Test
    public void testLockOnActiveWithModify2() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_LockOnActiveWithModify.drl");
        KieSession ksession = kbase.newKieSession();

        // populating working memory
        final int size = 3;

        Cell[][] cells = new Cell[size][];
        FactHandle[][] handles = new FactHandle[size][];
        for ( int row = 0; row < size; row++ ) {
            cells[row] = new Cell[size];
            handles[row] = new FactHandle[size];
            for ( int col = 0; col < size; col++ ) {
                cells[row][col] = new Cell( Cell.DEAD, row, col );
                handles[row][col] = ksession.insert(cells[row][col]);
                if ( row >= 1 && col >= 1 ) {
                    // northwest
                    ksession.insert( new Neighbor( cells[row - 1][col - 1],
                                                  cells[row][col] ) );
                    ksession.insert( new Neighbor( cells[row][col],
                                                  cells[row - 1][col - 1] ) );
                }
                if ( row >= 1 ) {
                    // north
                    ksession.insert( new Neighbor( cells[row - 1][col],
                                                  cells[row][col] ) );
                    ksession.insert( new Neighbor( cells[row][col],
                                                  cells[row - 1][col] ) );
                }
                if ( row >= 1 && col < (size - 1) ) {
                    // northeast
                    ksession.insert( new Neighbor( cells[row - 1][col + 1],
                                                  cells[row][col] ) );
                    ksession.insert( new Neighbor( cells[row][col],
                                                  cells[row - 1][col + 1] ) );
                }
                if ( col >= 1 ) {
                    // west
                    ksession.insert( new Neighbor( cells[row][col - 1],
                                                  cells[row][col] ) );
                    ksession.insert( new Neighbor( cells[row][col],
                                                  cells[row][col - 1] ) );
                }
            }
        }

        ksession.getAgenda().getAgendaGroup("calculate").clear();

         // now, start playing
        int fired = ksession.fireAllRules( 100 );
        assertThat(fired).isEqualTo(0);

        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        fired = ksession.fireAllRules( 100 );
        // logger.writeToDisk();
        assertThat(fired).isEqualTo(0);
        assertThat(((InternalAgenda) ksession.getAgenda()).getFocusName()).isEqualTo("MAIN");

        // on the fifth day God created the birds and sea creatures
        cells[0][0].setState( Cell.LIVE );
        ksession.update( handles[0][0], cells[0][0] );
        ksession.getAgenda().getAgendaGroup("birth").setFocus();
        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        fired = ksession.fireAllRules( 100 );

        // logger.writeToDisk();
        int[][] expected = new int[][]{{0, 1, 0}, {1, 1, 0}, {0, 0, 0}};
        assertEqualsMatrix( size, cells, expected );
        assertThat(((InternalAgenda) ksession.getAgenda()).getFocusName()).isEqualTo("MAIN");

        // on the sixth day God created the animals that walk over the land and
        // the Man
        cells[1][1].setState( Cell.LIVE );
        ksession.update( handles[1][1], cells[1][1] );
        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        ksession.fireAllRules( 100 );
        // logger.writeToDisk();

        expected = new int[][]{{1, 2, 1}, {2, 1, 1}, {1, 1, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertThat(((InternalAgenda) ksession.getAgenda()).getFocusName()).isEqualTo("MAIN");

        ksession.getAgenda().getAgendaGroup("birth").setFocus();
        ksession.fireAllRules( 100 );
        expected = new int[][]{{1, 2, 1}, {2, 1, 1}, {1, 1, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertThat(((InternalAgenda) ksession.getAgenda()).getFocusName()).isEqualTo("MAIN");

        System.out.println( "--------" );
        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        ksession.fireAllRules( 100 );
        // logger.writeToDisk();
        // printMatrix( cells );

        expected = new int[][]{{3, 3, 2}, {3, 3, 2}, {2, 2, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertThat(((InternalAgenda) ksession.getAgenda()).getFocusName()).isEqualTo("MAIN");
        System.out.println( "--------" );

        // on the seventh day, while God rested, man start killing them all
        cells[0][0].setState( Cell.DEAD );
        ksession.update( handles[0][0], cells[0][0] );
        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        ksession.fireAllRules( 100 );

        expected = new int[][]{{3, 2, 2}, {2, 2, 2}, {2, 2, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertThat(((InternalAgenda) ksession.getAgenda()).getFocusName()).isEqualTo("MAIN");

    }

    // private void printMatrix(Cell[][] cells) {
    // System.out.println("----------");
    // for( int row = 0; row < cells.length; row++) {
    // for( int col = 0; col < cells[row].length; col++ ) {
    // System.out.print( cells[row][col].getValue() +
    // ((cells[row][col].getState()==Cell.LIVE)?"L  ":".  ") );
    // }
    // System.out.println();
    // }
    // System.out.println("----------");
    // }

    private void assertEqualsMatrix(final int size,
                                    Cell[][] cells,
                                    int[][] expected) {
        for ( int row = 0; row < size; row++ ) {
            for ( int col = 0; col < size; col++ ) {
                assertThat(cells[row][col].getValue()).as("Wrong value at " + row + "," + col + ": ").isEqualTo(expected[row][col]);
            }
        }
    }

    @Test
    public void testAgendaGroups() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_AgendaGroups.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        ksession.insert( brie );

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(7);

        assertThat(list.get(0)).isEqualTo("group3");
        assertThat(list.get(1)).isEqualTo("group4");
        assertThat(list.get(2)).isEqualTo("group3");
        assertThat(list.get(3)).isEqualTo("MAIN");
        assertThat(list.get(4)).isEqualTo("group1");
        assertThat(list.get(5)).isEqualTo("group1");
        assertThat(list.get(6)).isEqualTo("MAIN");

        ksession.getAgenda().getAgendaGroup( "group2" ).setFocus();
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(8);
        assertThat(list.get(7)).isEqualTo("group2");
    }

    @Test
    public void testActivationGroups() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_ActivationGroups.drl");
        KieSession ksession = kbase.newKieSession();


        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        ksession.insert( brie );

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo("rule0");
        assertThat(list.get(1)).isEqualTo("rule2");
    }
    
    public static class Holder {
        private Integer val;
        private String  outcome;

        public Holder(Integer val) {
            this.val = val;
        }

        public void setValue(Integer val) {
            this.val = val;
        }

        public Integer getValue() {
            return this.val;
        }

        public void setOutcome(String outcome) {
            this.outcome = outcome;
        }

        public String getOutcome() {
            return this.outcome;
        }
    }

    @Test
    // JBRULES-2398
    public void
    testActivationGroupWithTroubledSyntax() {
    String str = "package BROKEN_TEST;\n" + "import "
                 + Holder.class.getCanonicalName() + ";\n"
                 + "rule \"_12\"\n"
                 + "    \n"
                 + "    salience 3\n"
                 + "    activation-group \"BROKEN\"\n"
                 + "    when\n"
                 + "        $a : Holder(value in (0))\n"
                 + "    then\n"
                 + "        System.out.println(\"setting 0\");\n"
                 + "        $a.setOutcome(\"setting 0\");\n"
                 + "end\n" + "\n"
                 + "rule \"_13\"\n"
                 + "    \n"
                 + "    salience 2\n"
                 + "    activation-group \"BROKEN\"\n"
                 + "    when\n"
                 + "        $a : Holder(value in (1))\n"
                 + "    then\n"
                 + "        System.out.println(\"setting 1\");\n"
                 + "        $a.setOutcome(\"setting 1\");\n"
                 + "end\n" + "\n"
                 + "rule \"_22\"\n"
                 + "    \n" + "    salience 1\n"
                 + "    activation-group \"BROKEN\"\n"
                 + "    when\n"
                 + "        $a : Holder(value == null)\n"
                 + "    then\n"
                 + "        System.out.println(\"setting null\");\n"
                 + "        $a.setOutcome(\"setting null\");\n"
                 + "end\n" + "\n"
                 + "";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Holder inrec = new Holder( 1 );
        System.out.println( "Holds: " + inrec.getValue() );
        ksession.insert( inrec );
        ksession.fireAllRules();
        assertThat(ksession.getFactHandles().size()).isEqualTo(1);
        assertThat(inrec.getOutcome()).isEqualTo("setting 1");

        ksession.dispose();
        ksession = kbase.newKieSession();
        inrec = new Holder( null );
        System.out.println( "Holds: " + inrec.getValue() );
        ksession.insert( inrec );
        ksession.fireAllRules();
        assertThat(ksession.getFactHandles().size()).isEqualTo(1);
        assertThat(inrec.getOutcome()).isEqualTo("setting null");

        ksession.dispose();
        ksession = kbase.newKieSession();
        inrec = new Holder( 0 );
        System.out.println( "Holds: " + inrec.getValue() );
        ksession.insert( inrec );
        ksession.fireAllRules(); // appropriate rule is not fired!
        assertThat(ksession.getFactHandles().size()).isEqualTo(1);
        assertThat(inrec.getOutcome()).isEqualTo("setting 0");
    }

    @Test
    public void testInsertRetractNoloop() throws Exception {
        // read in the source
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_Insert_Retract_Noloop.drl");
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Cheese( "stilton", 15 ) );

        ksession.fireAllRules();
        assertThat(ksession.getObjects().size()).isEqualTo(0);
    }

    @Test
    public void testUpdateNoLoop() throws Exception {
        // JBRULES-780, throws a NullPointer or infinite loop if there is an
        // issue
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_UpdateNoloop.drl");

        KieSession ksession = kbase.newKieSession();

        Cheese cheese = new Cheese( "stilton", 15 );
        ksession.insert( cheese  );

        ksession.fireAllRules();

        assertThat(cheese.getPrice()).isEqualTo(14);
    }

    @Test
    public void testUpdateActivationCreationNoLoop() throws Exception {
        // JBRULES-787, no-loop blocks all dependant tuples for update
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_UpdateActivationCreationNoLoop.drl");

        KieSession ksession = kbase.newKieSession();

        final List created = new ArrayList();
        final List cancelled = new ArrayList();
        final AgendaEventListener l = new DefaultAgendaEventListener() {
            @Override
            public void matchCreated(MatchCreatedEvent event) {
                created.add( event );
            }

            @Override
            public void matchCancelled(MatchCancelledEvent event) {
                cancelled.add( event );
            }
        };

        ksession.addEventListener( l );

        final Cheese stilton = new Cheese( "stilton", 15 );
        final FactHandle stiltonHandle = ksession.insert( stilton );

        final Person p1 = new Person( "p1" );
        p1.setCheese( stilton );
        ksession.insert( p1 );

        final Person p2 = new Person( "p2" );
        p2.setCheese( stilton );
        ksession.insert( p2 );

        final Person p3 = new Person( "p3" );
        p3.setCheese( stilton );
        ksession.insert( p3 );

        ksession.fireAllRules();

        assertThat(created.size()).isEqualTo(3);
        assertThat(cancelled.size()).isEqualTo(0);

        // simulate a modify inside a consequence
        ksession.update( stiltonHandle, stilton );

        // with true modify, no reactivations should be triggered
        assertThat(created.size()).isEqualTo(3);
        assertThat(cancelled.size()).isEqualTo(0);
    }

    @Test
    public void testRuleFlowGroup() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "ruleflowgroup.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( "Test" );
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);

        ((InternalAgenda)ksession.getAgenda()).activateRuleFlowGroup("Group1");
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testRuleFlowGroupDeactivate() throws Exception {
        // need to make eager, for cancel to work, (mdp)
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "ruleflowgroup2.drl");

        KieSession ksession = kbase.newKieSession();


        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( "Test" );
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);
        assertThat(((InternalRuleFlowGroup) ksession.getAgenda().getRuleFlowGroup("Group1")).size()).isEqualTo(2);

        ((InternalAgenda)ksession.getAgenda()).activateRuleFlowGroup("Group1");
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(0);
    }

    @Test(timeout=10000)
    public void testRuleFlowGroupInActiveMode() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "ruleflowgroup.drl");

        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );
        
        final AtomicBoolean fired = new AtomicBoolean(false);
        ksession.addEventListener(new org.kie.api.event.rule.DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                synchronized( fired ) {
                    fired.set(true);
                    fired.notifyAll();
                }
            }
        });

        new Thread(ksession::fireUntilHalt).start();
        try {
            ksession.insert( "Test" );
            assertThat(list.size()).isEqualTo(0);

            ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup("Group1");

            synchronized( fired ) {
                if( !fired.get() ) {
                    fired.wait();
                }
            }

            assertThat(list.size()).isEqualTo(1);
        } finally {
            ksession.halt();
            ksession.dispose();
        }
    }

    @Test
    public void testDateEffective() throws Exception {
        // read in the source
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_EffectiveDate.drl");

        KieSession ksession = kbase.newKieSession();


        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        // go !
        final Message message = new Message( "hola" );
        ksession.insert( message );
        ksession.fireAllRules();
        assertThat(message.isFired()).isFalse();
    }

    @Test
    public void testNullPointerOnModifyWithLockOnActive() {
        // JBRULES-3234

        String str = "package org.kie.test \n"
                     + "import org.drools.mvel.compiler.Person; \n"
                     + "rule 'Rule 1' agenda-group 'g1' lock-on-active	when \n"
                     + "		$p : Person( age != 35 ) \n"
                     + "	then \n"
                     + "		modify( $p ) { setAge( 35 ) };	\n"
                     + "end \n"
                     + "rule 'Rule 2' agenda-group 'g1' no-loop when \n"
                     + "		$p:  Person( age == 35) \n"
                     + "	then \n"
                     + "		modify( $p ) { setAge( 36 ) }; \n"
                     + "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person p = new Person( "darth", 36 );
        FactHandle fh = ksession.insert(p);

        ksession.getAgenda().getAgendaGroup( "g1" ).setFocus();

        ksession.fireAllRules();

        ksession.update( fh, p ); // normally NPE thrown here, for BUG
        
        assertThat(p.getAge()).isEqualTo(36);
    }


    @Test
    public void testAgendaGroupGivewaySequence() {
        // BZ-999360
        String str =
                "global java.util.List ruleList\n" +
                "\n" +

                "rule r1 agenda-group \"g1\" salience 20\n when" +
                "    String( this == 'r1' )\n" +
                "then\n" +
                "    ruleList.add(1);\n" +
                "end\n" +

                "rule r2 agenda-group \"g1\" salience 15\n when" +
                "    String( this == 'r2' )\n" +
                "then\n" +
                "    ruleList.add(2);\n" +
                "    kcontext.getKnowledgeRuntime().getAgenda().getAgendaGroup(\"g2\").setFocus();\n" +
                "end\n" +

                "rule r3 agenda-group \"g1\" salience 10\n when" +
                "    String( this == 'r3' )\n" +
                "then\n" +
                "    ruleList.add(3);\n" +
                "end\n" +

                "rule r4 agenda-group \"g2\" salience 5\n when" +
                "    String( this == 'r4' )\n" +
                "then\n" +
                "    ruleList.add(4);\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Integer> ruleList = new ArrayList<>();
        ksession.setGlobal("ruleList", ruleList);

        ksession.insert(new String("r1"));
        ksession.insert(new String("r1"));
        ksession.insert(new String("r2"));
        ksession.insert(new String("r2"));
        ksession.insert(new String("r3"));
        ksession.insert(new String("r3"));
        ksession.insert(new String("r4"));
        ksession.insert(new String("r4"));
        ksession.getAgenda().getAgendaGroup("g1").setFocus();

        assertThat(ksession.fireAllRules()).isEqualTo(8);


        assertThat(ruleList.size()).isEqualTo(8);
        assertThat(ruleList.get(0)).isEqualTo(1);
        assertThat(ruleList.get(1)).isEqualTo(1);
        assertThat(ruleList.get(2)).isEqualTo(2);

        assertThat(ruleList.get(3)).isEqualTo(4);
        assertThat(ruleList.get(4)).isEqualTo(4);

        assertThat(ruleList.get(5)).isEqualTo(2);

        assertThat(ruleList.get(6)).isEqualTo(3);
        assertThat(ruleList.get(7)).isEqualTo(3);
    }

    @Test
    public void testActivationGroupWithNots() {
        // BZ-1318052
        String drl =
                "global java.util.List list;\n" +
                "rule R1 activation-group \"fatal\" when\n" +
                "    $s : String()\n" +
                "    not Integer( this.toString() == $s )\n" +
                "then\n" +
                "    list.add(\"R1\");\n" +
                "end\n" +
                "rule R2 activation-group \"fatal\" when\n" +
                "    $s : String()\n" +
                "    not Long( this.toString() == $s )\n" +
                "then\n" +
                "    list.add(\"R2\");\n" +
                "end\n" +
                "rule R3 activation-group \"fatal\" when\n" +
                "    Long( )\n" +
                "then\n" +
                "    list.add(\"R3\");\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert( "1" );
        ksession.insert( 2 );
        ksession.insert( 3L );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
    }
}
