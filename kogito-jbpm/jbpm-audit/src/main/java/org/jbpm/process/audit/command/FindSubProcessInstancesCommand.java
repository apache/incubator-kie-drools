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
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.internal.command.Context;

public class FindSubProcessInstancesCommand extends AbstractHistoryLogCommand<List<ProcessInstanceLog>> {

    /** generated serial version UID */
    private static final long serialVersionUID = 7334860936916050668L;

    private final long processInstanceId;
    
    public FindSubProcessInstancesCommand(long processInstanceId) {
        this.processInstanceId = processInstanceId;
	}
	
    public List<ProcessInstanceLog> execute(Context cntxt) {
        setLogEnvironment(cntxt);
        return this.auditLogService.findSubProcessInstances(processInstanceId);
    }
    
    public String toString() {
        return JPAAuditLogService.class.getSimpleName() + ".findSubProcessInstances("+ processInstanceId + ")";
    }
}
