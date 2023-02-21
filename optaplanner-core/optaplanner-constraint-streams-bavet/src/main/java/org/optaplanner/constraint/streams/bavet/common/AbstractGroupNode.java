package org.optaplanner.constraint.streams.bavet.common;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractGroupNode<InTuple_ extends Tuple, OutTuple_ extends Tuple, MutableOutTuple_ extends OutTuple_, GroupKey_, ResultContainer_, Result_>
        extends AbstractNode
        implements TupleLifecycle<InTuple_> {

    private final int groupStoreIndex;
    /**
     * Unused when {@link #hasCollector} is false.
     */
    private final int undoStoreIndex;
    /**
     * Unused when {@link #hasMultipleGroups} is false.
     */
    private final Function<InTuple_, GroupKey_> groupKeyFunction;
    /**
     * Unused when {@link #hasCollector} is false.
     */
    private final Supplier<ResultContainer_> supplier;
    /**
     * Unused when {@link #hasCollector} is false.
     */
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
    private final Map<GroupKey_, AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_>> groupMap;
    /**
     * Used when {@link #hasMultipleGroups} is false, otherwise {@link #groupMap} is used.
     *
     * The field is lazy initialized in order to maintain the same semantics as with the groupMap above.
     * When all tuples are removed, the field will be set to null, as if the group never existed.
     */
    private AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> singletonGroup;
    private final Queue<AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_>> dirtyGroupQueue;

    protected AbstractGroupNode(int groupStoreIndex, int undoStoreIndex, Function<InTuple_, GroupKey_> groupKeyFunction,
            Supplier<ResultContainer_> supplier, Function<ResultContainer_, Result_> finisher,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle) {
        this.groupStoreIndex = groupStoreIndex;
        this.undoStoreIndex = undoStoreIndex;
        this.groupKeyFunction = groupKeyFunction;
        this.supplier = supplier;
        this.finisher = finisher;
        this.hasMultipleGroups = groupKeyFunction != null;
        this.hasCollector = supplier != null;
        this.nextNodesTupleLifecycle = nextNodesTupleLifecycle;
        /*
         * Not using the default sizing to 1000.
         * The number of groups can be very small, and that situation is not unlikely.
         * Therefore, the size of these collections is kept default.
         */
        this.groupMap = hasMultipleGroups ? new HashMap<>() : null;
        this.dirtyGroupQueue = new ArrayDeque<>();
    }

    protected AbstractGroupNode(int groupStoreIndex, Function<InTuple_, GroupKey_> groupKeyFunction,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle) {
        this(groupStoreIndex, -1, groupKeyFunction, null, null, nextNodesTupleLifecycle);
    }

    @Override
    public void insert(InTuple_ tuple) {
        if (tuple.getStore(groupStoreIndex) != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tuple
                    + ") was already added in the tupleStore.");
        }
        GroupKey_ groupKey = hasMultipleGroups ? groupKeyFunction.apply(tuple) : null;
        createTuple(tuple, groupKey);
    }

    private void createTuple(InTuple_ tuple, GroupKey_ newGroupKey) {
        AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> newGroup = getOrCreateGroup(newGroupKey);
        OutTuple_ outTuple = accumulate(tuple, newGroup);
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

    private OutTuple_ accumulate(InTuple_ tuple, AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> group) {
        if (hasCollector) {
            Runnable undoAccumulator = accumulate(group.getResultContainer(), tuple);
            tuple.setStore(undoStoreIndex, undoAccumulator);
        }
        tuple.setStore(groupStoreIndex, group);
        return group.outTuple;
    }

    private AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> getOrCreateGroup(GroupKey_ key) {
        if (hasMultipleGroups) {
            // Avoids computeIfAbsent in order to not create lambdas on the hot path.
            AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> group = groupMap.get(key);
            if (group == null) {
                group = createGroup(key);
                groupMap.put(key, group);
            } else {
                group.parentCount++;
            }
            return group;
        } else {
            if (singletonGroup == null) {
                singletonGroup = createGroup(key);
            } else {
                singletonGroup.parentCount++;
            }
            return singletonGroup;
        }
    }

    private AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> createGroup(GroupKey_ key) {
        MutableOutTuple_ outTuple = createOutTuple(key);
        AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> group =
                hasCollector ? new GroupWithAccumulate<>(key, supplier.get(), outTuple)
                        : new GroupWithoutAccumulate<>(key, outTuple);
        // Don't add it if (state == CREATING), but (newGroup != null), which is a 2nd insert of the same newGroupKey.
        dirtyGroupQueue.add(group);
        return group;
    }

    @Override
    public void update(InTuple_ tuple) {
        AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> oldGroup = tuple.getStore(groupStoreIndex);
        if (oldGroup == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insert(tuple);
            return;
        }
        if (hasCollector) {
            Runnable undoAccumulator = tuple.getStore(undoStoreIndex);
            undoAccumulator.run();
        }

        GroupKey_ oldGroupKey = oldGroup.groupKey;
        GroupKey_ newGroupKey = hasMultipleGroups ? groupKeyFunction.apply(tuple) : null;
        if (Objects.equals(newGroupKey, oldGroupKey)) {
            // No need to change parentCount because it is the same group
            OutTuple_ outTuple = accumulate(tuple, oldGroup);
            switch (outTuple.getState()) {
                case CREATING:
                case UPDATING:
                    break;
                case OK:
                    outTuple.setState(BavetTupleState.UPDATING);
                    dirtyGroupQueue.add(oldGroup);
                    break;
                case DYING:
                case ABORTING:
                case DEAD:
                default:
                    throw new IllegalStateException("Impossible state: The group (" + oldGroup + ") in node (" +
                            this + ") is in an unexpected state (" + outTuple.getState() + ").");
            }
        } else {
            killTuple(oldGroup);
            createTuple(tuple, newGroupKey);
        }
    }

    private void killTuple(AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> group) {
        int newParentCount = --group.parentCount;
        boolean killGroup = (newParentCount == 0);
        if (killGroup) {
            GroupKey_ groupKey = group.groupKey;
            AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> old = removeGroup(groupKey);
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

    private AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> removeGroup(GroupKey_ groupKey) {
        if (hasMultipleGroups) {
            return groupMap.remove(groupKey);
        } else {
            AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> old = singletonGroup;
            singletonGroup = null;
            return old;
        }
    }

    @Override
    public void retract(InTuple_ tuple) {
        AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> group = tuple.removeStore(groupStoreIndex);
        if (group == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        if (hasCollector) {
            Runnable undoAccumulator = tuple.removeStore(undoStoreIndex);
            undoAccumulator.run();
        }
        killTuple(group);
    }

    protected abstract Runnable accumulate(ResultContainer_ resultContainer, InTuple_ tuple);

    @Override
    public void calculateScore() {
        for (AbstractGroup<MutableOutTuple_, GroupKey_, ResultContainer_> group : dirtyGroupQueue) {
            MutableOutTuple_ outTuple = group.outTuple;
            // Delay calculating finisher right until the tuple propagates
            switch (outTuple.getState()) {
                case CREATING:
                    if (hasCollector) {
                        updateOutTupleToFinisher(outTuple, group.getResultContainer());
                    }
                    nextNodesTupleLifecycle.insert(outTuple);
                    outTuple.setState(BavetTupleState.OK);
                    break;
                case UPDATING:
                    if (hasCollector) {
                        updateOutTupleToFinisher(outTuple, group.getResultContainer());
                    }
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
                            this + ") is in an unexpected state (" + outTuple.getState() + ").");
            }
        }
        dirtyGroupQueue.clear();
    }

    /**
     *
     * @param groupKey null if the node only has one group
     * @return never null
     */
    protected abstract MutableOutTuple_ createOutTuple(GroupKey_ groupKey);

    private void updateOutTupleToFinisher(MutableOutTuple_ outTuple, ResultContainer_ resultContainer) {
        Result_ result = finisher.apply(resultContainer);
        updateOutTupleToResult(outTuple, result);
    }

    protected abstract void updateOutTupleToResult(MutableOutTuple_ outTuple, Result_ result);

}
