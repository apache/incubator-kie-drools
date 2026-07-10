/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
        if (tuple.getStore(inputStoreIndex) != null) {
            throw new IllegalStateException("Impossible state: the input for the tuple (" + tuple
                    + ") was already added in the tupleStore.");
        }
        tuple.setStore(inputStoreIndex, impact(tuple));
    }

    @Override
    public final void update(Tuple_ tuple) {
        UndoScoreImpacter undoScoreImpacter = tuple.getStore(inputStoreIndex);
        // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
        if (undoScoreImpacter != null) {
            undoScoreImpacter.run();
        }
        tuple.setStore(inputStoreIndex, impact(tuple));
    }

    protected abstract UndoScoreImpacter impact(Tuple_ tuple);

    /**
     * Helps with debugging exceptions thrown by user code during impact calls.
     *
     * @param tuple never null
     * @param cause never null
     * @return never null, exception to be thrown.
     */
    protected RuntimeException createExceptionOnImpact(Tuple_ tuple, Exception cause) {
        return new IllegalStateException(
                "Consequence of a constraint (" + constraintId + ") threw an exception processing a tuple (" + tuple + ").",
                cause);
    }

    @Override
    public final void retract(Tuple_ tuple) {
        UndoScoreImpacter undoScoreImpacter = tuple.getStore(inputStoreIndex);
        // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
        if (undoScoreImpacter != null) {
            undoScoreImpacter.run();
            tuple.setStore(inputStoreIndex, null);
        }
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "(" + constraintId + ") with constraintWeight (" + constraintWeight + ")";
    }

}
