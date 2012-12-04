/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.impl;

import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.jbpm.executor.ExecutorServiceEntryPoint;
import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.api.Executor;
import org.jbpm.executor.api.ExecutorQueryService;
import org.jbpm.executor.api.ExecutorRequestAdminService;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;

/**
 *
 * @author salaboy
 */
public class ExecutorServiceEntryPointImpl implements ExecutorServiceEntryPoint {
    @Inject
    private Executor executor;
    @Inject 
    private ExecutorQueryService queryService;
    @Inject
    private ExecutorRequestAdminService adminService;

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
    }

    public void destroy() {
        executor.destroy();
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
    
    
    
    
}
