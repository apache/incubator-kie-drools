/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.Context;
import org.kie.internal.command.CorrelationKeyCommand;
import org.kie.internal.jaxb.CorrelationKeyXmlAdapter;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetProcessInstanceByCorrelationKeyCommand implements ExecutableCommand<ProcessInstance>, CorrelationKeyCommand {

    /** Generated serial version UID */
    private static final long serialVersionUID = -211522165088235065L;
    
    @XmlElement(name = "correlation-key", required = true)
    @XmlJavaTypeAdapter(value = CorrelationKeyXmlAdapter.class)
    private CorrelationKey correlationKey;

    public GetProcessInstanceByCorrelationKeyCommand() {}

    public GetProcessInstanceByCorrelationKeyCommand(CorrelationKey correlationKey) {
        this.correlationKey = correlationKey;
    }

    @Override
    public CorrelationKey getCorrelationKey() {
        return correlationKey;
    }

    @Override
    public void setCorrelationKey(CorrelationKey correlationKey) {
        this.correlationKey = correlationKey;
    }

    public ProcessInstance execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        if (correlationKey == null) {
            return null;
        }
        return ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(correlationKey);
    }

    public String toString() {
        return "session.getProcessInstance(" + correlationKey + ");";
    }

}
