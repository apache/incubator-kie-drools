/*
 * Copyright 2014 JBoss Inc
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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that a bean property is a custom shadow of 1 or more {@link PlanningVariable}'s.
 * <p/>
 * It is specified on a getter of a java bean property of a {@link PlanningEntity} class.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface CustomShadowVariable {

    /**
     * A {@link VariableListener} gets notified after a source planning variable has changed.
     * That listener changes the shadow variable (often recursively on multiple planning entities) accordingly,
     * Those shadow variables should make the score calculation more natural to write.
     * <p/>
     * For example: VRP with time windows uses a {@link VariableListener} to update the arrival times
     * of all the trailing entities when an entity is changed.
     * @return never null
     */
    Class<? extends VariableListener> variableListenerClass();

    /**
     * The source variables (masters) that trigger a change to this shadow variable (slave).
     * @return never null, at least 1
     */
    Source[] sources();

    /**
     * Declares which genuine variable (or other shadow variable) causes the shadow variable to change.
     */
    public static @interface Source {

        /**
         * Specified if the source variable is on a different {@link Class} than the shadow variable.
         * @return {@link NullEntityClass} when it is null (workaround for annotation limitation).
         * Defaults to the same {@link Class} as the one that contains the {@link CustomShadowVariable} annotation.
         */
        Class<?> entityClass() default NullEntityClass.class;

        interface NullEntityClass {}

        /**
         * A source variable that causes the shadow variable to change.
         * @return never null, a genuine or shadow variable
         */
        String variableName();

    }

}
