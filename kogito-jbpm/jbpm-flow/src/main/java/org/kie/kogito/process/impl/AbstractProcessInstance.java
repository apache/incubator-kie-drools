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
package org.kie.kogito.process.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;
import org.kie.kogito.Model;
import org.kie.kogito.internal.process.event.KogitoEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.WorkItemNotFoundException;
import org.kie.kogito.process.EventDescription;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.NodeInstanceNotFoundException;
import org.kie.kogito.process.NodeNotFoundException;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.Signal;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.flexible.AdHocFragment;
import org.kie.kogito.process.flexible.Milestone;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;
import org.kie.kogito.services.uow.ProcessInstanceWorkUnit;

public abstract class AbstractProcessInstance<T extends Model> implements ProcessInstance<T> {

    private static final String KOGITO_PROCESS_INSTANCE = "KogitoProcessInstance";

    protected final T variables;
    protected final AbstractProcess<T> process;
    protected InternalProcessRuntime rt;
    protected WorkflowProcessInstance processInstance;

    protected Integer status;
    protected String id;
    protected CorrelationKey correlationKey;
    protected String description;

    protected ProcessError processError;

    protected Consumer<AbstractProcessInstance<?>> reloadSupplier;

    protected CompletionEventListener completionEventListener;

    protected long version;

    public AbstractProcessInstance(AbstractProcess<T> process, T variables, ProcessRuntime rt) {
        this(process, variables, null, rt);
    }

    public AbstractProcessInstance(AbstractProcess<T> process, T variables, String businessKey, ProcessRuntime rt) {
        this.process = process;
        this.rt = (InternalProcessRuntime) rt;
        this.variables = variables;

        setCorrelationKey(businessKey);

        Map<String, Object> map = bind(variables);
        String processId = process.process().getId();
        syncProcessInstance((WorkflowProcessInstance) ((CorrelationAwareProcessRuntime) rt).createProcessInstance(processId, correlationKey, map));
        processInstance.setMetaData(KOGITO_PROCESS_INSTANCE, this);
    }

    /**
     * Without providing a ProcessRuntime the ProcessInstance can only be used as read-only
     * 
     * @param process
     * @param variables
     * @param wpi
     */
    public AbstractProcessInstance(AbstractProcess<T> process, T variables, org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        this.process = process;
        this.variables = variables;
        syncProcessInstance((WorkflowProcessInstance) wpi);
        unbind(variables, processInstance.getVariables());
    }

    public AbstractProcessInstance(AbstractProcess<T> process, T variables, ProcessRuntime rt, org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        this.process = process;
        this.rt = (InternalProcessRuntime) rt;
        this.variables = variables;
        syncProcessInstance((WorkflowProcessInstance) wpi);
        reconnect();
    }

    protected void reconnect() {
        if (processInstance.getKnowledgeRuntime() == null) {
            processInstance.setKnowledgeRuntime(getProcessRuntime().getInternalKieRuntime());
        }
        getProcessRuntime().getProcessInstanceManager().setLock(((MutableProcessInstances<T>) process.instances()).lock());
        processInstance.reconnect();
        processInstance.setMetaData(KOGITO_PROCESS_INSTANCE, this);
        addCompletionEventListener();

        for (org.kie.api.runtime.process.NodeInstance nodeInstance : processInstance.getNodeInstances()) {
            if (nodeInstance instanceof WorkItemNodeInstance) {
                ((WorkItemNodeInstance) nodeInstance).internalRegisterWorkItem();
            }
        }

        unbind(variables, processInstance.getVariables());
    }

    private void addCompletionEventListener() {
        if (completionEventListener == null) {
            completionEventListener = new CompletionEventListener();
            processInstance.addEventListener("processInstanceCompleted:" + id, completionEventListener, false);
        }
    }

    private void removeCompletionListener() {
        if (completionEventListener != null) {
            processInstance.removeEventListener("processInstanceCompleted:" + id, completionEventListener, false);
            completionEventListener = null;
        }
    }

    protected void disconnect() {
        if (processInstance == null) {
            return;
        }

        for (org.kie.api.runtime.process.NodeInstance nodeInstance : processInstance.getNodeInstances()) {
            if (nodeInstance instanceof WorkItemNodeInstance) {
                ((WorkItemNodeInstance) nodeInstance).internalRemoveWorkItem();
            }
        }

        processInstance.disconnect();
        processInstance.setMetaData(KOGITO_PROCESS_INSTANCE, null);
    }

    private void syncProcessInstance(WorkflowProcessInstance wpi) {
        processInstance = wpi;
        status = wpi.getState();
        id = wpi.getStringId();
        description = wpi.getDescription();
        setCorrelationKey(wpi.getCorrelationKey());
    }

    private void setCorrelationKey(String businessKey) {
        if (businessKey != null && !businessKey.trim().isEmpty()) {
            correlationKey = new StringCorrelationKey(businessKey);
        }
    }

    public WorkflowProcessInstance internalGetProcessInstance() {
        return processInstance;
    }

    public void internalSetProcessInstance(WorkflowProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    public void internalRemoveProcessInstance(Consumer<AbstractProcessInstance<?>> reloadSupplier) {
        this.reloadSupplier = reloadSupplier;
        this.status = processInstance.getState();
        if (this.status == STATE_ERROR) {
            this.processError = buildProcessError();
        }
        removeCompletionListener();
        if (processInstance.getKnowledgeRuntime() != null) {
            disconnect();
        }
        processInstance = null;
    }

    @Override
    public void start() {
        start(null, null);
    }

    @Override
    public void start(String trigger, String referenceId) {
        if (this.status != KogitoProcessInstance.STATE_PENDING) {
            throw new IllegalStateException("Impossible to start process instance that already was started");
        }
        this.status = KogitoProcessInstance.STATE_ACTIVE;

        if (referenceId != null) {
            processInstance.setReferenceId(referenceId);
        }

        getProcessRuntime().getProcessInstanceManager().setLock(((MutableProcessInstances<T>) process.instances()).lock());
        getProcessRuntime().getProcessInstanceManager().addProcessInstance(this.processInstance);
        this.id = processInstance.getStringId();
        addCompletionEventListener();
        ((MutableProcessInstances<T>) process.instances()).create(id, this);
        KogitoProcessInstance kogitoProcessInstance = getProcessRuntime().getKogitoProcessRuntime().startProcessInstance(this.id, trigger);
        if (kogitoProcessInstance.getState() != STATE_ABORTED && kogitoProcessInstance.getState() != STATE_COMPLETED) {
            addToUnitOfWork(pi -> ((MutableProcessInstances<T>) process.instances()).update(pi.id(), pi));
        }
        unbind(variables, kogitoProcessInstance.getVariables());
        if (this.processInstance != null) {
            this.status = this.processInstance.getState();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void addToUnitOfWork(Consumer<ProcessInstance<T>> action) {
        getProcessRuntime().getUnitOfWorkManager().currentUnitOfWork().intercept(new ProcessInstanceWorkUnit(this, action));
    }

    @Override
    public void abort() {
        String pid = processInstance().getStringId();
        unbind(variables, processInstance().getVariables());
        getProcessRuntime().getKogitoProcessRuntime().abortProcessInstance(pid);
        this.status = processInstance.getState();
        addToUnitOfWork(pi -> ((MutableProcessInstances<T>) process.instances()).remove(pi.id()));
    }

    private InternalProcessRuntime getProcessRuntime() {
        if (rt == null) {
            throw new UnsupportedOperationException("Process instance is not connected to a Process Runtime");
        } else {
            return rt;
        }
    }

    @Override
    public <S> void send(Signal<S> signal) {
        if (signal.referenceId() != null) {
            processInstance().setReferenceId(signal.referenceId());
        }
        processInstance().signalEvent(signal.channel(), signal.payload());
        removeOnFinish();
    }

    @Override
    public Process<T> process() {
        return process;
    }

    @Override
    public T variables() {
        return variables;
    }

    @Override
    public int status() {
        return this.status;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String businessKey() {
        return this.correlationKey == null ? null : this.correlationKey.getName();
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public Date startDate() {
        return this.processInstance != null ? this.processInstance.getStartDate() : null;
    }

    @Override
    public long version() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public T updateVariables(T updates) {
        Map<String, Object> map = bind(updates);

        for (Entry<String, Object> entry : map.entrySet()) {
            processInstance().setVariable(entry.getKey(), entry.getValue());
        }
        this.variables.update(map);
        addToUnitOfWork(pi -> ((MutableProcessInstances<T>) process.instances()).update(pi.id(), pi));
        return variables;
    }

    @Override
    public Optional<ProcessError> error() {
        if (this.status == STATE_ERROR) {
            return Optional.of(this.processError != null ? this.processError : buildProcessError());
        }

        return Optional.empty();
    }

    @Override
    public void startFrom(String nodeId) {
        startFrom(nodeId, null);
    }

    @Override
    public void startFrom(String nodeId, String referenceId) {
        processInstance.setStartDate(new Date());
        processInstance.setState(STATE_ACTIVE);
        getProcessRuntime().getProcessInstanceManager().addProcessInstance(this.processInstance);
        this.id = processInstance.getStringId();
        addCompletionEventListener();
        if (referenceId != null) {
            processInstance.setReferenceId(referenceId);
        }
        triggerNode(nodeId);
        unbind(variables, processInstance.getVariables());
        if (processInstance != null) {
            this.status = processInstance.getState();
        }
    }

    @Override
    public void triggerNode(String nodeId) {
        WorkflowProcessInstance wfpi = processInstance();
        RuleFlowProcess rfp = ((RuleFlowProcess) wfpi.getProcess());

        org.kie.api.definition.process.Node node = rfp.getNodesRecursively()
                .stream()
                .filter(ni -> nodeId.equals(ni.getMetaData().get("UniqueId"))).findFirst().orElseThrow(() -> new NodeNotFoundException(this.id, nodeId));

        org.kie.api.definition.process.Node parentNode = rfp.getParentNode(node.getId());

        NodeInstanceContainer nodeInstanceContainerNode = parentNode == null ? wfpi : ((NodeInstanceContainer) wfpi.getNodeInstance(parentNode));

        nodeInstanceContainerNode.getNodeInstance(node).trigger(null, Node.CONNECTION_DEFAULT_TYPE);

        addToUnitOfWork(pi -> ((MutableProcessInstances<T>) process.instances()).update(pi.id(), pi));
    }

    @Override
    public void cancelNodeInstance(String nodeInstanceId) {
        NodeInstance nodeInstance = processInstance()
                .getNodeInstances(true)
                .stream()
                .filter(ni -> ni.getStringId().equals(nodeInstanceId))
                .findFirst()
                .orElseThrow(() -> new NodeInstanceNotFoundException(this.id, nodeInstanceId));

        nodeInstance.cancel();
        removeOnFinish();
    }

    @Override
    public void retriggerNodeInstance(String nodeInstanceId) {
        NodeInstance nodeInstance = processInstance()
                .getNodeInstances(true)
                .stream()
                .filter(ni -> ni.getStringId().equals(nodeInstanceId))
                .findFirst()
                .orElseThrow(() -> new NodeInstanceNotFoundException(this.id, nodeInstanceId));

        ((NodeInstanceImpl) nodeInstance).retrigger(true);
        removeOnFinish();
    }

    protected WorkflowProcessInstance processInstance() {
        if (this.processInstance == null) {
            reloadSupplier.accept(this);
            if (this.processInstance == null) {
                throw new ProcessInstanceNotFoundException(id);
            } else if (getProcessRuntime() != null) {
                reconnect();
            }
        }

        return this.processInstance;
    }

    @Override
    public Collection<KogitoNodeInstance> findNodes(Predicate<KogitoNodeInstance> predicate) {
        return processInstance().getKogitoNodeInstances(predicate, true);
    }

    @Override
    public WorkItem workItem(String workItemId, Policy<?>... policies) {
        WorkItemNodeInstance workItemInstance = (WorkItemNodeInstance) processInstance().getNodeInstances(true)
                .stream()
                .filter(ni -> ni instanceof WorkItemNodeInstance && ((WorkItemNodeInstance) ni).getWorkItemId().equals(workItemId) && ((WorkItemNodeInstance) ni).getWorkItem().enforce(policies))
                .findFirst()
                .orElseThrow(() -> new WorkItemNotFoundException("Work item with id " + workItemId + " was not found in process instance " + id(), workItemId));
        return new BaseWorkItem(workItemInstance.getStringId(),
                workItemInstance.getWorkItem().getStringId(),
                Long.toString(workItemInstance.getNode().getId()),
                (String) workItemInstance.getWorkItem().getParameters().getOrDefault("TaskName", workItemInstance.getNodeName()),
                workItemInstance.getWorkItem().getState(),
                workItemInstance.getWorkItem().getPhaseId(),
                workItemInstance.getWorkItem().getPhaseStatus(),
                workItemInstance.getWorkItem().getParameters(),
                workItemInstance.getWorkItem().getResults());
    }

    @Override
    public List<WorkItem> workItems(Policy<?>... policies) {
        return workItems(WorkItemNodeInstance.class::isInstance, policies);
    }

    @Override
    public List<WorkItem> workItems(Predicate<KogitoNodeInstance> p, Policy<?>... policies) {
        List<WorkItem> list = new ArrayList<>();
        for (NodeInstance ni : processInstance().getNodeInstances(true)) {
            if (p.test(ni) && ((WorkItemNodeInstance) ni).getWorkItem().enforce(policies)) {
                BaseWorkItem taskName = new BaseWorkItem(ni.getStringId(),
                        ((WorkItemNodeInstance) ni).getWorkItemId(),
                        Long.toString(ni.getNode().getId()),
                        (String) ((WorkItemNodeInstance) ni).getWorkItem().getParameters().getOrDefault("TaskName", ni.getNodeName()),
                        ((WorkItemNodeInstance) ni).getWorkItem().getState(),
                        ((WorkItemNodeInstance) ni).getWorkItem().getPhaseId(),
                        ((WorkItemNodeInstance) ni).getWorkItem().getPhaseStatus(),
                        ((WorkItemNodeInstance) ni).getWorkItem().getParameters(),
                        ((WorkItemNodeInstance) ni).getWorkItem().getResults());
                list.add(taskName);
            }
        }
        return list;
    }

    @Override
    public void completeWorkItem(String id, Map<String, Object> variables, Policy<?>... policies) {
        getProcessRuntime().getKogitoProcessRuntime().getKogitoWorkItemManager().completeWorkItem(id, variables, policies);
        removeOnFinish();
    }

    @Override
    public <R> R updateWorkItem(String id, Function<KogitoWorkItem, R> updater, Policy<?>... policies) {
        R result = getProcessRuntime().getKogitoProcessRuntime().getKogitoWorkItemManager().updateWorkItem(id, updater,
                policies);
        addToUnitOfWork(pi -> ((MutableProcessInstances<T>) process.instances()).update(pi.id(), pi));
        return result;
    }

    @Override
    public void abortWorkItem(String id, Policy<?>... policies) {
        getProcessRuntime().getKogitoProcessRuntime().getKogitoWorkItemManager().abortWorkItem(id, policies);
        removeOnFinish();
    }

    @Override
    public void transitionWorkItem(String id, Transition<?> transition) {
        getProcessRuntime().getKogitoProcessRuntime().getKogitoWorkItemManager().transitionWorkItem(id, transition);
        removeOnFinish();
    }

    @Override
    public Set<EventDescription<?>> events() {
        return processInstance().getEventDescriptions();
    }

    @Override
    public Collection<Milestone> milestones() {
        return processInstance.milestones();
    }

    @Override
    public Collection<AdHocFragment> adHocFragments() {
        return processInstance.adHocFragments();
    }

    protected void removeOnFinish() {
        if (processInstance.getState() != KogitoProcessInstance.STATE_ACTIVE && processInstance.getState() != KogitoProcessInstance.STATE_ERROR) {
            removeCompletionListener();
            syncProcessInstance(processInstance);
            addToUnitOfWork(pi -> ((MutableProcessInstances<T>) process.instances()).remove(pi.id()));
        } else {
            addToUnitOfWork(pi -> ((MutableProcessInstances<T>) process.instances()).update(pi.id(), pi));
        }
        unbind(this.variables, processInstance().getVariables());
        this.status = processInstance.getState();
    }

    // this must be overridden at compile time
    protected Map<String, Object> bind(T variables) {
        HashMap<String, Object> vmap = new HashMap<>();
        if (variables == null) {
            return vmap;
        }
        try {
            for (Field f : variables.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object v = f.get(variables);
                vmap.put(f.getName(), v);
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        vmap.put("$v", variables);
        return vmap;
    }

    protected void unbind(T variables, Map<String, Object> vmap) {
        if (vmap == null) {
            return;
        }
        try {
            for (Field f : variables.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                f.set(variables, vmap.get(f.getName()));
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        vmap.put("$v", variables);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractProcessInstance other = (AbstractProcessInstance) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        return true;
    }

    protected ProcessError buildProcessError() {
        WorkflowProcessInstance pi = processInstance();

        final String errorMessage = pi.getErrorMessage();
        final String nodeInError = pi.getNodeIdInError();
        final Throwable errorCause = pi.getErrorCause().orElse(null);
        return new ProcessError() {

            @Override
            public String failedNodeId() {
                return nodeInError;
            }

            @Override
            public String errorMessage() {
                return errorMessage;
            }

            @Override
            public Throwable errorCause() {
                return errorCause;
            }

            @Override
            public void retrigger() {
                WorkflowProcessInstanceImpl pInstance = (WorkflowProcessInstanceImpl) processInstance();
                NodeInstance ni = pInstance.getByNodeDefinitionId(nodeInError, pInstance.getNodeContainer());
                pInstance.setState(STATE_ACTIVE);
                pInstance.internalSetErrorNodeId(null);
                pInstance.internalSetErrorMessage(null);
                ni.trigger(null, Node.CONNECTION_DEFAULT_TYPE);
                removeOnFinish();
            }

            @Override
            public void skip() {
                WorkflowProcessInstanceImpl pInstance = (WorkflowProcessInstanceImpl) processInstance();
                NodeInstance ni = pInstance.getByNodeDefinitionId(nodeInError, pInstance.getNodeContainer());
                pInstance.setState(STATE_ACTIVE);
                pInstance.internalSetErrorNodeId(null);
                pInstance.internalSetErrorMessage(null);
                ((NodeInstanceImpl) ni).triggerCompleted(Node.CONNECTION_DEFAULT_TYPE, true);
                removeOnFinish();
            }
        };
    }

    private class CompletionEventListener implements KogitoEventListener {

        @Override
        public void signalEvent(String type, Object event) {
            removeOnFinish();
        }

        @Override
        public String[] getEventTypes() {
            return new String[] { "processInstanceCompleted:" + processInstance.getStringId() };
        }
    }

    private class StringCorrelationKey implements CorrelationKey {

        private final String correlationKey;

        public StringCorrelationKey(String correlationKey) {
            this.correlationKey = correlationKey;
        }

        @Override
        public String getName() {
            return correlationKey;
        }

        @Override
        public List<CorrelationProperty<?>> getProperties() {
            return Collections.emptyList();
        }

        @Override
        public String toExternalForm() {
            return correlationKey;
        }

    }
}
