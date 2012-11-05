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
package org.droolsjbpm.services.api;

import java.util.Collection;
import org.droolsjbpm.services.impl.model.NodeInstanceDesc;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.droolsjbpm.services.impl.model.VariableStateDesc;

/**
 *
 * @author salaboy
 */
public interface KnowledgeDataService {
    Collection<ProcessInstanceDesc> getProcessInstances();

    Collection<ProcessInstanceDesc> getProcessInstancesBySessionId(String sessionId);

    Collection<ProcessDesc> getProcessesBySessionId(String sessionId);

    Collection<ProcessDesc> getProcesses();
    
    ProcessInstanceDesc getProcessInstanceById(int sessionId, long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceHistory(int sessionId, long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceHistory(int sessionId, long processId, boolean completed);
    
    Collection<NodeInstanceDesc> getProcessInstanceFullHistory(int sessionId, long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceActiveNodes(int sessionId, long processId);
    
    Collection<VariableStateDesc> getVariablesCurrentState(long processInstanceId);
    
    

}
