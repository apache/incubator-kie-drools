/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.process.events;

import java.util.Objects;

import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.kogito.event.DataEventAttrBuilder;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.cloudevents.CloudEventMeta;

/**
 * Representation of {@link CloudEventMeta} with information about the generated process.
 */
public class ProcessCloudEventMeta extends CloudEventMeta {

    final String processId;
    final String triggerName;

    public ProcessCloudEventMeta(String processId, TriggerMetaData trigger) {
        this.processId = processId;
        this.triggerName = trigger.getName();

        this.setKind(TriggerMetaData.TriggerType.ProduceMessage.equals(trigger.getType()) ? EventKind.PRODUCED : EventKind.CONSUMED);
        this.setType(this.getKind() == EventKind.PRODUCED ? DataEventAttrBuilder.toType(triggerName, processId) : triggerName);
        this.setSource(this.getKind() == EventKind.PRODUCED ? DataEventAttrBuilder.toSource(processId) : "");
    }

    public String getProcessId() {
        return processId;
    }

    public String getTriggerName() {
        return triggerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProcessCloudEventMeta that = (ProcessCloudEventMeta) o;
        return processId.equals(that.processId) && triggerName.equals(that.triggerName) && getKind() == that.getKind() && getType().equals(that.getType()) && getSource().equals(that.getSource());
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId, triggerName, getKind(), getType(), getSource());
    }

    @Override
    public String toString() {
        return "ProcessCloudEventMeta{" +
                "processId='" + processId + '\'' +
                ", triggerName='" + triggerName + '\'' +
                ", source='" + getSource() + '\'' +
                ", type='" + getType() + '\'' +
                ", kind='" + getKind() + '\'' +
                '}';
    }
}
