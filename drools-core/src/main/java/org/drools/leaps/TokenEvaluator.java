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

import org.drools.base.ClassObjectType;
import org.drools.common.PropagationContextImpl;
import org.drools.leaps.util.TableIterator;
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
/**
 * @author bageale
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
	final static protected void evaluate(Token token)
			throws NoMatchesFoundException, Exception, InvalidRuleException {

		WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) token
				.getWorkingMemory();

		RuleHandle ruleHandle = token.getCurrentRuleHandle();
		TableIterator[] iterators = new TableIterator[ruleHandle.getLeapsRule()
				.getNumberOfColumns()];
		int numberOfColumns = ruleHandle.getLeapsRule().getNumberOfColumns();
		// getting iterators first
		for (int i = 0; i < numberOfColumns; i++) {
			if (i == ruleHandle.getDominantPosition()) {
				iterators[i] = TableIterator.baseFactIterator(token
						.getDominantFactHandle());
			} else {
				iterators[i] = workingMemory.getFactTable(
						ruleHandle.getLeapsRule()
								.getColumnClassObjectTypeAtPosition(i)
								.getClassType()).tailIterator(
						token.getDominantFactHandle(),
						(token.isResume() ? token.getFactHandleAtPosition(i)
								: token.getDominantFactHandle()));
			}
		}
		// check if any iterators are empty to abort
		// check if we resume and any facts disappeared
		boolean someIteratorsEmpty = false;
		boolean doReset = false;
		boolean skip = token.isResume();
		TableIterator currentIterator;
		for (int i = 0; i < numberOfColumns && !someIteratorsEmpty; i++) {
			currentIterator = iterators[i];
			if (currentIterator.isEmpty()) {
				someIteratorsEmpty = true;
			} else {
				if (!doReset) {
					if (skip
							&& currentIterator.hasNext()
							&& !currentIterator.peekNext().equals(
									token.getFactHandleAtPosition(i))) {
						skip = false;
						doReset = true;
					}
				} else {
					currentIterator.reset();
				}
			}

		}
		// check if one of them is empty and immediate return
		if (someIteratorsEmpty) {
			throw new NoMatchesFoundException();
			// "some of tables do not have facts");
		}
		// iterating is done in nested loop
		// column position in the nested loop
		int jj = 0;
		boolean found = false;
		boolean done = false;
		while (!done) {
			currentIterator = iterators[jj];
			if (!currentIterator.hasNext()) {
				if (jj == 0) {
					done = true;
				} else {
					//                    
					currentIterator.reset();
					token.setCurrentFactHandleAtPosition(jj,
							(FactHandleImpl) null);
					jj = jj - 1;
					if (skip) {
						skip = false;
					}
				}
			} else {
				currentIterator.next();
				token.setCurrentFactHandleAtPosition(jj,
						(FactHandleImpl) iterators[jj].current());
				// check if match found
				if (token.getCurrentRuleHandle().getLeapsRule()
						.getColumnConstraintsAtPosition(jj).isAllowed(
								token.getFactHandleAtPosition(jj), token,
								workingMemory)) {
					// start iteratating next iterator
					// or for the last one check negative conditions and fire
					// consequence
					if (jj == (numberOfColumns - 1)) {
						if (!skip) {
							LeapsTuple tuple = token
									.getTuple(new PropagationContextImpl(
											workingMemory
													.increamentPropagationIdCounter(),
											PropagationContext.ASSERTION, token
													.getCurrentRuleHandle()
													.getLeapsRule().getRule(),
											(Activation) null
											));
							if (tuple.isExistsConstraintsPresent()) {
								TokenEvaluator.evaluateExistsConditions(tuple,
										workingMemory);
							}
							if (tuple.isNotConstraintsPresent()) {
								TokenEvaluator.evaluateNotConditions(tuple,
										workingMemory);
							}
							// check for negative conditions
							if (tuple.isReadyForActivation()) {
								// let agenda to do its work
								workingMemory.assertTuple(tuple, ruleHandle.getLeapsRule().getRule());

								done = true;
								found = true;
							}
						} else {
							skip = false;
						}
					} else {
						jj = jj + 1;
					}
				} else {
					if (skip) {
						skip = false;
					}
				}
			}
		}
		if (!found) {
			throw new NoMatchesFoundException();
			// "iteration did not find anything");
		}
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
			WorkingMemoryImpl workingMemory) throws Exception {
		FactHandleImpl factHandle;
		FactTable factTable;
		TableIterator tableIterator;
		ColumnConstraints constraint;
		ColumnConstraints[] not = tuple.getNotConstraints();
		for (int i = 0; i < not.length; i++) {
			constraint = not[i];
			// scan the whole table
			factTable = workingMemory
					.getFactTable(((ClassObjectType) constraint.getColumn()
							.getObjectType()).getClassType());
			tableIterator = factTable.iterator();
			// fails if exists
			while (tableIterator.hasNext()) {
				factHandle = (FactHandleImpl) tableIterator.next();
				// check alphas
				if (constraint.isAllowed(factHandle, tuple, workingMemory)) {
					tuple.addNotFactHandle(factHandle, i);
					factTable.addTuple(tuple);
					factHandle.addNotTuple(tuple, i);
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
			WorkingMemoryImpl workingMemory) throws Exception {
		FactHandleImpl factHandle;
		FactTable factTable;
		TableIterator tableIterator;
		ColumnConstraints constraint;
		ColumnConstraints[] exists = tuple.getExistsConstraints();
		for (int i = 0; i < exists.length; i++) {
			constraint = exists[i];
			// scan the whole table
			factTable = workingMemory
					.getFactTable(((ClassObjectType) constraint.getColumn()
							.getObjectType()).getClassType());
			tableIterator = factTable.iterator();
			// fails if exists
			while (tableIterator.hasNext()) {
				factHandle = (FactHandleImpl) tableIterator.next();
				// check alphas
				if (constraint.isAllowed(factHandle, tuple, workingMemory)) {
					tuple.addExistsFactHandle(factHandle, i);
					factTable.addTuple(tuple);
					factHandle.addExistsTuple(tuple, i);
				}
			}
		}
	}
}
