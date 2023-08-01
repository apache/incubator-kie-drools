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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VariableInstanceDataEvent extends AbstractDataEvent<VariableInstanceEventBody> {

    private static final Set<String> INTERNAL_EXTENSION_ATTRIBUTES = Collections.singleton(CloudEventExtensionConstants.KOGITO_VARIABLE_NAME);

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.KOGITO_VARIABLE_NAME)
    private String kogitoVariableName;

    public VariableInstanceDataEvent() {
    }

    public VariableInstanceDataEvent(String source, String addons, String identity, Map<String, String> metaData, VariableInstanceEventBody body) {
        super("VariableInstanceEvent",
                source,
                body,
                metaData.get(ProcessInstanceEventBody.ID_META_DATA),
                metaData.get(ProcessInstanceEventBody.ROOT_ID_META_DATA),
                metaData.get(ProcessInstanceEventBody.PROCESS_ID_META_DATA),
                metaData.get(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA),
                addons,
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
