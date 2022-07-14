package org.optaplanner.constraint.streams.bavet.common;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Function;

public abstract class AbstractFlattenLastNode<InTuple_ extends Tuple, OutTuple_ extends Tuple, EffectiveItem_, FlattenedItem_>
        extends AbstractNode
        implements TupleLifecycle<InTuple_> {

    private final int flattenLastStoreIndex;
    private final Function<EffectiveItem_, Iterable<FlattenedItem_>> mappingFunction;
    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)}, and/or ...
     */
    private final TupleLifecycle<OutTuple_> nextNodesTupleLifecycle;
    private final Queue<OutTuple_> dirtyTupleQueue = new ArrayDeque<>(1000);

    protected AbstractFlattenLastNode(int flattenLastStoreIndex,
            Function<EffectiveItem_, Iterable<FlattenedItem_>> mappingFunction,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle) {
        this.flattenLastStoreIndex = flattenLastStoreIndex;
        this.mappingFunction = Objects.requireNonNull(mappingFunction);
        this.nextNodesTupleLifecycle = Objects.requireNonNull(nextNodesTupleLifecycle);
    }

    @Override
    public void insert(InTuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        if (tupleStore[flattenLastStoreIndex] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tuple
                    + ") was already added in the tupleStore.");
        }
        List<OutTuple_> outTupleList = new ArrayList<>();
        for (FlattenedItem_ item : mappingFunction.apply(getEffectiveFactIn(tuple))) {
            addTuple(tuple, item, outTupleList);
        }
        if (!outTupleList.isEmpty()) {
            tupleStore[flattenLastStoreIndex] = outTupleList;
        }
    }

    private void addTuple(InTuple_ originalTuple, FlattenedItem_ item, List<OutTuple_> outTupleList) {
        OutTuple_ tuple = createTuple(originalTuple, item);
        outTupleList.add(tuple);
        tuple.setState(BavetTupleState.CREATING);
        dirtyTupleQueue.add(tuple);
    }

    protected abstract OutTuple_ createTuple(InTuple_ originalTuple, FlattenedItem_ item);

    @Override
    public void update(InTuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        List<OutTuple_> outTupleList = (List<OutTuple_>) tupleStore[flattenLastStoreIndex];
        if (outTupleList == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s).
            insert(tuple);
            return;
        }
        Iterator<FlattenedItem_> iterator = mappingFunction.apply(getEffectiveFactIn(tuple)).iterator();
        if (!iterator.hasNext()) { // No need for incremental logic as everything will be removed.
            retract(tuple);
            return;
        }
        // Convert Iterable into something we can query.
        List<FlattenedItem_> newFlattenedItemList = new ArrayList<>();
        iterator.forEachRemaining(newFlattenedItemList::add);
        // Remove all facts from the input that are already contained.
        Iterator<OutTuple_> outTupleIterator = outTupleList.listIterator();
        while (outTupleIterator.hasNext()) {
            OutTuple_ outTuple = outTupleIterator.next();
            FlattenedItem_ existingFlattenedItem = getEffectiveFactOut(outTuple);
            // A fact can be present more than once and every iteration should only remove one instance.
            boolean existsAlsoInNew = false;
            Iterator<FlattenedItem_> newFlattenedItemIterator = newFlattenedItemList.listIterator();
            while (newFlattenedItemIterator.hasNext()) {
                FlattenedItem_ newFlattenedItem = newFlattenedItemIterator.next();
                // We check for identity, not equality, to not introduce dependency on user equals().
                if (newFlattenedItem == existingFlattenedItem) {
                    // Remove item from the list, as it means its tuple need not be added later.
                    newFlattenedItemIterator.remove();
                    existsAlsoInNew = true;
                    break;
                }
            }
            if (!existsAlsoInNew) {
                outTupleIterator.remove();
                removeTuple(outTuple);
            } else {
                outTuple.setState(BavetTupleState.UPDATING);
                dirtyTupleQueue.add(outTuple);
            }
        }
        // Whatever is left in the input needs to be added.
        for (FlattenedItem_ newFlattenedItem : newFlattenedItemList) {
            addTuple(tuple, newFlattenedItem, outTupleList);
        }
    }

    protected abstract EffectiveItem_ getEffectiveFactIn(InTuple_ tuple);

    protected abstract FlattenedItem_ getEffectiveFactOut(OutTuple_ outTuple);

    @Override
    public void retract(InTuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        List<OutTuple_> outTupleList = (List<OutTuple_>) tupleStore[flattenLastStoreIndex];
        if (outTupleList == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tupleStore[flattenLastStoreIndex] = null;
        for (OutTuple_ item : outTupleList) {
            removeTuple(item);
        }
    }

    private void removeTuple(OutTuple_ outTuple) {
        switch (outTuple.getState()) {
            case CREATING:
                outTuple.setState(BavetTupleState.ABORTING);
                break;
            case UPDATING:
            case OK:
                outTuple.setState(BavetTupleState.DYING);
                break;
            default:
                throw new IllegalStateException("Impossible state: The tuple (" + outTuple +
                        ") is in an unexpected state (" + outTuple.getState() + ").");
        }
        dirtyTupleQueue.add(outTuple);
    }

    @Override
    public void calculateScore() {
        for (OutTuple_ outTuple : dirtyTupleQueue) {
            switch (outTuple.getState()) {
                case CREATING:
                    nextNodesTupleLifecycle.insert(outTuple);
                    outTuple.setState(BavetTupleState.OK);
                    break;
                case UPDATING:
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
                default:
                    throw new IllegalStateException("Impossible state: The tuple (" + outTuple + ") in node (" +
                            this + ") is in an unexpected state (" + outTuple.getState() + ").");
            }
        }
        dirtyTupleQueue.clear();
    }

}
