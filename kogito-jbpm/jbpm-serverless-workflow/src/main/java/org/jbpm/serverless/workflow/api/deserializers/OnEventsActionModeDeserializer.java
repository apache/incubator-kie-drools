/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.serverless.workflow.api.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.jbpm.serverless.workflow.api.events.OnEvents;
import org.jbpm.serverless.workflow.api.interfaces.WorkflowPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OnEventsActionModeDeserializer extends StdDeserializer<OnEvents.ActionMode> {

    private static final long serialVersionUID = 510l;
    private static Logger logger = LoggerFactory.getLogger(OnEventsActionModeDeserializer.class);

    private WorkflowPropertySource context;

    public OnEventsActionModeDeserializer() {
        this(OnEvents.ActionMode.class);
    }

    public OnEventsActionModeDeserializer(WorkflowPropertySource context) {
        this(OnEvents.ActionMode.class);
        this.context = context;
    }

    public OnEventsActionModeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public OnEvents.ActionMode deserialize(JsonParser jp,
                                           DeserializationContext ctxt) throws IOException {

        String value = jp.getText();
        if (context != null) {
            try {
                String result = context.getPropertySource().getProperty(value);

                if (result != null) {
                    return OnEvents.ActionMode.fromValue(result);
                } else {
                    return OnEvents.ActionMode.fromValue(jp.getText());
                }
            } catch (Exception e) {
                logger.info("Exception trying to evaluate property: {}", e.getMessage());
                return OnEvents.ActionMode.fromValue(jp.getText());
            }
        } else {
            return OnEvents.ActionMode.fromValue(jp.getText());
        }
    }
}