/*
 *
 *   Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.jbpm.serverless.workflow.api.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.jbpm.serverless.workflow.api.interfaces.Choice;
import org.jbpm.serverless.workflow.api.choices.AndChoice;
import org.jbpm.serverless.workflow.api.choices.DefaultChoice;
import org.jbpm.serverless.workflow.api.choices.NotChoice;
import org.jbpm.serverless.workflow.api.choices.OrChoice;
import org.jbpm.serverless.workflow.api.choices.SingleChoice;

public class ChoiceDeserializer extends StdDeserializer<Choice> {

    public ChoiceDeserializer() {
        this(null);
    }

    public ChoiceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Choice deserialize(JsonParser jp,
                              DeserializationContext ctxt)
            throws IOException {

        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = jp.getCodec().readTree(jp);

        if (node.get("and") != null) {
            return mapper.treeToValue(node,
                                      AndChoice.class);
        } else if (node.get("not") != null) {
            return mapper.treeToValue(node,
                                      NotChoice.class);
        } else if (node.get("or") != null) {
            return mapper.treeToValue(node,
                                      OrChoice.class);
        } else {
            if(node.get("next-state") != null) {
                return mapper.treeToValue(node,
                                          SingleChoice.class);
            } else {
                return mapper.treeToValue(node,
                                          DefaultChoice.class);
            }
        }
    }
}