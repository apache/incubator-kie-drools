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
package org.kie.kogito.workflows.services;

import org.kie.kogito.examples.sw.greeting.Greeter;
import org.kie.kogito.examples.sw.greeting.HelloArrayReply;
import org.kie.kogito.examples.sw.greeting.HelloArrayReply.Builder;
import org.kie.kogito.examples.sw.greeting.HelloArrayRequest;
import org.kie.kogito.examples.sw.greeting.HelloReply;
import org.kie.kogito.examples.sw.greeting.HelloRequest;
import org.kie.kogito.examples.sw.greeting.InnerMessage;
import org.kie.kogito.examples.sw.greeting.State;

import com.google.protobuf.Empty;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

@GrpcService
public class GreeterService implements Greeter {
    @Override
    public Uni<HelloReply> sayHello(HelloRequest request) {
        return Uni.createFrom().item(() -> buildReply(request));
    }

    @Override
    public Uni<Empty> doNothing(Empty request) {
        return Uni.createFrom().item(Empty.getDefaultInstance());
    }

    @Override
    public Uni<HelloArrayReply> sayHelloArray(HelloArrayRequest request) {
        Builder reply = HelloArrayReply.newBuilder();
        request.getRequestsList().forEach(r -> reply.addReplies(buildReply(r)));
        return Uni.createFrom().item(reply.build());

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
