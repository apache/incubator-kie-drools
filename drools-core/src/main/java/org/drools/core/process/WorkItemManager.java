package org.drools.core.process;

import java.util.Map;
import java.util.Set;

public interface WorkItemManager extends org.kie.api.runtime.process.WorkItemManager {

    void internalExecuteWorkItem(WorkItem workItem);

    void internalAddWorkItem(WorkItem workItem);

    void internalAbortWorkItem(long id);
    
    Set<WorkItem> getWorkItems();

    WorkItem getWorkItem(long id);

    void clear();
    
    void signalEvent(String type, Object event);
    
    void signalEvent(String type, Object event, String processInstanceId);

    void dispose();
    
    void retryWorkItem( Long workItemID, Map<String, Object> params ) ;

}
