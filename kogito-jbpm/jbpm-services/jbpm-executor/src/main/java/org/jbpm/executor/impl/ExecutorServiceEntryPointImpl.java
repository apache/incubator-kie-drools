/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.impl;

import java.util.Date;
import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jbpm.executor.ExecutorServiceEntryPoint;
import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.api.Executor;
import org.jbpm.executor.api.ExecutorQueryService;
import org.jbpm.executor.api.ExecutorRequestAdminService;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.STATUS;
import org.jbpm.shared.services.impl.events.JbpmServicesEventImpl;
import org.jbpm.shared.services.impl.events.JbpmServicesEventListener;

/**
 *
 * @author salaboy
 */
public class ExecutorServiceEntryPointImpl implements ExecutorServiceEntryPoint {
    @Inject
    private Executor executor;
    private boolean executorStarted = false;
    @Inject 
    private ExecutorQueryService queryService;
    @Inject
    private ExecutorRequestAdminService adminService;
    
    // External NON CDI event Listeners for the executor service
    private Event<RequestInfo> executorEvents = new JbpmServicesEventImpl<RequestInfo>();

    public ExecutorServiceEntryPointImpl() {
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public ExecutorQueryService getQueryService() {
        return queryService;
    }

    public void setQueryService(ExecutorQueryService queryService) {
        this.queryService = queryService;
    }

    public ExecutorRequestAdminService getAdminService() {
        return adminService;
    }

    public void setAdminService(ExecutorRequestAdminService adminService) {
        this.adminService = adminService;
    }
    
    public List<RequestInfo> getFutureQueuedRequests() {
        return queryService.getFutureQueuedRequests();
    }

    public List<RequestInfo> getQueuedRequests() {
        return queryService.getQueuedRequests();
    }

    public List<RequestInfo> getCompletedRequests() {
        return queryService.getCompletedRequests();
    }

    public List<RequestInfo> getInErrorRequests() {
        return queryService.getInErrorRequests();
    }

    public List<RequestInfo> getCancelledRequests() {
        return queryService.getCancelledRequests();
    }

    public List<ErrorInfo> getAllErrors() {
        return queryService.getAllErrors();
    }

    public List<RequestInfo> getAllRequests() {
        return queryService.getAllRequests();
    }

    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses) {
    	return queryService.getRequestsByStatus(statuses);
    }
    
    public int clearAllRequests() {
        return adminService.clearAllRequests();
    }

    public int clearAllErrors() {
        return adminService.clearAllErrors();
    }

    public Long scheduleRequest(String commandName, CommandContext ctx) {
        return executor.scheduleRequest(commandName, ctx);
    }

    public void cancelRequest(Long requestId) {
        executor.cancelRequest(requestId);
    }

    public void init() {
        executor.init();
        this.executorStarted = true;
    }

    public void destroy() {
    	this.executorStarted = false;
        executor.destroy();
    }
    
    public boolean isActive() {
    	return executorStarted;
    }

    public int getInterval() {
        return executor.getInterval();
    }

    public void setInterval(int waitTime) {
        executor.setInterval(waitTime);
    }

    public int getRetries() {
        return executor.getRetries();
    }

    public void setRetries(int defaultNroOfRetries) {
        executor.setRetries(defaultNroOfRetries);
    }

    public int getThreadPoolSize() {
        return executor.getThreadPoolSize();
    }

    public void setThreadPoolSize(int nroOfThreads) {
        executor.setThreadPoolSize(nroOfThreads);
    }

    public List<RequestInfo> getPendingRequests() {
        return queryService.getPendingRequests();
    }

    public List<RequestInfo> getPendingRequestById(Long id) {
        return queryService.getPendingRequestById(id);
    }

    public Long scheduleRequest(String commandId, Date date, CommandContext ctx) {
        return executor.scheduleRequest(commandId, date, ctx);
    }

    public List<RequestInfo> getRunningRequests() {
        return queryService.getRunningRequests();
    }

    public RequestInfo getRequestById(Long requestId) {
            return queryService.getRequestById(requestId);
    }

    public List<ErrorInfo> getErrorsByRequestId(Long requestId) {
            return queryService.getErrorsByRequestId(requestId);
    }

    @Override
    public void registerExecutorEventListener(JbpmServicesEventListener<RequestInfo> executorEventListener) {
        ((JbpmServicesEventImpl<RequestInfo>)executorEvents).addListener(executorEventListener);
    }

    @Override
    public Event<RequestInfo> getExecutorEventListeners() {
        return executorEvents;
    }

  
    
    
    
}
