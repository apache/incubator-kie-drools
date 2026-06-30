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
import org.kie.api.runtime.rule.FactHandle;

/**
 * BiLinearRuleTerminalNodeLeftTuple is a specialized terminal tuple for BiLinearJoinNode.
 * It extends RuleTerminalNodeLeftTuple to be compatible with PhreakRuleTerminalNode,
 * while also providing cross-network fact access like BiLinearTuple.
 *
 * This allows rules using BiLinearJoinNode to access variables from both input networks
 * in their consequences.
 */
public class BiLinearRuleTerminalNodeLeftTuple extends RuleTerminalNodeLeftTuple {

    private static final long serialVersionUID = 540l;

    private final TupleImpl firstNetworkTuple;
    private final TupleImpl secondNetworkTuple;

    private final int firstSize;
    private final int secondSize;
    private final int size;

    private transient BiLinearParentView[] parentViewCache;

    public BiLinearRuleTerminalNodeLeftTuple(TupleImpl firstNetworkTuple,
                                            TupleImpl secondNetworkTuple,
                                            Sink sink) {
        super();
        setSink(sink);

        this.firstNetworkTuple = firstNetworkTuple;
        this.secondNetworkTuple = secondNetworkTuple;

        this.firstSize = firstNetworkTuple != null ? firstNetworkTuple.size() : 0;
        this.secondSize = secondNetworkTuple != null ? secondNetworkTuple.size() : 0;
        this.size = this.firstSize + this.secondSize;

        setIndex(size - 1);

        // Set handle to the last fact in the chain (from second network)
        if (secondNetworkTuple != null) {
            setFactHandle(secondNetworkTuple.getFactHandle());
        }

        // Link to firstNetworkTuple (A+B) as left parent for delete propagation
        // When A+B is deleted, this child will be found and deleted via the handleNext chain
        if (firstNetworkTuple != null) {
            setLeftParent(firstNetworkTuple);
            // Link into left parent's child list
            if (firstNetworkTuple.getLastChild() != null) {
                setHandlePrevious(firstNetworkTuple.getLastChild());
                getHandlePrevious().setHandleNext(this);
            } else {
                firstNetworkTuple.setFirstChild(this);
            }
            firstNetworkTuple.setLastChild(this);
        }
    }

    /**
     * Override get to provide cross-network access.
     * Index mapping:
     * - 0 to firstNetworkSize-1: First network facts
     * - firstNetworkSize to firstNetworkSize+secondNetworkSize-1: Second network facts
     */
    @Override
    public FactHandle get(int index) {
        if (index < firstSize) {
            return firstNetworkTuple.get(index);
        }

        // Second network range
        if (index < size) {
            int secondNetworkIndex = index - firstSize;
            return secondNetworkTuple.get(secondNetworkIndex);
        }

        throw new IndexOutOfBoundsException("Tuple index " + index + " is out of bounds. " +
            "First network size: " + firstSize + ", Second network size: " + secondSize);
    }

    @Override
    public FactHandle get(Declaration declaration) {
        return get(declaration.getTupleIndex());
    }

    @Override
    public Object getObject(int index) {
        FactHandle handle = get(index);
        return handle != null ? handle.getObject() : null;
    }

    @Override
    public Object getObject(Declaration declaration) {
        return getObject(declaration.getTupleIndex());
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Returns the second network tuple (e.g., C+D in a BiLinear join of A+B with C+D).
     * This is used by BiLinearJoinNode.doRightDeletes to find children that reference
     * a deleted second network tuple.
     */
    public TupleImpl getSecondNetworkTuple() {
        return secondNetworkTuple;
    }

    @Override
    public TupleImpl getParent() {
        int currentIdx = getIndex();
        if (currentIdx <= 0) {
            return null;
        }
        return getOrCreateParentView(currentIdx - 1);
    }

    private BiLinearParentView getOrCreateParentView(int viewIndex) {
        if (viewIndex < 0) {
            return null;
        }

        // Lazy initialize the cache array
        if (parentViewCache == null) {
            parentViewCache = new BiLinearParentView[size];
        }

        BiLinearParentView cached = parentViewCache[viewIndex];
        if (cached == null) {
            cached = new BiLinearParentView(this, viewIndex);
            parentViewCache[viewIndex] = cached;
        }
        return cached;
    }

    /**
     * Inner class providing virtual parent view for index traversal.
     */
    private static class BiLinearParentView extends TupleImpl {
        private final BiLinearRuleTerminalNodeLeftTuple biLinearTuple;
        private final int viewIndex;

        BiLinearParentView(BiLinearRuleTerminalNodeLeftTuple biLinearTuple, int viewIndex) {
            this.biLinearTuple = biLinearTuple;
            this.viewIndex = viewIndex;
        }

        @Override
        public int getIndex() {
            return viewIndex;
        }

        @Override
        public org.drools.core.common.InternalFactHandle getFactHandle() {
            return (org.drools.core.common.InternalFactHandle) biLinearTuple.get(viewIndex);
        }

        @Override
        public TupleImpl getParent() {
            if (viewIndex <= 0) {
                return null;
            }
            return biLinearTuple.getOrCreateParentView(viewIndex - 1);
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
        public org.drools.core.common.InternalFactHandle getOriginalFactHandle() {
            org.drools.core.common.InternalFactHandle fh = getFactHandle();
            if (fh != null && fh.isEvent()) {
                org.drools.core.common.InternalFactHandle linkedFH =
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
        }

        if (reverse) {
            // Reverse the array
            for (int i = 0; i < totalSize / 2; i++) {
                Object temp = objects[i];
                objects[i] = objects[totalSize - 1 - i];
                objects[totalSize - 1 - i] = temp;
            }
        }

        return objects;
    }

    @Override
    public FactHandle[] toFactHandles() {
        int totalSize = size();
        FactHandle[] handles = new FactHandle[totalSize];

        int pos = 0;

        // Add first network handles
        if (firstNetworkTuple != null) {
            FactHandle[] firstHandles = firstNetworkTuple.toFactHandles();
            System.arraycopy(firstHandles, 0, handles, pos, firstHandles.length);
            pos += firstHandles.length;
        }

        // Add second network handles
        if (secondNetworkTuple != null) {
            FactHandle[] secondHandles = secondNetworkTuple.toFactHandles();
            System.arraycopy(secondHandles, 0, handles, pos, secondHandles.length);
        }

        return handles;
    }

    @Override
    public String toString() {
        return "BiLinearRuleTerminalNodeLeftTuple{" +
                "firstNetwork=" + (firstNetworkTuple != null ? firstNetworkTuple.size() : 0) + " facts, " +
                "secondNetwork=" + (secondNetworkTuple != null ? secondNetworkTuple.size() : 0) + " facts" +
                '}';
    }
}
