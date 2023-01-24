/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.json;

import java.util.function.Function;

import org.kie.kogito.event.process.ProcessInstanceDataEvent;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;
import static org.kie.kogito.index.storage.Constants.ID;
import static org.kie.kogito.index.storage.Constants.KOGITO_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.LAST_UPDATE;
import static org.kie.kogito.index.storage.Constants.PROCESS_ID;
import static org.kie.kogito.index.storage.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.PROCESS_NAME;

public class ProcessInstanceMetaMapper implements Function<ProcessInstanceDataEvent, ObjectNode> {

    @Override
    public ObjectNode apply(ProcessInstanceDataEvent event) {
        if (event == null) {
            return null;
        } else {
            ObjectNode json = getObjectMapper().createObjectNode();
            json.put(ID, isNullOrEmpty(event.getData().getRootInstanceId()) ? event.getData().getId() : event.getData().getRootInstanceId());
            json.put(PROCESS_ID, isNullOrEmpty(event.getData().getRootProcessId()) ? event.getData().getProcessId() : event.getData().getRootProcessId());
            ObjectNode kogito = getObjectMapper().createObjectNode();
            kogito.put(LAST_UPDATE, event.getTime().toInstant().toEpochMilli());
            kogito.withArray(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE).add(getProcessJson(event));
            json.set(KOGITO_DOMAIN_ATTRIBUTE, kogito);
            json.setAll((ObjectNode) getObjectMapper().valueToTree(event.getData().getVariables()));
            return json;
        }
    }

    private ObjectNode getProcessJson(ProcessInstanceDataEvent event) {
        ObjectNode json = getObjectMapper().createObjectNode();
        json.put(ID, event.getData().getId());
        json.put(PROCESS_ID, event.getData().getProcessId());
        json.put(PROCESS_NAME, event.getData().getProcessName());
        if (!isNullOrEmpty(event.getData().getRootInstanceId())) {
            json.put("rootProcessInstanceId", event.getData().getRootInstanceId());
        }
        if (!isNullOrEmpty(event.getData().getParentInstanceId())) {
            json.put("parentProcessInstanceId", event.getData().getParentInstanceId());
        }
        if (!isNullOrEmpty(event.getData().getRootProcessId())) {
            json.put("rootProcessId", event.getData().getRootProcessId());
        }
        json.put("state", event.getData().getState());
        if (event.getSource() != null) {
            json.put("endpoint", event.getSource().toString());
        }
        if (event.getData().getStartDate() != null) {
            json.put("start", event.getData().getStartDate().toInstant().toEpochMilli());
        }
        if (event.getData().getEndDate() != null) {
            json.put("end", event.getData().getEndDate().toInstant().toEpochMilli());
        }
        if (event.getTime() != null) {
            json.put(LAST_UPDATE, event.getTime().toInstant().toEpochMilli());
        }
        if (!isNullOrEmpty(event.getData().getBusinessKey())) {
            json.put("businessKey", event.getData().getBusinessKey());
        }
        return json;
    }
}
