/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.process.instance.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;
import org.kie.internal.command.ProcessInstanceIdCommand;

@XmlRootElement(name="update-process-command")
@XmlAccessorType(XmlAccessType.NONE)
public class UpdateProcessCommand implements GenericCommand<Void>, ProcessInstanceIdCommand {
	
	private static final long serialVersionUID = 6L;

	@XmlElement
	@XmlSchemaType(name="long")
	private Long processInstanceId;
	
	@XmlElement
	@XmlSchemaType(name="string")
    private String processXml;
    
    public UpdateProcessCommand(Long processInstanceId, String processXml) {
    	this.processInstanceId = processInstanceId;
    	this.processXml = processXml;
    }

    @Override
    public Long getProcessInstanceId() {
		return processInstanceId;
	}

    @Override
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessXml() {
		return processXml;
	}

	public void setProcessXml(String processXml) {
		this.processXml = processXml;
	}

	public Void execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        ProcessInstanceImpl processInstance = (ProcessInstanceImpl)
    		ksession.getProcessInstance(processInstanceId);
        if (processInstance != null) {
        	processInstance.setProcessXml(processXml);
        }
        return null;
    }
	
	public String toString() {
		return "((ProcessInstanceImpl) ksession.getProcessInstance(" 
			+ processInstanceId + ")).setProcessXml(" + processXml + ");";
	}
}
