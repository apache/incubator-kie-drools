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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.common.BetaConstraints;
import org.drools.common.DefaultBetaConstraints;
import org.drools.rule.And;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.GroupElement;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Not;
import org.drools.rule.Rule;
import org.drools.spi.AlphaNodeFieldConstraint;

/**
 * A Rule<code>Builder</code> to process <code>Rule</code>s for use with
 * Leaps WorkingMemories. Produces list of Leaps rules that wrap Rule and can be
 * used in Leaps algorithm. All methods are static
 * 
 * @author Alexander Bagerman
 * 
 */
class Builder {
    /**
     * follows RETEOO logic flow but returns leaps rules list
     * 
     * @param rule
     * @return list of leaps rule
     * @throws InvalidPatternException
     */
    final protected static List processRule(final Rule rule) throws InvalidPatternException {
        final ArrayList leapsRules = new ArrayList();
        final And[] and = rule.getTransformedLhs();
        for ( int i = 0, length = and.length; i < length; i++ ) {
            leapsRules.addAll( processRuleForAnd( and[i],
                                                  rule ) );
        }
        return leapsRules;
    }

    /**
     * Creates list of leaps rules for each individual And
     * 
     * @param and
     * @param rule
     * @return list of leaps rules for the given And
     */
    final private static List processRuleForAnd(final And and,
                                                final Rule rule) {
        ColumnConstraints constraints;
        final ArrayList leapsRules = new ArrayList();
        final ArrayList cols = new ArrayList();
        final ArrayList notCols = new ArrayList();
        final ArrayList existsCols = new ArrayList();
        final ArrayList evalConditions = new ArrayList();
        for ( final Iterator it = and.getChildren().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof EvalCondition ) {
                final EvalCondition eval = (EvalCondition) object;
                evalConditions.add( eval );
            } else {
                if ( object instanceof Column ) {
                    constraints = Builder.processColumn( (Column) object, true );
                    // create column constraints
                } else {
                    // NOTS and EXISTS
                    GroupElement ce = (GroupElement) object;
                    while ( !(ce.getChildren().get( 0 ) instanceof Column) ) {
                        ce = (GroupElement) ce.getChildren().get( 0 );
                    }
                    constraints = Builder.processColumn( (Column) ce.getChildren().get( 0 ) , false);
                }
                if ( object instanceof Not ) {
                    notCols.add( constraints );
                } else if ( object instanceof Exists ) {
                    existsCols.add( constraints );
                } else {
                    cols.add( constraints );
                }
            }
        }

        // check eval for presence of required declarations
        checkEvalUnboundDeclarations( rule,
                                      evalConditions );
        //
        leapsRules.add( new LeapsRule( rule,
                                       cols,
                                       notCols,
                                       existsCols,
                                       evalConditions ) );

        return leapsRules;
    }

    /**
     * Make sure the required declarations are previously bound
     * 
     * @param declarations
     * @throws InvalidPatternException
     */
    static void checkEvalUnboundDeclarations(final Rule rule,
                                             final ArrayList evals) throws InvalidPatternException {
        final List list = new ArrayList();
        for ( final Iterator it = evals.iterator(); it.hasNext(); ) {
            final EvalCondition ec = (EvalCondition) it.next();
            final Declaration[] declarations = ec.getRequiredDeclarations();
            for ( int i = 0, length = declarations.length; i < length; i++ ) {
                if ( rule.getDeclaration( declarations[i].getIdentifier() ) == null ) {
                    list.add( declarations[i].getIdentifier() );
                }
            }
        }

        // Make sure the required declarations
        if ( list.size() != 0 ) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append( list.get( 0 ) );
            for ( int i = 1, size = list.size(); i < size; i++ ) {
                buffer.append( ", " + list.get( i ) );
            }

            throw new InvalidPatternException( "Required Declarations not bound: '" + buffer );
        }
    }

    /**
     * extracts column specific constraints and packages it into
     * <code>ColumnConstraints</code>
     * 
     * @param column
     * @param and
     * @return leaps packaged ColumnConstraints
     */
    final private static ColumnConstraints processColumn(final Column column,
                                                         final boolean removeIdentities ) {
        BetaConstraints binder;
        final List alphaConstraints = new ArrayList( );
        final List predicateConstraints = new ArrayList( );

        final List constraints = column.getConstraints( );

        Map declarations = new HashMap( );

        if (column.getDeclaration( ) != null) {
            final Declaration declaration = column.getDeclaration( );
            // Add the declaration the map of previously bound declarations
            declarations.put( declaration.getIdentifier( ), declaration );
        }

        for (final Iterator it = constraints.iterator( ); it.hasNext( );) {
            final Object object = it.next( );
            // Check if its a declaration
            if (object instanceof Declaration) {
                final Declaration declaration = (Declaration) object;
                // Add the declaration the map of previously bound declarations
                declarations.put( declaration.getIdentifier( ), declaration );
                continue;
            }

            final AlphaNodeFieldConstraint fieldConstraint = (AlphaNodeFieldConstraint) object;
            if ( fieldConstraint.getRequiredDeclarations().length == 0 ) {
                alphaConstraints.add( fieldConstraint);
            } else {
                predicateConstraints.add( fieldConstraint );
            }
        }


        if ( !predicateConstraints.isEmpty() ) {
            binder = new DefaultBetaConstraints( (AlphaNodeFieldConstraint[]) predicateConstraints.toArray( new AlphaNodeFieldConstraint[predicateConstraints.size()] ) );
        } else {
            binder = new DefaultBetaConstraints();
        }

        return new ColumnConstraints( column,
                                      alphaConstraints,
                                      binder );
    }
    /**
     * Make sure the required declarations are previously bound
     * 
     * @param declarations
     * @throws InvalidPatternException
     */
    private static void checkUnboundDeclarations(final Map declarations, final Declaration[] requiredDeclarations) throws InvalidPatternException {
        final List list = new ArrayList();
        for ( int i = 0, length = requiredDeclarations.length; i < length; i++ ) {
            if ( declarations.get( requiredDeclarations[i].getIdentifier() ) == null ) {
                list.add( requiredDeclarations[i].getIdentifier() );
            }
        }

        // Make sure the required declarations        
        if ( list.size() != 0 ) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append( list.get( 0 ) );
            for ( int i = 1, size = list.size(); i < size; i++ ) {
                buffer.append( ", " + list.get( i ) );
            }

            throw new InvalidPatternException( "Required Declarations not bound: '" + buffer );
        }

    }
}