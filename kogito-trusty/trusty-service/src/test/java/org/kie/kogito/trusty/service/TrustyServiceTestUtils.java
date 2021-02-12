/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;
import org.kie.kogito.cloudevents.CloudEventUtils;
import org.kie.kogito.decision.DecisionModelType;
import org.kie.kogito.tracing.decision.event.message.Message;
import org.kie.kogito.tracing.decision.event.message.MessageCategory;
import org.kie.kogito.tracing.decision.event.message.MessageExceptionField;
import org.kie.kogito.tracing.decision.event.message.MessageLevel;
import org.kie.kogito.tracing.decision.event.model.ModelEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEventType;
import org.kie.kogito.tracing.decision.event.trace.TraceExecutionStep;
import org.kie.kogito.tracing.decision.event.trace.TraceExecutionStepType;
import org.kie.kogito.tracing.decision.event.trace.TraceHeader;
import org.kie.kogito.tracing.decision.event.trace.TraceInputValue;
import org.kie.kogito.tracing.decision.event.trace.TraceOutputValue;
import org.kie.kogito.tracing.decision.event.trace.TraceResourceId;
import org.kie.kogito.tracing.decision.event.trace.TraceType;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;

public class TrustyServiceTestUtils {

    public static final String CLOUDEVENT_SOURCE = "http://localhost:8080/Traffic+Violation";

    public static final String CORRECT_CLOUDEVENT_ID = "82639415-ceb1-411a-b3c8-4832e6a82905";
    public static final String CLOUDEVENT_WITH_ERRORS_ID = "6f8f5a8b-5477-464c-b5d3-1e3ed399e0da";
    public static final String CLOUDEVENT_WITH_NULL_FIELDS_ID = "03c3db32-5b93-473f-a83d-39e661e2462e";
    public static final String CLOUDEVENT_WITHOUT_DATA_ID = "7dad3bf4-14cc-4c8e-aa3c-8f4598865142";

    public static final ObjectMapper MAPPER = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());

    private static final long CORRECT_CLOUDEVENT_START_TS = 1594105482568L;
    private static final long CORRECT_CLOUDEVENT_DURATION = 26L;
    private static final long CLOUDEVENT_WITH_ERRORS_START_TS = 1594136494308L;
    private static final long CLOUDEVENT_WITH_ERRORS_DURATION = 165L;
    private static final long CLOUDEVENT_WITH_NULL_FIELDS_START_TS = 1594136494408L;
    private static final long CLOUDEVENT_WITH_NULL_FIELDS_DURATION = 265L;

    private static final String EVALUATION_STATUS_SKIPPED = "SKIPPED";
    private static final String EVALUATION_STATUS_SUCCEEDED = "SUCCEEDED";

    private static final String SERVICE_URL = "http://localhost:8080";
    private static final String MODEL_NAME = "Traffic Violation";
    private static final String MODEL_NAMESPACE = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";

    private static final String EXCEPTION_CAUSE_CLASS_NAME = "ExceptionCauseClass";
    private static final String EXCEPTION_CAUSE_MESSAGE = "Exception cause message";
    private static final String EXCEPTION_CLASS_NAME = "ExceptionClass";
    private static final String EXCEPTION_MESSAGE = "Exception message";
    private static final String INPUT_DRIVER_JSON = "{\"Age\": 25,\"Points\": 13}";
    private static final String INPUT_DRIVER_NODE_ID = "_1F9350D7-146D-46F1-85D8-15B5B68AF22A";
    private static final String INPUT_DRIVER_NODE_NAME = "Driver";
    private static final String INPUT_VIOLATION_JSON = "{\"Type\": \"speed\",\"Actual Speed\": 140,\"Speed Limit\": 100}";
    private static final String INPUT_VIOLATION_NODE_ID = "_1929CBD5-40E0-442D-B909-49CEDE0101DC";
    private static final String INPUT_VIOLATION_NODE_NAME = "Violation";
    private static final MessageCategory MESSAGE_INFO_CATEGORY = MessageCategory.INTERNAL;
    private static final String MESSAGE_INFO_TEXT = "This is a info test message";
    private static final String MESSAGE_INFO_TYPE = "INFO_TEST_MESSAGE";
    private static final MessageCategory MESSAGE_ERROR_CATEGORY = MessageCategory.DMN;
    private static final String MESSAGE_ERROR_TEXT = "DMN: Required dependency 'Driver' not found on node 'Should the driver be suspended?' (DMN id: _8A408366-D8E9-4626-ABF3-5F69AA01F880, The referenced node was not found) ";
    private static final String MESSAGE_ERROR_TYPE = "REQ_NOT_FOUND";
    private static final MessageCategory MESSAGE_WARNING_CATEGORY = MessageCategory.INTERNAL;
    private static final String MESSAGE_WARNING_TEXT = "This is a warning test message";
    private static final String MESSAGE_WARNING_TYPE = "WARNING_TEST_MESSAGE";
    private static final String OUTPUT_FINE_JSON = "{\"Points\": 7,\"Amount\": 1000}";
    private static final String OUTPUT_FINE_NODE_ID = "_4055D956-1C47-479C-B3F4-BAEB61F1C929";
    private static final String OUTPUT_FINE_NODE_NAME = "Fine";
    private static final String OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_JSON = "\"Yes\"";
    private static final String OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID = "_8A408366-D8E9-4626-ABF3-5F69AA01F880";
    private static final String OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_NAME = "Should the driver be suspended?";
    private static final String TYPE_DRIVER_NODE_ID = "_63824D3F-9173-446D-A940-6A7F0FA056BB";
    private static final String TYPE_FINE_NODE_ID = "_2D4F30EE-21A6-4A78-A524-A5C238D433AE";
    private static final String TYPE_VIOLATION_NODE_ID = "_40731093-0642-4588-9183-1660FC55053B";

    private static final TraceResourceId trafficViolationResourceId = new TraceResourceId(SERVICE_URL, MODEL_NAMESPACE, MODEL_NAME);
    private static final TraceType stringType = new TraceType(null, "http://www.omg.org/spec/DMN/20180521/FEEL/", "string");
    private static final TraceType tDriverType = new TraceType(TYPE_DRIVER_NODE_ID, MODEL_NAMESPACE, "tDriver");
    private static final TraceType tFineType = new TraceType(TYPE_FINE_NODE_ID, MODEL_NAMESPACE, "tFine");
    private static final TraceType tViolationType = new TraceType(TYPE_VIOLATION_NODE_ID, MODEL_NAMESPACE, "tViolation");

    private static final TypedValue vEventDriver = new StructureValue("tDriver", Map.of(
            "Age", new UnitValue("number", toJsonNode("25")),
            "Points", new UnitValue("number", toJsonNode("13"))
    ));
    private static final TypedValue vEventDriverNull = new StructureValue("tDriver", null);
    private static final TypedValue vEventViolation = new StructureValue("tViolation", Map.of(
            "Type", new UnitValue("string", toJsonNode("\"speed\"")),
            "Actual Speed", new UnitValue("number", toJsonNode("140")),
            "Speed Limit", new UnitValue("number", toJsonNode("100"))
    ));
    private static final TypedValue vEventFine = new StructureValue("tFine", Map.of(
            "Amount", new UnitValue("number", toJsonNode("1000")),
            "Points", new UnitValue("number", toJsonNode("7"))
    ));
    private static final TypedValue vEventSuspended = new UnitValue("string", toJsonNode("\"Yes\""));

    private static final TypedVariable vDecisionDriver = TypedVariable.buildStructure(INPUT_DRIVER_NODE_NAME, "tDriver", List.of(
            TypedVariable.buildUnit("Age", "number", toJsonNode("25")),
            TypedVariable.buildUnit("Points", "number", toJsonNode("13"))
    ));
    private static final TypedVariable vDecisionDriverNull = TypedVariable.buildStructure(INPUT_DRIVER_NODE_NAME, "tDriver", null);
    private static final TypedVariable vDecisionViolation = TypedVariable.buildStructure(INPUT_VIOLATION_NODE_NAME, "tViolation", List.of(
            TypedVariable.buildUnit("Type", "string", toJsonNode("\"speed\"")),
            TypedVariable.buildUnit("Actual Speed", "number", toJsonNode("140")),
            TypedVariable.buildUnit("Speed Limit", "number", toJsonNode("100"))
    ));
    private static final TypedVariable vDecisionFine = TypedVariable.buildStructure(OUTPUT_FINE_NODE_NAME, "tFine", List.of(
            TypedVariable.buildUnit("Amount", "number", toJsonNode("1000")),
            TypedVariable.buildUnit("Points", "number", toJsonNode("7"))
    ));
    private static final TypedVariable vDecisionSuspended = TypedVariable.buildUnit(OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_NAME, "string", toJsonNode("\"Yes\""));

    public static CloudEvent buildCloudEvent(TraceEvent traceEvent) {
        return CloudEventUtils.build(
                traceEvent.getHeader().getExecutionId(),
                URI.create(CLOUDEVENT_SOURCE),
                traceEvent,
                TraceEvent.class
        ).get();
    }

    public static String buildCloudEventJsonString(TraceEvent traceEvent) {
        return CloudEventUtils.encode(buildCloudEvent(traceEvent)).orElseThrow(IllegalStateException::new);
    }

    public static CloudEvent buildCloudEventWithoutData() {
        return CloudEventUtils.build(CLOUDEVENT_WITHOUT_DATA_ID, URI.create(URLEncoder.encode(MODEL_NAME, StandardCharsets.UTF_8)), null, TraceEvent.class).orElseThrow(IllegalStateException::new);
    }

    public static String buildCloudEventWithoutDataJsonString() {
        return CloudEventUtils.encode(buildCloudEventWithoutData()).orElseThrow(IllegalStateException::new);
    }

    public static TraceEvent buildCorrectTraceEvent(String cloudEventId) {
        return new TraceEvent(
                buildHeader(cloudEventId, CORRECT_CLOUDEVENT_START_TS, CORRECT_CLOUDEVENT_START_TS + CORRECT_CLOUDEVENT_DURATION, CORRECT_CLOUDEVENT_DURATION, null),
                List.of(
                        buildInputViolation(INPUT_VIOLATION_JSON, null),
                        buildInputDriver(INPUT_DRIVER_JSON, null)
                ),
                List.of(
                        buildOutputFine(OUTPUT_FINE_JSON, null),
                        buildOutputShouldTheDriverBeSuspended(EVALUATION_STATUS_SUCCEEDED, OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_JSON, null)
                ),
                List.of(
                        new TraceExecutionStep(
                                TraceExecutionStepType.DMN_DECISION, 10, OUTPUT_FINE_NODE_NAME,
                                toJsonNode(OUTPUT_FINE_JSON),
                                null,
                                Map.of("nodeId", OUTPUT_FINE_NODE_ID),
                                List.of(
                                        new TraceExecutionStep(
                                                TraceExecutionStepType.DMN_DECISION_TABLE, 8, OUTPUT_FINE_NODE_NAME,
                                                null,
                                                null,
                                                Map.of(
                                                        "matches", "2",
                                                        "nodeId", OUTPUT_FINE_NODE_ID,
                                                        "selected", "2"
                                                ),
                                                null
                                        )
                                )
                        ),
                        new TraceExecutionStep(
                                TraceExecutionStepType.DMN_DECISION, 9, OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_NAME,
                                toJsonNode(OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_JSON),
                                null,
                                Map.of("nodeId", OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID),
                                List.of(
                                        new TraceExecutionStep(
                                                TraceExecutionStepType.DMN_BKM_EVALUATION, 1, "calculateTotalPoints",
                                                null,
                                                null,
                                                Map.of(
                                                        "nodeId", "_AB2593EF-E85F-425B-B5F8-9A29397CA4E9"
                                                ),
                                                null
                                        ),
                                        new TraceExecutionStep(
                                                TraceExecutionStepType.DMN_CONTEXT_ENTRY, 6, "Total Points",
                                                toJsonNode("20"),
                                                null,
                                                Map.of(
                                                        "expressionId", "_F1BEBF16-033F-4A25-9523-CAC23ACC5DFC",
                                                        "variableId", "_09385E8D-68E0-4DFD-AAD8-141C15C96B71",
                                                        "nodeId", OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID
                                                ),
                                                null
                                        ),
                                        new TraceExecutionStep(
                                                TraceExecutionStepType.DMN_CONTEXT_ENTRY, 0, "__RESULT__",
                                                toJsonNode(OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_JSON),
                                                null,
                                                Map.of(
                                                        "expressionId", "_1929D813-B1C9-43C5-9497-CE5D8B2B040C",
                                                        "nodeId", OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID
                                                ),
                                                null
                                        )
                                )
                        )
                )
        );
    }

    public static TraceEvent buildTraceEventWithNullType(String cloudEventId) {
        return new TraceEvent(
                buildNullTypeHeader(cloudEventId, CORRECT_CLOUDEVENT_START_TS, CORRECT_CLOUDEVENT_START_TS + CORRECT_CLOUDEVENT_DURATION, CORRECT_CLOUDEVENT_DURATION, null),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static Decision buildCorrectDecision(String cloudEventId) {
        return new Decision(
                cloudEventId, CLOUDEVENT_SOURCE, CORRECT_CLOUDEVENT_START_TS, true, null, MODEL_NAME, MODEL_NAMESPACE,
                List.of(
                        new DecisionInput(INPUT_VIOLATION_NODE_ID, INPUT_VIOLATION_NODE_NAME, vDecisionViolation),
                        new DecisionInput(INPUT_DRIVER_NODE_ID, INPUT_DRIVER_NODE_NAME, vDecisionDriver)
                ),
                List.of(
                        new DecisionOutcome(
                                OUTPUT_FINE_NODE_ID, OUTPUT_FINE_NODE_NAME, EVALUATION_STATUS_SUCCEEDED,
                                vDecisionFine,
                                List.of(vDecisionViolation), null
                        ),
                        new DecisionOutcome(
                                OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID, OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_NAME, EVALUATION_STATUS_SUCCEEDED,
                                vDecisionSuspended,
                                List.of(vDecisionDriver, vDecisionFine), null
                        )
                )
        );
    }

    public static TraceEvent buildTraceEventWithErrors() {
        return new TraceEvent(
                buildHeader(CLOUDEVENT_WITH_ERRORS_ID, CLOUDEVENT_WITH_ERRORS_START_TS, CLOUDEVENT_WITH_ERRORS_START_TS + CLOUDEVENT_WITH_ERRORS_DURATION, CLOUDEVENT_WITH_ERRORS_DURATION, null),
                List.of(
                        buildInputViolation(INPUT_VIOLATION_JSON, null),
                        buildInputDriver(null, null)
                ),
                List.of(
                        buildOutputFine(OUTPUT_FINE_JSON, List.of(
                                new Message(
                                        MessageLevel.WARNING, MESSAGE_WARNING_CATEGORY, MESSAGE_WARNING_TYPE,
                                        OUTPUT_FINE_NODE_ID,
                                        MESSAGE_WARNING_TEXT,
                                        null,
                                        new MessageExceptionField(
                                                EXCEPTION_CLASS_NAME, EXCEPTION_MESSAGE,
                                                new MessageExceptionField(EXCEPTION_CAUSE_CLASS_NAME, EXCEPTION_CAUSE_MESSAGE, null)
                                        )
                                ),
                                new Message(
                                        MessageLevel.INFO, MESSAGE_INFO_CATEGORY, MESSAGE_INFO_TYPE,
                                        OUTPUT_FINE_NODE_ID,
                                        MESSAGE_INFO_TEXT,
                                        null,
                                        null
                                )
                        )),
                        buildOutputShouldTheDriverBeSuspended(EVALUATION_STATUS_SKIPPED, null, List.of(
                                new Message(
                                        MessageLevel.ERROR, MESSAGE_ERROR_CATEGORY, MESSAGE_ERROR_TYPE,
                                        OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID,
                                        MESSAGE_ERROR_TEXT,
                                        null,
                                        null
                                )
                        ))
                ),
                List.of(
                        new TraceExecutionStep(
                                TraceExecutionStepType.DMN_DECISION, 81, OUTPUT_FINE_NODE_NAME,
                                toJsonNode(OUTPUT_FINE_JSON),
                                null,
                                Map.of("nodeId", OUTPUT_FINE_NODE_ID),
                                List.of(
                                        new TraceExecutionStep(
                                                TraceExecutionStepType.DMN_DECISION_TABLE, 80, OUTPUT_FINE_NODE_NAME,
                                                null,
                                                null,
                                                Map.of(
                                                        "matches", "2",
                                                        "nodeId", OUTPUT_FINE_NODE_ID,
                                                        "selected", "2"
                                                ),
                                                null
                                        )
                                )
                        ),
                        new TraceExecutionStep(
                                TraceExecutionStepType.DMN_DECISION, 9, OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_NAME,
                                toJsonNode(OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_JSON),
                                List.of(
                                        new Message(
                                                MessageLevel.ERROR, MESSAGE_ERROR_CATEGORY, MESSAGE_ERROR_TYPE,
                                                OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID,
                                                MESSAGE_ERROR_TEXT,
                                                null,
                                                null
                                        )
                                ),
                                Map.of("nodeId", OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID),
                                List.of(
                                        new TraceExecutionStep(
                                                TraceExecutionStepType.DMN_BKM_EVALUATION, 1, "calculateTotalPoints",
                                                null,
                                                null,
                                                Map.of(
                                                        "nodeId", "_AB2593EF-E85F-425B-B5F8-9A29397CA4E9"
                                                ),
                                                null
                                        )
                                )
                        )
                )
        );
    }

    public static Decision buildDecisionWithErrors() {
        return new Decision(
                CLOUDEVENT_WITH_ERRORS_ID, CLOUDEVENT_SOURCE, CLOUDEVENT_WITH_ERRORS_START_TS, false, null, MODEL_NAME, MODEL_NAMESPACE,
                List.of(
                        new DecisionInput(INPUT_VIOLATION_NODE_ID, INPUT_VIOLATION_NODE_NAME, vDecisionViolation),
                        new DecisionInput(INPUT_DRIVER_NODE_ID, INPUT_DRIVER_NODE_NAME, vDecisionDriverNull)
                ),
                List.of(
                        new DecisionOutcome(
                                OUTPUT_FINE_NODE_ID, OUTPUT_FINE_NODE_NAME, EVALUATION_STATUS_SUCCEEDED,
                                vDecisionFine,
                                List.of(vDecisionViolation),
                                List.of(
                                        new org.kie.kogito.trusty.storage.api.model.Message(
                                                MessageLevel.WARNING, MESSAGE_WARNING_CATEGORY.name(), MESSAGE_WARNING_TYPE,
                                                OUTPUT_FINE_NODE_ID,
                                                MESSAGE_WARNING_TEXT,
                                                new org.kie.kogito.trusty.storage.api.model.MessageExceptionField(
                                                        EXCEPTION_CLASS_NAME, EXCEPTION_MESSAGE,
                                                        new org.kie.kogito.trusty.storage.api.model.MessageExceptionField(EXCEPTION_CAUSE_CLASS_NAME, "Exception cause message", null)
                                                )
                                        ),
                                        new org.kie.kogito.trusty.storage.api.model.Message(
                                                MessageLevel.INFO, MESSAGE_INFO_CATEGORY.name(), MESSAGE_INFO_TYPE,
                                                OUTPUT_FINE_NODE_ID,
                                                MESSAGE_INFO_TEXT,
                                                null
                                        )
                                )
                        ),
                        new DecisionOutcome(
                                OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID, OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_NAME, EVALUATION_STATUS_SUCCEEDED,
                                vDecisionSuspended,
                                List.of(vDecisionDriverNull, vDecisionFine),
                                List.of(
                                        new org.kie.kogito.trusty.storage.api.model.Message(
                                                MessageLevel.ERROR, MESSAGE_ERROR_CATEGORY.name(), MESSAGE_ERROR_TYPE,
                                                OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID,
                                                MESSAGE_ERROR_TEXT,
                                                null
                                        )
                                )
                        )
                )
        );
    }

    public static TraceEvent buildTraceEventWithNullFields() {
        return new TraceEvent(
                buildHeader(CLOUDEVENT_WITH_NULL_FIELDS_ID, CLOUDEVENT_WITH_NULL_FIELDS_START_TS, CLOUDEVENT_WITH_NULL_FIELDS_START_TS + CLOUDEVENT_WITH_NULL_FIELDS_DURATION, CLOUDEVENT_WITH_NULL_FIELDS_DURATION, null),
                null, null, null
        );
    }

    public static Decision buildDecisionWithNullFields() {
        return new Decision(CLOUDEVENT_WITH_NULL_FIELDS_ID, CLOUDEVENT_SOURCE, CLOUDEVENT_WITH_NULL_FIELDS_START_TS, false, null, MODEL_NAME, MODEL_NAMESPACE, null, null);
    }

    private static TraceHeader buildNullTypeHeader(String executionId, Long startTs, Long endTs, Long duration, List<Message> messages) {
        return new TraceHeader(null, executionId, startTs, endTs, duration, trafficViolationResourceId, messages);
    }

    private static TraceHeader buildHeader(String executionId, Long startTs, Long endTs, Long duration, List<Message> messages) {
        return new TraceHeader(TraceEventType.DMN, executionId, startTs, endTs, duration, trafficViolationResourceId, messages);
    }

    private static TraceInputValue buildInputDriver(String value, List<Message> messages) {
        return new TraceInputValue(INPUT_DRIVER_NODE_ID, INPUT_DRIVER_NODE_NAME, value == null ? vEventDriverNull : vEventDriver, messages);
    }

    private static TraceInputValue buildInputViolation(String value, List<Message> messages) {
        return new TraceInputValue(INPUT_VIOLATION_NODE_ID, INPUT_VIOLATION_NODE_NAME, vEventViolation, messages);
    }

    private static TraceOutputValue buildOutputFine(String value, List<Message> messages) {
        return new TraceOutputValue(OUTPUT_FINE_NODE_ID, OUTPUT_FINE_NODE_NAME, EVALUATION_STATUS_SUCCEEDED, vEventFine, Map.of(INPUT_VIOLATION_NODE_NAME, vEventViolation), messages);
    }

    private static TraceOutputValue buildOutputShouldTheDriverBeSuspended(String status, String value, List<Message> messages) {
        return new TraceOutputValue(OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_ID, OUTPUT_SHOULD_THE_DRIVER_BE_SUSPENDED_NODE_NAME,
                status,
                value == null ? null : vEventSuspended,
                value == null ? Map.of("Fine", vEventFine) : Map.of("Driver", vEventDriver, "Fine", vEventFine),
                messages
        );
    }

    private static JsonNode toJsonNode(String serializedJson) {
        if (serializedJson == null) {
            return null;
        }
        try {
            return MAPPER.readTree(serializedJson);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static ModelEvent buildCorrectModelEvent() {
        final ModelEvent.GAV gav = new ModelEvent.GAV("groupId", "artifactId", "version");
        return new ModelEvent(gav, "name", "namespace", DecisionModelType.DMN, "definition");
    }

    public static String buildCloudEventJsonString(ModelEvent modelEvent) {
        return CloudEventUtils.encode(buildCloudEvent(modelEvent)).orElseThrow(IllegalStateException::new);
    }

    public static CloudEvent buildCloudEvent(ModelEvent modelEvent) {
        return CloudEventUtils.build("id",
                                     URI.create(URLEncoder.encode(ModelEvent.class.getName(), StandardCharsets.UTF_8)),
                                     modelEvent,
                                     ModelEvent.class).get();
    }
}
