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

package org.optaplanner.core.api.domain.variable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;

/**
 * Specifies that a bean property (or a field) is a custom shadow of 1 or more {@link PlanningVariable}'s.
 * <p>
 * It is specified on a getter of a java bean property (or a field) of a {@link PlanningEntity} class.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface CustomShadowVariable {

    /**
     * A {@link VariableListener} gets notified after a source planning variable has changed.
     * That listener changes the shadow variable (often recursively on multiple planning entities) accordingly,
     * Those shadow variables should make the score calculation more natural to write.
     * <p>
     * For example: VRP with time windows uses a {@link VariableListener} to update the arrival times
     * of all the trailing entities when an entity is changed.
     *
     * @return never null (unless {@link #variableListenerRef()} is not null)
     */
    Class<? extends VariableListener> variableListenerClass() default NullVariableListener.class;

    /** Workaround for annotation limitation in {@link #variableListenerClass()}. */
    interface NullVariableListener extends VariableListener {
    }

    /**
     * The source variables (masters) that trigger a change to this shadow variable (slave).
     *
     * @return never null (unless {@link #variableListenerRef()} is not null), at least 1
     */
    PlanningVariableReference[] sources() default {};

    /**
     * Use this when this shadow variable is updated by the {@link VariableListener} of another {@link CustomShadowVariable}.
     *
     * @return null if (and only if) any of the other fields is non null.
     */
    PlanningVariableReference variableListenerRef() default @PlanningVariableReference(variableName = "");

}
