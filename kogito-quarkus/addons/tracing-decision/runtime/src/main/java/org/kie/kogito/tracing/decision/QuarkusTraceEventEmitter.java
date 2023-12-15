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
package org.kie.kogito.tracing.decision;

import java.util.concurrent.Flow.Publisher;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.kie.kogito.tracing.EventEmitter;

import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

import jakarta.inject.Singleton;

@Singleton
public class QuarkusTraceEventEmitter implements EventEmitter {

    private final BroadcastProcessor<String> eventSubject;

    public QuarkusTraceEventEmitter() {
        this.eventSubject = BroadcastProcessor.create();
    }

    @Outgoing("kogito-tracing-decision")
    public Publisher<String> getEventPublisher() {
        return eventSubject.toHotStream();
    }

    @Override
    public void emit(final String payload) {
        eventSubject.onNext(payload);
    }
}
