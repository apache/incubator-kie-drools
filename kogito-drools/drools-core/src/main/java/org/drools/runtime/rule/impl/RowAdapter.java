/*
 * Copyright 2010 JBoss Inc
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
        return this.factHandles[  declr.getPattern().getOffset() ];
    }  	

	public Object get(String identifier) {
		if ( factHandles == null ) {
			this.factHandles = this.leftTuple.toFactHandles();
		}
		Declaration declr = this.rule.getDeclaration( identifier );
		if ( declr == null ) {
		    throw new RuntimeException("The identifier '" + identifier + "' does not exist as a bound varirable for this query" );
		}
		InternalFactHandle factHandle = getFactHandle( declr );
		return declr.getValue( null, factHandle.getObject() );
	}

	public FactHandle getFactHandle(String identifier) {
		if ( factHandles == null ) {
			this.factHandles = this.leftTuple.toFactHandles();
		}
        Declaration declr = this.rule.getDeclaration( identifier );
        if ( declr == null ) {
            throw new RuntimeException("The identifier '" + identifier + "' does not exist as a bound varirable for this query" );
        }
        InternalFactHandle factHandle = getFactHandle( declr );
		return factHandle;
	}

	public FactHandle getFactHandle(int i) {
		if ( factHandles == null ) {
			this.factHandles = this.leftTuple.toFactHandles();
		}
		return null;
	}

	public int size() {
		if ( factHandles == null ) {
			this.factHandles = this.leftTuple.toFactHandles();
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
	
	public String toString() {
	    StringBuilder sbuilder = new StringBuilder();
	    for ( int i = 0, length = this.factHandles.length -1; i < length; i++ ) {
	        sbuilder.append( this.factHandles[i].getObject().toString() );
	        if ( i < length - 1 ) {
	            sbuilder.append( ", " );
	        }
	    }
	    
	    return "Row[" +  sbuilder.toString() +"]";
	}
}
