package org.optaplanner.core.impl.exhaustivesearch.node.comparator;

import org.junit.jupiter.api.Test;

class ScoreFirstNodeComparatorTest extends AbstractNodeComparatorTest {

    @Test
    void compare() {
        ScoreFirstNodeComparator comparator = new ScoreFirstNodeComparator(true);
        assertScoreCompareToOrder(comparator,
                buildNode(1, "-110", 5, 41),
                buildNode(1, "-110", 5, 40),
                buildNode(1, "-110", 7, 40),
                buildNode(2, "-110", 5, 40),
                buildNode(2, "-110", 7, 40),
                buildNode(1, "-95", 0, 5, 40),
                buildNode(2, "-95", 0, 5, 40),
                buildNode(2, "-95", 0, 7, 40),
                buildNode(1, "-90", 5, 40),
                buildNode(1, "-90", 7, 40),
                buildNode(2, "-90", 5, 40),
                buildNode(2, "-90", 7, 40),
                buildNode(1, "-1init/-85", 5, 40),
                buildNode(1, "-100init/-80", 7, 40));
    }

}
