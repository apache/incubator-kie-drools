package org.optaplanner.constraint.streams.bavet.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.impl.util.FieldBasedScalingMap;

/**
 * There is a strong likelihood that any change to this class, which is not related to indexing,
 * should also be made to {@link AbstractUnindexedJoinNode}.
 *
 * @param <LeftTuple_>
 * @param <Right_>
 */
public abstract class AbstractIndexedJoinNode<LeftTuple_ extends Tuple, Right_, OutTuple_ extends Tuple, MutableOutTuple_ extends OutTuple_>
        extends AbstractJoinNode<LeftTuple_, Right_, OutTuple_, MutableOutTuple_>
        implements LeftTupleLifecycle<LeftTuple_>, RightTupleLifecycle<UniTuple<Right_>> {

    private final Function<Right_, IndexProperties> mappingRight;
    private final int inputStoreIndexLeft;
    private final int inputStoreIndexRight;
    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)} and/or ...
     */
    private final Indexer<LeftTuple_, Map<UniTuple<Right_>, MutableOutTuple_>> indexerLeft;
    private final Indexer<UniTuple<Right_>, Map<LeftTuple_, MutableOutTuple_>> indexerRight;

    protected AbstractIndexedJoinNode(Function<Right_, IndexProperties> mappingRight, int inputStoreIndexLeft,
            int inputStoreIndexRight, TupleLifecycle<OutTuple_> nextNodesTupleLifecycle,
            Indexer<LeftTuple_, Map<UniTuple<Right_>, MutableOutTuple_>> indexerLeft,
            Indexer<UniTuple<Right_>, Map<LeftTuple_, MutableOutTuple_>> indexerRight) {
        super(nextNodesTupleLifecycle);
        this.mappingRight = mappingRight;
        this.inputStoreIndexLeft = inputStoreIndexLeft;
        this.inputStoreIndexRight = inputStoreIndexRight;
        this.indexerLeft = indexerLeft;
        this.indexerRight = indexerRight;
    }

    @Override
    public final void insertLeft(LeftTuple_ leftTuple) {
        if (leftTuple.getStore(inputStoreIndexLeft) != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + leftTuple
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = createIndexPropertiesLeft(leftTuple);
        leftTuple.setStore(inputStoreIndexLeft, indexProperties);

        Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = new FieldBasedScalingMap<>(LinkedHashMap::new);
        indexAndPropagateLeft(leftTuple, indexProperties, outTupleMapLeft);
    }

    private void indexAndPropagateLeft(LeftTuple_ leftTuple, IndexProperties newIndexProperties,
            Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft) {
        indexerLeft.put(newIndexProperties, leftTuple, outTupleMapLeft);
        indexerRight.visit(newIndexProperties,
                (rightTuple, emptyMap) -> insertTuple(outTupleMapLeft, leftTuple, rightTuple));
    }

    @Override
    public final void updateLeft(LeftTuple_ leftTuple) {
        IndexProperties oldIndexProperties = leftTuple.getStore(inputStoreIndexLeft);
        if (oldIndexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertLeft(leftTuple);
            return;
        }
        IndexProperties newIndexProperties = createIndexPropertiesLeft(leftTuple);

        if (oldIndexProperties.equals(newIndexProperties)) {
            // No need for re-indexing because the index properties didn't change
            // Still needed to propagate the update for downstream filters, matchWeighters, ...
            Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = indexerLeft.get(oldIndexProperties, leftTuple);
            for (MutableOutTuple_ outTuple : outTupleMapLeft.values()) {
                updateOutTupleLeft(outTuple, leftTuple);
                updateTuple(outTuple);
            }
        } else {
            Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = indexerLeft.remove(oldIndexProperties, leftTuple);
            for (OutTuple_ outTuple : outTupleMapLeft.values()) {
                retractTuple(outTuple);
            }
            outTupleMapLeft.clear();

            leftTuple.setStore(inputStoreIndexLeft, newIndexProperties);
            indexAndPropagateLeft(leftTuple, newIndexProperties, outTupleMapLeft);
        }
    }

    @Override
    public final void retractLeft(LeftTuple_ leftTuple) {
        IndexProperties indexProperties = leftTuple.getStore(inputStoreIndexLeft);
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        leftTuple.setStore(inputStoreIndexLeft, null);

        Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = indexerLeft.remove(indexProperties, leftTuple);
        for (OutTuple_ outTuple : outTupleMapLeft.values()) {
            retractTuple(outTuple);
        }
    }

    @Override
    public final void insertRight(UniTuple<Right_> rightTuple) {
        if (rightTuple.getStore(inputStoreIndexRight) != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + rightTuple
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = mappingRight.apply(rightTuple.getFactA());
        rightTuple.setStore(inputStoreIndexRight, indexProperties);
        indexAndPropagateRight(rightTuple, indexProperties);
    }

    private void indexAndPropagateRight(UniTuple<Right_> rightTuple, IndexProperties indexProperties) {
        indexerRight.put(indexProperties, rightTuple, Collections.emptyMap());
        indexerLeft.visit(indexProperties,
                (leftTuple, outTupleMapLeft) -> insertTuple(outTupleMapLeft, leftTuple, rightTuple));
    }

    @Override
    public final void updateRight(UniTuple<Right_> rightTuple) {
        IndexProperties oldIndexProperties = rightTuple.getStore(inputStoreIndexRight);
        if (oldIndexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertRight(rightTuple);
            return;
        }
        IndexProperties newIndexProperties = mappingRight.apply(rightTuple.getFactA());

        if (oldIndexProperties.equals(newIndexProperties)) {
            // No need for re-indexing because the index properties didn't change
            // Still needed to propagate the update for downstream filters, matchWeighters, ...
            indexerLeft.visit(oldIndexProperties, (leftTuple, outTupleMapLeft) -> {
                MutableOutTuple_ outTuple = outTupleMapLeft.get(rightTuple);
                updateOutTupleRight(outTuple, rightTuple);
                if (outTuple == null) {
                    throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                            + ") with indexProperties (" + oldIndexProperties
                            + ") has tuples on the right side that didn't exist on the left side.");
                }
                updateTuple(outTuple);
            });
        } else {
            deindexRightTuple(oldIndexProperties, rightTuple);
            rightTuple.setStore(inputStoreIndexRight, newIndexProperties);
            indexAndPropagateRight(rightTuple, newIndexProperties);
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
        IndexProperties indexProperties = rightTuple.getStore(inputStoreIndexRight);
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        rightTuple.setStore(inputStoreIndexRight, null);
        deindexRightTuple(indexProperties, rightTuple);
    }

    protected abstract IndexProperties createIndexPropertiesLeft(LeftTuple_ leftTuple);

}
