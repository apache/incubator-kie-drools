/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.backend.fluent;

import org.drools.scenariosimulation.backend.runner.ScenarioException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleScenarioExecutableBuilderTest {

    @Test
    public void testPseudoClock() {
        KieContainer kieContainerMock = mock(KieContainer.class);
        when(kieContainerMock.getKieSessionModel(anyString())).thenReturn(null);
        assertThatThrownBy(() -> RuleScenarioExecutableBuilder.forcePseudoClock.apply(null, kieContainerMock))
                .isInstanceOf(ScenarioException.class);
    }

}