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

package org.optaplanner.core.api.domain.variable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

/**
 * Specifies that a bean property (or a field) is a custom shadow variable that is updated by another shadow variable's
 * variable listener.
 * <p>
 * It is specified on a getter of a java bean property (or a field) of a {@link PlanningEntity} class.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PiggybackShadowVariable {

    /**
     * The {@link PlanningEntity} class of the shadow variable with a variable listener.
     * <p>
     * Specified if the referenced shadow variable is on a different {@link Class} than the class that uses this annotation.
     *
     * @return {@link NullEntityClass} when it is null (workaround for annotation limitation).
     *         Defaults to the same {@link Class} as the one that uses this annotation.
     */
    Class<?> shadowEntityClass() default NullEntityClass.class;

    /**
     * The shadow variable name.
     *
     * @return never null, a genuine or shadow variable name
     */
    String shadowVariableName();

    /** Workaround for annotation limitation in {@link #shadowEntityClass()}. */
    interface NullEntityClass {
    }
}
