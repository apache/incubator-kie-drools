package org.optaplanner.core.impl.exhaustivesearch.node.comparator;

import org.junit.jupiter.api.Test;

class OptimisticBoundFirstNodeComparatorTest extends AbstractNodeComparatorTest {

    @Test
    void compare() {
        OptimisticBoundFirstNodeComparator comparator = new OptimisticBoundFirstNodeComparator(true);
        assertScoreCompareToOrder(comparator,
                buildNode(1, "-300", 5, 41),
                buildNode(1, "-300", 5, 40),
                buildNode(1, "-10init/-200", 5, 40),
                buildNode(1, "-110", 5, 40),
                buildNode(1, "-110", 7, 40),
                buildNode(2, "-110", 5, 40),
                buildNode(2, "-110", 7, 40),
                buildNode(1, "-90", 5, 40),
                buildNode(1, "-90", 7, 40),
                buildNode(2, "-90", 5, 40),
                buildNode(2, "-90", 7, 40),
                buildNode(1, "-95", 0, 5, 40),
                buildNode(2, "-95", 0, 5, 40),
                buildNode(2, "-95", 0, 7, 40),
                buildNode(1, "-11", 1, 5, 40),
                buildNode(1, "-1init/-10", 1, 5, 40));
    }

}
