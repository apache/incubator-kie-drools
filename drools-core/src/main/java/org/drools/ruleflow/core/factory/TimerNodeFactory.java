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

import org.drools.process.core.timer.Timer;
import org.drools.ruleflow.core.RuleFlowNodeContainerFactory;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.node.TimerNode;

/**
 *
 * @author salaboy
 */
public class TimerNodeFactory extends NodeFactory {

    public TimerNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new TimerNode();
    }
    
    protected TimerNode getTimerNode() {
    	return (TimerNode) getNode();
    }

    public TimerNodeFactory name(String name) {
        getNode().setName(name);
        return this;
    }
    
    public TimerNodeFactory delay(String delay) {
    	Timer timer = getTimerNode().getTimer();
    	if (timer == null) {
    		timer = new Timer();
    		getTimerNode().setTimer(timer);
    	}
    	timer.setDelay(delay);
    	return this;
    }
    
    public TimerNodeFactory period(String period) {
    	Timer timer = getTimerNode().getTimer();
    	if (timer == null) {
    		timer = new Timer();
    		getTimerNode().setTimer(timer);
    	}
    	timer.setPeriod(period);
    	return this;
    }
    
}
