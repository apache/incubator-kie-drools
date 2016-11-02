/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ReTryWorkItemCommand implements ExecutableCommand<Void> {
    @XmlAttribute(name="id", required=true)
    private long workItemId;
    
    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    private Map<String, Object> params = new HashMap<String, Object>();
    
    public ReTryWorkItemCommand() {
        
    }

    public ReTryWorkItemCommand(long workItemId ,Map<String,Object> params) {
        this.workItemId = workItemId;
        this.params = params;
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }

    
    public Map<String, Object> getParams() {
        return params;
    }

    
    public void setParams( Map<String, Object> params ) {
        this.params = params;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ((org.drools.core.process.instance.WorkItemManager)ksession.getWorkItemManager()).retryWorkItem( workItemId, params );
        return null;
    }

    public String toString() {
        return "session.getWorkItemManager().retryWorkItem(" + workItemId + ","+ params+" );";
    }
}
