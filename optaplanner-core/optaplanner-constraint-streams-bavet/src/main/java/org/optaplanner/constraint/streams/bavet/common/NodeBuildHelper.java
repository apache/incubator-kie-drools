/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.optaplanner.constraint.streams.common.AbstractConstraintStream;
import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class NodeBuildHelper<Score_ extends Score<Score_>> {

    private final Set<? extends ConstraintStream> activeStreamSet;
    private final Map<Constraint, Score_> constraintWeightMap;
    private final AbstractScoreInliner<Score_> scoreInliner;
    private final Map<ConstraintStream, TupleLifecycle<? extends Tuple>> tupleLifecycleMap;
    private final Map<ConstraintStream, Integer> storeIndexMap;

    private List<AbstractNode> reversedNodeList;

    public NodeBuildHelper(Set<? extends ConstraintStream> activeStreamSet, Map<Constraint, Score_> constraintWeightMap,
            AbstractScoreInliner<Score_> scoreInliner) {
        this.activeStreamSet = activeStreamSet;
        this.constraintWeightMap = constraintWeightMap;
        this.scoreInliner = scoreInliner;
        int activeStreamSetSize = activeStreamSet.size();
        this.tupleLifecycleMap = new HashMap<>(Math.max(16, activeStreamSetSize));
        this.storeIndexMap = new HashMap<>(Math.max(16, activeStreamSetSize / 2));
        this.reversedNodeList = new ArrayList<>(activeStreamSetSize);
    }

    public boolean isStreamActive(ConstraintStream stream) {
        return activeStreamSet.contains(stream);
    }

    public AbstractScoreInliner<Score_> getScoreInliner() {
        return scoreInliner;
    }

    public Score_ getConstraintWeight(Constraint constraint) {
        return constraintWeightMap.get(constraint);
    }

    public void addNode(AbstractNode node) {
        reversedNodeList.add(node);
    }

    public void addNode(AbstractNode node, ConstraintStream parent) {
        addNode(node);
        putInsertUpdateRetract(parent, (TupleLifecycle<? extends Tuple>) node);
    }

    public void addNode(AbstractNode node, ConstraintStream leftParent, ConstraintStream rightParent) {
        addNode(node);
        putInsertUpdateRetract(leftParent, TupleLifecycle.ofLeft((LeftTupleLifecycle<? extends Tuple>) node));
        putInsertUpdateRetract(rightParent, TupleLifecycle.ofRight((RightTupleLifecycle<? extends Tuple>) node));
    }

    public <Tuple_ extends Tuple> void putInsertUpdateRetract(ConstraintStream stream, TupleLifecycle<Tuple_> tupleLifecycle) {
        tupleLifecycleMap.put(stream, tupleLifecycle);
    }

    public <Tuple_ extends Tuple> void putInsertUpdateRetract(ConstraintStream stream,
            List<? extends AbstractConstraintStream> childStreamList,
            Function<TupleLifecycle<Tuple_>, AbstractConditionalTupleLifecycle<Tuple_>> tupleLifecycleFunction) {
        TupleLifecycle<Tuple_> tupleLifecycle = getAggregatedTupleLifecycle(childStreamList);
        putInsertUpdateRetract(stream, tupleLifecycleFunction.apply(tupleLifecycle));
    }

    public <Tuple_ extends Tuple> TupleLifecycle<Tuple_> getAggregatedTupleLifecycle(
            List<? extends ConstraintStream> streamList) {
        TupleLifecycle<Tuple_>[] tupleLifecycles = streamList.stream()
                .filter(this::isStreamActive)
                .map(s -> getTupleLifecycle(s, tupleLifecycleMap))
                .toArray(TupleLifecycle[]::new);
        switch (tupleLifecycles.length) {
            case 0:
                throw new IllegalStateException("Impossible state: None of the streamList (" + streamList
                        + ") are active.");
            case 1:
                return tupleLifecycles[0];
            default:
                return new AggregatedTupleLifecycle<>(tupleLifecycles);
        }
    }

    private static <Tuple_ extends Tuple> TupleLifecycle<Tuple_> getTupleLifecycle(ConstraintStream stream,
            Map<ConstraintStream, TupleLifecycle<? extends Tuple>> tupleLifecycleMap) {
        TupleLifecycle<Tuple_> tupleLifecycle = (TupleLifecycle<Tuple_>) tupleLifecycleMap.get(stream);
        if (tupleLifecycle == null) {
            throw new IllegalStateException("Impossible state: the stream (" + stream + ") hasn't built a node yet.");
        }
        return tupleLifecycle;
    }

    public int reserveTupleStoreIndex(ConstraintStream tupleSourceStream) {
        return storeIndexMap.compute(tupleSourceStream, (k, index) -> {
            if (index == null) {
                return 0;
            } else if (index < 0) {
                throw new IllegalStateException("Impossible state: the tupleSourceStream (" + k
                        + ") is reserving a store after it has been extracted.");
            } else {
                return index + 1;
            }
        });
    }

    public int extractTupleStoreSize(ConstraintStream tupleSourceStream) {
        Integer lastIndex = storeIndexMap.put(tupleSourceStream, Integer.MIN_VALUE);
        return (lastIndex == null) ? 0 : lastIndex + 1;
    }

    public List<AbstractNode> destroyAndGetNodeList() {
        List<AbstractNode> nodeList = this.reversedNodeList;
        Collections.reverse(nodeList);
        this.reversedNodeList = null;
        return nodeList;
    }

}
