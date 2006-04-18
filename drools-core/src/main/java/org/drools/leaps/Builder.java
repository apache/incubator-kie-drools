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
import java.util.Iterator;
import java.util.List;

import org.drools.common.BetaNodeBinder;
import org.drools.rule.And;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.GroupElement;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Not;
import org.drools.rule.Rule;
import org.drools.spi.FieldConstraint;

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
    final protected static List processRule(Rule rule) throws InvalidPatternException {
        ArrayList leapsRules = new ArrayList();
        And[] and = rule.getTransformedLhs();
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
    final private static List processRuleForAnd(And and,
                                                Rule rule) {
        ColumnConstraints constraints;
        ArrayList leapsRules = new ArrayList();
        ArrayList cols = new ArrayList();
        ArrayList notCols = new ArrayList();
        ArrayList existsCols = new ArrayList();
        ArrayList evalConditions = new ArrayList();
        for ( Iterator it = and.getChildren().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof EvalCondition ) {
                EvalCondition eval = (EvalCondition) object;
                evalConditions.add( eval );
            } else {
                if ( object instanceof Column ) {
                    constraints = Builder.processColumn( (Column) object );
                    // create column constraints
                } else {
                    // NOTS and EXISTS
                    GroupElement ce = (GroupElement) object;
                    while ( !(ce.getChildren().get( 0 ) instanceof Column) ) {
                        ce = (GroupElement) ce.getChildren().get( 0 );
                    }
                    constraints = Builder.processColumn( (Column) ce.getChildren().get( 0 ) );
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
    static void checkEvalUnboundDeclarations(Rule rule,
                                             ArrayList evals) throws InvalidPatternException {
        List list = new ArrayList();
        for ( Iterator it = evals.iterator(); it.hasNext(); ) {
            EvalCondition ec = (EvalCondition) it.next();
            Declaration[] declarations = ec.getRequiredDeclarations();
            for ( int i = 0, length = declarations.length; i < length; i++ ) {
                if ( rule.getDeclaration( declarations[i].getIdentifier() ) == null ) {
                    list.add( declarations[i].getIdentifier() );
                }
            }
        }

        // Make sure the required declarations
        if ( list.size() != 0 ) {
            StringBuffer buffer = new StringBuffer();
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
    final private static ColumnConstraints processColumn(Column column) {
        BetaNodeBinder binder;
        List alphaConstraints = new ArrayList();
        List betaConstraints = new ArrayList();

        for ( Iterator it = column.getConstraints().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( !(object instanceof FieldConstraint) ) {
                continue;
            }

            FieldConstraint fieldConstraint = (FieldConstraint) object;
            if ( fieldConstraint.getRequiredDeclarations().length == 0 ) {
                alphaConstraints.add( fieldConstraint );
            } else {
                betaConstraints.add( fieldConstraint );
            }
        }

        if ( !betaConstraints.isEmpty() ) {
            binder = new BetaNodeBinder( (FieldConstraint[]) betaConstraints.toArray( new FieldConstraint[betaConstraints.size()] ) );
        } else {
            binder = new BetaNodeBinder();
        }

        return new ColumnConstraints( column,
                                      alphaConstraints,
                                      binder );
    }
}