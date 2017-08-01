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
import org.jbpm.workflow.core.node.BoundaryEventNode;

public class BoundaryEventNodeFactory extends NodeFactory {

    private NodeContainer nodeContainer;

    private String attachedToUniqueId;

    public BoundaryEventNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
        this.nodeContainer = nodeContainer;
    }

    public BoundaryEventNodeFactory attachedTo(long attachedToId) {
        attachedToUniqueId = (String)nodeContainer.getNode(attachedToId).getMetaData().get("UniqueId");
        getBoundaryEventNode().setAttachedToNodeId(attachedToUniqueId);
        getBoundaryEventNode().setMetaData("AttachedTo", attachedToUniqueId);
        return this;
    }

    protected Node createNode() {
        return new BoundaryEventNode();
    }

    protected BoundaryEventNode getBoundaryEventNode() {
        return(BoundaryEventNode) getNode();
    }

    public BoundaryEventNodeFactory name(String name) {
        getNode().setName(name);
        return this;
    }

    public BoundaryEventNodeFactory variableName(String variableName) {
        getBoundaryEventNode().setVariableName(variableName);
        return this;
    }

    public BoundaryEventNodeFactory eventFilter(EventFilter eventFilter) {
        getBoundaryEventNode().addEventFilter(eventFilter);
        return this;
    }

    public BoundaryEventNodeFactory eventType(String eventType) {
        EventTypeFilter filter = new EventTypeFilter();
        filter.setType(eventType);
        return eventFilter(filter);
    }

    public BoundaryEventNodeFactory eventType(String eventTypePrefix, String eventTypeSurffix) {
        if (attachedToUniqueId == null) {
            throw new IllegalStateException("attachedTo() must be called before");
        }
        EventTypeFilter filter = new EventTypeFilter();
        filter.setType(eventTypePrefix + "-" + attachedToUniqueId + "-" + eventTypeSurffix);
        return eventFilter(filter);
    }

    public BoundaryEventNodeFactory timeCycle(String timeCycle) {
        eventType("Timer", timeCycle);
        setMetaData("TimeCycle", timeCycle);
        return this;
    }

    public BoundaryEventNodeFactory timeCycle(String timeCycle, String language) {
        eventType("Timer", timeCycle);
        setMetaData("TimeCycle", timeCycle);
        setMetaData("Language", language);
        return this;
    }

    public BoundaryEventNodeFactory timeDuration(String timeDuration) {
        eventType("Timer", timeDuration);
        setMetaData("TimeDuration", timeDuration);
        return this;
    }

    public BoundaryEventNodeFactory cancelActivity(boolean cancelActivity) {
        setMetaData("CancelActivity", cancelActivity);
        return this;
    }

    public BoundaryEventNodeFactory eventTransformer(EventTransformer transformer) {
        getBoundaryEventNode().setEventTransformer(transformer);
        return this;
    }

    public BoundaryEventNodeFactory scope(String scope) {
        getBoundaryEventNode().setScope(scope);
        return this;
    }

    public BoundaryEventNodeFactory setMetaData(String name,Object value) {
        getBoundaryEventNode().setMetaData(name, value);
        return this;
    }
}
