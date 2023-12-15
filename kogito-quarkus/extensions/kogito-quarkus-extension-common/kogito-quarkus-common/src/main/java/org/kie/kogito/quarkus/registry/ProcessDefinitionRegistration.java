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
package org.kie.kogito.quarkus.registry;

import org.kie.kogito.Application;
import org.kie.kogito.process.Processes;
import org.kie.kogito.quarkus.config.KogitoRuntimeConfig;
import org.kie.kogito.services.registry.ProcessDefinitionEventRegistry;

import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProcessDefinitionRegistration {

    Instance<Processes> processes;
    ProcessDefinitionEventRegistry processDefinitionRegistry;

    @Inject
    public ProcessDefinitionRegistration(Application application, KogitoRuntimeConfig runtimeConfig, Instance<Processes> processes) {
        this.processes = processes;
        this.processDefinitionRegistry = new ProcessDefinitionEventRegistry(application, runtimeConfig.serviceUrl.orElse(null));
    }

    void onStartUp(@Observes StartupEvent startupEvent) {
        if (processes.isResolvable()) {
            processDefinitionRegistry.register(processes.get());
        }
    }
}
