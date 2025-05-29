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
package org.kie.kogito.process.dynamic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.KogitoProcessContextImpl;
import org.jbpm.util.ContextFactory;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kogito.workitem.rest.RestWorkItemHandler;

public class ProcessInstanceDynamicCallHelper {

    private ProcessInstanceDynamicCallHelper() {
    }

    public static void executeRestCall(RestWorkItemHandler handler, Collection<Process<?>> processes, String processId, String processInstanceId, RestCallInfo input) {
        Process<?> processDef = processes.stream().filter(p -> p.id().equals(processId)).findAny().orElseThrow(() -> new IllegalArgumentException("Cannot find process " + processId));
        AbstractProcessInstance<?> processInstance = findProcessInstance(processDef, processInstanceId);
        processInstance.executeInWorkflowProcessInstanceWrite(pi -> {
            WorkflowProcessImpl process = (WorkflowProcessImpl) pi.getProcess();
            if (!process.isDynamic()) {
                process.setDynamic(true);
            }
            InternalKnowledgeRuntime runtime = pi.getKnowledgeRuntime();
            InternalProcessRuntime.asKogitoProcessRuntime(runtime).getKogitoWorkItemManager().registerWorkItemHandler(RestWorkItemHandler.REST_TASK_TYPE, handler);
            Map<String, Object> parameters = input.getArguments() == null ? new HashMap<>() : new HashMap<>(input.getArguments());
            if (input.getHost() != null) {
                parameters.put(RestWorkItemHandler.HOST, input.getHost());
            }
            if (input.getPort() != null) {
                parameters.put(RestWorkItemHandler.PORT, input.getPort());
            }
            if (input.getMethod() != null) {
                parameters.put(RestWorkItemHandler.METHOD, input.getMethod());
            }
            if (input.getEndpoint() != null) {
                parameters.put(RestWorkItemHandler.URL, input.getEndpoint());
            }
            parameters.put(RestWorkItemHandler.PATH_PARAM_RESOLVER, new DynamicPathParamResolver(processInstanceId));
            WorkItemHandlerResultHolder holder = new WorkItemHandlerResultHolder();
            parameters.put(RestWorkItemHandler.RESULT_HANDLER, holder);
            KogitoProcessContextImpl context = ContextFactory.fromItem(DynamicUtils.addDynamicWorkItem(pi, runtime, RestWorkItemHandler.REST_TASK_TYPE, parameters));

            Map<String, Object> variables = input.getOutputExpression() != null
                    ? ExpressionHandlerFactory.get(input.getOutputExpressionLang(), input.getOutputExpression()).eval(holder.getResult(), Map.class, context)
                    : holder.getResult();
            variables.forEach(pi::setVariable);
            return null;
        });

    }

    private static AbstractProcessInstance<?> findProcessInstance(Process<?> process, String processInstanceId) {
        return process.instances()
                .findById(processInstanceId).map(AbstractProcessInstance.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find process instance " + processInstanceId + " for process " + process.id()));
    }
}
