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





import org.drools.leaps.util.Table;
import org.drools.leaps.util.TableIterator;
import org.drools.rule.EvalCondition;
import org.drools.rule.InvalidRuleException;

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
                                                     InvalidRuleException {
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) token.getWorkingMemory();
        LeapsRule leapsRule = token.getCurrentRuleHandle().getLeapsRule();
        // sometimes there is no normal conditions, only not and exists
        int numberOfColumns = leapsRule.getNumberOfColumns();
        //        if (numberOfColumns > 0) {
        int dominantFactPosition = token.getCurrentRuleHandle().getDominantPosition();
        FactHandleImpl dominantFactHandle = token.getDominantFactHandle();
        if ( leapsRule.getColumnConstraintsAtPosition( dominantFactPosition ).isAllowedAlpha( dominantFactHandle,
                                                                                              token,
                                                                                              workingMemory ) ) {
            Class dominantClass = leapsRule.getColumnClassObjectTypeAtPosition( dominantFactPosition );
            TableIterator[] iterators = new TableIterator[numberOfColumns];
            // getting iterators first
            for ( int i = 0; i < numberOfColumns; i++ ) {
                if ( i == dominantFactPosition ) {
                    iterators[i] = Table.singleItemIterator( dominantFactHandle );
                } else {
                    Class columnClass = leapsRule.getColumnClassObjectTypeAtPosition( i );
                    ColumnConstraints constraints = leapsRule.getColumnConstraintsAtPosition( i );
                    FactTable factTable = workingMemory.getFactTable( columnClass );
                    // JBRULES-189
                    FactHandleImpl startFactHandle = (dominantClass == columnClass) ? new FactHandleImpl( dominantFactHandle.getId() - 1,
                                                                                                          null ) : dominantFactHandle;
                    //
                    if ( i > 0 && constraints.isAlphaPresent() ) {
                        iterators[i] = factTable.tailConstrainedIterator( workingMemory,
                                                                          constraints,
                                                                          startFactHandle,
                                                                          (token.isResume() ? (FactHandleImpl) token.get( i ) : startFactHandle) );
                    } else {
                        iterators[i] = factTable.tailIterator( startFactHandle,
                                                               (token.isResume() ? (FactHandleImpl) token.get( i ) : startFactHandle) );
                    }
                }
            }

            // check if any iterators are empty to abort
            // check if we resume and any starting facts disappeared than we
            // do not do skip on resume
            boolean doReset = false;
            boolean skip = token.isResume();
            TableIterator currentIterator;
            for ( int i = 0; i < numberOfColumns; i++ ) {
                currentIterator = iterators[i];
                // check if one of them is empty and immediate return
                if ( currentIterator.isEmpty() ) {
                    throw new NoMatchesFoundException();
                } else {
                    if ( !doReset ) {
                        if ( skip && currentIterator.hasNext() && !currentIterator.peekNext().equals( token.get( i ) ) ) {
                            // we tried to resume but our fact handle at marker disappear
                            // no need to resume just reset all interators positioned
                            // at the marker where we stoped last time
                            skip = false;
                            doReset = true;
                        }
                    } else {
                        currentIterator.reset();
                    }
                }
            }

            // iterating is done in nested loop
            // column position in the nested loop
            int jj = 0;
            boolean done = false;
            int stopIteratingCount = numberOfColumns - 1;
            while ( !done ) {
                currentIterator = iterators[jj];
                if ( !currentIterator.hasNext() ) {
                    if ( jj == 0 ) {
                        done = true;
                    } else {
                        // nothing for this column, go back and check next 
                        // on the one level up in nested loop
                        currentIterator.reset();
                        jj = jj - 1;
                        if ( skip ) {
                            skip = false;
                        }
                    }
                } else {
                    FactHandleImpl currentFactHandle = (FactHandleImpl) currentIterator.next();
                    // check if match found we need to check only beta for
                    // dominant fact
                    // alpha was already checked
                    boolean localMatch = false;
                    if ( !skip ) {
                        if ( jj != 0 || jj == dominantFactPosition ) {
                            localMatch = leapsRule.getColumnConstraintsAtPosition( jj ).isAllowedBeta( currentFactHandle,
                                                                                                       token,
                                                                                                       workingMemory );
                        } else {
                            localMatch = leapsRule.getColumnConstraintsAtPosition( jj ).isAllowed( currentFactHandle,
                                                                                                   token,
                                                                                                   workingMemory );
                        }
                    }
                    if ( localMatch || skip ) {
                        token.set( jj,
                                   currentFactHandle );
                        // start iteratating next iterator or for the last
                        // one check negative conditions and fire consequence
                        if ( jj == stopIteratingCount ) {
                            if ( !skip ) {
                                if ( processAfterAllPositiveConstraintOk( token.getTuple(),
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
                    }
                }
            }
        }
        //        }
        //        else {
        //            LeapsTuple tuple = token;
        //
        //            if (processAfterAllPositiveConstraintOk(token..getTuple(), leapsRule, workingMemory)) {
        //                return;
        //            }
        //        }
        // nothing was found. inform caller about it
        throw new NoMatchesFoundException();
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
    final static boolean processAfterAllPositiveConstraintOk(LeapsTuple tuple,
                                                             LeapsRule leapsRule,
                                                             WorkingMemoryImpl workingMemory) {
        if ( leapsRule.containsEvalConditions() && !TokenEvaluator.evaluateEvalConditions( leapsRule,
                                                                                           tuple,
                                                                                           workingMemory ) ) {
            return false;
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
        // put tuple onto fact tables that might affect activation status
        // via exists or not conditions
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
    private final static boolean evaluateEvalConditions(LeapsRule leapsRule,
                                                        LeapsTuple tuple,
                                                        WorkingMemoryImpl workingMemory) {
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
                                            WorkingMemoryImpl workingMemory) {
        ColumnConstraints[] not = rule.getNotColumnConstraints();
        for ( int i = 0, length = not.length; i < length; i++ ) {
            ColumnConstraints constraint = not[i];
            // scan table starting at start fact handle
            TableIterator tableIterator = workingMemory.getFactTable( constraint.getClassType() ).iterator();
            // stops if exists
            boolean done = false;
            while ( !done && tableIterator.hasNext() ) {
                FactHandleImpl factHandle = (FactHandleImpl) tableIterator.next();
                // check constraint conditions
                if ( constraint.isAllowed( factHandle,
                                           tuple,
                                           workingMemory ) ) {
                    tuple.setBlockingNotFactHandle( factHandle,
                                                    i );
                    factHandle.addNotTuple( tuple,
                                            i );
                    done = true;
                }
            }
        }
    }

    /**
     * To evaluate conditions above the water line that is supplied in the first argument
     * 
     * @param startFactHandle
     * @param index
     * @param tuple
     * @param rule
     * @param workingMemory
     */
    final static void evaluateNotCondition(FactHandleImpl startFactHandle,
                                           int index,
                                           LeapsTuple tuple,
                                           WorkingMemoryImpl workingMemory) {
        LeapsRule rule = tuple.getLeapsRule();
        // scan table starting at start fact handle
        ColumnConstraints constraint = rule.getNotColumnConstraints()[index];
        TableIterator tableIterator = workingMemory.getFactTable( constraint.getClassType() ).headIterator( startFactHandle );
        // stops if exists
        boolean done = false;
        while ( !done && tableIterator.hasNext() ) {
            FactHandleImpl factHandle = (FactHandleImpl) tableIterator.next();
            // check constraint conditions
            if ( constraint.isAllowed( factHandle,
                                       tuple,
                                       workingMemory ) ) {
                tuple.setBlockingNotFactHandle( factHandle,
                                                index );
                factHandle.addNotTuple( tuple,
                                        index );
                done = true;
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
    private final static void evaluateExistsConditions(LeapsTuple tuple,
                                                       LeapsRule rule,
                                                       WorkingMemoryImpl workingMemory) {
        ColumnConstraints[] exists = rule.getExistsColumnConstraints();
        for ( int i = 0, length = exists.length; i < length; i++ ) {
            ColumnConstraints constraint = exists[i];
            // scan table starting at start fact handle
            TableIterator tableIterator = workingMemory.getFactTable( constraint.getClassType() ).iterator();
            // stop if exists
            boolean done = false;
            while ( !done && tableIterator.hasNext() ) {
                FactHandleImpl factHandle = (FactHandleImpl) tableIterator.next();
                // check constraint conditions
                if ( constraint.isAllowed( factHandle,
                                           tuple,
                                           workingMemory ) ) {
                    tuple.setExistsFactHandle( factHandle,
                                               i );
                    factHandle.addExistsTuple( tuple,
                                               i );
                    done = true;
                }
            }
        }
    }

    /**
     * To evaluate conditions above the water line that is supplied in the first
     * argument
     * 
     * @param startFactHandle
     * @param index
     * @param tuple
     * @param rule
     * @param workingMemory
     */
    final static void evaluateExistsCondition(FactHandleImpl startFactHandle,
                                              int index,
                                              LeapsTuple tuple,
                                              WorkingMemoryImpl workingMemory) {
        LeapsRule rule = tuple.getLeapsRule();
        // scan table starting at start fact handle
        ColumnConstraints constraint = rule.getExistsColumnConstraints()[index];
        TableIterator tableIterator = workingMemory.getFactTable( constraint.getClassType() ).headIterator( startFactHandle );
        // stop if exists
        boolean done = false;
        while ( !done && tableIterator.hasNext() ) {
            FactHandleImpl factHandle = (FactHandleImpl) tableIterator.next();
            // check constraint conditions
            if ( constraint.isAllowed( factHandle,
                                       tuple,
                                       workingMemory ) ) {
                tuple.setExistsFactHandle( factHandle,
                                           index );
                factHandle.addExistsTuple( tuple,
                                           index );
                done = true;
            }
        }
    }
}