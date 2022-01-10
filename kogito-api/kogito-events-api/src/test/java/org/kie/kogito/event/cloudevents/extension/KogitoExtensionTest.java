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
package org.kie.kogito.event.cloudevents.extension;

import java.net.URI;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.provider.ExtensionProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class KogitoExtensionTest {

    private static final String TEST_ID = "test-cloudevent-id";
    private static final String TEST_SOURCE = "http://test-cloudevent-source";
    private static final String TEST_TYPE = "test-cloudevent-type";
    private static final String TEST_DATA = "\"TEST_DATA\"";

    private static final String TEST_EXECUTION_ID = "test-kogito-execution-id";
    private static final String TEST_DMN_MODEL_NAME = "test-kogito-dmn-model-name";
    private static final String TEST_DMN_MODEL_NAMESPACE = "test-kogito-dmn-model-namespace";
    private static final String TEST_DMN_EVALUATE_DECISION = "test-kogito-dmn-eval-decision";

    @BeforeAll
    static void registerExtension() {
        KogitoExtension.register();
    }

    @Test
    void writeExtension() {
        assertWriteExtension(null, null, null, null);
        assertWriteExtension(null, null, null, false);
        assertWriteExtension(null, null, null, true);
        assertWriteExtension(null, null, false, null);
        assertWriteExtension(null, null, false, false);
        assertWriteExtension(null, null, false, true);
        assertWriteExtension(null, null, true, null);
        assertWriteExtension(null, null, true, false);
        assertWriteExtension(null, null, true, true);

        assertWriteExtension(null, TEST_EXECUTION_ID, null, null);
        assertWriteExtension(null, TEST_EXECUTION_ID, null, false);
        assertWriteExtension(null, TEST_EXECUTION_ID, null, true);
        assertWriteExtension(null, TEST_EXECUTION_ID, false, null);
        assertWriteExtension(null, TEST_EXECUTION_ID, false, false);
        assertWriteExtension(null, TEST_EXECUTION_ID, false, true);
        assertWriteExtension(null, TEST_EXECUTION_ID, true, null);
        assertWriteExtension(null, TEST_EXECUTION_ID, true, false);
        assertWriteExtension(null, TEST_EXECUTION_ID, true, true);

        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, null, null, null);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, null, null, false);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, null, null, true);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, null, false, null);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, null, false, false);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, null, false, true);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, null, true, null);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, null, true, false);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, null, true, true);

        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, null, null);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, null, false);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, null, true);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, false, null);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, false, false);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, false, true);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, true, null);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, true, false);
        assertWriteExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, true, true);
    }

    private void assertWriteExtension(String dmnEvaluateDecision, String executionId, Boolean dmnFullResult, Boolean dmnFilteredCtx) {
        CloudEvent event = cloudEventFromExtensionObject(dmnEvaluateDecision, executionId, dmnFullResult, dmnFilteredCtx);
        assertCloudEvent(event, dmnEvaluateDecision, executionId, dmnFullResult, dmnFilteredCtx);
    }

    @Test
    void parseExtension() {
        assertParseExtension(null, null, null, null);
        assertParseExtension(null, null, null, false);
        assertParseExtension(null, null, null, true);
        assertParseExtension(null, null, false, null);
        assertParseExtension(null, null, false, false);
        assertParseExtension(null, null, false, true);
        assertParseExtension(null, null, true, null);
        assertParseExtension(null, null, true, false);
        assertParseExtension(null, null, true, true);

        assertParseExtension(null, TEST_EXECUTION_ID, null, null);
        assertParseExtension(null, TEST_EXECUTION_ID, null, false);
        assertParseExtension(null, TEST_EXECUTION_ID, null, true);
        assertParseExtension(null, TEST_EXECUTION_ID, false, null);
        assertParseExtension(null, TEST_EXECUTION_ID, false, false);
        assertParseExtension(null, TEST_EXECUTION_ID, false, true);
        assertParseExtension(null, TEST_EXECUTION_ID, true, null);
        assertParseExtension(null, TEST_EXECUTION_ID, true, false);
        assertParseExtension(null, TEST_EXECUTION_ID, true, true);

        assertParseExtension(TEST_DMN_EVALUATE_DECISION, null, null, null);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, null, null, false);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, null, null, true);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, null, false, null);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, null, false, false);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, null, false, true);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, null, true, null);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, null, true, false);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, null, true, true);

        assertParseExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, null, null);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, null, false);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, null, true);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, false, null);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, false, false);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, false, true);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, true, null);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, true, false);
        assertParseExtension(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, true, true);
    }

    private void assertParseExtension(String dmnEvaluateDecision, String executionId, Boolean dmnFullResult, Boolean dmnFilteredCtx) {
        KogitoExtension extension = extensionObjectFromCloudEvent(dmnEvaluateDecision, executionId, dmnFullResult, dmnFilteredCtx);
        assertExtension(extension, dmnEvaluateDecision, executionId, dmnFullResult, dmnFilteredCtx);
    }

    @Test
    void writeAndParseExtensionWithEncoding() {
        assertWriteAndParseExtensionWithEncoding(null, null, null, null);
        assertWriteAndParseExtensionWithEncoding(null, null, null, false);
        assertWriteAndParseExtensionWithEncoding(null, null, null, true);
        assertWriteAndParseExtensionWithEncoding(null, null, false, null);
        assertWriteAndParseExtensionWithEncoding(null, null, false, false);
        assertWriteAndParseExtensionWithEncoding(null, null, false, true);
        assertWriteAndParseExtensionWithEncoding(null, null, true, null);
        assertWriteAndParseExtensionWithEncoding(null, null, true, false);
        assertWriteAndParseExtensionWithEncoding(null, null, true, true);

        assertWriteAndParseExtensionWithEncoding(null, TEST_EXECUTION_ID, null, null);
        assertWriteAndParseExtensionWithEncoding(null, TEST_EXECUTION_ID, null, false);
        assertWriteAndParseExtensionWithEncoding(null, TEST_EXECUTION_ID, null, true);
        assertWriteAndParseExtensionWithEncoding(null, TEST_EXECUTION_ID, false, null);
        assertWriteAndParseExtensionWithEncoding(null, TEST_EXECUTION_ID, false, false);
        assertWriteAndParseExtensionWithEncoding(null, TEST_EXECUTION_ID, false, true);
        assertWriteAndParseExtensionWithEncoding(null, TEST_EXECUTION_ID, true, null);
        assertWriteAndParseExtensionWithEncoding(null, TEST_EXECUTION_ID, true, false);
        assertWriteAndParseExtensionWithEncoding(null, TEST_EXECUTION_ID, true, true);

        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, null, null, null);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, null, null, false);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, null, null, true);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, null, false, null);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, null, false, false);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, null, false, true);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, null, true, null);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, null, true, false);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, null, true, true);

        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, null, null);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, null, false);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, null, true);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, false, null);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, false, false);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, false, true);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, true, null);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, true, false);
        assertWriteAndParseExtensionWithEncoding(TEST_DMN_EVALUATE_DECISION, TEST_EXECUTION_ID, true, true);
    }

    private void assertWriteAndParseExtensionWithEncoding(String dmnEvaluateDecision, String executionId, Boolean dmnFullResult, Boolean dmnFilteredCtx) {
        CloudEvent inputEvent = cloudEventFromExtensionObject(dmnEvaluateDecision, executionId, dmnFullResult, dmnFilteredCtx);
        String inputEventJson = CloudEventUtils.encode(inputEvent).orElseThrow(IllegalStateException::new);
        CloudEvent outputEvent = CloudEventUtils.decode(inputEventJson).orElseThrow(IllegalStateException::new);
        KogitoExtension outputKogitoExt = ExtensionProvider.getInstance().parseExtension(KogitoExtension.class, outputEvent);
        assertExtension(outputKogitoExt, dmnEvaluateDecision, executionId, dmnFullResult, dmnFilteredCtx);
    }

    private CloudEvent cloudEventFromExtensionObject(String dmnEvaluateDecision, String executionId, Boolean dmnFullResult, Boolean dmnFilteredCtx) {
        KogitoExtension kogitoExt = new KogitoExtension();
        kogitoExt.setDmnModelName(TEST_DMN_MODEL_NAME);
        kogitoExt.setDmnModelNamespace(TEST_DMN_MODEL_NAMESPACE);

        if (dmnEvaluateDecision != null) {
            kogitoExt.setDmnEvaluateDecision(dmnEvaluateDecision);
        }

        if (executionId != null) {
            kogitoExt.setExecutionId(executionId);
        }

        if (dmnFullResult != null) {
            kogitoExt.setDmnFullResult(dmnFullResult);
        }

        if (dmnFilteredCtx != null) {
            kogitoExt.setDmnFilteredCtx(dmnFilteredCtx);
        }

        return CloudEventBuilder
                .v1()
                .withId(TEST_ID)
                .withSource(URI.create(TEST_SOURCE))
                .withType(TEST_TYPE)
                .withData(TEST_DATA.getBytes())
                .withExtension(kogitoExt)
                .build();
    }

    private void assertCloudEvent(CloudEvent event, String dmnEvaluateDecision, String executionId, Boolean dmnFullResult, Boolean dmnFilteredCtx) {
        assertNotNull(event);
        assertEquals(TEST_DMN_MODEL_NAME, event.getExtension(KogitoExtension.KOGITO_DMN_MODEL_NAME));
        assertEquals(TEST_DMN_MODEL_NAMESPACE, event.getExtension(KogitoExtension.KOGITO_DMN_MODEL_NAMESPACE));
        assertEquals(dmnEvaluateDecision, event.getExtension(KogitoExtension.KOGITO_DMN_EVALUATE_DECISION));
        assertEquals(executionId, event.getExtension(KogitoExtension.KOGITO_EXECUTION_ID));
        assertEquals(dmnFullResult, event.getExtension(KogitoExtension.KOGITO_DMN_FULL_RESULT));
        assertEquals(dmnFilteredCtx, event.getExtension(KogitoExtension.KOGITO_DMN_FILTERED_CTX));
    }

    private KogitoExtension extensionObjectFromCloudEvent(String dmnEvaluateDecision, String executionId, Boolean dmnFullResult, Boolean dmnFilteredCtx) {
        CloudEventBuilder builder = CloudEventBuilder.v1()
                .withId(TEST_ID)
                .withSource(URI.create(TEST_SOURCE))
                .withType(TEST_TYPE)
                .withData(TEST_DATA.getBytes())
                .withExtension(KogitoExtension.KOGITO_DMN_MODEL_NAME, TEST_DMN_MODEL_NAME)
                .withExtension(KogitoExtension.KOGITO_DMN_MODEL_NAMESPACE, TEST_DMN_MODEL_NAMESPACE);

        if (dmnEvaluateDecision != null) {
            builder.withExtension(KogitoExtension.KOGITO_DMN_EVALUATE_DECISION, dmnEvaluateDecision);
        }

        if (executionId != null) {
            builder.withExtension(KogitoExtension.KOGITO_EXECUTION_ID, executionId);
        }

        if (dmnFullResult != null) {
            builder.withExtension(KogitoExtension.KOGITO_DMN_FULL_RESULT, dmnFullResult);
        }

        if (dmnFilteredCtx != null) {
            builder.withExtension(KogitoExtension.KOGITO_DMN_FILTERED_CTX, dmnFilteredCtx);
        }

        return ExtensionProvider.getInstance().parseExtension(KogitoExtension.class, builder.build());
    }

    private void assertExtension(KogitoExtension kogitoExtension, String dmnEvaluateDecision, String executionId, Boolean dmnFullResult, Boolean dmnFilteredCtx) {
        assertNotNull(kogitoExtension);
        assertEquals(TEST_DMN_MODEL_NAME, kogitoExtension.getDmnModelName());
        assertEquals(TEST_DMN_MODEL_NAMESPACE, kogitoExtension.getDmnModelNamespace());
        assertEquals(dmnEvaluateDecision, kogitoExtension.getDmnEvaluateDecision());
        assertEquals(executionId, kogitoExtension.getExecutionId());
        assertSame(dmnFullResult, kogitoExtension.isDmnFullResult());
        assertSame(dmnFilteredCtx, kogitoExtension.isDmnFilteredCtx());
    }
}
