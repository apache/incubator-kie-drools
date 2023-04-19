/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.utils;

import org.drools.util.StringUtils;

import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.getValidIdentifier;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.removeExt;

public class OpenAPIWorkflowUtils {
    public static String getOpenApiWorkItemName(String fileName, String methodName) {
        return removeExt(fileName) + '_' + methodName;
    }

    public static String getOpenApiClassName(String fileName, String methodName) {
        return StringUtils.ucFirst(getValidIdentifier(removeExt(fileName.toLowerCase())) + '_' + methodName);
    }

    public static boolean isOpenApiOperation(FunctionDefinition function) {
        return function.getType() == Type.REST && function.getOperation() != null && function.getOperation().contains(ServerlessWorkflowUtils.OPERATION_SEPARATOR);
    }

    private OpenAPIWorkflowUtils() {
    }
}
