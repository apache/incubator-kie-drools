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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.drools.core.command.impl.GenericCommand;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.FindObjectCommand;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.internal.command.Context;
import org.kie.internal.executor.api.ErrorInfo;
import org.kie.internal.executor.api.ExecutorQueryService;
import org.kie.internal.executor.api.RequestInfo;
import org.kie.internal.executor.api.STATUS;


/**
 * Default implementation of <code>ExecutorQueryService</code> that is backed with JPA
 *
 */
public class ExecutorQueryServiceImpl implements ExecutorQueryService {

    @Inject
    private TransactionalCommandService commandService;
   
    public ExecutorQueryServiceImpl() {
    }

    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getPendingRequests() {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("now", new Date());
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("PendingRequests", params));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getPendingRequestById(Long id) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("id", id);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("PendingRequestById", params));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestInfo getRequestById(Long id) {
    	return commandService.execute(new FindObjectCommand<org.jbpm.executor.entities.RequestInfo>(id, org.jbpm.executor.entities.RequestInfo.class));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getRunningRequests() {
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("RunningRequests"));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getQueuedRequests() {
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("QueuedRequests"));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getFutureQueuedRequests() {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("now", new Date());
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("FutureQueuedRequests", params));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getCompletedRequests() {
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("CompletedRequests"));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getInErrorRequests() {
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("InErrorRequests"));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getCancelledRequests() {
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("CancelledRequests"));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<ErrorInfo> getAllErrors() {
    	return commandService.execute(new QueryNameCommand<List<ErrorInfo>>("GetAllErrors"));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<ErrorInfo> getErrorsByRequestId(Long requestId) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("id", requestId);
    	return commandService.execute(new QueryNameCommand<List<ErrorInfo>>("GetErrorsByRequestId", params));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getAllRequests() {
    	return commandService.execute(new QueryNameCommand<List<RequestInfo>>("GetAllRequests"));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("statuses", statuses);
    	return commandService.execute(new QueryNameCommand<List<RequestInfo>>("GetRequestsByStatus",params));
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<RequestInfo> getRequestByBusinessKey(String businessKey) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("businessKey", businessKey);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("GetRequestsByBusinessKey", params));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestInfo getRequestForProcessing() {
        
        // need to do the lock here to avoid many executor services fetch the same element
    	RequestInfo request = commandService.execute(new LockAndUpdateRequestInfoCommand());
        
        return request;
    }

    private class LockAndUpdateRequestInfoCommand implements GenericCommand<RequestInfo> {

		private static final long serialVersionUID = 8670412133363766161L;

		@Override
		public RequestInfo execute(Context context) {
			Map<String, Object> params = new HashMap<String, Object>();
	    	params.put("now", new Date());
	    	params.put("firstResult", 0);
	    	params.put("maxResults", 1);
	    	RequestInfo request = null;
	    	try {
				JpaPersistenceContext ctx = (JpaPersistenceContext) context;
				request = ctx.queryAndLockWithParametersInTransaction("PendingRequestsForProcessing",params, true, RequestInfo.class);
				
				if (request != null) {
	                request.setStatus(STATUS.RUNNING);
	                ctx.merge(request);
	            }
	    	} catch (NoResultException e) {
	    		
	    	}
			return request;
		}
    	
    }

}
