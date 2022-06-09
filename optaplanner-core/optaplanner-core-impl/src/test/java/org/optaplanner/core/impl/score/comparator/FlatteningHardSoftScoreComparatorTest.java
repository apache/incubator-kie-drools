package org.optaplanner.core.impl.score.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.impl.score.buildin.HardSoftScoreDefinition;

class FlatteningHardSoftScoreComparatorTest {

    static Collection parameters() {
        String simpleScore = "10hard/123soft";
        String lowHardScore = "10hard/987654321soft";
        String highHardScore = "987654321hard/123soft";
        return Arrays.asList(new Object[][] {
                { 0, 0, simpleScore, simpleScore }, // 0 - comparison according to soft score
                { 0, 1, simpleScore, simpleScore }, // 1 - no changes
                { 0, 1024, simpleScore, simpleScore }, // "huge" modifier
                { -1, 0, simpleScore, lowHardScore },
                { -1, 1, simpleScore, lowHardScore },
                { -1, 1024, simpleScore, lowHardScore },
                { 1, 0, lowHardScore, simpleScore },
                { 1, 1, lowHardScore, simpleScore },
                { 1, 1024, lowHardScore, simpleScore },
                { 1, 0, lowHardScore, highHardScore },
                { -1, 1, lowHardScore, highHardScore },
                { -1, 1024, lowHardScore, highHardScore },
                { -1, 0, highHardScore, lowHardScore },
                { 1, 1, highHardScore, lowHardScore },
                { 1, 1024, highHardScore, lowHardScore },
                { 0, 0, highHardScore, simpleScore },
                { 1, 1, highHardScore, simpleScore },
                { 1, 1024, highHardScore, simpleScore }
        });
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("parameters")
    void compare(int expectedResult, int modifier, String firstScore, String secondScore) {
        assertThat(new FlatteningHardSoftScoreComparator(modifier)
                .compare(new HardSoftScoreDefinition().parseScore(firstScore),
                        new HardSoftScoreDefinition().parseScore(secondScore))).isEqualTo(expectedResult);
    }
}
