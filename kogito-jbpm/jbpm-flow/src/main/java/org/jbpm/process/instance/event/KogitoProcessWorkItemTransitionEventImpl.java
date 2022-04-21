/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.event;

import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.event.ProcessWorkItemTransitionEvent;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.workitem.Transition;

public class KogitoProcessWorkItemTransitionEventImpl extends ProcessEvent
        implements ProcessWorkItemTransitionEvent {
    private static final long serialVersionUID = 510l;

    private KogitoWorkItem workItem;
    private Transition<?> transition;
    private boolean transitioned;

    public KogitoProcessWorkItemTransitionEventImpl(final ProcessInstance instance, KogitoWorkItem workItem, Transition<?> transition, KieRuntime kruntime, boolean transitioned) {
        super(instance, kruntime);
        this.workItem = workItem;
        this.transition = transition;
        this.transitioned = transitioned;
    }

    @Override
    public KogitoWorkItem getWorkItem() {
        return workItem;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("==>[WorkItemTransition(id=" + getWorkItem().getStringId());
        if (transition != null) {
            sb.append(" phase=" + transition.phase() + ")]");
        }
        return sb.toString();
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
