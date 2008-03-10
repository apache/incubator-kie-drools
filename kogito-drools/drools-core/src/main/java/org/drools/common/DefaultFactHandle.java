package org.drools.common;

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
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
public class DefaultFactHandle
    implements
    InternalFactHandle {
    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /**
     * 
     */
    private static final long serialVersionUID = 400L;
    /** Handle id. */
    private int               id;
    private long              recency;
    private Object            object;
    private EqualityKey       key;
    private int               objectHashCode;
    private boolean           shadowFact;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public DefaultFactHandle() {

    }

    public DefaultFactHandle(final int id,
                             final Object object) {
        this( id,
              object,
              id );
    }

    /**
     * Construct.
     * 
     * @param id
     *            Handle id.
     */
    public DefaultFactHandle(final int id,
                             final Object object,
                             final long recency) {
        this.id = id;
        this.recency = recency;
        this.object = object;
        this.objectHashCode = object.hashCode();
    }

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /**
     * @see Object
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof DefaultFactHandle) ) {
            return false;
        }

        return this.id == ((DefaultFactHandle) object).id;
    }

    public int getObjectHashCode() {
        return this.objectHashCode;
    }

    /**
     * @see Object
     */
    public int hashCode() {
        return this.id;
    }

    /**
     * @see FactHandle
     */
    public String toExternalForm() {
        return "[fact fid:" + this.id + ":" + this.recency + ":" + this.object + "]";
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

    public void setRecency(final long recency) {
        this.recency = recency;
    }

    public long getId() {
        return this.id;
    }

    public void invalidate() {
        this.id = -1;
        this.object = null;
    }

    public boolean isShadowFact() {
        return this.shadowFact;
    }

    public void setShadowFact(final boolean shadowFact) {
        this.shadowFact = shadowFact;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(final Object object) {
        this.object = object;
    }

    /**
     * @return the key
     */
    public EqualityKey getEqualityKey() {
        return this.key;
    }

    /**
     * @param key the key to set
     */
    public void setEqualityKey(final EqualityKey key) {
        this.key = key;
    }

    /**
     * Always returns false, since the DefaultFactHandle is
     * only used for regular Facts, and not for Events
     */
    public boolean isEvent() {
        return false;
    }
}
