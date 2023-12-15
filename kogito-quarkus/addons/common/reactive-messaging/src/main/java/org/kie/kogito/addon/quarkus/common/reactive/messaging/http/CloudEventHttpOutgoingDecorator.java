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
package org.kie.kogito.addon.quarkus.common.reactive.messaging.http;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.addon.quarkus.common.reactive.messaging.MessageDecorator;

import io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata;

import jakarta.ws.rs.core.HttpHeaders;

/**
 * Decorators for Http CloudEvents outgoing messages
 */
public class CloudEventHttpOutgoingDecorator implements MessageDecorator {

    // Note: this constant is also declared in cloudevents-json-jackson.
    // However, to avoid importing a library for only one constant that won't likely to change, we opt to have it declared here.
    public static final String CLOUD_EVENTS_CONTENT_TYPE = "application/cloudevents+json";

    /**
     * Metadata to include content-type for structured CloudEvents messages
     */
    static final OutgoingHttpMetadata HTTP_RESPONSE_METADATA =
            new OutgoingHttpMetadata.Builder().addHeader(HttpHeaders.CONTENT_TYPE, CLOUD_EVENTS_CONTENT_TYPE).build();

    /**
     * Decorates a given payload with custom metadata needed by Http Outgoing processing
     *
     * @param message of the given message
     * @param <T> Payload type
     */
    @Override
    public <T> Message<T> decorate(Message<T> message) {
        return message.addMetadata(HTTP_RESPONSE_METADATA);
    }
}
