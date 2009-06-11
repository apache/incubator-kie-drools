/*
 * Copyright 2008 JBoss Inc
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
package org.drools.ruleflow.core.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.drools.process.core.impl.WorkImpl;
import org.drools.process.core.timer.Timer;
import org.drools.ruleflow.core.RuleFlowNodeContainerFactory;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.WorkItemNode;

/**
 *
 * @author salaboy
 */
public class WorkItemNodeFactory extends NodeFactory {

    public WorkItemNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new ActionNode();
    }

    protected WorkItemNode getWorkItemNode() {
        return (WorkItemNode) getNode();
    }

    public WorkItemNodeFactory name(String name) {
        getNode().setName(name);
        return this;
    }
    
    public WorkItemNodeFactory waitForCompletion(boolean waitForCompletion) {
    	getWorkItemNode().setWaitForCompletion(waitForCompletion);
    	return this;
    }
    
    public WorkItemNodeFactory inMapping(String parameterName, String variableName) {
    	getWorkItemNode().addInMapping(parameterName, variableName);
    	return this;
    }

    public WorkItemNodeFactory outMapping(String parameterName, String variableName) {
    	getWorkItemNode().addOutMapping(parameterName, variableName);
    	return this;
    }
    
    public WorkItemNodeFactory workName(String name) {
    	Work work = getWorkItemNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getWorkItemNode().setWork(work);
    	}
    	work.setName(name);
    	return this;
    }

    public WorkItemNodeFactory workParameter(String name, Object value) {
    	Work work = getWorkItemNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getWorkItemNode().setWork(work);
    	}
    	work.setParameter(name, value);
    	return this;
    }
    
    public WorkItemNodeFactory workParameterDefinition(String name, DataType dataType) {
    	Work work = getWorkItemNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getWorkItemNode().setWork(work);
    	}
    	Set<ParameterDefinition> parameterDefinitions = work.getParameterDefinitions();
    	parameterDefinitions.add(new ParameterDefinitionImpl(name, dataType));
    	work.setParameterDefinitions(parameterDefinitions);
    	return this;
    }

    public WorkItemNodeFactory onEntryAction(String dialect, String action) {
        if (getWorkItemNode().getActions(dialect) != null) {
        	getWorkItemNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getWorkItemNode().setActions(MilestoneNode.EVENT_NODE_ENTER, actions);
        }
        return this;
    }

    public WorkItemNodeFactory onExitAction(String dialect, String action) {
        if (getWorkItemNode().getActions(dialect) != null) {
        	getWorkItemNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getWorkItemNode().setActions(MilestoneNode.EVENT_NODE_EXIT, actions);
        }
        return this;
    }

    public WorkItemNodeFactory timer(String delay, String period, String dialect, String action) {
    	Timer timer = new Timer();
    	timer.setDelay(delay);
    	timer.setPeriod(period);
    	getWorkItemNode().addTimer(timer, new DroolsConsequenceAction(dialect, action));
    	return this;
    }
    
}
