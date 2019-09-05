package org.jbpm.process.instance.impl.workitem;

import java.util.Arrays;
import java.util.List;

import org.kie.kogito.process.workitem.LifeCyclePhase;


/**
 * Active life cycle phase that applies to any work item.
 * It will set the status to "Ready"
 * 
 * This is initial state so it can transition even if there is no phase set yet.
 */
public class Active implements LifeCyclePhase {

    public static final String ID = "active";
    public static final String STATUS = "Ready";
    
    private List<String> allowedTransitions = Arrays.asList();
    
    @Override
    public String id() {
        return ID;
    }

    @Override
    public String status() {
        return STATUS;
    }

    @Override
    public boolean isTerminating() {
        return false;
    }
    
    @Override
    public boolean canTransition(LifeCyclePhase phase) {
        if (phase == null) {
            return true;
        }
        
        return allowedTransitions.contains(phase.id());        
    }

}
