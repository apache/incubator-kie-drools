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

package org.drools.reteoo.beta;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.drools.WorkingMemory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ObjectMatches;
import org.drools.spi.Tuple;
import org.drools.util.IdentityMap;
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * @author etirelli
 *
 */
public class InstanceEqualConstrRightMemory
    implements
    BetaRightMemory {

    private static final long serialVersionUID = -2834558788591711298L;

    private BetaRightMemory innerMemory  = null;

    private Map             memoryMap    = null;

    private Object          selectedObject = null;

    private int             column;

    public InstanceEqualConstrRightMemory(final int column) {
        this( column,
              null );
    }

    public InstanceEqualConstrRightMemory(final int column,
                                        final BetaRightMemory childMemory) {
        this.column = column;
        this.innerMemory = childMemory;
        this.memoryMap = new IdentityMap();
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public final void add(final WorkingMemory workingMemory,
                          final ObjectMatches matches) {
        this.memoryMap.put( matches.getFactHandle().getObject(), matches );

        if ( this.innerMemory != null ) {
            matches.setChild( new MultiLinkedListNodeWrapper( matches ) );
            this.innerMemory.add( workingMemory,
                                  (MultiLinkedListNodeWrapper) matches.getChild() );
        }
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#remove(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public final void remove(final WorkingMemory workingMemory,
                             final ObjectMatches matches) {
        if ( this.innerMemory != null ) {
            this.innerMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) matches.getChild() );
        }
        this.memoryMap.remove( matches.getFactHandle().getObject() );
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final void add(final WorkingMemory workingMemory,
                          final MultiLinkedListNodeWrapper wrapper) {
        final ObjectMatches matches = (ObjectMatches) wrapper.getNode();
        this.memoryMap.put( matches.getFactHandle().getObject(), wrapper );

        if ( this.innerMemory != null ) {
            wrapper.setChild( new MultiLinkedListNodeWrapper( matches ) );
            this.innerMemory.add( workingMemory,
                                  (MultiLinkedListNodeWrapper) wrapper.getChild() );
        }
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#remove(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final void remove(final WorkingMemory workingMemory,
                             final MultiLinkedListNodeWrapper matches) {
        if ( this.innerMemory != null ) {
            this.innerMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) matches.getChild() );
        }
        this.memoryMap.remove( ((ObjectMatches) matches.getNode()).getFactHandle().getObject() );
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#iterator(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public final Iterator iterator(final WorkingMemory workingMemory,
                                   final Tuple tuple) {
        this.selectPossibleMatches( workingMemory,
                                    tuple );
        Iterator iterator = null;
        if ( this.selectedObject != null ) {
            iterator = Collections.singleton( this.selectedObject ).iterator();        
        } else {
            iterator = Collections.EMPTY_LIST.iterator();
        }
        return iterator;
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#isEmpty()
     */
    public final boolean isEmpty() {
        return this.memoryMap.isEmpty();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public final void selectPossibleMatches(final WorkingMemory workingMemory,
                                            final Tuple tuple) {
        final Object select = tuple.get( this.column ).getObject();
        this.selectedObject = this.memoryMap.get( select );

        if ( this.innerMemory != null ) {
            this.innerMemory.selectPossibleMatches( workingMemory,
                                                    tuple );
        }
    }

    public final boolean isPossibleMatch(final MultiLinkedListNodeWrapper wrapper) {
        boolean ret = this.selectedObject == ((ObjectMatches)wrapper.getNode()).getFactHandle().getObject();
        if ( ret && ( this.innerMemory != null ) ) {
            ret = this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) wrapper.getChild() );
        }
        return ret;
    }

    public final int size() {
        return this.memoryMap.size();
    }

    /**
     * @inheritDoc
     */
    public final Iterator iterator() {
        final TreeSet set = new TreeSet( new Comparator() {
            public int compare(Object arg0,
                               Object arg1) {
                InternalFactHandle f0 = ((ObjectMatches) arg0).getFactHandle();
                InternalFactHandle f1 = ((ObjectMatches) arg1).getFactHandle();
                return (f0.getRecency() == f1.getRecency()) ? 0 : (f0.getRecency() > f1.getRecency()) ? 1 : -1;
            }

        } );
        for ( final Iterator i = this.memoryMap.values().iterator(); i.hasNext(); ) {
            final MultiLinkedList list = (MultiLinkedList) i.next();
            for ( final Iterator j = list.iterator(); j.hasNext(); ) {
                set.add( j.next() );
            }
        }

        return set.iterator();
    }

    /**
     * @inheritDoc
     */
    public BetaRightMemory getInnerMemory() {
        return this.innerMemory;
    }

    /**
     * @inheritDoc
     */
    public void setInnerMemory(final BetaRightMemory innerMemory) {
        this.innerMemory = innerMemory;
    }

}
