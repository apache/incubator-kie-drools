/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class DefaultScoreManager<Solution_> implements ScoreManager<Solution_> {

    private InnerScoreDirectorFactory<Solution_> scoreDirectorFactory;

    public DefaultScoreManager(InnerScoreDirectorFactory<Solution_> scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
    }

    public InnerScoreDirectorFactory<Solution_> getScoreDirectorFactory() {
        return scoreDirectorFactory;
    }

    @Override
    public Score updateScore(Solution_ solution) {
        try (InnerScoreDirector<Solution_> scoreDirector = scoreDirectorFactory.buildScoreDirector()) {
            scoreDirector.setWorkingSolution(solution);
            return scoreDirector.calculateScore();
        }
    }

    @Override
    public String getSummary(Solution_ solution) {
        return explainScore(solution).getSummary();
    }

    @Override
    public ScoreExplanation<Solution_> explainScore(Solution_ solution) {
        try (InnerScoreDirector<Solution_> scoreDirector = scoreDirectorFactory.buildScoreDirector(true, true)) {
            boolean constraintMatchEnabled = scoreDirector.isConstraintMatchEnabled();
            if (!constraintMatchEnabled) {
                throw new IllegalStateException("When constraintMatchEnabled (" + constraintMatchEnabled
                        + ") is disabled, this method should not be called.");
            }
            scoreDirector.setWorkingSolution(solution);
            return new DefaultScoreExplanation(solution, scoreDirector.calculateScore(), scoreDirector.explainScore(),
                    scoreDirector.getConstraintMatchTotalMap(), scoreDirector.getIndictmentMap());
        }
    }
}
