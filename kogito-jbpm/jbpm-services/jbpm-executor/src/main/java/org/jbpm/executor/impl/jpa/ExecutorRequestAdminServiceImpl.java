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

import java.util.Date;
import java.util.List;

import org.jbpm.executor.RequeueAware;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.impl.ExecutorImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.executor.Executor;
import org.kie.api.executor.ExecutorAdminService;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of <code>ExecutorAdminService</code> backed with JPA
 * IMPORTANT: please keep all classes from package org.jbpm.shared.services.impl as FQCN
 * inside method body to avoid exception logged by CDI when used with in memory mode
 */
public class ExecutorRequestAdminServiceImpl implements ExecutorAdminService, RequeueAware  {

    private Executor executor;
    private CommandExecutor commandService;
   
    public ExecutorRequestAdminServiceImpl() {
    }

    public void setCommandService(CommandExecutor commandService ) {
        this.commandService = commandService;
    }
    
    public void setExecutor(Executor executor) {
        this.executor = executor;
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

	@Override
	public void requeue(Long olderThan) {
		commandService.execute(new RequeueRunningJobsCommand(olderThan));
	}
	
	@Override
	public void requeueById(Long requestId) {
		commandService.execute(new RequeueRunningJobCommand(requestId));
	}
	
	private class RequeueRunningJobsCommand implements ExecutableCommand<Void> {

		private Logger logger = LoggerFactory.getLogger(RequeueRunningJobsCommand.class);
		private static final long serialVersionUID = 8670412133363766161L;

		private Long upperLimitTime;
		
		public RequeueRunningJobsCommand(Long maxRunningTime) {
			this.upperLimitTime = System.currentTimeMillis() - maxRunningTime;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Void execute(Context context) {
			
	    	List<RequestInfo> requests = null;
	    	try {
	    		org.jbpm.shared.services.impl.JpaPersistenceContext ctx = (org.jbpm.shared.services.impl.JpaPersistenceContext) context;
				requests = ctx.queryInTransaction("RunningRequests", List.class);
				
				for (RequestInfo request : requests) {
					if (request != null && maxRunningTimeExceeded(request.getTime())) {
						logger.debug("Requeing request as the time exceeded for its running state id : {}, key : {}, start time : {}, max time {}",
								request.getId(), request.getKey(), request.getTime(), new Date(upperLimitTime));
		                request.setStatus(STATUS.QUEUED);
		                ctx.merge(request);
		                
		                ((ExecutorImpl) executor).scheduleExecutionViaSync(request, request.getTime());
		            }
				}
	    	} catch (Exception e) {
	    		logger.warn("Error while trying to requeue jobs that runs for too long {}", e.getMessage());
	    	}
			return null;
		}
		
		private boolean maxRunningTimeExceeded(Date actualDate) {
			if (actualDate.getTime() < upperLimitTime) {
				return true;
			}
			
			return false;
		}
		
	}
	
	private class RequeueRunningJobCommand implements ExecutableCommand<Void> {

		private Logger logger = LoggerFactory.getLogger(RequeueRunningJobCommand.class);
		private static final long serialVersionUID = 8670412133363766161L;

		private Long requestId;
		
		public RequeueRunningJobCommand(Long id) {
			this.requestId = id;
		}
		
		@Override
		public Void execute(Context context) {
				    	
	    	try {
	    		org.jbpm.shared.services.impl.JpaPersistenceContext ctx = (org.jbpm.shared.services.impl.JpaPersistenceContext) context;
				
	    		RequestInfo request = ctx.find(RequestInfo.class, requestId);
								
				if (request != null && request.getStatus() != STATUS.CANCELLED
						&& request.getStatus() != STATUS.DONE) {
					logger.debug("Requeing request with id : {}, key : {}, start time : {}", request.getId(), request.getKey(), request.getTime());
	                request.setStatus(STATUS.QUEUED);
	                ctx.merge(request);
	                
	                ((ExecutorImpl) executor).scheduleExecutionViaSync(request, request.getTime());
	            } else {
	            	throw new IllegalArgumentException("Retrying completed or cancelled job is not allowed (job id " + requestId +")");
	            }
				
	    	} catch (Exception e) {
	    		logger.warn("Error while trying to requeue jobs that runs for too long {}", e.getMessage());
	    	}
			return null;
		}
	}
}
