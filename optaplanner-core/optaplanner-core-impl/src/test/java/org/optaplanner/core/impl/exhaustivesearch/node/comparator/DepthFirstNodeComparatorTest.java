package org.optaplanner.core.impl.exhaustivesearch.node.comparator;

import org.junit.jupiter.api.Test;

class DepthFirstNodeComparatorTest extends AbstractNodeComparatorTest {

    @Test
    void compare() {
        DepthFirstNodeComparator comparator = new DepthFirstNodeComparator(true);
        assertScoreCompareToOrder(comparator,
                buildNode(1, "-110", 5, 41),
                buildNode(1, "-110", 5, 40),
                buildNode(1, "-110", 7, 40),
                buildNode(1, "-90", 5, 40),
                buildNode(1, "-90", 7, 40),
                buildNode(2, "-110", 5, 40),
                buildNode(2, "-110", 7, 40),
                buildNode(2, "-90", 5, 40),
                buildNode(2, "-90", 7, 40),
                buildNode(2, "-1init/-80", 7, 40));
    }

}
