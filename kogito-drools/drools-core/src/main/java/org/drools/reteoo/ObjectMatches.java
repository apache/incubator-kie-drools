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

import org.drools.util.LinkedList;

/**
 * 
 * <code>ObjectMatches</code> maintains a reference to its <code>FactHandleImpl</code> and a <code>LinkedList</code> of <code>TupleMatch</code>es.
 * 
 * @see TupleMatch
 * @see LinkedList
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
class ObjectMatches {
    private LinkedList list;
    
    private final FactHandleImpl handle;    

    /**
     * Constructs an ObjectMatches which maintain a reference to its <code>FactHandleImpl</code> with an empty <code>LinkedList</code>.
     *  
     * @param handle
     */
    public ObjectMatches(FactHandleImpl handle) {
        this.list = new LinkedList();
        this.handle = handle;
    }


    /**
     * Adds a matched <code>ReteTuple</code>, which is then wrapped in a <code>TupleMatch</code> which is added to 
     * the <code>LinkedList</code> and then returned.
     * 
     * @param tuple
     * @return
     */
    TupleMatch add(ReteTuple tuple) {               
        TupleMatch tupleMatch = new TupleMatch( tuple, this );

        this.list.add( tupleMatch );
        
        return tupleMatch;
    }
        
    /**
     * Removes the <code>TupleMatch</code> as the underlying <code>ReteTuple</code> has been retracted and no longer matches.
     * 
     * @param tupleMatch
     */
    void remove(TupleMatch tupleMatch) {
        this.list.remove( tupleMatch );          
    }

    /**
     * Return <code>FactHandleImpl</code> that this provides the <code>TupleMatch</code>es for.
     * @return
     */
    public FactHandleImpl getFactHandle() {
        return this.handle;
    }
    
    /**
     * Return the first <code>TupleMatch</code> that this <code>FactHandleImpl</code> matches.
     * 
     * @return the <code>TupleMatch</code>
     */
    public TupleMatch getFirstTupleMatch() {
        return (TupleMatch) this.list.getFirst();
    }

    /**
     * Return the last <code>TupleMatch</code> that this <code>FactHandleImpl</code> matches.
     * 
     * @return the <code>TupleMatch</code>
     */    
    public TupleMatch getLastTupleMatch() {
        return (TupleMatch) this.list.getLast();
    }  
    
    public int size() {
        return this.list.size();
    }
    
    public boolean hasMatches() {
        return !this.list.isEmpty();
    }
}
