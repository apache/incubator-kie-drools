package org.optaplanner.constraint.streams.bavet.common;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.impl.util.FieldBasedScalingSet;

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

    private final Map<LeftTuple_, ExistsCounter<LeftTuple_>> leftMap = new LinkedHashMap<>();
    private final Map<UniTuple<Right_>, Set<ExistsCounter<LeftTuple_>>> rightMap = new LinkedHashMap<>();

    protected AbstractUnindexedIfExistsNode(boolean shouldExist, TupleLifecycle<LeftTuple_> nextNodeTupleLifecycle,
            boolean isFiltering) {
        super(shouldExist, nextNodeTupleLifecycle, isFiltering);
    }

    @Override
    public final void insertLeft(LeftTuple_ leftTuple) {
        ExistsCounter<LeftTuple_> counter = new ExistsCounter<>(leftTuple, shouldExist);
        leftMap.put(leftTuple, counter);
        for (Map.Entry<UniTuple<Right_>, Set<ExistsCounter<LeftTuple_>>> entry : rightMap.entrySet()) {
            UniTuple<Right_> rightTuple = entry.getKey();
            if (!isFiltering || testFiltering(leftTuple, rightTuple)) {
                counter.countRight++;
                Set<ExistsCounter<LeftTuple_>> counterSetRight = entry.getValue();
                counterSetRight.add(counter);
            }
        }
        if (counter.isAlive()) {
            counter.state = BavetTupleState.CREATING;
            dirtyCounterQueue.add(counter);
        }
    }

    @Override
    public final void updateLeft(LeftTuple_ leftTuple) {
        ExistsCounter<LeftTuple_> counter = leftMap.get(leftTuple);
        if (counter == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertLeft(leftTuple);
            return;
        }
        // The indexers contain counters in the DEAD state, to track the rightCount.
        if (!isFiltering) {
            processCounterUpdate(counter);
        } else {
            // Call filtering for the leftTuple and rightTuple combinations again
            counter.countRight = 0;
            for (Map.Entry<UniTuple<Right_>, Set<ExistsCounter<LeftTuple_>>> entry : rightMap.entrySet()) {
                UniTuple<Right_> rightTuple = entry.getKey();
                if (testFiltering(leftTuple, rightTuple)) {
                    counter.countRight++;
                } else {
                    Set<ExistsCounter<LeftTuple_>> counterSetRight = entry.getValue();
                    counterSetRight.remove(counter);
                }
            }
            if (counter.isAlive()) {
                // Insert or update
                insertOrUpdateCounter(counter);
            } else {
                // Retract or remain dead
                retractOrRemainDeadCounter(counter);
            }
        }
    }

    @Override
    public final void retractLeft(LeftTuple_ leftTuple) {
        ExistsCounter<LeftTuple_> counter = leftMap.remove(leftTuple);
        if (counter == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        for (Set<ExistsCounter<LeftTuple_>> counterSetRight : rightMap.values()) {
            boolean changed = counterSetRight.remove(counter);
            // If filtering is active, not all counterSets contain the counter and we don't track which ones do
            if (!changed && !isFiltering) {
                throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                        + ") has a counter on the left side that doesn't exist on the right side.");
            }
        }
        if (counter.isAlive()) {
            retractCounter(counter);
        }
    }

    @Override
    public final void insertRight(UniTuple<Right_> rightTuple) {
        Set<ExistsCounter<LeftTuple_>> counterSetRight = new FieldBasedScalingSet<>(LinkedHashSet::new);
        rightMap.put(rightTuple, counterSetRight);
        for (Map.Entry<LeftTuple_, ExistsCounter<LeftTuple_>> entry : leftMap.entrySet()) {
            LeftTuple_ leftTuple = entry.getKey();
            ExistsCounter<LeftTuple_> counter = entry.getValue();
            processInsert(leftTuple, rightTuple, counter, counterSetRight);
        }
    }

    @Override
    public final void updateRight(UniTuple<Right_> rightTuple) {
        Set<ExistsCounter<LeftTuple_>> counterSetRight = rightMap.get(rightTuple);
        if (counterSetRight == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertRight(rightTuple);
        } else if (isFiltering) {
            // Call filtering for the leftTuple and rightTuple combinations again
            processAndClearCounters(counterSetRight);
            for (Map.Entry<LeftTuple_, ExistsCounter<LeftTuple_>> entry : leftMap.entrySet()) {
                LeftTuple_ leftTuple = entry.getKey();
                ExistsCounter<LeftTuple_> counter = entry.getValue();
                processUpdate(leftTuple, rightTuple, counter, counterSetRight);
            }
        }
    }

    @Override
    public final void retractRight(UniTuple<Right_> rightTuple) {
        Set<ExistsCounter<LeftTuple_>> counterSetRight = rightMap.remove(rightTuple);
        if (counterSetRight == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        processCounters(counterSetRight);
    }

}
