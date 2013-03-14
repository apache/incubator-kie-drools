/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.executor.api.ExecutorQueryService;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.STATUS;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;

/**
 *
 * @author salaboy
 */
@Transactional
public class ExecutorQueryServiceImpl implements ExecutorQueryService {

    @Inject
    private JbpmServicesPersistenceManager pm;
   
    public ExecutorQueryServiceImpl() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }
    
    public List<RequestInfo> getPendingRequests() {
        return (List<RequestInfo>)pm.queryWithParametersInTransaction("PendingRequests", pm.addParametersToMap("now", new Date()));
    }
    public List<RequestInfo> getPendingRequestById(Long id) {
        return (List<RequestInfo>)pm.queryWithParametersInTransaction("PendingRequestById", pm.addParametersToMap("id", id));
    }
    public RequestInfo getRequestById(Long id) {
    	return pm.find(RequestInfo.class, id);
    }
    public List<RequestInfo> getRunningRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("RunningRequests");
    }

    public List<RequestInfo> getQueuedRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("QueuedRequests");
    }
    
    public List<RequestInfo> getFutureQueuedRequests() {
        return (List<RequestInfo>)pm.queryWithParametersInTransaction("FutureQueuedRequests", pm.addParametersToMap("now", new Date()));
    }

    public List<RequestInfo> getCompletedRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("CompletedRequests");
    }

    public List<RequestInfo> getInErrorRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("InErrorRequests");
    }

    public List<RequestInfo> getCancelledRequests() {
        return (List<RequestInfo>)pm.queryInTransaction("CancelledRequests");
    }

    public List<ErrorInfo> getAllErrors() {
    	return (List<ErrorInfo>)pm.queryInTransaction("GetAllErrors");
    }

    public List<ErrorInfo> getErrorsByRequestId(Long requestId) {
    	return (List<ErrorInfo>)pm.queryWithParametersInTransaction("GetErrorsByRequestId", pm.addParametersToMap("id", requestId));
    }

    public List<RequestInfo> getAllRequests() {
    	return (List<RequestInfo>)pm.queryInTransaction("GetAllRequests");
    }
    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses) {
    	return (List<RequestInfo>)pm.queryWithParametersInTransaction("GetRequestsByStatus",pm.addParametersToMap("statuses", statuses));
    }


}
