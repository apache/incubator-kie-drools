/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;

class ProblemBenchmarksConfigTest {

    @Test
    void testDetermineProblemStatisticTypeList() {
        ProblemBenchmarksConfig problemBenchmarksConfig = new ProblemBenchmarksConfig();
        assertThat(problemBenchmarksConfig.determineProblemStatisticTypeList())
                .isEqualTo(ProblemStatisticType.defaultList());
        problemBenchmarksConfig.setProblemStatisticTypeList(Collections.emptyList());
        assertThat(problemBenchmarksConfig.determineProblemStatisticTypeList())
                .isEqualTo(ProblemStatisticType.defaultList());
        problemBenchmarksConfig.setProblemStatisticTypeList(List.of(ProblemStatisticType.MEMORY_USE));

        // This assert is to verify the assert below is testing against a different list from the default
        assertThat(List.of(ProblemStatisticType.MEMORY_USE)).isNotEqualTo(ProblemStatisticType.defaultList());

        assertThat(problemBenchmarksConfig.determineProblemStatisticTypeList())
                .isEqualTo(List.of(ProblemStatisticType.MEMORY_USE));

        problemBenchmarksConfig.setProblemStatisticEnabled(true);
        assertThat(problemBenchmarksConfig.determineProblemStatisticTypeList())
                .isEqualTo(List.of(ProblemStatisticType.MEMORY_USE));

        problemBenchmarksConfig.setProblemStatisticEnabled(false);
        assertThat(problemBenchmarksConfig.determineProblemStatisticTypeList()).isEqualTo(Collections.emptyList());
    }

    @Test
    void testDetermineSingleStatisticTypeList() {
        ProblemBenchmarksConfig problemBenchmarksConfig = new ProblemBenchmarksConfig();
        assertThat(problemBenchmarksConfig.determineSingleStatisticTypeList()).isEqualTo(Collections.emptyList());
        problemBenchmarksConfig.setSingleStatisticTypeList(Collections.emptyList());
        assertThat(problemBenchmarksConfig.determineSingleStatisticTypeList()).isEqualTo(Collections.emptyList());
        problemBenchmarksConfig.setSingleStatisticTypeList(List.of(SingleStatisticType.CONSTRAINT_MATCH_TOTAL_STEP_SCORE));
        assertThat(problemBenchmarksConfig.determineSingleStatisticTypeList())
                .isEqualTo(List.of(SingleStatisticType.CONSTRAINT_MATCH_TOTAL_STEP_SCORE));
    }
}
