/*
 *  Copyright 2009 salaboy.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.command.vsm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.process.ProcessInstance;
import org.drools.vsm.remote.ProcessInstanceRemoteClient;

/**
 *
 * @author salaboy
 */
public class StartProcessRemoteCommand implements GenericCommand<ExecutionResults>{
    private String processId;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private List<Object> data = null;

    public StartProcessRemoteCommand() {
    }


    public StartProcessRemoteCommand(String processId) {
        this.processId = processId;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }


    public ExecutionResults execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();

		if (data != null) {
			for (Object o: data) {
				ksession.insert(o);
			}
		}
		ProcessInstance processInstance = (ProcessInstance) ksession.startProcess(processId, parameters);

                ((ExecutionResultImpl)((KnowledgeCommandContext) context ).getExecutionResults()).getResults().put( processId, getRemoteClient(processInstance) );

		return ((ExecutionResultImpl)((KnowledgeCommandContext) context ).getExecutionResults());
	}

	public String toString() {
		String result = "session.startProcess(" + processId + ", [";
		if (parameters != null) {
			int i = 0;
			for (Map.Entry<String, Object> entry: parameters.entrySet()) {
				if (i++ > 0) {
					result += ", ";
				}
				result += entry.getKey() + "=" + entry.getValue();
			}
		}
		result += "]);";
		return result;
	}

    private ProcessInstance getRemoteClient(ProcessInstance processInstance) {
        return new ProcessInstanceRemoteClient(processInstance.getId(), processInstance.getProcessId(),
                 processInstance.getProcessName(), processInstance.getState());
    }
}
