package org.optaplanner.persistence.jpa.api.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HardMediumSoftBigDecimalScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new HardMediumSoftBigDecimalScoreConverterTestJpaEntity(HardMediumSoftBigDecimalScore.ZERO), null,
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.01000"), new BigDecimal("-4.32100"),
                        new BigDecimal("-2.20000")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.01000"), new BigDecimal("-4.32100"),
                        new BigDecimal("-2.20000")));
    }

    @Entity
    static class HardMediumSoftBigDecimalScoreConverterTestJpaEntity
            extends AbstractTestJpaEntity<HardMediumSoftBigDecimalScore> {

        @Convert(converter = HardMediumSoftBigDecimalScoreConverter.class)
        protected HardMediumSoftBigDecimalScore score;

        HardMediumSoftBigDecimalScoreConverterTestJpaEntity() {
        }

        public HardMediumSoftBigDecimalScoreConverterTestJpaEntity(HardMediumSoftBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardMediumSoftBigDecimalScore score) {
            this.score = score;
        }
    }
}
