/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.event;

import org.kie.api.event.process.ProcessWorkItemTransitionEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.process.workitem.Transition;

public class KogitoProcessWorkItemTransitionEventImpl extends ProcessEvent implements ProcessWorkItemTransitionEvent {

    private static final long serialVersionUID = 510l;

    private WorkItem workItem;
    private Transition<?> transition;

    private boolean transitioned;

    public KogitoProcessWorkItemTransitionEventImpl( final ProcessInstance instance, WorkItem workItem, Transition<?> transition, KieRuntime kruntime, boolean transitioned) {
        super( instance, kruntime );
        this.workItem = workItem;
        this.transition = transition;
        this.transitioned = transitioned;
    }

    public String toString() {
        return "==>[WorkItemTransition(id=" + getWorkItem().getId() + " phase=" + getTransition().phase() + ")]";
    }

    @Override
    public WorkItem getWorkItem() {
        return workItem;
    }

    @Override
    public Transition<?> getTransition() {
        return transition;
    }

    @Override
    public boolean isTransitioned() {
        return transitioned;
    }

}
