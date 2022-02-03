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
package org.jbpm.audit.event;

import org.drools.kiesession.audit.RuleFlowVariableLogEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

public class KogitoRuleFlowVariableLogEvent extends RuleFlowVariableLogEvent {

    public KogitoRuleFlowVariableLogEvent(final int type,
            final String variableId,
            final String variableInstanceId,
            final ProcessInstance processInstance,
            final String objectToString) {
        super(type, variableId, variableInstanceId,
                processInstance.getProcessId(), processInstance.getProcessName(), ((KogitoProcessInstance) processInstance).getStringId(), objectToString);
    }
}
