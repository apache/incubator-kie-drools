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

package org.optaplanner.benchmark.impl.ranking;

import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToOrder;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;

public class ResilientScoreComparatorTest {

    @Test
    public void compareTo() {
        Comparator<Score> comparator = new ResilientScoreComparator(new SimpleScoreDefinition());

        assertCompareToOrder(comparator,
                SimpleScore.of(-20),
                SimpleScore.of(-1));
        assertCompareToOrder(comparator,
                HardSoftScore.of(-20, -300),
                HardSoftScore.of(-1, -4000));
        assertCompareToOrder(comparator,
                SimpleScore.of(-4000),
                HardSoftScore.of(-300, -300),
                HardSoftScore.of(-20, -4000),
                SimpleScore.of(-20),
                HardSoftScore.of(-20, 4000),
                SimpleScore.of(-1));
    }

}
