package org.drools.reteoo;

/*
 * $Id: TupleSet.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A set of <code>Tuple<code>s indexed by <code>TupleKey<code>s.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
class TupleSet
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Tuples, indexed by TupleKey. */
    private final Map tuples;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    TupleSet(){
        this.tuples = new HashMap();
    }

    /**
     * Construct.
     * 
     * @param initialCapacity
     *            The initial capacity of the set.
     * @param loadFactor
     *            The load factor of the set.
     */
    TupleSet(int initialCapacity,
             float loadFactor){
        this.tuples = new HashMap( initialCapacity,
                                   loadFactor );
    }

    /**
     * Construct with a single tuple.
     * 
     * @param tuple
     *            The tuple.
     */
    TupleSet(ReteTuple tuple){
        this.tuples = Collections.singletonMap( tuple.getKey(),
                                                tuple );
    }

    /**
     * Retrieve the size (number of tuples) in this set.
     * 
     * @return The size of this set.
     */
    public int size(){
        return this.tuples.size();
    }

    /**
     * Return empty status
     * 
     * @return The size of this set.
     */
    public boolean isEmpty(){
        return this.tuples.isEmpty();
    }

    /**
     * Add a single <code>Tuple</code> to this set.
     * 
     * @param tuple
     *            The tuple.
     */
    public void addTuple(ReteTuple tuple){
        this.tuples.put( tuple.getKey(),
                         tuple );
    }

    /**
     * Retract tuples from this memory.
     * 
     * @param key
     *            The key for the tuples to be removed.
     * @return <code>true</code> if at least one tuple was removed;
     *         <code>false</code> otherwise.
     */
    public boolean removeAllTuples(TupleKey key){
        boolean removed = false;
        Iterator tupleIter = this.tuples.values().iterator();
        while ( tupleIter.hasNext() ) {
            if ( ((ReteTuple) tupleIter.next()).getKey().containsAll( key ) ) {
                tupleIter.remove();
                removed = true;
            }
        }
        return removed;
    }

    /**
     * Retrieve a <code>Tuple</code> by <code>TupleKey</code>.
     * 
     * @param key
     *            The tuple key.
     * 
     * @return The matching tuple or <code>null</code> if this set contains no
     *         matching tuple.
     */
    public ReteTuple getTuple(TupleKey key){
        return (ReteTuple) this.tuples.get( key );
    }

    public boolean containsKey(TupleKey key){
        return this.tuples.containsKey( key );
    }

    /**
     * Remove a tuple from this set.
     * 
     * @param key
     *            Key matching the tuple.
     */
    public void removeTuple(TupleKey key){
        if ( this.tuples.size() == 1 ) {
            this.tuples.clear();
        }
        else {
            this.tuples.remove( key );
        }
    }

    /**
     * Retrieve an iterator over the tuples.
     * 
     * @return The iterator.
     */
    public Iterator iterator(){
        return this.tuples.values().iterator();
    }

    /**
     * Clears the TupleSet
     * 
     */
    public void clear(){
        this.tuples.clear();
    }

    public String toString(){
        return this.tuples.values().toString();
    }
}
