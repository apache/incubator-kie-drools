package org.drools.process.instance;

import java.util.Map;

public interface WorkItemManager {

    void completeWorkItem(long id, Map<String, Object> results);

    void abortWorkItem(long id);

    void registerWorkItemHandler(String workItemName, WorkItemHandler handler);

}
