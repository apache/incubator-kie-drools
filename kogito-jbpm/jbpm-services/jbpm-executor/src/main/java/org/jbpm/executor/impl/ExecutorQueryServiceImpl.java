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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
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
    private JbpmServicesPersistenceManager pm;
   
    public ExecutorQueryServiceImpl() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }
    
    @PostConstruct
    public void init() {
        // make sure it has tx manager as it runs as background thread - no request scope available
        if (!((JbpmServicesPersistenceManagerImpl) pm).hasTransactionManager()) {
            ((JbpmServicesPersistenceManagerImpl) pm).setTransactionManager(new JbpmJTATransactionManager());
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getPendingRequests() {
        return (List<RequestInfo>)pm.queryWithParametersInTransaction("PendingRequests", pm.addParametersToMap("now", new Date()));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getPendingRequestById(Long id) {
        return (List<RequestInfo>)pm.queryWithParametersInTransaction("PendingRequestById", pm.addParametersToMap("id", id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestInfo getRequestById(Long id) {
    	return pm.find(org.jbpm.executor.entities.RequestInfo.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getRunningRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("RunningRequests");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getQueuedRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("QueuedRequests");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getFutureQueuedRequests() {
        return (List<RequestInfo>)pm.queryWithParametersInTransaction("FutureQueuedRequests", pm.addParametersToMap("now", new Date()));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getCompletedRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("CompletedRequests");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getInErrorRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("InErrorRequests");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getCancelledRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("CancelledRequests");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ErrorInfo> getAllErrors() {
    	return (List<ErrorInfo>)pm.queryInTransaction("GetAllErrors");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ErrorInfo> getErrorsByRequestId(Long requestId) {
    	return (List<ErrorInfo>)pm.queryWithParametersInTransaction("GetErrorsByRequestId", pm.addParametersToMap("id", requestId));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getAllRequests() {
    	return (List<RequestInfo>)pm.queryInTransaction("GetAllRequests");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses) {
    	return (List<RequestInfo>)pm.queryWithParametersInTransaction("GetRequestsByStatus",pm.addParametersToMap("statuses", statuses));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestInfo> getRequestByBusinessKey(String businessKey) {
        
        return (List<RequestInfo>)pm.queryWithParametersInTransaction("GetRequestsByBusinessKey", pm.addParametersToMap("businessKey", businessKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestInfo getRequestForProcessing() {
        RequestInfo request = null;
        boolean txOwner = pm.beginTransaction();
        try {
            // need to do the lock here to avoid many executor services fetch the same element
            request = (RequestInfo) pm.queryAndLockWithParametersInTransaction("PendingRequestsForProcessing", 
                                                    pm.addParametersToMap("now", new Date(), "firstResult", 0, "maxResults", 1), true);

            
            if (request != null) {
                request.setStatus(STATUS.RUNNING);
                pm.merge(request);
            }
            pm.endTransaction(txOwner);
        } catch(NoResultException e) {
            // no result is considered ok here so end transaction normally
            pm.endTransaction(txOwner);
        } catch(Exception e) {
            
            pm.rollBackTransaction(txOwner);
        }
        
        return request;
    }


}
