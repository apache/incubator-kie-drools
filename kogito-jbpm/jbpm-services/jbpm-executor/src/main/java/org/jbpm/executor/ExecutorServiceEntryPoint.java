/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor;

import java.util.Date;
import java.util.List;
import javax.enterprise.event.Event;

import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.STATUS;
import org.jbpm.shared.services.impl.events.JbpmServicesEventListener;

/**
 *
 * @author salaboy
 */
public interface ExecutorServiceEntryPoint {

    public List<RequestInfo> getQueuedRequests();

    public List<RequestInfo> getCompletedRequests();

    public List<RequestInfo> getInErrorRequests();

    public List<RequestInfo> getCancelledRequests();

    public List<ErrorInfo> getAllErrors();

    public List<RequestInfo> getAllRequests();
    
    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses);

    public int clearAllRequests();

    public int clearAllErrors();

    public Long scheduleRequest(String commandName, CommandContext ctx);

    public void cancelRequest(Long requestId);

    public void init();

    public void destroy();
    
    public boolean isActive();

    public int getInterval();

    public void setInterval(int waitTime);

    public int getRetries();

    public void setRetries(int defaultNroOfRetries);

    public int getThreadPoolSize();

    public void setThreadPoolSize(int nroOfThreads);
    
    public List<RequestInfo> getPendingRequests();

    public List<RequestInfo> getPendingRequestById(Long id);

    public Long scheduleRequest(String commandId, Date date, CommandContext ctx);

    public List<RequestInfo> getRunningRequests();
    
    public List<RequestInfo> getFutureQueuedRequests();

    public RequestInfo getRequestById(Long requestId);

    public List<ErrorInfo> getErrorsByRequestId(Long requestId);
    
    //Listeners NON CDI, CDI Listener will be automatically registered
    public void registerExecutorEventListener(JbpmServicesEventListener<RequestInfo> executorEventListener);
    
    public Event<RequestInfo> getExecutorEventListeners();
    
}
