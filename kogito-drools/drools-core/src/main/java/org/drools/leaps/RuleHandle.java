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

/**
 * class container for rules used in the system. Handle is created for each
 * leaps rule, dominant position (column/ce position), dominant position type
 * (class at the column/ce position) or indicator if handle is for asserted or
 * retracted tuple combination
 * 
 * @author Alexander Bagerman
 * 
 */
public class RuleHandle extends Handle {
	// ce position for which handle is created
	private final int dominantPosition;

	public RuleHandle(long id, LeapsRule rule, int dominantPosition) {
		super(id, rule);
		this.dominantPosition = dominantPosition;
	}

	/**
	 * @return leaps wrapped rule
	 */
	public LeapsRule getLeapsRule() {
		return (LeapsRule) this.getObject();
	}

	/**
	 * @return base column / ce position
	 */
	public int getDominantPosition() {
		return this.dominantPosition;
	}

	/**
	 * @see org.drools.rule.Rule
	 */
	public int getRuleComplexity() {
		return this.getLeapsRule().getRule().getDeclarations().length;
	}

	/**
	 * @see org.drools.rule.Rule
	 */
	public int getSalience() {
		return this.getLeapsRule().getRule().getSalience();
	}

	/**
	 * @see java.lang.Object
	 */
	public boolean equals(Object that) {
		return super.equals(that)
				&& (this.getDominantPosition() == ((RuleHandle) that)
						.getDominantPosition());
	}

	/**
	 * @see java.lang.Object
	 */
	public String toString() {
		return "R-" + this.getId() + " \"" + this.getLeapsRule().toString()
				+ "\" [pos - " + this.dominantPosition + "]";
	}
}
