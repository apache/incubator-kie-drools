package org.drools.runtime.rule.impl;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.Row;

public class RowAdapter implements Row {
	
    private Rule                 rule;
	private LeftTuple 			 leftTuple;
	private InternalFactHandle[] factHandles;

	public RowAdapter(final Rule rule,
	                  final LeftTuple leftTuple) {
	    this.rule = rule;
		this.leftTuple = leftTuple;
	}
	
    private InternalFactHandle getFactHandle(Declaration declr) {
        return this.factHandles[  declr.getPattern().getOffset() ]; // -1 because we shifted the array left
                                                                       // when removing the query object
    }  	

	public Object get(String identifier) {
		if ( factHandles == null ) {
			this.factHandles = this.leftTuple.getFactHandles();
		}
		Declaration declr = this.rule.getDeclaration( identifier );
		InternalFactHandle factHandle = getFactHandle( declr );
		return declr.getValue( null, factHandle.getObject() );
	}

	public Object get(int i) {
		if ( factHandles == null ) {
			this.factHandles = this.leftTuple.getFactHandles();
		}
		return this.factHandles[ i + 1].getObject();
	}

	public FactHandle getFactHandle(String identifier) {
		if ( factHandles == null ) {
			this.factHandles = this.leftTuple.getFactHandles();
		}
		return null;
	}

	public FactHandle getFactHandle(int i) {
		if ( factHandles == null ) {
			this.factHandles = this.leftTuple.getFactHandles();
		}
		return null;
	}

	public int size() {
		if ( factHandles == null ) {
			this.factHandles = this.leftTuple.getFactHandles();
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.leftTuple == null) ? 0 : this.leftTuple.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RowAdapter other = (RowAdapter) obj;
		if (this.leftTuple == null) {
			if (other.leftTuple != null)
				return false;
		} else if (!this.leftTuple.equals(other.leftTuple))
			return false;
		return true;
	}
}