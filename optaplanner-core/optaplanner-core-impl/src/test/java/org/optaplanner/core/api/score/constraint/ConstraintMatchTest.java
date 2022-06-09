package org.optaplanner.core.api.score.constraint;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class ConstraintMatchTest {

    @Test
    void equalsAndHashCode() { // No CM should equal any other.
        ConstraintMatch<SimpleScore> constraintMatch =
                new ConstraintMatch<>("a.b", "c", Arrays.asList("e1"), SimpleScore.ZERO);
        PlannerAssert.assertObjectsAreEqual(constraintMatch, constraintMatch);
        ConstraintMatch<SimpleScore> constraintMatch2 =
                new ConstraintMatch<>("a.b", "c", Arrays.asList("e1"), SimpleScore.ZERO);
        // Cast do avoid Comparable checks.
        PlannerAssert.assertObjectsAreNotEqual(constraintMatch, (Object) constraintMatch2);
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "aa"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "aa", "a"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "aa", "b"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "ab"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "c"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "b", Arrays.asList("a", "aa"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "b", Arrays.asList("a", "ab"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "b", Arrays.asList("a", "c"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.c", "a", Arrays.asList("a", "aa"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.c", "a", Arrays.asList("a", "ab"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.c", "a", Arrays.asList("a", "c"), SimpleScore.ZERO));
    }

}
