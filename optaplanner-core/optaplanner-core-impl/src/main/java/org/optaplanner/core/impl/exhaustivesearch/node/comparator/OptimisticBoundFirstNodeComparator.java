package org.optaplanner.core.impl.exhaustivesearch.node.comparator;

import java.util.Comparator;

import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;

/**
 * Investigate the nodes with a better optimistic bound first, then deeper nodes.
 */
public class OptimisticBoundFirstNodeComparator implements Comparator<ExhaustiveSearchNode> {

    public OptimisticBoundFirstNodeComparator(boolean scoreBounderEnabled) {
        if (!scoreBounderEnabled) {
            throw new IllegalArgumentException("This " + getClass().getSimpleName()
                    + " only works if scoreBounderEnabled (" + scoreBounderEnabled + ") is true.");
        }
    }

    @Override
    public int compare(ExhaustiveSearchNode a, ExhaustiveSearchNode b) {
        // Investigate better optimistic bound first (ignore initScore to avoid depth first ordering)
        int optimisticBoundComparison = a.getOptimisticBound().compareTo(b.getOptimisticBound());
        if (optimisticBoundComparison < 0) {
            return -1;
        } else if (optimisticBoundComparison > 0) {
            return 1;
        }
        // Investigate better score first
        int scoreComparison = a.getScore().withInitScore(0).compareTo(b.getScore().withInitScore(0));
        if (scoreComparison < 0) {
            return -1;
        } else if (scoreComparison > 0) {
            return 1;
        }
        // Investigate deeper first
        int aDepth = a.getDepth();
        int bDepth = b.getDepth();
        if (aDepth < bDepth) {
            return -1;
        } else if (aDepth > bDepth) {
            return 1;
        }
        // Investigate higher parent breadth index first (to reduce on the churn on workingSolution)
        long aParentBreadth = a.getParentBreadth();
        long bParentBreadth = b.getParentBreadth();
        if (aParentBreadth < bParentBreadth) {
            return -1;
        } else if (aParentBreadth > bParentBreadth) {
            return 1;
        }
        // Investigate lower breadth index first (to respect ValueSortingManner)
        return Long.compare(b.getBreadth(), a.getBreadth());
    }

}
