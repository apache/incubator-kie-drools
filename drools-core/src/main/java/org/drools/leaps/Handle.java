package org.drools.leaps;

import org.drools.FactHandle;

/**
 * Leaps handle for use with facts and rules. Is used extensively by leaps tables.
 * 
 * @author Alexander Bagerman
 *
 */
public class Handle {
    // object to handle
    final private Object object;

    final private long id;

    final private long timeStamp;

    /**
     * creates a handle for object
     * 
     * @param id that is used to identify the object
     * @param object to handle
     */
    public Handle(long id, Object object) {
        this.id = id;
        this.timeStamp = System.currentTimeMillis();
        this.object = object;
    }

    /**
     * @return id of the object
     */
    public long getId() {
        return this.id;
    }

    /**
     * @return time when object was created
     */
    public long getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * @return object being handled
     */
    public Object getObject() {
        return object;
    }

    
    
    public int hashCode() {
        return (int) this.id;
    }


	/**
	 * @see FactHandle
	 */
	public long getRecency() {
		return this.id;
	}

    public String toString() {
        return "id=" + this.id + " [" + this.object + "]";
    }
}
