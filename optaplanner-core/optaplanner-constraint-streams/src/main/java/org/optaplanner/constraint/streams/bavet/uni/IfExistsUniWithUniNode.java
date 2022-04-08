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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.bi.JoinBiNode;
import org.optaplanner.constraint.streams.bavet.common.AbstractNode;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;

public final class IfExistsUniWithUniNode<A, B> extends AbstractNode {

    private final boolean shouldExist;
    private final Function<A, Object[]> mappingA;
    private final Function<B, Object[]> mappingB;
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
    private final Queue<Counter<A>> dirtyCounterQueue;

    public IfExistsUniWithUniNode(boolean shouldExist, Function<A, Object[]> mappingA, Function<B, Object[]> mappingB,
            int inputStoreIndexA, int inputStoreIndexB,
            Consumer<UniTuple<A>> nextNodesInsert, Consumer<UniTuple<A>> nextNodesRetract,
            Indexer<UniTuple<A>, Counter<A>> indexerA, Indexer<UniTuple<B>, Set<Counter<A>>> indexerB) {
        this.shouldExist = shouldExist;
        this.mappingA = mappingA;
        this.mappingB = mappingB;
        this.inputStoreIndexA = inputStoreIndexA;
        this.inputStoreIndexB = inputStoreIndexB;
        this.nextNodesInsert = nextNodesInsert;
        this.nextNodesRetract = nextNodesRetract;
        this.indexerA = indexerA;
        this.indexerB = indexerB;
        dirtyCounterQueue = new ArrayDeque<>(1000);
    }

    public void insertA(UniTuple<A> tupleA) {
        if (tupleA.store[inputStoreIndexA] != null) {
            throw new IllegalStateException("Impossible state: the input for the fact ("
                    + tupleA.factA + ") was already added in the tupleStore.");
        }
        Object[] indexProperties = mappingA.apply(tupleA.factA);
        tupleA.store[inputStoreIndexA] = indexProperties;

        Counter<A> counter = new Counter<>(tupleA);
        indexerA.put(indexProperties, tupleA, counter);

        counter.countB = indexerB.countValues(indexProperties);
        if (shouldExist ? counter.countB > 0 : counter.countB == 0) {
            counter.state = BavetTupleState.CREATING;
            indexerB.visit(indexProperties, (tuple, counterSetB) -> counterSetB.add(counter));
            dirtyCounterQueue.add(counter);
        }
    }

    public void retractA(UniTuple<A> tupleA) {
        Object[] indexProperties = (Object[]) tupleA.store[inputStoreIndexA];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleA.store[inputStoreIndexA] = null;

        Counter<A> counter = indexerA.remove(indexProperties, tupleA);
        indexerB.visit(indexProperties, (tuple, counterSet) -> {
            boolean changed = counterSet.remove(counter);
            if (!changed) {
                throw new IllegalStateException("Impossible state: the fact (" + tupleA.factA
                        + ") with indexProperties (" + Arrays.toString(indexProperties)
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
        Object[] indexProperties = mappingB.apply(tupleB.factA);
        tupleB.store[inputStoreIndexB] = indexProperties;

        Set<Counter<A>> counterSetB = new LinkedHashSet<>();
        indexerB.put(indexProperties, tupleB, counterSetB);
        indexerA.visit(indexProperties, (tuple, counter) -> {
            if (counter.countB == 0) {
                if (shouldExist) {
                    if (counter.state != BavetTupleState.DEAD) {
                        // TODO what if it was retracted before this insert with no calculateScore()?
                        throw new UnsupportedOperationException();
                    }
                    counter.state = BavetTupleState.CREATING;
                    counterSetB.add(counter);
                    dirtyCounterQueue.add(counter);
                } else {
                    throw new UnsupportedOperationException();
                    //                    counterSetB.remove(counter);
                    //                    retractCounter(counter);
                }
            }
            counter.countB++;
        });
    }

    public void retractB(UniTuple<B> tupleB) {
        Object[] indexProperties = (Object[]) tupleB.store[inputStoreIndexB];
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
                    throw new UnsupportedOperationException();
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

    private void retractCounter(Counter<A> counter) {
        // Don't add the tuple to the dirtyTupleQueue twice
        if (counter.state.isDirty()) {
            switch (counter.state) {
                case CREATING:
                    // Kill it before it propagates
                    counter.state = BavetTupleState.ABORTING;
                    break;
                case UPDATING:
                    // Kill the original propagation
                    counter.state = BavetTupleState.DYING;
                    break;
                case DYING:
                    break;
                default:
                    throw new IllegalStateException("Impossible state: The counter for the fact ("
                            + counter.tuple.factA + ") has the dirty state (" + counter.state + ").");
            }
        } else {
            counter.state = BavetTupleState.DYING;
            dirtyCounterQueue.add(counter);
        }
    }

    @Override
    public void calculateScore() {
        dirtyCounterQueue.forEach(counter -> {
            // Retract
            if (counter.state == BavetTupleState.UPDATING || counter.state == BavetTupleState.DYING) {
                nextNodesRetract.accept(counter.tuple);
            }
            // Insert
            if (counter.state == BavetTupleState.CREATING || counter.state == BavetTupleState.UPDATING) {
                nextNodesInsert.accept(counter.tuple);
            }
            switch (counter.state) {
                case CREATING:
                case UPDATING:
                    counter.state = BavetTupleState.OK;
                    return;
                case DYING:
                case ABORTING:
                    counter.state = BavetTupleState.DEAD;
                    return;
                case DEAD:
                    throw new IllegalStateException("Impossible state: The counter (" + counter + ") in node (" +
                            this + ") is already in the dead state (" + counter.state + ").");
                default:
                    throw new IllegalStateException("Impossible state: The counter (" + counter + ") in node (" +
                            this + ") is in an unexpected state (" + counter.state + ").");
            }
        });
        dirtyCounterQueue.clear();
    }

    @Override
    public String toString() {
        return "IfExistsUniWithUniNode";
    }

}
