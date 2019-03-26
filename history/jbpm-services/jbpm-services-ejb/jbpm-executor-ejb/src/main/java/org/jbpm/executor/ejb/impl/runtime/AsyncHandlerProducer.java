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

package org.jbpm.executor.ejb.impl.runtime;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.jbpm.executor.commands.PrintOutCommand;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.jbpm.services.ejb.api.ExecutorServiceEJB;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.WorkItemHandlerProducer;

/**
 * Dedicated <code>WorkItemHandlerProducer</code> to register <code>AsyncWorkItemHandler</code>
 * in EJB environment when using deployment services (jbpm-kie-services).
 *
 */
@ApplicationScoped
public class AsyncHandlerProducer implements WorkItemHandlerProducer {
	
	@EJB
	private ExecutorServiceEJB executorService;

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params) {
        Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();
        
        if (executorService != null) {
            handlers.put("async",new AsyncWorkItemHandler(executorService, PrintOutCommand.class.getName()));
        }
        
        return handlers;
    }

}
