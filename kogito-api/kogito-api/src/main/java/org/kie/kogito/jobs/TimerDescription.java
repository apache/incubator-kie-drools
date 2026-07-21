/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.jobs;

import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * A description of a timer scheduled either by a ProcessInstance or a NodeInstance.
 */
public class TimerDescription {

    private final String processId;
    private final String processInstanceId;
    private final String nodeInstanceId;
    private final String timerId;
    private final String description;

    private TimerDescription(String processId, String processInstanceId, String nodeInstanceId, String timerId, String description) {
        this.processId = processId;
        this.processInstanceId = processInstanceId;
        this.nodeInstanceId = nodeInstanceId;
        this.timerId = timerId;
        this.description = description;
    }

    public String getProcessId() {
        return processId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public String getTimerId() {
        return timerId;
    }

    public String getDescription() {
        return description;
    }

    public static class Builder {
        private final String processId;
        private final String processInstanceId;
        private String nodeInstanceId;
        private String timerId;
        private String timerDescription;

        private Builder(String processId, String processInstanceId) {
            this.processId = processId;
            this.processInstanceId = processInstanceId;
        }

        private Builder(String processId, String processInstanceId, String nodeInstanceId) {
            this(processId, processInstanceId);
            this.nodeInstanceId = nodeInstanceId;
        }

        public static Builder ofProcessInstance(ProcessInstance processInstance) {
            return new Builder(processInstance.getProcessId(), processInstance.getId());
        }

        public static Builder ofNodeInstance(NodeInstance nodeInstance) {
            return new Builder(nodeInstance.getProcessInstance().getProcessId(), nodeInstance.getProcessInstance().getId(), nodeInstance.getId());
        }

        public Builder timerId(String timerId) {
            this.timerId = timerId;
            return this;
        }

        public Builder timerDescription(String timerDescription) {
            this.timerDescription = timerDescription;
            return this;
        }

        public TimerDescription build() {
            return new TimerDescription(processId, processInstanceId, nodeInstanceId, timerId, timerDescription);
        }

    }
}
