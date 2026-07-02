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

package org.optaplanner.core.impl.score.director;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link ScoreDirectorFactory}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the score type to go with the solution
 * @see ScoreDirectorFactory
 */
public abstract class AbstractScoreDirectorFactory<Solution_, Score_ extends Score<Score_>>
        implements InnerScoreDirectorFactory<Solution_, Score_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolutionDescriptor<Solution_> solutionDescriptor;

    protected InitializingScoreTrend initializingScoreTrend;

    protected InnerScoreDirectorFactory<Solution_, Score_> assertionScoreDirectorFactory = null;

    protected boolean assertClonedSolution = false;

    public AbstractScoreDirectorFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    @Override
    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    @Override
    public ScoreDefinition<Score_> getScoreDefinition() {
        return solutionDescriptor.getScoreDefinition();
    }

    @Override
    public InitializingScoreTrend getInitializingScoreTrend() {
        return initializingScoreTrend;
    }

    public void setInitializingScoreTrend(InitializingScoreTrend initializingScoreTrend) {
        this.initializingScoreTrend = initializingScoreTrend;
    }

    public InnerScoreDirectorFactory<Solution_, Score_> getAssertionScoreDirectorFactory() {
        return assertionScoreDirectorFactory;
    }

    public void setAssertionScoreDirectorFactory(InnerScoreDirectorFactory<Solution_, Score_> assertionScoreDirectorFactory) {
        this.assertionScoreDirectorFactory = assertionScoreDirectorFactory;
    }

    public boolean isAssertClonedSolution() {
        return assertClonedSolution;
    }

    public void setAssertClonedSolution(boolean assertClonedSolution) {
        this.assertClonedSolution = assertClonedSolution;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public InnerScoreDirector<Solution_, Score_> buildScoreDirector() {
        return buildScoreDirector(true, true);
    }

    @Override
    public void assertScoreFromScratch(Solution_ solution) {
        // Get the score before uncorruptedScoreDirector.calculateScore() modifies it
        Score_ score = (Score_) getSolutionDescriptor().getScore(solution);
        try (InnerScoreDirector<Solution_, Score_> uncorruptedScoreDirector = buildScoreDirector(false, true)) {
            uncorruptedScoreDirector.setWorkingSolution(solution);
            Score_ uncorruptedScore = uncorruptedScoreDirector.calculateScore();
            if (!score.equals(uncorruptedScore)) {
                throw new IllegalStateException(
                        "Score corruption (" + score.subtract(uncorruptedScore).toShortString()
                                + "): the solution's score (" + score + ") is not the uncorruptedScore ("
                                + uncorruptedScore + ").");
            }
        }
    }

}
