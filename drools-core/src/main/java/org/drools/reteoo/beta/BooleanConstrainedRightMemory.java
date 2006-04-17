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

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.drools.WorkingMemory;
import org.drools.reteoo.FactHandleImpl;
import org.drools.reteoo.ObjectMatches;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * BooleanConstrainedRightMemory
 * A boolean constrained right memory
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 19/02/2006
 */
public class BooleanConstrainedRightMemory
    implements
    BetaRightMemory {

    private BetaRightMemory childMemory  = null;

    private MultiLinkedList trueList     = null;
    private MultiLinkedList falseList    = null;
    private MultiLinkedList selectedList = null;

    private FieldExtractor  extractor    = null;
    private Declaration     declaration  = null;
    private Evaluator       evaluator    = null;

    public BooleanConstrainedRightMemory(FieldExtractor extractor,
                                         Declaration declaration,
                                         Evaluator evaluator) {
        this( extractor,
              declaration,
              evaluator,
              null );
    }

    public BooleanConstrainedRightMemory(FieldExtractor extractor,
                                         Declaration declaration,
                                         Evaluator evaluator,
                                         BetaRightMemory childMemory) {
        this.extractor = extractor;
        this.declaration = declaration;
        this.evaluator = evaluator;
        this.childMemory = childMemory;
        this.trueList = new MultiLinkedList();
        this.falseList = new MultiLinkedList();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public void add(WorkingMemory workingMemory,
                    ObjectMatches matches) {
        MultiLinkedList list = this.getMatchingList( workingMemory,
                                                     matches.getFactHandle() );
        list.add( matches );

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
        matches.getLinkedList().remove( matches );
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
        MultiLinkedList list = this.getMatchingList( workingMemory,
                                                     matches.getFactHandle() );
        list.add( wrapper );

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
                       MultiLinkedListNodeWrapper wrapper) {
        if ( this.childMemory != null ) {
            this.childMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) wrapper.getChild() );
        }
        wrapper.getLinkedList().remove( wrapper );
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
        Iterator iterator = new Iterator() {
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
        return iterator;
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#isEmpty()
     */
    public boolean isEmpty() {
        return this.trueList.isEmpty() && this.falseList.isEmpty();
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public void selectPossibleMatches(WorkingMemory workingMemory,
                                      ReteTuple tuple) {
        boolean select = ((Boolean) declaration.getValue( workingMemory.getObject( tuple.get( this.declaration ) ) )).booleanValue();
        select = (evaluator.getOperator()) == Evaluator.EQUAL ? select : !select;
        this.selectedList = (select == true) ? trueList : falseList;

        if ( this.childMemory != null ) {
            this.childMemory.selectPossibleMatches( workingMemory,
                                                    tuple );
        }
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#isPossibleMatch(org.drools.util.MultiLinkedListNodeWrapper)
     */
    public boolean isPossibleMatch(MultiLinkedListNodeWrapper matches) {
        boolean ret = false;
        if ( this.selectedList != null ) {
            ret = matches.getLinkedList() == this.selectedList;
            if ( ret && (this.childMemory != null) ) {
                ret = this.childMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) matches.getChild() );
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
    private MultiLinkedList getMatchingList(WorkingMemory workingMemory,
                                            FactHandleImpl handle) {
        boolean select = ((Boolean) this.extractor.getValue( workingMemory.getObject( handle ) )).booleanValue();
        MultiLinkedList list = (select == true) ? this.trueList : this.falseList;
        return list;
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#size()
     */
    public int size() {
        return this.trueList.size() + this.falseList.size();
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
        for ( Iterator i = this.trueList.iterator(); i.hasNext(); ) {
            set.add( i.next() );
        }
        for ( Iterator i = this.falseList.iterator(); i.hasNext(); ) {
            set.add( i.next() );
        }

        return set.iterator();
    }

}
