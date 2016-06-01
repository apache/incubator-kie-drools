/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.ranking;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

import static org.junit.Assert.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToOrder;

public class ResilientScoreComparatorTest {

    @Test
    public void compareTo() {
        ResilientScoreComparator comparator = new ResilientScoreComparator();

        assertCompareToOrder(comparator,
                SimpleScore.valueOfInitialized(-20),
                SimpleScore.valueOfInitialized(-1));
        assertCompareToOrder(comparator,
                HardSoftScore.valueOfInitialized(-20, -300),
                HardSoftScore.valueOfInitialized(-1, -4000));
        assertCompareToOrder(comparator,
                SimpleScore.valueOfInitialized(-4000),
                HardSoftScore.valueOfInitialized(-300, -300),
                HardSoftScore.valueOfInitialized(-20, -4000),
                SimpleScore.valueOfInitialized(-20),
                HardSoftScore.valueOfInitialized(-20, 4000),
                SimpleScore.valueOfInitialized(-1));
    }

}
