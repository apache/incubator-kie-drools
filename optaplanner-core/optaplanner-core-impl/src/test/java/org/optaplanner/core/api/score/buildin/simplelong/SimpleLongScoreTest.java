package org.optaplanner.core.api.score.buildin.simplelong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class SimpleLongScoreTest extends AbstractScoreTest {

    @Test
    void parseScore() {
        assertThat(SimpleLongScore.parseScore("-147")).isEqualTo(SimpleLongScore.of(-147L));
        assertThat(SimpleLongScore.parseScore("-7init/-147")).isEqualTo(SimpleLongScore.ofUninitialized(-7, -147L));
        assertThat(SimpleLongScore.parseScore("*")).isEqualTo(SimpleLongScore.of(Long.MIN_VALUE));
    }

    @Test
    void toShortString() {
        assertThat(SimpleLongScore.of(0L).toShortString()).isEqualTo("0");
        assertThat(SimpleLongScore.of(-147L).toShortString()).isEqualTo("-147");
        assertThat(SimpleLongScore.ofUninitialized(-7, -147L).toShortString()).isEqualTo("-7init/-147");
        assertThat(SimpleLongScore.ofUninitialized(-7, 0L).toShortString()).isEqualTo("-7init");
    }

    @Test
    void testToString() {
        assertThat(SimpleLongScore.of(0).toString()).isEqualTo("0");
        assertThat(SimpleLongScore.of(-147L).toString()).isEqualTo("-147");
        assertThat(SimpleLongScore.ofUninitialized(-7, -147L).toString()).isEqualTo("-7init/-147");
    }

    @Test
    void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> SimpleLongScore.parseScore("-147hard/-258soft"));
    }

    @Test
    void withInitScore() {
        assertThat(SimpleLongScore.of(-147L).withInitScore(-7)).isEqualTo(SimpleLongScore.ofUninitialized(-7, -147L));
    }

    @Test
    void add() {
        assertThat(SimpleLongScore.of(20L).add(
                SimpleLongScore.of(-1L))).isEqualTo(SimpleLongScore.of(19L));
        assertThat(SimpleLongScore.ofUninitialized(-70, 20L).add(
                SimpleLongScore.ofUninitialized(-7, -1L))).isEqualTo(SimpleLongScore.ofUninitialized(-77, 19L));
    }

    @Test
    void subtract() {
        assertThat(SimpleLongScore.of(20L).subtract(
                SimpleLongScore.of(-1L))).isEqualTo(SimpleLongScore.of(21L));
        assertThat(SimpleLongScore.ofUninitialized(-70, 20L).subtract(
                SimpleLongScore.ofUninitialized(-7, -1L))).isEqualTo(SimpleLongScore.ofUninitialized(-63, 21L));
    }

    @Test
    void multiply() {
        assertThat(SimpleLongScore.of(5L).multiply(1.2)).isEqualTo(SimpleLongScore.of(6L));
        assertThat(SimpleLongScore.of(1L).multiply(1.2)).isEqualTo(SimpleLongScore.of(1L));
        assertThat(SimpleLongScore.of(4L).multiply(1.2)).isEqualTo(SimpleLongScore.of(4L));
        assertThat(SimpleLongScore.ofUninitialized(-7, 4L).multiply(2.0)).isEqualTo(SimpleLongScore.ofUninitialized(-14, 8L));
    }

    @Test
    void divide() {
        assertThat(SimpleLongScore.of(25L).divide(5.0)).isEqualTo(SimpleLongScore.of(5L));
        assertThat(SimpleLongScore.of(21L).divide(5.0)).isEqualTo(SimpleLongScore.of(4L));
        assertThat(SimpleLongScore.of(24L).divide(5.0)).isEqualTo(SimpleLongScore.of(4L));
        assertThat(SimpleLongScore.ofUninitialized(-14, 8L).divide(2.0)).isEqualTo(SimpleLongScore.ofUninitialized(-7, 4L));
    }

    @Test
    void power() {
        assertThat(SimpleLongScore.of(5L).power(2.0)).isEqualTo(SimpleLongScore.of(25L));
        assertThat(SimpleLongScore.of(25L).power(0.5)).isEqualTo(SimpleLongScore.of(5L));
        assertThat(SimpleLongScore.ofUninitialized(-7, 5L).power(3.0)).isEqualTo(SimpleLongScore.ofUninitialized(-343, 125L));
    }

    @Test
    void negate() {
        assertThat(SimpleLongScore.of(5L).negate()).isEqualTo(SimpleLongScore.of(-5L));
        assertThat(SimpleLongScore.of(-5L).negate()).isEqualTo(SimpleLongScore.of(5L));
    }

    @Test
    void abs() {
        assertThat(SimpleLongScore.of(5L).abs()).isEqualTo(SimpleLongScore.of(5L));
        assertThat(SimpleLongScore.of(-5L).abs()).isEqualTo(SimpleLongScore.of(5L));
    }

    @Test
    void zero() {
        SimpleLongScore manualZero = SimpleLongScore.of(0);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(manualZero.zero()).isEqualTo(manualZero);
            softly.assertThatObject(manualZero.isZero()).isEqualTo(true);
            SimpleLongScore manualOne = SimpleLongScore.of(1);
            softly.assertThat(manualOne.isZero())
                    .isEqualTo(false);
        });
    }

    @Test
    void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                SimpleLongScore.of(-10L),
                SimpleLongScore.of(-10L),
                SimpleLongScore.ofUninitialized(0, -10L));
        PlannerAssert.assertObjectsAreEqual(
                SimpleLongScore.ofUninitialized(-7, -10L),
                SimpleLongScore.ofUninitialized(-7, -10L));
        PlannerAssert.assertObjectsAreNotEqual(
                SimpleLongScore.of(-10L),
                SimpleLongScore.of(-30L),
                SimpleLongScore.ofUninitialized(-7, -10L));
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleLongScore.ofUninitialized(-8, 0L),
                SimpleLongScore.ofUninitialized(-7, -20L),
                SimpleLongScore.ofUninitialized(-7, -1L),
                SimpleLongScore.ofUninitialized(-7, 0L),
                SimpleLongScore.ofUninitialized(-7, 1L),
                SimpleLongScore.of(Integer.MIN_VALUE - 4000L),
                SimpleLongScore.of(-300L),
                SimpleLongScore.of(-20L),
                SimpleLongScore.of(-1L),
                SimpleLongScore.of(0L),
                SimpleLongScore.of(1L),
                SimpleLongScore.of(Integer.MAX_VALUE + 4000L));
    }
}
