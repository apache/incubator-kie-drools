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

package org.drools.planner.core.score.director;

import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
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

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public void assertScore(Solution solution) {
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
