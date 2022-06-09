package org.optaplanner.core.impl.domain.variable.inverserelation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.shadow.inverserelation.TestdataInverseRelationEntity;
import org.optaplanner.core.impl.testdata.domain.shadow.inverserelation.TestdataInverseRelationSolution;
import org.optaplanner.core.impl.testdata.domain.shadow.inverserelation.TestdataInverseRelationValue;

class CollectionInverseVariableListenerTest {

    @Test
    void normal() {
        InnerScoreDirector<TestdataInverseRelationSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);
        SolutionDescriptor<TestdataInverseRelationSolution> solutionDescriptor =
                TestdataInverseRelationSolution.buildSolutionDescriptor();
        EntityDescriptor<TestdataInverseRelationSolution> entityDescriptor =
                solutionDescriptor.findEntityDescriptorOrFail(TestdataInverseRelationEntity.class);
        EntityDescriptor<TestdataInverseRelationSolution> shadowEntityDescriptor =
                solutionDescriptor.findEntityDescriptorOrFail(TestdataInverseRelationValue.class);
        ShadowVariableDescriptor<TestdataInverseRelationSolution> entitiesVariableDescriptor =
                shadowEntityDescriptor.getShadowVariableDescriptor("entities");
        CollectionInverseVariableListener<TestdataInverseRelationSolution> variableListener =
                new CollectionInverseVariableListener<>(
                        (InverseRelationShadowVariableDescriptor<TestdataInverseRelationSolution>) entitiesVariableDescriptor,
                        entityDescriptor.getGenuineVariableDescriptor("value"));

        TestdataInverseRelationValue val1 = new TestdataInverseRelationValue("1");
        TestdataInverseRelationValue val2 = new TestdataInverseRelationValue("2");
        TestdataInverseRelationValue val3 = new TestdataInverseRelationValue("3");
        TestdataInverseRelationEntity a = new TestdataInverseRelationEntity("a", val1);
        TestdataInverseRelationEntity b = new TestdataInverseRelationEntity("b", val1);
        TestdataInverseRelationEntity c = new TestdataInverseRelationEntity("c", val3);
        TestdataInverseRelationEntity d = new TestdataInverseRelationEntity("d", val3);

        TestdataInverseRelationSolution solution = new TestdataInverseRelationSolution("solution");
        solution.setEntityList(Arrays.asList(a, b, c, d));
        solution.setValueList(Arrays.asList(val1, val2, val3));

        assertThat(val1.getEntities()).containsExactly(a, b);
        assertThat(val2.getEntities()).isEmpty();
        assertThat(val3.getEntities()).containsExactly(c, d);

        variableListener.beforeVariableChanged(scoreDirector, c);
        c.setValue(val2);
        variableListener.afterVariableChanged(scoreDirector, c);

        assertThat(val1.getEntities()).containsExactly(a, b);
        assertThat(val2.getEntities()).containsExactly(c);
        assertThat(val3.getEntities()).containsExactly(d);
    }

}
