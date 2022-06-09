package org.optaplanner.test.api.solver.change;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValue;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValueGroup;

class MockProblemChangeDirectorTest {

    @Test
    void problemChange() {
        final TestdataLavishEntityGroup entityGroupOne = new TestdataLavishEntityGroup("entityGroupOne");
        final TestdataLavishValueGroup valueGroupOne = new TestdataLavishValueGroup("valueGroupOne");
        final TestdataLavishEntity addedEntity = new TestdataLavishEntity("newly added entity", entityGroupOne);
        final TestdataLavishEntity removedEntity = new TestdataLavishEntity("entity to remove", entityGroupOne);
        final TestdataLavishValue addedFact = new TestdataLavishValue("newly added fact", valueGroupOne);
        final TestdataLavishValue removedFact = new TestdataLavishValue("fact to remove", valueGroupOne);
        final TestdataLavishEntity changedEntity = new TestdataLavishEntity("changed entity", entityGroupOne);
        final TestdataLavishValue changedFact = new TestdataLavishValue("changed entity value", valueGroupOne);

        // Working solution counterparts.
        final TestdataLavishEntity removedWorkingEntity = new TestdataLavishEntity("working entity to remove", entityGroupOne);
        final TestdataLavishValue removedWorkingFact = new TestdataLavishValue("working fact to remove", valueGroupOne);
        final TestdataLavishEntity changedWorkingEntity = new TestdataLavishEntity("working changed entity", entityGroupOne);

        MockProblemChangeDirector mockProblemChangeDirector = new MockProblemChangeDirector();
        // Configure look-up mocks.
        mockProblemChangeDirector
                .whenLookingUp(removedEntity).thenReturn(removedWorkingEntity)
                .whenLookingUp(removedFact).thenReturn(removedWorkingFact)
                .whenLookingUp(changedEntity).thenReturn(changedWorkingEntity);

        ProblemChange<TestdataLavishSolution> problemChange = ((workingSolution, problemChangeDirector) -> {
            // Add an entity.
            problemChangeDirector.addEntity(addedEntity, workingSolution.getEntityList()::add);
            // Remove an entity.
            problemChangeDirector.removeEntity(removedEntity, workingSolution.getEntityList()::remove);
            // Change a planning variable.
            problemChangeDirector.changeVariable(changedEntity, TestdataLavishEntity.VALUE_FIELD,
                    testdataEntity -> testdataEntity.setValue(changedFact));
            // Change a property
            problemChangeDirector.changeProblemProperty(changedEntity,
                    workingEntity -> workingEntity.setEntityGroup(null));
            // Add a problem fact.
            problemChangeDirector.addProblemFact(addedFact, workingSolution.getValueList()::add);
            // Remove a problem fact.
            problemChangeDirector.removeProblemFact(removedFact, workingSolution.getValueList()::remove);
        });

        TestdataLavishSolution testdataSolution = TestdataLavishSolution.generateSolution();
        testdataSolution.getEntityList().add(removedWorkingEntity);
        testdataSolution.getEntityList().add(changedWorkingEntity);
        testdataSolution.getValueList().add(removedWorkingFact);

        problemChange.doChange(testdataSolution, mockProblemChangeDirector);

        SoftAssertions.assertSoftly((softAssertions) -> {
            softAssertions.assertThat(testdataSolution.getEntityList()).doesNotContain(removedWorkingEntity);
            softAssertions.assertThat(testdataSolution.getValueList()).doesNotContain(removedWorkingFact);
            softAssertions.assertThat(changedWorkingEntity.getValue()).isEqualTo(changedFact);
            softAssertions.assertThat(changedWorkingEntity.getEntityGroup()).isNull();
        });
    }
}
