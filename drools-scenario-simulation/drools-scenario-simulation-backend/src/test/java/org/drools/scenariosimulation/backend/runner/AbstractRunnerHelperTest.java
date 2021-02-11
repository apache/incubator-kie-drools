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
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.backend.expression.BaseExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.junit.Test;
import org.kie.api.runtime.KieContainer;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        protected Map<String, Object> executeScenario(KieContainer kieContainer, ScenarioRunnerData scenarioRunnerData, ExpressionEvaluatorFactory expressionEvaluatorFactory, ScesimModelDescriptor scesimModelDescriptor, Settings settings) {
            return null;
        }

        @Override
        protected void verifyConditions(ScesimModelDescriptor scesimModelDescriptor, ScenarioRunnerData scenarioRunnerData, ExpressionEvaluatorFactory expressionEvaluatorFactory, Map<String, Object> requestContext) {

        }

        @Override
        protected Object createObject(ValueWrapper<Object> initialInstance, String className, Map<List<String>, Object> params, ClassLoader classLoader) {
            return null;
        }
    };

    @Test
    public void isFactMappingValueToSkip() {
        FactIdentifier factIdentifier = FactIdentifier.create("MyInstance", String.class.getCanonicalName());
        ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create("MyProperty", FactMappingType.GIVEN);

        FactMappingValue factMappingValueWithValidValue = new FactMappingValue(factIdentifier, expressionIdentifier, VALUE);
        assertFalse(abstractRunnerHelper.isFactMappingValueToSkip(factMappingValueWithValidValue));

        FactMappingValue factMappingValueWithoutValue = new FactMappingValue(factIdentifier, expressionIdentifier, null);
        assertTrue(abstractRunnerHelper.isFactMappingValueToSkip(factMappingValueWithoutValue));
    }

    @Test
    public void fillResult() {
        FactIdentifier factIdentifier = FactIdentifier.create("MyInstance", String.class.getCanonicalName());
        ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create("MyProperty", FactMappingType.GIVEN);
        FactMappingValue expectedResultSpy = spy(new FactMappingValue(factIdentifier, expressionIdentifier, VALUE));
        AtomicReference<ValueWrapper> resultWrapperAtomicReference = new AtomicReference<>();
        Supplier<ValueWrapper<?>> resultWrapperSupplier = resultWrapperAtomicReference::get;
        ExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(AbstractRunnerHelper.class.getClassLoader());

        // Success
        resultWrapperAtomicReference.set(ValueWrapper.of(VALUE));
        assertTrue(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult());
        verify(expectedResultSpy, times(1)).resetStatus();

        reset(expectedResultSpy);

        // Fail with expected value
        resultWrapperAtomicReference.set(ValueWrapper.errorWithValidValue(VALUE, "value1"));
        assertFalse(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult());
        verify(expectedResultSpy, times(1)).setErrorValue(eq(VALUE));

        reset(expectedResultSpy);

        // Fail with exception while reverting actual value
        resultWrapperAtomicReference.set(ValueWrapper.errorWithValidValue(VALUE, "value1"));
        ExpressionEvaluator expressionEvaluatorMock = mock(ExpressionEvaluator.class);
        when(expressionEvaluatorMock.fromObjectToExpression(any())).thenThrow(new IllegalArgumentException("Error"));
        assertFalse(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluatorMock).getResult());
        verify(expectedResultSpy, times(1)).setExceptionMessage(eq("Error"));

        reset(expectedResultSpy);

        // Fail with exception
        resultWrapperAtomicReference.set(ValueWrapper.errorWithMessage("detailedError"));
        assertFalse(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult());
        verify(expectedResultSpy, times(1)).setExceptionMessage(eq("detailedError"));
    }

    @Test
    public void getResultWrapper() {
        ExpressionEvaluator expressionEvaluatorMock = mock(ExpressionEvaluator.class);
        Object resultRaw = "test";
        Object expectedResultRaw = "";
        String collectionError = "Impossible to find elements in the collection to satisfy the conditions";
        String genericErrorMessage = "errorMessage";

        // case 1: succeed
        when(expressionEvaluatorMock.evaluateUnaryExpression(any(), any(), any(Class.class))).thenReturn(true);
        ValueWrapper valueWrapper = abstractRunnerHelper.getResultWrapper(String.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, String.class);
        assertTrue(valueWrapper.isValid());

        // case 2: failed with actual value
        when(expressionEvaluatorMock.evaluateUnaryExpression(any(), any(), any(Class.class))).thenReturn(false);
        valueWrapper = abstractRunnerHelper.getResultWrapper(String.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, String.class);
        assertFalse(valueWrapper.isValid());
        assertEquals(resultRaw, valueWrapper.getValue());

        // case 3: failed without actual value (list)
        valueWrapper = abstractRunnerHelper.getResultWrapper(List.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, List.class);
        assertEquals(collectionError, valueWrapper.getErrorMessage().get());

        // case 4: failed without actual value (map)
        valueWrapper = abstractRunnerHelper.getResultWrapper(Map.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, Map.class);
        assertEquals(collectionError, valueWrapper.getErrorMessage().get());

        // case 5: failed with generic exception
        when(expressionEvaluatorMock.evaluateUnaryExpression(any(), any(), any(Class.class))).thenThrow(new IllegalArgumentException(genericErrorMessage));
        FactMappingValue expectedResult5 = new FactMappingValue();
        valueWrapper = abstractRunnerHelper.getResultWrapper(Map.class.getCanonicalName(), expectedResult5, expressionEvaluatorMock, expectedResultRaw, resultRaw, Map.class);
        assertEquals(genericErrorMessage, valueWrapper.getErrorMessage().get());
        assertEquals(genericErrorMessage, expectedResult5.getExceptionMessage());
    }
}