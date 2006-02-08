package org.drools.leaps;
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
    public static final TupleKey EMPTY_KEY = new TupleKey();

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Columns. */
    private final FactHandleList handles;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    private TupleKey() {
        this.handles = FactHandleList.EMPTY_LIST;// FactHandleList.EMPTY_LIST;
    }
    
    public TupleKey(TupleKey key) {
        this.handles = key.handles;
    }    

    public TupleKey(FactHandleImpl handle) {
        this.handles = new FactHandleList( handle );
    }
    
    public TupleKey(TupleKey left,
                    FactHandleImpl handle) {
        this.handles = new FactHandleList( left.handles,
                                           handle );
    }

    public String toString() {
        return "[TupleKey: handles=" + this.handles + "]";
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
    public FactHandleImpl get(int index) {
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
    public boolean containsFactHandle(FactHandle handle) {
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
    public boolean containsAll(TupleKey that) {
        return this.handles.containsAll( that.handles );
    }
    
    FactHandle[] getFactHandles() {
        return this.handles.getHandles();
    }
    
    public int size() {
        return this.handles.size();
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @see Object
     */
    public boolean equals(Object object) {
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
