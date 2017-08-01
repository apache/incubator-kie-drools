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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FindVariableInstancesByNameCommand extends AuditCommand<List<VariableInstanceLog>> {

    /** generated serial version UID */
    private static final long serialVersionUID = 7087452375594067164L;

    @XmlAttribute(required=true)
    @XmlSchemaType(name="string")
    private String variableId;
    
    @XmlAttribute(required=false)
    @XmlSchemaType(name="string")
    private String value = null;
    
    @XmlAttribute(required=true)
    @XmlSchemaType(name="boolean")
    private Boolean activeProcesses;
    
    public FindVariableInstancesByNameCommand() { 
        // no-arg for JAXB
    }
    
    public FindVariableInstancesByNameCommand(String variableId) {
        this.variableId = variableId;
        this.activeProcesses = true;
	}
	
    public FindVariableInstancesByNameCommand(String variableId, boolean onlyFromActiveProcesses) {
        this.variableId = variableId;
        this.activeProcesses = onlyFromActiveProcesses;
	}
	
    public FindVariableInstancesByNameCommand(String variableId, String value) {
        if( variableId == null || variableId.isEmpty() ) { 
            throw new IllegalArgumentException("The variableId field may not be null or empty." );
        }
        this.variableId = variableId;
        if( value == null || value.isEmpty() ) { 
            throw new IllegalArgumentException("The value field may not be null or empty." );
        }
        this.value = value;
        this.activeProcesses = true;
	}
	
    public FindVariableInstancesByNameCommand(String variableId, String value, boolean onlyFromActiveProcesses) {
        this(variableId, value);
        this.activeProcesses = onlyFromActiveProcesses;
    }
    
    public List<VariableInstanceLog> execute(Context cntxt) {
        setLogEnvironment(cntxt);
        if( this.value == null || this.value.isEmpty() ) { 
            return this.auditLogService.findVariableInstancesByName(variableId, activeProcesses);
        } else { 
            return this.auditLogService.findVariableInstancesByNameAndValue(variableId, value, activeProcesses);
        }
    }
    
    public String toString() {
        if( variableId == null || variableId.isEmpty() ) { 
            return AuditLogService.class.getSimpleName() + ".findVariableInstancesByName("+ variableId + ", " + activeProcesses + ")";
        } else { 
            return AuditLogService.class.getSimpleName() + ".findNodeInstancesByNameAndValue("+ variableId + ", " + value + ", " + activeProcesses + ")";
        }
    }
}
