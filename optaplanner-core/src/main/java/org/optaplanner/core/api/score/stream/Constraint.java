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

package org.optaplanner.core.api.score.stream;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;

/**
 * This represents a single constraint in the {@link ConstraintStream} API
 * that impacts the {@link Score}.
 * It is defined in {@link ConstraintProvider#defineConstraints(ConstraintFactory)}
 * by calling {@link ConstraintFactory#from(Class)}.
 */
public interface Constraint {

    /**
     * The {@link ConstraintFactory} that build this.
     * @return never null
     */
    ConstraintFactory getConstraintFactory();

    /**
     * The constraint package is the namespace of the constraint.
     * <p>
     * When using a {@link ConstraintConfiguration},
     * it is equal to the {@link ConstraintWeight#constraintPackage()}.
     * @return never null
     */
    String getConstraintPackage();

    /**
     * The constraint name.
     * It might not be unique, but {@link #getConstraintId()} is unique.
     * <p>
     * When using a {@link ConstraintConfiguration},
     * it is equal to the {@link ConstraintWeight#value()}.
     * @return never null
     */
    String getConstraintName();

    /**
     * The constraint id is {@link #getConstraintPackage() the constraint package}
     * concatenated with "/" and {@link #getConstraintName() the constraint name}.
     * It is unique.
     * @return never null
     */
    default String getConstraintId() {
        return ConstraintMatchTotal.composeConstraintId(getConstraintPackage(), getConstraintName());
    }

}
