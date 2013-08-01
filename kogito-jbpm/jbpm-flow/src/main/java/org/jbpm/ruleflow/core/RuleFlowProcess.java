/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.ruleflow.core;


import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.impl.NodeContainerImpl;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;

public class RuleFlowProcess extends WorkflowProcessImpl {

    public static final String RULEFLOW_TYPE = "RuleFlow";

    private static final long serialVersionUID = 510l;
    
    public RuleFlowProcess() {
        setType(RULEFLOW_TYPE);
        // TODO create contexts on request ?
        VariableScope variableScope = new VariableScope();
        addContext(variableScope);
        setDefaultContext(variableScope);
        SwimlaneContext swimLaneContext = new SwimlaneContext();
        addContext(swimLaneContext);
        setDefaultContext(swimLaneContext);
        ExceptionScope exceptionScope = new ExceptionScope();
        addContext(exceptionScope);
        setDefaultContext(exceptionScope);
    }
    
    public VariableScope getVariableScope() {
        return (VariableScope) getDefaultContext(VariableScope.VARIABLE_SCOPE);
    }
    
    public SwimlaneContext getSwimlaneContext() {
        return (SwimlaneContext) getDefaultContext(SwimlaneContext.SWIMLANE_SCOPE);
    }

    public ExceptionScope getExceptionScope() {
        return (ExceptionScope) getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
    }
    
    public CompensationScope getCompensationScope() {
        return (CompensationScope) getDefaultContext(CompensationScope.COMPENSATION_SCOPE);
    }

    protected NodeContainer createNodeContainer() {
        return new WorkflowProcessNodeContainer();
    }
    
    public StartNode getStart() {
        Node[] nodes = getNodes();
        int startNodeIndex = -1;
        
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof StartNode) {
                // return start node that is not event based node
                if ((((StartNode) nodes[i]).getTriggers() == null 
                        || ((StartNode) nodes[i]).getTriggers().isEmpty())
                        && ((StartNode) nodes[i]).getTimer() == null) {
                    return (StartNode) nodes[i];
                }
                startNodeIndex = i;
            }
        }
        if (startNodeIndex > -1) {
            return (StartNode) nodes[startNodeIndex];
        }
        return null;
    }
    public List<StartNode> getTimerStart() {
        Node[] nodes = getNodes();

        List<StartNode> timerStartNodes = new ArrayList<StartNode>();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof StartNode) {
                // return start node that is not event based node
                if (((StartNode) nodes[i]).getTimer() != null) {
                    timerStartNodes.add((StartNode) nodes[i]);
                }
            }
        }

        return timerStartNodes;
    }

    private class WorkflowProcessNodeContainer extends NodeContainerImpl {
        
        private static final long serialVersionUID = 510l;

        protected void validateAddNode(Node node) {
            super.validateAddNode(node);
            StartNode startNode = getStart();
            if ((node instanceof StartNode) && (startNode != null && startNode.getTriggers() == null && startNode.getTimer() == null)) {
                // ignore start nodes that are event based
                if ((((StartNode) node).getTriggers() == null || ((StartNode) node).getTriggers().isEmpty()) && ((StartNode) node).getTimer() == null) {
                    throw new IllegalArgumentException(
                        "A RuleFlowProcess cannot have more than one start node!");
                }
            }
        }
        
    }

}
