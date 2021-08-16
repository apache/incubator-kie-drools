/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addon.cloudevents.quarkus.message;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;

import io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata;

import static org.assertj.core.api.Assertions.assertThat;

class CloudEventHttpOutgoingMessageDecoratorTest {

    @Test
    void verifyDecorateAndSend() {
        final String payload = "any message";
        final Message<String> message = new CloudEventHttpOutgoingDecorator().decorate(payload);
        assertThat(message).isNotNull();
        assertThat(message.getMetadata(OutgoingHttpMetadata.class)).isPresent();
    }
}
