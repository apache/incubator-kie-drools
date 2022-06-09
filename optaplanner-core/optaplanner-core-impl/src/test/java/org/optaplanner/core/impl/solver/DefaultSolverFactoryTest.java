package org.optaplanner.core.impl.solver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class DefaultSolverFactoryTest {

    @Test
    void moveThreadCountAutoIsCorrectlyResolvedWhenCpuCountIsPositive() {
        assertThat(mockMoveThreadCountResolverAuto(1)).isNull();
        assertThat(mockMoveThreadCountResolverAuto(2)).isNull();
        assertThat(mockMoveThreadCountResolverAuto(4)).isEqualTo(2);
        assertThat(mockMoveThreadCountResolverAuto(5)).isEqualTo(3);
        assertThat(mockMoveThreadCountResolverAuto(6)).isEqualTo(4);
        assertThat(mockMoveThreadCountResolverAuto(100)).isEqualTo(4);
    }

    @Test
    void moveThreadCountAutoIsResolvedToNullWhenCpuCountIsNegative() {
        assertThat(mockMoveThreadCountResolverAuto(-1)).isNull();
    }

    private Integer mockMoveThreadCountResolverAuto(int mockCpuCount) {
        DefaultSolverFactory.MoveThreadCountResolver moveThreadCountResolverMock =
                new DefaultSolverFactory.MoveThreadCountResolver() {
                    @Override
                    protected int getAvailableProcessors() {
                        return mockCpuCount;
                    }
                };

        return moveThreadCountResolverMock.resolveMoveThreadCount(SolverConfig.MOVE_THREAD_COUNT_AUTO);
    }

    @Test
    void moveThreadCountIsCorrectlyResolvedWhenValueIsPositive() {
        assertThat(resolveMoveThreadCount("2")).isEqualTo(2);
    }

    @Test
    void moveThreadCountThrowsExceptionWhenValueIsNegative() {
        assertThatIllegalArgumentException().isThrownBy(() -> resolveMoveThreadCount("-1"));
    }

    @Test
    void moveThreadCountIsResolvedToNullWhenValueIsNone() {
        assertThat(resolveMoveThreadCount(SolverConfig.MOVE_THREAD_COUNT_NONE)).isNull();
    }

    private Integer resolveMoveThreadCount(String moveThreadCountString) {
        DefaultSolverFactory.MoveThreadCountResolver moveThreadCountResolver =
                new DefaultSolverFactory.MoveThreadCountResolver();
        return moveThreadCountResolver.resolveMoveThreadCount(moveThreadCountString);
    }

    @Test
    void cachesScoreDirectorFactory() {
        SolverConfig solverConfig =
                SolverConfig.createFromXmlResource("org/optaplanner/core/config/solver/testdataSolverConfig.xml");
        DefaultSolverFactory<TestdataSolution> defaultSolverFactory = new DefaultSolverFactory<>(solverConfig);

        SolutionDescriptor<TestdataSolution> solutionDescriptor1 = defaultSolverFactory.getSolutionDescriptor();
        InnerScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory1 =
                defaultSolverFactory.getScoreDirectorFactory();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(solutionDescriptor1).isNotNull();
            softly.assertThat(scoreDirectorFactory1).isNotNull();
            softly.assertThat(scoreDirectorFactory1.getSolutionDescriptor()).isSameAs(solutionDescriptor1);
        });

        SolutionDescriptor<TestdataSolution> solutionDescriptor2 = defaultSolverFactory.getSolutionDescriptor();
        InnerScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory2 =
                defaultSolverFactory.getScoreDirectorFactory();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(solutionDescriptor2).isSameAs(solutionDescriptor1);
            softly.assertThat(scoreDirectorFactory2).isSameAs(scoreDirectorFactory1);
        });
    }

}
