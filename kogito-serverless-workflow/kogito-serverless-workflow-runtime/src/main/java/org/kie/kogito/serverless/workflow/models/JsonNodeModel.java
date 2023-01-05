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
package org.kie.kogito.serverless.workflow.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.MapInput;
import org.kie.kogito.MapInputId;
import org.kie.kogito.MapOutput;
import org.kie.kogito.MappableToModel;
import org.kie.kogito.Model;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.serverless.workflow.SWFConstants;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeModel implements Model, MapInput, MapInputId, MapOutput, MappableToModel<JsonNodeModelOutput> {

    private JsonNode workflowdata;
    private String id;
    private Map<String, Object> additionalProperties = Collections.emptyMap();

    public JsonNodeModel() {
    }

    public JsonNodeModel(JsonNode workflowdata) {
        this.workflowdata = workflowdata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonNode getWorkflowdata() {
        return workflowdata;
    }

    public void setWorkflowdata(JsonNode workflowdata) {
        this.workflowdata = workflowdata;
    }

    @Override
    public JsonNodeModelOutput toModel() {
        return new JsonNodeModelOutput(id, workflowdata);
    }

    @Override
    public void update(Map<String, Object> params) {
        Map<String, Object> copy = mutableMap(params);
        update((String) copy.remove("id"), copy);
    }

    @Override
    public void fromMap(String id, Map<String, Object> params) {
        update(id, mutableMap(params));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(SWFConstants.DEFAULT_WORKFLOW_VAR, workflowdata);
        map.putAll(additionalProperties);
        return map;
    }

    private void update(String id, Map<String, Object> params) {
        this.id = id;
        if (params.containsKey(SWFConstants.DEFAULT_WORKFLOW_VAR)) {
            this.workflowdata = JsonObjectUtils.fromValue(params.remove(SWFConstants.DEFAULT_WORKFLOW_VAR)).deepCopy();
            this.additionalProperties = params;
        } else {
            this.workflowdata = JsonObjectUtils.fromValue(params);
            this.additionalProperties = Collections.emptyMap();
        }
    }

    private static Map<String, Object> mutableMap(Map<String, Object> map) {
        return map instanceof HashMap ? map : new HashMap<>(map);
    }
}
