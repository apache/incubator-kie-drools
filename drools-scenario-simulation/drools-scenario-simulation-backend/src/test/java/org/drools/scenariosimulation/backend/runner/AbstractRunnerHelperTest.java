/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.backend.runner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.scenariosimulation.backend.expression.BaseExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.runner.model.ResultWrapper;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.junit.Test;
import org.kie.api.runtime.KieContainer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractRunnerHelperTest {

    AbstractRunnerHelper abstractRunnerHelper = new AbstractRunnerHelper() {
        @Override
        protected ScenarioResultMetadata extractResultMetadata(Map<String, Object> requestContext, ScenarioWithIndex scenarioWithIndex) {
            return null;
        }

        @Override
        protected Map<String, Object> executeScenario(KieContainer kieContainer, ScenarioRunnerData scenarioRunnerData, ExpressionEvaluatorFactory expressionEvaluatorFactory, SimulationDescriptor simulationDescriptor) {
            return null;
        }

        @Override
        protected void verifyConditions(SimulationDescriptor simulationDescriptor, ScenarioRunnerData scenarioRunnerData, ExpressionEvaluatorFactory expressionEvaluatorFactory, Map<String, Object> requestContext) {

        }

        @Override
        protected Object createObject(String className, Map<List<String>, Object> params, ClassLoader classLoader) {
            return null;
        }
    };

    @Test
    public void fillResult() {
        FactIdentifier factIdentifier = FactIdentifier.create("MyInstance", String.class.getCanonicalName());
        ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create("MyProperty", FactMappingType.GIVEN);
        FactMappingValue expectedResultSpy = spy(new FactMappingValue(factIdentifier, expressionIdentifier, "value"));
        AtomicReference<ResultWrapper> resultWrapperAtomicReference = new AtomicReference<>();
        Supplier<ResultWrapper<?>> resultWrapperSupplier = resultWrapperAtomicReference::get;
        ExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(AbstractRunnerHelper.class.getClassLoader());

        // Success
        resultWrapperAtomicReference.set(ResultWrapper.createResult("value"));
        assertTrue(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult());
        verify(expectedResultSpy, times(1)).resetStatus();

        reset(expectedResultSpy);

        // Fail with expected value
        resultWrapperAtomicReference.set(ResultWrapper.createErrorResult("value", "value1"));
        assertFalse(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult());
        verify(expectedResultSpy, times(1)).setErrorValue(eq("value"));

        reset(expectedResultSpy);

        // Fail with exception while reverting actual value
        resultWrapperAtomicReference.set(ResultWrapper.createErrorResult("value", "value1"));
        ExpressionEvaluator expressionEvaluatorMock = mock(ExpressionEvaluator.class);
        when(expressionEvaluatorMock.fromObjectToExpression(any())).thenThrow(new IllegalArgumentException("Error"));
        assertFalse(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluatorMock).getResult());
        verify(expectedResultSpy, times(1)).setExceptionMessage(eq("Error"));

        reset(expectedResultSpy);

        // Fail with exception
        resultWrapperAtomicReference.set(ResultWrapper.createErrorResultWithErrorMessage("detailedError"));
        assertFalse(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult());
        verify(expectedResultSpy, times(1)).setExceptionMessage(eq("detailedError"));
    }
}