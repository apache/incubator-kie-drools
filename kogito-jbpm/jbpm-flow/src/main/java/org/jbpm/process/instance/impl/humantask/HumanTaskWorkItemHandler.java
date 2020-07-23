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

import java.util.Map;
import java.util.stream.Stream;

import org.drools.core.process.instance.WorkItemHandler;
import org.jbpm.process.instance.impl.workitem.Abort;
import org.jbpm.process.instance.impl.workitem.Active;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.kogito.process.workitem.LifeCycle;
import org.kie.kogito.process.workitem.LifeCyclePhase;
import org.kie.kogito.process.workitem.Transition;

/**
 * Work item handler to be used with human tasks (work items).
 * It uses <code>BaseHumanTaskLifeCycle</code> by default but allows to plug in
 * another life cycle implementation.
 *
 */
public class HumanTaskWorkItemHandler implements WorkItemHandler {

    private LifeCycle<Map<String, Object>> lifeCycle;

    public HumanTaskWorkItemHandler() {
        this(new BaseHumanTaskLifeCycle());
    }

    public HumanTaskWorkItemHandler(LifeCycle<Map<String, Object>> lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        lifeCycle.transitionTo(workItem, manager, new HumanTaskTransition(Active.ID));
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        lifeCycle.transitionTo(workItem, manager, new HumanTaskTransition(Abort.ID));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void transitionToPhase(WorkItem workItem, WorkItemManager manager, Transition<?> transition) {
        lifeCycle.transitionTo(workItem, manager, (Transition<Map<String, Object>>) transition);
    }
    
    @Override
    public Stream<LifeCyclePhase> allowedPhases(String phaseId) {
        return lifeCycle.allowedPhases(phaseId);
    }

}
