package org.optaplanner.core.impl.domain.lookup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToOrder;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;

class ClassAndPlanningIdComparatorTest {
    private final Comparator<Object> comparator = new ClassAndPlanningIdComparator(false);

    @Test
    void comparesDifferentClassesByClassName() {
        assertCompareToOrder(comparator, 1d, 1);
    }

    @Test
    void comparesSameComparableClassesByNaturalOrder() {
        assertCompareToOrder(comparator, 1, 2, 3);
    }

    @Test
    void comparesSameUnComparableClassesByPlanningId() {
        TestdataLavishEntityGroup group = new TestdataLavishEntityGroup();
        TestdataLavishEntity firstEntity = new TestdataLavishEntity("a", group);
        TestdataLavishEntity secondEntity = new TestdataLavishEntity("b", group);
        TestdataLavishEntity thirdEntity = new TestdataLavishEntity("c", group);
        assertCompareToOrder(comparator, firstEntity, secondEntity, thirdEntity);
    }

    @Test
    void treatesSameUnComparableClassesWithoutPlanningIdAsEqual() {
        Object firstObject = new ClassAndPlanningIdComparator(false);
        Object secondObject = new ClassAndPlanningIdComparator(false);
        int result = comparator.compare(firstObject, secondObject);
        assertThat(result).isEqualTo(0);
    }

}
