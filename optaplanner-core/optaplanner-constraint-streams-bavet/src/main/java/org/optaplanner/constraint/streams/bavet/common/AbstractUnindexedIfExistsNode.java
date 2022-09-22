package org.optaplanner.constraint.streams.bavet.common;

import org.optaplanner.constraint.streams.bavet.common.collection.TupleList;
import org.optaplanner.constraint.streams.bavet.common.collection.TupleListEntry;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

/**
 * There is a strong likelihood that any change made to this class
 * should also be made to {@link AbstractIndexedIfExistsNode}.
 *
 * @param <LeftTuple_>
 * @param <Right_>
 */
public abstract class AbstractUnindexedIfExistsNode<LeftTuple_ extends Tuple, Right_>
        extends AbstractIfExistsNode<LeftTuple_, Right_>
        implements LeftTupleLifecycle<LeftTuple_>, RightTupleLifecycle<UniTuple<Right_>> {

    private final int inputStoreIndexLeftCounterEntry;

    private final int inputStoreIndexRightEntry;

    // Acts as a leftTupleList too
    private final TupleList<ExistsCounter<LeftTuple_>> leftCounterList = new TupleList<>();
    private final TupleList<UniTuple<Right_>> rightTupleList = new TupleList<>();

    protected AbstractUnindexedIfExistsNode(boolean shouldExist,
            int inputStoreIndexLeftCounterEntry, int inputStoreIndexLeftTrackerList,
            int inputStoreIndexRightEntry, int inputStoreIndexRightTrackerList,
            TupleLifecycle<LeftTuple_> nextNodesTupleLifecycle,
            boolean isFiltering) {
        super(shouldExist, inputStoreIndexLeftTrackerList, inputStoreIndexRightTrackerList,
                nextNodesTupleLifecycle, isFiltering);
        this.inputStoreIndexLeftCounterEntry = inputStoreIndexLeftCounterEntry;
        this.inputStoreIndexRightEntry = inputStoreIndexRightEntry;
    }

    @Override
    public final void insertLeft(LeftTuple_ leftTuple) {
        if (leftTuple.getStore(inputStoreIndexLeftCounterEntry) != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + leftTuple
                    + ") was already added in the tupleStore.");
        }
        ExistsCounter<LeftTuple_> counter = new ExistsCounter<>(leftTuple);
        TupleListEntry<ExistsCounter<LeftTuple_>> counterEntry = leftCounterList.add(counter);
        leftTuple.setStore(inputStoreIndexLeftCounterEntry, counterEntry);

        if (!isFiltering) {
            counter.countRight = rightTupleList.size();
        } else {
            TupleList<FilteringTracker> leftTrackerList = new TupleList<>();
            rightTupleList.forEach(rightTuple -> {
                if (testFiltering(leftTuple, rightTuple)) {
                    counter.countRight++;
                    TupleList<FilteringTracker> rightTrackerList = rightTuple.getStore(inputStoreIndexRightTrackerList);
                    new FilteringTracker(counter, leftTrackerList, rightTrackerList);
                }
            });
            leftTuple.setStore(inputStoreIndexLeftTrackerList, leftTrackerList);
        }
        initCounterLeft(counter);
    }

    @Override
    public final void updateLeft(LeftTuple_ leftTuple) {
        TupleListEntry<ExistsCounter<LeftTuple_>> counterEntry = leftTuple.getStore(inputStoreIndexLeftCounterEntry);
        if (counterEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertLeft(leftTuple);
            return;
        }
        ExistsCounter<LeftTuple_> counter = counterEntry.getElement();
        // The indexers contain counters in the DEAD state, to track the rightCount.
        if (!isFiltering) {
            updateUnchangedCounterLeft(counter);
        } else {
            // Call filtering for the leftTuple and rightTuple combinations again
            TupleList<FilteringTracker> leftTrackerList = leftTuple.getStore(inputStoreIndexLeftTrackerList);
            leftTrackerList.forEach(FilteringTracker::remove);
            counter.countRight = 0;
            rightTupleList.forEach(rightTuple -> {
                if (testFiltering(leftTuple, rightTuple)) {
                    counter.countRight++;
                    TupleList<FilteringTracker> rightTrackerList = rightTuple.getStore(inputStoreIndexRightTrackerList);
                    new FilteringTracker(counter, leftTrackerList, rightTrackerList);
                }
            });
            updateCounterLeft(counter);
        }
    }

    @Override
    public final void retractLeft(LeftTuple_ leftTuple) {
        TupleListEntry<ExistsCounter<LeftTuple_>> counterEntry = leftTuple.getStore(inputStoreIndexLeftCounterEntry);
        if (counterEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        ExistsCounter<LeftTuple_> counter = counterEntry.getElement();
        counterEntry.remove();
        if (isFiltering) {
            TupleList<FilteringTracker> leftTrackerList = leftTuple.getStore(inputStoreIndexLeftTrackerList);
            leftTrackerList.forEach(FilteringTracker::remove);
        }
        killCounterLeft(counter);
    }

    @Override
    public final void insertRight(UniTuple<Right_> rightTuple) {
        if (rightTuple.getStore(inputStoreIndexRightEntry) != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + rightTuple
                    + ") was already added in the tupleStore.");
        }
        TupleListEntry<UniTuple<Right_>> rightEntry = rightTupleList.add(rightTuple);
        rightTuple.setStore(inputStoreIndexRightEntry, rightEntry);
        if (!isFiltering) {
            leftCounterList.forEach(this::incrementCounterRight);
        } else {
            TupleList<FilteringTracker> rightTrackerList = new TupleList<>();
            leftCounterList.forEach(counter -> {
                if (testFiltering(counter.leftTuple, rightTuple)) {
                    incrementCounterRight(counter);
                    TupleList<FilteringTracker> leftTrackerList = counter.leftTuple.getStore(inputStoreIndexLeftTrackerList);
                    new FilteringTracker(counter, leftTrackerList, rightTrackerList);
                }
            });
            rightTuple.setStore(inputStoreIndexRightTrackerList, rightTrackerList);
        }
    }

    @Override
    public final void updateRight(UniTuple<Right_> rightTuple) {
        TupleListEntry<UniTuple<Right_>> rightEntry = rightTuple.getStore(inputStoreIndexRightEntry);
        if (rightEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertRight(rightTuple);
            return;
        }
        if (isFiltering) {
            TupleList<FilteringTracker> rightTrackerList = rightTuple.getStore(inputStoreIndexRightTrackerList);
            rightTrackerList.forEach(filteringTacker -> {
                decrementCounterRight(filteringTacker.counter);
                filteringTacker.remove();
            });
            leftCounterList.forEach(counter -> {
                if (testFiltering(counter.leftTuple, rightTuple)) {
                    incrementCounterRight(counter);
                    TupleList<FilteringTracker> leftTrackerList = counter.leftTuple.getStore(inputStoreIndexLeftTrackerList);
                    new FilteringTracker(counter, leftTrackerList, rightTrackerList);
                }
            });
        }
    }

    @Override
    public final void retractRight(UniTuple<Right_> rightTuple) {
        TupleListEntry<UniTuple<Right_>> rightEntry = rightTuple.getStore(inputStoreIndexRightEntry);
        if (rightEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        rightEntry.remove();
        rightTuple.setStore(inputStoreIndexRightEntry, null);
        if (!isFiltering) {
            leftCounterList.forEach(this::decrementCounterRight);
        } else {
            TupleList<FilteringTracker> rightTrackerList = rightTuple.getStore(inputStoreIndexRightTrackerList);
            rightTrackerList.forEach(filteringTacker -> {
                decrementCounterRight(filteringTacker.counter);
                filteringTacker.remove();
            });
        }
    }

}
