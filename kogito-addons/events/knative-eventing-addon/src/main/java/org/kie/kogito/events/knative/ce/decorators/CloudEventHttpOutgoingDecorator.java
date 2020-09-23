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
package org.kie.kogito.events.knative.ce.decorators;

import io.cloudevents.jackson.JsonFormat;
import io.smallrye.reactive.messaging.http.HttpResponseMetadata;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;

import javax.ws.rs.core.HttpHeaders;
import java.util.Collections;

/**
 * Decorators for Http CloudEvents outgoing messages
 */
public final class CloudEventHttpOutgoingDecorator implements MessageDecorator {

    /**
     * Metadata to include content-type for structured CloudEvents messages
     */
    static final Metadata HTTP_RESPONSE_METADATA =
            Metadata.of(HttpResponseMetadata.builder()
                    .withQueryParameter(Collections.emptyMap())
                    .withHeader(HttpHeaders.CONTENT_TYPE, JsonFormat.CONTENT_TYPE).build());

    CloudEventHttpOutgoingDecorator() {

    }

    /**
     * Decorates a given payload with custom metadata needed by Http Outgoing processing
     *
     * @param payload of the given message
     * @param <T>     Payload type
     */
    public <T> Message<T> decorate(T payload) {
        return Message.of(payload, HTTP_RESPONSE_METADATA);
    }
}
