/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.impl.actions;

import java.io.Serializable;

import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.event.EventTransformerImpl;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.instance.NodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ABORTED;

public class HandleEscalationAction implements Action, Serializable {

    private static final long serialVersionUID = 1L;

    private String faultName;
    private String variableName;

    public HandleEscalationAction(String faultName, String variableName) {
        this.faultName = faultName;
        this.variableName = variableName;
    }

    @Override
    public void execute(KogitoProcessContext context) throws Exception {
        ExceptionScopeInstance scopeInstance = (ExceptionScopeInstance) ((NodeInstance) context.getNodeInstance()).resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE,
                faultName);
        if (scopeInstance != null) {

            Object tVariable = variableName == null ? null : context.getVariable(variableName);
            org.jbpm.workflow.core.node.Transformation transformation = (org.jbpm.workflow.core.node.Transformation) context.getNodeInstance().getNode().getMetaData().get("Transformation");
            if (transformation != null) {
                tVariable = new EventTransformerImpl(transformation).transformEvent(((KogitoProcessInstance) context.getProcessInstance()).getVariables());
            }
            scopeInstance.handleException(faultName, tVariable);
        } else {

            ((ProcessInstance) context.getProcessInstance()).setState(STATE_ABORTED);
        }
    }

}
