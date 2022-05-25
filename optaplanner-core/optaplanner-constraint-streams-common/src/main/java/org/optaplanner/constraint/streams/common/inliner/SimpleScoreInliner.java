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

package org.optaplanner.constraint.streams.common.inliner;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class SimpleScoreInliner extends AbstractScoreInliner<SimpleScore> {

    private int score;

    SimpleScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter buildWeightedScoreImpacter(Constraint constraint, SimpleScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        int simpleConstraintWeight = constraintWeight.getScore();
        return WeightedScoreImpacter.of((int matchWeight, JustificationsSupplier justificationsSupplier) -> {
            int impact = simpleConstraintWeight * matchWeight;
            this.score += impact;
            UndoScoreImpacter undoScoreImpact = () -> this.score -= impact;
            if (!constraintMatchEnabled) {
                return undoScoreImpact;
            }
            Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight, SimpleScore.of(impact),
                    justificationsSupplier.get());
            return () -> {
                undoScoreImpact.run();
                undoConstraintMatch.run();
            };
        });
    }

    @Override
    public SimpleScore extractScore(int initScore) {
        return SimpleScore.ofUninitialized(initScore, score);
    }

    @Override
    public String toString() {
        return SimpleScore.class.getSimpleName() + " inliner";
    }

}
