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
import org.drools.reteoo.ObjectMatches;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * ObjectNotEqualConstrRightMemory
 * An Object not equal constrained right memory implementation
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 22/02/2006
 */
public class ObjectNotEqualConstrRightMemory
    implements
    BetaRightMemory {

    private BetaRightMemory innerMemory      = null;

    private Map             memoryMap        = null;
    private MultiLinkedList memoryMasterList = null;
    private MultiLinkedList noMatchList      = null;

    private FieldExtractor  extractor        = null;
    private Declaration     declaration      = null;
    private int             column;

    public ObjectNotEqualConstrRightMemory(final FieldExtractor extractor,
                                           final Declaration declaration,
                                           final Evaluator evaluator) {
        this( extractor,
              declaration,
              evaluator,
              null );
    }

    public ObjectNotEqualConstrRightMemory(final FieldExtractor extractor,
                                           final Declaration declaration,
                                           final Evaluator evaluator,
                                           final BetaRightMemory childMemory) {
        this.extractor = extractor;
        this.declaration = declaration;
        this.column = declaration.getColumn();
        this.innerMemory = childMemory;
        this.memoryMap = new HashMap();
        this.memoryMasterList = new MultiLinkedList();
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public final void add(final WorkingMemory workingMemory,
                          final ObjectMatches matches) {
        // adding to master list
        this.memoryMasterList.add( matches );

        // creating child wrapper
        final MultiLinkedListNodeWrapper wrapper = new MultiLinkedListNodeWrapper( matches );
        matches.setChild( wrapper );

        // Adding to the indexed list
        final MultiLinkedList list = this.getFactList( workingMemory,
                                                       matches.getFactHandle() );
        list.add( wrapper );

        if ( this.innerMemory != null ) {
            // Adding to inner indexes
            wrapper.setChild( new MultiLinkedListNodeWrapper( matches ) );
            this.innerMemory.add( workingMemory,
                                  (MultiLinkedListNodeWrapper) wrapper.getChild() );
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
            // removing from inner indexes
            this.innerMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) matches.getChild().getChild() );
            matches.getChild().setChild( null );
        }

        // removing from indexed list
        matches.getChild().getOuterList().remove( matches.getChild() );

        if ( matches.getChild().getOuterList().isEmpty() ) {
            // removing index map entry 
            this.removeMemoryEntry( (MultiLinkedList) matches.getChild().getOuterList() );
        }
        matches.setChild( null );

        // removing from master list
        this.memoryMasterList.remove( matches );
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final void add(final WorkingMemory workingMemory,
                          final MultiLinkedListNodeWrapper matches) {
        final ObjectMatches om = (ObjectMatches) matches.getNode();

        // adding to master list
        this.memoryMasterList.add( matches );

        // creating child wrapper
        final MultiLinkedListNodeWrapper wrapper = new MultiLinkedListNodeWrapper( om );
        matches.setChild( wrapper );

        // Adding to the indexed list
        final MultiLinkedList list = this.getFactList( workingMemory,
                                                       om.getFactHandle() );
        list.add( wrapper );

        if ( this.innerMemory != null ) {
            // Adding to inner indexes
            wrapper.setChild( new MultiLinkedListNodeWrapper( om ) );
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
            // removing from inner indexes
            this.innerMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) matches.getChild().getChild() );
            matches.getChild().setChild( null );
        }

        // removing from indexed list
        matches.getChild().getOuterList().remove( matches.getChild() );

        if ( matches.getChild().getOuterList().isEmpty() ) {
            // removing index map entry 
            this.removeMemoryEntry( (MultiLinkedList) matches.getChild().getOuterList() );
        }
        matches.setChild( null );

        // removing from master list
        this.memoryMasterList.remove( matches );
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#iterator(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public final Iterator iterator(final WorkingMemory workingMemory,
                                   final ReteTuple tuple) {
        this.selectPossibleMatches( workingMemory,
                                    tuple );
        final Iterator iterator = new Iterator() {
            //Iterator      it      = memoryMasterList.iterator();
            ObjectMatches current   = null;
            ObjectMatches next      = null;
            ObjectMatches candidate = (ObjectMatches) ObjectNotEqualConstrRightMemory.this.memoryMasterList.getFirst();

            public final boolean hasNext() {
                boolean hasnext = false;
                if ( this.next == null ) {
                    while ( this.candidate != null ) {
                        if ( this.candidate.getChild().getOuterList() != ObjectNotEqualConstrRightMemory.this.noMatchList ) {
                            if ( (ObjectNotEqualConstrRightMemory.this.innerMemory == null) || (ObjectNotEqualConstrRightMemory.this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) this.candidate.getChild().getChild() )) ) {
                                hasnext = true;
                                this.next = this.candidate;
                                this.candidate = (ObjectMatches) this.candidate.getNext();
                                break;
                            }
                        }
                        this.candidate = (ObjectMatches) this.candidate.getNext();
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
                throw new UnsupportedOperationException( "Iterator.remove() should not be used to remove right side objects from right memory." );
            }

        };
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
                                            final ReteTuple tuple) {
        final Object select = this.declaration.getValue( tuple.get( this.column ).getObject() );
        final Integer hash = (select != null) ? new Integer( select.hashCode() ) : new Integer( 0 );
        this.noMatchList = (MultiLinkedList) this.memoryMap.get( hash );

        if ( this.innerMemory != null ) {
            this.innerMemory.selectPossibleMatches( workingMemory,
                                                    tuple );
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#isPossibleMatch(org.drools.reteoo.FactHandleImpl)
     */
    public final boolean isPossibleMatch(final MultiLinkedListNodeWrapper matches) {
        boolean ret = false;
        if ( (matches != null) && (matches.getChild() != null) && (matches.getChild().getOuterList() != null) ) {
            ret = (matches.getChild().getOuterList() != this.noMatchList);

            if ( ret && (this.innerMemory != null) ) {
                ret = this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) matches.getChild().getChild() );
            }
        }
        return ret;

    }

    /**
     * Returns appropriate list based on the given handle
     * 
     * @param workingMemory
     * @param handle
     * @return
     */
    private final MultiLinkedList getFactList(final WorkingMemory workingMemory,
                                              final InternalFactHandle handle) {
        final Object select = this.extractor.getValue( handle.getObject() );
        final Integer hash = (select != null) ? new Integer( select.hashCode() ) : new Integer( 0 );
        MultiLinkedList list = (MultiLinkedList) this.memoryMap.get( hash );
        if ( list == null ) {
            list = new KeyMultiLinkedList( hash );
            this.memoryMap.put( hash,
                                list );
        }
        return list;
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#size()
     */
    public final int size() {
        return this.memoryMasterList.size();
    }

    /**
     * @param matches
     */
    private final void removeMemoryEntry(final MultiLinkedList list) {
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

    public final Iterator iterator() {
        return this.memoryMasterList.iterator();
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
