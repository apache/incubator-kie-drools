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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.fluent.DMNScenarioExecutableBuilder;
import org.drools.scenariosimulation.backend.runner.model.ResultWrapper;
import org.drools.scenariosimulation.backend.runner.model.ScenarioExpect;
import org.drools.scenariosimulation.backend.runner.model.ScenarioGiven;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DecisionNode;

import static org.drools.scenariosimulation.backend.runner.model.ResultWrapper.createErrorResult;
import static org.drools.scenariosimulation.backend.runner.model.ResultWrapper.createResult;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED;

public class DMNScenarioRunnerHelper extends AbstractRunnerHelper {

    @Override
    public RequestContext executeScenario(KieContainer kieContainer,
                                          ScenarioRunnerData scenarioRunnerData,
                                          ExpressionEvaluator expressionEvaluator,
                                          SimulationDescriptor simulationDescriptor) {
        if (!ScenarioSimulationModel.Type.DMN.equals(simulationDescriptor.getType())) {
            throw new ScenarioException("Impossible to run a not-DMN simulation with DMN runner");
        }
        DMNScenarioExecutableBuilder executableBuilder = DMNScenarioExecutableBuilder.createBuilder(kieContainer);
        executableBuilder.setActiveModel(simulationDescriptor.getDmnFilePath());
        for (ScenarioGiven input : scenarioRunnerData.getGivens()) {
            executableBuilder.setValue(input.getFactIdentifier().getName(), input.getValue());
        }

        return executableBuilder.run();
    }

    @Override
    protected ScenarioResultMetadata extractResultMetadata(RequestContext requestContext, ScenarioWithIndex scenarioWithIndex) {
        DMNModel dmnModel = requestContext.getOutput(DMNScenarioExecutableBuilder.DMN_MODEL);
        DMNResult dmnResult = requestContext.getOutput(DMNScenarioExecutableBuilder.DMN_RESULT);

        ScenarioResultMetadata scenarioResultMetadata = new ScenarioResultMetadata(scenarioWithIndex);

        for (DecisionNode decision : dmnModel.getDecisions()) {
            scenarioResultMetadata.addAvailable(decision.getName());
        }

        for (DMNDecisionResult decisionResult : dmnResult.getDecisionResults()) {
            if (SUCCEEDED.equals(decisionResult.getEvaluationStatus())) {
                scenarioResultMetadata.addExecuted(decisionResult.getDecisionName());
            }
        }

        return scenarioResultMetadata;
    }

    @Override
    public void verifyConditions(SimulationDescriptor simulationDescriptor,
                                 ScenarioRunnerData scenarioRunnerData,
                                 ExpressionEvaluator expressionEvaluator,
                                 RequestContext requestContext) {
        DMNResult dmnResult = requestContext.getOutput(DMNScenarioExecutableBuilder.DMN_RESULT);

        for (ScenarioExpect output : scenarioRunnerData.getExpects()) {
            FactIdentifier factIdentifier = output.getFactIdentifier();
            String decisionName = factIdentifier.getName();
            DMNDecisionResult decisionResult = dmnResult.getDecisionResultByName(decisionName);
            if (decisionResult == null) {
                throw new ScenarioException("DMN execution has not generated a decision result with name " + decisionName);
            }

            for (FactMappingValue expectedResult : output.getExpectedResult()) {
                ExpressionIdentifier expressionIdentifier = expectedResult.getExpressionIdentifier();

                FactMapping factMapping = simulationDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                        .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

                ScenarioResult scenarioResult = fillResult(expectedResult, factIdentifier, () -> getSingleFactValueResult(factMapping, expectedResult, decisionResult, expressionEvaluator));

                scenarioRunnerData.addResult(scenarioResult);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected ResultWrapper getSingleFactValueResult(FactMapping factMapping,
                                                     FactMappingValue expectedResult,
                                                     DMNDecisionResult decisionResult,
                                                     ExpressionEvaluator expressionEvaluator) {
        Object resultRaw = decisionResult.getResult();
        final DMNDecisionResult.DecisionEvaluationStatus evaluationStatus = decisionResult.getEvaluationStatus();
        if (!SUCCEEDED.equals(evaluationStatus)) {
            return createErrorResult(SUCCEEDED, evaluationStatus);
        }

        for (ExpressionElement expressionElement : factMapping.getExpressionElementsWithoutClass()) {
            if (!(resultRaw instanceof Map)) {
                throw new ScenarioException("Wrong resultRaw structure because it is not a complex type as expected");
            }
            Map<String, Object> result = (Map<String, Object>) resultRaw;
            resultRaw = result.get(expressionElement.getStep());
        }

        Class<?> resultClass = resultRaw != null ? resultRaw.getClass() : null;

        Object expectedResultRaw = expectedResult.getRawValue();
        try {
            return expressionEvaluator.evaluateUnaryExpression(expectedResultRaw, resultRaw, resultClass) ?
                    createResult(resultRaw) :
                    createErrorResult(resultRaw, expectedResultRaw);
        } catch (Exception e) {
            expectedResult.setExceptionMessage(e.getMessage());
            throw new ScenarioException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object createObject(String className, Map<List<String>, Object> params, ClassLoader classLoader) {
        Map<String, Object> toReturn = new HashMap<>();
        for (Map.Entry<List<String>, Object> listObjectEntry : params.entrySet()) {

            List<String> allSteps = listObjectEntry.getKey();
            List<String> steps = allSteps.subList(0, allSteps.size() - 1);
            String lastStep = allSteps.get(allSteps.size() - 1);

            Map<String, Object> targetMap = toReturn;
            for (String step : steps) {
                targetMap = (Map<String, Object>) targetMap.computeIfAbsent(step, k -> new HashMap<>());
            }
            targetMap.put(lastStep, listObjectEntry.getValue());
        }
        return toReturn;
    }
}
