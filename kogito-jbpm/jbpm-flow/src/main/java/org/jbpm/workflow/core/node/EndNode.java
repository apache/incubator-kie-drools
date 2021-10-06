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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.kie.api.definition.process.Connection;

import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;

/**
 * Default implementation of an end node.
 * 
 */
public class EndNode extends ExtendedNodeImpl implements Mappable {

    public static final int CONTAINER_SCOPE = 0;
    public static final int PROCESS_SCOPE = 1;

    private static final String[] EVENT_TYPES = new String[] { EVENT_NODE_ENTER };
    private static final long serialVersionUID = 510l;

    private boolean terminate = true;
    private int scope = CONTAINER_SCOPE;
    private List<DataAssociation> inMapping = new LinkedList<>();

    public boolean isTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    public String[] getActionTypes() {
        return EVENT_TYPES;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getMetaData().get(UNIQUE_ID) + ", " + connection.getTo().getName()
                            + "] only accepts default incoming connection type!");
        }
        if (getFrom() != null && !"true".equals(System.getProperty("jbpm.enable.multi.con"))) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getMetaData().get(UNIQUE_ID) + ", " + connection.getTo().getName()
                            + "] cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
                "An end node does not have an outgoing connection!");
    }

    public void validateRemoveOutgoingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
                "An end node does not have an outgoing connection!");
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public int getScope() {
        return scope;
    }

    @Override
    public void addInAssociation(DataAssociation dataAssociation) {
        inMapping.add(dataAssociation);
    }

    @Override
    public List<DataAssociation> getInAssociations() {
        return Collections.unmodifiableList(inMapping);
    }

    @Override
    public void addOutAssociation(DataAssociation dataAssociation) {
        throwUnsupported();
    }

    @Override
    public List<DataAssociation> getOutAssociations() {
        return Collections.emptyList();
    }

    @Override
    public void addOutMapping(String parameterName, String variableName) {
        throwUnsupported();
    }

    private void throwUnsupported() {
        throw new IllegalArgumentException("An end event [" + this.getMetaData(UNIQUE_ID) + ", " + this.getName() + "] does not support output mappings");
    }

    @Override
    public String getOutMapping(String parameterName) {
        return null;
    }

    @Override
    public Map<String, String> getOutMappings() {
        return Collections.emptyMap();
    }

    @Override
    public void setOutMappings(Map<String, String> outMapping) {
        throwUnsupported();
    }

    @Override
    public void addInMapping(String parameterName, String variableName) {
        inMapping.add(new DataAssociation(variableName, parameterName, null, null));
    }

    @Override
    public String getInMapping(String parameterName) {
        return getInMappings().get(parameterName);
    }

    @Override
    public Map<String, String> getInMappings() {
        Map<String, String> in = new HashMap<>();
        for (DataAssociation a : inMapping) {
            if (a.getSources().size() == 1 && (a.getAssignments() == null || a.getAssignments().isEmpty()) && a.getTransformation() == null) {
                in.put(a.getTarget(), a.getSources().get(0));
            }
        }
        return in;
    }

    @Override
    public void setInMappings(Map<String, String> inMapping) {
        this.inMapping = new LinkedList<>();
        for (Map.Entry<String, String> entry : inMapping.entrySet()) {
            addInMapping(entry.getKey(), entry.getValue());
        }
    }
}
