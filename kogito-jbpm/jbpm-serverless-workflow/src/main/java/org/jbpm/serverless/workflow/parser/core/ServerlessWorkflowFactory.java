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
package org.jbpm.serverless.workflow.parser.core;

import com.fasterxml.jackson.databind.JsonNode;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.serverless.workflow.api.Workflow;
import org.jbpm.serverless.workflow.api.end.End;
import org.jbpm.serverless.workflow.api.events.EventDefinition;
import org.jbpm.serverless.workflow.api.functions.Function;
import org.jbpm.serverless.workflow.parser.util.ServerlessWorkflowUtils;
import org.jbpm.serverless.workflow.parser.util.WorkflowAppContext;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.node.*;
import org.kie.api.definition.process.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ServerlessWorkflowFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessWorkflowFactory.class);

    public static final String EOL = System.getProperty("line.separator");
    public static final String DEFAULT_WORKFLOW_ID = "serverless";
    public static final String DEFAULT_WORKFLOW_NAME = "workflow";
    public static final String DEFAULT_WORKFLOW_VERSION = "1.0";
    public static final String DEFAULT_PACKAGE_NAME = "org.kie.kogito.serverless";
    public static final String DEFAULT_VISIBILITY = "Public";
    public static final String DEFAULT_DECISION = "decision";
    public static final String JSON_NODE = "com.fasterxml.jackson.databind.JsonNode";
    public static final String DEFAULT_WORKFLOW_VAR = "workflowdata";
    public static final String UNIQUE_ID_PARAM = "UniqueId";
    public static final String DEFAULT_SERVICE_IMPL = "Java";
    public static final String SERVICE_INTERFACE_KEY = "interface";
    public static final String SERVICE_OPERATION_KEY = "operation";
    public static final String SERVICE_IMPL_KEY = "implementation";
    public static final String DEFAULT_HT_TASKNAME = "workflowhtask";
    public static final String DEFAULT_HT_SKIPPABLE = "true";
    public static final String HT_TASKNAME = "taskname";
    public static final String HT_SKIPPABLE = "skippable";
    public static final String HTP_GROUPID = "groupid";
    public static final String HT_ACTORID = "actorid";
    public static final String RF_GROUP = "ruleflowgroup";

    private WorkflowAppContext workflowAppContext;

    public ServerlessWorkflowFactory(WorkflowAppContext workflowAppContext) {
        this.workflowAppContext = workflowAppContext;
    }

    public RuleFlowProcess createProcess(Workflow workflow) {
        RuleFlowProcess process = new RuleFlowProcess();

        if (workflow.getId() != null && !workflow.getId().isEmpty()) {
            process.setId(workflow.getId());
        } else {
            LOGGER.info("setting default id {}", DEFAULT_WORKFLOW_ID);
            process.setId(DEFAULT_WORKFLOW_ID);
        }

        if (workflow.getName() != null && !workflow.getName().isEmpty()) {
            process.setName(workflow.getName());
        } else {
            LOGGER.info("setting default name {}", DEFAULT_WORKFLOW_NAME);
            process.setName(DEFAULT_WORKFLOW_NAME);
        }

        if (workflow.getVersion() != null && !workflow.getVersion().isEmpty()) {
            process.setVersion(workflow.getVersion());
        } else {
            LOGGER.info("setting default version {}", DEFAULT_WORKFLOW_VERSION);
            process.setVersion(DEFAULT_WORKFLOW_VERSION);
        }

        if (workflow.getMetadata() != null && workflow.getMetadata().get("package") != null) {
            process.setPackageName(workflow.getMetadata().get("package"));
        } else {
            process.setPackageName(DEFAULT_PACKAGE_NAME);
        }

        process.setAutoComplete(true);
        process.setVisibility(DEFAULT_VISIBILITY);

        // add workflow data var
        processVar(DEFAULT_WORKFLOW_VAR, JsonNode.class, process);

        return process;
    }

    public StartNode startNode(long id, String name, NodeContainer nodeContainer) {
        StartNode startNode = new StartNode();
        startNode.setId(id);
        startNode.setName(name);

        nodeContainer.addNode(startNode);

        return startNode;
    }

    public StartNode messageStartNode(long id, EventDefinition eventDefinition, NodeContainer nodeContainer) {

        StartNode startNode = new StartNode();
        startNode.setId(id);
        startNode.setName(eventDefinition.getName());
        startNode.setMetaData("TriggerMapping", DEFAULT_WORKFLOW_VAR);
        startNode.setMetaData("TriggerType", "ConsumeMessage");
        startNode.setMetaData("TriggerRef", eventDefinition.getSource());
        startNode.setMetaData("MessageType", JSON_NODE);
        addTriggerToStartNode(startNode, JSON_NODE);

        nodeContainer.addNode(startNode);

        return startNode;
    }

    public EndNode endNode(long id, String name, boolean terminate, NodeContainer nodeContainer) {
        EndNode endNode = new EndNode();
        endNode.setId(id);
        endNode.setName(name);
        endNode.setTerminate(terminate);

        nodeContainer.addNode(endNode);

        return endNode;
    }

    public EndNode messageEndNode(long id, String name, Workflow workflow, End stateEnd, NodeContainer nodeContainer) {
        EndNode endNode = new EndNode();
        endNode.setTerminate(false);
        endNode.setId(id);
        endNode.setName(name);

        EventDefinition eventDef = ServerlessWorkflowUtils.getWorkflowEventFor(workflow, stateEnd.getProduceEvent().getNameRef());

        endNode.setMetaData("TriggerRef", eventDef.getSource());
        endNode.setMetaData("TriggerType", "ProduceMessage");
        endNode.setMetaData("MessageType", JSON_NODE);
        endNode.setMetaData("MappingVariable", DEFAULT_WORKFLOW_VAR);
        addMessageEndNodeAction(endNode, DEFAULT_WORKFLOW_VAR, JSON_NODE);

        nodeContainer.addNode(endNode);

        return endNode;
    }

    public TimerNode timerNode(long id, String name, String delay, NodeContainer nodeContainer) {
        TimerNode timerNode = new TimerNode();
        timerNode.setId(id);
        timerNode.setName(name);
        timerNode.setMetaData("EventType", "timer");

        Timer timer = new Timer();
        timer.setTimeType(Timer.TIME_DURATION);
        timer.setDelay(delay);
        timerNode.setTimer(timer);

        nodeContainer.addNode(timerNode);

        return timerNode;
    }

    public SubProcessNode callActivity(long id, String name, String calledId, boolean waitForCompletion, NodeContainer nodeContainer) {
        SubProcessNode subProcessNode = new SubProcessNode();
        subProcessNode.setId(id);
        subProcessNode.setName(name);
        subProcessNode.setProcessId(calledId);
        subProcessNode.setWaitForCompletion(waitForCompletion);
        subProcessNode.setIndependent(true);

        VariableScope variableScope = new VariableScope();
        subProcessNode.addContext(variableScope);
        subProcessNode.setDefaultContext(variableScope);

        Map<String, String> inputOtuputTypes = new HashMap<>();
        inputOtuputTypes.put(DEFAULT_WORKFLOW_VAR, JSON_NODE);
        subProcessNode.setMetaData("BPMN.InputTypes", inputOtuputTypes);
        subProcessNode.setMetaData("BPMN.OutputTypes", inputOtuputTypes);

        // parent and sub processes have process var "workflowdata"
        subProcessNode.addInMapping(DEFAULT_WORKFLOW_VAR, DEFAULT_WORKFLOW_VAR);
        subProcessNode.addOutMapping(DEFAULT_WORKFLOW_VAR, DEFAULT_WORKFLOW_VAR);

        nodeContainer.addNode(subProcessNode);

        return subProcessNode;
    }

    public void addMessageEndNodeAction(EndNode endNode, String variable, String messageType) {
        List<DroolsAction> actions = new ArrayList<>();

        actions.add(new DroolsConsequenceAction("java",
                "org.drools.core.process.instance.impl.WorkItemImpl workItem = new org.drools.core.process.instance.impl.WorkItemImpl();" + EOL +
                        "workItem.setName(\"Send Task\");" + EOL +
                        "workItem.setNodeInstanceId(kcontext.getNodeInstance().getId());" + EOL +
                        "workItem.setProcessInstanceId(kcontext.getProcessInstance().getId());" + EOL +
                        "workItem.setNodeId(kcontext.getNodeInstance().getNodeId());" + EOL +
                        "workItem.setParameter(\"MessageType\", \"" + messageType + "\");" + EOL +
                        (variable == null ? "" : "workItem.setParameter(\"Message\", " + variable + ");" + EOL) +
                        "workItem.setDeploymentId((String) kcontext.getKnowledgeRuntime().getEnvironment().get(\"deploymentId\"));" + EOL +
                        "((org.drools.core.process.instance.WorkItemManager) kcontext.getKnowledgeRuntime().getWorkItemManager()).internalExecuteWorkItem(workItem);"));
        endNode.setActions(ExtendedNodeImpl.EVENT_NODE_ENTER, actions);
    }

    public void addTriggerToStartNode(StartNode startNode, String triggerEventType) {
        EventTrigger trigger = new EventTrigger();
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType(triggerEventType);
        trigger.addEventFilter(eventFilter);

        String mapping = (String) startNode.getMetaData("TriggerMapping");
        if (mapping != null) {
            trigger.addInMapping(mapping, startNode.getOutMapping(mapping));
        }

        startNode.addTrigger(trigger);
    }

    public ActionNode scriptNode(long id, String name, String script, NodeContainer nodeContainer) {
        ActionNode scriptNode = new ActionNode();
        scriptNode.setId(id);
        scriptNode.setName(name);

        scriptNode.setAction(new DroolsConsequenceAction());
        ((DroolsConsequenceAction) scriptNode.getAction()).setConsequence(script);
        ((DroolsConsequenceAction) scriptNode.getAction()).setDialect(JavaDialect.ID);

        nodeContainer.addNode(scriptNode);

        return scriptNode;
    }

    public WorkItemNode serviceNode(long id, String name, Function function, NodeContainer nodeContainer) {
        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setId(id);
        workItemNode.setName(name);
        workItemNode.setMetaData("Type", "Service Task");

        Work work = new WorkImpl();
        workItemNode.setWork(work);

        work.setName("Service Task");
        work.setParameter("Interface", ServerlessWorkflowUtils.resolveFunctionMetadata(function, SERVICE_INTERFACE_KEY, workflowAppContext));
        work.setParameter("Operation", ServerlessWorkflowUtils.resolveFunctionMetadata(function, SERVICE_OPERATION_KEY, workflowAppContext));
        work.setParameter("interfaceImplementationRef", ServerlessWorkflowUtils.resolveFunctionMetadata(function, SERVICE_INTERFACE_KEY, workflowAppContext));
        work.setParameter("operationImplementationRef", ServerlessWorkflowUtils.resolveFunctionMetadata(function, SERVICE_OPERATION_KEY, workflowAppContext));
        work.setParameter("ParameterType", JSON_NODE);
        String metaImpl = ServerlessWorkflowUtils.resolveFunctionMetadata(function, SERVICE_IMPL_KEY, workflowAppContext);
        if (metaImpl == null || metaImpl.isEmpty()) {
            metaImpl = DEFAULT_SERVICE_IMPL;
        }
        work.setParameter(SERVICE_IMPL_KEY, metaImpl);

        workItemNode.addInMapping("Parameter", DEFAULT_WORKFLOW_VAR);
        workItemNode.addOutMapping("Result", DEFAULT_WORKFLOW_VAR);

        nodeContainer.addNode(workItemNode);

        return workItemNode;

    }

    public void processVar(String varName, Class varType, RuleFlowProcess process) {
        Variable variable = new Variable();
        variable.setName(varName);
        variable.setType(new ObjectDataType(varType.getName()));
        process.getVariableScope().getVariables().add(variable);
    }

    public CompositeContextNode subProcessNode(long id, String name, NodeContainer nodeContainer) {
        CompositeContextNode subProcessNode = new CompositeContextNode();
        subProcessNode.setId(id);
        subProcessNode.setName(name);
        subProcessNode.setAutoComplete(true);
        nodeContainer.addNode(subProcessNode);

        return subProcessNode;
    }


    public Split splitNode(long id, String name, int type, NodeContainer nodeContainer) {
        Split split = new Split();
        split.setId(id);
        split.setName(name);
        split.setType(type);
        split.setMetaData(UNIQUE_ID_PARAM, Long.toString(id));

        nodeContainer.addNode(split);
        return split;
    }

    public Join joinNode(long id, String name, int type, NodeContainer nodeContainer) {
        Join join = new Join();
        join.setId(id);
        join.setName(name);
        join.setType(type);
        join.setMetaData(UNIQUE_ID_PARAM, Long.toString(id));

        nodeContainer.addNode(join);
        return join;
    }

    public ConstraintImpl splitConstraint(String name, String type, String dialect, String constraint, int priority, boolean isDefault) {
        ConstraintImpl constraintImpl = new ConstraintImpl();
        constraintImpl.setName(name);
        constraintImpl.setType(type);
        constraintImpl.setDialect(dialect);
        constraintImpl.setConstraint(constraint);
        constraintImpl.setPriority(priority);
        constraintImpl.setDefault(isDefault);

        return constraintImpl;
    }

    public HumanTaskNode humanTaskNode(long id, String name, Function function, RuleFlowProcess process, NodeContainer nodeContainer) {
        // first add the node "decision" variable
        processVar(ServerlessWorkflowUtils.resolveFunctionMetadata(function, HT_TASKNAME, workflowAppContext)
                + DEFAULT_DECISION, JsonNode.class, process);
        // then the ht node
        HumanTaskNode humanTaskNode = new HumanTaskNode();
        humanTaskNode.setId(id);
        humanTaskNode.setName(name);
        Work work = new WorkImpl();
        work.setName("Human Task");
        humanTaskNode.setWork(work);

        work.setParameter("TaskName", ServerlessWorkflowUtils.resolveFunctionMetadata(function, HT_TASKNAME, workflowAppContext).length() > 0 ?
                ServerlessWorkflowUtils.resolveFunctionMetadata(function, HT_TASKNAME, workflowAppContext) : DEFAULT_HT_TASKNAME);
        work.setParameter("Skippable", ServerlessWorkflowUtils.resolveFunctionMetadata(function, HT_SKIPPABLE, workflowAppContext).length() > 0 ?
                ServerlessWorkflowUtils.resolveFunctionMetadata(function, HT_SKIPPABLE, workflowAppContext) : DEFAULT_HT_SKIPPABLE);

        if (ServerlessWorkflowUtils.resolveFunctionMetadata(function, HTP_GROUPID, workflowAppContext).length() > 0) {
            work.setParameter("GroupId", ServerlessWorkflowUtils.resolveFunctionMetadata(function, HTP_GROUPID, workflowAppContext));
        }

        if (ServerlessWorkflowUtils.resolveFunctionMetadata(function, HT_ACTORID, workflowAppContext).length() > 0) {
            work.setParameter("ActorId", ServerlessWorkflowUtils.resolveFunctionMetadata(function, HT_ACTORID, workflowAppContext));
        }
        work.setParameter("NodeName", name);

        humanTaskNode.addInMapping(DEFAULT_WORKFLOW_VAR, DEFAULT_WORKFLOW_VAR);
        humanTaskNode.addOutMapping(DEFAULT_DECISION, ServerlessWorkflowUtils.resolveFunctionMetadata(function, HT_TASKNAME,
                workflowAppContext) + DEFAULT_DECISION);

        nodeContainer.addNode(humanTaskNode);

        return humanTaskNode;
    }

    public RuleSetNode ruleSetNode(long id, String name, Function function, NodeContainer nodeContainer) {
        RuleSetNode ruleSetNode = new RuleSetNode();
        ruleSetNode.setId(id);
        ruleSetNode.setName(name);

        ruleSetNode.setRuleType(RuleSetNode.RuleType.ruleFlowGroup(ServerlessWorkflowUtils.resolveFunctionMetadata(function, RF_GROUP, workflowAppContext)));
        ruleSetNode.setLanguage(RuleSetNode.DRL_LANG);

        ruleSetNode.addInMapping(DEFAULT_WORKFLOW_VAR, DEFAULT_WORKFLOW_VAR);
        ruleSetNode.addOutMapping(DEFAULT_WORKFLOW_VAR, DEFAULT_WORKFLOW_VAR);

        nodeContainer.addNode(ruleSetNode);

        return ruleSetNode;
    }

    public void connect(long fromId, long toId, String uniqueId, NodeContainer nodeContainer) {
        Node from = nodeContainer.getNode(fromId);
        Node to = nodeContainer.getNode(toId);
        ConnectionImpl connection = new ConnectionImpl(
                from, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
                to, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        connection.setMetaData(UNIQUE_ID_PARAM, uniqueId);
    }

    public void validate(RuleFlowProcess process) {
        ProcessValidationError[] errors = RuleFlowProcessValidator.getInstance().validateProcess(process);
        for (ProcessValidationError error : errors) {
            LOGGER.error(error.toString());
        }
        if (errors.length > 0) {
            throw new RuntimeException("Workflow could not be validated !");
        }
    }

}
