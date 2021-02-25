/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process;

import java.util.HashMap;
import java.util.Map;

public class AbstractEventDescription<T> implements EventDescription<T> {

    protected String id;

    protected String event;
    protected String nodeId;
    protected String nodeName;
    protected String eventType;

    protected String nodeInstanceId;

    protected String processInstanceId;

    protected T dataType;

    protected Map<String, String> properties = new HashMap<>();

    public AbstractEventDescription(String event, String nodeId, String nodeName, String eventType, String nodeInstanceId, String processInstanceId, T dataType) {
        this.id = nodeInstanceId != null ? nodeInstanceId : nodeId;
        this.event = event;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.eventType = eventType;
        this.nodeInstanceId = nodeInstanceId;
        this.processInstanceId = processInstanceId;
        this.dataType = dataType;
    }

    public AbstractEventDescription(String event, String nodeId, String nodeName, String eventType, String nodeInstanceId, String processInstanceId, T dataType, Map<String, String> properties) {
        this.id = nodeInstanceId != null ? nodeInstanceId : nodeId;
        this.event = event;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.eventType = eventType;
        this.nodeInstanceId = nodeInstanceId;
        this.processInstanceId = processInstanceId;
        this.dataType = dataType;
        this.properties = properties;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public T getDataType() {
        return dataType;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "EventDesciption [event=" + event + ", nodeId=" + nodeId + ", nodeName=" + nodeName + ", eventType=" + eventType + ", nodeInstanceId=" + nodeInstanceId + ", processInstanceId="
                + processInstanceId +
                ", dataType=" + dataType + ", properties=" + properties + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
        result = prime * result + ((nodeName == null) ? 0 : nodeName.hashCode());
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        result = prime * result + ((processInstanceId == null) ? 0 : processInstanceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEventDescription other = (AbstractEventDescription) obj;
        if (dataType == null) {
            if (other.dataType != null)
                return false;
        } else if (!dataType.equals(other.dataType))
            return false;
        if (event == null) {
            if (other.event != null)
                return false;
        } else if (!event.equals(other.event))
            return false;
        if (nodeId == null) {
            if (other.nodeId != null)
                return false;
        } else if (!nodeId.equals(other.nodeId))
            return false;
        if (nodeName == null) {
            if (other.nodeName != null)
                return false;
        } else if (!nodeName.equals(other.nodeName))
            return false;
        if (eventType == null) {
            if (other.eventType != null)
                return false;
        } else if (!eventType.equals(other.eventType))
            return false;
        if (processInstanceId == null) {
            if (other.processInstanceId != null)
                return false;
        } else if (!processInstanceId.equals(other.processInstanceId))
            return false;
        return true;
    }

}
