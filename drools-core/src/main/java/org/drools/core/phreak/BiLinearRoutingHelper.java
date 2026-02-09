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
     * Routes tuples to BiLinearJoinNode's right memory (staged right tuples)
     * instead of the normal left memory staging.
     */
    public static void routeToBiLinearRightMemory(SegmentMemory targetSegment, TupleSets leftTuples) {
        SegmentMemory.SegmentPrototype proto = targetSegment.getSegmentPrototype();
        int biLinearIndex = proto.getBiLinearNodeIndex();

        Memory[] nodeMemories = targetSegment.getNodeMemories();
        BetaMemory bm = (BetaMemory) nodeMemories[biLinearIndex];

        if (bm.getStagedRightTuples().isEmpty()) {
            bm.setNodeDirtyWithoutNotify();
        }

        bm.getStagedRightTuples().addAll(leftTuples);

        targetSegment.notifyRuleLinkSegment();

        List<PathMemory> pathMems = targetSegment.getPathMemories();
        for (PathMemory pmem : pathMems) {
            pmem.getOrCreateRuleAgendaItem();
            pmem.queueRuleAgendaItem();
        }
    }

    /**
     * Routes a single peer tuple to BiLinearJoinNode's right memory.
     * Used when processing peer inserts for segments that need BiLinear right memory routing.
     */
    public static void routePeerToBiLinearRightMemory(SegmentMemory targetSegment, TupleImpl peerTuple) {
        SegmentMemory.SegmentPrototype proto = targetSegment.getSegmentPrototype();
        int biLinearIndex = proto.getBiLinearNodeIndex();

        Memory[] nodeMemories = targetSegment.getNodeMemories();
        BetaMemory bm = (BetaMemory) nodeMemories[biLinearIndex];

        if (bm.getStagedRightTuples().isEmpty()) {
            bm.setNodeDirtyWithoutNotify();
        }

        bm.getStagedRightTuples().addInsert(peerTuple);

        targetSegment.notifyRuleLinkSegment();

        List<PathMemory> pathMems = targetSegment.getPathMemories();
        for (PathMemory pmem : pathMems) {
            pmem.getOrCreateRuleAgendaItem();
            pmem.queueRuleAgendaItem();
        }
    }
}
