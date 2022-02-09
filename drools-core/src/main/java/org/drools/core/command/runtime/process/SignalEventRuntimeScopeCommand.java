/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.xml.jaxb.util.JaxbUnknownAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SignalEventRuntimeScopeCommand implements ExecutableCommand<Void> {

    /** Generated serial version UID */
    private static final long serialVersionUID = 2134028686669740220L;

    @XmlAttribute(name="event-type", required=true)
    private String eventType;

    @XmlElement(name="event")
    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    private Object event;

    public SignalEventRuntimeScopeCommand() {
    }

    public SignalEventRuntimeScopeCommand(String eventType, Object event) {
        this.eventType = eventType;
        this.event = event;
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
        ((InternalKnowledgeRuntime) ksession).getProcessRuntime().signalEventRuntimeManagerScope(eventType, event);
        return null;
    }

}
