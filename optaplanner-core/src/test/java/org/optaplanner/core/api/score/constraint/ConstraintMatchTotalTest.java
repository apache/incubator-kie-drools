/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.constraint;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

public class ConstraintMatchTotalTest {

    @Test
    public void getScoreTotal() {
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        ConstraintMatchTotal constraintMatchTotal = new ConstraintMatchTotal("package1", "constraint1", SimpleScore.ZERO);
        assertEquals(SimpleScore.ZERO, constraintMatchTotal.getScoreTotal());

        ConstraintMatch match1 = constraintMatchTotal.addConstraintMatch(asList(e1, e2), SimpleScore.valueOf(-1));
        assertEquals(SimpleScore.valueOf(-1), constraintMatchTotal.getScoreTotal());
        ConstraintMatch match2 = constraintMatchTotal.addConstraintMatch(asList(e1, e3), SimpleScore.valueOf(-20));
        assertEquals(SimpleScore.valueOf(-21), constraintMatchTotal.getScoreTotal());
        // Almost duplicate, but e2 and e1 are in reverse order, so different justification
        ConstraintMatch match3 = constraintMatchTotal.addConstraintMatch(asList(e2, e1), SimpleScore.valueOf(-300));
        assertEquals(SimpleScore.valueOf(-321), constraintMatchTotal.getScoreTotal());

        constraintMatchTotal.removeConstraintMatch(match2);
        assertEquals(SimpleScore.valueOf(-301), constraintMatchTotal.getScoreTotal());
        constraintMatchTotal.removeConstraintMatch(match1);
        assertEquals(SimpleScore.valueOf(-300), constraintMatchTotal.getScoreTotal());
        constraintMatchTotal.removeConstraintMatch(match3);
        assertEquals(SimpleScore.ZERO, constraintMatchTotal.getScoreTotal());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                new ConstraintMatchTotal("a.b", "c", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.b", "c", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.b", "c", SimpleScore.valueOf(-7))
        );
        PlannerAssert.assertObjectsAreNotEqual(
                new ConstraintMatchTotal("a.b", "c", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.b", "d", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.c", "d", SimpleScore.ZERO)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                new ConstraintMatchTotal("a.b", "aa", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.b", "ab", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.b", "ca", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.b", "cb", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.b", "d", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.c", "a", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.c", "b", SimpleScore.ZERO),
                new ConstraintMatchTotal("a.c", "c", SimpleScore.ZERO)
        );
    }

}
