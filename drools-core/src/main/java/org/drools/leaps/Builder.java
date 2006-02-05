package org.drools.leaps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.reteoo.BetaNodeBinder;
import org.drools.rule.And;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Exists;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Rule;
import org.drools.spi.BetaNodeConstraint;
import org.drools.spi.Constraint;

/**
 * A Rule<code>Builder</code> to process <code>Rule</code>s for use with
 * Leaps WorkingMemories. Produces list of Leaps rules that wrap Rule
 * and can be used in Leaps algorithm
 * 
 * @author Alexander Bagerman
 * 
 */
class Builder {
	/**
	 * 
	 */
	Builder() {
	}

	/**
	 * follows RETEOO logic flow but returns leaps rules list
	 * 
	 * @param rule
	 * @return list of leaps rule 
	 * @throws InvalidPatternException
	 */
	public List processRule(Rule rule) throws InvalidPatternException {
		ArrayList leapsRules = new ArrayList();
		And[] and = rule.getProcessPatterns();
		for (int i = 0; i < and.length; i++) {
			leapsRules.addAll(processRuleForAnd(and[i], rule));
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
	private List processRuleForAnd(And and, Rule rule) {
		ArrayList leapsRules = new ArrayList();
		ArrayList cols = new ArrayList();
		ArrayList notCols = new ArrayList();
		ArrayList existsCols = new ArrayList();
		for (Iterator it = and.getChildren().iterator(); it.hasNext();) {
			Object object = it.next();
			if (object instanceof Column) {
				// create column constraints
				cols.add(this.processColumn((Column) object, and));
			} else {
				// NOTS and EXISTS
				ConditionalElement ce = (ConditionalElement) object;
				if (!(ce.getChildren().get(0) instanceof Column)) {
					ce = (ConditionalElement) ce.getChildren().get(0);
				}
				if (object instanceof Not) {
					notCols.add(this.processColumn((Column) ce.getChildren()
							.get(0), and));
				} else if (object instanceof Exists) {
					existsCols.add(this.processColumn((Column) ce.getChildren()
							.get(0), and));
				} else {
				}
			}
		}

		leapsRules.add(new LeapsRule(rule, cols, notCols, existsCols));

		return leapsRules;
	}

	/**
	 * extracts column specific constraints and packages it into
	 * <code>ColumnConstraints</code>
	 * 
	 * @param column
	 * @param and
	 * @return leaps packaged ColumnConstraints
	 */
	private ColumnConstraints processColumn(Column column, And and) {
		BetaNodeBinder binder;
		List alphaConstraints = new ArrayList();
		List betaConstraints = new ArrayList();

		for (Iterator it = column.getConstraints().iterator(); it.hasNext();) {
			Constraint constraint = (Constraint) it.next();
			if (constraint instanceof LiteralConstraint) {
				alphaConstraints.add(constraint);
			} else if (constraint instanceof BetaNodeConstraint) {
				betaConstraints.add(constraint);
			}
		}

		if (!betaConstraints.isEmpty()) {
			binder = new BetaNodeBinder((BetaNodeConstraint[]) betaConstraints
					.toArray(new BetaNodeConstraint[betaConstraints.size()]));
		} else {
			binder = new BetaNodeBinder();
		}

		return new ColumnConstraints(column, alphaConstraints, binder);
	}
}
