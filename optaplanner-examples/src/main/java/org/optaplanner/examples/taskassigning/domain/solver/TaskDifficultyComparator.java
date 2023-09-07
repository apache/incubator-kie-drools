/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.taskassigning.domain.solver;

import java.util.Comparator;

import org.optaplanner.examples.taskassigning.domain.Task;

/**
 * Compares tasks by difficulty.
 */
public class TaskDifficultyComparator implements Comparator<Task> {
    // FIXME This class is currently unused until the @PlanningListVariable(comparator = ???) API is stable.
    //  See https://issues.redhat.com/browse/PLANNER-2542.

    static final Comparator<Task> INCREASING_DIFFICULTY_COMPARATOR = Comparator.comparing(Task::getPriority)
            .thenComparingInt(task -> task.getTaskType().getRequiredSkillList().size())
            .thenComparingInt(task -> task.getTaskType().getBaseDuration())
            .thenComparingLong(Task::getId);

    static final Comparator<Task> DECREASING_DIFFICULTY_COMPARATOR = INCREASING_DIFFICULTY_COMPARATOR.reversed();

    @Override
    public int compare(Task a, Task b) {
        return DECREASING_DIFFICULTY_COMPARATOR.compare(a, b);
    }
}
