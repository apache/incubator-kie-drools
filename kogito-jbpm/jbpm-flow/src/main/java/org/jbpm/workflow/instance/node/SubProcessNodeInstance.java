/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.util.StringUtils;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.KogitoProcessContextImpl;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.StartProcessHelper;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.process.instance.impl.ContextInstanceFactoryRegistry;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.EventListener;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime counterpart of a SubFlow node.
 */
public class SubProcessNodeInstance extends StateBasedNodeInstance implements EventListener,
        ContextInstanceContainer {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(SubProcessNodeInstance.class);

    // NOTE: ContextInstances are not persisted as current functionality (exception scope) does not require it
    private Map<String, ContextInstance> contextInstances = new HashMap<String, ContextInstance>();
    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<>();

    private String processInstanceId;

    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    @Override
    public void internalTrigger(final KogitoNodeInstance from, String type) {
        super.internalTrigger(from, type);
        // if node instance was cancelled, abort
        if (getNodeInstanceContainer().getNodeInstance(getStringId()) == null) {
            return;
        }
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "A SubProcess node only accepts default incoming connections!");
        }

        Map<String, Object> parameters = NodeIoHelper.processInputs(this, key -> getVariable(key));

        String processIdExpression = getSubProcessNode().getProcessId();
        if (processIdExpression == null) {
            // if process id is not given try with process name
            processIdExpression = getSubProcessNode().getProcessName();
        }
        String processId = resolveExpression(processIdExpression);

        KieBase kbase = getProcessInstance().getKnowledgeRuntime().getKieBase();
        // start process instance
        Process process = kbase.getProcess(processId);

        if (process == null) {
            // try to find it by name
            String latestProcessId = StartProcessHelper.findLatestProcessByName(kbase, processId);
            if (latestProcessId != null) {
                processId = latestProcessId;
                process = kbase.getProcess(processId);
            }
        }

        if (process == null) {
            logger.error("Could not find process {}", processId);
            logger.error("Aborting process");
            getProcessInstance().setState(ProcessInstance.STATE_ABORTED);
            throw new RuntimeException("Could not find process " + processId);
        } else {
            KogitoProcessRuntime kruntime = InternalProcessRuntime.asKogitoProcessRuntime(getProcessInstance().getKnowledgeRuntime());
            if (getSubProcessNode().getMetaData("MICollectionInput") != null) {
                // remove foreach input variable to avoid problems when running in variable strict mode
                parameters.remove(getSubProcessNode().getMetaData("MICollectionInput"));
            }

            ProcessInstance processInstance = null;
            if (getProcessInstance().getCorrelationKey() != null) {
                // in case there is correlation key on parent instance pass it along to child so it can be easily correlated 
                // since correlation key must be unique for active instances it appends processId and timestamp
                List<String> businessKeys = new ArrayList<String>();
                businessKeys.add(getProcessInstance().getCorrelationKey());
                businessKeys.add(processId);
                businessKeys.add(String.valueOf(System.currentTimeMillis()));
                CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
                CorrelationKey subProcessCorrelationKey = correlationKeyFactory.newCorrelationKey(businessKeys);
                processInstance = (ProcessInstance) ((CorrelationAwareProcessRuntime) kruntime).createProcessInstance(processId, subProcessCorrelationKey, parameters);
            } else {

                processInstance = (ProcessInstance) kruntime.createProcessInstance(processId, parameters);
            }
            this.processInstanceId = processInstance.getStringId();
            processInstance.setMetaData("ParentProcessInstanceId", getProcessInstance().getStringId());
            processInstance.setMetaData("ParentNodeInstanceId", getUniqueId());
            processInstance.setMetaData("ParentNodeId", getSubProcessNode().getUniqueId());
            processInstance.setParentProcessInstanceId(getProcessInstance().getStringId());
            processInstance.setRootProcessInstanceId(
                    StringUtils.isEmpty(getProcessInstance().getRootProcessInstanceId()) ? getProcessInstance().getStringId() : getProcessInstance().getRootProcessInstanceId());
            processInstance.setRootProcessId(StringUtils.isEmpty(getProcessInstance().getRootProcessId()) ? getProcessInstance().getProcessId() : getProcessInstance().getRootProcessId());
            processInstance.setSignalCompletion(getSubProcessNode().isWaitForCompletion());

            kruntime.startProcessInstance(processInstance.getStringId());
            if (!getSubProcessNode().isWaitForCompletion()) {
                triggerCompleted();
            } else if (processInstance.getState() == ProcessInstance.STATE_COMPLETED
                    || processInstance.getState() == ProcessInstance.STATE_ABORTED) {
                processInstanceCompleted(processInstance);
            } else {
                addProcessListener();
            }
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if (getSubProcessNode() == null || !getSubProcessNode().isIndependent()) {
            KogitoProcessRuntime kruntime = InternalProcessRuntime.asKogitoProcessRuntime(getProcessInstance().getKnowledgeRuntime());

            ProcessInstance processInstance = (ProcessInstance) kruntime.getProcessInstance(processInstanceId);

            if (processInstance != null) {
                processInstance.setState(ProcessInstance.STATE_ABORTED);
            }
        }
    }

    public String getProcessInstanceId() {
        return processInstanceId;
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

        Map<String, Object> outputSet = processInstance.getVariables();
        NodeIoHelper.processOutputs(this, varRef -> outputSet.get(varRef), varName -> this.getVariable(varName));

        if (processInstance.getState() == ProcessInstance.STATE_ABORTED) {
            String faultName = processInstance.getOutcome() == null ? "" : processInstance.getOutcome();
            // handle exception as sub process failed with error code
            ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance) resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, faultName);
            if (exceptionScopeInstance != null) {

                KogitoProcessContextImpl context = new KogitoProcessContextImpl(this.getProcessInstance().getKnowledgeRuntime());
                context.setProcessInstance(this.getProcessInstance());
                context.setNodeInstance(this);
                context.getContextData().put("Exception", processInstance.getFaultData());
                exceptionScopeInstance.handleException(faultName, context);

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
            list = new ArrayList<>();
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
}
