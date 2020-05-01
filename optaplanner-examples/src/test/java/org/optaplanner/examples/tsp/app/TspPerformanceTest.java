/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.tsp.app;

import java.util.stream.Stream;

import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.tsp.domain.TspSolution;

public class TspPerformanceTest extends SolverPerformanceTest<TspSolution> {

    private static final String UNSOLVED_DATA_FILE = "data/tsp/unsolved/europe40.xml";

    @Override
    protected TspApp createCommonApp() {
        return new TspApp();
    }

    @Override
    protected Stream<TestData> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, "-217957000", EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, "-219637000", EnvironmentMode.FAST_ASSERT));
    }
}
