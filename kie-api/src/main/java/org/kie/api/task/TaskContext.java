package org.kie.api.task;

import org.kie.api.task.model.Task;

public interface TaskContext {

    /**
     * Returns currently configured UserGroupCallback.
     * @return returns user group callback
     */
    UserGroupCallback getUserGroupCallback();

    /**
     * Loads task (given as argument) variables - both input and output if exists.
     * In case variables are already set they are not reread from data store.
     * @param task task which should have variables (both input and output) set
     * @return returns task with variables set
     */
    Task loadTaskVariables(Task task);
    
    /**
     * Returns user id who performs the operation
     * @return user id of the caller
     */
    String getUserId();
}
