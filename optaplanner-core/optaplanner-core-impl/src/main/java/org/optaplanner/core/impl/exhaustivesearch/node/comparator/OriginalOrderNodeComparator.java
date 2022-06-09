package org.optaplanner.core.impl.exhaustivesearch.node.comparator;

import java.util.Comparator;

import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;

/**
 * Investigate deeper nodes first, in order.
 */
public class OriginalOrderNodeComparator implements Comparator<ExhaustiveSearchNode> {

    @Override
    public int compare(ExhaustiveSearchNode a, ExhaustiveSearchNode b) {
        // Investigate deeper first
        int aDepth = a.getDepth();
        int bDepth = b.getDepth();
        if (aDepth < bDepth) {
            return -1;
        } else if (aDepth > bDepth) {
            return 1;
        }
        // Investigate lower breadth index first (to respect ValueSortingManner)
        return Long.compare(b.getBreadth(), a.getBreadth());
    }

}
