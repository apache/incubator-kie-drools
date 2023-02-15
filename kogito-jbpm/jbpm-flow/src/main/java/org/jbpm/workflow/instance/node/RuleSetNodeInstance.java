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
import java.util.Map.Entry;
import java.util.Optional;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.process.instance.impl.ContextInstanceFactoryRegistry;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.jbpm.workflow.instance.rule.RuleType;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;

/**
 * Runtime counterpart of a ruleset node.
 */
public class RuleSetNodeInstance extends StateBasedNodeInstance implements EventListener,
        ContextInstanceContainer {

    private static final long serialVersionUID = 510L;

    private static final String ACT_AS_WAIT_STATE_PROPERTY = "org.jbpm.rule.task.waitstate";

    private Map<String, FactHandle> factHandles = new HashMap<>();
    private String ruleFlowGroup;

    // NOTE: ContextInstances are not persisted as current functionality (exception scope) does not require it
    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<>();

    public RuleSetNode getRuleSetNode() {
        return (RuleSetNode) getNode();
    }

    @Override
    public void internalTrigger(KogitoNodeInstance from, String type) {
        try {
            super.internalTrigger(from, type);
            // if node instance was cancelled, abort
            if (getNodeInstanceContainer().getNodeInstance(getStringId()) == null) {
                return;
            }
            if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
                throw new IllegalArgumentException("A RuleSetNode only accepts default incoming connections!");
            }
            RuleSetNode ruleSetNode = getRuleSetNode();

            RuleType ruleType = ruleSetNode.getRuleType();
            if (ruleType != null) {
                ruleType.evaluate(this);
            } else {
                throw new UnsupportedOperationException("Unsupported Rule Type: " + ruleType);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Throwable e) {
        ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance) resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, e);
        if (exceptionScopeInstance != null) {
            exceptionScopeInstance.handleException(e, getProcessContext(e));
        } else {
            throw new WorkflowRuntimeException(this, getProcessInstance(), "Unable to execute Action: " + e.getMessage(), e);
        }
    }

    @Override
    public void addEventListeners() {
        super.addEventListeners();
        addRuleSetListener();
    }

    private String getRuleSetEventType() {
        InternalKnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
        if (kruntime instanceof StatefulKnowledgeSession) {
            return "RuleFlowGroup_" + getRuleFlowGroup() + "_" + ((StatefulKnowledgeSession) kruntime).getIdentifier();
        } else {
            return "RuleFlowGroup_" + getRuleFlowGroup();
        }
    }

    public void addRuleSetListener() {
        getProcessInstance().addEventListener(getRuleSetEventType(), this, true);
    }

    @Override
    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener(getRuleSetEventType(), this, true);
    }

    @Override
    public void cancel() {
        super.cancel();
        if (actAsWaitState()) {
            ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda()).getAgendaGroupsManager().deactivateRuleFlowGroup(getRuleFlowGroup());
        }
    }

    @Override
    public void signalEvent(String type, Object event) {
        if (getRuleSetEventType().equals(type)) {
            removeEventListeners();
            KieRuntime kruntime = Optional.ofNullable(getRuleSetNode().getKieRuntime()).orElse(() -> getProcessInstance().getKnowledgeRuntime()).get();
            retractFacts(kruntime);
            triggerCompleted();
        }
    }

    public void addFact(String key, FactHandle handle) {
        factHandles.put(key, handle);
    }

    public void retractFacts(KieRuntime kruntime) {
        Map<String, Object> objects = new HashMap<>();

        for (Entry<String, FactHandle> entry : factHandles.entrySet()) {
            Object object = kruntime.getObject(entry.getValue());
            String key = entry.getKey();
            key = key.replaceAll(getRuleFlowGroup() + "_", "");
            key = key.replaceAll(getProcessInstance().getStringId() + "_", "");
            objects.put(key, object);

            kruntime.delete(entry.getValue());
        }

        NodeIoHelper.processOutputs(this, objects::get, this::getVariable);
        factHandles.clear();
    }

    public String getRuleFlowGroup() {
        if (ruleFlowGroup == null || ruleFlowGroup.trim().length() == 0) {
            RuleType ruleType = getRuleSetNode().getRuleType();
            ruleFlowGroup = ruleType.isRuleFlowGroup() ? ruleType.getName() : null;
        }
        return ruleFlowGroup;
    }

    public void setRuleFlowGroup(String ruleFlowGroup) {
        this.ruleFlowGroup = resolveExpression(ruleFlowGroup);
    }

    public boolean actAsWaitState() {
        Object asWaitState = getProcessInstance().getKnowledgeRuntime().getEnvironment().get(ACT_AS_WAIT_STATE_PROPERTY);
        if (asWaitState != null) {
            return Boolean.parseBoolean(asWaitState.toString());
        }

        return false;
    }

    @Override
    public List<ContextInstance> getContextInstances(String contextId) {
        return this.subContextInstances.get(contextId);
    }

    @Override
    public void addContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = this.subContextInstances.computeIfAbsent(contextId, key -> new ArrayList<>());
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
        ContextInstance contextInstance = conf.getContextInstance(context, this, getProcessInstance());
        if (contextInstance == null) {
            throw new IllegalArgumentException("Illegal context type (instance not found): " + context.getClass());
        }
        return contextInstance;
    }

    @Override
    public ContextContainer getContextContainer() {
        return getRuleSetNode();
    }
}
