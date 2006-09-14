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
import org.drools.base.evaluators.Operator;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ObjectMatches;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;
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

    private BetaRightMemory innerMemory  = null;

    private MultiLinkedList trueList     = null;
    private MultiLinkedList falseList    = null;
    private MultiLinkedList selectedList = null;

    private FieldExtractor  extractor    = null;
    private Declaration     declaration  = null;
    private Column             column;
    private Evaluator       evaluator    = null;

    public BooleanConstrainedRightMemory(final FieldExtractor extractor,
                                         final Declaration declaration,
                                         final Evaluator evaluator) {
        this( extractor,
              declaration,
              evaluator,
              null );
    }

    public BooleanConstrainedRightMemory(final FieldExtractor extractor,
                                         final Declaration declaration,
                                         final Evaluator evaluator,
                                         final BetaRightMemory childMemory) {
        this.extractor = extractor;
        this.declaration = declaration;
        this.column = declaration.getColumn();
        this.evaluator = evaluator;
        this.innerMemory = childMemory;
        this.trueList = new MultiLinkedList();
        this.falseList = new MultiLinkedList();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public final void add(final WorkingMemory workingMemory,
                          final ObjectMatches matches) {
        final MultiLinkedList list = this.getMatchingList( workingMemory,
                                                           matches.getFactHandle() );
        list.add( matches );

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
        matches.getOuterList().remove( matches );
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
        final MultiLinkedList list = this.getMatchingList( workingMemory,
                                                           matches.getFactHandle() );
        list.add( wrapper );

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
                             final MultiLinkedListNodeWrapper wrapper) {
        if ( this.innerMemory != null ) {
            this.innerMemory.remove( workingMemory,
                                     (MultiLinkedListNodeWrapper) wrapper.getChild() );
        }
        wrapper.getOuterList().remove( wrapper );
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
        final Iterator iterator = new Iterator() {
            ObjectMatches current   = null;
            ObjectMatches next      = null;
            ObjectMatches candidate = (ObjectMatches) BooleanConstrainedRightMemory.this.selectedList.getFirst();

            public final boolean hasNext() {
                boolean hasnext = false;
                if ( this.next == null ) {
                    while ( this.candidate != null ) {
                        if ( (BooleanConstrainedRightMemory.this.innerMemory == null) || (BooleanConstrainedRightMemory.this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) this.candidate.getChild() )) ) {
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
        return iterator;
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#isEmpty()
     */
    public final boolean isEmpty() {
        return this.trueList.isEmpty() && this.falseList.isEmpty();
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public final void selectPossibleMatches(final WorkingMemory workingMemory,
                                            final Tuple tuple) {
        boolean select = ((Boolean) this.declaration.getValue( tuple.get( this.column.getFactIndex() ).getObject() )).booleanValue();
        select = (this.evaluator.getOperator()) == Operator.EQUAL ? select : !select;
        this.selectedList = (select == true) ? this.trueList : this.falseList;

        if ( this.innerMemory != null ) {
            this.innerMemory.selectPossibleMatches( workingMemory,
                                                    tuple );
        }
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#isPossibleMatch(org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final boolean isPossibleMatch(final MultiLinkedListNodeWrapper matches) {
        boolean ret = false;
        if ( this.selectedList != null ) {
            ret = matches.getOuterList() == this.selectedList;
            if ( ret && (this.innerMemory != null) ) {
                ret = this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) matches.getChild() );
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
    private final MultiLinkedList getMatchingList(final WorkingMemory workingMemory,
                                                  final InternalFactHandle handle) {
        final boolean select = ((Boolean) this.extractor.getValue( handle.getObject() )).booleanValue();
        final MultiLinkedList list = (select == true) ? this.trueList : this.falseList;
        return list;
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#size()
     */
    public final int size() {
        return this.trueList.size() + this.falseList.size();
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
        for ( final Iterator i = this.trueList.iterator(); i.hasNext(); ) {
            set.add( i.next() );
        }
        for ( final Iterator i = this.falseList.iterator(); i.hasNext(); ) {
            set.add( i.next() );
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
