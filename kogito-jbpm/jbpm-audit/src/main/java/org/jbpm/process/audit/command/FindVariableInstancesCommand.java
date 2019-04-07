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
import org.jbpm.process.audit.VariableInstanceLog;
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
public class FindVariableInstancesCommand extends AuditCommand<List<VariableInstanceLog>> implements ProcessInstanceIdCommand {

    /** generated serial version UID */
    private static final long serialVersionUID = 7087452375594067164L;

    @XmlAttribute(required=true, name="process-instance-id")
    @XmlSchemaType(name="long")
    private Long processInstanceId;
    
    @XmlAttribute(required=true)
    @XmlSchemaType(name="string")
    private String variableId;
    
    public FindVariableInstancesCommand() { 
        // no-arg for JAXB
    }
    
    public FindVariableInstancesCommand(long processInstanceId) {
        this.processInstanceId = processInstanceId;
        this.variableId = null;
	}
	
    public FindVariableInstancesCommand(long processInstanceId, String variableId) {
        this.processInstanceId = processInstanceId;
        this.variableId = variableId;
        if( variableId == null || variableId.isEmpty() ) { 
            throw new IllegalArgumentException("The variableId field must not be null or empty." );
        }
	}
	
    public List<VariableInstanceLog> execute(Context cntxt) {
        setLogEnvironment(cntxt);
        if( variableId == null || variableId.isEmpty() ) { 
            return this.auditLogService.findVariableInstances(processInstanceId);
        } else { 
            return this.auditLogService.findVariableInstances(processInstanceId, variableId);
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

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String toString() {
        if( variableId == null || variableId.isEmpty() ) { 
            return AuditLogService.class.getSimpleName() + ".findVariableInstances("+ processInstanceId + ")";
        } else { 
            return AuditLogService.class.getSimpleName() + ".findVariableInstances("+ processInstanceId + ", " + variableId + ")";
        }
    }
}
