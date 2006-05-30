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

import java.io.Serializable;

import org.drools.FactHandle;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.spi.Tuple;

/**
 * A composite key to match tuples.
 * 
 * @see Tuple
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
class TupleKey
    implements
    Serializable {
    /**
     * 
     */
    private static final long    serialVersionUID = -880184112928387666L;

    public static final TupleKey EMPTY_KEY        = new TupleKey();

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Columns. */
    private final FactHandleList handles;

    /**
     * The recency of this tuple is given by the 
     * highest recency of all added fact handles
     */
    private long                 recency          = 0;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    private TupleKey() {
        this.handles = FactHandleList.EMPTY_LIST;// FactHandleList.EMPTY_LIST;
    }

    public TupleKey(final TupleKey key) {
        this.handles = key.handles;
        this.recency = key.recency;
    }

    public TupleKey(final DefaultFactHandle handle) {
        this.handles = new FactHandleList( handle );
        this.recency = handle.getRecency();
    }

    public TupleKey(final TupleKey left,
                    final DefaultFactHandle handle) {
        this.handles = new FactHandleList( left.handles,
                                           handle );
        this.recency = left.recency + handle.getRecency();
    }

    public String toString() {
        return "[TupleKey: recency=" + this.recency + " handles=" + this.handles + "]";
    }

    // ------------------------------------------------------------
    //
    // ------------------------------------------------------------

    /**
     * Retrieve a <code>FactHandle</code> by declaration.
     * 
     * @param declaration
     *            The declaration.
     * 
     * @return The fact handle.
     */
    public InternalFactHandle get(final int index) {
        return this.handles.get( index );
    }

    /**
     * Determine if this key contains the specified root fact object.
     * 
     * @param handle
     *            The fact-handle to test.
     * 
     * @return <code>true</code> if this key contains the specified root
     *         fact-handle, otherwise <code>false</code>.
     */
    public boolean containsFactHandle(final FactHandle handle) {
        return this.handles.contains( handle );
    }

    /**
     * Determine if the specified key is a subset of this key.
     * 
     * @param that
     *            The key to compare.
     * 
     * @return <code>true</code> if the specified key is a subset of this key.
     */
    public boolean containsAll(final TupleKey that) {
        return this.handles.containsAll( that.handles );
    }

    InternalFactHandle[] getFactHandles() {
        return this.handles.getHandles();
    }

    public int size() {
        return this.handles.size();
    }

    public long getRecency() {
        return this.recency;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @see Object
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        return this.handles.equals( ((TupleKey) object).handles );
    }

    /**
     * @see Object
     */
    public int hashCode() {
        return this.handles.hashCode();
    }

}
