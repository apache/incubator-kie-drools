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
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorResult;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.junit.Test;
import org.kie.api.runtime.KieContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.mockito.ArgumentMatchers.any;
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
        assertThat(abstractRunnerHelper.isFactMappingValueToSkip(factMappingValueWithValidValue)).isFalse();

        FactMappingValue factMappingValueWithoutValue = new FactMappingValue(factIdentifier, expressionIdentifier, null);
        assertThat(abstractRunnerHelper.isFactMappingValueToSkip(factMappingValueWithoutValue)).isTrue();
    }

    @Test
    public void fillResult() {
        FactIdentifier factIdentifier = FactIdentifier.create("MyInstance", String.class.getCanonicalName());
        ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create("MyProperty", FactMappingType.GIVEN);
        FactMappingValue expectedResultSpy = spy(new FactMappingValue(factIdentifier, expressionIdentifier, VALUE));
        AtomicReference<ValueWrapper<Object>> resultWrapperAtomicReference = new AtomicReference<>();
        Supplier<ValueWrapper<?>> resultWrapperSupplier = resultWrapperAtomicReference::get;
        ExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(AbstractRunnerHelper.class.getClassLoader());

        // Success
        resultWrapperAtomicReference.set(ValueWrapper.of(VALUE));
        assertThat(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult()).isTrue();
        verify(expectedResultSpy, times(1)).resetStatus();

        reset(expectedResultSpy);

        // Fail with expected value
        resultWrapperAtomicReference.set(ValueWrapper.errorWithValidValue(VALUE, "value1"));
        assertThat(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult()).isFalse();
        verify(expectedResultSpy, times(1)).setErrorValue(VALUE);

        reset(expectedResultSpy);

        // Fail with exception while reverting actual value
        resultWrapperAtomicReference.set(ValueWrapper.errorWithValidValue(VALUE, "value1"));
        ExpressionEvaluator expressionEvaluatorMock = mock(ExpressionEvaluator.class);
        when(expressionEvaluatorMock.fromObjectToExpression(any())).thenThrow(new IllegalArgumentException("Error"));
        assertThat(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluatorMock).getResult()).isFalse();
        verify(expectedResultSpy, times(1)).setExceptionMessage("Error");

        reset(expectedResultSpy);

        // Fail in collection case
        List<String> pathToValue = List.of("field1", "fields2");
        resultWrapperAtomicReference.set(ValueWrapper.errorWithCollectionPathToValue(VALUE, pathToValue));
        assertThat(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult()).isFalse();
        verify(expectedResultSpy, times(1)).setCollectionPathToValue(pathToValue);
        verify(expectedResultSpy, times(1)).setErrorValue(VALUE);

        // Fail with exception
        resultWrapperAtomicReference.set(ValueWrapper.errorWithMessage("detailedError"));
        assertThat(abstractRunnerHelper.fillResult(expectedResultSpy, resultWrapperSupplier, expressionEvaluator).getResult()).isFalse();
        verify(expectedResultSpy, times(1)).setExceptionMessage("detailedError");
    }
    
	@Test
    public void getResultWrapper() {
        ExpressionEvaluator expressionEvaluatorMock = mock(ExpressionEvaluator.class);
        Object resultRaw = "test";
        Object expectedResultRaw = "";
        String collectionWrongValue = "value";
        String collectionValuePath = "Item #: 1";
        String genericErrorMessage = "errorMessage";

        // case 1: succeed
        when(expressionEvaluatorMock.evaluateUnaryExpression(any(), any(), any(Class.class))).thenReturn(ExpressionEvaluatorResult.ofSuccessful());
        ValueWrapper<Object> valueWrapper = abstractRunnerHelper.getResultWrapper(String.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, String.class);
        assertThat(valueWrapper.isValid()).isTrue();
        assertThat(valueWrapper.getCollectionPathToValue()).isNull();

        // case 2: failed with actual value
        when(expressionEvaluatorMock.evaluateUnaryExpression(any(), any(), any(Class.class))).thenReturn(ExpressionEvaluatorResult.ofFailed());
        valueWrapper = abstractRunnerHelper.getResultWrapper(String.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, String.class);
        assertThat(valueWrapper.isValid()).isFalse();
        assertThat(valueWrapper.getValue()).isEqualTo(resultRaw);
        assertThat(valueWrapper.getCollectionPathToValue()).isNull();

        // case 3: failed without actual value (list)
        valueWrapper = abstractRunnerHelper.getResultWrapper(List.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, List.class);
        assertThat(valueWrapper.getErrorMessage()).isNotPresent();
        assertThat(valueWrapper.getCollectionPathToValue()).isEmpty();
        assertThat(valueWrapper.getValue()).isNull();

        // case 4: failed without actual value (map)
        valueWrapper = abstractRunnerHelper.getResultWrapper(Map.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, Map.class);
        assertThat(valueWrapper.getErrorMessage()).isNotPresent();
        assertThat(valueWrapper.getCollectionPathToValue()).isEmpty();
        assertThat(valueWrapper.getValue()).isNull();

        // case 5: failed with wrong value (list)
        ExpressionEvaluatorResult result = ExpressionEvaluatorResult.ofFailed(collectionWrongValue, collectionValuePath);
        when(expressionEvaluatorMock.evaluateUnaryExpression(any(), any(), any(Class.class))).thenReturn(result);
        valueWrapper = abstractRunnerHelper.getResultWrapper(List.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, List.class);
        assertThat(valueWrapper.getErrorMessage()).isNotPresent();
        assertThat(valueWrapper.getCollectionPathToValue()).hasSize(1);
        assertThat(valueWrapper.getValue()).isEqualTo(collectionWrongValue);

        // case 6: failed without actual value (map)
        valueWrapper = abstractRunnerHelper.getResultWrapper(Map.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, Map.class);
        assertThat(valueWrapper.getErrorMessage()).isNotPresent();
        assertThat(valueWrapper.getCollectionPathToValue()).hasSize(1);
        assertThat(valueWrapper.getValue()).isEqualTo(collectionWrongValue);

        // case 7: failed without wrong value (list)
        result = ExpressionEvaluatorResult.ofFailed(null, collectionValuePath);
        when(expressionEvaluatorMock.evaluateUnaryExpression(any(), any(), any(Class.class))).thenReturn(result);
        valueWrapper = abstractRunnerHelper.getResultWrapper(List.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, List.class);
        assertThat(valueWrapper.getErrorMessage()).isNotPresent();
        assertThat(valueWrapper.getCollectionPathToValue()).hasSize(1);
        assertThat(valueWrapper.getValue()).isNull();

        // case 8: failed without actual value (map)
        valueWrapper = abstractRunnerHelper.getResultWrapper(Map.class.getCanonicalName(), new FactMappingValue(), expressionEvaluatorMock, expectedResultRaw, resultRaw, Map.class);
        assertThat(valueWrapper.getErrorMessage()).isNotPresent();
        assertThat(valueWrapper.getCollectionPathToValue()).hasSize(1);
        assertThat(valueWrapper.getValue()).isNull();

        // case 9: failed with generic exception
        when(expressionEvaluatorMock.evaluateUnaryExpression(any(), any(), any(Class.class))).thenThrow(new IllegalArgumentException(genericErrorMessage));
        FactMappingValue expectedResult5 = new FactMappingValue();
        valueWrapper = abstractRunnerHelper.getResultWrapper(Map.class.getCanonicalName(), expectedResult5, expressionEvaluatorMock, expectedResultRaw, resultRaw, Map.class);
        assertThat(valueWrapper.getErrorMessage().get()).isEqualTo(genericErrorMessage);
        assertThat(expectedResult5.getExceptionMessage()).isEqualTo(genericErrorMessage);
    }
}