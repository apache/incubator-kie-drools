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

import java.util.List;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.kie.internal.command.Context;

public class FindNodeInstancesCommand extends AbstractHistoryLogCommand<List<NodeInstanceLog>> {

    /** generated serial version UID */
    private static final long serialVersionUID = 5374910016873481604L;

    private final long processInstanceId;
    private final String nodeId;
    
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
    
    public String toString() {
        if( nodeId == null || nodeId.isEmpty() ) { 
            return JPAAuditLogService.class.getSimpleName() + ".findNodeInstances("+ processInstanceId + ")";
        } else { 
            return "JPAProcessInstanceDbLog.findNodeInstances("+ processInstanceId + ", " + nodeId + ")";
        }
    }
}
