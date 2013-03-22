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

package org.optaplanner.core.impl.score.director.incremental;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.simple.SimpleScoreCalculator;
import org.optaplanner.core.impl.solution.Solution;

/**
 * Used for incremental java {@link Score} calculation.
 * This is much faster than {@link SimpleScoreCalculator} but requires much more code to implement too.
 * <p/>
 * Any implementation is naturally stateful.
 * @param <Sol>
 * @see IncrementalScoreDirector
 */
public interface IncrementalScoreCalculator<Sol extends Solution> {

    void resetWorkingSolution(Sol workingSolution);

    void beforeEntityAdded(Object entity);

    void afterEntityAdded(Object entity);

    void beforeAllVariablesChanged(Object entity);

    void afterAllVariablesChanged(Object entity);

    void beforeVariableChanged(Object entity, String variableName);

    void afterVariableChanged(Object entity, String variableName);

    void beforeEntityRemoved(Object entity);

    void afterEntityRemoved(Object entity);

    /**
     * @return never null
     */
    Score calculateScore();

    /**
     * @param uncorruptedIncrementalScoreCalculator never null
     * @return null if this {@link IncrementalScoreCalculator} implementation does not support this.
     */
    String buildScoreCorruptionAnalysis(IncrementalScoreCalculator uncorruptedIncrementalScoreCalculator);

}
