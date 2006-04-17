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

package org.drools.reteoo;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;
import org.drools.util.BaseMultiLinkedListNode;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;

/**
 * Rete-OO <code>Tuple</code> implementation. A <code>ReteTuple</code> implements <code>LinkedListNode</code> so that it can referenced
 * in a <code>LinkedList<code>, this allows for fast removal, without searching; as <code>ReteTuple</code>s are add sequentially to a nodes 
 * memory (<code>LinkedList</code>), but removed randomly. 
 * 
 * Each <code>ReteTuple</code> has a <code>Map</code> of matches. The key is the matching <code>FactHandleImpl</code> and the value
 * is the <code>TupleMatch</code>, which is also referenced in the <code>ObjectMatches</code> <code>LinkedList</code>.
 * <p>
 * Each <code>ReteTuple</code> also reference a <code>TupleKey</code> which is special array of FactHandles
 * representing the <code>Column</code>s in a <code>Rule</code>. The <code>TupleKey</code> also provide the 
 * hashcode implementation for <code>ReteTuple</code>.
 * 
 * @see Tuple
 * @see TupleMatch
 * @see TupleKey
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class ReteTuple extends BaseMultiLinkedListNode
    implements
    Tuple,
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The </code>TupleKey</code> */
    private final TupleKey key;

    /** The <code>Map</code> of <code>FactHandleImpl</code> matches */
    private Map            matches = Collections.EMPTY_MAP;

    /** The resuling propagation when used in a <code>NotNode</code> */
    private LinkedList     linkedTuples;

    private Activation     activation;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    /**
     * Creates a <code>ReteTuple</code> with the handle at the specified column in the <code>TupleKey</code>.
     * 
     * @param column
     *      The column position of the handle in the <code>TupleKey</code>
     * @param handle
     *      The <code>FactHandleImpl</code> to be placed in the <code>TupleKey</code> at the given column position.
     * @param workingMemory
     *      The <code>WorkingMemory</code> session.
     */
    public ReteTuple(FactHandleImpl handle) {
        this.key = new TupleKey( handle );
    }

    /**
     * Creates a copy of the passed <code>ReteTuple</code>.
     * 
     * @param tuple
     */
    ReteTuple(ReteTuple tuple) {
        this.key = new TupleKey( tuple.key );
    }

    /**
     * Merges the left <code>ReteTuple</code> with the right <code>FactHandleImpl</code>.
     * 
     * @param left
     *      The <code>ReteTuple</code> to be joined.
     * @param handle
     *      the <code>FactHandleImpl</code> to be joined.
     */
    ReteTuple(ReteTuple left,
              FactHandleImpl handle) {
        this.key = new TupleKey( left.key,
                                 handle );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the key for this tuple.
     * 
     * @return The key.
     */
    TupleKey getKey() {
        return this.key;
    }

    public long getRecency() {
        return this.key.getRecency();
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Tuple#getFactHandles()
     */
    public FactHandle[] getFactHandles() {
        return this.key.getFactHandles();
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
    public boolean dependsOn(FactHandle handle) {
        return this.key.containsFactHandle( handle );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /* (non-Javadoc)
     * @see org.drools.spi.Tuple#get(int)
     */
    public FactHandle get(int col) {
        return this.key.get( col );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Tuple#get(org.drools.rule.Declaration)
     */
    public FactHandle get(Declaration declaration) {
        return get( declaration.getColumn() );
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public Activation getActivation() {
        return this.activation;
    }

    /**
     * Specifies the tuple as the result of <code>NotNode</code> propagation.
     * 
     * @param tuple
     *      The <code>ReteTuple</code>
     */
    public void addLinkedTuple(LinkedListNode node) {
        if ( this.linkedTuples == null ) {
            this.linkedTuples = new LinkedList();
        }
        this.linkedTuples.add( node );
    }

    /**
     * Returns the tuple from the result of <code>NotNode</code> propagation. If there has been no 
     * <code>NotNode</code> propagation then it returns null.
     * 
     * @return
     *  The <code>ReteTuple</code>
     */
    public LinkedList getLinkedTuples() {
        return this.linkedTuples;
    }

    public void clearLinkedTuple() {
        this.linkedTuples.clear();
    }

    public void addTupleMatch(FactHandleImpl handle,
                              TupleMatch node) {
        if ( this.matches == Collections.EMPTY_MAP ) {
            this.matches = new HashMap();
        }
        this.matches.put( handle,
                          node );
    }

    public int matchesSize() {
        return this.matches.size();
    }

    public Map getTupleMatches() {
        return this.matches;
    }

    public TupleMatch getTupleMatch(FactHandleImpl handle) {
        return (TupleMatch) this.matches.get( handle );
    }

    public TupleMatch removeMatch(FactHandleImpl handle) {
        return (TupleMatch) this.matches.remove( handle );
    }

    //    public void remove(PropagationContext context,
    //                       WorkingMemoryImpl workingMemory) {
    //        if ( this.callback != null ) {
    //            this.callback.retract( this,
    //                                   context,
    //                                   workingMemory );
    //        }
    //
    //        if ( !this.matches.isEmpty() ) {
    //            for ( Iterator it = this.matches.values().iterator(); it.hasNext(); ) {
    //                TupleMatch node = (TupleMatch) it.next();
    //                if ( node != null && node.getJoinedTuple() != null ) {
    //                    node.getJoinedTuple().remove( context,
    //                                                  workingMemory );
    //                }
    //                node.remove( context,
    //                             workingMemory );
    //            }
    //        }
    //
    //        if ( previous != null ) {
    //            this.previous.setNext( this.next );
    //            if ( this.next != null ) {
    //                this.next.setPrevious( this.previous );
    //            }
    //        }
    //
    //        this.previous = null;
    //        this.next = null;
    //        this.matches = null;
    //    }         

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof ReteTuple) ) {
            return false;
        }

        return this.key.equals( ((ReteTuple) object).key );
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for ( int i = 0; i < this.key.size(); i++ ) {
            buffer.append( this.key.get( i ) + ", " );
        }
        return buffer.toString();
    }
}
