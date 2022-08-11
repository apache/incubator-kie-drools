package org.optaplanner.core.api.score.constraint;

import static org.optaplanner.core.api.score.buildin.simple.SimpleScore.ONE;
import static org.optaplanner.core.api.score.buildin.simple.SimpleScore.ZERO;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class ConstraintMatchTest {

    @Test
    void equalsAndHashCode() { // No CM should equal any other.
        ConstraintMatch<SimpleScore> constraintMatch =
                new ConstraintMatch<>("a.b", "c", List.of("e1"), ZERO);
        PlannerAssert.assertObjectsAreEqual(constraintMatch, constraintMatch);
        ConstraintMatch<SimpleScore> constraintMatch2 =
                new ConstraintMatch<>("a.b", "c", List.of("e1"), ZERO);
        // Cast do avoid Comparable checks.
        PlannerAssert.assertObjectsAreNotEqual(constraintMatch, (Object) constraintMatch2);
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                new ConstraintMatch<>("a.b", "a", List.of("a"), ZERO),
                new ConstraintMatch<>("a.b", "a", List.of("a", "aa"), ZERO),
                new ConstraintMatch<>("a.b", "a", List.of("a", "ab"), ZERO),
                new ConstraintMatch<>("a.b", "a", List.of("a", "c"), ZERO),
                new ConstraintMatch<>("a.b", "a", List.of("a", "aa", "a"), ZERO),
                new ConstraintMatch<>("a.b", "a", List.of("a", "aa", "b"), ZERO),
                new ConstraintMatch<>("a.b", "a", List.of("a", "aa"), ONE),
                new ConstraintMatch<>("a.b", "b", List.of("a", "aa"), ZERO),
                new ConstraintMatch<>("a.b", "b", List.of("a", "ab"), ZERO),
                new ConstraintMatch<>("a.b", "b", List.of("a", "c"), ZERO),
                new ConstraintMatch<>("a.c", "a", List.of("a", "aa"), ZERO),
                new ConstraintMatch<>("a.c", "a", List.of("a", "ab"), ZERO),
                new ConstraintMatch<>("a.c", "a", List.of("a", "c"), ZERO));
    }

}
