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
package org.kie.kogito.event.serializer;

import java.io.IOException;

import org.kie.kogito.event.usertask.MultipleUserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JsonUserTaskInstanceDataEventDeserializer extends StdDeserializer<UserTaskInstanceDataEvent<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUserTaskInstanceDataEventDeserializer.class);

    private static final long serialVersionUID = -6626663191296012306L;

    public JsonUserTaskInstanceDataEventDeserializer() {
        this(null);
    }

    public JsonUserTaskInstanceDataEventDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public UserTaskInstanceDataEvent<?> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        LOGGER.debug("Deserialize user task instance data event: {}", node);
        String type = node.get("type").asText();

        switch (type) {
            case MultipleUserTaskInstanceDataEvent.TYPE:
                return jp.getCodec().treeToValue(node, MultipleUserTaskInstanceDataEvent.class);
            case "UserTaskInstanceAssignmentDataEvent":
                return (UserTaskInstanceDataEvent<?>) jp.getCodec().treeToValue(node, UserTaskInstanceAssignmentDataEvent.class);
            case "UserTaskInstanceAttachmentDataEvent":
                return (UserTaskInstanceDataEvent<?>) jp.getCodec().treeToValue(node, UserTaskInstanceAttachmentDataEvent.class);
            case "UserTaskInstanceCommentDataEvent":
                return (UserTaskInstanceDataEvent<?>) jp.getCodec().treeToValue(node, UserTaskInstanceCommentDataEvent.class);
            case "UserTaskInstanceDeadlineDataEvent":
                return (UserTaskInstanceDataEvent<?>) jp.getCodec().treeToValue(node, UserTaskInstanceDeadlineDataEvent.class);
            case "UserTaskInstanceStateDataEvent":
                return (UserTaskInstanceDataEvent<?>) jp.getCodec().treeToValue(node, UserTaskInstanceStateDataEvent.class);
            case "UserTaskInstanceVariableDataEvent":
                return (UserTaskInstanceDataEvent<?>) jp.getCodec().treeToValue(node, UserTaskInstanceVariableDataEvent.class);
            default:
                LOGGER.warn("Unknown type {} in json data {}", type, node);
                return (UserTaskInstanceDataEvent<?>) jp.getCodec().treeToValue(node, UserTaskInstanceDataEvent.class);

        }
    }
}
