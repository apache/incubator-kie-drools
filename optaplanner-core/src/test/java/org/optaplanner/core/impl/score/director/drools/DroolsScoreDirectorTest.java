/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.score.director.drools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;

public class DroolsScoreDirectorTest {

    @Test
    public void illegalStateExceptionThrownWhenConstraintMatchNotEnabled() {
        DroolsScoreDirector<Object> director = new DroolsScoreDirector<>(mockDroolsScoreDirectorFactory(), false, false);
        director.setWorkingSolution(new Object());
        assertThatIllegalStateException()
                .isThrownBy(director::getConstraintMatchTotalMap)
                .withMessageContaining("constraintMatchEnabled");
    }

    @Test
    public void constraintMatchTotalsNeverNull() {
        DroolsScoreDirector<Object> director = new DroolsScoreDirector<>(mockDroolsScoreDirectorFactory(), false, true);
        director.setWorkingSolution(new Object());
        assertThat(director.getConstraintMatchTotalMap()).isNotNull();
        assertThat(director.getConstraintMatchTotalMap()).isNotNull();
    }

    @Test
    public void indictmentMapNeverNull() {
        DroolsScoreDirector<Object> director = new DroolsScoreDirector<>(mockDroolsScoreDirectorFactory(), false, true);
        director.setWorkingSolution(new Object());
        assertThat(director.getIndictmentMap()).isNotNull();
    }

    @SuppressWarnings("unchecked")
    private DroolsScoreDirectorFactory<Object> mockDroolsScoreDirectorFactory() {
        DroolsScoreDirectorFactory<Object> factory = mock(DroolsScoreDirectorFactory.class);
        when(factory.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        when(factory.getSolutionDescriptor()).thenReturn(mock(SolutionDescriptor.class));
        when(factory.newKieSession()).thenReturn(
                mock(KieSession.class, withSettings().extraInterfaces(RuleEventManager.class)));
        return factory;
    }
}
