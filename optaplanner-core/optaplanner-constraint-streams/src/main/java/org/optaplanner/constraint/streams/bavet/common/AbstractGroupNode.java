
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

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.bi.JoinBiNode;
import org.optaplanner.constraint.streams.bavet.uni.UniScorer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

public abstract class AbstractGroupNode<InTuple_ extends Tuple, OutTuple_ extends Tuple, GroupKey_, ResultContainer_>
        extends AbstractNode {

    private final int groupStoreIndex;
    private final Supplier<ResultContainer_> supplier;
    /**
     * Some code paths may decide to not supply a collector.
     * In that case, we skip the code path that would attempt to use it.
     */
    private final boolean runAccumulate;
    /**
     * Calls for example {@link UniScorer#insert(UniTuple)}, {@link JoinBiNode#insertA(UniTuple)} and/or ...
     */
    private final Consumer<OutTuple_> nextNodesInsert;
    /**
     * Calls for example {@link UniScorer#retract(UniTuple)}, {@link JoinBiNode#retractA(UniTuple)} and/or ...
     */
    private final Consumer<OutTuple_> nextNodesRetract;
    private final Map<GroupKey_, Group<OutTuple_, GroupKey_, ResultContainer_>> groupMap;
    private final Queue<Group<OutTuple_, GroupKey_, ResultContainer_>> dirtyGroupQueue;

    protected AbstractGroupNode(int groupStoreIndex, Supplier<ResultContainer_> supplier,
            Consumer<OutTuple_> nextNodesInsert, Consumer<OutTuple_> nextNodesRetract) {
        this.groupStoreIndex = groupStoreIndex;
        this.supplier = supplier;
        this.runAccumulate = supplier != null;
        this.nextNodesInsert = nextNodesInsert;
        this.nextNodesRetract = nextNodesRetract;
        this.groupMap = new HashMap<>(1000);
        this.dirtyGroupQueue = new ArrayDeque<>(1000);
    }

    public void insert(InTuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        if (tupleStore[groupStoreIndex] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tuple
                    + ") was already added in the tupleStore.");
        }
        GroupKey_ groupKey = createGroupKey(tuple);
        Group<OutTuple_, GroupKey_, ResultContainer_> group = groupMap.computeIfAbsent(groupKey,
                k -> new Group<>(k, runAccumulate ? supplier.get() : null));
        group.parentCount++;

        Runnable undoAccumulator = runAccumulate ? accumulate(group.resultContainer, tuple) : null;
        GroupPart<Group<OutTuple_, GroupKey_, ResultContainer_>> groupPart = new GroupPart<>(group, undoAccumulator);
        tupleStore[groupStoreIndex] = groupPart;
        if (!group.dirty) {
            group.dirty = true;
            dirtyGroupQueue.add(group);
        }
    }

    protected abstract GroupKey_ createGroupKey(InTuple_ tuple);

    protected abstract Runnable accumulate(ResultContainer_ resultContainer, InTuple_ tuple);

    protected abstract OutTuple_ createOutTuple(Group<OutTuple_, GroupKey_, ResultContainer_> group);

    public void retract(InTuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        GroupPart<Group<OutTuple_, GroupKey_, ResultContainer_>> groupPart =
                (GroupPart<Group<OutTuple_, GroupKey_, ResultContainer_>>) tupleStore[groupStoreIndex];
        if (groupPart == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleStore[groupStoreIndex] = null;
        Group<OutTuple_, GroupKey_, ResultContainer_> group = groupPart.group;
        group.parentCount--;
        if (runAccumulate) {
            groupPart.undoAccumulator.run();
        }
        if (group.parentCount == 0) {
            GroupKey_ groupKey = group.groupKey;
            Group<OutTuple_, GroupKey_, ResultContainer_> old = groupMap.remove(groupKey);
            if (old == null) {
                throw new IllegalStateException("Impossible state: the group for the groupKey ("
                        + groupKey + ") doesn't exist in the groupMap.");
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
            if (group.tuple != null) {
                OutTuple_ tuple = group.tuple;
                BavetTupleState tupleState = tuple.getState();
                if (tupleState != BavetTupleState.OK) {
                    throw new IllegalStateException("Impossible state: The tuple (" + tuple + ") in node (" +
                            this + ") is in the state (" + tupleState + ").");
                }
                tuple.setState(BavetTupleState.DYING);
                nextNodesRetract.accept(tuple);
                tuple.setState(BavetTupleState.DEAD);
            }
            if (!group.dying) {
                // Delay calculating right until it propagates
                OutTuple_ tuple = createOutTuple(group);
                group.tuple = tuple;
                nextNodesInsert.accept(tuple);
                tuple.setState(BavetTupleState.OK);
            }
        });
        dirtyGroupQueue.clear();
    }

}
