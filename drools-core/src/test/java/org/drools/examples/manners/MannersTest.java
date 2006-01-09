package org.drools.examples.manners;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactException;
import org.drools.ReteooJungViewer;
import org.drools.RuleBase;
import org.drools.RuleIntegrationException;
import org.drools.RuleSetIntegrationException;
import org.drools.WorkingMemory;
import org.drools.reteoo.RuleBaseImpl;
import org.drools.rule.And;
import org.drools.rule.BoundVariableConstraint;
import org.drools.rule.Column;
import org.drools.rule.ColumnBinding;
import org.drools.rule.Declaration;
import org.drools.rule.DuplicateRuleNameException;
import org.drools.rule.EvaluatorFactory;
import org.drools.rule.FieldBinding;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;
import org.drools.spi.Activation;
import org.drools.spi.ClassFieldExtractor;
import org.drools.spi.ClassObjectType;
import org.drools.spi.BaseEvaluator;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.Constraint;
import org.drools.spi.DefaultKnowledgeHelper;
import org.drools.spi.Evaluator;
import org.drools.spi.Field;
import org.drools.spi.FieldExtractor;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.LiteralExpressionConstraint;
import org.drools.spi.MockField;
import org.drools.spi.Tuple;

public class MannersTest extends TestCase {
    /** Number of guests at the dinner (default: 16). */
    private int             numGuests  = 16;

    /** Number of seats at the table (default: 16). */
    private int             numSeats   = 16;

    /** Minimum number of hobbies each guest should have (default: 2). */
    private int             minHobbies = 2;

    /** Maximun number of hobbies each guest should have (default: 3). */
    private int             maxHobbies = 3;

    private ClassObjectType contextType;
    private ClassObjectType guestType;
    private ClassObjectType seatingType;
    private ClassObjectType lastSeatType;
    private ClassObjectType countType;
    private ClassObjectType pathType;
    private ClassObjectType chosenType;
    private Evaluator       objectEqualEvaluator;
    private Evaluator       objectNotEqualEvaluator;
    private Evaluator       integerEqualEvaluator;
    private Evaluator       integerNotEqualEvaluator;
    private Evaluator       booleanEqualEvaluator;
    private Evaluator       booleanNotEqualEvaluator;

    protected void setUp() throws Exception {
        this.contextType = new ClassObjectType( Context.class );
        this.guestType = new ClassObjectType( Guest.class );
        this.seatingType = new ClassObjectType( Seating.class );
        this.lastSeatType = new ClassObjectType( LastSeat.class );
        this.countType = new ClassObjectType( Count.class );
        this.pathType = new ClassObjectType( Path.class );
        this.chosenType = new ClassObjectType( Chosen.class );

        this.integerEqualEvaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.INTEGER_TYPE,
                                                                                  Evaluator.EQUAL );
        this.integerNotEqualEvaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.INTEGER_TYPE,
                                                                                     Evaluator.NOT_EQUAL );

        this.objectEqualEvaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.OBJECT_TYPE,
                                                                                 Evaluator.EQUAL );
        this.objectNotEqualEvaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.OBJECT_TYPE,
                                                                                    Evaluator.NOT_EQUAL );

        this.booleanEqualEvaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.BOOLEAN_TYPE,
                                                                                  Evaluator.EQUAL );
        this.booleanNotEqualEvaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.BOOLEAN_TYPE,
                                                                                     Evaluator.NOT_EQUAL );

    }     
    
    public void test1() throws DuplicateRuleNameException, InvalidRuleException, IntrospectionException, RuleIntegrationException, RuleSetIntegrationException, InvalidPatternException, FactException, IOException, InterruptedException {
        RuleSet ruleSet = new RuleSet( "Miss Manners" );
        ruleSet.addRule( getAssignFirstSeatRule() );
//        ruleSet.addRule( getMakePath() );
        ruleSet.addRule( getFindSeating() );
//        ruleSet.addRule( getPathDone() );
//        ruleSet.addRule( getAreWeDone() );
//        ruleSet.addRule( getContinueProcessing() );
//        ruleSet.addRule( getAllDone() );
        
        final RuleBaseImpl ruleBase = new RuleBaseImpl();
        ruleBase.addRuleSet( ruleSet );
        
        final ReteooJungViewer viewer = new ReteooJungViewer(ruleBase);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                viewer.showGUI();
            }
        });               
              
        
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        InputStream is = getClass().getResourceAsStream( "/manners16.dat" );
        List list = getInputObjects(is);
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            workingMemory.assertObject( it.next() );
        }
        
        workingMemory.assertObject( new Count(0) );
        
        workingMemory.fireAllRules();
        
        while (viewer.isRunning()) {
            Thread.sleep( 1000 );
        }
        
    }

    /**
     * <pre>
     *   rule assignFirstSeat() {
     *       Context context;
     *       Guest guest;
     *       Count count;
     *       when {
     *           context : Context( state == Context.START_UP )
     *           guest : Guest()
     *           count : Count()
     *       } then {
     *           String guestName = guest.getName();
     *           drools.assert( new Seating( count.getValue(), 1, true, 1, guestName, 1, guestName) );
     *           drools.assert( new Path( count.getValue(), 1, guestName ) );
     *           count.setCount(  count.getValue() + 1 );
     *  
     *           System.out.println( &quot;seat 1 &quot; + guest.getName() + &quot; );
     *  
     *           context.setPath( Context.ASSIGN_SEATS );
     *       }
     *   } 
     * </pre>
     * 
     * 
     * @return
     * @throws IntrospectionException
     * @throws InvalidRuleException
     */
    private Rule getAssignFirstSeatRule() throws IntrospectionException,
                                         InvalidRuleException {
        final Rule rule = new Rule( "assignFirstSeat" );

        // -----------
        // context : Context( state == Context.START_UP )
        // -----------
        Column contextColumn = new Column( 0,
                                           contextType,
                                           "context" );

        contextColumn.addConstraint( getLiteralConstraint( contextColumn,
                                                           "state",
                                                           new Integer( Context.START_UP ),
                                                           this.integerEqualEvaluator ) );

        rule.addPattern( contextColumn );

        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // -----------
        // guest: Guest()
        // -----------
        Column guestColumn = new Column( 1,
                                         guestType,
                                         "guest" );

        rule.addPattern( guestColumn );

        final Declaration guestDeclaration = rule.getDeclaration( "guest" );

        // ------------
        // count : Count()
        // ------------
        Column countColumn = new Column( 2,
                                         countType,
                                         "count" );

        rule.addPattern( countColumn );

        final Declaration countDeclaration = rule.getDeclaration( "count" );

        Consequence consequence = new Consequence() {

            public void invoke(Activation activation) throws ConsequenceException {
                try {
                    Rule rule = activation.getRule();
                    Tuple tuple = activation.getTuple();
                    KnowledgeHelper drools = new DefaultKnowledgeHelper( rule,
                                                                         tuple );

                    Guest guest = (Guest) tuple.get( guestDeclaration );
                    Context context = (Context) tuple.get( contextDeclaration );
                    Count count = (Count) tuple.get( countDeclaration );

                    String guestName = guest.getName();

                    drools.assertObject( new Seating( count.getValue(),
                                                      0,
                                                      true,
                                                      1,
                                                      guestName,
                                                      1,
                                                      guestName ) );
                    drools.assertObject( new Path( count.getValue(),
                                                   1,
                                                   guestName ) );

                    count.setValue( count.getValue() + 1 );
                    drools.modifyObject( tuple.getFactHandleForDeclaration( countDeclaration ),
                                         count );

                    context.setState( Context.ASSIGN_SEATS );
                    drools.modifyObject( tuple.getFactHandleForDeclaration( contextDeclaration ),
                                         context );
                    System.out.println( "assigned first seat :  " + guest );

                } catch ( Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * <pre>
     *   rule makePath() {
     *       Context context;
     *       int seatingId, seatingPid, pathSeat;
     *       String pathGuestName;
     *  
     *       when {
     *           context : Context( state == Context.MAKE_PATH )
     *           Seating( seatingId:id, seatingPid:pid, pathDone == false )
     *           Path( id == seatingPid, pathGuestName:guest, pathSeat:seat )
     *           (not Path( id == seatingId, guestName == pathGuestName )
     *       } else {
     *           drools.assert( new Path( seatingId, pathSeat, pathGuestName ) );
     *  
     *       }
     *   } 
     * </pre>
     * 
     * @return
     * @throws IntrospectionException
     * @throws InvalidRuleException
     */
    private Rule getMakePath() throws IntrospectionException,
                           InvalidRuleException {
        final Rule rule = new Rule( "makePath" );

        // -----------
        // context : Context( state == Context.MAKE_PATH )
        // -----------
        Column contextColumn = new Column( 0,
                                           contextType );

        contextColumn.addConstraint( getLiteralConstraint( contextColumn,
                                                           "state",
                                                           new Integer( Context.MAKE_PATH ),
                                                           this.integerEqualEvaluator ) );

        rule.addPattern( contextColumn );

        // ---------------
        // Seating( seatingId:id, seatingPid:pid, pathDone == false )
        // ---------------
        Column seatingColumn = new Column( 1,
                                           seatingType );

        seatingColumn.addConstraint( getFieldBinding( seatingColumn,
                                                      "id",
                                                      "seatingId" ) );
        seatingColumn.addConstraint( getFieldBinding( seatingColumn,
                                                      "pid",
                                                      "seatingPid" ) );
        seatingColumn.addConstraint( getLiteralConstraint( seatingColumn,
                                                           "pathDone",
                                                           new Boolean( false ),
                                                           booleanEqualEvaluator ) );

        rule.addPattern( seatingColumn );

        final Declaration seatingIdDeclaration = rule.getDeclaration( "seatingId" );
        final Declaration seatingPidDeclaration = rule.getDeclaration( "seatingPid" );

        // -----------
        // Path( id == seatingPid, pathGuestName:guestName, pathSeat:seat )
        // -----------
        Column pathColumn = new Column( 2,
                                        pathType );

        pathColumn.addConstraint( getBoundVariableConstraint( pathColumn,
                                                              "id",
                                                              seatingPidDeclaration,
                                                              integerEqualEvaluator ) );
        pathColumn.addConstraint( getFieldBinding( pathColumn,
                                                   "guestName",
                                                   "pathGuestName" ) );
        pathColumn.addConstraint( getFieldBinding( pathColumn,
                                                   "seat",
                                                   "pathSeat" ) );

        rule.addPattern( pathColumn );

        final Declaration pathGuestNameDeclaration = rule.getDeclaration( "pathGuestName" );
        final Declaration pathSeatDeclaration = rule.getDeclaration( "pathSeat" );
        // -------------
        // (not Path( id == seatingId, guestName == pathGuestName )
        // -------------
        Column notPathColumn = new Column( 3,
                                           pathType );

        notPathColumn.addConstraint( getBoundVariableConstraint( notPathColumn,
                                                                 "id",
                                                                 seatingIdDeclaration,
                                                                 integerEqualEvaluator ) );
        notPathColumn.addConstraint( getBoundVariableConstraint( notPathColumn,
                                                                 "guestName",
                                                                 pathGuestNameDeclaration,
                                                                 objectEqualEvaluator ) );

        Not not = new Not();

        not.addChild( notPathColumn );

        rule.addPattern( not );

        // ------------
        // drools.assert( new Path( id, pathName, pathSeat ) );
        // ------------
        Consequence consequence = new Consequence() {

            public void invoke(Activation activation) throws ConsequenceException {
                try {
                    Rule rule = activation.getRule();
                    Tuple tuple = activation.getTuple();
                    KnowledgeHelper drools = new DefaultKnowledgeHelper( rule,
                                                                         tuple );

                    int id = ((Integer) tuple.get( seatingIdDeclaration )).intValue();
                    String guestName = (String) tuple.get( pathGuestNameDeclaration );
                    int seat = ((Integer) tuple.get( pathSeatDeclaration )).intValue();

                    Path path = new Path( id,
                                          seat,
                                          guestName );
                    
                    drools.assertObject( path );
                    
                    System.out.println( path );
                } catch ( Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * <pre>
     *   rule findSeating() {
     *      Context context;
     *      int seatingId, seatingPid;
     *      String seatingRightGuestName, leftGuestName;
     *      Sex rightGuestSex;
     *      Hobby rightGuestHobby;
     *      Count count;
     *      
     *      when {
     *          context : Context( state == Context.ASSIGN_SEATS )
     *          Seating( seatingId:id, seatingPid:pid, pathDone == true 
     *                   seatingRightSeat:rightSeat seatingRightGuestName:rightGuestName )
     *          Guest( name == seatingRightGuestName, rightGuestSex:sex, rightGuestHobby:hobby )
     *          Guest( leftGuestName:name , sex != rightGuestSex, hobby == rightGuestHobby )
     *   
     *          count : Count()
     *   
     *          not ( Path( id == seatingId, guestName == leftGuestName) )
     *          not ( Chosen( id == seatingId, guestName == leftGuestName, hobby == rightGuestHobby) )
     *      } then {
     *          int newSeat = rightSeat + 1;
     *          drools.assert( new Seating( coung.getValue(), rightSeat, rightSeatName, leftGuestName, newSeat, countValue, id, false );
     *          drools.assert( new Path( countValue, leftGuestName, newSeat );
     *          drools.assert( new Chosen( id, leftGuestName, rightGuestHobby ) );
     *   
     *          System.out.println( &quot;seat &quot; + rightSeat + &quot; &quot; + rightSeatName + &quot; &quot; + leftGuestName );
     *   
     *          count.setCount(  countValue + 1 );
     *          context.setPath( Context.MAKE_PATH );
     *      }
     *   } 
     * </pre>
     * 
     * @return
     * @throws IntrospectionException
     * @throws InvalidRuleException
     */
    private Rule getFindSeating() throws IntrospectionException,
                              InvalidRuleException {
        final Rule rule = new Rule( "findSeating" );

        // ---------------
        // context : Context( state == Context.ASSIGN_SEATS )
        // ---------------
        Column contextColumn = new Column( 0,
                                           contextType,
                                           "context" );

        contextColumn.addConstraint( getLiteralConstraint( contextColumn,
                                                           "state",
                                                           new Integer( Context.ASSIGN_SEATS ),
                                                           this.integerEqualEvaluator ) );

        rule.addPattern( contextColumn );

        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // -------------------------------
        // Seating( seatingId:id, seatingPid:pid, pathDone == true
        //          seatingRightSeat:rightSeat seatingRightGuestName:rightGuestName )
        // -------------------------------
        Column seatingColumn = new Column( 1,
                                           seatingType );

        seatingColumn.addConstraint( getFieldBinding( seatingColumn,
                                                      "id",
                                                      "seatingId" ) );
        seatingColumn.addConstraint( getFieldBinding( seatingColumn,
                                                      "pid",
                                                      "seatingPid" ) );
        seatingColumn.addConstraint( getLiteralConstraint( seatingColumn,
                                                           "pathDone",
                                                           new Boolean( true ),
                                                           this.booleanEqualEvaluator ) );
        seatingColumn.addConstraint( getFieldBinding( seatingColumn,
                                                      "rightSeat",
                                                      "seatingRightSeat" ) );
        seatingColumn.addConstraint( getFieldBinding( seatingColumn,
                                                      "rightGuestName",
                                                      "seatingRightGuestName" ) );

        rule.addPattern( seatingColumn );

        final Declaration seatingIdDeclaration = rule.getDeclaration( "seatingId" );
        final Declaration seatingPidDeclaration = rule.getDeclaration( "seatingPid" );
        final Declaration seatingRightGuestNameDeclaration = rule.getDeclaration( "seatingRightGuestName" );
        final Declaration seatingRightSeatDeclaration = rule.getDeclaration( "seatingRightSeat" );
        // --------------
        // Guest( name == seatingRightGuestName, rightGuestSex:sex, rightGuestHobby:hobby )
        // ---------------
        Column rightGuestColumn = new Column( 2,
                                              guestType );

        rightGuestColumn.addConstraint( getBoundVariableConstraint( rightGuestColumn,
                                                                    "name",
                                                                    seatingRightGuestNameDeclaration,
                                                                    objectEqualEvaluator ) );

        rightGuestColumn.addConstraint( getFieldBinding( rightGuestColumn,
                                                         "sex",
                                                         "rightGuestSex" ) );

        rightGuestColumn.addConstraint( getFieldBinding( rightGuestColumn,
                                                         "hobby",
                                                         "rightGuestHobby" ) );

        rule.addPattern( rightGuestColumn );

        final Declaration rightGuestSexDeclaration = rule.getDeclaration( "rightGuestSex" );
        final Declaration rightGuestHobbyDeclaration = rule.getDeclaration( "rightGuestHobby" );

        // ----------------
        // Guest( leftGuestName:name , sex != rightGuestSex, hobby == rightGuestHobby )
        // ----------------
        Column leftGuestColumn = new Column( 3,
                                             guestType );

        leftGuestColumn.addConstraint( getFieldBinding( leftGuestColumn,
                                                        "name",
                                                        "leftGuestHobby" ) );

        leftGuestColumn.addConstraint( getBoundVariableConstraint( leftGuestColumn,
                                                                   "sex",
                                                                   rightGuestSexDeclaration,
                                                                   objectEqualEvaluator ) );

        leftGuestColumn.addConstraint( getBoundVariableConstraint( rightGuestColumn,
                                                                   "hobby",
                                                                   rightGuestHobbyDeclaration,
                                                                   objectEqualEvaluator ) );
        rule.addPattern( leftGuestColumn );
        final Declaration leftGuestNameDeclaration = rule.getDeclaration( "lefttGuestName" );

        // ---------------
        // count : Count()
        // ---------------
        Column count = new Column( 2,
                                   countType,
                                   "count" );

        rule.addPattern( count );

        final Declaration countDeclaration = rule.getDeclaration( "count" );

        // --------------
        // not ( Path( id == seatingId, guestName == leftGuestName) )
        // --------------
        Column notPathColumn = new Column( 3,
                                           pathType );

        notPathColumn.addConstraint( getBoundVariableConstraint( notPathColumn,
                                                                 "id",
                                                                 seatingIdDeclaration,
                                                                 integerEqualEvaluator ) );

        notPathColumn.addConstraint( getBoundVariableConstraint( notPathColumn,
                                                                 "guestName",
                                                                 leftGuestNameDeclaration,
                                                                 objectEqualEvaluator ) );
        Not notPath = new Not();
        notPath.addChild( notPathColumn );
        rule.addPattern( notPath );
        // ------------
        // not ( Chosen( id == seatingId, guestName == leftGuestName, hobby == rightGuestHobby ) )
        // ------------
        Column notChosenColumn = new Column( 4,
                                             chosenType );

        notChosenColumn.addConstraint( getBoundVariableConstraint( notChosenColumn,
                                                                   "id",
                                                                   seatingIdDeclaration,
                                                                   integerEqualEvaluator ) );

        notChosenColumn.addConstraint( getBoundVariableConstraint( notChosenColumn,
                                                                   "guestName",
                                                                   leftGuestNameDeclaration,
                                                                   objectEqualEvaluator ) );

        notChosenColumn.addConstraint( getBoundVariableConstraint( notChosenColumn,
                                                                   "hobby",
                                                                   rightGuestHobbyDeclaration,
                                                                   objectEqualEvaluator ) );

        Not notChosen = new Not();
        notChosen.addChild( notChosenColumn );

        rule.addPattern( notChosen );

        // ------------
        // int newSeat = rightSeat + 1;
        // drools.assert( new Seating( coung.getValue(), rightSeat,
        // rightSeatName, leftGuestName, newSeat, countValue, id, false );
        // drools.assert( new Path( countValue, leftGuestName, newSeat );
        // drools.assert( new Chosen( id, leftGuestName, rightGuestHobby ) );
        // 
        // System.out.println( "seat " + rightSeat + " " + rightSeatName + " " +
        // leftGuestName );
        //
        // count.setCount( countValue + 1 );
        // context.setPath( Context.MAKE_PATH );
        // ------------
        Consequence consequence = new Consequence() {

            public void invoke(Activation activation) throws ConsequenceException {
                try {
                    Rule rule = activation.getRule();
                    Tuple tuple = activation.getTuple();
                    KnowledgeHelper drools = new DefaultKnowledgeHelper( rule,
                                                                         tuple );

                    Context context = (Context) tuple.get( contextDeclaration );
                    Count count = (Count) tuple.get( countDeclaration );
                    int seatId = ((Integer) tuple.get( seatingIdDeclaration )).intValue();
                    int seatingRightSeat = ((Integer) tuple.get( seatingRightSeatDeclaration )).intValue();
                    String leftGuestName = (String) tuple.get( leftGuestNameDeclaration );
                    String rightGuestName = (String) tuple.get( seatingRightGuestNameDeclaration );
                    Hobby rightGuestHobby = (Hobby) tuple.get( rightGuestHobbyDeclaration );

                    Seating seating = new Seating( count.getValue(),
                                                   seatId,
                                                   false,
                                                   seatingRightSeat,
                                                   leftGuestName,
                                                   seatingRightSeat + 1,
                                                   rightGuestName );
                    drools.assertObject( seating );

                    drools.assertObject( new Path( count.getValue(),
                                                   seatingRightSeat + 1,
                                                   leftGuestName ) );

                    drools.assertObject( new Chosen( seatId,
                                                     leftGuestName,
                                                     rightGuestHobby ) );

                    count.setValue( count.getValue() + 1 );
                    drools.modifyObject( tuple.getFactHandleForDeclaration( countDeclaration ),
                                         count );

                    context.setState( Context.MAKE_PATH );
                    drools.modifyObject( tuple.getFactHandleForDeclaration( contextDeclaration ),
                                         context );

                    System.out.println( seating );
                    
                } catch ( Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * 
     * rule pathDone() {
     *   Context context;
     *   Seating seating;
     *   when {
     *       context : Context( state == Context.MAKE_PATH )
     *       seating : Seating( pathDone == false )
     *   } then {
     *       seating.setPathDone( true );
     *       context.setName( Context.CHECK_DONE );
     *   }
     * }
     *
     * 
     * @return
     * @throws IntrospectionException
     * @throws InvalidRuleException
     */
    private Rule getPathDone() throws IntrospectionException,
                           InvalidRuleException {
        final Rule rule = new Rule( "pathDone" );

        // -----------
        // context : Context( state == Context.MAKE_PATH )
        // -----------
        Column contextColumn = new Column( 0,
                                           contextType );

        contextColumn.addConstraint( getLiteralConstraint( contextColumn,
                                                           "state",
                                                           new Integer( Context.MAKE_PATH ),
                                                           this.integerEqualEvaluator ) );

        rule.addPattern( contextColumn );
        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // ---------------
        // seating : Seating( pathDone == false )
        // ---------------
        Column seatingColumn = new Column( 1,
                                           seatingType,
                                           "seating" );

        seatingColumn.addConstraint( getLiteralConstraint( seatingColumn,
                                                           "pathDone",
                                                           new Boolean( false ),
                                                           booleanEqualEvaluator ) );

        rule.addPattern( seatingColumn );

        final Declaration seatingDeclaration = rule.getDeclaration( "seating" );

        // ------------
        // context.setName( Context.CHECK_DONE );        
        // seating.setPathDone( true ); 
        // ------------
        Consequence consequence = new Consequence() {

            public void invoke(Activation activation) throws ConsequenceException {
                try {
                    Rule rule = activation.getRule();
                    Tuple tuple = activation.getTuple();
                    KnowledgeHelper drools = new DefaultKnowledgeHelper( rule,
                                                                         tuple );

                    Context context = (Context) tuple.get( contextDeclaration );
                    Seating seating = (Seating) tuple.get( seatingDeclaration );
                
                    seating.setPathDone( true );
                    drools.modifyObject( tuple.getFactHandleForDeclaration( seatingDeclaration ),
                                         seating );                    
                    
                    context.setState( Context.CHECK_DONE );
                    drools.modifyObject( tuple.getFactHandleForDeclaration( contextDeclaration ),
                                         context );                                        
                } catch ( Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * 
     * rule areWeDone() {
     *     Context context;
     *     LastSeat lastSear;
     *     when {
     *         context : Context( state == Context.CHECK_DONE )
     *         LastSeat( lastSeat: seat )
     *         Seating( rightSeat == lastSeat ) 
     *     } then {
     *         context.setState( Context.PRINT_RESULTS );
     *     }
     * }
     *
     * 
     * @return
     * @throws IntrospectionException
     * @throws InvalidRuleException
     */
    private Rule getAreWeDone() throws IntrospectionException,
                           InvalidRuleException {
        final Rule rule = new Rule( "areWeDone" );

        // -----------
        // context : Context( state == Context.CHECK_DONE )
        // -----------
        Column contextColumn = new Column( 0,
                                           contextType );

        contextColumn.addConstraint( getLiteralConstraint( contextColumn,
                                                           "state",
                                                           new Integer( Context.CHECK_DONE ),
                                                           this.integerEqualEvaluator ) );

        rule.addPattern( contextColumn );
        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // ---------------
        // LastSeat( lastSeat: seat )
        // ---------------
        Column lastSeatColumn = new Column( 1,
                                           lastSeatType,
                                           null );

        lastSeatColumn.addConstraint( getFieldBinding( lastSeatColumn,
                                                         "seat",
                                                         "lastSeat" ) );        
        rule.addPattern( lastSeatColumn );
        final Declaration lastSeatDeclaration = rule.getDeclaration( "lastSeat" );
        // -------------
        // Seating( rightSeat == lastSeat )         
        // -------------
        Column seatingColumn = new Column( 2,
                                           seatingType,
                                           null );
        
        seatingColumn.addConstraint( getBoundVariableConstraint( seatingColumn,
                                                                 "rightSeat",
                                                                 lastSeatDeclaration,
                                                                 integerEqualEvaluator ) );        
        
        rule.addPattern( seatingColumn );
        
        // ------------
        // context.setName( Context.PRINT_RESULTS );        
        // ------------
        Consequence consequence = new Consequence() {

            public void invoke(Activation activation) throws ConsequenceException {
                try {
                    Rule rule = activation.getRule();
                    Tuple tuple = activation.getTuple();
                    KnowledgeHelper drools = new DefaultKnowledgeHelper( rule,
                                                                         tuple );

                    Context context = (Context) tuple.get( contextDeclaration );
                    context.setState( Context.PRINT_RESULTS );
                
                    drools.modifyObject( tuple.getFactHandleForDeclaration( contextDeclaration ),
                                         context );                                                            
                } catch ( Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

        };

        rule.setConsequence( consequence );

        return rule;
    }    
    
    /**
     * 
     * rule continue() {
     *   Context context;
     *   when {
     *       context : Context( state == Context.CHECK_DONE )
     *   } then {
     *      context.setState( Context.ASSIGN_SEATS );
     *   }
     * }
     * 
     * @return
     * @throws IntrospectionException
     * @throws InvalidRuleException
     */
    private Rule getContinueProcessing() throws IntrospectionException,
                           InvalidRuleException {
        final Rule rule = new Rule( "continueProcessng" );

        // -----------
        // context : Context( state == Context.CHECK_DONE )
        // -----------
        Column contextColumn = new Column( 0,
                                           contextType );

        contextColumn.addConstraint( getLiteralConstraint( contextColumn,
                                                           "state",
                                                           new Integer( Context.CHECK_DONE ),
                                                           this.integerEqualEvaluator ) );

        rule.addPattern( contextColumn );
        final Declaration contextDeclaration = rule.getDeclaration( "context" );
        
        // ------------
        // context.setName( Context.ASSIGN_SEATS );        
        // ------------
        Consequence consequence = new Consequence() {

            public void invoke(Activation activation) throws ConsequenceException {
                try {
                    Rule rule = activation.getRule();
                    Tuple tuple = activation.getTuple();
                    KnowledgeHelper drools = new DefaultKnowledgeHelper( rule,
                                                                         tuple );

                    Context context = (Context) tuple.get( contextDeclaration );
                    context.setState( Context.ASSIGN_SEATS );
                
                    drools.modifyObject( tuple.getFactHandleForDeclaration( contextDeclaration ),
                                         context );                                                            
                } catch ( Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

        };

        rule.setConsequence( consequence );

        return rule;
    }        

    /**
     * 
     * rule all_done() {
     *   Context context;
     *   when {
     *          context : Context( state == Context.PRINT_RESULTS )
     *   } then {
     *      
     *   }
     * }
     *
     * 
     * @return
     * @throws IntrospectionException
     * @throws InvalidRuleException
     */
    private Rule getAllDone() throws IntrospectionException,
                           InvalidRuleException {
        final Rule rule = new Rule( "alldone" );

        // -----------
        // context : Context( state == Context.PRINT_RESULTS )
        // -----------
        Column contextColumn = new Column( 0,
                                           contextType );

        contextColumn.addConstraint( getLiteralConstraint( contextColumn,
                                                           "state",
                                                           new Integer( Context.PRINT_RESULTS ),
                                                           this.integerEqualEvaluator ) );

        rule.addPattern( contextColumn );
        final Declaration contextDeclaration = rule.getDeclaration( "context" );
        
        // ------------
        //     
        // ------------
        Consequence consequence = new Consequence() {

            public void invoke(Activation activation) throws ConsequenceException {
                try {
                                                     
                } catch ( Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

        };

        rule.setConsequence( consequence );

        return rule;
    }    

    /**
     * Convert the facts from the <code>InputStream</code> to a list of
     * objects.
     */
    private List getInputObjects(InputStream inputStream) throws IOException
    {
        List list = new ArrayList( );

        BufferedReader br = new BufferedReader( new InputStreamReader( inputStream ) );

        String line;
        while ( (line = br.readLine( )) != null )
        {
            if ( line.trim( ).length( ) == 0 || line.trim( ).startsWith( ";" ) )
            {
                continue;
            }
            StringTokenizer st = new StringTokenizer( line,
                                                      "() " );
            String type = st.nextToken( );

            if ( "guest".equals( type ) )
            {
                if ( !"name".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'name' in: " + line );
                }
                String name = st.nextToken( );
                if ( !"sex".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'sex' in: " + line );
                }
                String sex = st.nextToken( );
                if ( !"hobby".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'hobby' in: " + line );
                }
                String hobby = st.nextToken( );

                Guest guest = new Guest( name,
                                         Sex.resolve(sex),
                                         Hobby.resolve(hobby));

                list.add( guest );               
            }

            if ( "last_seat".equals( type ) )
            {
                if ( !"seat".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'seat' in: " + line );
                }
                list.add( new LastSeat( new Integer( st.nextToken( ) ).intValue( ) ) );
            }

            if ( "context".equals( type ) )
            {
                if ( !"state".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'state' in: " + line );
                }
                list.add( new Context( st.nextToken( ) ) );
            }
        }
        inputStream.close( );

        return list;
    }    
    
    private InputStream generateData() {
        final String LINE_SEPARATOR = System.getProperty( "line.separator" );

        StringWriter writer = new StringWriter();

        int maxMale = numGuests / 2;
        int maxFemale = numGuests / 2;

        int maleCount = 0;
        int femaleCount = 0;

        // init hobbies
        List hobbyList = new ArrayList();
        for ( int i = 1; i <= maxHobbies; i++ ) {
            hobbyList.add( "h" + i );
        }

        Random rnd = new Random();
        for ( int i = 1; i <= numGuests; i++ ) {
            char sex = rnd.nextBoolean() ? 'm' : 'f';
            if ( sex == 'm' && maleCount == maxMale ) {
                sex = 'f';
            }
            if ( sex == 'f' && femaleCount == maxFemale ) {
                sex = 'm';
            }
            if ( sex == 'm' ) {
                maleCount++;
            }
            if ( sex == 'f' ) {
                femaleCount++;
            }

            List guestHobbies = new ArrayList( hobbyList );

            int numHobbies = minHobbies + rnd.nextInt( maxHobbies - minHobbies + 1 );
            for ( int j = 0; j < numHobbies; j++ ) {
                int hobbyIndex = rnd.nextInt( guestHobbies.size() );
                String hobby = (String) guestHobbies.get( hobbyIndex );
                writer.write( "(guest (name n" + i + ") (sex " + sex + ") (hobby " + hobby + "))" + LINE_SEPARATOR );
                guestHobbies.remove( hobbyIndex );
            }
        }
        writer.write( "(last_seat (seat " + numSeats + "))" + LINE_SEPARATOR );

        writer.write( LINE_SEPARATOR );
        writer.write( "(context (state start))" + LINE_SEPARATOR );

        return new ByteArrayInputStream( writer.getBuffer().toString().getBytes() );
    }

    public static int getIndex(Class clazz,
                               String name) throws IntrospectionException {
        PropertyDescriptor[] descriptors = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0; i < descriptors.length; i++ ) {
            if ( descriptors[i].getName().equals( name ) ) {
                return i;
            }
        }
        return -1;
    }

    private Constraint getLiteralConstraint(Column column,
                                            String fieldName,
                                            Object fieldValue,
                                            Evaluator evaluator) throws IntrospectionException {
        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        int index = getIndex( clazz,
                              fieldName );

        Field field = new MockField( fieldName,
                                     fieldValue,
                                     index );

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            index );

        return new LiteralConstraint( field,
                                      extractor,
                                      evaluator );
    }

    private Constraint getFieldBinding(Column column,
                                       String fieldName,
                                       String declarationName) throws IntrospectionException {
        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();
        int index = getIndex( clazz,
                              fieldName );

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            index );

        return new FieldBinding( declarationName,
                                 null,
                                 extractor,
                                 column.getIndex() );
    }

    private Constraint getBoundVariableConstraint(Column column,
                                                  String fieldName,
                                                  Declaration declaration,
                                                  Evaluator evaluator) throws IntrospectionException {
        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();
        int index = getIndex( clazz,
                              fieldName );

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            index );

        return new BoundVariableConstraint( extractor,
                                            declaration,
                                            evaluator );
    }
}
