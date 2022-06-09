package org.optaplanner.core.impl.score.constraint;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class DefaultConstraintMatchTotalTest {

    @Test
    void getScoreTotal() {
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        DefaultConstraintMatchTotal<SimpleScore> constraintMatchTotal =
                new DefaultConstraintMatchTotal<>("package1", "constraint1", SimpleScore.ZERO);
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.ZERO);

        ConstraintMatch<SimpleScore> match1 = constraintMatchTotal.addConstraintMatch(asList(e1, e2), SimpleScore.of(-1));
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-1));
        ConstraintMatch<SimpleScore> match2 = constraintMatchTotal.addConstraintMatch(asList(e1, e3), SimpleScore.of(-20));
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-21));
        // Almost duplicate, but e2 and e1 are in reverse order, so different justification
        ConstraintMatch<SimpleScore> match3 = constraintMatchTotal.addConstraintMatch(asList(e2, e1), SimpleScore.of(-300));
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-321));

        constraintMatchTotal.removeConstraintMatch(match2);
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-301));
        constraintMatchTotal.removeConstraintMatch(match1);
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-300));
        constraintMatchTotal.removeConstraintMatch(match3);
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.ZERO);
    }

    @Test
    void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                new DefaultConstraintMatchTotal<>("a.b", "c", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.b", "c", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.b", "c", SimpleScore.of(-7)));
        PlannerAssert.assertObjectsAreNotEqual(
                new DefaultConstraintMatchTotal<>("a.b", "c", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.b", "d", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.c", "d", SimpleScore.ZERO));
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                new DefaultConstraintMatchTotal<>("a.b", "aa", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.b", "ab", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.b", "ca", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.b", "cb", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.b", "d", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.c", "a", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.c", "b", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal<>("a.c", "c", SimpleScore.ZERO));
    }

}
