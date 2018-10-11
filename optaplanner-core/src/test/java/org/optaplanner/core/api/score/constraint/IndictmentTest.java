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

public class IndictmentTest {

    @Test
    public void getScoreTotal() {
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        Indictment indictment = new Indictment(e1, SimpleScore.ZERO);
        assertEquals(SimpleScore.ZERO, indictment.getScore());

        ConstraintMatch match1 = new ConstraintMatch("package1", "constraint1", asList(e1), SimpleScore.of(-1));
        indictment.addConstraintMatch(match1);
        assertEquals(SimpleScore.of(-1), indictment.getScore());
        // Different constraintName
        ConstraintMatch match2 = new ConstraintMatch("package1", "constraint2", asList(e1), SimpleScore.of(-20));
        indictment.addConstraintMatch(match2);
        assertEquals(SimpleScore.of(-21), indictment.getScore());
        indictment.addConstraintMatch(new ConstraintMatch("package1", "constraint3", asList(e1, e2), SimpleScore.of(-300)));
        assertEquals(SimpleScore.of(-321), indictment.getScore());
        // Different justification
        indictment.addConstraintMatch(new ConstraintMatch("package1", "constraint3", asList(e1, e3), SimpleScore.of(-4000)));
        assertEquals(SimpleScore.of(-4321), indictment.getScore());
        // Almost duplicate, but e2 and e1 are in reverse order, so different justification
        indictment.addConstraintMatch(new ConstraintMatch("package1", "constraint3", asList(e2, e1), SimpleScore.of(-50000)));
        assertEquals(SimpleScore.of(-54321), indictment.getScore());

        indictment.removeConstraintMatch(match2);
        assertEquals(SimpleScore.of(-54301), indictment.getScore());
        indictment.removeConstraintMatch(match1);
        assertEquals(SimpleScore.of(-54300), indictment.getScore());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                new Indictment("e1", SimpleScore.ZERO),
                new Indictment("e1", SimpleScore.ZERO),
                new Indictment("e1", SimpleScore.of(-7))
        );
        PlannerAssert.assertObjectsAreNotEqual(
                new Indictment("a", SimpleScore.ZERO),
                new Indictment("aa", SimpleScore.ZERO),
                new Indictment("b", SimpleScore.ZERO),
                new Indictment("c", SimpleScore.ZERO)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                new Indictment("a", SimpleScore.ZERO),
                new Indictment("aa", SimpleScore.ZERO),
                new Indictment("ab", SimpleScore.ZERO),
                new Indictment("b", SimpleScore.ZERO),
                new Indictment("c", SimpleScore.ZERO),
                new Indictment("d", SimpleScore.ZERO)
        );
    }

}
