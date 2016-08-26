/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.examples.manners;

import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.field.BooleanFieldImpl;
import org.drools.core.base.field.LongFieldImpl;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.GroupElementFactory;
import org.drools.core.rule.InvalidRuleException;
import org.drools.core.rule.MvelConstraintTestUtil;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.ConsequenceException;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.KnowledgeHelper;
import org.junit.Before;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public abstract class BaseMannersTest {
    /** Number of guests at the dinner (default: 16). */
    private final int       numGuests  = 16;

    /** Number of seats at the table (default: 16). */
    private final int       numSeats   = 16;

    /** Minimum number of hobbies each guest should have (default: 2). */
    private final int       minHobbies = 2;

    /** Maximun number of hobbies each guest should have (default: 3). */
    private final int       maxHobbies = 3;

    protected InternalKnowledgePackage pkg;

    private ClassObjectType contextType;
    private ClassObjectType guestType;
    private ClassObjectType seatingType;
    private ClassObjectType lastSeatType;
    private ClassObjectType countType;
    private ClassObjectType pathType;
    private ClassObjectType chosenType;

    ClassFieldAccessorStore store;

    @Before
    public void setUp() throws Exception {
        //Class shadow = ShadowProxyFactory.getProxy( Context.class );
        this.contextType = new ClassObjectType( Context.class );

        //shadow = ShadowProxyFactory.getProxy( Guest.class );
        this.guestType = new ClassObjectType( Guest.class );

        //shadow = ShadowProxyFactory.getProxy( Seating.class );
        this.seatingType = new ClassObjectType( Seating.class );

        //shadow = ShadowProxyFactory.getProxy( LastSeat.class );
        this.lastSeatType = new ClassObjectType( LastSeat.class );

        //shadow = ShadowProxyFactory.getProxy( Count.class );
        this.countType = new ClassObjectType( Count.class );

        //shadow = ShadowProxyFactory.getProxy( Path.class );
        this.pathType = new ClassObjectType( Path.class );

        //shadow = ShadowProxyFactory.getProxy( Chosen.class );
        this.chosenType = new ClassObjectType( Chosen.class );

        this.pkg = new KnowledgePackageImpl( "org.drools.examples.manners" );
        this.pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store = this.pkg.getClassFieldAccessorStore();
        store.setEagerWire( true );
        
        this.pkg.addRule( getAssignFirstSeatRule() );
        this.pkg.addRule( getFindSeating() );
        this.pkg.addRule( getMakePath() );
        this.pkg.addRule( getPathDone() );
        this.pkg.addRule( getContinueProcessing() );
        this.pkg.addRule( getAreWeDone() );
        this.pkg.addRule( getAllDone() );

    }

    /**
     * <pre>
     *    rule assignFirstSeat() {
     *        Context context;
     *        Guest guest;
     *        Count count;
     *        when {
     *            context : Context( state == Context.START_UP )
     *            guest : Guest()
     *            count : Count()
     *        } then {
     *            String guestName = guest.getName();
     *            drools.assert( new Seating( count.getValue(), 1, true, 1, guestName, 1, guestName) );
     *            drools.assert( new Path( count.getValue(), 1, guestName ) );
     *            count.setCount(  count.getValue() + 1 );
     *
     *            System.err.println( &quot;seat 1 &quot; + guest.getName() + &quot; );
     *
     *            context.setPath( Context.ASSIGN_SEATS );
     *        }
     *    }
     * </pre>
     *
     *
     * @return
     * @throws InvalidRuleException
     */
    private RuleImpl getAssignFirstSeatRule() throws InvalidRuleException {
        final RuleImpl rule = new RuleImpl( "assignFirstSeat" );

        // -----------
        // context : Context( state == Context.START_UP )
        // -----------
        final Pattern contextPattern = new Pattern( 0,
                                                    this.contextType,
                                                    "context" );

        contextPattern.addConstraint( getLiteralConstraint( contextPattern,
                                                            "state",
                                                            Context.START_UP ) );

        rule.addPattern( contextPattern );

        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // -----------
        // guest: Guest()
        // -----------
        final Pattern guestPattern = new Pattern( 1,
                                                  this.guestType,
                                                  "guest" );

        rule.addPattern( guestPattern );

        final Declaration guestDeclaration = rule.getDeclaration( "guest" );

        // ------------
        // count : Count()
        // ------------
        final Pattern countPattern = new Pattern( 2,
                                                  this.countType,
                                                  "count" );

        rule.addPattern( countPattern );

        final Declaration countDeclaration = rule.getDeclaration( "count" );

        final Consequence consequence = new Consequence() {

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    RuleImpl rule = drools.getRule();
                    LeftTuple tuple = (LeftTuple)drools.getTuple();

                    Guest guest = (Guest) drools.get( guestDeclaration );
                    Context context = (Context) drools.get( contextDeclaration );
                    Count count = (Count) drools.get( countDeclaration );

                    String guestName = guest.getName();

                    Seating seating = new Seating( count.getValue(),
                                                   0,
                                                   true,
                                                   1,
                                                   guestName,
                                                   1,
                                                   guestName );

                    drools.insert( seating );

                    Path path = new Path( count.getValue(),
                                          1,
                                          guestName );

                    drools.insert( path );

                    count.setValue( count.getValue() );
                    drools.update( tuple.get( countDeclaration ),
                                   count );

                    context.setState( Context.ASSIGN_SEATS );
                    //                    drools.update( tuple.get( contextDeclaration ),
                    //                            context );

                    drools.update( tuple.get( contextDeclaration ) );

                    //                    System.err.println( "assign first seat :  " + seating + " : " + path );

                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * <pre>
     *    rule findSeating() {
     *       Context context;
     *       int seatingId, seatingPid;
     *       String seatingRightGuestName, leftGuestName;
     *       Sex rightGuestSex;
     *       Hobby rightGuestHobby;
     *       Count count;
     *
     *       when {
     *           context : Context( state == Context.ASSIGN_SEATS )
     *           Seating( seatingId:id, seatingPid:pid, pathDone == true
     *                    seatingRightSeat:rightSeat seatingRightGuestName:rightGuestName )
     *           Guest( name == seatingRightGuestName, rightGuestSex:sex, rightGuestHobby:hobby )
     *           Guest( leftGuestName:name , sex != rightGuestSex, hobby == rightGuestHobby )
     *
     *           count : Count()
     *
     *           not ( Path( id == seatingId, guestName == leftGuestName) )
     *           not ( Chosen( id == seatingId, guestName == leftGuestName, hobby == rightGuestHobby) )
     *       } then {
     *           int newSeat = rightSeat + 1;
     *           drools.assert( new Seating( coung.getValue(), rightSeat, rightSeatName, leftGuestName, newSeat, countValue, id, false );
     *           drools.assert( new Path( countValue, leftGuestName, newSeat );
     *           drools.assert( new Chosen( id, leftGuestName, rightGuestHobby ) );
     *
     *           System.err.println( &quot;seat &quot; + rightSeat + &quot; &quot; + rightSeatName + &quot; &quot; + leftGuestName );
     *
     *           count.setCount(  countValue + 1 );
     *           context.setPath( Context.MAKE_PATH );
     *       }
     *    }
     * </pre>
     *
     * @return
     * @throws InvalidRuleException
     */
    private RuleImpl getFindSeating() throws InvalidRuleException {
        final RuleImpl rule = new RuleImpl( "findSeating" );

        // ---------------
        // context : Context( state == Context.ASSIGN_SEATS )
        // ---------------
        final Pattern contextPattern = new Pattern( 0,
                                                    this.contextType,
                                                    "context" );

        contextPattern.addConstraint( getLiteralConstraint( contextPattern,
                                                            "state",
                                                            Context.ASSIGN_SEATS ) );

        rule.addPattern( contextPattern );

        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // -------------------------------
        // Seating( seatingId:id, seatingPid:pid, pathDone == true
        // seatingRightSeat:rightSeat seatingRightGuestName:rightGuestName )
        // -------------------------------
        final Pattern seatingPattern = new Pattern( 1,
                                                    this.seatingType );

        setFieldDeclaration( seatingPattern,
                             "id",
                             "seatingId" );

        setFieldDeclaration( seatingPattern,
                             "pid",
                             "seatingPid" );

        seatingPattern.addConstraint( getLiteralConstraint( seatingPattern,
                                                            "pathDone",
                                                            true ) );

        setFieldDeclaration( seatingPattern,
                             "rightSeat",
                             "seatingRightSeat" );

        setFieldDeclaration( seatingPattern,
                             "rightGuestName",
                             "seatingRightGuestName" );

        rule.addPattern( seatingPattern );

        final Declaration seatingIdDeclaration = rule.getDeclaration( "seatingId" );
        final Declaration seatingPidDeclaration = rule.getDeclaration( "seatingPid" );
        final Declaration seatingRightGuestNameDeclaration = rule.getDeclaration( "seatingRightGuestName" );
        final Declaration seatingRightSeatDeclaration = rule.getDeclaration( "seatingRightSeat" );
        // --------------
        // Guest( name == seatingRightGuestName, rightGuestSex:sex,
        // rightGuestHobby:hobby )
        // ---------------
        final Pattern rightGuestPattern = new Pattern( 2,
                                                       this.guestType );

        rightGuestPattern.addConstraint( getBoundVariableConstraint( rightGuestPattern,
                                                                     "name",
                                                                     seatingRightGuestNameDeclaration,
                                                                     "==" ) );

        setFieldDeclaration( rightGuestPattern,
                             "sex",
                             "rightGuestSex" );

        setFieldDeclaration( rightGuestPattern,
                             "hobby",
                             "rightGuestHobby" );

        rule.addPattern( rightGuestPattern );

        final Declaration rightGuestSexDeclaration = rule.getDeclaration( "rightGuestSex" );
        final Declaration rightGuestHobbyDeclaration = rule.getDeclaration( "rightGuestHobby" );

        // ----------------
        // Guest( leftGuestName:name , sex != rightGuestSex, hobby ==
        // rightGuestHobby )
        // ----------------
        final Pattern leftGuestPattern = new Pattern( 3,
                                                      this.guestType );

        setFieldDeclaration( leftGuestPattern,
                             "name",
                             "leftGuestName" );

        leftGuestPattern.addConstraint( getBoundVariableConstraint( rightGuestPattern,
                                                                    "hobby",
                                                                    rightGuestHobbyDeclaration,
                                                                    "==" ) );

        leftGuestPattern.addConstraint( getBoundVariableConstraint( leftGuestPattern,
                                                                    "sex",
                                                                    rightGuestSexDeclaration,
                                                                    "!=" ) );

        rule.addPattern( leftGuestPattern );
        final Declaration leftGuestNameDeclaration = rule.getDeclaration( "leftGuestName" );

        // ---------------
        // count : Count()
        // ---------------
        final Pattern count = new Pattern( 4,
                                           this.countType,
                                           "count" );

        rule.addPattern( count );

        final Declaration countDeclaration = rule.getDeclaration( "count" );

        // --------------
        // not ( Path( id == seatingId, guestName == leftGuestName) )
        // --------------
        final Pattern notPathPattern = new Pattern( 5,
                                                    this.pathType );

        notPathPattern.addConstraint( getBoundVariableConstraint( notPathPattern,
                                                                  "id",
                                                                  seatingIdDeclaration,
                                                                  "==" ) );

        notPathPattern.addConstraint( getBoundVariableConstraint( notPathPattern,
                                                                  "guestName",
                                                                  leftGuestNameDeclaration,
                                                                  "==" ) );
        final GroupElement notPath = GroupElementFactory.newNotInstance();
        notPath.addChild( notPathPattern );
        rule.addPattern( notPath );
        // ------------
        // not ( Chosen( id == seatingId, guestName == leftGuestName, hobby ==
        // rightGuestHobby ) )
        // ------------
        final Pattern notChosenPattern = new Pattern( 6,
                                                      this.chosenType );

        notChosenPattern.addConstraint( getBoundVariableConstraint( notChosenPattern,
                                                                    "id",
                                                                    seatingIdDeclaration,
                                                                    "==" ) );

        notChosenPattern.addConstraint( getBoundVariableConstraint( notChosenPattern,
                                                                    "guestName",
                                                                    leftGuestNameDeclaration,
                                                                    "==" ) );

        notChosenPattern.addConstraint( getBoundVariableConstraint( notChosenPattern,
                                                                    "hobby",
                                                                    rightGuestHobbyDeclaration,
                                                                    "==" ) );

        final GroupElement notChosen = GroupElementFactory.newNotInstance();
        notChosen.addChild( notChosenPattern );

        rule.addPattern( notChosen );

        // ------------
        // int newSeat = rightSeat + 1;
        // drools.assert( new Seating( coung.getValue(), rightSeat,
        // rightSeatName, leftGuestName, newSeat, countValue, id, false );
        // drools.assert( new Path( countValue, leftGuestName, newSeat );
        // drools.assert( new Chosen( id, leftGuestName, rightGuestHobby ) );
        //
        // System.err.println( "seat " + rightSeat + " " + rightSeatName + " " +
        // leftGuestName );
        //
        // count.setCount( countValue + 1 );
        // context.setPath( Context.MAKE_PATH );
        // ------------
        final Consequence consequence = new Consequence() {

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    //                    MemoryVisitor visitor = new MemoryVisitor( ( InternalWorkingMemory ) workingMemory );
                    //                    visitor.visit( workingMemory.getRuleBase() );

                    RuleImpl rule = drools.getRule();
                    LeftTuple tuple = (LeftTuple)drools.getTuple();

                    Context context = (Context) drools.get( contextDeclaration );
                    Count count = (Count) drools.get( countDeclaration );
                    int seatId = seatingIdDeclaration.getExtractor().getIntValue( (InternalWorkingMemory) workingMemory,
                                                                                  tuple.get( seatingIdDeclaration ).getObject() );
                    int seatingRightSeat = seatingRightSeatDeclaration.getExtractor().getIntValue( (InternalWorkingMemory) workingMemory,
                                                                                                   tuple.get( seatingRightSeatDeclaration ).getObject() );

                    String leftGuestName = (String) drools.get( leftGuestNameDeclaration );
                    String rightGuestName = (String) drools.get( seatingRightGuestNameDeclaration );
                    Hobby rightGuestHobby = (Hobby) drools.get( rightGuestHobbyDeclaration );

                    Seating seating = new Seating( count.getValue(),
                                                   seatId,
                                                   false,
                                                   seatingRightSeat,
                                                   rightGuestName,
                                                   seatingRightSeat + 1,
                                                   leftGuestName );
                    drools.insert( seating );

                    Path path = new Path( count.getValue(),
                                          seatingRightSeat + 1,
                                          leftGuestName );

                    drools.insert( path );

                    Chosen chosen = new Chosen( seatId,
                                                leftGuestName,
                                                rightGuestHobby );

                    drools.insert( chosen );
                    count.setValue( count.getValue() + 1 );

                    //                    if ( count.getValue() == 5 ) {
                    //                        drools.retractObject( tuple.getFactHandleForDeclaration( countDeclaration ) );
                    //                    } else {
                    //                        drools.update( tuple.getFactHandleForDeclaration( countDeclaration ),
                    //                                             count );
                    //                    }

                    drools.update( tuple.get( countDeclaration ),
                                   count );

                    context.setState( Context.MAKE_PATH );
                    drools.update( tuple.get( contextDeclaration ),
                                   context );

                    System.err.println( "find seating : " + seating + " : " + path + " : " + chosen );

                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * <pre>
     *    rule makePath() {
     *        Context context;
     *        int seatingId, seatingPid, pathSeat;
     *        String pathGuestName;
     *
     *        when {
     *            Context( state == Context.MAKE_PATH )
     *            Seating( seatingId:id, seatingPid:pid, pathDone == false )
     *            Path( id == seatingPid, pathGuestName:guest, pathSeat:seat )
     *            (not Path( id == seatingId, guestName == pathGuestName )
     *        } else {
     *            drools.assert( new Path( seatingId, pathSeat, pathGuestName ) );
     *
     *        }
     *    }
     * </pre>
     *
     * @return
     * @throws InvalidRuleException
     */
    private RuleImpl getMakePath() throws InvalidRuleException {
        final RuleImpl rule = new RuleImpl( "makePath" );

        // -----------
        // context : Context( state == Context.MAKE_PATH )
        // -----------
        final Pattern contextPattern = new Pattern( 0,
                                                    this.contextType );

        contextPattern.addConstraint( getLiteralConstraint( contextPattern,
                                                            "state",
                                                            Context.MAKE_PATH ) );

        rule.addPattern( contextPattern );

        // ---------------
        // Seating( seatingId:id, seatingPid:pid, pathDone == false )
        // ---------------
        final Pattern seatingPattern = new Pattern( 1,
                                                    this.seatingType );

        setFieldDeclaration( seatingPattern,
                             "id",
                             "seatingId" );

        setFieldDeclaration( seatingPattern,
                             "pid",
                             "seatingPid" );

        seatingPattern.addConstraint( getLiteralConstraint( seatingPattern,
                                                            "pathDone",
                                                            false ) );

        rule.addPattern( seatingPattern );

        final Declaration seatingIdDeclaration = rule.getDeclaration( "seatingId" );
        final Declaration seatingPidDeclaration = rule.getDeclaration( "seatingPid" );

        // -----------
        // Path( id == seatingPid, pathGuestName:guestName, pathSeat:seat )
        // -----------
        final Pattern pathPattern = new Pattern( 2,
                                                 this.pathType );

        pathPattern.addConstraint( getBoundVariableConstraint( pathPattern,
                                                               "id",
                                                               seatingPidDeclaration,
                                                               "==" ) );

        setFieldDeclaration( pathPattern,
                             "guestName",
                             "pathGuestName" );

        setFieldDeclaration( pathPattern,
                             "seat",
                             "pathSeat" );

        rule.addPattern( pathPattern );

        final Declaration pathGuestNameDeclaration = rule.getDeclaration( "pathGuestName" );
        final Declaration pathSeatDeclaration = rule.getDeclaration( "pathSeat" );
        // -------------
        // (not Path( id == seatingId, guestName == pathGuestName )
        // -------------
        final Pattern notPathPattern = new Pattern( 3,
                                                    this.pathType );

        notPathPattern.addConstraint( getBoundVariableConstraint( notPathPattern,
                                                                  "id",
                                                                  seatingIdDeclaration,
                                                                  "==" ) );
        notPathPattern.addConstraint( getBoundVariableConstraint( notPathPattern,
                                                                  "guestName",
                                                                  pathGuestNameDeclaration,
                                                                  "==" ) );

        final GroupElement not = GroupElementFactory.newNotInstance();

        not.addChild( notPathPattern );

        rule.addPattern( not );

        // ------------
        // drools.assert( new Path( id, pathName, pathSeat ) );
        // ------------
        final Consequence consequence = new Consequence() {

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    RuleImpl rule = drools.getRule();
                    LeftTuple tuple = (LeftTuple)drools.getTuple();

                    int id = seatingIdDeclaration.getExtractor().getIntValue( (InternalWorkingMemory) workingMemory,
                                                                              tuple.get( seatingIdDeclaration ).getObject() );
                    int seat = pathSeatDeclaration.getExtractor().getIntValue( (InternalWorkingMemory) workingMemory,
                                                                               tuple.get( pathSeatDeclaration ).getObject() );
                    String guestName = (String) drools.get( pathGuestNameDeclaration );

                    Path path = new Path( id,
                                          seat,
                                          guestName );

                    drools.insert( path );

                    //System.err.println( "make path : " + path );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     *
     * <pre>
     * rule pathDone() {
     *     Context context; Seating seating;
     *     when {
     *         context : Context( state == Context.MAKE_PATH )
     *         seating : Seating( pathDone == false )
     *     } then {
     *         seating.setPathDone( true );
     *         context.setName( Context.CHECK_DONE );
     *     }
     * }
     * </pre>
     *
     * @return
     * @throws InvalidRuleException
     */
    private RuleImpl getPathDone() throws InvalidRuleException {
        final RuleImpl rule = new RuleImpl( "pathDone" );

        // -----------
        // context : Context( state == Context.MAKE_PATH )
        // -----------
        final Pattern contextPattern = new Pattern( 0,
                                                    this.contextType,
                                                    "context" );

        contextPattern.addConstraint( getLiteralConstraint( contextPattern,
                                                            "state",
                                                            Context.MAKE_PATH ) );

        rule.addPattern( contextPattern );
        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // ---------------
        // seating : Seating( pathDone == false )
        // ---------------
        final Pattern seatingPattern = new Pattern( 1,
                                                    this.seatingType,
                                                    "seating" );

        seatingPattern.addConstraint( getLiteralConstraint( seatingPattern,
                                                            "pathDone",
                                                            false ) );

        rule.addPattern( seatingPattern );

        final Declaration seatingDeclaration = rule.getDeclaration( "seating" );

        // ------------
        // context.setName( Context.CHECK_DONE );
        // seating.setPathDone( true );
        // ------------
        final Consequence consequence = new Consequence() {

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    RuleImpl rule = drools.getRule();
                    LeftTuple tuple = (LeftTuple)drools.getTuple();

                    Context context = (Context) drools.get( contextDeclaration );
                    Seating seating = (Seating) drools.get( seatingDeclaration );

                    seating.setPathDone( true );

                    //                    if ( seating.getId() == 6 ) {
                    //                        System.err.println( "pause" );
                    //                    }
                    drools.update( tuple.get( seatingDeclaration ) );

                    context.setState( Context.CHECK_DONE );
                    drools.update( tuple.get( contextDeclaration ),
                                   context );
                    //System.err.println( "path done" + seating );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * <pre>
     * rule areWeDone() {
     *     Context context; LastSeat lastSear;
     *     when {
     *         context : Context( state == Context.CHECK_DONE )
     *         LastSeat( lastSeat: seat )
     *         Seating( rightSeat == lastSeat )
     *     } then {
     *         context.setState(Context.PRINT_RESULTS );
     *     }
     * }
     * </pre>
     *
     * @return
     * @throws InvalidRuleException
     */
    private RuleImpl getAreWeDone() throws InvalidRuleException {
        final RuleImpl rule = new RuleImpl( "areWeDone" );

        // -----------
        // context : Context( state == Context.CHECK_DONE )
        // -----------
        final Pattern contextPattern = new Pattern( 0,
                                                    this.contextType,
                                                    "context" );

        contextPattern.addConstraint( getLiteralConstraint( contextPattern,
                                                            "state",
                                                            Context.CHECK_DONE ) );

        rule.addPattern( contextPattern );
        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // ---------------
        // LastSeat( lastSeat: seat )
        // ---------------
        final Pattern lastSeatPattern = new Pattern( 1,
                                                     this.lastSeatType );

        setFieldDeclaration( lastSeatPattern,
                             "seat",
                             "lastSeat" );

        rule.addPattern( lastSeatPattern );
        final Declaration lastSeatDeclaration = rule.getDeclaration( "lastSeat" );
        // -------------
        // Seating( rightSeat == lastSeat )
        // -------------
        final Pattern seatingPattern = new Pattern( 2,
                                                    this.seatingType,
                                                    null );

        seatingPattern.addConstraint( getBoundVariableConstraint( seatingPattern,
                                                                  "rightSeat",
                                                                  lastSeatDeclaration,
                                                                  "==" ) );

        rule.addPattern( seatingPattern );

        // ------------
        // context.setName( Context.PRINT_RESULTS );
        // ------------
        final Consequence consequence = new Consequence() {

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    RuleImpl rule = drools.getRule();
                    LeftTuple tuple = (LeftTuple)drools.getTuple();

                    Context context = (Context) drools.get( contextDeclaration );
                    context.setState( Context.PRINT_RESULTS );

                    drools.update( tuple.get( contextDeclaration ),
                                   context );

                    //                    System.err.println( "We Are Done!!!" );
                } catch ( Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * <pre>
     * rule continue() {
     *     Context context;
     *     when {
     *         context : Context( state == Context.CHECK_DONE )
     *     } then {
     *         context.setState( Context.ASSIGN_SEATS );
     *     }
     * }
     * </pre>
     * @return
     * @throws InvalidRuleException
     */
    private RuleImpl getContinueProcessing() throws InvalidRuleException {
        final RuleImpl rule = new RuleImpl( "continueProcessng" );

        // -----------
        // context : Context( state == Context.CHECK_DONE )
        // -----------
        final Pattern contextPattern = new Pattern( 0,
                                                    this.contextType,
                                                    "context" );

        contextPattern.addConstraint( getLiteralConstraint( contextPattern,
                                                            "state",
                                                            Context.CHECK_DONE ) );

        rule.addPattern( contextPattern );
        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // ------------
        // context.setName( Context.ASSIGN_SEATS );
        // ------------
        final Consequence consequence = new Consequence() {

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    RuleImpl rule = drools.getRule();
                    LeftTuple tuple = (LeftTuple)drools.getTuple();

                    Context context = (Context) drools.get( contextDeclaration );
                    context.setState( Context.ASSIGN_SEATS );

                    drools.update( tuple.get( contextDeclaration ),
                                   context );

                    //System.err.println( "continue processing" );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * <pre>
     * rule all_done() {
     *     Context context;
     *     when {
     *         context : Context( state == Context.PRINT_RESULTS )
     *     } then {
     *     }
     * }
     * </pre>
     *
     * @return
     * @throws InvalidRuleException
     */
    private RuleImpl getAllDone() throws InvalidRuleException {
        final RuleImpl rule = new RuleImpl( "alldone" );

        // -----------
        // context : Context( state == Context.PRINT_RESULTS )
        // -----------
        final Pattern contextPattern = new Pattern( 0,
                                                    this.contextType );

        contextPattern.addConstraint( getLiteralConstraint( contextPattern,
                                                            "state",
                                                            Context.PRINT_RESULTS ) );

        rule.addPattern( contextPattern );
        final Declaration contextDeclaration = rule.getDeclaration( "context" );

        // ------------
        //
        // ------------
        final Consequence consequence = new Consequence() {

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    System.err.println( "all done" );
                } catch ( Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * Convert the facts from the <code>InputStream</code> to a list of
     * objects.
     */
    protected List getInputObjects(final InputStream inputStream) throws IOException {
        final List list = new ArrayList();

        final BufferedReader br = new BufferedReader( new InputStreamReader( inputStream ) );

        String line;
        while ( (line = br.readLine()) != null ) {
            if ( line.trim().length() == 0 || line.trim().startsWith( ";" ) ) {
                continue;
            }
            final StringTokenizer st = new StringTokenizer( line,
                                                            "() " );
            final String type = st.nextToken();

            if ( "guest".equals( type ) ) {
                if ( !"name".equals( st.nextToken() ) ) {
                    throw new IOException( "expected 'name' in: " + line );
                }
                final String name = st.nextToken();
                if ( !"sex".equals( st.nextToken() ) ) {
                    throw new IOException( "expected 'sex' in: " + line );
                }
                final String sex = st.nextToken();
                if ( !"hobby".equals( st.nextToken() ) ) {
                    throw new IOException( "expected 'hobby' in: " + line );
                }
                final String hobby = st.nextToken();

                final Guest guest = new Guest( name,
                                               Sex.resolve( sex ),
                                               Hobby.resolve( hobby ) );

                list.add( guest );
            }

            if ( "last_seat".equals( type ) ) {
                if ( !"seat".equals( st.nextToken() ) ) {
                    throw new IOException( "expected 'seat' in: " + line );
                }
                list.add( new LastSeat( Integer.parseInt( st.nextToken() ) ) );
            }

            if ( "context".equals( type ) ) {
                if ( !"state".equals( st.nextToken() ) ) {
                    throw new IOException( "expected 'state' in: " + line );
                }
                list.add( new Context( st.nextToken() ) );
            }
        }
        inputStream.close();

        return list;
    }

    private InputStream generateData() {
        final String LINE_SEPARATOR = System.getProperty( "line.separator" );

        final StringWriter writer = new StringWriter();

        final int maxMale = this.numGuests / 2;
        final int maxFemale = this.numGuests / 2;

        int maleCount = 0;
        int femaleCount = 0;

        // init hobbies
        final List hobbyList = new ArrayList();
        for ( int i = 1; i <= this.maxHobbies; i++ ) {
            hobbyList.add( "h" + i );
        }

        final Random rnd = new Random();
        for ( int i = 1; i <= this.numGuests; i++ ) {
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

            final List guestHobbies = new ArrayList( hobbyList );

            final int numHobbies = this.minHobbies + rnd.nextInt( this.maxHobbies - this.minHobbies + 1 );
            for ( int j = 0; j < numHobbies; j++ ) {
                final int hobbyIndex = rnd.nextInt( guestHobbies.size() );
                final String hobby = (String) guestHobbies.get( hobbyIndex );
                writer.write( "(guest (name n" + i + ") (sex " + sex + ") (hobby " + hobby + "))" + LINE_SEPARATOR );
                guestHobbies.remove( hobbyIndex );
            }
        }
        writer.write( "(last_seat (seat " + this.numSeats + "))" + LINE_SEPARATOR );

        writer.write( LINE_SEPARATOR );
        writer.write( "(context (state start))" + LINE_SEPARATOR );

        return new ByteArrayInputStream( writer.getBuffer().toString().getBytes() );
    }

    public static int getIndex(final Class clazz,
                               final String name) throws IntrospectionException {
        final PropertyDescriptor[] descriptors = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0; i < descriptors.length; i++ ) {
            if ( descriptors[i].getName().equals( name ) ) {
                return i;
            }
        }
        return -1;
    }

    private AlphaNodeFieldConstraint getLiteralConstraint(final Pattern pattern,
                                                          final String fieldName,
                                                          final int fieldValue) {
        final Class clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();

        final InternalReadAccessor extractor = store.getReader( clazz,
                                                                fieldName );

        final FieldValue field = new LongFieldImpl( fieldValue );

        return new MvelConstraintTestUtil( fieldName + " == " + fieldValue,
                                           new LongFieldImpl( fieldValue ),
                                           extractor );
    }

    private AlphaNodeFieldConstraint getLiteralConstraint(final Pattern pattern,
                                                          final String fieldName,
                                                          final boolean fieldValue) {
        final Class clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();

        final InternalReadAccessor extractor = store.getReader( clazz,
                                                                fieldName );

        final FieldValue field = new BooleanFieldImpl( fieldValue );

        return new MvelConstraintTestUtil( fieldName + " == " + fieldValue,
                new BooleanFieldImpl( fieldValue ),
                extractor );
    }

    private void setFieldDeclaration(final Pattern pattern,
                                     final String fieldName,
                                     final String identifier) {
        final Class clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();

        final InternalReadAccessor extractor = store.getReader( clazz,
                                                                fieldName );

        pattern.addDeclaration( identifier ).setReadAccessor( extractor );
    }

    private BetaNodeFieldConstraint getBoundVariableConstraint(final Pattern pattern,
                                                               final String fieldName,
                                                               final Declaration declaration,
                                                               final String operator) {
        final Class clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();

        final InternalReadAccessor extractor = store.getReader( clazz,
                                                                fieldName );

        String expression = fieldName + " " + operator + " " + declaration.getIdentifier();
        return new MvelConstraintTestUtil(expression, declaration, extractor);
    }
}
