/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo;

import org.drools.base.rule.Declaration;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.rule.FactHandle;

/**
 * BiLinearTuple represents a tuple that combines facts from two separate left input networks.
 * This specialized tuple enables cross-network variable resolution for BiLinearJoinNode,
 * allowing constraints to reference variables from both input networks.
 * 
 * The tuple maintains references to both source network tuples and provides unified
 * variable access across networks through enhanced Declaration resolution.
 */
public class BiLinearTuple extends TupleImpl {
    
    private static final long serialVersionUID = 540l;
    
    /** First network tuple (primary left input) */
    private final TupleImpl firstNetworkTuple;
    
    /** Second network tuple (secondary left input) */ 
    private final TupleImpl secondNetworkTuple;

    /**
     * Creates a BiLinearTuple combining tuples from two networks
     * 
     * @param firstNetworkTuple Tuple from the first left input network
     * @param secondNetworkTuple Tuple from the second left input network  
     * @param rightFactHandle Right input fact handle (may be null for some scenarios)
     * @param sink The sink node for this tuple
     */
    public BiLinearTuple(TupleImpl firstNetworkTuple,
                        TupleImpl secondNetworkTuple,
                        InternalFactHandle rightFactHandle,
                        Sink sink) {
        super(rightFactHandle, sink, false);

        this.firstNetworkTuple = firstNetworkTuple;
        this.secondNetworkTuple = secondNetworkTuple;

        // Set up parent chain for proper traversal by code generator
        // The combined index is firstSize + secondSize - 1 (0-based)
        int firstSize = firstNetworkTuple != null ? firstNetworkTuple.size() : 0;
        int secondSize = secondNetworkTuple != null ? secondNetworkTuple.size() : 0;

        // Create a virtual parent chain that allows proper index traversal
        // Start from the end of second network and chain through first network
        if (secondSize > 0) {
            // Set this tuple's handle to the last fact of second network
            this.handle = secondNetworkTuple.getFactHandle();
        }

        setIndex(firstSize + secondSize - 1);
    }

    /**
     * Override getParent to provide virtual parent chain for code generator traversal.
     * Returns a BiLinearParentView that continues the parent chain across both networks.
     */
    @Override
    public TupleImpl getParent() {
        int currentIdx = getIndex();
        if (currentIdx <= 0) {
            return null;
        }
        return new BiLinearParentView(this, currentIdx - 1);
    }

    /**
     * Inner class that provides a virtual parent view for a specific index.
     * This allows the code generator to traverse the parent chain correctly.
     */
    private static class BiLinearParentView extends TupleImpl {
        private final BiLinearTuple biLinearTuple;
        private final int viewIndex;

        BiLinearParentView(BiLinearTuple biLinearTuple, int viewIndex) {
            this.biLinearTuple = biLinearTuple;
            this.viewIndex = viewIndex;
        }

        @Override
        public int getIndex() {
            return viewIndex;
        }

        @Override
        public InternalFactHandle getFactHandle() {
            return (InternalFactHandle) biLinearTuple.get(viewIndex);
        }

        @Override
        public TupleImpl getParent() {
            if (viewIndex <= 0) {
                return null;
            }
            return new BiLinearParentView(biLinearTuple, viewIndex - 1);
        }

        @Override
        public FactHandle get(int index) {
            return biLinearTuple.get(index);
        }

        @Override
        public FactHandle get(Declaration declaration) {
            return biLinearTuple.get(declaration);
        }

        @Override
        public InternalFactHandle getOriginalFactHandle() {
            InternalFactHandle fh = getFactHandle();
            if (fh != null && fh.isEvent()) {
                InternalFactHandle linkedFH =
                    ((org.drools.core.common.DefaultEventHandle)fh).getLinkedFactHandle();
                return linkedFH != null ? linkedFH : fh;
            }
            return fh;
        }

        @Override
        public ObjectTypeNodeId getInputOtnId() {
            return biLinearTuple.getInputOtnId();
        }

        @Override
        public boolean isLeftTuple() {
            return true;
        }

        @Override
        public void reAdd() {
            // No-op for view
        }
    }

    @Override
    public FactHandle get(Declaration declaration) {
        return get(declaration.getTupleIndex());
    }
    
    /**
     * Enhanced get method that resolves indices across both networks
     * 
     * Index mapping:
     * - 0 to firstNetworkSize-1: First network facts
     * - firstNetworkSize to firstNetworkSize+secondNetworkSize-1: Second network facts  
     * - firstNetworkSize+secondNetworkSize: Right fact (if present)
     */
    @Override
    public FactHandle get(int index) {
        int firstSize = firstNetworkTuple != null ? firstNetworkTuple.size() : 0;
        int secondSize = secondNetworkTuple != null ? secondNetworkTuple.size() : 0;
        
        // First network range
        if (index < firstSize) {
            return firstNetworkTuple.get(index);
        }
        
        // Second network range  
        if (index < firstSize + secondSize) {
            int secondNetworkIndex = index - firstSize;
            return secondNetworkTuple.get(secondNetworkIndex);
        }
        
        // Right fact
        if (index == firstSize + secondSize && this.handle != null) {
            return this.handle;
        }
        
        throw new IndexOutOfBoundsException("Tuple index " + index + " is out of bounds. " +
            "First network size: " + firstSize + ", Second network size: " + secondSize + 
            ", Has right fact: " + (this.handle != null));
    }
    
    /**
     * Enhanced getObject method for cross-network object access
     */
    @Override
    public Object getObject(Declaration declaration) {
        return getObject(declaration.getTupleIndex());
    }

    @Override
    public Object getObject(int index) {
        FactHandle handle = get(index);
        return handle != null ? handle.getObject() : null;
    }

    @Override
    public int size() {
        int firstSize = firstNetworkTuple != null ? firstNetworkTuple.size() : 0;
        int secondSize = secondNetworkTuple != null ? secondNetworkTuple.size() : 0;
        int rightSize = this.handle != null ? 1 : 0;
        return firstSize + secondSize + rightSize;
    }

    @Override
    public int getIndex() {
        return size() - 1;
    }

    @Override
    public Object[] toObjects() {
        return toObjects(false);
    }

    @Override
    public Object[] toObjects(boolean reverse) {
        int totalSize = size();
        Object[] objects = new Object[totalSize];
        
        int pos = 0;
        
        // Add first network objects
        if (firstNetworkTuple != null) {
            Object[] firstObjects = firstNetworkTuple.toObjects(reverse);
            System.arraycopy(firstObjects, 0, objects, pos, firstObjects.length);
            pos += firstObjects.length;
        }
        
        // Add second network objects
        if (secondNetworkTuple != null) {
            Object[] secondObjects = secondNetworkTuple.toObjects(reverse);
            System.arraycopy(secondObjects, 0, objects, pos, secondObjects.length);
            pos += secondObjects.length;
        }
        
        // Add right object if present
        if (this.handle != null) {
            objects[pos] = this.handle.getObject();
        }
        
        return reverse ? reverseArray(objects) : objects;
    }

    @Override
    public FactHandle[] toFactHandles() {
        int totalSize = size();
        FactHandle[] handles = new FactHandle[totalSize];
        
        int pos = 0;
        
        if (firstNetworkTuple != null) {
            FactHandle[] firstHandles = firstNetworkTuple.toFactHandles();
            System.arraycopy(firstHandles, 0, handles, pos, firstHandles.length);
            pos += firstHandles.length;
        }
        
        if (secondNetworkTuple != null) {
            FactHandle[] secondHandles = secondNetworkTuple.toFactHandles();
            System.arraycopy(secondHandles, 0, handles, pos, secondHandles.length);
            pos += secondHandles.length;
        }
        
        if (this.handle != null) {
            handles[pos] = this.handle;
        }
        
        return handles;
    }

    @Override
    public TupleImpl getTuple(int index) {
        int firstSize = firstNetworkTuple != null ? firstNetworkTuple.size() : 0;
        
        if (index < firstSize && firstNetworkTuple != null) {
            return firstNetworkTuple.getTuple(index);
        } else if (index < firstSize + (secondNetworkTuple != null ? secondNetworkTuple.size() : 0) 
                   && secondNetworkTuple != null) {
            return secondNetworkTuple.getTuple(index - firstSize);
        }
        
        return this;
    }
    
    public TupleImpl getFirstNetworkTuple() {
        return firstNetworkTuple;
    }
    
    public TupleImpl getSecondNetworkTuple() {
        return secondNetworkTuple;
    }

    private Object[] reverseArray(Object[] array) {
        Object[] reversed = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }
    
    @Override
    public String toString() {
        return "BiLinearTuple{" +
                "firstNetwork=" + (firstNetworkTuple != null ? firstNetworkTuple.size() : 0) + " facts, " +
                "secondNetwork=" + (secondNetworkTuple != null ? secondNetworkTuple.size() : 0) + " facts, " +
                "rightFact=" + (handle != null ? "present" : "absent") +
                '}';
    }
    
    @Override
    public ObjectTypeNodeId getInputOtnId() {
        if (firstNetworkTuple != null) {
            return firstNetworkTuple.getInputOtnId();
        }
        return null;
    }
    
    @Override
    public boolean isLeftTuple() {
        return true; // BiLinearTuple is always a left tuple
    }
    
    @Override
    public void reAdd() {
        if (firstNetworkTuple != null) {
            firstNetworkTuple.reAdd();
        }
    }
}