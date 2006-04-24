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
import org.drools.reteoo.FactHandleImpl;
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

    private BetaRightMemory childMemory  = null;

    private Map             memoryMap    = null;
    private int             memorySize   = 0;

    private MultiLinkedList selectedList = null;

    private FieldExtractor  extractor    = null;
    private Declaration     declaration  = null;
    private int             column;

    public ObjectEqualConstrRightMemory(FieldExtractor extractor,
                                        Declaration declaration,
                                        Evaluator evaluator) {
        this( extractor,
              declaration,
              evaluator,
              null );
    }

    public ObjectEqualConstrRightMemory(FieldExtractor extractor,
                                        Declaration declaration,
                                        Evaluator evaluator,
                                        BetaRightMemory childMemory) {
        this.extractor = extractor;
        this.declaration = declaration;
        this.column = declaration.getColumn();
        this.childMemory = childMemory;
        this.memoryMap = new HashMap();
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public void add(WorkingMemory workingMemory,
                    ObjectMatches matches) {
        MultiLinkedList list = this.getFactList( workingMemory,
                                                 matches.getFactHandle() );
        list.add( matches );
        this.memorySize++;

        if ( this.childMemory != null ) {
            matches.setChild( new MultiLinkedListNodeWrapper( matches ) );
            this.childMemory.add( workingMemory,
                                  (MultiLinkedListNodeWrapper) matches.getChild() );
        }
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#remove(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public void remove(WorkingMemory workingMemory,
                       ObjectMatches matches) {
        if ( this.childMemory != null ) {
            this.childMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) matches.getChild() );
        }
        KeyMultiLinkedList list = (KeyMultiLinkedList) matches.getLinkedList();
        list.remove( matches );
        this.memorySize--;
        if ( list.isEmpty() ) {
            Object hash = list.getKey();
            this.memoryMap.remove( hash );
        }
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public void add(WorkingMemory workingMemory,
                    MultiLinkedListNodeWrapper wrapper) {
        ObjectMatches matches = (ObjectMatches) wrapper.getNode();
        MultiLinkedList list = this.getFactList( workingMemory,
                                                 matches.getFactHandle() );
        list.add( wrapper );
        this.memorySize++;

        if ( this.childMemory != null ) {
            wrapper.setChild( new MultiLinkedListNodeWrapper( matches ) );
            this.childMemory.add( workingMemory,
                                  (MultiLinkedListNodeWrapper) wrapper.getChild() );
        }
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#remove(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public void remove(WorkingMemory workingMemory,
                       MultiLinkedListNodeWrapper matches) {
        if ( this.childMemory != null ) {
            this.childMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) matches.getChild() );
        }
        KeyMultiLinkedList list = (KeyMultiLinkedList) matches.getLinkedList();
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
    public Iterator iterator(final WorkingMemory workingMemory,
                             final ReteTuple tuple) {
        this.selectPossibleMatches( workingMemory,
                                    tuple );
        Iterator iterator = null;
        if ( this.selectedList != null ) {
            iterator = new Iterator() {
                Iterator      it      = selectedList.iterator();
                ObjectMatches current = null;
                ObjectMatches next    = null;

                public boolean hasNext() {
                    boolean hasnext = false;
                    if ( next == null ) {
                        while ( it.hasNext() ) {
                            next = (ObjectMatches) it.next();
                            if ( (childMemory == null) || (childMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) next.getChild() )) ) {
                                hasnext = true;
                                break;
                            }
                        }
                    } else {
                        hasnext = true;
                    }
                    return hasnext;
                }

                public Object next() {
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

                public void remove() {
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
    public boolean isEmpty() {
        return this.memoryMap.isEmpty();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public void selectPossibleMatches(WorkingMemory workingMemory,
                                      ReteTuple tuple) {
        Object select = declaration.getValue( tuple.get( this.column ).getObject() );
        Integer hash = (select != null) ? new Integer( select.hashCode() ) : new Integer( 0 );
        this.selectedList = (MultiLinkedList) this.memoryMap.get( hash );

        if ( this.childMemory != null ) {
            this.childMemory.selectPossibleMatches( workingMemory,
                                                    tuple );
        }
    }

    public boolean isPossibleMatch(MultiLinkedListNodeWrapper wrapper) {
        boolean ret = false;
        if ( this.selectedList != null ) {
            ret = wrapper.getLinkedList() == this.selectedList;
            if ( ret && (this.childMemory != null) ) {
                ret = this.childMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) wrapper.getChild() );
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
    private MultiLinkedList getFactList(WorkingMemory workingMemory,
                                        InternalFactHandle handle) {
        Object select = this.extractor.getValue( handle.getObject() );
        Integer hash = (select != null) ? new Integer( select.hashCode() ) : new Integer( 0 );
        MultiLinkedList list = (MultiLinkedList) this.memoryMap.get( hash );
        if ( list == null ) {
            list = new KeyMultiLinkedList( hash );
            this.memoryMap.put( hash,
                                list );
        }
        return list;
    }

    private void removeMapEntry(KeyMultiLinkedList list) {
        Object hash = list.getKey();
        this.memoryMap.remove( hash );
    }

    public int size() {
        return this.memorySize;
    }

    /**
     * Test method that checks if there is any garbage list
     * in memory, what indicates an error.
     * It must be used only in unit tests.
     * 
     * @return
     */
    public boolean isClean() {
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

    /**
     * @inheritDoc
     */
    public Iterator iterator() {
        TreeSet set = new TreeSet( new Comparator() {
            public int compare(Object arg0,
                               Object arg1) {
                FactHandleImpl f0 = ((ObjectMatches) arg0).getFactHandle();
                FactHandleImpl f1 = ((ObjectMatches) arg1).getFactHandle();
                return (f0.getRecency() == f1.getRecency()) ? 0 : (f0.getRecency() > f1.getRecency()) ? 1 : -1;
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

    private static class KeyMultiLinkedList extends MultiLinkedList {
        private final Object key;

        public KeyMultiLinkedList(Object key) {
            this.key = key;
        }

        public final Object getKey() {
            return this.key;
        }
    }
}
