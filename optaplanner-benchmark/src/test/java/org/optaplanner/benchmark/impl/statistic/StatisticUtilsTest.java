/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.benchmark.impl.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.optaplanner.benchmark.impl.result.BenchmarkResult;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class StatisticUtilsTest {

    private static final double DELTA = 0.001d;

    @Test
    public void testSingleDetermineStandardDeviationDoubles() throws Exception {
        List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = Arrays.asList(createSubSingleBenchmarkResult(SimpleScore.valueOf(0), 0));
        assertArrayEquals(new double[]{0d}, StatisticUtils.determineStandardDeviationDoubles(subSingleBenchmarkResultList, SimpleScore.valueOf(0), subSingleBenchmarkResultList.size()), DELTA);
    }

    @Test
    public void testMultipleDetermineStandardDeviationDoubles() throws Exception {
        List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = new ArrayList<SubSingleBenchmarkResult>(2);
        subSingleBenchmarkResultList.add(createSubSingleBenchmarkResult(SimpleScore.valueOf(-2), 0));
        subSingleBenchmarkResultList.add(createSubSingleBenchmarkResult(SimpleScore.valueOf(-4), 1));
        assertArrayEquals(new double[]{1d}, StatisticUtils.determineStandardDeviationDoubles(subSingleBenchmarkResultList, SimpleScore.valueOf(-3), subSingleBenchmarkResultList.size()), DELTA);
    }

    @Test
    public void testLargeDetermineStandardDeviationDoubles() throws Exception {
        long[] subSingleBenchmarkScores = new long[]{-19289560268L, -19345935795L, -19715516752L, -19589259253L, -19390707618L, -19641410518L};
        List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = new ArrayList<SubSingleBenchmarkResult>(6);
        SimpleLongScore averageScore = SimpleLongScore.valueOf(0);
        for (int i = 0; i < subSingleBenchmarkScores.length; i++) {
            SimpleLongScore current = SimpleLongScore.valueOf(subSingleBenchmarkScores[i]);
            subSingleBenchmarkResultList.add(createSubSingleBenchmarkResult(current, i));
            averageScore = averageScore.add(current);
        }
        averageScore = averageScore.divide(subSingleBenchmarkScores.length);
        assertArrayEquals(new double[]{160338212.294d}, StatisticUtils.determineStandardDeviationDoubles(subSingleBenchmarkResultList, averageScore, subSingleBenchmarkResultList.size()), DELTA);
    }

    @Test
    public void testGetStandardDeviationString() throws Exception {
        assertEquals(null, StatisticUtils.getStandardDeviationString(null));
        assertEquals("2.0", StatisticUtils.getStandardDeviationString(new double[]{2d}));
        assertEquals("1.41", StatisticUtils.getStandardDeviationString(new double[]{Math.sqrt(2)}));
        assertEquals("1.6E8", StatisticUtils.getStandardDeviationString(new double[]{160338212.294d}));
        assertEquals("2.0E9", StatisticUtils.getStandardDeviationString(new double[]{2000000000d}));
        assertEquals("2.0E10", StatisticUtils.getStandardDeviationString(new double[]{20000000000d}));
    }

    private SubSingleBenchmarkResult createSubSingleBenchmarkResult(Score score, int index) {
        SubSingleBenchmarkResult subSingleBenchmarkResult = spy(new SubSingleBenchmarkResult(null, index));
        when(subSingleBenchmarkResult.getAverageScore()).thenReturn(score);
        when(subSingleBenchmarkResult.hasAllSuccess()).thenReturn(true);
        return subSingleBenchmarkResult;
    }

}
