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

package org.kie.kogito.taskassigning.core.model;

import java.util.Collections;
import java.util.function.Predicate;

public class ModelConstants {

    private ModelConstants() {
    }

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
}
