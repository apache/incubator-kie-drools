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

package org.drools.ruleflow.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.process.core.timer.Timer;
import org.drools.ruleflow.core.RuleFlowNodeContainerFactory;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.HumanTaskNode;
import org.drools.workflow.core.node.MilestoneNode;

/**
 *
 * @author salaboy
 */
public class HumanTaskNodeFactory extends NodeFactory {

    public HumanTaskNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new HumanTaskNode();
    }
    
    protected HumanTaskNode getHumanTaskNode() {
    	return (HumanTaskNode) getNode();
    }

    public HumanTaskNodeFactory name(String name) {
        getNode().setName(name);
        return this;
    }
    
    public HumanTaskNodeFactory taskName(String taskName) {
    	Work work = getHumanTaskNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getHumanTaskNode().setWork(work);
    	}
    	work.setParameter("TaskName", taskName);
    	return this;
    }
    
    public HumanTaskNodeFactory actorId(String actorId) {
    	Work work = getHumanTaskNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getHumanTaskNode().setWork(work);
    	}
    	work.setParameter("ActorId", actorId);
    	return this;
    }
    
    public HumanTaskNodeFactory priority(String priority) {
    	Work work = getHumanTaskNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getHumanTaskNode().setWork(work);
    	}
    	work.setParameter("Priority", priority);
    	return this;
    }
    
    public HumanTaskNodeFactory comment(String comment) {
    	Work work = getHumanTaskNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getHumanTaskNode().setWork(work);
    	}
    	work.setParameter("Comment", comment);
    	return this;
    }
    
    public HumanTaskNodeFactory skippable(boolean skippable) {
    	Work work = getHumanTaskNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getHumanTaskNode().setWork(work);
    	}
    	work.setParameter("Skippable", Boolean.toString(skippable));
    	return this;
    }
    
    public HumanTaskNodeFactory content(String content) {
    	Work work = getHumanTaskNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getHumanTaskNode().setWork(work);
    	}
    	work.setParameter("Content", content);
    	return this;
    }
    
    public HumanTaskNodeFactory inMapping(String parameterName, String variableName) {
    	getHumanTaskNode().addInMapping(parameterName, variableName);
        return this;
    }

    public HumanTaskNodeFactory outMapping(String parameterName, String variableName) {
    	getHumanTaskNode().addOutMapping(parameterName, variableName);
        return this;
    }

    public HumanTaskNodeFactory waitForCompletion(boolean waitForCompletion) {
    	getHumanTaskNode().setWaitForCompletion(waitForCompletion);
        return this;
    }

    public HumanTaskNodeFactory swimlane(String swimlane) {
    	getHumanTaskNode().setSwimlane(swimlane);
        return this;
    }

    public HumanTaskNodeFactory onEntryAction(String dialect, String action) {
        if (getHumanTaskNode().getActions(dialect) != null) {
        	getHumanTaskNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getHumanTaskNode().setActions(MilestoneNode.EVENT_NODE_ENTER, actions);
        }
        return this;
    }

    public HumanTaskNodeFactory onExitAction(String dialect, String action) {
        if (getHumanTaskNode().getActions(dialect) != null) {
        	getHumanTaskNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getHumanTaskNode().setActions(MilestoneNode.EVENT_NODE_EXIT, actions);
        }
        return this;
    }

    public HumanTaskNodeFactory timer(String delay, String period, String dialect, String action) {
    	Timer timer = new Timer();
    	timer.setDelay(delay);
    	timer.setPeriod(period);
    	getHumanTaskNode().addTimer(timer, new DroolsConsequenceAction(dialect, action));
    	return this;
    }
    
}

