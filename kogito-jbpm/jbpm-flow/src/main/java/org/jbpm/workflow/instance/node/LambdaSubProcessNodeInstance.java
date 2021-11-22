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
package org.jbpm.workflow.instance.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.core.spi.KogitoProcessContextImpl;
import org.drools.core.util.StringUtils;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.process.instance.impl.ContextInstanceFactoryRegistry;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.SubProcessFactory;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.instance.impl.MVELProcessHelper;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.kie.kogito.internal.process.event.KogitoEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime counterpart of a SubFlow node.
 * 
 */
public class LambdaSubProcessNodeInstance extends StateBasedNodeInstance implements KogitoEventListener, ContextInstanceContainer {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(LambdaSubProcessNodeInstance.class);

    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<>();

    private String processInstanceId;
    private boolean asyncWaitingNodeInstance;

    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void internalTrigger(KogitoNodeInstance from, String type) {
        super.internalTrigger(from, type);
        // if node instance was cancelled, abort
        if (getNodeInstanceContainer().getNodeInstance(getStringId()) == null) {
            return;
        }
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "A SubProcess node only accepts default incoming connections!");
        }

        KogitoProcessContextImpl context = new KogitoProcessContextImpl(getProcessInstance().getKnowledgeRuntime());
        context.setNodeInstance(this);
        SubProcessFactory subProcessFactory = getSubProcessNode().getSubProcessFactory();
        Object o = subProcessFactory.bind(context);
        org.kie.kogito.process.ProcessInstance<?> processInstance = subProcessFactory.createInstance(o);

        org.kie.api.runtime.process.ProcessInstance pi = ((AbstractProcessInstance<?>) processInstance).internalGetProcessInstance();
        ((ProcessInstanceImpl) pi).setMetaData("ParentProcessInstanceId", getProcessInstance().getStringId());
        ((ProcessInstanceImpl) pi).setMetaData("ParentNodeInstanceId", getUniqueId());
        ((ProcessInstanceImpl) pi).setMetaData("ParentNodeId", getSubProcessNode().getUniqueId());
        ((ProcessInstanceImpl) pi).setParentProcessInstanceId(getProcessInstance().getStringId());
        ((ProcessInstanceImpl) pi)
                .setRootProcessInstanceId(StringUtils.isEmpty(getProcessInstance().getRootProcessInstanceId()) ? getProcessInstance().getStringId() : getProcessInstance().getRootProcessInstanceId());
        ((ProcessInstanceImpl) pi).setRootProcessId(StringUtils.isEmpty(getProcessInstance().getRootProcessId()) ? getProcessInstance().getProcessId() : getProcessInstance().getRootProcessId());
        ((ProcessInstanceImpl) pi).setSignalCompletion(getSubProcessNode().isWaitForCompletion());

        processInstance.start();
        this.processInstanceId = processInstance.id();
        this.asyncWaitingNodeInstance = hasAsyncNodeInstance(pi);

        subProcessFactory.unbind(context, processInstance.variables());

        if (!getSubProcessNode().isWaitForCompletion()) {
            triggerCompleted();
        } else if (processInstance.status() == ProcessInstance.STATE_COMPLETED || processInstance.status() == ProcessInstance.STATE_ABORTED) {
            triggerCompleted();
        } else {
            addProcessListener();
        }
    }

    private boolean hasAsyncNodeInstance(org.kie.api.runtime.process.ProcessInstance pi) {
        return Optional.ofNullable(pi)
                .map(ProcessInstanceImpl.class::cast)
                .map(instance -> instance.getMetaData().get(Metadata.ASYNC_WAITING))
                .map(Boolean.class::cast)
                .orElse(false);
    }

    @Override
    public void cancel() {
        super.cancel();
        if (getSubProcessNode() == null || !getSubProcessNode().isIndependent()) {
            ProcessInstance processInstance = null;
            KogitoProcessRuntime kruntime = (KogitoProcessRuntime) ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime();

            processInstance = (ProcessInstance) kruntime.getProcessInstance(processInstanceId);

            if (processInstance != null) {
                processInstance.setState(ProcessInstance.STATE_ABORTED);
            }
        }
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public boolean isAsyncWaitingNodeInstance() {
        return asyncWaitingNodeInstance;
    }

    public void internalSetProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public void addEventListeners() {
        super.addEventListeners();
        addProcessListener();
    }

    private void addProcessListener() {
        getProcessInstance().addEventListener("processInstanceCompleted:" + processInstanceId, this, true);
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("processInstanceCompleted:" + processInstanceId, this, true);
    }

    @Override
    public void signalEvent(String type, Object event) {
        if (("processInstanceCompleted:" + processInstanceId).equals(type)) {
            processInstanceCompleted((ProcessInstance) event);
        } else {
            super.signalEvent(type, event);
        }
    }

    @Override
    public String[] getEventTypes() {
        return new String[] { "processInstanceCompleted:" + processInstanceId };
    }

    public void processInstanceCompleted(ProcessInstance processInstance) {
        removeEventListeners();
        handleOutMappings(processInstance);
        if (processInstance.getState() == ProcessInstance.STATE_ABORTED) {
            String faultName = processInstance.getOutcome() == null ? "" : processInstance.getOutcome();
            // handle exception as sub process failed with error code
            ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance) resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, faultName);
            if (exceptionScopeInstance != null) {

                exceptionScopeInstance.handleException(faultName, processInstance.getFaultData());
                if (getSubProcessNode() != null && !getSubProcessNode().isIndependent() && getSubProcessNode().isAbortParent()) {
                    cancel();
                }
                return;
            } else if (getSubProcessNode() != null && !getSubProcessNode().isIndependent() && getSubProcessNode().isAbortParent()) {
                getProcessInstance().setState(ProcessInstance.STATE_ABORTED, faultName);
                return;
            }

        }
        // handle dynamic subprocess
        if (getNode() == null) {
            setMetaData("NodeType", "SubProcessNode");
        }
        // if there were no exception proceed normally
        triggerCompleted();

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void handleOutMappings(ProcessInstance processInstance) {

        SubProcessFactory subProcessFactory = getSubProcessNode().getSubProcessFactory();
        KogitoProcessContextImpl context = new KogitoProcessContextImpl(getProcessInstance().getKnowledgeRuntime());
        context.setNodeInstance(this);
        org.kie.kogito.process.ProcessInstance<?> pi = ((org.kie.kogito.process.ProcessInstance<?>) processInstance.getMetaData().get("KogitoProcessInstance"));
        if (pi != null) {
            subProcessFactory.unbind(context, pi.variables());
        }
    }

    public String getNodeName() {
        org.kie.api.definition.process.Node node = getNode();
        if (node == null) {
            return "[Dynamic] Sub Process";
        }
        return super.getNodeName();
    }

    @Override
    public List<ContextInstance> getContextInstances(String contextId) {
        return this.subContextInstances.get(contextId);
    }

    @Override
    public void addContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = this.subContextInstances.get(contextId);
        if (list == null) {
            list = new ArrayList<ContextInstance>();
            this.subContextInstances.put(contextId, list);
        }
        list.add(contextInstance);
    }

    @Override
    public void removeContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = this.subContextInstances.get(contextId);
        if (list != null) {
            list.remove(contextInstance);
        }
    }

    @Override
    public ContextInstance getContextInstance(String contextId, long id) {
        List<ContextInstance> contextInstances = subContextInstances.get(contextId);
        if (contextInstances != null) {
            for (ContextInstance contextInstance : contextInstances) {
                if (contextInstance.getContextId() == id) {
                    return contextInstance;
                }
            }
        }
        return null;
    }

    @Override
    public ContextInstance getContextInstance(Context context) {
        ContextInstanceFactory conf = ContextInstanceFactoryRegistry.INSTANCE.getContextInstanceFactory(context);
        if (conf == null) {
            throw new IllegalArgumentException("Illegal context type (registry not found): " + context.getClass());
        }
        ContextInstance contextInstance = (ContextInstance) conf.getContextInstance(context, this, (ProcessInstance) getProcessInstance());
        if (contextInstance == null) {
            throw new IllegalArgumentException("Illegal context type (instance not found): " + context.getClass());
        }
        return contextInstance;
    }

    @Override
    public ContextContainer getContextContainer() {
        return getSubProcessNode();
    }

    protected Map<String, Object> getSourceParameters(DataAssociation association) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (String sourceParam : association.getSources()) {
            Object parameterValue = null;
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance) resolveContextInstance(VariableScope.VARIABLE_SCOPE, sourceParam);
            if (variableScopeInstance != null) {
                parameterValue = variableScopeInstance.getVariable(sourceParam);
            } else {
                try {
                    parameterValue = MVELProcessHelper.evaluator().eval(sourceParam, new NodeInstanceResolverFactory(this));
                } catch (Throwable t) {
                    logger.warn("Could not find variable scope for variable {}", sourceParam);
                }
            }
            if (parameterValue != null) {
                parameters.put(association.getTarget(), parameterValue);
            }
        }

        return parameters;
    }

}
