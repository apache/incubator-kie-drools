/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.executor.greeting;

import java.util.stream.Collectors;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class GreeterService extends GreeterGrpc.GreeterImplBase {

    public static Server buildServer(int port) {
        return ServerBuilder.forPort(port).addService(new GreeterService()).build();
    }

    @Override
    public void sayHello(HelloRequest request,
            StreamObserver<HelloReply> responseObserver) {
        responseObserver.onNext(buildReply(request));
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloArray(HelloArrayRequest requests,
            StreamObserver<HelloArrayReply> responseObserver) {
        responseObserver.onNext(HelloArrayReply.newBuilder().addAllReplies(requests.getRequestsList().stream().map(request -> buildReply(request)).collect(Collectors.toList())).build());
        responseObserver.onCompleted();
    }

    private HelloReply buildReply(HelloRequest request) {
        String message;
        switch (request.getLanguage().toLowerCase()) {
            case "spanish":
                message = "Saludos desde gRPC service " + request.getName();
                break;
            case "italian":
                message = "Boungiorno " + request.getName();
                break;
            case "catalan":
                message = "Bon dia" + request.getName();
                break;
            case "english":
            default:
                message = "Hello from gRPC service " + request.getName();
        }
        return HelloReply.newBuilder().setMessage(message).setState(request.getInnerHello().getUnknown() ? State.UNKNOWN : State.SUCCESS)
                .setInnerMessage(InnerMessage.newBuilder().setNumber(23).build()).build();
    }
}
