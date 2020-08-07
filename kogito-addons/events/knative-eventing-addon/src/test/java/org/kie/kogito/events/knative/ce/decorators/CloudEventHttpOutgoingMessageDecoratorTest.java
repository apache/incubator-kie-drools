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

import io.smallrye.reactive.messaging.http.HttpResponseMetadata;
import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CloudEventHttpOutgoingMessageDecoratorTest {

    @Test
    void verifyDecorateAndSend() {
        final String payload = "any message";
        final Message<String> message = new CloudEventHttpOutgoingDecorator().decorate(payload);
        assertThat(message).isNotNull();
        assertThat(message.getMetadata(HttpResponseMetadata.class)).isPresent();
    }
}