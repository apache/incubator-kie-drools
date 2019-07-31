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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetWorkItemCommand implements ExecutableCommand<WorkItem> {

    @XmlAttribute(required = true)
    private String workItemId;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public GetWorkItemCommand() {
    }

    public GetWorkItemCommand(String workItemId) {
        this.workItemId = workItemId;
    }
        
    public String getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(String workItemId) {
        this.workItemId = workItemId;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public WorkItem execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        final WorkItem workItem = ((WorkItemManager) ksession.getWorkItemManager()).getWorkItem(workItemId);

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult(this.outIdentifier,
                                                                                      workItem);
        }

        return workItem;
    }

    public String toString() {
        return "((org.drools.core.process.instance.WorkItemManager) session.getWorkItemManager()).getWorkItem("
            + workItemId +  ");";
    }

}
