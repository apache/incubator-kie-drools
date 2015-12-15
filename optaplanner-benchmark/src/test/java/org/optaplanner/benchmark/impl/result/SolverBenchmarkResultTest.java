/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.impl.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SolverBenchmarkResultTest {

    @Test
    public void testGetAverageScoreWithUninitializedPrefix() throws Exception {
        SolverBenchmarkResult solverBenchmarkResult = spy(new SolverBenchmarkResult(null));
        SingleBenchmarkResult singleBenchmarkResult1 = mock(SingleBenchmarkResult.class);
        SingleBenchmarkResult singleBenchmarkResult2 = mock(SingleBenchmarkResult.class);
        when(solverBenchmarkResult.getSingleBenchmarkResultList()).thenReturn(Arrays.asList(singleBenchmarkResult1, singleBenchmarkResult2));
        when(solverBenchmarkResult.getAverageScore()).thenReturn(HardSoftScore.valueOf(-10, -100));
        when(solverBenchmarkResult.getTotalUninitializedVariableCount()).thenReturn(0);
        when(solverBenchmarkResult.getFailureCount()).thenReturn(0);
        assertEquals("-10hard/-100soft", solverBenchmarkResult.getAverageScoreWithUninitializedPrefix());
        when(solverBenchmarkResult.getTotalUninitializedVariableCount()).thenReturn(2);
        assertEquals("1uninitialized/-10hard/-100soft", solverBenchmarkResult.getAverageScoreWithUninitializedPrefix());
        when(solverBenchmarkResult.getTotalUninitializedVariableCount()).thenReturn(3);
        assertEquals("2uninitialized/-10hard/-100soft", solverBenchmarkResult.getAverageScoreWithUninitializedPrefix());
        when(solverBenchmarkResult.getTotalUninitializedVariableCount()).thenReturn(1);
        assertEquals("1uninitialized/-10hard/-100soft", solverBenchmarkResult.getAverageScoreWithUninitializedPrefix());
    }

    @Test
    public void testGetAverageScoreWithUninitializedPrefixWithFailure() throws Exception {
        SolverBenchmarkResult solverBenchmarkResult = spy(new SolverBenchmarkResult(null));
        SingleBenchmarkResult singleBenchmarkResult1 = mock(SingleBenchmarkResult.class);
        SingleBenchmarkResult singleBenchmarkResult2 = mock(SingleBenchmarkResult.class);
        when(solverBenchmarkResult.getSingleBenchmarkResultList()).thenReturn(Arrays.asList(singleBenchmarkResult1, singleBenchmarkResult2));
        when(solverBenchmarkResult.getAverageScore()).thenReturn(HardSoftScore.valueOf(-10, -100));
        when(solverBenchmarkResult.getTotalUninitializedVariableCount()).thenReturn(2);
        when(solverBenchmarkResult.getFailureCount()).thenReturn(1);
        assertEquals("2uninitialized/-10hard/-100soft", solverBenchmarkResult.getAverageScoreWithUninitializedPrefix());
        when(solverBenchmarkResult.getFailureCount()).thenReturn(0);
        assertEquals("1uninitialized/-10hard/-100soft", solverBenchmarkResult.getAverageScoreWithUninitializedPrefix());
        when(solverBenchmarkResult.getFailureCount()).thenReturn(2);
        assertEquals(null, solverBenchmarkResult.getAverageScoreWithUninitializedPrefix());
    }
}
