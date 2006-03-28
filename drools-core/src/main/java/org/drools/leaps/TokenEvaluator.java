package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

import org.drools.common.PropagationContextImpl;
import org.drools.leaps.util.Table;
import org.drools.leaps.util.TableIterator;
import org.drools.rule.EvalCondition;
import org.drools.rule.InvalidRuleException;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

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
    final static protected void evaluate(Token token) throws NoMatchesFoundException,
                                                     Exception,
                                                     InvalidRuleException {
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) token.getWorkingMemory();
        LeapsRule leapsRule = token.getCurrentRuleHandle().getLeapsRule();
        // sometimes there is no normal conditions, only not and exists
        int numberOfColumns = leapsRule.getNumberOfColumns();
        if ( numberOfColumns > 0 ) {
            int dominantFactPosition = token.getCurrentRuleHandle().getDominantPosition();
            if ( leapsRule.getColumnConstraintsAtPosition( dominantFactPosition ).isAllowedAlpha( token.getDominantFactHandle(),
                                                                                                  token,
                                                                                                  workingMemory ) ) {
                TableIterator[] iterators = new TableIterator[numberOfColumns];
                // getting iterators first
                for ( int i = 0; i < numberOfColumns; i++ ) {
                    if ( i == dominantFactPosition ) {
                        iterators[i] = Table.singleItemIterator( token.getDominantFactHandle() );
                    } else {
                        if ( i > 0 && leapsRule.getColumnConstraintsAtPosition( i ).isAlphaPresent() ) {
                            iterators[i] = workingMemory.getFactTable( leapsRule.getColumnClassObjectTypeAtPosition( i ) ).tailConstrainedIterator( workingMemory,
                                                                                                                                                    leapsRule.getColumnConstraintsAtPosition( i ),
                                                                                                                                                    token.getDominantFactHandle(),
                                                                                                                                                    (token.isResume() ? token.get( i ) : token.getDominantFactHandle()) );
                        } else {
                            iterators[i] = workingMemory.getFactTable( leapsRule.getColumnClassObjectTypeAtPosition( i ) ).tailIterator( token.getDominantFactHandle(),
                                                                                                                                         (token.isResume() ? token.get( i ) : token.getDominantFactHandle()) );
                        }
                    }
                }
                // check if any iterators are empty to abort
                // check if we resume and any starting facts disappeared than we
                // do
                // not do skip on resume
                boolean someIteratorsEmpty = false;
                boolean doReset = false;
                boolean skip = token.isResume();
                TableIterator currentIterator;
                for ( int i = 0; i < numberOfColumns && !someIteratorsEmpty; i++ ) {
                    currentIterator = iterators[i];
                    if ( currentIterator.isEmpty() ) {
                        someIteratorsEmpty = true;
                    } else {
                        if ( !doReset ) {
                            if ( skip && currentIterator.hasNext() && !currentIterator.peekNext().equals( token.get( i ) ) ) {
                                skip = false;
                                doReset = true;
                            }
                        } else {
                            currentIterator.reset();
                        }
                    }

                }
                // check if one of them is empty and immediate return
                if ( someIteratorsEmpty ) {
                    throw new NoMatchesFoundException();
                    // "some of tables do not have facts");
                }
                // iterating is done in nested loop
                // column position in the nested loop
                int jj = 0;
                boolean done = false;
                while ( !done ) {
                    currentIterator = iterators[jj];
                    if ( !currentIterator.hasNext() ) {
                        if ( jj == 0 ) {
                            done = true;
                        } else {
                            //                    
                            currentIterator.reset();
                            token.set( jj,
                                       (FactHandleImpl) null );
                            jj = jj - 1;
                            if ( skip ) {
                                skip = false;
                            }
                        }
                    } else {
                        currentIterator.next();
                        token.set( jj,
                                   (FactHandleImpl) iterators[jj].current() );
                        // check if match found
                        // we need to check only beta for dominant fact
                        // alpha was already checked
                        boolean localMatch = false;
                        if ( jj == 0 && jj != dominantFactPosition ) {
                            localMatch = leapsRule.getColumnConstraintsAtPosition( jj ).isAllowed( token.get( jj ),
                                                                                                   token,
                                                                                                   workingMemory );
                        } else {
                            localMatch = leapsRule.getColumnConstraintsAtPosition( jj ).isAllowedBeta( token.get( jj ),
                                                                                                       token,
                                                                                                       workingMemory );
                        }

                        if ( localMatch ) {
                            // start iteratating next iterator
                            // or for the last one check negative conditions and
                            // fire
                            // consequence
                            if ( jj == (numberOfColumns - 1) ) {
                                if ( !skip ) {
                                    if ( processAfterAllPositiveConstraintOk( token,
                                                                              leapsRule,
                                                                              workingMemory ) ) {
                                        return;
                                    }
                                } else {
                                    skip = false;
                                }
                            } else {
                                jj = jj + 1;
                            }
                        } else {
                            if ( skip ) {
                                skip = false;
                            }
                        }
                    }
                }
            }
        } else {
            if ( processAfterAllPositiveConstraintOk( token,
                                                      leapsRule,
                                                      workingMemory ) ) {
                return;
            }
        }
        // nothing was found. inform caller about it
        throw new NoMatchesFoundException();
    }

    /**
     * Makes final check on eval, exists and not conditions after all column values
     * isAllowed by column constraints
     * 
     * @param token
     * @param leapsRule
     * @param workingMemory
     * @return
     * @throws Exception
     */
    final static boolean processAfterAllPositiveConstraintOk(Token token,
                                                             LeapsRule leapsRule,
                                                             WorkingMemoryImpl workingMemory) throws Exception {
        LeapsTuple tuple = token.getTuple( new PropagationContextImpl( workingMemory.increamentPropagationIdCounter(),
                                                                       PropagationContext.ASSERTION,
                                                                       leapsRule.getRule(),
                                                                       (Activation) null ) );
        if ( leapsRule.containsEvalConditions() ) {
            if ( !TokenEvaluator.evaluateEvalConditions( leapsRule,
                                                         tuple,
                                                         workingMemory ) ) {
                return false;
            }
        }
        if ( leapsRule.containsExistsColumns() ) {
            TokenEvaluator.evaluateExistsConditions( tuple,
                                                     leapsRule,
                                                     workingMemory );
        }
        if ( leapsRule.containsNotColumns() ) {
            TokenEvaluator.evaluateNotConditions( tuple,
                                                  leapsRule,
                                                  workingMemory );
        }
        //
        Class[] classes = leapsRule.getExistsNotColumnsClasses();
        for ( int i = 0, length = classes.length; i < length; i++ ) {
            workingMemory.getFactTable( classes[i] ).addTuple( tuple );
        }

        // 
        if ( tuple.isReadyForActivation() ) {
            // let agenda to do its work
            workingMemory.assertTuple( tuple );
            return true;
        } else {
            return false;
        }
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
    final static boolean evaluateEvalConditions(LeapsRule leapsRule,
                                                LeapsTuple tuple,
                                                WorkingMemoryImpl workingMemory) throws Exception {
        EvalCondition[] evals = leapsRule.getEvalConditions();
        for ( int i = 0; i < evals.length; i++ ) {
            if ( !evals[i].isAllowed( tuple,
                                      workingMemory ) ) {
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
    final static void evaluateNotConditions(LeapsTuple tuple,
                                            LeapsRule rule,
                                            WorkingMemoryImpl workingMemory) throws Exception {
        FactHandleImpl factHandle;
        TableIterator tableIterator;
        ColumnConstraints constraint;
        ColumnConstraints[] not = rule.getNotColumnConstraints();
        for ( int i = 0, length = not.length; i < length; i++ ) {
            constraint = not[i];
            // scan the whole table
            tableIterator = workingMemory.getFactTable( constraint.getClassType() ).iterator();
            // fails if exists
            while ( tableIterator.hasNext() ) {
                factHandle = (FactHandleImpl) tableIterator.next();
                // check constraint condition
                if ( constraint.isAllowed( factHandle,
                                           tuple,
                                           workingMemory ) ) {
                    tuple.addNotFactHandle( factHandle,
                                            i );
                    factHandle.addNotTuple( tuple,
                                            i );
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
    final static void evaluateExistsConditions(LeapsTuple tuple,
                                               LeapsRule rule,
                                               WorkingMemoryImpl workingMemory) throws Exception {
        FactHandleImpl factHandle;
        TableIterator tableIterator;
        ColumnConstraints constraint;
        ColumnConstraints[] exists = rule.getExistsColumnConstraints();
        for ( int i = 0, length = exists.length; i < length; i++ ) {
            constraint = exists[i];
            // scan the whole table
            tableIterator = workingMemory.getFactTable( constraint.getClassType() ).iterator();
            // fails if exists
            while ( tableIterator.hasNext() ) {
                factHandle = (FactHandleImpl) tableIterator.next();
                // check constraint conditions
                if ( constraint.isAllowed( factHandle,
                                           tuple,
                                           workingMemory ) ) {
                    tuple.addExistsFactHandle( factHandle,
                                               i );
                    factHandle.addExistsTuple( tuple,
                                               i );
                }
            }
        }
    }
}
