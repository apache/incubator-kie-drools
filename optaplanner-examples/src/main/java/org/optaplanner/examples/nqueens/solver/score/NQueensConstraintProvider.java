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
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.examples.nqueens.domain.Queen;

public class NQueensConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        // TODO rename to "horizontal conflict", ...
        multipleQueensHorizontal(constraintFactory);
        multipleQueensAscendingDiagonal(constraintFactory);
        multipleQueensDescendingDiagonal(constraintFactory);
    }

    protected void multipleQueensHorizontal(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory
                .newConstraintWithWeight("multipleQueensHorizontal", SimpleScore.of(1));
        UniConstraintStream<Queen> aQueenStream = constraint.select(Queen.class)
                .filter(queen -> queen.getRow() != null);
        UniConstraintStream<Queen> bQueenStream = constraint.select(Queen.class)
                .filter(queen -> queen.getRow() != null);
        aQueenStream.join(bQueenStream, BiJoiner.equals(Queen::getRowIndex))
                .filter((a, b) -> a.getId() < b.getId())
                .penalize();
    }

    protected void multipleQueensAscendingDiagonal(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory
                .newConstraintWithWeight("multipleQueensAscendingDiagonal", SimpleScore.of(1));
        UniConstraintStream<Queen> aQueenStream = constraint.select(Queen.class)
                .filter(queen -> queen.getRow() != null);
        UniConstraintStream<Queen> bQueenStream = constraint.select(Queen.class)
                .filter(queen -> queen.getRow() != null);
        aQueenStream.join(bQueenStream, BiJoiner.equals(Queen::getAscendingDiagonalIndex))
                .filter((a, b) -> a.getId() < b.getId())
                .penalize();
    }

    protected void multipleQueensDescendingDiagonal(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory
                .newConstraintWithWeight("multipleQueensDescendingDiagonal", SimpleScore.of(1));
        UniConstraintStream<Queen> aQueenStream = constraint.select(Queen.class)
                .filter(queen -> queen.getRow() != null);
        UniConstraintStream<Queen> bQueenStream = constraint.select(Queen.class)
                .filter(queen -> queen.getRow() != null);
        aQueenStream.join(bQueenStream, BiJoiner.equals(Queen::getDescendingDiagonalIndex))
                .filter((a, b) -> a.getId() < b.getId())
                .penalize();
    }

}
