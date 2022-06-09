package org.optaplanner.core.impl.score.definition;

import org.optaplanner.core.api.score.AbstractBendableScore;
import org.optaplanner.core.api.score.Score;

public abstract class AbstractBendableScoreDefinition<Score_ extends Score<Score_>> extends AbstractScoreDefinition<Score_>
        implements ScoreDefinition<Score_> {

    protected static String[] generateLevelLabels(int hardLevelsSize, int softLevelsSize) {
        if (hardLevelsSize < 0 || softLevelsSize < 0) {
            throw new IllegalArgumentException("The hardLevelsSize (" + hardLevelsSize
                    + ") and softLevelsSize (" + softLevelsSize + ") should be positive.");
        }
        String[] levelLabels = new String[hardLevelsSize + softLevelsSize];
        for (int i = 0; i < levelLabels.length; i++) {
            String labelPrefix;
            if (i < hardLevelsSize) {
                labelPrefix = "hard " + i;
            } else {
                labelPrefix = "soft " + (i - hardLevelsSize);
            }
            levelLabels[i] = labelPrefix + " score";
        }
        return levelLabels;
    }

    protected final int hardLevelsSize;
    protected final int softLevelsSize;

    public AbstractBendableScoreDefinition(int hardLevelsSize, int softLevelsSize) {
        super(generateLevelLabels(hardLevelsSize, softLevelsSize));
        this.hardLevelsSize = hardLevelsSize;
        this.softLevelsSize = softLevelsSize;
    }

    public int getHardLevelsSize() {
        return hardLevelsSize;
    }

    public int getSoftLevelsSize() {
        return softLevelsSize;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelsSize() {
        return hardLevelsSize + softLevelsSize;
    }

    @Override
    public int getFeasibleLevelsSize() {
        return hardLevelsSize;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score score) {
        if (super.isCompatibleArithmeticArgument(score)) {
            AbstractBendableScore<?> bendableScore = (AbstractBendableScore<?>) score;
            return getLevelsSize() == bendableScore.getLevelsSize()
                    && getHardLevelsSize() == bendableScore.getHardLevelsSize()
                    && getSoftLevelsSize() == bendableScore.getSoftLevelsSize();
        }
        return false;
    }
}
