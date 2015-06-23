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

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;

public class UpdateProcessCommand implements GenericCommand<Void> {
	
	private static final long serialVersionUID = 6L;
	
	private Long processInstanceId;
    private String processXml;
    
    public UpdateProcessCommand(Long processInstanceId, String processXml) {
    	this.processInstanceId = processInstanceId;
    	this.processXml = processXml;
    }

    public Long getProcessInstanceId() {
		return processInstanceId;
	}

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
