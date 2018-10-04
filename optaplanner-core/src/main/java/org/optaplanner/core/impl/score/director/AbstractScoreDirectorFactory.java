/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see ScoreDirectorFactory
 */
public abstract class AbstractScoreDirectorFactory<Solution_> implements InnerScoreDirectorFactory<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolutionDescriptor<Solution_> solutionDescriptor;

    protected InitializingScoreTrend initializingScoreTrend;

    protected InnerScoreDirectorFactory<Solution_> assertionScoreDirectorFactory = null;

    protected boolean assertClonedSolution = false;

    public AbstractScoreDirectorFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    @Override
    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    @Override
    public ScoreDefinition getScoreDefinition() {
        return solutionDescriptor.getScoreDefinition();
    }

    @Override
    public InitializingScoreTrend getInitializingScoreTrend() {
        return initializingScoreTrend;
    }

    public void setInitializingScoreTrend(InitializingScoreTrend initializingScoreTrend) {
        this.initializingScoreTrend = initializingScoreTrend;
    }

    public InnerScoreDirectorFactory<Solution_> getAssertionScoreDirectorFactory() {
        return assertionScoreDirectorFactory;
    }

    public void setAssertionScoreDirectorFactory(InnerScoreDirectorFactory<Solution_> assertionScoreDirectorFactory) {
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
    public InnerScoreDirector<Solution_> buildScoreDirector() {
        return buildScoreDirector(true, true);
    }

    @Override
    public void assertScoreFromScratch(Solution_ solution) {
        // Get the score before uncorruptedScoreDirector.calculateScore() modifies it
        Score score = getSolutionDescriptor().getScore(solution);
        try (InnerScoreDirector<Solution_> uncorruptedScoreDirector = buildScoreDirector(false, true)) {
            uncorruptedScoreDirector.setWorkingSolution(solution);
            Score uncorruptedScore = uncorruptedScoreDirector.calculateScore();
            if (!score.equals(uncorruptedScore)) {
                throw new IllegalStateException(
                        "Score corruption (" + score.subtract(uncorruptedScore).toShortString()
                                + "): the solution's score (" + score + ") is not the uncorruptedScore ("
                                + uncorruptedScore + ").");
            }
        }
    }

}
