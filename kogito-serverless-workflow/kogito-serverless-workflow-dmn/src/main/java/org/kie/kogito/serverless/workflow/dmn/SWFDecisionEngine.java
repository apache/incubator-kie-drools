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
package org.kie.kogito.serverless.workflow.dmn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jbpm.util.ContextFactory;
import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.jbpm.workflow.instance.rule.DecisionRuleTypeEngine;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.dmn.rest.DMNJSONUtils;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;

import static org.kie.kogito.serverless.workflow.SWFConstants.CONTENT_DATA;

public class SWFDecisionEngine implements DecisionRuleTypeEngine {

    public static final String EXPR_LANG = "lang";

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

        //Input Binding
        DMNContext context = DMNJSONUtils.ctx(modelInstance, getInputParameters(rsni));
        DMNResult dmnResult = modelInstance.evaluateAll(context);
        if (dmnResult.hasErrors()) {
            String errors = dmnResult.getMessages(DMNMessage.Severity.ERROR).stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            throw new RuntimeException("DMN result errors:: " + errors);
        }
        //Output Binding
        Map<String, Object> outputSet = Map.of(SWFConstants.RESULT, dmnResult.getContext().getAll());
        NodeIoHelper.processOutputs(rsni, outputSet::get, rsni::getVariable);

        rsni.triggerCompleted();
    }

    private Map<String, Object> getInputParameters(RuleSetNodeInstance rsni) {
        RuleSetNode node = rsni.getRuleSetNode();
        Map<String, Object> inputParameters = node.getParameters();
        int size = inputParameters.size();
        if (size == 0) {
            inputParameters = JsonObjectUtils.convertValue(getInputs(rsni).get(SWFConstants.MODEL_WORKFLOW_VAR), Map.class);
        } else if (size == 1 && inputParameters.containsKey(CONTENT_DATA)) {
            return eval(ContextFactory.fromNode(rsni), ExpressionHandlerFactory.get((String) node.getMetaData().get(EXPR_LANG), (String) inputParameters.get(CONTENT_DATA)));
        } else {
            inputParameters = getInputParameters(ContextFactory.fromNode(rsni), (String) node.getMetaData().get(EXPR_LANG), new HashMap<>(inputParameters));
        }
        return inputParameters;

    }

    private Map<String, Object> getInputParameters(KogitoProcessContext context, String exprLang, Map<String, Object> inputParameters) {
        for (Map.Entry<String, Object> entry : inputParameters.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                entry.setValue(getInputParameters(context, exprLang, (Map<String, Object>) value));
            } else if (value instanceof CharSequence) {
                Expression expr = ExpressionHandlerFactory.get(exprLang, value.toString());
                if (expr.isValid()) {
                    entry.setValue(eval(context, expr));
                }
            }
        }
        return inputParameters;
    }

    private Map<String, Object> eval(KogitoProcessContext context, Expression expr) {
        return expr.eval(JsonObjectUtils.fromValue(context.getVariable(SWFConstants.DEFAULT_WORKFLOW_VAR)), Map.class, context);
    }

}
