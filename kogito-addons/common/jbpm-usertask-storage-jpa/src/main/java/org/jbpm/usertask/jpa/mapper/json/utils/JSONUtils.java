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

package org.jbpm.usertask.jpa.mapper.json.utils;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static java.lang.Thread.currentThread;

public class JSONUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static JavaType buildJavaType(Class<?> clazz, Class<?> parameter) {
        return OBJECT_MAPPER.getTypeFactory().constructParametricType(clazz, parameter);
    }

    public static String valueToString(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object stringTreeToValue(String value, String javaType) {
        try {
            if (Objects.isNull(value) || Objects.isNull(javaType)) {
                return null;
            }
            return OBJECT_MAPPER.readValue(value, currentThread().getContextClassLoader().loadClass(javaType));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object stringTreeToValue(String value, JavaType javaType) {
        try {
            if (Objects.isNull(value) || Objects.isNull(javaType)) {
                return null;
            }
            return OBJECT_MAPPER.readValue(value, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
