package org.optaplanner.constraint.drl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;

class DrlScoreDirectorTest {

    @Test
    void illegalStateExceptionThrownWhenConstraintMatchNotEnabled() {
        DrlScoreDirector<Object, ?> director = new DrlScoreDirector<>(mockDroolsScoreDirectorFactory(), false, false);
        director.setWorkingSolution(new Object());
        assertThatIllegalStateException()
                .isThrownBy(director::getConstraintMatchTotalMap)
                .withMessageContaining("constraintMatchEnabled");
    }

    @Test
    void constraintMatchTotalsNeverNull() {
        DrlScoreDirector<Object, ?> director = new DrlScoreDirector<>(mockDroolsScoreDirectorFactory(), false, true);
        director.setWorkingSolution(new Object());
        assertThat(director.getConstraintMatchTotalMap()).isNotNull();
        assertThat(director.getConstraintMatchTotalMap()).isNotNull();
    }

    @Test
    void indictmentMapNeverNull() {
        DrlScoreDirector<Object, ?> director = new DrlScoreDirector<>(mockDroolsScoreDirectorFactory(), false, true);
        director.setWorkingSolution(new Object());
        assertThat(director.getIndictmentMap()).isNotNull();
    }

    @SuppressWarnings("unchecked")
    private DrlScoreDirectorFactory<Object, SimpleScore> mockDroolsScoreDirectorFactory() {
        DrlScoreDirectorFactory<Object, SimpleScore> factory = mock(DrlScoreDirectorFactory.class);
        when(factory.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        when(factory.getSolutionDescriptor()).thenReturn(mock(SolutionDescriptor.class));
        when(factory.newKieSession()).thenReturn(
                mock(KieSession.class, withSettings().extraInterfaces(RuleEventManager.class)));
        return factory;
    }
}
