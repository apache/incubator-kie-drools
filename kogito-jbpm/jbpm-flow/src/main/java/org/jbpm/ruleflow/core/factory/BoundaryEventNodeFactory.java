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

import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.CompensationHandler;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTransformer;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.event.NonAcceptingEventTypeFilter;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.BoundaryEventNode;

import static org.jbpm.ruleflow.core.Metadata.ATTACHED_TO;

public class BoundaryEventNodeFactory extends EventNodeFactory {

    public static final String METHOD_ATTACHED_TO = "attachedTo";
    public static final String METHOD_ADD_COMPENSATION_HANDLER = "addCompensationHandler";

    private NodeContainer nodeContainer;

    private String attachedToUniqueId;

    public BoundaryEventNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
        this.nodeContainer = nodeContainer;
    }

    protected BoundaryEventNode getBoundaryEventNode() {
        return (BoundaryEventNode) getNode();
    }

    @Override
    protected Node createNode() {
        return new BoundaryEventNode();
    }

    @Override
    public BoundaryEventNodeFactory name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public BoundaryEventNodeFactory variableName(String variableName) {
        super.variableName(variableName);
        return this;
    }

    @Override
    public BoundaryEventNodeFactory eventFilter(EventFilter eventFilter) {
        super.eventFilter(eventFilter);
        return this;
    }

    @Override
    public BoundaryEventNodeFactory eventTransformer(EventTransformer transformer) {
        super.eventTransformer(transformer);
        return this;
    }

    @Override
    public BoundaryEventNodeFactory scope(String scope) {
        super.scope(scope);
        return this;
    }

    @Override
    public BoundaryEventNodeFactory metaData(String name, Object value) {
        super.metaData(name, value);
        return this;
    }

    public BoundaryEventNodeFactory attachedTo(long attachedToId) {
        return attachedTo((String) nodeContainer.getNode(attachedToId).getMetaData().get("UniqueId"));
    }

    public BoundaryEventNodeFactory attachedTo(String attachedToId) {
        attachedToUniqueId = attachedToId;
        getBoundaryEventNode().setAttachedToNodeId(attachedToUniqueId);
        getBoundaryEventNode().setMetaData(ATTACHED_TO, attachedToUniqueId);
        return this;
    }

    public BoundaryEventNodeFactory addCompensationHandler(String compensationHandlerId) {
        if(!(nodeContainer instanceof ContextContainer)) {
            return this;
        }
        ContextContainer contextContainer = (ContextContainer) nodeContainer;
        Context compensationScope = contextContainer.getDefaultContext(CompensationScope.COMPENSATION_SCOPE);
        if(compensationScope instanceof CompensationScope) {
            CompensationHandler handler = new CompensationHandler();
            handler.setNode(getBoundaryEventNode());
            ((CompensationScope) compensationScope).setExceptionHandler(compensationHandlerId, handler);
        }
        return this;
    }

    @Override
    public BoundaryEventNodeFactory eventType(String eventType) {
        if(Metadata.EVENT_TYPE_COMPENSATION.equalsIgnoreCase(eventType)) {
            EventTypeFilter eventFilter = new NonAcceptingEventTypeFilter();
            eventFilter.setType(eventType);
            eventFilter(eventFilter);
        } else {
            super.eventType(eventType);
        }
        return this;
    }

    public BoundaryEventNodeFactory eventType(String eventTypePrefix, String eventTypeSuffix) {
        if (attachedToUniqueId == null) {
            throw new IllegalStateException("attachedTo() must be called before");
        }
        EventTypeFilter filter = new EventTypeFilter();
        filter.setType(eventTypePrefix + "-" + attachedToUniqueId + "-" + eventTypeSuffix);
        super.eventFilter(filter);
        return this;
    }
}
