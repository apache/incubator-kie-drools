package org.optaplanner.core.impl.domain.variable.anchor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.ExternalizedSingletonInverseVariableSupply;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;

class ExternalizedAnchorVariableSupplyTest {

    @Test
    void chainedEntity() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor =
                TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        ScoreDirector<TestdataChainedSolution> scoreDirector = mock(ScoreDirector.class);
        ExternalizedSingletonInverseVariableSupply<TestdataChainedSolution> nextVariableSupply =
                new ExternalizedSingletonInverseVariableSupply<>(variableDescriptor);
        ExternalizedAnchorVariableSupply<TestdataChainedSolution> supply =
                new ExternalizedAnchorVariableSupply<>(variableDescriptor, nextVariableSupply);

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
        nextVariableSupply.resetWorkingSolution(scoreDirector);
        supply.resetWorkingSolution(scoreDirector);

        assertThat(supply.getAnchor(a1)).isSameAs(a0);
        assertThat(supply.getAnchor(a2)).isSameAs(a0);
        assertThat(supply.getAnchor(a3)).isSameAs(a0);
        assertThat(supply.getAnchor(b1)).isSameAs(b0);

        nextVariableSupply.beforeVariableChanged(scoreDirector, a3);
        supply.beforeVariableChanged(scoreDirector, a3);
        a3.setChainedObject(b1);
        nextVariableSupply.afterVariableChanged(scoreDirector, a3);
        supply.afterVariableChanged(scoreDirector, a3);

        assertThat(supply.getAnchor(a1)).isSameAs(a0);
        assertThat(supply.getAnchor(a2)).isSameAs(a0);
        assertThat(supply.getAnchor(a3)).isSameAs(b0);
        assertThat(supply.getAnchor(b1)).isSameAs(b0);

        nextVariableSupply.close();
        supply.close();
    }

}
