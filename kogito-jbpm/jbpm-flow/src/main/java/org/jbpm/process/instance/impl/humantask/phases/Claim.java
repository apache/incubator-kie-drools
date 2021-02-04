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

package org.jbpm.process.instance.impl.humantask.phases;

import java.util.Arrays;
import java.util.List;

import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.process.instance.impl.workitem.Active;
import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitem.LifeCyclePhase;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;

/**
 * Claim life cycle phase that applies to human tasks.
 * It will set the status to "Reserved" and assign actual owner if there is security
 * context available.
 *
 * It can transition from
 * <ul>
 *  <li>Active</li>
 * </ul>
 */
public class Claim implements LifeCyclePhase {

    public static final String ID = "claim";
    public static final String STATUS = "Reserved";
    
    private List<String> allowedTransitions = Arrays.asList(Active.ID, Release.ID);
    
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
        return allowedTransitions.contains(phase.id());        
    }

    @Override
    public void apply(WorkItem workitem, Transition<?> transition) {
        
        if (transition.policies() != null) {
            for (Policy<?> policy : transition.policies()) {
                if (policy instanceof SecurityPolicy) {
                    ((HumanTaskWorkItemImpl) workitem).setActualOwner(((SecurityPolicy)policy).value().getName());
                    break;
                }
            }
        }
        workitem.getResults().put("ActorId", (( HumanTaskWorkItem ) workitem).getActualOwner());
    }

}
