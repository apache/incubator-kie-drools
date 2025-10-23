/*
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
package org.jbpm.bpmn2.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jbpm.process.core.transformation.JsonResolver;
import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.jbpm.workflow.instance.rule.DecisionRuleTypeEngine;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DmnDecisionModel;

public class DecisionRuleTypeEngineImpl implements DecisionRuleTypeEngine {

    private final JsonResolver jsonResolver = new JsonResolver();

    @Override
    public void evaluate(RuleSetNodeInstance rsni, String inputNamespace, String inputModel, String decision) {
        String namespace = rsni.resolveExpression(inputNamespace);
        String model = rsni.resolveExpression(inputModel);

        DecisionModel modelInstance =
                Optional.ofNullable(rsni.getRuleSetNode().getDecisionModel())
                        .orElse(() -> new DmnDecisionModel(
                                ((KieSession) getKieRuntime(rsni)).getKieRuntime(DMNRuntime.class),
                                namespace,
                                model))
                        .get();
        // Input binding
        Map<String, Object> variables = getDMNAnnotatedAdjustedMap(rsni);
        DMNContext context = modelInstance.newContext(variables);
        DMNResult dmnResult = modelInstance.evaluateAll(context);
        if (dmnResult.hasErrors()) {
            String errors = dmnResult.getMessages(DMNMessage.Severity.ERROR).stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            throw new RuntimeException("DMN result errors:: " + errors);
        }
        //Output Binding
        Map<String, Object> outputSet = dmnResult.getContext().getAll();
        NodeIoHelper.processOutputs(rsni, outputSet::get, rsni::getVariable);

        rsni.triggerCompleted();
    }

    Map<String, Object> getDMNAnnotatedAdjustedMap(RuleSetNodeInstance rsni) {
        // Get inputs
        Map<String, Object> inputs = getInputs(rsni);
        // resolve inputs with the JsonResolver' objectMapper
        Map<String, Object> jsonResolvedInputs = jsonResolver.resolveAll(inputs);
        return getDMNAnnotatedAdjustedMap(inputs, jsonResolvedInputs);
    }

    Map<String, Object> getDMNAnnotatedAdjustedMap(Map<String, Object> rsniInputs, Map<String, Object> jsonResolvedInputs) {
        Map<String, Object> toReturn = new HashMap<>(jsonResolvedInputs);
        // Retrieving DMN-annotated inputs
        Map<String, Object> dmnAnnotatedBeans = rsniInputs.entrySet()
                .stream()
                .filter(entry -> isDMNAnnotatedBean(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // replacing/adding DMN-annotated beans inside returned Map
        toReturn.putAll(dmnAnnotatedBeans);
        return toReturn;
    }

    boolean isDMNAnnotatedBean(Object bean) {
        return bean != null && isDMNAnnotatedClass(bean.getClass());
    }

    boolean isDMNAnnotatedClass(Class<?> clazz) {
        return !JavaBackedType.of(clazz).equals(BuiltInType.UNKNOWN);
    }

}
