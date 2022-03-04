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
package org.kie.kogito.serverless.workflow;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonNodeVisitor;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.process.expr.ExpressionWorkItemResolver;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeResolver extends ExpressionWorkItemResolver {

    public JsonNodeResolver(String exprLang, Object expr, String paramName) {
        super(exprLang, expr, paramName);
    }

    @Override
    protected Object evalExpression(Object inputModel, KogitoProcessContext context) {
        return JsonNodeVisitor.transformTextNode(JsonObjectUtils.fromValue(expression), node -> transform(node, inputModel, context));
    }

    private JsonNode transform(JsonNode node, Object inputModel, KogitoProcessContext context) {
        Expression expr = ExpressionHandlerFactory.get(language, node.asText());
        return expr.isValid() ? expr.eval(inputModel, JsonNode.class, context) : node;
    }
}
