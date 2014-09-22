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

package org.optaplanner.core.impl.score.director.easy;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;

/**
 * Used for easy java {@link Score} calculation. This is non-incremental calculation, which is slow.
 * <p/>
 * An implementation must be stateless.
 * @param <Sol>
 * @see EasyScoreDirector
 */
public interface EasyScoreCalculator<Sol extends Solution> {

    /**
     * @param solution never null
     * @return never null
     */
    Score calculateScore(Sol solution);
    
}
