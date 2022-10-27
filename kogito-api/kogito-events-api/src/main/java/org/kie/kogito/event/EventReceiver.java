/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * Event receiver interface.
 * 
 * Implementation are responsible for interacting with the external event publisher and transforming the events received into the model object.
 * 
 * @see EventUnmarshaller
 * @see CloudEventUnmarshaller
 */
public interface EventReceiver {

    /**
     * Subscribe an event consumer for a receiver. The implementation will receive the event (in some format) from the external service, transform it
     * into a data event instance and invoke the callback.
     * 
     * @param consumer consumer function that accepts the data event object and return a completion stage with the result of the consumption.
     * @param dataClass the model object class wrapped into the data event
     */
    <T> void subscribe(Function<DataEvent<T>, CompletionStage<?>> consumer, Class<T> dataClass);
}
