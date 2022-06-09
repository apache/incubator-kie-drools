package org.optaplanner.benchmark.impl.ranking;

import java.util.Comparator;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

/**
 * Able to compare {@link Score}s of different types or nulls.
 */
final class ResilientScoreComparator implements Comparator<Score> {

    private final ScoreDefinition aScoreDefinition;

    public ResilientScoreComparator(ScoreDefinition aScoreDefinition) {
        this.aScoreDefinition = aScoreDefinition;
    }

    @Override
    public int compare(Score a, Score b) {
        if (a == null) {
            return b == null ? 0 : -1;
        } else if (b == null) {
            return 1;
        }
        if (!aScoreDefinition.isCompatibleArithmeticArgument(a) ||
                !aScoreDefinition.isCompatibleArithmeticArgument(b)) {
            Number[] aNumbers = a.toLevelNumbers();
            Number[] bNumbers = b.toLevelNumbers();
            for (int i = 0; i < aNumbers.length || i < bNumbers.length; i++) {
                Number aToken = i < aNumbers.length ? aNumbers[i] : 0;
                Number bToken = i < bNumbers.length ? bNumbers[i] : 0;
                int comparison;
                if (aToken.getClass().equals(bToken.getClass())) {
                    comparison = ((Comparable) aToken).compareTo(bToken);
                } else {
                    comparison = Double.compare(aToken.doubleValue(), bToken.doubleValue());
                }
                if (comparison != 0) {
                    return comparison;
                }
            }
            return 0;
        }
        return a.compareTo(b);
    }

}
