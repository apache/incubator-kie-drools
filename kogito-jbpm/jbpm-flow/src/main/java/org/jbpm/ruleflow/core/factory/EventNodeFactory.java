/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.ruleflow.core.factory;

import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTransformer;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.EventNode;

/**
 *
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
