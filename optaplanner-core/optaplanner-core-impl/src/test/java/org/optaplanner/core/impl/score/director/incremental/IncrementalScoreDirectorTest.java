package org.optaplanner.core.impl.score.director.incremental;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.ConstraintMatchAwareIncrementalScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedSolution;

class IncrementalScoreDirectorTest {

    @Test
    void variableListener() {
        TestdataShadowingChainedAnchor a0 = new TestdataShadowingChainedAnchor("a0");
        TestdataShadowingChainedEntity a1 = new TestdataShadowingChainedEntity("a1", a0);
        a0.setNextEntity(a1);
        TestdataShadowingChainedEntity a2 = new TestdataShadowingChainedEntity("a2", a1);
        a1.setNextEntity(a2);
        TestdataShadowingChainedEntity a3 = new TestdataShadowingChainedEntity("a3", a2);
        a2.setNextEntity(a3);

        TestdataShadowingChainedAnchor b0 = new TestdataShadowingChainedAnchor("b0");
        TestdataShadowingChainedEntity b1 = new TestdataShadowingChainedEntity("b1", b0);
        b0.setNextEntity(b1);

        TestdataShadowingChainedSolution solution = new TestdataShadowingChainedSolution("solution");
        List<TestdataShadowingChainedAnchor> anchorList = Arrays.asList(a0, b0);
        solution.setChainedAnchorList(anchorList);
        List<TestdataShadowingChainedEntity> originalEntityList = Arrays.asList(a1, a2, a3, b1);
        solution.setChainedEntityList(originalEntityList);

        SolutionDescriptor<TestdataShadowingChainedSolution> solutionDescriptor = TestdataShadowingChainedSolution
                .buildSolutionDescriptor();
        IncrementalScoreDirectorFactory<TestdataShadowingChainedSolution, SimpleScore> scoreDirectorFactory =
                mock(IncrementalScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        IncrementalScoreCalculator<TestdataShadowingChainedSolution, SimpleScore> incrementalScoreCalculator =
                mock(IncrementalScoreCalculator.class);
        IncrementalScoreDirector<TestdataShadowingChainedSolution, SimpleScore> scoreDirector =
                new IncrementalScoreDirector<TestdataShadowingChainedSolution, SimpleScore>(scoreDirectorFactory, false, false,
                        incrementalScoreCalculator) {
                    @Override
                    public SimpleScore calculateScore() {
                        return SimpleScore.of(-100);
                    }
                };
        scoreDirector.setWorkingSolution(solution);
        reset(incrementalScoreCalculator);

        assertThat(b1.getNextEntity()).isEqualTo(null);

        scoreDirector.beforeVariableChanged(a3, "chainedObject");
        a3.setChainedObject(b1);
        scoreDirector.afterVariableChanged(a3, "chainedObject");
        scoreDirector.triggerVariableListeners();
        assertThat(b1.getNextEntity()).isEqualTo(a3);

        InOrder inOrder = inOrder(incrementalScoreCalculator);
        inOrder.verify(incrementalScoreCalculator, times(1)).beforeVariableChanged(a3, "chainedObject");
        inOrder.verify(incrementalScoreCalculator, times(1)).afterVariableChanged(a3, "chainedObject");
        inOrder.verify(incrementalScoreCalculator, times(1)).beforeVariableChanged(b1, "nextEntity");
        inOrder.verify(incrementalScoreCalculator, times(1)).afterVariableChanged(b1, "nextEntity");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void illegalStateExceptionThrownWhenConstraintMatchNotEnabled() {
        IncrementalScoreDirector<Object, SimpleScore> director =
                new IncrementalScoreDirector<>(mockIncrementalScoreDirectorFactory(), false, false,
                        mockIncrementalScoreCalculator(false));
        director.setWorkingSolution(new Object());
        assertThatIllegalStateException()
                .isThrownBy(director::getConstraintMatchTotalMap)
                .withMessageContaining("constraintMatchEnabled");
    }

    @Test
    void constraintMatchTotalsNeverNull() {
        IncrementalScoreDirector<Object, SimpleScore> director = new IncrementalScoreDirector<>(
                mockIncrementalScoreDirectorFactory(), false, true, mockIncrementalScoreCalculator(true));
        director.setWorkingSolution(new Object());
        assertThat(director.getConstraintMatchTotalMap()).isNotNull();
    }

    @Test
    void constraintMatchIsNotEnabledWhenScoreCalculatorNotConstraintMatchAware() {
        IncrementalScoreDirector<Object, ?> director =
                new IncrementalScoreDirector<>(mockIncrementalScoreDirectorFactory(), false,
                        true,
                        mockIncrementalScoreCalculator(false));
        assertThat(director.isConstraintMatchEnabled()).isFalse();
    }

    @SuppressWarnings("unchecked")
    private IncrementalScoreDirectorFactory<Object, SimpleScore> mockIncrementalScoreDirectorFactory() {
        IncrementalScoreDirectorFactory<Object, SimpleScore> factory = mock(IncrementalScoreDirectorFactory.class);
        when(factory.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        when(factory.getSolutionDescriptor()).thenReturn(mock(SolutionDescriptor.class));
        return factory;
    }

    @SuppressWarnings("unchecked")
    private IncrementalScoreCalculator<Object, SimpleScore> mockIncrementalScoreCalculator(boolean constraintMatchAware) {
        return constraintMatchAware
                ? mock(ConstraintMatchAwareIncrementalScoreCalculator.class)
                : mock(IncrementalScoreCalculator.class);
    }
}
