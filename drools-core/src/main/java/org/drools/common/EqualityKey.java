/*
 * Copyright 2005 JBoss Inc
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

package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Upon instantiation the EqualityKey caches the first Object's hashCode
 * this can never change. The EqualityKey has an internal datastructure
 * which references all the handles which are equal. It also records
 * Whether the referenced facts are JUSTIFIED or STATED
 */
public class EqualityKey
    implements
    Externalizable {
    public final static int    STATED    = 1;
    public final static int    JUSTIFIED = 2;

    /** this is an optimisation so single stated equalities can tracked  without the overhead of  an ArrayList */
    private InternalFactHandle handle;

    /** this is always lazily maintainned  and deleted  when empty to minimise memory consumption */
    private List<InternalFactHandle>               instances;

    /** This is cached in the constructor from the first added Object */
    private int          hashCode;

    /** Tracks whether this Fact is Stated or Justified */
    private int                status;

    public EqualityKey() {

    }

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

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        handle      = (InternalFactHandle)in.readObject();
        instances   = (List)in.readObject();
        hashCode    = in.readInt();
        status      = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(handle);
        out.writeObject(instances);
        out.writeInt(hashCode);
        out.writeInt(status);
    }

    public InternalFactHandle getFactHandle() {
        return this.handle;
    }

    public List<InternalFactHandle> getOtherFactHandle() {
        return this.instances;
    }

    public void addFactHandle(final InternalFactHandle handle) {
        if ( this.instances == null ) {
            this.instances = new ArrayList<InternalFactHandle>();
        }
        this.instances.add( handle );
    }

    public void removeFactHandle(final InternalFactHandle handle) {
        if ( this.handle == handle ) {
            if ( this.instances == null ) {
                this.handle = null;
            } else {
                this.handle = (InternalFactHandle) this.instances.remove( 0 );
                if ( this.instances.isEmpty() ) {
                    this.instances = null;
                }
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

        if ( object instanceof EqualityKey ) {
            return this == object;
        }

        return (this.handle.getObject().equals( object ));
    }

}
