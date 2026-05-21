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
package org.kie.kogito.index.addon.api;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.kie.kogito.Application;
import org.kie.kogito.index.api.KogitoRuntimeClient;
import org.kie.kogito.index.service.auth.DataIndexAuthTokenReader;
import org.kie.kogito.process.Processes;
import org.kie.kogito.source.files.SourceFilesProvider;
import org.kie.kogito.svg.ProcessSvgService;

import io.vertx.core.Vertx;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusKogitoAddonRuntimeClientProducer {

    private Optional<String> gatewayTargetUrl;
    private DataIndexAuthTokenReader dataIndexAuthTokenReader;
    private Vertx vertx;
    private Instance<ProcessSvgService> processSvgService;
    private Instance<SourceFilesProvider> sourceFilesProvider;
    private Instance<Processes> processesInstance;
    private Instance<Application> application;
    private ManagedExecutor managedExecutor;

    @Inject
    public QuarkusKogitoAddonRuntimeClientProducer(@ConfigProperty(name = "kogito.dataindex.gateway.url") Optional<String> gatewayTargetUrl, DataIndexAuthTokenReader dataIndexAuthTokenReader,
            Vertx vertx, Instance<ProcessSvgService> processSvgService,
            Instance<SourceFilesProvider> sourceFilesProvider, Instance<Processes> processesInstance, Instance<Application> application, ManagedExecutor managedExecutor) {
        this.gatewayTargetUrl = gatewayTargetUrl;
        this.dataIndexAuthTokenReader = dataIndexAuthTokenReader;
        this.vertx = vertx;
        this.processSvgService = processSvgService;
        this.sourceFilesProvider = sourceFilesProvider;
        this.processesInstance = processesInstance;
        this.application = application;
        this.managedExecutor = managedExecutor;
    }

    @Produces
    public KogitoRuntimeClient create() {
        return new KogitoAddonRuntimeClient(gatewayTargetUrl, dataIndexAuthTokenReader, vertx, processSvgService, sourceFilesProvider, processesInstance, application, managedExecutor);
    }
}
