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
package org.kie.kogito.workflows.services;

import java.util.Map;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RPCCustomWorkItemHandler extends WorkflowWorkItemHandler {

    public static final String NAME = "RPCCustomWorkItemHandler";
    public static final String OPERATION = "operation";

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters) {
        String operationId = (String) workItem.getNodeInstance().getNode().getMetaData().get(OPERATION);
        if (!"division".equals(operationId)) {
            throw new IllegalArgumentException("Operation " + operationId + " is not supported");
        }
        return (Integer) parameters.get("dividend") / (Integer) parameters.get("divisor");
    }

    @Override
    public String getName() {
        return NAME;
    }
}
