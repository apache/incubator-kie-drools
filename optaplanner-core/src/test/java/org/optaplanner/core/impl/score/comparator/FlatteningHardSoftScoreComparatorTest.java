package org.optaplanner.core.impl.score.comparator;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;

import static org.junit.Assert.assertEquals;

public class FlatteningHardSoftScoreComparatorTest {

    @Test
    public void compare() {

        HardSoftScore simpleScore = new HardSoftScoreDefinition().parseScore("10hard/123soft");
        HardSoftScore lowHardScore = new HardSoftScoreDefinition().parseScore("10hard/987654321soft");
        HardSoftScore highHardScore = new HardSoftScoreDefinition().parseScore("987654321hard/123soft");
        assertEquals(0, new FlatteningHardSoftScoreComparator(0).compare(simpleScore, simpleScore));
        assertEquals(0, new FlatteningHardSoftScoreComparator(1).compare(simpleScore, simpleScore));
        assertEquals(0, new FlatteningHardSoftScoreComparator(2).compare(simpleScore, simpleScore));;
        assertEquals(0, new FlatteningHardSoftScoreComparator(64).compare(simpleScore, simpleScore));
        assertEquals(0, new FlatteningHardSoftScoreComparator(1024).compare(simpleScore, simpleScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(0).compare(simpleScore, lowHardScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(1).compare(simpleScore, lowHardScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(2).compare(simpleScore, lowHardScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(64).compare(simpleScore, lowHardScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(1024).compare(simpleScore, lowHardScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(0).compare(lowHardScore, simpleScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(1).compare(lowHardScore, simpleScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(2).compare(lowHardScore, simpleScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(64).compare(lowHardScore, simpleScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(1024).compare(lowHardScore, simpleScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(0).compare(lowHardScore, highHardScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(1).compare(lowHardScore, highHardScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(2).compare(lowHardScore, highHardScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(64).compare(lowHardScore, highHardScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(1024).compare(lowHardScore, highHardScore));
        assertEquals(-1, new FlatteningHardSoftScoreComparator(0).compare(highHardScore, lowHardScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(1).compare(highHardScore, lowHardScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(2).compare(highHardScore, lowHardScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(64).compare(highHardScore, lowHardScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(1024).compare(highHardScore, lowHardScore));
        assertEquals(0, new FlatteningHardSoftScoreComparator(0).compare(highHardScore, simpleScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(1).compare(highHardScore, simpleScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(2).compare(highHardScore, simpleScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(64).compare(highHardScore, simpleScore));
        assertEquals(1, new FlatteningHardSoftScoreComparator(1024).compare(highHardScore, simpleScore));
    }
}
