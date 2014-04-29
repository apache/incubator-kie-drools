/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.executor.impl.jpa;

import java.util.List;

import org.drools.core.command.CommandService;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.kie.internal.executor.api.ExecutorAdminService;

/**
 * Default implementation of <code>ExecutorAdminService</code> backed with JPA
 * IMPORTANT: please keep all classes from package org.jbpm.shared.services.impl as FQCN
 * inside method body to avoid exception logged by CDI when used with in memory mode
 */
public class ExecutorRequestAdminServiceImpl implements ExecutorAdminService {

    
    private CommandService commandService;
   
    public ExecutorRequestAdminServiceImpl() {
    }

    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * {@inheritDoc}
     */
    public int clearAllRequests() {
        
        List<RequestInfo> requests = 
        		commandService.execute(new org.jbpm.shared.services.impl.commands.QueryStringCommand<List<RequestInfo>>("select r from RequestInfo r"));
        
        commandService.execute(new org.jbpm.shared.services.impl.commands.RemoveObjectCommand(requests.toArray()));

        return requests.size();
    }

    /**
     * {@inheritDoc}
     */
    public int clearAllErrors() {
        List<ErrorInfo> errors = 
        		commandService.execute(new org.jbpm.shared.services.impl.commands.QueryStringCommand<List<ErrorInfo>>("select e from ErrorInfo e"));

        commandService.execute(new org.jbpm.shared.services.impl.commands.RemoveObjectCommand(errors.toArray()));

        return errors.size();
    }
}
