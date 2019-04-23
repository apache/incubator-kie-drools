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
import org.optaplanner.core.api.score.stream.common.Joiners;
import org.optaplanner.examples.nqueens.domain.Queen;

public class NQueensConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        horizontalConflict(constraintFactory);
        ascendingDiagonalConflict(constraintFactory);
        descendingDiagonalConflict(constraintFactory);
    }

    protected void horizontalConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "Horizontal conflict", SimpleScore.of(1));
        c.from(Queen.class)
                .join(c.from(Queen.class),
                        Joiners.equalTo(Queen::getRowIndex)
                                .and(Joiners.lessThan(Queen::getId)))
                .penalize();
    }

    protected void ascendingDiagonalConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "Ascending diagonal conflict", SimpleScore.of(1));
        c.from(Queen.class)
                .join(c.from(Queen.class),
                        Joiners.equalTo(Queen::getAscendingDiagonalIndex)
                                .and(Joiners.lessThan(Queen::getId)))
                .penalize();
    }

    protected void descendingDiagonalConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight(
                "Descending diagonal conflict", SimpleScore.of(1));
        c.from(Queen.class)
                .join(c.from(Queen.class),
                        Joiners.equalTo(Queen::getDescendingDiagonalIndex)
                                .and(Joiners.lessThan(Queen::getId)))
                .penalize();
    }

}
