/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.workflow.core.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTransformer;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.kie.api.definition.process.Connection;

public class EventNode extends ExtendedNodeImpl implements EventNodeInterface,
        Mappable {

    private static final long serialVersionUID = 510l;

    private List<EventFilter> filters = new ArrayList<EventFilter>();
    private EventTransformer transformer;
    private String variableName;
    private String scope;
    private List<DataAssociation> outMapping = new LinkedList<>();

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void addEventFilter(EventFilter eventFilter) {
        filters.add(eventFilter);
    }

    public void removeEventFilter(EventFilter eventFilter) {
        filters.remove(eventFilter);
    }

    public List<EventFilter> getEventFilters() {
        return filters;
    }

    public void setEventFilters(List<EventFilter> filters) {
        this.filters = filters;
    }

    public String getType() {
        for (EventFilter filter : filters) {
            if (filter instanceof EventTypeFilter) {
                return ((EventTypeFilter) filter).getType();
            }
        }
        return null;
    }

    @Override
    public boolean acceptsEvent(String type, Object event, Function<String, Object> resolver) {
        for (EventFilter filter : filters) {
            if (!filter.acceptsEvent(type, event, resolver)) {
                return false;
            }
        }
        return true;
    }

    public void setEventTransformer(EventTransformer transformer) {
        this.transformer = transformer;
    }

    public EventTransformer getEventTransformer() {
        return transformer;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getMetaData().get("UniqueId") + ", " + connection.getTo().getName()
                            + "] only accepts default incoming connection type!");
        }
        if (getFrom() != null && !"true".equals(System.getProperty("jbpm.enable.multi.con"))) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getMetaData().get("UniqueId") + ", " + connection.getTo().getName()
                            + "] cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getFrom().getMetaData().get("UniqueId") + ", " + connection.getFrom().getName()
                            + "] only accepts default outgoing connection type!");
        }
        if (getTo() != null && !"true".equals(System.getProperty("jbpm.enable.multi.con"))) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getFrom().getMetaData().get("UniqueId") + ", " + connection.getFrom().getName()
                            + "] cannot have more than one outgoing connection!");
        }
    }

    @Override
    public void addInAssociation(DataAssociation dataAssociation) {
        throwUnsupported();
    }

    @Override
    public List<DataAssociation> getInAssociations() {
        return Collections.emptyList();
    }

    @Override
    public void addOutAssociation(DataAssociation dataAssociation) {
        outMapping.add(dataAssociation);
    }

    @Override
    public List<DataAssociation> getOutAssociations() {
        return Collections.unmodifiableList(outMapping);
    }

    @Override
    public void addOutMapping(String parameterName, String variableName) {
        outMapping.add(new DataAssociation(variableName, parameterName, null, null));
    }

    private void throwUnsupported() {
        throw new IllegalArgumentException("An event node [" + this.getMetaData("UniqueId") + ", " + this.getName() + "] does not support input mappings");
    }

    @Override
    public String getOutMapping(String parameterName) {
        return getOutMappings().get(parameterName);
    }

    @Override
    public Map<String, String> getOutMappings() {
        Map<String, String> in = new HashMap<>();
        for (DataAssociation a : outMapping) {
            if (a.getSources().size() == 1 && (a.getAssignments() == null || a.getAssignments().isEmpty()) && a.getTransformation() == null) {
                in.put(a.getTarget(), a.getSources().get(0));
            }
        }
        return in;
    }

    @Override
    public void setOutMappings(Map<String, String> outMapping) {
        this.outMapping = new LinkedList<>();
        for (Map.Entry<String, String> entry : outMapping.entrySet()) {
            addOutMapping(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void addInMapping(String parameterName, String variableName) {
        throwUnsupported();
    }

    @Override
    public String getInMapping(String parameterName) {
        return null;
    }

    @Override
    public Map<String, String> getInMappings() {
        return Collections.emptyMap();
    }

    @Override
    public void setInMappings(Map<String, String> inMapping) {
        throwUnsupported();
    }

}
