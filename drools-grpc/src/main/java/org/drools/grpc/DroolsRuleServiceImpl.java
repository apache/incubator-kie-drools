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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.grpc.stub.StreamObserver;
import org.drools.grpc.proto.CreateSessionRequest;
import org.drools.grpc.proto.CreateSessionResponse;
import org.drools.grpc.proto.DeleteFactRequest;
import org.drools.grpc.proto.DeleteFactResponse;
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
import org.drools.grpc.proto.StreamEvent;
import org.drools.grpc.proto.StreamResult;
import org.drools.grpc.proto.StreamResultType;
import org.drools.grpc.proto.UpdateFactRequest;
import org.drools.grpc.proto.UpdateFactResponse;
import org.drools.grpc.session.SessionManager;
import org.drools.grpc.util.FactConverter;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gRPC service implementation that delegates rule evaluation to a Drools {@link KieBase}.
 * Uses {@link org.kie.api.runtime.KieSessionsPool} via {@link SessionManager} for
 * efficient session reuse. Sessions are borrowed from the pool for stateless execution
 * and returned automatically on dispose.
 */
public class DroolsRuleServiceImpl extends DroolsRuleServiceGrpc.DroolsRuleServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(DroolsRuleServiceImpl.class);

    private final SessionManager sessionManager;
    private final FactConverter factConverter;

    public DroolsRuleServiceImpl(KieBase kieBase) {
        this(new SessionManager(kieBase), new FactConverter());
    }

    public DroolsRuleServiceImpl(SessionManager sessionManager, FactConverter factConverter) {
        this.sessionManager = sessionManager;
        this.factConverter = factConverter;
    }

    // --- Stateless execution ---

    @Override
    public void execute(ExecuteRequest request, StreamObserver<ExecuteResponse> responseObserver) {
        try {
            KieSession session = sessionManager.borrowSession(request.getKieBaseName());
            try {
                List<String> firedRuleNames = new ArrayList<>();
                session.addEventListener(ruleNameCollector(firedRuleNames));

                for (Fact fact : request.getFactsList()) {
                    Object obj = factConverter.toObject(fact);
                    session.insert(obj);
                }

                int maxRules = request.getMaxRules();
                int rulesFired = (maxRules > 0) ? session.fireAllRules(maxRules) : session.fireAllRules();

                List<Fact> resultFacts = new ArrayList<>();
                for (Object obj : session.getObjects()) {
                    resultFacts.add(factConverter.toFact(obj));
                }

                responseObserver.onNext(ExecuteResponse.newBuilder()
                        .setSuccess(true)
                        .setRulesFired(rulesFired)
                        .addAllResultFacts(resultFacts)
                        .addAllRulesFiredNames(firedRuleNames)
                        .build());
            } finally {
                session.dispose();
            }
        } catch (Exception e) {
            log.error("Execute failed", e);
            responseObserver.onNext(ExecuteResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    // --- Session lifecycle ---

    @Override
    public void createSession(CreateSessionRequest request, StreamObserver<CreateSessionResponse> responseObserver) {
        try {
            String sessionId = sessionManager.createSession(request.getSessionId(), request.getKieBaseName());
            log.info("Session created: {}", sessionId);
            responseObserver.onNext(CreateSessionResponse.newBuilder()
                    .setSuccess(true)
                    .setSessionId(sessionId)
                    .build());
        } catch (Exception e) {
            log.error("CreateSession failed", e);
            responseObserver.onNext(CreateSessionResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void disposeSession(DisposeSessionRequest request, StreamObserver<DisposeSessionResponse> responseObserver) {
        try {
            boolean disposed = sessionManager.disposeSession(request.getSessionId());
            if (disposed) {
                log.info("Session disposed: {}", request.getSessionId());
            }
            responseObserver.onNext(DisposeSessionResponse.newBuilder()
                    .setSuccess(disposed)
                    .setErrorMessage(disposed ? "" : "Session not found: " + request.getSessionId())
                    .build());
        } catch (Exception e) {
            log.error("DisposeSession failed", e);
            responseObserver.onNext(DisposeSessionResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    // --- Fact operations ---

    @Override
    public void insertFact(InsertFactRequest request, StreamObserver<InsertFactResponse> responseObserver) {
        try {
            KieSession session = sessionManager.getSession(request.getSessionId());
            Object obj = factConverter.toObject(request.getFact());
            FactHandle handle = session.insert(obj);
            String handleId = handle.toExternalForm();
            sessionManager.trackFactHandle(request.getSessionId(), handleId, handle);

            responseObserver.onNext(InsertFactResponse.newBuilder()
                    .setSuccess(true)
                    .setFactHandleId(handleId)
                    .build());
        } catch (Exception e) {
            log.error("InsertFact failed", e);
            responseObserver.onNext(InsertFactResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void deleteFact(DeleteFactRequest request, StreamObserver<DeleteFactResponse> responseObserver) {
        try {
            KieSession session = sessionManager.getSession(request.getSessionId());
            FactHandle handle = sessionManager.getFactHandle(request.getSessionId(), request.getFactHandleId());
            session.delete(handle);
            sessionManager.removeFactHandle(request.getSessionId(), request.getFactHandleId());

            responseObserver.onNext(DeleteFactResponse.newBuilder()
                    .setSuccess(true)
                    .build());
        } catch (Exception e) {
            log.error("DeleteFact failed", e);
            responseObserver.onNext(DeleteFactResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void updateFact(UpdateFactRequest request, StreamObserver<UpdateFactResponse> responseObserver) {
        try {
            KieSession session = sessionManager.getSession(request.getSessionId());
            FactHandle handle = sessionManager.getFactHandle(request.getSessionId(), request.getFactHandleId());
            Object obj = factConverter.toObject(request.getFact());
            session.update(handle, obj);

            responseObserver.onNext(UpdateFactResponse.newBuilder()
                    .setSuccess(true)
                    .build());
        } catch (Exception e) {
            log.error("UpdateFact failed", e);
            responseObserver.onNext(UpdateFactResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    // --- Rule firing ---

    @Override
    public void fireAllRules(FireAllRulesRequest request, StreamObserver<FireAllRulesResponse> responseObserver) {
        try {
            KieSession session = sessionManager.getSession(request.getSessionId());
            List<String> firedRuleNames = new ArrayList<>();
            AgendaEventListener collector = ruleNameCollector(firedRuleNames);
            session.addEventListener(collector);

            try {
                int maxRules = request.getMaxRules();
                int rulesFired = (maxRules > 0) ? session.fireAllRules(maxRules) : session.fireAllRules();

                responseObserver.onNext(FireAllRulesResponse.newBuilder()
                        .setSuccess(true)
                        .setRulesFired(rulesFired)
                        .addAllRulesFiredNames(firedRuleNames)
                        .build());
            } finally {
                session.removeEventListener(collector);
            }
        } catch (Exception e) {
            log.error("FireAllRules failed", e);
            responseObserver.onNext(FireAllRulesResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    // --- Query facts ---

    @Override
    public void getFacts(GetFactsRequest request, StreamObserver<GetFactsResponse> responseObserver) {
        try {
            KieSession session = sessionManager.getSession(request.getSessionId());
            Collection<?> objects;
            if (request.getFactType() != null && !request.getFactType().isEmpty()) {
                Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(request.getFactType());
                objects = session.getObjects(obj -> clazz.isInstance(obj));
            } else {
                objects = session.getObjects();
            }

            List<Fact> facts = new ArrayList<>();
            for (Object obj : objects) {
                facts.add(factConverter.toFact(obj));
            }

            responseObserver.onNext(GetFactsResponse.newBuilder()
                    .setSuccess(true)
                    .addAllFacts(facts)
                    .build());
        } catch (Exception e) {
            log.error("GetFacts failed", e);
            responseObserver.onNext(GetFactsResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    // --- Bidirectional streaming ---

    private final Map<StreamObserver<StreamResult>, AgendaEventListener> streamAgendaListeners =
            new ConcurrentHashMap<>();
    private final Map<StreamObserver<StreamResult>, RuleRuntimeEventListener> streamRuntimeListeners =
            new ConcurrentHashMap<>();

    @Override
    public StreamObserver<StreamEvent> streamingSession(StreamObserver<StreamResult> responseObserver) {
        StreamObserver<StreamResult> out = synchronizedObserver(responseObserver);

        return new StreamObserver<StreamEvent>() {

            private volatile String boundSessionId;
            private volatile KieSession boundSession;
            private volatile long lastProcessedSequenceId;

            @Override
            public void onNext(StreamEvent event) {
                try {
                    lastProcessedSequenceId = event.getSequenceId();

                    switch (event.getType()) {
                        case CREATE_SESSION_EVENT:
                            handleCreateSession(event);
                            return;
                        case HEARTBEAT_EVENT:
                            handleHeartbeat(event);
                            return;
                        default:
                            break;
                    }

                    bindSession(event.getSessionId());

                    switch (event.getType()) {
                        case INSERT_FACT_EVENT:
                            handleInsert(event);
                            break;
                        case DELETE_FACT_EVENT:
                            handleDelete(event);
                            break;
                        case UPDATE_FACT_EVENT:
                            handleUpdate(event);
                            break;
                        case FIRE_RULES_EVENT:
                            handleFireRules(event);
                            break;
                        case GET_FACTS_EVENT:
                            handleGetFacts(event);
                            break;
                        case INSERT_CEP_EVENT:
                            handleInsertCepEvent(event);
                            break;
                        case BATCH_INSERT_EVENT:
                            handleBatchInsert(event);
                            break;
                        case SUBSCRIBE_QUERY:
                            handleSubscribeQuery(event);
                            break;
                        case UNSUBSCRIBE_QUERY:
                            handleUnsubscribeQuery(event);
                            break;
                        case SET_GLOBAL_EVENT:
                            handleSetGlobal(event);
                            break;
                        case GET_GLOBAL_EVENT:
                            handleGetGlobal(event);
                            break;
                        case SET_AGENDA_FOCUS_EVENT:
                            handleSetAgendaFocus(event);
                            break;
                        case FIRE_UNTIL_HALT_EVENT:
                            handleFireUntilHalt(event);
                            break;
                        case HALT_EVENT:
                            handleHalt(event);
                            break;
                        case DISPOSE_SESSION_EVENT:
                            handleDisposeSession(event);
                            break;
                        default:
                            sendError(out, event.getSequenceId(),
                                    "Unknown event type: " + event.getType());
                    }
                } catch (Exception e) {
                    log.error("Stream event processing failed", e);
                    sendError(out, event.getSequenceId(), e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                log.warn("Streaming session terminated with error", t);
                cleanupListeners();
            }

            @Override
            public void onCompleted() {
                log.debug("Streaming session completed for session {}", boundSessionId);
                cleanupListeners();
                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.CHECKPOINT)
                        .setSuccess(true)
                        .setLastSequenceId(lastProcessedSequenceId)
                        .build());
                out.onCompleted();
            }

            private void cleanupListeners() {
                removeAgendaListener(out);
                removeRuntimeListener(out);
            }

            private void bindSession(String sessionId) {
                if (boundSession != null && sessionId.equals(boundSessionId)) {
                    return;
                }
                boundSession = sessionManager.getSession(sessionId);
                boundSessionId = sessionId;
                installAgendaListener(out, boundSession);
                installRuntimeListener(out, boundSession);
            }

            // --- Core handlers (existing) ---

            private void handleInsert(StreamEvent event) {
                Object obj = factConverter.toObject(event.getFact());
                FactHandle handle = boundSession.insert(obj);
                String handleId = handle.toExternalForm();
                sessionManager.trackFactHandle(boundSessionId, handleId, handle);

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.FACT_INSERTED)
                        .setSuccess(true)
                        .setFactHandleId(handleId)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            private void handleDelete(StreamEvent event) {
                FactHandle handle = sessionManager.getFactHandle(boundSessionId, event.getFactHandleId());
                boundSession.delete(handle);
                sessionManager.removeFactHandle(boundSessionId, event.getFactHandleId());

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.FACT_DELETED)
                        .setSuccess(true)
                        .setFactHandleId(event.getFactHandleId())
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            private void handleUpdate(StreamEvent event) {
                FactHandle handle = sessionManager.getFactHandle(boundSessionId, event.getFactHandleId());
                Object obj = factConverter.toObject(event.getFact());
                boundSession.update(handle, obj);

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.FACT_UPDATED)
                        .setSuccess(true)
                        .setFactHandleId(event.getFactHandleId())
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            private void handleFireRules(StreamEvent event) {
                if (sessionManager.isFireUntilHaltActive(boundSessionId)) {
                    sendError(out, event.getSequenceId(),
                            "Cannot fire rules explicitly while fire-until-halt is active");
                    return;
                }
                List<String> firedRuleNames = new ArrayList<>();
                AgendaEventListener collector = ruleNameCollector(firedRuleNames);
                boundSession.addEventListener(collector);
                try {
                    int maxRules = event.getMaxRules();
                    int rulesFired = (maxRules > 0)
                            ? boundSession.fireAllRules(maxRules)
                            : boundSession.fireAllRules();

                    out.onNext(StreamResult.newBuilder()
                            .setType(StreamResultType.RULES_FIRED)
                            .setSuccess(true)
                            .setRulesFired(rulesFired)
                            .addAllRulesFiredNames(firedRuleNames)
                            .setSequenceId(event.getSequenceId())
                            .build());
                } finally {
                    boundSession.removeEventListener(collector);
                }
            }

            private void handleGetFacts(StreamEvent event) {
                Collection<?> objects;
                String factType = event.getFactType();
                if (factType != null && !factType.isEmpty()) {
                    try {
                        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(factType);
                        objects = boundSession.getObjects(clazz::isInstance);
                    } catch (ClassNotFoundException e) {
                        sendError(out, event.getSequenceId(), "Unknown fact type: " + factType);
                        return;
                    }
                } else {
                    objects = boundSession.getObjects();
                }

                List<Fact> facts = new ArrayList<>();
                for (Object obj : objects) {
                    facts.add(factConverter.toFact(obj));
                }

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.FACTS_SNAPSHOT)
                        .setSuccess(true)
                        .addAllFacts(facts)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            // --- CEP entry point ---

            private void handleInsertCepEvent(StreamEvent event) {
                String epName = event.getEntryPoint();
                EntryPoint ep = boundSession.getEntryPoint(epName);
                if (ep == null) {
                    sendError(out, event.getSequenceId(), "Entry point not found: " + epName);
                    return;
                }
                Object obj = factConverter.toObject(event.getFact());
                FactHandle handle = ep.insert(obj);
                String handleId = handle.toExternalForm();
                sessionManager.trackFactHandle(boundSessionId, handleId, handle);

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.CEP_EVENT_INSERTED)
                        .setSuccess(true)
                        .setFactHandleId(handleId)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            // --- Batch insert ---

            private void handleBatchInsert(StreamEvent event) {
                List<String> handleIds = new ArrayList<>();
                for (Fact fact : event.getFactsList()) {
                    Object obj = factConverter.toObject(fact);
                    FactHandle handle = boundSession.insert(obj);
                    String handleId = handle.toExternalForm();
                    sessionManager.trackFactHandle(boundSessionId, handleId, handle);
                    handleIds.add(handleId);
                }

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.BATCH_INSERTED)
                        .setSuccess(true)
                        .addAllFactHandleIds(handleIds)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            // --- Live queries ---

            private void handleSubscribeQuery(StreamEvent event) {
                String queryName = event.getQueryName();
                Object[] args = event.getQueryArgsList().stream()
                        .map(json -> {
                            try {
                                return factConverter.getObjectMapper().readValue(json, Object.class);
                            } catch (Exception e) {
                                throw new RuntimeException("Invalid query argument: " + json, e);
                            }
                        })
                        .toArray();

                ViewChangedEventListener listener = new ViewChangedEventListener() {
                    @Override
                    public void rowInserted(org.kie.api.runtime.rule.Row row) {
                        sendQueryChange(queryName, "INSERTED", row);
                    }
                    @Override
                    public void rowDeleted(org.kie.api.runtime.rule.Row row) {
                        sendQueryChange(queryName, "DELETED", row);
                    }
                    @Override
                    public void rowUpdated(org.kie.api.runtime.rule.Row row) {
                        sendQueryChange(queryName, "UPDATED", row);
                    }
                };

                LiveQuery liveQuery = boundSession.openLiveQuery(queryName, args, listener);
                sessionManager.trackLiveQuery(boundSessionId, liveQuery);

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.QUERY_SUBSCRIBED)
                        .setSuccess(true)
                        .setQueryName(queryName)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            private void sendQueryChange(String queryName, String changeType,
                                          org.kie.api.runtime.rule.Row row) {
                StreamResult.Builder builder = StreamResult.newBuilder()
                        .setType(StreamResultType.QUERY_RESULT_CHANGED)
                        .setSuccess(true)
                        .setQueryName(queryName)
                        .setChangeType(changeType);

                try {
                    QueryResults results = boundSession.getQueryResults(queryName);
                    String[] ids = results.getIdentifiers();
                    for (String id : ids) {
                        Object obj = row.get(id);
                        if (obj != null) {
                            builder.addFacts(factConverter.toFact(obj));
                        }
                    }
                } catch (Exception e) {
                    log.debug("Could not extract row data for query {}", queryName, e);
                }
                out.onNext(builder.build());
            }

            private void handleUnsubscribeQuery(StreamEvent event) {
                sessionManager.closeAllLiveQueries(boundSessionId);

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.QUERY_UNSUBSCRIBED)
                        .setSuccess(true)
                        .setQueryName(event.getQueryName())
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            // --- Globals ---

            private void handleSetGlobal(StreamEvent event) {
                String name = event.getGlobalName();
                String type = event.getGlobalType();
                String json = event.getGlobalJson();
                Object value;
                try {
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(type);
                    value = factConverter.getObjectMapper().readValue(json, clazz);
                } catch (Exception e) {
                    sendError(out, event.getSequenceId(),
                            "Failed to deserialize global '" + name + "': " + e.getMessage());
                    return;
                }
                boundSession.setGlobal(name, value);

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.GLOBAL_SET)
                        .setSuccess(true)
                        .setGlobalName(name)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            private void handleGetGlobal(StreamEvent event) {
                String name = event.getGlobalName();
                Object value = boundSession.getGlobal(name);
                String json = "";
                if (value != null) {
                    try {
                        json = factConverter.getObjectMapper().writeValueAsString(value);
                    } catch (Exception e) {
                        sendError(out, event.getSequenceId(),
                                "Failed to serialize global '" + name + "': " + e.getMessage());
                        return;
                    }
                }

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.GLOBAL_VALUE)
                        .setSuccess(true)
                        .setGlobalName(name)
                        .setGlobalJson(json)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            // --- Agenda control ---

            private void handleSetAgendaFocus(StreamEvent event) {
                String group = event.getAgendaGroup();
                boundSession.getAgenda().getAgendaGroup(group).setFocus();

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.AGENDA_FOCUS_SET)
                        .setSuccess(true)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            private void handleFireUntilHalt(StreamEvent event) {
                sessionManager.startFireUntilHalt(boundSessionId);

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.FIRE_UNTIL_HALT_STARTED)
                        .setSuccess(true)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            private void handleHalt(StreamEvent event) {
                sessionManager.halt(boundSessionId);

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.HALTED)
                        .setSuccess(true)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            // --- Heartbeat ---

            private void handleHeartbeat(StreamEvent event) {
                StreamResult.Builder builder = StreamResult.newBuilder()
                        .setType(StreamResultType.HEARTBEAT_ACK)
                        .setSuccess(true)
                        .setActiveSessionCount(sessionManager.getActiveStatefulSessionCount())
                        .setSequenceId(event.getSequenceId());

                if (boundSessionId != null) {
                    builder.setSessionId(boundSessionId)
                            .setFactCount(sessionManager.getFactCount(boundSessionId));
                }

                out.onNext(builder.build());
            }

            // --- Session lifecycle on stream ---

            private void handleCreateSession(StreamEvent event) {
                String requestedId = event.getSessionId();
                String sessionId = sessionManager.createSession(requestedId);
                log.info("Session created on stream: {}", sessionId);

                boundSession = sessionManager.getSession(sessionId);
                boundSessionId = sessionId;
                installAgendaListener(out, boundSession);
                installRuntimeListener(out, boundSession);

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.SESSION_CREATED)
                        .setSuccess(true)
                        .setSessionId(sessionId)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }

            private void handleDisposeSession(StreamEvent event) {
                cleanupListeners();
                boolean disposed = sessionManager.disposeSession(boundSessionId);
                log.info("Session disposed on stream: {}", boundSessionId);
                boundSession = null;
                boundSessionId = null;

                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.SESSION_DISPOSED)
                        .setSuccess(disposed)
                        .setSequenceId(event.getSequenceId())
                        .build());
            }
        };
    }

    // --- Listener installation ---

    private void installAgendaListener(StreamObserver<StreamResult> out, KieSession session) {
        if (streamAgendaListeners.containsKey(out)) {
            return;
        }
        AgendaEventListener listener = new AgendaEventListener() {
            @Override
            public void matchCreated(MatchCreatedEvent event) {
                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.RULE_ACTIVATED)
                        .setSuccess(true)
                        .setRuleName(event.getMatch().getRule().getName())
                        .build());
            }

            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                out.onNext(StreamResult.newBuilder()
                        .setType(StreamResultType.RULE_MATCH_FIRED)
                        .setSuccess(true)
                        .setRuleName(event.getMatch().getRule().getName())
                        .build());
            }

            @Override public void beforeMatchFired(BeforeMatchFiredEvent event) { }
            @Override public void matchCancelled(MatchCancelledEvent event) { }
            @Override public void agendaGroupPopped(AgendaGroupPoppedEvent event) { }
            @Override public void agendaGroupPushed(AgendaGroupPushedEvent event) { }
            @Override public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { }
            @Override public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { }
            @Override public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }
            @Override public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }
        };
        session.addEventListener(listener);
        streamAgendaListeners.put(out, listener);
    }

    private void installRuntimeListener(StreamObserver<StreamResult> out, KieSession session) {
        if (streamRuntimeListeners.containsKey(out)) {
            return;
        }
        RuleRuntimeEventListener listener = new RuleRuntimeEventListener() {
            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                if (event.getRule() != null) {
                    out.onNext(StreamResult.newBuilder()
                            .setType(StreamResultType.FACT_INSERTED_BY_RULE)
                            .setSuccess(true)
                            .setRuleName(event.getRule().getName())
                            .addFacts(factConverter.toFact(event.getObject()))
                            .build());
                }
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                if (event.getRule() != null) {
                    out.onNext(StreamResult.newBuilder()
                            .setType(StreamResultType.FACT_UPDATED_BY_RULE)
                            .setSuccess(true)
                            .setRuleName(event.getRule().getName())
                            .addFacts(factConverter.toFact(event.getObject()))
                            .build());
                }
            }

            @Override
            public void objectDeleted(ObjectDeletedEvent event) {
                if (event.getRule() != null) {
                    out.onNext(StreamResult.newBuilder()
                            .setType(StreamResultType.FACT_RETRACTED_BY_RULE)
                            .setSuccess(true)
                            .setRuleName(event.getRule().getName())
                            .addFacts(factConverter.toFact(event.getOldObject()))
                            .build());
                }
            }
        };
        session.addEventListener(listener);
        streamRuntimeListeners.put(out, listener);
    }

    private void removeAgendaListener(StreamObserver<StreamResult> out) {
        streamAgendaListeners.remove(out);
    }

    private void removeRuntimeListener(StreamObserver<StreamResult> out) {
        streamRuntimeListeners.remove(out);
    }

    private static void sendError(StreamObserver<StreamResult> out, long sequenceId, String message) {
        out.onNext(StreamResult.newBuilder()
                .setType(StreamResultType.SESSION_ERROR)
                .setSuccess(false)
                .setErrorMessage(message)
                .setSequenceId(sequenceId)
                .build());
    }

    private static <T> StreamObserver<T> synchronizedObserver(StreamObserver<T> delegate) {
        return new StreamObserver<T>() {
            @Override public synchronized void onNext(T value) { delegate.onNext(value); }
            @Override public synchronized void onError(Throwable t) { delegate.onError(t); }
            @Override public synchronized void onCompleted() { delegate.onCompleted(); }
        };
    }

    // --- Helpers ---

    private static AgendaEventListener ruleNameCollector(List<String> names) {
        return new AgendaEventListener() {
            @Override public void afterMatchFired(AfterMatchFiredEvent event) {
                names.add(event.getMatch().getRule().getName());
            }
            @Override public void matchCreated(MatchCreatedEvent event) { }
            @Override public void matchCancelled(MatchCancelledEvent event) { }
            @Override public void beforeMatchFired(BeforeMatchFiredEvent event) { }
            @Override public void agendaGroupPopped(AgendaGroupPoppedEvent event) { }
            @Override public void agendaGroupPushed(AgendaGroupPushedEvent event) { }
            @Override public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { }
            @Override public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { }
            @Override public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }
            @Override public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }
        };
    }
}
