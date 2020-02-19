/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.messaging;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.kie.kogito.index.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCloudEventDeserializer<T> implements Deserializer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCloudEventDeserializer.class);
    private final Class<T> type;

    public AbstractCloudEventDeserializer(Class<T> type) {
        this.type = type;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return JsonUtils.getObjectMapper().readValue(data, type);
        } catch (IOException e) {
            LOGGER.error("Error parsing JSON content: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {

    }
}
