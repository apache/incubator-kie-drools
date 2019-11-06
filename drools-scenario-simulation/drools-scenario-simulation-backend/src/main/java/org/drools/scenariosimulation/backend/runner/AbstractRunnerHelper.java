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

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.BackgroundData;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.runner.model.ResultWrapper;
import org.drools.scenariosimulation.backend.runner.model.ScenarioExpect;
import org.drools.scenariosimulation.backend.runner.model.InstanceGiven;
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

    public void run(KieContainer kieContainer,
                    ScesimModelDescriptor scesimModelDescriptor,
                    ScenarioWithIndex scenarioWithIndex,
                    ExpressionEvaluatorFactory expressionEvaluatorFactory,
                    ClassLoader classLoader,
                    ScenarioRunnerData scenarioRunnerData,
                    Settings settings,
                    Background background) {

        Scenario scenario = scenarioWithIndex.getScesimData();

        extractBackgroundValues(background,
                                classLoader,
                                expressionEvaluatorFactory)
                .forEach(scenarioRunnerData::addBackground);

        extractGivenValues(scesimModelDescriptor,
                           scenario.getUnmodifiableFactMappingValues(),
                           classLoader,
                           expressionEvaluatorFactory)
                .forEach(scenarioRunnerData::addGiven);

        extractExpectedValues(scenario.getUnmodifiableFactMappingValues()).forEach(scenarioRunnerData::addExpect);

        Map<String, Object> requestContext = executeScenario(kieContainer,
                                                             scenarioRunnerData,
                                                             expressionEvaluatorFactory,
                                                             scesimModelDescriptor, settings);

        scenarioRunnerData.setMetadata(extractResultMetadata(requestContext, scenarioWithIndex));

        verifyConditions(scesimModelDescriptor,
                         scenarioRunnerData,
                         expressionEvaluatorFactory,
                         requestContext);

        validateAssertion(scenarioRunnerData.getResults(),
                          scenario);
    }

    protected List<InstanceGiven> extractBackgroundValues(Background background,
                                                          ClassLoader classLoader,
                                                          ExpressionEvaluatorFactory expressionEvaluatorFactory) {
        List<InstanceGiven> backgrounds = new ArrayList<>();
        for (BackgroundData row : background.getUnmodifiableData()) {
            try {
                List<InstanceGiven> givens = extractGivenValues(background.getScesimModelDescriptor(),
                                                                row.getUnmodifiableFactMappingValues(),
                                                                classLoader,
                                                                expressionEvaluatorFactory);
                backgrounds.addAll(givens);
            } catch (ScenarioException e) {
                throw new ScenarioException("Error in BACKGROUND data");
            }
        }
        return backgrounds;
    }

    protected List<InstanceGiven> extractGivenValues(ScesimModelDescriptor scesimModelDescriptor,
                                                     List<FactMappingValue> factMappingValues,
                                                     ClassLoader classLoader,
                                                     ExpressionEvaluatorFactory expressionEvaluatorFactory) {
        List<InstanceGiven> instanceGiven = new ArrayList<>();

        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier =
                groupByFactIdentifierAndFilter(factMappingValues, FactMappingType.GIVEN);

        boolean hasError = false;

        for (Map.Entry<FactIdentifier, List<FactMappingValue>> entry : groupByFactIdentifier.entrySet()) {

            try {

                FactIdentifier factIdentifier = entry.getKey();

                // for each fact, create a map of path to fields and values to set
                Map<List<String>, Object> paramsForBean = getParamsForBean(scesimModelDescriptor,
                                                                           factIdentifier,
                                                                           entry.getValue(),
                                                                           expressionEvaluatorFactory);

                Object bean = getDirectMapping(paramsForBean)
                        .orElseGet(() -> createObject(factIdentifier.getClassName(), paramsForBean, classLoader));

                instanceGiven.add(new InstanceGiven(factIdentifier, bean));
            } catch (Exception e) {
                hasError = true;
            }
        }

        if (hasError) {
            throw new ScenarioException("Error in GIVEN data");
        }

        return instanceGiven;
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

    protected Map<List<String>, Object> getParamsForBean(ScesimModelDescriptor scesimModelDescriptor,
                                                         FactIdentifier factIdentifier,
                                                         List<FactMappingValue> factMappingValues,
                                                         ExpressionEvaluatorFactory expressionEvaluatorFactory) {
        Map<List<String>, Object> paramsForBean = new HashMap<>();

        boolean hasError = false;

        for (FactMappingValue factMappingValue : factMappingValues) {
            ExpressionIdentifier expressionIdentifier = factMappingValue.getExpressionIdentifier();

            FactMapping factMapping = scesimModelDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                    .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

            List<String> pathToField = factMapping.getExpressionElementsWithoutClass().stream()
                    .map(ExpressionElement::getStep).collect(toList());

            ExpressionEvaluator expressionEvaluator = expressionEvaluatorFactory.getOrCreate(factMappingValue);

            try {
                Object value = expressionEvaluator.evaluateLiteralExpression(factMapping.getClassName(),
                                                                             factMapping.getGenericTypes(),
                                                                             factMappingValue.getRawValue());
                paramsForBean.put(pathToField, value);
            } catch (RuntimeException e) {
                factMappingValue.setExceptionMessage(e.getMessage());
                hasError = true;
            }
        }

        if (hasError) {
            throw new ScenarioException("Error in one or more input values");
        }

        return paramsForBean;
    }

    protected void validateAssertion(List<ScenarioResult> scenarioResults, Scenario scenario) {
        boolean scenarioFailed = false;
        for (ScenarioResult scenarioResult : scenarioResults) {
            if (!scenarioResult.getResult()) {
                scenarioFailed = true;
                break;
            }
        }

        if (scenarioFailed) {
            throw new ScenarioException("Scenario '" + scenario.getDescription() + "' failed");
        }
    }

    protected ScenarioResult fillResult(FactMappingValue expectedResult,
                                        Supplier<ResultWrapper<?>> resultSupplier,
                                        ExpressionEvaluator expressionEvaluator) {
        ResultWrapper<?> resultValue = resultSupplier.get();

        if (resultValue.isSatisfied()) {
            // result is satisfied so clean up previous error state
            expectedResult.resetStatus();
        } else if (resultValue.getErrorMessage().isPresent()) {
            // propagate error message
            expectedResult.setExceptionMessage(resultValue.getErrorMessage().get());
        } else {
            try {
                // set actual as proposed value
                expectedResult.setErrorValue(expressionEvaluator.fromObjectToExpression(resultValue.getResult()));
            } catch (Exception e) {
                // otherwise generic error message
                expectedResult.setExceptionMessage(e.getMessage());
            }
        }

        return new ScenarioResult(expectedResult, resultValue.getResult()).setResult(resultValue.isSatisfied());
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
            return createErrorResultWithErrorMessage(e.getMessage());
        }
    }

    protected abstract ScenarioResultMetadata extractResultMetadata(Map<String, Object> requestContext,
                                                                    ScenarioWithIndex scenarioWithIndex);

    protected abstract Map<String, Object> executeScenario(KieContainer kieContainer,
                                                           ScenarioRunnerData scenarioRunnerData,
                                                           ExpressionEvaluatorFactory expressionEvaluatorFactory,
                                                           ScesimModelDescriptor scesimModelDescriptor,
                                                           Settings settings);

    protected abstract void verifyConditions(ScesimModelDescriptor scesimModelDescriptor,
                                             ScenarioRunnerData scenarioRunnerData,
                                             ExpressionEvaluatorFactory expressionEvaluatorFactory,
                                             Map<String, Object> requestContext);

    protected abstract Object createObject(String className,
                                           Map<List<String>, Object> params,
                                           ClassLoader classLoader);
}
