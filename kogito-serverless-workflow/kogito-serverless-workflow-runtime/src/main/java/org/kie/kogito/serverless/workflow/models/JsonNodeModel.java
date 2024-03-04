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
package org.kie.kogito.serverless.workflow.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.MapInput;
import org.kie.kogito.MapInputId;
import org.kie.kogito.MapOutput;
import org.kie.kogito.MappableToModel;
import org.kie.kogito.Model;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.MergeUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonNodeModel implements Model, MapInput, MapInputId, MapOutput, MappableToModel<JsonNodeModelOutput> {

    private JsonNode workflowdata;
    private String id;
    private Map<String, Object> additionalProperties = Collections.emptyMap();

    public JsonNodeModel() {
    }

    public JsonNodeModel(Object workflowdata) {
        this(null, workflowdata);
    }

    public JsonNodeModel(String id, Object workflowdata) {
        this.id = id;
        if (workflowdata instanceof JsonNode) {
            this.workflowdata = (JsonNode) workflowdata;
        } else {
            ObjectMapper mapper = ObjectMapperFactory.listenerAware();
            this.workflowdata = workflowdata == null ? mapper.createObjectNode() : mapper.convertValue(workflowdata, JsonNode.class);
        }
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
        update(params, w -> w);
    }

    @Override
    public Map<String, Object> updatePartially(Map<String, Object> params) {
        update(params, w -> MergeUtils.merge(w, this.workflowdata));
        return toMap();
    }

    private void update(Map<String, Object> params, Function<JsonNode, JsonNode> merger) {
        if (params.containsKey(SWFConstants.DEFAULT_WORKFLOW_VAR)) {
            params = mutableMap(params);
            this.workflowdata = merger.apply(JsonObjectUtils.fromValue(params.remove(SWFConstants.DEFAULT_WORKFLOW_VAR)));
            this.additionalProperties = params;
        } else {
            this.workflowdata = merger.apply(JsonObjectUtils.fromValue(params));
            this.additionalProperties = Collections.emptyMap();
        }
    }

    private static Map<String, Object> mutableMap(Map<String, Object> map) {
        return map instanceof HashMap ? map : new HashMap<>(map);
    }

    @Override
    public void fromMap(String id, Map<String, Object> params) {
        this.id = id;
        update(params);
    }

    @Override
    public MapInput fromMap(Map<String, Object> params) {
        update(params);
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(SWFConstants.DEFAULT_WORKFLOW_VAR, workflowdata);
        map.putAll(additionalProperties);
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append("id=").append(id).append(", ");
        }
        sb.append("workflowdata=" + workflowdata);
        if (!additionalProperties.isEmpty()) {
            sb.append(", additionalProperties=").append(additionalProperties);
        }
        return sb.toString();
    }
}
