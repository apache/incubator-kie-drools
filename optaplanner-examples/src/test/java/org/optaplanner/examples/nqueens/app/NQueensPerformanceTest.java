/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nqueens.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.nqueens.domain.NQueens;

public class NQueensPerformanceTest extends SolverPerformanceTest<NQueens, SimpleScore> {

    @Override
    protected NQueensApp createCommonApp() {
        return new NQueensApp();
    }

    @Override
    protected Stream<TestData<SimpleScore>> testData() {
        return Stream.of(
                testData("data/nqueens/unsolved/16queens.xml", SimpleScore.ZERO, EnvironmentMode.REPRODUCIBLE),
                testData("data/nqueens/unsolved/8queens.xml", SimpleScore.ZERO, EnvironmentMode.FAST_ASSERT),
                testData("data/nqueens/unsolved/4queens.xml", SimpleScore.ZERO, EnvironmentMode.FULL_ASSERT));
    }
}
