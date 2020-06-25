/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.constraint;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

public class DefaultConstraintMatchTotalTest {

    @Test
    public void getScoreTotal() {
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        DefaultConstraintMatchTotal constraintMatchTotal =
                new DefaultConstraintMatchTotal("package1", "constraint1", SimpleScore.ZERO);
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.ZERO);

        ConstraintMatch match1 = constraintMatchTotal.addConstraintMatch(asList(e1, e2), SimpleScore.of(-1));
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-1));
        ConstraintMatch match2 = constraintMatchTotal.addConstraintMatch(asList(e1, e3), SimpleScore.of(-20));
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-21));
        // Almost duplicate, but e2 and e1 are in reverse order, so different justification
        ConstraintMatch match3 = constraintMatchTotal.addConstraintMatch(asList(e2, e1), SimpleScore.of(-300));
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-321));

        constraintMatchTotal.removeConstraintMatch(match2);
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-301));
        constraintMatchTotal.removeConstraintMatch(match1);
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.of(-300));
        constraintMatchTotal.removeConstraintMatch(match3);
        assertThat(constraintMatchTotal.getScore()).isEqualTo(SimpleScore.ZERO);
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                new DefaultConstraintMatchTotal("a.b", "c", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.b", "c", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.b", "c", SimpleScore.of(-7)));
        PlannerAssert.assertObjectsAreNotEqual(
                new DefaultConstraintMatchTotal("a.b", "c", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.b", "d", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.c", "d", SimpleScore.ZERO));
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                new DefaultConstraintMatchTotal("a.b", "aa", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.b", "ab", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.b", "ca", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.b", "cb", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.b", "d", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.c", "a", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.c", "b", SimpleScore.ZERO),
                new DefaultConstraintMatchTotal("a.c", "c", SimpleScore.ZERO));
    }

}
