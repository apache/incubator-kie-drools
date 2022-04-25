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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.AbstractNode;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.tri.JoinTriNode;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

public final class GroupBiToBiNode<OldA, OldB, A, B, ResultContainer_> extends AbstractNode {

    // TODO Most of this code duplicates GroupUniToBiNode.

    private final BiFunction<OldA, OldB, A> groupKeyMapping;
    private final int groupStoreIndex;
    private final Supplier<ResultContainer_> supplier;
    private final TriFunction<ResultContainer_, OldA, OldB, Runnable> accumulator;
    private final Function<ResultContainer_, B> finisher;
    /**
     * Calls for example {@link BiScorer#insert(BiTuple)}, {@link JoinTriNode#insertAB(BiTuple)} and/or ...
     */
    private final Consumer<BiTuple<A, B>> nextNodesInsert;
    /**
     * Calls for example {@link BiScorer#retract(BiTuple)}, {@link JoinTriNode#retractAB(BiTuple)} and/or ...
     */
    private final Consumer<BiTuple<A, B>> nextNodesRetract;
    private final int outputStoreSize;

    private final Map<A, Group<A, B, ResultContainer_>> groupMap;
    private final Queue<Group<A, B, ResultContainer_>> dirtyGroupQueue;

    public GroupBiToBiNode(BiFunction<OldA, OldB, A> groupKeyMapping, int groupStoreIndex,
            BiConstraintCollector<OldA, OldB, ResultContainer_, B> collector,
            Consumer<BiTuple<A, B>> nextNodesInsert, Consumer<BiTuple<A, B>> nextNodesRetract,
            int outputStoreSize) {
        this.groupKeyMapping = groupKeyMapping;
        this.groupStoreIndex = groupStoreIndex;
        supplier = collector.supplier();
        accumulator = collector.accumulator();
        finisher = collector.finisher();
        this.nextNodesInsert = nextNodesInsert;
        this.nextNodesRetract = nextNodesRetract;
        this.outputStoreSize = outputStoreSize;
        groupMap = new HashMap<>(1000);
        dirtyGroupQueue = new ArrayDeque<>(1000);
    }

    private static final class Group<A, B, ResultContainer_> {
        A groupKey;
        ResultContainer_ resultContainer;
        int parentCount = 0;
        boolean dirty = false;
        boolean dying = false;
        BiTuple<A, B> tupleAB = null;

        public Group(A groupKey, ResultContainer_ resultContainer) {
            this.groupKey = groupKey;
            this.resultContainer = resultContainer;
        }
    }

    private static final class GroupPart<A, B, ResultContainer_> {
        Group<A, B, ResultContainer_> group;
        Runnable undoAccumulator;

        public GroupPart(Group<A, B, ResultContainer_> group, Runnable undoAccumulator) {
            this.group = group;
            this.undoAccumulator = undoAccumulator;
        }
    }

    public void insertAB(BiTuple<OldA, OldB> tupleOldAB) {
        if (tupleOldAB.store[groupStoreIndex] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tupleOldAB
                    + ") was already added in the tupleStore.");
        }
        A groupKey = groupKeyMapping.apply(tupleOldAB.factA, tupleOldAB.factB);
        Group<A, B, ResultContainer_> group = groupMap.computeIfAbsent(groupKey, k -> new Group<>(k, supplier.get()));
        group.parentCount++;

        Runnable undoAccumulator = accumulator.apply(group.resultContainer, tupleOldAB.factA, tupleOldAB.factB);
        GroupPart<A, B, ResultContainer_> groupPart = new GroupPart<>(group, undoAccumulator);
        tupleOldAB.store[groupStoreIndex] = groupPart;
        if (!group.dirty) {
            group.dirty = true;
            dirtyGroupQueue.add(group);
        }
    }

    public void retractAB(BiTuple<OldA, OldB> tupleOldAB) {
        GroupPart<A, B, ResultContainer_> groupPart = (GroupPart<A, B, ResultContainer_>) tupleOldAB.store[groupStoreIndex];
        if (groupPart == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleOldAB.store[groupStoreIndex] = null;
        Group<A, B, ResultContainer_> group = groupPart.group;
        group.parentCount--;
        groupPart.undoAccumulator.run();
        if (group.parentCount == 0) {
            Group<A, B, ResultContainer_> old = groupMap.remove(group.groupKey);
            if (old == null) {
                throw new IllegalStateException("Impossible state: the group for the groupKey ("
                        + group.groupKey + ") doesn't exist in the groupMap.");
            }
            group.dying = true;
        }
        if (!group.dirty) {
            group.dirty = true;
            dirtyGroupQueue.add(group);
        }
    }

    @Override
    public void calculateScore() {
        dirtyGroupQueue.forEach(group -> {
            group.dirty = false;
            if (group.tupleAB != null) {
                if (group.tupleAB.state != BavetTupleState.OK) {
                    throw new IllegalStateException("Impossible state: The tuple (" + group.tupleAB + ") in node (" +
                            this + ") is in the state (" + group.tupleAB.state + ").");
                }
                group.tupleAB.state = BavetTupleState.DYING;
                nextNodesRetract.accept(group.tupleAB);
                group.tupleAB.state = BavetTupleState.DEAD;
            }
            if (!group.dying) {
                // Delay calculating B until it propagates
                B factB = finisher.apply(group.resultContainer);
                group.tupleAB = new BiTuple<>(group.groupKey, factB, outputStoreSize);
                nextNodesInsert.accept(group.tupleAB);
                group.tupleAB.state = BavetTupleState.OK;
            }
        });
        dirtyGroupQueue.clear();
    }

    @Override
    public String toString() {
        return "GroupBiToBiNode";
    }

}
