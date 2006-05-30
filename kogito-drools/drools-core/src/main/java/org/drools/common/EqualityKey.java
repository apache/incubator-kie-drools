package org.drools.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Upon instantiation the EqualityKey caches the first Object's hashCode
 * this can never change. The EqualityKey has an internal datastructure 
 * which references all the handles which are equal. It also records
 * Whether the referenced facts are JUSTIFIED or STATED
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 *
 */
public class EqualityKey {
    public final static int    STATED    = 1;
    public final static int    JUSTIFIED = 2;

    /** this is an optimisation so single stated equalities can tracked  without the overhead of  an ArrayList */
    private InternalFactHandle handle;

    /** this is always lazily maintainned  and deleted  when empty to minimise memory consumption */
    private List               instances;

    /** This is cached in the constructor from the first added Object */
    private final int          hashCode;

    /** Tracks whether this Fact is Stated or Justified */
    private int                status;

    public EqualityKey(final InternalFactHandle handle) {
        this.handle = handle;
        this.hashCode = handle.getObjectHashCode();
    }

    public EqualityKey(final InternalFactHandle handle,
                       final int status) {
        this.handle = handle;
        this.hashCode = handle.getObjectHashCode();
        this.status = status;
    }

    public InternalFactHandle getFactHandle() {
        return this.handle;
    }

    public List getOtherFactHandle() {
        return this.instances;
    }

    public void addFactHandle(final InternalFactHandle handle) {
        if ( this.instances == null ) {
            this.instances = new ArrayList();
        }
        this.instances.add( handle );
    }

    public void removeFactHandle(final InternalFactHandle handle) {
        if ( this.handle == handle ) {
            if ( this.instances == null ) {
                this.handle = null;
            } else {
                this.handle = (InternalFactHandle) this.instances.remove( 0 );
            }
        } else {
            this.instances.remove( handle );
            if ( this.instances.isEmpty() ) {
                this.instances = null;
            }
        }
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(final int status) {
        this.status = status;
    }

    public int size() {
        if ( this.instances != null ) {
            return this.instances.size() + 1;
        } else {
            return (this.handle != null) ? 1 : 0;
        }
    }

    public boolean isEmpty() {
        return (this.handle == null);
    }

    public String toString() {
        String str = null;
        switch ( this.status ) {
            case 1 :
                str = "STATED";
                break;
            case 2 :
                str = "JUSTIFIED";
                break;
        }
        return "[FactStatus status=" + this.status + "]";
    }

    /**
     * Returns the cached hashCode
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * Equality for the EqualityKey means two things. It returns 
     * true if the object is also an EqualityKey the of the same
     * the same identity as this. It also returns true if the object
     * is equal to the head FactHandle's referenced Object. 
     */
    public boolean equals(final Object object) {
        if ( object == null ) {
            return false;
        }

        if ( object.getClass() == EqualityKey.class ) {
            return this == object;
        }

        return (this.handle.getObject().equals( object ));
    }

}
