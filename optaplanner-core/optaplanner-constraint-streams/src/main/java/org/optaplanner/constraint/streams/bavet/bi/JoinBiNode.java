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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractNode;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.tri.JoinTriNode;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

public final class JoinBiNode<A, B> extends AbstractNode {

    private final Function<A, Object[]> mappingA;
    private final Function<B, Object[]> mappingB;
    /**
     * Calls for example {@link BiScorer#insert(BiTuple)}, {@link JoinTriNode#insertAB(BiTuple)} and/or ...
     */
    public final Consumer<BiTuple<A, B>> nextNodesInsert;
    /**
     * Calls for example {@link BiScorer#retract(BiTuple)}, {@link JoinTriNode#insertAB(BiTuple)} and/or ...
     */
    public final Consumer<BiTuple<A, B>> nextNodesRetract;

    private final Indexer<UniTuple<A>, Set<BiTuple<A, B>>> indexerA;
    private final Map<UniTuple<A>, Object[]> indexPropertiesMapA = new HashMap<>();
    private final Indexer<UniTuple<B>, Set<BiTuple<A, B>>> indexerB;
    private final Map<UniTuple<B>, Object[]> indexPropertiesMapB = new HashMap<>();
    private final Queue<BiTuple<A, B>> dirtyTupleQueue;

    public JoinBiNode(Function<A, Object[]> mappingA, Function<B, Object[]> mappingB,
            Consumer<BiTuple<A, B>> nextNodesInsert, Consumer<BiTuple<A, B>> nextNodesRetract,
            Indexer<UniTuple<A>, Set<BiTuple<A, B>>> indexerA, Indexer<UniTuple<B>, Set<BiTuple<A, B>>> indexerB) {
        this.mappingA = mappingA;
        this.mappingB = mappingB;
        this.nextNodesInsert = nextNodesInsert;
        this.nextNodesRetract = nextNodesRetract;
        this.indexerA = indexerA;
        this.indexerB = indexerB;
        dirtyTupleQueue = new ArrayDeque<>(1000);
    }

    public void insertA(UniTuple<A> tupleA) {
        Object[] indexProperties = mappingA.apply(tupleA.factA);
        Object[] old = indexPropertiesMapA.put(tupleA, indexProperties);
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple for the fact ("
                    + tupleA.factA + ") was already added in the indexPropertiesMapA.");
        }

        Map<UniTuple<B>, Set<BiTuple<A, B>>> tupleABSetMapB = indexerB.get(indexProperties);
        // Use standard initial capacity (16) to grow into, unless we already know more is probably needed
        Set<BiTuple<A, B>> tupleABSetA = new LinkedHashSet<>(Math.max(16, tupleABSetMapB.size()));
        indexerA.put(indexProperties, tupleA, tupleABSetA);

        tupleABSetMapB.forEach((tupleB, tupleABSetB) -> {
            BiTuple<A, B> tupleAB = new BiTuple<>(tupleA.factA, tupleB.factA);
            tupleAB.state = BavetTupleState.CREATING;
            tupleABSetA.add(tupleAB);
            tupleABSetB.add(tupleAB);
            dirtyTupleQueue.add(tupleAB);
        });
    }

    public void retractA(UniTuple<A> tupleA) {
        Object[] indexProperties = indexPropertiesMapA.remove(tupleA);
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }

        Set<BiTuple<A, B>> tupleABSetA = indexerA.remove(indexProperties, tupleA);
        // Remove tupleABs from the other side
        Map<UniTuple<B>, Set<BiTuple<A, B>>> tupleABSetMapB = indexerB.get(indexProperties);
        tupleABSetMapB.forEach((tupleB, tupleABSetB) -> {
            // TODO Performance: if tupleAB would contain tupleB, do this faster code instead:
            // for (tupleAB : tupleABSetA { tupleABSetMapB.get(tupleAB.tupleB).remove(tupleAB); }
            boolean changed = tupleABSetB.removeAll(tupleABSetA);
            if (!changed) {
                throw new IllegalStateException("Impossible state: the fact (" + tupleA.factA
                        + ") with indexProperties (" + Arrays.toString(indexProperties)
                        + ") has tuples on the A side that didn't exist on the B side.");
            }
        });
        for (BiTuple<A, B> tupleAB : tupleABSetA) {
            killTuple(tupleAB);
        }
    }

    public void insertB(UniTuple<B> tupleB) {
        Object[] indexProperties = mappingB.apply(tupleB.factA);
        Object[] old = indexPropertiesMapB.put(tupleB, indexProperties);
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple for the fact ("
                    + tupleB.factA + ") was already added in the indexPropertiesMapB.");
        }

        Map<UniTuple<A>, Set<BiTuple<A, B>>> tupleABSetMapB = indexerA.get(indexProperties);
        // Use standard initial capacity (16) to grow into, unless we already know more is probably needed
        Set<BiTuple<A, B>> tupleABSetB = new LinkedHashSet<>(Math.max(16, tupleABSetMapB.size()));
        indexerB.put(indexProperties, tupleB, tupleABSetB);

        tupleABSetMapB.forEach((tupleA, tupleABSetA) -> {
            BiTuple<A, B> tupleAB = new BiTuple<>(tupleA.factA, tupleB.factA);
            tupleAB.state = BavetTupleState.CREATING;
            tupleABSetB.add(tupleAB);
            tupleABSetA.add(tupleAB);
            dirtyTupleQueue.add(tupleAB);
        });
    }

    public void retractB(UniTuple<B> tupleB) {
        Object[] indexProperties = indexPropertiesMapB.remove(tupleB);
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }

        Set<BiTuple<A, B>> tupleABSetB = indexerB.remove(indexProperties, tupleB);
        // Remove tupleABs from the other side
        Map<UniTuple<A>, Set<BiTuple<A, B>>> tupleABSetMapA = indexerA.get(indexProperties);
        tupleABSetMapA.forEach((tupleA, tupleABSetA) -> {
            // TODO Performance: if tupleAB would contain tupleA, do this faster code instead:
            // for (tupleAB : tupleABSetB { tupleABSetMapA.get(tupleAB.tupleA).remove(tupleAB); }
            boolean changed = tupleABSetA.removeAll(tupleABSetB);
            if (!changed) {
                throw new IllegalStateException("Impossible state: the fact (" + tupleA.factA
                        + ") with indexProperties (" + Arrays.toString(indexProperties)
                        + ") has tuples on the B side that didn't exist on the A side.");
            }
        });
        for (BiTuple<A, B> tupleAB : tupleABSetB) {
            killTuple(tupleAB);
        }
    }

    private void killTuple(BiTuple<A, B> tupleAB) {
        // Don't add the tuple to the dirtyTupleQueue twice
        if (tupleAB.state.isDirty()) {
            switch (tupleAB.state) {
                case CREATING:
                    // Kill it before it propagates
                    tupleAB.state = BavetTupleState.ABORTING;
                    break;
                case UPDATING:
                    // Kill the original propagation
                    tupleAB.state = BavetTupleState.DYING;
                    break;
                case DYING:
                    break;
                default:
                    throw new IllegalStateException("Impossible state: The tuple for the facts ("
                            + tupleAB.factA + ", " + tupleAB.factB
                            + ") has the dirty state (" + tupleAB.state + ").");
            }
        } else {
            tupleAB.state = BavetTupleState.DYING;
            dirtyTupleQueue.add(tupleAB);
        }
    }

    @Override
    public void calculateScore() {
        dirtyTupleQueue.forEach(tuple -> {
            // Retract
            if (tuple.state == BavetTupleState.UPDATING || tuple.state == BavetTupleState.DYING) {
                nextNodesRetract.accept(tuple);
            }
            // Insert
            if (tuple.state == BavetTupleState.CREATING || tuple.state == BavetTupleState.UPDATING) {
                nextNodesInsert.accept(tuple);
            }
            switch (tuple.state) {
                case CREATING:
                case UPDATING:
                    tuple.state = BavetTupleState.OK;
                    return;
                case DYING:
                case ABORTING:
                    tuple.state = BavetTupleState.DEAD;
                    return;
                case DEAD:
                    throw new IllegalStateException("Impossible state: The tuple (" + tuple + ") in node (" +
                            this + ") is already in the dead state (" + tuple.state + ").");
                default:
                    throw new IllegalStateException("Impossible state: Tuple (" + tuple + ") in node (" +
                            this + ") is in an unexpected state (" + tuple.state + ").");
            }
        });
        dirtyTupleQueue.clear();
    }

    @Override
    public String toString() {
        return "JoinBiNode";
    }

}
