/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.backend.fluent;

import java.util.Collections;
import java.util.function.Function;

import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.command.RegistryContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidateFactCommandTest {

    @Mock
    private KieSession kieSession;

    @Mock
    private ScenarioResult scenarioResult;

    @Mock
    private FactMappingValue factMappingValue;

    @Mock
    private RegistryContext registryContext;

    @Test
    public void executeTest() {
        when(registryContext.lookup(KieSession.class)).thenReturn(kieSession);
        Function<Object, ValueWrapper> alwaysMatchFunction = ValueWrapper::of;

        ValidateFactCommand validateFactCommand = new ValidateFactCommand(asList(new FactCheckerHandle(String.class, alwaysMatchFunction, scenarioResult)));

        when(kieSession.getObjects(any(ObjectFilter.class))).thenReturn(Collections.singleton(null));
        validateFactCommand.execute(registryContext);
        verify(scenarioResult, times(1)).setResult(anyBoolean());

        reset(scenarioResult);

        when(kieSession.getObjects(any(ObjectFilter.class))).thenReturn(Collections.emptyList());
        when(scenarioResult.getFactMappingValue()).thenReturn(factMappingValue);
        validateFactCommand.execute(registryContext);
        verify(scenarioResult, times(0)).setResult(anyBoolean());
    }
}