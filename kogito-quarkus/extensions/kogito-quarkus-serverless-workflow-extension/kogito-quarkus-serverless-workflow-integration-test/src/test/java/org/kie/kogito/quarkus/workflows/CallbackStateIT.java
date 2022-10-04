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

package org.kie.kogito.quarkus.workflows;

import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
@QuarkusTestResource(ExternalServiceMock.class)
@QuarkusTestResource(KafkaQuarkusTestResource.class)
class CallbackStateIT extends AbstractCallbackStateIT {

    private static final String CALLBACK_STATE_SERVICE_URL = "/callback_state";
    private static final String CALLBACK_STATE_SERVICE_GET_BY_ID_URL = CALLBACK_STATE_SERVICE_URL + "/{id}";
    private static final String CALLBACK_STATE_EVENT_TYPE = "callback_state_event_type";
    private static final String CALLBACK_STATE_EVENT_TOPIC = "callback_state_event_type";

    @Test
    @SuppressWarnings("squid:S2699")
    void callbackStateSuccessful() throws Exception {
        executeCallbackStateSuccessfulPath(CALLBACK_STATE_SERVICE_URL,
                CALLBACK_STATE_SERVICE_GET_BY_ID_URL,
                ANSWER,
                CALLBACK_STATE_EVENT_TYPE,
                CALLBACK_STATE_EVENT_TOPIC);
    }

    @Test
    @SuppressWarnings("squid:S2699")
    void callbackStateWithError() throws Exception {
        executeCallbackStateWithErrorPath(CALLBACK_STATE_SERVICE_URL, CALLBACK_STATE_SERVICE_GET_BY_ID_URL);
    }
}
