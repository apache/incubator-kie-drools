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