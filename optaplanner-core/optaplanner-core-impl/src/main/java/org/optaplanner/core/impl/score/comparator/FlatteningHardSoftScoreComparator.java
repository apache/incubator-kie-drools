package org.optaplanner.core.impl.score.comparator;

import java.util.Comparator;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

/**
 * Compares 2 {@link HardSoftScore}s based on the calculation of the hard multiplied by a weight, summed with the soft.
 */
public class FlatteningHardSoftScoreComparator implements Comparator<Score> {

    private int hardWeight;

    public FlatteningHardSoftScoreComparator(int hardWeight) {
        this.hardWeight = hardWeight;
    }

    public int getHardWeight() {
        return hardWeight;
    }

    @Override
    public int compare(Score s1, Score s2) {
        HardSoftScore score1 = (HardSoftScore) s1;
        HardSoftScore score2 = (HardSoftScore) s2;
        long score1Side = score1.getHardScore() * (long) hardWeight + score1.getSoftScore();
        long score2Side = score2.getHardScore() * (long) hardWeight + score2.getSoftScore();
        return score1Side < score2Side ? -1 : (score1Side == score2Side ? 0 : 1);
    }

}
