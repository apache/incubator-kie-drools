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
package org.jbpm.process.instance;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.drools.core.process.instance.WorkItem;
import org.jbpm.process.instance.impl.workitem.Abort;
import org.jbpm.process.instance.impl.workitem.Active;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.WorkItemNotFoundException;
import org.kie.kogito.process.workitem.NotAuthorizedException;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;
import org.kie.kogito.process.workitems.InternalKogitoWorkItemManager;
import org.kie.kogito.process.workitems.KogitoWorkItemHandlerNotFoundException;
import org.kie.kogito.process.workitems.impl.KogitoWorkItemImpl;
import org.kie.kogito.signal.SignalManager;

import static org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemHandler.transitionToPhase;
import static org.jbpm.process.instance.impl.workitem.Abort.ID;
import static org.jbpm.process.instance.impl.workitem.Abort.STATUS;
import static org.kie.api.runtime.process.WorkItem.ABORTED;
import static org.kie.api.runtime.process.WorkItem.COMPLETED;

public class LightWorkItemManager implements InternalKogitoWorkItemManager {

    private Map<String, InternalKogitoWorkItem> workItems = new ConcurrentHashMap<>();
    private Map<String, KogitoWorkItemHandler> workItemHandlers = new HashMap<>();

    private final ProcessInstanceManager processInstanceManager;
    private final SignalManager signalManager;
    private final KogitoProcessEventSupport eventSupport;

    private Complete completePhase = new Complete();
    private Abort abortPhase = new Abort();

    public LightWorkItemManager(ProcessInstanceManager processInstanceManager, SignalManager signalManager, KogitoProcessEventSupport eventSupport) {
        this.processInstanceManager = processInstanceManager;
        this.signalManager = signalManager;
        this.eventSupport = eventSupport;
    }

    @Override
    public void internalExecuteWorkItem(InternalKogitoWorkItem workItem) {
        ((KogitoWorkItemImpl) workItem).setId(UUID.randomUUID().toString());
        internalAddWorkItem(workItem);
        KogitoWorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
        if (handler != null) {
            KogitoProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceStringId());
            Transition<?> transition = new TransitionToActive();
            eventSupport.fireBeforeWorkItemTransition(processInstance, workItem, transition, null);

            handler.executeWorkItem(workItem, this);

            eventSupport.fireAfterWorkItemTransition(processInstance, workItem, transition, null);
        } else {
            throw new KogitoWorkItemHandlerNotFoundException(workItem.getName());
        }
    }

    @Override
    public void internalAddWorkItem(InternalKogitoWorkItem workItem) {
        workItems.put(workItem.getStringId(), workItem);
    }

    @Override
    public void internalAbortWorkItem(String id) {
        KogitoWorkItemImpl workItem = (KogitoWorkItemImpl) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            workItem.setCompleteDate(new Date());
            KogitoWorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {

                KogitoProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceStringId());
                Transition<?> transition = new TransitionToAbort(Collections.emptyList());
                eventSupport.fireBeforeWorkItemTransition(processInstance, workItem, transition, null);

                handler.abortWorkItem(workItem, this);
                workItem.setPhaseId(ID);
                workItem.setPhaseStatus(STATUS);
                eventSupport.fireAfterWorkItemTransition(processInstance, workItem, transition, null);
            } else {
                workItems.remove(workItem.getStringId());
                throw new KogitoWorkItemHandlerNotFoundException(workItem.getName());
            }
            workItems.remove(workItem.getStringId());
        }
    }

    public void retryWorkItem(String workItemId) {
        InternalKogitoWorkItem workItem = workItems.get(workItemId);
        retryWorkItem(workItem);
    }

    public void retryWorkItemWithParams(String workItemId, Map<String, Object> map) {
        InternalKogitoWorkItem workItem = workItems.get(workItemId);

        if (workItem != null) {
            workItem.setParameters(map);

            retryWorkItem(workItem);
        }
    }

    private void retryWorkItem(InternalKogitoWorkItem workItem) {
        if (workItem != null) {
            KogitoWorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.executeWorkItem(workItem, this);
            } else
                throw new KogitoWorkItemHandlerNotFoundException(workItem.getName());
        }
    }

    @Override
    public InternalKogitoWorkItem getWorkItem(String id) {
        return workItems.get(id);
    }

    @Override
    public void internalRemoveWorkItem(String id) {
        workItems.remove(id);
    }

    @Override
    public void completeWorkItem(String id, Map<String, Object> results, Policy<?>... policies) {
        transitionWorkItem(id, new TransitionToComplete(results, Arrays.asList(policies)));
    }

    @Override
    public <T> T updateWorkItem(String id,
            Function<org.kie.kogito.internal.process.runtime.KogitoWorkItem, T> updater,
            Policy<?>... policies) {
        InternalKogitoWorkItem workItem = workItems.get(id);
        if (workItem != null) {
            if (!workItem.enforce(policies)) {
                throw new NotAuthorizedException("User is not authorized to access task instance with id " + id);
            }
            T results = updater.apply(workItem);
            eventSupport.fireAfterWorkItemTransition(processInstanceManager.getProcessInstance(workItem
                    .getProcessInstanceStringId()), workItem, null, null);
            return results;
        } else {
            throw new WorkItemNotFoundException(id);
        }
    }

    @Override
    public void internalCompleteWorkItem(InternalKogitoWorkItem workItem) {
        KogitoProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceStringId());
        workItem.setState(COMPLETED);
        workItem.setCompleteDate(new Date());

        // process instance may have finished already
        if (processInstance != null) {
            processInstance.signalEvent("workItemCompleted", workItem);
        }
        workItems.remove(workItem.getStringId());

    }

    @Override
    public void transitionWorkItem(String id, Transition<?> transition) {
        InternalKogitoWorkItem workItem = workItems.get(id);
        if (workItem != null) {
            transitionWorkItem(workItem, transition);
        } else {
            throw new WorkItemNotFoundException("Work Item (" + id + ") does not exist", id);
        }
    }

    @SuppressWarnings("unchecked")
    private void transitionWorkItem(InternalKogitoWorkItem workItem, Transition<?> transition) {
        // work item may have been aborted
        KogitoWorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
        if (handler != null) {
            KogitoProcessInstance processInstance = processInstanceManager
                    .getProcessInstance(workItem.getProcessInstanceStringId());
            eventSupport.fireBeforeWorkItemTransition(processInstance, workItem, transition, null);

            if (!transitionToPhase(handler, workItem, this, transition)) {
                workItem.setResults((Map<String, Object>) transition.data());
                workItem.setPhaseId(Complete.ID);
                workItem.setPhaseStatus(Complete.STATUS);
                completePhase.apply(workItem, transition);
                internalCompleteWorkItem(workItem);
            }
            processInstance.signalEvent("workItemTransition", transition);
            eventSupport.fireAfterWorkItemTransition(processInstance, workItem, transition, null);
        } else {
            throw new KogitoWorkItemHandlerNotFoundException(workItem.getName());
        }
    }

    @Override
    public void abortWorkItem(String id, Policy<?>... policies) {
        KogitoWorkItemImpl workItem = (KogitoWorkItemImpl) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            if (!workItem.enforce(policies)) {
                throw new NotAuthorizedException("Work item can be aborted as it does not fulfil policies (e.g. security)");
            }
            KogitoProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceStringId());
            Transition<?> transition = new TransitionToAbort(Arrays.asList(policies));
            eventSupport.fireBeforeWorkItemTransition(processInstance, workItem, transition, null);
            workItem.setState(ABORTED);
            abortPhase.apply(workItem, transition);

            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemAborted", workItem);
            }
            workItem.setPhaseId(ID);
            workItem.setPhaseStatus(STATUS);
            eventSupport.fireAfterWorkItemTransition(processInstance, workItem, transition, null);
            workItems.remove(id);
        }
    }

    @Override
    public void completeWorkItem(long l, Map<String, Object> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abortWorkItem(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerWorkItemHandler(String workItemName, KogitoWorkItemHandler handler) {
        this.workItemHandlers.put(workItemName, handler);
    }

    @Override
    public void clear() {
        this.workItems.clear();
    }

    @Override
    public void signalEvent(String type, Object event) {
        this.signalManager.signalEvent(type, event);
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void signalEvent(String type, Object event, String processInstanceId) {
        this.signalManager.signalEvent(processInstanceId, type, event);
    }

    @Override
    public void retryWorkItem(String workItemID, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            retryWorkItem(workItemID);
        } else {
            this.retryWorkItemWithParams(workItemID, params);
        }

    }

    @Override
    public Set<WorkItem> getWorkItems() {
        throw new UnsupportedOperationException();
    }

    private static class TransitionToActive implements Transition<Void> {

        @Override
        public String phase() {
            return Active.ID;
        }

        @Override
        public Void data() {
            return null;
        }

        @Override
        public List<Policy<?>> policies() {
            return Collections.emptyList();
        }
    }

    private static class TransitionToAbort implements Transition<Void> {

        private List<Policy<?>> policies;

        TransitionToAbort(List<Policy<?>> policies) {
            this.policies = policies;
        }

        @Override
        public String phase() {
            return ID;
        }

        @Override
        public Void data() {
            return null;
        }

        @Override
        public List<Policy<?>> policies() {
            return policies;
        }
    }

    private static class TransitionToComplete implements Transition<Map<String, Object>> {
        private Map<String, Object> data;
        private List<Policy<?>> policies;

        TransitionToComplete(Map<String, Object> data, List<Policy<?>> policies) {
            this.data = data;
            this.policies = policies;
        }

        @Override
        public String phase() {
            return Complete.ID;
        }

        @Override
        public Map<String, Object> data() {
            return data;
        }

        @Override
        public List<Policy<?>> policies() {
            return policies;
        }
    }

}
