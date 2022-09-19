/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.process;

import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessDataEvent<T> extends AbstractDataEvent<T> {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.PROCESS_INSTANCE_VERSION)
    private String kogitoProcessInstanceVersion;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_PARENT_PROCESS_INSTANCE_ID)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoParentProcessInstanceId;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoProcessInstanceState;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_REFERENCE_ID)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoReferenceId;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_START_FROM_NODE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoStartFromNode;

    @JsonProperty(CloudEventExtensionConstants.BUSINESS_KEY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoBusinessKey;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_TYPE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoProcessType;

    public ProcessDataEvent() {
    }

    public ProcessDataEvent(String source,
            T body,
            String kogitoProcessInstanceId,
            String kogitoProcessInstanceVersion,
            String kogitoParentProcessInstanceId,
            String kogitoRootProcessInstanceId,
            String kogitoProcessId,
            String kogitoRootProcessId,
            String kogitoProcessInstanceState,
            String kogitoAddons,
            String kogitoProcessType) {
        this(null,
                source,
                body,
                kogitoProcessInstanceId,
                kogitoProcessInstanceVersion,
                kogitoParentProcessInstanceId,
                kogitoRootProcessInstanceId,
                kogitoProcessId,
                kogitoRootProcessId,
                kogitoProcessInstanceState,
                kogitoAddons,
                kogitoProcessType);
    }

    public ProcessDataEvent(String type,
            String source,
            T body,
            String kogitoProcessInstanceId,
            String kogitoProcessInstanceVersion,
            String kogitoParentProcessInstanceId,
            String kogitoRootProcessInstanceId,
            String kogitoProcessId,
            String kogitoRootProcessId,
            String kogitoProcessInstanceState,
            String kogitoAddons,
            String kogitoProcessType) {
        this(type,
                source,
                body,
                kogitoProcessInstanceId,
                kogitoProcessInstanceVersion,
                kogitoParentProcessInstanceId,
                kogitoRootProcessInstanceId,
                kogitoProcessId,
                kogitoRootProcessId,
                kogitoProcessInstanceState,
                kogitoAddons,
                kogitoProcessType,
                null);
    }

    public ProcessDataEvent(String type,
            String source,
            T body,
            String kogitoProcessInstanceId,
            String kogitoProcessInstanceVersion,
            String kogitoParentProcessInstanceId,
            String kogitoRootProcessInstanceId,
            String kogitoProcessId,
            String kogitoRootProcessId,
            String kogitoProcessInstanceState,
            String kogitoAddons,
            String kogitoProcessType,
            String kogitoReferenceId) {
        super(type,
                source,
                body,
                kogitoProcessInstanceId,
                kogitoRootProcessInstanceId,
                kogitoProcessId,
                kogitoRootProcessId,
                kogitoAddons);
        this.kogitoProcessInstanceVersion = kogitoProcessInstanceVersion;
        this.kogitoParentProcessInstanceId = kogitoParentProcessInstanceId;
        this.kogitoProcessInstanceState = kogitoProcessInstanceState;
        this.kogitoReferenceId = kogitoReferenceId;
        this.kogitoProcessType = kogitoProcessType;
    }

    public String getKogitoParentProcessInstanceId() {
        return kogitoParentProcessInstanceId;
    }

    public String getKogitoProcessInstanceState() {
        return kogitoProcessInstanceState;
    }

    public String getKogitoReferenceId() {
        return this.kogitoReferenceId;
    }

    public String getKogitoBusinessKey() {
        return this.kogitoBusinessKey;
    }

    public void setKogitoStartFromNode(String kogitoStartFromNode) {
        this.kogitoStartFromNode = kogitoStartFromNode;
    }

    public String getKogitoStartFromNode() {
        return this.kogitoStartFromNode;
    }

    public String getKogitoProcessInstanceVersion() {
        return kogitoProcessInstanceVersion;
    }

    public String getKogitoProcessType() {
        return kogitoProcessType;
    }

    public void setKogitoProcessInstanceVersion(String kogitoProcessInstanceVersion) {
        this.kogitoProcessInstanceVersion = kogitoProcessInstanceVersion;
    }

    public void setKogitoParentProcessInstanceId(String kogitoParentProcessInstanceId) {
        this.kogitoParentProcessInstanceId = kogitoParentProcessInstanceId;
    }

    public void setKogitoProcessInstanceState(String kogitoProcessInstanceState) {
        this.kogitoProcessInstanceState = kogitoProcessInstanceState;
    }

    public void setKogitoReferenceId(String kogitoReferenceId) {
        this.kogitoReferenceId = kogitoReferenceId;
    }

    public void setKogitoBusinessKey(String kogitoBusinessKey) {
        this.kogitoBusinessKey = kogitoBusinessKey;
    }

    public void setKogitoProcessType(String kogitoProcessType) {
        this.kogitoProcessType = kogitoProcessType;
    }

    @Override
    public void addExtensionAttribute(String name, Object value) {
        switch (name) {
            case CloudEventExtensionConstants.PROCESS_REFERENCE_ID:
                this.kogitoReferenceId = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_INSTANCE_VERSION:
                this.kogitoProcessInstanceVersion = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_PARENT_PROCESS_INSTANCE_ID:
                this.kogitoParentProcessInstanceId = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID:
                this.kogitoProcessInstanceState = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_START_FROM_NODE:
                this.kogitoStartFromNode = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_TYPE:
                this.kogitoProcessType = (String) value;
                break;
            case CloudEventExtensionConstants.BUSINESS_KEY:
                this.kogitoBusinessKey = (String) value;
                break;
            default:
                super.addExtensionAttribute(name, value);
        }
    }

    @Override
    public String toString() {
        return "ProcessDataEvent{" +
                "kogitoProcessInstanceVersion='" + kogitoProcessInstanceVersion + '\'' +
                ", kogitoParentProcessInstanceId='" + kogitoParentProcessInstanceId + '\'' +
                ", kogitoProcessInstanceState='" + kogitoProcessInstanceState + '\'' +
                ", kogitoReferenceId='" + kogitoReferenceId + '\'' +
                ", kogitoStartFromNode='" + kogitoStartFromNode + '\'' +
                ", kogitoBusinessKey='" + kogitoBusinessKey + '\'' +
                ", kogitoProcessType='" + kogitoProcessType + '\'' +
                "} " + super.toString();
    }

}
