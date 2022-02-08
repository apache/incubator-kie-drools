/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.domain.variable;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

/**
 * A reference to a genuine {@link PlanningVariable} or a shadow variable.
 */
public @interface PlanningVariableReference {

    /**
     * The {@link PlanningEntity} class of the planning variable.
     * <p>
     * Specified if the planning variable is on a different {@link Class}
     * than the class that uses this referencing annotation.
     *
     * @return {@link NullEntityClass} when it is null (workaround for annotation limitation).
     *         Defaults to the same {@link Class} as the one that uses this annotation.
     */
    Class<?> entityClass() default NullEntityClass.class;

    /** Workaround for annotation limitation in {@link #entityClass()}. */
    interface NullEntityClass {
    }

    /**
     * The name of the planning variable that is referenced.
     *
     * @return never null, a genuine or shadow variable name
     */
    String variableName();

}
