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
package org.kie.kogito.serverless.workflow.executor;

import org.kie.kogito.serverless.workflow.functions.FunctionDefinitionEx;

import io.serverlessworkflow.api.Workflow;

public class StaticJavaRegister implements StaticWorkflowRegister {

    @Override
    public void register(StaticWorkflowApplication application, Workflow workflow) {
        if (workflow.getFunctions() != null && workflow.getFunctions().getFunctionDefs() != null) {
            workflow.getFunctions().getFunctionDefs().stream().filter(FunctionDefinitionEx.class::isInstance).map(FunctionDefinitionEx.class::cast)
                    .forEach(function -> application.registerHandler(new StaticFunctionWorkItemHandler<>(function.getName(), function.getFunction())));
        }
    }
}
