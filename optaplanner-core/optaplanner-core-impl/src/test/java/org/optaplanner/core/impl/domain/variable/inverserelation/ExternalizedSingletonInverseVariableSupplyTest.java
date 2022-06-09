package org.optaplanner.core.impl.domain.variable.inverserelation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;

class ExternalizedSingletonInverseVariableSupplyTest {

    @Test
    void chainedEntity() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor =
                TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        ScoreDirector<TestdataChainedSolution> scoreDirector = mock(ScoreDirector.class);
        ExternalizedSingletonInverseVariableSupply<TestdataChainedSolution> supply =
                new ExternalizedSingletonInverseVariableSupply<>(variableDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1));

        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        supply.resetWorkingSolution(scoreDirector);

        assertThat(supply.getInverseSingleton(a0)).isSameAs(a1);
        assertThat(supply.getInverseSingleton(a1)).isSameAs(a2);
        assertThat(supply.getInverseSingleton(a2)).isSameAs(a3);
        assertThat(supply.getInverseSingleton(a3)).isSameAs(null);
        assertThat(supply.getInverseSingleton(b0)).isSameAs(b1);
        assertThat(supply.getInverseSingleton(b1)).isSameAs(null);

        supply.beforeVariableChanged(scoreDirector, a3);
        a3.setChainedObject(b1);
        supply.afterVariableChanged(scoreDirector, a3);

        assertThat(supply.getInverseSingleton(a2)).isSameAs(null);
        assertThat(supply.getInverseSingleton(b1)).isSameAs(a3);

        supply.close();
    }

}
