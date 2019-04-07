/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.xes.dataset;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.jbpm.xes.model.QueryDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyList;

public class QueryDefinitionLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryDefinitionLoader.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final QueryDefinitionLoader INSTANCE = new QueryDefinitionLoader();

    private QueryDefinitionLoader() {
        MAPPER.registerModule(new JaxbAnnotationModule());
    }

    public static QueryDefinitionLoader get() {
        return INSTANCE;
    }

    protected List<QueryDefinition> loadQueryDefinitions() {
        // load any default query definitions
        try (InputStream qdStream = this.getClass().getResourceAsStream("/default-query-definitions.json")) {
            if (qdStream == null) {
                LOGGER.info("Default query definitions file default-query-definitions.json not found");
                return emptyList();
            }
            return loadQueryDefinitions(qdStream);
        } catch (Exception e) {
            LOGGER.error("Error when loading default query definitions from default-query-definitions.json", e);
            return emptyList();
        }
    }

    protected List<QueryDefinition> loadQueryDefinitions(final InputStream qdStream) {
        try {
            QueryDefinition[] queries = MAPPER.readValue(qdStream, QueryDefinition[].class);

            LOGGER.info("Found {} query definitions", queries == null ? 0 : queries.length);

            return queries == null ? emptyList() : Arrays.asList(queries);
        } catch (Exception e) {
            LOGGER.error("Error when unmarshalling query definitions from stream.", e);
            return emptyList();
        }
    }
}
