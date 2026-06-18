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

package org.optaplanner.core.api.score.stream;

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.Score;

/**
 * Used by Constraint Streams' {@link Score} calculation.
 * An implementation must be stateless in order to facilitate building a single set of constraints
 * independent of potentially changing constraint weights.
 */
public interface ConstraintProvider {

    /**
     * This method is called once to create the constraints.
     * To create a {@link Constraint}, start with {@link ConstraintFactory#forEach(Class)}.
     *
     * @param constraintFactory never null
     * @return an array of all {@link Constraint constraints} that could apply.
     *         The constraints with a zero {@link ConstraintWeight} for a particular problem
     *         will be automatically disabled when scoring that problem, to improve performance.
     */
    Constraint[] defineConstraints(ConstraintFactory constraintFactory);

}
