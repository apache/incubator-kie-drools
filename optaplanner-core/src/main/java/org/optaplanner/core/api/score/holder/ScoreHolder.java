/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.holder;

import java.util.Collection;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;

/**
 * This class is injected as a global by {@link DroolsScoreDirector} into the Drools DRL.
 * Other {@link ScoreDirector} implementations do not use this class.
 * <p>
 * An implementation must extend {@link AbstractScoreHolder} to ensure backwards compatibility in future versions.
 * @param <Score_> the {@link Score} type
 * @see AbstractScoreHolder
 */
public interface ScoreHolder<Score_ extends Score<Score_>> {

    /**
     * Extracts the {@link Score}, calculated by the {@link KieSession} for {@link DroolsScoreDirector}.
     * <p>
     * Should not be called directly, use {@link ScoreDirector#calculateScore()} instead.
     * @param initScore {@code <= 0}, managed by OptaPlanner, needed as a parameter in the {@link Score}'s creation
     * method, see {@link Score#getInitScore()}
     * @return never null, the {@link Score} of the working {@link PlanningSolution}
     */
    Score_ extractScore(int initScore);

    /**
     * Sets up a {@link ConstraintWeight} from the {@link ConstraintConfiguration} during initialization.
     * @param rule never null
     * @param constraintWeight never null, with {@link Score#getInitScore()} equal to 0.
     */
    void configureConstraintWeight(Rule rule, Score_ constraintWeight);

    /**
     * Must be in sync with {@link ScoreDirector#isConstraintMatchEnabled()}
     * for the {@link ScoreDirector} which contains this {@link ScoreHolder}.
     * <p>
     * Defaults to true.
     * @return false if the {@link ConstraintMatch}s and {@link ConstraintMatchTotal}s do not need to be collected
     * which is a performance boost
     * @see #getConstraintMatchTotals()
     */
    boolean isConstraintMatchEnabled();

    /**
     * Explains the {@link Score} of {@link #extractScore(int)}.
     * <p>
     * Should not be called directly, use {@link ScoreDirector#getConstraintMatchTotals()} instead.
     * @return never null
     * @throws IllegalStateException if {@link #isConstraintMatchEnabled()} is false
     * @see ScoreDirector#getConstraintMatchTotals()
     */
    Collection<ConstraintMatchTotal> getConstraintMatchTotals();

    /**
     * Explains the {@link Score} of {@link #extractScore(int)}.
     * <p>
     * Should not be called directly, use {@link ScoreDirector#getConstraintMatchTotalMap()} instead.
     * @return never null
     * @throws IllegalStateException if {@link #isConstraintMatchEnabled()} is false
     * @see ScoreDirector#getConstraintMatchTotalMap()
     */
    Map<String, ConstraintMatchTotal> getConstraintMatchTotalMap();

    /**
     * Explains the impact of each planning entity or problem fact on the {@link Score}.
     * <p>
     * Should not be called directly, use {@link ScoreDirector#getIndictmentMap()} instead.
     * @return never null
     * @throws IllegalStateException if {@link #isConstraintMatchEnabled()} returns false
     * @see ScoreDirector#getIndictmentMap()
     */
    Map<Object, Indictment> getIndictmentMap();

}
