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
package org.kie.kogito.serverless.workflow.rpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
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

public abstract class RPCWorkItemHandler extends WorkflowWorkItemHandler {

    public static final String SERVICE_PROP = "serviceName";
    public static final String FILE_PROP = "fileName";
    public static final String METHOD_PROP = "methodName";

    public static final String GRPC_ENUM_DEFAULT_PROPERTY = "kogito.grpc.enum.includeDefault";
    public static final String GRPC_STREAM_TIMEOUT_PROPERTY = "kogito.grpc.stream.timeout";
    public static final boolean GRPC_ENUM_DEFAULT_VALUE = false;
    public static final int GRPC_STREAM_TIMEOUT_VALUE = 20;

    private final Collection<RPCDecorator> decorators = new ArrayList<>();
    private final int streamTimeout;

    private Map<String, FileDescriptor> fileDescriptors = new ConcurrentHashMap<>();

    public RPCWorkItemHandler() {
        this(GRPC_ENUM_DEFAULT_VALUE, GRPC_STREAM_TIMEOUT_VALUE);
    }

    public RPCWorkItemHandler(boolean enumDefault, int streamTimeout) {
        this.streamTimeout = streamTimeout;
        if (enumDefault) {
            decorators.add(new DefaultEnumRpcDecorator());
        }
    }

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

    private JsonNode doCall(FileDescriptorSet fdSet, Map<String, Object> parameters, Channel channel, String fileName, String serviceName, String methodName) {
        FileDescriptor descriptor = buildFileDescriptor(fdSet, fileName);
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
                    nodes -> nodes.isEmpty() ? NullNode.instance : nodes.get(0));
        } else if (methodType == MethodType.BIDI_STREAMING) {
            return asyncStreamingCall(parameters, methodDesc, responseObserver -> ClientCalls.asyncBidiStreamingCall(call, responseObserver), JsonObjectUtils::fromValue);
        } else if (methodType == MethodType.SERVER_STREAMING) {
            List<JsonNode> nodes = new ArrayList<>();
            ClientCalls.blockingServerStreamingCall(call, RPCConverterFactory.get().buildMessage(parameters, DynamicMessage.newBuilder(methodDesc.getInputType())).build())
                    .forEachRemaining(m -> nodes.add(convert(m, methodDesc)));
            return JsonObjectUtils.fromValue(nodes);
        } else {
            return convert(ClientCalls.blockingUnaryCall(call, RPCConverterFactory.get().buildMessage(parameters, DynamicMessage.newBuilder(methodDesc.getInputType())).build()), methodDesc);
        }
    }

    private FileDescriptor buildFileDescriptor(FileDescriptorSet fdSet, String fileName) {
        return fileDescriptors.computeIfAbsent(fileName, name -> {
            FileDescriptorProto fdProto =
                    fdSet.getFileList().stream().filter(f -> f.getName().equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("Cannot find file name " + fileName));
            try {
                return FileDescriptor.buildFrom(fdProto, fdProto.getDependencyList().stream().map(fdName -> buildFileDescriptor(fdSet, fdName)).toArray(FileDescriptor[]::new));
            } catch (DescriptorValidationException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private JsonNode convert(Message m, MethodDescriptor descriptor) {
        JsonNode node = RPCConverterFactory.get().getJsonNode(m);
        for (RPCDecorator decorator : decorators) {
            node = decorator.decorate(node, descriptor.getOutputType());
        }
        return node;
    }

    private JsonNode asyncStreamingCall(Map<String, Object> parameters, MethodDescriptor methodDesc, UnaryOperator<StreamObserver<Message>> streamObserverFunction,
            Function<List<JsonNode>, JsonNode> nodesFunction) {
        WaitingStreamObserver responseObserver = new WaitingStreamObserver(streamTimeout);
        StreamObserver<Message> requestObserver = streamObserverFunction.apply(responseObserver);

        for (Object messageParam : Objects.requireNonNull((List<Object>) parameters.get(SWFConstants.CONTENT_DATA), "Missing streaming call parameter")) {
            try {
                Message message = RPCConverterFactory.get().buildMessage(messageParam, DynamicMessage.newBuilder(methodDesc.getInputType())).build();
                requestObserver.onNext(message);
            } catch (Exception e) {
                requestObserver.onError(e);
                throw e;
            }
            responseObserver.checkForServerStreamErrors();
        }
        requestObserver.onCompleted();

        return nodesFunction.apply(responseObserver.get().stream().map(m -> convert(m, methodDesc)).collect(Collectors.toList()));
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
        private final int timeout;

        public WaitingStreamObserver(int timeout) {
            this.timeout = timeout;
        }

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
