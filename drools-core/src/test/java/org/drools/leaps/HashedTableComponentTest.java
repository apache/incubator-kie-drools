package org.drools.leaps;

/*
 * Copyright 2005 JBoss Inc
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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Iterator;

import org.drools.DroolsTestCase;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.common.DefaultFactHandle;
import org.drools.examples.manners.Chosen;
import org.drools.examples.manners.Context;
import org.drools.examples.manners.Count;
import org.drools.examples.manners.Guest;
import org.drools.examples.manners.LastSeat;
import org.drools.examples.manners.Path;
import org.drools.examples.manners.Seating;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.VariableConstraint;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.Evaluator;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.MockField;
import org.drools.spi.Tuple;

/**
 * 
 * @author Alexander Bagerman
 *
 */
public class HashedTableComponentTest extends DroolsTestCase {

    protected Package       pkg;


    private ClassObjectType pathType;


    private Evaluator       objectEqualEvaluator;

    private Evaluator       integerEqualEvaluator;

    private Path            p1Alex;

    private Path            p2John;

    private Path            p3Mike;

    private Path            p4Alex;

    private Path            p5Alex;

    protected void setUp() throws Exception {

        this.p1Alex = new Path( 1, 1, "Alex" );
        this.p2John = new Path( 2, 1, "John" );
        this.p3Mike = new Path( 3, 1, "Mike" );
        this.p4Alex = new Path( 4, 1, "Alex" );
        this.p5Alex = new Path( 1, 4, "Alex" );
        this.pathType = new ClassObjectType( Path.class );

        this.integerEqualEvaluator = ValueType.INTEGER_TYPE.getEvaluator( Operator.EQUAL );
        this.objectEqualEvaluator = ValueType.OBJECT_TYPE.getEvaluator( Operator.EQUAL );

        this.pkg = new Package( "Miss Manners for Hashed Table Component" );
    }

    /*
     * Test method for
     * 'org.drools.leaps.ColumnConstraints.evaluateAlphas(FactHandleImpl, Token,
     * WorkingMemoryImpl)'
     */
    public void testHashedTableComponentNoAlpha() throws Exception {

        Rule r = this.getMakePathNoAlpha( );
        this.pkg.addRule( r );
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.LEAPS );
        ruleBase.addPackage( this.pkg );
        final LeapsWorkingMemory wm = (LeapsWorkingMemory) ruleBase.newWorkingMemory( );
        wm.assertObject( this.p1Alex );
        wm.assertObject( this.p2John );
        wm.assertObject( this.p3Mike );
        wm.assertObject( this.p4Alex );
        wm.assertObject( this.p5Alex );

        Iterator it;
        Tuple tuple;
        FactTable ft = wm.getFactTable( Path.class );
        ColumnConstraints notConstraint = (ColumnConstraints) ft.getHashedConstraints( )
                                                                .next( );
        LeapsFactHandle[] fh = new LeapsFactHandle[1];
        fh[0] = new LeapsFactHandle( 99, p1Alex );
        tuple = new LeapsTuple( fh, null, null );

        it = ft.reverseOrderIterator( tuple, notConstraint );
        assertSame( "Expected matching",
                    ( (LeapsFactHandle) it.next( ) ).getObject( ),
                    p1Alex );
        assertSame( "Expected matching",
                    ( (LeapsFactHandle) it.next( ) ).getObject( ),
                    p5Alex );
        assertFalse( "Did not expect any more data", it.hasNext( ) );

        fh[0] = new LeapsFactHandle( 99, p3Mike );
        tuple = new LeapsTuple( fh, null, null );

        it = ft.reverseOrderIterator( tuple, notConstraint );
        assertSame( "Expected matching",
                    ( (LeapsFactHandle) it.next( ) ).getObject( ),
                    p3Mike );
        assertFalse( "Did not expect any more data", it.hasNext( ) );
    }

    /*
     * Test method for
     * 'org.drools.leaps.ColumnConstraints.evaluateAlphas(FactHandleImpl, Token,
     * WorkingMemoryImpl)'
     */
    public void testHashedTableComponentAlpha() throws Exception {

        Rule r = this.getMakePathAlpha( );
        this.pkg.addRule( r );
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.LEAPS );
        ruleBase.addPackage( this.pkg );
        final LeapsWorkingMemory wm = (LeapsWorkingMemory) ruleBase.newWorkingMemory( );

        wm.assertObject( this.p1Alex );
        wm.assertObject( this.p2John );
        wm.assertObject( this.p3Mike );
        wm.assertObject( this.p4Alex );
        wm.assertObject( this.p5Alex );

        Iterator it;
        Tuple tuple;
        FactTable ft = wm.getFactTable( Path.class );
        ColumnConstraints notConstraint = (ColumnConstraints) ft.getHashedConstraints( )
                                                                .next( );
        LeapsFactHandle[] fh = new LeapsFactHandle[1];
        fh[0] = new LeapsFactHandle( 99, p1Alex );
        tuple = new LeapsTuple( fh, null, null );

        it = ft.reverseOrderIterator( tuple, notConstraint );
        assertFalse( "Did not expect any more data", it.hasNext( ) );

        fh[0] = new LeapsFactHandle( 99, p3Mike );
        tuple = new LeapsTuple( fh, null, null );

        it = ft.reverseOrderIterator( tuple, notConstraint );
        assertSame( "Expected matching",
                    ( (LeapsFactHandle) it.next( ) ).getObject( ),
                    p3Mike );
        assertFalse( "Did not expect any more data", it.hasNext( ) );
    }

    /**
     * <pre>
     *     rule makePath() {
     *         int pathId;
     *         String pathGuestName;
     *    
     *         when {
     *             Path( pathId:id, pathGuestName:guest )
     *             (not Path( id == pathId, guestName == pathGuestName )
     *         } then {
     *             nothing;
     *    
     *         }
     *     } 
     * </pre>
     * 
     * @return
     * @throws IntrospectionException
     * @throws InvalidRuleException
     */
    private Rule getMakePathAlpha() throws IntrospectionException, InvalidRuleException {
        final Rule rule = new Rule( "makePathNoAlpha" );
        // -----------
        // Path( id == seatingPid, pathGuestName:guestName, pathSeat:seat )
        // -----------
        final Column pathColumn = new Column( 0, this.pathType );
        setFieldDeclaration( pathColumn, "id", "pathId" );

        setFieldDeclaration( pathColumn, "guestName", "pathGuestName" );

        rule.addPattern( pathColumn );

        final Declaration pathIdDeclaration = rule.getDeclaration( "pathId" );
        final Declaration pathGuestNameDeclaration = rule.getDeclaration( "pathGuestName" );
        // -------------
        // (not Path( id == seatingId, guestName == pathGuestName )
        // -------------
        final Column notPathColumn = new Column( 3, this.pathType );

        notPathColumn.addConstraint( getLiteralConstraint( notPathColumn,
                                                           "guestName",
                                                           "Mike",
                                                           this.objectEqualEvaluator ) );

        notPathColumn.addConstraint( getBoundVariableConstraint( notPathColumn,
                                                                 "id",
                                                                 pathIdDeclaration,
                                                                 this.integerEqualEvaluator ) );
        notPathColumn.addConstraint( getBoundVariableConstraint( notPathColumn,
                                                                 "guestName",
                                                                 pathGuestNameDeclaration,
                                                                 this.objectEqualEvaluator ) );

        final Not not = new Not( );

        not.addChild( notPathColumn );

        rule.addPattern( not );

        // ------------
        // drools.assert( new Path( id, pathName, pathSeat ) );
        // ------------
        final Consequence consequence = new Consequence( ) {

            public void evaluate( KnowledgeHelper drools, WorkingMemory workingMemory )
                    throws ConsequenceException {
                // empty
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    /**
     * <pre>
     *     rule makePath() {
     *         int pathId;
     *         String pathGuestName;
     *    
     *         when {
     *             Path( pathId:id, pathGuestName:guest )
     *             (not Path( id == pathId, guestName == pathGuestName )
     *         } then {
     *             nothing;
     *    
     *         }
     *     } 
     * </pre>
     * 
     * @return
     * @throws IntrospectionException
     * @throws InvalidRuleException
     */
    private Rule getMakePathNoAlpha() throws IntrospectionException, InvalidRuleException {
        final Rule rule = new Rule( "makePathNoAlpha" );
        // -----------
        // Path( id == seatingPid, pathGuestName:guestName, pathSeat:seat )
        // -----------
        final Column pathColumn = new Column( 0, this.pathType );
        setFieldDeclaration( pathColumn, "id", "pathId" );

        setFieldDeclaration( pathColumn, "guestName", "pathGuestName" );

        rule.addPattern( pathColumn );

        final Declaration pathIdDeclaration = rule.getDeclaration( "pathId" );
        final Declaration pathGuestNameDeclaration = rule.getDeclaration( "pathGuestName" );
        // -------------
        // (not Path( id == seatingId, guestName == pathGuestName )
        // -------------
        final Column notPathColumn = new Column( 3, this.pathType );

        notPathColumn.addConstraint( getBoundVariableConstraint( notPathColumn,
                                                                 "id",
                                                                 pathIdDeclaration,
                                                                 this.integerEqualEvaluator ) );
        notPathColumn.addConstraint( getBoundVariableConstraint( notPathColumn,
                                                                 "guestName",
                                                                 pathGuestNameDeclaration,
                                                                 this.objectEqualEvaluator ) );

        final Not not = new Not( );

        not.addChild( notPathColumn );

        rule.addPattern( not );

        // ------------
        // drools.assert( new Path( id, pathName, pathSeat ) );
        // ------------
        final Consequence consequence = new Consequence( ) {

            public void evaluate( KnowledgeHelper drools, WorkingMemory workingMemory )
                    throws ConsequenceException {
                // empty
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    public static int getIndex( final Class clazz, final String name )
            throws IntrospectionException {
        final PropertyDescriptor[] descriptors = Introspector.getBeanInfo( clazz )
                                                             .getPropertyDescriptors( );
        for (int i = 0; i < descriptors.length; i++) {
            if (descriptors[i].getName( ).equals( name )) {
                return i;
            }
        }
        return -1;
    }

    private AlphaNodeFieldConstraint getLiteralConstraint( final Column column,
                                                  final String fieldName,
                                                  final Object fieldValue,
                                                  final Evaluator evaluator )
            throws IntrospectionException {
        final Class clazz = ( (ClassObjectType) column.getObjectType( ) ).getClassType( );

        final FieldExtractor extractor = new ClassFieldExtractor( clazz, fieldName );

        final FieldValue field = new MockField( fieldValue );

        return new LiteralConstraint( extractor, evaluator, field );
    }

    private void setFieldDeclaration( final Column column,
                                      final String fieldName,
                                      final String identifier )
            throws IntrospectionException {
        final Class clazz = ( (ClassObjectType) column.getObjectType( ) ).getClassType( );

        final FieldExtractor extractor = new ClassFieldExtractor( clazz, fieldName );

        column.addDeclaration( identifier, extractor );
    }

    private AlphaNodeFieldConstraint getBoundVariableConstraint( final Column column,
                                                        final String fieldName,
                                                        final Declaration declaration,
                                                        final Evaluator evaluator )
            throws IntrospectionException {
        final Class clazz = ( (ClassObjectType) column.getObjectType( ) ).getClassType( );

        final FieldExtractor extractor = new ClassFieldExtractor( clazz, fieldName );

        return new VariableConstraint( extractor, declaration, evaluator );
    }

}
