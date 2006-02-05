package org.drools.leaps;

import java.util.Iterator;
import java.util.List;

import org.drools.reteoo.BetaNodeBinder;
import org.drools.rule.Column;

/**
 * Collection of <code>Column</code> specific constraints
 * 
 * @author Alexander Bagerman
 * 
 */
class ColumnConstraints {
	private Column column;

	private List alpha;

	private BetaNodeBinder beta;

	public ColumnConstraints(Column column, List alpha, BetaNodeBinder beta) {
		this.column = column;
		this.alpha = alpha;
		this.beta = beta;
	}

	public Column getColumn() {
		return column;
	}

	public Iterator getAlpha() {
		return this.alpha.iterator();
	}

	public BetaNodeBinder getBeta() {
		return this.beta;
	}
}
