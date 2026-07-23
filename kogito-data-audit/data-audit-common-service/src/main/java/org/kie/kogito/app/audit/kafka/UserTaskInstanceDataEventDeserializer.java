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
package org.kie.kogito.app.audit.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.kie.kogito.app.audit.json.JsonUtils;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

public class UserTaskInstanceDataEventDeserializer implements Deserializer<UserTaskInstanceDataEvent<?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTaskInstanceDataEventDeserializer.class);

    @Override
    public UserTaskInstanceDataEvent<?> deserialize(String topic, byte[] data) {
        try {
            return JsonUtils.getObjectMapper().readValue(new String(data), UserTaskInstanceDataEvent.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("not possible to deserialize UserTaskInstanceDataEvent data {}", new String(data), e);
            throw new IllegalArgumentException("not possible to deserialize data");
        }

    }

}
