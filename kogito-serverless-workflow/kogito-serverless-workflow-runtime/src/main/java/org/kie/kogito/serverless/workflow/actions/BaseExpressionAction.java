/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.actions;

import org.jbpm.process.instance.impl.Action;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.kie.kogito.serverless.workflow.actions.ActionUtils.getJsonNode;

public abstract class BaseExpressionAction implements Action {

    protected final Expression expr;
    protected final String modelVar;
    protected final String[] addInputVars;

    public BaseExpressionAction(String lang, String expr, String inputVar, String... addVars) {
        this.expr = ExpressionHandlerFactory.get(lang, expr);
        this.modelVar = inputVar;
        this.addInputVars = addVars;
    }

    protected final <T> T evaluate(KogitoProcessContext context, Class<T> resultClass) {
        JsonNode node = getJsonNode(context, modelVar);
        if (node instanceof ObjectNode) {
            for (String addVar : addInputVars) {
                JsonObjectUtils.addToNode(addVar, context.getVariable(addVar), (ObjectNode) node);
            }
        }
        T result = expr.eval(node, resultClass, context);
        if (node instanceof ObjectNode) {
            for (String addVar : addInputVars) {
                context.setVariable(addVar, JsonObjectUtils.toJavaValue(((ObjectNode) node).remove(addVar)));
            }
        }
        return result;
    }

    protected final <T> T assign(KogitoProcessContext context, T value) {
        expr.assign(getJsonNode(context, modelVar), value, context);
        return value;
    }
}
