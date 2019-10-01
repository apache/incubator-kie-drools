/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetScoringNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniNode;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniTuple;

public final class BavetConstraintSession<Solution_> implements ConstraintSession<Solution_> {

    private final boolean constraintMatchEnabled;
    private final Score<?> zeroScore;
    private final ScoreInliner<?> scoreInliner;

    private final Map<Class<?>, BavetFromUniNode<Object>> declaredClassToNodeMap;
    private final int nodeOrderSize;
    private final Map<String, BavetScoringNode> constraintIdToScoringNodeMap;

    private final Map<Class<?>, List<BavetFromUniNode<Object>>> effectiveClassToNodeListMap;

    private final List<Queue<BavetAbstractTuple>> nodeOrderedQueueList;
    private final Map<Object, List<BavetFromUniTuple<Object>>> fromTupleListMap;

    public BavetConstraintSession(boolean constraintMatchEnabled, ScoreDefinition scoreDefinition,
            Map<BavetConstraint<Solution_>, Score<?>> constraintToWeightMap) {
        this.constraintMatchEnabled = constraintMatchEnabled;
        this.zeroScore = scoreDefinition.getZeroScore();
        this.scoreInliner = scoreDefinition.buildScoreInliner(constraintMatchEnabled);
        declaredClassToNodeMap = new HashMap<>(50);
        BavetNodeBuildPolicy<Solution_> buildPolicy = new BavetNodeBuildPolicy<>(this, constraintToWeightMap.size());
        constraintToWeightMap.forEach((constraint, constraintWeight) -> {
            constraint.createNodes(buildPolicy, declaredClassToNodeMap, constraintWeight);
        });
        this.nodeOrderSize = buildPolicy.getNodeOrderMaximum() + 1;
        constraintIdToScoringNodeMap = buildPolicy.getConstraintIdToScoringNodeMap();
        effectiveClassToNodeListMap = new HashMap<>(declaredClassToNodeMap.size());
        nodeOrderedQueueList = new ArrayList<>(nodeOrderSize);
        for (int i = 0; i < nodeOrderSize; i++) {
            nodeOrderedQueueList.add(new ArrayDeque<>(1000));
        }
        fromTupleListMap = new IdentityHashMap<>(1000);
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

    @Override
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

    @Override
    public void update(Object fact) {
        List<BavetFromUniTuple<Object>> tupleList = fromTupleListMap.get(fact);
        if (tupleList == null) {
            throw new IllegalStateException("The fact (" + fact + ") was never inserted, so it cannot update.");
        }
        for (BavetFromUniTuple<Object> tuple : tupleList) {
            transitionTuple(tuple, BavetTupleState.UPDATING);
        }
    }

    @Override
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
        nodeOrderedQueueList.get(tuple.getNodeOrder()).add(tuple);
    }

    @Override
    public Score<?> calculateScore(int initScore) {
        for (int i = 0; i < nodeOrderSize; i++) {
            Queue<BavetAbstractTuple> queue = nodeOrderedQueueList.get(i);
            BavetAbstractTuple tuple = queue.poll();
            while (tuple != null) {
                tuple.refresh();
                tuple = queue.poll();
            }
        }
        return scoreInliner.extractScore(initScore);
    }

    @Override
    public Map<String, ConstraintMatchTotal> getConstraintMatchTotalMap() {
        Map<String, ConstraintMatchTotal> constraintMatchTotalMap = new LinkedHashMap<>(
                constraintIdToScoringNodeMap.size());
        constraintIdToScoringNodeMap.forEach((constraintId, scoringNode) -> {
            ConstraintMatchTotal constraintMatchTotal = scoringNode.buildConstraintMatchTotal(zeroScore);
            constraintMatchTotalMap.put(constraintId, constraintMatchTotal);
        });
        return constraintMatchTotalMap;
    }

    @Override
    public Map<Object, Indictment> getIndictmentMap() {
        // TODO This is temporary, inefficient code, replace it!
        Map<Object, Indictment> indictmentMap = new LinkedHashMap<>(); // TODO use entitySize
        for (ConstraintMatchTotal constraintMatchTotal : getConstraintMatchTotalMap().values()) {
            for (ConstraintMatch constraintMatch : constraintMatchTotal.getConstraintMatchSet()) {
                constraintMatch.getJustificationList().stream()
                        .distinct() // One match might have the same justification twice
                        .forEach(justification -> {
                            Indictment indictment = indictmentMap.computeIfAbsent(justification,
                                    k -> new Indictment(justification, zeroScore));
                            indictment.addConstraintMatch(constraintMatch);
                        });
            }
        }
        return indictmentMap;
    }

    @Override
    public void close() {}

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public boolean isConstraintMatchEnabled() {
        return constraintMatchEnabled;
    }

    public ScoreInliner<?> getScoreInliner() {
        return scoreInliner;
    }

}
