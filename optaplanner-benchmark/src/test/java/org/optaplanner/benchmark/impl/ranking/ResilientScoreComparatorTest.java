package org.optaplanner.benchmark.impl.ranking;

import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToOrder;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;

class ResilientScoreComparatorTest {

    @Test
    void compareTo() {
        Comparator<Score> comparator = new ResilientScoreComparator(new SimpleScoreDefinition());

        assertCompareToOrder(comparator,
                SimpleScore.of(-20),
                SimpleScore.of(-1));
        assertCompareToOrder(comparator,
                HardSoftScore.of(-20, -300),
                HardSoftScore.of(-1, -4000));
        assertCompareToOrder(comparator,
                SimpleScore.of(-4000),
                HardSoftScore.of(-300, -300),
                HardSoftScore.of(-20, -4000),
                SimpleScore.of(-20),
                HardSoftScore.of(-20, 4000),
                SimpleScore.of(-1));
    }

}
