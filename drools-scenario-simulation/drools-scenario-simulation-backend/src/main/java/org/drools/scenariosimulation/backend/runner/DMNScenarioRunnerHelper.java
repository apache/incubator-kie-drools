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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.fluent.DMNScenarioExecutableBuilder;
import org.drools.scenariosimulation.backend.runner.model.InstanceGiven;
import org.drools.scenariosimulation.backend.runner.model.ScenarioExpect;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DecisionNode;

import static org.drools.scenariosimulation.backend.runner.model.ValueWrapper.errorWithMessage;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.FAILED;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED;
import static org.kie.dmn.api.core.DMNMessage.Severity.ERROR;

public class DMNScenarioRunnerHelper extends AbstractRunnerHelper {

    @Override
    protected Map<String, Object> executeScenario(KieContainer kieContainer,
                                                  ScenarioRunnerData scenarioRunnerData,
                                                  ExpressionEvaluatorFactory expressionEvaluatorFactory,
                                                  ScesimModelDescriptor scesimModelDescriptor,
                                                  Settings settings) {
        if (!ScenarioSimulationModel.Type.DMN.equals(settings.getType())) {
            throw new ScenarioException("Impossible to run a not-DMN simulation with DMN runner");
        }
        DMNScenarioExecutableBuilder executableBuilder = createBuilderWrapper(kieContainer);
        executableBuilder.setActiveModel(settings.getDmnFilePath());

        defineInputValues(scenarioRunnerData.getBackgrounds(), scenarioRunnerData.getGivens()).forEach(executableBuilder::setValue);

        return executableBuilder.run().getOutputs();
    }

    /**
     * It returns a {@link Map} which contains the actual data in the DMN Executable Builder (BC) or DMN Context (Kogito)
     * Typically, the Map contains a pair with the <b>Fact Name</b> as a Key and its <b>Object</b> as value
     * (another Map containing the fact properties)
     * (eg.   "Driver": {
     *              "Name": "string"
     *         }
     * )
     * In case of a Imported Fact, i.e. a Decision or a Input node imported from an external DMN file, the Map contains
     * the <b>Fact prefix as a Key</b>, which is the name of the imported DMN document, and another Map as value which
     * contains all the Imported Fact with that prefix.
     * (eg.   "imp" : {
     *              "Violation": {
     *                  "Code": "string"
     *              }
     *        }
     * )
     * If the the same fact is present in both Background and Given list, the Given one will override the background one.
     * @param backgroundData,
     * @param givenData
     * @return
     */
    protected Map<String, Object> defineInputValues(List<InstanceGiven> backgroundData, List<InstanceGiven> givenData) {
        List<InstanceGiven> inputData = new ArrayList<>();
        inputData.addAll(backgroundData);
        inputData.addAll(givenData);

        Map<String, Object> inputValues = new HashMap<>();
        Map<String, Map<String, Object>> importedInputValues = new HashMap<>();

        for (InstanceGiven input : inputData) {
            String factName = input.getFactIdentifier().getName();
            String importPrefix = input.getFactIdentifier().getImportPrefix();
            if (importPrefix != null && !importPrefix.isEmpty()) {
                if (!factName.startsWith(importPrefix)) {
                    throw new IllegalArgumentException("Fact name: " + factName + " has defined an invalid import prefix: " + importPrefix);
                }
                String importedFactName = factName.replaceFirst(Pattern.quote(importPrefix + "."), "");
                Map<String, Object> groupedFacts = importedInputValues.computeIfAbsent(importPrefix, k -> new HashMap<>());
                Object value = groupedFacts.containsKey(importedFactName) ?
                        mergeValues(groupedFacts.get(importedFactName), input.getValue()) :
                        input.getValue();
                importedInputValues.get(importPrefix).put(importedFactName, value);
            } else {
                Object value = inputValues.containsKey(factName) ?
                        mergeValues(inputValues.get(factName), input.getValue()) :
                        input.getValue();
                inputValues.put(factName, value);
            }
        }

        inputValues.putAll(importedInputValues);
        return inputValues;
    }

    /**
     * It manages the merge of two values in case a Fact is defined in both Background and Given input data.
     * In case of DMN scenario, values are Maps. In case of properties present in both values map, the new Value
     * will override the old one.
     * @param oldValue
     * @param newValue
     * @return
     */
    private Map<String, Object> mergeValues(Object oldValue, Object newValue) {
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.putAll((Map<String, Object>) oldValue);
        toReturn.putAll((Map<String, Object>) newValue);

        return toReturn;
    }

    @Override
    protected ScenarioResultMetadata extractResultMetadata(Map<String, Object> requestContext,
                                                           ScenarioWithIndex scenarioWithIndex) {
        DMNModel dmnModel = (DMNModel) requestContext.get(DMNScenarioExecutableBuilder.DMN_MODEL);
        DMNResult dmnResult = (DMNResult) requestContext.get(DMNScenarioExecutableBuilder.DMN_RESULT);

        ScenarioResultMetadata scenarioResultMetadata = new ScenarioResultMetadata(scenarioWithIndex);
        for (DecisionNode decision : dmnModel.getDecisions()) {
            scenarioResultMetadata.addAvailable(decision.getName());
        }
        final AtomicInteger counter = new AtomicInteger(0);
        for (DMNDecisionResult decisionResult : dmnResult.getDecisionResults()) {
            if (SUCCEEDED.equals(decisionResult.getEvaluationStatus())) {
                scenarioResultMetadata.addExecuted(decisionResult.getDecisionName());
            }
            if (decisionResult.getMessages().isEmpty()) {
                scenarioResultMetadata.addAuditMessage(counter.addAndGet(1),
                                                       decisionResult.getDecisionName(),
                                                       decisionResult.getEvaluationStatus().name());
            } else {
                decisionResult.getMessages().forEach(dmnMessage -> scenarioResultMetadata.addAuditMessage(counter.addAndGet(1),
                                                                                                          decisionResult.getDecisionName(),
                                                                                                          decisionResult.getEvaluationStatus().name(),
                                                                                                          dmnMessage.getLevel().name() + ": " + dmnMessage.getText()));
            }
        }
        return scenarioResultMetadata;
    }

    @Override
    protected void verifyConditions(ScesimModelDescriptor scesimModelDescriptor,
                                    ScenarioRunnerData scenarioRunnerData,
                                    ExpressionEvaluatorFactory expressionEvaluatorFactory,
                                    Map<String, Object> requestContext) {
        DMNResult dmnResult = (DMNResult) requestContext.get(DMNScenarioExecutableBuilder.DMN_RESULT);
        List<DMNMessage> dmnMessages = dmnResult.getMessages();

        for (ScenarioExpect output : scenarioRunnerData.getExpects()) {
            FactIdentifier factIdentifier = output.getFactIdentifier();
            String decisionName = factIdentifier.getName();
            DMNDecisionResult decisionResult = dmnResult.getDecisionResultByName(decisionName);
            if (decisionResult == null) {
                throw new ScenarioException("DMN execution has not generated a decision result with name " + decisionName);
            }

            for (FactMappingValue expectedResult : output.getExpectedResult()) {
                ExpressionIdentifier expressionIdentifier = expectedResult.getExpressionIdentifier();

                FactMapping factMapping = scesimModelDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                        .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

                ExpressionEvaluator expressionEvaluator = expressionEvaluatorFactory.getOrCreate(expectedResult);

                ScenarioResult scenarioResult = fillResult(expectedResult,
                                                           () -> getSingleFactValueResult(factMapping,
                                                                                          expectedResult,
                                                                                          decisionResult,
                                                                                          dmnMessages,
                                                                                          expressionEvaluator),
                                                           expressionEvaluator);

                scenarioRunnerData.addResult(scenarioResult);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected ValueWrapper getSingleFactValueResult(FactMapping factMapping,
                                                    FactMappingValue expectedResult,
                                                    DMNDecisionResult decisionResult,
                                                    List<DMNMessage> failureMessages,
                                                    ExpressionEvaluator expressionEvaluator) {
        Object resultRaw = decisionResult.getResult();
        final DMNDecisionResult.DecisionEvaluationStatus evaluationStatus = decisionResult.getEvaluationStatus();
        if (!SUCCEEDED.equals(evaluationStatus)) {
            String failureReason = determineFailureMessage(evaluationStatus, failureMessages);
            return errorWithMessage("The decision \"" +
                                            decisionResult.getDecisionName() +
                                            "\" has not been successfully evaluated: " +
                                            failureReason);
        }

        List<ExpressionElement> elementsWithoutClass = factMapping.getExpressionElementsWithoutClass();

        // DMN engine doesn't generate the whole object when no entry of the decision table match
        if (resultRaw != null) {
            for (ExpressionElement expressionElement : elementsWithoutClass) {
                if (!(resultRaw instanceof Map)) {
                    throw new ScenarioException("Wrong resultRaw structure because it is not a complex type as expected");
                }
                Map<String, Object> result = (Map<String, Object>) resultRaw;
                resultRaw = result.get(expressionElement.getStep());
            }
        }

        Class<?> resultClass = resultRaw != null ? resultRaw.getClass() : null;

        Object expectedResultRaw = expectedResult.getRawValue();
        return getResultWrapper(factMapping.getClassName(),
                                expectedResult,
                                expressionEvaluator,
                                expectedResultRaw,
                                resultRaw,
                                resultClass);
    }

    private String determineFailureMessage(final DMNDecisionResult.DecisionEvaluationStatus evaluationStatus,
                                           final List<DMNMessage> dmnMessages) {
        return FAILED.equals(evaluationStatus) && (dmnMessages != null && !dmnMessages.isEmpty()) ?
                dmnMessages.stream()
                        .filter(dmnMessage -> ERROR.equals(dmnMessage.getSeverity()))
                        .findFirst().map(DMNMessage::getMessage)
                        .orElse(evaluationStatus.toString()) :
                evaluationStatus.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object createObject(ValueWrapper<Object> initialInstance, String className, Map<List<String>, Object> params, ClassLoader classLoader) {
        // simple types
        if (initialInstance.isValid() && !(initialInstance.getValue() instanceof Map)) {
            return initialInstance.getValue();
        }
        Map<String, Object> toReturn = (Map<String, Object>) initialInstance.orElseGet(HashMap::new);
        for (Map.Entry<List<String>, Object> listObjectEntry : params.entrySet()) {

            // direct mapping already considered
            if (listObjectEntry.getKey().isEmpty()) {
                continue;
            }

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

    protected DMNScenarioExecutableBuilder createBuilderWrapper(KieContainer kieContainer) {
        return DMNScenarioExecutableBuilder.createBuilder(kieContainer);
    }
}
