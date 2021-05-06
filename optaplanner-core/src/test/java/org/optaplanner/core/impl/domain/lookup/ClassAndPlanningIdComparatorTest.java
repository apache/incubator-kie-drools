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

package org.optaplanner.core.impl.domain.lookup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToOrder;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;

public class ClassAndPlanningIdComparatorTest {

    private final Comparator<Object> comparator = new ClassAndPlanningIdComparator(false);

    @Test
    public void comparesDifferentClassesByClassName() {
        assertCompareToOrder(comparator, 1d, 1);
    }

    @Test
    public void comparesSameComparableClassesByNaturalOrder() {
        assertCompareToOrder(comparator, 1, 2, 3);
    }

    @Test
    public void comparesSameUnComparableClassesByPlanningId() {
        TestdataLavishEntityGroup group = new TestdataLavishEntityGroup();
        TestdataLavishEntity firstEntity = new TestdataLavishEntity("a", group);
        TestdataLavishEntity secondEntity = new TestdataLavishEntity("b", group);
        TestdataLavishEntity thirdEntity = new TestdataLavishEntity("c", group);
        assertCompareToOrder(comparator, firstEntity, secondEntity, thirdEntity);
    }

    @Test
    public void treatesSameUnComparableClassesWithoutPlanningIdAsEqual() {
        Object firstObject = new ClassAndPlanningIdComparator(false);
        Object secondObject = new ClassAndPlanningIdComparator(false);
        int result = comparator.compare(firstObject, secondObject);
        assertThat(result).isEqualTo(0);
    }

}
