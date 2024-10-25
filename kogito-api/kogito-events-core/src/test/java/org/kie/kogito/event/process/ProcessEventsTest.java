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
package org.kie.kogito.event.process;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.DataEventFactory;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.serializer.MultipleProcessDataInstanceConverterFactory;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.jackson.JsonFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.toDate;

class ProcessEventsTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(JsonFormat.getCloudEventJacksonModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .findAndRegisterModules();

    private static final Logger logger = LoggerFactory.getLogger(ProcessEventsTest.class);

    private static final Set<String> BASE_EXTENSION_NAMES = Arrays.stream(new String[] {
            CloudEventExtensionConstants.PROCESS_INSTANCE_ID,
            CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_INSTANCE_ID,
            CloudEventExtensionConstants.PROCESS_ID,
            CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID,
            CloudEventExtensionConstants.ADDONS,
            CloudEventExtensionConstants.PROCESS_INSTANCE_VERSION,
            CloudEventExtensionConstants.PROCESS_PARENT_PROCESS_INSTANCE_ID,
            CloudEventExtensionConstants.PROCESS_INSTANCE_STATE,
            CloudEventExtensionConstants.BUSINESS_KEY,
            CloudEventExtensionConstants.PROCESS_TYPE,
            CloudEventExtensionConstants.IDENTITY }).collect(Collectors.toSet());

    private static final String PROCESS_INSTANCE_EVENT_TYPE = "ProcessInstanceEvent";
    private static final String USER_TASK_INSTANCE_EVENT_TYPE = "UserTaskInstanceEvent";
    private static final String VARIABLE_INSTANCE_EVENT_TYPE = "VariableInstanceEvent";

    private static final String ID = "ID";
    private static final SpecVersion SPEC_VERSION = SpecVersion.V1;
    private static final URI SOURCE = URI.create("http://event-test-source");
    private static final OffsetDateTime TIME = OffsetDateTime.parse("2021-11-24T18:00:00.000+01:00");
    private static final String SUBJECT = "SUBJECT";
    private static final String DATA_CONTENT_TYPE = "application/json";
    private static final URI DATA_SCHEMA = URI.create("http://event-test-source/data-schema");

    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String PROCESS_INSTANCE_VERSION = "PROCESS_INSTANCE_VERSION";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    private static final String PROCESS_PARENT_PROCESS_INSTANCE_ID = "PROCESS_PARENT_PROCESS_INSTANCE_ID";
    private static final String PROCESS_INSTANCE_STATE = "PROCESS_INSTANCE_STATE";
    private static final String BUSINESS_KEY = "BUSINESS_KEY";
    private static final String PROCESS_TYPE = "PROCESS_TYPE";
    private static final String ADDONS = "ADDONS";
    private static final int PROCESS_STATE = 1;
    private static final String NODE_CONTAINER_ID = "323";
    private static final String NODE_CONTAINER_INSTANCEID = "323-3232-3232";
    private static final String EXTENSION_1 = "EXTENSION_1";
    private static final String EXTENSION_1_VALUE = "EXTENSION_1_VALUE";
    private static final String EXTENSION_2 = "EXTENSION_2";
    private static final String EXTENSION_2_VALUE = "EXTENSION_2_VALUE";
    private static final String ERROR_MESSAGE = "AAAAAAHHHHH!!!!!";

    private static final int EVENT_TYPE = 1;

    private static final String NODE_NAME = "NODE_NAME";
    private static final String NODE_TYPE = "NODE_TYPE";

    private static final String VARIABLE_NAME = "VARIABLE_NAME";

    private static final String PROCESS_USER_TASK_INSTANCE_ID = "PROCESS_USER_TASK_INSTANCE_ID";
    private static final String PROCESS_USER_TASK_INSTANCE_STATE = "PROCESS_USER_TASK_INSTANCE_STATE";

    @Test
    void processInstanceDataEvent() throws Exception {
        ProcessInstanceStateDataEvent event = new ProcessInstanceStateDataEvent();
        setBaseEventValues(event, PROCESS_INSTANCE_EVENT_TYPE);
        setAdditionalExtensions(event);

        assertExtensionNames(event, BASE_EXTENSION_NAMES, EXTENSION_1, EXTENSION_2);

        String json = OBJECT_MAPPER.writeValueAsString(event);
        assertExtensionsNotDuplicated(json, event.getExtensionNames());

        ProcessInstanceStateDataEvent deserializedEvent = OBJECT_MAPPER.readValue(json, ProcessInstanceStateDataEvent.class);

        assertBaseEventValues(deserializedEvent, PROCESS_INSTANCE_EVENT_TYPE);
        assertThat(deserializedEvent.getExtension(EXTENSION_1)).isEqualTo(EXTENSION_1_VALUE);
        assertThat(deserializedEvent.getExtension(EXTENSION_2)).isEqualTo(EXTENSION_2_VALUE);
        assertExtensionNames(deserializedEvent, BASE_EXTENSION_NAMES, EXTENSION_1, EXTENSION_2);
    }

    @Test
    void multipleInstanceDataEvent() throws IOException {
        JsonNode expectedVarValue = OBJECT_MAPPER.createObjectNode().put("name", "John Doe");
        int standard = processMultipleInstanceDataEvent(expectedVarValue, false, false);
        int binary = processMultipleInstanceDataEvent(expectedVarValue, true, false);
        int binaryCompressed = processMultipleInstanceDataEvent(expectedVarValue, true, true);
        assertThat(standard).isGreaterThan(binary);
        assertThat(binary).isGreaterThan(binaryCompressed);
    }

    private int processMultipleInstanceDataEvent(JsonNode expectedVarValue, boolean binary, boolean compress) throws IOException {
        ProcessInstanceStateDataEvent stateEvent = new ProcessInstanceStateDataEvent();
        setBaseEventValues(stateEvent, ProcessInstanceStateDataEvent.STATE_TYPE);
        stateEvent.setData(ProcessInstanceStateEventBody.create().eventDate(toDate(TIME)).eventType(EVENT_TYPE).eventUser(SUBJECT)
                .businessKey(BUSINESS_KEY).processId(PROCESS_ID).processInstanceId(PROCESS_INSTANCE_ID).state(PROCESS_STATE)
                .processVersion(PROCESS_INSTANCE_VERSION).parentInstanceId(PROCESS_PARENT_PROCESS_INSTANCE_ID).processName(PROCESS_ID)
                .processType(PROCESS_TYPE).rootProcessId(ROOT_PROCESS_ID).rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID).build());

        ProcessInstanceVariableDataEvent varEvent = new ProcessInstanceVariableDataEvent();
        setBaseEventValues(varEvent, ProcessInstanceVariableDataEvent.VAR_TYPE);
        varEvent.addExtensionAttribute(CloudEventExtensionConstants.KOGITO_VARIABLE_NAME, VARIABLE_NAME);
        varEvent.setData(ProcessInstanceVariableEventBody.create().eventDate(toDate(TIME)).eventUser(SUBJECT)
                .processId(PROCESS_ID).processInstanceId(PROCESS_INSTANCE_ID).processVersion(PROCESS_INSTANCE_VERSION)
                .nodeContainerDefinitionId(NODE_CONTAINER_ID).nodeContainerInstanceId(NODE_CONTAINER_INSTANCEID)
                .variableName(VARIABLE_NAME)
                .variableId(VARIABLE_NAME)
                .variableValue(expectedVarValue)
                .build());

        ProcessInstanceErrorDataEvent errorEvent = new ProcessInstanceErrorDataEvent();
        setBaseEventValues(errorEvent, ProcessInstanceErrorDataEvent.ERROR_TYPE);
        errorEvent.setData(ProcessInstanceErrorEventBody.create().errorMessage(ERROR_MESSAGE).eventDate(toDate(TIME)).eventUser(SUBJECT)
                .processId(PROCESS_ID).processInstanceId(PROCESS_INSTANCE_ID).processVersion(PROCESS_INSTANCE_VERSION).nodeDefinitionId(NODE_CONTAINER_ID)
                .nodeInstanceId(NODE_CONTAINER_INSTANCEID).build());

        ProcessInstanceNodeDataEvent nodeEvent = new ProcessInstanceNodeDataEvent();
        setBaseEventValues(nodeEvent, ProcessInstanceNodeDataEvent.NODE_TYPE);
        nodeEvent
                .setData(ProcessInstanceNodeEventBody.create().processId(PROCESS_ID).processInstanceId(PROCESS_INSTANCE_ID).processVersion(PROCESS_INSTANCE_VERSION).nodeDefinitionId(NODE_CONTAINER_ID)
                        .nodeInstanceId(NODE_CONTAINER_INSTANCEID).eventDate(toDate(TIME)).eventUser(SUBJECT).connectionNodeDefinitionId(NODE_CONTAINER_ID).workItemId(NODE_CONTAINER_ID)
                        .nodeType(NODE_TYPE).nodeName(NODE_NAME)
                        .eventType(EVENT_TYPE).slaDueDate(toDate(TIME)).build());

        ProcessInstanceSLADataEvent slaEvent = new ProcessInstanceSLADataEvent();
        setBaseEventValues(slaEvent, ProcessInstanceSLADataEvent.SLA_TYPE);
        slaEvent
                .setData(ProcessInstanceSLAEventBody.create().processId(PROCESS_ID).processInstanceId(PROCESS_INSTANCE_ID).processVersion(PROCESS_INSTANCE_VERSION).nodeDefinitionId(NODE_CONTAINER_ID)
                        .nodeInstanceId(NODE_CONTAINER_INSTANCEID).eventDate(toDate(TIME)).eventUser(SUBJECT)
                        .nodeType(NODE_TYPE).nodeName(NODE_NAME).slaDueDate(toDate(TIME)).build());

        MultipleProcessInstanceDataEvent event = new MultipleProcessInstanceDataEvent(SOURCE, Arrays.asList(stateEvent, varEvent, errorEvent, nodeEvent, slaEvent));
        if (binary) {
            event.setDataContentType(MultipleProcessInstanceDataEvent.BINARY_CONTENT_TYPE);
        }
        if (compress) {
            event.setCompressed(compress);
        }

        byte[] json = OBJECT_MAPPER.writeValueAsBytes(event);
        logger.info("Serialized chunk size is {}", json.length);

        // cloud event structured mode check
        MultipleProcessInstanceDataEvent deserializedEvent = OBJECT_MAPPER.readValue(json, MultipleProcessInstanceDataEvent.class);
        assertThat(deserializedEvent.getData()).hasSize(event.getData().size());
        assertMultipleIntance(deserializedEvent, expectedVarValue);

        // cloud event binary mode check
        CloudEvent cloudEvent = OBJECT_MAPPER.readValue(json, CloudEvent.class);
        deserializedEvent = DataEventFactory.from(new MultipleProcessInstanceDataEvent(), cloudEvent, MultipleProcessDataInstanceConverterFactory.fromCloudEvent(cloudEvent, OBJECT_MAPPER));
        assertThat(deserializedEvent.getData()).hasSize(event.getData().size());
        assertMultipleIntance(deserializedEvent, expectedVarValue);
        return json.length;
    }

    private void assertMultipleIntance(MultipleProcessInstanceDataEvent deserializedEvent, JsonNode expectedVarValue) {

        Iterator<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>> iter = deserializedEvent.getData().iterator();
        ProcessInstanceStateDataEvent deserializedStateEvent = (ProcessInstanceStateDataEvent) iter.next();
        assertBaseEventValues(deserializedStateEvent, ProcessInstanceStateDataEvent.STATE_TYPE);
        assertExtensionNames(deserializedStateEvent, BASE_EXTENSION_NAMES);
        assertStateBody(deserializedStateEvent.getData());

        ProcessInstanceVariableDataEvent deserializedVariableEvent = (ProcessInstanceVariableDataEvent) iter.next();
        assertBaseEventValues(deserializedVariableEvent, ProcessInstanceVariableDataEvent.VAR_TYPE);
        assertExtensionNames(deserializedVariableEvent, BASE_EXTENSION_NAMES, CloudEventExtensionConstants.KOGITO_VARIABLE_NAME);
        assertThat(deserializedVariableEvent.getExtension(CloudEventExtensionConstants.KOGITO_VARIABLE_NAME)).isEqualTo(VARIABLE_NAME);
        assertVarBody(deserializedVariableEvent.getData(), expectedVarValue);

        ProcessInstanceErrorDataEvent deserializedErrorEvent = (ProcessInstanceErrorDataEvent) iter.next();
        assertBaseEventValues(deserializedErrorEvent, ProcessInstanceErrorDataEvent.ERROR_TYPE);
        assertExtensionNames(deserializedErrorEvent, BASE_EXTENSION_NAMES);
        assertErrorBody(deserializedErrorEvent.getData());

        ProcessInstanceNodeDataEvent deserializedNodeEvent = (ProcessInstanceNodeDataEvent) iter.next();
        assertBaseEventValues(deserializedNodeEvent, ProcessInstanceNodeDataEvent.NODE_TYPE);
        assertExtensionNames(deserializedNodeEvent, BASE_EXTENSION_NAMES);
        assertNodeBody(deserializedNodeEvent.getData());

        ProcessInstanceSLADataEvent deserializedSLAEvent = (ProcessInstanceSLADataEvent) iter.next();
        assertBaseEventValues(deserializedSLAEvent, ProcessInstanceSLADataEvent.SLA_TYPE);
        assertExtensionNames(deserializedSLAEvent, BASE_EXTENSION_NAMES);
        assertSLABody(deserializedSLAEvent.getData());
    }

    private void assertSLABody(ProcessInstanceSLAEventBody data) {
        assertThat(data.getNodeDefinitionId()).isEqualTo(NODE_CONTAINER_ID);
        assertThat(data.getNodeInstanceId()).isEqualTo(NODE_CONTAINER_INSTANCEID);
        assertThat(data.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(data.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(data.getProcessVersion()).isEqualTo(PROCESS_INSTANCE_VERSION);
        assertThat(data.getEventUser()).isEqualTo(SUBJECT);
        assertThat(data.getEventDate()).isEqualTo(toDate(TIME));
        assertThat(data.getSlaDueDate()).isEqualTo(toDate(TIME));
        assertThat(data.getNodeName()).isEqualTo(NODE_NAME);
        assertThat(data.getNodeType()).isEqualTo(NODE_TYPE);
    }

    private void assertNodeBody(ProcessInstanceNodeEventBody data) {
        assertThat(data.getNodeDefinitionId()).isEqualTo(NODE_CONTAINER_ID);
        assertThat(data.getNodeInstanceId()).isEqualTo(NODE_CONTAINER_INSTANCEID);
        assertThat(data.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(data.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(data.getProcessVersion()).isEqualTo(PROCESS_INSTANCE_VERSION);
        assertThat(data.getEventUser()).isEqualTo(SUBJECT);
        assertThat(data.getEventDate()).isEqualTo(toDate(TIME));
        assertThat(data.getEventType()).isEqualTo(EVENT_TYPE);
        assertThat(data.getConnectionNodeDefinitionId()).isEqualTo(NODE_CONTAINER_ID);
        assertThat(data.getWorkItemId()).isEqualTo(NODE_CONTAINER_ID);
        assertThat(data.getSlaDueDate()).isEqualTo(toDate(TIME));
        assertThat(data.getNodeName()).isEqualTo(NODE_NAME);
        assertThat(data.getNodeType()).isEqualTo(NODE_TYPE);
    }

    private void assertErrorBody(ProcessInstanceErrorEventBody data) {
        assertThat(data.getNodeDefinitionId()).isEqualTo(NODE_CONTAINER_ID);
        assertThat(data.getNodeInstanceId()).isEqualTo(NODE_CONTAINER_INSTANCEID);
        assertThat(data.getErrorMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(data.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(data.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(data.getProcessVersion()).isEqualTo(PROCESS_INSTANCE_VERSION);
        assertThat(data.getEventUser()).isEqualTo(SUBJECT);
        assertThat(data.getEventDate()).isEqualTo(toDate(TIME));
    }

    private static void assertVarBody(ProcessInstanceVariableEventBody data, JsonNode expectedVarValue) {
        assertThat(data.getVariableId()).isEqualTo(VARIABLE_NAME);
        assertThat(data.getVariableName()).isEqualTo(VARIABLE_NAME);
        assertThat(JsonObjectUtils.fromValue(data.getVariableValue())).isEqualTo(expectedVarValue);
        assertThat(data.getNodeContainerDefinitionId()).isEqualTo(NODE_CONTAINER_ID);
        assertThat(data.getNodeContainerInstanceId()).isEqualTo(NODE_CONTAINER_INSTANCEID);
        assertThat(data.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(data.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(data.getProcessVersion()).isEqualTo(PROCESS_INSTANCE_VERSION);
        assertThat(data.getEventUser()).isEqualTo(SUBJECT);
        assertThat(data.getEventDate()).isEqualTo(toDate(TIME));
    }

    private static void assertStateBody(ProcessInstanceStateEventBody data) {
        assertThat(data.getBusinessKey()).isEqualTo(BUSINESS_KEY);
        assertThat(data.getParentInstanceId()).isEqualTo(PROCESS_PARENT_PROCESS_INSTANCE_ID);
        assertThat(data.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(data.getProcessType()).isEqualTo(PROCESS_TYPE);
        assertThat(data.getState()).isEqualTo(PROCESS_STATE);
        assertThat(data.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(data.getEventType()).isEqualTo(EVENT_TYPE);
        assertThat(data.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(data.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(data.getProcessVersion()).isEqualTo(PROCESS_INSTANCE_VERSION);
        assertThat(data.getEventUser()).isEqualTo(SUBJECT);
        assertThat(data.getEventDate()).isEqualTo(toDate(TIME));
    }

    @Test
    void userTaskInstanceDataEvent() throws Exception {
        UserTaskInstanceStateDataEvent event = new UserTaskInstanceStateDataEvent();
        setBaseEventValues(event, USER_TASK_INSTANCE_EVENT_TYPE);
        event.addExtensionAttribute(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID, PROCESS_USER_TASK_INSTANCE_ID);
        event.addExtensionAttribute(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE, PROCESS_USER_TASK_INSTANCE_STATE);
        setAdditionalExtensions(event);

        assertExtensionNames(event, BASE_EXTENSION_NAMES,
                CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID, CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE,
                EXTENSION_1, EXTENSION_2);

        String json = OBJECT_MAPPER.writeValueAsString(event);
        assertExtensionsNotDuplicated(json, event.getExtensionNames());

        UserTaskInstanceStateDataEvent deserializedEvent = OBJECT_MAPPER.readValue(json, UserTaskInstanceStateDataEvent.class);
        assertBaseEventValues(deserializedEvent, USER_TASK_INSTANCE_EVENT_TYPE);
        assertThat(deserializedEvent.getExtension(EXTENSION_1)).isEqualTo(EXTENSION_1_VALUE);
        assertThat(deserializedEvent.getExtension(EXTENSION_2)).isEqualTo(EXTENSION_2_VALUE);
        assertThat(deserializedEvent.getExtension(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID)).isEqualTo(PROCESS_USER_TASK_INSTANCE_ID);
        assertThat(deserializedEvent.getExtension(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE)).isEqualTo(PROCESS_USER_TASK_INSTANCE_STATE);
        assertExtensionNames(deserializedEvent, BASE_EXTENSION_NAMES,
                CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID, CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE,
                EXTENSION_1, EXTENSION_2);
    }

    @Test
    void variableInstanceDataEvent() throws Exception {
        ProcessInstanceVariableDataEvent event = new ProcessInstanceVariableDataEvent();
        setBaseEventValues(event, VARIABLE_INSTANCE_EVENT_TYPE);
        event.addExtensionAttribute(CloudEventExtensionConstants.KOGITO_VARIABLE_NAME, VARIABLE_NAME);
        setAdditionalExtensions(event);

        assertExtensionNames(event, BASE_EXTENSION_NAMES, CloudEventExtensionConstants.KOGITO_VARIABLE_NAME, EXTENSION_1, EXTENSION_2);

        String json = OBJECT_MAPPER.writeValueAsString(event);
        assertExtensionsNotDuplicated(json, event.getExtensionNames());

        ProcessInstanceVariableDataEvent deserializedEvent = OBJECT_MAPPER.readValue(json, ProcessInstanceVariableDataEvent.class);
        assertBaseEventValues(deserializedEvent, VARIABLE_INSTANCE_EVENT_TYPE);
        assertThat(deserializedEvent.getExtension(EXTENSION_1)).isEqualTo(EXTENSION_1_VALUE);
        assertThat(deserializedEvent.getExtension(EXTENSION_2)).isEqualTo(EXTENSION_2_VALUE);
        assertThat(deserializedEvent.getExtension(CloudEventExtensionConstants.KOGITO_VARIABLE_NAME)).isEqualTo(VARIABLE_NAME);
        assertExtensionNames(event, BASE_EXTENSION_NAMES, CloudEventExtensionConstants.KOGITO_VARIABLE_NAME, EXTENSION_1, EXTENSION_2);
    }

    private static void setBaseEventValues(AbstractDataEvent<?> event, String eventType) {
        event.setType(eventType);
        event.setId(ID);
        event.setSpecVersion(SPEC_VERSION);
        event.setSource(SOURCE);
        event.setTime(TIME);
        event.setSubject(SUBJECT);
        event.setDataContentType(DATA_CONTENT_TYPE);
        event.setDataSchema(DATA_SCHEMA);

        event.setKogitoProcessInstanceId(PROCESS_INSTANCE_ID);
        event.setKogitoProcessInstanceVersion(PROCESS_INSTANCE_VERSION);
        event.setKogitoProcessId(PROCESS_ID);
        event.setKogitoRootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID);
        event.setKogitoRootProcessId(ROOT_PROCESS_ID);
        event.setKogitoParentProcessInstanceId(PROCESS_PARENT_PROCESS_INSTANCE_ID);
        event.setKogitoProcessInstanceState(PROCESS_INSTANCE_STATE);
        event.setKogitoBusinessKey(BUSINESS_KEY);
        event.setKogitoProcessType(PROCESS_TYPE);
        event.setKogitoAddons(ADDONS);
        event.setKogitoIdentity(SUBJECT);
    }

    private static void setAdditionalExtensions(AbstractDataEvent<?> event) {
        event.addExtensionAttribute(EXTENSION_1, EXTENSION_1_VALUE);
        event.addExtensionAttribute(EXTENSION_2, EXTENSION_2_VALUE);
    }

    private static void assertBaseEventValues(AbstractDataEvent<?> deserializedEvent, String eventType) {
        assertThat(deserializedEvent.getType()).isEqualTo(eventType);
        assertThat(deserializedEvent.getId()).isEqualTo(ID);
        assertThat(deserializedEvent.getSpecVersion()).isEqualTo(SPEC_VERSION);
        assertThat(deserializedEvent.getSource()).isEqualTo(SOURCE);
        assertThat(deserializedEvent.getTime()).isEqualTo(TIME);
        assertThat(deserializedEvent.getSubject()).isEqualTo(SUBJECT);
        assertThat(deserializedEvent.getDataContentType()).isEqualTo(DATA_CONTENT_TYPE);
        assertThat(deserializedEvent.getDataSchema()).isEqualTo(DATA_SCHEMA);
        assertThat(deserializedEvent.getKogitoProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(deserializedEvent.getKogitoProcessId()).isEqualTo(PROCESS_ID);
        assertThat(deserializedEvent.getKogitoRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(deserializedEvent.getKogitoRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(deserializedEvent.getKogitoParentProcessInstanceId()).isEqualTo(PROCESS_PARENT_PROCESS_INSTANCE_ID);
        assertThat(deserializedEvent.getKogitoProcessInstanceState()).isEqualTo(PROCESS_INSTANCE_STATE);
        assertThat(deserializedEvent.getKogitoBusinessKey()).isEqualTo(BUSINESS_KEY);
        assertThat(deserializedEvent.getKogitoProcessType()).isEqualTo(PROCESS_TYPE);
        assertThat(deserializedEvent.getKogitoIdentity()).isEqualTo(SUBJECT);
        assertThat(deserializedEvent.getKogitoAddons()).isEqualTo(ADDONS);
    }

    private static void assertExtensionNames(AbstractDataEvent<?> event, Set<String> baseNames, String... names) {
        Set<String> extensionNames = event.getExtensionNames();
        assertThat(extensionNames).hasSize(baseNames.size() + names.length)
                .containsAll(baseNames);
        if (names.length > 0) {
            assertThat(extensionNames).contains(names);
        }
    }

    private static void assertExtensionsNotDuplicated(String json, Set<String> extensionNames) {
        extensionNames.forEach(name -> assertOnlyOneTime(json, name));
    }

    private static void assertOnlyOneTime(String json, String propertyName) {
        int count = json.split("\"" + propertyName + "\"").length - 1;
        assertThat(count)
                .withFailMessage("It looks like the extension: %s is duplicated in json: %s", propertyName, json)
                .isEqualTo(1);
    }
}
