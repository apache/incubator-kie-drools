package org.optaplanner.core.impl.exhaustivesearch.node.comparator;

import java.util.Comparator;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.ScoreBounder;

/**
 * Investigate nodes layer by layer: investigate shallower nodes first.
 * This results in horrible memory scalability.
 * <p>
 * A typical {@link ScoreBounder}'s {@link ScoreBounder#calculateOptimisticBound(ScoreDirector, Score)}
 * will be weak, which results in horrible performance scalability too.
 */
public class BreadthFirstNodeComparator implements Comparator<ExhaustiveSearchNode> {

    private final boolean scoreBounderEnabled;

    public BreadthFirstNodeComparator(boolean scoreBounderEnabled) {
        this.scoreBounderEnabled = scoreBounderEnabled;
    }

    @Override
    public int compare(ExhaustiveSearchNode a, ExhaustiveSearchNode b) {
        // Investigate shallower nodes first
        int aDepth = a.getDepth();
        int bDepth = b.getDepth();
        if (aDepth < bDepth) {
            return 1;
        } else if (aDepth > bDepth) {
            return -1;
        }
        // Investigate better score first (ignore initScore to avoid depth first ordering)
        int scoreComparison = a.getScore().withInitScore(0).compareTo(b.getScore().withInitScore(0));
        if (scoreComparison < 0) {
            return -1;
        } else if (scoreComparison > 0) {
            return 1;
        }
        if (scoreBounderEnabled) {
            // Investigate better optimistic bound first
            int optimisticBoundComparison = a.getOptimisticBound().compareTo(b.getOptimisticBound());
            if (optimisticBoundComparison < 0) {
                return -1;
            } else if (optimisticBoundComparison > 0) {
                return 1;
            }
        }
        // No point to investigating higher parent breadth index first (no impact on the churn on workingSolution)
        // Investigate lower breadth index first (to respect ValueSortingManner)
        return Long.compare(b.getBreadth(), a.getBreadth());
    }

}
