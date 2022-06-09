package org.optaplanner.quarkus.deployment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.jboss.jandex.IndexView;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.quarkus.deployment.config.OptaPlannerBuildTimeConfig;

class OptaPlannerProcessorTest {

    @Test
    void customScoreDrl_overrides_solverConfig() {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig()
                .withScoreDrls("config_constraints.drl");
        SolverConfig solverConfig = new SolverConfig().withScoreDirectorFactory(scoreDirectorFactoryConfig);
        OptaPlannerProcessor optaPlannerProcessor = mockOptaPlannerProcessor();
        when(optaPlannerProcessor.constraintsDrl()).thenReturn(Optional.of("some.drl"));

        optaPlannerProcessor.applyScoreDirectorFactoryProperties(mock(IndexView.class), solverConfig);
        assertThat(scoreDirectorFactoryConfig.getScoreDrlList()).containsExactly("some.drl");
    }

    @Test
    void defaultScoreDrl_does_not_override_solverConfig() {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig()
                .withScoreDrls("config_constraints.drl");
        SolverConfig solverConfig = new SolverConfig().withScoreDirectorFactory(scoreDirectorFactoryConfig);
        OptaPlannerProcessor optaPlannerProcessor = mockOptaPlannerProcessor();
        when(optaPlannerProcessor.constraintsDrl()).thenReturn(Optional.empty());
        when(optaPlannerProcessor.defaultConstraintsDrl())
                .thenReturn(Optional.of(OptaPlannerBuildTimeConfig.DEFAULT_CONSTRAINTS_DRL_URL));

        optaPlannerProcessor.applyScoreDirectorFactoryProperties(mock(IndexView.class), solverConfig);
        assertThat(scoreDirectorFactoryConfig.getScoreDrlList())
                .containsExactly("config_constraints.drl");
    }

    @Test
    void defaultScoreDrl_applies_if_solverConfig_does_not_define_scoreDrl() {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        SolverConfig solverConfig = new SolverConfig().withScoreDirectorFactory(scoreDirectorFactoryConfig);
        OptaPlannerProcessor optaPlannerProcessor = mockOptaPlannerProcessor();
        when(optaPlannerProcessor.constraintsDrl()).thenReturn(Optional.empty());
        when(optaPlannerProcessor.defaultConstraintsDrl())
                .thenReturn(Optional.of(OptaPlannerBuildTimeConfig.DEFAULT_CONSTRAINTS_DRL_URL));

        optaPlannerProcessor.applyScoreDirectorFactoryProperties(mock(IndexView.class), solverConfig);
        assertThat(scoreDirectorFactoryConfig.getScoreDrlList())
                .containsExactly(OptaPlannerBuildTimeConfig.DEFAULT_CONSTRAINTS_DRL_URL);
    }

    @Test
    void error_if_kie_base_configuration_properties_is_present() {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setKieBaseConfigurationProperties(Collections.emptyMap());
        SolverConfig solverConfig = new SolverConfig().withScoreDirectorFactory(scoreDirectorFactoryConfig);
        OptaPlannerProcessor optaPlannerProcessor = mockOptaPlannerProcessor();
        when(optaPlannerProcessor.constraintsDrl()).thenReturn(Optional.empty());
        when(optaPlannerProcessor.defaultConstraintsDrl())
                .thenReturn(Optional.of(OptaPlannerBuildTimeConfig.DEFAULT_CONSTRAINTS_DRL_URL));

        assertThatCode(() -> optaPlannerProcessor.applyScoreDirectorFactoryProperties(mock(IndexView.class), solverConfig))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Using kieBaseConfigurationProperties ("
                                + scoreDirectorFactoryConfig.getKieBaseConfigurationProperties()
                                + ") in Quarkus, which is unsupported.");
        assertThat(scoreDirectorFactoryConfig.getScoreDrlList())
                .containsExactly(OptaPlannerBuildTimeConfig.DEFAULT_CONSTRAINTS_DRL_URL);
    }

    private OptaPlannerProcessor mockOptaPlannerProcessor() {
        OptaPlannerProcessor optaPlannerProcessor = mock(OptaPlannerProcessor.class);
        doCallRealMethod().when(optaPlannerProcessor).applyScoreDirectorFactoryProperties(any(), any());
        return optaPlannerProcessor;
    }
}
