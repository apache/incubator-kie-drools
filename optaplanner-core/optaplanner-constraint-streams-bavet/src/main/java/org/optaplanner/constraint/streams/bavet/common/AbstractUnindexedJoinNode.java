package org.optaplanner.constraint.streams.bavet.common;

import java.util.IdentityHashMap;
import java.util.Map;

import org.optaplanner.constraint.streams.bavet.common.collection.TupleList;
import org.optaplanner.constraint.streams.bavet.common.collection.TupleListEntry;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

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

    private final int inputStoreIndexLeftEntry;
    private final int inputStoreIndexRightEntry;

    private final TupleList<LeftTuple_> leftTupleList = new TupleList<>();
    private final TupleList<UniTuple<Right_>> rightTupleList = new TupleList<>();

    protected AbstractUnindexedJoinNode(
            int inputStoreIndexLeftEntry, int inputStoreIndexLeftOutTupleList,
            int inputStoreIndexRightEntry, int inputStoreIndexRightOutTupleList,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle, boolean isFiltering,
            int outputStoreIndexLeftOutEntry, int outputStoreIndexRightOutEntry) {
        super(inputStoreIndexLeftOutTupleList, inputStoreIndexRightOutTupleList,
                nextNodesTupleLifecycle, isFiltering,
                outputStoreIndexLeftOutEntry, outputStoreIndexRightOutEntry);
        this.inputStoreIndexLeftEntry = inputStoreIndexLeftEntry;
        this.inputStoreIndexRightEntry = inputStoreIndexRightEntry;
    }

    @Override
    public final void insertLeft(LeftTuple_ leftTuple) {
        if (leftTuple.getStore(inputStoreIndexLeftEntry) != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + leftTuple
                    + ") was already added in the tupleStore.");
        }
        TupleListEntry<LeftTuple_> leftEntry = leftTupleList.add(leftTuple);
        leftTuple.setStore(inputStoreIndexLeftEntry, leftEntry);
        TupleList<MutableOutTuple_> outTupleListLeft = new TupleList<>();
        leftTuple.setStore(inputStoreIndexLeftOutTupleList, outTupleListLeft);
        rightTupleList.forEach(rightTuple -> {
            if (!isFiltering || testFiltering(leftTuple, rightTuple)) {
                insertOutTuple(leftTuple, rightTuple);
            }
        });
    }

    @Override
    public final void updateLeft(LeftTuple_ leftTuple) {
        TupleListEntry<LeftTuple_> leftEntry = leftTuple.getStore(inputStoreIndexLeftEntry);
        if (leftEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertLeft(leftTuple);
            return;
        }
        // Prefer an update over retract-insert if possible
        TupleList<MutableOutTuple_> outTupleListLeft = leftTuple.getStore(inputStoreIndexLeftOutTupleList);
        if (!isFiltering) {
            // Propagate the update for downstream filters, matchWeighers, ...
            outTupleListLeft.forEach(outTuple -> updateOutTupleLeft(outTuple, leftTuple));
        } else {
            // Hack: the outTuple has no left/right input tuple reference, use the left/right outList reference instead
            Map<TupleList<MutableOutTuple_>, MutableOutTuple_> rightToOutMap = new IdentityHashMap<>(outTupleListLeft.size());
            outTupleListLeft.forEach(outTuple -> {
                TupleListEntry<MutableOutTuple_> rightOutEntry = outTuple.getStore(outputStoreIndexRightOutEntry);
                rightToOutMap.put(rightOutEntry.getList(), outTuple);
            });
            rightTupleList.forEach((rightTuple) -> {
                TupleList<MutableOutTuple_> rightOutList = rightTuple.getStore(inputStoreIndexRightOutTupleList);
                MutableOutTuple_ outTuple = rightToOutMap.get(rightOutList);
                if (testFiltering(leftTuple, rightTuple)) {
                    if (outTuple == null) {
                        insertOutTuple(leftTuple, rightTuple);
                    } else {
                        updateOutTupleLeft(outTuple, leftTuple);
                    }
                } else {
                    if (outTuple != null) {
                        retractOutTuple(outTuple);
                    }
                }
            });
        }
    }

    @Override
    public final void retractLeft(LeftTuple_ leftTuple) {
        TupleListEntry<LeftTuple_> leftEntry = leftTuple.removeStore(inputStoreIndexLeftEntry);
        if (leftEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        TupleList<MutableOutTuple_> outTupleListLeft = leftTuple.removeStore(inputStoreIndexLeftOutTupleList);
        leftEntry.remove();
        outTupleListLeft.forEach(this::retractOutTuple);
    }

    @Override
    public final void insertRight(UniTuple<Right_> rightTuple) {
        if (rightTuple.getStore(inputStoreIndexRightEntry) != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + rightTuple
                    + ") was already added in the tupleStore.");
        }
        TupleListEntry<UniTuple<Right_>> rightEntry = rightTupleList.add(rightTuple);
        rightTuple.setStore(inputStoreIndexRightEntry, rightEntry);
        TupleList<MutableOutTuple_> outTupleListRight = new TupleList<>();
        rightTuple.setStore(inputStoreIndexRightOutTupleList, outTupleListRight);
        leftTupleList.forEach(leftTuple -> {
            if (!isFiltering || testFiltering(leftTuple, rightTuple)) {
                insertOutTuple(leftTuple, rightTuple);
            }
        });
    }

    @Override
    public final void updateRight(UniTuple<Right_> rightTuple) {
        TupleListEntry<UniTuple<Right_>> rightEntry = rightTuple.getStore(inputStoreIndexRightEntry);
        if (rightEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insertRight(rightTuple);
            return;
        }
        // Prefer an update over retract-insert if possible
        TupleList<MutableOutTuple_> outTupleListRight = rightTuple.getStore(inputStoreIndexRightOutTupleList);
        if (!isFiltering) {
            // Propagate the update for downstream filters, matchWeighers, ...
            outTupleListRight.forEach(outTuple -> updateOutTupleRight(outTuple, rightTuple));
        } else {
            // Hack: the outTuple has no left/right input tuple reference, use the left/right outList reference instead
            Map<TupleList<MutableOutTuple_>, MutableOutTuple_> leftToOutMap = new IdentityHashMap<>(outTupleListRight.size());
            outTupleListRight.forEach(outTuple -> {
                TupleListEntry<MutableOutTuple_> leftOutEntry = outTuple.getStore(outputStoreIndexLeftOutEntry);
                leftToOutMap.put(leftOutEntry.getList(), outTuple);
            });
            leftTupleList.forEach((leftTuple) -> {
                TupleList<MutableOutTuple_> leftOutList = leftTuple.getStore(inputStoreIndexLeftOutTupleList);
                MutableOutTuple_ outTuple = leftToOutMap.get(leftOutList);
                if (testFiltering(leftTuple, rightTuple)) {
                    if (outTuple == null) {
                        insertOutTuple(leftTuple, rightTuple);
                    } else {
                        updateOutTupleRight(outTuple, rightTuple);
                    }
                } else {
                    if (outTuple != null) {
                        retractOutTuple(outTuple);
                    }
                }
            });
        }
    }

    @Override
    public final void retractRight(UniTuple<Right_> rightTuple) {
        TupleListEntry<UniTuple<Right_>> rightEntry = rightTuple.removeStore(inputStoreIndexRightEntry);
        if (rightEntry == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        TupleList<MutableOutTuple_> outTupleListRight = rightTuple.removeStore(inputStoreIndexRightOutTupleList);
        rightEntry.remove();
        outTupleListRight.forEach(this::retractOutTuple);
    }

}
