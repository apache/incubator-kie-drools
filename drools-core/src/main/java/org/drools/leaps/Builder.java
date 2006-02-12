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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.rule.And;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Exists;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Rule;
import org.drools.spi.BetaNodeBinder;
import org.drools.spi.Constraint;
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
	final protected static List processRule(Rule rule)
			throws InvalidPatternException {
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
	final private static List processRuleForAnd(And and, Rule rule) {
		ArrayList leapsRules = new ArrayList();
		ArrayList cols = new ArrayList();
		ArrayList notCols = new ArrayList();
		ArrayList existsCols = new ArrayList();
		for (Iterator it = and.getChildren().iterator(); it.hasNext();) {
			Object object = it.next();
			if (object instanceof Column) {
				// create column constraints
				cols.add(Builder.processColumn((Column) object, and));
			} else {
				// NOTS and EXISTS
				ConditionalElement ce = (ConditionalElement) object;
				if (!(ce.getChildren().get(0) instanceof Column)) {
					ce = (ConditionalElement) ce.getChildren().get(0);
				}
				if (object instanceof Not) {
					notCols.add(Builder.processColumn((Column) ce.getChildren()
							.get(0), and));
				} else if (object instanceof Exists) {
					existsCols.add(Builder.processColumn((Column) ce
							.getChildren().get(0), and));
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
	final private static ColumnConstraints processColumn(Column column, And and) {
		BetaNodeBinder binder;
		List alphaConstraints = new ArrayList();
		List betaConstraints = new ArrayList();

		for (Iterator it = column.getConstraints().iterator(); it.hasNext();) {
			Constraint constraint = (Constraint) it.next();
			if (constraint instanceof LiteralConstraint) {
				alphaConstraints.add(constraint);
			} else if (constraint instanceof FieldConstraint) {
				betaConstraints.add(constraint);
			}
		}

		if (!betaConstraints.isEmpty()) {
			binder = new BetaNodeBinder((FieldConstraint[]) betaConstraints
					.toArray(new FieldConstraint[betaConstraints.size()]));
		} else {
			binder = new BetaNodeBinder();
		}

		return new ColumnConstraints(column, alphaConstraints, binder);
	}
}
