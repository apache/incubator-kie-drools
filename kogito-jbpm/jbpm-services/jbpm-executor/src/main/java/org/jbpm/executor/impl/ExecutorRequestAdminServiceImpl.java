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

package org.jbpm.executor.impl;

import java.util.List;

import javax.inject.Inject;

import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryStringCommand;
import org.jbpm.shared.services.impl.commands.RemoveObjectCommand;
import org.kie.internal.executor.api.ExecutorAdminService;

/**
 * Default implementation of <code>ExecutorAdminService</code> backed with JPA
 *
 */
public class ExecutorRequestAdminServiceImpl implements ExecutorAdminService {

    @Inject
    private TransactionalCommandService commandService;
   
    public ExecutorRequestAdminServiceImpl() {
    }

    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * {@inheritDoc}
     */
    public int clearAllRequests() {
        
        List<RequestInfo> requests = 
        		commandService.execute(new QueryStringCommand<List<RequestInfo>>("select r from RequestInfo r"));
        
        commandService.execute(new RemoveObjectCommand(requests.toArray()));

        return requests.size();
    }

    /**
     * {@inheritDoc}
     */
    public int clearAllErrors() {
        List<ErrorInfo> errors = 
        		commandService.execute(new QueryStringCommand<List<ErrorInfo>>("select e from ErrorInfo e"));

        commandService.execute(new RemoveObjectCommand(errors.toArray()));

        return errors.size();
    }
}
