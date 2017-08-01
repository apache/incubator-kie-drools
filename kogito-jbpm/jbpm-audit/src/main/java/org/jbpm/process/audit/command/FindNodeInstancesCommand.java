/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.audit.command;

import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.kie.api.runtime.Context;
import org.kie.internal.command.ProcessInstanceIdCommand;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FindNodeInstancesCommand extends AuditCommand<List<NodeInstanceLog>> implements ProcessInstanceIdCommand {

    /** generated serial version UID */
    private static final long serialVersionUID = 5374910016873481604L;

    @XmlAttribute(required=true, name="process-instance-id")
    @XmlSchemaType(name="long")
    private Long processInstanceId;
    
    @XmlAttribute 
    @XmlSchemaType(name="string")
    private String nodeId;
    
    public FindNodeInstancesCommand() { 
        // no-arg for JAXB
    }
    
    public FindNodeInstancesCommand(long processInstanceId) {
        this.processInstanceId = processInstanceId;
        this.nodeId = null;
	}
	
    public FindNodeInstancesCommand(long processInstanceId, String nodeId) {
        this.processInstanceId = processInstanceId;
        this.nodeId = nodeId;
        if( nodeId == null || nodeId.isEmpty() ) { 
            throw new IllegalArgumentException("The nodeId field must not be null or empty." );
        }
	}
	
    public List<NodeInstanceLog> execute(Context cntxt) {
        setLogEnvironment(cntxt);
        if( nodeId == null || nodeId.isEmpty() ) { 
            return this.auditLogService.findNodeInstances(processInstanceId);
        } else { 
            return this.auditLogService.findNodeInstances(processInstanceId, nodeId);
        }
    }
   
    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String toString() {
        if( nodeId == null || nodeId.isEmpty() ) { 
            return AuditLogService.class.getSimpleName() + ".findNodeInstances("+ processInstanceId + ")";
        } else { 
            return AuditLogService.class.getSimpleName() + ".findNodeInstances("+ processInstanceId + ", " + nodeId + ")";
        }
    }
}
