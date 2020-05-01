/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.examination.app;

import java.util.stream.Stream;

import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.examination.domain.Examination;

public class ExaminationPerformanceTest extends SolverPerformanceTest<Examination> {

    private static final String UNSOLVED_DATA_FILE = "data/examination/unsolved/exam_comp_set5.xml";

    @Override
    protected ExaminationApp createCommonApp() {
        return new ExaminationApp();
    }

    @Override
    protected Stream<TestData> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, "0hard/-4393soft", EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, "0hard/-4407soft", EnvironmentMode.FAST_ASSERT));
    }
}
