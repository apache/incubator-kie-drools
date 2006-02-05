package org.drools.leaps;

import java.io.Serializable;
import java.util.Iterator;

import org.drools.leaps.RuleHandle;
import org.drools.leaps.util.Table;
import org.drools.leaps.util.TableOutOfBoundException;

/**
 * Implementation of a container to store data elements used throughout the
 * leaps. Stores fact handles and companion information - relevant rules
 * 
 * TODO review add rule after fireall was issued already
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
	private RuleTable positiveRules;

	/**
	 * negative rules are not complete rules but rather its conditions that
	 * relates by type
	 */
	private RuleTable negativeRules;

	/**
	 * dynamic rule management support. used to push facts on stack again after
	 * fireAllRules by working memory and adding of a new rule after that
	 */
	private boolean reseededStack = false;

	/**
	 * initializes base LeapsTable with appropriate Comparator and positive and
	 * negative rules repositories
	 * 
	 * @param factConflictResolver
	 * @param ruleConflictResolver
	 */
	public FactTable(ConflictResolver conflictResolver) {
		super(conflictResolver.getFactConflictResolver());
		positiveRules = new RuleTable(conflictResolver
				.getRuleConflictResolver());
		negativeRules = new RuleTable(conflictResolver
				.getRuleConflictResolver());
	}

	/**
	 * Add rules that
	 * 
	 * @param workingMemory
	 * @param ruleHandle
	 */
	public void addNegativeRule(WorkingMemoryImpl workingMemory,
			RuleHandle ruleHandle) {
		this.negativeRules.add(ruleHandle);
		// no need to check negative facts
	}

	public void addPositiveRule(WorkingMemoryImpl workingMemory,
			RuleHandle ruleHandle) {
		this.positiveRules.add(ruleHandle);
		// push facts back to stack if needed
		this.checkAndAddFactsToStack(workingMemory);
	}

	/**
	 * checks if rule arrived after working memory fireAll event and if no rules
	 * where added since then. Iterates through all facts asserted (and not
	 * retracted, they are not here duh) and adds them to the stack.
	 * 
	 * @param working memory
	 * 
	 */
	private void checkAndAddFactsToStack(WorkingMemoryImpl workingMemory) {
		if (this.reseededStack) {
			this.setReseededStack(false);
			// let's only add facts below waterline - added before rule is added
			// rest would be added to stack automatically
			Handle factHandle = new FactHandleImpl(
                    workingMemory.getIdLastFireAllAt(), null);
			try {
			for (Iterator it = this.tailIterator(factHandle, factHandle); it.hasNext();) {
				workingMemory.pushTokenOnStack(new Token(workingMemory,
						(FactHandleImpl) it.next(), Token.ASSERTED));
			}
			}
			catch (TableOutOfBoundException e){
				// should never get here
			}
		}
	}

	/**
	 * set indicator if rule was added already after fire all completed
	 * @param new value
	 */
	public void setReseededStack(boolean reseeded) {
		this.reseededStack = reseeded;
	}

	/**
	 * returns an iterator of rule handles to the negative CEs "(not ())"
	 * portions of rules were type matches this fact table underlying type
	 * 
	 * @return iterator of negative rule handles
	 */
	public Iterator getNegativeRulesIterator() {
		return this.negativeRules.iterator();
	}

	/**
	 * returns an iterator of rule handles to the regular(positive) CEs portions
	 * of rules were type matches this fact table underlying type
	 * 
	 * @return iterator of positive rule handles
	 */
	public Iterator getPositiveRulesIterator() {
		return this.positiveRules.iterator();
	}

	public String toString() {
		String ret = this.toString();
		ret = ret + "\n" + "POSITIVE RULES :";
		ret = ret + "\n" + this.positiveRules.toString();
		ret = ret + "\n" + "NEGATIVE RULES :";
		ret = ret + "\n" + this.negativeRules.toString();
		return ret;
	}
}
