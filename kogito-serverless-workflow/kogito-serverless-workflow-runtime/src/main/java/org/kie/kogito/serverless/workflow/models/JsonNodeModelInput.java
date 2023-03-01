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
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeModelInput implements Model, MapInput, MapInputId, MapOutput, MappableToModel<JsonNodeModel> {

    private Object workflowdata;

    public Object getWorkflowdata() {
        return workflowdata;
    }

    @JsonAnySetter
    public void setData(String key, JsonNode value) {
        if (workflowdata == null) {
            workflowdata = ObjectMapperFactory.listenerAware().createObjectNode();
        }
        if (workflowdata instanceof ObjectNode) {
            ((ObjectNode) workflowdata).set(key, value);
        }
    }

    @Override
    public JsonNodeModel toModel() {
        return new JsonNodeModel(workflowdata);
    }
}
