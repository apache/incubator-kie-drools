/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.api;

import java.util.List;

import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.STATUS;

/**
 *
 * @author salaboy
 */
public interface ExecutorQueryService {
    List<RequestInfo> getPendingRequests();
    List<RequestInfo> getPendingRequestById(Long id);
    RequestInfo getRequestById(Long id);
    List<ErrorInfo> getErrorsByRequestId(Long id);
    List<RequestInfo> getQueuedRequests();
    List<RequestInfo> getCompletedRequests();
    List<RequestInfo> getInErrorRequests();
    List<RequestInfo> getCancelledRequests();
    List<ErrorInfo> getAllErrors(); 
    List<RequestInfo> getAllRequests(); 
    List<RequestInfo> getRunningRequests();
    List<RequestInfo> getFutureQueuedRequests();
    List<RequestInfo> getRequestsByStatus(List<STATUS> statuses);
}
