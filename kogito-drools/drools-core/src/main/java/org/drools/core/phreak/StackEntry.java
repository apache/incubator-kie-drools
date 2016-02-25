/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.phreak;

import org.drools.core.common.Memory;
import org.drools.core.common.NetworkNode;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.util.AbstractBaseLinkedListNode;

/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:47
* To change this template use File | Settings | File Templates.
*/
public class StackEntry extends AbstractBaseLinkedListNode<StackEntry> {
    private final LeftInputAdapterNode liaNode;
    private final long                 bit;
    private final NetworkNode          node;
    private final LeftTupleSinkNode    sink;
    private final PathMemory           pmem;
    private final Memory               nodeMem;
    private final SegmentMemory[]      smems;
    private final int                  smemIndex;
    private final TupleSets<LeftTuple> trgTuples;
    private final boolean              resumeFromNextNode;
    private final boolean              processRian;


    public StackEntry(LeftInputAdapterNode liaNode,
                      NetworkNode node,
                      long bit,
                      LeftTupleSinkNode sink,
                      PathMemory pmem,
                      Memory nodeMem,
                      SegmentMemory[] smems,
                      int smemIndex,
                      TupleSets<LeftTuple> trgTuples,
                      boolean resumeFromNextNode,
                      boolean processRian) {
        this.liaNode = liaNode;
        this.bit = bit;
        this.node = node;
        this.sink = sink;
        this.pmem = pmem;
        this.nodeMem = nodeMem;
        this.smems = smems;
        this.smemIndex = smemIndex;
        this.trgTuples = trgTuples;
        this.resumeFromNextNode = resumeFromNextNode;
        this.processRian = processRian;
    }



    public LeftInputAdapterNode getLiaNode() {
        return this.liaNode;
    }

    public long getBit() {
        return bit;
    }

    public NetworkNode getNode() {
        return node;
    }

    public PathMemory getRmem() {
        return pmem;
    }

    public Memory getNodeMem() {
        return nodeMem;
    }

    public SegmentMemory[] getSmems() {
        return smems;
    }

    public int getSmemIndex() {
        return smemIndex;
    }

    public TupleSets<LeftTuple> getTrgTuples() {
        return trgTuples;
    }

    public LeftTupleSinkNode getSink() {
        return sink;
    }

    public boolean isResumeFromNextNode() {
        return resumeFromNextNode;
    }


    public boolean isProcessRian() {
        return processRian;
    }
}
