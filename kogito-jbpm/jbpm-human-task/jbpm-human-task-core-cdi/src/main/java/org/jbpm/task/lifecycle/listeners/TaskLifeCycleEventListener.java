/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.lifecycle.listeners;

import org.jbpm.task.Task;

/**
 *
 */

public interface TaskLifeCycleEventListener {
    public void afterTaskStartedEvent(Task ti);
    
}
