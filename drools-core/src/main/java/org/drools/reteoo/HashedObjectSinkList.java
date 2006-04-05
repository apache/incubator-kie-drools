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

package org.drools.reteoo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.drools.WorkingMemory;
import org.drools.rule.LiteralConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;

/**
 * HashedObjectSinkList
 * A hashed implementation for ObjectSinkList to use in nodes
 * that do alpha node hashing
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 06/march/2006
 */
public class HashedObjectSinkList
    implements
    ObjectSinkList,
    Serializable {

    /** A switch map for hashed alpha nodes */
    private Map        alphaSwitch = new HashMap();
    /** The hashed sinks list, for simple and quick retrieval */
    private List       hashedSinks = new ArrayList( 1 );
    /** A list for not hashed sinks */
    private List       otherSinks  = new ArrayList( 1 );
    /** The last added objectSink */
    private ObjectSink lastObjectSink;

    /**
     * @inheritDoc
     */
    public boolean contains(ObjectSink objectSink) {
        return this.otherSinks.contains( objectSink ) || this.alphaSwitch.containsValue( objectSink );
    }

    /**
     * @inheritDoc
     */
    public boolean add(ObjectSink objectSink) {
        if ( (objectSink instanceof AlphaNode) && (((AlphaNode) objectSink).getConstraint() instanceof LiteralConstraint) && (((LiteralConstraint) ((AlphaNode) objectSink).getConstraint()).getEvaluator().getOperator() == Evaluator.EQUAL) ) {

            FieldConstraint constraint = ((AlphaNode) objectSink).getConstraint();
            AlphaNodeSwitch wrapper = new AlphaNodeSwitch( (LiteralConstraint) constraint );

            AlphaNodeSwitch aux = (AlphaNodeSwitch) this.alphaSwitch.get( wrapper );
            if ( aux == null ) {
                this.alphaSwitch.put( wrapper,
                                      wrapper );
                aux = wrapper;
            }
            aux.addAlphaNode( (AlphaNode) objectSink );
            this.hashedSinks.add( objectSink );
        } else {
            this.otherSinks.add( objectSink );
        }
        this.lastObjectSink = objectSink;
        return true;
    }

    /**
     * @inheritDoc
     */
    public boolean remove(ObjectSink objectSink) {
        if ( (objectSink instanceof AlphaNode) && (((AlphaNode) objectSink).getConstraint() instanceof LiteralConstraint) && (((LiteralConstraint) ((AlphaNode) objectSink).getConstraint()).getEvaluator().getOperator() == Evaluator.EQUAL) ) {

            FieldConstraint constraint = ((AlphaNode) objectSink).getConstraint();
            AlphaNodeSwitch wrapper = new AlphaNodeSwitch( (LiteralConstraint) constraint );

            wrapper = (AlphaNodeSwitch) this.alphaSwitch.get( wrapper );
            wrapper.removeAlphaNode( (AlphaNode) objectSink );
            this.hashedSinks.remove( objectSink );
            if ( wrapper.getSwitchCount() == 0 ) {
                this.alphaSwitch.remove( wrapper );
            }
        } else {
            this.otherSinks.remove( objectSink );
        }
        if ( this.lastObjectSink == objectSink ) {
            this.lastObjectSink = null;
        }
        return true;
    }

    /**
     * @inheritDoc
     */
    public ObjectSink getLastObjectSink() {
        return this.lastObjectSink;
    }

    /**
     * @inheritDoc
     */
    public Iterator iterator(final WorkingMemory workingMemory,
                             final FactHandleImpl handle) {
        return new Iterator() {
            private static final int FLAG_ITER_HASH = 0;
            private static final int FLAG_ITER_LIST = 1;

            private Iterator         it             = alphaSwitch.values().iterator();
            private ObjectSink       current        = null;
            private ObjectSink       next           = null;
            private int              flag           = FLAG_ITER_HASH;

            public boolean hasNext() {
                boolean hasnext = false;
                if ( next == null ) {
                    switch ( flag ) {
                        // iterating over hashed list
                        case FLAG_ITER_HASH :
                            while ( it.hasNext() ) {
                                AlphaNodeSwitch wrapper = (AlphaNodeSwitch) it.next();
                                next = wrapper.getNode( workingMemory,
                                                        handle );
                                if ( next != null ) {
                                    hasnext = true;
                                    break;
                                }
                            }
                            if ( hasnext == false ) {
                                it = otherSinks.iterator();
                                hasnext = it.hasNext();
                                if ( hasnext ) {
                                    next = (ObjectSink) it.next();
                                }
                                flag = FLAG_ITER_LIST;
                            }
                            break;
                        // interating over other not hashed sinks
                        case FLAG_ITER_LIST :
                            hasnext = it.hasNext();
                            if ( hasnext ) {
                                next = (ObjectSink) it.next();
                            }
                            break;
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
                if ( this.current != null ) {
                    HashedObjectSinkList.this.remove( current );
                    this.current = null;
                } else {
                    throw new IllegalStateException( "No item to remove. Call next() before calling remove()." );
                }
            }
        };
    }

    /**
     * @inheritDoc
     */
    public Iterator iterator() {
        return new Iterator() {
            private static final int FLAG_ITER_HASH = 0;
            private static final int FLAG_ITER_LIST = 1;

            private Iterator         it             = hashedSinks.iterator();
            private ObjectSink       current        = null;
            private ObjectSink       next           = null;
            private int              flag           = FLAG_ITER_HASH;

            public boolean hasNext() {
                boolean hasnext = false;
                if ( next == null ) {
                    switch ( flag ) {
                        // iterating over hashed list
                        case FLAG_ITER_HASH :
                            hasnext = it.hasNext();
                            if ( hasnext ) {
                                next = (ObjectSink) it.next();
                            } else {
                                it = otherSinks.iterator();
                                hasnext = it.hasNext();
                                if ( hasnext ) {
                                    next = (ObjectSink) it.next();
                                }
                                flag = FLAG_ITER_LIST;
                            }
                            break;
                        // interating over other not hashed sinks
                        case FLAG_ITER_LIST :
                            hasnext = it.hasNext();
                            if ( hasnext ) {
                                next = (ObjectSink) it.next();
                            }
                            break;
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
                if ( this.current != null ) {
                    HashedObjectSinkList.this.remove( current );
                    this.current = null;
                } else {
                    throw new IllegalStateException( "No item to remove. Call next() before calling remove()." );
                }
            }
        };
    }

    /**
     * @inheritDoc
     * 
     * This method is not efficient and shall be used only for debugging and
     * test purposes.
     */
    public List getObjectsAsList() {
        List list = new ArrayList();
        for ( Iterator i = this.alphaSwitch.values().iterator(); i.hasNext(); ) {
            AlphaNodeSwitch wrapper = (AlphaNodeSwitch) i.next();
            list.addAll( wrapper.getAllNodes() );
        }
        list.addAll( this.otherSinks );
        return list;
    }

}
