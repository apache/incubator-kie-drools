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
package org.kie.kogito.index;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class CommonUtils {

    public static final int ERROR_STATE = 5;
    private static final Set<String> finalStates = Set.of("Completed", "Aborted");
    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static boolean isTaskCompleted(String status) {
        return finalStates.contains(status);
    }

    public static String getServiceUrl(String endpoint, String processId) {
        logger.debug("Process endpoint {}", endpoint);
        if (endpoint == null) {
            return null;
        }
        if (endpoint.startsWith("/")) {
            logger.warn("Process '{}' endpoint '{}', does not contain full URL, please review the kogito.service.url system property to point the public URL for this runtime.",
                    processId, endpoint);
        }
        String context = getContext(processId);
        logger.debug("Process context {}", context);
        if (context.equals(endpoint) || endpoint.equals("/" + context)) {
            return null;
        } else {
            return endpoint.contains("/" + context) ? endpoint.substring(0, endpoint.lastIndexOf("/" + context)) : null;
        }
    }

    public static TypeDefinitionRegistry loadSchemaDefinitionFile(String fileName) {
        SchemaParser schemaParser = new SchemaParser();
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                InputStreamReader reader = new InputStreamReader(stream)) {
            return schemaParser.parse(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> mergeMap(Map<K, V> source, Map<K, V> target) {
        if (source == null) {
            return target;
        } else if (target == null) {
            return source;
        } else {
            Map<K, V> result = new HashMap<>(target);
            source.forEach((key, value) -> {
                if (value != null) {
                    result.merge(key, value, (targetValue, srcValue) -> {
                        if (srcValue instanceof Map && targetValue instanceof Map) {
                            return (V) mergeMap((Map<K, V>) srcValue, (Map<K, V>) targetValue);
                        } else {
                            return srcValue;
                        }
                    });
                }
            });
            return result;
        }
    }

    private static String getContext(String processId) {
        return processId != null && processId.contains(".") ? processId.substring(processId.lastIndexOf('.') + 1) : processId;
    }
}
