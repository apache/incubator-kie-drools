/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.process.audit.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.internal.command.Context;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FindProcessInstanceCommand extends AbstractHistoryLogCommand<ProcessInstanceLog> {

    /** generated serial version UID */
    private static final long serialVersionUID = -7548733507155126870L;

    @XmlAttribute(required=true)
    @XmlSchemaType(name="long")
    private Long processInstanceId;
    
    public FindProcessInstanceCommand() { 
        // no-arg for JAXB
    }
    
    public FindProcessInstanceCommand(long processInstanceId) {
        this.processInstanceId = processInstanceId;
	}
	
    public ProcessInstanceLog execute(Context cntxt) {
        setLogEnvironment(cntxt);
        return this.auditLogService.findProcessInstance(processInstanceId);
    }
    
    public String toString() {
        return JPAAuditLogService.class.getSimpleName() + ".findProcessInstance("+ processInstanceId + ")";
    }
}
