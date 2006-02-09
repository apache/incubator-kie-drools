package org.drools.leaps;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.reteoo.TupleMatch;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;

/**
 * Leaps Tuple implementation
 *  
 * @author Alexander Bagerman
 * 
 */
public class LeapsTuple implements Tuple, Serializable {
	private static final long serialVersionUID = 1L;

	private FactHandleImpl[] factHandles;


	private Activation activation; 
	
	/**
	 * activation parts
	 */

	LeapsTuple(FactHandleImpl factHandles[]) {
		this.factHandles = new FactHandleImpl[factHandles.length];
		System.arraycopy(factHandles, 0, this.factHandles, 0,
				factHandles.length);
	}

    /**
     * Determine if this tuple depends upon a specified object.
     * 
     * @param handle
     *            The object handle to test.
     * 
     * @return <code>true</code> if this tuple depends upon the specified
     *         object, otherwise <code>false</code>.
     */
    public boolean dependsOn(FactHandle handle) {
    	for (int i = 0; i < this.factHandles.length; i++){
    		if (!handle.equals(this.factHandles[i])){
    			return false;
    		}
    	}
        return true;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /* (non-Javadoc)
     * @see org.drools.spi.Tuple#get(int)
     */
    public FactHandle get(int col) {
        return this.factHandles[ col ];
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Tuple#get(org.drools.rule.Declaration)
     */
    public FactHandle get(Declaration declaration) {
        return this.get( declaration.getColumn() );
    }
    
     public void setActivation(Activation activation) {
         this.activation = activation;
     }
     
     public Activation getActivation() {
         return this.activation;
     }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof LeapsTuple) ) {
            return false;
        }

        
        return true;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for ( int i = 0; i < this.factHandles.length; i++ ) {
            buffer.append( this.factHandles[i] + ", " );
        }
        return buffer.toString();
    }

	public FactHandle[] getFactHandles() {
		return this.factHandles;
	}

}
