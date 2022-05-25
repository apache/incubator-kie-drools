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

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class SimpleLongScoreInliner extends AbstractScoreInliner<SimpleLongScore> {

    private long score;

    SimpleLongScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter buildWeightedScoreImpacter(Constraint constraint, SimpleLongScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        long simpleConstraintWeight = constraintWeight.getScore();
        return WeightedScoreImpacter.of((long matchWeight, JustificationsSupplier justificationsSupplier) -> {
            long impact = simpleConstraintWeight * matchWeight;
            this.score += impact;
            UndoScoreImpacter undoScoreImpact = () -> this.score -= impact;
            if (!constraintMatchEnabled) {
                return undoScoreImpact;
            }
            Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight, SimpleLongScore.of(impact),
                    justificationsSupplier.get());
            return () -> {
                undoScoreImpact.run();
                undoConstraintMatch.run();
            };
        });
    }

    @Override
    public SimpleLongScore extractScore(int initScore) {
        return SimpleLongScore.ofUninitialized(initScore, score);
    }

    @Override
    public String toString() {
        return SimpleLongScore.class.getSimpleName() + " inliner";
    }

}
