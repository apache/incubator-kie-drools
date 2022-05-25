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

import org.optaplanner.constraint.streams.bavet.common.AbstractScorer;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;

final class TriScorer<A, B, C> extends AbstractScorer<TriTuple<A, B, C>> {

    private final TriFunction<A, B, C, UndoScoreImpacter> scoreImpacter;

    public TriScorer(String constraintPackage, String constraintName, Score<?> constraintWeight,
                     TriFunction<A, B, C, UndoScoreImpacter> scoreImpacter, int inputStoreIndex) {
        super(constraintPackage, constraintName, constraintWeight, inputStoreIndex);
        this.scoreImpacter = scoreImpacter;
    }

    @Override
    protected UndoScoreImpacter impact(TriTuple<A, B, C> tuple) {
        return scoreImpacter.apply(tuple.factA, tuple.factB, tuple.factC);
    }
}
