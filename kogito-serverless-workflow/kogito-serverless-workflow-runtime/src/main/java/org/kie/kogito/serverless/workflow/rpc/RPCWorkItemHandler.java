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
package org.kie.kogito.serverless.workflow.rpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;
import org.kie.kogito.serverless.workflow.utils.ConfigResolverHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.MethodDescriptorProto;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor.MethodType;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;

import static org.kogito.workitem.rest.RestWorkItemHandler.CONTENT_DATA;

public abstract class RPCWorkItemHandler extends WorkflowWorkItemHandler {

    public static final String NAME = "gRPC";
    public static final String SERVICE_PROP = "serviceName";
    public static final String FILE_PROP = "fileName";
    public static final String METHOD_PROP = "methodName";
    public static final String GRPC_WAIT_TIMEOUT_PROPERTY = "kogito.grpc.wait.timeout";

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters) {
        Map<String, Object> metadata = workItem.getNodeInstance().getNode().getMetaData();
        String file = (String) metadata.get(FILE_PROP);
        String service = (String) metadata.get(SERVICE_PROP);
        String method = (String) metadata.get(METHOD_PROP);
        return doCall(FileDescriptorHolder.get().descriptor().orElseThrow(() -> new IllegalStateException("Descriptor " + FileDescriptorHolder.DESCRIPTOR_PATH + " is not present")),
                parameters, getChannel(file, service), file, service, method);
    }

    protected abstract Channel getChannel(String file, String service);

    private static JsonNode doCall(FileDescriptorSet fdSet, Map<String, Object> parameters, Channel channel, String fileName, String serviceName, String methodName) {
        try {
            RPCConverter converter = RPCConverterFactory.get();
            FileDescriptor descriptor = FileDescriptor.buildFrom(fdSet.getFileList().stream().filter(f -> f.getName().equals(fileName))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Cannot find file name " + fileName)), new FileDescriptor[0], true);
            ServiceDescriptor serviceDesc = Objects.requireNonNull(descriptor.findServiceByName(serviceName), "Cannot find service name " + serviceName);
            MethodDescriptor methodDesc = Objects.requireNonNull(serviceDesc.findMethodByName(methodName), "Cannot find method name " + methodName);
            MethodType methodType = getMethodType(methodDesc);
            ClientCall<Message, Message> call = channel.newCall(io.grpc.MethodDescriptor.<Message, Message> newBuilder()
                    .setType(methodType)
                    .setFullMethodName(io.grpc.MethodDescriptor.generateFullMethodName(
                            serviceDesc.getFullName(), methodDesc.getName()))
                    .setRequestMarshaller(ProtoUtils.marshaller(
                            DynamicMessage.newBuilder(methodDesc.getInputType()).buildPartial()))
                    .setResponseMarshaller(ProtoUtils.marshaller(
                            DynamicMessage.newBuilder(methodDesc.getOutputType()).buildPartial()))
                    .build(), CallOptions.DEFAULT.withWaitForReady());

            if (methodType == MethodType.CLIENT_STREAMING) {
                return asyncStreamingCall(parameters, methodDesc, responseObserver -> ClientCalls.asyncClientStreamingCall(call, responseObserver),
                        nodes -> nodes.isEmpty() ? JsonObjectUtils.fromValue(null) : nodes.get(0));
            } else if (methodType == MethodType.BIDI_STREAMING) {
                return asyncStreamingCall(parameters, methodDesc, responseObserver -> ClientCalls.asyncBidiStreamingCall(call, responseObserver), JsonObjectUtils::fromValue);
            } else if (methodType == MethodType.SERVER_STREAMING) {
                Message message = converter.buildMessage(parameters, DynamicMessage.newBuilder(methodDesc.getInputType())).build();
                Iterator<Message> responses = ClientCalls.blockingServerStreamingCall(call, message);
                List<JsonNode> nodes = new ArrayList<>();
                responses.forEachRemaining(m -> nodes.add(converter.getJsonNode(m)));
                return JsonObjectUtils.fromValue(nodes);
            } else {
                Message message = converter.buildMessage(parameters, DynamicMessage.newBuilder(methodDesc.getInputType())).build();
                return converter.getJsonNode(ClientCalls.blockingUnaryCall(call, message));
            }

        } catch (DescriptorValidationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static JsonNode asyncStreamingCall(Map<String, Object> parameters, MethodDescriptor methodDesc, UnaryOperator<StreamObserver<Message>> streamObserverFunction,
            Function<List<JsonNode>, JsonNode> nodesFunction) {
        List<Object> messageParams = (List<Object>) parameters.get(CONTENT_DATA);
        RPCConverter converter = RPCConverterFactory.get();
        WaitingStreamObserver responseObserver = new WaitingStreamObserver();
        StreamObserver<Message> requestObserver = streamObserverFunction.apply(responseObserver);

        for (Object messageParam : messageParams) {
            try {
                Message message = converter.buildMessage(messageParam, DynamicMessage.newBuilder(methodDesc.getInputType())).build();
                requestObserver.onNext(message);
            } catch (Exception e) {
                requestObserver.onError(e);
                throw e;
            }
            responseObserver.checkForServerStreamErrors();
        }
        requestObserver.onCompleted();

        List<Message> responses = responseObserver.get();
        List<JsonNode> nodes = responses.stream().map(converter::getJsonNode).collect(Collectors.toList());
        return nodesFunction.apply(nodes);
    }

    private static MethodType getMethodType(MethodDescriptor methodDesc) {
        MethodDescriptorProto methodDescProto = methodDesc.toProto();
        if (methodDescProto.getClientStreaming()) {
            if (methodDescProto.getServerStreaming()) {
                return MethodType.BIDI_STREAMING;
            }
            return MethodType.CLIENT_STREAMING;
        } else if (methodDescProto.getServerStreaming()) {
            return MethodType.SERVER_STREAMING;
        } else {
            return MethodType.UNARY;
        }
    }

    private static class WaitingStreamObserver implements StreamObserver<Message> {
        List<Message> responses = new ArrayList<>();
        CompletableFuture<List<Message>> responsesFuture = new CompletableFuture<>();

        @Override
        public void onNext(Message messageReply) {
            responses.add(messageReply);
        }

        @Override
        public void onError(Throwable throwable) {
            responsesFuture.completeExceptionally(throwable);
        }

        @Override
        public void onCompleted() {
            responsesFuture.complete(responses);
        }

        public List<Message> get() {
            Long timeout = ConfigResolverHolder.getConfigResolver().getConfigProperty(GRPC_WAIT_TIMEOUT_PROPERTY, Long.class).orElse(20L);
            try {
                return responsesFuture.get(timeout, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            } catch (TimeoutException e) {
                throw new IllegalStateException(String.format("gRPC call timed out after %d seconds", timeout), e);
            } catch (ExecutionException e) {
                throw new IllegalStateException(getServerStreamErrorMessage(e.getCause()), e.getCause());
            }
        }

        public void checkForServerStreamErrors() {
            if (responsesFuture.isCompletedExceptionally()) {
                try {
                    responsesFuture.join();
                } catch (CompletionException e) {
                    throw new IllegalStateException(getServerStreamErrorMessage(e.getCause()), e.getCause());
                }
            }
        }

        private String getServerStreamErrorMessage(Throwable throwable) {
            return String.format("Received an error through gRPC server stream with status: %s", Status.fromThrowable(throwable));
        }
    }
}
