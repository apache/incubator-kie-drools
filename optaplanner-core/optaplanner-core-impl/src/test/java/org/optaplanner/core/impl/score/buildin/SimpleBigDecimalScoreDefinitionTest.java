package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;

class SimpleBigDecimalScoreDefinitionTest {

    @Test
    void getZeroScore() {
        SimpleBigDecimalScore score = new SimpleBigDecimalScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(SimpleBigDecimalScore.ZERO);
    }

    @Test
    void getSoftestOneScore() {
        SimpleBigDecimalScore score = new SimpleBigDecimalScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(SimpleBigDecimalScore.ONE);
    }

    @Test
    void getLevelsSize() {
        assertThat(new SimpleBigDecimalScoreDefinition().getLevelsSize()).isEqualTo(1);
    }

    @Test
    void getLevelLabels() {
        assertThat(new SimpleBigDecimalScoreDefinition().getLevelLabels()).isEqualTo(new String[] { "score" });
    }

    // Optimistic and pessimistic bounds are currently not supported for this score definition

    @Test
    void divideBySanitizedDivisor() {
        SimpleBigDecimalScoreDefinition scoreDefinition = new SimpleBigDecimalScoreDefinition();
        SimpleBigDecimalScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] { BigDecimal.TEN });
        SimpleBigDecimalScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        SimpleBigDecimalScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        SimpleBigDecimalScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] { BigDecimal.TEN });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] { BigDecimal.ONE }));
    }

}
