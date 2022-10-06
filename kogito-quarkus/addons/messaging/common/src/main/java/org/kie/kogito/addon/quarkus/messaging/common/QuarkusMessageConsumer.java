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
package org.kie.kogito.addon.quarkus.messaging.common;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.event.EventExecutorServiceFactory;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.impl.AbstractMessageConsumer;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessService;

public abstract class QuarkusMessageConsumer<M extends Model, D> extends AbstractMessageConsumer<M, D> {

    @Inject
    Application application;

    @Inject
    ProcessService processService;

    @Inject
    EventExecutorServiceFactory factory;

    private ExecutorService executor;

    protected void init(Process<M> process, String trigger, Class<D> objectClass, EventReceiver eventReceiver, Set<String> correlation) {
        executor = factory.getExecutorService(trigger);
        init(application, process, trigger, eventReceiver, objectClass, processService, executor, correlation);
    }

    @javax.annotation.PreDestroy
    public void close() {
        executor.shutdownNow();
    }
}
