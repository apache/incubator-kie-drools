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

import org.drools.process.core.timer.Timer;
import org.drools.ruleflow.core.RuleFlowNodeContainerFactory;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.MilestoneNode;

/**
 *
 * @author salaboy
 */
public class MilestoneNodeFactory extends NodeFactory {

    public MilestoneNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new MilestoneNode();
    }

    protected MilestoneNode getMilestoneNode() {
        return (MilestoneNode) getNode();
    }

    public MilestoneNodeFactory name(String name) {
        getNode().setName(name);
        return this;
    }

    public MilestoneNodeFactory onEntryAction(String dialect, String action) {
        if (getMilestoneNode().getActions(dialect) != null) {
            getMilestoneNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getMilestoneNode().setActions(MilestoneNode.EVENT_NODE_ENTER, actions);
        }
        return this;
    }

    public MilestoneNodeFactory onExitAction(String dialect, String action) {
        if (getMilestoneNode().getActions(dialect) != null) {
            getMilestoneNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getMilestoneNode().setActions(MilestoneNode.EVENT_NODE_EXIT, actions);
        }
        return this;
    }

    public MilestoneNodeFactory constraint(String constraint) {
        getMilestoneNode().setConstraint(constraint);
        return this;
    }

    public MilestoneNodeFactory timer(String delay, String period, String dialect, String action) {
    	Timer timer = new Timer();
    	timer.setDelay(delay);
    	timer.setPeriod(period);
    	getMilestoneNode().addTimer(timer, new DroolsConsequenceAction(dialect, action));
    	return this;
    }
    
}

