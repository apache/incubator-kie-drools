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

import java.util.Map;
import java.util.Objects;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

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
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;

public abstract class RPCWorkItemHandler extends WorkflowWorkItemHandler {

    public static final String NAME = "gRPC";
    public static final String SERVICE_PROP = "serviceName";
    public static final String FILE_PROP = "fileName";
    public static final String METHOD_PROP = "methodName";

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
            Message message = converter.buildMessage(parameters, DynamicMessage.newBuilder(methodDesc.getInputType())).build();
            ClientCall<Message, Message> call = channel.newCall(io.grpc.MethodDescriptor.<Message, Message> newBuilder()
                    .setType(getMethodType(methodDesc))
                    .setFullMethodName(io.grpc.MethodDescriptor.generateFullMethodName(
                            serviceDesc.getFullName(), methodDesc.getName()))
                    .setRequestMarshaller(ProtoUtils.marshaller(
                            DynamicMessage.newBuilder(methodDesc.getInputType()).buildPartial()))
                    .setResponseMarshaller(ProtoUtils.marshaller(
                            DynamicMessage.newBuilder(methodDesc.getOutputType()).buildPartial()))
                    .build(), CallOptions.DEFAULT.withWaitForReady());
            return converter.getJsonNode(ClientCalls.blockingUnaryCall(call, message));

        } catch (DescriptorValidationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static MethodType getMethodType(MethodDescriptor methodDesc) {
        MethodDescriptorProto methodDescProto = methodDesc.toProto();
        if (methodDescProto.getClientStreaming()) {
            return MethodType.CLIENT_STREAMING;
        } else if (methodDescProto.getServerStreaming()) {
            return MethodType.SERVER_STREAMING;
        } else {
            return MethodType.UNKNOWN;
        }
    }
}
