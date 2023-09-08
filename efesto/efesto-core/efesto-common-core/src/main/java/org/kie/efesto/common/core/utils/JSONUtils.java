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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.serialization.ModelLocalUriIdDeSerializer;
import org.kie.efesto.common.core.serialization.ModelLocalUriIdSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONUtils {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        SimpleModule toRegister = new SimpleModule();
        toRegister.addDeserializer(ModelLocalUriId.class, new ModelLocalUriIdDeSerializer());
        toRegister.addSerializer(ModelLocalUriId.class, new ModelLocalUriIdSerializer());
        objectMapper.registerModule(toRegister);
    }
    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class.getName());

    private JSONUtils() {
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
}
