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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.leaps.util.Table;
import org.drools.leaps.util.TableOutOfBoundException;

/**
 * Implementation of a container to store data elements used throughout the
 * leaps. Stores fact handles and companion information - relevant rules
 * 
 * @author Alexander Bagerman
 * 
 */
class FactTable extends Table implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * positive rules are not complete rules but rather its conditions that
	 * relates by type
	 */
	private final RuleTable rules;

	/**
	 * dynamic rule management support. used to push facts on stack again after
	 * fireAllRules by working memory and adding of a new rule after that
	 */
	private boolean reseededStack = false;

	/**
	 * Tuples that are either already on agenda or are very close (missing exists or 
	 * have not facts matching)
	 */
	
	private final Set tuples;
	/**
	 * initializes base LeapsTable with appropriate Comparator and positive and
	 * negative rules repositories
	 * 
	 * @param factConflictResolver
	 * @param ruleConflictResolver
	 */
	public FactTable(ConflictResolver conflictResolver) {
		super(conflictResolver.getFactConflictResolver());
		this.rules = new RuleTable(conflictResolver.getRuleConflictResolver());
		this.tuples = new HashSet();
	}

	/**
	 * Add rules that
	 * 
	 * @param workingMemory
	 * @param ruleHandle
	 */
	public void addRule(WorkingMemoryImpl workingMemory, RuleHandle ruleHandle) {
		this.rules.add(ruleHandle);
		// push facts back to stack if needed
		this.checkAndAddFactsToStack(workingMemory);
	}

	/**
	 * Add tuple
	 * 
	 * @param tuple
	 */
	public void addRule(LeapsTuple tuple) {
		this.tuples.add(tuple);
	}

	/**
	 * checks if rule arrived after working memory fireAll event and if no rules
	 * where added since then. Iterates through all facts asserted (and not
	 * retracted, they are not here duh) and adds them to the stack.
	 * 
	 * @param working
	 *            memory
	 * 
	 */
	private void checkAndAddFactsToStack(WorkingMemoryImpl workingMemory) {
		if (this.reseededStack) {
			this.setReseededStack(false);
			// let's only add facts below waterline - added before rule is added
			// rest would be added to stack automatically
			Handle factHandle = new FactHandleImpl(workingMemory
					.getIdLastFireAllAt(), null);
			try {
				for (Iterator it = this.tailIterator(factHandle, factHandle); it
						.hasNext();) {
					workingMemory.pushTokenOnStack(new Token(workingMemory,
							(FactHandleImpl) it.next()));
				}
			} catch (TableOutOfBoundException e) {
				// should never get here
			}
		}
	}

	/**
	 * set indicator if rule was added already after fire all completed
	 * 
	 * @param new
	 *            value
	 */
	public void setReseededStack(boolean reseeded) {
		this.reseededStack = reseeded;
	}

	/**
	 * returns an iterator of rule handles to the regular(positive) CEs portions
	 * of rules were type matches this fact table underlying type
	 * 
	 * @return iterator of positive rule handles
	 */
	public Iterator getRulesIterator() {
		return this.rules.iterator();
	}

	/**
	 * @see java.lang.Object
	 */
	public String toString() {
		String ret = this.toString();
		ret = ret + "\n" + "POSITIVE RULES :";
		ret = ret + "\n" + this.rules.toString();

		return ret;
	}
	
	Iterator getTuplesIterator() {
		return this.tuples.iterator();
	}
	
	boolean addTuple(LeapsTuple tuple) {
		return this.tuples.add(tuple);
	}
	
	void removeTuple(LeapsTuple tuple) {
		this.tuples.remove(tuple);
	}
}
