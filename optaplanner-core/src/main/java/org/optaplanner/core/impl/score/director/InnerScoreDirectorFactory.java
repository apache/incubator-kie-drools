/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface InnerScoreDirectorFactory<Solution_> extends ScoreDirectorFactory<Solution_> {

    /**
     * @return never null
     */
    SolutionDescriptor<Solution_> getSolutionDescriptor();

    /**
     * @return never null
     */
    ScoreDefinition getScoreDefinition();

    @Override
    InnerScoreDirector<Solution_> buildScoreDirector();

    /**
     * Like {@link #buildScoreDirector()}, but optionally disables {@link ConstraintMatch} tracking and look up
     * for more performance (presuming the {@link ScoreDirector} implementation actually supports it to begin with).
     * @param lookUpEnabled true if a {@link ScoreDirector} implementation should track all working objects
     * for {@link ScoreDirector#lookUpWorkingObject(Object)}
     * @param constraintMatchEnabledPreference false if a {@link ScoreDirector} implementation
     * should not do {@link ConstraintMatch} tracking even if it supports it.
     * @return never null
     * @see ScoreDirector#isConstraintMatchEnabled()
     * @see ScoreDirector#getConstraintMatchTotalMap()
     */
    InnerScoreDirector<Solution_> buildScoreDirector(boolean lookUpEnabled, boolean constraintMatchEnabledPreference);

    /**
     * @return never null
     */
    InitializingScoreTrend getInitializingScoreTrend();

    /**
     * Asserts that if the {@link Score} is calculated for the parameter solution,
     * it would be equal to the score of that parameter.
     * @param solution never null
     * @see InnerScoreDirector#assertWorkingScoreFromScratch(Score, Object)
     */
    void assertScoreFromScratch(Solution_ solution);

}
