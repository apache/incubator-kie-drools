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

package org.optaplanner.test.api.score.stream;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public interface MultiConstraintAssertion {

    /**
     * Asserts that the {@link ConstraintProvider} under test, given a set of facts, results in a specific {@link Score}.
     *
     * @param score total score calculated for the given set of facts
     * @throws AssertionError when the expected score does not match the calculated score
     */
    default void scores(Score<?> score) {
        scores(score, null);
    }

    /**
     * As defined by {@link #scores(Score)}.
     *
     * @param score total score calculated for the given set of facts
     * @param message sometimes null, description of the scenario being asserted
     * @throws AssertionError when the expected score does not match the calculated score
     */
    void scores(Score<?> score, String message);

}
