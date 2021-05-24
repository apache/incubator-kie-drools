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
package org.kie.kogito.index.service;

import javax.inject.Inject;

import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.messaging.ReactiveMessagingEventConsumer;

import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

public abstract class AbstractIndexingIT {

    @Inject
    ReactiveMessagingEventConsumer consumer;

    protected void indexProcessCloudEvent(KogitoProcessCloudEvent event) {
        consumer.onProcessInstanceEvent(event).subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted();
    }

    protected void indexUserTaskCloudEvent(KogitoUserTaskCloudEvent event) {
        consumer.onUserTaskInstanceEvent(event).subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted();
    }

    protected void indexJobCloudEvent(KogitoJobCloudEvent event) {
        consumer.onJobEvent(event).subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted();
    }
}
