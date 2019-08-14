/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nqueens.solver.score;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.nqueens.domain.Queen;

import static org.optaplanner.core.api.score.stream.common.Joiners.*;

public class NQueensConstraintProvider implements ConstraintProvider {

    // WARNING: The ConstraintStreams/ConstraintProvider API is TECH PREVIEW.
    // It is stable but it has many API gaps.
    // Therefore, it is not rich enough yet to handle complex constraints.

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        horizontalConflict(constraintFactory);
        ascendingDiagonalConflict(constraintFactory);
        descendingDiagonalConflict(constraintFactory);
    }

    protected void horizontalConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "Horizontal conflict", SimpleScore.of(1));
        c.fromUniquePair(Queen.class,
                equal(Queen::getRowIndex)
        ).penalize();
        // fromUniquePair() is syntactic sugar for from().join(..., lessThan(getId())
//        c.from(Queen.class)
//                .join(Queen.class,
//                        equal(Queen::getRowIndex),
//                        lessThan(Queen::getId))
//                .penalize();
    }

    protected void ascendingDiagonalConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "Ascending diagonal conflict", SimpleScore.of(1));
        c.fromUniquePair(Queen.class,
                equal(Queen::getAscendingDiagonalIndex)
        ).penalize();
    }

    protected void descendingDiagonalConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "Descending diagonal conflict", SimpleScore.of(1));
        c.fromUniquePair(Queen.class,
                equal(Queen::getDescendingDiagonalIndex)
        ).penalize();
    }

}
