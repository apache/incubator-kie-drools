/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.lifecycle.listeners;

import org.kie.internal.task.api.model.Task;

/**
 *
 */

public interface TaskLifeCycleEventListener {
    
    //@TODO: add all the methods
    public void afterTaskActivatedEvent(Task ti);
    public void afterTaskClaimedEvent(Task ti);
    public void afterTaskSkippedEvent(Task ti);
    public void afterTaskStartedEvent(Task ti);
    public void afterTaskStoppedEvent(Task ti);
    public void afterTaskCompletedEvent(Task ti);
    public void afterTaskFailedEvent(Task ti);
    public void afterTaskAddedEvent(Task ti);
    public void afterTaskExitedEvent(Task ti);
    
}
