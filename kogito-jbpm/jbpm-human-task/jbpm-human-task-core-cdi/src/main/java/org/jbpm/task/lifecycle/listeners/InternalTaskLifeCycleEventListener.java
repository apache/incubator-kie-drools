/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.lifecycle.listeners;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import org.jbpm.task.Task;
import org.jbpm.task.annotations.Internal;
import org.jbpm.task.events.BeforeTaskStartedEvent;


/**
 *
 * @author salaboy
 */

@Internal
public class InternalTaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    public InternalTaskLifeCycleEventListener() {
    }

    
    public void afterTaskStartedEvent(@Observes(notifyObserver= Reception.ALWAYS) @BeforeTaskStartedEvent Task ti) {
        System.out.println(" >>> Task Instance Started: "+ti);
    }
    
}
