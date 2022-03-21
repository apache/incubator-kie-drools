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

package org.optaplanner.constraint.streams.bavet.tri;

import java.util.HashMap;
import java.util.Map;

import org.optaplanner.constraint.streams.bavet.common.AbstractScorer;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;

public final class TriScorer<A, B, C> extends AbstractScorer {

    private final String constraintPackage;
    private final String constraintName;
    private final Score<?> constraintWeight;
    private final TriFunction<A, B, C, UndoScoreImpacter> scoreImpacter;

    private final Map<TriTuple<A, B, C>, UndoScoreImpacter> impacterMap = new HashMap<>();

    public TriScorer(String constraintPackage, String constraintName,
            Score<?> constraintWeight, TriFunction<A, B, C, UndoScoreImpacter> scoreImpacter) {
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.constraintWeight = constraintWeight;
        this.scoreImpacter = scoreImpacter;
    }

    public void insert(TriTuple<A, B, C> tupleABC) {
        UndoScoreImpacter undoScoreImpacter = scoreImpacter.apply(tupleABC.factA, tupleABC.factB, tupleABC.factC);
        UndoScoreImpacter old = impacterMap.put(tupleABC, undoScoreImpacter);
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple for the facts ("
                    + tupleABC.factA + ", " + tupleABC.factB + ", " + tupleABC.factC
                    + ") was already added in the impacterMap.");
        }
    }

    public void retract(TriTuple<A, B, C> tupleABC) {
        UndoScoreImpacter undoScoreImpacter = impacterMap.remove(tupleABC);
        // No fail fast if null because we don't track which tuples made it through the filter predicate(s)
        if (undoScoreImpacter != null) {
            undoScoreImpacter.run();
        }
    }

    @Override
    public String toString() {
        return "Scorer(" + constraintName + ") with constraintWeight (" + constraintWeight + ")";
    }

}
