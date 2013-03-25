/*
 * Copyright 2010 JBoss Inc
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

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.solution.Solution;

/**
 * This class is injected as a global by {@link DroolsScoreDirector} into the Drools DRL.
 * Other {@link ScoreDirector} implementations do not use this class.
 * @see AbstractScoreHolder
 */
public interface ScoreHolder {

    /**
     * Extracts the {@link Score}, calculated by the {@link KieSession} for {@link DroolsScoreDirector}.
     * </p>
     * Should not be called directly, use {@link ScoreDirector#calculateScore()} instead.
     * @return never null, the  {@link Score} of the working {@link Solution}
     */
    Score extractScore();

}
