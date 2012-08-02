/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.lifecycle.listeners;

import org.jbpm.task.Task;

/**
 *
 */

public interface DeadlinesEventListener {
    public void afterTaskAddedEvent(Task ti);
    public void afterTaskStartedEvent(Task ti);
    public void afterTaskSkippedEvent(Task ti);
    public void afterTaskStoppedEvent(Task ti);
    public void afterTaskCompletedEvent(Task ti);
    public void afterTaskFailedEvent(Task ti);
    public void afterTaskExitedEvent(Task ti);
    
}
