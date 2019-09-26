/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.infinispan.protostream.ProtobufService;
import org.kie.kogito.index.messaging.ReactiveMessagingEventConsumer;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.query.ProcessInstanceFilter;
import org.kie.kogito.index.query.UserTaskInstanceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.kie.kogito.index.GraphQLUtils.toGraphQLString;
import static org.kie.kogito.index.TestUtils.getDealsProtoBufferFile;
import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.TestUtils.getTravelsProtoBufferFile;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;

@QuarkusTest
@QuarkusTestResource(InfinispanServerTestResource.class)
public class IndexingServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingServiceTest.class);

    @Inject
    ReactiveMessagingEventConsumer consumer;

    @Inject
    ProtobufService protobufService;

    @BeforeAll
    public static void setup() {
        RestAssured.config = RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
    }

    private static String formatZonedDateTime(ZonedDateTime time) {
        return time.truncatedTo(ChronoUnit.MILLIS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Test
    public void testAddBrokenProtoFile() {
        try {
            protobufService.registerProtoBufferType(getBrokenProtoBufferFile());
            fail("Registering broken proto file should fail");
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("Failed to resolve type of field \"travels.traveller\". Type not found : stringa");
        }
    }

    @Test
    public void testAddProtoFileMissingModel() {
        try {
            protobufService.registerProtoBufferType(getProtoBufferFileWithoutModel());
            fail("Registering broken proto file should fail");
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("Missing marker for main message type in proto file, please add option kogito_model=\"messagename\"");
        }
    }

    @Test
    public void testAddProtoFileMissingId() {
        try {
            protobufService.registerProtoBufferType(getProtoBufferFileWithoutId());
            fail("Registering broken proto file should fail");
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("Missing marker for process id in proto file, please add option kogito_id=\"processid\"");
        }
    }

    @Test
    public void testAddProtoFileMissingModelType() {
        try {
            protobufService.registerProtoBufferType(getProtoBufferFileWithoutModelType());
            fail("Registering broken proto file should fail");
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("Could not find message with name: traveller in proto file, e, please review option kogito_model");
        }
    }

    @Test //Reproducer for KOGITO-172
    public void testAddProtoFileTwice() throws Exception {
        protobufService.registerProtoBufferType(getProtoBufferFileV1());
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{Game{ player, id, name, processInstances { id } } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Game", isA(Collection.class));
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessInstances", isA(Collection.class));

        protobufService.registerProtoBufferType(getProtoBufferFileV2());
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{Game{ id, name, company, processInstances { id } } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Game", isA(Collection.class));
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessInstances", isA(Collection.class));
    }
    
    @Test //Reproducer for KOGITO-334
    public void testDefaultGraphqlTypes(){
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessInstances", isA(Collection.class));

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.UserTaskInstances", isA(Collection.class));
    }

    @Test
    public void testAddProtoFile() throws Exception {
        String processId = "travels";
        String subProcessId = processId + "_sub";
        String processInstanceId = UUID.randomUUID().toString();
        String subProcessInstanceId = UUID.randomUUID().toString();
        String firstTaskId = UUID.randomUUID().toString();
        String secondTaskId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getTravelsProtoBufferFile());

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        KogitoProcessCloudEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        validateProcessInstance(toGraphQLString(ProcessInstanceFilter.builder().id(singletonList(processInstanceId)).state(singletonList(ProcessInstanceState.ACTIVE.ordinal())).build()), startEvent);

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.traveller.firstName:'ma*' and t.processInstances.id:'" + processInstanceId + "'\\\"){ id, flight { flightNumber }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId, start, end } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances.size()", is(1))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].start", is(formatZonedDateTime(startEvent.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[0].end", isEmptyOrNullString())
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].flight.flightNumber", is("MX555"));

        KogitoProcessCloudEvent subProcessStartEvent = getProcessCloudEvent(subProcessId, subProcessInstanceId, ProcessInstanceState.ACTIVE, processInstanceId, processId, processInstanceId);
        indexProcessCloudEvent(subProcessStartEvent);

        validateProcessInstance(toGraphQLString(ProcessInstanceFilter.builder().id(singletonList(subProcessInstanceId)).state(singletonList(ProcessInstanceState.ACTIVE.ordinal())).build()), subProcessStartEvent);

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.traveller.firstName:'ma*' and t.processInstances.id:'" + subProcessInstanceId + "'\\\"){ id, flight { flightNumber, arrival, departure }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId, start, end } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances.size()", is(2))
                .body("data.Travels[0].processInstances[0].id", is(subProcessInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(subProcessId))
                .body("data.Travels[0].processInstances[0].rootProcessId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].start", is(formatZonedDateTime(subProcessStartEvent.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[0].end", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[1].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[1].processId", is(processId))
                .body("data.Travels[0].processInstances[1].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[1].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[1].parentProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[1].start", is(formatZonedDateTime(startEvent.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[1].end", isEmptyOrNullString())
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].flight.flightNumber", is("MX555"))
                .body("data.Travels[0].flight.arrival", is("2019-08-20T22:12:57.340Z"))
                .body("data.Travels[0].flight.departure", is("2019-08-20T07:12:57.340Z"));

        KogitoProcessCloudEvent endEvent = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.COMPLETED, null, null, null);
        indexProcessCloudEvent(endEvent);

        validateProcessInstance(toGraphQLString(ProcessInstanceFilter.builder().id(singletonList(processInstanceId)).state(singletonList(ProcessInstanceState.COMPLETED.ordinal())).build()), endEvent);

        KogitoUserTaskCloudEvent firstUserTaskEvent = getUserTaskCloudEvent(firstTaskId, subProcessId, subProcessInstanceId, processInstanceId, processId);

        indexUserTaskCloudEvent(firstUserTaskEvent);

        validateUserTaskInstance(toGraphQLString(UserTaskInstanceFilter.builder().id(singletonList(firstTaskId)).build()), firstUserTaskEvent);

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.userTasks.id:'" + firstTaskId + "'\\\"){ id, flight { flightNumber, arrival, departure }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId, start, end }, userTasks { id, description, name, priority, processInstanceId, actualOwner } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].userTasks.size()", is(1))
                .body("data.Travels[0].userTasks[0].id", is(firstTaskId))
                .body("data.Travels[0].userTasks[0].processInstanceId", is(subProcessInstanceId))
                .body("data.Travels[0].userTasks[0].description", is("TaskDescription"))
                .body("data.Travels[0].userTasks[0].name", is("TaskName"))
                .body("data.Travels[0].userTasks[0].priority", is("High"))
                .body("data.Travels[0].userTasks[0].actualOwner", is("kogito"))
                .body("data.Travels[0].processInstances.size()", is(2))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].start", is(formatZonedDateTime(endEvent.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[0].end", is(formatZonedDateTime(endEvent.getData().getEnd().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[1].id", is(subProcessInstanceId))
                .body("data.Travels[0].processInstances[1].processId", is(subProcessId))
                .body("data.Travels[0].processInstances[1].rootProcessId", is(processId))
                .body("data.Travels[0].processInstances[1].rootProcessInstanceId", is(processInstanceId))
                .body("data.Travels[0].processInstances[1].parentProcessInstanceId", is(processInstanceId))
                .body("data.Travels[0].processInstances[1].start", is(formatZonedDateTime(subProcessStartEvent.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[1].end", isEmptyOrNullString())
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].flight.flightNumber", is("MX555"))
                .body("data.Travels[0].flight.arrival", is("2019-08-20T22:12:57.340Z"))
                .body("data.Travels[0].flight.departure", is("2019-08-20T07:12:57.340Z"));

        KogitoUserTaskCloudEvent secondUserTaskEvent = getUserTaskCloudEvent(secondTaskId, processId, processInstanceId, null, null);

        indexUserTaskCloudEvent(secondUserTaskEvent);

        validateUserTaskInstance(toGraphQLString(UserTaskInstanceFilter.builder().id(singletonList(secondTaskId)).build()), secondUserTaskEvent);

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.userTasks.id:'" + secondTaskId + "'\\\"){ id, flight { flightNumber, arrival, departure }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId, start, end }, userTasks { id, description, name, priority, processInstanceId, actualOwner } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].userTasks.size()", is(2))
                .body("data.Travels[0].userTasks[0].id", is(secondTaskId))
                .body("data.Travels[0].userTasks[0].processInstanceId", is(processInstanceId))
                .body("data.Travels[0].userTasks[0].description", is("TaskDescription"))
                .body("data.Travels[0].userTasks[0].name", is("TaskName"))
                .body("data.Travels[0].userTasks[0].priority", is("High"))
                .body("data.Travels[0].userTasks[0].actualOwner", is("kogito"))
                .body("data.Travels[0].userTasks[1].id", is(firstTaskId))
                .body("data.Travels[0].userTasks[1].processInstanceId", is(subProcessInstanceId))
                .body("data.Travels[0].userTasks[1].description", is("TaskDescription"))
                .body("data.Travels[0].userTasks[1].name", is("TaskName"))
                .body("data.Travels[0].userTasks[1].priority", is("High"))
                .body("data.Travels[0].userTasks[1].actualOwner", is("kogito"))
                .body("data.Travels[0].processInstances.size()", is(2))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].start", is(formatZonedDateTime(endEvent.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[0].end", is(formatZonedDateTime(endEvent.getData().getEnd().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[1].id", is(subProcessInstanceId))
                .body("data.Travels[0].processInstances[1].processId", is(subProcessId))
                .body("data.Travels[0].processInstances[1].rootProcessId", is(processId))
                .body("data.Travels[0].processInstances[1].rootProcessInstanceId", is(processInstanceId))
                .body("data.Travels[0].processInstances[1].parentProcessInstanceId", is(processInstanceId))
                .body("data.Travels[0].processInstances[1].start", is(formatZonedDateTime(subProcessStartEvent.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[1].end", isEmptyOrNullString())
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].flight.flightNumber", is("MX555"))
                .body("data.Travels[0].flight.arrival", is("2019-08-20T22:12:57.340Z"))
                .body("data.Travels[0].flight.departure", is("2019-08-20T07:12:57.340Z"));
    }

    private void validateProcessInstance(String query, KogitoProcessCloudEvent event) {
        LOGGER.debug("GraphQL query: {}", query);
        given().contentType(ContentType.JSON).body(query)
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.ProcessInstances[0].id", is(event.getProcessInstanceId()))
                .body("data.ProcessInstances[0].processId", is(event.getProcessId()))
                .body("data.ProcessInstances[0].rootProcessId", is(event.getRootProcessId()))
                .body("data.ProcessInstances[0].rootProcessInstanceId", is(event.getRootProcessInstanceId()))
                .body("data.ProcessInstances[0].parentProcessInstanceId", is(event.getParentProcessInstanceId()))
                .body("data.ProcessInstances[0].start", is(formatZonedDateTime(event.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.ProcessInstances[0].end", event.getData().getEnd() == null ? isEmptyOrNullString() : is(formatZonedDateTime(event.getData().getEnd().withZoneSameInstant(ZoneOffset.UTC))));
    }

    private void indexProcessCloudEvent(KogitoProcessCloudEvent event) throws Exception {
        consumer.onProcessInstanceEvent(event).toCompletableFuture().get();
        consumer.onProcessInstanceDomainEvent(event).toCompletableFuture().get();
    }

    private void indexUserTaskCloudEvent(KogitoUserTaskCloudEvent event) throws Exception {
        consumer.onUserTaskInstanceEvent(event).toCompletableFuture().get();
        consumer.onUserTaskInstanceDomainEvent(event).toCompletableFuture().get();
    }

    @Test
    public void testIndexingDomainUsingUserTaskEventFirst() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getTravelsProtoBufferFile());

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        KogitoUserTaskCloudEvent userTaskEvent = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null);
        consumer.onUserTaskInstanceDomainEvent(userTaskEvent).toCompletableFuture().get();

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.userTasks.id:'" + taskId + "'\\\"){ id, flight { flightNumber, arrival, departure }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId, start, end }, userTasks { id, description, name, priority, processInstanceId, actualOwner } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].flight", is(nullValue()))
                .body("data.Travels[0].hotel", is(nullValue()))
                .body("data.Travels[0].traveller", is(nullValue()))
                .body("data.Travels[0].userTasks.size()", is(1))
                .body("data.Travels[0].userTasks[0].id", is(taskId))
                .body("data.Travels[0].userTasks[0].processInstanceId", is(processInstanceId))
                .body("data.Travels[0].userTasks[0].description", is(userTaskEvent.getData().getDescription()))
                .body("data.Travels[0].userTasks[0].name", is(userTaskEvent.getData().getName()))
                .body("data.Travels[0].userTasks[0].priority", is(userTaskEvent.getData().getPriority()))
                .body("data.Travels[0].userTasks[0].actualOwner", is(userTaskEvent.getData().getActualOwner()))
                .body("data.Travels[0].processInstances", is(nullValue()));

        KogitoProcessCloudEvent processEvent = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null, null, null);
        consumer.onProcessInstanceDomainEvent(processEvent).toCompletableFuture().get();

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.processInstances.id:'" + processInstanceId + "'\\\"){ id, flight { flightNumber }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId }, userTasks { id, description, name, priority, processInstanceId, actualOwner } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].flight.flightNumber", is("MX555"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].userTasks.size()", is(1))
                .body("data.Travels[0].userTasks[0].id", is(taskId))
                .body("data.Travels[0].userTasks[0].processInstanceId", is(processInstanceId))
                .body("data.Travels[0].userTasks[0].description", is(userTaskEvent.getData().getDescription()))
                .body("data.Travels[0].userTasks[0].name", is(userTaskEvent.getData().getName()))
                .body("data.Travels[0].userTasks[0].priority", is(userTaskEvent.getData().getPriority()))
                .body("data.Travels[0].userTasks[0].actualOwner", is(userTaskEvent.getData().getActualOwner()))
                .body("data.Travels[0].processInstances.size()", is(1))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString());
    }

    @Test
    public void testIndexingDomainUsingProcessEventFirst() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getTravelsProtoBufferFile());

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        KogitoProcessCloudEvent processEvent = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null, null, null);
        consumer.onProcessInstanceDomainEvent(processEvent).toCompletableFuture().get();

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.processInstances.id:'" + processInstanceId + "'\\\"){ id, flight { flightNumber }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId }, userTasks { id, description, name, priority, processInstanceId, actualOwner } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].flight.flightNumber", is("MX555"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].userTasks", is(nullValue()))
                .body("data.Travels[0].processInstances.size()", is(1))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString());

        KogitoUserTaskCloudEvent userTaskEvent = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null);
        consumer.onUserTaskInstanceDomainEvent(userTaskEvent).toCompletableFuture().get();

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.userTasks.id:'" + taskId + "'\\\"){ id, flight { flightNumber, arrival, departure }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId, start, end }, userTasks { id, description, name, priority, processInstanceId, actualOwner } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].flight.flightNumber", is("MX555"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].userTasks.size()", is(1))
                .body("data.Travels[0].userTasks[0].id", is(taskId))
                .body("data.Travels[0].userTasks[0].processInstanceId", is(processInstanceId))
                .body("data.Travels[0].userTasks[0].description", is(userTaskEvent.getData().getDescription()))
                .body("data.Travels[0].userTasks[0].name", is(userTaskEvent.getData().getName()))
                .body("data.Travels[0].userTasks[0].priority", is(userTaskEvent.getData().getPriority()))
                .body("data.Travels[0].userTasks[0].actualOwner", is(userTaskEvent.getData().getActualOwner()))
                .body("data.Travels[0].processInstances.size()", is(1))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString());
    }

    @Test
    public void testIndexingDomainParallelEvents() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getTravelsProtoBufferFile());

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        KogitoProcessCloudEvent processEvent = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null, null, null);
        KogitoUserTaskCloudEvent userTaskEvent = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null);

        CompletableFuture.allOf(
                consumer.onProcessInstanceDomainEvent(processEvent).toCompletableFuture(),
                consumer.onUserTaskInstanceDomainEvent(userTaskEvent).toCompletableFuture()
        ).get();

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.processInstances.id:'" + processInstanceId + "'\\\"){ id, flight { flightNumber, arrival, departure }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId, start, end }, userTasks { id, description, name, priority, processInstanceId, actualOwner } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].flight.flightNumber", is("MX555"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].userTasks.size()", is(1))
                .body("data.Travels[0].userTasks[0].id", is(taskId))
                .body("data.Travels[0].userTasks[0].processInstanceId", is(processInstanceId))
                .body("data.Travels[0].userTasks[0].description", is(userTaskEvent.getData().getDescription()))
                .body("data.Travels[0].userTasks[0].name", is(userTaskEvent.getData().getName()))
                .body("data.Travels[0].userTasks[0].priority", is(userTaskEvent.getData().getPriority()))
                .body("data.Travels[0].userTasks[0].actualOwner", is(userTaskEvent.getData().getActualOwner()))
                .body("data.Travels[0].processInstances.size()", is(1))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString());
    }

    @Test
    public void testProcessInstanceIndex() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getTravelsProtoBufferFile());

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        KogitoProcessCloudEvent event = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null, null, null);
        indexProcessCloudEvent(event);

        validateProcessInstance(toGraphQLString(ProcessInstanceFilter.builder().id(singletonList(processInstanceId)).build()), event);
        validateProcessInstance(toGraphQLString(ProcessInstanceFilter.builder().id(singletonList(processInstanceId)).state(singletonList(ProcessInstanceState.ACTIVE.ordinal())).build()), event);
        validateProcessInstance(toGraphQLString(ProcessInstanceFilter.builder().id(singletonList(processInstanceId)).processId(singletonList(processId)).build()), event);

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.processInstances.id:'" + processInstanceId + "'\\\"){ id, flight { flightNumber }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId, state, start, end } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances.size()", is(1))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].state", is(ProcessInstanceState.ACTIVE.name()))
                .body("data.Travels[0].processInstances[0].start", is(formatZonedDateTime(event.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[0].end", isEmptyOrNullString())
                .body("data.Travels[0].flight.flightNumber", is("MX555"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].traveller.firstName", is("Maciej"));

        event = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.COMPLETED, null, null, null);
        event.getData().setEnd(ZonedDateTime.now());
        event.getData().setVariables("{ \"traveller\":{\"firstName\":\"Maciej\"},\"hotel\":{\"name\":\"Ibis\"},\"flight\":{\"arrival\":\"2019-08-20T22:12:57.340Z\",\"departure\":\"2019-08-20T07:12:57.340Z\",\"flightNumber\":\"QF444\"} }");
        indexProcessCloudEvent(event);

        validateProcessInstance(toGraphQLString(ProcessInstanceFilter.builder().id(singletonList(processInstanceId)).state(singletonList(ProcessInstanceState.COMPLETED.ordinal())).build()), event);

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.processInstances.id:'" + processInstanceId + "'\\\"){ id, flight { flightNumber }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId, state, start, end } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances.size()", is(1))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].state", is(ProcessInstanceState.COMPLETED.name()))
                .body("data.Travels[0].processInstances[0].start", is(formatZonedDateTime(event.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].processInstances[0].end", is(formatZonedDateTime(event.getData().getEnd().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Travels[0].flight.flightNumber", is("QF444"))
                .body("data.Travels[0].hotel.name", is("Ibis"))
                .body("data.Travels[0].traveller.firstName", is("Maciej"));
    }

    @Test
    public void testUserTaskInstanceIndex() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String processId = "deals";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getDealsProtoBufferFile());

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Deals{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Deals", isA(Collection.class));

        KogitoUserTaskCloudEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null);
        indexUserTaskCloudEvent(event);

        validateUserTaskInstance(toGraphQLString(UserTaskInstanceFilter.builder().id(singletonList(taskId)).build()), event);
        validateUserTaskInstance(toGraphQLString(UserTaskInstanceFilter.builder().id(singletonList(taskId)).actualOwner(singletonList("kogito")).build()), event);
        validateUserTaskInstance(toGraphQLString(UserTaskInstanceFilter.builder().id(singletonList(taskId)).potentialGroups(new ArrayList<>(event.getData().getPotentialGroups())).build()), event);
        validateUserTaskInstance(toGraphQLString(UserTaskInstanceFilter.builder().id(singletonList(taskId)).potentialUsers(new ArrayList<>(event.getData().getPotentialUsers())).build()), event);
        validateUserTaskInstance(toGraphQLString(UserTaskInstanceFilter.builder().id(singletonList(taskId)).state(singletonList(event.getData().getState())).build()), event);

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Deals(query: \\\"from org.acme.deals.Deals d where d.userTasks.id:'" + taskId + "'\\\"){ id, name, review, userTasks { id, description, state, name, priority, processInstanceId, actualOwner, started, completed } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Deals[0].id", is(processInstanceId))
                .body("data.Deals[0].userTasks.size()", is(1))
                .body("data.Deals[0].userTasks[0].id", is(taskId))
                .body("data.Deals[0].userTasks[0].description", is("TaskDescription"))
                .body("data.Deals[0].userTasks[0].state", is("InProgress"))
                .body("data.Deals[0].userTasks[0].name", is("TaskName"))
                .body("data.Deals[0].userTasks[0].priority", is("High"))
                .body("data.Deals[0].userTasks[0].actualOwner", is("kogito"))
                .body("data.Deals[0].userTasks[0].started", is(formatZonedDateTime(event.getData().getStarted().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Deals[0].userTasks[0].completed", is(formatZonedDateTime(event.getData().getCompleted().withZoneSameInstant(ZoneOffset.UTC))));

        event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null);
        event.getData().setCompleted(ZonedDateTime.now());
        event.getData().setPriority("Low");
        event.getData().setActualOwner("admin");
        event.getData().setState("Completed");

        indexUserTaskCloudEvent(event);

        validateUserTaskInstance(toGraphQLString(UserTaskInstanceFilter.builder().id(singletonList(taskId)).actualOwner(singletonList("admin")).build()), event);

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Deals(query: \\\"from org.acme.deals.Deals d where d.userTasks.id:'" + taskId + "'\\\"){ id, name, review, userTasks { id, description, state, name, priority, processInstanceId, actualOwner, started, completed } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Deals[0].id", is(processInstanceId))
                .body("data.Deals[0].userTasks.size()", is(1))
                .body("data.Deals[0].userTasks[0].id", is(taskId))
                .body("data.Deals[0].userTasks[0].description", is("TaskDescription"))
                .body("data.Deals[0].userTasks[0].state", is("Completed"))
                .body("data.Deals[0].userTasks[0].name", is("TaskName"))
                .body("data.Deals[0].userTasks[0].priority", is("Low"))
                .body("data.Deals[0].userTasks[0].actualOwner", is("admin"))
                .body("data.Deals[0].userTasks[0].started", is(formatZonedDateTime(event.getData().getStarted().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.Deals[0].userTasks[0].completed", is(formatZonedDateTime(event.getData().getCompleted().withZoneSameInstant(ZoneOffset.UTC))));
    }

    private void validateUserTaskInstance(String query, KogitoUserTaskCloudEvent event) {
        LOGGER.debug("GraphQL query: {}", query);
        given().contentType(ContentType.JSON).body(query)
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.UserTaskInstances[0].id", is(event.getUserTaskInstanceId()))
                .body("data.UserTaskInstances[0].processId", is(event.getProcessId()))
                .body("data.UserTaskInstances[0].rootProcessId", is(event.getRootProcessId()))
                .body("data.UserTaskInstances[0].rootProcessInstanceId", is(event.getRootProcessInstanceId()))
                .body("data.UserTaskInstances[0].description", is(event.getData().getDescription()))
                .body("data.UserTaskInstances[0].name", is(event.getData().getName()))
                .body("data.UserTaskInstances[0].priority", is(event.getData().getPriority()))
                .body("data.UserTaskInstances[0].actualOwner", is(event.getData().getActualOwner()))
                .body("data.UserTaskInstances[0].excludedUsers", hasItems(event.getData().getExcludedUsers().toArray()))
                .body("data.UserTaskInstances[0].potentialUsers", hasItems(event.getData().getPotentialUsers().toArray()))
                .body("data.UserTaskInstances[0].potentialGroups", hasItems(event.getData().getPotentialGroups().toArray()))
                .body("data.UserTaskInstances[0].started", is(formatZonedDateTime(event.getData().getStarted().withZoneSameInstant(ZoneOffset.UTC))))
                .body("data.UserTaskInstances[0].completed", is(formatZonedDateTime(event.getData().getCompleted().withZoneSameInstant(ZoneOffset.UTC))));
    }

    private String getProtoBufferFileWithoutModelType() {
        return "   option kogito_id=\"travels\";\n" +
                "   option kogito_model=\"traveller\";\n" +
                "message travels {\n" +
                "   optional string traveller = 1;\n" +
                "   optional string hotel = 2;\n" +
                "   optional string flight = 3;\n" +
                "}\n" +
                "\n";
    }

    private String getProtoBufferFileWithoutId() {
        return "   option kogito_model=\"travels\";\n" +
                "message travels {\n" +
                "   optional string traveller = 1;\n" +
                "   optional string hotel = 2;\n" +
                "   optional string flight = 3;\n" +
                "}\n" +
                "\n";
    }

    private String getProtoBufferFileWithoutModel() {
        return "   option kogito_id=\"travels\";\n" +
                "message travels {\n" +
                "   optional string traveller = 1;\n" +
                "   optional string hotel = 2;\n" +
                "   optional string flight = 3;\n" +
                "}\n" +
                "\n";
    }

    private String getBrokenProtoBufferFile() {
        return "message travels {\n" +
                "   optional stringa traveller = 1;\n" +
                "   optional string hotel = 2;\n" +
                "   optional string flight = 3;\n" +
                "}\n" +
                "\n";
    }

    private String getProtoBufferFileV1() {
        return "import \"kogito-index.proto\";\n" +
                "option kogito_model=\"Game\";\n" +
                "option kogito_id=\"game\";\n" +
                "message Game {\n" +
                "   optional string player = 1;\n" +
                "   optional string id = 2;\n" +
                "   optional string name = 3;\n" +
                "   repeated org.kie.kogito.index.model.ProcessInstanceMeta processInstances = 4;\n" +
                "   repeated org.kie.kogito.index.model.UserTaskInstanceMeta userTasks = 5;\n" +
                "}\n" +
                "\n";
    }

    private String getProtoBufferFileV2() {
        return "import \"kogito-index.proto\";\n" +
                "option kogito_model=\"Game\";\n" +
                "option kogito_id=\"game\";\n" +
                "message Game {\n" +
                "   optional string id = 1;\n" +
                "   optional string name = 2;\n" +
                "   optional string company = 3;\n" +
                "   repeated org.kie.kogito.index.model.ProcessInstanceMeta processInstances = 4;\n" +
                "   repeated org.kie.kogito.index.model.UserTaskInstanceMeta userTasks = 5;\n" +
                "}\n" +
                "\n";
    }
}