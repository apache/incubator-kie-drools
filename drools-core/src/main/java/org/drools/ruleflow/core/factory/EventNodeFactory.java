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

import org.drools.process.core.event.EventFilter;
import org.drools.process.core.event.EventTransformer;
import org.drools.process.core.event.EventTypeFilter;
import org.drools.ruleflow.core.RuleFlowNodeContainerFactory;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.node.EventNode;

/**
 *
 * @author salaboy
 */
public class EventNodeFactory extends NodeFactory {

    public EventNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new EventNode();
    }
    
    protected EventNode getEventNode() {
    	return(EventNode) getNode();
    }

    public EventNodeFactory name(String name) {
        getNode().setName(name);
        return this;
    }

    public EventNodeFactory variableName(String variableName) {
    	getEventNode().setVariableName(variableName);
        return this;
    }

    public EventNodeFactory eventFilter(EventFilter eventFilter) {
    	getEventNode().addEventFilter(eventFilter);
        return this;
    }

    public EventNodeFactory eventType(String eventType) {
    	EventTypeFilter filter = new EventTypeFilter();
    	filter.setType(eventType);
    	return eventFilter(filter);
    }

    public EventNodeFactory eventTransformer(EventTransformer transformer) {
    	getEventNode().setEventTransformer(transformer);
        return this;
    }

    public EventNodeFactory scope(String scope) {
    	getEventNode().setScope(scope);
        return this;
    }
}
