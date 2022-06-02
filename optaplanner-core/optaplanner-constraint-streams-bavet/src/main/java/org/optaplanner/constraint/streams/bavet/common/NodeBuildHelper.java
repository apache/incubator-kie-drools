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
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
    private final Map<ConstraintStream, Consumer<? extends Tuple>> insertMap;
    private final Map<ConstraintStream, Consumer<? extends Tuple>> updateMap;
    private final Map<ConstraintStream, Consumer<? extends Tuple>> retractMap;
    private final Map<ConstraintStream, Integer> storeIndexMap;

    private List<AbstractNode> reversedNodeList;

    public NodeBuildHelper(Set<? extends ConstraintStream> activeStreamSet, Map<Constraint, Score_> constraintWeightMap,
            AbstractScoreInliner<Score_> scoreInliner) {
        this.activeStreamSet = activeStreamSet;
        this.constraintWeightMap = constraintWeightMap;
        this.scoreInliner = scoreInliner;
        int activeStreamSetSize = activeStreamSet.size();
        int consumerMapSize = Math.max(16, activeStreamSetSize);
        this.insertMap = new HashMap<>(consumerMapSize);
        this.updateMap = new HashMap<>(consumerMapSize);
        this.retractMap = new HashMap<>(consumerMapSize);
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

    public <Tuple_ extends Tuple> void putInsertUpdateRetract(ConstraintStream stream, Consumer<Tuple_> insert,
            Consumer<Tuple_> update, Consumer<Tuple_> retract) {
        insertMap.put(stream, insert);
        updateMap.put(stream, update);
        retractMap.put(stream, retract);
    }

    public <Tuple_ extends Tuple> void putInsertUpdateRetract(ConstraintStream stream,
            List<? extends AbstractConstraintStream> childStreamList,
            Function<Consumer<Tuple_>, AbstractInserter<Tuple_>> inserterConstructor,
            BiFunction<Consumer<Tuple_>, Consumer<Tuple_>, AbstractUpdater<Tuple_>> updaterConstructor) {
        Consumer<Tuple_> insert = getAggregatedInsert(childStreamList);
        Consumer<Tuple_> update = getAggregatedUpdate(childStreamList);
        Consumer<Tuple_> retract = getAggregatedRetract(childStreamList);
        putInsertUpdateRetract(stream,
                inserterConstructor.apply(insert),
                updaterConstructor.apply(update, retract),
                retract);
    }

    public <Tuple_ extends Tuple> Consumer<Tuple_> getAggregatedInsert(List<? extends ConstraintStream> streamList) {
        return getAggregatedConsumer(streamList, insertMap);
    }

    public <Tuple_ extends Tuple> Consumer<Tuple_> getAggregatedUpdate(List<? extends ConstraintStream> streamList) {
        return getAggregatedConsumer(streamList, updateMap);
    }

    public <Tuple_ extends Tuple> Consumer<Tuple_> getAggregatedRetract(List<? extends ConstraintStream> streamList) {
        return getAggregatedConsumer(streamList, retractMap);
    }

    private <Tuple_ extends Tuple> Consumer<Tuple_> getAggregatedConsumer(List<? extends ConstraintStream> streamList,
            Map<ConstraintStream, Consumer<? extends Tuple>> consumerMap) {
        Consumer<Tuple_>[] consumers = streamList.stream()
                .filter(this::isStreamActive)
                .map(s -> getConsumer(s, consumerMap))
                .toArray(Consumer[]::new);
        switch (consumers.length) {
            case 0:
                throw new IllegalStateException("Impossible state: None of the streamList (" + streamList
                        + ") are active.");
            case 1:
                return consumers[0];
            default:
                return new AggregatedConsumer<>(consumers);
        }
    }

    private static final class AggregatedConsumer<Tuple_ extends Tuple> implements Consumer<Tuple_> {
        private final Consumer<Tuple_>[] consumers;

        public AggregatedConsumer(Consumer<Tuple_>[] consumers) {
            this.consumers = consumers;
        }

        @Override
        public void accept(Tuple_ tuple) {
            for (Consumer<Tuple_> consumer : consumers) {
                consumer.accept(tuple);
            }
        }

    }

    private static <Tuple_ extends Tuple> Consumer<Tuple_> getConsumer(ConstraintStream stream,
            Map<ConstraintStream, Consumer<? extends Tuple>> consumerMap) {
        Consumer<Tuple_> consumer = (Consumer<Tuple_>) consumerMap.get(stream);
        if (consumer == null) {
            throw new IllegalStateException("Impossible state: the stream (" + stream + ") hasn't built a node yet.");
        }
        return consumer;
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
