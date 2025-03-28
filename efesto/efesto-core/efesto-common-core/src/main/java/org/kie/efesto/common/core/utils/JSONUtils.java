/**
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
package org.kie.efesto.common.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.serialization.DeserializerService;
import org.kie.efesto.common.core.serialization.SerializerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class JSONUtils {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class.getName());
    private static final ServiceLoader<DeserializerService> deserializerServiceServiceLoader = ServiceLoader.load(DeserializerService.class);
    private static final ServiceLoader<SerializerService> serializerServiceServiceLoader = ServiceLoader.load(SerializerService.class);
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        SimpleModule toRegister = new SimpleModule();
        deserializerServiceServiceLoader.forEach(deserializerService -> {
            logger.debug("Registering deserializer {} for {}", deserializerService.deser(), deserializerService.type());
            toRegister.addDeserializer(deserializerService.type(), deserializerService.deser());
        });
        serializerServiceServiceLoader.forEach(serializerService -> {
            logger.debug("Registering serializer {} for {}", serializerService.ser(), serializerService.type());
            toRegister.addSerializer(serializerService.type(), serializerService.ser());
        });
        objectMapper.registerModule(toRegister);
    }

    private JSONUtils() {
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static String getGeneratedResourceString(GeneratedResource generatedResource) throws JsonProcessingException {
        return objectMapper.writeValueAsString(generatedResource);
    }

    public static GeneratedResource getGeneratedResourceObject(String generatedResourceString) throws JsonProcessingException {
        return objectMapper.readValue(generatedResourceString, GeneratedResource.class);
    }

    public static String getGeneratedResourcesString(GeneratedResources generatedResources) throws JsonProcessingException {
        return objectMapper.writeValueAsString(generatedResources);
    }

    public static GeneratedResources getGeneratedResourcesObject(String generatedResourcesString) throws JsonProcessingException {
        return objectMapper.readValue(generatedResourcesString, GeneratedResources.class);
    }

    public static GeneratedResources getGeneratedResourcesObject(IndexFile indexFile) throws IOException {
        logger.debug("getGeneratedResourcesObject {}", indexFile);
        logger.debug("indexFile.length() {}", indexFile.length());
        return indexFile.length() == 0 ? new GeneratedResources() : objectMapper.readValue(indexFile.getContent(),
                                                                                           GeneratedResources.class);
    }

    public static void writeGeneratedResourcesObject(GeneratedResources toWrite, IndexFile indexFile) throws IOException {
        objectMapper.writeValue(indexFile, toWrite);
    }

    public static String getModelLocalUriIdString(ModelLocalUriId localUriId) throws JsonProcessingException {
        return objectMapper.writeValueAsString(localUriId);
    }

    public static ModelLocalUriId getModelLocalUriIdObject(String localUriString) throws JsonProcessingException {
        return objectMapper.readValue(localUriString, ModelLocalUriId.class);
    }

    public static Map<String, Object> getInputData(String inputDataString) throws JsonProcessingException {
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<>() {
        };
        String toRead = cleanupMapString(inputDataString);
        return objectMapper.readValue(toRead, typeRef);
    }

    static String cleanupMapString(String toClean) {
        String toReturn = toClean.replaceAll("^\"|\"$", "").replaceAll("\\\\", "");
        if (toReturn.startsWith("\"")) {
            toReturn = toReturn.substring(1);
        }
        if (toReturn.endsWith("\"")) {
            toReturn = toReturn.substring(0, toReturn.length() -1);
        }
        return toReturn;
    }
}
