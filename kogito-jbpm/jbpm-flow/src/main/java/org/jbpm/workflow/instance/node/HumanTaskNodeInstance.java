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

import java.util.Set;

import org.drools.core.process.instance.WorkItem;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.instance.context.swimlane.SwimlaneContextInstance;
import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.runtime.process.HumanTaskWorkItem;

public class HumanTaskNodeInstance extends WorkItemNodeInstance {

    private static final long serialVersionUID = 510l;
    private String separator = System.getProperty("org.jbpm.ht.user.separator", ",");
    
    private static final String ACTOR_ID = "ActorId";
    private static final String GROUP_ID = "GroupId";
    private static final String BUSINESSADMINISTRATOR_ID = "BusinessAdministratorId";
    private static final String BUSINESSADMINISTRATOR_GROUP_ID = "BusinessAdministratorGroupId";
    private static final String EXCLUDED_OWNER_ID = "ExcludedOwnerId";

    private transient SwimlaneContextInstance swimlaneContextInstance;
    
    public HumanTaskNode getHumanTaskNode() {
        return (HumanTaskNode) getNode();
    }
    
    @Override
    protected WorkItem newWorkItem() {
        return new HumanTaskWorkItemImpl();
    }

    protected WorkItem createWorkItem(WorkItemNode workItemNode) {
        HumanTaskWorkItemImpl workItem = (HumanTaskWorkItemImpl)super.createWorkItem(workItemNode);
        String actorId = assignWorkItem(workItem);
        if (actorId != null) {
            workItem.setParameter(ACTOR_ID, actorId);
        }
        
        workItem.setTaskName((String) workItem.getParameter("NodeName"));
        workItem.setTaskDescription((String) workItem.getParameter("Description"));
        workItem.setTaskPriority((String) workItem.getParameter("Priority"));
        workItem.setReferenceName((String) workItem.getParameter("TaskName"));
        
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
        	actorId = (String) workItem.getParameter(ACTOR_ID);
        	if (actorId != null && swimlaneContextInstance != null && actorId.split(separator).length == 1) {
        		swimlaneContextInstance.setActorId(swimlaneName, actorId);
        		workItem.setParameter("SwimlaneActorId", actorId);
        	}
        }
        
        processAssigment(ACTOR_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getPotentialUsers());
        processAssigment(GROUP_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getPotentialGroups());
        processAssigment(EXCLUDED_OWNER_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getExcludedUsers());
        processAssigment(BUSINESSADMINISTRATOR_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getAdminUsers());
        processAssigment(BUSINESSADMINISTRATOR_GROUP_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getAdminGroups());
        
        // always return ActorId from workitem as SwimlaneActorId is kept as separate parameter
        return (String) workItem.getParameter(ACTOR_ID);
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
            String newActorId = (workItem instanceof HumanTaskWorkItem) ? ((HumanTaskWorkItem) workItem).getActualOwner() : (String)workItem.getParameter(ACTOR_ID);
            if (newActorId != null) {
                swimlaneContextInstance.setActorId(swimlaneName, newActorId);
            }
        }
        super.triggerCompleted(workItem);
    }
    
    protected void processAssigment(String type, WorkItem workItem, Set<String> store) {
        
        String value = (String) workItem.getParameter(type);
        
        if (value != null) {
            for (String item : value.split(separator)) {
                store.add(item);
            }
        }
    }
}
