package org.optaplanner.constraint.streams.bavet.common;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.impl.util.FieldBasedScalingMap;

/**
 * There is a strong likelihood that any change made to this class
 * should also be made to {@link AbstractIndexedJoinNode}.
 *
 * @param <LeftTuple_>
 * @param <Right_>
 */
public abstract class AbstractUnindexedJoinNode<LeftTuple_ extends Tuple, Right_, OutTuple_ extends Tuple, MutableOutTuple_ extends OutTuple_>
        extends AbstractJoinNode<LeftTuple_, Right_, OutTuple_, MutableOutTuple_>
        implements LeftTupleLifecycle<LeftTuple_>, RightTupleLifecycle<UniTuple<Right_>> {

    private final Map<LeftTuple_, Map<UniTuple<Right_>, MutableOutTuple_>> leftToRightMap = new LinkedHashMap<>();
    private final Set<UniTuple<Right_>> rightSet = new LinkedHashSet<>();

    protected AbstractUnindexedJoinNode(TupleLifecycle<OutTuple_> nextNodesTupleLifecycle) {
        super(nextNodesTupleLifecycle);
    }

    @Override
    public final void insertLeft(LeftTuple_ leftTuple) {
        Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = new FieldBasedScalingMap<>(LinkedHashMap::new);
        leftToRightMap.put(leftTuple, outTupleMapLeft);
        for (UniTuple<Right_> rightTuple : rightSet) {
            insertTuple(outTupleMapLeft, leftTuple, rightTuple);
        }
    }

    @Override
    public final void updateLeft(LeftTuple_ leftTuple) {
        Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = leftToRightMap.get(leftTuple);
        if (outTupleMapLeft == null) { // We don't track which tuples made it through the filter predicate(s).
            insertLeft(leftTuple);
            return;
        }
        for (MutableOutTuple_ outTuple : outTupleMapLeft.values()) {
            updateOutTupleLeft(outTuple, leftTuple);
            updateTuple(outTuple);
        }
    }

    @Override
    public final void retractLeft(LeftTuple_ leftTuple) {
        Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = leftToRightMap.remove(leftTuple);
        if (outTupleMapLeft == null) { // We don't track which tuples made it through the filter predicate(s).
            return;
        }
        for (OutTuple_ outTuple : outTupleMapLeft.values()) {
            retractTuple(outTuple);
        }
    }

    @Override
    public final void insertRight(UniTuple<Right_> rightTuple) {
        rightSet.add(rightTuple);
        for (Map.Entry<LeftTuple_, Map<UniTuple<Right_>, MutableOutTuple_>> entry : leftToRightMap.entrySet()) {
            LeftTuple_ leftTuple = entry.getKey();
            Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = entry.getValue();
            insertTuple(outTupleMapLeft, leftTuple, rightTuple);
        }
    }

    @Override
    public final void updateRight(UniTuple<Right_> rightTuple) {
        if (!rightSet.contains(rightTuple)) { // We don't track which tuples made it through the filter predicate(s).
            insertRight(rightTuple);
            return;
        }
        for (Map.Entry<LeftTuple_, Map<UniTuple<Right_>, MutableOutTuple_>> entry : leftToRightMap.entrySet()) {
            LeftTuple_ leftTuple = entry.getKey();
            Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = entry.getValue();
            MutableOutTuple_ outTuple = outTupleMapLeft.get(rightTuple);
            updateOutTupleRight(outTuple, rightTuple);
            if (outTuple == null) {
                throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                        + ") has tuples on the right side that didn't exist on the left side.");
            }
            updateTuple(outTuple);
        }
    }

    @Override
    public final void retractRight(UniTuple<Right_> rightTuple) {
        boolean removed = rightSet.remove(rightTuple);
        if (!removed) {
            // We don't track which tuples made it through the filter predicate(s).
            return;
        }
        for (Map.Entry<LeftTuple_, Map<UniTuple<Right_>, MutableOutTuple_>> entry : leftToRightMap.entrySet()) {
            LeftTuple_ leftTuple = entry.getKey();
            Map<UniTuple<Right_>, MutableOutTuple_> outTupleMapLeft = entry.getValue();
            OutTuple_ outTuple = outTupleMapLeft.remove(rightTuple);
            if (outTuple == null) {
                throw new IllegalStateException("Impossible state: the tuple (" + leftTuple
                        + ") has tuples on the right side that didn't exist on the left side.");
            }
            retractTuple(outTuple);
        }
    }

}
