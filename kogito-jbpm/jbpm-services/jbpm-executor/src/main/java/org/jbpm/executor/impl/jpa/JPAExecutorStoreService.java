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

package org.jbpm.executor.impl.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.drools.core.command.impl.ExecutableCommand;
import org.jbpm.executor.impl.event.ExecutorEventSupportImpl;
import org.jbpm.shared.services.impl.commands.FindObjectCommand;
import org.jbpm.shared.services.impl.commands.FunctionCommand;
import org.jbpm.shared.services.impl.commands.MergeObjectCommand;
import org.jbpm.shared.services.impl.commands.PersistObjectCommand;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.jbpm.shared.services.impl.commands.RemoveObjectCommand;
import org.kie.api.executor.ErrorInfo;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.ExecutorStoreService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.Context;

/**
 * 
 * IMPORTANT: please keep all classes from package org.jbpm.shared.services.impl as FQCN
 * inside method body to avoid exception logged by CDI when used with in memory mode
 */
public class JPAExecutorStoreService implements ExecutorStoreService {
	
	private EntityManagerFactory emf;
    private CommandExecutor commandService;
    
    private ExecutorEventSupportImpl eventSupport = new ExecutorEventSupportImpl();


    public JPAExecutorStoreService(boolean active) {
    	
    }
        
    public void setEventSupport(ExecutorEventSupportImpl eventSupport) {
        this.eventSupport = eventSupport;
    }
    
    public void setCommandService(CommandExecutor commandService ) {
        this.commandService = commandService;
    }

    public void setEmf(EntityManagerFactory emf) {
 	   this.emf = emf;
    }

	@Override
	public void persistRequest(RequestInfo request, Consumer<Object> function) {
		commandService.execute(new FunctionCommand(new PersistObjectCommand(request), function));
	}

	@Override
	public void updateRequest(RequestInfo request, Consumer<Object> function) {
		commandService.execute(new FunctionCommand(new MergeObjectCommand(request), function));

	}

	@Override
	public RequestInfo removeRequest(Long requestId, Consumer<Object> function) {
		return (RequestInfo) commandService.execute(new FunctionCommand(new LockAndCancelRequestInfoCommand(requestId), function));
		
	}

	@Override
	public RequestInfo findRequest(Long id) {
		return commandService.execute(new FindObjectCommand<org.jbpm.executor.entities.RequestInfo>(id, org.jbpm.executor.entities.RequestInfo.class));
	}

	@Override
	public void persistError(ErrorInfo error) {
		commandService.execute(new PersistObjectCommand(error));

	}

	@Override
	public void updateError(ErrorInfo error) {
		commandService.execute(new MergeObjectCommand(error));

	}

	@Override
	public ErrorInfo removeError(Long errorId) {
		ErrorInfo error = findError(errorId);
		commandService.execute(new RemoveObjectCommand(error));
		return error;
	}

	@Override
	public ErrorInfo findError(Long id) {
		return commandService.execute(new FindObjectCommand<ErrorInfo>(id, ErrorInfo.class));
	}

    @Override
    public List<RequestInfo> loadRequests() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("owner", ExecutorService.EXECUTOR_ID);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("LoadPendingRequests", params));
    }

    private class LockAndCancelRequestInfoCommand implements ExecutableCommand<RequestInfo> {

		private static final long serialVersionUID = 8670412133363766161L;

		private Long requestId;
		
		LockAndCancelRequestInfoCommand(Long requestId) {
			this.requestId = requestId;
		}
		
		@Override
		public RequestInfo execute(Context context) {
			Map<String, Object> params = new HashMap<String, Object>();
	    	params.put("id", requestId);
	    	params.put("firstResult", 0);
	    	params.put("maxResults", 1);
	    	RequestInfo request = null;
	    	try {
	    		org.jbpm.shared.services.impl.JpaPersistenceContext ctx = (org.jbpm.shared.services.impl.JpaPersistenceContext) context;
				request = ctx.queryAndLockWithParametersInTransaction("EligibleRequestById", params, true, RequestInfo.class);
				
				if (request != null) {
	                request.setStatus(STATUS.CANCELLED);
	                ctx.merge(request);
	            }
	    	} catch (NoResultException e) {
	    		
	    	}
			return request;
		}
    	
    }

}
