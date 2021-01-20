/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.core.model.solver;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper class for manging priority calculations for tasks coming from the jBPM runtime.
 */
public class PriorityHelper {

    private static final String HIGH_LEVEL_PRIORITY_RANGE = "org.kie.kogito.taskassigning.core.model.solver.priorityHelper.highLevelPriorities";
    private static final String MEDIUM_LEVEL_PRIORITY_RANGE = "org.kie.kogito.taskassigning.core.model.solver.priorityHelper.mediumLevelPriorities";
    private static final String LOW_LEVEL_PRIORITY_RANGE = "org.kie.kogito.taskassigning.core.model.solver.priorityHelper.lowLevelPriorities";

    private static final Set<String> HIGH_LEVEL_PRIORITIES = buildRange(System.getProperty(HIGH_LEVEL_PRIORITY_RANGE, "0,1,2"));
    private static final Set<String> MEDIUM_LEVEL_PRIORITIES = buildRange(System.getProperty(MEDIUM_LEVEL_PRIORITY_RANGE, "3,4,5,6"));
    private static final Set<String> LOW_LEVEL_PRIORITIES = buildRange(System.getProperty(LOW_LEVEL_PRIORITY_RANGE, "7,8,9,10"));

    private PriorityHelper() {
    }

    public static boolean isHighLevel(String priority) {
        return HIGH_LEVEL_PRIORITIES.contains(priority);
    }

    public static boolean isMediumLevel(String priority) {
        return MEDIUM_LEVEL_PRIORITIES.contains(priority);
    }

    public static boolean isLowLevel(String priority) {
        return LOW_LEVEL_PRIORITIES.contains(priority);
    }

    private static Set<String> buildRange(String tokens) {
        return Arrays.stream(tokens.split(",")).collect(Collectors.toSet());
    }
}
