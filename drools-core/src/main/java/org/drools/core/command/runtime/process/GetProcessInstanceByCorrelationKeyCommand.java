/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.command.runtime.process;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.command.Context;
import org.kie.process.CorrelationAwareProcessRuntime;
import org.kie.process.CorrelationKey;
import org.kie.runtime.KieSession;
import org.kie.runtime.process.ProcessInstance;

public class GetProcessInstanceByCorrelationKeyCommand implements GenericCommand<ProcessInstance> {

    private CorrelationKey correlationKey;

    public GetProcessInstanceByCorrelationKeyCommand() {}

    public GetProcessInstanceByCorrelationKeyCommand(CorrelationKey correlationKey) {
        this.correlationKey = correlationKey;
    }

    public CorrelationKey getCorrelationKey() {
        return correlationKey;
    }

    public void setCorrelationKey(CorrelationKey correlationKey) {
        this.correlationKey = correlationKey;
    }

    public ProcessInstance execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        if (correlationKey == null) {
            return null;
        }
        return ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(correlationKey);
    }

    public String toString() {
        return "session.getProcessInstance(" + correlationKey + ");";
    }

}
