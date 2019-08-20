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

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirector;

/**
 * WARNING: The ConstraintStreams/ConstraintProvider API is TECH PREVIEW.
 * It works but it has many API gaps.
 * Therefore, it is not rich enough yet to handle complex constraints.
 * <p>
 * Used by constraint stream {@link Score} calculation.
 * <p>
 * An implementation must be stateless.
 * @see ConstraintStreamScoreDirector
 */
public interface ConstraintProvider {

    /**
     * This method is called once to create the constraints.
     * To create a {@link Constraint}, start with {@link ConstraintFactory#from(Class)}.
     * @param constraintFactory never null
     * @return an array of all {@link Constraint constraints} that could apply.
     * The constraints with a zero {@link ConstraintWeight} for a particular problem
     * will be automatically disabled when scoring that problem, to improve performance.
     */
    Constraint[] defineConstraints(ConstraintFactory constraintFactory);

}
