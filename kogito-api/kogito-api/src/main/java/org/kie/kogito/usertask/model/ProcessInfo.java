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
package org.kie.kogito.usertask.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessInfo {
    private String processInstanceId;
    private String processId;
    private String processVersion;

    private String parentProcessInstanceId;
    private String rootProcessId;
    private String rootProcessInstanceId;

    private ProcessInfo() {
    }

    private ProcessInfo(String processInstanceId, String processId, String processVersion) {
        this.processInstanceId = processInstanceId;
        this.processId = processId;
        this.processVersion = processVersion;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public String getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(String parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public void setRootProcessId(String rootProcessId) {
        this.rootProcessId = rootProcessId;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(String rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String processId;
        private String processVersion;
        private String processInstanceId;

        private String parentProcessInstanceId;
        private String rootProcessId;
        private String rootProcessInstanceId;

        public Builder withProcessId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder withProcessVersion(String processVersion) {
            this.processVersion = processVersion;
            return this;
        }

        public Builder withProcessInstanceId(String processInstanceId) {
            this.processInstanceId = processInstanceId;
            return this;
        }

        public Builder withParentProcessInstanceId(String parentProcessInstanceId) {
            this.parentProcessInstanceId = parentProcessInstanceId;
            return this;
        }

        public Builder withRootProcessId(String rootProcessId) {
            this.rootProcessId = rootProcessId;
            return this;
        }

        public Builder withRootProcessInstanceId(String rootProcessInstanceId) {
            this.rootProcessInstanceId = rootProcessInstanceId;
            return this;
        }

        public ProcessInfo build() {
            ProcessInfo processInfo = new ProcessInfo(processInstanceId, processId, processVersion);

            processInfo.setRootProcessInstanceId(rootProcessInstanceId);
            processInfo.setRootProcessId(rootProcessId);
            processInfo.setParentProcessInstanceId(parentProcessInstanceId);
            return processInfo;
        }
    }

}
