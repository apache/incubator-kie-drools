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

package org.optaplanner.core.impl.score;

import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolutionUpdatePolicy;
import org.optaplanner.core.impl.solver.DefaultSolutionManager;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @deprecated Use {@link DefaultSolutionManager} instead.
 */
@Deprecated(forRemoval = true)
public final class DefaultScoreManager<Solution_, Score_ extends Score<Score_>>
        implements ScoreManager<Solution_, Score_> {

    private final SolutionManager<Solution_, Score_> solutionManager;

    public DefaultScoreManager(SolutionManager<Solution_, Score_> solutionManager) {
        this.solutionManager = Objects.requireNonNull(solutionManager);
    }

    @Override
    public Score_ updateScore(Solution_ solution) {
        return solutionManager.update(solution, SolutionUpdatePolicy.UPDATE_SCORE_ONLY);
    }

    @Override
    public String getSummary(Solution_ solution) {
        return explainScore(solution)
                .getSummary();
    }

    @Override
    public ScoreExplanation<Solution_, Score_> explainScore(Solution_ solution) {
        return solutionManager.explain(solution, SolutionUpdatePolicy.UPDATE_SCORE_ONLY);
    }

    @Override
    public Score_ update(Solution_ solution, SolutionUpdatePolicy solutionUpdatePolicy) {
        return solutionManager.update(solution, solutionUpdatePolicy);
    }

    @Override
    public ScoreExplanation<Solution_, Score_> explain(Solution_ solution, SolutionUpdatePolicy solutionUpdatePolicy) {
        return solutionManager.explain(solution, solutionUpdatePolicy);
    }

}
