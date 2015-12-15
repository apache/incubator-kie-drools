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

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link ScoreDirectorFactory}.
 * @see ScoreDirectorFactory
 */
public abstract class AbstractScoreDirectorFactory implements InnerScoreDirectorFactory {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolutionDescriptor solutionDescriptor;
    protected ScoreDefinition scoreDefinition;

    protected InitializingScoreTrend initializingScoreTrend;

    protected InnerScoreDirectorFactory assertionScoreDirectorFactory = null;

    protected boolean assertClonedSolution = false;

    public SolutionDescriptor getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public void setSolutionDescriptor(SolutionDescriptor solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDefinition;
    }

    public void setScoreDefinition(ScoreDefinition scoreDefinition) {
        this.scoreDefinition = scoreDefinition;
    }

    public InitializingScoreTrend getInitializingScoreTrend() {
        return initializingScoreTrend;
    }

    public void setInitializingScoreTrend(InitializingScoreTrend initializingScoreTrend) {
        this.initializingScoreTrend = initializingScoreTrend;
    }

    public InnerScoreDirectorFactory getAssertionScoreDirectorFactory() {
        return assertionScoreDirectorFactory;
    }

    public void setAssertionScoreDirectorFactory(InnerScoreDirectorFactory assertionScoreDirectorFactory) {
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

    public InnerScoreDirector buildScoreDirector() {
        return buildScoreDirector(true);
    }

    public void assertScoreFromScratch(Solution solution) {
        // Get the score before uncorruptedScoreDirector.calculateScore() modifies it
        Score score = solution.getScore();
        InnerScoreDirector uncorruptedScoreDirector = buildScoreDirector(true);
        uncorruptedScoreDirector.setWorkingSolution(solution);
        Score uncorruptedScore = uncorruptedScoreDirector.calculateScore();
        uncorruptedScoreDirector.dispose();
        if (!score.equals(uncorruptedScore)) {
            throw new IllegalStateException(
                    "Score corruption: the solution's score (" + score + ") is not the uncorruptedScore ("
                            + uncorruptedScore + ").");
        }
    }

}
