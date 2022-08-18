package org.optaplanner.constraint.streams.bavet.common;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.impl.util.FieldBasedScalingSet;

/**
 * There is a strong likelihood that any change to this class, which is not related to indexing,
 * should also be made to {@link AbstractUnindexedIfExistsNode}.
 *
 * @param <LeftTuple_>
 * @param <Right_>
 */
public abstract class AbstractIndexedIfExistsNode<LeftTuple_ extends Tuple, Right_>
        extends AbstractIfExistsNode<LeftTuple_, Right_>
        implements LeftTupleLifecycle<LeftTuple_>, RightTupleLifecycle<UniTuple<Right_>> {

    private final Function<Right_, IndexProperties> mappingRight;
    private final int inputStoreIndexLeft;
    private final int inputStoreIndexRight;
    private final Indexer<LeftTuple_, ExistsCounter<LeftTuple_>> indexerLeft;
    private final Indexer<UniTuple<Right_>, Set<ExistsCounter<LeftTuple_>>> indexerRight;

    protected AbstractIndexedIfExistsNode(boolean shouldExist,
            Function<Right_, IndexProperties> mappingRight,
            int inputStoreIndexLeft, int inputStoreIndexRight,
            TupleLifecycle<LeftTuple_> nextNodeTupleLifecycle,
            Indexer<LeftTuple_, ExistsCounter<LeftTuple_>> indexerLeft,
            Indexer<UniTuple<Right_>, Set<ExistsCounter<LeftTuple_>>> indexerRight,
            boolean isFiltering) {
        super(shouldExist, nextNodeTupleLifecycle, isFiltering);
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
        IndexProperties indexProperties = createIndexProperties(leftTuple);
        leftTuple.setStore(inputStoreIndexLeft, indexProperties);

        ExistsCounter<LeftTuple_> counter = new ExistsCounter<>(leftTuple, shouldExist);
        indexerLeft.put(indexProperties, leftTuple, counter);

        indexerRight.visit(indexProperties, (rightTuple, counterSetRight) -> {
            if (!isFiltering || testFiltering(leftTuple, rightTuple)) {
                counter.countRight++;
                counterSetRight.add(counter);
            }
        });
        if (counter.isAlive()) {
            counter.state = BavetTupleState.CREATING;
            dirtyCounterQueue.add(counter);
        }
    }

    @Override
    public final void updateLeft(LeftTuple_ leftTuple) {
        IndexProperties oldIndexProperties = leftTuple.getStore(inputStoreIndexLeft);
        if (oldIndexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertLeft(leftTuple);
            return;
        }
        IndexProperties newIndexProperties = createIndexProperties(leftTuple);

        if (oldIndexProperties.equals(newIndexProperties)) {
            // No need for re-indexing because the index properties didn't change
            ExistsCounter<LeftTuple_> counter = indexerLeft.get(oldIndexProperties, leftTuple);
            // The indexers contain counters in the DEAD state, to track the rightCount.
            if (!isFiltering) {
                processCounterUpdate(counter);
            } else {
                // Call filtering for the leftTuple and rightTuple combinations again
                counter.countRight = 0;
                indexerRight.visit(oldIndexProperties, (rightTuple, counterSetRight) -> {
                    if (testFiltering(leftTuple, rightTuple)) {
                        counter.countRight++;
                    } else {
                        counterSetRight.remove(counter);
                    }
                });
                if (counter.isAlive()) {
                    // Insert or update
                    insertOrUpdateCounter(counter);
                } else {
                    // Retract or remain dead
                    retractOrRemainDeadCounter(counter);
                }
            }
        } else {
            ExistsCounter<LeftTuple_> counter = deindexLeft(leftTuple, oldIndexProperties);

            counter.countRight = 0;
            leftTuple.setStore(inputStoreIndexLeft, newIndexProperties);
            indexerLeft.put(newIndexProperties, leftTuple, counter);
            indexerRight.visit(newIndexProperties, (rightTuple, counterSetRight) -> {
                if (!isFiltering || testFiltering(leftTuple, rightTuple)) {
                    counter.countRight++;
                    counterSetRight.add(counter);
                }
            });

            if (counter.isAlive()) {
                insertOrUpdateCounter(counter);
            } else {
                retractOrRemainDeadCounter(counter);
            }
        }
    }

    private ExistsCounter<LeftTuple_> deindexLeft(LeftTuple_ leftTuple, IndexProperties oldIndexProperties) {
        ExistsCounter<LeftTuple_> counter = indexerLeft.remove(oldIndexProperties, leftTuple);
        indexerRight.visit(oldIndexProperties, (rightTuple, counterSetRight) -> {
            boolean changed = counterSetRight.remove(counter);
            // If filtering is active, not all counterSets contain the counter and we don't track which ones do
            if (!changed && !isFiltering) {
                throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                        + ") with indexProperties (" + oldIndexProperties
                        + ") has a counter on the left side that doesn't exist on the right side.");
            }
        });
        return counter;
    }

    @Override
    public final void retractLeft(LeftTuple_ leftTuple) {
        IndexProperties indexProperties = leftTuple.getStore(inputStoreIndexLeft);
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        leftTuple.setStore(inputStoreIndexLeft, null);

        ExistsCounter<LeftTuple_> counter = deindexLeft(leftTuple, indexProperties);
        if (counter.isAlive()) {
            retractCounter(counter);
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

        Set<ExistsCounter<LeftTuple_>> counterSetRight = new FieldBasedScalingSet<>(LinkedHashSet::new);
        indexRight(rightTuple, indexProperties, counterSetRight);
    }

    private void indexRight(UniTuple<Right_> rightTuple, IndexProperties indexProperties,
            Set<ExistsCounter<LeftTuple_>> counterSetRight) {
        indexerRight.put(indexProperties, rightTuple, counterSetRight);
        indexerLeft.visit(indexProperties,
                (leftTuple, counter) -> processInsert(leftTuple, rightTuple, counter, counterSetRight));
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
            if (isFiltering) {
                // Call filtering for the leftTuple and rightTuple combinations again
                Set<ExistsCounter<LeftTuple_>> counterSetRight = indexerRight.get(oldIndexProperties, rightTuple);
                processAndClearCounters(counterSetRight);
                indexerLeft.visit(newIndexProperties,
                        (leftTuple, counter) -> processUpdate(leftTuple, rightTuple, counter, counterSetRight));
            }
        } else {
            Set<ExistsCounter<LeftTuple_>> counterSetRight = indexerRight.remove(oldIndexProperties, rightTuple);
            processAndClearCounters(counterSetRight);

            rightTuple.setStore(inputStoreIndexRight, newIndexProperties);
            indexRight(rightTuple, newIndexProperties, counterSetRight);
        }
    }

    @Override
    public final void retractRight(UniTuple<Right_> rightTuple) {
        IndexProperties indexProperties = rightTuple.getStore(inputStoreIndexRight);
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        rightTuple.setStore(inputStoreIndexRight, null);
        Set<ExistsCounter<LeftTuple_>> counterSetRight = indexerRight.remove(indexProperties, rightTuple);
        processCounters(counterSetRight);
    }

    protected abstract IndexProperties createIndexProperties(LeftTuple_ leftTuple);

}
