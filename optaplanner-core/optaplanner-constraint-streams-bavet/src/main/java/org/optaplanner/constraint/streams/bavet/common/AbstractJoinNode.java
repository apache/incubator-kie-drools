package org.optaplanner.constraint.streams.bavet.common;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

public abstract class AbstractJoinNode<LeftTuple_ extends Tuple, Right_, OutTuple_ extends Tuple>
        extends AbstractNode
        implements LeftTupleLifecycle<LeftTuple_>, RightTupleLifecycle<UniTuple<Right_>> {

    private final Function<Right_, IndexProperties> mappingRight;
    private final int inputStoreIndexLeft;
    private final int inputStoreIndexRight;
    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)} and/or ...
     */
    private final TupleLifecycle<OutTuple_> nextNodesTupleLifecycle;
    private final Indexer<LeftTuple_, Map<UniTuple<Right_>, OutTuple_>> indexerLeft;
    private final Indexer<UniTuple<Right_>, Map<LeftTuple_, OutTuple_>> indexerRight;
    private final Queue<OutTuple_> dirtyTupleQueue;

    protected AbstractJoinNode(Function<Right_, IndexProperties> mappingRight, int inputStoreIndexLeft,
            int inputStoreIndexRight, TupleLifecycle<OutTuple_> nextNodesTupleLifecycle,
            Indexer<LeftTuple_, Map<UniTuple<Right_>, OutTuple_>> indexerLeft,
            Indexer<UniTuple<Right_>, Map<LeftTuple_, OutTuple_>> indexerRight) {
        this.mappingRight = mappingRight;
        this.inputStoreIndexLeft = inputStoreIndexLeft;
        this.inputStoreIndexRight = inputStoreIndexRight;
        this.nextNodesTupleLifecycle = nextNodesTupleLifecycle;
        this.indexerLeft = indexerLeft;
        this.indexerRight = indexerRight;
        dirtyTupleQueue = new ArrayDeque<>(1000);
    }

    @Override
    public final void insertLeft(LeftTuple_ leftTuple) {
        Object[] tupleStore = leftTuple.getStore();
        if (tupleStore[inputStoreIndexLeft] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + leftTuple
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = createIndexProperties(leftTuple);
        tupleStore[inputStoreIndexLeft] = indexProperties;

        Map<UniTuple<Right_>, OutTuple_> outTupleMapLeft = new HashMap<>();
        indexLeftTuple(leftTuple, indexProperties, outTupleMapLeft);
    }

    private void indexLeftTuple(LeftTuple_ leftTuple, IndexProperties newIndexProperties,
            Map<UniTuple<Right_>, OutTuple_> outTupleMapLeft) {
        indexerLeft.put(newIndexProperties, leftTuple, outTupleMapLeft);
        indexerRight.visit(newIndexProperties, (rightTuple, emptyMap) -> {
            OutTuple_ outTuple = createOutTuple(leftTuple, rightTuple);
            outTuple.setState(BavetTupleState.CREATING);
            outTupleMapLeft.put(rightTuple, outTuple);
            dirtyTupleQueue.add(outTuple);
        });
    }

    @Override
    public final void updateLeft(LeftTuple_ leftTuple) {
        Object[] tupleStore = leftTuple.getStore();
        IndexProperties oldIndexProperties = (IndexProperties) tupleStore[inputStoreIndexLeft];
        if (oldIndexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertLeft(leftTuple);
            return;
        }
        IndexProperties newIndexProperties = createIndexProperties(leftTuple);

        if (oldIndexProperties.equals(newIndexProperties)) {
            // No need for re-indexing because the index properties didn't change
            // Still needed to propagate the update for downstream filters, matchWeighters, ...
            Map<UniTuple<Right_>, OutTuple_> outTupleMapLeft = indexerLeft.get(oldIndexProperties, leftTuple);
            for (OutTuple_ outTuple : outTupleMapLeft.values()) {
                updateTuple(outTuple);
            }
        } else {
            Map<UniTuple<Right_>, OutTuple_> outTupleMapLeft = indexerLeft.remove(oldIndexProperties, leftTuple);
            for (OutTuple_ outTuple : outTupleMapLeft.values()) {
                retractTuple(outTuple);
            }
            outTupleMapLeft.clear();

            tupleStore[inputStoreIndexLeft] = newIndexProperties;
            indexLeftTuple(leftTuple, newIndexProperties, outTupleMapLeft);
        }
    }

    @Override
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
            retractTuple(outTuple);
        }
    }

    @Override
    public final void insertRight(UniTuple<Right_> rightTuple) {
        if (rightTuple.store[inputStoreIndexRight] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + rightTuple
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = mappingRight.apply(rightTuple.factA);
        rightTuple.store[inputStoreIndexRight] = indexProperties;
        indexRightTuple(rightTuple, indexProperties);
    }

    private void indexRightTuple(UniTuple<Right_> rightTuple, IndexProperties indexProperties) {
        indexerRight.put(indexProperties, rightTuple, Collections.emptyMap());
        indexerLeft.visit(indexProperties, (leftTuple, outTupleMapLeft) -> {
            OutTuple_ outTuple = createOutTuple(leftTuple, rightTuple);
            outTuple.setState(BavetTupleState.CREATING);
            outTupleMapLeft.put(rightTuple, outTuple);
            dirtyTupleQueue.add(outTuple);
        });
    }

    @Override
    public final void updateRight(UniTuple<Right_> rightTuple) {
        IndexProperties oldIndexProperties = (IndexProperties) rightTuple.store[inputStoreIndexRight];
        if (oldIndexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertRight(rightTuple);
            return;
        }
        IndexProperties newIndexProperties = mappingRight.apply(rightTuple.factA);

        if (oldIndexProperties.equals(newIndexProperties)) {
            // No need for re-indexing because the index properties didn't change
            // Still needed to propagate the update for downstream filters, matchWeighters, ...
            indexerLeft.visit(oldIndexProperties, (leftTuple, outTupleMapLeft) -> {
                OutTuple_ outTuple = outTupleMapLeft.get(rightTuple);
                if (outTuple == null) {
                    throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                            + ") with indexProperties (" + oldIndexProperties
                            + ") has tuples on the right side that didn't exist on the left side.");
                }
                updateTuple(outTuple);
            });
        } else {
            deindexRightTuple(oldIndexProperties, rightTuple);
            rightTuple.store[inputStoreIndexRight] = newIndexProperties;
            indexRightTuple(rightTuple, newIndexProperties);
        }
    }

    private void deindexRightTuple(IndexProperties indexProperties, UniTuple<Right_> rightTuple) {
        indexerRight.remove(indexProperties, rightTuple);
        // Remove out tuples from the other side
        indexerLeft.visit(indexProperties, (leftTuple, outTupleMapLeft) -> {
            OutTuple_ outTuple = outTupleMapLeft.remove(rightTuple);
            if (outTuple == null) {
                throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                        + ") with indexProperties (" + indexProperties
                        + ") has tuples on the right side that didn't exist on the left side.");
            }
            retractTuple(outTuple);
        });
    }

    @Override
    public final void retractRight(UniTuple<Right_> rightTuple) {
        IndexProperties indexProperties = (IndexProperties) rightTuple.store[inputStoreIndexRight];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        rightTuple.store[inputStoreIndexRight] = null;
        deindexRightTuple(indexProperties, rightTuple);
    }

    protected abstract IndexProperties createIndexProperties(LeftTuple_ leftTuple);

    protected abstract OutTuple_ createOutTuple(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple);

    private void updateTuple(OutTuple_ outTuple) {
        switch (outTuple.getState()) {
            case CREATING:
            case UPDATING:
                // Don't add the tuple to the dirtyTupleQueue twice
                break;
            case OK:
                outTuple.setState(BavetTupleState.UPDATING);
                dirtyTupleQueue.add(outTuple);
                break;
            // Impossible because they shouldn't linger in the indexes
            case DYING:
            case ABORTING:
            case DEAD:
            default:
                throw new IllegalStateException("Impossible state: The tuple (" + outTuple.getState() + ") in node (" +
                        this + ") is in an unexpected state (" + outTuple.getState() + ").");
        }
    }

    private void retractTuple(OutTuple_ outTuple) {
        switch (outTuple.getState()) {
            case CREATING:
                // Don't add the tuple to the dirtyTupleQueue twice
                // Kill it before it propagates
                outTuple.setState(BavetTupleState.ABORTING);
                break;
            case OK:
                outTuple.setState(BavetTupleState.DYING);
                dirtyTupleQueue.add(outTuple);
                break;
            case UPDATING:
                // Don't add the tuple to the dirtyTupleQueue twice
                // Kill the original propagation
                outTuple.setState(BavetTupleState.DYING);
                break;
            // Impossible because they shouldn't linger in the indexes
            case DYING:
            case ABORTING:
            case DEAD:
            default:
                throw new IllegalStateException("Impossible state: The tuple (" + outTuple.getState() + ") in node (" +
                        this + ") is in an unexpected state (" + outTuple.getState() + ").");
        }
    }

    @Override
    public void calculateScore() {
        for (OutTuple_ tuple : dirtyTupleQueue) {
            switch (tuple.getState()) {
                case CREATING:
                    nextNodesTupleLifecycle.insert(tuple);
                    tuple.setState(BavetTupleState.OK);
                    break;
                case UPDATING:
                    nextNodesTupleLifecycle.update(tuple);
                    tuple.setState(BavetTupleState.OK);
                    break;
                case DYING:
                    nextNodesTupleLifecycle.retract(tuple);
                    tuple.setState(BavetTupleState.DEAD);
                    break;
                case ABORTING:
                    tuple.setState(BavetTupleState.DEAD);
                    break;
                case OK:
                case DEAD:
                default:
                    throw new IllegalStateException("Impossible state: The tuple (" + tuple + ") in node (" +
                            this + ") is in an unexpected state (" + tuple.getState() + ").");
            }
        }
        dirtyTupleQueue.clear();
    }

}
