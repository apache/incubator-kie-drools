package org.jbpm.process.instance.impl.workitem;

import java.util.Arrays;
import java.util.List;

import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.humantask.phases.Release;
import org.kie.kogito.process.workitem.LifeCyclePhase;

/**
 * Abort life cycle phase that applies to any work item.
 * It will set the status to "Aborted"
 *
 * It can transition from
 * <ul>
 *  <li>Active</li>
 *  <li>Claim</li>
 *  <li>Release</li>
 * </ul>
 * 
 * This is a terminating (final) phase.
 */
public class Abort implements LifeCyclePhase {

    public static final String ID = "abort";
    public static final String STATUS = "Aborted";
    
    private List<String> allowedTransitions = Arrays.asList(Active.ID, Claim.ID, Release.ID);
    
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
        return true;
    }
    
    @Override
    public boolean canTransition(LifeCyclePhase phase) {
        return allowedTransitions.contains(phase.id());        
    }

}
