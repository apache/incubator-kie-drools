package org.jbpm.process.instance.impl.workitem;

import java.util.Arrays;
import java.util.List;

import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.humantask.phases.Release;
import org.kie.api.runtime.process.HumanTaskWorkItem;
import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.workitem.LifeCyclePhase;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;

/**
 * Complete life cycle phase that applies to any human task.
 * It will set the status to "Completed" 
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
public class Complete implements LifeCyclePhase {

    public static final String ID = "complete";
    public static final String STATUS = "Completed";
    
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
    
    @Override
    public void apply(WorkItem workitem, Transition<?> transition) {
        if (workitem instanceof HumanTaskWorkItem) {
            if (transition.policies() != null) {
                for (Policy<?> policy : transition.policies()) {
                    if (policy instanceof SecurityPolicy) {
                        ((HumanTaskWorkItemImpl) workitem).setActualOwner(((SecurityPolicy) policy).value().getName());
                        break;
                    }
                }
            }
            workitem.getResults().put("ActorId", ((HumanTaskWorkItem) workitem).getActualOwner());
        }
    }
}
