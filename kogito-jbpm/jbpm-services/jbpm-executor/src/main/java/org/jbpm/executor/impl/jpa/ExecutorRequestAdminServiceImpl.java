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

import java.util.Date;
import java.util.List;

import org.drools.core.command.CommandService;
import org.drools.core.command.impl.GenericCommand;
import org.jbpm.executor.RequeueAware;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.kie.internal.command.Context;
import org.kie.internal.executor.api.ExecutorAdminService;
import org.kie.internal.executor.api.STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of <code>ExecutorAdminService</code> backed with JPA
 * IMPORTANT: please keep all classes from package org.jbpm.shared.services.impl as FQCN
 * inside method body to avoid exception logged by CDI when used with in memory mode
 */
public class ExecutorRequestAdminServiceImpl implements ExecutorAdminService, RequeueAware  {

    
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

	@Override
	public void requeue(Long olderThan) {
		commandService.execute(new RequeueRunningJobsCommand(olderThan));
	}
	
	@Override
	public void requeueById(Long requestId) {
		commandService.execute(new RequeueRunningJobCommand(requestId));
	}
	
	private class RequeueRunningJobsCommand implements GenericCommand<Void> {

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
						logger.info("Requeing request as the time exceeded for its running state id : {}, key : {}, start time : {}, max time {}",
								request.getId(), request.getKey(), request.getTime(), new Date(upperLimitTime));
		                request.setStatus(STATUS.QUEUED);
		                ctx.merge(request);
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
	
	private class RequeueRunningJobCommand implements GenericCommand<Void> {

		private Logger logger = LoggerFactory.getLogger(RequeueRunningJobCommand.class);
		private static final long serialVersionUID = 8670412133363766161L;

		private Long requestId;
		
		public RequeueRunningJobCommand(Long id) {
			this.requestId = id;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Void execute(Context context) {
			
	    	List<RequestInfo> requests = null;
	    	try {
	    		org.jbpm.shared.services.impl.JpaPersistenceContext ctx = (org.jbpm.shared.services.impl.JpaPersistenceContext) context;
				requests = ctx.queryInTransaction("RunningRequests", List.class);
				
				for (RequestInfo request : requests) {
					if (request != null && request.getId() == requestId) {
						logger.info("Requeing request with id : {}, key : {}, start time : {}",
								request.getId(), request.getKey(), request.getTime());
		                request.setStatus(STATUS.QUEUED);
		                ctx.merge(request);
		                break;
		            }
				}
	    	} catch (Exception e) {
	    		logger.warn("Error while trying to requeue jobs that runs for too long {}", e.getMessage());
	    	}
			return null;
		}
	}
}
