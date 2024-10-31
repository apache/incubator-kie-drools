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
package org.kie.kogito.serverless.workflow.executor;

import java.util.Optional;

import org.kie.kogito.serverless.workflow.parser.handlers.ActionResource;
import org.kie.kogito.serverless.workflow.parser.handlers.ActionResourceFactory;
import org.kie.kogito.serverless.workflow.utils.RPCWorkflowUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

public class StaticRPCRegister implements StaticWorkflowRegister {

    @Override
    public void register(StaticWorkflowApplication application, Workflow workflow) {
        if (workflow.getFunctions() != null && workflow.getFunctions().getFunctionDefs() != null) {
            workflow.getFunctions().getFunctionDefs().stream().filter(function -> function.getType() == Type.RPC).forEach(f -> registerHandler(application, f));
        }
    }

    private void registerHandler(StaticWorkflowApplication application, FunctionDefinition function) {
        ActionResource actionResource = ActionResourceFactory.getActionResource(function, Optional.empty());
        application.registerHandler(new StaticRPCWorkItemHandler(RPCWorkflowUtils.getRPCClassName(actionResource.getService())));
    }
}
