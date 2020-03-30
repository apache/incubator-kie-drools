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
package org.jbpm.serverless.workflow.parser;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.serverless.workflow.api.end.End;
import org.jbpm.serverless.workflow.api.functions.Function;
import org.jbpm.serverless.workflow.api.interfaces.State;
import org.jbpm.serverless.workflow.api.mapper.BaseObjectMapper;
import org.jbpm.serverless.workflow.api.states.DelayState;
import org.jbpm.serverless.workflow.api.states.EventState;
import org.jbpm.serverless.workflow.api.states.SubflowState;
import org.jbpm.serverless.workflow.api.transitions.Transition;
import org.jbpm.serverless.workflow.parser.core.ServerlessWorkflowFactory;
import org.jbpm.serverless.workflow.parser.util.ServerlessWorkflowUtils;
import org.jbpm.workflow.core.node.*;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.jbpm.serverless.workflow.api.Workflow;
import org.jbpm.serverless.workflow.api.actions.Action;
import org.jbpm.serverless.workflow.api.states.DefaultState.Type;
import org.jbpm.serverless.workflow.api.states.OperationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerlessWorkflowParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessWorkflowParser.class);

    private AtomicLong idCounter = new AtomicLong(1);
    private ServerlessWorkflowFactory factory;
    private BaseObjectMapper objectMapper;

    public ServerlessWorkflowParser() {
        this.factory = new ServerlessWorkflowFactory();
    }

    public ServerlessWorkflowParser(String workflowFormat) {
        this.objectMapper = ServerlessWorkflowUtils.getObjectMapper(workflowFormat);
        this.factory = new ServerlessWorkflowFactory();
    }

    public Process parseWorkFlow(Reader workflowFile) throws Exception {
        Workflow workflow = objectMapper.readValue(ServerlessWorkflowUtils.readWorkflowFile(workflowFile), Workflow.class);
        RuleFlowProcess process = factory.createProcess(workflow);
        Map<String, Long> nameToNodeId = new HashMap<>();

        if(!ServerlessWorkflowUtils.includesSupportedStates(workflow)) {
            LOGGER.warn("workflow includes currently unsupported states.");
            LOGGER.warn("default process is generated.");

            StartNode startNode = factory.startNode(idCounter.getAndIncrement(), "Start", process);
            EndNode endNode = factory.endNode(idCounter.getAndIncrement(), "End", true, process);
            factory.connect(startNode.getId(), endNode.getId(), startNode.getId() + "_" + endNode.getId(), process);

            factory.validate(process);
            return process;
        }

        List<State> workflowStates = workflow.getStates();
        List<Function> workflowFunctions = workflow.getFunctions();

        StartNode processStartNode;
        EndNode processEndNode;

        State workflowStartState = ServerlessWorkflowUtils.getWorkflowStartState(workflow);
        State workflowEndState = ServerlessWorkflowUtils.getWorkflowEndState(workflow);

        if(workflowStartState.getType().equals(Type.EVENT)) {
            EventState startEventState = (EventState) workflowStartState;
            processStartNode = factory.messageStartNode(idCounter.getAndIncrement(), ServerlessWorkflowUtils.getWorkflowEventFor(workflow, startEventState.getEventsActions().get(0).getEventRefs().get(0)), process);
        } else {
            processStartNode = factory.startNode(idCounter.getAndIncrement(), "Start", process);
        }

        if(workflowEndState.getEnd().getKind() == End.Kind.EVENT) {
            processEndNode = factory.messageEndNode(idCounter.getAndIncrement(), workflow, workflowEndState.getEnd(), process);
        } else {
            processEndNode = factory.endNode(idCounter.getAndIncrement(), "End", true, process);
        }

        for(State state : workflowStates) {
            if(state.getType().equals(Type.EVENT)) {
                EventState eventState = (EventState) state;
                if(eventState.getStart() == null) {
                    throw new RuntimeException("currently support only event start states");
                }

                CompositeContextNode embeddedSubProcess = factory.subProcessNode(idCounter.getAndIncrement(), state.getName(), process);
                handleActions(workflowFunctions, eventState.getEventsActions().get(0).getActions(), embeddedSubProcess);

                factory.connect(processStartNode.getId(), embeddedSubProcess.getId(), processStartNode.getId() + "_" + embeddedSubProcess.getId(), process);

                if (state.getEnd() != null) {
                    factory.connect(embeddedSubProcess.getId(), processEndNode.getId(), embeddedSubProcess.getId() + "_" + processEndNode.getId(), process);
                }

                nameToNodeId.put(state.getName(), embeddedSubProcess.getId());
            }

            if (state.getType().equals(Type.OPERATION)) {
                OperationState operationState = (OperationState) state;
                CompositeContextNode embeddedSubProcess = factory.subProcessNode(idCounter.getAndIncrement(), state.getName(), process);
                handleActions(workflowFunctions, operationState.getActions(), embeddedSubProcess);

                if(state.getStart() != null) {
                    factory.connect(processStartNode.getId(), embeddedSubProcess.getId(), processStartNode.getId() + "_" + embeddedSubProcess.getId(), process);
                }

                if(state.getEnd() != null) {
                    factory.connect(embeddedSubProcess.getId(), processEndNode.getId(), embeddedSubProcess.getId() + "_" + processEndNode.getId(), process);
                }

                nameToNodeId.put(state.getName(), embeddedSubProcess.getId());
            }

            if (state.getType().equals(Type.DELAY)) {
                DelayState delayState = (DelayState) state;

                TimerNode timerNode = factory.timerNode(idCounter.getAndIncrement(), delayState.getName(), delayState.getTimeDelay(), process);

                if(state.getStart() != null) {
                    factory.connect(processStartNode.getId(), timerNode.getId(), processStartNode.getId() + "_" + timerNode.getId(), process);
                }

                if(state.getEnd() != null) {
                    factory.connect(timerNode.getId(), processEndNode.getId(), timerNode.getId() + "_" + processEndNode.getId(), process);
                }

                nameToNodeId.put(state.getName(), timerNode.getId());

            }

            if (state.getType().equals(Type.SUBFLOW)) {
                SubflowState subflowState = (SubflowState) state;

                SubProcessNode callActivityNode = factory.callActivity(idCounter.getAndIncrement(), subflowState.getName(), subflowState.getWorkflowId(), subflowState.isWaitForCompletion(), process);

                if(state.getStart() != null) {
                    factory.connect(processStartNode.getId(), callActivityNode.getId(), processStartNode.getId() + "_" + callActivityNode.getId(), process);
                }

                if(state.getEnd() != null) {
                    factory.connect(callActivityNode.getId(), processEndNode.getId(), callActivityNode.getId() + "_" + processEndNode.getId(), process);
                }

                nameToNodeId.put(state.getName(), callActivityNode.getId());
            }
        }

        workflow.getStates().stream().filter(state -> (state instanceof State)).forEach(state -> {
            Transition transition = state.getTransition();

            if (transition != null && transition.getNextState() != null) {
                Long sourceId = nameToNodeId.get(state.getName());
                Long targetId = nameToNodeId.get(state.getTransition().getNextState());

                factory.connect(sourceId, targetId, sourceId + "_" + targetId, process);

            }
        });

        factory.validate(process);
        return process;
    }

    protected void handleActions(List<Function> workflowFunctions, List<Action> actions, CompositeContextNode embeddedSubProcess) {
        if(actions != null && !actions.isEmpty()) {
            StartNode embeddedStartNode = factory.startNode(idCounter.getAndIncrement(), "EmbeddedStart", embeddedSubProcess);
            Node start = embeddedStartNode;
            Node current = null;

            for(Action action : actions) {
                Function actionFunction = workflowFunctions.stream()
                        .filter(wf -> wf.getName().equals(action.getFunctionRef().getRefName()))
                        .findFirst()
                        .get();

                if(actionFunction.getType() != null) {
                    if ("script".equalsIgnoreCase(actionFunction.getType())) {
                        String script = ServerlessWorkflowUtils.applySubstitutionsToScript(action.getFunctionRef().getParameters().get("script"));
                        current = factory.scriptNode(idCounter.getAndIncrement(), action.getFunctionRef().getRefName(), script, embeddedSubProcess);

                        factory.connect(start.getId(), current.getId(), start.getId() + "_" + current.getId(), embeddedSubProcess);
                        start = current;
                    } else if ("sysout".equalsIgnoreCase(actionFunction.getType())) {
                        String script = ServerlessWorkflowUtils.applySubstitutionsToScript("System.out.println(" + "\"" + action.getFunctionRef().getParameters().get("prefix") + " \" + " + action.getFunctionRef().getParameters().get("message") + ");");
                        current = factory.scriptNode(idCounter.getAndIncrement(), action.getFunctionRef().getRefName(), script, embeddedSubProcess);

                        factory.connect(start.getId(), current.getId(), start.getId() + "_" + current.getId(), embeddedSubProcess);
                        start = current;
                    } else if ("service".equalsIgnoreCase(actionFunction.getType())) {
                        current = factory.serviceNode(idCounter.getAndIncrement(), action.getFunctionRef().getRefName(), actionFunction, embeddedSubProcess);
                        factory.connect(start.getId(), current.getId(), start.getId() + "_" + current.getId(), embeddedSubProcess);
                        start = current;
                    } else {
                        LOGGER.warn("currently unsupported function type, supported types are 'script', 'sysout', 'service'");
                        LOGGER.warn("defaulting to script type");
                        String script = ServerlessWorkflowUtils.applySubstitutionsToScript("");
                        current = factory.scriptNode(idCounter.getAndIncrement(), action.getFunctionRef().getRefName(), script, embeddedSubProcess);

                        factory.connect(start.getId(), current.getId(), start.getId() + "_" + current.getId(), embeddedSubProcess);
                        start = current;
                    }
                } else {
                    LOGGER.warn("invalid function type. supported types are 'script', 'sysout', 'service'");
                    LOGGER.warn("defaulting to script type");
                    String script = ServerlessWorkflowUtils.applySubstitutionsToScript("");
                    current = factory.scriptNode(idCounter.getAndIncrement(), action.getFunctionRef().getRefName(), script, embeddedSubProcess);

                    factory.connect(start.getId(), current.getId(), start.getId() + "_" + current.getId(), embeddedSubProcess);
                    start = current;
                }
            }
            EndNode embeddedEndNode = factory.endNode(idCounter.getAndIncrement(), "EmbeddedEnd", true, embeddedSubProcess);
            factory.connect(current.getId(), embeddedEndNode.getId(), current.getId() + "_" + embeddedEndNode.getId(), embeddedSubProcess);
        }
    }

}