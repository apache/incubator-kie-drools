package org.optaplanner.core.impl.score.comparator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FlatteningHardSoftScoreComparatorTest {

    private int expectedResult;
    private int modifier;
    private String firstScore;
    private String secondScore;

    public FlatteningHardSoftScoreComparatorTest(int expectedResult, int modifier, String firstScore, String secondScore) {
        this.expectedResult = expectedResult;
        this.modifier = modifier;
        this.firstScore = firstScore;
        this.secondScore = secondScore;
    }

    @Parameterized.Parameters
    public static Collection parameters() {
        String simpleScore = "10hard/123soft";
        String lowHardScore = "10hard/987654321soft";
        String highHardScore = "987654321hard/123soft";
        return Arrays.asList(new Object[][]{
                {0, 0, simpleScore, simpleScore}, // 0 - comparison according to soft score
                {0, 1, simpleScore, simpleScore}, // 1 - no changes
                {0, 1024, simpleScore, simpleScore}, // "huge" modifier
                {-1, 0, simpleScore, lowHardScore},
                {-1, 1, simpleScore, lowHardScore},
                {-1, 1024, simpleScore, lowHardScore},
                {1, 0, lowHardScore, simpleScore},
                {1, 1, lowHardScore, simpleScore},
                {1, 1024, lowHardScore, simpleScore},
                {1, 0, lowHardScore, highHardScore},
                {-1, 1, lowHardScore, highHardScore},
                {-1, 1024, lowHardScore, highHardScore},
                {-1, 0, highHardScore, lowHardScore},
                {1, 1, highHardScore, lowHardScore},
                {1, 1024, highHardScore, lowHardScore},
                {0, 0, highHardScore, simpleScore},
                {1, 1, highHardScore, simpleScore},
                {1, 1024, highHardScore, simpleScore}
        });
    }

    @Test
    public void compare() {
        assertEquals(expectedResult, new FlatteningHardSoftScoreComparator(modifier)
                .compare(new HardSoftScoreDefinition().parseScore(firstScore),
                        new HardSoftScoreDefinition().parseScore(secondScore)));
    }
}
