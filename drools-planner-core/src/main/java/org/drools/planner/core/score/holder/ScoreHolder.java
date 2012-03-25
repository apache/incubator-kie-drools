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

package org.drools.planner.core.score.holder;

import org.drools.planner.core.score.Score;

/**
 * A wrapper for the elements of a Score, injected as a global in the WorkingMemory
 * to avoid a performance problem in Drools Expert with using 2 or more accumulates in the same rule.
 * <p/>
 * TODO JBRULES-2238 remove when the rule that sums the final score can be written as a single rule and {@link ScoreHolder} is dead
 */
public interface ScoreHolder {

    /**
     * Calculates the score: the solution (encountered at a step)
     * with the highest score will be seen as the the best solution.
     * </p>
     * The step score calculation should be kept stable over all steps.
     * </p>
     * When the solution is modified during a Move,
     * the WorkingMemory's FactHandles should have been correctly notified.
     * Before the score is calculated, all rules are fired,
     * which should trigger an update of this instance.
     * @return never null, the score of the solution
     */
    Score calculateScore();

}
