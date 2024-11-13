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
package org.kie.kogito.jackson.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.cloudevents.jackson.JsonFormat;

public class ObjectMapperFactory {

    private ObjectMapperFactory() {
    }

    private static class DefaultObjectMapper {
        private static ObjectMapper instance = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setTypeFactory(TypeFactory.defaultInstance().withClassLoader(Thread.currentThread().getContextClassLoader()))
                .registerModule(JsonFormat.getCloudEventJacksonModule())
                .registerModule(new CommonObjectModule())
                .findAndRegisterModules();
    }

    private static class ListenerAwareMapper {
        private static ObjectMapper instance = DefaultObjectMapper.instance.copy().setNodeFactory(new JsonNodeFactoryListener());
    }

    public static ObjectMapper get() {
        return DefaultObjectMapper.instance;
    }

    public static ObjectMapper listenerAware() {
        return ListenerAwareMapper.instance;
    }
}
