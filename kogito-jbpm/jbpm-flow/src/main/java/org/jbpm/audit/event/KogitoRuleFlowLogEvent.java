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

import org.drools.kiesession.audit.RuleFlowLogEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

/**
 * A ruleflow event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the working memory.
 * It contains the process name and id.
 */
public class KogitoRuleFlowLogEvent extends RuleFlowLogEvent {

    public KogitoRuleFlowLogEvent(final int type,
            ProcessInstance processInstance) {
        super(type, processInstance.getProcessId(), processInstance.getProcessName(), ((KogitoProcessInstance) processInstance).getStringId());
    }
}
