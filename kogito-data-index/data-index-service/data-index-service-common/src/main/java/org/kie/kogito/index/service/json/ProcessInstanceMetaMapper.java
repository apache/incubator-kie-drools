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
package org.kie.kogito.index.service.json;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;
import static org.kie.kogito.index.storage.Constants.ID;
import static org.kie.kogito.index.storage.Constants.KOGITO_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.LAST_UPDATE;
import static org.kie.kogito.index.storage.Constants.PROCESS_ID;
import static org.kie.kogito.index.storage.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.PROCESS_NAME;
import static org.kie.kogito.internal.utils.ConversionUtils.isEmpty;

public class ProcessInstanceMetaMapper implements Function<ProcessInstanceDataEvent<?>, ObjectNode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInstanceMetaMapper.class);

    @Override
    public ObjectNode apply(ProcessInstanceDataEvent<?> event) {
        if (event == null) {
            return null;
        }

        ObjectNode json = getObjectMapper().createObjectNode();
        json.put(ID, isEmpty(event.getKogitoRootProcessInstanceId()) ? event.getKogitoProcessInstanceId() : event.getKogitoRootProcessInstanceId());
        json.put(PROCESS_ID, isEmpty(event.getKogitoRootProcessId()) ? event.getKogitoProcessId() : event.getKogitoRootProcessId());
        ObjectNode kogito = getObjectMapper().createObjectNode();
        kogito.withArray(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE).add(getProcessJson(event));
        kogito.put(LAST_UPDATE, event.getTime() == null ? null : event.getTime().toInstant().toEpochMilli());
        json.set(KOGITO_DOMAIN_ATTRIBUTE, kogito);

        if (event instanceof ProcessInstanceVariableDataEvent) {
            ProcessInstanceVariableDataEvent vars = (ProcessInstanceVariableDataEvent) event;
            String name = vars.getData().getVariableName();
            Object value = vars.getData().getVariableValue();
            Map<String, Object> newVars = Collections.singletonMap(name, value);
            LOGGER.debug("Setting domain variable name {} with value {}", name, value);
            json.putAll((ObjectNode) getObjectMapper().valueToTree(newVars));

        }
        return json;

    }

    private ObjectNode getProcessJson(ProcessInstanceDataEvent<?> event) {
        ObjectNode json = getObjectMapper().createObjectNode();
        json.put(ID, event.getKogitoProcessInstanceId());
        json.put(PROCESS_ID, event.getKogitoProcessId());
        json.put(LAST_UPDATE, event.getTime() == null ? null : event.getTime().toInstant().toEpochMilli());

        if (!isEmpty(event.getKogitoRootProcessInstanceId())) {
            json.put("rootProcessInstanceId", event.getKogitoRootProcessInstanceId());
        }
        if (!isEmpty(event.getKogitoParentProcessInstanceId())) {
            json.put("parentProcessInstanceId", event.getKogitoParentProcessInstanceId());
        }
        if (!isEmpty(event.getKogitoRootProcessId())) {
            json.put("rootProcessId", event.getKogitoRootProcessId());
        }

        if (event.getSource() != null) {
            json.put("endpoint", event.getSource().toString());
        }

        if (event instanceof ProcessInstanceStateDataEvent) {
            ProcessInstanceStateDataEvent state = (ProcessInstanceStateDataEvent) event;

            json.put("state", state.getData().getState());
            json.put(PROCESS_NAME, state.getData().getProcessName());
            if (!isEmpty(state.getData().getBusinessKey())) {
                json.put("businessKey", state.getData().getBusinessKey());
            }
            if (state.getData().getEventType() != null && state.getData().getEventType() == 1) {
                json.put("start", state.getData().getEventDate().toInstant().toEpochMilli());
                if (!isEmpty(state.getData().getEventUser())) {
                    json.put("createdBy", state.getData().getEventUser());
                }
            }
            if (state.getData().getEventType() != null && state.getData().getEventType() == 2) {
                json.put("end", state.getData().getEventDate().toInstant().toEpochMilli());
            }

            if (!isEmpty(state.getData().getEventUser())) {
                json.put("updatedBy", state.getData().getEventUser());
            }
        }

        return json;
    }
}
