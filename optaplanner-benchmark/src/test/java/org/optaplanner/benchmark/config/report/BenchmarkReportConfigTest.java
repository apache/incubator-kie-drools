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

package org.optaplanner.benchmark.config.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.config.ranking.SolverRankingType;
import org.optaplanner.benchmark.impl.ranking.TotalRankSolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.ranking.TotalScoreSolverRankingComparator;

class BenchmarkReportConfigTest {

    @Test
    void inheritBenchmarkReportConfig() {
        BenchmarkReportConfig inheritedReportConfig = new BenchmarkReportConfig();
        inheritedReportConfig.setLocale(Locale.CANADA);
        inheritedReportConfig.setSolverRankingType(SolverRankingType.TOTAL_RANKING);
        inheritedReportConfig.setSolverRankingComparatorClass(TotalScoreSolverRankingComparator.class);
        inheritedReportConfig.setSolverRankingWeightFactoryClass(TotalRankSolverRankingWeightFactory.class);

        BenchmarkReportConfig reportConfig = new BenchmarkReportConfig(inheritedReportConfig);

        assertThat(reportConfig.getLocale()).isEqualTo(inheritedReportConfig.getLocale());
        assertThat(reportConfig.getSolverRankingType()).isEqualTo(inheritedReportConfig.getSolverRankingType());
        assertThat(reportConfig.getSolverRankingComparatorClass())
                .isEqualTo(inheritedReportConfig.getSolverRankingComparatorClass());
        assertThat(reportConfig.getSolverRankingWeightFactoryClass())
                .isEqualTo(inheritedReportConfig.getSolverRankingWeightFactoryClass());
    }
}
