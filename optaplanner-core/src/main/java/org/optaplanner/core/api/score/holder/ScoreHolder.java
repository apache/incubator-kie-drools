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

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;

/**
 * This class is injected as a global by {@link DroolsScoreDirector} into the Drools DRL.
 * Other {@link ScoreDirector} implementations do not use this class.
 * <p>
 * An implementation must extend {@link AbstractScoreHolder} to ensure backwards compatibility in future versions.
 * @see AbstractScoreHolder
 */
public interface ScoreHolder {

    /**
     * Extracts the {@link Score}, calculated by the {@link KieSession} for {@link DroolsScoreDirector}.
     * <p>
     * Should not be called directly, use {@link ScoreDirector#calculateScore()} instead.
     * @return never null, the  {@link Score} of the working {@link Solution}
     */
    Score extractScore();

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
     * Explains the {@link Score} of {@link #extractScore()}.
     * @return never null
     * @throws RuntimeException if {@link #isConstraintMatchEnabled()} is false
     */
    Collection<ConstraintMatchTotal> getConstraintMatchTotals();

}
