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
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

import static org.junit.Assert.*;

public class ConstraintMatchTotalTest {

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
