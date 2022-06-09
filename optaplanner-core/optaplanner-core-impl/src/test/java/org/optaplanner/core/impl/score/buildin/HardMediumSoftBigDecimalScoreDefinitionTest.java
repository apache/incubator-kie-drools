package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;

class HardMediumSoftBigDecimalScoreDefinitionTest {

    @Test
    void getZeroScore() {
        HardMediumSoftBigDecimalScore score = new HardMediumSoftBigDecimalScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(HardMediumSoftBigDecimalScore.ZERO);
    }

    @Test
    void getSoftestOneScore() {
        HardMediumSoftBigDecimalScore score = new HardMediumSoftBigDecimalScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(HardMediumSoftBigDecimalScore.ONE_SOFT);
    }

    @Test
    void getLevelsSize() {
        assertThat(new HardMediumSoftBigDecimalScoreDefinition().getLevelsSize()).isEqualTo(3);
    }

    @Test
    void getLevelLabels() {
        assertThat(new HardMediumSoftBigDecimalScoreDefinition().getLevelLabels())
                .isEqualTo(new String[] { "hard score", "medium score", "soft score" });
    }

    @Test
    void getFeasibleLevelsSize() {
        assertThat(new HardMediumSoftBigDecimalScoreDefinition().getFeasibleLevelsSize()).isEqualTo(1);
    }

    // Optimistic and pessimistic bounds are currently not supported for this score definition

    @Test
    void divideBySanitizedDivisor() {
        HardMediumSoftBigDecimalScoreDefinition scoreDefinition = new HardMediumSoftBigDecimalScoreDefinition();
        HardMediumSoftBigDecimalScore dividend = scoreDefinition.fromLevelNumbers(2,
                new Number[] { BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN });
        HardMediumSoftBigDecimalScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        HardMediumSoftBigDecimalScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        HardMediumSoftBigDecimalScore tenDivisor = scoreDefinition.fromLevelNumbers(10,
                new Number[] { BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0,
                        new Number[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE }));
    }

}
