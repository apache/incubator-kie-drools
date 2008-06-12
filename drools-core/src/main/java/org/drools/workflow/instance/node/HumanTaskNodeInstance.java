package org.drools.workflow.instance.node;

import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.context.swimlane.SwimlaneContextInstance;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.workflow.core.node.HumanTaskNode;
import org.drools.workflow.core.node.WorkItemNode;

public class HumanTaskNodeInstance extends WorkItemNodeInstance {

    private static final long serialVersionUID = 4L;
    
    private transient SwimlaneContextInstance swimlaneContextInstance;
    
    public HumanTaskNode getHumanTaskNode() {
        return (HumanTaskNode) getNode();
    }
    
    protected WorkItem createWorkItem(WorkItemNode workItemNode) {
        WorkItem workItem = super.createWorkItem(workItemNode);
        String actorId = assignWorkItem();
        if (actorId != null) {
            ((WorkItemImpl) workItem).setParameter("ActorId", actorId);
        }
        return workItem;
    }
    
    protected String assignWorkItem() {
        String actorId = null;
        // if this human task node is part of a swim lane, check whether an actor
        // has already been assigned to this swim lane
        String swimlaneName = getHumanTaskNode().getSwimlane();
        SwimlaneContextInstance swimlaneContextInstance = getSwimlaneContextInstance(swimlaneName);
        if (swimlaneContextInstance != null) {
            actorId = swimlaneContextInstance.getActorId(swimlaneName);
        }
        if (actorId == null) {
            // if the actorId has not yet been assigned, check whether assigners are
            // defined for this human task node
            // TODO
        }
        if (actorId == null) {
            // if the actorId has not yet been assigned, check whether assigners are
            // defined for this swim lane
        }
        return actorId;
    }
    
    private SwimlaneContextInstance getSwimlaneContextInstance(String swimlaneName) {
        if (this.swimlaneContextInstance == null) {
            if (swimlaneName == null) {
                return null;
            }
            SwimlaneContextInstance swimlaneContextInstance =
                (SwimlaneContextInstance) resolveContextInstance(
                    SwimlaneContext.SWIMLANE_SCOPE, swimlaneName);
            if (swimlaneContextInstance == null) {
                throw new IllegalArgumentException(
                    "Could not find swimlane context instance");
            }
            this.swimlaneContextInstance = swimlaneContextInstance;
        }
        return this.swimlaneContextInstance;
    }
    
    public void triggerCompleted(WorkItem workItem) {
        String swimlaneName = getHumanTaskNode().getSwimlane();
        SwimlaneContextInstance swimlaneContextInstance = getSwimlaneContextInstance(swimlaneName);
        if (swimlaneContextInstance != null) {
            String oldActorId = swimlaneContextInstance.getActorId(swimlaneName);
            // only assign if swimlane has not already been assigned to an actor
            if (oldActorId == null) {
                String newActorId = (String) workItem.getResult("ActorId");
                if (newActorId != null) {
                    swimlaneContextInstance.setActorId(swimlaneName, newActorId);
                }
            }
        }
        super.triggerCompleted(workItem);
    }
}
