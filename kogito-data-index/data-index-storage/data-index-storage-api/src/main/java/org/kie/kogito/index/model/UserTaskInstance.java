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

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserTaskInstance extends UserTaskInstanceMeta {

    private String processId;
    private String processVersion;
    private String rootProcessId;
    private String rootProcessVersion;
    private String rootProcessInstanceId;
    private ObjectNode inputs;
    private ObjectNode outputs;
    private String endpoint;
    private String externalReferenceId;
    private ZonedDateTime slaDueDate;
    private String userTaskId;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.processId = id;
        }
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String version) {
        if (version != null && !version.trim().isEmpty()) {
            this.processVersion = version;
        }
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public void setRootProcessId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.rootProcessId = id;
        }
    }

    public String getRootProcessVersion() {
        return rootProcessVersion;
    }

    public void setRootProcessVersion(String rootProcessVersion) {
        if (rootProcessVersion != null && !rootProcessVersion.trim().isEmpty()) {
            this.rootProcessVersion = rootProcessVersion;
        }
    }

    public String getUserTaskId() {
        return userTaskId;
    }

    public void setUserTaskId(String userTaskId) {
        this.userTaskId = userTaskId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String toString() {
        return "UserTaskInstance{" +
                "processId='" + processId + '\'' +
                ", processVersion='" + processVersion + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", rootProcessVersion='" + rootProcessVersion + '\'' +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                ", endpoint='" + endpoint + '\'' +
                ", slaDueDate=" + slaDueDate +
                "} " + super.toString();
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.rootProcessInstanceId = id;
        }
    }

    public ObjectNode getInputs() {
        return inputs;
    }

    public void setInputs(ObjectNode inputs) {
        this.inputs = inputs;
    }

    public ObjectNode getOutputs() {
        return outputs;
    }

    public void setOutputs(ObjectNode outputs) {
        this.outputs = outputs;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }

    public ZonedDateTime getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(ZonedDateTime slaDueDate) {
        this.slaDueDate = slaDueDate;
    }
}
