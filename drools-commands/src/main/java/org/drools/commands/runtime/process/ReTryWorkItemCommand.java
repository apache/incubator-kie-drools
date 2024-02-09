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

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.process.WorkItemManager;
import org.drools.commands.jaxb.JaxbMapAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ReTryWorkItemCommand implements ExecutableCommand<Void> {
    @XmlAttribute(name="id", required=true)
    private long workItemId;
    
    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    private Map<String, Object> params = new HashMap<>();
    
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
        ((WorkItemManager)ksession.getWorkItemManager()).retryWorkItem( workItemId, params );
        return null;
    }

    public String toString() {
        return "session.getWorkItemManager().retryWorkItem(" + workItemId + ","+ params+" );";
    }
}
