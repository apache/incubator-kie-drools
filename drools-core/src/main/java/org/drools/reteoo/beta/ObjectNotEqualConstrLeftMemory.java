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
import org.drools.reteoo.FactHandleImpl;
import org.drools.reteoo.ReteTuple;
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
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 18/02/2006
 */
public class ObjectNotEqualConstrLeftMemory implements BetaLeftMemory {
    private BetaLeftMemory innerMemory = null;
    
    private Map             memoryMap   = null;
    private MultiLinkedList memoryMasterList  = null;
    private MultiLinkedList noMatchList = null;
    
    private FieldExtractor extractor   = null;
    private Declaration    declaration = null;

    public ObjectNotEqualConstrLeftMemory(FieldExtractor extractor,
                                        Declaration    declaration,
                                        Evaluator      evaluator) {
        this(extractor, declaration, evaluator, null);
    }

    public ObjectNotEqualConstrLeftMemory(FieldExtractor extractor,
                                        Declaration    declaration,
                                        Evaluator      evaluator,
                                        BetaLeftMemory childMemory) {
        this.extractor = extractor;
        this.declaration = declaration;
        this.innerMemory = childMemory;
        this.memoryMap = new HashMap();
        this.memoryMasterList = new MultiLinkedList();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public void add(WorkingMemory workingMemory,
                    ReteTuple tuple) {
        this.memoryMasterList.add(tuple);
        
        // this memory is double indexed, so an additional wrapper is needed
        tuple.setChild(new MultiLinkedListNodeWrapper(tuple));
        MultiLinkedList list = getTupleBucket( workingMemory, tuple );
        list.add(tuple.getChild());
        
        if(this.innerMemory != null) {
            // double indexes require the child of the child of the tuple to be propagated
            tuple.getChild().setChild(new MultiLinkedListNodeWrapper(tuple));
            this.innerMemory.add(workingMemory, 
                                 ((MultiLinkedListNodeWrapper) tuple.getChild().getChild()));
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#remove(org.drools.reteoo.ReteTuple)
     */
    public void remove(WorkingMemory workingMemory, ReteTuple tuple) {
        if(this.innerMemory != null) {
            this.innerMemory.remove(workingMemory, 
                                    (MultiLinkedListNodeWrapper)tuple.getChild().getChild());
        }
        LinkedList list = tuple.getChild().getLinkedList(); 
        list.remove(tuple.getChild());
        if(list.isEmpty()) {
            removeMemoryEntry( list );
        }
        this.memoryMasterList.remove(tuple);
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.reteoo.ReteTuple)
     */
    public void add(WorkingMemory workingMemory, MultiLinkedListNodeWrapper tuple) {
        this.memoryMasterList.add(tuple);
        
        // this memory is double indexed, so an additional wrapper is needed
        tuple.setChild(new MultiLinkedListNodeWrapper(tuple.getNode()));

        MultiLinkedList list = this.getTupleBucket( workingMemory, (ReteTuple)tuple.getNode() );
      
        // adding the wrapper instead of the node
        list.add(tuple.getChild()); 
        
        if(this.innerMemory != null) {
            tuple.getChild().setChild(new MultiLinkedListNodeWrapper(tuple.getNode()));
            this.innerMemory.add(workingMemory, 
                                 ((MultiLinkedListNodeWrapper) tuple.getChild().getChild()));
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#remove(org.drools.reteoo.ReteTuple)
     */
    public void remove(WorkingMemory workingMemory, MultiLinkedListNodeWrapper tuple) {
        if(this.innerMemory != null) {
            this.innerMemory.remove(workingMemory, (MultiLinkedListNodeWrapper)tuple.getChild().getChild());
        }

        LinkedList list = tuple.getChild().getLinkedList();
        list.remove(tuple.getChild());
        
        if(list.isEmpty()) {
            this.removeMemoryEntry(list);
        }
        this.memoryMasterList.remove(tuple);
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#isEmpty()
     */
    public boolean isEmpty() {
        return memoryMasterList.isEmpty();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#iterator(org.drools.WorkingMemory, org.drools.reteoo.FactHandleImpl)
     */
    public Iterator iterator(final WorkingMemory workingMemory, final FactHandleImpl handle) {
        this.selectPossibleMatches(workingMemory, handle);
        Iterator iterator = new Iterator() {
            Iterator it = memoryMasterList.iterator();
            MultiLinkedListNode current = null;
            MultiLinkedListNode next = null;

            public boolean hasNext() {
                boolean hasnext = false;
                if(next == null) {
                    while(it.hasNext()) {
                        next = (MultiLinkedListNode) it.next();
                        if(next.getChild().getLinkedList() != noMatchList) {
                            if((innerMemory == null) || 
                                (innerMemory.isPossibleMatch((MultiLinkedListNodeWrapper) next.getChild().getChild()))) {
                                 hasnext = true;
                                 break;
                            }
                        }
                    }
                } else {
                    hasnext = true;
                }
                return hasnext;
            }

            public Object next() {
                if(this.next == null) {
                    this.hasNext();
                }
                this.current = this.next;
                this.next = null;
                if(this.current == null) {
                    throw new NoSuchElementException("No more items to return");
                }
                return this.current;
            }

            public void remove() {
                if(this.current != null) {
                    // Iterator is always called on the outer most memory, 
                    // so elements shall always be ReteTuples
                    ObjectNotEqualConstrLeftMemory.this.remove(workingMemory, (ReteTuple) current);
                } else {
                    throw new IllegalStateException("No item to remove. Call next() before calling remove().");
                }
            }
        };
        return iterator;
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.FactHandleImpl)
     */
    public void selectPossibleMatches(WorkingMemory workingMemory, FactHandleImpl handle) {
        Object select = this.extractor.getValue( workingMemory.getObject( handle ));
        this.noMatchList = (MultiLinkedList) this.memoryMap.get(select);
    }
    
    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#isPossibleMatch(org.drools.util.MultiLinkedListNodeWrapper)
     */
    public boolean isPossibleMatch(MultiLinkedListNodeWrapper tuple) {
        boolean ret = false;
        if((tuple != null) &&
           (tuple.getChild() != null) &&
           (tuple.getChild().getLinkedList() != null) ) {
                ret = (tuple.getChild().getLinkedList() != this.noMatchList);
                if(ret && (this.innerMemory != null)) {
                    ret = this.innerMemory.isPossibleMatch((MultiLinkedListNodeWrapper) tuple.getChild().getChild());
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
    private MultiLinkedList getTupleBucket(WorkingMemory workingMemory,
                                           ReteTuple tuple) {
        Object key = getTupleKey( workingMemory,tuple );
        MultiLinkedList list = (MultiLinkedList) this.memoryMap.get(key);
        if(list == null) {
            list = new KeyMultiLinkedList(key);
            this.memoryMap.put(key, list);
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
    private Object getTupleKey(WorkingMemory workingMemory,
                                 ReteTuple tuple) {
        Object select = declaration.getValue( 
                             workingMemory.getObject(
                             tuple.get( this.declaration ) ) );
        return select;
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#size()
     */
    public int size() {
        return this.memoryMasterList.size();
    }

    /**
     * @param list
     */
    private void removeMemoryEntry(LinkedList list) {
        this.memoryMap.remove(((KeyMultiLinkedList)list).getKey());
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
        for(Iterator i = this.memoryMap.values().iterator(); i.hasNext(); ) {
            MultiLinkedList list = (MultiLinkedList) i.next();
            if(list.size() == 0) {
                ret = false;
                break;
            }
        }
        return ret;
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
