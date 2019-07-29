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

package org.drools.scenariosimulation.backend.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.runner.model.ResultWrapper;
import org.drools.scenariosimulation.backend.runner.model.ScenarioExpect;
import org.drools.scenariosimulation.backend.runner.model.ScenarioGiven;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.kie.api.runtime.KieContainer;

import static java.util.stream.Collectors.toList;
import static org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils.isCollection;
import static org.drools.scenariosimulation.backend.runner.model.ResultWrapper.createErrorResult;
import static org.drools.scenariosimulation.backend.runner.model.ResultWrapper.createErrorResultWithErrorMessage;
import static org.drools.scenariosimulation.backend.runner.model.ResultWrapper.createResult;

public abstract class AbstractRunnerHelper {

    public void run(KieContainer kieContainer, SimulationDescriptor simulationDescriptor, ScenarioWithIndex scenarioWithIndex, ExpressionEvaluator expressionEvaluator, ClassLoader classLoader, ScenarioRunnerData scenarioRunnerData) {

        Scenario scenario = scenarioWithIndex.getScenario();

        extractGivenValues(simulationDescriptor, scenario.getUnmodifiableFactMappingValues(), classLoader, expressionEvaluator)
                .forEach(scenarioRunnerData::addGiven);

        extractExpectedValues(scenario.getUnmodifiableFactMappingValues()).forEach(scenarioRunnerData::addExpect);

        Map<String, Object> requestContext = executeScenario(kieContainer,
                                                             scenarioRunnerData,
                                                             expressionEvaluator,
                                                             simulationDescriptor);

        scenarioRunnerData.setMetadata(extractResultMetadata(requestContext, scenarioWithIndex));

        verifyConditions(simulationDescriptor,
                         scenarioRunnerData,
                         expressionEvaluator,
                         requestContext);

        validateAssertion(scenarioRunnerData.getResults(),
                          scenario);
    }

    protected List<ScenarioGiven> extractGivenValues(SimulationDescriptor simulationDescriptor,
                                                     List<FactMappingValue> factMappingValues,
                                                     ClassLoader classLoader,
                                                     ExpressionEvaluator expressionEvaluator) {
        List<ScenarioGiven> scenarioGiven = new ArrayList<>();

        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier =
                groupByFactIdentifierAndFilter(factMappingValues, FactMappingType.GIVEN);

        for (Map.Entry<FactIdentifier, List<FactMappingValue>> entry : groupByFactIdentifier.entrySet()) {

            FactIdentifier factIdentifier = entry.getKey();

            // for each fact, create a map of path to fields and values to set
            Map<List<String>, Object> paramsForBean = getParamsForBean(simulationDescriptor,
                                                                       factIdentifier,
                                                                       entry.getValue(),
                                                                       expressionEvaluator);

            Object bean = getDirectMapping(paramsForBean)
                    .orElseGet(() -> createObject(factIdentifier.getClassName(), paramsForBean, classLoader));

            scenarioGiven.add(new ScenarioGiven(factIdentifier, bean));
        }

        return scenarioGiven;
    }

    protected ResultWrapper<Object> getDirectMapping(Map<List<String>, Object> params) {
        // if a direct mapping exists (no steps to reach the field) the value itself is the object (just converted)
        for (Map.Entry<List<String>, Object> entry : params.entrySet()) {
            if (entry.getKey().isEmpty()) {
                return ResultWrapper.createResult(entry.getValue());
            }
        }
        return ResultWrapper.createErrorResultWithErrorMessage("No direct mapping available");
    }

    protected List<ScenarioExpect> extractExpectedValues(List<FactMappingValue> factMappingValues) {
        List<ScenarioExpect> scenarioExpect = new ArrayList<>();

        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier =
                groupByFactIdentifierAndFilter(factMappingValues, FactMappingType.EXPECT);

        Set<FactIdentifier> inputFacts = factMappingValues.stream()
                .filter(elem -> FactMappingType.GIVEN.equals(elem.getExpressionIdentifier().getType()))
                .map(FactMappingValue::getFactIdentifier)
                .collect(Collectors.toSet());

        for (Map.Entry<FactIdentifier, List<FactMappingValue>> entry : groupByFactIdentifier.entrySet()) {

            FactIdentifier factIdentifier = entry.getKey();

            scenarioExpect.add(new ScenarioExpect(factIdentifier, entry.getValue(), !inputFacts.contains(factIdentifier)));
        }

        return scenarioExpect;
    }

    protected Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifierAndFilter(List<FactMappingValue> factMappingValues,
                                                                                         FactMappingType type) {
        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier = new HashMap<>();
        for (FactMappingValue factMappingValue : factMappingValues) {
            FactIdentifier factIdentifier = factMappingValue.getFactIdentifier();

            // null means skip
            if (factMappingValue.getRawValue() == null) {
                continue;
            }

            ExpressionIdentifier expressionIdentifier = factMappingValue.getExpressionIdentifier();
            if (expressionIdentifier == null) {
                throw new IllegalArgumentException("ExpressionIdentifier malformed");
            }

            if (!Objects.equals(expressionIdentifier.getType(), type)) {
                continue;
            }

            groupByFactIdentifier.computeIfAbsent(factIdentifier, key -> new ArrayList<>())
                    .add(factMappingValue);
        }
        return groupByFactIdentifier;
    }

    protected Map<List<String>, Object> getParamsForBean(SimulationDescriptor simulationDescriptor,
                                                         FactIdentifier factIdentifier,
                                                         List<FactMappingValue> factMappingValues,
                                                         ExpressionEvaluator expressionEvaluator) {
        Map<List<String>, Object> paramsForBean = new HashMap<>();

        for (FactMappingValue factMappingValue : factMappingValues) {
            ExpressionIdentifier expressionIdentifier = factMappingValue.getExpressionIdentifier();

            FactMapping factMapping = simulationDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                    .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

            List<String> pathToField = factMapping.getExpressionElementsWithoutClass().stream()
                    .map(ExpressionElement::getStep).collect(toList());

            try {
                Object value = expressionEvaluator.evaluateLiteralExpression(factMapping.getClassName(),
                                                                             factMapping.getGenericTypes(),
                                                                             factMappingValue.getRawValue());
                paramsForBean.put(pathToField, value);
            } catch (RuntimeException e) {
                factMappingValue.setExceptionMessage(e.getMessage());
                throw new ScenarioException(e.getMessage(), e);
            }
        }

        return paramsForBean;
    }

    protected void validateAssertion(List<ScenarioResult> scenarioResults, Scenario scenario) {
        boolean scenarioFailed = false;
        for (ScenarioResult scenarioResult : scenarioResults) {
            if (!scenarioResult.getResult()) {
                scenarioFailed = true;
            }
        }

        if (scenarioFailed) {
            throw new ScenarioException("Scenario '" + scenario.getDescription() + "' failed");
        }
    }

    protected ScenarioResult fillResult(FactMappingValue expectedResult,
                                        FactIdentifier factIdentifier,
                                        Supplier<ResultWrapper<?>> resultSupplier,
                                        ExpressionEvaluator expressionEvaluator) {
        ResultWrapper<?> resultValue = resultSupplier.get();

        if (!resultValue.isSatisfied() && resultValue.getErrorMessage().isPresent()) {
            expectedResult.setExceptionMessage(resultValue.getErrorMessage().get());
        } else if (!resultValue.isSatisfied()) {
            expectedResult.setErrorValue(expressionEvaluator.fromObjectToExpression(resultValue.getResult()));
        } else {
            expectedResult.resetStatus();
        }

        return new ScenarioResult(factIdentifier, expectedResult, resultValue.getResult()).setResult(resultValue.isSatisfied());
    }

    protected ResultWrapper getResultWrapper(String className,
                                             FactMappingValue expectedResult,
                                             ExpressionEvaluator expressionEvaluator,
                                             Object expectedResultRaw,
                                             Object resultRaw,
                                             Class<?> resultClass) {
        try {
            boolean evaluationSucceed = expressionEvaluator.evaluateUnaryExpression(expectedResultRaw, resultRaw, resultClass);
            if (evaluationSucceed) {
                return createResult(resultRaw);
            } else if (isCollection(className)) {
                // no suggestions for collection yet
                return createErrorResultWithErrorMessage("Impossible to find elements in the collection to satisfy the conditions");
            } else {
                return createErrorResult(resultRaw, expectedResultRaw);
            }
        } catch (Exception e) {
            expectedResult.setExceptionMessage(e.getMessage());
            throw new ScenarioException(e.getMessage(), e);
        }
    }

    protected abstract ScenarioResultMetadata extractResultMetadata(Map<String, Object> requestContext,
                                                                    ScenarioWithIndex scenarioWithIndex);

    protected abstract Map<String, Object> executeScenario(KieContainer kieContainer,
                                                           ScenarioRunnerData scenarioRunnerData,
                                                           ExpressionEvaluator expressionEvaluator,
                                                           SimulationDescriptor simulationDescriptor);

    protected abstract void verifyConditions(SimulationDescriptor simulationDescriptor,
                                             ScenarioRunnerData scenarioRunnerData,
                                             ExpressionEvaluator expressionEvaluator,
                                             Map<String, Object> requestContext);

    protected abstract Object createObject(String className,
                                           Map<List<String>, Object> params,
                                           ClassLoader classLoader);
}
