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
package org.kie.kogito.serverless.workflow.workitemparams;

import org.jbpm.util.ContextFactory;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.jackson.utils.JsonNodeVisitor;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.workitems.impl.WorkItemParamResolver;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class ExpressionWorkItemResolver<T> implements WorkItemParamResolver<T> {

    protected final String language;
    protected final Object expression;
    private final String paramName;

    protected ExpressionWorkItemResolver(String language, Object expression, String paramName) {
        this.language = language;
        this.expression = expression;
        this.paramName = paramName;
    }

    protected final JsonNode evalExpression(KogitoWorkItem workItem) {
        return JsonNodeVisitor.transformTextNode(JsonObjectUtils.fromValue(expression),
                node -> ExpressionHandlerUtils.transform(node, workItem.getParameter(paramName), ContextFactory.fromItem(workItem), language));
    }

}
