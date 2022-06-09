package org.optaplanner.core.impl.score.constraint;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class DefaultIndictmentTest {

    @Test
    void getScoreTotal() {
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        DefaultIndictment<SimpleScore> indictment = new DefaultIndictment<>(e1, SimpleScore.ZERO);
        assertThat(indictment.getScore()).isEqualTo(SimpleScore.ZERO);

        ConstraintMatch<SimpleScore> match1 = new ConstraintMatch<>("package1", "constraint1", asList(e1), SimpleScore.of(-1));
        indictment.addConstraintMatch(match1);
        assertThat(indictment.getScore()).isEqualTo(SimpleScore.of(-1));
        // Different constraintName
        ConstraintMatch<SimpleScore> match2 = new ConstraintMatch<>("package1", "constraint2", asList(e1), SimpleScore.of(-20));
        indictment.addConstraintMatch(match2);
        assertThat(indictment.getScore()).isEqualTo(SimpleScore.of(-21));
        indictment.addConstraintMatch(new ConstraintMatch<>("package1", "constraint3", asList(e1, e2), SimpleScore.of(-300)));
        assertThat(indictment.getScore()).isEqualTo(SimpleScore.of(-321));
        // Different justification
        indictment.addConstraintMatch(new ConstraintMatch<>("package1", "constraint3", asList(e1, e3), SimpleScore.of(-4000)));
        assertThat(indictment.getScore()).isEqualTo(SimpleScore.of(-4321));
        // Almost duplicate, but e2 and e1 are in reverse order, so different justification
        indictment.addConstraintMatch(new ConstraintMatch<>("package1", "constraint3", asList(e2, e1), SimpleScore.of(-50000)));
        assertThat(indictment.getScore()).isEqualTo(SimpleScore.of(-54321));

        indictment.removeConstraintMatch(match2);
        assertThat(indictment.getScore()).isEqualTo(SimpleScore.of(-54301));
        indictment.removeConstraintMatch(match1);
        assertThat(indictment.getScore()).isEqualTo(SimpleScore.of(-54300));
    }

    @Test
    void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                new DefaultIndictment<>("e1", SimpleScore.ZERO),
                new DefaultIndictment<>("e1", SimpleScore.ZERO),
                new DefaultIndictment<>("e1", SimpleScore.of(-7)));
        PlannerAssert.assertObjectsAreNotEqual(
                new DefaultIndictment<>("a", SimpleScore.ZERO),
                new DefaultIndictment<>("aa", SimpleScore.ZERO),
                new DefaultIndictment<>("b", SimpleScore.ZERO),
                new DefaultIndictment<>("c", SimpleScore.ZERO));
    }

}
