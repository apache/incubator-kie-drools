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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
				if (TokenEvaluator.evaluatePositiveConditions(token, jj,
						workingMemory)) {
					// start iteratating next iterator
					// or for the last one check negative conditions and fire
					// consequence
					if (jj == (numberOfColumns - 1)) {
						if (!skip) {
							List existsEnablingFacts = TokenEvaluator
									.evaluateExistsConditions(token,
											workingMemory);
							// check for negative conditions
							if (existsEnablingFacts == null || existsEnablingFacts.size() > 0) {
								// event support
								PropagationContextImpl propagationContext = new PropagationContextImpl(
										workingMemory
												.increamentPropagationIdCounter(),
										PropagationContext.ASSERTION,
										token
										.getCurrentRuleHandle()
										.getLeapsRule().getRule(), (Activation) null);
								// let agenda to do its work
								workingMemory.assertTuple(token.getTuple(),
										TokenEvaluator.evaluateNotConditions(
												token, workingMemory),
										existsEnablingFacts,
										propagationContext, token
												.getCurrentRuleHandle()
												.getLeapsRule().getRule());

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
	 * 
	 * Check if any conditions with max value of declaration index at this
	 * position (<code>index</code>) are satisfied
	 * 
	 * @param index
	 *            Position of the iterator that needs condition checking
	 * @return success Indicator if all conditions at this position were
	 *         satisfied.
	 * @throws Exception
	 */
	final static private boolean evaluatePositiveConditions(Token token,
			int index, WorkingMemoryImpl workingMemory) throws Exception {
		boolean satisfied = false;
		FactHandleImpl factHandle = token.getFactHandleAtPosition(index);
		ColumnConstraints constraints = token.getCurrentRuleHandle()
				.getLeapsRule().getColumnConstraintsAtPosition(index);
		// check alphas
		if (constraints.evaluateAlphas(factHandle, token, workingMemory)) {
			// finaly beta
			satisfied = constraints.getBeta().isAllowed(factHandle, token,
					token.getWorkingMemory());
		}
		return satisfied;

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
	final static private List evaluateNotConditions(Token token,
			WorkingMemoryImpl workingMemory) throws Exception {
		List blockingFactHandles = new LinkedList();
		if (token.getCurrentRuleHandle().getLeapsRule().containsNotColumns()) {
			// get each NOT column spec and check
			for (Iterator it = token.getCurrentRuleHandle().getLeapsRule()
					.getNotColumnsIterator(); it.hasNext();) {
				blockingFactHandles.addAll(TokenEvaluator.getExistsEnablingFacts(
						token, workingMemory, (ColumnConstraints) it.next()));
			}
		}

		return blockingFactHandles;
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
	final static private List evaluateExistsConditions(Token token,
			WorkingMemoryImpl workingMemory) throws Exception {
		List enablingFactHandles = null;
		if (token.getCurrentRuleHandle().getLeapsRule().containsExistsColumns()) {
			enablingFactHandles = new LinkedList();
			// get each NOT column spec and check
			for (Iterator it = token.getCurrentRuleHandle().getLeapsRule()
					.getExistsColumnsIterator(); it.hasNext();) {
				enablingFactHandles.addAll(TokenEvaluator.getExistsEnablingFacts(
						token, workingMemory, (ColumnConstraints) it.next()));
			}
		}

		return enablingFactHandles;
	}

	/**
	 * return set of all facts that satisfy exists condition including not and
	 * exists cases
	 * 
	 * @param token
	 * @param workingMemory
	 * @param constraints
	 * @return all facts that satisfy exists conditions
	 * @throws Exception
	 */
	final static private List getExistsEnablingFacts(Token token,
			WorkingMemoryImpl workingMemory, ColumnConstraints constraints)
			throws Exception {
		List matchingFactHandles = new LinkedList();
		TableIterator tableIterator;
		// scan the whole table
		tableIterator = workingMemory.getFactTable(
				((ClassObjectType) constraints.getColumn().getObjectType())
						.getClassType()).iterator();
		// fails if exists
		FactHandleImpl factHandle;
		while (tableIterator.hasNext()) {
			factHandle = (FactHandleImpl) tableIterator.next();
			// check alphas
			if (constraints.evaluateAlphas(factHandle, token, workingMemory)) {
				if (constraints.getBeta().isAllowed(factHandle, token,
						workingMemory)) {
					// it's blocking based on beta
					matchingFactHandles.add(factHandle);
				}
			}
		}

		return matchingFactHandles;
	}
}
