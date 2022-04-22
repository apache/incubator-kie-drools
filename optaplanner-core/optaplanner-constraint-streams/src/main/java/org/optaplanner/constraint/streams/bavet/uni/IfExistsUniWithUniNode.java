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

package org.optaplanner.constraint.streams.bavet.uni;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.bi.JoinBiNode;
import org.optaplanner.constraint.streams.bavet.common.AbstractNode;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;

public final class IfExistsUniWithUniNode<A, B> extends AbstractNode {

    private final boolean shouldExist;
    private final Function<A, IndexProperties> mappingA;
    private final Function<B, IndexProperties> mappingB;
    private final int inputStoreIndexA;
    private final int inputStoreIndexB;
    /**
     * Calls for example {@link UniScorer#insert(UniTuple)}, {@link JoinBiNode#insertA(UniTuple)} and/or ...
     */
    private final Consumer<UniTuple<A>> nextNodesInsert;
    /**
     * Calls for example {@link UniScorer#retract(UniTuple)}, {@link JoinBiNode#retractA(UniTuple)} and/or ...
     */
    private final Consumer<UniTuple<A>> nextNodesRetract;
    // No outputStoreSize because this node is not a tuple source, even though it has a dirtyCounterQueue.

    private final Indexer<UniTuple<A>, Counter<A>> indexerA;
    private final Indexer<UniTuple<B>, Set<Counter<A>>> indexerB;
    private final BiPredicate<A, B> filtering;
    private final Queue<Counter<A>> dirtyCounterQueue;

    public IfExistsUniWithUniNode(boolean shouldExist,
            Function<A, IndexProperties> mappingA, Function<B, IndexProperties> mappingB,
            int inputStoreIndexA, int inputStoreIndexB,
            Consumer<UniTuple<A>> nextNodesInsert, Consumer<UniTuple<A>> nextNodesRetract,
            Indexer<UniTuple<A>, Counter<A>> indexerA, Indexer<UniTuple<B>, Set<Counter<A>>> indexerB,
            BiPredicate<A, B> filtering) {
        this.shouldExist = shouldExist;
        this.mappingA = mappingA;
        this.mappingB = mappingB;
        this.inputStoreIndexA = inputStoreIndexA;
        this.inputStoreIndexB = inputStoreIndexB;
        this.nextNodesInsert = nextNodesInsert;
        this.nextNodesRetract = nextNodesRetract;
        this.indexerA = indexerA;
        this.indexerB = indexerB;
        this.filtering = filtering;
        dirtyCounterQueue = new ArrayDeque<>(1000);
    }

    public void insertA(UniTuple<A> tupleA) {
        if (tupleA.store[inputStoreIndexA] != null) {
            throw new IllegalStateException("Impossible state: the input for the fact ("
                    + tupleA.factA + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = mappingA.apply(tupleA.factA);
        tupleA.store[inputStoreIndexA] = indexProperties;

        Counter<A> counter = new Counter<>(tupleA);
        indexerA.put(indexProperties, tupleA, counter);

        counter.countB = 0;
        indexerB.visit(indexProperties, (tupleB, counterSetB) -> {
            if (filtering == null || filtering.test(tupleA.factA, tupleB.factA)) {
                counter.countB++;
                counterSetB.add(counter);
            }
        });
        if (shouldExist ? counter.countB > 0 : counter.countB == 0) {
            counter.state = BavetTupleState.CREATING;
            dirtyCounterQueue.add(counter);
        }
    }

    public void retractA(UniTuple<A> tupleA) {
        IndexProperties indexProperties = (IndexProperties) tupleA.store[inputStoreIndexA];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleA.store[inputStoreIndexA] = null;

        Counter<A> counter = indexerA.remove(indexProperties, tupleA);
        indexerB.visit(indexProperties, (tupleB, counterSet) -> {
            boolean changed = counterSet.remove(counter);
            // If filtering is active, not all counterSets contain the counter and we don't track which ones do
            if (!changed && filtering == null) {
                throw new IllegalStateException("Impossible state: the fact (" + tupleA.factA
                        + ") with indexProperties (" + indexProperties
                        + ") has a counter on the A side that doesn't exist on the B side.");
            }
        });
        if (shouldExist ? counter.countB > 0 : counter.countB == 0) {
            retractCounter(counter);
        }
    }

    public void insertB(UniTuple<B> tupleB) {
        if (tupleB.store[inputStoreIndexB] != null) {
            throw new IllegalStateException("Impossible state: the input for the fact ("
                    + tupleB.factA + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = mappingB.apply(tupleB.factA);
        tupleB.store[inputStoreIndexB] = indexProperties;

        // TODO Maybe predict capacity with Math.max(16, counterMapA.size())
        Set<Counter<A>> counterSetB = new LinkedHashSet<>();
        indexerB.put(indexProperties, tupleB, counterSetB);
        indexerA.visit(indexProperties, (tupleA, counter) -> {
            if (filtering == null || filtering.test(tupleA.factA, tupleB.factA)) {
                if (counter.countB == 0) {
                    if (shouldExist) {
                        insertCounter(counter);
                    } else {
                        retractCounter(counter);
                    }
                }
                counter.countB++;
                counterSetB.add(counter);
            }
        });
    }

    public void retractB(UniTuple<B> tupleB) {
        IndexProperties indexProperties = (IndexProperties) tupleB.store[inputStoreIndexB];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleB.store[inputStoreIndexB] = null;
        Set<Counter<A>> counterSetB = indexerB.remove(indexProperties, tupleB);
        for (Counter<A> counter : counterSetB) {
            counter.countB--;
            if (counter.countB == 0) {
                if (shouldExist) {
                    retractCounter(counter);
                } else {
                    insertCounter(counter);
                }
            }
        }
    }

    protected static class Counter<A> {
        public UniTuple<A> tuple;
        public BavetTupleState state = BavetTupleState.DEAD;
        public int countB = 0;

        public Counter(UniTuple<A> tuple) {
            this.tuple = tuple;
        }

        @Override
        public String toString() {
            return "Counter(" + tuple + ")";
        }
    }

    private void insertCounter(Counter<A> counter) {
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
                throw new IllegalStateException("Impossible state: the counter ("
                        + counter + ") has an impossible insert state (" + counter.state + ").");
        }
    }

    private void retractCounter(Counter<A> counter) {
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
                throw new IllegalStateException("Impossible state: The counter ("
                        + counter + ") has an impossible retract state (" + counter.state + ").");
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
        return "IfExistsUniWithUniNode";
    }

}
