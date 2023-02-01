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
package org.kie.kogito.jobs.api.event;

import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public abstract class ProcessInstanceContextJobCloudEvent<T> extends JobCloudEvent<T> {

    //Process context extensions
    @JsonProperty(CloudEventExtensionConstants.PROCESS_INSTANCE_ID)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String processInstanceId;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_ID)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String processId;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_INSTANCE_ID)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String rootProcessInstanceId;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String rootProcessId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.ADDONS)
    private String kogitoAddons;

    protected ProcessInstanceContextJobCloudEvent() {
        // marshalling constructor.
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public String getKogitoAddons() {
        return kogitoAddons;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setRootProcessInstanceId(String rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
    }

    public void setRootProcessId(String rootProcessId) {
        this.rootProcessId = rootProcessId;
    }

    public void setKogitoAddons(String kogitoAddons) {
        this.kogitoAddons = kogitoAddons;
    }

    @Override
    public String toString() {
        return "ProcessInstanceContextJobCloudEvent{" +
                "processInstanceId='" + processInstanceId + '\'' +
                ", processId='" + processId + '\'' +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", kogitoAddons='" + kogitoAddons + '\'' +
                "} " + super.toString();
    }
}
