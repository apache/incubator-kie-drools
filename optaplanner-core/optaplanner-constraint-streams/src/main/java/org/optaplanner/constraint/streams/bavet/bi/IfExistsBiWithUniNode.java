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
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractNode;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.tri.JoinTriNode;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.TriPredicate;

public final class IfExistsBiWithUniNode<A, B, C> extends AbstractNode {

    private final boolean shouldExist;
    private final BiFunction<A, B, IndexProperties> mappingAB;
    private final Function<C, IndexProperties> mappingC;
    private final int inputStoreIndexAB;
    private final int inputStoreIndexC;
    /**
     * Calls for example {@link BiScorer#insert(BiTuple)}, {@link JoinTriNode#insertAB(BiTuple)} and/or ...
     */
    private final Consumer<BiTuple<A, B>> nextNodesInsert;
    /**
     * Calls for example {@link BiScorer#retract(BiTuple)}, {@link JoinTriNode#retractAB(BiTuple)} and/or ...
     */
    private final Consumer<BiTuple<A, B>> nextNodesRetract;
    // No outputStoreSize because this node is not a tuple source, even though it has a dirtyCounterQueue.

    private final Indexer<BiTuple<A, B>, Counter<A, B>> indexerAB;
    private final Indexer<UniTuple<C>, Set<Counter<A, B>>> indexerC;
    private final TriPredicate<A, B, C> filtering;
    private final Queue<Counter<A, B>> dirtyCounterQueue;

    public IfExistsBiWithUniNode(boolean shouldExist,
            BiFunction<A, B, IndexProperties> mappingAB, Function<C, IndexProperties> mappingC,
            int inputStoreIndexAB, int inputStoreIndexC,
            Consumer<BiTuple<A, B>> nextNodesInsert, Consumer<BiTuple<A, B>> nextNodesRetract,
            Indexer<BiTuple<A, B>, Counter<A, B>> indexerAB, Indexer<UniTuple<C>, Set<Counter<A, B>>> indexerC,
            TriPredicate<A, B, C> filtering) {
        this.shouldExist = shouldExist;
        this.mappingAB = mappingAB;
        this.mappingC = mappingC;
        this.inputStoreIndexAB = inputStoreIndexAB;
        this.inputStoreIndexC = inputStoreIndexC;
        this.nextNodesInsert = nextNodesInsert;
        this.nextNodesRetract = nextNodesRetract;
        this.indexerAB = indexerAB;
        this.indexerC = indexerC;
        this.filtering = filtering;
        dirtyCounterQueue = new ArrayDeque<>(1000);
    }

    public void insertAB(BiTuple<A, B> tupleAB) {
        if (tupleAB.store[inputStoreIndexAB] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tupleAB
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = mappingAB.apply(tupleAB.factA, tupleAB.factB);
        tupleAB.store[inputStoreIndexAB] = indexProperties;

        Counter<A, B> counter = new Counter<>(tupleAB);
        indexerAB.put(indexProperties, tupleAB, counter);

        counter.countC = 0;
        indexerC.visit(indexProperties, (tupleC, counterSetC) -> {
            if (filtering == null || filtering.test(tupleAB.factA, tupleAB.factB, tupleC.factA)) {
                counter.countC++;
                counterSetC.add(counter);
            }
        });
        if (shouldExist ? counter.countC > 0 : counter.countC == 0) {
            counter.state = BavetTupleState.CREATING;
            dirtyCounterQueue.add(counter);
        }
    }

    public void retractAB(BiTuple<A, B> tupleAB) {
        IndexProperties indexProperties = (IndexProperties) tupleAB.store[inputStoreIndexAB];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleAB.store[inputStoreIndexAB] = null;

        Counter<A, B> counter = indexerAB.remove(indexProperties, tupleAB);
        indexerC.visit(indexProperties, (tupleC, counterSetC) -> {
            boolean changed = counterSetC.remove(counter);
            // If filtering is active, not all counterSets contain the counter and we don't track which ones do
            if (!changed && filtering == null) {
                throw new IllegalStateException("Impossible state: the tuple (" + tupleAB
                        + ") with indexProperties (" + indexProperties
                        + ") has a counter on the AB side that doesn't exist on the C side.");
            }
        });
        if (shouldExist ? counter.countC > 0 : counter.countC == 0) {
            retractCounter(counter);
        }
    }

    public void insertC(UniTuple<C> tupleC) {
        if (tupleC.store[inputStoreIndexC] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tupleC
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = mappingC.apply(tupleC.factA);
        tupleC.store[inputStoreIndexC] = indexProperties;

        // TODO Maybe predict capacity with Math.max(16, counterMapA.size())
        Set<Counter<A, B>> counterSetC = new LinkedHashSet<>();
        indexerC.put(indexProperties, tupleC, counterSetC);
        indexerAB.visit(indexProperties, (tupleAB, counter) -> {
            if (filtering == null || filtering.test(tupleAB.factA, tupleAB.factB, tupleC.factA)) {
                if (counter.countC == 0) {
                    if (shouldExist) {
                        insertCounter(counter);
                    } else {
                        retractCounter(counter);
                    }
                }
                counter.countC++;
                counterSetC.add(counter);
            }
        });
    }

    public void retractC(UniTuple<C> tupleC) {
        IndexProperties indexProperties = (IndexProperties) tupleC.store[inputStoreIndexC];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleC.store[inputStoreIndexC] = null;
        Set<Counter<A, B>> counterSetC = indexerC.remove(indexProperties, tupleC);
        for (Counter<A, B> counter : counterSetC) {
            counter.countC--;
            if (counter.countC == 0) {
                if (shouldExist) {
                    retractCounter(counter);
                } else {
                    insertCounter(counter);
                }
            }
        }
    }

    protected static class Counter<A, B> {
        public BiTuple<A, B> tuple;
        public BavetTupleState state = BavetTupleState.DEAD;
        public int countC = 0;

        public Counter(BiTuple<A, B> tuple) {
            this.tuple = tuple;
        }

        @Override
        public String toString() {
            return "Counter(" + tuple + ")";
        }
    }

    private void insertCounter(Counter<A, B> counter) {
        switch (counter.state) {
            case DYING:
                counter.state = BavetTupleState.UPDATING;
                break;
            case DEAD:
                counter.state = BavetTupleState.CREATING;
                dirtyCounterQueue.add(counter);
                break;
            case ABORTING:
                counter.state = BavetTupleState.CREATING;
                break;
            default:
                throw new IllegalStateException("Impossible state: the counter (" + counter
                        + ") has an impossible insert state (" + counter.state + ").");
        }
    }

    private void retractCounter(Counter<A, B> counter) {
        switch (counter.state) {
            case CREATING:
                // Kill it before it propagates
                counter.state = BavetTupleState.ABORTING;
                break;
            case UPDATING:
                // Kill the original propagation
                counter.state = BavetTupleState.DYING;
                break;
            case OK:
                counter.state = BavetTupleState.DYING;
                dirtyCounterQueue.add(counter);
                break;
            default:
                throw new IllegalStateException("Impossible state: The counter (" + counter
                        + ") has an impossible retract state (" + counter.state + ").");
        }
    }

    @Override
    public void calculateScore() {
        dirtyCounterQueue.forEach(counter -> {
            switch (counter.state) {
                case CREATING:
                    nextNodesInsert.accept(counter.tuple);
                    counter.state = BavetTupleState.OK;
                    break;
                case UPDATING:
                    nextNodesRetract.accept(counter.tuple);
                    nextNodesInsert.accept(counter.tuple);
                    counter.state = BavetTupleState.OK;
                    break;
                case DYING:
                    nextNodesRetract.accept(counter.tuple);
                    counter.state = BavetTupleState.DEAD;
                    break;
                case ABORTING:
                    counter.state = BavetTupleState.DEAD;
                    break;
                default:
                    throw new IllegalStateException("Impossible state: The dirty counter (" + counter
                            + ") has an non-dirty state (" + counter.state + ").");
            }
        });
        dirtyCounterQueue.clear();
    }

    @Override
    public String toString() {
        return "IfExistsBiWithUniNode";
    }

}
