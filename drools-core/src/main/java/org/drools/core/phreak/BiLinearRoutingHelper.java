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
package org.drools.core.phreak;

import org.drools.core.common.Memory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.builder.BiLinearDetector;

import java.util.List;

public final class BiLinearRoutingHelper {

    private BiLinearRoutingHelper() {
    }

    /**
     * Check if tuples should be routed to BiLinearJoinNode's right memory instead of left memory.
     * This happens when the source segment's tip node is a BiLinearJoinNode's second left input.
     */
    public static boolean shouldRouteToBiLinearRightMemory(SegmentMemory sourceSegment, SegmentMemory targetSegment) {
        if (!BiLinearDetector.isBiLinearEnabled()) {
            return false;
        }

        SegmentMemory.SegmentPrototype proto = targetSegment.getSegmentPrototype();

        return proto.hasBiLinearNode() &&
               sourceSegment.getTipNode().getId() == proto.getBiLinearSecondInputId();
    }

    /**
     * Routes a single peer tuple insert to BiLinearJoinNode's right memory.
     * Used when processing peer inserts for segments that need BiLinear right memory routing.
     * Also links the BiLinearJoinNode in its segment when the first insert arrives.
     */
    public static void routePeerToBiLinearRightMemory(SegmentMemory targetSegment, TupleImpl peerTuple) {
        BetaMemory bm = getBiLinearBetaMemory(targetSegment);
        TupleSets rightTuples = bm.getStagedRightTuples();

        // Link the BiLinearJoinNode when the first tuple arrives at its right memory.
        // This is needed because BiLinear has two left inputs - when the second input
        // receives tuples from peers, we need to link the node in the consumer segment.
        if (rightTuples.isEmpty()) {
            bm.setNodeDirtyWithoutNotify();
            targetSegment.linkNode(bm.getNodePosMaskBit());
        }

        rightTuples.addInsert(peerTuple);
        notifySegmentAndPaths(targetSegment);
    }

    /**
     * Routes a single peer tuple update to BiLinearJoinNode's right memory.
     * Used when processing peer updates for segments that need BiLinear right memory routing.
     */
    public static void routePeerUpdateToBiLinearRightMemory(SegmentMemory targetSegment, TupleImpl peerTuple) {
        TupleSets rightTuples = getBiLinearRightTuples(targetSegment);
        // Only stage if not already staged; if insert, leave as insert
        if (peerTuple.getStagedType() == TupleImpl.NONE) {
            rightTuples.addUpdate(peerTuple);
            notifySegmentAndPaths(targetSegment);
        }
    }

    /**
     * Routes a single peer tuple delete to BiLinearJoinNode's right memory.
     * Used when processing peer deletes for segments that need BiLinear right memory routing.
     */
    public static void routePeerDeleteToBiLinearRightMemory(SegmentMemory targetSegment, TupleImpl peerTuple) {
        TupleSets rightTuples = getBiLinearRightTuples(targetSegment);
        rightTuples.addDelete(peerTuple);
        notifySegmentAndPaths(targetSegment);
    }

    private static BetaMemory getBiLinearBetaMemory(SegmentMemory targetSegment) {
        SegmentMemory.SegmentPrototype proto = targetSegment.getSegmentPrototype();
        int biLinearIndex = proto.getBiLinearNodeIndex();
        Memory[] nodeMemories = targetSegment.getNodeMemories();
        return (BetaMemory) nodeMemories[biLinearIndex];
    }

    private static TupleSets getBiLinearRightTuples(SegmentMemory targetSegment) {
        BetaMemory bm = getBiLinearBetaMemory(targetSegment);

        if (bm.getStagedRightTuples().isEmpty()) {
            bm.setNodeDirtyWithoutNotify();
        }

        return bm.getStagedRightTuples();
    }

    private static void notifySegmentAndPaths(SegmentMemory targetSegment) {
        targetSegment.notifyRuleLinkSegment();

        List<PathMemory> pathMems = targetSegment.getPathMemories();
        for (PathMemory pmem : pathMems) {
            pmem.getOrCreateRuleAgendaItem();
            pmem.queueRuleAgendaItem();
        }
    }
}
