package org.optaplanner.constraint.streams.bavet.common;

import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;

public abstract class AbstractScorer<Tuple_ extends Tuple> implements TupleLifecycle<Tuple_> {

    private final String constraintId;
    private final Score<?> constraintWeight;
    private final int inputStoreIndex;

    protected AbstractScorer(String constraintPackage, String constraintName,
            Score<?> constraintWeight, int inputStoreIndex) {
        this.constraintId = ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
        this.constraintWeight = constraintWeight;
        this.inputStoreIndex = inputStoreIndex;
    }

    @Override
    public final void insert(Tuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        if (tupleStore[inputStoreIndex] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tuple
                    + ") was already added in the tupleStore.");
        }
        tupleStore[inputStoreIndex] = impact(tuple);
    }

    @Override
    public final void update(Tuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        UndoScoreImpacter undoScoreImpacter = (UndoScoreImpacter) tupleStore[inputStoreIndex];
        // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
        if (undoScoreImpacter != null) {
            undoScoreImpacter.run();
        }
        tupleStore[inputStoreIndex] = impact(tuple);
    }

    protected abstract UndoScoreImpacter impact(Tuple_ tuple);

    @Override
    public final void retract(Tuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        UndoScoreImpacter undoScoreImpacter = (UndoScoreImpacter) tupleStore[inputStoreIndex];
        // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
        if (undoScoreImpacter != null) {
            undoScoreImpacter.run();
            tupleStore[inputStoreIndex] = null;
        }
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "(" + constraintId + ") with constraintWeight (" + constraintWeight + ")";
    }

}
