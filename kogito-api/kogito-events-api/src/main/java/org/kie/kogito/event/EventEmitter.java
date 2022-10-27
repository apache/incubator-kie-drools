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

/**
 * It is responsible to interact with the external event service for event publishing.
 * One of its task is to transform the data event into the format expected by the external service.
 * 
 * @see EventMarshaller
 * @see CloudEventMarshaller
 */
public interface EventEmitter {
    /**
     * Publish the data event object into an external event service.
     * 
     * @param dataEvent The DataEvent
     */
    CompletionStage<Void> emit(DataEvent<?> dataEvent);
}
