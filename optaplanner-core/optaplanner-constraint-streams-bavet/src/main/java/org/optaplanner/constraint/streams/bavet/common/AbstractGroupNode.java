package org.optaplanner.constraint.streams.bavet.common;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractGroupNode<InTuple_ extends Tuple, OutTuple_ extends Tuple, GroupKey_, ResultContainer_, Result_>
        extends AbstractNode
        implements TupleLifecycle<InTuple_> {

    private final int groupStoreIndex;
    private final Function<InTuple_, GroupKey_> groupKeyFunction;
    private final Supplier<ResultContainer_> supplier;
    private final Function<ResultContainer_, Result_> finisher;
    /**
     * Some code paths may decide to not supply a grouping function.
     * In that case, every tuple accumulates into {@link #singletonGroup} and not to {@link #groupMap}.
     */
    private final boolean hasMultipleGroups;
    /**
     * Some code paths may decide to not supply a collector.
     * In that case, we skip the code path that would attempt to use it.
     */
    private final boolean hasCollector;
    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)}, and/or ...
     */
    private final TupleLifecycle<OutTuple_> nextNodesTupleLifecycle;
    /**
     * Used when {@link #hasMultipleGroups} is true, otherwise {@link #singletonGroup} is used.
     */
    private final Map<GroupKey_, Group<OutTuple_, GroupKey_, ResultContainer_>> groupMap;
    /**
     * Used when {@link #hasMultipleGroups} is false, otherwise {@link #groupMap} is used.
     */
    private Group<OutTuple_, GroupKey_, ResultContainer_> singletonGroup;
    private final Queue<Group<OutTuple_, GroupKey_, ResultContainer_>> dirtyGroupQueue;

    protected AbstractGroupNode(int groupStoreIndex,
            Function<InTuple_, GroupKey_> groupKeyFunction,
            Supplier<ResultContainer_> supplier,
            Function<ResultContainer_, Result_> finisher,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle) {
        this.groupStoreIndex = groupStoreIndex;
        this.groupKeyFunction = groupKeyFunction;
        this.supplier = supplier;
        this.finisher = finisher;
        this.hasMultipleGroups = groupKeyFunction != null;
        this.hasCollector = supplier != null;
        this.nextNodesTupleLifecycle = nextNodesTupleLifecycle;
        this.groupMap = hasMultipleGroups ? new HashMap<>() : null;
        this.dirtyGroupQueue = new ArrayDeque<>();
    }

    @Override
    public void insert(InTuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        if (tupleStore[groupStoreIndex] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tuple
                    + ") was already added in the tupleStore.");
        }
        GroupKey_ groupKey = hasMultipleGroups ? groupKeyFunction.apply(tuple) : null;
        createTuple(tuple, tupleStore, groupKey);
    }

    private void createTuple(InTuple_ tuple, Object[] tupleStore, GroupKey_ newGroupKey) {
        Group<OutTuple_, GroupKey_, ResultContainer_> newGroup = getOrCreateGroup(newGroupKey);
        newGroup.parentCount++;
        Runnable undoAccumulator = hasCollector ? accumulate(newGroup.resultContainer, tuple) : null;
        GroupPart<Group<OutTuple_, GroupKey_, ResultContainer_>> newGroupPart = new GroupPart<>(newGroup, undoAccumulator);
        tupleStore[groupStoreIndex] = newGroupPart;

        OutTuple_ outTuple = newGroup.outTuple;
        switch (outTuple.getState()) {
            case CREATING:
            case UPDATING:
                break;
            case OK:
                outTuple.setState(BavetTupleState.UPDATING);
                dirtyGroupQueue.add(newGroup);
                break;
            case DYING:
                outTuple.setState(BavetTupleState.UPDATING);
                break;
            case ABORTING:
                outTuple.setState(BavetTupleState.CREATING);
                break;
            case DEAD:
            default:
                throw new IllegalStateException("Impossible state: The group (" + newGroup + ") in node (" +
                        this + ") is in an unexpected state (" + outTuple.getState() + ").");
        }
    }

    private Group<OutTuple_, GroupKey_, ResultContainer_> getOrCreateGroup(GroupKey_ key) {
        if (hasMultipleGroups) {
            return groupMap.computeIfAbsent(key, this::createGroup);
        } else {
            if (singletonGroup == null) {
                singletonGroup = createGroup(key);
            }
            return singletonGroup;
        }
    }

    private Group<OutTuple_, GroupKey_, ResultContainer_> createGroup(GroupKey_ key) {
        OutTuple_ outTuple = createOutTuple(key);
        outTuple.setState(BavetTupleState.CREATING);
        ResultContainer_ resultContainer = hasCollector ? supplier.get() : null;
        Group<OutTuple_, GroupKey_, ResultContainer_> group = new Group<>(key, resultContainer, outTuple);
        // Don't add it if (state == CREATING), but (newGroup != null), which is a 2nd insert of the same newGroupKey.
        dirtyGroupQueue.add(group);
        return group;
    }

    @Override
    public void update(InTuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        GroupPart<Group<OutTuple_, GroupKey_, ResultContainer_>> oldGroupPart =
                (GroupPart<Group<OutTuple_, GroupKey_, ResultContainer_>>) tupleStore[groupStoreIndex];
        if (oldGroupPart == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insert(tuple);
            return;
        }

        oldGroupPart.undoAccumulate();
        Group<OutTuple_, GroupKey_, ResultContainer_> oldGroup = oldGroupPart.group;
        GroupKey_ oldGroupKey = oldGroup.groupKey;
        GroupKey_ newGroupKey = hasMultipleGroups ? groupKeyFunction.apply(tuple) : null;
        if (Objects.equals(newGroupKey, oldGroupKey)) {
            // No need to change parentCount because it is the same group
            Runnable undoAccumulator = hasCollector ? accumulate(oldGroup.resultContainer, tuple) : null;
            GroupPart<Group<OutTuple_, GroupKey_, ResultContainer_>> newGroupPart = new GroupPart<>(oldGroup, undoAccumulator);
            tupleStore[groupStoreIndex] = newGroupPart;
            switch (oldGroup.outTuple.getState()) {
                case CREATING:
                case UPDATING:
                    break;
                case OK:
                    oldGroup.outTuple.setState(BavetTupleState.UPDATING);
                    dirtyGroupQueue.add(oldGroup);
                    break;
                case DYING:
                case ABORTING:
                case DEAD:
                default:
                    throw new IllegalStateException("Impossible state: The group (" + oldGroup + ") in node (" +
                            this + ") is in an unexpected state (" + oldGroup.outTuple.getState() + ").");
            }
        } else {
            killTuple(oldGroup);
            createTuple(tuple, tupleStore, newGroupKey);
        }
    }

    private void killTuple(Group<OutTuple_, GroupKey_, ResultContainer_> group) {
        group.parentCount--;
        boolean killGroup = (group.parentCount == 0);
        if (killGroup) {
            GroupKey_ groupKey = group.groupKey;
            Group<OutTuple_, GroupKey_, ResultContainer_> old = removeGroup(groupKey);
            if (old == null) {
                throw new IllegalStateException("Impossible state: the group for the groupKey ("
                        + groupKey + ") doesn't exist in the groupMap.");
            }
        }
        OutTuple_ outTuple = group.outTuple;
        switch (outTuple.getState()) {
            case CREATING:
                if (killGroup) {
                    outTuple.setState(BavetTupleState.ABORTING);
                }
                break;
            case UPDATING:
                if (killGroup) {
                    outTuple.setState(BavetTupleState.DYING);
                }
                break;
            case OK:
                outTuple.setState(killGroup ? BavetTupleState.DYING : BavetTupleState.UPDATING);
                dirtyGroupQueue.add(group);
                break;
            case DYING:
            case ABORTING:
            case DEAD:
            default:
                throw new IllegalStateException("Impossible state: The group (" + group + ") in node (" +
                        this + ") is in an unexpected state (" + outTuple.getState() + ").");
        }
    }

    private Group<OutTuple_, GroupKey_, ResultContainer_> removeGroup(GroupKey_ groupKey) {
        if (hasMultipleGroups) {
            return groupMap.remove(groupKey);
        } else {
            Group<OutTuple_, GroupKey_, ResultContainer_> old = singletonGroup;
            singletonGroup = null;
            return old;
        }
    }

    @Override
    public void retract(InTuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        GroupPart<Group<OutTuple_, GroupKey_, ResultContainer_>> groupPart =
                (GroupPart<Group<OutTuple_, GroupKey_, ResultContainer_>>) tupleStore[groupStoreIndex];
        if (groupPart == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleStore[groupStoreIndex] = null;
        Group<OutTuple_, GroupKey_, ResultContainer_> group = groupPart.group;
        groupPart.undoAccumulate();
        killTuple(group);
    }

    protected abstract Runnable accumulate(ResultContainer_ resultContainer, InTuple_ tuple);

    @Override
    public void calculateScore() {
        for (Group<OutTuple_, GroupKey_, ResultContainer_> group : dirtyGroupQueue) {
            OutTuple_ outTuple = group.outTuple;
            // Delay calculating finisher right until the tuple propagates
            switch (outTuple.getState()) {
                case CREATING:
                    updateOutTupleToFinisher(outTuple, group.resultContainer);
                    nextNodesTupleLifecycle.insert(outTuple);
                    outTuple.setState(BavetTupleState.OK);
                    break;
                case UPDATING:
                    updateOutTupleToFinisher(outTuple, group.resultContainer);
                    nextNodesTupleLifecycle.update(outTuple);
                    outTuple.setState(BavetTupleState.OK);
                    break;
                case DYING:
                    nextNodesTupleLifecycle.retract(outTuple);
                    outTuple.setState(BavetTupleState.DEAD);
                    break;
                case ABORTING:
                    outTuple.setState(BavetTupleState.DEAD);
                    break;
                case OK:
                case DEAD:
                default:
                    throw new IllegalStateException("Impossible state: The group (" + group + ") in node (" +
                            this + ") is in an unexpected state (" + group.outTuple.getState() + ").");
            }
        }
        dirtyGroupQueue.clear();
    }

    /**
     *
     * @param groupKey null if the node only has one group
     * @return never null
     */
    protected abstract OutTuple_ createOutTuple(GroupKey_ groupKey);

    private void updateOutTupleToFinisher(OutTuple_ outTuple, ResultContainer_ resultContainer) {
        if (finisher == null) {
            return;
        }
        Result_ result = finisher.apply(resultContainer);
        updateOutTupleToResult(outTuple, result);
    }

    protected abstract void updateOutTupleToResult(OutTuple_ outTuple, Result_ result);

}
