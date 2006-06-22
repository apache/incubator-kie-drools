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
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ObjectMatches;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * ObjectEqualConstrRightMemory
 * An Equal Constrained Right Memory
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 22/02/2006
 */
public class ObjectEqualConstrRightMemory
    implements
    BetaRightMemory {

    private BetaRightMemory innerMemory  = null;

    private Map             memoryMap    = null;
    private int             memorySize   = 0;

    private MultiLinkedList selectedList = null;

    private FieldExtractor  extractor    = null;
    private Declaration     declaration  = null;
    private int             column;

    public ObjectEqualConstrRightMemory(final FieldExtractor extractor,
                                        final Declaration declaration,
                                        final Evaluator evaluator) {
        this( extractor,
              declaration,
              evaluator,
              null );
    }

    public ObjectEqualConstrRightMemory(final FieldExtractor extractor,
                                        final Declaration declaration,
                                        final Evaluator evaluator,
                                        final BetaRightMemory childMemory) {
        this.extractor = extractor;
        this.declaration = declaration;
        this.column = declaration.getColumn();
        this.innerMemory = childMemory;
        this.memoryMap = new HashMap();
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public final void add(final WorkingMemory workingMemory,
                          final ObjectMatches matches) {
        final MultiLinkedList list = this.getFactList( workingMemory,
                                                       matches.getFactHandle() );
        list.add( matches );
        this.memorySize++;

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
        final KeyMultiLinkedList list = (KeyMultiLinkedList) matches.getOuterList();
        list.remove( matches );
        this.memorySize--;
        if ( list.isEmpty() ) {
            final Object hash = list.getKey();
            this.memoryMap.remove( hash );
        }
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
        final MultiLinkedList list = this.getFactList( workingMemory,
                                                       matches.getFactHandle() );
        list.add( wrapper );
        this.memorySize++;

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
        final KeyMultiLinkedList list = (KeyMultiLinkedList) matches.getOuterList();
        list.remove( matches );
        this.memorySize--;
        if ( list.isEmpty() ) {
            this.removeMapEntry( list );
        }
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
        Iterator iterator = null;
        if ( this.selectedList != null ) {
            iterator = new Iterator() {
                ObjectMatches current   = null;
                ObjectMatches next      = null;
                ObjectMatches candidate = (ObjectMatches) ObjectEqualConstrRightMemory.this.selectedList.getFirst();

                public final boolean hasNext() {
                    boolean hasnext = false;
                    if ( this.next == null ) {
                        while ( this.candidate != null ) {
                            if ( (ObjectEqualConstrRightMemory.this.innerMemory == null) || (ObjectEqualConstrRightMemory.this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) this.candidate.getChild() )) ) {
                                hasnext = true;
                                this.next = this.candidate;
                                this.candidate = (ObjectMatches) this.candidate.getNext();
                                break;
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
                                            final ReteTuple tuple) {
        final Object select = this.declaration.getValue( tuple.get( this.column ).getObject() );
        final Integer hash = (select != null) ? new Integer( select.hashCode() ) : new Integer( 0 );
        this.selectedList = (MultiLinkedList) this.memoryMap.get( hash );

        if ( this.innerMemory != null ) {
            this.innerMemory.selectPossibleMatches( workingMemory,
                                                    tuple );
        }
    }

    public final boolean isPossibleMatch(final MultiLinkedListNodeWrapper wrapper) {
        boolean ret = false;
        if ( this.selectedList != null ) {
            ret = wrapper.getOuterList() == this.selectedList;
            if ( ret && (this.innerMemory != null) ) {
                ret = this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) wrapper.getChild() );
            }
        }
        return ret;
    }

    /**
     * Returns appropriate map based on the given handle
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

    private final void removeMapEntry(final KeyMultiLinkedList list) {
        final Object hash = list.getKey();
        this.memoryMap.remove( hash );
    }

    public final int size() {
        return this.memorySize;
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
    public final Iterator iterator() {
        final TreeSet set = new TreeSet( new Comparator() {
            public int compare(Object arg0,
                               Object arg1) {
                DefaultFactHandle f0 = ((ObjectMatches) arg0).getFactHandle();
                DefaultFactHandle f1 = ((ObjectMatches) arg1).getFactHandle();
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
