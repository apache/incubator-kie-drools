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

/**
 * Builds a {@link ScoreDirector}.
 */
public interface ScoreDirectorFactory {

    /**
     * @return never null
     */
    SolutionDescriptor getSolutionDescriptor();

    /**
     * @return never null
     */
    ScoreDefinition getScoreDefinition();

    /**
     * Creates a new {@link ScoreDirector} instance.
     * @return never null
     */
    ScoreDirector buildScoreDirector();

    /**
     * Asserts that if the {@link Score} is calculated for the parameter solution,
     * it would be equal to the {@link Solution#getScore()} of that parameter.
     * @param solution never null
     * @see ScoreDirector#assertWorkingScoreFromScratch(Score)
     */
    void assertScoreFromScratch(Solution solution);

}
