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
package org.kie.kogito.event.process;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessInstanceVariableDataEvent extends ProcessInstanceDataEvent<ProcessInstanceVariableEventBody> {

    private static final Set<String> INTERNAL_EXTENSION_ATTRIBUTES = Collections.singleton(CloudEventExtensionConstants.KOGITO_VARIABLE_NAME);

    public static final String VAR_TYPE = "ProcessInstanceVariableDataEvent";

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.KOGITO_VARIABLE_NAME)
    private String kogitoVariableName;

    public ProcessInstanceVariableDataEvent() {
        this.setType(VAR_TYPE);
    }

    public ProcessInstanceVariableDataEvent(String source, String addons, String identity, Map<String, Object> metaData, ProcessInstanceVariableEventBody body) {
        super(VAR_TYPE,
                source,
                body,
                (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA),
                (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA),
                (String) metaData.get(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA),
                (String) metaData.get(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA),
                (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA),
                (String) metaData.get(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA),
                (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA),
                addons,
                (String) metaData.get(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA),
                null,
                identity);
        this.kogitoVariableName = body.getVariableName();
    }

    public String getKogitoVariableName() {
        return kogitoVariableName;
    }

    public void setKogitoVariableName(String kogitoVariableName) {
        addExtensionAttribute(CloudEventExtensionConstants.KOGITO_VARIABLE_NAME, kogitoVariableName);
    }

    @Override
    @JsonAnySetter
    public void addExtensionAttribute(String name, Object value) {
        if (value != null) {
            switch (name) {
                case CloudEventExtensionConstants.KOGITO_VARIABLE_NAME:
                    this.kogitoVariableName = (String) value;
                    break;
            }
            super.addExtensionAttribute(name, value);
        }
    }

    @Override
    protected boolean isInternalAttribute(String name) {
        return INTERNAL_EXTENSION_ATTRIBUTES.contains(name) || super.isInternalAttribute(name);
    }
}
