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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.utils.ConstantsHolder;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.fluent.CoverageAgendaListener;
import org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder;
import org.drools.scenariosimulation.backend.runner.model.ScenarioExpect;
import org.drools.scenariosimulation.backend.runner.model.InstanceGiven;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.drools.scenariosimulation.backend.util.ScenarioBeanUtil;
import org.drools.scenariosimulation.backend.util.ScenarioBeanWrapper;
import org.kie.api.runtime.KieContainer;

import static java.util.stream.Collectors.toList;
import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.COVERAGE_LISTENER;
import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.RULES_AVAILABLE;
import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.createBuilder;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.fillBean;

public class RuleScenarioRunnerHelper extends AbstractRunnerHelper {

    @Override
    protected Map<String, Object> executeScenario(KieContainer kieContainer,
                                                  ScenarioRunnerData scenarioRunnerData,
                                                  ExpressionEvaluatorFactory expressionEvaluatorFactory,
                                                  ScesimModelDescriptor scesimModelDescriptor,
                                                  Settings settings) {
        if (!Type.RULE.equals(settings.getType())) {
            throw new ScenarioException("Impossible to run a not-RULE simulation with RULE runner");
        }
        RuleScenarioExecutableBuilder ruleScenarioExecutableBuilder = createBuilderWrapper(kieContainer, settings);

        if (settings.getRuleFlowGroup() != null) {
            ruleScenarioExecutableBuilder.setActiveRuleFlowGroup(settings.getRuleFlowGroup());
        }

        loadInputData(scenarioRunnerData.getBackgrounds(), ruleScenarioExecutableBuilder);
        loadInputData(scenarioRunnerData.getGivens(), ruleScenarioExecutableBuilder);
        // all new facts should be verified internally to the working memory
        scenarioRunnerData.getExpects().stream()
                .filter(ScenarioExpect::isNewFact)
                .flatMap(output -> output.getExpectedResult().stream()
                        .map(ScenarioResult::new))
                .forEach(scenarioResult -> {
                    Class<?> clazz = ScenarioBeanUtil.loadClass(scenarioResult.getFactIdentifier().getClassName(), kieContainer.getClassLoader());
                    ExpressionEvaluator expressionEvaluator = expressionEvaluatorFactory.getOrCreate(scenarioResult.getFactMappingValue());
                    scenarioRunnerData.addResult(scenarioResult);
                    ruleScenarioExecutableBuilder.addInternalCondition(clazz,
                                                                       createExtractorFunction(expressionEvaluator, scenarioResult.getFactMappingValue(), scesimModelDescriptor),
                                                                       scenarioResult);
                });

        return ruleScenarioExecutableBuilder.run();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ScenarioResultMetadata extractResultMetadata(Map<String, Object> requestContext, ScenarioWithIndex scenarioWithIndex) {
        CoverageAgendaListener coverageAgendaListener = (CoverageAgendaListener) requestContext.get(COVERAGE_LISTENER);
        Map<String, Integer> ruleExecuted = coverageAgendaListener.getRuleExecuted();
        Set<String> availableRules = (Set<String>) requestContext.get(RULES_AVAILABLE);
        ScenarioResultMetadata scenarioResultMetadata = new ScenarioResultMetadata(scenarioWithIndex);
        scenarioResultMetadata.addAllAvailable(availableRules);
        scenarioResultMetadata.addAllExecuted(ruleExecuted);
        final AtomicInteger counter = new AtomicInteger(0);
        coverageAgendaListener.getAuditsMessages().forEach(auditMessage ->
                scenarioResultMetadata.addAuditMessage(counter.addAndGet(1), auditMessage, ConstantsHolder.EXECUTED));
        return scenarioResultMetadata;
    }

    @Override
    protected void verifyConditions(ScesimModelDescriptor scesimModelDescriptor,
                                    ScenarioRunnerData scenarioRunnerData,
                                    ExpressionEvaluatorFactory expressionEvaluatorFactory,
                                    Map<String, Object> requestContext) {

        for (InstanceGiven input : scenarioRunnerData.getGivens()) {
            FactIdentifier factIdentifier = input.getFactIdentifier();
            List<ScenarioExpect> assertionOnFact = scenarioRunnerData.getExpects().stream()
                    .filter(elem -> !elem.isNewFact())
                    .filter(elem -> Objects.equals(elem.getFactIdentifier(), factIdentifier)).collect(toList());

            // check if this fact has something to check
            if (assertionOnFact.isEmpty()) {
                continue;
            }

            getScenarioResultsFromGivenFacts(scesimModelDescriptor, assertionOnFact, input, expressionEvaluatorFactory).forEach(scenarioRunnerData::addResult);
        }
    }

    protected List<ScenarioResult> getScenarioResultsFromGivenFacts(ScesimModelDescriptor scesimModelDescriptor,
                                                                    List<ScenarioExpect> scenarioOutputsPerFact,
                                                                    InstanceGiven input,
                                                                    ExpressionEvaluatorFactory expressionEvaluatorFactory) {
        Object factInstance = input.getValue();
        List<ScenarioResult> scenarioResults = new ArrayList<>();
        for (ScenarioExpect scenarioExpect : scenarioOutputsPerFact) {
            if (scenarioExpect.isNewFact()) {
                continue;
            }

            for (FactMappingValue expectedResult : scenarioExpect.getExpectedResult()) {

                ExpressionEvaluator expressionEvaluator = expressionEvaluatorFactory.getOrCreate(expectedResult);

                ScenarioResult scenarioResult = fillResult(expectedResult,
                                                           () -> createExtractorFunction(expressionEvaluator, expectedResult, scesimModelDescriptor)
                                                                   .apply(factInstance),
                                                           expressionEvaluator);

                scenarioResults.add(scenarioResult);
            }
        }
        return scenarioResults;
    }

    protected Function<Object, ValueWrapper> createExtractorFunction(ExpressionEvaluator expressionEvaluator,
                                                                     FactMappingValue expectedResult,
                                                                     ScesimModelDescriptor scesimModelDescriptor) {
        return objectToCheck -> {

            ExpressionIdentifier expressionIdentifier = expectedResult.getExpressionIdentifier();

            FactMapping factMapping = scesimModelDescriptor.getFactMapping(expectedResult.getFactIdentifier(), expressionIdentifier)
                    .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

            List<String> pathToValue = factMapping.getExpressionElementsWithoutClass().stream().map(ExpressionElement::getStep).collect(toList());
            ScenarioBeanWrapper<?> scenarioBeanWrapper = ScenarioBeanUtil.navigateToObject(objectToCheck, pathToValue, false);
            Object resultValue = scenarioBeanWrapper.getBean();
            Object expectedResultValue = expectedResult.getRawValue();

            return getResultWrapper(factMapping.getClassName(),
                                    expectedResult,
                                    expressionEvaluator,
                                    expectedResultValue,
                                    resultValue,
                                    scenarioBeanWrapper.getBeanClass());
        };
    }

    protected void loadInputData(List<InstanceGiven> dataToLoad, RuleScenarioExecutableBuilder executableBuilder) {
        for (InstanceGiven instanceGiven : dataToLoad) {
            executableBuilder.insert(instanceGiven.getValue());
        }
    }

    @Override
    protected Object createObject(ValueWrapper<Object> initialInstance, String className, Map<List<String>, Object> params, ClassLoader classLoader) {
        return fillBean(initialInstance, className, params, classLoader);
    }

    protected RuleScenarioExecutableBuilder createBuilderWrapper(KieContainer kieContainer, Settings settings) {
        return createBuilder(kieContainer,
                             settings.getDmoSession(),
                             settings.isStateless());
    }
}
