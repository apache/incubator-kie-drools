package org.drools.leaps;

import org.drools.FactHandle;

/**
 * class container for each object asserted / retracted into the system
 * 
 * @author Alexander Bagerman
 */
public class FactHandleImpl extends Handle implements FactHandle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * actual object that is asserted to the system no getters just a direct
	 * access to speed things up
	 */
	public FactHandleImpl(long id, Object object) {
		super(id, object);
	}

	/**
	 * Leaps fact handles considered equal if ids match and content points to
	 * the same object.
	 */
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof FactHandleImpl))
			return false;
		return this.getId() == ((FactHandleImpl) that).getId()
				&& this.getObject() == ((FactHandleImpl) that).getObject();

	}

	/**
	 * @see FactHandle
	 */
	public String toExternalForm() {
		return "[fid:" + this.getId() + "]";
	}

	/**
	 * @see Object
	 */
	public String toString() {
		return toExternalForm();
	}
}
