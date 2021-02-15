/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.process.workitem.LifeCyclePhase;
import org.kie.kogito.process.workitem.Transition;

/**
 * Release life cycle phase that applies to human tasks.
 * It will set the status to "Ready" and resets actual owner
 *
 * It can transition from
 * <ul>
 *  <li>Claim</li>
 * </ul>
 */
public class Release implements LifeCyclePhase {

    public static final String ID = "release";
    public static final String STATUS = "Ready";
    
    private List<String> allowedTransitions = Arrays.asList(Claim.ID);
    
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
        
        ((HumanTaskWorkItemImpl) workitem).setActualOwner(null);     
        
    }
}
