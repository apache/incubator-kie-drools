/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;

/**
 * This interface is one of the extension point for customers to incorporate more event formats when using cloud events.
 * It is responsible for converting objects received in the external service format into Cloud Events consumed by Kogito.
 * Default implementation uses Jackson.
 *
 * @param <I> the external service object type
 * @param <O> kogito business object type
 */
public interface CloudEventUnmarshaller<I, O> {

    /**
     * Create Cloud Event from structure event payload
     * 
     * @return Cloud Event
     */
    Converter<I, CloudEvent> cloudEvent();

    /**
     * Create Cloud Event from binary event payload
     * 
     * @return Cloud Event Data
     */
    Converter<I, CloudEventData> binaryCloudEvent();

    /**
     * Creates Kogito business object from Cloud Event data
     * 
     * @return Kogito Businnes Object
     */
    Converter<CloudEventData, O> data();
}
