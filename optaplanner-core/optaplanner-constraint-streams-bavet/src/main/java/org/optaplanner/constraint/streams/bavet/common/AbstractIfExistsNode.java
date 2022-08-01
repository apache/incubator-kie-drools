package org.optaplanner.constraint.streams.bavet.common;

import static org.optaplanner.constraint.streams.bavet.common.BavetTupleState.DEAD;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntPredicate;

import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

public abstract class AbstractIfExistsNode<LeftTuple_ extends Tuple, Right_>
        extends AbstractNode
        implements LeftTupleLifecycle<LeftTuple_>, RightTupleLifecycle<UniTuple<Right_>> {

    private final boolean shouldExist;
    private final Function<Right_, IndexProperties> mappingRight;
    private final int inputStoreIndexLeft;
    private final int inputStoreIndexRight;
    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)}, and/or ...
     */
    private final TupleLifecycle<LeftTuple_> nextNodesTupleLifecycle;

    // No outputStoreSize because this node is not a tuple source, even though it has a dirtyCounterQueue.

    private final Indexer<LeftTuple_, Counter<LeftTuple_>> indexerLeft;
    private final Indexer<UniTuple<Right_>, Set<Counter<LeftTuple_>>> indexerRight;
    private final Queue<Counter<LeftTuple_>> dirtyCounterQueue;
    private final boolean isFiltering;

    protected AbstractIfExistsNode(boolean shouldExist,
            Function<Right_, IndexProperties> mappingRight,
            int inputStoreIndexLeft, int inputStoreIndexRight,
            TupleLifecycle<LeftTuple_> nextNodeTupleLifecycle,
            Indexer<LeftTuple_, Counter<LeftTuple_>> indexerLeft,
            Indexer<UniTuple<Right_>, Set<Counter<LeftTuple_>>> indexerRight,
            boolean isFiltering) {
        this.shouldExist = shouldExist;
        this.mappingRight = mappingRight;
        this.inputStoreIndexLeft = inputStoreIndexLeft;
        this.inputStoreIndexRight = inputStoreIndexRight;
        this.nextNodesTupleLifecycle = nextNodeTupleLifecycle;
        this.indexerLeft = indexerLeft;
        this.indexerRight = indexerRight;
        this.dirtyCounterQueue = new ArrayDeque<>(1000);
        this.isFiltering = isFiltering;
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

        Counter<LeftTuple_> counter = new Counter<>(leftTuple, shouldExist);
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
            Counter<LeftTuple_> counter = indexerLeft.get(oldIndexProperties, leftTuple);
            // The indexers contain counters in the DEAD state, to track the rightCount.
            if (!isFiltering) {
                switch (counter.state) {
                    case CREATING:
                    case UPDATING:
                    case DYING:
                    case ABORTING:
                    case DEAD:
                        // Counter state does not change because the index properties didn't change
                        break;
                    case OK:
                        // Still needed to propagate the update for downstream filters, matchWeighters, ...
                        counter.state = BavetTupleState.UPDATING;
                        dirtyCounterQueue.add(counter);
                        break;
                    default:
                        throw new IllegalStateException("Impossible state: The counter (" + counter.state + ") in node (" +
                                this + ") is in an unexpected state (" + counter.state + ").");
                }
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
            Counter<LeftTuple_> counter = deindexLeft(leftTuple, oldIndexProperties);

            counter.countRight = 0;
            tupleStore[inputStoreIndexLeft] = newIndexProperties;
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

    private Counter<LeftTuple_> deindexLeft(LeftTuple_ leftTuple, IndexProperties oldIndexProperties) {
        Counter<LeftTuple_> counter = indexerLeft.remove(oldIndexProperties, leftTuple);
        indexerRight.visit(oldIndexProperties, (rightTuple, counterSetRight) -> {
            boolean changed = counterSetRight.remove(counter);
            // If filtering is active, not all counterSets contain the counter and we don't track which ones do
            if (!changed && !isFiltering) {
                throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                        + ") with indexProperties (" + oldIndexProperties
                        + ") has a counter on the AB side that doesn't exist on the C side.");
            }
        });
        return counter;
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

        Counter<LeftTuple_> counter = deindexLeft(leftTuple, indexProperties);
        if (counter.isAlive()) {
            retractCounter(counter);
        }
    }

    @Override
    public final void insertRight(UniTuple<Right_> rightTuple) {
        Object[] tupleStore = rightTuple.getStore();
        if (tupleStore[inputStoreIndexRight] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + rightTuple
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = mappingRight.apply(rightTuple.getFactA());
        tupleStore[inputStoreIndexRight] = indexProperties;

        // TODO Maybe predict capacity with Math.max(16, counterMapA.size())
        Set<Counter<LeftTuple_>> counterSetRight = new LinkedHashSet<>();
        indexRight(rightTuple, indexProperties, counterSetRight);
    }

    private void indexRight(UniTuple<Right_> rightTuple, IndexProperties indexProperties,
            Set<Counter<LeftTuple_>> counterSetRight) {
        indexerRight.put(indexProperties, rightTuple, counterSetRight);
        indexerLeft.visit(indexProperties, (leftTuple, counter) -> {
            if (!isFiltering || testFiltering(leftTuple, rightTuple)) {
                if (counter.countRight == 0) {
                    if (shouldExist) {
                        insertCounter(counter);
                    } else {
                        retractCounter(counter);
                    }
                }
                counter.countRight++;
                counterSetRight.add(counter);
            }
        });
    }

    @Override
    public final void updateRight(UniTuple<Right_> rightTuple) {
        Object[] tupleStore = rightTuple.getStore();
        IndexProperties oldIndexProperties = (IndexProperties) tupleStore[inputStoreIndexRight];
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
                Set<Counter<LeftTuple_>> counterSetRight = indexerRight.get(oldIndexProperties, rightTuple);
                processAndClearCounters(counterSetRight);

                indexerLeft.visit(newIndexProperties, (leftTuple, counter) -> {
                    if (testFiltering(leftTuple, rightTuple)) {
                        if (counter.countRight == 0) {
                            if (shouldExist) {
                                insertOrUpdateCounter(counter);
                            } else {
                                retractOrRemainDeadCounter(counter);
                            }
                        }
                        counter.countRight++;
                        counterSetRight.add(counter);
                    }
                });
            }
        } else {
            Set<Counter<LeftTuple_>> counterSetRight = indexerRight.remove(oldIndexProperties, rightTuple);
            processAndClearCounters(counterSetRight);

            tupleStore[inputStoreIndexRight] = newIndexProperties;
            indexRight(rightTuple, newIndexProperties, counterSetRight);
        }
    }

    private void processAndClearCounters(Set<Counter<LeftTuple_>> counterSetRight) {
        processCounters(counterSetRight);
        counterSetRight.clear();
    }

    private void processCounters(Set<Counter<LeftTuple_>> counterSetRight) {
        for (Counter<LeftTuple_> counter : counterSetRight) {
            counter.countRight--;
            if (counter.countRight == 0) {
                if (shouldExist) {
                    retractCounter(counter);
                } else {
                    insertCounter(counter);
                }
            }
        }
    }

    @Override
    public final void retractRight(UniTuple<Right_> rightTuple) {
        Object[] tupleStore = rightTuple.getStore();
        IndexProperties indexProperties = (IndexProperties) tupleStore[inputStoreIndexRight];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleStore[inputStoreIndexRight] = null;
        Set<Counter<LeftTuple_>> counterSetRight = indexerRight.remove(indexProperties, rightTuple);
        processCounters(counterSetRight);
    }

    protected abstract IndexProperties createIndexProperties(LeftTuple_ leftTuple);

    protected abstract boolean testFiltering(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple);

    public static final class Counter<Tuple_ extends Tuple> {

        private static final IntPredicate ZERO_COUNT = count -> count == 0;
        private static final IntPredicate NON_ZERO_COUNT = count -> count > 0;

        private final Tuple_ leftTuple;
        private final IntPredicate alivePredicate;
        private BavetTupleState state = DEAD;
        private int countRight = 0;

        private Counter(Tuple_ leftTuple, boolean shouldExist) {
            this.leftTuple = leftTuple;
            this.alivePredicate = shouldExist ? NON_ZERO_COUNT : ZERO_COUNT;
        }

        private boolean isAlive() {
            return alivePredicate.test(countRight);
        }

        @Override
        public String toString() {
            return "Counter(" + leftTuple + ")";
        }
    }

    private void insertCounter(Counter<LeftTuple_> counter) {
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

    private void insertOrUpdateCounter(Counter<LeftTuple_> counter) {
        // Insert or update
        switch (counter.state) {
            case CREATING:
            case UPDATING:
                // Don't add the tuple to the dirtyTupleQueue twice
                break;
            case OK:
                counter.state = BavetTupleState.UPDATING;
                dirtyCounterQueue.add(counter);
                break;
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

    private void retractOrRemainDeadCounter(Counter<LeftTuple_> counter) {
        // Retract or remain dead
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
            case DYING:
            case DEAD:
            case ABORTING:
                // Don't add the tuple to the dirtyTupleQueue twice
                break;
            default:
                throw new IllegalStateException("Impossible state: The counter (" + counter
                        + ") has an impossible retract state (" + counter.state + ").");
        }
    }

    private void retractCounter(Counter<LeftTuple_> counter) {
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
        for (Counter<LeftTuple_> counter : dirtyCounterQueue) {
            switch (counter.state) {
                case CREATING:
                    nextNodesTupleLifecycle.insert(counter.leftTuple);
                    counter.state = BavetTupleState.OK;
                    break;
                case UPDATING:
                    nextNodesTupleLifecycle.update(counter.leftTuple);
                    counter.state = BavetTupleState.OK;
                    break;
                case DYING:
                    nextNodesTupleLifecycle.retract(counter.leftTuple);
                    counter.state = DEAD;
                    break;
                case ABORTING:
                    counter.state = DEAD;
                    break;
                case OK:
                case DEAD:
                default:
                    throw new IllegalStateException("Impossible state: The dirty counter (" + counter
                            + ") has an non-dirty state (" + counter.state + ").");
            }
        }
        dirtyCounterQueue.clear();
    }

}
