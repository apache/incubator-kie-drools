/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.domain.constraintweight;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.score.Score;

/**
 * Specifies that a bean property (or a field) set the constraint weight and score level of a constraint.
 * For example, with a constraint weight of {@code 2soft},
 * a constraint match penalization with weightMultiplier {@code 3}
 * will result in a {@link Score} of {@code -6soft}.
 * <p>
 * It is specified on a getter of a java bean property (or directly on a field) of a {@link ConstraintConfiguration} class.
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
public @interface ConstraintWeight {

    /**
     * The constraint package is the namespace of the constraint.
     * <p>
     * The constraint id is this constraint package
     * concatenated with "/" and {@link #value() the constraint name}.
     *
     * @return defaults to {@link ConstraintConfiguration#constraintPackage()}
     */
    String constraintPackage() default "";

    /**
     * The constraint name.
     * <p>
     * The constraint id is {@link #constraintPackage() the constraint package}
     * concatenated with "/" and this constraint name.
     *
     * @return never null, often a constant that is used by the constraints too, because they need to match.
     */
    String value();

}
