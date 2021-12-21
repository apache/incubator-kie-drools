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
import java.util.Collections;
import java.util.Map;

import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;

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
            Object event = variableName == null ? null : context.getVariable(variableName);
            NodeInstanceImpl impl = ((NodeInstanceImpl) context.getNodeInstance());
            // for event nodes we create a "virtual assignment and we process it"
            Map<String, Object> outputSet = Collections.singletonMap(variableName, event);
            NodeIoHelper.processOutputs(impl, varRef -> outputSet.get(varRef), target -> impl.getVariable(target));
            context.getContextData().put("Exception", context.getVariable(variableName));
            scopeInstance.handleException(faultName, context);
        } else {

            ((ProcessInstance) context.getProcessInstance()).setState(STATE_ABORTED);
        }
    }

}
