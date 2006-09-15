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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.drools.WorkingMemory;
import org.drools.base.evaluators.Operator;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNode;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * BooleanConstrainedLeftMemory
 * A boolean constrained implementation for the left memory
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 12/02/2006
 */
public class BooleanConstrainedLeftMemory
    implements
    BetaLeftMemory {

    private static final long serialVersionUID = 2385678633684465433L;

    private BetaLeftMemory  innerMemory  = null;

    private MultiLinkedList trueList     = null;
    private MultiLinkedList falseList    = null;
    private MultiLinkedList selectedList = null;

    private FieldExtractor  extractor    = null;
    private Declaration     declaration  = null;
    private Column             column;
    private Evaluator       evaluator    = null;
    
    public BooleanConstrainedLeftMemory(final FieldExtractor extractor,
                                        final Declaration declaration,
                                        final Evaluator evaluator) {
        this( extractor,
              declaration,
              evaluator,
              null );
    }

    public BooleanConstrainedLeftMemory(final FieldExtractor extractor,
                                        final Declaration declaration,
                                        final Evaluator evaluator,
                                        final BetaLeftMemory childMemory) {
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
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public final void add(final WorkingMemory workingMemory,
                          final ReteTuple tuple) {
        final boolean select = ((Boolean) this.declaration.getValue( tuple.get( this.column.getFactIndex() ).getObject() )).booleanValue();
        if ( select == true ) {
            this.trueList.add( tuple );
        } else {
            this.falseList.add( tuple );
        }
        if ( this.innerMemory != null ) {
            tuple.setChild( new MultiLinkedListNodeWrapper( tuple ) );
            this.innerMemory.add( workingMemory,
                                  ((MultiLinkedListNodeWrapper) tuple.getChild()) );
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
                                     (MultiLinkedListNodeWrapper) tuple.getChild() );
        }
        tuple.getOuterList().remove( tuple );
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.reteoo.ReteTuple)
     */
    public final void add(final WorkingMemory workingMemory,
                          final MultiLinkedListNodeWrapper tuple) {
        final boolean partition = ((Boolean) this.declaration.getValue( ((Tuple) tuple.getNode()).get( this.column.getFactIndex() ).getObject() )).booleanValue();
        if ( partition == true ) {
            this.trueList.add( tuple );
        } else {
            this.falseList.add( tuple );
        }
        if ( this.innerMemory != null ) {
            tuple.setChild( new MultiLinkedListNodeWrapper( tuple.getNode() ) );
            this.innerMemory.add( workingMemory,
                                  ((MultiLinkedListNodeWrapper) tuple.getChild()) );
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
                                     (MultiLinkedListNodeWrapper) tuple.getChild() );
        }
        tuple.getOuterList().remove( tuple );
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#isEmpty()
     */
    public final boolean isEmpty() {
        return (this.trueList.isEmpty()) && (this.falseList.isEmpty());
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
            MultiLinkedListNode candidate = (MultiLinkedListNode) BooleanConstrainedLeftMemory.this.selectedList.getFirst();

            public final boolean hasNext() {
                boolean hasnext = false;
                if ( this.next == null ) {
                    while ( this.candidate != null ) {
                        if ( (BooleanConstrainedLeftMemory.this.innerMemory == null) || (BooleanConstrainedLeftMemory.this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) this.candidate.getChild() )) ) {
                            hasnext = true;
                            this.next = this.candidate;
                            this.candidate = (MultiLinkedListNode) this.candidate.getNext();
                            break;
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
                    throw new NoSuchElementException( "No more elements to return" );
                }
                return this.current;
            }

            public final void remove() {
                if ( this.current != null ) {
                    BooleanConstrainedLeftMemory.this.remove( workingMemory,
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
        return new Iterator() {
            Iterator  trueIt       = BooleanConstrainedLeftMemory.this.trueList.iterator();
            Iterator  falseIt      = BooleanConstrainedLeftMemory.this.falseList.iterator();
            Tuple currentTrue  = null;
            Tuple currentFalse = null;
            Tuple current      = null;
            Tuple next         = null;

            public final boolean hasNext() {
                boolean hasnext = false;
                if ( this.next == null ) {
                    if ( (this.currentTrue == null) && (this.trueIt.hasNext()) ) {
                        this.currentTrue = (Tuple) this.trueIt.next();
                    }
                    if ( (this.currentFalse == null) && (this.falseIt.hasNext()) ) {
                        this.currentFalse = (Tuple) this.falseIt.next();
                    }
                    if ( (this.currentTrue != null) && (this.currentFalse != null) ) {
                        if ( this.currentTrue.getRecency() <= this.currentFalse.getRecency() ) {
                            this.next = this.currentTrue;
                            this.currentTrue = null;
                        } else {
                            this.next = this.currentFalse;
                            this.currentFalse = null;
                        }
                        hasnext = true;
                    } else if ( this.currentTrue != null ) {
                        this.next = this.currentTrue;
                        this.currentTrue = null;
                        hasnext = true;
                    } else if ( this.currentFalse != null ) {
                        this.next = this.currentFalse;
                        this.currentFalse = null;
                        hasnext = true;
                    }
                    // if no previous condition evaluates to true, 
                    // than next will be null and hasnext will be false
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
                throw new UnsupportedOperationException( "Not possible to call remove when iterating over all elements" );
            }
        };
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.FactHandleImpl)
     */
    public final void selectPossibleMatches(final WorkingMemory workingMemory,
                                            final InternalFactHandle handle) {
        boolean select = ((Boolean) this.extractor.getValue( handle.getObject() )).booleanValue();
        select = (this.evaluator.getOperator()) == Operator.EQUAL ? select : !select;
        if ( select == true ) {
            this.selectedList = this.trueList;
        } else {
            this.selectedList = this.falseList;
        }
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
        boolean isPossible = ((this.selectedList != null) && (tuple.getOuterList() == this.selectedList));
        if ( (isPossible) && (this.innerMemory != null) ) {
            isPossible = this.innerMemory.isPossibleMatch( (MultiLinkedListNodeWrapper) tuple.getChild() );
        }
        return isPossible;
    }

    public final int size() {
        return this.trueList.size() + this.falseList.size();
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

}
