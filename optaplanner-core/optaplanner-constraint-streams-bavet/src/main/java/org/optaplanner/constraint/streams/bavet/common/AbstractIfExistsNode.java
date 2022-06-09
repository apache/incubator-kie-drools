package org.optaplanner.constraint.streams.bavet.common;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

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

    protected AbstractIfExistsNode(boolean shouldExist,
            Function<Right_, IndexProperties> mappingRight,
            int inputStoreIndexLeft, int inputStoreIndexRight,
            TupleLifecycle<LeftTuple_> nextNodeTupleLifecycle,
            Indexer<LeftTuple_, Counter<LeftTuple_>> indexerLeft,
            Indexer<UniTuple<Right_>, Set<Counter<LeftTuple_>>> indexerRight) {
        this.shouldExist = shouldExist;
        this.mappingRight = mappingRight;
        this.inputStoreIndexLeft = inputStoreIndexLeft;
        this.inputStoreIndexRight = inputStoreIndexRight;
        this.nextNodesTupleLifecycle = nextNodeTupleLifecycle;
        this.indexerLeft = indexerLeft;
        this.indexerRight = indexerRight;
        dirtyCounterQueue = new ArrayDeque<>(1000);
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

        Counter<LeftTuple_> counter = new Counter<>(leftTuple);
        indexerLeft.put(indexProperties, leftTuple, counter);

        counter.countRight = 0;
        indexerRight.visit(indexProperties, (rightTuple, counterSetRight) -> {
            if (!isFiltering() || isFiltered(leftTuple, rightTuple)) {
                counter.countRight++;
                counterSetRight.add(counter);
            }
        });
        if (shouldExist ? counter.countRight > 0 : counter.countRight == 0) {
            counter.state = BavetTupleState.CREATING;
            dirtyCounterQueue.add(counter);
        }
    }

    @Override
    public final void updateLeft(LeftTuple_ leftTuple) {
        // TODO Implement actual update
        retractLeft(leftTuple);
        insertLeft(leftTuple);
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

        Counter<LeftTuple_> counter = indexerLeft.remove(indexProperties, leftTuple);
        indexerRight.visit(indexProperties, (rightTuple, counterSetRight) -> {
            boolean changed = counterSetRight.remove(counter);
            // If filtering is active, not all counterSets contain the counter and we don't track which ones do
            if (!changed && !isFiltering()) {
                throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                        + ") with indexProperties (" + indexProperties
                        + ") has a counter on the AB side that doesn't exist on the C side.");
            }
        });
        if (shouldExist ? counter.countRight > 0 : counter.countRight == 0) {
            retractCounter(counter);
        }
    }

    @Override
    public final void updateRight(UniTuple<Right_> rightTuple) {
        // TODO Implement actual update
        retractRight(rightTuple);
        insertRight(rightTuple);
    }

    @Override
    public final void insertRight(UniTuple<Right_> rightTuple) {
        if (rightTuple.store[inputStoreIndexRight] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + rightTuple
                    + ") was already added in the tupleStore.");
        }
        IndexProperties indexProperties = mappingRight.apply(rightTuple.factA);
        rightTuple.store[inputStoreIndexRight] = indexProperties;

        // TODO Maybe predict capacity with Math.max(16, counterMapA.size())
        Set<Counter<LeftTuple_>> counterSetRight = new LinkedHashSet<>();
        indexerRight.put(indexProperties, rightTuple, counterSetRight);
        indexerLeft.visit(indexProperties, (leftTuple, counter) -> {
            if (!isFiltering() || isFiltered(leftTuple, rightTuple)) {
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
    public final void retractRight(UniTuple<Right_> rightTuple) {
        Object[] tupleStore = rightTuple.store;
        IndexProperties indexProperties = (IndexProperties) tupleStore[inputStoreIndexRight];
        if (indexProperties == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleStore[inputStoreIndexRight] = null;
        Set<Counter<LeftTuple_>> counterSetRight = indexerRight.remove(indexProperties, rightTuple);
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

    protected abstract IndexProperties createIndexProperties(LeftTuple_ leftTuple);

    protected abstract boolean isFiltering();

    protected abstract boolean isFiltered(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple);

    public static final class Counter<Tuple_ extends Tuple> {
        public final Tuple_ leftTuple;
        public BavetTupleState state = BavetTupleState.DEAD;
        public int countRight = 0;

        public Counter(Tuple_ leftTuple) {
            this.leftTuple = leftTuple;
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
        dirtyCounterQueue.forEach(counter -> {
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
                    counter.state = BavetTupleState.DEAD;
                    break;
                case ABORTING:
                    counter.state = BavetTupleState.DEAD;
                    break;
                case OK:
                case DEAD:
                default:
                    throw new IllegalStateException("Impossible state: The dirty counter (" + counter
                            + ") has an non-dirty state (" + counter.state + ").");
            }
        });
        dirtyCounterQueue.clear();
    }

}
