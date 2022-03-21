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
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public class NodeBuildHelper<Score_ extends Score<Score_>> {

    private final Set<? extends ConstraintStream> activeStreamSet;
    private final Map<Constraint, Score_> constraintWeightMap;
    private final AbstractScoreInliner<Score_> scoreInliner;

    private final Map<ConstraintStream, Consumer<? extends Tuple>> insertMap;
    private final Map<ConstraintStream, Consumer<? extends Tuple>> retractMap;

    private List<AbstractNode> reversedNodeList;

    public NodeBuildHelper(Set<? extends ConstraintStream> activeStreamSet,
            Map<Constraint, Score_> constraintWeightMap,
            AbstractScoreInliner<Score_> scoreInliner) {
        this.activeStreamSet = activeStreamSet;
        insertMap = new HashMap<>(Math.max(16, activeStreamSet.size() * 4));
        retractMap = new HashMap<>(Math.max(16, activeStreamSet.size() * 4));
        reversedNodeList = new ArrayList<>(activeStreamSet.size());
        this.constraintWeightMap = constraintWeightMap;
        this.scoreInliner = scoreInliner;
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

    public <Tuple_ extends Tuple> void putInsertRetract(ConstraintStream stream,
            Consumer<Tuple_> insert, Consumer<Tuple_> retract) {
        insertMap.put(stream, insert);
        retractMap.put(stream, retract);
    }

    public <Tuple_ extends Tuple> Consumer<Tuple_> getInsert(ConstraintStream stream) {
        Consumer<Tuple_> insert = (Consumer<Tuple_>) insertMap.get(stream);
        if (insert == null) {
            throw new IllegalStateException("Impossible state: the stream (" + stream + ") hasn't build a node yet.");
        }
        return insert;
    }

    public <Tuple_ extends Tuple> Consumer<Tuple_> getRetract(ConstraintStream stream) {
        Consumer<Tuple_> retract = (Consumer<Tuple_>) retractMap.get(stream);
        if (retract == null) {
            throw new IllegalStateException("Impossible state: the stream (" + stream + ") hasn't build a node yet.");
        }
        return retract;
    }

    public <Tuple_ extends Tuple> Consumer<Tuple_> getAggregatedInsert(List<? extends ConstraintStream> streamList) {
        Consumer<Tuple_> aggregatedInsert = null;
        for (ConstraintStream stream : streamList) {
            if (isStreamActive(stream)) {
                Consumer<Tuple_> insert = getInsert(stream);
                // TODO investigate if iterating is faster (or less StackOverflowError prone) than chaining .andThen()
                aggregatedInsert = (aggregatedInsert == null) ? insert : aggregatedInsert.andThen(insert);
            }
        }
        if (aggregatedInsert == null) {
            throw new IllegalStateException("Impossible state: None of the streamList (" + streamList
                    + ") are active.");
        }
        return aggregatedInsert;
    }

    public <Tuple_ extends Tuple> Consumer<Tuple_> getAggregatedRetract(List<? extends ConstraintStream> streamList) {
        Consumer<Tuple_> aggregatedRetract = null;
        for (ConstraintStream stream : streamList) {
            if (isStreamActive(stream)) {
                Consumer<Tuple_> retract = getRetract(stream);
                // TODO investigate if iterating is faster (or less StackOverflowError prone) than chaining .andThen()
                aggregatedRetract = (aggregatedRetract == null) ? retract : aggregatedRetract.andThen(retract);
            }
        }
        if (aggregatedRetract == null) {
            throw new IllegalStateException("Impossible state: None of the streamList (" + streamList
                    + ") are active.");
        }
        return aggregatedRetract;
    }

    public List<AbstractNode> destroyAndGetNodeList() {
        List<AbstractNode> nodeList = this.reversedNodeList;
        Collections.reverse(nodeList);
        this.reversedNodeList = null;
        return nodeList;
    }

}
