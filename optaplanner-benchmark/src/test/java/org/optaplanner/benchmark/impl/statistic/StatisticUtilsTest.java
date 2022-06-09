package org.optaplanner.benchmark.impl.statistic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;

class StatisticUtilsTest {

    private static final double DELTA = 0.001;

    @Test
    void singleDetermineStandardDeviationDoubles() {
        List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = Arrays
                .asList(createSubSingleBenchmarkResult(SimpleScore.of(0), 0));
        assertThat(StatisticUtils.determineStandardDeviationDoubles(subSingleBenchmarkResultList,
                SimpleScore.of(0), subSingleBenchmarkResultList.size()))
                        .containsSequence(new double[] { 0d }, offset(DELTA));
    }

    @Test
    void multipleDetermineStandardDeviationDoubles() {
        List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = new ArrayList<>(2);
        subSingleBenchmarkResultList.add(createSubSingleBenchmarkResult(SimpleScore.of(-2), 0));
        subSingleBenchmarkResultList.add(createSubSingleBenchmarkResult(SimpleScore.of(-4), 1));
        assertThat(StatisticUtils.determineStandardDeviationDoubles(subSingleBenchmarkResultList,
                SimpleScore.of(-3), subSingleBenchmarkResultList.size()))
                        .containsSequence(new double[] { 1d }, offset(DELTA));
    }

    @Test
    void largeDetermineStandardDeviationDoubles() {
        long[] subSingleBenchmarkScores = new long[] { -19289560268L, -19345935795L, -19715516752L, -19589259253L,
                -19390707618L, -19641410518L };
        List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = new ArrayList<>(6);
        SimpleLongScore averageScore = SimpleLongScore.of(0);
        for (int i = 0; i < subSingleBenchmarkScores.length; i++) {
            SimpleLongScore current = SimpleLongScore.of(subSingleBenchmarkScores[i]);
            subSingleBenchmarkResultList.add(createSubSingleBenchmarkResult(current, i));
            averageScore = averageScore.add(current);
        }
        averageScore = averageScore.divide(subSingleBenchmarkScores.length);
        assertThat(StatisticUtils.determineStandardDeviationDoubles(
                subSingleBenchmarkResultList, averageScore, subSingleBenchmarkResultList.size()))
                        .containsSequence(new double[] { 160338212.294 }, offset(DELTA));
    }

    @Test
    void getStandardDeviationString() {
        assertThat(StatisticUtils.getStandardDeviationString(null)).isEqualTo(null);
        assertThat(StatisticUtils.getStandardDeviationString(new double[] { 2.0 })).isEqualTo("2.0");
        assertThat(StatisticUtils.getStandardDeviationString(new double[] { Math.sqrt(2.0) })).isEqualTo("1.41");
        assertThat(StatisticUtils.getStandardDeviationString(new double[] { 160338212.294 })).isEqualTo("1.6E8");
        assertThat(StatisticUtils.getStandardDeviationString(new double[] { 2000000000.0 })).isEqualTo("2.0E9");
        assertThat(StatisticUtils.getStandardDeviationString(new double[] { 20000000000.0 })).isEqualTo("2.0E10");
    }

    private SubSingleBenchmarkResult createSubSingleBenchmarkResult(Score score, int index) {
        SubSingleBenchmarkResult subSingleBenchmarkResult = spy(new SubSingleBenchmarkResult(null, index));
        when(subSingleBenchmarkResult.getAverageScore()).thenReturn(score);
        when(subSingleBenchmarkResult.hasAllSuccess()).thenReturn(true);
        return subSingleBenchmarkResult;
    }

}
