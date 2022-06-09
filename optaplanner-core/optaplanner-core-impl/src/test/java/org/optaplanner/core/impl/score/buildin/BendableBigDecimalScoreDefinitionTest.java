package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;

class BendableBigDecimalScoreDefinitionTest {

    @Test
    void getZeroScore() {
        BendableBigDecimalScore score = new BendableBigDecimalScoreDefinition(1, 2).getZeroScore();
        assertThat(score).isEqualTo(BendableBigDecimalScore.zero(1, 2));
    }

    @Test
    void getSoftestOneScore() {
        BendableBigDecimalScore score = new BendableBigDecimalScoreDefinition(1, 2).getOneSoftestScore();
        assertThat(score).isEqualTo(BendableBigDecimalScore.of(new BigDecimal[] { BigDecimal.ZERO },
                new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ONE }));
    }

    @Test
    void getLevelsSize() {
        assertThat(new BendableBigDecimalScoreDefinition(1, 1).getLevelsSize()).isEqualTo(2);
        assertThat(new BendableBigDecimalScoreDefinition(3, 4).getLevelsSize()).isEqualTo(7);
        assertThat(new BendableBigDecimalScoreDefinition(4, 3).getLevelsSize()).isEqualTo(7);
        assertThat(new BendableBigDecimalScoreDefinition(0, 5).getLevelsSize()).isEqualTo(5);
        assertThat(new BendableBigDecimalScoreDefinition(5, 0).getLevelsSize()).isEqualTo(5);
    }

    @Test
    void getLevelLabels() {
        assertThat(new BendableBigDecimalScoreDefinition(1, 1).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "soft 0 score" });
        assertThat(new BendableBigDecimalScoreDefinition(3, 4).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "hard 1 score", "hard 2 score", "soft 0 score", "soft 1 score",
                        "soft 2 score", "soft 3 score" });
        assertThat(new BendableBigDecimalScoreDefinition(4, 3).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "soft 0 score",
                        "soft 1 score", "soft 2 score" });
        assertThat(new BendableBigDecimalScoreDefinition(0, 5).getLevelLabels())
                .isEqualTo(new String[] { "soft 0 score", "soft 1 score", "soft 2 score", "soft 3 score", "soft 4 score" });
        assertThat(new BendableBigDecimalScoreDefinition(5, 0).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "hard 4 score" });
    }

    @Test
    void getFeasibleLevelsSize() {
        assertThat(new BendableBigDecimalScoreDefinition(1, 1).getFeasibleLevelsSize()).isEqualTo(1);
        assertThat(new BendableBigDecimalScoreDefinition(3, 4).getFeasibleLevelsSize()).isEqualTo(3);
        assertThat(new BendableBigDecimalScoreDefinition(4, 3).getFeasibleLevelsSize()).isEqualTo(4);
        assertThat(new BendableBigDecimalScoreDefinition(0, 5).getFeasibleLevelsSize()).isEqualTo(0);
        assertThat(new BendableBigDecimalScoreDefinition(5, 0).getFeasibleLevelsSize()).isEqualTo(5);
    }

    @Test
    void createScoreWithIllegalArgument() {
        BendableBigDecimalScoreDefinition bendableScoreDefinition = new BendableBigDecimalScoreDefinition(2, 3);
        assertThatIllegalArgumentException().isThrownBy(() -> bendableScoreDefinition.createScore(
                new BigDecimal(1), new BigDecimal(2), new BigDecimal(3)));
    }

    @Test
    void createScore() {
        for (int hardLevelSize = 1; hardLevelSize < 5; hardLevelSize++) {
            for (int softLevelSize = 1; softLevelSize < 5; softLevelSize++) {
                int levelSize = hardLevelSize + softLevelSize;
                BigDecimal[] scores = new BigDecimal[levelSize];
                for (int i = 0; i < levelSize; i++) {
                    scores[i] = new BigDecimal(i);
                }
                BendableBigDecimalScoreDefinition bendableScoreDefinition = new BendableBigDecimalScoreDefinition(hardLevelSize,
                        softLevelSize);
                BendableBigDecimalScore bendableScore = bendableScoreDefinition.createScore(scores);
                assertThat(bendableScore.getHardLevelsSize()).isEqualTo(hardLevelSize);
                assertThat(bendableScore.getSoftLevelsSize()).isEqualTo(softLevelSize);
                for (int i = 0; i < levelSize; i++) {
                    if (i < hardLevelSize) {
                        assertThat(bendableScore.getHardScore(i)).isEqualTo(scores[i]);
                    } else {
                        assertThat(bendableScore.getSoftScore(i - hardLevelSize)).isEqualTo(scores[i]);
                    }
                }
            }
        }
    }

    // Optimistic and pessimistic bounds are currently not supported for this score definition

    @Test
    void divideBySanitizedDivisor() {
        BendableBigDecimalScoreDefinition scoreDefinition = new BendableBigDecimalScoreDefinition(1, 1);
        BendableBigDecimalScore dividend = scoreDefinition.createScoreUninitialized(2, BigDecimal.ZERO, BigDecimal.TEN);
        BendableBigDecimalScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        BendableBigDecimalScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        BendableBigDecimalScore tenDivisor = scoreDefinition.createScoreUninitialized(10, BigDecimal.TEN, BigDecimal.TEN);
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.createScoreUninitialized(0, BigDecimal.ZERO, BigDecimal.ONE));
    }

}
