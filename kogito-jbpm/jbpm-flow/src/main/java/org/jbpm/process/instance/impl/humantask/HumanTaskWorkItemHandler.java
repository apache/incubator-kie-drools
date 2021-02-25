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
package org.jbpm.process.instance.impl.humantask;

import java.util.Map;
import java.util.stream.Stream;

import org.jbpm.process.instance.impl.workitem.Abort;
import org.jbpm.process.instance.impl.workitem.Active;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.process.workitem.LifeCycle;
import org.kie.kogito.process.workitem.LifeCyclePhase;
import org.kie.kogito.process.workitem.Transition;

/**
 * Work item handler to be used with human tasks (work items).
 * It uses <code>BaseHumanTaskLifeCycle</code> by default but allows to plug in
 * another life cycle implementation.
 *
 */
public class HumanTaskWorkItemHandler implements KogitoWorkItemHandler {

    private final LifeCycle<Map<String, Object>> lifeCycle;

    public HumanTaskWorkItemHandler() {
        this(new BaseHumanTaskLifeCycle());
    }

    public HumanTaskWorkItemHandler(LifeCycle<Map<String, Object>> lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        lifeCycle.transitionTo(workItem, manager, new HumanTaskTransition(Active.ID));
    }

    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        lifeCycle.transitionTo(workItem, manager, new HumanTaskTransition(Abort.ID));
    }

    @SuppressWarnings("unchecked")
    public static boolean transitionToPhase(KogitoWorkItemHandler handler, KogitoWorkItem workItem, KogitoWorkItemManager manager, Transition<?> transition) {
        if (handler instanceof HumanTaskWorkItemHandler) {
            ((HumanTaskWorkItemHandler) handler).lifeCycle.transitionTo(workItem, manager, (Transition<Map<String, Object>>) transition);
            return true;
        }
        return false;
    }

    public static Stream<LifeCyclePhase> allowedPhases(KogitoWorkItemHandler handler, String phaseId) {
        if (handler instanceof HumanTaskWorkItemHandler) {
            return ((HumanTaskWorkItemHandler) handler).lifeCycle.allowedPhases(phaseId);
        }
        return null;
    }

}
