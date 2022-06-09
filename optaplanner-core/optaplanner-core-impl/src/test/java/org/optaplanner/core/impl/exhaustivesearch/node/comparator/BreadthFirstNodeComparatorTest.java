package org.optaplanner.core.impl.exhaustivesearch.node.comparator;

import org.junit.jupiter.api.Test;

class BreadthFirstNodeComparatorTest extends AbstractNodeComparatorTest {

    @Test
    void compare() {
        BreadthFirstNodeComparator comparator = new BreadthFirstNodeComparator(true);
        assertScoreCompareToOrder(comparator,
                buildNode(2, "-110", 5, 51),
                buildNode(2, "-110", 5, 50),
                buildNode(2, "-90", 7, 41),
                buildNode(2, "-90", 5, 40),
                buildNode(1, "-110", 7, 61),
                buildNode(1, "-110", 5, 60),
                buildNode(1, "-90", 7, 71),
                buildNode(1, "-90", 5, 70),
                buildNode(1, "-85", 5, 60),
                buildNode(1, "-1init/-80", 5, 60));
    }

}
