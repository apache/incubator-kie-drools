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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.util.LinkedList;
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNode;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * ObjectConstrainedLeftMemory
 * An equal object constrained left memory implementation
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 15/02/2006
 */
public class ObjectEqualConstrLeftMemory
    implements
    BetaLeftMemory {

    private BetaLeftMemory  childMemory  = null;

    private Map             memoryMap    = null;
    private int             size         = 0;
    private MultiLinkedList selectedList = null;

    private FieldExtractor  extractor    = null;
    private Declaration     declaration  = null;
    private int             column;

    public ObjectEqualConstrLeftMemory(FieldExtractor extractor,
                                       Declaration declaration,
                                       Evaluator evaluator) {
        this( extractor,
              declaration,
              evaluator,
              null );
    }

    public ObjectEqualConstrLeftMemory(FieldExtractor extractor,
                                       Declaration declaration,
                                       Evaluator evaluator,
                                       BetaLeftMemory childMemory) {
        this.extractor = extractor;
        this.declaration = declaration;
        this.column = declaration.getColumn();
        this.childMemory = childMemory;
        this.memoryMap = new HashMap();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public final void add(WorkingMemory workingMemory,
                    ReteTuple tuple) {
        MultiLinkedList list = getTupleBucket( workingMemory,
                                               tuple );
        list.add( tuple );
        this.size++;
        if ( this.childMemory != null ) {
            tuple.setChild( new MultiLinkedListNodeWrapper( tuple ) );
            this.childMemory.add( workingMemory,
                                  ((MultiLinkedListNodeWrapper) tuple.getChild()) );
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#remove(org.drools.reteoo.ReteTuple)
     */
    public final void remove(WorkingMemory workingMemory,
                       ReteTuple tuple) {
        if ( this.childMemory != null ) {
            this.childMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) tuple.getChild() );
        }
        LinkedList list = tuple.getLinkedList();
        list.remove( tuple );
        this.size--;
        if ( list.isEmpty() ) {
            this.removeMemoryEntry( list );
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.reteoo.ReteTuple)
     */
    public final void add(WorkingMemory workingMemory,
                    MultiLinkedListNodeWrapper tuple) {
        MultiLinkedList list = this.getTupleBucket( workingMemory,
                                                    (ReteTuple) tuple.getNode() );

        // adding the wrapper instead of the node
        list.add( tuple );
        this.size++;

        if ( this.childMemory != null ) {
            tuple.setChild( new MultiLinkedListNodeWrapper( tuple.getNode() ) );
            this.childMemory.add( workingMemory,
                                  ((MultiLinkedListNodeWrapper) tuple.getChild()) );
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#remove(org.drools.reteoo.ReteTuple)
     */
    public final void remove(WorkingMemory workingMemory,
                       MultiLinkedListNodeWrapper tuple) {
        if ( this.childMemory != null ) {
            this.childMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) tuple.getChild() );
        }

        LinkedList list = tuple.getLinkedList();
        list.remove( tuple );
        this.size--;

        if ( list.isEmpty() ) {
            this.removeMemoryEntry( list );
        }

    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#isEmpty()
     */
    public final boolean isEmpty() {
        return memoryMap.isEmpty();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#iterator(org.drools.WorkingMemory, org.drools.reteoo.FactHandleImpl)
     */
    public final Iterator iterator(final WorkingMemory workingMemory,
                             final InternalFactHandle handle) {
        this.selectPossibleMatches( workingMemory,
                                    handle );
        Iterator iterator = null;
        if ( this.selectedList != null ) {
            iterator = new Iterator() {
                MultiLinkedListNode current   = null;
                MultiLinkedListNode next      = null;
                MultiLinkedListNode candidate = (MultiLinkedListNode) selectedList.getFirst();
                
                public final boolean hasNext() {
                    boolean hasnext = false;
                    if ( next == null ) {
                        while ( candidate != null ) {
                            if ( (childMemory == null) || (childMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) candidate.getChild() )) ) {
                                hasnext = true;
                                next = candidate;
                                candidate = (MultiLinkedListNode) candidate.getNext();
                                break;
                            }
                            candidate = (MultiLinkedListNode) candidate.getNext();
                        }
                    } else {
                        hasnext = true;
                    }
                    return hasnext;
                }

                public final Object next() {
                    if ( this.next == null ) {
                        this.hasNext();
                    }
                    this.current = this.next;
                    this.next = null;
                    if ( this.current == null ) {
                        throw new NoSuchElementException( "No more elements to return" );
                    }
                    return this.current;
                }

                public final void remove() {
                    if ( this.current != null ) {
                        ObjectEqualConstrLeftMemory.this.remove( workingMemory,
                                                                 (ReteTuple) current );
                    } else {
                        throw new IllegalStateException( "No item to remove. Call next() before calling remove()." );
                    }
                }

            };
        } else {
            iterator = Collections.EMPTY_LIST.iterator();
        }
        return iterator;
    }

    /**
     * @inheritDoc
     */
    public final Iterator iterator() {
        TreeSet set = new TreeSet( new Comparator() {
            public int compare(Object arg0,
                               Object arg1) {
                ReteTuple t0 = (ReteTuple) arg0;
                ReteTuple t1 = (ReteTuple) arg1;
                return (t0.getRecency() <= t1.getRecency()) ? -1 : 1;
            }
        } );
        for ( Iterator i = this.memoryMap.values().iterator(); i.hasNext(); ) {
            MultiLinkedList list = (MultiLinkedList) i.next();
            for ( Iterator j = list.iterator(); j.hasNext(); ) {
                set.add( j.next() );
            }
        }
        return set.iterator();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.FactHandleImpl)
     */
    public final void selectPossibleMatches(WorkingMemory workingMemory,
                                      InternalFactHandle handle) {
        Object select = this.extractor.getValue( handle.getObject() );
        Integer hash = (select != null) ? new Integer( select.hashCode() ) : new Integer( 0 );
        this.selectedList = (MultiLinkedList) this.memoryMap.get( hash );
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#isPossibleMatch(org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final boolean isPossibleMatch(MultiLinkedListNodeWrapper tuple) {
        return ((this.selectedList != null) && (tuple != null) && (tuple.getLinkedList() == this.selectedList));
    }

    /**
     * Returns the list related to the given tuple, 
     * creating if it does not exist already
     * 
     * @param workingMemory
     * @param tuple
     * @return
     */
    private final MultiLinkedList getTupleBucket(WorkingMemory workingMemory,
                                           ReteTuple tuple) {
        Integer hash = getTupleHash( workingMemory,
                                     tuple );
        MultiLinkedList list = (MultiLinkedList) this.memoryMap.get( hash );
        if ( list == null ) {
            list = new KeyMultiLinkedList( hash );
            this.memoryMap.put( hash,
                                list );
        }
        return list;
    }

    /**
     * Calculates and returns hash code for the given tuple
     * 
     * @param workingMemory
     * @param tuple
     * @return
     */
    private final Integer getTupleHash(WorkingMemory workingMemory,
                                 ReteTuple tuple) {
        Object select = declaration.getValue( tuple.get( this.column ).getObject() );
        Integer hash = (select != null) ? new Integer( select.hashCode() ) : new Integer( 0 );
        return hash;
    }

    public final int size() {
        return this.size;
    }

    /**
     * Remove a clean memory entry
     * @param list
     */
    private final void removeMemoryEntry(LinkedList list) {
        Object hash = ((KeyMultiLinkedList) list).getKey();
        this.memoryMap.remove( hash );
    }

    /**
     * Test method that checks if there is any garbage list
     * in memory, what indicates an error.
     * It must be used only in unit tests.
     * 
     * @return
     */
    public final boolean isClean() {
        boolean ret = true;
        for ( Iterator i = this.memoryMap.values().iterator(); i.hasNext(); ) {
            MultiLinkedList list = (MultiLinkedList) i.next();
            if ( list.size() == 0 ) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    private static final class KeyMultiLinkedList extends MultiLinkedList {
        private final Object key;

        public KeyMultiLinkedList(Object key) {
            this.key = key;
        }

        public final Object getKey() {
            return this.key;
        }
    }

}
