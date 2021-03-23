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

package org.kie.kogito.taskassigning.service.messaging;

import java.io.IOException;
import java.net.URI;
import java.time.temporal.ChronoUnit;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.google.common.net.UrlEscapers;

import static java.lang.String.format;
import static org.kie.kogito.taskassigning.util.JsonUtils.OBJECT_MAPPER;

public class UserTaskEventDeserializer implements Deserializer<UserTaskEvent> {

    @Override
    public UserTaskEvent deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                return null;
            }
            UserTaskEventMessage message = OBJECT_MAPPER.readValue(data, UserTaskEventMessage.class);
            UserTaskEvent event = message.getData();
            event.setEventTime(message.getTime());
            event.setLastUpdate(message.getTime().truncatedTo(ChronoUnit.MILLIS));
            event.setEndpoint(buildEndpoint(message.getSource(), event.getProcessInstanceId(), event.getName(), event.getTaskId()));
            return event;
        } catch (IOException e) {
            throw new SerializationException("An error was produced during UserTaskEventMessage deserialization: " + e.getMessage(), e);
        }
    }

    private static String buildEndpoint(URI source, String processId, String taskName, String taskId) {
        String escapedTaskName = UrlEscapers.urlPathSegmentEscaper().escape(taskName);
        return source.toString() + format("/%s/%s/%s", processId, escapedTaskName, taskId);
    }
}
