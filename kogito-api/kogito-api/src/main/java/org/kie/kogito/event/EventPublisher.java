/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.event;

import java.util.Collection;

/**
 * Responsible for publishing events for consumption to the "outside world"
 * 
 * Depending on the implementation it can be to push over the wire or use an 
 * in memory queue to notify other parties about particular events.
 * 
 * In case any filtering needs to take place, this is where it should happen.
 *
 */
public interface EventPublisher {

    /**
     * Publishes individual event
     * @param event event to be published
     */
    void publish(DataEvent<?> event);
    
    /**
     * Publish collection of events. It's up to implementation to publish them
     * individually or as complete collection. 
     * @param events events to be published
     */
    void publish(Collection<DataEvent<?>> events);
}
