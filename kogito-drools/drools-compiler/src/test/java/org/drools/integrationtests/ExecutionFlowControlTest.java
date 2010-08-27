package org.drools.integrationtests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cell;
import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Message;
import org.drools.Neighbor;
import org.drools.Person;
import org.drools.PersonInterface;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.DefaultAgenda;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilder.PackageMergeException;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaGroup;

public class ExecutionFlowControlTest extends TestCase {
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    public void testRuleFlowConstraintDialects() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "test_ConstraintDialects.rfm" ) ) );

        System.err.print( builder.getErrors() );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        StatefulSession session = ruleBase.newStatefulSession();
        List inList = new ArrayList();
        List outList = new ArrayList();
        session.setGlobal( "inList",
                           inList );
        session.setGlobal( "outList",
                           outList );

        inList.add( 1 );
        inList.add( 3 );
        inList.add( 6 );
        inList.add( 25 );

        FactHandle handle = session.insert( inList );
        session.startProcess( "ConstraintDialects" );
        assertEquals( 4,
                      outList.size() );
        assertEquals( "MVELCodeConstraint was here",
                      outList.get( 0 ) );
        assertEquals( "JavaCodeConstraint was here",
                      outList.get( 1 ) );
        assertEquals( "MVELRuleConstraint was here",
                      outList.get( 2 ) );
        assertEquals( "JavaRuleConstraint was here",
                      outList.get( 3 ) );

        outList.clear();
        inList.remove( new Integer( 1 ) );
        session.update( handle,
                        inList );
        session.startProcess( "ConstraintDialects" );
        assertEquals( 3,
                      outList.size() );
        assertEquals( "JavaCodeConstraint was here",
                      outList.get( 0 ) );
        assertEquals( "MVELRuleConstraint was here",
                      outList.get( 1 ) );
        assertEquals( "JavaRuleConstraint was here",
                      outList.get( 2 ) );

        outList.clear();
        inList.remove( new Integer( 6 ) );
        session.update( handle,
                        inList );
        session.startProcess( "ConstraintDialects" );
        assertEquals( 2,
                      outList.size() );
        assertEquals( "JavaCodeConstraint was here",
                      outList.get( 0 ) );
        assertEquals( "JavaRuleConstraint was here",
                      outList.get( 1 ) );

        outList.clear();
        inList.remove( new Integer( 3 ) );
        session.update( handle,
                        inList );
        session.startProcess( "ConstraintDialects" );
        assertEquals( 1,
                      outList.size() );
        assertEquals( "JavaRuleConstraint was here",
                      outList.get( 0 ) );

        outList.clear();
        inList.remove( new Integer( 25 ) );
        session.update( handle,
                        inList );
        try {
            session.startProcess( "ConstraintDialects" );
            fail( "This should have thrown an exception" );
        } catch ( Exception e ) {
        }
    }

    public void testSalienceIntegerAndDepthCrs() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_salienceIntegerRule.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface person = new Person( "Edson",
                                                   "cheese" );
        workingMemory.insert( person );

        workingMemory.fireAllRules();

        Assert.assertEquals( "Three rules should have been fired",
                             3,
                             list.size() );
        Assert.assertEquals( "Rule 4 should have been fired first",
                             "Rule 4",
                             list.get( 0 ) );
        Assert.assertEquals( "Rule 2 should have been fired second",
                             "Rule 2",
                             list.get( 1 ) );        
        Assert.assertEquals( "Rule 3 should have been fired third",
                             "Rule 3",
                             list.get( 2 ) );        
    }

    public void testSalienceExpression() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_salienceExpressionRule.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface person10 = new Person( "bob",
                                                     "cheese",
                                                     10 );
        workingMemory.insert( person10 );

        final PersonInterface person20 = new Person( "mic",
                                                     "cheese",
                                                     20 );
        workingMemory.insert( person20 );

        workingMemory.fireAllRules();

        Assert.assertEquals( "Two rules should have been fired",
                             2,
                             list.size() );
        Assert.assertEquals( "Rule 3 should have been fired first",
                             "Rule 3",
                             list.get( 0 ) );
        Assert.assertEquals( "Rule 2 should have been fired second",
                             "Rule 2",
                             list.get( 1 ) );
    }

    public void testNoLoop() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "no-loop.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        Assert.assertEquals( "Should not loop  and thus size should be 1",
                             1,
                             list.size() );

    }
    
    public void testNoLoopWithModify() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "no-loop_with_modify.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.insert( brie );

        workingMemory.fireAllRules();

        Assert.assertEquals( "Should not loop  and thus size should be 1",
                             1,
                             list.size() );
        assertEquals( 50, brie.getPrice() );

    }    

    public void testLockOnActive() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LockOnActive.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        // AgendaGroup "group1" is not active, so should receive activation
        final Cheese brie12 = new Cheese( "brie",
                                          12 );
        workingMemory.insert( brie12 );
        DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();
        final AgendaGroup group1 = agenda.getAgendaGroup( "group1" );
        assertEquals( 1,
                      group1.size() );

        workingMemory.setFocus( "group1" );
        // AgendaqGroup "group1" is now active, so should not receive activations
        final Cheese brie10 = new Cheese( "brie",
                                          10 );
        workingMemory.insert( brie10 );
        assertEquals( 1,
                      group1.size() );

        final Cheese cheddar20 = new Cheese( "cheddar",
                                             20 );
        workingMemory.insert( cheddar20 );
        final AgendaGroup group2 = agenda.getAgendaGroup( "group1" );
        assertEquals( 1,
                      group2.size() );

        final RuleFlowGroupImpl rfg = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "ruleflow2" );
        rfg.setActive( true );
        final Cheese cheddar17 = new Cheese( "cheddar",
                                             17 );
        workingMemory.insert( cheddar17 );
        assertEquals( 1,
                      group2.size() );
    }
    
    public void testLockOnActiveForMain() {
        String str = "";
        str += "package org.drools \n";
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
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
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
    
    public void testLockOnActiveForMainWithHalt() {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    lock-on-active true \n";
        str += "when \n";
        str += "    $str : String() \n";
        str += "then \n";
        str += "    list.add( $str ); \n";
        str += "    if ( list.size() == 2 ) drools.halt();\n";
        str += "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        assertFalse( kbuilder.hasErrors() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( "hello1" );
        ksession.insert( "hello2" );
        ksession.insert( "hello3" );
        
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        
        // because we have halted, the next 3 will be ignored, but it will still fire the remaing 3rd activation from previous asserts
        ksession.insert( "hello4" );
        ksession.insert( "hello5" );
        ksession.insert( "hello6" );
        
        ksession.fireAllRules();
        assertEquals( 3, list.size() );                
    }    

    public void testLockOnActiveWithModify() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LockOnActiveWithUpdate.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        wm.setGlobal( "list",
                      list );

        final Cheese brie = new Cheese( "brie",
                                        13 );

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
        assertEquals( 3,
                      group1.size() );
        agenda.fireNextItem( null );
        assertEquals( 2,
                      group1.size() );
        wm.update( brieHandle,
                   brie );
        assertEquals( 2,
                      group1.size() );

        AgendaGroup group2 = agenda.getAgendaGroup( "group2" );
        agenda.setFocus( group2 );

        RuleFlowGroupImpl rfg = (RuleFlowGroupImpl) ((DefaultAgenda) wm.getAgenda()).getRuleFlowGroup( "ruleflow2" );
        assertEquals( 3,
                      rfg.size() );

        agenda.activateRuleFlowGroup( "ruleflow2" );
        agenda.fireNextItem( null );
        assertEquals( 2,
                      rfg.size() );
        wm.update( brieHandle,
                   brie );
        assertEquals( 2,
                      group2.size() );
    }

    public void testLockOnActiveWithModify2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LockOnActiveWithModify.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final StatefulSession session = ruleBase.newStatefulSession();
        //        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        //        logger.setFileName( "conway" );

        // populating working memory
        final int size = 3;

        Cell[][] cells = new Cell[size][];
        FactHandle[][] handles = new FactHandle[size][];
        for ( int row = 0; row < size; row++ ) {
            cells[row] = new Cell[size];
            handles[row] = new FactHandle[size];
            for ( int col = 0; col < size; col++ ) {
                cells[row][col] = new Cell( Cell.DEAD,
                                            row,
                                            col );
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
        assertEquals( 0,
                      fired );

        session.setFocus( "calculate" );
        fired = session.fireAllRules( 100 );
        //        logger.writeToDisk();
        assertEquals( 0,
                      fired );
        assertEquals( "MAIN",
                      session.getAgenda().getFocusName() );

        // on the fifth day God created the birds and sea creatures
        cells[0][0].setState( Cell.LIVE );
        session.update( handles[0][0],
                              cells[0][0] );
        session.setFocus( "birth" );
        session.setFocus( "calculate" );
        fired = session.fireAllRules( 100 );

        //        logger.writeToDisk();
        int[][] expected = new int[][]{{0, 1, 0}, {1, 1, 0}, {0, 0, 0}};
        assertEqualsMatrix( size,
                            cells,
                            expected );
        assertEquals( "MAIN",
                      session.getAgenda().getFocusName() );

        // on the sixth day God created the animals that walk over the land and the Man
        cells[1][1].setState( Cell.LIVE );
        session.update( handles[1][1],
                              cells[1][1] );
        session.setFocus( "calculate" );
        session.fireAllRules( 100 );
        //        logger.writeToDisk();

        expected = new int[][]{{1, 2, 1}, {2, 1, 1}, {1, 1, 1}};
        assertEqualsMatrix( size,
                            cells,
                            expected );
        assertEquals( "MAIN",
                      session.getAgenda().getFocusName() );

        session.setFocus( "birth" );
        session.fireAllRules( 100 );
        expected = new int[][]{{1, 2, 1}, {2, 1, 1}, {1, 1, 1}};
        assertEqualsMatrix( size,
                            cells,
                            expected );
        assertEquals( "MAIN",
                      session.getAgenda().getFocusName() );

        session.setFocus( "calculate" );
        session.fireAllRules( 100 );
        //        logger.writeToDisk();
        //        printMatrix( cells );

        expected = new int[][]{{3, 3, 2}, {3, 3, 2}, {2, 2, 1}};
        assertEqualsMatrix( size,
                            cells,
                            expected );
        assertEquals( "MAIN",
                      session.getAgenda().getFocusName() );

        // on the seventh day, while God rested, man start killing them all
        cells[0][0].setState( Cell.DEAD );
        session.update( handles[0][0],
                              cells[0][0] );
        session.setFocus( "calculate" );
        session.fireAllRules( 100 );

        expected = new int[][]{{3, 2, 2}, {2, 2, 2}, {2, 2, 1}};
        assertEqualsMatrix( size,
                            cells,
                            expected );
        assertEquals( "MAIN",
                      session.getAgenda().getFocusName() );

    }

    //    private void printMatrix(Cell[][] cells) {
    //        System.out.println("----------");
    //        for( int row = 0; row < cells.length; row++) {
    //            for( int col = 0; col < cells[row].length; col++ ) {
    //                System.out.print( cells[row][col].getValue() + ((cells[row][col].getState()==Cell.LIVE)?"L  ":".  ") );
    //            }
    //            System.out.println();
    //        }
    //        System.out.println("----------");
    //    }

    private void assertEqualsMatrix(final int size,
                                    Cell[][] cells,
                                    int[][] expected) {
        for ( int row = 0; row < size; row++ ) {
            for ( int col = 0; col < size; col++ ) {
                assertEquals( "Wrong value at " + row + "," + col + ": ",
                              expected[row][col],
                              cells[row][col].getValue() );
            }
        }
    }

    public void testAgendaGroups() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_AgendaGroups.drl", getClass() ), ResourceType.DRL );

        assertFalse( kbuilder.hasErrors() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        session.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        session.insert( brie );

        session.fireAllRules();

        assertEquals( 7,
                      list.size() );

        assertEquals( "group3",
                      list.get( 0 ) );
        assertEquals( "group4",
                      list.get( 1 ) );
        assertEquals( "group3",
                      list.get( 2 ) );
        assertEquals( "MAIN",
                      list.get( 3 ) );
        assertEquals( "group1",
                      list.get( 4 ) );
        assertEquals( "group1",
                      list.get( 5 ) );
        assertEquals( "MAIN",
                      list.get( 6 ) );

        session.getAgenda().getAgendaGroup( "group2" ).setFocus( );
        session.fireAllRules();

        assertEquals( 8,
                      list.size() );
        assertEquals( "group2",
                      list.get( 7 ) );
        
        // clear main only the auto focus related ones should fire
        list.clear();
        session.insert( new Cheese( "cheddar" ) );        
        session.getAgenda().getAgendaGroup( "MAIN" ).clear();
        session.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertEquals( "group3",
                      list.get( 0 ) );
        assertEquals( "group4",
                      list.get( 1 ) );        
        assertEquals( "group3",
                      list.get( 2 ) );        

        
    }

    public void testActivationGroups() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ActivationGroups.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.insert( brie );

        DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();
        final ActivationGroup activationGroup0 = agenda.getActivationGroup( "activation-group-0" );
        assertEquals( 2,
                      activationGroup0.size() );

        final ActivationGroup activationGroup3 = agenda.getActivationGroup( "activation-group-3" );
        assertEquals( 1,
                      activationGroup3.size() );

        final AgendaGroup agendaGroup3 = agenda.getAgendaGroup( "agenda-group-3" );
        assertEquals( 1,
                      agendaGroup3.size() );

        final AgendaGroup agendaGroupMain = agenda.getAgendaGroup( "MAIN" );
        assertEquals( 3,
                      agendaGroupMain.size() );

        workingMemory.clearAgendaGroup( "agenda-group-3" );
        assertEquals( 0,
                      activationGroup3.size() );
        assertEquals( 0,
                      agendaGroup3.size() );

        workingMemory.fireAllRules();

        assertEquals( 0,
                      activationGroup0.size() );

        assertEquals( 2,
                      list.size() );
        assertEquals( "rule0",
                      list.get( 0 ) );
        assertEquals( "rule2",
                      list.get( 1 ) );

    }

    public void testInsertRetractNoloop() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Insert_Retract_Noloop.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );
    
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory wm = ruleBase.newStatefulSession();
        wm.insert( new Cheese( "stilton",
                               15 ) );
    
        wm.fireAllRules();
    }

    public void testUpdateNoLoop() throws Exception {
        // JBRULES-780, throws a NullPointer or infinite loop if there is an issue
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_UpdateNoloop.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory wm = ruleBase.newStatefulSession();
        wm.insert( new Cheese( "stilton",
                               15 ) );

        wm.fireAllRules();
    }

    public void testUpdateActivationCreationNoLoop() throws Exception {
        // JBRULES-787, no-loop blocks all dependant tuples for update 
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_UpdateActivationCreationNoLoop.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final InternalWorkingMemoryActions wm = (InternalWorkingMemoryActions) ruleBase.newStatefulSession();
        final List created = new ArrayList();
        final List cancelled = new ArrayList();
        final AgendaEventListener l = new DefaultAgendaEventListener() {
            public void activationCreated(ActivationCreatedEvent event,
                                          WorkingMemory workingMemory) {
                created.add( event );
            }

            public void activationCancelled(ActivationCancelledEvent event,
                                            WorkingMemory workingMemory) {
                cancelled.add( event );
            }

        };

        wm.addEventListener( l );

        final Cheese stilton = new Cheese( "stilton",
                                           15 );
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

        assertEquals( 3,
                      created.size() );
        assertEquals( 0,
                      cancelled.size() );

        final Activation item = ((ActivationCreatedEvent) created.get( 2 )).getActivation();

        // simulate a modify inside a consequence
        wm.update( stiltonHandle,
                   stilton,
                   item.getRule(),
                   item );

        // with true modify, no reactivations should be triggered
        assertEquals( 3,
                      created.size() );
        assertEquals( 0,
                      cancelled.size() );
    }

    public void testRuleFlowGroup() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflowgroup.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.insert( "Test" );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        workingMemory.getAgenda().activateRuleFlowGroup( "Group1" );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testRuleFlowGroupDeactivate() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflowgroup2.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.insert( "Test" );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );
        assertEquals( 2, workingMemory.getAgenda().getRuleFlowGroup( "Group1" ).size() );

        workingMemory.getAgenda().activateRuleFlowGroup( "Group1" );
        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );
    }

    public void testRuleFlowGroupInActiveMode() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflowgroup.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final StatefulSession workingMemory = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );
        
        workingMemory.insert( "Test" );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        workingMemory.getAgenda().activateRuleFlowGroup( "Group1" );
        workingMemory.fireAllRules();
        
        assertEquals( 1,
                      list.size() );
        
        workingMemory.halt();
    }

    public void testRuleFlow() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );
        final Package pkg = builder.getPackage();
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        final ProcessInstance processInstance = workingMemory.startProcess( "0" );
        assertEquals( ProcessInstance.STATE_ACTIVE,
                      processInstance.getState() );
        workingMemory.fireAllRules();
        assertEquals( 4,
                      list.size() );
        assertEquals( "Rule1",
                      list.get( 0 ) );
        assertEquals( "Rule3",
                      list.get( 1 ) );
        assertEquals( "Rule2",
                      list.get( 2 ) );
        assertEquals( "Rule4",
                      list.get( 3 ) );
        assertEquals( ProcessInstance.STATE_COMPLETED,
                      processInstance.getState() );
    }

    public void testRuleFlowUpgrade() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        // Set the system property so that automatic conversion can happen
        System.setProperty( "drools.ruleflow.port",
                            "true" );

        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow40.rfm" ) ) );
        final Package pkg = builder.getPackage();
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        final ProcessInstance processInstance = workingMemory.startProcess( "0" );
        assertEquals( ProcessInstance.STATE_ACTIVE,
                      processInstance.getState() );
        workingMemory.fireAllRules();
        assertEquals( 4,
                      list.size() );
        assertEquals( "Rule1",
                      list.get( 0 ) );
        assertEquals( "Rule3",
                      list.get( 1 ) );
        assertEquals( "Rule2",
                      list.get( 2 ) );
        assertEquals( "Rule4",
                      list.get( 3 ) );
        assertEquals( ProcessInstance.STATE_COMPLETED,
                      processInstance.getState() );
        // Reset the system property so that automatic conversion should not happen
        System.setProperty( "drools.ruleflow.port",
                            "false" );
    }

    public void testRuleFlowClear() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ruleflowClear.drl" ) ) );
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "test_ruleflowClear.rfm" ) ) );
        final Package pkg = builder.getPackage();
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final List activations = new ArrayList();
        AgendaEventListener listener = new DefaultAgendaEventListener() {
            public void activationCancelled(ActivationCancelledEvent event,
                                            WorkingMemory workingMemory) {
                activations.add( event.getActivation() );
            }
        };

        workingMemory.addEventListener( listener );
        DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();
        assertEquals( 0,
                      agenda.getRuleFlowGroup( "flowgroup-1" ).size() );

        // We need to call fireAllRules here to get the InitialFact into the system, to the eval(true)'s kick in
        workingMemory.fireAllRules();

        // Now we have 4 in the RuleFlow, but not yet in the agenda
        assertEquals( 4,
                      agenda.getRuleFlowGroup( "flowgroup-1" ).size() );

        // Check they aren't in the Agenda
        assertEquals( 0,
                      agenda.getAgendaGroup( "MAIN" ).size() );

        // Start the process, which shoudl populate the Agenda
        final ProcessInstance processInstance = workingMemory.startProcess( "ruleFlowClear" );
        assertEquals( 4,
                      agenda.getAgendaGroup( "MAIN" ).size() );

        // Check we have 0 activation cancellation events
        assertEquals( 0,
                      activations.size() );

        workingMemory.getAgenda().clearAndCancelRuleFlowGroup( "flowgroup-1" );

        // Check the AgendaGroup and RuleFlowGroup  are now empty
        assertEquals( 0,
                      agenda.getAgendaGroup( "MAIN" ).size() );
        assertEquals( 0,
                      agenda.getRuleFlowGroup( "flowgroup-1" ).size() );

        // Check we have four activation cancellation events
        assertEquals( 4,
                      activations.size() );
    }

    public void testRuleFlowInPackage() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        final ProcessInstance processInstance = workingMemory.startProcess( "0" );
        assertEquals( ProcessInstance.STATE_ACTIVE,
                      processInstance.getState() );
        workingMemory.fireAllRules();
        assertEquals( 4,
                      list.size() );
        assertEquals( "Rule1",
                      list.get( 0 ) );
        assertEquals( "Rule3",
                      list.get( 1 ) );
        assertEquals( "Rule2",
                      list.get( 2 ) );
        assertEquals( "Rule4",
                      list.get( 3 ) );
        assertEquals( ProcessInstance.STATE_COMPLETED,
                      processInstance.getState() );

    }

    public void testLoadingRuleFlowInPackage1() throws Exception {
        // adding ruleflow before adding package
        final PackageBuilder builder = new PackageBuilder();
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        builder.getPackage();
    }

    public void testLoadingRuleFlowInPackage2() throws Exception {
        // only adding ruleflow
        final PackageBuilder builder = new PackageBuilder();
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );
        builder.getPackage();
    }

    public void testLoadingRuleFlowInPackage3() throws Exception {
        // only adding ruleflow without any generated rules
        final PackageBuilder builder = new PackageBuilder();
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "empty_ruleflow.rfm" ) ) );
        builder.getPackage();
    }

    public void FIXME_testLoadingRuleFlowInPackage4() throws Exception {
        // adding ruleflows of different package
        final PackageBuilder builder = new PackageBuilder();
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "empty_ruleflow.rfm" ) ) );
        try {
            builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );
            throw new Exception( "An exception should have been thrown." );
        } catch ( PackageMergeException e ) {
            // do nothing
        }
    }

    public void FIXME_testLoadingRuleFlowInPackage5() throws Exception {
        // adding ruleflow of different package than rules
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        try {
            builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "empty_ruleflow.rfm" ) ) );
            throw new Exception( "An exception should have been thrown." );
        } catch ( PackageMergeException e ) {
            // do nothing
        }
    }

    public void FIXME_testLoadingRuleFlowInPackage6() throws Exception {
        // adding rules of different package than ruleflow
        final PackageBuilder builder = new PackageBuilder();
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "empty_ruleflow.rfm" ) ) );
        try {
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
            throw new Exception( "An exception should have been thrown." );
        } catch ( PackageMergeException e ) {
            // do nothing
        }
    }

    public void testRuleFlowActionDialects() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "test_ActionDialects.rfm" ) ) );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        session.startProcess( "ActionDialects" );

        assertEquals( 2,
                      list.size() );
        assertEquals( "mvel was here",
                      list.get( 0 ) );
        assertEquals( "java was here",
                      list.get( 1 ) );
    }

    public void testLoadingRuleFlowInPackage7() throws Exception {
        // loading a ruleflow with errors
        final PackageBuilder builder = new PackageBuilder();
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "error_ruleflow.rfm" ) ) );
        assertEquals( 1,
                      builder.getErrors().getErrors().length );
    }

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        // load up the rulebase
        return ruleBase;
    }

    public void testDateEffective() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_EffectiveDate.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        // go !
        final Message message = new Message( "hola" );
        workingMemory.insert( message );
        workingMemory.fireAllRules();
        assertFalse( message.isFired() );

    }
}
