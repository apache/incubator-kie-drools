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

import java.util.Objects;

import org.drools.core.util.StringUtils;

public class TriggerMetaData {

    public enum TriggerType {
        ConsumeMessage,
        ProduceMessage,
        Signal
    }

    // name of the trigger derived from message or signal
    private String name;
    // type of the trigger e.g. message, signal, timer...
    private TriggerType type;
    // data type of the event associated with this trigger
    private String dataType;
    // reference in the model of the process the event should be mapped to
    private String modelRef;
    // reference to owner of the trigger usually node
    private String ownerId;

    public TriggerMetaData(String name, String type, String dataType, String modelRef, String ownerId) {
        super();
        this.name = name;
        this.type = TriggerType.valueOf(type);
        this.dataType = dataType;
        this.modelRef = modelRef;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TriggerType getType() {
        return type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getModelRef() {
        return modelRef;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public TriggerMetaData validate() {
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
}
