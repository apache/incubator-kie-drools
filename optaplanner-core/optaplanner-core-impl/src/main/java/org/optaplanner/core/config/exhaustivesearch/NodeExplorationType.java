package org.optaplanner.core.config.exhaustivesearch;

import java.util.Comparator;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.BreadthFirstNodeComparator;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.DepthFirstNodeComparator;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.OptimisticBoundFirstNodeComparator;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.OriginalOrderNodeComparator;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.ScoreFirstNodeComparator;

@XmlEnum
public enum NodeExplorationType {
    ORIGINAL_ORDER,
    DEPTH_FIRST,
    BREADTH_FIRST,
    SCORE_FIRST,
    OPTIMISTIC_BOUND_FIRST;

    public Comparator<ExhaustiveSearchNode> buildNodeComparator(boolean scoreBounderEnabled) {
        switch (this) {
            case ORIGINAL_ORDER:
                return new OriginalOrderNodeComparator();
            case DEPTH_FIRST:
                return new DepthFirstNodeComparator(scoreBounderEnabled);
            case BREADTH_FIRST:
                return new BreadthFirstNodeComparator(scoreBounderEnabled);
            case SCORE_FIRST:
                return new ScoreFirstNodeComparator(scoreBounderEnabled);
            case OPTIMISTIC_BOUND_FIRST:
                return new OptimisticBoundFirstNodeComparator(scoreBounderEnabled);
            default:
                throw new IllegalStateException("The nodeExplorationType ("
                        + this + ") is not implemented.");
        }
    }

}
