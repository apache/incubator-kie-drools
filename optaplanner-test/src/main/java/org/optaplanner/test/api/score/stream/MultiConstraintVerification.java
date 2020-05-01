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

public interface MultiConstraintVerification<Solution_> {

    /**
     * As defined by {@link SingleConstraintVerification#given(Object...)}.
     *
     * @param facts never null, at least one
     * @return never null
     */
    MultiConstraintAssertion given(Object... facts);

    /**
     * As defined by {@link SingleConstraintVerification#givenSolution(Object)}.
     *
     * @param solution never null
     * @return never null
     */
    MultiConstraintAssertion givenSolution(Solution_ solution);

}
