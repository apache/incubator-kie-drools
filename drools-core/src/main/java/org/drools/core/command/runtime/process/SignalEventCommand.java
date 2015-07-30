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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.xml.jaxb.util.JaxbUnknownAdapter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.Context;
import org.kie.internal.command.ProcessInstanceIdCommand;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SignalEventCommand implements GenericCommand<Void>, ProcessInstanceIdCommand {

    /** Generated serial version UID */
    private static final long serialVersionUID = 2134028686669740220L;

    @XmlAttribute(name="process-instance-id")
    private long processInstanceId = -1;

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

    public SignalEventCommand(long processInstanceId,
                              String eventType,
                              Object event) {
        this.processInstanceId = processInstanceId;
        this.eventType = eventType;
        this.event = event;
    }


    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
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
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        
        if (processInstanceId == -1) {
            ksession.signalEvent(eventType, event);
        } else {
            ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
            if (processInstance != null) {
                processInstance.signalEvent(eventType, event);
            }
        }
        return null;
    }

    public String toString() {
        if (processInstanceId == -1) {
            return "ksession.signalEvent(" + eventType + ", " + event + ");";
        } else {
            return "ksession.signalEvent(" + processInstanceId + ", " + eventType + ", " + event + ");";
        }
    }

}
