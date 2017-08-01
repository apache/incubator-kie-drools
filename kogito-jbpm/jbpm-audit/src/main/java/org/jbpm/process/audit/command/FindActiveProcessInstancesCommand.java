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
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.runtime.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FindActiveProcessInstancesCommand extends AuditCommand<List<ProcessInstanceLog>> {

    /** generated serial version UID */
    private static final long serialVersionUID = 3096240261041200350L;

    @XmlAttribute(required=false)
    @XmlSchemaType(name="string")
    private String processId = null;
    
    public FindActiveProcessInstancesCommand() { 
        // no-arg for JAXB
    }
    
    public FindActiveProcessInstancesCommand(String processId) {
        this.processId = processId;
        if( processId == null || processId.isEmpty() ) { 
            throw new IllegalArgumentException("The processId field must not be null or empty." );
        }
	}
	
    public List<ProcessInstanceLog> execute(Context cntxt) {
        setLogEnvironment(cntxt);
        if( processId != null && ! processId.trim().isEmpty() ) { 
            return this.auditLogService.findActiveProcessInstances(processId);
        } else { 
            return this.auditLogService.findActiveProcessInstances();
        }
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
    
    public String toString() {
        return AuditLogService.class.getSimpleName() + ".findActiveProcessInstances("+ processId + ")";
    }
}
