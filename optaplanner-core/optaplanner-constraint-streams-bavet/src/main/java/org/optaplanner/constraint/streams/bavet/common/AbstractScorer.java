/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.constraint.streams.bavet.common;

import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;

public abstract class AbstractScorer<Tuple_ extends Tuple> {

    private final String constraintId;
    private final Score<?> constraintWeight;
    private final int inputStoreIndex;

    protected AbstractScorer(String constraintPackage, String constraintName,
            Score<?> constraintWeight, int inputStoreIndex) {
        this.constraintId = ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
        this.constraintWeight = constraintWeight;
        this.inputStoreIndex = inputStoreIndex;
    }

    public final void insert(Tuple_ tuple) {
        Object[] tupleStore = tuple.getStore();
        if (tupleStore[inputStoreIndex] != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tuple
                    + ") was already added in the tupleStore.");
        }
        tupleStore[inputStoreIndex] = impact(tuple);
    }

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
