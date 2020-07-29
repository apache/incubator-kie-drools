/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision.event;

import java.net.URI;

import com.fasterxml.jackson.core.type.TypeReference;
import io.cloudevents.json.Json;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;

public class CloudEventUtils {

    public static <E> CloudEventImpl<E> build(String id,
                                              URI source,
                                              E data,
                                              Class<E> dataType) {
        return CloudEventBuilder.<E>builder()
                .withType(dataType.getName())
                .withId(id)
                .withSource(source)
                .withData(data)
                .build();
    }

    public static <E> String encode(CloudEventImpl<E> event) {
        return Json.encode(event);
    }

    public static <E> CloudEventImpl<E> decode(String json, TypeReference<CloudEventImpl<E>> ref) {
        return Json.decodeValue(json, ref);
    }

    private CloudEventUtils() {
    }
}
