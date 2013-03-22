/*
 * Copyright 2012 JBoss Inc
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

import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link ScoreDirectorFactory}.
 * @see ScoreDirectorFactory
 */
public abstract class AbstractScoreDirectorFactory implements ScoreDirectorFactory {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolutionDescriptor solutionDescriptor;
    protected ScoreDefinition scoreDefinition;

    protected ScoreDirectorFactory assertionScoreDirectorFactory = null;

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

    public ScoreDirectorFactory getAssertionScoreDirectorFactory() {
        return assertionScoreDirectorFactory;
    }

    public void setAssertionScoreDirectorFactory(ScoreDirectorFactory assertionScoreDirectorFactory) {
        this.assertionScoreDirectorFactory = assertionScoreDirectorFactory;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public void assertScoreFromScratch(Solution solution) {
        // Get the score before uncorruptedScoreDirector.calculateScore() modifies it
        Score score = solution.getScore();
        ScoreDirector uncorruptedScoreDirector = buildScoreDirector();
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
