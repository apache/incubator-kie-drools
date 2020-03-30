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
import org.jbpm.serverless.workflow.api.choices.DefaultChoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultChoiceOperatorDeserializer extends StdDeserializer<DefaultChoice.Operator> {

    private WorkflowPropertySource context;
    private static Logger logger = LoggerFactory.getLogger(DefaultChoiceOperatorDeserializer.class);

    public DefaultChoiceOperatorDeserializer() {
        this(DefaultChoice.Operator.class);
    }

    public DefaultChoiceOperatorDeserializer(WorkflowPropertySource context) {
        this(DefaultChoice.Operator.class);
        this.context = context;
    }

    public DefaultChoiceOperatorDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DefaultChoice.Operator deserialize(JsonParser jp,
                                              DeserializationContext ctxt) throws IOException {

        String value = jp.getText();
        if (context != null) {
            try {
                String result = context.getPropertySource().getProperty(value);

                if (result != null) {
                    return DefaultChoice.Operator.fromValue(result);
                } else {
                    return DefaultChoice.Operator.fromValue(jp.getText());
                }
            } catch (Exception e) {
                logger.info("Exception trying to evaluate property: {}", e.getMessage());
                return DefaultChoice.Operator.fromValue(jp.getText());
            }
        } else {
            return DefaultChoice.Operator.fromValue(jp.getText());
        }
    }
}