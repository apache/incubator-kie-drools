/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;

public class BavetNodeBuildPolicy<Solution_> {

    private final BavetConstraintSession session;

    private int nextNodeIndex = 0;
    private Map<String, BavetScoringNode> constraintIdToScoringNodeMap;
    private Map<BavetJoinConstraintStream<Solution_>, BavetJoinBridgeNode> joinConstraintStreamToJoinBridgeNodeMap =
            new HashMap<>();
    private Map<BavetAbstractNode, BavetAbstractNode> sharableNodeMap = new HashMap<>();

    public BavetNodeBuildPolicy(BavetConstraintSession session, int constraintCount) {
        this.session = session;
        constraintIdToScoringNodeMap = new LinkedHashMap<>(constraintCount);
    }

    public <Node_ extends BavetAbstractNode> Node_ retrieveSharedNode(Node_ node) {
        Node_ sharedNode = (Node_) sharableNodeMap.computeIfAbsent(node, k -> node);
        if (sharedNode != node) {
            // We are throwing away the new instance; throw away the new index, too.
            nextNodeIndex = node.getNodeIndex();
        }
        return sharedNode;
    }

    public void addScoringNode(BavetScoringNode scoringNode) {
        constraintIdToScoringNodeMap.put(scoringNode.getConstraintId(), scoringNode);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public BavetConstraintSession getSession() {
        return session;
    }

    public int nextNodeIndex() {
        return nextNodeIndex++;
    }

    public Map<String, BavetScoringNode> getConstraintIdToScoringNodeMap() {
        return constraintIdToScoringNodeMap;
    }

    public Map<BavetJoinConstraintStream<Solution_>, BavetJoinBridgeNode> getJoinConstraintStreamToJoinBridgeNodeMap() {
        return joinConstraintStreamToJoinBridgeNodeMap;
    }

    public List<BavetNode> getCreatedNodes() {
        // Make a sequential list of unique nodes.
        SortedMap<Integer, BavetNode> nodeIndexToNodeMap = sharableNodeMap.keySet().stream()
                .collect(Collectors.toMap(k -> k.getNodeIndex(), Function.identity(), (a, b) -> {
                    throw new IllegalStateException("Impossible state: 2 nodes (" + a + ", " + b +
                            ") share the same index (" + a.getNodeIndex() + ").");
                }, TreeMap::new));
        // Ensure there are no gaps in that list.
        int maxNodeIndex = nodeIndexToNodeMap.lastKey();
        int expectedMaxNodeIndex = nodeIndexToNodeMap.size() - 1;
        if (maxNodeIndex != expectedMaxNodeIndex) {
            throw new IllegalStateException("Impossible state: maximum node index (" + maxNodeIndex +
                    ") does not match the expected maximum node index (" + expectedMaxNodeIndex + ").");
        }
        return new ArrayList<>(nodeIndexToNodeMap.values());
    }

}
