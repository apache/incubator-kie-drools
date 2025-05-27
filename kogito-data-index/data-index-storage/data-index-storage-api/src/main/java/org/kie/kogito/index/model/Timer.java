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
package org.kie.kogito.index.model;

import org.kie.kogito.jobs.TimerDescription;

public class Timer {

    private String processId;
    private String processInstanceId;
    private String nodeInstanceId;
    private String timerId;
    private String description;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getTimerId() {
        return timerId;
    }

    public void setTimerId(String timerId) {
        this.timerId = timerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Timer{" +
                "processId='" + processId + '\'' +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", nodeInstanceId='" + nodeInstanceId + '\'' +
                ", timerId='" + timerId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static Timer from(TimerDescription timerDescription) {
        Timer timer = new Timer();
        timer.setProcessId(timerDescription.getProcessId());
        timer.setProcessInstanceId(timerDescription.getProcessInstanceId());
        timer.setNodeInstanceId(timerDescription.getNodeInstanceId());
        timer.setTimerId(timerDescription.getTimerId());
        timer.setDescription(timerDescription.getDescription());
        return timer;
    }
}
