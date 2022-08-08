package org.optaplanner.constraint.streams.bavet.common;

import java.util.ArrayDeque;
import java.util.Queue;

import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.bavet.uni.UniTupleImpl;

public abstract class AbstractMapNode<InTuple_ extends Tuple, Right_>
        extends AbstractNode
        implements TupleLifecycle<InTuple_> {

    private final int inputStoreIndex;
    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)} and/or ...
     */
    private final TupleLifecycle<UniTuple<Right_>> nextNodesTupleLifecycle;
    private final int outputStoreSize;
    private final Queue<UniTuple<Right_>> dirtyTupleQueue;

    protected AbstractMapNode(int inputStoreIndex, TupleLifecycle<UniTuple<Right_>> nextNodesTupleLifecycle,
            int outputStoreSize) {
        this.inputStoreIndex = inputStoreIndex;
        this.nextNodesTupleLifecycle = nextNodesTupleLifecycle;
        this.outputStoreSize = outputStoreSize;
        dirtyTupleQueue = new ArrayDeque<>(1000);
    }

    @Override
    public void insert(InTuple_ tuple) {
        if (tuple.getStore(inputStoreIndex) != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tuple
                    + ") was already added in the tupleStore.");
        }
        Right_ mapped = map(tuple);
        UniTuple<Right_> outTuple = new UniTupleImpl<>(mapped, outputStoreSize);
        tuple.setStore(inputStoreIndex, outTuple);
        dirtyTupleQueue.add(outTuple);
    }

    protected abstract Right_ map(InTuple_ tuple);

    @Override
    public void update(InTuple_ tuple) {
        UniTupleImpl<Right_> outTuple = tuple.getStore(inputStoreIndex);
        if (outTuple == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            insert(tuple);
            return;
        }
        Right_ oldMapped = outTuple.factA;
        Right_ mapped = map(tuple);
        // We check for identity, not equality, to not introduce dependency on user equals().
        if (mapped != oldMapped) {
            outTuple.factA = mapped;
            outTuple.state = BavetTupleState.UPDATING;
            dirtyTupleQueue.add(outTuple);
        }
    }

    @Override
    public void retract(InTuple_ tuple) {
        UniTuple<Right_> outTuple = tuple.getStore(inputStoreIndex);
        if (outTuple == null) {
            // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
            return;
        }
        tuple.setStore(inputStoreIndex, null);
        outTuple.setState(BavetTupleState.DYING);
        dirtyTupleQueue.add(outTuple);
    }

    @Override
    public void calculateScore() {
        for (UniTuple<Right_> tuple : dirtyTupleQueue) {
            switch (tuple.getState()) {
                case CREATING:
                    nextNodesTupleLifecycle.insert(tuple);
                    tuple.setState(BavetTupleState.OK);
                    break;
                case UPDATING:
                    nextNodesTupleLifecycle.update(tuple);
                    tuple.setState(BavetTupleState.OK);
                    break;
                case DYING:
                    nextNodesTupleLifecycle.retract(tuple);
                    tuple.setState(BavetTupleState.DEAD);
                    break;
                case ABORTING:
                    tuple.setState(BavetTupleState.DEAD);
                    break;
                case OK:
                case DEAD:
                default:
                    throw new IllegalStateException("Impossible state: The tuple (" + tuple + ") in node (" +
                            this + ") is in an unexpected state (" + tuple.getState() + ").");
            }
        }
        dirtyTupleQueue.clear();
    }

}
