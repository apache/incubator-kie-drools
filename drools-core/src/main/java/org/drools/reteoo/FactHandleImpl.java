package org.drools.reteoo;

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

import org.drools.FactHandle;

/**
 * Implementation of <code>FactHandle</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
class FactHandleImpl
    implements
    FactHandle {
    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /** Handle id. */
    private long             id;
    private long             recency;
    private transient Object object;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    protected FactHandleImpl(long id) {
        this.id = id;
        this.recency = id;
    }

    /**
     * Construct.
     * 
     * @param id
     *            Handle id.
     */
    protected FactHandleImpl(long id,
                             long recency) {
        this.id = id;
        this.recency = recency;
    }

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /**
     * @see Object
     */
    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !( object instanceof FactHandleImpl ) ) {
            return false;
        }

        return this.id == ((FactHandleImpl) object).id;
    }

    /**
     * @see Object
     */
    public int hashCode() {
        return (int) this.id;
    }

    /**
     * @see FactHandle
     */
    public String toExternalForm() {
        return "[fid:" + this.id + ":" + this.recency + "]";
    }

    /**
     * @see Object
     */
    public String toString() {
        return toExternalForm();
    }

    public long getRecency() {
        return this.recency;
    }

    public void setRecency(long recency) {
        this.recency = recency;
    }

    public long getId() {
        return this.id;
    }

    void invalidate() {
        this.id = -1;
    }

    Object getObject() {
        return this.object;
    }

    void setObject(Object object) {
        this.object = object;
    }

}
