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

import java.util.List;

import org.drools.common.BetaNodeBinder;
import org.drools.rule.Column;
import org.drools.rule.LiteralConstraint;

/**
 * Collection of <code>Column</code> specific constraints
 * 
 * @author Alexander Bagerman
 * 
 */
class ColumnConstraints {
	private Column column;

	private LiteralConstraint[] alphaConstraints = new LiteralConstraint[0];

	private BetaNodeBinder beta;

	public ColumnConstraints(Column column, List alpha, BetaNodeBinder beta) {
		this.column = column;
		this.beta = beta;
		this.alphaConstraints = (LiteralConstraint[]) alpha
				.toArray(this.alphaConstraints);
	}

	public Column getColumn() {
		return this.column;
	}

	public boolean evaluateAlphas(FactHandleImpl factHandle, Token token,
			WorkingMemoryImpl workingMemory) {
		boolean found = true;
		for (int i = 0; i < this.alphaConstraints.length && found; i++) {
			// escape immediately if some condition does not match
			found = this.alphaConstraints[i].isAllowed(factHandle, token,
					workingMemory);
		}
		return found;
	}

	public BetaNodeBinder getBeta() {
		return this.beta;
	}
}
