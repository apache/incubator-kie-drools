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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.jaxb.JaxbUnknownAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class RegisterWorkItemHandlerCommand implements ExecutableCommand<Void> {

    @XmlElement
    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    private WorkItemHandler handler;

    @XmlElement
    private String workItemName;

    public RegisterWorkItemHandlerCommand() {
    }

    public RegisterWorkItemHandlerCommand(String workItemName, WorkItemHandler handler) {
        this.handler = handler;
        this.workItemName = workItemName;
    }
        
    public WorkItemHandler getHandler() {
        return handler;
    }

    public void setHandler(WorkItemHandler handler) {
        this.handler = handler;
    }

    public String getWorkItemName() {
        return workItemName;
    }

    public void setWorkItemName(String workItemName) {
        this.workItemName = workItemName;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ksession.getWorkItemManager().registerWorkItemHandler(workItemName, handler);
        return null;
    }

    public String toString() {
        return "session.getWorkItemManager().registerWorkItemHandler("
            + workItemName + ", " + handler +  ");";
    }

}
