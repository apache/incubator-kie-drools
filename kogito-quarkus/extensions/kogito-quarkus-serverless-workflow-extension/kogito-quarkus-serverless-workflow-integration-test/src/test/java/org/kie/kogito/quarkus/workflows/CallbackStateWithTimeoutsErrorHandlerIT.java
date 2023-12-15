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
package org.kie.kogito.quarkus.workflows;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.Converter;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.impl.StringCloudEventUnmarshallerFactory;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.quarkus.workflows.ExternalServiceMock.SUCCESSFUL_QUERY;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceExists;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceHasFinished;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstanceAndGetId;

@QuarkusIntegrationTest
@QuarkusTestResource(ExternalServiceMock.class)
@QuarkusTestResource(KafkaQuarkusTestResource.class)
class CallbackStateWithTimeoutsErrorHandlerIT extends AbstractCallbackStateIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackStateWithTimeoutsErrorHandlerIT.class);

    private static final String CALLBACK_STATE_TIMEOUTS_SERVICE_URL = "/callback_state_with_timeouts_error_handler";
    private static final String CALLBACK_STATE_TIMEOUTS_GET_BY_ID_URL = CALLBACK_STATE_TIMEOUTS_SERVICE_URL + "/{id}";
    private static final String CALLBACK_STATE_TIMEOUTS_EVENT_TYPE = "callback_state_timeouts_event_type";
    private static final String CALLBACK_STATE_TIMEOUTS_TOPIC = "callback_state_timeouts_event_type";

    @Test
    @SuppressWarnings("squid:S2699")
    void callbackStateTimeoutsSuccessful() throws Exception {
        String processInstanceId = executeCallbackStateSuccessfulPath(CALLBACK_STATE_TIMEOUTS_SERVICE_URL,
                CALLBACK_STATE_TIMEOUTS_GET_BY_ID_URL,
                ANSWER,
                CALLBACK_STATE_TIMEOUTS_EVENT_TYPE,
                CALLBACK_STATE_TIMEOUTS_TOPIC);
        waitForFinalizedEvent(processInstanceId, "success");
    }

    @Test
    void callbackStateTimeoutsExceeded() throws Exception {
        String processInput = buildProcessInput(SUCCESSFUL_QUERY);
        String processInstanceId = newProcessInstanceAndGetId(CALLBACK_STATE_TIMEOUTS_SERVICE_URL, processInput);
        assertProcessInstanceExists(CALLBACK_STATE_TIMEOUTS_GET_BY_ID_URL, processInstanceId);

        assertProcessInstanceHasFinished(CALLBACK_STATE_TIMEOUTS_GET_BY_ID_URL, processInstanceId, 1, 10);
        waitForFinalizedEvent(processInstanceId, "timeoutCallbackError");
    }

    @Test
    @SuppressWarnings("squid:S2699")
    void callbackStateWithError() throws Exception {
        String processInstanceId = executeCallbackStateWithErrorPath(CALLBACK_STATE_TIMEOUTS_SERVICE_URL, CALLBACK_STATE_TIMEOUTS_GET_BY_ID_URL);
        waitForFinalizedEvent(processInstanceId, "error");
    }

    private void waitForFinalizedEvent(String processInstanceId, String topic) throws InterruptedException {
        Converter<String, CloudEvent> converter = new StringCloudEventUnmarshallerFactory(objectMapper).unmarshaller(Map.class).cloudEvent();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        kafkaClient.consume(topic, v -> {
            try {
                CloudEvent event = converter.convert(v);
                LOGGER.debug("Found on topic {} CE {}", topic, event);
                if (processInstanceId.equals(event.getExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_ID))) {
                    countDownLatch.countDown();
                }
            } catch (IOException e) {
                LOGGER.info("Unmarshall exception", e);
            }
        });
        countDownLatch.await(10, TimeUnit.SECONDS);
        assertThat(countDownLatch.getCount()).isZero();
    }
}
