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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.common.NodeMemories;
import org.drools.core.common.SegmentMemorySupport;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;


public class SegmentPrototypeRegistryImpl implements SegmentPrototypeRegistry {
    
    private final transient Map<Integer, SegmentPrototype> segmentProtos;

    public SegmentPrototypeRegistryImpl(boolean isEagerCreation) {
        segmentProtos =  isEagerCreation ? new HashMap<>() : new ConcurrentHashMap<>();
    }
    
    public boolean hasSegmentPrototypes() {
        return !segmentProtos.isEmpty();
    }

    public void registerSegmentPrototype(LeftTupleNode tupleSource, SegmentPrototype smem) {
        segmentProtos.put(tupleSource.getId(), smem);
    }

    public void invalidateSegmentPrototype(LeftTupleNode rootNode) {
        segmentProtos.remove(rootNode.getId());
    }

    @Override
    public SegmentPrototype getSegmentPrototype(LeftTupleNode node) {
        return segmentProtos.get(node.getId());
    }

    @Override
    public SegmentMemory createSegmentFromPrototype(NodeMemories nodeMemories, SegmentMemorySupport segmentMemorySupport, LeftTupleSource tupleSource) {
        SegmentPrototype proto = segmentProtos.get(tupleSource.getId());
        return createSegmentFromPrototype(proto, nodeMemories, segmentMemorySupport);
    }

    public SegmentMemory createSegmentFromPrototype(SegmentPrototype proto, NodeMemories nodeMemories, SegmentMemorySupport segmentMemorySupport) {
        return proto.newSegmentMemory(nodeMemories, segmentMemorySupport);
    }

    public SegmentPrototype getSegmentPrototype(SegmentMemory segment) {
        return segmentProtos.get(segment.getRootNode().getId());
    }

}
