/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.taskassigning.core.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.function.Predicate;

public class ModelConstants {

    private ModelConstants() {
    }

    private static final ZonedDateTime DUMMY_DATE = ZonedDateTime.parse("2021-01-01T01:01:01.001Z",
                                                                        DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    /**
     * System property for configuring the PLANNING_USER entityId.
     */
    public static final String PLANNING_USER_ID_PROPERTY = "org.kie.kogito.taskassigning.core.model.planningUserId";

    public static final String PLANNING_USER_ID = System.getProperty(PLANNING_USER_ID_PROPERTY, "planninguser");

    /**
     * Planning user is defined user for avoid breaking hard constraints. When no user is found that met the task required
     * potential owners set, or the required skills set, etc, the PLANNING_USER is assigned.
     */
    public static final User PLANNING_USER = new ImmutableUser(PLANNING_USER_ID, true, Collections.emptySet(), Collections.emptyMap());

    public static final Predicate<String> IS_PLANNING_USER = entityId -> PLANNING_USER.getId().equals(entityId);

    /**
     * This task was introduced for dealing with situations where the solution ends up with no tasks. e.g. there is a
     * solution with tasks A and B, and a user completes both tasks in the kogito runtime. When the completion events
     * are processed both tasks are removed from the solution with the proper problem fact changes. The solution remains
     * thus with no tasks and an exception is thrown.
     * Since the only potential owner for the dummy task is the PLANNING_USER this task won't affect the score dramatically.
     */
    public static final TaskAssignment DUMMY_TASK_ASSIGNMENT = new ImmutableTaskAssignment(new ImmutableTask("-1",
                                                                                                             "dummy-task",
                                                                                                             "dummy-state",
                                                                                                             "dummy-description",
                                                                                                             "dummy-reference-name",
                                                                                                             "1",
                                                                                                             "dummy-process-instance-id",
                                                                                                             "dummy-process-id",
                                                                                                             "dummy-root-process-id",
                                                                                                             "dummy-root-process-id",
                                                                                                             DUMMY_DATE,
                                                                                                             DUMMY_DATE,
                                                                                                             DUMMY_DATE,
                                                                                                             "dummy-endpoint"),
                                                                                           false);
}
