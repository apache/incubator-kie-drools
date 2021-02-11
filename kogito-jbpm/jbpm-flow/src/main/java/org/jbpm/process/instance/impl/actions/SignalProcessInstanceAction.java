/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.impl.actions;

import java.io.Serializable;
import java.util.function.Function;

import org.jbpm.process.instance.impl.Action;
import org.jbpm.process.instance.impl.util.VariableUtil;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.process.workitems.KogitoWorkItemManager;
import org.kie.kogito.process.workitems.impl.KogitoWorkItemImpl;

public class SignalProcessInstanceAction implements Action, Serializable {

    public static final String DEFAULT_SCOPE = "default";
    public static final String PROCESS_INSTANCE_SCOPE = "processInstance";
    public static final String EXTERNAL_SCOPE = "external";
    
    public static final String UNSET_SCOPE = System.getProperty("org.jbpm.signals.defaultscope", PROCESS_INSTANCE_SCOPE);

    private static final long serialVersionUID = 1L;

    private final String signalNameTemplate;
    private String variableName;
    private Function<KogitoProcessContext, Object> eventDataSupplier = (kcontext) -> null;

    private String scope = UNSET_SCOPE;
    private Transformation transformation;

    public SignalProcessInstanceAction(String signalName, String variableName) {
        this.signalNameTemplate = signalName;
        this.variableName = variableName;
    }

    public SignalProcessInstanceAction(String signalName, String variableName, Transformation transformation) {
        this.signalNameTemplate = signalName;
        this.variableName = variableName;
        this.transformation = transformation;
    }

    public SignalProcessInstanceAction(String signalName, String variableName, String scope) {
        this.signalNameTemplate = signalName;
        this.variableName = variableName;
        if (scope != null) {
            this.scope = scope;
        }
    }

    public SignalProcessInstanceAction(String signalName, String variableName, String scope, Transformation transformation) {
        this.signalNameTemplate = signalName;
        this.variableName = variableName;
        if (scope != null) {
            this.scope = scope;
        }
        this.transformation = transformation;
    }

    public SignalProcessInstanceAction(String signalName, Function<KogitoProcessContext, Object> eventDataSupplier, String scope) {
        this.signalNameTemplate = signalName;
        this.eventDataSupplier = eventDataSupplier;
        if (scope != null) {
            this.scope = scope;
        }
    }

    @Override
    public void execute( KogitoProcessContext context) throws Exception {
        String variableName = VariableUtil.resolveVariable(this.variableName, context.getNodeInstance());
        Object signal = variableName == null ? eventDataSupplier.apply(context) : context.getVariable(variableName);
        if (transformation != null) {
            signal = new org.jbpm.process.core.event.EventTransformerImpl(transformation).transformEvent((( KogitoProcessInstance ) context.getProcessInstance()).getVariables());
        }
        if (signal == null) {
            signal = variableName;
        }
        String signalName = VariableUtil.resolveVariable(this.signalNameTemplate, context.getNodeInstance());
        KogitoProcessRuntime.asKogitoProcessRuntime(context.getKieRuntime())
                .getProcessEventSupport().fireOnSignal(context.getProcessInstance(), context
                        .getNodeInstance(), context.getKieRuntime(), signalName, signal);
        
        if (DEFAULT_SCOPE.equals(scope)) {
            context.getKieRuntime().signalEvent(signalName, signal);
        } else if (PROCESS_INSTANCE_SCOPE.equals(scope)) {
            context.getProcessInstance().signalEvent(signalName, signal);
        } else if (EXTERNAL_SCOPE.equals(scope)) {
            KogitoWorkItemImpl workItem = new KogitoWorkItemImpl();
            workItem.setName("External Send Task");
            workItem.setNodeInstanceId( (( KogitoNodeInstance ) context.getNodeInstance()).getStringId());
            workItem.setProcessInstanceId( (( KogitoProcessInstance ) context.getProcessInstance()).getStringId());
            workItem.setNodeId(context.getNodeInstance().getNodeId());
            workItem.setParameter("Signal", signalName);
            workItem.setParameter("SignalProcessInstanceId", context.getVariable("SignalProcessInstanceId"));
            workItem.setParameter("SignalWorkItemId", context.getVariable("SignalWorkItemId"));
            workItem.setParameter("SignalDeploymentId", context.getVariable("SignalDeploymentId"));
            if (signal == null) {
                workItem.setParameter("Data", signal);
            }
            (( KogitoWorkItemManager ) context.getKieRuntime().getWorkItemManager()).internalExecuteWorkItem(workItem);
        }
    }

}
