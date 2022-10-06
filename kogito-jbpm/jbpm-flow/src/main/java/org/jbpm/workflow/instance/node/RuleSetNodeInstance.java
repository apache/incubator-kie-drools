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
import java.util.stream.Collectors;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.AbstractProcessContext;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.transformation.JsonResolver;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.process.instance.impl.ContextInstanceFactoryRegistry;
import org.jbpm.util.ContextFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.RuleUnitFactory;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.dmn.rest.DMNJSONUtils;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;

/**
 * Runtime counterpart of a ruleset node.
 */
public class RuleSetNodeInstance extends StateBasedNodeInstance implements EventListener,
        ContextInstanceContainer {

    private static final long serialVersionUID = 510L;

    private static final String ACT_AS_WAIT_STATE_PROPERTY = "org.jbpm.rule.task.waitstate";
    private static final String FIRE_RULE_LIMIT_PROPERTY = "org.jbpm.rule.task.firelimit";
    private static final String FIRE_RULE_LIMIT_PARAMETER = "FireRuleLimit";
    private static final int DEFAULT_FIRE_RULE_LIMIT = Integer.parseInt(System.getProperty(FIRE_RULE_LIMIT_PROPERTY, "10000"));

    private Map<String, FactHandle> factHandles = new HashMap<>();
    private String ruleFlowGroup;
    private final JsonResolver jsonResolver = new JsonResolver();

    // NOTE: ContextInstances are not persisted as current functionality (exception scope) does not require it
    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<>();

    protected RuleSetNode getRuleSetNode() {
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

            KieRuntime kruntime = Optional.ofNullable(getRuleSetNode().getKieRuntime()).orElse(() -> getProcessInstance().getKnowledgeRuntime()).get();
            Map<String, Object> inputs = NodeIoHelper.processInputs(this, varRef -> getVariable(varRef));

            RuleSetNode.RuleType ruleType = ruleSetNode.getRuleType();
            if (ruleType.isDecision()) {
                RuleSetNode.RuleType.Decision decisionModel = (RuleSetNode.RuleType.Decision) ruleType;
                String namespace = resolveExpression(decisionModel.getNamespace());
                String model = resolveExpression(decisionModel.getModel());

                DecisionModel modelInstance =
                        Optional.ofNullable(getRuleSetNode().getDecisionModel())
                                .orElse(() -> new DmnDecisionModel(
                                        ((KieSession) kruntime).getKieRuntime(DMNRuntime.class),
                                        namespace,
                                        model))
                                .get();

                //Input Binding
                DMNContext context = DMNJSONUtils.ctx(modelInstance, jsonResolver.resolveAll(inputs));
                DMNResult dmnResult = modelInstance.evaluateAll(context);
                if (dmnResult.hasErrors()) {
                    String errors = dmnResult.getMessages(Severity.ERROR).stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));

                    throw new RuntimeException("DMN result errors:: " + errors);
                }
                //Output Binding
                Map<String, Object> outputSet = dmnResult.getContext().getAll();
                NodeIoHelper.processOutputs(this, key -> outputSet.get(key), varName -> this.getVariable(varName));

                triggerCompleted();
            } else if (ruleType.isRuleFlowGroup()) {
                // first set rule flow group
                setRuleFlowGroup(resolveRuleFlowGroup(ruleType.getName()));

                //proceed
                for (Entry<String, Object> entry : inputs.entrySet()) {
                    if (FIRE_RULE_LIMIT_PARAMETER.equals(entry.getKey())) {
                        // don't put control parameter for fire limit into working memory
                        continue;
                    }

                    String inputKey = getRuleFlowGroup() + "_" + getProcessInstance().getStringId() + "_" + entry.getKey();

                    factHandles.put(inputKey, kruntime.insert(entry.getValue()));
                }

                if (actAsWaitState()) {
                    addRuleSetListener();
                    ((InternalAgenda) kruntime.getAgenda())
                            .activateRuleFlowGroup(getRuleFlowGroup(), getProcessInstance().getStringId(), getUniqueId());
                } else {
                    int fireLimit = DEFAULT_FIRE_RULE_LIMIT;
                    WorkflowProcessInstance processInstance = getProcessInstance();

                    if (inputs.containsKey(FIRE_RULE_LIMIT_PARAMETER)) {
                        fireLimit = Integer.parseInt(inputs.get(FIRE_RULE_LIMIT_PARAMETER).toString());
                    }
                    ((InternalAgenda) kruntime.getAgenda())
                            .activateRuleFlowGroup(getRuleFlowGroup(), processInstance.getStringId(), getUniqueId());

                    int fired = ((KieSession) kruntime).fireAllRules(processInstance.getAgendaFilter(), fireLimit);
                    if (fired == fireLimit) {
                        throw new RuntimeException("Fire rule limit reached " + fireLimit + ", limit can be set via system property " + FIRE_RULE_LIMIT_PROPERTY
                                + " or via data input of business rule task named " + FIRE_RULE_LIMIT_PARAMETER);
                    }

                    removeEventListeners();
                    retractFacts(kruntime);
                    triggerCompleted();
                }
            } else if (ruleType.isRuleUnit()) {
                RuleUnitFactory<RuleUnitData> factory = ruleSetNode.getRuleUnitFactory();
                AbstractProcessContext context = ContextFactory.fromNode(this);
                RuleUnitData model = factory.bind(context);
                try (RuleUnitInstance<RuleUnitData> instance = factory.unit().createInstance(model)) {
                    instance.fire();
                    factory.unbind(context, model);
                    triggerCompleted();
                }
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

    private void addRuleSetListener() {
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

        Map<String, Object> outputSet = objects;
        NodeIoHelper.processOutputs(this, key -> outputSet.get(key), varName -> this.getVariable(varName));
        factHandles.clear();
    }

    private String resolveRuleFlowGroup(String origin) {
        return resolveExpression(origin);
    }

    public Map<String, FactHandle> getFactHandles() {
        return factHandles;
    }

    public void setFactHandles(Map<String, FactHandle> factHandles) {
        this.factHandles = factHandles;
    }

    public String getRuleFlowGroup() {
        if (ruleFlowGroup == null || ruleFlowGroup.trim().length() == 0) {
            RuleSetNode.RuleType ruleType = getRuleSetNode().getRuleType();
            ruleFlowGroup = ruleType.isRuleFlowGroup() ? ruleType.getName() : null;
        }
        return ruleFlowGroup;
    }

    public void setRuleFlowGroup(String ruleFlowGroup) {

        this.ruleFlowGroup = ruleFlowGroup;
    }

    protected boolean actAsWaitState() {
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
