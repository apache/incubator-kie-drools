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

import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import org.drools.grpc.proto.CreateSessionRequest;
import org.drools.grpc.proto.CreateSessionResponse;
import org.drools.grpc.proto.DeleteFactRequest;
import org.drools.grpc.proto.DeleteFactResponse;
import org.drools.grpc.proto.DisposeSessionRequest;
import org.drools.grpc.proto.DisposeSessionResponse;
import org.drools.grpc.proto.ExecuteRequest;
import org.drools.grpc.proto.ExecuteResponse;
import org.drools.grpc.proto.FireAllRulesRequest;
import org.drools.grpc.proto.FireAllRulesResponse;
import org.drools.grpc.proto.GetFactsRequest;
import org.drools.grpc.proto.GetFactsResponse;
import org.drools.grpc.proto.InsertFactRequest;
import org.drools.grpc.proto.InsertFactResponse;
import org.drools.grpc.proto.StreamEvent;
import org.drools.grpc.proto.StreamResult;
import org.drools.grpc.proto.UpdateFactRequest;
import org.drools.grpc.proto.UpdateFactResponse;

/**
 * Hand-written gRPC service definition for {@code DroolsRuleService}.
 * Equivalent to what protoc-gen-grpc-java would generate from the proto file.
 */
public final class DroolsRuleServiceGrpc {

    public static final String SERVICE_NAME = "org.drools.grpc.DroolsRuleService";

    public static final MethodDescriptor<ExecuteRequest, ExecuteResponse> EXECUTE_METHOD =
            MethodDescriptor.<ExecuteRequest, ExecuteResponse>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "Execute"))
                    .setRequestMarshaller(ProtoUtils.marshaller(ExecuteRequest.getDefaultInstance()))
                    .setResponseMarshaller(ProtoUtils.marshaller(ExecuteResponse.getDefaultInstance()))
                    .build();

    public static final MethodDescriptor<CreateSessionRequest, CreateSessionResponse> CREATE_SESSION_METHOD =
            MethodDescriptor.<CreateSessionRequest, CreateSessionResponse>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "CreateSession"))
                    .setRequestMarshaller(ProtoUtils.marshaller(CreateSessionRequest.getDefaultInstance()))
                    .setResponseMarshaller(ProtoUtils.marshaller(CreateSessionResponse.getDefaultInstance()))
                    .build();

    public static final MethodDescriptor<DisposeSessionRequest, DisposeSessionResponse> DISPOSE_SESSION_METHOD =
            MethodDescriptor.<DisposeSessionRequest, DisposeSessionResponse>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "DisposeSession"))
                    .setRequestMarshaller(ProtoUtils.marshaller(DisposeSessionRequest.getDefaultInstance()))
                    .setResponseMarshaller(ProtoUtils.marshaller(DisposeSessionResponse.getDefaultInstance()))
                    .build();

    public static final MethodDescriptor<InsertFactRequest, InsertFactResponse> INSERT_FACT_METHOD =
            MethodDescriptor.<InsertFactRequest, InsertFactResponse>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "InsertFact"))
                    .setRequestMarshaller(ProtoUtils.marshaller(InsertFactRequest.getDefaultInstance()))
                    .setResponseMarshaller(ProtoUtils.marshaller(InsertFactResponse.getDefaultInstance()))
                    .build();

    public static final MethodDescriptor<DeleteFactRequest, DeleteFactResponse> DELETE_FACT_METHOD =
            MethodDescriptor.<DeleteFactRequest, DeleteFactResponse>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "DeleteFact"))
                    .setRequestMarshaller(ProtoUtils.marshaller(DeleteFactRequest.getDefaultInstance()))
                    .setResponseMarshaller(ProtoUtils.marshaller(DeleteFactResponse.getDefaultInstance()))
                    .build();

    public static final MethodDescriptor<UpdateFactRequest, UpdateFactResponse> UPDATE_FACT_METHOD =
            MethodDescriptor.<UpdateFactRequest, UpdateFactResponse>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "UpdateFact"))
                    .setRequestMarshaller(ProtoUtils.marshaller(UpdateFactRequest.getDefaultInstance()))
                    .setResponseMarshaller(ProtoUtils.marshaller(UpdateFactResponse.getDefaultInstance()))
                    .build();

    public static final MethodDescriptor<FireAllRulesRequest, FireAllRulesResponse> FIRE_ALL_RULES_METHOD =
            MethodDescriptor.<FireAllRulesRequest, FireAllRulesResponse>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "FireAllRules"))
                    .setRequestMarshaller(ProtoUtils.marshaller(FireAllRulesRequest.getDefaultInstance()))
                    .setResponseMarshaller(ProtoUtils.marshaller(FireAllRulesResponse.getDefaultInstance()))
                    .build();

    public static final MethodDescriptor<GetFactsRequest, GetFactsResponse> GET_FACTS_METHOD =
            MethodDescriptor.<GetFactsRequest, GetFactsResponse>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "GetFacts"))
                    .setRequestMarshaller(ProtoUtils.marshaller(GetFactsRequest.getDefaultInstance()))
                    .setResponseMarshaller(ProtoUtils.marshaller(GetFactsResponse.getDefaultInstance()))
                    .build();

    public static final MethodDescriptor<StreamEvent, StreamResult> STREAMING_SESSION_METHOD =
            MethodDescriptor.<StreamEvent, StreamResult>newBuilder()
                    .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "StreamingSession"))
                    .setRequestMarshaller(ProtoUtils.marshaller(StreamEvent.getDefaultInstance()))
                    .setResponseMarshaller(ProtoUtils.marshaller(StreamResult.getDefaultInstance()))
                    .build();

    private static volatile ServiceDescriptor serviceDescriptor;

    public static ServiceDescriptor getServiceDescriptor() {
        ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (DroolsRuleServiceGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = ServiceDescriptor.newBuilder(SERVICE_NAME)
                            .addMethod(EXECUTE_METHOD)
                            .addMethod(CREATE_SESSION_METHOD)
                            .addMethod(DISPOSE_SESSION_METHOD)
                            .addMethod(INSERT_FACT_METHOD)
                            .addMethod(DELETE_FACT_METHOD)
                            .addMethod(UPDATE_FACT_METHOD)
                            .addMethod(FIRE_ALL_RULES_METHOD)
                            .addMethod(GET_FACTS_METHOD)
                            .addMethod(STREAMING_SESSION_METHOD)
                            .build();
                }
            }
        }
        return result;
    }

    /**
     * Base class for the DroolsRuleService gRPC service implementation.
     * Override the methods you want to handle; unimplemented methods return UNIMPLEMENTED status.
     */
    public static abstract class DroolsRuleServiceImplBase implements BindableService {

        public void execute(ExecuteRequest request, StreamObserver<ExecuteResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(EXECUTE_METHOD, responseObserver);
        }

        public void createSession(CreateSessionRequest request, StreamObserver<CreateSessionResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(CREATE_SESSION_METHOD, responseObserver);
        }

        public void disposeSession(DisposeSessionRequest request, StreamObserver<DisposeSessionResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(DISPOSE_SESSION_METHOD, responseObserver);
        }

        public void insertFact(InsertFactRequest request, StreamObserver<InsertFactResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(INSERT_FACT_METHOD, responseObserver);
        }

        public void deleteFact(DeleteFactRequest request, StreamObserver<DeleteFactResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(DELETE_FACT_METHOD, responseObserver);
        }

        public void updateFact(UpdateFactRequest request, StreamObserver<UpdateFactResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(UPDATE_FACT_METHOD, responseObserver);
        }

        public void fireAllRules(FireAllRulesRequest request, StreamObserver<FireAllRulesResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(FIRE_ALL_RULES_METHOD, responseObserver);
        }

        public void getFacts(GetFactsRequest request, StreamObserver<GetFactsResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(GET_FACTS_METHOD, responseObserver);
        }

        public StreamObserver<StreamEvent> streamingSession(StreamObserver<StreamResult> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(STREAMING_SESSION_METHOD, responseObserver);
            return new StreamObserver<StreamEvent>() {
                @Override public void onNext(StreamEvent value) { }
                @Override public void onError(Throwable t) { }
                @Override public void onCompleted() { }
            };
        }

        @Override
        public ServerServiceDefinition bindService() {
            return ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(EXECUTE_METHOD, ServerCalls.asyncUnaryCall(this::execute))
                    .addMethod(CREATE_SESSION_METHOD, ServerCalls.asyncUnaryCall(this::createSession))
                    .addMethod(DISPOSE_SESSION_METHOD, ServerCalls.asyncUnaryCall(this::disposeSession))
                    .addMethod(INSERT_FACT_METHOD, ServerCalls.asyncUnaryCall(this::insertFact))
                    .addMethod(DELETE_FACT_METHOD, ServerCalls.asyncUnaryCall(this::deleteFact))
                    .addMethod(UPDATE_FACT_METHOD, ServerCalls.asyncUnaryCall(this::updateFact))
                    .addMethod(FIRE_ALL_RULES_METHOD, ServerCalls.asyncUnaryCall(this::fireAllRules))
                    .addMethod(GET_FACTS_METHOD, ServerCalls.asyncUnaryCall(this::getFacts))
                    .addMethod(STREAMING_SESSION_METHOD, ServerCalls.asyncBidiStreamingCall(this::streamingSession))
                    .build();
        }
    }

    private DroolsRuleServiceGrpc() {
    }
}
