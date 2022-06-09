package org.optaplanner.core.impl.domain.variable.anchor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.InverseRelationShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableListener;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedSolution;

class AnchorVariableListenerTest {

    @Test
    void chained() {
        SolutionDescriptor<TestdataShadowingChainedSolution> solutionDescriptor =
                TestdataShadowingChainedSolution.buildSolutionDescriptor();
        EntityDescriptor<TestdataShadowingChainedSolution> entityDescriptor =
                solutionDescriptor.findEntityDescriptorOrFail(TestdataShadowingChainedEntity.class);
        GenuineVariableDescriptor<TestdataShadowingChainedSolution> chainedObjectVariableDescriptor = entityDescriptor
                .getGenuineVariableDescriptor("chainedObject");
        ShadowVariableDescriptor<TestdataShadowingChainedSolution> nextEntityVariableDescriptor =
                entityDescriptor.getShadowVariableDescriptor("nextEntity");
        SingletonInverseVariableListener<TestdataShadowingChainedSolution> inverseVariableListener =
                new SingletonInverseVariableListener<>(
                        (InverseRelationShadowVariableDescriptor<TestdataShadowingChainedSolution>) nextEntityVariableDescriptor,
                        entityDescriptor.getGenuineVariableDescriptor("chainedObject"));
        ShadowVariableDescriptor<TestdataShadowingChainedSolution> anchorVariableDescriptor =
                entityDescriptor.getShadowVariableDescriptor("anchor");
        AnchorVariableListener<TestdataShadowingChainedSolution> variableListener = new AnchorVariableListener<>(
                (AnchorShadowVariableDescriptor<TestdataShadowingChainedSolution>) anchorVariableDescriptor,
                chainedObjectVariableDescriptor, inverseVariableListener);
        InnerScoreDirector<TestdataShadowingChainedSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);

        TestdataShadowingChainedAnchor a0 = new TestdataShadowingChainedAnchor("a0");
        TestdataShadowingChainedEntity a1 = new TestdataShadowingChainedEntity("a1", a0);
        a1.setAnchor(a0);
        a0.setNextEntity(a1);
        TestdataShadowingChainedEntity a2 = new TestdataShadowingChainedEntity("a2", a1);
        a2.setAnchor(a0);
        a1.setNextEntity(a2);
        TestdataShadowingChainedEntity a3 = new TestdataShadowingChainedEntity("a3", a2);
        a3.setAnchor(a0);
        a2.setNextEntity(a3);

        TestdataShadowingChainedAnchor b0 = new TestdataShadowingChainedAnchor("b0");
        TestdataShadowingChainedEntity b1 = new TestdataShadowingChainedEntity("b1", b0);
        b1.setAnchor(b0);
        b0.setNextEntity(b1);

        TestdataShadowingChainedSolution solution = new TestdataShadowingChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1));

        assertThat(a1.getAnchor()).isSameAs(a0);
        assertThat(a2.getAnchor()).isSameAs(a0);
        assertThat(a3.getAnchor()).isSameAs(a0);
        assertThat(b1.getAnchor()).isSameAs(b0);

        inverseVariableListener.beforeVariableChanged(scoreDirector, a3);
        variableListener.beforeVariableChanged(scoreDirector, a3);
        a3.setChainedObject(b1);
        inverseVariableListener.afterVariableChanged(scoreDirector, a3);
        variableListener.afterVariableChanged(scoreDirector, a3);

        assertThat(a1.getAnchor()).isSameAs(a0);
        assertThat(a2.getAnchor()).isSameAs(a0);
        assertThat(a3.getAnchor()).isSameAs(b0);
        assertThat(b1.getAnchor()).isSameAs(b0);

        InOrder inOrder = inOrder(scoreDirector);
        inOrder.verify(scoreDirector).beforeVariableChanged(anchorVariableDescriptor, a3);
        inOrder.verify(scoreDirector).afterVariableChanged(anchorVariableDescriptor, a3);
        inOrder.verifyNoMoreInteractions();
    }

}
