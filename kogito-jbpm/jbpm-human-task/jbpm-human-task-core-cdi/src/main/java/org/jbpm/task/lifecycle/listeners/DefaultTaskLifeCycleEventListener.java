/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.lifecycle.listeners;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;
import org.jbpm.task.Task;
import org.jbpm.task.events.BeforeTaskStartedEvent;


/**
 *
 */

@Alternative @Singleton
public class DefaultTaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    public DefaultTaskLifeCycleEventListener() {
        
    }

    public void afterTaskStartedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @BeforeTaskStartedEvent Task ti) {
        System.out.println(" XXX Default Log Task Started");
    }
    
}
