/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.scenariosimulation.backend.fluent;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.command.RegistryContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
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

	private ValidateFactCommand validateFactCommand;

    @Before
    public void setUp() {
        when(registryContext.lookup(KieSession.class)).thenReturn(kieSession);
        Function<Object, ValueWrapper> alwaysMatchFunction = ValueWrapper::of;

        validateFactCommand = new ValidateFactCommand(List.of(new FactCheckerHandle(String.class, alwaysMatchFunction, scenarioResult)));
    }
    
    @Test
    public void execute_setResultIsCalled() {
        when(kieSession.getObjects(any(ObjectFilter.class))).thenReturn(Collections.singleton(null));

        validateFactCommand.execute(registryContext);
        
        verify(scenarioResult, times(1)).setResult(anyBoolean());
    }
    
    @Test
    public void execute_setResultIsNotCalled() {
        when(kieSession.getObjects(any(ObjectFilter.class))).thenReturn(List.of());
        when(scenarioResult.getFactMappingValue()).thenReturn(factMappingValue);

        validateFactCommand.execute(registryContext);
        
        verify(scenarioResult, times(0)).setResult(anyBoolean());
    }
}