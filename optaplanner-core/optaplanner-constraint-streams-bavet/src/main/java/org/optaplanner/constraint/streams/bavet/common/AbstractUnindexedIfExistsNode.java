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
            TupleList<FilteringTracker<LeftTuple_>> leftTrackerList = new TupleList<>();
            rightTupleList.forEach(rightTuple -> updateCounterFromLeft(leftTuple, rightTuple, counter, leftTrackerList));
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
            TupleList<FilteringTracker<LeftTuple_>> leftTrackerList = leftTuple.getStore(inputStoreIndexLeftTrackerList);
            leftTrackerList.forEach(FilteringTracker::remove);
            counter.countRight = 0;
            rightTupleList.forEach(rightTuple -> updateCounterFromLeft(leftTuple, rightTuple, counter, leftTrackerList));
            updateCounterLeft(counter);
        }
    }

    @Override
    public final void retractLeft(LeftTuple_ leftTuple) {
        TupleListEntry<ExistsCounter<LeftTuple_>> counterEntry = leftTuple.removeStore(inputStoreIndexLeftCounterEntry);
        if (counterEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        ExistsCounter<LeftTuple_> counter = counterEntry.getElement();
        counterEntry.remove();
        if (isFiltering) {
            TupleList<FilteringTracker<LeftTuple_>> leftTrackerList = leftTuple.getStore(inputStoreIndexLeftTrackerList);
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
            TupleList<FilteringTracker<LeftTuple_>> rightTrackerList = new TupleList<>();
            leftCounterList.forEach(counter -> updateCounterFromRight(rightTuple, counter, rightTrackerList));
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
            TupleList<FilteringTracker<LeftTuple_>> rightTrackerList = updateRightTrackerList(rightTuple);
            leftCounterList.forEach(counter -> updateCounterFromRight(rightTuple, counter, rightTrackerList));
        }
    }

    @Override
    public final void retractRight(UniTuple<Right_> rightTuple) {
        TupleListEntry<UniTuple<Right_>> rightEntry = rightTuple.removeStore(inputStoreIndexRightEntry);
        if (rightEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        rightEntry.remove();
        if (!isFiltering) {
            leftCounterList.forEach(this::decrementCounterRight);
        } else {
            updateRightTrackerList(rightTuple);
        }
    }

}
