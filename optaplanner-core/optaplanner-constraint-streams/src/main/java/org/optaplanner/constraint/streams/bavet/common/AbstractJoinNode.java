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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

public abstract class AbstractJoinNode<LeftTuple_ extends Tuple, Right_, OutTuple_ extends Tuple> extends AbstractNode {

    private final Function<Right_, IndexProperties> mappingRight;
    private final int inputStoreIndexLeft;
    private final int inputStoreIndexRight;
    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)} and/or ...
     */
    private final Consumer<OutTuple_> nextNodesInsert;
    /**
     * Calls for example {@link AbstractScorer#retract(Tuple)} and/or ...
     */
    private final Consumer<OutTuple_> nextNodesRetract;
    private final Indexer<LeftTuple_, Map<UniTuple<Right_>, OutTuple_>> indexerLeft;
    private final Indexer<UniTuple<Right_>, Map<LeftTuple_, OutTuple_>> indexerRight;
    private final Queue<OutTuple_> dirtyTupleQueue;

    protected AbstractJoinNode(Function<Right_, IndexProperties> mappingRight,
            int inputStoreIndexLeft, int inputStoreIndexRight,
            Consumer<OutTuple_> nextNodesInsert, Consumer<OutTuple_> nextNodesRetract,
            Indexer<LeftTuple_, Map<UniTuple<Right_>, OutTuple_>> indexerLeft,
            Indexer<UniTuple<Right_>, Map<LeftTuple_, OutTuple_>> indexerRight) {
        this.mappingRight = mappingRight;
        this.inputStoreIndexLeft = inputStoreIndexLeft;
        this.inputStoreIndexRight = inputStoreIndexRight;
        this.nextNodesInsert = nextNodesInsert;
        this.nextNodesRetract = nextNodesRetract;
        this.indexerLeft = indexerLeft;
        this.indexerRight = indexerRight;
        dirtyTupleQueue = new ArrayDeque<>(1000);
    }

    public final void insertLeft(LeftTuple_ leftTuple) {
        Object[] tupleStore = leftTuple.getStore();
        if (tupleStore[inputStoreIndexLeft] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + leftTuple
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = createIndexProperties(leftTuple);
        tupleStore[inputStoreIndexLeft] = indexProperties;

        Map<UniTuple<Right_>, OutTuple_> outTupleMapLeft = new HashMap<>();
        indexerLeft.put(indexProperties, leftTuple, outTupleMapLeft);
        indexerRight.visit(indexProperties, (rightTuple, emptyMap) -> {
            OutTuple_ outTuple = createOutTuple(leftTuple, rightTuple);
            outTuple.setState(BavetTupleState.CREATING);
            outTupleMapLeft.put(rightTuple, outTuple);
            dirtyTupleQueue.add(outTuple);
        });
    }

    protected abstract IndexProperties createIndexProperties(LeftTuple_ leftTuple);

    protected abstract OutTuple_ createOutTuple(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple);

    public final void retractLeft(LeftTuple_ leftTuple) {
        Object[] tupleStore = leftTuple.getStore();
        IndexProperties indexProperties = (IndexProperties) tupleStore[inputStoreIndexLeft];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleStore[inputStoreIndexLeft] = null;

        Map<UniTuple<Right_>, OutTuple_> outTupleMapLeft = indexerLeft.remove(indexProperties, leftTuple);
        for (OutTuple_ outTuple : outTupleMapLeft.values()) {
            killTuple(outTuple);
        }
    }

    public final void insertRight(UniTuple<Right_> rightTuple) {
        if (rightTuple.store[inputStoreIndexRight] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + rightTuple
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = mappingRight.apply(rightTuple.factA);
        rightTuple.store[inputStoreIndexRight] = indexProperties;

        indexerRight.put(indexProperties, rightTuple, Collections.emptyMap());
        indexerLeft.visit(indexProperties, (leftTuple, outTupleMapLeft) -> {
            OutTuple_ outTuple = createOutTuple(leftTuple, rightTuple);
            outTuple.setState(BavetTupleState.CREATING);
            outTupleMapLeft.put(rightTuple, outTuple);
            dirtyTupleQueue.add(outTuple);
        });
    }

    public final void retractRight(UniTuple<Right_> rightTuple) {
        IndexProperties indexProperties = (IndexProperties) rightTuple.store[inputStoreIndexRight];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        rightTuple.store[inputStoreIndexRight] = null;

        indexerRight.remove(indexProperties, rightTuple);
        // Remove out tuples from the other side
        indexerLeft.visit(indexProperties, (leftTuple, outTupleMapLeft) -> {
            OutTuple_ outTuple = outTupleMapLeft.remove(rightTuple);
            if (outTuple == null) {
                throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                        + ") with indexProperties (" + indexProperties
                        + ") has tuples on the right side that didn't exist on the left side.");
            }
            killTuple(outTuple);
        });
    }

    private void killTuple(OutTuple_ outTuple) {
        // Don't add the tuple to the dirtyTupleQueue twice
        BavetTupleState state = outTuple.getState();
        if (state.isDirty()) {
            switch (state) {
                case CREATING:
                    // Kill it before it propagates
                    outTuple.setState(BavetTupleState.ABORTING);
                    break;
                case UPDATING:
                    // Kill the original propagation
                    outTuple.setState(BavetTupleState.DYING);
                    break;
                case DYING:
                    break;
                default:
                    throw new IllegalStateException("Impossible state: The tuple (" + outTuple
                            + ") has the dirty state (" + state + ").");
            }
        } else {
            outTuple.setState(BavetTupleState.DYING);
            dirtyTupleQueue.add(outTuple);
        }
    }

    @Override
    public void calculateScore() {
        dirtyTupleQueue.forEach(tuple -> {
            BavetTupleState state = tuple.getState();
            // Retract
            if (state == BavetTupleState.UPDATING || state == BavetTupleState.DYING) {
                nextNodesRetract.accept(tuple);
            }
            // Insert
            if (state == BavetTupleState.CREATING || state == BavetTupleState.UPDATING) {
                nextNodesInsert.accept(tuple);
            }
            switch (state) {
                case CREATING:
                case UPDATING:
                    tuple.setState(BavetTupleState.OK);
                    return;
                case DYING:
                case ABORTING:
                    tuple.setState(BavetTupleState.DEAD);
                    return;
                case DEAD:
                    throw new IllegalStateException("Impossible state: The tuple (" + tuple + ") in node (" +
                            this + ") is already in the dead state (" + state + ").");
                default:
                    throw new IllegalStateException("Impossible state: The tuple (" + tuple + ") in node (" +
                            this + ") is in an unexpected state (" + state + ").");
            }
        });
        dirtyTupleQueue.clear();
    }

}
