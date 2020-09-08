/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.impl;

import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;

class EventUtils {

    private EventUtils() {}

    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true).withTimeZone(TimeZone.getDefault()));
    }

    public static <T> T readEvent(String payload,
                                  Class<T> resultClass) throws JsonProcessingException {
        return objectMapper.readValue(payload, resultClass);
    }

    public static <T> String writeEvent(T event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(event);
    }

}
