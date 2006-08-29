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

import java.util.Iterator;

import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.leaps.util.Table;
import org.drools.leaps.util.TableIterator;
import org.drools.rule.EvalCondition;
import org.drools.rule.InvalidRuleException;
import org.drools.spi.Tuple;

/**
 * helper class that does condition evaluation on token when working memory does
 * seek. all methods are static
 * 
 * @author Alexander Bagerman
 * 
 */
final class TokenEvaluator {
    /**
     * this method does nested loops iterations on all relavant fact tables and
     * evaluates rules conditions
     * 
     * @param token
     * @throws NoMatchesFoundException
     * @throws Exception
     * @throws InvalidRuleException
     */
    final static protected void evaluate( final Token token )
            throws NoMatchesFoundException, InvalidRuleException {
        final LeapsWorkingMemory workingMemory = (LeapsWorkingMemory) token.getWorkingMemory( );
        final LeapsRule leapsRule = token.getCurrentRuleHandle( ).getLeapsRule( );
        // sometimes there is no normal conditions, only not and exists
        final int numberOfColumns = leapsRule.getNumberOfColumns( );
        // if (numberOfColumns > 0) {
        final int dominantFactPosition = token.getCurrentRuleHandle( )
                                              .getDominantPosition( );
        final InternalFactHandle dominantFactHandle = token.getDominantFactHandle( );
        if (leapsRule.getColumnConstraintsAtPosition( dominantFactPosition )
                     .isAllowedAlpha( dominantFactHandle, token, workingMemory )) {
            final Object dominantClass = leapsRule.getColumnClassObjectTypeAtPosition( dominantFactPosition );
            final TableIterator[] iterators = new TableIterator[numberOfColumns];
            // getting iterators first
            for (int i = 0; i < numberOfColumns; i++) {
                final Object columnClass = leapsRule.getColumnClassObjectTypeAtPosition( i );
                final ColumnConstraints constraints = leapsRule.getColumnConstraintsAtPosition( i );
                // we do not need iterators for From constraint
                if (constraints.getClass( ) != FromConstraint.class) {
                    if (i == dominantFactPosition) {
                        iterators[i] = Table.singleItemIterator( dominantFactHandle );
                    }
                    else {
                        final FactTable factTable = workingMemory.getFactTable( columnClass );
                        final LeapsFactHandle startFactHandle = ( dominantClass == columnClass ) ? new LeapsFactHandle( dominantFactHandle.getRecency( ) - 1,
                                                                                                                        new Object( ) )
                                : (LeapsFactHandle) dominantFactHandle;
                        if (i > 0 && constraints.isAlphaPresent( )) {
                            iterators[i] = factTable.constrainedIteratorFromPositionToTableStart( workingMemory,
                                                                                                  constraints,
                                                                                                  startFactHandle,
                                                                                                  ( token.isResume( ) ? (LeapsFactHandle) token.get( i )
                                                                                                          : startFactHandle ) );
                        }
                        else {
                            iterators[i] = factTable.iteratorFromPositionToTableStart( startFactHandle,
                                                                                       ( token.isResume( ) ? (LeapsFactHandle) token.get( i )
                                                                                               : startFactHandle ) );
                        }
                    }
                }
                else {
                    // we do not need iterator for from constraint
                    // it has its own
                    iterators[i] = null;
                }
            }

            // check if any iterators are empty to abort
            // check if we resume and any starting facts disappeared than we
            // do not do skip on resume
            boolean doReset = false;
            boolean skip = token.isResume( );
            TableIterator currentIterator;
            for (int i = 0; i < numberOfColumns; i++) {
                currentIterator = iterators[i];
                if (currentIterator != null) {
                    // check if one of them is empty and immediate return
                    if (currentIterator.isEmpty( )) {
                        throw new NoMatchesFoundException( );
                    }
                    else {
                        if (!doReset) {
                            if (skip && currentIterator.hasNext( )
                                    && !currentIterator.peekNext( ).equals( token.get( i ) )) {
                                // we tried to resume but our fact handle at
                                // marker
                                // disappear no need to resume just reset all
                                // interators
                                // positioned at the marker where we stoped last
                                // time
                                skip = false;
                                doReset = true;
                            }
                        }
                        else {
                            currentIterator.reset( );
                        }
                    }
                }
            }

            // iterating is done in nested loop
            // column position in the nested loop
            int jj = 0;
            boolean done = false;
            final int stopIteratingCount = numberOfColumns - 1;
            while (!done) {
                currentIterator = iterators[jj];
                // if it's not From and does not have next
                if (currentIterator != null && !currentIterator.hasNext( )) {
                    if (jj == 0) {
                        done = true;
                    }
                    else {
                        // nothing for this column, go back and check next
                        // on the one level up in nested loop
                        currentIterator.reset( );
                        jj = jj - 1;
                        if (skip) {
                            skip = false;
                        }
                    }
                }
                else {
                    boolean localMatch = false;
                    LeapsFactHandle currentFactHandle = null;
                    if (currentIterator != null) {
                        currentFactHandle = (LeapsFactHandle) currentIterator.next( );
                        // check if match found we need to check only beta for
                        // dominant fact
                        // alpha was already checked
                        if (!skip) {
                            if (jj != 0 || jj == dominantFactPosition) {
                                localMatch = leapsRule.getColumnConstraintsAtPosition( jj )
                                                      .isAllowedBeta( currentFactHandle,
                                                                      token,
                                                                      workingMemory );
                            }
                            else {
                                localMatch = leapsRule.getColumnConstraintsAtPosition( jj )
                                                      .isAllowed( currentFactHandle,
                                                                  token,
                                                                  workingMemory );
                            }
                        }
                    }
                    else {
                        Object fromMatch = TokenEvaluator.evaluateFrom( (FromConstraint) leapsRule.getColumnConstraintsAtPosition( jj ),
                                                                        token,
                                                                        leapsRule,
                                                                        workingMemory );
                        if (fromMatch != null) {
                            localMatch = true;
                            // this is not a real fact. just to make it work with 
                            // token / tuple
                            currentFactHandle = new LeapsFactHandle( -1, fromMatch );
                        }
                    }
                    if (localMatch || skip) {
                        token.set( jj, currentFactHandle );
                        // start iteratating next iterator or for the last
                        // one check negative conditions and fire consequence
                        if (jj == stopIteratingCount) {
                            if (!skip) {
                                final LeapsTuple tuple = token.getTuple( );
                                if (processAfterAllPositiveConstraintOk( tuple,
                                                                         leapsRule,
                                                                         workingMemory )) {
                                    workingMemory.assertTuple( tuple );
                                    return;
                                }
                            }
                            else {
                                skip = false;
                            }
                        }
                        else {
                            jj = jj + 1;
                        }
                    }
                }
            }
        }
        // nothing was found. inform caller about it
        throw new NoMatchesFoundException( );
    }

    /**
     * Makes final check on eval, exists and not conditions after all column
     * values isAllowed by column constraints
     * 
     * @param token
     * @param leapsRule
     * @param workingMemory
     * @return
     * @throws Exception
     */
    final static boolean processAfterAllPositiveConstraintOk( final LeapsTuple tuple,
                                                              final LeapsRule leapsRule,
                                                              final LeapsWorkingMemory workingMemory ) {
        if (leapsRule.containsEvalConditions( )
                && !TokenEvaluator.evaluateEvalConditions( tuple, leapsRule, workingMemory )) {
            return false;
        }
        if (leapsRule.containsExistsColumns( )) {
            TokenEvaluator.evaluateExistsConditions( tuple, leapsRule, workingMemory );
        }
        if (tuple.isReadyForActivation( ) && leapsRule.containsNotColumns( )) {
            TokenEvaluator.evaluateNotConditions( tuple, leapsRule, workingMemory );
        }
        // 
        return tuple.isReadyForActivation( );
    }

    /**
     * checks is EvalConditions isAllowed()
     * 
     * @param leapsRule
     * @param tuple
     * @param workingMemory
     * @return
     * @throws Exception
     */
    private final static Object evaluateFrom( final FromConstraint from,
                                              final Tuple tuple,
                                              final LeapsRule leapsRule,
                                              final LeapsWorkingMemory workingMemory ) {
        for (Iterator it = from.getProvider( ).getResults( tuple, workingMemory, null ); it.hasNext( );) {
            Object object = it.next( );
            if (from.isAllowed( new DefaultFactHandle( -1, object ), tuple, workingMemory )) {
                return object;
            }
        }
        return null;
    }

    /**
     * checks is EvalConditions isAllowed()
     * 
     * @param leapsRule
     * @param tuple
     * @param workingMemory
     * @return
     * @throws Exception
     */
    private final static boolean evaluateEvalConditions( final LeapsTuple tuple,
                                                         final LeapsRule leapsRule,
                                                         final LeapsWorkingMemory workingMemory ) {
        final EvalCondition[] evals = leapsRule.getEvalConditions( );
        for (int i = 0; i < evals.length; i++) {
            if (!evals[i].isAllowed( tuple, workingMemory )) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if any of the negative conditions are satisfied success when none
     * found
     * 
     * @param memory
     * @param token
     * @return success
     * @throws Exception
     */
    final static void evaluateNotConditions( final LeapsTuple tuple,
                                             final LeapsRule rule,
                                             final LeapsWorkingMemory workingMemory ) {
        // stops if exists
        boolean done = false;
        final ColumnConstraints[] not = rule.getNotColumnConstraints( );
        for (int i = 0, length = not.length; i < length && !done; i++) {
            final ColumnConstraints constraint = not[i];
            // scan table starting at start fact handle
            final TableIterator tableIterator = workingMemory.getFactTable( constraint.getClassType( ) )
                                                             .reverseOrderIterator( tuple, constraint );
            while (!done  && tableIterator.hasNext( )) {
                final LeapsFactHandle factHandle = (LeapsFactHandle) tableIterator.next( );
                // check constraint conditions
                if (constraint.isAllowed( factHandle, tuple, workingMemory )) {
                    tuple.setBlockingNotFactHandle( factHandle, i );
                    factHandle.addNotTuple( tuple, i );
                    done = true;
                }
            }
        }
    }

    /**
     * Check if any of the exists conditions are satisfied
     * 
     * @param tuple
     * @param memory
     * @throws Exception
     */
    public final static void evaluateExistsConditions( final LeapsTuple tuple,
                                                       final LeapsRule rule,
                                                       final LeapsWorkingMemory workingMemory ) {
        // stop if exists
        boolean notFound = false;
        boolean done = false;
        final ColumnConstraints[] exists = rule.getExistsColumnConstraints( );
        for (int i = 0, length = exists.length; !notFound && i < length; i++) {
            final ColumnConstraints constraint = exists[i];
            if (!tuple.isExistsFactHandle( i )) {
                // scan table starting at start fact handle
                final TableIterator tableIterator = workingMemory.getFactTable( constraint.getClassType( ) )
                                                                 .reverseOrderIterator( tuple,
                                                                                        constraint );
                done = false;
                while (!done && tableIterator.hasNext( )) {
                    final LeapsFactHandle factHandle = (LeapsFactHandle) tableIterator.next( );
                    // check constraint conditions
                    if (constraint.isAllowed( factHandle, tuple, workingMemory )) {
                        tuple.setExistsFactHandle( factHandle, i );
                        factHandle.addExistsTuple( tuple, i );
                        done = true;
                    }
                }
                if (!done) {
                    notFound = true;
                    workingMemory.getFactTable( constraint.getClassType( ) )
                                 .addTuple( tuple );
                }
            }
        }
    }
}
