/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetScoringNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniNode;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniTuple;

public final class BavetConstraintSession<Solution_, Score_ extends Score<Score_>> {

    private final ScoreInliner<Score_> scoreInliner;
    private final Map<Class<?>, BavetFromUniNode<Object>> declaredClassToNodeMap;
    private final List<BavetNode> nodeIndexedNodeMap;
    private final List<BavetScoringNode> scoringNodeList;
    private final Map<Class<?>, List<BavetFromUniNode<Object>>> effectiveClassToNodeListMap;
    private final List<Queue<BavetAbstractTuple>> nodeIndexToDirtyTupleQueueMap;
    private final Map<Object, List<BavetFromUniTuple<Object>>> fromTupleListMap;

    public BavetConstraintSession(boolean constraintMatchEnabled, ScoreDefinition<Score_> scoreDefinition,
            Map<BavetConstraint<Solution_>, Score_> constraintToWeightMap) {
        scoreInliner = scoreDefinition.buildScoreInliner((Map) constraintToWeightMap, constraintMatchEnabled);
        declaredClassToNodeMap = new HashMap<>(50);
        BavetNodeBuildPolicy<Solution_> buildPolicy = new BavetNodeBuildPolicy<>(this);
        constraintToWeightMap.forEach((constraint, constraintWeight) -> constraint.createNodes(buildPolicy,
                declaredClassToNodeMap, constraintWeight));
        nodeIndexedNodeMap = buildPolicy.getCreatedNodes();
        scoringNodeList = nodeIndexedNodeMap.stream()
                .filter(node -> node instanceof BavetScoringNode)
                .map(node -> (BavetScoringNode) node)
                .collect(Collectors.toList());
        effectiveClassToNodeListMap = new HashMap<>(declaredClassToNodeMap.size());
        int nodeCount = nodeIndexedNodeMap.size();
        nodeIndexToDirtyTupleQueueMap = new ArrayList<>(nodeCount);
        for (int i = 0; i < nodeCount; i++) {
            nodeIndexToDirtyTupleQueueMap.add(new ArrayDeque<>(1000));
        }
        fromTupleListMap = new IdentityHashMap<>(1000);
    }

    private static void refreshTuple(BavetAbstractTuple tuple) {
        tuple.getNode().refresh(tuple);
        switch (tuple.getState()) {
            case CREATING:
            case UPDATING:
                tuple.setState(BavetTupleState.OK);
                return;
            case DYING:
            case ABORTING:
                tuple.setState(BavetTupleState.DEAD);
                return;
            case DEAD:
                throw new IllegalStateException("Impossible state: The tuple (" + tuple + ") in node (" +
                        tuple.getNode() + ") is already in the dead state (" + tuple.getState() + ").");
            default:
                throw new IllegalStateException("Impossible state: Tuple (" + tuple + ") in node (" +
                        tuple.getNode() + ") is in an unexpected state (" + tuple.getState() + ").");
        }
    }

    public List<BavetFromUniNode<Object>> findFromNodeList(Class<?> factClass) {
        return effectiveClassToNodeListMap.computeIfAbsent(factClass, key -> {
            List<BavetFromUniNode<Object>> nodeList = new ArrayList<>();
            declaredClassToNodeMap.forEach((declaredClass, declaredNode) -> {
                if (declaredClass.isAssignableFrom(factClass)) {
                    nodeList.add(declaredNode);
                }
            });
            return nodeList;
        });
    }

    public void insert(Object fact) {
        Class<?> factClass = fact.getClass();
        List<BavetFromUniNode<Object>> fromNodeList = findFromNodeList(factClass);
        List<BavetFromUniTuple<Object>> tupleList = new ArrayList<>(fromNodeList.size());
        List<BavetFromUniTuple<Object>> old = fromTupleListMap.put(fact, tupleList);
        if (old != null) {
            throw new IllegalStateException("The fact (" + fact + ") was already inserted, so it cannot insert again.");
        }
        for (BavetFromUniNode<Object> node : fromNodeList) {
            BavetFromUniTuple<Object> tuple = node.createTuple(fact);
            tupleList.add(tuple);
            transitionTuple(tuple, BavetTupleState.CREATING);
        }
    }

    public void update(Object fact) {
        List<BavetFromUniTuple<Object>> tupleList = fromTupleListMap.get(fact);
        if (tupleList == null) {
            throw new IllegalStateException("The fact (" + fact + ") was never inserted, so it cannot update.");
        }
        for (BavetFromUniTuple<Object> tuple : tupleList) {
            transitionTuple(tuple, BavetTupleState.UPDATING);
        }
    }

    public void retract(Object fact) {
        List<BavetFromUniTuple<Object>> tupleList = fromTupleListMap.remove(fact);
        if (tupleList == null) {
            throw new IllegalStateException("The fact (" + fact + ") was never inserted, so it cannot retract.");
        }
        for (BavetFromUniTuple<Object> tuple : tupleList) {
            transitionTuple(tuple, BavetTupleState.DYING);
        }
    }

    public void transitionTuple(BavetAbstractTuple tuple, BavetTupleState newState) {
        if (tuple.isDirty()) {
            if (tuple.getState() != newState) {
                if ((tuple.getState() == BavetTupleState.CREATING && newState == BavetTupleState.DYING)) {
                    tuple.setState(BavetTupleState.ABORTING);
                } else if ((tuple.getState() == BavetTupleState.UPDATING && newState == BavetTupleState.DYING)) {
                    tuple.setState(BavetTupleState.DYING);
                } else {
                    throw new IllegalStateException("The tuple (" + tuple
                            + ") already has a dirty state (" + tuple.getState()
                            + ") so it cannot transition to newState (" + newState + ").");
                }
            }
            // Don't add it to the queue twice
            return;
        }
        tuple.setState(newState);
        nodeIndexToDirtyTupleQueueMap.get(tuple.getNodeIndex()).add(tuple);
    }

    public Score_ calculateScore(int initScore) {
        for (Queue<BavetAbstractTuple> queue : nodeIndexToDirtyTupleQueueMap) {
            BavetAbstractTuple tuple = queue.poll();
            while (tuple != null) {
                refreshTuple(tuple);
                tuple = queue.poll();
            }
        }
        return scoreInliner.extractScore(initScore);
    }

    public Map<String, ConstraintMatchTotal<Score_>> getConstraintMatchTotalMap() {
        return scoreInliner.getConstraintMatchTotalMap();
    }

    public Map<Object, Indictment<Score_>> getIndictmentMap() {
        return scoreInliner.getIndictmentMap();
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public ScoreInliner<Score_> getScoreInliner() {
        return scoreInliner;
    }

    public List<BavetNode> getNodes() {
        return nodeIndexedNodeMap;
    }

    public List<BavetScoringNode> getScoringNodeList() {
        return scoringNodeList;
    }

}
