/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.api.runtime.KieSession;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DroolsScoreDirectorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void illegalStateExceptionThrownWhenConstraintMatchNotEnabled() {
        DroolsScoreDirector<Object> director = new DroolsScoreDirector<>(mockDroolsScoreDirectorFactory(), false, false);
        director.setWorkingSolution(new Object());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("constraintMatchEnabled");
        director.getConstraintMatchTotals();
    }

    @Test
    public void constraintMatchTotalsNeverNull() {
        DroolsScoreDirector<Object> director = new DroolsScoreDirector<>(mockDroolsScoreDirectorFactory(), false, true);
        director.setWorkingSolution(new Object());
        assertNotNull(director.getConstraintMatchTotals());
        assertNotNull(director.getConstraintMatchTotalMap());
    }

    @Test
    public void indictmentMapNeverNull() {
        DroolsScoreDirector<Object> director = new DroolsScoreDirector<>(mockDroolsScoreDirectorFactory(), false, true);
        director.setWorkingSolution(new Object());
        assertNotNull(director.getIndictmentMap());
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
