package org.kie.internal.task.api;

import org.kie.api.runtime.Context;
import org.kie.api.task.UserGroupCallback;

public interface TaskContext extends Context, org.kie.api.task.TaskContext {

    TaskPersistenceContext getPersistenceContext();

    void setPersistenceContext(TaskPersistenceContext context);

    UserGroupCallback getUserGroupCallback();
    
    void setUserId(String userId);

}
