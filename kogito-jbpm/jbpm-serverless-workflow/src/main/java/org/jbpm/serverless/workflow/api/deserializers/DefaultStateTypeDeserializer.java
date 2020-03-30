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
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.jbpm.serverless.workflow.api.WorkflowPropertySource;
import org.jbpm.serverless.workflow.api.states.DefaultState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultStateTypeDeserializer extends StdDeserializer<DefaultState.Type> {

    private WorkflowPropertySource context;
    private static Logger logger = LoggerFactory.getLogger(DefaultStateTypeDeserializer.class);

    public DefaultStateTypeDeserializer() {
        this(DefaultState.Type.class);
    }

    public DefaultStateTypeDeserializer(WorkflowPropertySource context) {
        this(DefaultState.Type.class);
        this.context = context;
    }

    public DefaultStateTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DefaultState.Type deserialize(JsonParser jp,
                                         DeserializationContext ctxt) throws IOException {

        String value = jp.getText();

        if (context != null) {
            try {
                String result = context.getPropertySource().getProperty(value);

                if (result != null) {
                    return DefaultState.Type.fromValue(result);
                } else {
                    return DefaultState.Type.fromValue(jp.getText());
                }
            } catch (Exception e) {
                logger.info("Exception trying to evaluate property: {}", e.getMessage());
                return DefaultState.Type.fromValue(jp.getText());
            }
        } else {
            return DefaultState.Type.fromValue(jp.getText());
        }
    }
}