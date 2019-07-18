/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.cloud.kubernetes.client.operations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationsResponseParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationsResponseParser.class);

    private final String response;
    private final ObjectMapper mapper;

    public OperationsResponseParser(final String response) {
        this.response = response;
        this.mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    }

    /**
     * Returns the raw JSON Document Response from the API
     *  
     * @return
     */
    public String asJson() {
        return response;
    }

    /**
     * Returns the JSON Document as a list of map
     * 
     * @return
     */
    public Map<String, Object> asMap() {
        if (response == null || response.isEmpty()) {
            return new HashMap<>();
        }
        try {
            final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };
            LOGGER.debug("Trying to parse API response {}", response);
            return mapper.readValue(response, typeRef);
        } catch (IOException e) {
            throw new KogitoKubeClientException("Error while trying to parse API response", e);
        }
    }

    /**
     * Default {@link MapWalker} that explodes an {@link IllegalArgumentException} in case the wrong path is taken
     * 
     * @return
     */
    public MapWalker asMapWalker() {
        return this.asMapWalker(false);
    }

    /**
     * A {@link MapWalker} with the option to turn on/off safe nulls
     * @param safeNull
     * @return
     */
    public MapWalker asMapWalker(final boolean safeNull) {
        if (response == null || response.isEmpty()) {
            return new MapWalker(new HashMap<>(), safeNull);
        }
        return new MapWalker(this.asMap(), safeNull);
    }
}
