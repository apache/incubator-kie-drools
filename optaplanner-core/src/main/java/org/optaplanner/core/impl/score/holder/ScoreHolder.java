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

package org.optaplanner.core.impl.score.holder;

import org.optaplanner.core.impl.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.solution.Solution;

/**
 * A workaround class that wraps parts of a {@link Score}.
 * It injected as a global by {@link DroolsScoreDirector} in the {@link org.drools.core.WorkingMemory}
 * to avoid a performance problem in Drools Expert with using 2 or more accumulates in the same rule.
 * Other {@link ScoreDirector} implementations do not use this class.
 * <p/>
 * TODO JBRULES-2238 remove this class when the rule that sums the final score can be written as a single rule and it is dead
 */
public interface ScoreHolder {

    /**
     * Extracts the {@link Score}, calculated by the {@link org.drools.core.WorkingMemory} for {@link DroolsScoreDirector}.
     * </p>
     * Should not be called directly, use {@link ScoreDirector#calculateScore()} instead.
     * @return never null, the  {@link Score} of the working {@link Solution}
     */
    Score extractScore();

}
