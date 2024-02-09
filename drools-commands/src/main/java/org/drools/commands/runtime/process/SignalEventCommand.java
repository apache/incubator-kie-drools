/**
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
package org.drools.commands.runtime.process;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.jaxb.JaxbUnknownAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.jaxb.CorrelationKeyXmlAdapter;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SignalEventCommand implements ExecutableCommand<Void>, ProcessInstanceIdCommand {

    /** Generated serial version UID */
    private static final long serialVersionUID = 2134028686669740220L;

    @XmlAttribute(name="process-instance-id")
    private String processInstanceId = "-1";

    @XmlElement(name = "correlation-key", required = false)
    @XmlJavaTypeAdapter(value = CorrelationKeyXmlAdapter.class)
    private CorrelationKey correlationKey;
    
    @XmlAttribute(name="event-type", required=true)
    private String eventType;

    @XmlElement(name="event")
    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    private Object event;

    public SignalEventCommand() {
    }

    public SignalEventCommand(String eventType,
                              Object event) {
        this.eventType = eventType;
        this.event = event;
    }

    public SignalEventCommand(String processInstanceId,
                              String eventType,
                              Object event) {
        this.processInstanceId = processInstanceId;
        this.eventType = eventType;
        this.event = event;
    }


    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public CorrelationKey getCorrelationKey() {
        return correlationKey;
    }

    public void setCorrelationKey( CorrelationKey correlationKey ) {
        this.correlationKey = correlationKey;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        
        if ("-1".equals(processInstanceId) && correlationKey == null) {
            ksession.signalEvent(eventType, event);
        } else {
            ProcessInstance processInstance;
            if( correlationKey != null ) { 
                processInstance = ((CorrelationAwareProcessRuntime) ksession).getProcessInstance(correlationKey);
            } else { 
                processInstance = ksession.getProcessInstance(processInstanceId);
            }
            if (processInstance != null) {
                processInstance.signalEvent(eventType, event);
            }
        }
        return null;
    }

    public String toString() {
        if ("-1".equals(processInstanceId) && correlationKey == null) {
            return "ksession.signalEvent(" + eventType + ", " + event + ");"; 
        } else if (correlationKey != null) {
            return "ksession.signalEvent(" + correlationKey + ", " + eventType + ", " + event + ");";
        } else {
            return "ksession.signalEvent(" + processInstanceId + ", " + eventType + ", " + event + ");";
        }
    }

}
