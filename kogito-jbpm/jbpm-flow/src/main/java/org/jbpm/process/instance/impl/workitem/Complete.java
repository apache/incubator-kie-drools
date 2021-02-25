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
package org.jbpm.process.instance.impl.workitem;

import java.util.Arrays;
import java.util.List;

import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.humantask.phases.Release;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitem.LifeCyclePhase;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;

/**
 * Complete life cycle phase that applies to any human task.
 * It will set the status to "Completed"
 *
 * It can transition from
 * <ul>
 * <li>Active</li>
 * <li>Claim</li>
 * <li>Release</li>
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
    public void apply(KogitoWorkItem workitem, Transition<?> transition) {
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
