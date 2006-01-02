package org.drools.reteoo;

/*
 * $Id: TupleKey.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
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

    public TupleKey(TupleKey left,
                    TupleKey right) {
        this.handles = new FactHandleList( left.handles,
                                           right.handles );
    }

    public TupleKey(int column,
                    FactHandle handle) {
        this.handles = new FactHandleList( column,
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
    public FactHandle get(int index) {
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

    public FactHandleImpl getMostRecentFact() {
        FactHandleImpl mostRecent = null;
        long currentRecency = Long.MIN_VALUE;
        FactHandleImpl eachHandle;
        long recency;

        for ( int i = this.handles.size() - 1; i >= 0; i-- ) {
            eachHandle = (FactHandleImpl) this.handles.get( i );
            if ( eachHandle != null ) {
                recency = eachHandle.getRecency();
                if ( recency > currentRecency ) {
                    currentRecency = recency;
                    mostRecent = eachHandle;
                }
            }
        }

        return mostRecent;
    }

    public FactHandleImpl getLeastRecentFact() {
        FactHandleImpl leastRecent = null;
        long currentRecency = Long.MAX_VALUE;
        FactHandleImpl eachHandle;
        long recency;

        for ( int i = this.handles.size() - 1; i >= 0; i-- ) {
            eachHandle = (FactHandleImpl) this.handles.get( i );
            if ( eachHandle != null ) {
                recency = eachHandle.getRecency();
                if ( recency < currentRecency ) {
                    currentRecency = recency;
                    leastRecent = eachHandle;
                }
            }
        }

        return leastRecent;
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
