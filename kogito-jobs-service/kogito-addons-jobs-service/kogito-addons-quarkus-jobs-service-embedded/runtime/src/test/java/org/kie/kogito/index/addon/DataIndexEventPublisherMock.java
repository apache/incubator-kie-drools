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

package org.kie.kogito.index.addon;

import java.util.Collection;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;

/**
 * Mock class used by the org.kie.kogito.addons.quarkus.jobs.service.embedded.stream.EventPublisherJobStreamsTest.
 */
public class DataIndexEventPublisherMock implements EventPublisher {

    @Override
    public void publish(DataEvent<?> event) {
        // this method is por testing purposes, no code is required here.
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        // this method is por testing purposes, no code is required here.
    }
}
