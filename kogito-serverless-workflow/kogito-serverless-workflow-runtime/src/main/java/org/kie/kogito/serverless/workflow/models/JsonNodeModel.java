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

import org.kie.kogito.MapInput;
import org.kie.kogito.MapInputId;
import org.kie.kogito.MapOutput;
import org.kie.kogito.MappableToModel;
import org.kie.kogito.Model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeModel implements Model, MapInput, MapInputId, MapOutput, MappableToModel<JsonNodeModelOutput> {

    private JsonNode workflowdata;
    private String id;

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

    @JsonAnyGetter
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
}
