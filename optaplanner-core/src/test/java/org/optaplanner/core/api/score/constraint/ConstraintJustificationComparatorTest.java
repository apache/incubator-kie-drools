package org.optaplanner.core.api.score.constraint;

import java.util.Comparator;

import org.junit.Test;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;

import static org.junit.Assert.assertEquals;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToOrder;

public class ConstraintJustificationComparatorTest {

    private final Comparator<Object> comparator = new ConstraintJustificationComparator();

    @Test
    public void comparesDifferentClassesByClassName() {
        assertCompareToOrder(comparator, Double.valueOf(1), Integer.valueOf(1));
    }

    @Test
    public void comparesSameComparableClassesByNaturalOrder() {
        assertCompareToOrder(comparator, Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
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
        Object firstObject = new ConstraintJustificationComparator();
        Object secondObject = new ConstraintJustificationComparator();
        int result = comparator.compare(firstObject, secondObject);
        assertEquals(0, result);
    }

}
