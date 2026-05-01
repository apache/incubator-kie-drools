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
package org.drools.grpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;
import org.drools.grpc.proto.CreateSessionRequest;
import org.drools.grpc.proto.CreateSessionResponse;
import org.drools.grpc.proto.DisposeSessionRequest;
import org.drools.grpc.proto.DisposeSessionResponse;
import org.drools.grpc.proto.ExecuteRequest;
import org.drools.grpc.proto.ExecuteResponse;
import org.drools.grpc.proto.Fact;
import org.drools.grpc.proto.FireAllRulesRequest;
import org.drools.grpc.proto.FireAllRulesResponse;
import org.drools.grpc.proto.GetFactsRequest;
import org.drools.grpc.proto.GetFactsResponse;
import org.drools.grpc.proto.InsertFactRequest;
import org.drools.grpc.proto.InsertFactResponse;
import org.drools.grpc.proto.UpdateFactRequest;
import org.drools.grpc.proto.UpdateFactResponse;
import org.drools.grpc.proto.StreamEvent;
import org.drools.grpc.proto.StreamEventType;
import org.drools.grpc.proto.StreamResult;
import org.drools.grpc.proto.StreamResultType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;

import static org.assertj.core.api.Assertions.assertThat;

class DroolsRuleServiceImplTest {

    private Server server;
    private ManagedChannel channel;

    @BeforeEach
    void setUp() throws Exception {
        KieBase kieBase = createKieBase();
        String serverName = InProcessServerBuilder.generateName();

        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new DroolsRuleServiceImpl(kieBase))
                .build()
                .start();

        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();
    }

    @AfterEach
    void tearDown() {
        channel.shutdownNow();
        server.shutdownNow();
    }

    @Test
    void shouldExecuteStatelessRules() {
        Fact orderFact = Fact.newBuilder()
                .setType("org.drools.grpc.TestOrder")
                .setJson("{\"amount\": 150.0, \"discount\": 0.0}")
                .build();

        ExecuteRequest request = ExecuteRequest.newBuilder()
                .addFacts(orderFact)
                .build();

        ExecuteResponse response = blockingUnary(
                DroolsRuleServiceGrpc.EXECUTE_METHOD, request);

        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getRulesFired()).isGreaterThan(0);
        assertThat(response.getRulesFiredNamesList()).contains("Apply Discount");
    }

    @Test
    void shouldManageStatefulSessions() {
        // Create session
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("test-session").build());
        assertThat(createResp.getSuccess()).isTrue();
        assertThat(createResp.getSessionId()).isEqualTo("test-session");

        // Insert fact
        Fact orderFact = Fact.newBuilder()
                .setType("org.drools.grpc.TestOrder")
                .setJson("{\"amount\": 200.0, \"discount\": 0.0}")
                .build();
        InsertFactResponse insertResp = blockingUnary(
                DroolsRuleServiceGrpc.INSERT_FACT_METHOD,
                InsertFactRequest.newBuilder()
                        .setSessionId("test-session")
                        .setFact(orderFact)
                        .build());
        assertThat(insertResp.getSuccess()).isTrue();
        assertThat(insertResp.getFactHandleId()).isNotEmpty();

        // Fire rules
        FireAllRulesResponse fireResp = blockingUnary(
                DroolsRuleServiceGrpc.FIRE_ALL_RULES_METHOD,
                FireAllRulesRequest.newBuilder()
                        .setSessionId("test-session")
                        .build());
        assertThat(fireResp.getSuccess()).isTrue();
        assertThat(fireResp.getRulesFired()).isGreaterThan(0);

        // Get facts
        GetFactsResponse getResp = blockingUnary(
                DroolsRuleServiceGrpc.GET_FACTS_METHOD,
                GetFactsRequest.newBuilder()
                        .setSessionId("test-session")
                        .build());
        assertThat(getResp.getSuccess()).isTrue();
        assertThat(getResp.getFactsList()).isNotEmpty();

        // Dispose session
        DisposeSessionResponse disposeResp = blockingUnary(
                DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder()
                        .setSessionId("test-session")
                        .build());
        assertThat(disposeResp.getSuccess()).isTrue();
    }

    @Test
    void shouldUpdateFactInStatefulSession() {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("update-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        Fact orderFact = Fact.newBuilder()
                .setType("org.drools.grpc.TestOrder")
                .setJson("{\"amount\": 50.0, \"discount\": 0.0}")
                .build();
        InsertFactResponse insertResp = blockingUnary(
                DroolsRuleServiceGrpc.INSERT_FACT_METHOD,
                InsertFactRequest.newBuilder()
                        .setSessionId("update-session")
                        .setFact(orderFact)
                        .build());
        assertThat(insertResp.getSuccess()).isTrue();
        String handleId = insertResp.getFactHandleId();

        Fact updatedFact = Fact.newBuilder()
                .setType("org.drools.grpc.TestOrder")
                .setJson("{\"amount\": 200.0, \"discount\": 0.0}")
                .build();
        UpdateFactResponse updateResp = blockingUnary(
                DroolsRuleServiceGrpc.UPDATE_FACT_METHOD,
                UpdateFactRequest.newBuilder()
                        .setSessionId("update-session")
                        .setFactHandleId(handleId)
                        .setFact(updatedFact)
                        .build());
        assertThat(updateResp.getSuccess()).isTrue();

        FireAllRulesResponse fireResp = blockingUnary(
                DroolsRuleServiceGrpc.FIRE_ALL_RULES_METHOD,
                FireAllRulesRequest.newBuilder()
                        .setSessionId("update-session")
                        .build());
        assertThat(fireResp.getSuccess()).isTrue();
        assertThat(fireResp.getRulesFiredNamesList()).contains("Apply Discount");

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("update-session").build());
    }

    @Test
    void shouldReturnErrorForInvalidSession() {
        FireAllRulesResponse response = blockingUnary(
                DroolsRuleServiceGrpc.FIRE_ALL_RULES_METHOD,
                FireAllRulesRequest.newBuilder()
                        .setSessionId("nonexistent")
                        .build());
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getErrorMessage()).contains("nonexistent");
    }

    @Test
    void shouldReturnErrorForInvalidFactType() {
        Fact badFact = Fact.newBuilder()
                .setType("com.nonexistent.Foo")
                .setJson("{}")
                .build();
        ExecuteResponse response = blockingUnary(
                DroolsRuleServiceGrpc.EXECUTE_METHOD,
                ExecuteRequest.newBuilder().addFacts(badFact).build());
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getErrorMessage()).contains("nonexistent");
    }

    // --- Streaming tests ---

    @Test
    void shouldStreamInsertAndFireRules() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("stream-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);

        StreamObserver<StreamResult> responseObserver = new StreamObserver<StreamResult>() {
            @Override public void onNext(StreamResult result) { results.add(result); }
            @Override public void onError(Throwable t) { completedLatch.countDown(); }
            @Override public void onCompleted() { completedLatch.countDown(); }
        };

        StreamObserver<StreamEvent> requestObserver = ClientCalls.asyncBidiStreamingCall(
                channel.newCall(DroolsRuleServiceGrpc.STREAMING_SESSION_METHOD,
                        io.grpc.CallOptions.DEFAULT),
                responseObserver);

        Fact orderFact = Fact.newBuilder()
                .setType("org.drools.grpc.TestOrder")
                .setJson("{\"amount\": 250.0, \"discount\": 0.0}")
                .build();

        requestObserver.onNext(StreamEvent.newBuilder()
                .setSessionId("stream-session")
                .setType(StreamEventType.INSERT_FACT_EVENT)
                .setFact(orderFact)
                .setSequenceId(1)
                .build());

        requestObserver.onNext(StreamEvent.newBuilder()
                .setSessionId("stream-session")
                .setType(StreamEventType.FIRE_RULES_EVENT)
                .setSequenceId(2)
                .build());

        requestObserver.onNext(StreamEvent.newBuilder()
                .setSessionId("stream-session")
                .setType(StreamEventType.GET_FACTS_EVENT)
                .setSequenceId(3)
                .build());

        requestObserver.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        List<StreamResult> insertResults = results.stream()
                .filter(r -> r.getType() == StreamResultType.FACT_INSERTED)
                .toList();
        assertThat(insertResults).hasSize(1);
        assertThat(insertResults.get(0).getSuccess()).isTrue();
        assertThat(insertResults.get(0).getFactHandleId()).isNotEmpty();
        assertThat(insertResults.get(0).getSequenceId()).isEqualTo(1);

        List<StreamResult> fireResults = results.stream()
                .filter(r -> r.getType() == StreamResultType.RULES_FIRED)
                .toList();
        assertThat(fireResults).hasSize(1);
        assertThat(fireResults.get(0).getRulesFired()).isGreaterThan(0);
        assertThat(fireResults.get(0).getRulesFiredNamesList()).contains("Apply Discount");
        assertThat(fireResults.get(0).getSequenceId()).isEqualTo(2);

        List<StreamResult> snapshotResults = results.stream()
                .filter(r -> r.getType() == StreamResultType.FACTS_SNAPSHOT)
                .toList();
        assertThat(snapshotResults).hasSize(1);
        assertThat(snapshotResults.get(0).getFactsList()).isNotEmpty();
        assertThat(snapshotResults.get(0).getSequenceId()).isEqualTo(3);

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("stream-session").build());
    }

    @Test
    void shouldStreamRealtimeRuleEvents() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("realtime-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);

        StreamObserver<StreamResult> responseObserver = new StreamObserver<StreamResult>() {
            @Override public void onNext(StreamResult result) { results.add(result); }
            @Override public void onError(Throwable t) { completedLatch.countDown(); }
            @Override public void onCompleted() { completedLatch.countDown(); }
        };

        StreamObserver<StreamEvent> requestObserver = ClientCalls.asyncBidiStreamingCall(
                channel.newCall(DroolsRuleServiceGrpc.STREAMING_SESSION_METHOD,
                        io.grpc.CallOptions.DEFAULT),
                responseObserver);

        requestObserver.onNext(StreamEvent.newBuilder()
                .setSessionId("realtime-session")
                .setType(StreamEventType.INSERT_FACT_EVENT)
                .setFact(Fact.newBuilder()
                        .setType("org.drools.grpc.TestOrder")
                        .setJson("{\"amount\": 300.0, \"discount\": 0.0}")
                        .build())
                .setSequenceId(1)
                .build());

        requestObserver.onNext(StreamEvent.newBuilder()
                .setSessionId("realtime-session")
                .setType(StreamEventType.FIRE_RULES_EVENT)
                .setSequenceId(2)
                .build());

        requestObserver.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        List<StreamResult> ruleMatchFired = results.stream()
                .filter(r -> r.getType() == StreamResultType.RULE_MATCH_FIRED)
                .toList();
        assertThat(ruleMatchFired).isNotEmpty();
        assertThat(ruleMatchFired.get(0).getRuleName()).isEqualTo("Apply Discount");

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("realtime-session").build());
    }

    @Test
    void shouldStreamDeleteFact() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("delete-stream-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);

        StreamObserver<StreamResult> responseObserver = new StreamObserver<StreamResult>() {
            @Override public void onNext(StreamResult result) { results.add(result); }
            @Override public void onError(Throwable t) { completedLatch.countDown(); }
            @Override public void onCompleted() { completedLatch.countDown(); }
        };

        StreamObserver<StreamEvent> requestObserver = ClientCalls.asyncBidiStreamingCall(
                channel.newCall(DroolsRuleServiceGrpc.STREAMING_SESSION_METHOD,
                        io.grpc.CallOptions.DEFAULT),
                responseObserver);

        requestObserver.onNext(StreamEvent.newBuilder()
                .setSessionId("delete-stream-session")
                .setType(StreamEventType.INSERT_FACT_EVENT)
                .setFact(Fact.newBuilder()
                        .setType("org.drools.grpc.TestOrder")
                        .setJson("{\"amount\": 50.0, \"discount\": 0.0}")
                        .build())
                .setSequenceId(1)
                .build());

        requestObserver.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        String factHandleId = results.stream()
                .filter(r -> r.getType() == StreamResultType.FACT_INSERTED)
                .findFirst()
                .orElseThrow()
                .getFactHandleId();

        List<StreamResult> results2 = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch2 = new CountDownLatch(1);

        StreamObserver<StreamResult> responseObserver2 = new StreamObserver<StreamResult>() {
            @Override public void onNext(StreamResult result) { results2.add(result); }
            @Override public void onError(Throwable t) { completedLatch2.countDown(); }
            @Override public void onCompleted() { completedLatch2.countDown(); }
        };

        StreamObserver<StreamEvent> requestObserver2 = ClientCalls.asyncBidiStreamingCall(
                channel.newCall(DroolsRuleServiceGrpc.STREAMING_SESSION_METHOD,
                        io.grpc.CallOptions.DEFAULT),
                responseObserver2);

        requestObserver2.onNext(StreamEvent.newBuilder()
                .setSessionId("delete-stream-session")
                .setType(StreamEventType.DELETE_FACT_EVENT)
                .setFactHandleId(factHandleId)
                .setSequenceId(10)
                .build());

        requestObserver2.onNext(StreamEvent.newBuilder()
                .setSessionId("delete-stream-session")
                .setType(StreamEventType.GET_FACTS_EVENT)
                .setSequenceId(11)
                .build());

        requestObserver2.onCompleted();
        assertThat(completedLatch2.await(5, TimeUnit.SECONDS)).isTrue();

        List<StreamResult> deleteResults = results2.stream()
                .filter(r -> r.getType() == StreamResultType.FACT_DELETED)
                .toList();
        assertThat(deleteResults).hasSize(1);
        assertThat(deleteResults.get(0).getSuccess()).isTrue();
        assertThat(deleteResults.get(0).getSequenceId()).isEqualTo(10);

        List<StreamResult> snapshotResults = results2.stream()
                .filter(r -> r.getType() == StreamResultType.FACTS_SNAPSHOT)
                .toList();
        assertThat(snapshotResults).hasSize(1);
        assertThat(snapshotResults.get(0).getFactsList()).isEmpty();

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("delete-stream-session").build());
    }

    @Test
    void shouldStreamBatchInsert() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("batch-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);
        StreamObserver<StreamEvent> req = openStream(results, completedLatch);

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("batch-session")
                .setType(StreamEventType.BATCH_INSERT_EVENT)
                .addFacts(orderFact(100.0))
                .addFacts(orderFact(200.0))
                .addFacts(orderFact(300.0))
                .setSequenceId(1)
                .build());

        req.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        List<StreamResult> batchResults = results.stream()
                .filter(r -> r.getType() == StreamResultType.BATCH_INSERTED)
                .toList();
        assertThat(batchResults).hasSize(1);
        assertThat(batchResults.get(0).getFactHandleIdsList()).hasSize(3);

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("batch-session").build());
    }

    @Test
    void shouldStreamSessionLifecycle() throws Exception {
        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);
        StreamObserver<StreamEvent> req = openStream(results, completedLatch);

        req.onNext(StreamEvent.newBuilder()
                .setType(StreamEventType.CREATE_SESSION_EVENT)
                .setSessionId("inline-session")
                .setSequenceId(1)
                .build());

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("inline-session")
                .setType(StreamEventType.INSERT_FACT_EVENT)
                .setFact(orderFact(150.0))
                .setSequenceId(2)
                .build());

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("inline-session")
                .setType(StreamEventType.FIRE_RULES_EVENT)
                .setSequenceId(3)
                .build());

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("inline-session")
                .setType(StreamEventType.DISPOSE_SESSION_EVENT)
                .setSequenceId(4)
                .build());

        req.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        assertThat(results.stream().anyMatch(r -> r.getType() == StreamResultType.SESSION_CREATED
                && r.getSessionId().equals("inline-session"))).isTrue();
        assertThat(results.stream().anyMatch(r -> r.getType() == StreamResultType.FACT_INSERTED)).isTrue();
        assertThat(results.stream().anyMatch(r -> r.getType() == StreamResultType.RULES_FIRED)).isTrue();
        assertThat(results.stream().anyMatch(r -> r.getType() == StreamResultType.SESSION_DISPOSED)).isTrue();
    }

    @Test
    void shouldStreamGlobals() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("global-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);
        StreamObserver<StreamEvent> req = openStream(results, completedLatch);

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("global-session")
                .setType(StreamEventType.SET_GLOBAL_EVENT)
                .setGlobalName("auditLog")
                .setGlobalType("java.util.ArrayList")
                .setGlobalJson("[]")
                .setSequenceId(1)
                .build());

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("global-session")
                .setType(StreamEventType.GET_GLOBAL_EVENT)
                .setGlobalName("auditLog")
                .setSequenceId(2)
                .build());

        req.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        List<StreamResult> setResults = results.stream()
                .filter(r -> r.getType() == StreamResultType.GLOBAL_SET)
                .toList();
        assertThat(setResults).hasSize(1);
        assertThat(setResults.get(0).getGlobalName()).isEqualTo("auditLog");

        List<StreamResult> getResults = results.stream()
                .filter(r -> r.getType() == StreamResultType.GLOBAL_VALUE)
                .toList();
        assertThat(getResults).hasSize(1);
        assertThat(getResults.get(0).getGlobalName()).isEqualTo("auditLog");
        assertThat(getResults.get(0).getGlobalJson()).isEqualTo("[]");

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("global-session").build());
    }

    @Test
    void shouldStreamAgendaFocus() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("agenda-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);
        StreamObserver<StreamEvent> req = openStream(results, completedLatch);

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("agenda-session")
                .setType(StreamEventType.INSERT_FACT_EVENT)
                .setFact(orderFact(600.0))
                .setSequenceId(1)
                .build());

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("agenda-session")
                .setType(StreamEventType.SET_AGENDA_FOCUS_EVENT)
                .setAgendaGroup("premium")
                .setSequenceId(2)
                .build());

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("agenda-session")
                .setType(StreamEventType.FIRE_RULES_EVENT)
                .setSequenceId(3)
                .build());

        req.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        assertThat(results.stream().anyMatch(r -> r.getType() == StreamResultType.AGENDA_FOCUS_SET)).isTrue();

        List<StreamResult> fireResults = results.stream()
                .filter(r -> r.getType() == StreamResultType.RULES_FIRED)
                .toList();
        assertThat(fireResults).hasSize(1);
        assertThat(fireResults.get(0).getRulesFiredNamesList()).contains("Premium Discount");

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("agenda-session").build());
    }

    @Test
    void shouldStreamHeartbeat() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("heartbeat-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);
        StreamObserver<StreamEvent> req = openStream(results, completedLatch);

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("heartbeat-session")
                .setType(StreamEventType.HEARTBEAT_EVENT)
                .setSequenceId(1)
                .build());

        req.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        List<StreamResult> acks = results.stream()
                .filter(r -> r.getType() == StreamResultType.HEARTBEAT_ACK)
                .toList();
        assertThat(acks).hasSize(1);
        assertThat(acks.get(0).getSuccess()).isTrue();
        assertThat(acks.get(0).getActiveSessionCount()).isGreaterThanOrEqualTo(1);

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("heartbeat-session").build());
    }

    @Test
    void shouldStreamFactChangeNotifications() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("change-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);
        StreamObserver<StreamEvent> req = openStream(results, completedLatch);

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("change-session")
                .setType(StreamEventType.INSERT_FACT_EVENT)
                .setFact(orderFact(200.0))
                .setSequenceId(1)
                .build());

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("change-session")
                .setType(StreamEventType.FIRE_RULES_EVENT)
                .setSequenceId(2)
                .build());

        req.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        List<StreamResult> updatedByRule = results.stream()
                .filter(r -> r.getType() == StreamResultType.FACT_UPDATED_BY_RULE)
                .toList();
        assertThat(updatedByRule).isNotEmpty();
        assertThat(updatedByRule.get(0).getRuleName()).isEqualTo("Apply Discount");

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("change-session").build());
    }

    @Test
    void shouldStreamCheckpointOnComplete() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("checkpoint-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);
        StreamObserver<StreamEvent> req = openStream(results, completedLatch);

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("checkpoint-session")
                .setType(StreamEventType.INSERT_FACT_EVENT)
                .setFact(orderFact(50.0))
                .setSequenceId(42)
                .build());

        req.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        List<StreamResult> checkpoints = results.stream()
                .filter(r -> r.getType() == StreamResultType.CHECKPOINT)
                .toList();
        assertThat(checkpoints).hasSize(1);
        assertThat(checkpoints.get(0).getLastSequenceId()).isEqualTo(42);

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("checkpoint-session").build());
    }

    @Test
    void shouldStreamFireUntilHaltAndHalt() throws Exception {
        CreateSessionResponse createResp = blockingUnary(
                DroolsRuleServiceGrpc.CREATE_SESSION_METHOD,
                CreateSessionRequest.newBuilder().setSessionId("halt-session").build());
        assertThat(createResp.getSuccess()).isTrue();

        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch ruleFiredLatch = new CountDownLatch(1);
        CountDownLatch completedLatch = new CountDownLatch(1);

        StreamObserver<StreamResult> responseObserver = new StreamObserver<StreamResult>() {
            @Override public void onNext(StreamResult result) {
                results.add(result);
                if (result.getType() == StreamResultType.RULE_MATCH_FIRED) {
                    ruleFiredLatch.countDown();
                }
            }
            @Override public void onError(Throwable t) { completedLatch.countDown(); }
            @Override public void onCompleted() { completedLatch.countDown(); }
        };

        StreamObserver<StreamEvent> req = ClientCalls.asyncBidiStreamingCall(
                channel.newCall(DroolsRuleServiceGrpc.STREAMING_SESSION_METHOD,
                        io.grpc.CallOptions.DEFAULT),
                responseObserver);

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("halt-session")
                .setType(StreamEventType.FIRE_UNTIL_HALT_EVENT)
                .setSequenceId(1)
                .build());

        assertThat(results.stream().anyMatch(
                r -> r.getType() == StreamResultType.FIRE_UNTIL_HALT_STARTED)).isTrue();

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("halt-session")
                .setType(StreamEventType.INSERT_FACT_EVENT)
                .setFact(orderFact(400.0))
                .setSequenceId(2)
                .build());

        assertThat(ruleFiredLatch.await(5, TimeUnit.SECONDS)).isTrue();

        req.onNext(StreamEvent.newBuilder()
                .setSessionId("halt-session")
                .setType(StreamEventType.HALT_EVENT)
                .setSequenceId(3)
                .build());

        req.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        assertThat(results.stream().anyMatch(r -> r.getType() == StreamResultType.HALTED)).isTrue();
        assertThat(results.stream().anyMatch(
                r -> r.getType() == StreamResultType.RULE_MATCH_FIRED
                        && "Apply Discount".equals(r.getRuleName()))).isTrue();

        blockingUnary(DroolsRuleServiceGrpc.DISPOSE_SESSION_METHOD,
                DisposeSessionRequest.newBuilder().setSessionId("halt-session").build());
    }

    @Test
    void shouldReturnStreamErrorForInvalidSession() throws Exception {
        List<StreamResult> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch completedLatch = new CountDownLatch(1);

        StreamObserver<StreamResult> responseObserver = new StreamObserver<StreamResult>() {
            @Override public void onNext(StreamResult result) { results.add(result); }
            @Override public void onError(Throwable t) { completedLatch.countDown(); }
            @Override public void onCompleted() { completedLatch.countDown(); }
        };

        StreamObserver<StreamEvent> requestObserver = ClientCalls.asyncBidiStreamingCall(
                channel.newCall(DroolsRuleServiceGrpc.STREAMING_SESSION_METHOD,
                        io.grpc.CallOptions.DEFAULT),
                responseObserver);

        requestObserver.onNext(StreamEvent.newBuilder()
                .setSessionId("nonexistent-stream")
                .setType(StreamEventType.INSERT_FACT_EVENT)
                .setFact(Fact.newBuilder()
                        .setType("org.drools.grpc.TestOrder")
                        .setJson("{\"amount\": 100.0, \"discount\": 0.0}")
                        .build())
                .setSequenceId(1)
                .build());

        requestObserver.onCompleted();
        assertThat(completedLatch.await(5, TimeUnit.SECONDS)).isTrue();

        List<StreamResult> errors = results.stream()
                .filter(r -> r.getType() == StreamResultType.SESSION_ERROR)
                .toList();
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getSuccess()).isFalse();
        assertThat(errors.get(0).getErrorMessage()).contains("nonexistent-stream");
    }

    // --- Helpers ---

    private StreamObserver<StreamEvent> openStream(
            List<StreamResult> results, CountDownLatch completedLatch) {
        StreamObserver<StreamResult> responseObserver = new StreamObserver<StreamResult>() {
            @Override public void onNext(StreamResult result) { results.add(result); }
            @Override public void onError(Throwable t) { completedLatch.countDown(); }
            @Override public void onCompleted() { completedLatch.countDown(); }
        };
        return ClientCalls.asyncBidiStreamingCall(
                channel.newCall(DroolsRuleServiceGrpc.STREAMING_SESSION_METHOD,
                        io.grpc.CallOptions.DEFAULT),
                responseObserver);
    }

    private static Fact orderFact(double amount) {
        return Fact.newBuilder()
                .setType("org.drools.grpc.TestOrder")
                .setJson("{\"amount\": " + amount + ", \"discount\": 0.0}")
                .build();
    }

    private <ReqT, RespT> RespT blockingUnary(
            io.grpc.MethodDescriptor<ReqT, RespT> method, ReqT request) {
        return ClientCalls.blockingUnaryCall(channel, method,
                io.grpc.CallOptions.DEFAULT, request);
    }

    private static KieBase createKieBase() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/rules/test.drl",
                "package org.drools.grpc;\n" +
                "\n" +
                "import org.drools.grpc.TestOrder;\n" +
                "\n" +
                "global java.util.List auditLog;\n" +
                "\n" +
                "rule \"Apply Discount\"\n" +
                "when\n" +
                "    $order : TestOrder(amount > 100, discount == 0.0)\n" +
                "then\n" +
                "    $order.setDiscount($order.getAmount() * 0.1);\n" +
                "    update($order);\n" +
                "end\n" +
                "\n" +
                "rule \"Premium Discount\" agenda-group \"premium\"\n" +
                "when\n" +
                "    $order : TestOrder(amount > 500, discount == 0.0)\n" +
                "then\n" +
                "    $order.setDiscount($order.getAmount() * 0.2);\n" +
                "    update($order);\n" +
                "end\n" +
                "\n" +
                "query \"discountedOrders\"\n" +
                "    $order : TestOrder(discount > 0)\n" +
                "end\n");

        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Rule compilation errors: " + kieBuilder.getResults().getMessages());
        }
        KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        return kieContainer.getKieBase();
    }
}
