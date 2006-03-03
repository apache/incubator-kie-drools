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
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 22/02/2006
 */
public class ObjectNotEqualConstrRightMemory
    implements
    BetaRightMemory {

    private BetaRightMemory childMemory = null;
    
    private Map             memoryMap        = null;
    private MultiLinkedList memoryMasterList = null;
    private MultiLinkedList noMatchList      = null;
    
    private FieldExtractor extractor   = null;
    private Declaration    declaration = null;
    
    public ObjectNotEqualConstrRightMemory(FieldExtractor extractor,
                                        Declaration    declaration,
                                        Evaluator      evaluator) {
        this(extractor, declaration, evaluator, null);
    }

    public ObjectNotEqualConstrRightMemory(FieldExtractor extractor,
                                        Declaration    declaration,
                                        Evaluator      evaluator,
                                        BetaRightMemory childMemory) {
        this.extractor = extractor;
        this.declaration = declaration;
        this.childMemory = childMemory;
        this.memoryMap = new HashMap();
        this.memoryMasterList = new MultiLinkedList();
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public void add(WorkingMemory workingMemory,
                    ObjectMatches matches) {
        // adding to master list
        this.memoryMasterList.add(matches);
        
        // creating child wrapper
        MultiLinkedListNodeWrapper wrapper = new MultiLinkedListNodeWrapper(matches);
        matches.setChild(wrapper);
        
        // Adding to the indexed list
        MultiLinkedList list = this.getFactList( workingMemory, matches.getFactHandle() );
        list.add(wrapper);
        
        if(this.childMemory != null) {
            // Adding to inner indexes
            wrapper.setChild(new MultiLinkedListNodeWrapper(matches));
            this.childMemory.add(workingMemory, (MultiLinkedListNodeWrapper) wrapper.getChild());
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
        if(this.childMemory != null) {
            // removing from inner indexes
            this.childMemory.remove(workingMemory, (MultiLinkedListNodeWrapper) matches.getChild().getChild());
            matches.getChild().setChild(null);
        }
        
        // removing from indexed list
        matches.getChild().getLinkedList().remove(matches.getChild());
        
        if(matches.getChild().getLinkedList().isEmpty()) {
            // removing index map entry 
            this.removeMemoryEntry( (MultiLinkedList) matches.getChild().getLinkedList() );
        }
        matches.setChild(null);
        
        // removing from master list
        this.memoryMasterList.remove(matches);
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public void add(WorkingMemory workingMemory,
                    MultiLinkedListNodeWrapper matches) {
        ObjectMatches om = (ObjectMatches) matches.getNode(); 
        
        // adding to master list
        this.memoryMasterList.add(matches);
        
        // creating child wrapper
        MultiLinkedListNodeWrapper wrapper = new MultiLinkedListNodeWrapper(om);
        matches.setChild(wrapper);
        
        // Adding to the indexed list
        MultiLinkedList list = this.getFactList( workingMemory, om.getFactHandle() );
        list.add(wrapper);
        
        if(this.childMemory != null) {
            // Adding to inner indexes
            wrapper.setChild(new MultiLinkedListNodeWrapper(om));
            this.childMemory.add(workingMemory, (MultiLinkedListNodeWrapper) wrapper.getChild());
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
        if(this.childMemory != null) {
            // removing from inner indexes
            this.childMemory.remove(workingMemory, (MultiLinkedListNodeWrapper) matches.getChild().getChild());
            matches.getChild().setChild(null);
        }
        
        // removing from indexed list
        matches.getChild().getLinkedList().remove(matches.getChild());
        
        if(matches.getChild().getLinkedList().isEmpty()) {
            // removing index map entry 
            this.removeMemoryEntry((MultiLinkedList) matches.getChild().getLinkedList());
        }
        matches.setChild(null);
        
        // removing from master list
        this.memoryMasterList.remove(matches);
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#iterator(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public Iterator iterator(final WorkingMemory workingMemory,
                             final ReteTuple tuple) {
        this.selectPossibleMatches(workingMemory, tuple);
        Iterator iterator = new Iterator() {
            Iterator it = memoryMasterList.iterator();
            ObjectMatches current = null;
            ObjectMatches next = null;

            public boolean hasNext() {
                boolean hasnext = false;
                if(next == null) {
                    while(it.hasNext()) {
                        next = (ObjectMatches) it.next();
                        if( next.getChild().getLinkedList() != noMatchList) {
                            if((childMemory == null) || 
                               (childMemory.isPossibleMatch((MultiLinkedListNodeWrapper)next.getChild()))) {
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
                    throw new NoSuchElementException("No more elements to return");
                }
                return this.current;
            }

            public void remove() {
                throw new UnsupportedOperationException("Iterator.remove() should not be used to remove right side objects from right memory.");
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
        return this.memoryMap.isEmpty();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public void selectPossibleMatches(WorkingMemory workingMemory,
                                      ReteTuple tuple) {
        Object select = declaration.getValue( workingMemory.getObject( 
                             tuple.get( this.declaration ) ) );
        Integer hash = new Integer(select.hashCode());
        this.noMatchList = (MultiLinkedList) this.memoryMap.get(hash);
        
        if(this.childMemory != null) {
            this.childMemory.selectPossibleMatches(workingMemory, tuple);
        }
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#isPossibleMatch(org.drools.reteoo.FactHandleImpl)
     */
    public boolean isPossibleMatch(MultiLinkedListNodeWrapper matches) {
        boolean ret = false;
        if((matches != null) && 
           (matches.getChild() != null) &&
           (matches.getChild().getLinkedList() != null)) {
                ret = ( matches.getChild().getLinkedList() != noMatchList);
    
                if(ret && (this.childMemory != null)) {
                    ret = this.childMemory.isPossibleMatch((MultiLinkedListNodeWrapper)matches.getChild());
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
    private MultiLinkedList getFactList(WorkingMemory workingMemory,
                                     FactHandleImpl handle) {
        Object select = this.extractor.getValue( workingMemory.getObject( handle ));
        Integer hash = new Integer(select.hashCode());
        MultiLinkedList list = (MultiLinkedList) this.memoryMap.get(hash);
        if(list == null) {
            list = new KeyMultiLinkedList(hash);
            this.memoryMap.put(hash, list);
        }
        return list;
    }
    
    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#size()
     */
    public int size() {
        return this.memoryMasterList.size();
    }
    
    /**
     * @param matches
     */
    private void removeMemoryEntry(MultiLinkedList list) {
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
