package org.jbpm.executor.api;

import java.util.Date;

/**
 *
 * @author salaboy
 */
public interface Executor extends Service {

    public Long scheduleRequest(String commandName, CommandContext ctx);
    
    public Long scheduleRequest(String commandId, Date date, CommandContext ctx);

    public void cancelRequest(Long requestId);

    public int getInterval();

    public void setInterval(int waitTime);

    public int getRetries();

    public void setRetries(int defaultNroOfRetries);

    public int getThreadPoolSize();

    public void setThreadPoolSize(int nroOfThreads);
    
    
}
