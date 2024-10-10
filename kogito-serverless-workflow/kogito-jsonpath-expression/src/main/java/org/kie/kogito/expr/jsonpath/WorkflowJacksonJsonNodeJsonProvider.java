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
package org.kie.kogito.expr.jsonpath;

import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.FunctionBaseJsonNode;
import org.kie.kogito.jackson.utils.PrefixJsonNode;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;
import org.kie.kogito.serverless.workflow.utils.VariablesHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;

public class WorkflowJacksonJsonNodeJsonProvider extends JacksonJsonNodeJsonProvider {

    private KogitoProcessContext context;
    private Map<String, JsonNode> variables;

    public WorkflowJacksonJsonNodeJsonProvider(KogitoProcessContext context) {
        this.context = context;
        this.variables = VariablesHelper.getAdditionalVariables(context);
    }

    @Override
    public Object getMapValue(Object obj, String key) {
        if (obj instanceof Function) {
            return ((Function<String, Object>) obj).apply(key);
        } else if (obj instanceof FunctionBaseJsonNode) {
            return ((FunctionBaseJsonNode) obj).get(key);
        } else {
            switch (key) {
                case "$" + ExpressionHandlerUtils.SECRET_MAGIC:
                    return new PrefixJsonNode<>(ExpressionHandlerUtils::getOptionalSecret);
                case "$" + ExpressionHandlerUtils.CONTEXT_MAGIC:
                    return ExpressionHandlerUtils.getContextFunction(context);
                case "$" + ExpressionHandlerUtils.CONST_MAGIC:
                    return ExpressionHandlerUtils.getConstants(context);
                default:
                    return variables.containsKey(key) ? variables.get(key) : super.getMapValue(obj, key);
            }
        }
    }

    @Override
    public boolean isMap(Object obj) {
        return super.isMap(obj) || obj instanceof Function || obj instanceof FunctionBaseJsonNode;
    }
}
