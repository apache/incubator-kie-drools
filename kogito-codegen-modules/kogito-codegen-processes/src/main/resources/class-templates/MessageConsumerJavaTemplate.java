/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package com.myspace.demo;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.process.impl.ProcessServiceImpl;
import org.kie.kogito.services.event.EventConsumerFactory;
import org.kie.kogito.event.impl.DefaultEventConsumerFactory;

public class $Type$MessageConsumer {

    Process<$Type$> process;

    Application application;

    boolean useCloudEvents = true;

    EventConsumerFactory eventConsumerFactory;
    
    ExecutorService executor = Executors.newSingleThreadExecutor();
    
    ProcessService service;

    public void configure() {
        eventConsumerFactory = new DefaultEventConsumerFactory();
        service = new ProcessServiceImpl(application);
    }

    public void consume($DataType$ payload) {
        eventConsumerFactory
            .<$Type$,$DataType$>get(service, executor, event -> {
                $Type$ model = new $Type$();
                model.set$DataType$(event);
                return model;
            }, useCloudEvents)
            .consume(application, process, payload, "$Trigger$");
    }
}
