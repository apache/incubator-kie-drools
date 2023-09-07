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

package org.optaplanner.core.impl.partitionedsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class DefaultPartitionedSearchPhaseFactoryTest {

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "4, 2"
    })
    void resolvedActiveThreadCountAuto(int availableCpuCount, int expectedResolvedCpuCount) {
        assertThat(mockResolveActiveThreadCount(PartitionedSearchPhaseConfig.ACTIVE_THREAD_COUNT_AUTO, availableCpuCount))
                .isEqualTo(expectedResolvedCpuCount);
    }

    @Test
    void resolveActiveThreadCountUnlimited() {
        assertThat(createDefaultPartitionedSearchPhaseFactory()
                .resolveActiveThreadCount(PartitionedSearchPhaseConfig.ACTIVE_THREAD_COUNT_UNLIMITED)).isNull();
    }

    private Integer mockResolveActiveThreadCount(String runnablePartThreadLimit, int cpuCount) {
        DefaultPartitionedSearchPhaseFactory<TestdataSolution> partitionedSearchPhaseFactory =
                spy(createDefaultPartitionedSearchPhaseFactory());

        when(partitionedSearchPhaseFactory.getAvailableProcessors()).thenReturn(cpuCount);
        return partitionedSearchPhaseFactory.resolveActiveThreadCount(runnablePartThreadLimit);
    }

    private DefaultPartitionedSearchPhaseFactory<TestdataSolution> createDefaultPartitionedSearchPhaseFactory() {
        PartitionedSearchPhaseConfig phaseConfig = new PartitionedSearchPhaseConfig();
        return new DefaultPartitionedSearchPhaseFactory<>(phaseConfig);
    }

    @Test
    void assertionsForNonIntrusiveFullAssertMode() {
        DefaultPartitionedSearchPhase partitionedSearchPhase = mockEnvironmentMode(EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT);
        assertThat(partitionedSearchPhase.isAssertStepScoreFromScratch()).isTrue();
        assertThat(partitionedSearchPhase.isAssertExpectedStepScore()).isFalse();
        assertThat(partitionedSearchPhase.isAssertShadowVariablesAreNotStaleAfterStep()).isFalse();
    }

    @Test
    void assertionsForIntrusiveFastAssertMode() {
        DefaultPartitionedSearchPhase partitionedSearchPhase = mockEnvironmentMode(EnvironmentMode.FAST_ASSERT);
        assertThat(partitionedSearchPhase.isAssertStepScoreFromScratch()).isFalse();
        assertThat(partitionedSearchPhase.isAssertExpectedStepScore()).isTrue();
        assertThat(partitionedSearchPhase.isAssertShadowVariablesAreNotStaleAfterStep()).isTrue();
    }

    private DefaultPartitionedSearchPhase<TestdataSolution> mockEnvironmentMode(EnvironmentMode environmentMode) {
        HeuristicConfigPolicy heuristicConfigPolicy = mock(HeuristicConfigPolicy.class);
        when(heuristicConfigPolicy.getEnvironmentMode()).thenReturn(environmentMode);
        // Reuse the same mock as it doesn't matter.
        when(heuristicConfigPolicy.createPhaseConfigPolicy()).thenReturn(heuristicConfigPolicy);

        PartitionedSearchPhaseConfig phaseConfig = new PartitionedSearchPhaseConfig();
        phaseConfig.setSolutionPartitionerClass(TestdataSolutionPartitioner.class);
        DefaultPartitionedSearchPhaseFactory<TestdataSolution> partitionedSearchPhaseFactory =
                new DefaultPartitionedSearchPhaseFactory<>(phaseConfig);
        return (DefaultPartitionedSearchPhase<TestdataSolution>) partitionedSearchPhaseFactory.buildPhase(0,
                heuristicConfigPolicy, mock(BestSolutionRecaller.class), mock(Termination.class));
    }

}
