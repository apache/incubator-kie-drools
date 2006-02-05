package org.drools.leaps;

import java.io.Serializable;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

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
	}

	/**
	 * Retrieve the value at position
	 * 
	 * @param position
	 * 
	 * @return The currently bound <code>Object</code> value.
	 */
	public Object get(int idx) {
		return this.getFactHandleAtPosition(idx).getObject();
	}

	/**
	 * 
	 */
	public Object get(Declaration declaration) {
		return declaration.getValue(this.get(declaration.getColumn()));
	}

	public Object get(FactHandle factHandle) {
		return ((FactHandleImpl) factHandle).getObject();
	}

	public FactHandleImpl getFactHandleAtPosition(int idx) {
		return this.factHandles[idx];
	}

	public int hashCode() {
		// return (int) this.dominantFactHandle.id;
		return (int) this.dominantFactHandle.getId();
	}

	public FactHandleImpl getDominantFactHandle() {
		return this.dominantFactHandle;
	}

	/**
	 * We always have only one Tuple per fact handle hence match on handle id
	 * 
	 * @see Object
	 */
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof LeapsTuple))
			return false;
		for (int i = 0; i < this.factHandles.length; i++) {
			if (!this.factHandles[i].equals(((LeapsTuple) that)
					.getFactHandleAtPosition(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieve the <code>FactHandle</code> for a given object.
	 * 
	 * <p>
	 * Within a consequence of a rule, if the desire is to retract or modify a
	 * root fact this method provides a way to retrieve the
	 * <code>FactHandle</code>. Facts that are <b>not </b> root fact objects
	 * have no handle.
	 * </p>
	 * 
	 * @param object
	 *            The object.
	 * 
	 * @return The fact-handle or <code>null</code> if the supplied object is
	 *         not a root fact object.
	 */
	public FactHandle getFactHandleForObject(Object object) {
		if (this.factHandles != null) {
			for (int i = 0; i < this.factHandles.length; i++) {
				if (this.factHandles[i].getObject() == object) {
					return this.getFactHandleAtPosition(i);
				}
			}
		}

		return null;
	}

	/**
	 * @see Tuple
	 */
	public FactHandle getFactHandleForDeclaration(Declaration declaration) {
		return this.getFactHandleAtPosition(declaration.getColumn());
	}

	/**
	 * @see Tuple
	 */
	public FactHandle[] getFactHandles() {
		return this.factHandles;
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
	boolean dependsOn(FactHandle handle) {
		for (int i = 0; i < this.factHandles.length; i++) {
			if (this.factHandles[i].equals(handle)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a reference to the <code>WorkingMemory</code> associated with
	 * this object.
	 * 
	 * @return WorkingMemory
	 * @see Tuple
	 */
	public WorkingMemory getWorkingMemory() {
		return this.workingMemory;
	}

	/**
	 * does not matter at all for leaps.
	 * 
	 * @see Tuple.
	 */
	public long getMostRecentFactTimeStamp() {
		if (this.factHandles != null) {
			long recency = -1;
			for (int i = 0; i < this.factHandles.length; i++) {
				if (i == 0) {
					recency = this.factHandles[0].getRecency();
				} else if (this.factHandles[i].getRecency() > recency) {
					recency = this.factHandles[i].getRecency();
				}
			}
			return recency;
		} else {
			return -1L;
		}
	}

	/**
	 * does not matter at all for leaps.
	 * 
	 * @see Tuple.
	 */
	public long getLeastRecentFactTimeStamp() {
		if (this.factHandles != null) {
			long recency = -1;
			for (int i = 0; i < this.factHandles.length; i++) {
				if (i == 0) {
					recency = this.factHandles[0].getRecency();
				} else if (this.factHandles[i].getRecency() < recency) {
					recency = this.factHandles[i].getRecency();
				}
			}
			return recency;
		} else {
			return -1L;
		}
	}

	public String toString() {

		String ret = "TUPLE:: \n";
		if (this.factHandles != null) {
			for (int i = 0; i < this.factHandles.length; i++) {
				ret = ret + "\t" + i + " -> " + this.factHandles[i].getObject()
						+ "\n";
			}
		}
		return ret;
	}

}
