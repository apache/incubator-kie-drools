package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;

class HardSoftBigDecimalScoreDefinitionTest {

    @Test
    void getZeroScore() {
        HardSoftBigDecimalScore score = new HardSoftBigDecimalScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(HardSoftBigDecimalScore.ZERO);
    }

    @Test
    void getSoftestOneScore() {
        HardSoftBigDecimalScore score = new HardSoftBigDecimalScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(HardSoftBigDecimalScore.ONE_SOFT);
    }

    @Test
    void getLevelsSize() {
        assertThat(new HardSoftBigDecimalScoreDefinition().getLevelsSize()).isEqualTo(2);
    }

    @Test
    void getLevelLabels() {
        assertThat(new HardSoftBigDecimalScoreDefinition().getLevelLabels())
                .isEqualTo(new String[] { "hard score", "soft score" });
    }

    @Test
    void getFeasibleLevelsSize() {
        assertThat(new HardSoftBigDecimalScoreDefinition().getFeasibleLevelsSize()).isEqualTo(1);
    }

    // Optimistic and pessimistic bounds are currently not supported for this score definition

    @Test
    void divideBySanitizedDivisor() {
        HardSoftBigDecimalScoreDefinition scoreDefinition = new HardSoftBigDecimalScoreDefinition();
        HardSoftBigDecimalScore dividend = scoreDefinition.fromLevelNumbers(2,
                new Number[] { BigDecimal.ZERO, BigDecimal.TEN });
        HardSoftBigDecimalScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        HardSoftBigDecimalScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        HardSoftBigDecimalScore tenDivisor = scoreDefinition.fromLevelNumbers(10,
                new Number[] { BigDecimal.TEN, BigDecimal.TEN });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0,
                        new Number[] { BigDecimal.ZERO, BigDecimal.ONE }));
    }

}
