package org.kie.internal.runtime.manager;

import org.kie.api.task.TaskService;

/**
 * Factory that produces <code>TaskService</code> instances.
 *
 */
public interface TaskServiceFactory {

    /**
     * Produces new instance of <code>TaskService</code>
     * @return new instance of <code>TaskService</code>
     */
    TaskService newTaskService();

    /**
     * Closes this factory and releases all resources
     */
    void close();
}
