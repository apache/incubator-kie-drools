/*
 * Copyright 2014 JBoss by Red Hat.
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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.drools.core.command.CommandService;
import org.drools.core.command.impl.GenericCommand;
import org.jbpm.executor.ExecutorServiceFactory;
import org.kie.internal.command.Context;
import org.kie.internal.executor.api.ErrorInfo;
import org.kie.internal.executor.api.ExecutorStoreService;
import org.kie.internal.executor.api.RequestInfo;
import org.kie.internal.executor.api.STATUS;

/**
 * 
 * IMPORTANT: please keep all classes from package org.jbpm.shared.services.impl as FQCN
 * inside method body to avoid exception logged by CDI when used with in memory mode
 */
public class JPAExecutorStoreService implements ExecutorStoreService {
	
	private EntityManagerFactory emf;
    private CommandService commandService;
    
    public JPAExecutorStoreService(boolean active) {
    	
    }
    
    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }

    public void setEmf(EntityManagerFactory emf) {
 	   this.emf = emf;
    }

	@Override
	public void persistRequest(RequestInfo request) {
		commandService.execute(new org.jbpm.shared.services.impl.commands.PersistObjectCommand(request));
	}

	@Override
	public void updateRequest(RequestInfo request) {
		commandService.execute(new org.jbpm.shared.services.impl.commands.MergeObjectCommand(request));

	}

	@Override
	public RequestInfo removeRequest(Long requestId) {
		return commandService.execute(new LockAndCancelRequestInfoCommand(requestId));
		
	}

	@Override
	public RequestInfo findRequest(Long id) {
		return commandService.execute(new org.jbpm.shared.services.impl.commands.FindObjectCommand<RequestInfo>(id, RequestInfo.class));
	}

	@Override
	public void persistError(ErrorInfo error) {
		commandService.execute(new org.jbpm.shared.services.impl.commands.PersistObjectCommand(error));

	}

	@Override
	public void updateError(ErrorInfo error) {
		commandService.execute(new org.jbpm.shared.services.impl.commands.MergeObjectCommand(error));

	}

	@Override
	public ErrorInfo removeError(Long errorId) {
		ErrorInfo error = findError(errorId);
		commandService.execute(new org.jbpm.shared.services.impl.commands.RemoveObjectCommand(error));
		return error;
	}

	@Override
	public ErrorInfo findError(Long id) {
		return commandService.execute(new org.jbpm.shared.services.impl.commands.FindObjectCommand<ErrorInfo>(id, ErrorInfo.class));
	}

	@Override
	public Runnable buildExecutorRunnable() {
		return ExecutorServiceFactory.buildRunable(emf);
	}


    private class LockAndCancelRequestInfoCommand implements GenericCommand<RequestInfo> {

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
				request = ctx.queryAndLockWithParametersInTransaction("PendingRequestById",params, true, RequestInfo.class);
				
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
