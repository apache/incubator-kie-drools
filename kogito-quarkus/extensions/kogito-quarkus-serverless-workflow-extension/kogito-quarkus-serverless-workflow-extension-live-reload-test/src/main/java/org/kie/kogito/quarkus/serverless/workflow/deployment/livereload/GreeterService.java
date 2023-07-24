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
package org.kie.kogito.quarkus.serverless.workflow.deployment.livereload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.quarkus.serverless.workflow.deployment.livereload.Greeting.HelloReply;
import org.kie.kogito.quarkus.serverless.workflow.deployment.livereload.Greeting.HelloRequest;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;

@GrpcService
public class GreeterService extends GreeterGrpc.GreeterImplBase {

    protected static final String[] SUPPORTED_LANGUAGES = { "English", "Spanish" };

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = buildServer(Integer.getInteger("grpc.port", 50051));
        server.start();
        server.awaitTermination();
    }

    public static Server buildServer(int port) {
        return ServerBuilder.forPort(port).addService(new GreeterService()).build();
    }

    @Override
    public void sayHello(Greeting.HelloRequest request,
            StreamObserver<HelloReply> responseObserver) {
        responseObserver.onNext(HelloReply.newBuilder().setMessage(getMessage(request)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloAllLanguages(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        for (String language : SUPPORTED_LANGUAGES) {
            HelloRequest languageRequest = HelloRequest.newBuilder(request).setLanguage(language).build();
            responseObserver.onNext(HelloReply.newBuilder().setMessage(getMessage(languageRequest)).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloMultipleLanguagesAtOnce(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {

            private final List<String> messages = new ArrayList<>();

            @Override
            public void onNext(HelloRequest helloRequest) {
                messages.add(getMessage(helloRequest));
            }

            @Override
            public void onError(Throwable throwable) {
                // ignore
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(HelloReply.newBuilder().setMessage(String.join("\n", messages)).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloMultipleLanguages(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(HelloRequest helloRequest) {
                responseObserver.onNext(HelloReply.newBuilder().setMessage(getMessage(helloRequest)).build());
            }

            @Override
            public void onError(Throwable throwable) {
                // ignore
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloMultipleLanguagesError(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {
            int counter;

            @Override
            public void onNext(HelloRequest helloRequest) {
                counter++;
                if (counter == 2) {
                    responseObserver.onNext(HelloReply.newBuilder().setMessage(getMessage(helloRequest)).build());
                    RuntimeException ex = Status.OUT_OF_RANGE.asRuntimeException();
                    responseObserver.onError(ex);
                } else if (counter < 2) {
                    responseObserver.onNext(HelloReply.newBuilder().setMessage(getMessage(helloRequest)).build());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                // ignore
            }

            @Override
            public void onCompleted() {
                if (counter < 2) {
                    responseObserver.onCompleted();
                }
            }
        };
    }

    private static String getMessage(HelloRequest request) {
        String message;
        switch (request.getLanguage().toLowerCase()) {
            case "spanish":
                message = "Saludos desde gRPC service " + request.getName();
                break;
            case "english":
            default:
                message = "Hello from gRPC service " + request.getName();
        }
        return message;
    }
}
