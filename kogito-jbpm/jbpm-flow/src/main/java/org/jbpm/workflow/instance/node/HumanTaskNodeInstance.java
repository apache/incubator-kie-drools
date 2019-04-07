/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.instance.node;

import org.drools.core.process.instance.WorkItem;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.instance.context.swimlane.SwimlaneContextInstance;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.WorkItemNode;

public class HumanTaskNodeInstance extends WorkItemNodeInstance {

    private static final long serialVersionUID = 510l;
    private String separator = System.getProperty("org.jbpm.ht.user.separator", ",");
    
    private transient SwimlaneContextInstance swimlaneContextInstance;
    
    public HumanTaskNode getHumanTaskNode() {
        return (HumanTaskNode) getNode();
    }
    
    protected WorkItem createWorkItem(WorkItemNode workItemNode) {
        WorkItem workItem = super.createWorkItem(workItemNode);
        String actorId = assignWorkItem(workItem);
        if (actorId != null) {
            ((org.drools.core.process.instance.WorkItem) workItem).setParameter("ActorId", actorId);
        }
        return workItem;
    }
    
    protected String assignWorkItem(WorkItem workItem) {
        String actorId = null;
        // if this human task node is part of a swimlane, check whether an actor
        // has already been assigned to this swimlane
        String swimlaneName = getHumanTaskNode().getSwimlane();
        SwimlaneContextInstance swimlaneContextInstance = getSwimlaneContextInstance(swimlaneName);
        if (swimlaneContextInstance != null) {
            actorId = swimlaneContextInstance.getActorId(swimlaneName);
            workItem.setParameter("SwimlaneActorId", actorId);
        }
        // if no actor can be assigned based on the swimlane, check whether an
        // actor is specified for this human task
        if (actorId == null) {
        	actorId = (String) workItem.getParameter("ActorId");
        	if (actorId != null && swimlaneContextInstance != null && actorId.split(separator).length == 1) {
        		swimlaneContextInstance.setActorId(swimlaneName, actorId);
        		workItem.setParameter("SwimlaneActorId", actorId);
        	}
        }
        // always return ActorId from workitem as SwimlaneActorId is kept as separate parameter
        return (String) workItem.getParameter("ActorId");
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
            String newActorId = (String) workItem.getResult("ActorId");
            if (newActorId != null) {
                swimlaneContextInstance.setActorId(swimlaneName, newActorId);
            }
        }
        super.triggerCompleted(workItem);
    }
}
