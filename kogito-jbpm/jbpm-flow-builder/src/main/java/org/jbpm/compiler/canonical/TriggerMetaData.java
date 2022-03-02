/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.compiler.canonical;

import java.util.Map;
import java.util.Objects;

import org.drools.core.util.StringUtils;
import org.jbpm.workflow.core.node.CompositeNode;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.kogito.internal.process.runtime.KogitoNode;

import static org.jbpm.ruleflow.core.Metadata.DATA_ONLY;
import static org.jbpm.ruleflow.core.Metadata.MAPPING_VARIABLE;
import static org.jbpm.ruleflow.core.Metadata.MESSAGE_TYPE;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_TYPE;

public class TriggerMetaData {

    public enum TriggerType {
        ConsumeMessage,
        ProduceMessage,
        Signal
    }

    // name of the trigger derived from message or signal
    private final String name;
    // type of the trigger e.g. message, signal, timer...
    private final TriggerType type;
    // data type of the event associated with this trigger
    private final String dataType;
    // reference in the model of the process the event should be mapped to
    private final String modelRef;
    // reference to owner of the trigger usually node
    private final String ownerId;
    // indicates if the whole event should be consumed or just the data
    private final boolean dataOnly;
    // the owner node
    private final Node node;

    public static TriggerMetaData of(Node node) {
        return of(node, (String) node.getMetaData().get(MAPPING_VARIABLE));
    }

    public static TriggerMetaData of(Node node, String mappingVariable) {
        Map<String, Object> nodeMetaData = node.getMetaData();
        return new TriggerMetaData(
                node,
                (String) nodeMetaData.get(TRIGGER_REF),
                TriggerType.valueOf((String) nodeMetaData.get(TRIGGER_TYPE)),
                (String) nodeMetaData.get(MESSAGE_TYPE),
                mappingVariable,
                getOwnerId(node),
                (Boolean) nodeMetaData.get(DATA_ONLY)).validate();
    }

    private TriggerMetaData(Node node, String name, TriggerType type, String dataType, String modelRef, String ownerId, Boolean dataOnly) {
        this.node = node;
        this.name = name;
        this.type = type;
        this.dataType = dataType;
        this.modelRef = modelRef;
        this.ownerId = ownerId;
        this.dataOnly = dataOnly == null || dataOnly.booleanValue();
    }

    public String getName() {
        return name;
    }

    public TriggerType getType() {
        return type;
    }

    public String getDataType() {
        return dataType;
    }

    public String getModelRef() {
        return modelRef;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Node getNode() {
        return node;
    }

    public boolean dataOnly() {
        return dataOnly;
    }

    private TriggerMetaData validate() {
        if (TriggerType.ConsumeMessage.equals(type) || TriggerType.ProduceMessage.equals(type)) {

            if (StringUtils.isEmpty(name) ||
                    StringUtils.isEmpty(dataType) ||
                    StringUtils.isEmpty(modelRef)) {
                throw new IllegalArgumentException("Message Trigger information is not complete " + this);
            }
        } else if (TriggerType.Signal.equals(type) && StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Signal Trigger information is not complete " + this);
        }

        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, modelRef, name, ownerId, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TriggerMetaData))
            return false;
        TriggerMetaData other = (TriggerMetaData) obj;
        return Objects.equals(dataType, other.dataType) && Objects.equals(modelRef, other.modelRef) && Objects.equals(
                name, other.name) && Objects.equals(ownerId, other.ownerId) && type == other.type;
    }

    @Override
    public String toString() {
        return "TriggerMetaData [name=" + name + ", type=" + type + ", dataType=" + dataType + ", modelRef=" +
                modelRef + ", ownerId=" + ownerId + "]";
    }

    private static String getOwnerId(Node node) {
        StringBuilder prefix = new StringBuilder();
        if (node instanceof KogitoNode) {
            NodeContainer container = ((KogitoNode) node).getParentContainer();
            while (container instanceof CompositeNode) {
                CompositeNode compositeNode = (CompositeNode) container;
                prefix.append(compositeNode.getId()).append('_');
                container = compositeNode.getParentContainer();
            }
        }
        return prefix.append(node.getId()).toString();
    }

}
