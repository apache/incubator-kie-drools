/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.solution.director;

import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;

/**
 * A SolutionDirector hold a workingSolution and directs the Rule Engine to calculate the {@link Score}
 * of that {@link Solution}.
 */
public interface SolutionDirector {

    /**
     * The {@link Solution} that is used in the {@link WorkingMemory}.
     * <p/>
     * If the {@link Solution} has been changed since {@link #calculateScoreFromWorkingMemory} has been called,
     * the {@link Solution#getScore()} of this {@link Solution} won't be correct.
     * @return never null
     */
    Solution getWorkingSolution();

    /**
     * @return never null
     */
    WorkingMemory getWorkingMemory();

    /**
     * Calculates the {@Score} and updates the workingSolution accordingly.
     * @return never null, the score of the working solution
     */
    Score calculateScoreFromWorkingMemory();

    Map<Object, List<Object>> getVariableToEntitiesMap(PlanningVariableDescriptor variableDescriptor);

}
