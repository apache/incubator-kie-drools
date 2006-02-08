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

	private FactHandleImpl dominantFactHandle;

	private WorkingMemoryImpl workingMemory;

	private FactHandleImpl[] factHandles;

	   /** The </code>TupleKey</code> */
	private final TupleKey key;

	private Activation activation; 
	
	/**
	 * activation parts
	 */

	LeapsTuple(FactHandleImpl dominantFactHandle, FactHandleImpl factHandles[],
			WorkingMemoryImpl workingMemory) {
		this.dominantFactHandle = dominantFactHandle;
		this.workingMemory = workingMemory;
		this.factHandles = new FactHandleImpl[factHandles.length];
		System.arraycopy(factHandles, 0, this.factHandles, 0,
				factHandles.length);
		TupleKey tupleKey = null;
		for(int i = 0; i < this.factHandles.length; i++ ){
			if(i == 0){
				tupleKey = new TupleKey(this.factHandles[0]);
			}
			else {
				tupleKey= new TupleKey(this.key, this.factHandles[i]);
			}
		}
		this.key = tupleKey;

	}

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the key for this tuple.
     * 
     * @return The key.
     */
    TupleKey getKey() {
        return this.key;
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
        return this.key.containsFactHandle( handle );
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

        return this.key.equals( ((LeapsTuple) object).key );
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for ( int i = 0; i < this.key.size(); i++ ) {
            buffer.append( this.key.get( i ) + ", " );
        }
        return buffer.toString();
    }


    
    
    
    
    
//	public Object get(FactHandle factHandle) {
//		return ((FactHandleImpl) factHandle).getObject();
//	}
//
//	public FactHandleImpl getFactHandleAtPosition(int idx) {
//		return this.factHandles[idx];
//	}
//
//	public FactHandleImpl getDominantFactHandle() {
//		return this.dominantFactHandle;
//	}
//
//	/**
//	 * Retrieve the <code>FactHandle</code> for a given object.
//	 * 
//	 * <p>
//	 * Within a consequence of a rule, if the desire is to retract or modify a
//	 * root fact this method provides a way to retrieve the
//	 * <code>FactHandle</code>. Facts that are <b>not </b> root fact objects
//	 * have no handle.
//	 * </p>
//	 * 
//	 * @param object
//	 *            The object.
//	 * 
//	 * @return The fact-handle or <code>null</code> if the supplied object is
//	 *         not a root fact object.
//	 */
//	public FactHandle getFactHandleForObject(Object object) {
//		if (this.factHandles != null) {
//			for (int i = 0; i < this.factHandles.length; i++) {
//				if (this.factHandles[i].getObject() == object) {
//					return this.getFactHandleAtPosition(i);
//				}
//			}
//		}
//
//		return null;
//	}
//
//	/**
//	 * @see Tuple
//	 */
//	public FactHandle getFactHandleForDeclaration(Declaration declaration) {
//		return this.getFactHandleAtPosition(declaration.getColumn());
//	}
//
//	/**
//	 * @see Tuple
//	 */
	public FactHandle[] getFactHandles() {
		return this.factHandles;
	}

}
