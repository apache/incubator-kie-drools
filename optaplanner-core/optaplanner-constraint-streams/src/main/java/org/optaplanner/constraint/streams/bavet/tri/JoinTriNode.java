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

package org.optaplanner.constraint.streams.bavet.tri;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.AbstractNode;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

public final class JoinTriNode<A, B, C> extends AbstractNode {

    private final BiFunction<A, B, Object[]> mappingAB;
    private final Function<C, Object[]> mappingC;
    /**
     * Calls for example {@link TriScorer#insert(TriTuple)} and/or ...
     */
    public final Consumer<TriTuple<A, B, C>> nextNodesInsert;
    /**
     * Calls for example {@link TriScorer#retract(TriTuple)} and/or ...
     */
    public final Consumer<TriTuple<A, B, C>> nextNodesRetract;

    private final Indexer<BiTuple<A, B>, Set<TriTuple<A, B, C>>> indexerAB;
    private final Map<BiTuple<A, B>, Object[]> indexPropertiesMapAB = new HashMap<>();
    private final Indexer<UniTuple<C>, Set<TriTuple<A, B, C>>> indexerC;
    private final Map<UniTuple<C>, Object[]> indexPropertiesMapC = new HashMap<>();
    private final Queue<TriTuple<A, B, C>> dirtyTupleQueue;

    public JoinTriNode(BiFunction<A, B, Object[]> mappingAB, Function<C, Object[]> mappingC,
            Consumer<TriTuple<A, B, C>> nextNodesInsert, Consumer<TriTuple<A, B, C>> nextNodesRetract,
            Indexer<BiTuple<A, B>, Set<TriTuple<A, B, C>>> indexerAB, Indexer<UniTuple<C>, Set<TriTuple<A, B, C>>> indexerC) {
        this.mappingAB = mappingAB;
        this.mappingC = mappingC;
        this.nextNodesInsert = nextNodesInsert;
        this.nextNodesRetract = nextNodesRetract;
        this.indexerAB = indexerAB;
        this.indexerC = indexerC;
        dirtyTupleQueue = new ArrayDeque<>(1000);
    }

    public void insertAB(BiTuple<A, B> tupleAB) {
        Object[] indexProperties = mappingAB.apply(tupleAB.factA, tupleAB.factB);
        Object[] old = indexPropertiesMapAB.put(tupleAB, indexProperties);
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple for the fact ("
                    + tupleAB.factA + ", " + tupleAB.factB + ") was already added in the indexPropertiesMapAB.");
        }

        Map<UniTuple<C>, Set<TriTuple<A, B, C>>> tupleABCSetMapC = indexerC.get(indexProperties);
        // Use standard initial capacity (16) to grow into, unless we already know more is probably needed
        Set<TriTuple<A, B, C>> tupleABCSetAB = new LinkedHashSet<>(Math.max(16, tupleABCSetMapC.size()));
        indexerAB.put(indexProperties, tupleAB, tupleABCSetAB);

        tupleABCSetMapC.forEach((tupleC, tupleABCSetC) -> {
            TriTuple<A, B, C> tupleABC = new TriTuple<>(tupleAB.factA, tupleAB.factB, tupleC.factA);
            tupleABC.state = BavetTupleState.CREATING;
            tupleABCSetAB.add(tupleABC);
            tupleABCSetC.add(tupleABC);
            dirtyTupleQueue.add(tupleABC);
        });
    }

    public void retractAB(BiTuple<A, B> tupleAB) {
        Object[] indexProperties = indexPropertiesMapAB.remove(tupleAB);
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }

        Set<TriTuple<A, B, C>> tupleABCSetAB = indexerAB.remove(indexProperties, tupleAB);
        // Remove tupleABCs from the other side
        Map<UniTuple<C>, Set<TriTuple<A, B, C>>> tupleABCSetMapB = indexerC.get(indexProperties);
        tupleABCSetMapB.forEach((tupleC, tupleABCSetC) -> {
            // TODO Performance: if tupleABC would contain tupleC, do this faster code instead:
            // for (tupleABC : tupleABCSetAB { tupleABCSetMapB.get(tupleABC.tupleC).remove(tupleABC); }
            boolean changed = tupleABCSetC.removeAll(tupleABCSetAB);
            if (!changed) {
                throw new IllegalStateException("Impossible state: the fact (" + tupleAB.factA
                        + ") with indexProperties (" + Arrays.toString(indexProperties)
                        + ") has tuples on the AB side that didn't exist on the C side.");
            }
        });
        for (TriTuple<A, B, C> tupleABC : tupleABCSetAB) {
            killTuple(tupleABC);
        }
    }

    public void insertC(UniTuple<C> tupleC) {
        Object[] indexProperties = mappingC.apply(tupleC.factA);
        Object[] old = indexPropertiesMapC.put(tupleC, indexProperties);
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple for the fact ("
                    + tupleC.factA + ") was already added in the indexPropertiesMapC.");
        }

        Map<BiTuple<A, B>, Set<TriTuple<A, B, C>>> tupleABCSetMapAB = indexerAB.get(indexProperties);
        // Use standard initial capacity (16) to grow into, unless we already know more is probably needed
        Set<TriTuple<A, B, C>> tupleABCSetC = new LinkedHashSet<>(Math.max(16, tupleABCSetMapAB.size()));
        indexerC.put(indexProperties, tupleC, tupleABCSetC);

        tupleABCSetMapAB.forEach((tupleAB, tupleABCSetAB) -> {
            TriTuple<A, B, C> tupleABC = new TriTuple<>(tupleAB.factA, tupleAB.factB, tupleC.factA);
            tupleABC.state = BavetTupleState.CREATING;
            tupleABCSetC.add(tupleABC);
            tupleABCSetAB.add(tupleABC);
            dirtyTupleQueue.add(tupleABC);
        });
    }

    public void retractC(UniTuple<C> tupleC) {
        Object[] indexProperties = indexPropertiesMapC.remove(tupleC);
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }

        Set<TriTuple<A, B, C>> tupleABCSetC = indexerC.remove(indexProperties, tupleC);
        // Remove tupleABCs from the other side
        Map<BiTuple<A, B>, Set<TriTuple<A, B, C>>> tupleABCSetMapA = indexerAB.get(indexProperties);
        tupleABCSetMapA.forEach((tupleAB, tupleABCSetAB) -> {
            // TODO Performance: if tupleABC would contain tupleAB, do this faster code instead:
            // for (tupleABC : tupleABCSetC { tupleABCSetMapA.get(tupleABC.tupleAB).remove(tupleABC); }
            boolean changed = tupleABCSetAB.removeAll(tupleABCSetC);
            if (!changed) {
                throw new IllegalStateException("Impossible state: the fact (" + tupleAB.factA + ", " + tupleAB.factB
                        + ") with indexProperties (" + Arrays.toString(indexProperties)
                        + ") has tuples on the C side that didn't exist on the AB side.");
            }
        });
        for (TriTuple<A, B, C> tupleABC : tupleABCSetC) {
            killTuple(tupleABC);
        }
    }

    private void killTuple(TriTuple<A, B, C> tupleABC) {
        // Don't add the tuple to the dirtyTupleQueue twice
        if (tupleABC.state.isDirty()) {
            switch (tupleABC.state) {
                case CREATING:
                    // Kill it before it propagates
                    tupleABC.state = BavetTupleState.ABORTING;
                    break;
                case UPDATING:
                    // Kill the original propagation
                    tupleABC.state = BavetTupleState.DYING;
                    break;
                case DYING:
                    break;
                default:
                    throw new IllegalStateException("Impossible state: The tuple for the facts ("
                            + tupleABC.factA + ", " + tupleABC.factB + ", " + tupleABC.factC
                            + ") has the dirty state (" + tupleABC.state + ").");
            }
        } else {
            tupleABC.state = BavetTupleState.DYING;
            dirtyTupleQueue.add(tupleABC);
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
        return "JoinTriNode";
    }

}
