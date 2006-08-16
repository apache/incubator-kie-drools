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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.util.LinkedList;
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNode;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * ObjectNotEqualConstrLeftMemory
 * A not-equal object constrained left memory implementation 
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 18/02/2006
 */
public class ObjectNotEqualConstrLeftMemory
    implements
    BetaLeftMemory {
    private BetaLeftMemory  innerMemory      = null;

    private Map             memoryMap        = null;
    private MultiLinkedList memoryMasterList = null;
    private MultiLinkedList noMatchList      = null;

    private FieldExtractor  extractor        = null;
    private Declaration     declaration      = null;
    private Column             column;

    public ObjectNotEqualConstrLeftMemory(final FieldExtractor extractor,
                                          final Declaration declaration,
                                          final Evaluator evaluator) {
        this( extractor,
              declaration,
              evaluator,
              null );
    }

    public ObjectNotEqualConstrLeftMemory(final FieldExtractor extractor,
                                          final Declaration declaration,
                                          final Evaluator evaluator,
                                          final BetaLeftMemory childMemory) {
        this.extractor = extractor;
        this.declaration = declaration;
        this.column = declaration.getColumn();
        this.innerMemory = childMemory;
        this.memoryMap = new HashMap();
        this.memoryMasterList = new MultiLinkedList();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public final void add(final WorkingMemory workingMemory,
                          final ReteTuple tuple) {
        this.memoryMasterList.add( tuple );

        // this memory is double indexed, so an additional wrapper is needed
        tuple.setChild( new MultiLinkedListNodeWrapper( tuple ) );
        final MultiLinkedList list = getTupleBucket( workingMemory,
                                                     tuple );
        list.add( tuple.getChild() );

        if ( this.innerMemory != null ) {
            // double indexes require the child of the child of the tuple to be propagated
            tuple.getChild().setChild( new MultiLinkedListNodeWrapper( tuple ) );
            this.innerMemory.add( workingMemory,
                                  ((MultiLinkedListNodeWrapper) tuple.getChild().getChild()) );
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#remove(org.drools.reteoo.ReteTuple)
     */
    public final void remove(final WorkingMemory workingMemory,
                             final ReteTuple tuple) {
        if ( this.innerMemory != null ) {
            this.innerMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) tuple.getChild().getChild() );
        }
        final LinkedList list = tuple.getChild().getOuterList();
        list.remove( tuple.getChild() );
        if ( list.isEmpty() ) {
            removeMemoryEntry( list );
        }
        this.memoryMasterList.remove( tuple );
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.reteoo.ReteTuple)
     */
    public final void add(final WorkingMemory workingMemory,
                          final MultiLinkedListNodeWrapper tuple) {
        this.memoryMasterList.add( tuple );

        // this memory is double indexed, so an additional wrapper is needed
        tuple.setChild( new MultiLinkedListNodeWrapper( tuple.getNode() ) );

        final MultiLinkedList list = this.getTupleBucket( workingMemory,
                                                          (ReteTuple) tuple.getNode() );

        // adding the wrapper instead of the node
        list.add( tuple.getChild() );

        if ( this.innerMemory != null ) {
            tuple.getChild().setChild( new MultiLinkedListNodeWrapper( tuple.getNode() ) );
            this.innerMemory.add( workingMemory,
                                  ((MultiLinkedListNodeWrapper) tuple.getChild().getChild()) );
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#remove(org.drools.reteoo.ReteTuple)
     */
    public final void remove(final WorkingMemory workingMemory,
                             final MultiLinkedListNodeWrapper tuple) {
        if ( this.innerMemory != null ) {
            this.innerMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) tuple.getChild().getChild() );
        }

        final LinkedList list = tuple.getChild().getOuterList();
        list.remove( tuple.getChild() );

        if ( list.isEmpty() ) {
            this.removeMemoryEntry( list );
        }
        this.memoryMasterList.remove( tuple );
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#isEmpty()
     */
    public final boolean isEmpty() {
        return this.memoryMasterList.isEmpty();
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
        final Iterator iterator = new Iterator() {
            MultiLinkedListNode current   = null;
            MultiLinkedListNode next      = null;
            MultiLinkedListNode candidate = (MultiLinkedListNode) ObjectNotEqualConstrLeftMemory.this.memoryMasterList.getFirst();

            public final boolean hasNext() {
                boolean hasnext = false;
                if ( this.next == null ) {
                    while ( this.candidate != null ) {
                        if ( this.candidate.getChild().getOuterList() != ObjectNotEqualConstrLeftMemory.this.noMatchList ) {
                            if ( (ObjectNotEqualConstrLeftMemory.this.innerMemory == null) || (ObjectNotEqualConstrLeftMemory.this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) this.candidate.getChild().getChild() )) ) {
                                hasnext = true;
                                this.next = this.candidate;
                                this.candidate = (MultiLinkedListNode) this.candidate.getNext();
                                break;
                            }
                        }
                        this.candidate = (MultiLinkedListNode) this.candidate.getNext();
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
                    throw new NoSuchElementException( "No more items to return" );
                }
                return this.current;
            }

            public final void remove() {
                if ( this.current != null ) {
                    // Iterator is always called on the outer most memory, 
                    // so elements shall always be ReteTuples
                    ObjectNotEqualConstrLeftMemory.this.remove( workingMemory,
                                                                (ReteTuple) this.current );
                } else {
                    throw new IllegalStateException( "No item to remove. Call next() before calling remove()." );
                }
            }
        };
        return iterator;
    }

    /**
     * @inheritDoc
     */
    public final Iterator iterator() {
        return this.memoryMasterList.iterator();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.FactHandleImpl)
     */
    public final void selectPossibleMatches(final WorkingMemory workingMemory,
                                            final InternalFactHandle handle) {
        final Object select = this.extractor.getValue( handle.getObject() );
        this.noMatchList = (MultiLinkedList) this.memoryMap.get( select );

        if ( this.innerMemory != null ) {
            this.innerMemory.selectPossibleMatches( workingMemory,
                                                    handle );
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#isPossibleMatch(org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final boolean isPossibleMatch(final MultiLinkedListNodeWrapper tuple) {
        boolean ret = false;
        if ( (tuple != null) && (tuple.getChild() != null) && (tuple.getChild().getOuterList() != null) ) {
            ret = (tuple.getChild().getOuterList() != this.noMatchList);
            if ( ret && (this.innerMemory != null) ) {
                ret = this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) tuple.getChild().getChild() );
            }
        }
        return ret;
    }

    /**
     * Returns the list related to the given tuple, 
     * creating if it does not exist already
     * 
     * @param workingMemory
     * @param tuple
     * @return
     */
    private final MultiLinkedList getTupleBucket(final WorkingMemory workingMemory,
                                                 final ReteTuple tuple) {
        final Object key = getTupleKey( workingMemory,
                                        tuple );
        MultiLinkedList list = (MultiLinkedList) this.memoryMap.get( key );
        if ( list == null ) {
            list = new KeyMultiLinkedList( key );
            this.memoryMap.put( key,
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
    private final Object getTupleKey(final WorkingMemory workingMemory,
                                     final ReteTuple tuple) {
        final Object select = this.declaration.getValue( tuple.get( this.column.getFactIndex() ).getObject() );
        return select;
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#size()
     */
    public final int size() {
        return this.memoryMasterList.size();
    }

    /**
     * @param list
     */
    private final void removeMemoryEntry(final LinkedList list) {
        this.memoryMap.remove( ((KeyMultiLinkedList) list).getKey() );
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
        for ( final Iterator i = this.memoryMap.values().iterator(); i.hasNext(); ) {
            final MultiLinkedList list = (MultiLinkedList) i.next();
            if ( list.size() == 0 ) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    /**
     * @inheritDoc
     */
    public BetaLeftMemory getInnerMemory() {
        return this.innerMemory;
    }

    /**
     * @inheritDoc
     */
    public void setInnerMemory(final BetaLeftMemory innerMemory) {
        this.innerMemory = innerMemory;
    }

    private static class KeyMultiLinkedList extends MultiLinkedList {
        private final Object key;

        public KeyMultiLinkedList(final Object key) {
            this.key = key;
        }

        public final Object getKey() {
            return this.key;
        }
    }

}
