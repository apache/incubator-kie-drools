/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance.impl.humantask;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.humantask.phases.Release;
import org.jbpm.process.instance.impl.humantask.phases.Skip;
import org.jbpm.process.instance.impl.workitem.Abort;
import org.jbpm.process.instance.impl.workitem.Active;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.kogito.process.workitem.InvalidLifeCyclePhaseException;
import org.kie.kogito.process.workitem.InvalidTransitionException;
import org.kie.kogito.process.workitem.LifeCycle;
import org.kie.kogito.process.workitem.LifeCyclePhase;
import org.kie.kogito.process.workitem.NotAuthorizedException;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base life cycle definition for human tasks. It comes with following phases
 * 
 * <ul>
 *  <li>Active</li>
 *  <li>Claim</li>
 *  <li>Release</li>
 *  <li>Complete</li>
 *  <li>Skip</li>
 *  <li>Abort</li>
 * </ul>
 * At the beginning human task enters <pre>Active</pre> phase. From there it can go to
 * 
 * <ul>
 *  <li>Claim</li>
 *  <li>Complete</li>
 *  <li>Skip</li>
 *  <li>Abort</li>
 * </ul>
 * 
 * at any time. At each phase data can be associated and by that set on work item.
 */
public class BaseHumanTaskLifeCycle implements LifeCycle<Map<String, Object>> {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseHumanTaskLifeCycle.class);
    
    private Map<String, LifeCyclePhase> phases = new LinkedHashMap<>();
        
    public BaseHumanTaskLifeCycle() {
        phases.put(Claim.ID, new Claim());
        phases.put(Release.ID, new Release());
        phases.put(Complete.ID, new Complete());
        phases.put(Skip.ID, new Skip());
        phases.put(Active.ID, new Active());
        phases.put(Abort.ID, new Abort());
    }

    @Override
    public LifeCyclePhase phaseById(String phaseId) {        
        return phases.get(phaseId);
    }
    
    @Override
    public Collection<LifeCyclePhase> phases() {
        return phases.values();
    }

    @Override
    public Map<String, Object> transitionTo(WorkItem workItem, WorkItemManager manager, Transition<Map<String, Object>> transition) {
        logger.debug("Transition method invoked for work item {} to transition to {}, currently in phase {} and status {}", workItem.getId(), transition.phase(), workItem.getPhaseId(), workItem.getPhaseStatus());
        
        HumanTaskWorkItemImpl humanTaskWorkItem = (HumanTaskWorkItemImpl) workItem;
        
        LifeCyclePhase targetPhase = phases.get(transition.phase());
        if (targetPhase == null) {
            logger.debug("Target life cycle phase '{}' does not exist in {}", transition.phase(), this.getClass().getSimpleName());
            throw new InvalidLifeCyclePhaseException(transition.phase());
        }
        
        LifeCyclePhase currentPhase = phases.get(humanTaskWorkItem.getPhaseId());
        
        if (!targetPhase.canTransition(currentPhase)) {
            logger.debug("Target life cycle phase '{}' cannot transition from current state '{}'", targetPhase.id(), currentPhase.id());
            throw new InvalidTransitionException("Cannot transition from " + humanTaskWorkItem.getPhaseId() + " to " + targetPhase.id()); 
        }

        if (!humanTaskWorkItem.enforce(transition.policies().toArray(new Policy[transition.policies().size()]))) {
            throw new NotAuthorizedException("User is not authorized to access task instance with id " + humanTaskWorkItem.getId());
        }
        
        humanTaskWorkItem.setPhaseId(targetPhase.id());
        humanTaskWorkItem.setPhaseStatus(targetPhase.status());
        
        targetPhase.apply(humanTaskWorkItem, transition);
        if (transition.data() != null) {
            logger.debug("Updating data for work item {}", targetPhase.id(), humanTaskWorkItem.getId());
            humanTaskWorkItem.getResults().putAll(transition.data());
        }
        logger.debug("Transition for work item {} to {} done, currently in phase {} and status {}", workItem.getId(), transition.phase(), workItem.getPhaseId(), workItem.getPhaseStatus());
        
        if (targetPhase.isTerminating()) {
            logger.debug("Target life cycle phase '{}' is terminiating, completing work item {}", targetPhase.id(), humanTaskWorkItem.getId());
            // since target life cycle phase is terminating completing work item
            ((org.drools.core.process.instance.WorkItemManager)manager).internalCompleteWorkItem(humanTaskWorkItem);
        }
        
        return data(humanTaskWorkItem);
    }

    @Override
    public Map<String, Object> data(WorkItem workItem) {
        
        return ((HumanTaskWorkItemImpl) workItem).getResults();
    }

}
