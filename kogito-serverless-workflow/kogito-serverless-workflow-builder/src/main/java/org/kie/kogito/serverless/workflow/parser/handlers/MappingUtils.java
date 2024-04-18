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
package org.kie.kogito.serverless.workflow.parser.handlers;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jbpm.ruleflow.core.factory.MappableNodeFactory;
import org.kie.kogito.jackson.utils.JsonNodeVisitor;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;

public class MappingUtils {

    public static <T extends MappableNodeFactory<?>> T addMapping(T nodeFactory, String inputVar, String outputVar) {
        return (T) nodeFactory.inMapping(inputVar, SWFConstants.MODEL_WORKFLOW_VAR)
                .outMapping(SWFConstants.RESULT, outputVar);
    }

    public static final void processArgs(Workflow workflow,
            JsonNode functionArgs, MappingSetter setter) {
        if (functionArgs.isObject()) {
            functionsToMap(workflow, functionArgs).forEach((key, value) -> setter.accept(key, value));
        } else {
            Object object = functionReference(workflow, JsonObjectUtils.simpleToJavaValue(functionArgs));
            setter.accept(object);
        }
    }

    private static Map<String, Object> functionsToMap(Workflow workflow, JsonNode jsonNode) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (jsonNode != null) {
            Iterator<Entry<String, JsonNode>> iter = jsonNode.fields();
            while (iter.hasNext()) {
                Entry<String, JsonNode> entry = iter.next();
                map.put(entry.getKey(), functionReference(workflow, JsonObjectUtils.simpleToJavaValue(entry.getValue())));
            }
        }
        return map;
    }

    private static Object functionReference(Workflow workflow, Object object) {
        if (object instanceof JsonNode) {
            return JsonNodeVisitor.transformTextNode((JsonNode) object, node -> JsonObjectUtils.fromValue(ExpressionHandlerUtils.replaceExpr(workflow, node.asText())));
        } else if (object instanceof CharSequence) {
            return ExpressionHandlerUtils.replaceExpr(workflow, object.toString());
        } else {
            return object;
        }
    }
}
