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

package org.drools.planner.core.score.buildin.hardandsoft;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;

/**
 * A HardAndSoftScore is a Score based on hard and soft int constraints.
 * Hard constraints have priority over soft constraints.
 * <p/>
 * Implementations must be immutable.
 * @see Score
 * @see DefaultHardAndSoftScore
 */
public interface HardAndSoftScore extends Score<HardAndSoftScore> {

    /**
     * The total of the broken negative hard constraints and fulfilled positive hard constraints.
     * Their weight is included in the total.
     * The hard score is usually a negative number because most use cases only have negative constraints.
     * @return higher is better, usually negative, 0 if no hard constraints are broken/fulfilled
     */
    int getHardScore();

    /**
     * The total of the broken negative soft constraints and fulfilled positive soft constraints.
     * Their weight is included in the total.
     * The soft score is usually a negative number because most use cases only have negative constraints.
     * <p/>
     * In a normal score comparison, the soft score is irrelevant if the 2 scores don't have the same hard score.
     * @return higher is better, usually negative, 0 if no soft constraints are broken/fulfilled
     */
    int getSoftScore();

    /**
     * A {@link Solution} is feasible if it has no broken hard constraints.
     * @return true if the {@link #getHardScore()} is 0 or higher
     */
    boolean isFeasible();

}
