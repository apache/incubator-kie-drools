package org.kie.api.event.process;

import java.util.Date;

import org.kie.api.event.KieRuntimeEvent;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * A runtime event related to the execution of process instances.
 */
public interface ProcessEvent
    extends
    KieRuntimeEvent {

    /**
     * The ProcessInstance this event relates to.
     *
     * @return the process instance
     */
    ProcessInstance getProcessInstance();
    
    /**
     * Returns exact date when the event was created
     * @return time when event was created
     */
    Date getEventDate();

    /**
     * @return associated identity that performed the event
     */
    default String getEventIdentity(){
        return null;
    }

}
