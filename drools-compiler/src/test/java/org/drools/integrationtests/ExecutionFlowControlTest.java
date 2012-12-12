package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.drools.Cell;
import org.drools.Cheese;
import org.drools.CommonTestMethodBase;
import org.drools.FactA;
import org.drools.FactHandle;
import org.drools.Father;
import org.drools.Foo;
import org.drools.Message;
import org.drools.Neighbor;
import org.drools.Person;
import org.drools.PersonInterface;
import org.drools.Pet;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.TotalHolder;
import org.drools.WorkingMemory;
import org.drools.common.DefaultAgenda;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.compiler.PackageBuilder;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.rule.Package;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaGroup;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderErrors;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.StatefulKnowledgeSession;

public class ExecutionFlowControlTest extends CommonTestMethodBase {

    @Test
    public void testSalienceIntegerAndDepthCrs() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_salienceIntegerRule.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        final PersonInterface person = new Person( "Edson", "cheese" );
        workingMemory.insert( person );

        workingMemory.fireAllRules();

        assertEquals( "Three rules should have been fired", 3, list.size() );
        assertEquals( "Rule 4 should have been fired first", "Rule 4",
                      list.get( 0 ) );
        assertEquals( "Rule 2 should have been fired second", "Rule 2",
                      list.get( 1 ) );
        assertEquals( "Rule 3 should have been fired third", "Rule 3",
                      list.get( 2 ) );
    }

    @Test
    public void testSalienceExpression() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_salienceExpressionRule.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        final PersonInterface person10 = new Person( "bob", "cheese", 10 );
        workingMemory.insert( person10 );

        final PersonInterface person20 = new Person( "mic", "cheese", 20 );
        workingMemory.insert( person20 );

        workingMemory.fireAllRules();

        assertEquals( "Two rules should have been fired", 2, list.size() );
        assertEquals( "Rule 3 should have been fired first", "Rule 3",
                      list.get( 0 ) );
        assertEquals( "Rule 2 should have been fired second", "Rule 2",
                      list.get( 1 ) );
    }
    
    @Test
    public void testSalienceExpressionWithOr() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
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

        kbuilder.add( ResourceFactory.newByteArrayResource( text.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
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
        
        assertEquals( 3, list.size() );
        assertEquals( fact2, list.get( 0 ) );
        assertEquals( fact3, list.get( 1 ) );
        assertEquals( fact1, list.get( 2 ) );     
    }

    @Test
    public void testSalienceMinInteger() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
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

        builder.addPackageFromDrl( new StringReader( text ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );
        workingMemory.fireAllRules();

        assertEquals( "b", list.get( 2 ) );
    }
    
    @Test
    public void testEnabledExpressionWithOr() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
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

        kbuilder.add( ResourceFactory.newByteArrayResource( text.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
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
        
        assertEquals( 2, list.size() );
        assertEquals( fact2, list.get( 0 ) );
        assertEquals( fact3, list.get( 1 ) );   
    }    

    @Test
    public void testNoLoop() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass()
                .getResourceAsStream( "no-loop.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( "Should not loop  and thus size should be 1", 1,
                      list.size() );

    }

    @Test
    public void testNoLoopWithModify() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "no-loop_with_modify.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        assertEquals( "Should not loop  and thus size should be 1", 1,
                      list.size() );
        assertEquals( 50, brie.getPrice() );

    }

    @Test
    public void testLockOnActive() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LockOnActive.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        // AgendaGroup "group1" is not active, so should receive activation
        final Cheese brie12 = new Cheese( "brie", 12 );
        workingMemory.insert( brie12 );
        DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();
        final AgendaGroup group1 = agenda.getAgendaGroup( "group1" );
        assertEquals( 1, group1.size() );

        workingMemory.setFocus( "group1" );
        // AgendaqGroup "group1" is now active, so should not receive
        // activations
        final Cheese brie10 = new Cheese( "brie", 10 );
        workingMemory.insert( brie10 );
        assertEquals( 1, group1.size() );

        final Cheese cheddar20 = new Cheese( "cheddar", 20 );
        workingMemory.insert( cheddar20 );
        final AgendaGroup group2 = agenda.getAgendaGroup( "group1" );
        assertEquals( 1, group2.size() );

        final RuleFlowGroupImpl rfg = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "ruleflow2" );
        rfg.setActive( true );
        final Cheese cheddar17 = new Cheese( "cheddar", 17 );
        workingMemory.insert( cheddar17 );
        assertEquals( 1, group2.size() );
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
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( "hello1" );
        ksession.insert( "hello2" );
        ksession.insert( "hello3" );

        ksession.fireAllRules();
        assertEquals( 3, list.size() );

        ksession.insert( "hello4" );
        ksession.insert( "hello5" );
        ksession.insert( "hello6" );

        ksession.fireAllRules();
        assertEquals( 6, list.size() );
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
        str += "    if ( list.size() == 2 ) {\n" + "        drools.halt();\n"
               + "    }";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( "hello1" );
        ksession.insert( "hello2" );
        ksession.insert( "hello3" );

        ksession.fireAllRules();
        assertEquals( 2, list.size() );

        // because we have halted, the next 3 will be ignored, but it will still
        // fire the remaing 3rd activation from previous asserts
        ksession.insert( "hello4" );
        ksession.insert( "hello5" );
        ksession.insert( "hello6" );

        ksession.fireAllRules();
        assertEquals( 3, list.size() );
    }

    @Test
    public void testLockOnActiveWithModify() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LockOnActiveWithUpdate.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        wm.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 13 );

        final Person bob = new Person( "bob" );
        bob.setCheese( brie );

        final Person mic = new Person( "mic" );
        mic.setCheese( brie );

        final Person mark = new Person( "mark" );
        mark.setCheese( brie );

        final FactHandle brieHandle = wm.insert( brie );
        wm.insert( bob );
        wm.insert( mic );
        wm.insert( mark );

        final DefaultAgenda agenda = (DefaultAgenda) wm.getAgenda();
        final AgendaGroup group1 = agenda.getAgendaGroup( "group1" );
        agenda.setFocus( group1 );
        assertEquals( 3, group1.size() );
        agenda.fireNextItem( null );
        assertEquals( 2, group1.size() );
        wm.update( brieHandle, brie );
        assertEquals( 2, group1.size() );

        AgendaGroup group2 = agenda.getAgendaGroup( "group2" );
        agenda.setFocus( group2 );

        RuleFlowGroupImpl rfg = (RuleFlowGroupImpl) ((DefaultAgenda) wm.getAgenda()).getRuleFlowGroup( "ruleflow2" );
        assertEquals( 3, rfg.size() );

        agenda.activateRuleFlowGroup( "ruleflow2" );
        agenda.fireNextItem( null );
        assertEquals( 2, rfg.size() );
        wm.update( brieHandle, brie );
        assertEquals( 2, group2.size() );
    }

    @Test
    public void testLockOnActiveWithModify2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LockOnActiveWithModify.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final StatefulSession session = ruleBase.newStatefulSession();
        // WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session
        // );
        // logger.setFileName( "conway" );

        // populating working memory
        final int size = 3;

        Cell[][] cells = new Cell[size][];
        FactHandle[][] handles = new FactHandle[size][];
        for ( int row = 0; row < size; row++ ) {
            cells[row] = new Cell[size];
            handles[row] = new FactHandle[size];
            for ( int col = 0; col < size; col++ ) {
                cells[row][col] = new Cell( Cell.DEAD, row, col );
                handles[row][col] = session.insert( cells[row][col] );
                if ( row >= 1 && col >= 1 ) {
                    // northwest
                    session.insert( new Neighbor( cells[row - 1][col - 1],
                                                  cells[row][col] ) );
                    session.insert( new Neighbor( cells[row][col],
                                                  cells[row - 1][col - 1] ) );
                }
                if ( row >= 1 ) {
                    // north
                    session.insert( new Neighbor( cells[row - 1][col],
                                                  cells[row][col] ) );
                    session.insert( new Neighbor( cells[row][col],
                                                  cells[row - 1][col] ) );
                }
                if ( row >= 1 && col < (size - 1) ) {
                    // northeast
                    session.insert( new Neighbor( cells[row - 1][col + 1],
                                                  cells[row][col] ) );
                    session.insert( new Neighbor( cells[row][col],
                                                  cells[row - 1][col + 1] ) );
                }
                if ( col >= 1 ) {
                    // west
                    session.insert( new Neighbor( cells[row][col - 1],
                                                  cells[row][col] ) );
                    session.insert( new Neighbor( cells[row][col],
                                                  cells[row][col - 1] ) );
                }
            }
        }

        session.clearAgendaGroup( "calculate" );

        // now, start playing
        int fired = session.fireAllRules( 100 );
        assertEquals( 0, fired );

        session.setFocus( "calculate" );
        fired = session.fireAllRules( 100 );
        // logger.writeToDisk();
        assertEquals( 0, fired );
        assertEquals( "MAIN", session.getAgenda().getFocusName() );

        // on the fifth day God created the birds and sea creatures
        cells[0][0].setState( Cell.LIVE );
        session.update( handles[0][0], cells[0][0] );
        session.setFocus( "birth" );
        session.setFocus( "calculate" );
        fired = session.fireAllRules( 100 );

        // logger.writeToDisk();
        int[][] expected = new int[][]{{0, 1, 0}, {1, 1, 0}, {0, 0, 0}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", session.getAgenda().getFocusName() );

        // on the sixth day God created the animals that walk over the land and
        // the Man
        cells[1][1].setState( Cell.LIVE );
        session.update( handles[1][1], cells[1][1] );
        session.setFocus( "calculate" );
        session.fireAllRules( 100 );
        // logger.writeToDisk();

        expected = new int[][]{{1, 2, 1}, {2, 1, 1}, {1, 1, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", session.getAgenda().getFocusName() );

        session.setFocus( "birth" );
        session.fireAllRules( 100 );
        expected = new int[][]{{1, 2, 1}, {2, 1, 1}, {1, 1, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", session.getAgenda().getFocusName() );

        session.setFocus( "calculate" );
        session.fireAllRules( 100 );
        // logger.writeToDisk();
        // printMatrix( cells );

        expected = new int[][]{{3, 3, 2}, {3, 3, 2}, {2, 2, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", session.getAgenda().getFocusName() );

        // on the seventh day, while God rested, man start killing them all
        cells[0][0].setState( Cell.DEAD );
        session.update( handles[0][0], cells[0][0] );
        session.setFocus( "calculate" );
        session.fireAllRules( 100 );

        expected = new int[][]{{3, 2, 2}, {2, 2, 2}, {2, 2, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", session.getAgenda().getFocusName() );

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
                assertEquals( "Wrong value at " + row + "," + col + ": ",
                              expected[row][col], cells[row][col].getValue() );
            }
        }
    }

    @Test
    public void testAgendaGroups() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_AgendaGroups.drl", getClass() ), ResourceType.DRL );

        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession session = createKnowledgeSession( kbase );

        final List list = new ArrayList();
        session.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        session.insert( brie );

        session.fireAllRules();

        assertEquals( 7, list.size() );

        assertEquals( "group3", list.get( 0 ) );
        assertEquals( "group4", list.get( 1 ) );
        assertEquals( "group3", list.get( 2 ) );
        assertEquals( "MAIN", list.get( 3 ) );
        assertEquals( "group1", list.get( 4 ) );
        assertEquals( "group1", list.get( 5 ) );
        assertEquals( "MAIN", list.get( 6 ) );

        session.getAgenda().getAgendaGroup( "group2" ).setFocus();
        session.fireAllRules();

        assertEquals( 8, list.size() );
        assertEquals( "group2", list.get( 7 ) );

        // clear main only the auto focus related ones should fire
        list.clear();
        session.insert( new Cheese( "cheddar" ) );
        session.getAgenda().getAgendaGroup( "MAIN" ).clear();
        session.fireAllRules();
        assertEquals( 3, list.size() );
        assertEquals( "group3", list.get( 0 ) );
        assertEquals( "group4", list.get( 1 ) );
        assertEquals( "group3", list.get( 2 ) );

    }

    @Test
    public void testActivationGroups() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ActivationGroups.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        workingMemory.insert( brie );        

        DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();
        final ActivationGroup activationGroup0 = agenda.getActivationGroup( "activation-group-0" );
        assertEquals( 2, activationGroup0.size() );

        final ActivationGroup activationGroup3 = agenda.getActivationGroup( "activation-group-3" );
        assertEquals( 1, activationGroup3.size() );

        final AgendaGroup agendaGroup3 = agenda.getAgendaGroup( "agenda-group-3" );
        assertEquals( 1, agendaGroup3.size() );

        final AgendaGroup agendaGroupMain = agenda.getAgendaGroup( "MAIN" );
        assertEquals( 3, agendaGroupMain.size() );

        workingMemory.clearAgendaGroup( "agenda-group-3" );
        assertEquals( 0, activationGroup3.size() );
        assertEquals( 0, agendaGroup3.size() );

        workingMemory.fireAllRules();

        assertEquals( 0, activationGroup0.size() );

        assertEquals( 2, list.size() );
        assertEquals( "rule0", list.get( 0 ) );
        assertEquals( "rule2", list.get( 1 ) );
    }
    
    @Test 
    @Ignore("FIXME for Planner")
    public void testUnMatchListenerForChainedPlanningEntities() {
        String str =""+
                "package org.kie.test;\n" +
                "\n" +
                "import org.kie.Father;\n" +
                "import org.kie.TotalHolder;\n" +
                "\n" +
                "global TotalHolder totalHolder;\n" +
                "\n" +
                "rule \"sumWeightOfFather\"\n" +
                "when\n" +
                "    $h: Father(father != null, $wf : weightOfFather)\n" +
                "then\n" +
                "    totalHolder.add($wf);\n" +
"    System.out.println(\"match \" + totalHolder.getTotal());\n" +
                "    final TotalHolder finalTotalHolder = totalHolder;\n" +
                "    final int finalWf = $wf;\n" +
                "     org.kie.common.AgendaItem agendaItem = (org.kie.common.AgendaItem) kcontext.getActivation();" +
                "     agendaItem.setActivationUnMatchListener(new org.kie.event.rule.ActivationUnMatchListener() {" +
                "            public void unMatch(org.kie.runtime.rule.WorkingMemory workingMemory, org.kie.runtime.rule.Activation activation) {" +
                "                finalTotalHolder.subtract(finalWf);" +
                "                System.out.println(\"unmatch \" + finalTotalHolder.getTotal());\n" +
                "            }" +
                "     });" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                ResourceType.DRL );
        assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession kSession = createKnowledgeSession(kbase);

        kSession.setGlobal("totalHolder", new TotalHolder());
        Father abraham = new Father("abraham", null, 100);
        Father homer = new Father("homer", null, 20);
        Father bart = new Father("bart", null, 3);

        org.kie.runtime.rule.FactHandle abrahamHandle = kSession.insert(abraham);
        org.kie.runtime.rule.FactHandle bartHandle = kSession.insert(bart);
        kSession.fireAllRules();
        assertEquals(0, ((TotalHolder) kSession.getGlobal("totalHolder")).getTotal());

        bart.setFather(abraham);
        kSession.update(bartHandle, bart);
        kSession.fireAllRules();
        assertEquals(100, ((TotalHolder) kSession.getGlobal("totalHolder")).getTotal());

        bart.setFather(null);
        kSession.update(bartHandle, bart);
        kSession.fireAllRules();
        assertEquals(0, ((TotalHolder) kSession.getGlobal("totalHolder")).getTotal());

        bart.setFather(abraham);
        kSession.update(bartHandle, bart);
        kSession.fireAllRules();
        assertEquals(100, ((TotalHolder) kSession.getGlobal("totalHolder")).getTotal());

        org.kie.runtime.rule.FactHandle homerHandle = kSession.insert(homer);
        homer.setFather(abraham);
        kSession.update(homerHandle, homer);
        bart.setFather(homer);
        kSession.update(bartHandle, bart);
        kSession.fireAllRules();
        assertEquals(120, ((TotalHolder) kSession.getGlobal("totalHolder")).getTotal());
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
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            System.err.println( "Errors" );
            KnowledgeBuilderErrors errors = kBuilder.getErrors();
            for ( KnowledgeBuilderError kbe : errors ) {
                System.err.println( kbe.getMessage() );
                for ( int errLine : kbe.getLines() ) {
                    System.err.println( errLine );
                }
            }
            System.exit( 1 );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KnowledgePackage> knowledgePackages = kBuilder.getKnowledgePackages();
        kbase.addKnowledgePackages( knowledgePackages );

        StatefulKnowledgeSession session = createKnowledgeSession( kbase );

        Holder inrec = new Holder( 1 );
        System.out.println( "Holds: " + inrec.getValue() );
        session.insert( inrec );
        session.fireAllRules();
        Assert.assertEquals( 1, session.getFactHandles().size() );
        Assert.assertEquals( "setting 1", inrec.getOutcome() );

        session.dispose();
        session = createKnowledgeSession( kbase );
        inrec = new Holder( null );
        System.out.println( "Holds: " + inrec.getValue() );
        session.insert( inrec );
        session.fireAllRules();
        Assert.assertEquals( 1, session.getFactHandles().size() );
        Assert.assertEquals( "setting null", inrec.getOutcome() );

        session.dispose();
        session = createKnowledgeSession( kbase );
        inrec = new Holder( 0 );
        System.out.println( "Holds: " + inrec.getValue() );
        session.insert( inrec );
        session.fireAllRules(); // appropriate rule is not fired!
        Assert.assertEquals( 1, session.getFactHandles().size() );
        Assert.assertEquals( "setting 0", inrec.getOutcome() );
    }

    @Test
    public void testInsertRetractNoloop() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Insert_Retract_Noloop.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory wm = ruleBase.newStatefulSession();
        wm.insert( new Cheese( "stilton", 15 ) );

        wm.fireAllRules();
    }

    @Test
    public void testUpdateNoLoop() throws Exception {
        // JBRULES-780, throws a NullPointer or infinite loop if there is an
        // issue
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_UpdateNoloop.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory wm = ruleBase.newStatefulSession();
        wm.insert( new Cheese( "stilton", 15 ) );

        wm.fireAllRules();
    }

    @Test
    public void testUpdateActivationCreationNoLoop() throws Exception {
        // JBRULES-787, no-loop blocks all dependant tuples for update
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_UpdateActivationCreationNoLoop.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final InternalWorkingMemoryActions wm = (InternalWorkingMemoryActions) ruleBase
                .newStatefulSession();
        final List created = new ArrayList();
        final List cancelled = new ArrayList();
        final AgendaEventListener l = new DefaultAgendaEventListener() {
            @Override
            public void activationCreated(ActivationCreatedEvent event,
                                          WorkingMemory workingMemory) {
                created.add( event );
            }

            @Override
            public void activationCancelled(ActivationCancelledEvent event,
                                            WorkingMemory workingMemory) {
                cancelled.add( event );
            }

        };

        wm.addEventListener( l );

        final Cheese stilton = new Cheese( "stilton", 15 );
        final FactHandle stiltonHandle = wm.insert( stilton );

        final Person p1 = new Person( "p1" );
        p1.setCheese( stilton );
        wm.insert( p1 );

        final Person p2 = new Person( "p2" );
        p2.setCheese( stilton );
        wm.insert( p2 );

        final Person p3 = new Person( "p3" );
        p3.setCheese( stilton );
        wm.insert( p3 );
        
        wm.fireAllRules();

        assertEquals( 3, created.size() );
        assertEquals( 0, cancelled.size() );

        final Activation item = ((ActivationCreatedEvent) created.get( 2 ))
                .getActivation();

        // simulate a modify inside a consequence
        wm.update( stiltonHandle, stilton, Long.MAX_VALUE, item );

        // with true modify, no reactivations should be triggered
        assertEquals( 3, created.size() );
        assertEquals( 0, cancelled.size() );
    }

    @Test
    public void testRuleFlowGroup() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflowgroup.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        workingMemory.insert( "Test" );
        workingMemory.fireAllRules();
        assertEquals( 0, list.size() );

        workingMemory.getAgenda().activateRuleFlowGroup( "Group1" );
        workingMemory.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testRuleFlowGroupDeactivate() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflowgroup2.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        workingMemory.insert( "Test" );
        workingMemory.fireAllRules();
        assertEquals( 0, list.size() );
        assertEquals( 2, workingMemory.getAgenda().getRuleFlowGroup( "Group1" ).size() );

        workingMemory.getAgenda().activateRuleFlowGroup( "Group1" );
        workingMemory.fireAllRules();

        assertEquals( 0, list.size() );
    }

    @Test
    public void testRuleFlowGroupInActiveMode() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflowgroup.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final StatefulSession workingMemory = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        workingMemory.insert( "Test" );
        workingMemory.fireAllRules();
        assertEquals( 0, list.size() );

        workingMemory.getAgenda().activateRuleFlowGroup( "Group1" );
        workingMemory.fireAllRules();

        assertEquals( 1, list.size() );

        workingMemory.halt();
    }

    @Test
    public void testDateEffective() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_EffectiveDate.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list", list );

        // go !
        final Message message = new Message( "hola" );
        workingMemory.insert( message );
        workingMemory.fireAllRules();
        assertFalse( message.isFired() );
    }

    @Test
    public void testNullPointerOnModifyWithLockOnActive() {
        // JBRULES-3234

        String str = "package org.kie.test \n"
                     + "import org.drools.Person; \n"
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person p = new Person( "darth", 36 );
        FactHandle fh = (FactHandle) ksession.insert( p );

        //session.startProcess("fraudAnalysisFlow");			
        //session.getAgenda().getAgendaGroup("customerActivityLookup").setFocus();
        ksession.getAgenda().getAgendaGroup( "g1" ).setFocus();

        ksession.fireAllRules();

        ksession.update( fh, p ); // normally NPE thrown here, for BUG
        
        assertEquals( 36, p.getAge() );
    }
}
